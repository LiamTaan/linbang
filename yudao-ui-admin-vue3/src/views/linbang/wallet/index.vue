<template>
  <el-tabs v-model="activeTab" class="linbang-tabs">
    <el-tab-pane label="支付订单" name="pay-order">
      <PayOrderIndex />
    </el-tab-pane>
    <el-tab-pane label="钱包账户" name="account">
      <WalletAccountIndex />
    </el-tab-pane>
    <el-tab-pane label="资金流水" name="flow">
      <WalletFlowIndex />
    </el-tab-pane>
    <el-tab-pane label="提现申请" name="withdraw">
      <WalletWithdrawIndex />
    </el-tab-pane>
    <el-tab-pane label="退款审核" name="refund">
      <PayRefundIndex />
    </el-tab-pane>
    <el-tab-pane label="银行卡" name="bank-card">
      <WalletBankCardIndex />
    </el-tab-pane>
    <el-tab-pane label="分账规则" name="divide-rule">
      <DivideRuleIndex />
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
import { ref } from 'vue'

import DivideRuleIndex from '../dividerule/index.vue'
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
