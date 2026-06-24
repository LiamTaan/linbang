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
      <el-form-item label="角色编码" prop="roleCode">
        <el-select v-model="formData.roleCode" placeholder="请选择角色编码">
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_ROLE_CODE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="总资产" prop="totalAmount">
        <el-input v-model="formData.totalAmount" placeholder="请输入总资产" />
      </el-form-item>
      <el-form-item label="可提现金额" prop="availableAmount">
        <el-input v-model="formData.availableAmount" placeholder="请输入可提现金额" />
      </el-form-item>
      <el-form-item label="冻结金额" prop="frozenAmount">
        <el-input v-model="formData.frozenAmount" placeholder="请输入冻结金额" />
      </el-form-item>
      <el-form-item label="托管金额" prop="escrowAmount">
        <el-input v-model="formData.escrowAmount" placeholder="请输入托管金额" />
      </el-form-item>
      <el-form-item label="佣金金额" prop="commissionAmount">
        <el-input v-model="formData.commissionAmount" placeholder="请输入佣金金额" />
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
  <MemberUserSelectDialog ref="userSelectDialogRef" @selected="handleUserSelected" />
</template>
<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { WalletAccountApi, type WalletAccountFormData } from '@/api/linbang/walletaccount'
import { MemberUserApi, type MemberUser } from '@/api/linbang/memberuser'
import { DICT_TYPE, getStrDictOptions } from '@/utils/dict'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
import { ENABLE_STATUS_OPTIONS } from '../utils/display'
import MemberUserSelectDialog from '../memberaddress/MemberUserSelectDialog.vue'
/** 钱包账户 表单 */
defineOptions({ name: 'WalletAccountForm' })

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
  roleCode?: string
  totalAmount?: number
  availableAmount?: number
  frozenAmount?: number
  escrowAmount?: number
  commissionAmount?: number
  status?: string
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
  roleCode: undefined,
  totalAmount: undefined,
  availableAmount: undefined,
  frozenAmount: undefined,
  escrowAmount: undefined,
  commissionAmount: undefined,
  status: 'ENABLE'
})
const formData = ref<FormData>(createEmptyFormData())
const formRules = reactive({
  userId: [{ required: true, message: '用户不能为空', trigger: 'change' }],
  roleCode: [{ required: true, message: '角色编码不能为空', trigger: 'blur' }],
  totalAmount: [{ required: true, message: '总资产不能为空', trigger: 'blur' }],
  availableAmount: [{ required: true, message: '可提现金额不能为空', trigger: 'blur' }],
  frozenAmount: [{ required: true, message: '冻结金额不能为空', trigger: 'blur' }],
  escrowAmount: [{ required: true, message: '托管金额不能为空', trigger: 'blur' }],
  commissionAmount: [{ required: true, message: '佣金金额不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'blur' }]
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
      const detail = await WalletAccountApi.getWalletAccount(id)
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
    const data = formData.value as WalletAccountFormData
    if (formType.value === 'create') {
      await WalletAccountApi.createWalletAccount(data)
      message.success(t('common.createSuccess'))
    } else {
      await WalletAccountApi.updateWalletAccount(data)
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

const buildFormData = (data: WalletAccountFormData): FormData => ({
  id: data.id,
  userId: data.userId,
  roleCode: data.roleCode,
  totalAmount: data.totalAmount,
  availableAmount: data.availableAmount,
  frozenAmount: data.frozenAmount,
  escrowAmount: data.escrowAmount,
  commissionAmount: data.commissionAmount,
  status: data.status
})
</script>
