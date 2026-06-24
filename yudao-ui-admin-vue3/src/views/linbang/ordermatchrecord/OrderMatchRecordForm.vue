<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="关联订单" prop="orderId">
        <el-input :model-value="selectedOrderLabel" placeholder="请选择关联订单" readonly @click="openOrderDialog">
          <template #append>
            <el-button @click="openOrderDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="关联单元" prop="unitId">
        <el-input :model-value="selectedUnitLabel" placeholder="请选择关联单元" readonly @click="openUnitDialog">
          <template #append>
            <el-button @click="openUnitDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="服务商" prop="merchantId">
        <el-input :model-value="selectedMerchantLabel" placeholder="请选择服务商" readonly @click="openMerchantDialog">
          <template #append>
            <el-button @click="openMerchantDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="命中规则编码" prop="matchRuleCode">
        <el-input v-model="formData.matchRuleCode" placeholder="请输入命中规则编码" />
      </el-form-item>
      <el-form-item label="匹配分值" prop="matchScore">
        <el-input v-model="formData.matchScore" placeholder="请输入匹配分值" />
      </el-form-item>
      <el-form-item label="距离公里" prop="distanceKm">
        <el-input v-model="formData.distanceKm" placeholder="请输入距离公里" />
      </el-form-item>
      <el-form-item label="推送时间" prop="pushTime">
        <el-date-picker
          v-model="formData.pushTime"
          type="date"
          value-format="x"
          placeholder="选择推送时间"
        />
      </el-form-item>
      <el-form-item label="接单截止时间" prop="acceptDeadlineTime">
        <el-date-picker
          v-model="formData.acceptDeadlineTime"
          type="date"
          value-format="x"
          placeholder="选择接单截止时间"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio v-for="item in MATCH_STATUS_OPTIONS" :key="item.value" :value="item.value">
            {{ item.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
  <OrderSelectDialog ref="orderSelectDialogRef" @selected="handleOrderSelected" />
  <OrderUnitSelectDialog ref="unitSelectDialogRef" @selected="handleUnitSelected" />
  <MerchantInfoSelectDialog ref="merchantSelectDialogRef" @selected="handleMerchantSelected" />
</template>
<script setup lang="ts">
import {
  OrderMatchRecordApi,
  type OrderMatchRecord,
  type OrderMatchRecordDetail
} from '@/api/linbang/ordermatchrecord'
import { MerchantInfoApi, type MerchantInfo, type MerchantInfoDetail } from '@/api/linbang/merchantinfo'
import { OrderInfoApi, type OrderInfo } from '@/api/linbang/orderinfo'
import { OrderUnitApi, type OrderUnit } from '@/api/linbang/orderunit'
import { MATCH_STATUS_OPTIONS } from '../utils/display'
import MerchantInfoSelectDialog from '../shared/MerchantInfoSelectDialog.vue'
import OrderSelectDialog from '../shared/OrderSelectDialog.vue'
import OrderUnitSelectDialog from '../shared/OrderUnitSelectDialog.vue'

import { computed, reactive, ref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
/** 订单匹配记录 表单 */
defineOptions({ name: 'OrderMatchRecordForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const orderSelectDialogRef = ref()
const unitSelectDialogRef = ref()
const merchantSelectDialogRef = ref()
const selectedOrder = ref<OrderInfo>()
const selectedUnit = ref<OrderUnit>()
const selectedMerchant = ref<MerchantInfo>()
type OrderMatchRecordFormData = {
  id?: number
  orderId?: number
  unitId?: number
  merchantId?: number
  matchRuleCode?: string
  matchScore?: number
  distanceKm?: number
  pushTime?: number | string
  acceptDeadlineTime?: number | string
  status?: string
}

const createEmptyFormData = (): OrderMatchRecordFormData => ({
  id: undefined,
  orderId: undefined,
  unitId: undefined,
  merchantId: undefined,
  matchRuleCode: undefined,
  matchScore: undefined,
  distanceKm: undefined,
  pushTime: undefined,
  acceptDeadlineTime: undefined,
  status: undefined
})

const selectedOrderLabel = computed(() => {
  if (!selectedOrder.value) {
    return ''
  }
  return [selectedOrder.value.orderNo, selectedOrder.value.title, selectedOrder.value.userNickname]
    .filter(Boolean)
    .join(' / ')
})

const selectedUnitLabel = computed(() => {
  if (!selectedUnit.value) {
    return ''
  }
  return [selectedUnit.value.unitNo, selectedUnit.value.unitTitle].filter(Boolean).join(' / ')
})

const selectedMerchantLabel = computed(() => {
  if (!selectedMerchant.value) {
    return ''
  }
  return [
    selectedMerchant.value.merchantName,
    selectedMerchant.value.contactName,
    selectedMerchant.value.contactMobile
  ]
    .filter(Boolean)
    .join(' / ')
})

const formData = ref<OrderMatchRecordFormData>(createEmptyFormData())
const formRules = reactive({
  orderId: [{ required: true, message: '关联订单不能为空', trigger: 'change' }],
  merchantId: [{ required: true, message: '服务商不能为空', trigger: 'change' }],
  pushTime: [{ required: true, message: '推送时间不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  if (id) {
    formLoading.value = true
    try {
      const detail = await OrderMatchRecordApi.getOrderMatchRecord(id)
      formData.value = buildFormData(detail)
      await Promise.all([
        loadSelectedOrder(detail.orderId),
        loadSelectedUnit(detail.unitId),
        loadSelectedMerchant(detail.merchantId)
      ])
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open })

/** 提交表单 */
const emit = defineEmits(['success'])
const submitForm = async () => {
  await formRef.value.validate()
  formLoading.value = true
  try {
    const data = formData.value as unknown as OrderMatchRecord
    if (formType.value === 'create') {
      await OrderMatchRecordApi.createOrderMatchRecord(data)
      message.success(t('common.createSuccess'))
    } else {
      await OrderMatchRecordApi.updateOrderMatchRecord(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = createEmptyFormData()
  selectedOrder.value = undefined
  selectedUnit.value = undefined
  selectedMerchant.value = undefined
  formRef.value?.resetFields()
}

const openOrderDialog = () => {
  orderSelectDialogRef.value?.open(selectedOrder.value)
}

const openUnitDialog = () => {
  unitSelectDialogRef.value?.open(selectedUnit.value, formData.value.orderId)
}

const openMerchantDialog = () => {
  merchantSelectDialogRef.value?.open(selectedMerchant.value)
}

const handleOrderSelected = (row: OrderInfo) => {
  selectedOrder.value = row
  formData.value.orderId = row.id
  if (selectedUnit.value?.orderId !== row.id) {
    selectedUnit.value = undefined
    formData.value.unitId = undefined
  }
}

const handleUnitSelected = async (row: OrderUnit) => {
  selectedUnit.value = row
  formData.value.unitId = row.id
  if (row.orderId && selectedOrder.value?.id !== row.orderId) {
    formData.value.orderId = row.orderId
    await loadSelectedOrder(row.orderId)
  }
}

const handleMerchantSelected = (row: MerchantInfo) => {
  selectedMerchant.value = row
  formData.value.merchantId = row.id
}

const loadSelectedOrder = async (orderId?: number) => {
  if (!orderId) {
    selectedOrder.value = undefined
    return
  }
  selectedOrder.value = await OrderInfoApi.getOrderInfo(orderId)
}

const loadSelectedUnit = async (unitId?: number) => {
  if (!unitId) {
    selectedUnit.value = undefined
    return
  }
  selectedUnit.value = await OrderUnitApi.getOrderUnit(unitId)
}

const loadSelectedMerchant = async (merchantId?: number) => {
  if (!merchantId) {
    selectedMerchant.value = undefined
    return
  }
  const detail = await MerchantInfoApi.getMerchantInfo(merchantId)
  selectedMerchant.value = buildSelectedMerchant(detail)
}

const buildSelectedMerchant = (detail: MerchantInfoDetail): MerchantInfo => ({
  id: detail.id,
  userId: detail.userId,
  userNo: detail.userNo,
  userNickname: detail.userNickname,
  userMobile: detail.userMobile,
  merchantName: detail.merchantName,
  contactName: detail.contactName,
  contactMobile: detail.contactMobile,
  serviceScopeDesc: detail.serviceScopeDesc,
  status: detail.status,
  acceptStatus: detail.acceptStatus,
  creditScore: detail.creditScore,
  creditLevel: detail.creditLevel,
  createTime: detail.createTime
})

const buildFormData = (data: OrderMatchRecordDetail): OrderMatchRecordFormData => ({
  id: data.id,
  orderId: data.orderId,
  unitId: data.unitId,
  merchantId: data.merchantId,
  matchRuleCode: data.matchRuleCode,
  matchScore: data.matchScore,
  distanceKm: data.distanceKm,
  pushTime: data.pushTime ? new Date(data.pushTime as string).getTime() : undefined,
  acceptDeadlineTime: data.acceptDeadlineTime
    ? new Date(data.acceptDeadlineTime as string).getTime()
    : undefined,
  status: data.status
})
</script>
