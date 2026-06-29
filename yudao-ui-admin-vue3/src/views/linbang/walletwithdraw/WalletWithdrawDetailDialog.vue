<template>
  <Dialog v-model="dialogVisible" title="提现详情" width="760px" :loading="detailLoading">
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item label="提现单号">{{ detailData.withdrawNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="用户">{{ formatUserDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="钱包账户">{{ formatWalletAccountDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="提现银行卡">{{ formatBankCardDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="申请金额">{{ detailData.applyAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="手续费">{{ detailData.feeAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="实际到账金额">{{ detailData.realAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <dict-tag v-if="detailData.status" :type="DICT_TYPE.LB_WITHDRAW_STATUS" :value="detailData.status" />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="审核状态">
        <dict-tag
          v-if="detailData.auditStatus"
          :type="DICT_TYPE.LB_AUDIT_STATUS"
          :value="detailData.auditStatus"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="审核人">{{ detailData.auditBy ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="审核时间">{{ formatDate(detailData.auditTime) }}</el-descriptions-item>
      <el-descriptions-item label="打款时间">{{ formatDate(detailData.payTime) }}</el-descriptions-item>
      <el-descriptions-item label="出款转账 ID">{{ detailData.payTransferId ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="出款单号">{{ detailData.payTransferNo ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDate(detailData.createTime) }}</el-descriptions-item>
    </el-descriptions>
    <el-divider />
    <el-descriptions :column="1" border direction="vertical">
      <el-descriptions-item label="审核备注">{{ detailData.auditRemark || '-' }}</el-descriptions-item>
      <el-descriptions-item label="驳回原因">{{ detailData.rejectReason || '-' }}</el-descriptions-item>
      <el-descriptions-item label="出款失败原因">{{ detailData.transferErrorMsg || '-' }}</el-descriptions-item>
    </el-descriptions>
    <el-divider />
    <el-descriptions :column="2" border>
      <el-descriptions-item label="钱包角色">
        <dict-tag
          v-if="detailData.walletAccount?.roleCode"
          :type="DICT_TYPE.LB_ROLE_CODE"
          :value="detailData.walletAccount.roleCode"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="钱包状态">
        {{ formatEnableStatus(detailData.walletAccount?.status) }}
      </el-descriptions-item>
      <el-descriptions-item label="总资产">{{ detailData.walletAccount?.totalAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="可提现金额">
        {{ detailData.walletAccount?.availableAmount ?? '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="冻结金额">{{ detailData.walletAccount?.frozenAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="托管金额">{{ detailData.walletAccount?.escrowAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="佣金金额">
        {{ detailData.walletAccount?.commissionAmount ?? '-' }}
      </el-descriptions-item>
    </el-descriptions>
    <el-divider />
    <el-descriptions :column="2" border>
      <el-descriptions-item label="开户银行">{{ detailData.bankCard?.bankName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="银行编码">{{ detailData.bankCard?.bankCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="银行卡号">{{ detailData.bankCard?.cardNoMask || '-' }}</el-descriptions-item>
      <el-descriptions-item label="开户名">{{ detailData.bankCard?.accountName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="预留手机号">
        {{ detailData.bankCard?.reservedMobile || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="默认卡">
        {{ formatBooleanYesNo(detailData.bankCard?.isDefault) }}
      </el-descriptions-item>
      <el-descriptions-item label="银行卡状态">{{ formatEnableStatus(detailData.bankCard?.status) }}</el-descriptions-item>
    </el-descriptions>
    <el-divider />
    <el-table :data="detailData.relatedFlows || []" size="small" border>
      <el-table-column label="流水号" prop="flowNo" min-width="160" />
      <el-table-column label="业务类型" prop="bizType" width="120">
        <template #default="{ row }">
          {{ formatBizType(row.bizType) }}
        </template>
      </el-table-column>
      <el-table-column label="流水类型" prop="flowType" width="120">
        <template #default="{ row }">
          {{ formatFlowType(row.flowType) }}
        </template>
      </el-table-column>
      <el-table-column label="变动金额" prop="changeAmount" width="120" />
      <el-table-column label="变动前" prop="beforeAmount" width="120" />
      <el-table-column label="变动后" prop="afterAmount" width="120" />
      <el-table-column label="备注" prop="remark" min-width="180" />
      <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
    </el-table>
  </Dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { DICT_TYPE } from '@/utils/dict'
import { formatDate, dateFormatter } from '@/utils/formatTime'
import { WalletWithdrawApi, type WalletWithdrawDetail } from '@/api/linbang/walletwithdraw'
import { formatBizType, formatBooleanYesNo, formatEnableStatus, formatFlowType } from '../utils/display'

defineOptions({ name: 'WalletWithdrawDetailDialog' })

const dialogVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref<WalletWithdrawDetail>({} as WalletWithdrawDetail)

const formatUserDisplay = () => {
  const user = detailData.value.user
  const summary = [user?.nickname, user?.mobile, user?.userNo].filter(Boolean).join(' / ')
  return summary || (detailData.value.userId ? '用户信息缺失' : '-')
}

const formatWalletAccountDisplay = () => {
  const account = detailData.value.walletAccount
  const summary = [account?.roleCode, account?.status].filter(Boolean).join(' / ')
  return summary || (detailData.value.walletAccountId ? '钱包账户信息缺失' : '-')
}

const formatBankCardDisplay = () => {
  const card = detailData.value.bankCard
  const summary = [card?.bankName, card?.cardNoMask, card?.accountName].filter(Boolean).join(' / ')
  return summary || (detailData.value.bankCardId ? '银行卡信息缺失' : '-')
}

const open = async (id: number) => {
  dialogVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await WalletWithdrawApi.getWalletWithdraw(id)
    if (!detailData.value.relatedFlows) {
      detailData.value.relatedFlows = []
    }
  } finally {
    detailLoading.value = false
  }
}

defineExpose({ open })
</script>
