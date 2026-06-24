<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="服务商" prop="merchantId">
        <el-input
          :model-value="selectedMerchantLabel"
          placeholder="请选择服务商"
          readonly
          @click="openMerchantDialog"
        >
          <template #append>
            <el-button @click="openMerchantDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="用户" prop="userId">
        <el-input :model-value="selectedUserLabel" placeholder="请选择用户" readonly @click="openUserDialog">
          <template #append>
            <el-button @click="openUserDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="入驻单号" prop="entryNo">
        <el-input v-model="formData.entryNo" placeholder="请输入入驻单号" />
      </el-form-item>
      <el-form-item label="区域编码" prop="regionCode">
        <el-input v-model="formData.regionCode" placeholder="请输入区域编码" />
      </el-form-item>
      <el-form-item label="初审状态" prop="firstAuditStatus">
        <el-radio-group v-model="formData.firstAuditStatus">
          <el-radio
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_AUDIT_STATUS)"
            :key="dict.value"
            :value="dict.value"
          >
            {{ dict.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="初审人" prop="firstAuditBy">
        <el-input v-model="formData.firstAuditBy" placeholder="请输入初审人" />
      </el-form-item>
      <el-form-item label="初审时间" prop="firstAuditTime">
        <el-date-picker
          v-model="formData.firstAuditTime"
          type="datetime"
          value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="选择初审时间"
        />
      </el-form-item>
      <el-form-item label="终审状态" prop="finalAuditStatus">
        <el-radio-group v-model="formData.finalAuditStatus">
          <el-radio
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_AUDIT_STATUS)"
            :key="dict.value"
            :value="dict.value"
          >
            {{ dict.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="终审人" prop="finalAuditBy">
        <el-input v-model="formData.finalAuditBy" placeholder="请输入终审人" />
      </el-form-item>
      <el-form-item label="终审时间" prop="finalAuditTime">
        <el-date-picker
          v-model="formData.finalAuditTime"
          type="datetime"
          value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="选择终审时间"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_MERCHANT_ENTRY_STATUS)"
            :key="dict.value"
            :value="dict.value"
          >
            {{ dict.label }}
          </el-radio>
        </el-radio-group>
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
  <MerchantInfoSelectDialog ref="merchantSelectDialogRef" @selected="handleMerchantSelected" />
</template>
<script setup lang="ts">
import { getStrDictOptions, DICT_TYPE } from '@/utils/dict'
import { computed, reactive, ref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
import { MerchantEntryApi, MerchantEntry } from '@/api/linbang/merchantentry'
import { MerchantInfoApi, type MerchantInfo, type MerchantInfoDetail } from '@/api/linbang/merchantinfo'
import { MemberUserApi, type MemberUser, type MemberUserDetail } from '@/api/linbang/memberuser'
import MemberUserSelectDialog from '../memberaddress/MemberUserSelectDialog.vue'
import MerchantInfoSelectDialog from '../shared/MerchantInfoSelectDialog.vue'

/** 服务商入驻申请表 表单 */
defineOptions({ name: 'MerchantEntryForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const userSelectDialogRef = ref()
const merchantSelectDialogRef = ref()
type MerchantEntryFormData = {
  id?: number
  merchantId?: number
  userId?: number
  entryNo?: string
  regionCode?: string
  firstAuditStatus?: string
  firstAuditBy?: number
  firstAuditTime?: string
  finalAuditStatus?: string
  finalAuditBy?: number
  finalAuditTime?: string
  status?: string
  remark?: string
}
const selectedUser = ref<MemberUser>()
const selectedMerchant = ref<MerchantInfo>()

const createEmptyFormData = (): MerchantEntryFormData => ({
  id: undefined,
  merchantId: undefined,
  userId: undefined,
  entryNo: undefined,
  regionCode: undefined,
  firstAuditStatus: undefined,
  firstAuditBy: undefined,
  firstAuditTime: undefined,
  finalAuditStatus: undefined,
  finalAuditBy: undefined,
  finalAuditTime: undefined,
  status: undefined,
  remark: undefined
})
const formData = ref<MerchantEntryFormData>(createEmptyFormData())
const formRules = reactive({
  merchantId: [{ required: true, message: '服务商不能为空', trigger: 'change' }],
  userId: [{ required: true, message: '用户不能为空', trigger: 'change' }],
  entryNo: [{ required: true, message: '入驻单号不能为空', trigger: 'blur' }],
  firstAuditStatus: [{ required: true, message: '初审状态不能为空', trigger: 'blur' }],
  finalAuditStatus: [{ required: true, message: '终审状态不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'blur' }]
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
      const detail = await MerchantEntryApi.getMerchantEntry(id)
      formData.value = buildFormData(detail)
      await loadSelectedUser(detail.userId)
      await loadSelectedMerchant(detail.merchantId)
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
    const data = formData.value as MerchantEntry
    if (formType.value === 'create') {
      await MerchantEntryApi.createMerchantEntry(data)
      message.success(t('common.createSuccess'))
    } else {
      await MerchantEntryApi.updateMerchantEntry(data)
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
  formData.value = createEmptyFormData()
  selectedUser.value = undefined
  selectedMerchant.value = undefined
  formRef.value?.resetFields()
}

const buildFormData = (detail: MerchantEntry): MerchantEntryFormData => ({
  id: detail.id,
  merchantId: detail.merchantId,
  userId: detail.userId,
  entryNo: detail.entryNo,
  regionCode: detail.regionCode,
  firstAuditStatus: detail.firstAuditStatus,
  firstAuditBy: detail.firstAuditBy,
  firstAuditTime: detail.firstAuditTime ? String(detail.firstAuditTime) : undefined,
  finalAuditStatus: detail.finalAuditStatus,
  finalAuditBy: detail.finalAuditBy,
  finalAuditTime: detail.finalAuditTime ? String(detail.finalAuditTime) : undefined,
  status: detail.status,
  remark: detail.remark
})

const openUserDialog = () => {
  userSelectDialogRef.value?.open(selectedUser.value)
}

const openMerchantDialog = () => {
  merchantSelectDialogRef.value?.open(selectedMerchant.value)
}

const handleUserSelected = (row: MemberUser) => {
  selectedUser.value = row
  formData.value.userId = row.id
}

const handleMerchantSelected = (row: MerchantInfo) => {
  selectedMerchant.value = row
  formData.value.merchantId = row.id
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
