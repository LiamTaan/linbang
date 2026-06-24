<template>
  <el-tabs v-model="activeTab" class="linbang-tabs">
    <el-tab-pane label="风控规则" name="rule">
      <RiskRuleIndex v-if="activeTab === 'rule'" />
    </el-tab-pane>
    <el-tab-pane label="敏感词" name="word">
      <SensitiveWordIndex v-if="activeTab === 'word'" />
    </el-tab-pane>
    <el-tab-pane label="风险事件" name="event">
      <RiskEventIndex v-if="activeTab === 'event'" />
    </el-tab-pane>
    <el-tab-pane label="黑名单" name="blacklist">
      <BlacklistIndex v-if="activeTab === 'blacklist'" />
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
import { ref } from 'vue'

import BlacklistIndex from '../blacklist/index.vue'
import RiskEventIndex from '../riskevent/index.vue'
import RiskRuleIndex from '../riskrule/index.vue'
import SensitiveWordIndex from '../sensitiveword/index.vue'

defineOptions({ name: 'LinbangRisk' })

const activeTab = ref('rule')
</script>

<style scoped>
.linbang-tabs :deep(.el-tabs__content) {
  padding-top: 4px;
}
</style>
