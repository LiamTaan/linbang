<template>
  <el-tabs v-model="activeTab" class="linbang-tabs">
    <el-tab-pane label="消息场景" name="scene">
      <MessageSceneIndex v-if="activeTab === 'scene'" />
    </el-tab-pane>
    <el-tab-pane label="消息模板" name="template">
      <MessageTemplateIndex v-if="activeTab === 'template'" />
    </el-tab-pane>
    <el-tab-pane label="投放活动" name="campaign">
      <MessageCampaignIndex v-if="activeTab === 'campaign'" />
    </el-tab-pane>
    <el-tab-pane label="反馈统计" name="feedback">
      <MessageFeedbackIndex v-if="activeTab === 'feedback'" />
    </el-tab-pane>
    <el-tab-pane label="优化工作台" name="optimization">
      <MessageOptimizationIndex v-if="activeTab === 'optimization'" />
    </el-tab-pane>
    <el-tab-pane label="发送记录" name="record">
      <MessageRecordIndex v-if="activeTab === 'record'" />
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
import { ref } from 'vue'

import MessageCampaignIndex from '../messagecampaign/index.vue'
import MessageFeedbackIndex from '../messagefeedback/index.vue'
import MessageOptimizationIndex from '../messageoptimization/index.vue'
import MessageRecordIndex from '../messagerecord/index.vue'
import MessageSceneIndex from '../messagescene/index.vue'
import MessageTemplateIndex from '../messagetemplate/index.vue'

defineOptions({ name: 'LinbangMessage' })

const activeTab = ref('scene')
</script>

<style scoped>
.linbang-tabs :deep(.el-tabs__content) {
  padding-top: 4px;
}
</style>
