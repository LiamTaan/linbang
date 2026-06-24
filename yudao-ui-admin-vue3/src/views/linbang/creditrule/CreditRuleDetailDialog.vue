<template>
    <Dialog v-model="dialogVisible" title="信用规则详情" width="820px" :loading="detailLoading">
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item label="规则编码">{{ detailData.ruleCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="规则名称">{{ detailData.ruleName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="触发类型">{{ formatTriggerType(detailData.triggerType) }}</el-descriptions-item>
      <el-descriptions-item label="分值变动">{{ detailData.scoreChange ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="规则方向">
        <el-tag :type="detailData.positiveRule ? 'success' : 'danger'">
          {{ detailData.positiveRule ? '加分规则' : '扣分规则' }}
        </el-tag>
      </el-descriptions-item>
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
      <el-descriptions-item label="同触发类型规则数">
        {{ detailData.sameTriggerRuleCount ?? 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="全局加分规则数">
        {{ detailData.positiveRuleCount ?? 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="全局扣分规则数">
        {{ detailData.negativeRuleCount ?? 0 }}
      </el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">同触发类型关联规则</el-divider>
      <el-table :data="detailData.relatedRules || []" size="small" border>
      <el-table-column label="规则编码" prop="ruleCode" min-width="140" />
      <el-table-column label="规则名称" prop="ruleName" min-width="180" />
      <el-table-column label="分值变动" prop="scoreChange" width="120" />
      <el-table-column label="触发类型" prop="triggerType" width="120">
        <template #default="{ row }">
          {{ formatTriggerType(row.triggerType) }}
        </template>
      </el-table-column>
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
import { CreditRuleApi, type CreditRuleDetail } from '@/api/linbang/creditrule'
import { formatEnableStatus, formatTriggerType } from '../utils/display'

defineOptions({ name: 'CreditRuleDetailDialog' })

const dialogVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref<CreditRuleDetail>({} as CreditRuleDetail)

const open = async (id: number) => {
  dialogVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await CreditRuleApi.getCreditRule(id)
    if (!detailData.value.relatedRules) {
      detailData.value.relatedRules = []
    }
  } finally {
    detailLoading.value = false
  }
}

defineExpose({ open })
</script>
