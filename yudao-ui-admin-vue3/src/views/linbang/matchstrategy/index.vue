<template>
  <ContentWrap>
    <el-form ref="formRef" :model="formData" label-width="120px" v-loading="loading">
      <el-form-item label="策略编码">
        <el-input v-model="formData.strategyCode" placeholder="请输入策略编码" />
      </el-form-item>
      <el-form-item label="策略名称">
        <el-input v-model="formData.strategyName" placeholder="请输入策略名称" />
      </el-form-item>
      <el-form-item label="阶段配置 JSON">
        <el-input v-model="formData.stageConfigJson" type="textarea" :rows="8" placeholder="请输入阶段配置 JSON" />
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
import { onMounted, reactive, ref } from 'vue'
import { useMessage } from '@/hooks/web/useMessage'
import { MatchStrategyApi, type MatchStrategy } from '@/api/linbang/matchstrategy'

defineOptions({ name: 'LinbangMatchStrategy' })

const message = useMessage()
const loading = ref(false)
const saving = ref(false)
const formData = reactive<MatchStrategy>({
  strategyCode: '',
  strategyName: '',
  stageConfigJson: '',
  maxStageCount: 5,
  maxRadiusKm: 5,
  flowAdviceTemplate: '',
  autoDispatchEnabled: true,
  autoRefundEnabled: true,
  autoRefundRetryTimes: 3
})

const loadData = async () => {
  loading.value = true
  try {
    const data = await MatchStrategyApi.getMatchStrategy()
    if (data) {
      Object.assign(formData, data)
    }
  } finally {
    loading.value = false
  }
}

const submit = async () => {
  saving.value = true
  try {
    await MatchStrategyApi.updateMatchStrategy(formData)
    message.success('匹配策略已保存')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>
