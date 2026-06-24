<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="640px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="110px"
      v-loading="formLoading"
    >
      <el-alert
        title="一个用户可以拥有多种业务身份；这里维护的是当前端侧使用角色，多身份归属需走身份申请与审核链路。"
        type="info"
        :closable="false"
        class="mb-16px"
      />
      <el-form-item label="用户编号">
        <el-input
          :model-value="formData.userNo || ''"
          placeholder="创建后自动生成"
          disabled
        />
      </el-form-item>
      <el-form-item label="手机号" prop="mobile">
        <el-input v-model="formData.mobile" maxlength="11" placeholder="请输入手机号" />
      </el-form-item>
      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="formData.nickname" placeholder="请输入昵称" />
      </el-form-item>
      <el-form-item label="头像" prop="avatar">
        <UploadImg v-model="formData.avatar" width="120px" height="120px" />
      </el-form-item>
      <el-row :gutter="12">
        <el-col :span="12">
          <el-form-item label="性别" prop="gender">
            <el-select v-model="formData.gender" placeholder="请选择性别" class="!w-1/1" clearable>
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.SYSTEM_USER_SEX)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="生日" prop="birthday">
            <el-date-picker
              v-model="formData.birthday"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择生日"
              class="!w-1/1"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="注册来源" prop="registerSource">
        <el-input v-model="formData.registerSource" placeholder="请输入注册来源" />
      </el-form-item>
      <el-form-item label="当前端侧角色" prop="currentRoleCode">
        <el-select v-model="formData.currentRoleCode" placeholder="请选择当前端侧角色" class="!w-1/1">
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_ROLE_CODE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio value="ENABLE">开启</el-radio>
          <el-radio value="DISABLE">关闭</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-row :gutter="12">
        <el-col :span="12">
          <el-form-item label="最后登录时间">
            <el-input
              :model-value="formData.lastLoginTime || ''"
              placeholder="登录后自动记录"
              disabled
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="最后登录 IP">
            <el-input
              :model-value="formData.lastLoginIp || ''"
              placeholder="登录后自动记录"
              disabled
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import type { FormRules } from 'element-plus'
import { reactive, ref } from 'vue'
import { DICT_TYPE, getIntDictOptions, getStrDictOptions } from '@/utils/dict'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
import UploadImg from '@/components/UploadFile/src/UploadImg.vue'
import { MemberUserApi, type MemberUser, type MemberUserDetail } from '@/api/linbang/memberuser'

defineOptions({ name: 'MemberUserForm' })

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')

type FormData = {
  id?: number
  userNo?: string
  mobile?: string
  nickname?: string
  avatar?: string
  gender?: number
  birthday?: string
  registerSource?: string
  currentRoleCode?: string
  status?: string
  lastLoginTime?: string
  lastLoginIp?: string
  remark?: string
}

const formData = ref<FormData>({
  id: undefined,
  userNo: undefined,
  mobile: undefined,
  nickname: undefined,
  avatar: undefined,
  gender: undefined,
  birthday: undefined,
  registerSource: undefined,
  currentRoleCode: 'USER',
  status: 'ENABLE',
  lastLoginTime: undefined,
  lastLoginIp: undefined,
  remark: undefined
})

const formRules = reactive<FormRules>({
  mobile: [
    { required: true, message: '手机号不能为空', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  nickname: [{ required: true, message: '昵称不能为空', trigger: 'blur' }],
  currentRoleCode: [{ required: true, message: '当前端侧角色不能为空', trigger: 'change' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'change' }]
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
      const data = await MemberUserApi.getMemberUser(id)
      formData.value = buildFormData(data)
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
    const data = {
      id: formData.value.id,
      mobile: formData.value.mobile,
      nickname: formData.value.nickname,
      avatar: formData.value.avatar,
      gender: formData.value.gender,
      birthday: formData.value.birthday,
      registerSource: formData.value.registerSource,
      currentRoleCode: formData.value.currentRoleCode,
      status: formData.value.status,
      remark: formData.value.remark
    } as MemberUser
    if (formType.value === 'create') {
      await MemberUserApi.createMemberUser(data)
      message.success(t('common.createSuccess'))
    } else {
      await MemberUserApi.updateMemberUser(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}

const resetForm = () => {
  formData.value = {
    id: undefined,
    userNo: undefined,
    mobile: undefined,
    nickname: undefined,
    avatar: undefined,
    gender: undefined,
    birthday: undefined,
    registerSource: undefined,
    currentRoleCode: 'USER',
    status: 'ENABLE',
    lastLoginTime: undefined,
    lastLoginIp: undefined,
    remark: undefined
  }
  formRef.value?.resetFields()
}

const buildFormData = (data: MemberUserDetail): FormData => ({
  id: data.id,
  userNo: data.userNo,
  mobile: data.mobile,
  nickname: data.nickname,
  avatar: data.avatar,
  gender: data.gender,
  birthday: data.birthday ? String(data.birthday) : undefined,
  registerSource: data.registerSource,
  currentRoleCode: data.currentRoleCode,
  status: data.status || 'ENABLE',
  lastLoginTime: data.lastLoginTime ? String(data.lastLoginTime) : undefined,
  lastLoginIp: data.lastLoginIp,
  remark: data.remark
})
</script>
