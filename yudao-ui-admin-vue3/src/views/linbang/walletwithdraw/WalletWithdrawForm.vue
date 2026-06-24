<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="提现单号" prop="withdrawNo">
        <el-input v-model="formData.withdrawNo" placeholder="请输入提现单号" />
      </el-form-item>
      <el-form-item label="用户" prop="userId">
        <el-input :model-value="selectedUserLabel" placeholder="请选择用户" readonly @click="openUserDialog">
          <template #append>
            <el-button @click="openUserDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="钱包账户" prop="walletAccountId">
        <el-input
          :model-value="selectedWalletAccountLabel"
          placeholder="请选择钱包账户"
          readonly
          @click="openWalletAccountDialog"
        >
          <template #append>
            <el-button @click="openWalletAccountDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="银行卡" prop="bankCardId">
        <el-input
          :model-value="selectedBankCardLabel"
          placeholder="请选择银行卡"
          readonly
          @click="openBankCardDialog"
        >
          <template #append>
            <el-button @click="openBankCardDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="申请金额" prop="applyAmount">
        <el-input v-model="formData.applyAmount" placeholder="请输入申请金额" />
      </el-form-item>
      <el-form-item label="手续费" prop="feeAmount">
        <el-input v-model="formData.feeAmount" placeholder="请输入手续费" />
      </el-form-item>
      <el-form-item label="实际到账金额" prop="realAmount">
        <el-input v-model="formData.realAmount" placeholder="请输入实际到账金额" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_WITHDRAW_STATUS)"
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
      <el-form-item label="打款时间" prop="payTime">
        <el-date-picker
          v-model="formData.payTime"
          type="date"
          value-format="x"
          placeholder="选择打款时间"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
  <MemberUserSelectDialog ref="userSelectDialogRef" @selected="handleUserSelected" />
  <WalletAccountSelectDialog ref="walletAccountSelectDialogRef" @selected="handleWalletAccountSelected" />
  <WalletBankCardSelectDialog ref="bankCardSelectDialogRef" @selected="handleBankCardSelected" />
</template>
<script setup lang="ts">
import { getStrDictOptions, DICT_TYPE } from '@/utils/dict'
import { MemberUserApi, type MemberUser } from '@/api/linbang/memberuser'
import { WalletAccountApi, type WalletAccount } from '@/api/linbang/walletaccount'
import { WalletBankCardApi, type WalletBankCard } from '@/api/linbang/walletbankcard'
import {
  WalletWithdrawApi,
  type WalletWithdraw,
  type WalletWithdrawDetail
} from '@/api/linbang/walletwithdraw'

import { computed, reactive, ref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
import MemberUserSelectDialog from '../memberaddress/MemberUserSelectDialog.vue'
import WalletAccountSelectDialog from '../shared/WalletAccountSelectDialog.vue'
import WalletBankCardSelectDialog from '../shared/WalletBankCardSelectDialog.vue'
/** 提现申请 表单 */
defineOptions({ name: 'WalletWithdrawForm' })

interface WalletWithdrawFormData {
  id?: number
  withdrawNo?: string
  userId?: number
  walletAccountId?: number
  bankCardId?: number
  applyAmount?: number
  feeAmount?: number
  realAmount?: number
  status?: string
  auditStatus?: string
  auditRemark?: string
  auditBy?: number
  auditTime?: string | number
  rejectReason?: string
  payTime?: string | number
}

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const userSelectDialogRef = ref()
const walletAccountSelectDialogRef = ref()
const bankCardSelectDialogRef = ref()
const selectedUser = ref<MemberUser>()
const selectedWalletAccount = ref<WalletAccount>()
const selectedBankCard = ref<WalletBankCard>()
const formData = ref<WalletWithdrawFormData>({
  id: undefined,
  withdrawNo: undefined,
  userId: undefined,
  walletAccountId: undefined,
  bankCardId: undefined,
  applyAmount: undefined,
  feeAmount: undefined,
  realAmount: undefined,
  status: undefined,
  auditStatus: undefined,
  auditRemark: undefined,
  auditBy: undefined,
  auditTime: undefined,
  rejectReason: undefined,
  payTime: undefined
})
const selectedUserLabel = computed(() => {
  if (!selectedUser.value) {
    return ''
  }
  return [selectedUser.value.userNo, selectedUser.value.nickname, selectedUser.value.mobile]
    .filter(Boolean)
    .join(' / ')
})

const selectedWalletAccountLabel = computed(() => {
  if (!selectedWalletAccount.value) {
    return ''
  }
  return [
    selectedWalletAccount.value.userNickname,
    selectedWalletAccount.value.userMobile,
    selectedWalletAccount.value.roleCode
  ]
    .filter(Boolean)
    .join(' / ')
})

const selectedBankCardLabel = computed(() => {
  if (!selectedBankCard.value) {
    return ''
  }
  return [
    selectedBankCard.value.bankName,
    selectedBankCard.value.cardNoMask,
    selectedBankCard.value.accountName
  ]
    .filter(Boolean)
    .join(' / ')
})
const formRules = reactive({
  withdrawNo: [{ required: true, message: '提现单号不能为空', trigger: 'blur' }],
  userId: [{ required: true, message: '用户不能为空', trigger: 'change' }],
  walletAccountId: [{ required: true, message: '钱包账户不能为空', trigger: 'change' }],
  bankCardId: [{ required: true, message: '银行卡不能为空', trigger: 'change' }],
  applyAmount: [{ required: true, message: '申请金额不能为空', trigger: 'blur' }],
  feeAmount: [{ required: true, message: '手续费不能为空', trigger: 'blur' }],
  realAmount: [{ required: true, message: '实际到账金额不能为空', trigger: 'blur' }],
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
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      const data = await WalletWithdrawApi.getWalletWithdraw(id)
      formData.value = buildFormData(data)
      await loadSelectedUser(data.userId)
      await loadSelectedWalletAccount(data.walletAccountId, data)
      await loadSelectedBankCard(data.bankCardId, data)
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
    const data = formData.value as WalletWithdraw
    if (formType.value === 'create') {
      await WalletWithdrawApi.createWalletWithdraw(data)
      message.success(t('common.createSuccess'))
    } else {
      await WalletWithdrawApi.updateWalletWithdraw(data)
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
    withdrawNo: undefined,
    userId: undefined,
    walletAccountId: undefined,
    bankCardId: undefined,
    applyAmount: undefined,
    feeAmount: undefined,
    realAmount: undefined,
    status: undefined,
    auditStatus: undefined,
    auditRemark: undefined,
    auditBy: undefined,
    auditTime: undefined,
    rejectReason: undefined,
    payTime: undefined
  }
  selectedUser.value = undefined
  selectedWalletAccount.value = undefined
  selectedBankCard.value = undefined
  formRef.value?.resetFields()
}

const openUserDialog = () => {
  userSelectDialogRef.value?.open(selectedUser.value)
}

const openWalletAccountDialog = () => {
  walletAccountSelectDialogRef.value?.open(selectedWalletAccount.value, selectedUser.value)
}

const openBankCardDialog = () => {
  bankCardSelectDialogRef.value?.open(selectedBankCard.value, selectedUser.value)
}

const handleUserSelected = (row: MemberUser) => {
  if (selectedWalletAccount.value && selectedWalletAccount.value.userId !== row.id) {
    selectedWalletAccount.value = undefined
    formData.value.walletAccountId = undefined
  }
  if (selectedBankCard.value && selectedBankCard.value.userId !== row.id) {
    selectedBankCard.value = undefined
    formData.value.bankCardId = undefined
  }
  selectedUser.value = row
  formData.value.userId = row.id
}

const handleWalletAccountSelected = (row: WalletAccount) => {
  selectedWalletAccount.value = row
  formData.value.walletAccountId = row.id
  if (selectedBankCard.value && row.userId && selectedBankCard.value.userId !== row.userId) {
    selectedBankCard.value = undefined
    formData.value.bankCardId = undefined
  }
  if (row.userId && selectedUser.value?.id !== row.userId) {
    formData.value.userId = row.userId
    void loadSelectedUser(row.userId)
  }
}

const handleBankCardSelected = (row: WalletBankCard) => {
  selectedBankCard.value = row
  formData.value.bankCardId = row.id
  if (selectedWalletAccount.value && row.userId && selectedWalletAccount.value.userId !== row.userId) {
    selectedWalletAccount.value = undefined
    formData.value.walletAccountId = undefined
  }
  if (row.userId && selectedUser.value?.id !== row.userId) {
    formData.value.userId = row.userId
    void loadSelectedUser(row.userId)
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

const loadSelectedWalletAccount = async (walletAccountId?: number, detail?: WalletWithdrawDetail) => {
  if (!walletAccountId) {
    selectedWalletAccount.value = undefined
    return
  }
  if (detail?.walletAccount) {
    selectedWalletAccount.value = {
      id: detail.walletAccount.id!,
      userId: detail.walletAccount.userId,
      userNo: selectedUser.value?.userNo,
      userNickname: selectedUser.value?.nickname,
      userMobile: selectedUser.value?.mobile,
      roleCode: detail.walletAccount.roleCode,
      totalAmount: detail.walletAccount.totalAmount,
      availableAmount: detail.walletAccount.availableAmount,
      frozenAmount: detail.walletAccount.frozenAmount,
      escrowAmount: detail.walletAccount.escrowAmount,
      commissionAmount: detail.walletAccount.commissionAmount,
      status: detail.walletAccount.status
    }
    return
  }
  selectedWalletAccount.value = await WalletAccountApi.getWalletAccount(walletAccountId)
}

const loadSelectedBankCard = async (bankCardId?: number, detail?: WalletWithdrawDetail) => {
  if (!bankCardId) {
    selectedBankCard.value = undefined
    return
  }
  if (detail?.bankCard) {
    selectedBankCard.value = {
      id: detail.bankCard.id!,
      userId: detail.bankCard.userId,
      userNo: selectedUser.value?.userNo,
      userNickname: selectedUser.value?.nickname,
      userMobile: selectedUser.value?.mobile,
      bankName: detail.bankCard.bankName,
      bankCode: detail.bankCard.bankCode,
      cardNoMask: detail.bankCard.cardNoMask,
      accountName: detail.bankCard.accountName,
      reservedMobile: detail.bankCard.reservedMobile,
      status: detail.bankCard.status,
      isDefault: detail.bankCard.isDefault
    }
    return
  }
  selectedBankCard.value = await WalletBankCardApi.getWalletBankCard(bankCardId)
}

const buildFormData = (data: WalletWithdrawDetail): WalletWithdrawFormData => ({
  id: data.id,
  withdrawNo: data.withdrawNo,
  userId: data.userId,
  walletAccountId: data.walletAccountId,
  bankCardId: data.bankCardId,
  applyAmount: data.applyAmount,
  feeAmount: data.feeAmount,
  realAmount: data.realAmount,
  status: data.status,
  auditStatus: data.auditStatus,
  auditRemark: data.auditRemark,
  auditBy: data.auditBy,
  auditTime: data.auditTime as string | number | undefined,
  rejectReason: data.rejectReason,
  payTime: data.payTime as string | number | undefined
})
</script>
