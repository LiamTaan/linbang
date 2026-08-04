<template>
  <ContentWrap>
    <el-form :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="订单号">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单号"
          class="!w-220px"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="单元号">
        <el-input
          v-model="queryParams.unitNo"
          placeholder="请输入单元号"
          class="!w-220px"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="下单人">
        <el-input
          v-model="queryParams.userKeyword"
          placeholder="用户编号/昵称/手机号"
          class="!w-220px"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="批次状态">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择批次状态"
          class="!w-220px"
          clearable
        >
          <el-option
            v-for="item in BATCH_STATUS_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="订单信息" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.orderNo || formatIdFallback(row.orderId) }}</div>
            <div class="text-[var(--el-text-color-secondary)]">
              <dict-tag
                v-if="row.orderStatus"
                :type="DICT_TYPE.LB_ORDER_STATUS"
                :value="row.orderStatus"
              />
              <span v-else>-</span>
            </div>
            <div class="text-[var(--el-text-color-secondary)]"
              >订单ID：{{ row.orderId ?? '-' }}</div
            >
          </div>
        </template>
      </el-table-column>
      <el-table-column label="下单人" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.userNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{
              row.userNo || formatIdFallback(row.userId)
            }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="推送单元" min-width="260">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.unitNo || formatIdFallback(row.unitId) }}</div>
            <div>{{ row.unitTitle || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">
              第 {{ row.unitSeq ?? '-' }} 单元
              <span class="mx-4px">/</span>
              <dict-tag
                v-if="row.unitStatus"
                :type="DICT_TYPE.LB_ORDER_UNIT_STATUS"
                :value="row.unitStatus"
              />
              <span v-else>-</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="已推送对象" min-width="260">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">已推送 {{ row.pushedMerchantCount ?? 0 }} 家</div>
            <div class="text-[var(--el-text-color-secondary)]">
              {{ row.pushedMerchantNames || '当前批次暂无推送明细' }}
            </div>
            <div class="text-[var(--el-text-color-secondary)]"
              >已接单 {{ row.acceptedMatchCount ?? 0 }} 家</div
            >
          </div>
        </template>
      </el-table-column>
      <el-table-column label="当前承接服务商" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.acceptedMerchantName || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{
              row.acceptedMerchantContactName || '-'
            }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{
              row.acceptedMerchantContactMobile || '-'
            }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="批次信息" min-width="160">
        <template #default="{ row }">
          <div class="leading-20px">
            <div>阶段 {{ row.stageNo ?? '-' }}</div>
            <div>批次 {{ row.pushBatchNo ?? '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">
              半径 {{ formatRadiusRange(row.radiusStartKm, row.radiusEndKm) }}
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="计划时间" prop="plannedAt" :formatter="dateFormatter" width="180" />
      <el-table-column label="过期时间" prop="expiredAt" :formatter="dateFormatter" width="180" />
      <el-table-column label="批次状态" width="120">
        <template #default="{ row }">
          <el-tag :type="getBatchStatusTagType(row.status)">{{
            formatBatchStatus(row.status)
          }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="触发类型" min-width="140">
        <template #default="{ row }">{{ formatTriggerType(row.triggerType) }}</template>
      </el-table-column>
    </el-table>
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { dateFormatter } from '@/utils/formatTime'
import { DICT_TYPE } from '@/utils/dict'
import { MatchPushBatchApi, type MatchPushBatch } from '@/api/linbang/matchpushbatch'

defineOptions({ name: 'LinbangMatchPushBatch' })

const BATCH_STATUS_OPTIONS = [
  { label: '推送中', value: 'PUSHING' },
  { label: '已过期', value: 'EXPIRED' }
]

const BATCH_STATUS_LABEL_MAP: Record<string, string> = {
  PUSHING: '推送中',
  EXPIRED: '已过期'
}

const TRIGGER_TYPE_LABEL_MAP: Record<string, string> = {
  ORDER_PAID: '订单支付触发',
  SCHEDULE: '阶段续推触发'
}

const loading = ref(false)
const list = ref<MatchPushBatch[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  orderNo: undefined as string | undefined,
  unitNo: undefined as string | undefined,
  userKeyword: undefined as string | undefined,
  status: undefined as string | undefined
})

const getList = async () => {
  loading.value = true
  try {
    const data = await MatchPushBatchApi.getMatchPushBatchPage(queryParams)
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
  queryParams.pageNo = 1
  queryParams.orderNo = undefined
  queryParams.unitNo = undefined
  queryParams.userKeyword = undefined
  queryParams.status = undefined
  getList()
}

const formatRadiusRange = (start?: number, end?: number) => {
  if (start == null && end == null) {
    return '-'
  }
  return `${start ?? '-'} - ${end ?? '-'} km`
}

const formatBatchStatus = (status?: string) => {
  return (status && BATCH_STATUS_LABEL_MAP[status]) || status || '-'
}

const getBatchStatusTagType = (status?: string) => {
  if (status === 'PUSHING') {
    return 'primary'
  }
  if (status === 'EXPIRED') {
    return 'info'
  }
  return undefined
}

const formatTriggerType = (triggerType?: string) => {
  return (triggerType && TRIGGER_TYPE_LABEL_MAP[triggerType]) || triggerType || '-'
}

const formatIdFallback = (id?: number) => {
  return id != null ? `ID:${id}` : '-'
}

onMounted(() => getList())
</script>
