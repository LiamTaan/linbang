<template>
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :model="queryParams"
      :inline="true"
      label-width="88px"
      class="-mb-15px"
    >
      <el-form-item label="提现单号" prop="withdrawNo">
        <el-input v-model="queryParams.withdrawNo" placeholder="请输入提现单号" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="用户" prop="userKeyword">
        <el-input
          v-model="queryParams.userKeyword"
          placeholder="请输入用户编号 / 昵称 / 手机号"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-220px">
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_WITHDRAW_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select v-model="queryParams.auditStatus" placeholder="请选择审核状态" clearable class="!w-220px">
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_AUDIT_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-260px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="success"
          plain
          :loading="exportLoading"
          v-hasPermi="['linbang:wallet:withdraw:export']"
          @click="handleExport"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="提现单号" align="center" prop="withdrawNo" min-width="180" />
      <el-table-column label="用户" align="center" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.userNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="钱包账户" align="center" min-width="180">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ formatWalletAccountDisplay(row) }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ formatEnableStatus(row.walletStatus) }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="提现银行卡" align="center" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.bankName || (row.bankCardId ? '银行卡信息缺失' : '-') }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.cardNoMask || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.bankAccountName || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="申请金额" align="center" prop="applyAmount" width="110" />
      <el-table-column label="手续费" align="center" prop="feeAmount" width="110" />
      <el-table-column label="实际到账金额" align="center" prop="realAmount" width="130" />
      <el-table-column label="状态" align="center" prop="status" width="110">
        <template #default="{ row }">
          <dict-tag v-if="row.status" :type="DICT_TYPE.LB_WITHDRAW_STATUS" :value="row.status" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="审核状态" align="center" prop="auditStatus" width="110">
        <template #default="{ row }">
          <dict-tag v-if="row.auditStatus" :type="DICT_TYPE.LB_AUDIT_STATUS" :value="row.auditStatus" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="审核备注" align="center" prop="auditRemark" min-width="160" />
      <el-table-column label="驳回原因" align="center" prop="rejectReason" min-width="160" />
      <el-table-column label="审核人" align="center" prop="auditBy" width="100" />
      <el-table-column label="审核时间" align="center" prop="auditTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="打款时间" align="center" prop="payTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" align="center" fixed="right" width="120">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetailDialog(row.id)">详情</el-button>
          <el-button
            v-if="row.auditStatus !== 'APPROVED' && row.auditStatus !== 'REJECTED'"
            link
            type="primary"
            v-hasPermi="['linbang:wallet:withdraw:audit']"
            @click="openAuditDialog(row)"
          >
            审核
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>

  <Dialog v-model="auditDialogVisible" title="提现审核" width="520px">
    <el-form
      ref="auditFormRef"
      :model="auditFormData"
      :rules="auditFormRules"
      label-width="88px"
      v-loading="auditLoading"
    >
      <el-form-item label="提现单号">
        <el-input :model-value="currentRow?.withdrawNo" disabled />
      </el-form-item>
      <el-form-item label="用户">
        <el-input :model-value="formatUserDisplay(currentRow)" disabled />
      </el-form-item>
      <el-form-item label="审核结果" prop="auditStatus">
        <el-radio-group v-model="auditFormData.auditStatus">
          <el-radio value="APPROVED">通过</el-radio>
          <el-radio value="REJECTED">驳回</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="审核备注" prop="auditRemark">
        <el-input
          v-model="auditFormData.auditRemark"
          type="textarea"
          :rows="3"
          maxlength="255"
          show-word-limit
          placeholder="请输入审核备注"
        />
      </el-form-item>
      <el-form-item v-if="auditFormData.auditStatus === 'REJECTED'" label="驳回原因" prop="rejectReason">
        <el-input
          v-model="auditFormData.rejectReason"
          type="textarea"
          :rows="3"
          maxlength="255"
          show-word-limit
          placeholder="请输入驳回原因"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="auditDialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="auditLoading" @click="submitAudit">提交审核</el-button>
    </template>
  </Dialog>
  <WalletWithdrawDetailDialog ref="detailDialogRef" />
</template>

<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { getStrDictOptions, DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { useMessage } from '@/hooks/web/useMessage'
import {
  WalletWithdrawApi,
  type WalletWithdraw,
  type WithdrawAuditReqVO
} from '@/api/linbang/walletwithdraw'
import { requestDynamicKeyToken } from '../shared/dynamic-key'
import { formatEnableStatus } from '../utils/display'
import WalletWithdrawDetailDialog from './WalletWithdrawDetailDialog.vue'

defineOptions({ name: 'WalletWithdraw' })

const message = useMessage()
const loading = ref(false)
const exportLoading = ref(false)
const list = ref<WalletWithdraw[]>([])
const total = ref(0)
const queryFormRef = ref<FormInstance>()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  withdrawNo: undefined as string | undefined,
  userKeyword: undefined as string | undefined,
  walletAccountId: undefined as number | undefined,
  status: undefined as string | undefined,
  auditStatus: undefined as string | undefined,
  createTime: [] as string[]
})

const formatUserDisplay = (row?: Pick<WalletWithdraw, 'userNickname' | 'userMobile' | 'userNo' | 'userId'>) => {
  if (!row) {
    return '-'
  }
  const summary = [row.userNickname, row.userMobile, row.userNo].filter(Boolean).join(' / ')
  return summary || (row.userId ? '用户信息缺失' : '-')
}

const formatWalletAccountDisplay = (row?: Pick<WalletWithdraw, 'walletRoleCode' | 'walletAccountId'>) => {
  if (!row) {
    return '-'
  }
  return row.walletRoleCode || (row.walletAccountId ? '钱包账户信息缺失' : '-')
}

const getList = async () => {
  loading.value = true
  try {
    const data = await WalletWithdrawApi.getWalletWithdrawPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const resetQuery = () => {
  queryFormRef.value?.resetFields()
  handleQuery()
}

const handleExport = async () => {
  try {
    await message.exportConfirm()
    exportLoading.value = true
    const data = await WalletWithdrawApi.exportWalletWithdraw(queryParams)
    download.excel(data, '提现审核列表.xls')
  } finally {
    exportLoading.value = false
  }
}

const detailDialogRef = ref()
const openDetailDialog = (id: number) => {
  detailDialogRef.value?.open(id)
}

const auditDialogVisible = ref(false)
const auditLoading = ref(false)
const auditFormRef = ref<FormInstance>()
const currentRow = ref<WalletWithdraw>()
const auditFormData = reactive<WithdrawAuditReqVO>({
  id: 0,
  auditStatus: 'APPROVED',
  auditRemark: '',
  rejectReason: ''
})
const auditFormRules = reactive<FormRules>({
  auditStatus: [{ required: true, message: '请选择审核结果', trigger: 'change' }],
  rejectReason: [
    {
      validator: (_rule, value, callback) => {
        if (auditFormData.auditStatus === 'REJECTED' && !value) {
          callback(new Error('请输入驳回原因'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ]
})

const openAuditDialog = (row: WalletWithdraw) => {
  currentRow.value = row
  auditFormData.id = row.id
  auditFormData.auditStatus = row.auditStatus === 'REJECTED' ? 'REJECTED' : 'APPROVED'
  auditFormData.auditRemark = row.auditRemark || ''
  auditFormData.rejectReason = row.rejectReason || ''
  auditDialogVisible.value = true
}

const submitAudit = async () => {
  await auditFormRef.value?.validate()
  try {
    await message.confirm('确认提交提现审核结果？')
    const verifyToken = await requestDynamicKeyToken('审核提现申请')
    auditLoading.value = true
    await WalletWithdrawApi.auditWalletWithdraw(
      {
        id: auditFormData.id,
        auditStatus: auditFormData.auditStatus,
        auditRemark: auditFormData.auditRemark,
        rejectReason: auditFormData.auditStatus === 'REJECTED' ? auditFormData.rejectReason : ''
      },
      verifyToken
    )
    message.success('提现审核成功')
    auditDialogVisible.value = false
    await getList()
  } finally {
    auditLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
