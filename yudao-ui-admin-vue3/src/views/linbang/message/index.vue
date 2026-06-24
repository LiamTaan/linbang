<template>
  <el-tabs v-model="activeTab" class="linbang-tabs">
    <el-tab-pane label="消息模板" name="template">
      <MessageTemplateIndex v-if="activeTab === 'template'" />
    </el-tab-pane>
    <el-tab-pane label="发送记录" name="record">
      <MessageRecordIndex v-if="activeTab === 'record'" />
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
import { ref } from 'vue'

import MessageRecordIndex from '../messagerecord/index.vue'
import MessageTemplateIndex from '../messagetemplate/index.vue'

defineOptions({ name: 'LinbangMessage' })

const activeTab = ref('template')
</script>

<style scoped>
.linbang-tabs :deep(.el-tabs__content) {
  padding-top: 4px;
}
</style>
