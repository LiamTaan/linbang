<template>
  <el-tabs v-model="activeTab" class="linbang-tabs">
    <el-tab-pane label="投诉处理" name="complaint">
      <ComplaintIndex />
    </el-tab-pane>
    <el-tab-pane label="申诉审核" name="appeal">
      <AppealIndex />
    </el-tab-pane>
    <el-tab-pane label="评价记录" name="comment">
      <ReviewCommentIndex />
    </el-tab-pane>
    <el-tab-pane label="信用记录" name="credit-record">
      <CreditRecordIndex />
    </el-tab-pane>
    <el-tab-pane label="信用规则" name="credit-rule">
      <CreditRuleIndex />
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
