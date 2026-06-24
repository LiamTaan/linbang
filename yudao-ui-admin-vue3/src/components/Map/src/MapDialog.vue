<template>
  <Dialog title="位置服务" v-model="dialogVisible" width="560px">
    <div class="py-8">
      <el-empty description="当前项目未启用地图拾取能力" />
      <div class="mt-4 text-center text-sm text-[var(--el-text-color-secondary)]">
        如后续需要位置选择，请按项目规范接入高德地图 JS API。
      </div>
    </div>
    <template #footer>
      <el-button type="primary" @click="dialogVisible = false">我知道了</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const dialogVisible = ref(false)

/** 打开弹窗 */
const open = (_longitude?: number, _latitude?: number) => {
  dialogVisible.value = true
}

defineExpose({ open })
</script>
