<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="主订单" prop="orderId">
        <el-input :model-value="selectedOrderLabel" placeholder="请选择主订单" readonly @click="openOrderDialog">
          <template #append>
            <el-button @click="openOrderDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="单元订单号" prop="unitNo">
        <el-input v-model="formData.unitNo" placeholder="请输入单元订单号" />
      </el-form-item>
      <el-form-item label="单元序号" prop="unitSeq">
        <el-input v-model="formData.unitSeq" placeholder="请输入单元序号" />
      </el-form-item>
      <el-form-item label="单元标题" prop="unitTitle">
        <el-input v-model="formData.unitTitle" placeholder="请输入单元标题" />
      </el-form-item>
      <el-form-item label="单元金额" prop="unitAmount">
        <el-input v-model="formData.unitAmount" placeholder="请输入单元金额" />
      </el-form-item>
      <el-form-item label="拆分模式" prop="splitMode">
        <el-select v-model="formData.splitMode" placeholder="请选择拆分模式" clearable>
          <el-option
            v-for="item in SPLIT_MODE_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="前置单元" prop="prevUnitId">
        <el-input :model-value="selectedPrevUnitLabel" placeholder="请选择前置单元" readonly @click="openPrevUnitDialog">
          <template #append>
            <el-button @click="openPrevUnitDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="是否锁定" prop="isLocked">
        <el-radio-group v-model="formData.isLocked">
          <el-radio
            v-for="item in BOOLEAN_YES_NO_OPTIONS"
            :key="String(item.value)"
            :value="item.value"
          >
            {{ item.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="锁定原因" prop="lockReason">
        <el-input v-model="formData.lockReason" placeholder="请输入锁定原因" />
      </el-form-item>
      <el-form-item label="服务商" prop="merchantId">
        <el-input :model-value="selectedMerchantLabel" placeholder="请选择服务商" readonly @click="openMerchantDialog">
          <template #append>
            <el-button @click="openMerchantDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="单元状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_ORDER_UNIT_STATUS)"
            :key="dict.value"
            :label="dict.value"
          >
            {{ dict.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="接单截止时间" prop="acceptDeadlineTime">
        <el-date-picker
          v-model="formData.acceptDeadlineTime"
          type="datetime"
          value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="选择接单截止时间"
        />
      </el-form-item>
      <el-form-item label="完成时间" prop="finishTime">
        <el-date-picker
          v-model="formData.finishTime"
          type="datetime"
          value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="选择完成时间"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
  <OrderSelectDialog ref="orderSelectDialogRef" @selected="handleOrderSelected" />
  <OrderUnitSelectDialog ref="prevUnitSelectDialogRef" @selected="handlePrevUnitSelected" />
  <MerchantInfoSelectDialog ref="merchantSelectDialogRef" @selected="handleMerchantSelected" />
</template>
<script setup lang="ts">
import { getStrDictOptions, DICT_TYPE } from '@/utils/dict'
import { MerchantInfoApi, type MerchantInfo, type MerchantInfoDetail } from '@/api/linbang/merchantinfo'
import { OrderUnitApi, OrderUnit, OrderUnitDetail } from '@/api/linbang/orderunit'
import { OrderInfoApi, type OrderInfo } from '@/api/linbang/orderinfo'
import { BOOLEAN_YES_NO_OPTIONS, SPLIT_MODE_OPTIONS } from '../utils/display'
import MerchantInfoSelectDialog from '../shared/MerchantInfoSelectDialog.vue'
import OrderSelectDialog from '../shared/OrderSelectDialog.vue'
import OrderUnitSelectDialog from '../shared/OrderUnitSelectDialog.vue'

import { computed, reactive, ref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
/** 拆分单元 表单 */
defineOptions({ name: 'OrderUnitForm' })

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')
const orderSelectDialogRef = ref()
const prevUnitSelectDialogRef = ref()
const merchantSelectDialogRef = ref()
const selectedOrder = ref<OrderInfo>()
const selectedPrevUnit = ref<OrderUnitDetail>()
const selectedMerchant = ref<MerchantInfo>()
type FormData = {
  id?: number
  orderId?: number
  unitNo?: string
  unitSeq?: number
  unitTitle?: string
  unitAmount?: number
  splitMode?: string
  prevUnitId?: number
  isLocked?: boolean
  lockReason?: string
  merchantId?: number
  status?: string
  acceptDeadlineTime?: string
  finishTime?: string
}

const selectedOrderLabel = computed(() => {
  if (!selectedOrder.value) {
    return ''
  }
  return [selectedOrder.value.orderNo, selectedOrder.value.title, selectedOrder.value.userNickname]
    .filter(Boolean)
    .join(' / ')
})

const selectedPrevUnitLabel = computed(() => {
  if (!selectedPrevUnit.value) {
    return ''
  }
  return [selectedPrevUnit.value.unitNo, selectedPrevUnit.value.unitTitle].filter(Boolean).join(' / ')
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

const createEmptyFormData = (): FormData => ({
  id: undefined,
  orderId: undefined,
  unitNo: undefined,
  unitSeq: undefined,
  unitTitle: undefined,
  unitAmount: undefined,
  splitMode: undefined,
  prevUnitId: undefined,
  isLocked: undefined,
  lockReason: undefined,
  merchantId: undefined,
  status: undefined,
  acceptDeadlineTime: undefined,
  finishTime: undefined
})

const formData = ref<FormData>(createEmptyFormData())
const formRules = reactive({
  orderId: [{ required: true, message: '主订单不能为空', trigger: 'change' }],
  unitNo: [{ required: true, message: '单元订单号不能为空', trigger: 'blur' }],
  unitSeq: [{ required: true, message: '单元序号不能为空', trigger: 'blur' }],
  unitAmount: [{ required: true, message: '单元金额不能为空', trigger: 'blur' }],
  isLocked: [{ required: true, message: '是否锁定不能为空', trigger: 'change' }],
  merchantId: [{ required: true, message: '服务商不能为空', trigger: 'change' }],
  status: [{ required: true, message: '单元状态不能为空', trigger: 'blur' }]
})
const formRef = ref()

const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  if (id) {
    formLoading.value = true
    try {
      const data = await OrderUnitApi.getOrderUnit(id)
      formData.value = buildFormData(data)
      await Promise.all([
        loadSelectedOrder(data.orderId),
        loadSelectedPrevUnit(data.prevUnitId),
        loadSelectedMerchant(data.merchantId)
      ])
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open })

const emit = defineEmits(['success'])
const submitForm = async () => {
  await formRef.value.validate()
  formLoading.value = true
  try {
    const data = formData.value as unknown as OrderUnit
    if (formType.value === 'create') {
      await OrderUnitApi.createOrderUnit(data)
      message.success(t('common.createSuccess'))
    } else {
      await OrderUnitApi.updateOrderUnit(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}

const resetForm = () => {
  formData.value = createEmptyFormData()
  selectedOrder.value = undefined
  selectedPrevUnit.value = undefined
  selectedMerchant.value = undefined
  formRef.value?.resetFields()
}

const openOrderDialog = () => {
  orderSelectDialogRef.value?.open(selectedOrder.value)
}

const openPrevUnitDialog = () => {
  prevUnitSelectDialogRef.value?.open(selectedPrevUnit.value, formData.value.orderId)
}

const openMerchantDialog = () => {
  merchantSelectDialogRef.value?.open(selectedMerchant.value)
}

const handleOrderSelected = (row: OrderInfo) => {
  selectedOrder.value = row
  formData.value.orderId = row.id
  if (selectedPrevUnit.value?.orderId !== row.id) {
    selectedPrevUnit.value = undefined
    formData.value.prevUnitId = undefined
  }
}

const handlePrevUnitSelected = async (row: OrderUnit) => {
  formData.value.prevUnitId = row.id
  await loadSelectedPrevUnit(row.id)
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

const loadSelectedPrevUnit = async (unitId?: number) => {
  if (!unitId) {
    selectedPrevUnit.value = undefined
    return
  }
  selectedPrevUnit.value = await OrderUnitApi.getOrderUnit(unitId)
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

const buildFormData = (data: OrderUnitDetail): FormData => ({
  id: data.id,
  orderId: data.orderId,
  unitNo: data.unitNo,
  unitSeq: data.unitSeq,
  unitTitle: data.unitTitle,
  unitAmount: data.unitAmount,
  splitMode: data.splitMode,
  prevUnitId: data.prevUnitId,
  isLocked: data.isLocked,
  lockReason: data.lockReason,
  merchantId: data.merchantId,
  status: data.status,
  acceptDeadlineTime: data.acceptDeadlineTime ? String(data.acceptDeadlineTime) : undefined,
  finishTime: data.finishTime ? String(data.finishTime) : undefined
})
</script>
