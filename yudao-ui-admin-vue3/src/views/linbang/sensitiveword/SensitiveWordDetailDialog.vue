<template>
  <Dialog v-model="dialogVisible" title="敏感词详情" width="820px" :loading="detailLoading">
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item label="词条ID">{{ detailData.id ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="关键词">{{ detailData.word || '-' }}</el-descriptions-item>
      <el-descriptions-item label="词库类型">{{ formatSensitiveWordType(detailData.wordType) }}</el-descriptions-item>
      <el-descriptions-item label="匹配方式">{{ formatSensitiveWordMatchType(detailData.matchType) }}</el-descriptions-item>
      <el-descriptions-item label="拦截级别">{{ detailData.blockLevel || '-' }}</el-descriptions-item>
      <el-descriptions-item label="适用场景">{{ detailData.sceneType || '-' }}</el-descriptions-item>
      <el-descriptions-item label="替换文案">{{ detailData.replaceText || '-' }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="detailData.status === 'ENABLE' ? 'success' : 'info'">
          {{ formatEnableStatus(detailData.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDate(detailData.createTime) }}</el-descriptions-item>
      <el-descriptions-item label="更新时间">{{ formatDate(detailData.updateTime) }}</el-descriptions-item>
    </el-descriptions>
    <el-divider />
    <el-descriptions :column="3" border>
      <el-descriptions-item label="同词库词条数">
        {{ detailData.sameWordTypeCount ?? 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="同匹配方式词条数">
        {{ detailData.sameMatchTypeCount ?? 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="同拦截级别词条数">
        {{ detailData.sameBlockLevelCount ?? 0 }}
      </el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">同词库关联词</el-divider>
    <el-table :data="detailData.relatedWords || []" size="small" border>
      <el-table-column label="词条ID" prop="id" width="100" />
      <el-table-column label="关键词" prop="word" min-width="160" />
      <el-table-column label="匹配方式" prop="matchType" width="120">
        <template #default="{ row }">
          {{ formatSensitiveWordMatchType(row.matchType) }}
        </template>
      </el-table-column>
      <el-table-column label="适用场景" prop="sceneType" min-width="150" />
      <el-table-column label="拦截级别" prop="blockLevel" width="120" />
      <el-table-column label="状态" prop="status" width="120">
        <template #default="{ row }">
          {{ formatEnableStatus(row.status) }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
    </el-table>
  </Dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { dateFormatter, formatDate } from '@/utils/formatTime'
import { SensitiveWordApi, type SensitiveWordDetail } from '@/api/linbang/sensitiveword'
import { formatEnableStatus, formatSensitiveWordMatchType, formatSensitiveWordType } from '../utils/display'

defineOptions({ name: 'SensitiveWordDetailDialog' })

const dialogVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref<SensitiveWordDetail>({} as SensitiveWordDetail)

const open = async (id: number) => {
  dialogVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await SensitiveWordApi.getSensitiveWord(id)
    if (!detailData.value.relatedWords) {
      detailData.value.relatedWords = []
    }
  } finally {
    detailLoading.value = false
  }
}

defineExpose({ open })
</script>
