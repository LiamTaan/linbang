<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="业务类型" prop="bizType">
        <el-input
          v-model="queryParams.bizType"
          placeholder="请输入业务类型"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="业务标识" prop="bizKeyword">
        <el-input
          v-model="queryParams.bizKeyword"
          placeholder="请输入订单号/单元号/异常单号/投诉单号等"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="风险类型" prop="riskType">
        <el-input
          v-model="queryParams.riskType"
          placeholder="请输入风险类型"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="风险等级" prop="riskLevel">
        <el-input
          v-model="queryParams.riskLevel"
          placeholder="请输入风险等级"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="规则编码" prop="hitRuleCode">
        <el-input
          v-model="queryParams.hitRuleCode"
          placeholder="请输入规则编码"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-220px">
          <el-option
            v-for="item in RISK_EVENT_STATUS_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-260px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" /> 搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" /> 重置
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="业务类型" align="center" prop="bizType" width="120">
        <template #default="{ row }">
          {{ formatBizType(row.bizType) }}
        </template>
      </el-table-column>
      <el-table-column label="业务对象" align="center" prop="bizDisplay" min-width="180" />
      <el-table-column label="风险类型" align="center" prop="riskType" width="120" />
      <el-table-column label="风险等级" align="center" prop="riskLevel" width="120">
        <template #default="{ row }">
          {{ formatRiskLevel(row.riskLevel) }}
        </template>
      </el-table-column>
      <el-table-column label="命中规则" align="center" prop="hitRuleCode" min-width="150" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'HANDLED' ? 'success' : row.status === 'IGNORED' ? 'info' : 'warning'">
            {{ formatRiskEventStatus(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="处理人" align="center" prop="handleBy" width="100" />
      <el-table-column label="处理时间" align="center" prop="handleTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="备注" align="center" prop="remark" min-width="200" />
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" align="center" fixed="right" width="100">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row.id)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>

  <Dialog v-model="detailVisible" title="风险事件详情" width="920px">
    <el-descriptions v-loading="detailLoading" :column="2" border>
      <el-descriptions-item label="业务类型">{{ formatBizType(detailData?.bizType) }}</el-descriptions-item>
      <el-descriptions-item label="业务对象">{{ formatBizDisplay(detailData) }}</el-descriptions-item>
      <el-descriptions-item label="风险类型">{{ detailData?.riskType || '-' }}</el-descriptions-item>
      <el-descriptions-item label="风险等级">{{ formatRiskLevel(detailData?.riskLevel) }}</el-descriptions-item>
      <el-descriptions-item label="命中规则编码">{{ detailData?.hitRuleCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="处理状态">{{ formatRiskEventStatus(detailData?.status) }}</el-descriptions-item>
      <el-descriptions-item label="处理人">{{ detailData?.handleBy || '-' }}</el-descriptions-item>
      <el-descriptions-item label="处理时间">{{ formatDate(detailData?.handleTime) }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDate(detailData?.createTime) }}</el-descriptions-item>
      <el-descriptions-item label="备注" :span="2">{{ detailData?.remark || '-' }}</el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">命中规则</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="规则">{{ formatRiskRuleDisplay(detailData?.hitRule) }}</el-descriptions-item>
      <el-descriptions-item label="规则编码">{{ detailData?.hitRule?.ruleCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="规则名称">{{ detailData?.hitRule?.ruleName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="规则分组">{{ detailData?.hitRule?.ruleGroup || '-' }}</el-descriptions-item>
      <el-descriptions-item label="取值类型">{{ detailData?.hitRule?.valueType || '-' }}</el-descriptions-item>
      <el-descriptions-item label="规则状态">{{ formatEnableStatus(detailData?.hitRule?.status) }}</el-descriptions-item>
      <el-descriptions-item label="规则值" :span="2">{{ detailData?.hitRule?.ruleValue || '-' }}</el-descriptions-item>
      <el-descriptions-item label="规则备注" :span="2">{{ detailData?.hitRule?.remark || '-' }}</el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">订单上下文</el-divider>
    <el-row :gutter="12" class="mb-16px">
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">主订单</div>
          <div class="mt-8px text-16px font-600">{{ detailData?.order?.orderNo || '-' }}</div>
          <div class="mt-6px text-13px">{{ detailData?.order?.title || '-' }}</div>
          <div class="mt-6px text-[var(--el-text-color-secondary)]">
            状态：{{ formatOrderStatus(detailData?.order?.status) }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">拆分单元</div>
          <div class="mt-8px text-16px font-600">{{ detailData?.unit?.unitNo || '-' }}</div>
          <div class="mt-6px text-13px">{{ detailData?.unit?.unitTitle || '-' }}</div>
          <div class="mt-6px text-[var(--el-text-color-secondary)]">
            状态：{{ formatOrderUnitStatus(detailData?.unit?.status) }}
            <span v-if="detailData?.unit"> / 锁定：{{ formatBooleanYesNo(detailData.unit.isLocked) }}</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">异常单</div>
          <div class="mt-8px text-16px font-600">{{ detailData?.abnormal?.abnormalNo || '-' }}</div>
          <div class="mt-6px text-13px">{{ detailData?.abnormal?.abnormalType || '-' }}</div>
          <div class="mt-6px text-[var(--el-text-color-secondary)]">
            处理状态：{{ formatHandleStatus(detailData?.abnormal?.handleStatus) }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-descriptions :column="2" border>
      <el-descriptions-item label="主订单号">{{ detailData?.order?.orderNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="主订单状态">{{ formatOrderStatus(detailData?.order?.status) }}</el-descriptions-item>
      <el-descriptions-item label="下单用户">{{ formatOrderUserDisplay(detailData?.order) }}</el-descriptions-item>
      <el-descriptions-item label="服务商">{{ formatMerchantDisplay(detailData?.order, detailData?.unit) }}</el-descriptions-item>
      <el-descriptions-item label="单元号">{{ detailData?.unit?.unitNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="单元序号">{{ detailData?.unit?.unitSeq || '-' }}</el-descriptions-item>
      <el-descriptions-item label="锁单原因" :span="2">{{ detailData?.unit?.lockReason || '-' }}</el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">关联售后与提现</el-divider>
    <el-row :gutter="12" class="mb-16px">
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">投诉单</div>
          <div class="mt-8px text-16px font-600">{{ detailData?.complaint?.complaintNo || '-' }}</div>
          <div class="mt-6px text-[var(--el-text-color-secondary)]">
            状态：{{ formatComplaintStatus(detailData?.complaint?.status) }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">申诉单</div>
          <div class="mt-8px text-16px font-600">{{ detailData?.appeal?.appealNo || '-' }}</div>
          <div class="mt-6px text-[var(--el-text-color-secondary)]">
            审核：
            <dict-tag
              v-if="detailData?.appeal?.auditStatus"
              :type="DICT_TYPE.LB_AUDIT_STATUS"
              :value="detailData.appeal.auditStatus"
            />
            <span v-else>-</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">提现单</div>
          <div class="mt-8px text-16px font-600">{{ detailData?.withdraw?.withdrawNo || '-' }}</div>
          <div class="mt-6px text-[var(--el-text-color-secondary)]">
            审核：
            <dict-tag
              v-if="detailData?.withdraw?.auditStatus"
              :type="DICT_TYPE.LB_AUDIT_STATUS"
              :value="detailData.withdraw.auditStatus"
            />
            <span v-else>-</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-descriptions :column="2" border>
      <el-descriptions-item label="投诉类型">
        {{ formatComplaintType(detailData?.complaint?.complaintType) }}
      </el-descriptions-item>
      <el-descriptions-item label="投诉处理时间">
        {{ formatDate(detailData?.complaint?.handleTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="申诉类型">{{ formatAppealType(detailData?.appeal?.appealType) }}</el-descriptions-item>
      <el-descriptions-item label="申诉审核时间">
        {{ formatDate(detailData?.appeal?.auditTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="提现申请金额">
        {{ detailData?.withdraw?.applyAmount ?? '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="提现实付金额">
        {{ detailData?.withdraw?.realAmount ?? '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="投诉处理结果" :span="2">
        {{ detailData?.complaint?.resultDesc || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="申诉审核备注" :span="2">
        {{ detailData?.appeal?.auditRemark || detailData?.appeal?.rejectReason || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="提现审核备注" :span="2">
        {{ detailData?.withdraw?.auditRemark || detailData?.withdraw?.rejectReason || '-' }}
      </el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">订单操作日志</el-divider>
    <el-table
      v-if="detailData?.orderOperateLogs?.length"
      :data="detailData.orderOperateLogs"
      size="small"
      border
      max-height="280"
    >
      <el-table-column label="ID" prop="id" width="90" />
      <el-table-column label="操作类型" prop="operateType" width="140">
        <template #default="{ row }">{{ formatOperateType(row.operateType) }}</template>
      </el-table-column>
      <el-table-column label="操作角色" prop="operateRole" width="120">
        <template #default="{ row }">{{ formatOperateRole(row.operateRole) }}</template>
      </el-table-column>
      <el-table-column label="操作人" prop="operateBy" width="100" />
      <el-table-column label="变更前状态" prop="beforeStatus" width="120">
        <template #default="{ row }">
          {{ formatOperateStatus(row.beforeStatus) }}
        </template>
      </el-table-column>
      <el-table-column label="变更后状态" prop="afterStatus" width="120">
        <template #default="{ row }">
          {{ formatOperateStatus(row.afterStatus) }}
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="180" />
      <el-table-column label="操作时间" prop="operateTime" width="180">
        <template #default="{ row }">
          {{ formatDate(row.operateTime) }}
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无操作日志" :image-size="80" />
  </Dialog>
</template>

<script setup lang="ts">
import type { FormInstance } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { DICT_TYPE } from '@/utils/dict'
import { dateFormatter, formatDate } from '@/utils/formatTime'
import { RiskEventApi, type RiskEvent, type RiskEventDetail } from '@/api/linbang/riskevent'
import {
  formatAppealType,
  formatBizType,
  formatBooleanYesNo,
  formatComplaintType,
  formatEnableStatus,
  formatHandleStatus,
  formatOperateRole,
  formatOperateStatus,
  formatOperateType,
  formatOrderStatus,
  formatOrderUnitStatus,
  formatRiskEventStatus,
  formatRiskLevel,
  RISK_EVENT_STATUS_OPTIONS
} from '../utils/display'

defineOptions({ name: 'RiskEvent' })

const loading = ref(false)
const detailVisible = ref(false)
const detailLoading = ref(false)
const list = ref<RiskEvent[]>([])
const detailData = ref<RiskEventDetail>()
const total = ref(0)
const queryFormRef = ref<FormInstance>()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  bizType: undefined as string | undefined,
  bizKeyword: undefined as string | undefined,
  riskType: undefined as string | undefined,
  riskLevel: undefined as string | undefined,
  hitRuleCode: undefined as string | undefined,
  status: undefined as string | undefined,
  createTime: [] as string[]
})

const formatComplaintStatus = (value?: string) => {
  if (value === 'PENDING') {
    return '待处理'
  }
  if (value === 'PROCESSING') {
    return '处理中'
  }
  if (value === 'FINISHED') {
    return '已完结'
  }
  if (value === 'REJECTED') {
    return '已驳回'
  }
  return value || '-'
}

const formatOrderUserDisplay = (order?: RiskEventDetail['order']) => {
  const summary = [order?.userNickname, order?.userMobile, order?.userNo].filter(Boolean).join(' / ')
  return summary || (order?.userId ? '用户信息缺失' : '-')
}

const formatRiskRuleDisplay = (rule?: RiskEventDetail['hitRule']) => {
  const summary = [rule?.ruleName, rule?.ruleCode].filter(Boolean).join(' / ')
  return summary || (rule?.id ? '规则信息缺失' : '-')
}

const formatMerchantDisplay = (order?: RiskEventDetail['order'], unit?: RiskEventDetail['unit']) => {
  const merchantSummary = [
    order?.merchantName || unit?.merchantName,
    order?.merchantContactMobile || unit?.merchantContactMobile,
    order?.merchantContactName || unit?.merchantContactName
  ]
    .filter(Boolean)
    .join(' / ')
  return merchantSummary || (order?.merchantId || unit?.merchantId ? '服务商信息缺失' : '-')
}

const formatBizDisplay = (record?: RiskEvent | RiskEventDetail) => {
  if (!record) {
    return '-'
  }
  if (record.bizDisplay) {
    return record.bizDisplay
  }
  if (record.bizType === 'ORDER' || record.bizType === 'ORDER_INFO') {
    return record.bizId ? detailData.value?.order?.orderNo || '订单信息缺失' : '-'
  }
  if (record.bizType === 'ORDER_UNIT' || record.bizType === 'UNIT') {
    return record.bizId ? detailData.value?.unit?.unitNo || '单元信息缺失' : '-'
  }
  if (record.bizType === 'ORDER_ABNORMAL' || record.bizType === 'ABNORMAL') {
    return record.bizId ? detailData.value?.abnormal?.abnormalNo || '异常单信息缺失' : '-'
  }
  if (record.bizType === 'COMPLAINT') {
    return record.bizId ? detailData.value?.complaint?.complaintNo || '投诉单信息缺失' : '-'
  }
  if (record.bizType === 'APPEAL') {
    return record.bizId ? detailData.value?.appeal?.appealNo || '申诉单信息缺失' : '-'
  }
  if (record.bizType === 'WITHDRAW' || record.bizType === 'WITHDRAW_APPLY') {
    return record.bizId ? detailData.value?.withdraw?.withdrawNo || '提现单信息缺失' : '-'
  }
  if (record.bizType === 'USER') {
    const summary = [detailData.value?.order?.userNickname, detailData.value?.order?.userMobile, detailData.value?.order?.userNo]
      .filter(Boolean)
      .join(' / ')
    return summary || (record.bizId ? '用户信息缺失' : '-')
  }
  return record.bizId ? '业务信息缺失' : '-'
}

const getList = async () => {
  loading.value = true
  try {
    const data = await RiskEventApi.getRiskEventPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const resetQuery = () => {
  queryFormRef.value?.resetFields()
  handleQuery()
}

const openDetail = async (id: number) => {
  detailVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await RiskEventApi.getRiskEvent(id)
  } finally {
    detailLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
