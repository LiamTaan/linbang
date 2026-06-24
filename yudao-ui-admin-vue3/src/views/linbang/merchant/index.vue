<template>
  <el-tabs v-model="activeTab" class="linbang-tabs">
    <el-tab-pane label="服务商信息" name="info">
      <MerchantInfoIndex v-if="activeTab === 'info'" />
    </el-tab-pane>
    <el-tab-pane label="入驻申请" name="entry">
      <MerchantEntryIndex v-if="activeTab === 'entry'" />
    </el-tab-pane>
    <el-tab-pane label="服务类目" name="category">
      <MerchantCategoryIndex v-if="activeTab === 'category'" />
    </el-tab-pane>
    <el-tab-pane label="服务点管理" name="servicePoint">
      <MerchantServicePointIndex v-if="activeTab === 'servicePoint'" />
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
import { ref } from 'vue'

import MerchantCategoryIndex from '../merchantcategory/index.vue'
import MerchantEntryIndex from '../merchantentry/index.vue'
import MerchantInfoIndex from '../merchantinfo/index.vue'
import MerchantServicePointIndex from '../merchantservicepoint/index.vue'

defineOptions({ name: 'LinbangMerchant' })

const activeTab = ref('info')
</script>

<style scoped>
.linbang-tabs :deep(.el-tabs__content) {
  padding-top: 4px;
}
</style>
