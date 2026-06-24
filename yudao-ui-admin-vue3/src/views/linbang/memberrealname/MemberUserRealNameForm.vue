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
      <el-form-item label="真实姓名" prop="realName">
        <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
      </el-form-item>
      <el-form-item label="身份证号" prop="idCardNo">
        <el-input v-model="formData.idCardNo" placeholder="请输入身份证号" />
      </el-form-item>
      <el-form-item label="身份证正面" prop="idCardFrontFileId">
        <el-input :model-value="selectedFrontFileLabel" placeholder="请选择身份证正面文件" readonly @click="openFrontFileDialog">
          <template #append>
            <el-button @click="openFrontFileDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="身份证反面" prop="idCardBackFileId">
        <el-input :model-value="selectedBackFileLabel" placeholder="请选择身份证反面文件" readonly @click="openBackFileDialog">
          <template #append>
            <el-button @click="openBackFileDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="手持证件照" prop="holdCardFileId">
        <el-input :model-value="selectedHoldFileLabel" placeholder="请选择手持证件文件" readonly @click="openHoldFileDialog">
          <template #append>
            <el-button @click="openHoldFileDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="活体结果" prop="livenessResult">
        <el-input v-model="formData.livenessResult" placeholder="请输入活体结果" />
      </el-form-item>
      <el-form-item label="人脸核验结果" prop="faceVerifyResult">
        <el-input v-model="formData.faceVerifyResult" placeholder="请输入人脸核验结果" />
      </el-form-item>
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select v-model="formData.auditStatus" placeholder="请选择审核状态">
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_AUDIT_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="审核备注" prop="auditRemark">
        <el-input v-model="formData.auditRemark" placeholder="请输入审核备注" />
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
  <FileSelectDialog ref="frontFileDialogRef" @selected="handleFrontFileSelected" />
  <FileSelectDialog ref="backFileDialogRef" @selected="handleBackFileSelected" />
  <FileSelectDialog ref="holdFileDialogRef" @selected="handleHoldFileSelected" />
</template>
<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { getStrDictOptions, DICT_TYPE } from '@/utils/dict'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
import { MemberUserApi, type MemberUser } from '@/api/linbang/memberuser'
import {
  MemberUserRealNameApi,
  MemberUserRealName,
  MemberUserRealNameDetail
} from '@/api/linbang/memberrealname'
import MemberUserSelectDialog from '../memberaddress/MemberUserSelectDialog.vue'
import FileSelectDialog from '../shared/FileSelectDialog.vue'
import type { FileVO } from '@/api/infra/file'

/** 实名认证表 表单 */
defineOptions({ name: 'MemberUserRealNameForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const userSelectDialogRef = ref()
const frontFileDialogRef = ref()
const backFileDialogRef = ref()
const holdFileDialogRef = ref()
const selectedUser = ref<MemberUser>()
const selectedFrontFile = ref<FileVO>()
const selectedBackFile = ref<FileVO>()
const selectedHoldFile = ref<FileVO>()
type FormData = {
  id?: number
  userId?: number
  realName?: string
  idCardNo?: string
  idCardFrontFileId?: number
  idCardBackFileId?: number
  holdCardFileId?: number
  livenessResult?: string
  faceVerifyResult?: string
  auditStatus?: string
  auditRemark?: string
  auditBy?: number
  auditTime?: string | number
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

const selectedFrontFileLabel = computed(() => formatFileLabel(selectedFrontFile.value))
const selectedBackFileLabel = computed(() => formatFileLabel(selectedBackFile.value))
const selectedHoldFileLabel = computed(() => formatFileLabel(selectedHoldFile.value))

const formData = ref<FormData>({
  id: undefined,
  userId: undefined,
  realName: undefined,
  idCardNo: undefined,
  idCardFrontFileId: undefined,
  idCardBackFileId: undefined,
  holdCardFileId: undefined,
  livenessResult: undefined,
  faceVerifyResult: undefined,
  auditStatus: undefined,
  auditRemark: undefined,
  auditBy: undefined,
  auditTime: undefined,
  rejectReason: undefined
})
const formRules = reactive({
  userId: [{ required: true, message: '用户不能为空', trigger: 'change' }],
  realName: [{ required: true, message: '真实姓名不能为空', trigger: 'blur' }],
  idCardNo: [{ required: true, message: '身份证号不能为空', trigger: 'blur' }],
  idCardFrontFileId: [{ required: true, message: '请选择身份证正面文件', trigger: 'change' }],
  idCardBackFileId: [{ required: true, message: '请选择身份证反面文件', trigger: 'change' }],
  holdCardFileId: [{ required: true, message: '请选择手持证件文件', trigger: 'change' }],
  auditStatus: [{ required: true, message: '审核状态不能为空', trigger: 'change' }]
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
      const data = await MemberUserRealNameApi.getMemberUserRealName(id)
      formData.value = buildFormData(data)
      await Promise.all([
        loadSelectedUser(data.userId),
        loadSelectedFrontFile(data.idCardFrontFileId),
        loadSelectedBackFile(data.idCardBackFileId),
        loadSelectedHoldFile(data.holdCardFileId)
      ])
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
    const data = formData.value as unknown as MemberUserRealName
    if (formType.value === 'create') {
      await MemberUserRealNameApi.createMemberUserRealName(data)
      message.success(t('common.createSuccess'))
    } else {
      await MemberUserRealNameApi.updateMemberUserRealName(data)
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
    realName: undefined,
    idCardNo: undefined,
    idCardFrontFileId: undefined,
    idCardBackFileId: undefined,
    holdCardFileId: undefined,
    livenessResult: undefined,
    faceVerifyResult: undefined,
    auditStatus: undefined,
    auditRemark: undefined,
    auditBy: undefined,
    auditTime: undefined,
    rejectReason: undefined
  }
  selectedUser.value = undefined
  selectedFrontFile.value = undefined
  selectedBackFile.value = undefined
  selectedHoldFile.value = undefined
  formRef.value?.resetFields()
}

const openUserDialog = () => {
  userSelectDialogRef.value?.open(selectedUser.value)
}

const openFrontFileDialog = () => {
  frontFileDialogRef.value?.open(selectedFrontFile.value)
}

const openBackFileDialog = () => {
  backFileDialogRef.value?.open(selectedBackFile.value)
}

const openHoldFileDialog = () => {
  holdFileDialogRef.value?.open(selectedHoldFile.value)
}

const handleUserSelected = (row: MemberUser) => {
  selectedUser.value = row
  formData.value.userId = row.id
}

const handleFrontFileSelected = (row: FileVO) => {
  selectedFrontFile.value = row
  formData.value.idCardFrontFileId = row.id
}

const handleBackFileSelected = (row: FileVO) => {
  selectedBackFile.value = row
  formData.value.idCardBackFileId = row.id
}

const handleHoldFileSelected = (row: FileVO) => {
  selectedHoldFile.value = row
  formData.value.holdCardFileId = row.id
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

const loadSelectedFrontFile = async (fileId?: number) => {
  selectedFrontFile.value = fileId ? await frontFileDialogRef.value?.loadById(fileId) : undefined
}

const loadSelectedBackFile = async (fileId?: number) => {
  selectedBackFile.value = fileId ? await backFileDialogRef.value?.loadById(fileId) : undefined
}

const loadSelectedHoldFile = async (fileId?: number) => {
  selectedHoldFile.value = fileId ? await holdFileDialogRef.value?.loadById(fileId) : undefined
}

const formatFileLabel = (file?: FileVO) => {
  if (!file) {
    return ''
  }
  return [file.name, file.type, file.path].filter(Boolean).join(' / ')
}

const buildFormData = (data: MemberUserRealNameDetail): FormData => ({
  id: data.id,
  userId: data.userId,
  realName: data.realName,
  idCardNo: data.idCardNo,
  idCardFrontFileId: data.idCardFrontFileId,
  idCardBackFileId: data.idCardBackFileId,
  holdCardFileId: data.holdCardFileId,
  livenessResult: data.livenessResult,
  faceVerifyResult: data.faceVerifyResult,
  auditStatus: data.auditStatus,
  auditRemark: data.auditRemark,
  auditBy: data.auditBy,
  auditTime: data.auditTime as string | number | undefined,
  rejectReason: data.rejectReason
})
</script>
