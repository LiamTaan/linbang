<template>
  <ContentWrap>
    <el-form ref="formRef" :model="formData" label-width="120px" v-loading="loading">
      <el-form-item label="策略编码">
        <el-input v-model="formData.strategyCode" placeholder="请输入策略编码" />
      </el-form-item>
      <el-form-item label="策略名称">
        <el-input v-model="formData.strategyName" placeholder="请输入策略名称" />
      </el-form-item>
      <el-form-item label="扩圈阶段">
        <div class="stage-config">
          <el-table :data="stageRows" border row-key="stageNo">
            <el-table-column label="轮次" width="88" align="center">
              <template #default="{ $index }">第 {{ $index + 1 }} 轮</template>
            </el-table-column>
            <el-table-column label="从多少公里开始" min-width="180">
              <template #default="{ row }">
                <el-input-number v-model="row.radiusStartKm" :min="0" :precision="2" :step="0.1" controls-position="right" />
              </template>
            </el-table-column>
            <el-table-column label="扩到多少公里" min-width="180">
              <template #default="{ row }">
                <el-input-number v-model="row.radiusEndKm" :min="0.01" :precision="2" :step="0.1" controls-position="right" />
              </template>
            </el-table-column>
            <el-table-column label="本轮等待时间" min-width="170">
              <template #default="{ row }">
                <el-input-number v-model="row.durationSeconds" :min="1" :max="3600" :step="30" controls-position="right" />
                <span class="unit-text">秒</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="92" align="center">
              <template #default="{ $index }">
                <el-button link type="danger" :disabled="stageRows.length <= 1" @click="removeStage($index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="stage-actions">
            <el-button :disabled="stageRows.length >= formData.maxStageCount" @click="addStage">新增一轮</el-button>
            <el-button @click="resetDefaultStages">恢复默认 5 轮</el-button>
          </div>
        </div>
      </el-form-item>
      <el-form-item label="最大阶段数">
        <el-input-number v-model="formData.maxStageCount" :min="1" :max="10" />
      </el-form-item>
      <el-form-item label="最大扩圈半径">
        <el-input-number v-model="formData.maxRadiusKm" :min="0" :precision="2" />
      </el-form-item>
      <el-form-item label="流单建议模板">
        <el-input v-model="formData.flowAdviceTemplate" type="textarea" :rows="4" />
      </el-form-item>
      <el-form-item label="自动派单总开关">
        <el-switch v-model="formData.autoDispatchEnabled" />
      </el-form-item>
      <el-form-item label="自动退款">
        <el-switch v-model="formData.autoRefundEnabled" />
      </el-form-item>
      <el-form-item label="退款重试次数">
        <el-input-number v-model="formData.autoRefundRetryTimes" :min="0" :max="10" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="saving" @click="submit">保存策略</el-button>
        <el-button @click="loadData">刷新</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { useMessage } from '@/hooks/web/useMessage'
import { MatchStrategyApi, type MatchStrategy } from '@/api/linbang/matchstrategy'

defineOptions({ name: 'LinbangMatchStrategy' })

const message = useMessage()
const loading = ref(false)
const saving = ref(false)

interface StageConfigRow {
  stageNo: number
  radiusStartKm: number
  radiusEndKm: number
  durationSeconds: number
}

const defaultStages = (): StageConfigRow[] => [
  { stageNo: 1, radiusStartKm: 0, radiusEndKm: 0.5, durationSeconds: 60 },
  { stageNo: 2, radiusStartKm: 0.5, radiusEndKm: 1, durationSeconds: 60 },
  { stageNo: 3, radiusStartKm: 1, radiusEndKm: 2, durationSeconds: 60 },
  { stageNo: 4, radiusStartKm: 2, radiusEndKm: 5, durationSeconds: 60 },
  { stageNo: 5, radiusStartKm: 5, radiusEndKm: 999, durationSeconds: 60 }
]

const stageRows = ref<StageConfigRow[]>(defaultStages())
const formData = reactive<MatchStrategy>({
  strategyCode: '',
  strategyName: '',
  stageConfigJson: '',
  maxStageCount: 5,
  maxRadiusKm: 999,
  flowAdviceTemplate: '',
  autoDispatchEnabled: true,
  autoRefundEnabled: true,
  autoRefundRetryTimes: 3
})

const normalizeStages = () => {
  stageRows.value = stageRows.value.map((row, index) => ({
    ...row,
    stageNo: index + 1
  }))
}

const parseStages = (stageConfigJson?: string) => {
  if (!stageConfigJson) {
    stageRows.value = defaultStages()
    return
  }
  try {
    const parsed = JSON.parse(stageConfigJson)
    if (!Array.isArray(parsed) || parsed.length === 0) {
      throw new Error('阶段配置不能为空')
    }
    stageRows.value = parsed.map((row, index) => ({
      stageNo: index + 1,
      radiusStartKm: Number(row.radiusStartKm ?? 0),
      radiusEndKm: Number(row.radiusEndKm ?? 0),
      durationSeconds: Number(row.durationSeconds ?? 60)
    }))
    normalizeStages()
  } catch {
    stageRows.value = defaultStages()
    message.warning('阶段配置格式异常，已按默认 5 轮展示，请检查后再保存')
  }
}

const buildStageConfigJson = () => {
  normalizeStages()
  return JSON.stringify(
    stageRows.value.map((row) => ({
      stageNo: row.stageNo,
      radiusStartKm: Number(row.radiusStartKm.toFixed(2)),
      radiusEndKm: Number(row.radiusEndKm.toFixed(2)),
      durationSeconds: row.durationSeconds
    }))
  )
}

const validateStages = () => {
  if (stageRows.value.length < 1) {
    message.error('至少需要配置 1 轮扩圈')
    return false
  }
  if (stageRows.value.length > formData.maxStageCount) {
    message.error('扩圈轮次数不能超过最大阶段数')
    return false
  }
  for (const [index, row] of stageRows.value.entries()) {
    if (row.radiusEndKm <= row.radiusStartKm) {
      message.error(`第 ${index + 1} 轮的结束公里数必须大于开始公里数`)
      return false
    }
    if (row.durationSeconds <= 0) {
      message.error(`第 ${index + 1} 轮的等待时间必须大于 0 秒`)
      return false
    }
    if (row.radiusEndKm > formData.maxRadiusKm) {
      message.error(`第 ${index + 1} 轮的结束公里数不能超过最大扩圈半径`)
      return false
    }
    if (index > 0 && row.radiusStartKm < stageRows.value[index - 1].radiusEndKm) {
      message.error(`第 ${index + 1} 轮的开始公里数不能小于上一轮结束公里数`)
      return false
    }
  }
  return true
}

const addStage = () => {
  const lastStage = stageRows.value[stageRows.value.length - 1]
  const radiusStartKm = lastStage?.radiusEndKm ?? 0
  const radiusEndKm = Number((radiusStartKm + 1).toFixed(2))
  stageRows.value.push({
    stageNo: stageRows.value.length + 1,
    radiusStartKm,
    radiusEndKm,
    durationSeconds: lastStage?.durationSeconds ?? 60
  })
}

const removeStage = (index: number) => {
  stageRows.value.splice(index, 1)
  normalizeStages()
}

const resetDefaultStages = () => {
  stageRows.value = defaultStages()
  formData.maxStageCount = Math.max(formData.maxStageCount, 5)
  formData.maxRadiusKm = Math.max(formData.maxRadiusKm, 999)
}

const loadData = async () => {
  loading.value = true
  try {
    const data = await MatchStrategyApi.getMatchStrategy()
    if (data) {
      Object.assign(formData, data)
      parseStages(data.stageConfigJson)
    }
  } finally {
    loading.value = false
  }
}

const submit = async () => {
  if (!validateStages()) {
    return
  }
  saving.value = true
  try {
    formData.stageConfigJson = buildStageConfigJson()
    await MatchStrategyApi.updateMatchStrategy(formData)
    message.success('匹配策略已保存')
  } finally {
    saving.value = false
  }
}

watch(
  () => formData.maxStageCount,
  (maxStageCount) => {
    if (stageRows.value.length > maxStageCount) {
      stageRows.value = stageRows.value.slice(0, maxStageCount)
      normalizeStages()
    }
  }
)

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.stage-config {
  width: 100%;
}

.stage-config :deep(.el-input-number) {
  width: 150px;
}

.unit-text {
  margin-left: 8px;
  color: var(--el-text-color-secondary);
}

.stage-actions {
  display: flex;
  gap: 12px;
  margin-top: 12px;
}
</style>
