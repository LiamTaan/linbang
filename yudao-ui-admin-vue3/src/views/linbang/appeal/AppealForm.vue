<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="申诉单号" prop="appealNo">
        <el-input v-model="formData.appealNo" placeholder="请输入申诉单号" />
      </el-form-item>
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
      <el-form-item label="申诉人" prop="userId">
        <el-input :model-value="selectedUserLabel" placeholder="请选择申诉人" readonly @click="openUserDialog">
          <template #append>
            <el-button @click="openUserDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="申诉类型" prop="appealType">
        <el-input v-model="formData.appealType" placeholder="请输入申诉类型" />
      </el-form-item>
      <el-form-item label="申诉内容" prop="content">
        <Editor v-model="formData.content" height="150px" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_APPEAL_STATUS)"
            :key="dict.value"
            :label="dict.value"
          >
            {{ dict.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="审核状态" prop="auditStatus">
        <el-radio-group v-model="formData.auditStatus">
          <el-radio
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_AUDIT_STATUS)"
            :key="dict.value"
            :label="dict.value"
          >
            {{ dict.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="审核人" prop="auditBy">
        <el-input v-model="formData.auditBy" placeholder="请输入审核人" />
      </el-form-item>
      <el-form-item label="审核时间" prop="auditTime">
        <el-date-picker
          v-model="formData.auditTime"
          type="date"
          value-format="x"
          placeholder="选择审核时间"
        />
      </el-form-item>
      <el-form-item label="审核备注" prop="auditRemark">
        <el-input v-model="formData.auditRemark" placeholder="请输入审核备注" />
      </el-form-item>
      <el-form-item label="驳回原因" prop="rejectReason">
        <el-input v-model="formData.rejectReason" placeholder="请输入驳回原因" />
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
</template>
<script setup lang="ts">
import { getStrDictOptions, DICT_TYPE } from '@/utils/dict'
import { AppealApi, Appeal, AppealDetail } from '@/api/linbang/appeal'
import { MemberUserApi, type MemberUser } from '@/api/linbang/memberuser'
import { OrderInfoApi, type OrderInfo } from '@/api/linbang/orderinfo'
import { OrderUnitApi, type OrderUnit } from '@/api/linbang/orderunit'
import MemberUserSelectDialog from '../memberaddress/MemberUserSelectDialog.vue'
import OrderSelectDialog from '../shared/OrderSelectDialog.vue'
import OrderUnitSelectDialog from '../shared/OrderUnitSelectDialog.vue'

import { computed, reactive, ref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
/** 申诉 表单 */
defineOptions({ name: 'AppealForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const userSelectDialogRef = ref()
const orderSelectDialogRef = ref()
const unitSelectDialogRef = ref()
const selectedUser = ref<MemberUser>()
const selectedOrder = ref<OrderInfo>()
const selectedUnit = ref<OrderUnit>()
type FormData = {
  id?: number
  appealNo?: string
  orderId?: number
  unitId?: number
  userId?: number
  appealType?: string
  content?: string
  status?: string
  auditStatus?: string
  auditBy?: number
  auditTime?: string | number
  auditRemark?: string
  rejectReason?: string
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
  return [selectedUnit.value.unitNo, selectedUnit.value.unitTitle].filter(Boolean).join(' / ')
})

const formData = ref<FormData>({
  id: undefined,
  appealNo: undefined,
  orderId: undefined,
  unitId: undefined,
  userId: undefined,
  appealType: undefined,
  content: undefined,
  status: undefined,
  auditStatus: undefined,
  auditBy: undefined,
  auditTime: undefined,
  auditRemark: undefined,
  rejectReason: undefined
})
const formRules = reactive({
  appealNo: [{ required: true, message: '申诉单号不能为空', trigger: 'blur' }],
  orderId: [{ required: true, message: '关联订单不能为空', trigger: 'change' }],
  userId: [{ required: true, message: '申诉人不能为空', trigger: 'change' }],
  appealType: [{ required: true, message: '申诉类型不能为空', trigger: 'change' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'blur' }],
  auditStatus: [{ required: true, message: '审核状态不能为空', trigger: 'blur' }]
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
      const data = await AppealApi.getAppeal(id)
      formData.value = buildFormData(data)
      await Promise.all([
        loadSelectedUser(data.userId),
        loadSelectedOrder(data.orderId),
        loadSelectedUnit(data.unitId)
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
    const data = formData.value as unknown as Appeal
    if (formType.value === 'create') {
      await AppealApi.createAppeal(data)
      message.success(t('common.createSuccess'))
    } else {
      await AppealApi.updateAppeal(data)
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
  formData.value = {
    id: undefined,
    appealNo: undefined,
    orderId: undefined,
    unitId: undefined,
    userId: undefined,
    appealType: undefined,
    content: undefined,
    status: undefined,
    auditStatus: undefined,
    auditBy: undefined,
    auditTime: undefined,
    auditRemark: undefined,
    rejectReason: undefined
  }
  selectedUser.value = undefined
  selectedOrder.value = undefined
  selectedUnit.value = undefined
  formRef.value?.resetFields()
}

const openUserDialog = () => {
  userSelectDialogRef.value?.open(selectedUser.value)
}

const openOrderDialog = () => {
  orderSelectDialogRef.value?.open(selectedOrder.value)
}

const openUnitDialog = () => {
  unitSelectDialogRef.value?.open(selectedUnit.value, formData.value.orderId)
}

const handleUserSelected = (row: MemberUser) => {
  selectedUser.value = row
  formData.value.userId = row.id
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

const buildFormData = (data: AppealDetail): FormData => ({
  id: data.id,
  appealNo: data.appealNo,
  orderId: data.orderId,
  unitId: data.unitId,
  userId: data.userId,
  appealType: data.appealType,
  content: data.content,
  status: data.status,
  auditStatus: data.auditStatus,
  auditBy: data.auditBy,
  auditTime: data.auditTime as string | number | undefined,
  auditRemark: data.auditRemark,
  rejectReason: data.rejectReason
})
</script>
