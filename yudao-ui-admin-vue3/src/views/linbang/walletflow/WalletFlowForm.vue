<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="流水号" prop="flowNo">
        <el-input v-model="formData.flowNo" placeholder="请输入流水号" />
      </el-form-item>
      <el-form-item label="用户" prop="userId">
        <el-input :model-value="selectedUserLabel" placeholder="请选择用户" readonly @click="openUserDialog">
          <template #append>
            <el-button @click="openUserDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="关联订单" prop="relatedOrderId">
        <el-input :model-value="selectedOrderLabel" placeholder="请选择关联订单" readonly @click="openOrderDialog">
          <template #append>
            <el-button @click="openOrderDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="关联单元" prop="relatedUnitId">
        <el-input :model-value="selectedUnitLabel" placeholder="请选择关联单元" readonly @click="openUnitDialog">
          <template #append>
            <el-button @click="openUnitDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="钱包账户" prop="walletAccountId">
        <el-input
          :model-value="selectedWalletAccountLabel"
          placeholder="请选择钱包账户"
          readonly
          @click="openWalletAccountDialog"
        >
          <template #append>
            <el-button @click="openWalletAccountDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="业务类型" prop="bizType">
        <el-input v-model="formData.bizType" placeholder="请输入业务类型" />
      </el-form-item>
      <el-form-item label="流水类型" prop="flowType">
        <el-select v-model="formData.flowType" placeholder="请选择流水类型" class="!w-full">
          <el-option
            v-for="item in FLOW_TYPE_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="变动金额" prop="changeAmount">
        <el-input v-model="formData.changeAmount" placeholder="请输入变动金额" />
      </el-form-item>
      <el-form-item label="变动前金额" prop="beforeAmount">
        <el-input v-model="formData.beforeAmount" placeholder="请输入变动前金额" />
      </el-form-item>
      <el-form-item label="变动后金额" prop="afterAmount">
        <el-input v-model="formData.afterAmount" placeholder="请输入变动后金额" />
      </el-form-item>
      <el-form-item label="关联支付订单" prop="relatedPayOrderId">
        <el-input :model-value="selectedPayOrderLabel" placeholder="请选择关联支付订单" readonly @click="openPayOrderDialog">
          <template #append>
            <el-button @click="openPayOrderDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="关联退款单" prop="relatedRefundId">
        <el-input :model-value="selectedRefundLabel" placeholder="请选择关联退款单" readonly @click="openRefundDialog">
          <template #append>
            <el-button @click="openRefundDialog">选择</el-button>
          </template>
        </el-input>
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
  <MemberUserSelectDialog ref="userSelectDialogRef" @selected="handleUserSelected" />
  <OrderSelectDialog ref="orderSelectDialogRef" @selected="handleOrderSelected" />
  <OrderUnitSelectDialog ref="unitSelectDialogRef" @selected="handleUnitSelected" />
  <WalletAccountSelectDialog ref="walletAccountSelectDialogRef" @selected="handleWalletAccountSelected" />
  <PayOrderSelectDialog ref="payOrderSelectDialogRef" @selected="handlePayOrderSelected" />
  <PayRefundSelectDialog ref="refundSelectDialogRef" @selected="handleRefundSelected" />
</template>
<script setup lang="ts">
import { WalletFlowApi, type WalletFlowFormData } from '@/api/linbang/walletflow'
import { MemberUserApi, type MemberUser } from '@/api/linbang/memberuser'
import { OrderInfoApi, type OrderInfo } from '@/api/linbang/orderinfo'
import { OrderUnitApi, type OrderUnit } from '@/api/linbang/orderunit'
import { WalletAccountApi, type WalletAccount } from '@/api/linbang/walletaccount'
import type { RefundDetailVO } from '@/api/pay/refund'
import { FLOW_TYPE_OPTIONS } from '../utils/display'
import MemberUserSelectDialog from '../memberaddress/MemberUserSelectDialog.vue'
import OrderSelectDialog from '../shared/OrderSelectDialog.vue'
import OrderUnitSelectDialog from '../shared/OrderUnitSelectDialog.vue'
import WalletAccountSelectDialog from '../shared/WalletAccountSelectDialog.vue'
import PayOrderSelectDialog, { type PayOrderOption } from '../shared/PayOrderSelectDialog.vue'
import PayRefundSelectDialog from '../shared/PayRefundSelectDialog.vue'

import { computed, reactive, ref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
/** 钱包流水 表单 */
defineOptions({ name: 'WalletFlowForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const userSelectDialogRef = ref()
const orderSelectDialogRef = ref()
const unitSelectDialogRef = ref()
const walletAccountSelectDialogRef = ref()
const payOrderSelectDialogRef = ref()
const refundSelectDialogRef = ref()
const selectedUser = ref<MemberUser>()
const selectedOrder = ref<OrderInfo>()
const selectedUnit = ref<OrderUnit>()
const selectedWalletAccount = ref<WalletAccount>()
const selectedPayOrder = ref<PayOrderOption>()
const selectedRefund = ref<RefundDetailVO>()
type FormData = {
  id?: number
  flowNo?: string
  userId?: number
  walletAccountId?: number
  bizType?: string
  flowType?: string
  changeAmount?: number
  beforeAmount?: number
  afterAmount?: number
  relatedOrderId?: number
  relatedUnitId?: number
  relatedPayOrderId?: number
  relatedRefundId?: number
  remark?: string
}

const selectedUserLabel = computed(() => {
  if (!selectedUser.value) {
    return ''
  }
  return [selectedUser.value.userNo, selectedUser.value.nickname, selectedUser.value.mobile]
    .filter(Boolean)
    .join(' / ')
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
  return [selectedUnit.value.unitNo, selectedUnit.value.unitTitle]
    .filter(Boolean)
    .join(' / ')
})

const selectedWalletAccountLabel = computed(() => {
  if (!selectedWalletAccount.value) {
    return ''
  }
  return [
    selectedWalletAccount.value.userNickname,
    selectedWalletAccount.value.userMobile,
    selectedWalletAccount.value.roleCode
  ]
    .filter(Boolean)
    .join(' / ')
})

const selectedPayOrderLabel = computed(() => {
  if (!selectedPayOrder.value) {
    return ''
  }
  return [
    selectedPayOrder.value.merchantOrderId,
    selectedPayOrder.value.no,
    selectedPayOrder.value.subject
  ]
    .filter(Boolean)
    .join(' / ')
})

const selectedRefundLabel = computed(() => {
  if (!selectedRefund.value) {
    return ''
  }
  return [
    selectedRefund.value.merchantRefundId,
    selectedRefund.value.no,
    selectedRefund.value.merchantOrderId
  ]
    .filter(Boolean)
    .join(' / ')
})

const createEmptyFormData = (): FormData => ({
  id: undefined,
  flowNo: undefined,
  userId: undefined,
  walletAccountId: undefined,
  bizType: undefined,
  flowType: undefined,
  changeAmount: undefined,
  beforeAmount: undefined,
  afterAmount: undefined,
  relatedOrderId: undefined,
  relatedUnitId: undefined,
  relatedPayOrderId: undefined,
  relatedRefundId: undefined,
  remark: undefined
})
const formData = ref<FormData>(createEmptyFormData())
const formRules = reactive({
  flowNo: [{ required: true, message: '流水号不能为空', trigger: 'blur' }],
  userId: [{ required: true, message: '用户不能为空', trigger: 'change' }],
  walletAccountId: [{ required: true, message: '钱包账户不能为空', trigger: 'change' }],
  bizType: [{ required: true, message: '业务类型不能为空', trigger: 'change' }],
  flowType: [{ required: true, message: '流水类型不能为空', trigger: 'change' }],
  changeAmount: [{ required: true, message: '变动金额不能为空', trigger: 'blur' }],
  beforeAmount: [{ required: true, message: '变动前金额不能为空', trigger: 'blur' }],
  afterAmount: [{ required: true, message: '变动后金额不能为空', trigger: 'blur' }]
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
      const detail = await WalletFlowApi.getWalletFlow(id)
      formData.value = buildFormData(detail)
      await loadSelectedUser(detail.userId)
      await loadSelectedOrder(detail.relatedOrderId)
      await loadSelectedUnit(detail.relatedUnitId)
      await loadSelectedWalletAccount(detail.walletAccountId)
      await loadSelectedPayOrder(detail.relatedPayOrderId)
      await loadSelectedRefund(detail.relatedRefundId)
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
    const data = formData.value as WalletFlowFormData
    if (formType.value === 'create') {
      await WalletFlowApi.createWalletFlow(data)
      message.success(t('common.createSuccess'))
    } else {
      await WalletFlowApi.updateWalletFlow(data)
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
    ...createEmptyFormData()
  }
  selectedUser.value = undefined
  selectedOrder.value = undefined
  selectedUnit.value = undefined
  selectedWalletAccount.value = undefined
  selectedPayOrder.value = undefined
  selectedRefund.value = undefined
  formRef.value?.resetFields()
}

const openUserDialog = () => {
  userSelectDialogRef.value?.open(selectedUser.value)
}

const openOrderDialog = () => {
  orderSelectDialogRef.value?.open(selectedOrder.value)
}

const openUnitDialog = () => {
  unitSelectDialogRef.value?.open(selectedUnit.value, formData.value.relatedOrderId)
}

const openWalletAccountDialog = () => {
  walletAccountSelectDialogRef.value?.open(selectedWalletAccount.value, selectedUser.value)
}

const openPayOrderDialog = () => {
  payOrderSelectDialogRef.value?.open(selectedPayOrder.value)
}

const openRefundDialog = () => {
  refundSelectDialogRef.value?.open(selectedRefund.value)
}

const handleUserSelected = (row: MemberUser) => {
  if (selectedWalletAccount.value?.userId !== row.id) {
    selectedWalletAccount.value = undefined
    formData.value.walletAccountId = undefined
  }
  selectedUser.value = row
  formData.value.userId = row.id
}

const handleOrderSelected = (row: OrderInfo) => {
  selectedOrder.value = row
  formData.value.relatedOrderId = row.id
  if (selectedUnit.value?.orderId !== row.id) {
    selectedUnit.value = undefined
    formData.value.relatedUnitId = undefined
  }
}

const handleUnitSelected = (row: OrderUnit) => {
  selectedUnit.value = row
  formData.value.relatedUnitId = row.id
  if (row.orderId && selectedOrder.value?.id !== row.orderId) {
    formData.value.relatedOrderId = row.orderId
    void loadSelectedOrder(row.orderId)
  }
}

const handleWalletAccountSelected = (row: WalletAccount) => {
  selectedWalletAccount.value = row
  formData.value.walletAccountId = row.id
  if (row.userId && selectedUser.value?.id !== row.userId) {
    formData.value.userId = row.userId
    void loadSelectedUser(row.userId)
  }
}

const handlePayOrderSelected = (row: PayOrderOption) => {
  selectedPayOrder.value = row
  formData.value.relatedPayOrderId = row.id
}

const handleRefundSelected = (row: RefundDetailVO) => {
  selectedRefund.value = row
  formData.value.relatedRefundId = row.id
}

const loadSelectedUser = async (userId?: number) => {
  if (!userId) {
    selectedUser.value = undefined
    return
  }
  const user = await MemberUserApi.getMemberUser(userId)
  selectedUser.value = {
    id: user.id,
    userNo: user.userNo,
    nickname: user.nickname,
    mobile: user.mobile,
    currentRoleCode: user.currentRoleCode,
    status: user.status
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

const loadSelectedWalletAccount = async (walletAccountId?: number) => {
  if (!walletAccountId) {
    selectedWalletAccount.value = undefined
    return
  }
  selectedWalletAccount.value = await WalletAccountApi.getWalletAccount(walletAccountId)
}

const loadSelectedPayOrder = async (payOrderId?: number) => {
  if (!payOrderId) {
    selectedPayOrder.value = undefined
    return
  }
  selectedPayOrder.value = await payOrderSelectDialogRef.value?.loadById(payOrderId)
}

const loadSelectedRefund = async (refundId?: number) => {
  if (!refundId) {
    selectedRefund.value = undefined
    return
  }
  selectedRefund.value = await refundSelectDialogRef.value?.loadById(refundId)
}

const buildFormData = (data: WalletFlowFormData): FormData => ({
  id: data.id,
  flowNo: data.flowNo,
  userId: data.userId,
  walletAccountId: data.walletAccountId,
  bizType: data.bizType,
  flowType: data.flowType,
  changeAmount: data.changeAmount,
  beforeAmount: data.beforeAmount,
  afterAmount: data.afterAmount,
  relatedOrderId: data.relatedOrderId,
  relatedUnitId: data.relatedUnitId,
  relatedPayOrderId: data.relatedPayOrderId,
  relatedRefundId: data.relatedRefundId,
  remark: data.remark
})
</script>
