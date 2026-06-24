<template>
  <Dialog v-model="dialogVisible" title="钱包流水详情" width="860px" :loading="detailLoading">
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item label="流水ID">{{ detailData.id ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="流水号">{{ detailData.flowNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="用户">{{ formatUserDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="钱包账户">{{ formatWalletAccountDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="业务类型">{{ formatBizType(detailData.bizType) }}</el-descriptions-item>
      <el-descriptions-item label="流水类型">{{ formatFlowType(detailData.flowType) }}</el-descriptions-item>
      <el-descriptions-item label="变动金额">{{ detailData.changeAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="变动前金额">{{ detailData.beforeAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="变动后金额">{{ detailData.afterAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDate(detailData.createTime) }}</el-descriptions-item>
    </el-descriptions>
    <el-divider />
    <el-descriptions :column="1" border direction="vertical">
      <el-descriptions-item label="备注">{{ detailData.remark || '-' }}</el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">钱包账户</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="账户ID">{{ detailData.walletAccount?.id ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="角色编码">
        <dict-tag
          v-if="detailData.walletAccount?.roleCode"
          :type="DICT_TYPE.LB_ROLE_CODE"
          :value="detailData.walletAccount.roleCode"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="总资产">{{ detailData.walletAccount?.totalAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="可提现金额">
        {{ detailData.walletAccount?.availableAmount ?? '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="冻结金额">
        {{ detailData.walletAccount?.frozenAmount ?? '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="托管金额">
        {{ detailData.walletAccount?.escrowAmount ?? '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="佣金金额">
        {{ detailData.walletAccount?.commissionAmount ?? '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="账户状态">
        {{ formatEnableStatus(detailData.walletAccount?.status) }}
      </el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">关联订单</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="关联订单号">{{ detailData.order?.orderNo || detailData.relatedOrderNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="订单标题">{{ detailData.order?.title || '-' }}</el-descriptions-item>
      <el-descriptions-item label="订单金额">{{ detailData.order?.orderAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="计价方式">
        <dict-tag
          v-if="detailData.order?.pricingMode"
          :type="DICT_TYPE.LB_PRICING_MODE"
          :value="detailData.order.pricingMode"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="订单状态">
        <dict-tag
          v-if="detailData.order?.status"
          :type="DICT_TYPE.LB_ORDER_STATUS"
          :value="detailData.order.status"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="拆单状态">
        {{ formatSplitStatus(detailData.order?.splitStatus) }}
      </el-descriptions-item>
      <el-descriptions-item label="支付单">
        {{ detailData.payOrder?.merchantOrderId || detailData.payOrder?.no || (detailData.order?.payOrderId ? '支付订单信息缺失' : '-') }}
      </el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">关联单元</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="关联单元号">{{ detailData.unit?.unitNo || detailData.relatedUnitNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="单元标题">{{ detailData.unit?.unitTitle || '-' }}</el-descriptions-item>
      <el-descriptions-item label="单元金额">{{ detailData.unit?.unitAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="单元状态">
        <dict-tag
          v-if="detailData.unit?.status"
          :type="DICT_TYPE.LB_ORDER_UNIT_STATUS"
          :value="detailData.unit.status"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="锁单状态">
        {{ detailData.unit?.isLocked ? '已锁定' : detailData.unit ? '未锁定' : '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="锁定原因">{{ detailData.unit?.lockReason || '-' }}</el-descriptions-item>
      <el-descriptions-item label="服务商">{{ formatUnitMerchantDisplay() }}</el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">支付与退款</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="支付订单">
        {{ detailData.payOrder?.merchantOrderId || detailData.payOrder?.no || (detailData.relatedPayOrderId ? '支付订单信息缺失' : '-') }}
      </el-descriptions-item>
      <el-descriptions-item label="支付流水号">{{ detailData.payOrder?.no || '-' }}</el-descriptions-item>
      <el-descriptions-item label="支付主题">{{ detailData.payOrder?.subject || '-' }}</el-descriptions-item>
      <el-descriptions-item label="支付金额">{{ detailData.payOrder?.price ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="支付状态">
        <dict-tag
          v-if="detailData.payOrder?.status !== undefined && detailData.payOrder?.status !== null"
          :type="DICT_TYPE.PAY_ORDER_STATUS"
          :value="detailData.payOrder.status"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="支付渠道">{{ detailData.payOrder?.channelCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="渠道订单号">
        {{ detailData.payOrder?.channelOrderNo || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="退款订单">
        {{ detailData.refund?.merchantRefundId || (detailData.relatedRefundId ? '退款订单信息缺失' : '-') }}
      </el-descriptions-item>
      <el-descriptions-item label="退款金额">{{ detailData.refund?.refundPrice ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="退款状态">
        <dict-tag
          v-if="detailData.refund?.status !== undefined && detailData.refund?.status !== null"
          :type="DICT_TYPE.PAY_REFUND_STATUS"
          :value="detailData.refund.status"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="审核状态">
        <dict-tag
          v-if="detailData.refund?.auditStatus"
          :type="DICT_TYPE.LB_AUDIT_STATUS"
          :value="detailData.refund.auditStatus"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="退款原因">{{ detailData.refund?.reason || '-' }}</el-descriptions-item>
      <el-descriptions-item label="审核备注">{{ detailData.refund?.auditRemark || '-' }}</el-descriptions-item>
      <el-descriptions-item label="驳回原因">{{ detailData.refund?.rejectReason || '-' }}</el-descriptions-item>
    </el-descriptions>
  </Dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { DICT_TYPE } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import { WalletFlowApi, type WalletFlowDetail } from '@/api/linbang/walletflow'
import { formatBizType, formatEnableStatus, formatFlowType, formatSplitStatus } from '../utils/display'

defineOptions({ name: 'WalletFlowDetailDialog' })

const dialogVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref<WalletFlowDetail>({} as WalletFlowDetail)

const formatUserDisplay = () => {
  const summary = [detailData.value.userNickname, detailData.value.userMobile, detailData.value.userNo]
    .filter(Boolean)
    .join(' / ')
  return summary || (detailData.value.userId ? '用户信息缺失' : '-')
}

const formatWalletAccountDisplay = () => {
  const account = detailData.value.walletAccount
  const summary = [account?.roleCode, account?.status].filter(Boolean).join(' / ')
  return summary || (detailData.value.walletAccountId ? '钱包账户信息缺失' : '-')
}

const formatUnitMerchantDisplay = () => {
  const unit = detailData.value.unit
  const summary = [unit?.merchantName, unit?.merchantContactName, unit?.merchantContactMobile]
    .filter(Boolean)
    .join(' / ')
  return summary || (unit?.merchantId ? '服务商信息缺失' : '-')
}

const open = async (id: number) => {
  dialogVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await WalletFlowApi.getWalletFlow(id)
  } finally {
    detailLoading.value = false
  }
}

defineExpose({ open })
</script>
