<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="用户" prop="userId">
        <el-input :model-value="selectedUserLabel" placeholder="请选择用户" readonly @click="openUserDialog">
          <template #append>
            <el-button @click="openUserDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="银行名称" prop="bankName">
        <el-input v-model="formData.bankName" placeholder="请输入银行名称" />
      </el-form-item>
      <el-form-item label="银行编码" prop="bankCode">
        <el-input v-model="formData.bankCode" placeholder="请输入银行编码" />
      </el-form-item>
      <el-form-item label="加密卡号" prop="cardNoEncrypt">
        <el-input v-model="formData.cardNoEncrypt" placeholder="请输入加密卡号" />
      </el-form-item>
      <el-form-item label="脱敏卡号" prop="cardNoMask">
        <el-input v-model="formData.cardNoMask" placeholder="请输入脱敏卡号" />
      </el-form-item>
      <el-form-item label="开户名" prop="accountName">
        <el-input v-model="formData.accountName" placeholder="请输入开户名" />
      </el-form-item>
      <el-form-item label="预留手机号" prop="reservedMobile">
        <el-input v-model="formData.reservedMobile" placeholder="请输入预留手机号" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio v-for="item in ENABLE_STATUS_OPTIONS" :key="item.value" :value="item.value">
            {{ item.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="是否默认" prop="isDefault">
        <el-radio-group v-model="formData.isDefault">
          <el-radio
            v-for="item in BOOLEAN_YES_NO_OPTIONS"
            :key="String(item.value)"
            :value="item.value"
          >
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
  <MemberUserSelectDialog ref="userSelectDialogRef" @selected="handleUserSelected" />
</template>
<script setup lang="ts">
import { WalletBankCardApi, type WalletBankCardFormData } from '@/api/linbang/walletbankcard'
import { MemberUserApi, type MemberUser } from '@/api/linbang/memberuser'
import { BOOLEAN_YES_NO_OPTIONS, ENABLE_STATUS_OPTIONS } from '../utils/display'
import MemberUserSelectDialog from '../memberaddress/MemberUserSelectDialog.vue'

import { computed, reactive, ref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
/** 用户银行卡 表单 */
defineOptions({ name: 'WalletBankCardForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const userSelectDialogRef = ref()
const selectedUser = ref<MemberUser>()
type FormData = {
  id?: number
  userId?: number
  bankName?: string
  bankCode?: string
  cardNoEncrypt?: string
  cardNoMask?: string
  accountName?: string
  reservedMobile?: string
  status?: string
  isDefault?: boolean
}

const selectedUserLabel = computed(() => {
  if (!selectedUser.value) {
    return ''
  }
  return [selectedUser.value.userNo, selectedUser.value.nickname, selectedUser.value.mobile]
    .filter(Boolean)
    .join(' / ')
})

const createEmptyFormData = (): FormData => ({
  id: undefined,
  userId: undefined,
  bankName: undefined,
  bankCode: undefined,
  cardNoEncrypt: undefined,
  cardNoMask: undefined,
  accountName: undefined,
  reservedMobile: undefined,
  status: undefined,
  isDefault: undefined
})
const formData = ref<FormData>(createEmptyFormData())
const formRules = reactive({
  userId: [{ required: true, message: '用户不能为空', trigger: 'change' }],
  bankName: [{ required: true, message: '银行名称不能为空', trigger: 'blur' }],
  cardNoEncrypt: [{ required: true, message: '加密卡号不能为空', trigger: 'blur' }],
  cardNoMask: [{ required: true, message: '脱敏卡号不能为空', trigger: 'blur' }],
  accountName: [{ required: true, message: '开户名不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'blur' }],
  isDefault: [{ required: true, message: '是否默认不能为空', trigger: 'blur' }]
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
      const detail = await WalletBankCardApi.getWalletBankCard(id)
      formData.value = buildFormData(detail)
      await loadSelectedUser(detail.userId)
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
    const data = formData.value as WalletBankCardFormData
    if (formType.value === 'create') {
      await WalletBankCardApi.createWalletBankCard(data)
      message.success(t('common.createSuccess'))
    } else {
      await WalletBankCardApi.updateWalletBankCard(data)
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
  formRef.value?.resetFields()
}

const openUserDialog = () => {
  userSelectDialogRef.value?.open(selectedUser.value)
}

const handleUserSelected = (row: MemberUser) => {
  selectedUser.value = row
  formData.value.userId = row.id
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

const buildFormData = (data: WalletBankCardFormData): FormData => ({
  id: data.id,
  userId: data.userId,
  bankName: data.bankName,
  bankCode: data.bankCode,
  cardNoEncrypt: data.cardNoEncrypt,
  cardNoMask: data.cardNoMask,
  accountName: data.accountName,
  reservedMobile: data.reservedMobile,
  status: data.status,
  isDefault: data.isDefault
})
</script>
