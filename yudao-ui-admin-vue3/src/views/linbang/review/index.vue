<template>
  <el-tabs v-model="activeTab" class="linbang-tabs">
    <el-tab-pane label="投诉处理" name="complaint">
      <ComplaintIndex v-if="activeTab === 'complaint'" />
    </el-tab-pane>
    <el-tab-pane label="申诉审核" name="appeal">
      <AppealIndex v-if="activeTab === 'appeal'" />
    </el-tab-pane>
    <el-tab-pane label="评价记录" name="comment">
      <ReviewCommentIndex v-if="activeTab === 'comment'" />
    </el-tab-pane>
    <el-tab-pane label="信用记录" name="credit-record">
      <CreditRecordIndex v-if="activeTab === 'credit-record'" />
    </el-tab-pane>
    <el-tab-pane label="信用规则" name="credit-rule">
      <CreditRuleIndex v-if="activeTab === 'credit-rule'" />
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
import { ref } from 'vue'

import AppealIndex from '../appeal/index.vue'
import ComplaintIndex from '../complaint/index.vue'
import CreditRecordIndex from '../creditrecord/index.vue'
import CreditRuleIndex from '../creditrule/index.vue'
import ReviewCommentIndex from '../reviewcomment/index.vue'

defineOptions({ name: 'LinbangReview' })

const activeTab = ref('complaint')
</script>

<style scoped>
.linbang-tabs :deep(.el-tabs__content) {
  padding-top: 4px;
}
</style>
