<template>
    <Dialog v-model="dialogVisible" title="银行卡详情" width="820px" :loading="detailLoading">
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item label="用户">{{ formatUserDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="开户银行">{{ detailData.bankName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="银行编码">{{ detailData.bankCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="脱敏卡号">{{ detailData.cardNoMask || '-' }}</el-descriptions-item>
      <el-descriptions-item label="开户名">{{ detailData.accountName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="开户省份">{{ detailData.bankProvince || '-' }}</el-descriptions-item>
      <el-descriptions-item label="开户城市">{{ detailData.bankCity || '-' }}</el-descriptions-item>
      <el-descriptions-item label="预留手机号">
        {{ detailData.reservedMobile || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="状态">{{ formatEnableStatus(detailData.status) }}</el-descriptions-item>
      <el-descriptions-item label="是否默认">
        {{ formatBooleanYesNo(detailData.isDefault) }}
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDate(detailData.createTime) }}</el-descriptions-item>
      <el-descriptions-item label="更新时间">{{ formatDate(detailData.updateTime) }}</el-descriptions-item>
    </el-descriptions>
    <el-divider />
    <el-descriptions :column="3" border>
      <el-descriptions-item label="提现总笔数">
        {{ detailData.withdrawStats?.totalCount ?? 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="提现总金额">
        {{ detailData.withdrawStats?.totalApplyAmount ?? 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="待审核笔数">
        {{ detailData.withdrawStats?.pendingCount ?? 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="待审核金额">
        {{ detailData.withdrawStats?.pendingAmount ?? 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="成功笔数">
        {{ detailData.withdrawStats?.successCount ?? 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="成功金额">
        {{ detailData.withdrawStats?.successAmount ?? 0 }}
      </el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">关联钱包账户</el-divider>
    <el-table :data="detailData.walletAccounts || []" size="small" border>
      <el-table-column label="账户角色" prop="roleCode" width="120">
        <template #default="{ row }">
          <dict-tag v-if="row.roleCode" :type="DICT_TYPE.LB_ROLE_CODE" :value="row.roleCode" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="总资产" prop="totalAmount" width="120" />
      <el-table-column label="可提现金额" prop="availableAmount" width="120" />
      <el-table-column label="冻结金额" prop="frozenAmount" width="120" />
      <el-table-column label="托管金额" prop="escrowAmount" width="120" />
      <el-table-column label="佣金金额" prop="commissionAmount" width="120" />
      <el-table-column label="状态" prop="status" width="120">
        <template #default="{ row }">
          {{ formatEnableStatus(row.status) }}
        </template>
      </el-table-column>
    </el-table>
    <el-divider content-position="left">最近提现</el-divider>
    <el-table :data="detailData.recentWithdraws || []" size="small" border>
      <el-table-column label="提现单号" prop="withdrawNo" min-width="160" />
      <el-table-column label="关联钱包账户" width="160">
        <template #default="{ row }">
          <dict-tag
            v-if="findWalletAccount(row.walletAccountId)?.roleCode"
            :type="DICT_TYPE.LB_ROLE_CODE"
            :value="findWalletAccount(row.walletAccountId)?.roleCode || ''"
          />
          <span v-else>{{ formatWalletAccountDisplay(row.walletAccountId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="申请金额" prop="applyAmount" width="120" />
      <el-table-column label="手续费" prop="feeAmount" width="120" />
      <el-table-column label="到账金额" prop="realAmount" width="120" />
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <dict-tag v-if="row.status" :type="DICT_TYPE.LB_WITHDRAW_STATUS" :value="row.status" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="审核状态" width="120">
        <template #default="{ row }">
          <dict-tag
            v-if="row.auditStatus"
            :type="DICT_TYPE.LB_AUDIT_STATUS"
            :value="row.auditStatus"
          />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="驳回原因" prop="rejectReason" min-width="180" />
      <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
    </el-table>
  </Dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { DICT_TYPE } from '@/utils/dict'
import { formatDate, dateFormatter } from '@/utils/formatTime'
import { WalletBankCardApi, type WalletBankCardDetail } from '@/api/linbang/walletbankcard'
import { formatBooleanYesNo, formatEnableStatus } from '../utils/display'

defineOptions({ name: 'WalletBankCardDetailDialog' })

const dialogVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref<WalletBankCardDetail>({} as WalletBankCardDetail)

const formatUserDisplay = () => {
  const summary = [detailData.value.userNickname, detailData.value.userMobile, detailData.value.userNo]
    .filter(Boolean)
    .join(' / ')
  return summary || (detailData.value.userId ? '用户信息缺失' : '-')
}

const formatWalletAccountDisplay = (walletAccountId?: number) => {
  if (!walletAccountId) {
    return '-'
  }
  const account = findWalletAccount(walletAccountId)
  if (!account) {
    return '钱包账户信息缺失'
  }
  return account.roleCode || '钱包账户信息缺失'
}

const findWalletAccount = (walletAccountId?: number) => {
  if (!walletAccountId) {
    return undefined
  }
  return detailData.value.walletAccounts?.find((item) => item.id === walletAccountId)
}

const open = async (id: number) => {
  dialogVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await WalletBankCardApi.getWalletBankCard(id)
    if (!detailData.value.walletAccounts) {
      detailData.value.walletAccounts = []
    }
    if (!detailData.value.recentWithdraws) {
      detailData.value.recentWithdraws = []
    }
  } finally {
    detailLoading.value = false
  }
}

defineExpose({ open })
</script>
