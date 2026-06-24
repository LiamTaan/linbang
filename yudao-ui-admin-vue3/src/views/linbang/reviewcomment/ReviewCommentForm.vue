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
      <el-form-item label="评价发起人" prop="fromUserId">
        <el-input
          :model-value="fromUserLabel"
          placeholder="请选择评价发起人"
          readonly
          @click="openFromUserDialog"
        >
          <template #append>
            <el-button @click="openFromUserDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="评价目标用户" prop="toUserId">
        <el-input :model-value="toUserLabel" placeholder="请选择评价目标用户" readonly @click="openToUserDialog">
          <template #append>
            <el-button @click="openToUserDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="星级" prop="starLevel">
        <el-rate v-model="formData.starLevel" :max="5" show-score text-color="#ff9900" />
      </el-form-item>
      <el-form-item label="评价内容" prop="content">
        <Editor v-model="formData.content" height="150px" />
      </el-form-item>
      <el-form-item label="是否自动评价" prop="isAutoReview">
        <el-radio-group v-model="formData.isAutoReview">
          <el-radio
            v-for="item in AUTO_REVIEW_OPTIONS"
            :key="String(item.value)"
            :value="item.value"
          >
            {{ item.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio v-for="item in ENABLE_STATUS_OPTIONS" :key="item.value" :value="item.value">
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
  <MemberUserSelectDialog ref="fromUserSelectDialogRef" @selected="handleFromUserSelected" />
  <MemberUserSelectDialog ref="toUserSelectDialogRef" @selected="handleToUserSelected" />
  <OrderSelectDialog ref="orderSelectDialogRef" @selected="handleOrderSelected" />
  <OrderUnitSelectDialog ref="unitSelectDialogRef" @selected="handleUnitSelected" />
</template>
<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { ReviewCommentApi, ReviewComment, ReviewCommentDetail } from '@/api/linbang/reviewcomment'
import { MemberUserApi, type MemberUser } from '@/api/linbang/memberuser'
import { OrderInfoApi, type OrderInfo } from '@/api/linbang/orderinfo'
import { OrderUnitApi, type OrderUnit } from '@/api/linbang/orderunit'
import { AUTO_REVIEW_OPTIONS, ENABLE_STATUS_OPTIONS } from '../utils/display'
import MemberUserSelectDialog from '../memberaddress/MemberUserSelectDialog.vue'
import OrderSelectDialog from '../shared/OrderSelectDialog.vue'
import OrderUnitSelectDialog from '../shared/OrderUnitSelectDialog.vue'

import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
/** 评价 表单 */
defineOptions({ name: 'ReviewCommentForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const fromUserSelectDialogRef = ref()
const toUserSelectDialogRef = ref()
const orderSelectDialogRef = ref()
const unitSelectDialogRef = ref()
const selectedFromUser = ref<MemberUser>()
const selectedToUser = ref<MemberUser>()
const selectedOrder = ref<OrderInfo>()
const selectedUnit = ref<OrderUnit>()
type FormData = {
  id?: number
  orderId?: number
  unitId?: number
  fromUserId?: number
  toUserId?: number
  starLevel?: number
  content?: string
  isAutoReview?: boolean
  status?: string
}

const formatUserLabel = (user?: MemberUser) => {
  if (!user) {
    return ''
  }
  return [user.userNo, user.nickname, user.mobile].filter(Boolean).join(' / ')
}

const fromUserLabel = computed(() => formatUserLabel(selectedFromUser.value))
const toUserLabel = computed(() => formatUserLabel(selectedToUser.value))
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
  orderId: undefined,
  unitId: undefined,
  fromUserId: undefined,
  toUserId: undefined,
  starLevel: undefined,
  content: undefined,
  isAutoReview: undefined,
  status: undefined
})
const formRules = reactive({
  orderId: [{ required: true, message: '关联订单不能为空', trigger: 'change' }],
  fromUserId: [{ required: true, message: '评价发起人不能为空', trigger: 'change' }],
  toUserId: [{ required: true, message: '评价目标用户不能为空', trigger: 'change' }],
  starLevel: [{ required: true, message: '星级不能为空', trigger: 'blur' }],
  isAutoReview: [{ required: true, message: '是否自动评价不能为空', trigger: 'blur' }],
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
      const data = await ReviewCommentApi.getReviewComment(id)
      formData.value = buildFormData(data)
      await Promise.all([
        loadDetailSelections(data),
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
    const data = formData.value as unknown as ReviewComment
    if (formType.value === 'create') {
      await ReviewCommentApi.createReviewComment(data)
      message.success(t('common.createSuccess'))
    } else {
      await ReviewCommentApi.updateReviewComment(data)
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
    orderId: undefined,
    unitId: undefined,
    fromUserId: undefined,
    toUserId: undefined,
    starLevel: undefined,
    content: undefined,
    isAutoReview: undefined,
    status: undefined
  }
  selectedFromUser.value = undefined
  selectedToUser.value = undefined
  selectedOrder.value = undefined
  selectedUnit.value = undefined
  formRef.value?.resetFields()
}

const openFromUserDialog = () => {
  fromUserSelectDialogRef.value?.open(selectedFromUser.value)
}

const openToUserDialog = () => {
  toUserSelectDialogRef.value?.open(selectedToUser.value)
}

const openOrderDialog = () => {
  orderSelectDialogRef.value?.open(selectedOrder.value)
}

const openUnitDialog = () => {
  unitSelectDialogRef.value?.open(selectedUnit.value, formData.value.orderId)
}

const handleFromUserSelected = (row: MemberUser) => {
  selectedFromUser.value = row
  formData.value.fromUserId = row.id
}

const handleToUserSelected = (row: MemberUser) => {
  selectedToUser.value = row
  formData.value.toUserId = row.id
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

const buildFormData = (data: ReviewCommentDetail): FormData => ({
  id: data.id,
  orderId: data.orderId,
  unitId: data.unitId,
  fromUserId: data.fromUserId,
  toUserId: data.toUserId,
  starLevel: data.starLevel,
  content: data.content,
  isAutoReview: data.isAutoReview,
  status: data.status
})

const loadDetailSelections = async (data: ReviewCommentDetail) => {
  const [fromUser, toUser] = await Promise.all([
    loadSelectedUser(data.fromUserId),
    loadSelectedUser(data.toUserId)
  ])
  selectedFromUser.value = fromUser
  selectedToUser.value = toUser
}
</script>
