<template>
  <el-tabs v-model="activeTab" class="linbang-tabs">
    <el-tab-pane label="推广员" name="promoter">
      <PromoterIndex v-if="activeTab === 'promoter'" />
    </el-tab-pane>
    <el-tab-pane label="佣金记录" name="commission">
      <CommissionOrderIndex v-if="activeTab === 'commission'" />
    </el-tab-pane>
    <el-tab-pane label="区域合作商" name="partner">
      <PartnerInfoIndex v-if="activeTab === 'partner'" />
    </el-tab-pane>
    <el-tab-pane label="价格申报" name="priceReport">
      <MerchantPriceReportIndex v-if="activeTab === 'priceReport'" />
    </el-tab-pane>
    <el-tab-pane label="内容审核" name="content">
      <PromoteContentIndex v-if="activeTab === 'content'" />
    </el-tab-pane>
    <el-tab-pane label="处罚记录" name="penalty">
      <PromoterPenaltyRecordIndex v-if="activeTab === 'penalty'" />
    </el-tab-pane>
    <el-tab-pane label="推广申诉" name="appeal">
      <PromoteAppealIndex v-if="activeTab === 'appeal'" />
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
import { ref } from 'vue'

import CommissionOrderIndex from '../commissionorder/index.vue'
import MerchantPriceReportIndex from '../merchantpricereport/index.vue'
import PartnerInfoIndex from '../partnerinfo/index.vue'
import PromoteAppealIndex from '../promoteappeal/index.vue'
import PromoteContentIndex from '../promotecontent/index.vue'
import PromoterIndex from '../promoter/index.vue'
import PromoterPenaltyRecordIndex from '../promoterpenaltyrecord/index.vue'

defineOptions({ name: 'LinbangPromote' })

const activeTab = ref('promoter')
</script>

<style scoped>
.linbang-tabs :deep(.el-tabs__content) {
  padding-top: 4px;
}
</style>
