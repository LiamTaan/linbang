<template>
    <Dialog v-model="dialogVisible" title="钱包账户详情" width="820px" :loading="detailLoading">
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item label="用户">{{ formatUserDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="账户角色">
        <dict-tag v-if="detailData.roleCode" :type="DICT_TYPE.LB_ROLE_CODE" :value="detailData.roleCode" />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="状态">{{ formatEnableStatus(detailData.status) }}</el-descriptions-item>
      <el-descriptions-item label="总资产">{{ detailData.totalAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="可提现金额">{{ detailData.availableAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="冻结金额">{{ detailData.frozenAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="托管金额">{{ detailData.escrowAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="佣金金额">{{ detailData.commissionAmount ?? '-' }}</el-descriptions-item>
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
    <el-divider />
    <el-descriptions :column="2" border>
      <el-descriptions-item label="默认银行卡">
        {{ formatDefaultBankCardDisplay() }}
      </el-descriptions-item>
      <el-descriptions-item label="开户银行">
        {{ detailData.defaultBankCard?.bankName || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="银行编码">
        {{ detailData.defaultBankCard?.bankCode || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="银行卡号">
        {{ detailData.defaultBankCard?.cardNoMask || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="开户名">
        {{ detailData.defaultBankCard?.accountName || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="预留手机号">
        {{ detailData.defaultBankCard?.reservedMobile || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="是否默认">
        {{ formatDefaultBankCardFlag() }}
      </el-descriptions-item>
      <el-descriptions-item label="银行卡状态">
        {{ formatEnableStatus(detailData.defaultBankCard?.status) }}
      </el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">最近流水</el-divider>
    <el-table :data="detailData.recentFlows || []" size="small" border>
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
    <el-divider content-position="left">最近业务账单</el-divider>
    <el-table :data="detailData.recentBills || []" size="small" border>
      <el-table-column label="账单标题" prop="billTitle" min-width="140" />
      <el-table-column label="账单类型" prop="billType" width="120" />
      <el-table-column label="业务状态" prop="bizStatus" width="120" />
      <el-table-column label="方向" prop="amountDirection" width="90">
        <template #default="{ row }">
          {{ row.amountDirection === 'OUT' ? '支出' : '收入' }}
        </template>
      </el-table-column>
      <el-table-column label="金额" prop="amount" width="120" />
      <el-table-column label="关联订单" prop="relatedOrderId" width="120" />
      <el-table-column label="关联单元" prop="relatedUnitId" width="120" />
      <el-table-column label="备注" prop="remark" min-width="180" />
      <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
    </el-table>
    <el-divider content-position="left">最近提现</el-divider>
    <el-table :data="detailData.recentWithdraws || []" size="small" border>
      <el-table-column label="提现单号" prop="withdrawNo" min-width="160" />
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
import { WalletAccountApi, type WalletAccountDetail } from '@/api/linbang/walletaccount'
import { formatBizType, formatBooleanYesNo, formatEnableStatus, formatFlowType } from '../utils/display'

defineOptions({ name: 'WalletAccountDetailDialog' })

const dialogVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref<WalletAccountDetail>({} as WalletAccountDetail)

const formatUserDisplay = () => {
  const user = detailData.value.user
  const summary = [user?.nickname, user?.mobile, user?.userNo].filter(Boolean).join(' / ')
  return summary || (detailData.value.userId ? '用户信息缺失' : '-')
}

const formatDefaultBankCardFlag = () => {
  return formatBooleanYesNo(detailData.value.defaultBankCard?.isDefault)
}

const formatDefaultBankCardDisplay = () => {
  const card = detailData.value.defaultBankCard
  const summary = [card?.bankName, card?.cardNoMask, card?.accountName].filter(Boolean).join(' / ')
  return summary || (card?.id ? '银行卡信息缺失' : '-')
}

const open = async (id: number) => {
  dialogVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await WalletAccountApi.getWalletAccount(id)
    if (!detailData.value.recentFlows) {
      detailData.value.recentFlows = []
    }
    if (!detailData.value.recentWithdraws) {
      detailData.value.recentWithdraws = []
    }
    if (!detailData.value.recentBills) {
      detailData.value.recentBills = []
    }
  } finally {
    detailLoading.value = false
  }
}

defineExpose({ open })
</script>
