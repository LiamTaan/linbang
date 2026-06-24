<template>
  <el-tabs v-model="activeTab" class="linbang-tabs">
    <el-tab-pane label="订单总览" name="info">
      <OrderInfoIndex />
    </el-tab-pane>
    <el-tab-pane label="单元订单" name="unit">
      <OrderUnitIndex />
    </el-tab-pane>
    <el-tab-pane label="异常订单" name="abnormal">
      <OrderAbnormalIndex />
    </el-tab-pane>
    <el-tab-pane label="抢单记录" name="accept">
      <OrderAcceptRecordIndex />
    </el-tab-pane>
    <el-tab-pane label="匹配记录" name="match">
      <OrderMatchRecordIndex />
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
import { ref } from 'vue'

import OrderAbnormalIndex from '../orderabnormal/index.vue'
import OrderAcceptRecordIndex from '../orderacceptrecord/index.vue'
import OrderInfoIndex from '../orderinfo/index.vue'
import OrderMatchRecordIndex from '../ordermatchrecord/index.vue'
import OrderUnitIndex from '../orderunit/index.vue'

defineOptions({ name: 'LinbangOrder' })

const activeTab = ref('info')
</script>

<style scoped>
.linbang-tabs :deep(.el-tabs__content) {
  padding-top: 4px;
}
</style>
