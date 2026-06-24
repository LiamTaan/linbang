<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="订单号" prop="orderNo">
        <el-input v-model="formData.orderNo" placeholder="请输入订单号" />
      </el-form-item>
      <el-form-item label="下单用户" prop="userId">
        <el-input :model-value="selectedUserLabel" placeholder="请选择用户" readonly @click="openUserDialog">
          <template #append>
            <el-button @click="openUserDialog">选择</el-button>
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
      <el-form-item label="服务类目" prop="categoryId">
        <el-input :model-value="selectedCategoryLabel" placeholder="请选择服务类目" readonly @click="openCategoryDialog">
          <template #append>
            <el-button @click="openCategoryDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="订单标题" prop="title">
        <el-input v-model="formData.title" placeholder="请输入订单标题" />
      </el-form-item>
      <el-form-item label="计价方式" prop="pricingMode">
        <el-select v-model="formData.pricingMode" placeholder="请选择计价方式" clearable>
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_PRICING_MODE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="预算金额" prop="budgetAmount">
        <el-input v-model="formData.budgetAmount" placeholder="请输入预算金额" />
      </el-form-item>
      <el-form-item label="订单金额" prop="orderAmount">
        <el-input v-model="formData.orderAmount" placeholder="请输入订单金额" />
      </el-form-item>
      <el-form-item label="工期描述" prop="serviceDurationDesc">
        <el-input v-model="formData.serviceDurationDesc" placeholder="请输入工期描述" />
      </el-form-item>
      <el-form-item label="数量" prop="quantity">
        <el-input v-model="formData.quantity" placeholder="请输入数量" />
      </el-form-item>
      <el-form-item label="需求描述" prop="requireDesc">
        <el-input v-model="formData.requireDesc" placeholder="请输入需求描述" />
      </el-form-item>
      <el-form-item label="服务地址" prop="addressId">
        <el-input :model-value="selectedAddressLabel" placeholder="请选择服务地址" readonly @click="openAddressDialog">
          <template #append>
            <el-button @click="openAddressDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="地址区域">
        <el-input :model-value="selectedAddressRegion" placeholder="选择服务地址后自动带出" readonly />
      </el-form-item>
      <el-form-item label="详细地址">
        <el-input :model-value="formData.detailAddress || ''" placeholder="选择服务地址后自动带出" readonly />
      </el-form-item>
      <el-form-item label="地图坐标">
        <el-input :model-value="selectedAddressCoordinate" placeholder="选择服务地址后自动带出" readonly />
      </el-form-item>
      <el-form-item label="是否开票" prop="needInvoice">
        <el-radio-group v-model="formData.needInvoice">
          <el-radio
            v-for="item in BOOLEAN_YES_NO_OPTIONS"
            :key="`invoice-${String(item.value)}`"
            :value="item.value"
          >
            {{ item.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="是否拆单" prop="needSplit">
        <el-radio-group v-model="formData.needSplit">
          <el-radio
            v-for="item in BOOLEAN_YES_NO_OPTIONS"
            :key="`split-${String(item.value)}`"
            :value="item.value"
          >
            {{ item.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="拆单状态" prop="splitStatus">
        <el-select v-model="formData.splitStatus" placeholder="请选择拆单状态">
          <el-option
            v-for="item in SPLIT_STATUS_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="协议是否确认" prop="agreementConfirmed">
        <el-radio-group v-model="formData.agreementConfirmed">
          <el-radio :value="true">已确认</el-radio>
          <el-radio :value="false">未确认</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="支付订单" prop="payOrderId">
        <el-input :model-value="selectedPayOrderLabel" placeholder="请选择支付订单" readonly @click="openPayOrderDialog">
          <template #append>
            <el-button @click="openPayOrderDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="订单状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_ORDER_STATUS)"
            :key="dict.value"
            :label="dict.value"
          >
            {{ dict.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
  <MemberUserSelectDialog ref="userSelectDialogRef" @selected="handleUserSelected" />
  <MerchantInfoSelectDialog ref="merchantSelectDialogRef" @selected="handleMerchantSelected" />
  <ServiceCategorySelectDialog ref="categorySelectDialogRef" @selected="handleCategorySelected" />
  <MemberAddressSelectDialog ref="addressSelectDialogRef" @selected="handleAddressSelected" />
  <PayOrderSelectDialog ref="payOrderSelectDialogRef" @selected="handlePayOrderSelected" />
</template>
<script setup lang="ts">
import { getStrDictOptions, DICT_TYPE } from '@/utils/dict'
import { OrderInfoApi, OrderInfo, OrderInfoDetail } from '@/api/linbang/orderinfo'
import { MerchantInfoApi, type MerchantInfo, type MerchantInfoDetail } from '@/api/linbang/merchantinfo'
import {
  MerchantServiceCategoryApi,
  type MerchantServiceCategory
} from '@/api/linbang/merchantcategory'
import { MemberUserAddressApi, type MemberUserAddress } from '@/api/linbang/memberaddress'
import { BOOLEAN_YES_NO_OPTIONS, SPLIT_STATUS_OPTIONS } from '../utils/display'
import type { PayOrderOption } from '../shared/PayOrderSelectDialog.vue'

import { computed, reactive, ref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
import { MemberUserApi, type MemberUser, type MemberUserDetail } from '@/api/linbang/memberuser'
import MemberUserSelectDialog from '../memberaddress/MemberUserSelectDialog.vue'
import MerchantInfoSelectDialog from '../shared/MerchantInfoSelectDialog.vue'
import ServiceCategorySelectDialog from '../shared/ServiceCategorySelectDialog.vue'
import MemberAddressSelectDialog from '../shared/MemberAddressSelectDialog.vue'
import PayOrderSelectDialog from '../shared/PayOrderSelectDialog.vue'
/** 订单主 表单 */
defineOptions({ name: 'OrderInfoForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const userSelectDialogRef = ref()
const merchantSelectDialogRef = ref()
const categorySelectDialogRef = ref()
const addressSelectDialogRef = ref()
const payOrderSelectDialogRef = ref()
type FormData = {
  id?: number
  orderNo?: string
  userId?: number
  merchantId?: number
  categoryId?: number
  title?: string
  pricingMode?: string
  budgetAmount?: number
  orderAmount?: number
  serviceDurationDesc?: string
  quantity?: number
  requireDesc?: string
  addressId?: number
  province?: string
  city?: string
  district?: string
  street?: string
  detailAddress?: string
  longitude?: number
  latitude?: number
  needInvoice?: boolean
  needSplit?: boolean
  splitStatus?: string
  agreementConfirmed?: boolean
  payOrderId?: number
  status?: string
}
const selectedUser = ref<MemberUser>()
const selectedMerchant = ref<MerchantInfo>()
const selectedCategory = ref<MerchantServiceCategory>()
const selectedAddress = ref<MemberUserAddress>()
const selectedPayOrder = ref<PayOrderOption>()

const formData = ref<FormData>({
  id: undefined,
  orderNo: undefined,
  userId: undefined,
  merchantId: undefined,
  categoryId: undefined,
  title: undefined,
  pricingMode: undefined,
  budgetAmount: undefined,
  orderAmount: undefined,
  serviceDurationDesc: undefined,
  quantity: undefined,
  requireDesc: undefined,
  addressId: undefined,
  province: undefined,
  city: undefined,
  district: undefined,
  street: undefined,
  detailAddress: undefined,
  longitude: undefined,
  latitude: undefined,
  needInvoice: undefined,
  needSplit: undefined,
  splitStatus: undefined,
  agreementConfirmed: undefined,
  payOrderId: undefined,
  status: undefined
})
const formRules = reactive({
  orderNo: [{ required: true, message: '订单号不能为空', trigger: 'blur' }],
  userId: [{ required: true, message: '下单用户不能为空', trigger: 'change' }],
  merchantId: [{ required: true, message: '服务商不能为空', trigger: 'change' }],
  categoryId: [{ required: true, message: '服务类目不能为空', trigger: 'change' }],
  title: [{ required: true, message: '订单标题不能为空', trigger: 'blur' }],
  pricingMode: [{ required: true, message: '计价方式不能为空', trigger: 'change' }],
  addressId: [{ required: true, message: '服务地址不能为空', trigger: 'change' }],
  needInvoice: [{ required: true, message: '是否开票不能为空', trigger: 'blur' }],
  needSplit: [{ required: true, message: '是否拆单不能为空', trigger: 'blur' }],
  agreementConfirmed: [{ required: true, message: '协议是否确认不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '订单状态不能为空', trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

const selectedUserLabel = computed(() => {
  if (!selectedUser.value) {
    return ''
  }
  return [selectedUser.value.userNo, selectedUser.value.nickname, selectedUser.value.mobile]
    .filter(Boolean)
    .join(' / ')
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

const selectedCategoryLabel = computed(() => {
  if (!selectedCategory.value) {
    return ''
  }
  return [
    selectedCategory.value.categoryName,
    selectedCategory.value.categoryLevel && `L${selectedCategory.value.categoryLevel}`
  ]
    .filter(Boolean)
    .join(' / ')
})

const selectedAddressLabel = computed(() => {
  if (!selectedAddress.value) {
    return ''
  }
  return [
    selectedAddress.value.receiverName,
    selectedAddress.value.receiverMobile,
    [
      selectedAddress.value.province,
      selectedAddress.value.city,
      selectedAddress.value.district,
      selectedAddress.value.street,
      selectedAddress.value.detailAddress
    ]
      .filter(Boolean)
      .join(' / ')
  ]
    .filter(Boolean)
    .join(' / ')
})

const selectedAddressRegion = computed(() => {
  return [formData.value.province, formData.value.city, formData.value.district, formData.value.street]
    .filter(Boolean)
    .join(' / ')
})

const selectedAddressCoordinate = computed(() => {
  const coordinates = [formData.value.longitude, formData.value.latitude]
    .filter((item): item is number => item !== undefined && item !== null)
    .map((item) => String(item))
  return coordinates.join(' / ')
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
      const data = await OrderInfoApi.getOrderInfo(id)
      formData.value = buildFormData(data)
      await loadSelectedUser(data.userId)
      await loadSelectedMerchant(data.merchantId)
      await loadSelectedCategory(data.categoryId)
      await loadSelectedAddress(data.addressId)
      await loadSelectedPayOrder(data.payOrderId, data)
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
    const data = formData.value as unknown as OrderInfo
    if (formType.value === 'create') {
      await OrderInfoApi.createOrderInfo(data)
      message.success(t('common.createSuccess'))
    } else {
      await OrderInfoApi.updateOrderInfo(data)
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
    orderNo: undefined,
    userId: undefined,
    merchantId: undefined,
    categoryId: undefined,
    title: undefined,
    pricingMode: undefined,
    budgetAmount: undefined,
    orderAmount: undefined,
    serviceDurationDesc: undefined,
    quantity: undefined,
    requireDesc: undefined,
    addressId: undefined,
    province: undefined,
    city: undefined,
    district: undefined,
    street: undefined,
    detailAddress: undefined,
    longitude: undefined,
    latitude: undefined,
    needInvoice: undefined,
    needSplit: undefined,
    splitStatus: undefined,
    agreementConfirmed: undefined,
    payOrderId: undefined,
    status: undefined
  }
  selectedUser.value = undefined
  selectedMerchant.value = undefined
  selectedCategory.value = undefined
  selectedAddress.value = undefined
  selectedPayOrder.value = undefined
  formRef.value?.resetFields()
}

const buildFormData = (data: OrderInfoDetail): FormData => ({
  id: data.id,
  orderNo: data.orderNo,
  userId: data.userId,
  merchantId: data.merchantId,
  categoryId: data.categoryId,
  title: data.title,
  pricingMode: data.pricingMode,
  budgetAmount: data.budgetAmount,
  orderAmount: data.orderAmount,
  serviceDurationDesc: data.serviceDurationDesc,
  quantity: data.quantity,
  requireDesc: data.requireDesc,
  addressId: data.addressId,
  province: data.province,
  city: data.city,
  district: data.district,
  street: data.street,
  detailAddress: data.detailAddress,
  longitude: data.longitude,
  latitude: data.latitude,
  needInvoice: data.needInvoice,
  needSplit: data.needSplit,
  splitStatus: data.splitStatus,
  agreementConfirmed: data.agreementConfirmed,
  payOrderId: data.payOrderId,
  status: data.status
})

const openUserDialog = () => {
  userSelectDialogRef.value?.open(selectedUser.value)
}

const openMerchantDialog = () => {
  merchantSelectDialogRef.value?.open(selectedMerchant.value)
}

const openCategoryDialog = () => {
  categorySelectDialogRef.value?.open(selectedCategory.value)
}

const openAddressDialog = () => {
  addressSelectDialogRef.value?.open(selectedAddress.value, selectedUser.value)
}

const openPayOrderDialog = () => {
  payOrderSelectDialogRef.value?.open(selectedPayOrder.value)
}

const handleUserSelected = (row: MemberUser) => {
  if (selectedAddress.value?.userId !== row.id) {
    selectedAddress.value = undefined
    formData.value.addressId = undefined
    formData.value.province = undefined
    formData.value.city = undefined
    formData.value.district = undefined
    formData.value.street = undefined
    formData.value.detailAddress = undefined
    formData.value.longitude = undefined
    formData.value.latitude = undefined
  }
  selectedUser.value = row
  formData.value.userId = row.id
}

const handleMerchantSelected = (row: MerchantInfo) => {
  selectedMerchant.value = row
  formData.value.merchantId = row.id
}

const handleCategorySelected = (row: MerchantServiceCategory) => {
  selectedCategory.value = row
  formData.value.categoryId = row.id
}

const handleAddressSelected = (row: MemberUserAddress) => {
  selectedAddress.value = row
  formData.value.addressId = row.id
  formData.value.province = row.province
  formData.value.city = row.city
  formData.value.district = row.district
  formData.value.street = row.street
  formData.value.detailAddress = row.detailAddress
  formData.value.longitude = row.longitude
  formData.value.latitude = row.latitude
  if (row.userId && selectedUser.value?.id !== row.userId) {
    formData.value.userId = row.userId
    void loadSelectedUser(row.userId)
  }
}

const handlePayOrderSelected = (row: PayOrderOption) => {
  selectedPayOrder.value = row
  formData.value.payOrderId = row.id
}

const loadSelectedUser = async (userId?: number) => {
  if (!userId) {
    selectedUser.value = undefined
    return
  }
  const detail = await MemberUserApi.getMemberUser(userId)
  selectedUser.value = buildSelectedUser(detail)
}

const loadSelectedMerchant = async (merchantId?: number) => {
  if (!merchantId) {
    selectedMerchant.value = undefined
    return
  }
  const detail = await MerchantInfoApi.getMerchantInfo(merchantId)
  selectedMerchant.value = buildSelectedMerchant(detail)
}

const loadSelectedCategory = async (categoryId?: number) => {
  if (!categoryId) {
    selectedCategory.value = undefined
    return
  }
  selectedCategory.value = await MerchantServiceCategoryApi.getMerchantServiceCategory(categoryId)
}

const loadSelectedAddress = async (addressId?: number) => {
  if (!addressId) {
    selectedAddress.value = undefined
    return
  }
  selectedAddress.value = await MemberUserAddressApi.getMemberUserAddress(addressId)
}

const loadSelectedPayOrder = async (payOrderId?: number, detail?: OrderInfoDetail) => {
  if (!payOrderId) {
    selectedPayOrder.value = undefined
    return
  }
  if (detail?.payRecord) {
    selectedPayOrder.value = {
      id: payOrderId,
      merchantOrderId: detail.payRecord.merchantOrderId,
      subject: detail.payRecord.subject,
      price: detail.payRecord.price,
      refundPrice: detail.payRecord.refundPrice,
      status: detail.payRecord.status
    }
    return
  }
  selectedPayOrder.value = await payOrderSelectDialogRef.value?.loadById(payOrderId)
}

const buildSelectedUser = (detail: MemberUserDetail): MemberUser => ({
  id: detail.id,
  userNo: detail.userNo,
  mobile: detail.mobile,
  nickname: detail.nickname,
  avatar: detail.avatar,
  gender: detail.gender,
  birthday: detail.birthday,
  registerSource: detail.registerSource,
  currentRoleCode: detail.currentRoleCode,
  status: detail.status,
  lastLoginTime: detail.lastLoginTime,
  lastLoginIp: detail.lastLoginIp,
  remark: detail.remark,
  createTime: detail.createTime
})

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
</script>
