<template>
  <Dialog v-model="dialogVisible" title="佣金详情" width="920px" :loading="detailLoading">
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item label="佣金单号">{{ detailData.commissionNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="推广员">{{ formatPromoterDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="关联用户">{{ formatUserDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="佣金类型">{{ detailData.commissionType || '-' }}</el-descriptions-item>
      <el-descriptions-item label="佣金金额">{{ detailData.commissionAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="getCommissionStatusTagType(detailData.status)">
          {{ formatCommissionStatus(detailData.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="结算时间">{{ formatDate(detailData.settleTime) }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDate(detailData.createTime) }}</el-descriptions-item>
      <el-descriptions-item label="更新时间">{{ formatDate(detailData.updateTime) }}</el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">推广员摘要</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="推广员编号">{{ detailData.promoter?.userNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="推广员手机号">{{ detailData.promoter?.userMobile || '-' }}</el-descriptions-item>
      <el-descriptions-item label="推广员昵称">{{ detailData.promoter?.userNickname || '-' }}</el-descriptions-item>
      <el-descriptions-item label="推广员等级">{{ detailData.promoter?.levelCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="邀请码">{{ detailData.promoter?.inviteCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="推广员状态">
        {{ formatEnableStatus(detailData.promoter?.status) }}
      </el-descriptions-item>
      <el-descriptions-item label="累计佣金">
        {{ detailData.promoter?.totalCommissionAmount ?? '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="可提现佣金">
        {{ detailData.promoter?.availableCommissionAmount ?? '-' }}
      </el-descriptions-item>
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
      <el-descriptions-item label="用户状态">
        {{ formatEnableStatus(detailData.user?.status) }}
      </el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">来源订单</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="订单号">{{ detailData.sourceOrder?.orderNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="订单标题">{{ detailData.sourceOrder?.title || '-' }}</el-descriptions-item>
      <el-descriptions-item label="订单金额">{{ detailData.sourceOrder?.orderAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="计价方式">
        <dict-tag
          v-if="detailData.sourceOrder?.pricingMode"
          :type="DICT_TYPE.LB_PRICING_MODE"
          :value="detailData.sourceOrder.pricingMode"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="拆单状态">
        {{ formatSplitStatus(detailData.sourceOrder?.splitStatus) }}
      </el-descriptions-item>
      <el-descriptions-item label="订单状态">
        <dict-tag
          v-if="detailData.sourceOrder?.status"
          :type="DICT_TYPE.LB_ORDER_STATUS"
          :value="detailData.sourceOrder.status"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ formatDate(detailData.sourceOrder?.createTime) }}
      </el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">来源单元</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="单元订单号">{{ detailData.sourceUnit?.unitNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="单元序号">{{ detailData.sourceUnit?.unitSeq ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="单元标题">{{ detailData.sourceUnit?.unitTitle || '-' }}</el-descriptions-item>
      <el-descriptions-item label="单元金额">{{ detailData.sourceUnit?.unitAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="拆分模式">{{ formatSplitMode(detailData.sourceUnit?.splitMode) }}</el-descriptions-item>
      <el-descriptions-item label="是否锁定">
        {{ formatBooleanYesNo(detailData.sourceUnit?.isLocked) }}
      </el-descriptions-item>
      <el-descriptions-item label="锁定原因">{{ detailData.sourceUnit?.lockReason || '-' }}</el-descriptions-item>
      <el-descriptions-item label="单元状态">
        <dict-tag
          v-if="detailData.sourceUnit?.status"
          :type="DICT_TYPE.LB_ORDER_UNIT_STATUS"
          :value="detailData.sourceUnit.status"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="完成时间">{{ formatDate(detailData.sourceUnit?.finishTime) }}</el-descriptions-item>
    </el-descriptions>
  </Dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { DICT_TYPE } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import { CommissionOrderApi, type CommissionOrderDetail } from '@/api/linbang/commissionorder'
import {
  formatBooleanYesNo,
  formatCommissionStatus,
  formatEnableStatus,
  getCommissionStatusTagType,
  formatSplitMode,
  formatSplitStatus
} from '../utils/display'

defineOptions({ name: 'CommissionOrderDetailDialog' })

const dialogVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref<CommissionOrderDetail>({} as CommissionOrderDetail)

const formatIdFallback = (label: string, value?: number) => {
  return value ? `${label}信息缺失` : '-'
}

const formatPromoterDisplay = () => {
  const summary = [detailData.value.promoter?.userNickname, detailData.value.promoter?.userMobile, detailData.value.promoter?.userNo]
    .filter(Boolean)
    .join(' / ')
  return summary || formatIdFallback('推广员', detailData.value.promoterId)
}

const formatUserDisplay = () => {
  const summary = [detailData.value.user?.nickname, detailData.value.user?.mobile, detailData.value.user?.userNo]
    .filter(Boolean)
    .join(' / ')
  return summary || formatIdFallback('用户', detailData.value.userId)
}

const open = async (id: number) => {
  dialogVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await CommissionOrderApi.getCommissionOrder(id)
  } finally {
    detailLoading.value = false
  }
}

defineExpose({ open })
</script>
