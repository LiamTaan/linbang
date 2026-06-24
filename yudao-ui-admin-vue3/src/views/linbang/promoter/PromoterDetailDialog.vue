<template>
  <Dialog v-model="dialogVisible" title="推广员详情" width="920px" :loading="detailLoading">
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item label="推广员">{{ formatDetailUserDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="用户">{{ formatDetailUserDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="等级">{{ detailData.levelCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="detailData.status === 'ENABLE' ? 'success' : 'info'">
          {{ formatEnableStatus(detailData.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="邀请码">{{ detailData.inviteCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="邀请链接">{{ detailData.inviteUrl || '-' }}</el-descriptions-item>
      <el-descriptions-item label="绑定人数">{{ detailData.bindUserCount ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="转化人数">{{ detailData.convertCount ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="累计佣金">{{ detailData.totalCommissionAmount ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="可提现佣金">{{ detailData.availableCommissionAmount ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDate(detailData.createTime) }}</el-descriptions-item>
      <el-descriptions-item label="更新时间">{{ formatDate(detailData.updateTime) }}</el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">用户摘要</el-divider>
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
      <el-descriptions-item label="账号状态">{{ formatEnableStatus(detailData.user?.status) }}</el-descriptions-item>
      <el-descriptions-item label="最后登录">{{ formatDate(detailData.user?.lastLoginTime) }}</el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">推广摘要</el-divider>
    <el-descriptions :column="4" border>
      <el-descriptions-item label="关系数">{{ detailData.summary?.relationCount ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="已转化关系">{{ detailData.summary?.convertedRelationCount ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="待结算佣金单">{{ detailData.summary?.pendingCommissionCount ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="已结算佣金单">{{ detailData.summary?.settledCommissionCount ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="已失效佣金单">{{ detailData.summary?.invalidCommissionCount ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="待结算佣金额">
        {{ detailData.summary?.pendingCommissionAmount ?? 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="已结算佣金额">
        {{ detailData.summary?.settledCommissionAmount ?? 0 }}
      </el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">最近邀请关系</el-divider>
    <el-table :data="detailData.recentRelations || []" size="small" border>
      <el-table-column label="邀请用户" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.userNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || formatIdFallback(row.userId) }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="绑定时间" prop="bindTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="首单号" prop="firstOrderNo" min-width="150" />
      <el-table-column label="转化状态" prop="convertStatus" width="120">
        <template #default="{ row }">
          {{ formatConvertStatus(row.convertStatus) }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
    </el-table>
    <el-divider content-position="left">最近佣金单</el-divider>
    <el-table :data="detailData.recentCommissionOrders || []" size="small" border>
      <el-table-column label="佣金单号" prop="commissionNo" min-width="160" />
      <el-table-column label="关联用户" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.userNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || formatIdFallback(row.userId) }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="来源订单号" prop="sourceOrderNo" min-width="150" />
      <el-table-column label="来源单元号" prop="sourceUnitNo" min-width="150" />
      <el-table-column label="佣金类型" prop="commissionType" width="120" />
      <el-table-column label="佣金金额" prop="commissionAmount" width="120" />
      <el-table-column label="状态" prop="status" width="100">
        <template #default="{ row }">
          {{ formatCommissionStatus(row.status) }}
        </template>
      </el-table-column>
      <el-table-column label="结算时间" prop="settleTime" :formatter="dateFormatter" width="180" />
    </el-table>
  </Dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { DICT_TYPE } from '@/utils/dict'
import { dateFormatter, formatDate } from '@/utils/formatTime'
import { PromoterApi, type PromoterDetail } from '@/api/linbang/promoter'
import { formatCommissionStatus, formatConvertStatus, formatEnableStatus } from '../utils/display'

defineOptions({ name: 'PromoterDetailDialog' })

const dialogVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref<PromoterDetail>({} as PromoterDetail)

const formatIdFallback = (userId?: number) => {
  return userId ? '用户信息缺失' : '-'
}

const formatDetailUserDisplay = () => {
  const summary = [detailData.value.user?.nickname, detailData.value.user?.mobile, detailData.value.user?.userNo]
    .filter(Boolean)
    .join(' / ')
  return summary || formatIdFallback(detailData.value.userId)
}

const open = async (id: number) => {
  dialogVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await PromoterApi.getPromoter(id)
    if (!detailData.value.recentRelations) {
      detailData.value.recentRelations = []
    }
    if (!detailData.value.recentCommissionOrders) {
      detailData.value.recentCommissionOrders = []
    }
  } finally {
    detailLoading.value = false
  }
}

defineExpose({ open })
</script>
