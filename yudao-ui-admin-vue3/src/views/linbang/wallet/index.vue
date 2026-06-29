<template>
  <el-tabs v-model="activeTab" class="linbang-tabs">
    <el-tab-pane label="支付订单" name="pay-order">
      <PayOrderIndex v-if="activeTab === 'pay-order'" />
    </el-tab-pane>
    <el-tab-pane label="钱包账户" name="account">
      <WalletAccountIndex v-if="activeTab === 'account'" />
    </el-tab-pane>
    <el-tab-pane label="资金流水" name="flow">
      <WalletFlowIndex v-if="activeTab === 'flow'" />
    </el-tab-pane>
    <el-tab-pane label="提现申请" name="withdraw">
      <WalletWithdrawIndex v-if="activeTab === 'withdraw'" />
    </el-tab-pane>
    <el-tab-pane label="退款审核" name="refund">
      <PayRefundIndex v-if="activeTab === 'refund'" />
    </el-tab-pane>
    <el-tab-pane label="银行卡" name="bank-card">
      <WalletBankCardIndex v-if="activeTab === 'bank-card'" />
    </el-tab-pane>
    <el-tab-pane label="分账规则" name="divide-rule">
      <DivideRuleIndex v-if="activeTab === 'divide-rule'" />
    </el-tab-pane>
    <el-tab-pane label="分账明细" name="divide-record">
      <OrderDivideRecordIndex v-if="activeTab === 'divide-record'" />
    </el-tab-pane>
    <el-tab-pane label="托管凭证" name="escrow-proof">
      <EscrowProofIndex v-if="activeTab === 'escrow-proof'" />
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
import { ref } from 'vue'

import DivideRuleIndex from '../dividerule/index.vue'
import EscrowProofIndex from '../escrowproof/index.vue'
import OrderDivideRecordIndex from '../orderdividerecord/index.vue'
import PayOrderIndex from '@/views/pay/order/index.vue'
import PayRefundIndex from '@/views/pay/refund/index.vue'
import WalletAccountIndex from '../walletaccount/index.vue'
import WalletBankCardIndex from '../walletbankcard/index.vue'
import WalletFlowIndex from '../walletflow/index.vue'
import WalletWithdrawIndex from '../walletwithdraw/index.vue'

defineOptions({ name: 'LinbangWallet' })

const activeTab = ref('pay-order')
</script>

<style scoped>
.linbang-tabs :deep(.el-tabs__content) {
  padding-top: 4px;
}
</style>
