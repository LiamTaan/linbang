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
      <el-form-item label="服务商名称" prop="merchantName">
        <el-input v-model="formData.merchantName" placeholder="请输入服务商名称" />
      </el-form-item>
      <el-form-item label="联系人" prop="contactName">
        <el-input v-model="formData.contactName" placeholder="请输入联系人" />
      </el-form-item>
      <el-form-item label="联系人手机号" prop="contactMobile">
        <el-input v-model="formData.contactMobile" placeholder="请输入联系人手机号" />
      </el-form-item>
      <el-form-item label="服务范围说明" prop="serviceScopeDesc">
        <el-input v-model="formData.serviceScopeDesc" placeholder="请输入服务范围说明" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio v-for="item in ENABLE_STATUS_OPTIONS" :key="item.value" :value="item.value">
            {{ item.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="接单状态" prop="acceptStatus">
        <el-radio-group v-model="formData.acceptStatus">
          <el-radio v-for="item in ACCEPT_STATUS_OPTIONS" :key="item.value" :value="item.value">
            {{ item.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="自动派单" prop="dispatchEnabled">
        <el-switch v-model="formData.dispatchEnabled" />
      </el-form-item>
      <el-form-item label="信用分" prop="creditScore">
        <el-input v-model="formData.creditScore" placeholder="请输入信用分" />
      </el-form-item>
      <el-form-item label="信用等级" prop="creditLevel">
        <el-input v-model="formData.creditLevel" placeholder="请输入信用等级" />
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
import { computed, reactive, ref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
import { MerchantInfoApi, MerchantInfo, MerchantInfoDetail } from '@/api/linbang/merchantinfo'
import { MemberUserApi, type MemberUser, type MemberUserDetail } from '@/api/linbang/memberuser'
import { ACCEPT_STATUS_OPTIONS, ENABLE_STATUS_OPTIONS } from '../utils/display'
import MemberUserSelectDialog from '../memberaddress/MemberUserSelectDialog.vue'

/** 服务商信息表 表单 */
defineOptions({ name: 'MerchantInfoForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const userSelectDialogRef = ref()
type FormData = {
  id?: number
  userId?: number
  merchantName?: string
  contactName?: string
  contactMobile?: string
  serviceScopeDesc?: string
  status?: string
  acceptStatus?: string
  dispatchEnabled?: boolean
  creditScore?: number
  creditLevel?: string
}

const selectedUser = ref<MemberUser>()

const formData = ref<FormData>({
  id: undefined,
  userId: undefined,
  merchantName: undefined,
  contactName: undefined,
  contactMobile: undefined,
  serviceScopeDesc: undefined,
  status: undefined,
  acceptStatus: undefined,
  dispatchEnabled: true,
  creditScore: undefined,
  creditLevel: undefined
})
const formRules = reactive({
  userId: [{ required: true, message: '用户不能为空', trigger: 'change' }],
  merchantName: [{ required: true, message: '服务商名称不能为空', trigger: 'blur' }],
  contactName: [{ required: true, message: '联系人不能为空', trigger: 'blur' }],
  contactMobile: [{ required: true, message: '联系人手机号不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'blur' }],
  acceptStatus: [{ required: true, message: '接单状态不能为空', trigger: 'blur' }],
  dispatchEnabled: [{ required: true, message: '自动派单不能为空', trigger: 'change' }],
  creditScore: [{ required: true, message: '信用分不能为空', trigger: 'blur' }]
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
      const data = await MerchantInfoApi.getMerchantInfo(id)
      formData.value = buildFormData(data)
      await loadSelectedUser(data.userId)
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
    const data = formData.value as unknown as MerchantInfo
    if (formType.value === 'create') {
      await MerchantInfoApi.createMerchantInfo(data)
      message.success(t('common.createSuccess'))
    } else {
      await MerchantInfoApi.updateMerchantInfo(data)
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
    userId: undefined,
    merchantName: undefined,
    contactName: undefined,
    contactMobile: undefined,
    serviceScopeDesc: undefined,
    status: undefined,
    acceptStatus: undefined,
    dispatchEnabled: true,
    creditScore: undefined,
    creditLevel: undefined
  }
  selectedUser.value = undefined
  formRef.value?.resetFields()
}

const buildFormData = (data: MerchantInfoDetail): FormData => ({
  id: data.id,
  userId: data.userId,
  merchantName: data.merchantName,
  contactName: data.contactName,
  contactMobile: data.contactMobile,
  serviceScopeDesc: data.serviceScopeDesc,
  status: data.status,
  acceptStatus: data.acceptStatus,
  dispatchEnabled: data.dispatchEnabled,
  creditScore: data.creditScore,
  creditLevel: data.creditLevel
})

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
  const detail = await MemberUserApi.getMemberUser(userId)
  selectedUser.value = buildSelectedUser(detail)
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
</script>
