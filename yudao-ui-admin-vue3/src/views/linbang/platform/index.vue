<template>
  <el-tabs v-model="activeTab" class="linbang-tabs">
    <el-tab-pane label="帮助反馈" name="feedback">
      <HelpFeedbackIndex v-if="activeTab === 'feedback'" />
    </el-tab-pane>
    <el-tab-pane label="平台配置" name="config">
      <PlatformConfigIndex v-if="activeTab === 'config'" />
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
import { ref } from 'vue'

import HelpFeedbackIndex from '../helpfeedback/index.vue'
import PlatformConfigIndex from '../platformconfig/index.vue'

defineOptions({ name: 'LinbangPlatform' })

const activeTab = ref('feedback')
</script>

<style scoped>
.linbang-tabs :deep(.el-tabs__content) {
  padding-top: 4px;
}
</style>
