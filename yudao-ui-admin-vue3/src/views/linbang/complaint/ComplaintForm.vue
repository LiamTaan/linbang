<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="投诉单号" prop="complaintNo">
        <el-input v-model="formData.complaintNo" placeholder="请输入投诉单号" />
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
      <el-form-item label="投诉人" prop="complainantUserId">
        <el-input
          :model-value="complainantUserLabel"
          placeholder="请选择投诉人"
          readonly
          @click="openComplainantDialog"
        >
          <template #append>
            <el-button @click="openComplainantDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="被投诉人" prop="respondentUserId">
        <el-input
          :model-value="respondentUserLabel"
          placeholder="请选择被投诉人"
          readonly
          @click="openRespondentDialog"
        >
          <template #append>
            <el-button @click="openRespondentDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="投诉类型" prop="complaintType">
        <el-select v-model="formData.complaintType" placeholder="请选择投诉类型" class="!w-full">
          <el-option
            v-for="item in COMPLAINT_TYPE_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="投诉内容" prop="content">
        <Editor v-model="formData.content" height="150px" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_COMPLAINT_STATUS)"
            :key="dict.value"
            :label="dict.value"
          >
            {{ dict.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="处理人" prop="handleBy">
        <el-input v-model="formData.handleBy" placeholder="请输入处理人" />
      </el-form-item>
      <el-form-item label="处理时间" prop="handleTime">
        <el-date-picker
          v-model="formData.handleTime"
          type="date"
          value-format="x"
          placeholder="选择处理时间"
        />
      </el-form-item>
      <el-form-item label="处理结果" prop="resultDesc">
        <el-input v-model="formData.resultDesc" placeholder="请输入处理结果" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
  <MemberUserSelectDialog ref="complainantUserSelectDialogRef" @selected="handleComplainantSelected" />
  <MemberUserSelectDialog ref="respondentUserSelectDialogRef" @selected="handleRespondentSelected" />
  <OrderSelectDialog ref="orderSelectDialogRef" @selected="handleOrderSelected" />
  <OrderUnitSelectDialog ref="unitSelectDialogRef" @selected="handleUnitSelected" />
</template>
<script setup lang="ts">
import { getStrDictOptions, DICT_TYPE } from '@/utils/dict'
import { ComplaintApi, Complaint, ComplaintDetail } from '@/api/linbang/complaint'
import { MemberUserApi, type MemberUser } from '@/api/linbang/memberuser'
import { OrderInfoApi, type OrderInfo } from '@/api/linbang/orderinfo'
import { OrderUnitApi, type OrderUnit } from '@/api/linbang/orderunit'
import { COMPLAINT_TYPE_OPTIONS } from '../utils/display'
import MemberUserSelectDialog from '../memberaddress/MemberUserSelectDialog.vue'
import OrderSelectDialog from '../shared/OrderSelectDialog.vue'
import OrderUnitSelectDialog from '../shared/OrderUnitSelectDialog.vue'

import { computed, reactive, ref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
/** 投诉 表单 */
defineOptions({ name: 'ComplaintForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const complainantUserSelectDialogRef = ref()
const respondentUserSelectDialogRef = ref()
const orderSelectDialogRef = ref()
const unitSelectDialogRef = ref()
const selectedComplainantUser = ref<MemberUser>()
const selectedRespondentUser = ref<MemberUser>()
const selectedOrder = ref<OrderInfo>()
const selectedUnit = ref<OrderUnit>()
type FormData = {
  id?: number
  complaintNo?: string
  orderId?: number
  unitId?: number
  complainantUserId?: number
  respondentUserId?: number
  complaintType?: string
  content?: string
  status?: string
  handleBy?: number
  handleTime?: string | number
  resultDesc?: string
}

const formatUserLabel = (user?: MemberUser) => {
  if (!user) {
    return ''
  }
  return [user.userNo, user.nickname, user.mobile].filter(Boolean).join(' / ')
}

const complainantUserLabel = computed(() => formatUserLabel(selectedComplainantUser.value))
const respondentUserLabel = computed(() => formatUserLabel(selectedRespondentUser.value))
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
  complaintNo: undefined,
  orderId: undefined,
  unitId: undefined,
  complainantUserId: undefined,
  respondentUserId: undefined,
  complaintType: undefined,
  content: undefined,
  status: undefined,
  handleBy: undefined,
  handleTime: undefined,
  resultDesc: undefined
})
const formRules = reactive({
  complaintNo: [{ required: true, message: '投诉单号不能为空', trigger: 'blur' }],
  orderId: [{ required: true, message: '关联订单不能为空', trigger: 'change' }],
  complainantUserId: [{ required: true, message: '投诉人不能为空', trigger: 'change' }],
  respondentUserId: [{ required: true, message: '被投诉人不能为空', trigger: 'change' }],
  complaintType: [{ required: true, message: '投诉类型不能为空', trigger: 'change' }],
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
      const data = await ComplaintApi.getComplaint(id)
      formData.value = buildFormData(data)
      const [complainantUser, respondentUser] = await Promise.all([
        loadSelectedUser(data.complainantUserId),
        loadSelectedUser(data.respondentUserId)
      ])
      selectedComplainantUser.value = complainantUser
      selectedRespondentUser.value = respondentUser
      await Promise.all([loadSelectedOrder(data.orderId), loadSelectedUnit(data.unitId)])
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
    const data = formData.value as unknown as Complaint
    if (formType.value === 'create') {
      await ComplaintApi.createComplaint(data)
      message.success(t('common.createSuccess'))
    } else {
      await ComplaintApi.updateComplaint(data)
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
    complaintNo: undefined,
    orderId: undefined,
    unitId: undefined,
    complainantUserId: undefined,
    respondentUserId: undefined,
    complaintType: undefined,
    content: undefined,
    status: undefined,
    handleBy: undefined,
    handleTime: undefined,
    resultDesc: undefined
  }
  selectedComplainantUser.value = undefined
  selectedRespondentUser.value = undefined
  selectedOrder.value = undefined
  selectedUnit.value = undefined
  formRef.value?.resetFields()
}

const openComplainantDialog = () => {
  complainantUserSelectDialogRef.value?.open(selectedComplainantUser.value)
}

const openRespondentDialog = () => {
  respondentUserSelectDialogRef.value?.open(selectedRespondentUser.value)
}

const openOrderDialog = () => {
  orderSelectDialogRef.value?.open(selectedOrder.value)
}

const openUnitDialog = () => {
  unitSelectDialogRef.value?.open(selectedUnit.value, formData.value.orderId)
}

const handleComplainantSelected = (row: MemberUser) => {
  selectedComplainantUser.value = row
  formData.value.complainantUserId = row.id
}

const handleRespondentSelected = (row: MemberUser) => {
  selectedRespondentUser.value = row
  formData.value.respondentUserId = row.id
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
    return undefined
  }
  const user = await MemberUserApi.getMemberUser(userId)
  return {
    id: user.id,
    userNo: user.userNo,
    nickname: user.nickname,
    mobile: user.mobile,
    currentRoleCode: user.currentRoleCode,
    status: user.status
  } as MemberUser
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

const buildFormData = (data: ComplaintDetail): FormData => ({
  id: data.id,
  complaintNo: data.complaintNo,
  orderId: data.orderId,
  unitId: data.unitId,
  complainantUserId: data.complainantUserId,
  respondentUserId: data.respondentUserId,
  complaintType: data.complaintType,
  content: data.content,
  status: data.status,
  handleBy: data.handleBy,
  handleTime: data.handleTime as string | number | undefined,
  resultDesc: data.resultDesc
})
</script>
