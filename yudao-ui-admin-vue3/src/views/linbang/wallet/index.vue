<template>
  <div class="linbang-wallet-page">
    <ContentWrap>
      <div class="wallet-hero">
        <div>
          <p class="wallet-hero__eyebrow">Finance Hub</p>
          <h2 class="wallet-hero__title">资金中心与统一聚合支付</h2>
          <p class="wallet-hero__desc">
            微信、支付宝、H5、钱包展示入口等支付方式统一落到第三方聚合支付；资金中心聚焦支付订单、退款审核、提现、银行卡、分账与托管凭证的运营闭环，支付配置与回调排障下沉到支付中心。
          </p>
        </div>
        <div class="wallet-hero__chips">
          <span>唯一聚合通道</span>
          <span>订单对账</span>
          <span>钱包账务</span>
          <span>退款与提现</span>
        </div>
      </div>
      <el-alert type="warning" :closable="false" show-icon>
        <template #title>聚合支付接入基线</template>
        <template #default>
          当前第三方文档明确支持统一下单、订单查询、支付成功回调；退款、提现打款、绑卡校验需等待聚合支付补充接口后再接入，管理端不再按微信/支付宝拆分配置商户号。
        </template>
      </el-alert>
    </ContentWrap>

    <el-tabs v-model="activeTab" class="linbang-tabs">
      <el-tab-pane label="支付订单" name="pay-order">
        <PayOrderIndex v-if="activeTab === 'pay-order'" />
      </el-tab-pane>
      <el-tab-pane label="退款审核" name="refund">
        <PayRefundIndex v-if="activeTab === 'refund'" />
      </el-tab-pane>
      <el-tab-pane label="转账单" name="transfer">
        <PayTransferIndex v-if="activeTab === 'transfer'" />
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
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

import DivideRuleIndex from '../dividerule/index.vue'
import EscrowProofIndex from '../escrowproof/index.vue'
import OrderDivideRecordIndex from '../orderdividerecord/index.vue'
import PayOrderIndex from '@/views/pay/order/index.vue'
import PayRefundIndex from '@/views/pay/refund/index.vue'
import PayTransferIndex from '@/views/pay/transfer/index.vue'
import WalletAccountIndex from '../walletaccount/index.vue'
import WalletBankCardIndex from '../walletbankcard/index.vue'
import WalletFlowIndex from '../walletflow/index.vue'
import WalletWithdrawIndex from '../walletwithdraw/index.vue'

defineOptions({ name: 'LinbangWallet' })

const activeTab = ref('pay-order')
</script>

<style scoped>
.linbang-wallet-page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.wallet-hero {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  padding: 4px 0 12px;
}

.wallet-hero__eyebrow {
  margin: 0 0 8px;
  color: #b45309;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.wallet-hero__title {
  margin: 0;
  color: #111827;
  font-size: 28px;
  font-weight: 700;
  line-height: 1.2;
}

.wallet-hero__desc {
  max-width: 840px;
  margin: 12px 0 0;
  color: #4b5563;
  line-height: 1.7;
}

.wallet-hero__chips {
  display: flex;
  flex-wrap: wrap;
  align-content: flex-start;
  justify-content: flex-end;
  gap: 10px;
  min-width: 260px;
}

.wallet-hero__chips span {
  padding: 8px 12px;
  border: 1px solid #f3d6a4;
  border-radius: 999px;
  background: linear-gradient(135deg, #fff7ed 0%, #fffbeb 100%);
  color: #9a3412;
  font-size: 12px;
  font-weight: 600;
}

.linbang-tabs :deep(.el-tabs__content) {
  padding-top: 4px;
}

@media (max-width: 900px) {
  .wallet-hero {
    flex-direction: column;
  }

  .wallet-hero__chips {
    justify-content: flex-start;
    min-width: auto;
  }
}
</style>
