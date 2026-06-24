<template>
    <Dialog v-model="dialogVisible" title="风控规则详情" width="900px" :loading="detailLoading">
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item label="规则编码">{{ detailData.ruleCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="规则名称">{{ detailData.ruleName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="规则分组">{{ formatRiskRuleGroup(detailData.ruleGroup) }}</el-descriptions-item>
      <el-descriptions-item label="规则值">{{ detailData.ruleValue || '-' }}</el-descriptions-item>
      <el-descriptions-item label="值类型">{{ formatRiskRuleValueType(detailData.valueType) }}</el-descriptions-item>
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
      <el-descriptions-item label="累计命中事件">
        {{ detailData.hitCount ?? 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="待处理事件">
        {{ detailData.pendingEventCount ?? 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="关联黑名单用户">
        {{ detailData.userBlacklistCount ?? 0 }}
      </el-descriptions-item>
    </el-descriptions>
    <el-divider />
    <el-descriptions :column="1" border direction="vertical">
      <el-descriptions-item label="备注">{{ detailData.remark || '-' }}</el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">最近命中事件</el-divider>
    <el-table :data="detailData.recentEvents || []" size="small" border>
      <el-table-column label="业务类型" prop="bizType" width="120">
        <template #default="{ row }">
          {{ formatBizType(row.bizType) }}
        </template>
      </el-table-column>
      <el-table-column label="业务对象" min-width="220">
        <template #default="{ row }">
          {{ formatRiskEventBizDisplay(row) }}
        </template>
      </el-table-column>
      <el-table-column label="风险类型" prop="riskType" width="120" />
      <el-table-column label="风险等级" prop="riskLevel" width="120">
        <template #default="{ row }">
          {{ formatRiskLevel(row.riskLevel) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="120">
        <template #default="{ row }">
          {{ formatRiskEventStatus(row.status) }}
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="180" />
      <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
    </el-table>
    <el-divider content-position="left">关联黑名单</el-divider>
    <el-table :data="detailData.relatedBlacklists || []" size="small" border>
      <el-table-column label="用户" min-width="220">
        <template #default="scope">
          {{ formatUserDisplay(scope.row.user, scope.row.userId) }}
        </template>
      </el-table-column>
      <el-table-column label="用户编号" min-width="120">
        <template #default="scope">
          {{ scope.row.user?.userNo || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="手机号" min-width="140">
        <template #default="scope">
          {{ scope.row.user?.mobile || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="昵称" min-width="120">
        <template #default="scope">
          {{ scope.row.user?.nickname || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="黑名单类型" prop="blackType" width="120">
        <template #default="{ row }">
          {{ formatBlackType(row.blackType) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="120">
        <template #default="{ row }">
          {{ formatEnableStatus(row.status) }}
        </template>
      </el-table-column>
      <el-table-column label="原因" prop="reason" min-width="180" />
      <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
    </el-table>
  </Dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { dateFormatter, formatDate } from '@/utils/formatTime'
import {
  RiskRuleApi,
  type RiskRuleBlacklist,
  type RiskRuleDetail,
  type RiskRuleEvent
} from '@/api/linbang/riskrule'
import {
  formatBizType,
  formatBlackType,
  formatEnableStatus,
  formatRiskEventStatus,
  formatRiskLevel,
  formatRiskRuleGroup,
  formatRiskRuleValueType
} from '../utils/display'

defineOptions({ name: 'RiskRuleDetailDialog' })

const dialogVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref<RiskRuleDetail>({} as RiskRuleDetail)

const formatUserDisplay = (user?: RiskRuleBlacklist['user'], fallbackUserId?: number) => {
  const summary = [user?.nickname, user?.mobile, user?.userNo].filter(Boolean).join(' / ')
  return summary || (fallbackUserId ? '用户信息缺失' : '-')
}

const formatRiskEventBizDisplay = (row: RiskRuleEvent) => {
  if (row.bizType === 'USER') {
    return formatUserDisplay(row.user, row.bizId)
  }
  return row.bizId ?? '-'
}

const open = async (id: number) => {
  dialogVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await RiskRuleApi.getRiskRule(id)
    if (!detailData.value.recentEvents) {
      detailData.value.recentEvents = []
    }
    if (!detailData.value.relatedBlacklists) {
      detailData.value.relatedBlacklists = []
    }
  } finally {
    detailLoading.value = false
  }
}

defineExpose({ open })
</script>
