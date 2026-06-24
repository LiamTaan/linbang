<template>
  <Dialog v-model="dialogVisible" title="黑名单详情" width="760px" :loading="detailLoading">
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item label="用户">{{ formatUserDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="黑名单类型">{{ formatBlackType(detailData.blackType) }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="detailData.status === 'ENABLE' ? 'danger' : 'info'">
          {{ formatEnableStatus(detailData.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDate(detailData.createTime) }}</el-descriptions-item>
      <el-descriptions-item label="开始时间">{{ formatDate(detailData.startTime) }}</el-descriptions-item>
      <el-descriptions-item label="结束时间">{{ formatDate(detailData.endTime) }}</el-descriptions-item>
    </el-descriptions>
    <el-divider />
    <el-descriptions :column="1" border direction="vertical">
      <el-descriptions-item label="原因">{{ detailData.reason || '-' }}</el-descriptions-item>
    </el-descriptions>
    <el-divider />
    <el-descriptions :column="2" border>
      <el-descriptions-item label="用户编号">{{ detailData.user?.userNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="手机号">{{ detailData.user?.mobile || '-' }}</el-descriptions-item>
      <el-descriptions-item label="昵称">{{ detailData.user?.nickname || '-' }}</el-descriptions-item>
      <el-descriptions-item label="当前角色">
        <dict-tag
          v-if="detailData.user?.currentRoleCode"
          :type="DICT_TYPE.LB_ROLE_CODE"
          :value="detailData.user.currentRoleCode"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="用户状态">
        {{ formatEnableStatus(detailData.user?.status) }}
      </el-descriptions-item>
      <el-descriptions-item label="最后登录时间">
        {{ formatDate(detailData.user?.lastLoginTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="最后登录IP">{{ detailData.user?.lastLoginIp || '-' }}</el-descriptions-item>
    </el-descriptions>
    <el-divider />
    <el-table :data="detailData.recentRiskEvents || []" size="small" border>
      <el-table-column label="风险事件ID" prop="id" width="110" />
      <el-table-column label="业务类型" prop="bizType" width="120">
        <template #default="{ row }">
          {{ formatBizType(row.bizType) }}
        </template>
      </el-table-column>
      <el-table-column label="业务ID" prop="bizId" width="120" />
      <el-table-column label="风险类型" prop="riskType" width="120" />
      <el-table-column label="风险等级" prop="riskLevel" width="120">
        <template #default="{ row }">
          {{ formatRiskLevel(row.riskLevel) }}
        </template>
      </el-table-column>
      <el-table-column label="命中规则" prop="hitRuleCode" width="140" />
      <el-table-column label="状态" prop="status" width="120">
        <template #default="{ row }">
          {{ formatRiskEventStatus(row.status) }}
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="180" />
      <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
    </el-table>
  </Dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { DICT_TYPE } from '@/utils/dict'
import { formatDate, dateFormatter } from '@/utils/formatTime'
import { BlacklistApi, type BlacklistDetail } from '@/api/linbang/blacklist'
import {
  formatBizType,
  formatBlackType,
  formatEnableStatus,
  formatRiskEventStatus,
  formatRiskLevel
} from '../utils/display'

defineOptions({ name: 'BlacklistDetailDialog' })

const dialogVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref<BlacklistDetail>({} as BlacklistDetail)

const formatUserDisplay = () => {
  const summary = [detailData.value.user?.nickname, detailData.value.user?.mobile, detailData.value.user?.userNo]
    .filter(Boolean)
    .join(' / ')
  return summary || (detailData.value.userId ? '用户信息缺失' : '-')
}

const open = async (id: number) => {
  dialogVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await BlacklistApi.getBlacklist(id)
    if (!detailData.value.recentRiskEvents) {
      detailData.value.recentRiskEvents = []
    }
  } finally {
    detailLoading.value = false
  }
}

defineExpose({ open })
</script>
