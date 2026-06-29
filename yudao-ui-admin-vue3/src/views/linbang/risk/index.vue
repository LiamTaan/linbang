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
    <el-tab-pane label="限制记录" name="restrict">
      <UserRestrictRecordIndex v-if="activeTab === 'restrict'" />
    </el-tab-pane>
    <el-tab-pane label="冻结资金" name="frozen">
      <UserFrozenFundRecordIndex v-if="activeTab === 'frozen'" />
    </el-tab-pane>
    <el-tab-pane label="处罚日志" name="punish-log">
      <PunishLogIndex v-if="activeTab === 'punish-log'" />
    </el-tab-pane>
    <el-tab-pane label="设备监控" name="device">
      <UserDeviceIndex v-if="activeTab === 'device'" />
    </el-tab-pane>
    <el-tab-pane label="关联账号" name="relation">
      <UserRiskRelationIndex v-if="activeTab === 'relation'" />
    </el-tab-pane>
    <el-tab-pane label="敏感词命中" name="hit-log">
      <SensitiveHitLogIndex v-if="activeTab === 'hit-log'" />
    </el-tab-pane>
    <el-tab-pane label="黑名单" name="blacklist">
      <BlacklistIndex v-if="activeTab === 'blacklist'" />
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
import { ref } from 'vue'

import BlacklistIndex from '../blacklist/index.vue'
import PunishLogIndex from '../punishlog/index.vue'
import RiskEventIndex from '../riskevent/index.vue'
import RiskRuleIndex from '../riskrule/index.vue'
import SensitiveHitLogIndex from '../sensitivehitlog/index.vue'
import SensitiveWordIndex from '../sensitiveword/index.vue'
import UserDeviceIndex from '../userdevice/index.vue'
import UserFrozenFundRecordIndex from '../userfrozenfundrecord/index.vue'
import UserRiskRelationIndex from '../userriskrelation/index.vue'
import UserRestrictRecordIndex from '../userrestrictrecord/index.vue'

defineOptions({ name: 'LinbangRisk' })

const activeTab = ref('rule')
</script>

<style scoped>
.linbang-tabs :deep(.el-tabs__content) {
  padding-top: 4px;
}
</style>
