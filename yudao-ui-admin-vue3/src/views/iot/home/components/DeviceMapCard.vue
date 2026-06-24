<template>
  <el-card class="chart-card" shadow="never">
    <template #header>
      <div class="flex items-center justify-between">
        <span class="text-base font-medium text-gray-600">设备分布地图</span>
      </div>
    </template>
    <div class="h-[320px] flex flex-col justify-center items-center text-center px-6">
      <el-empty description="当前项目未启用地图展示能力" />
      <div class="mt-3 text-sm text-[var(--el-text-color-secondary)]">
        邻里互助项目统一要求地图能力使用高德，当前管理端未接入该能力。
      </div>
    </div>
  </el-card>
</template>

<script lang="ts" setup>
defineOptions({ name: 'DeviceMapCard' })
</script>
