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
        <el-input :model-value="selectedOrderLabel" placeholder="请选择订单" readonly @click="openOrderDialog">
          <template #append>
            <el-button @click="openOrderDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="关联单元" prop="unitId">
        <el-input :model-value="selectedUnitLabel" placeholder="请选择单元" readonly @click="openUnitDialog">
          <template #append>
            <el-button @click="openUnitDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="异常单号" prop="abnormalNo">
        <el-input v-model="formData.abnormalNo" placeholder="请输入异常单号" />
      </el-form-item>
      <el-form-item label="异常类型" prop="abnormalType">
        <el-input v-model="formData.abnormalType" placeholder="请输入异常类型" />
      </el-form-item>
      <el-form-item label="风险等级" prop="riskLevel">
        <el-input v-model="formData.riskLevel" placeholder="请输入风险等级" />
      </el-form-item>
      <el-form-item label="命中规则编码" prop="hitRuleCode">
        <el-input v-model="formData.hitRuleCode" placeholder="请输入命中规则编码" />
      </el-form-item>
      <el-form-item label="处理状态" prop="handleStatus">
        <el-radio-group v-model="formData.handleStatus">
          <el-radio v-for="item in HANDLE_STATUS_OPTIONS" :key="item.value" :value="item.value">
            {{ item.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="处理人" prop="handleBy">
        <el-input v-model="formData.handleBy" placeholder="请输入处理人" />
      </el-form-item>
      <el-form-item label="处理时间" prop="handleTime">
        <el-date-picker
          v-model="formData.handleTime"
          type="datetime"
          value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="选择处理时间"
        />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="formData.remark" placeholder="请输入备注" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
  <OrderSelectDialog ref="orderSelectDialogRef" @selected="handleOrderSelected" />
  <OrderUnitSelectDialog ref="unitSelectDialogRef" @selected="handleUnitSelected" />
</template>
<script setup lang="ts">
import { OrderAbnormalApi, OrderAbnormal } from '@/api/linbang/orderabnormal'
import { OrderInfoApi, type OrderInfo } from '@/api/linbang/orderinfo'
import { OrderUnitApi, type OrderUnit } from '@/api/linbang/orderunit'
import { HANDLE_STATUS_OPTIONS } from '../utils/display'
import OrderSelectDialog from '../shared/OrderSelectDialog.vue'
import OrderUnitSelectDialog from '../shared/OrderUnitSelectDialog.vue'

import { computed, reactive, ref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
/** 异常订单 表单 */
defineOptions({ name: 'OrderAbnormalForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const orderSelectDialogRef = ref()
const unitSelectDialogRef = ref()
const selectedOrder = ref<OrderInfo>()
const selectedUnit = ref<OrderUnit>()
type FormData = {
  id?: number
  orderId?: number
  unitId?: number
  abnormalNo?: string
  abnormalType?: string
  riskLevel?: string
  hitRuleCode?: string
  handleStatus?: string
  handleBy?: number
  handleTime?: string
  remark?: string
}

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
  return [selectedUnit.value.unitNo, selectedUnit.value.unitTitle]
    .filter(Boolean)
    .join(' / ')
})

const formData = ref<FormData>({
  id: undefined,
  orderId: undefined,
  unitId: undefined,
  abnormalNo: undefined,
  abnormalType: undefined,
  riskLevel: undefined,
  hitRuleCode: undefined,
  handleStatus: undefined,
  handleBy: undefined,
  handleTime: undefined,
  remark: undefined
})
const formRules = reactive({
  orderId: [{ required: true, message: '关联订单不能为空', trigger: 'change' }],
  abnormalNo: [{ required: true, message: '异常单号不能为空', trigger: 'blur' }],
  abnormalType: [{ required: true, message: '异常类型不能为空', trigger: 'change' }],
  riskLevel: [{ required: true, message: '风险等级不能为空', trigger: 'blur' }],
  handleStatus: [{ required: true, message: '处理状态不能为空', trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      const data = await OrderAbnormalApi.getOrderAbnormal(id)
      formData.value = buildFormData(data)
      await loadSelectedOrder(data.orderId)
      await loadSelectedUnit(data.unitId)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as OrderAbnormal
    if (formType.value === 'create') {
      await OrderAbnormalApi.createOrderAbnormal(data)
      message.success(t('common.createSuccess'))
    } else {
      await OrderAbnormalApi.updateOrderAbnormal(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    orderId: undefined,
    unitId: undefined,
    abnormalNo: undefined,
    abnormalType: undefined,
    riskLevel: undefined,
    hitRuleCode: undefined,
    handleStatus: undefined,
    handleBy: undefined,
    handleTime: undefined,
    remark: undefined
  }
  selectedOrder.value = undefined
  selectedUnit.value = undefined
  formRef.value?.resetFields()
}

const openOrderDialog = () => {
  orderSelectDialogRef.value?.open(selectedOrder.value)
}

const openUnitDialog = () => {
  unitSelectDialogRef.value?.open(selectedUnit.value, formData.value.orderId)
}

const handleOrderSelected = (row: OrderInfo) => {
  selectedOrder.value = row
  formData.value.orderId = row.id
  if (selectedUnit.value?.orderId !== row.id) {
    selectedUnit.value = undefined
    formData.value.unitId = undefined
  }
}

const handleUnitSelected = (row: OrderUnit) => {
  selectedUnit.value = row
  formData.value.unitId = row.id
  if (!formData.value.orderId && row.orderId) {
    formData.value.orderId = row.orderId
  }
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

const buildFormData = (data: OrderAbnormal): FormData => ({
  id: data.id,
  orderId: data.orderId,
  unitId: data.unitId,
  abnormalNo: data.abnormalNo,
  abnormalType: data.abnormalType,
  riskLevel: data.riskLevel,
  hitRuleCode: data.hitRuleCode,
  handleStatus: data.handleStatus,
  handleBy: data.handleBy,
  handleTime: data.handleTime ? String(data.handleTime) : undefined,
  remark: data.remark
})
</script>
