<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="订单号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="单元号" prop="unitNo">
        <el-input
          v-model="queryParams.unitNo"
          placeholder="请输入单元号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="命中规则编码" prop="matchRuleCode">
        <el-input
          v-model="queryParams.matchRuleCode"
          placeholder="请输入命中规则编码"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="匹配分值" prop="matchScore">
        <el-input
          v-model="queryParams.matchScore"
          placeholder="请输入匹配分值"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="距离公里" prop="distanceKm">
        <el-input
          v-model="queryParams.distanceKm"
          placeholder="请输入距离公里"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="推送时间" prop="pushTime">
        <el-date-picker
          v-model="queryParams.pushTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item label="接单截止时间" prop="acceptDeadlineTime">
        <el-date-picker
          v-model="queryParams.acceptDeadlineTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择状态"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="item in MATCH_STATUS_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['linbang:order:match-record:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['linbang:order:match-record:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
        <el-button
            type="danger"
            plain
            :disabled="isEmpty(checkedIds)"
            @click="handleDeleteBatch"
            v-hasPermi="['linbang:order:match-record:delete']"
        >
          <Icon icon="ep:delete" class="mr-5px" /> 批量删除
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table
        row-key="id"
        v-loading="loading"
        :data="list"
        :stripe="true"
        :show-overflow-tooltip="true"
        @selection-change="handleRowCheckboxChange"
    >
    <el-table-column type="selection" width="55" />
      <el-table-column label="订单号" align="center" prop="orderNo" min-width="180" />
      <el-table-column label="单元号" align="center" prop="unitNo" min-width="160" />
      <el-table-column label="匹配服务商" align="center" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.merchantName || (row.merchantId ? '服务商信息缺失' : '-') }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.merchantContactName || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.merchantContactMobile || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="命中规则编码" align="center" prop="matchRuleCode" />
      <el-table-column label="匹配分值" align="center" prop="matchScore" />
      <el-table-column label="距离公里" align="center" prop="distanceKm" />
      <el-table-column label="阶段号" align="center" prop="stageNo" width="90" />
      <el-table-column label="批次号" align="center" prop="pushBatchNo" width="90" />
      <el-table-column label="优先层" align="center" prop="priorityLayer" width="140" />
      <el-table-column label="优先池" align="center" width="90">
        <template #default="{ row }">{{ row.priorityPoolFlag ? '是' : '否' }}</template>
      </el-table-column>
      <el-table-column label="品类命中" align="center" prop="categoryMatchLevel" width="110" />
      <el-table-column
        label="推送时间"
        align="center"
        prop="pushTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column
        label="接单截止时间"
        align="center"
        prop="acceptDeadlineTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          {{ formatMatchStatus(scope.row.status) }}
        </template>
      </el-table-column>
      <el-table-column label="最终结果" align="center" prop="finalResult" width="120" />
      <el-table-column label="操作" align="center" min-width="120px">
        <template #default="scope">
          <el-button link type="primary" @click="openDetail(scope.row.id)">详情</el-button>
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['linbang:order:match-record:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['linbang:order:match-record:delete']"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <OrderMatchRecordForm ref="formRef" @success="getList" />

  <Dialog v-model="detailVisible" title="订单匹配记录详情" width="920px">
    <el-descriptions v-loading="detailLoading" :column="2" border>
      <el-descriptions-item label="命中规则编码">{{ detailData?.matchRuleCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="订单号">{{ detailData?.order?.orderNo || detailData?.orderNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="单元号">{{ detailData?.unit?.unitNo || detailData?.unitNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="匹配服务商">{{ formatMerchantDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ formatMatchStatus(detailData?.status) }}</el-descriptions-item>
      <el-descriptions-item label="匹配分值">{{ detailData?.matchScore ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="距离公里">{{ detailData?.distanceKm ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="推送时间">{{ formatDate(detailData?.pushTime) }}</el-descriptions-item>
      <el-descriptions-item label="接单截止时间">{{ formatDate(detailData?.acceptDeadlineTime) }}</el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">订单与单元上下文</el-divider>
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
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">匹配服务商</div>
          <div class="mt-8px text-16px font-600">{{ detailData?.merchant?.merchantName || '-' }}</div>
          <div class="mt-6px text-13px">{{ detailData?.merchant?.contactName || '-' }}</div>
          <div class="mt-6px text-[var(--el-text-color-secondary)]">
            状态：{{ formatEnableStatus(detailData?.merchant?.status) }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-descriptions :column="2" border>
      <el-descriptions-item label="订单金额">{{ detailData?.order?.orderAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="下单用户">{{ formatOrderUserDisplay(detailData?.order) }}</el-descriptions-item>
      <el-descriptions-item label="单元序号">{{ detailData?.unit?.unitSeq || '-' }}</el-descriptions-item>
      <el-descriptions-item label="单元金额">{{ detailData?.unit?.unitAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="锁单状态">
        <span v-if="detailData?.unit">{{ detailData.unit.isLocked ? '已锁定' : '未锁定' }}</span>
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="锁单原因">{{ detailData?.unit?.lockReason || '-' }}</el-descriptions-item>
      <el-descriptions-item label="商家联系人">{{ detailData?.merchant?.contactMobile || '-' }}</el-descriptions-item>
      <el-descriptions-item label="信用分 / 等级">
        {{ detailData?.merchant?.creditScore ?? '-' }} / {{ detailData?.merchant?.creditLevel || '-' }}
      </el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">命中规则与统计</el-divider>
    <el-row :gutter="12" class="mb-16px">
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">抢单记录数</div>
          <div class="mt-8px text-24px font-600">{{ detailData?.summary?.acceptRecordCount ?? 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">已接受 / 未接受</div>
          <div class="mt-8px text-18px font-600">
            {{ detailData?.summary?.acceptedCount ?? 0 }} / {{ detailData?.summary?.rejectedCount ?? 0 }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">最近截止 / 最近距离</div>
          <div class="mt-8px text-18px font-600">
            {{ detailData?.summary?.deadlineExpired ? '已过期' : '未过期' }} /
            {{ detailData?.summary?.closestDistanceKm ?? '-' }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-descriptions :column="2" border>
      <el-descriptions-item label="规则名称">{{ detailData?.rule?.ruleName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="规则分组">{{ detailData?.rule?.ruleGroup || '-' }}</el-descriptions-item>
      <el-descriptions-item label="取值类型">{{ detailData?.rule?.valueType || '-' }}</el-descriptions-item>
      <el-descriptions-item label="规则状态">{{ formatEnableStatus(detailData?.rule?.status) }}</el-descriptions-item>
      <el-descriptions-item label="规则值" :span="2">{{ detailData?.rule?.ruleValue || '-' }}</el-descriptions-item>
      <el-descriptions-item label="规则备注" :span="2">{{ detailData?.rule?.remark || '-' }}</el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">关联抢单记录</el-divider>
    <el-table
      v-if="detailData?.acceptRecords?.length"
      :data="detailData.acceptRecords"
      size="small"
      border
      max-height="260"
    >
      <el-table-column label="服务商" min-width="200">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.merchantName || (row.merchantId ? '服务商信息缺失' : '-') }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.merchantContactName || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.merchantContactMobile || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="接单时间" prop="acceptTime" width="180">
        <template #default="{ row }">
          {{ formatDate(row.acceptTime) }}
        </template>
      </el-table-column>
      <el-table-column label="距离公里" prop="distanceKm" width="100" />
      <el-table-column label="接单结果" prop="acceptResult" width="120">
        <template #default="{ row }">{{ formatAcceptResult(row.acceptResult) }}</template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="180" />
    </el-table>
    <el-empty v-else description="暂无关联抢单记录" :image-size="80" />
  </Dialog>
</template>

<script setup lang="ts">
import { isEmpty } from '@/utils/is'
import { dateFormatter, formatDate } from '@/utils/formatTime'
import download from '@/utils/download'
import { OrderMatchRecordApi, OrderMatchRecord, OrderMatchRecordDetail } from '@/api/linbang/ordermatchrecord'
import {
  formatAcceptResult,
  formatEnableStatus,
  formatMatchStatus,
  formatOrderStatus,
  formatOrderUnitStatus,
  MATCH_STATUS_OPTIONS
} from '../utils/display'
import OrderMatchRecordForm from './OrderMatchRecordForm.vue'

import { onMounted, reactive, ref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
/** 订单匹配记录 列表 */
defineOptions({ name: 'OrderMatchRecord' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const detailVisible = ref(false)
const detailLoading = ref(false)
const list = ref<OrderMatchRecord[]>([]) // 列表的数据
const detailData = ref<OrderMatchRecordDetail>()
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  orderNo: undefined,
  unitNo: undefined,
  merchantId: undefined,
  matchRuleCode: undefined,
  matchScore: undefined,
  distanceKm: undefined,
  pushTime: [],
  acceptDeadlineTime: [],
  status: undefined
})

const formatOrderUserDisplay = (order?: OrderMatchRecordDetail['order']) => {
  const summary = [order?.userNickname, order?.userMobile, order?.userNo].filter(Boolean).join(' / ')
  return summary || (order?.userId ? '用户信息缺失' : '-')
}

const formatMerchantDisplay = () => {
  const summary = [
    detailData.value?.merchant?.merchantName,
    detailData.value?.merchant?.contactName,
    detailData.value?.merchant?.contactMobile
  ]
    .filter(Boolean)
    .join(' / ')
  return summary || (detailData.value?.merchantId ? '服务商信息缺失' : '-')
}

const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await OrderMatchRecordApi.getOrderMatchRecordPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const openDetail = async (id: number) => {
  detailVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await OrderMatchRecordApi.getOrderMatchRecord(id)
  } finally {
    detailLoading.value = false
  }
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await OrderMatchRecordApi.deleteOrderMatchRecord(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 批量删除订单匹配记录 */
const handleDeleteBatch = async () => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    await OrderMatchRecordApi.deleteOrderMatchRecordList(checkedIds.value);
    checkedIds.value = [];
    message.success(t('common.delSuccess'))
    await getList();
  } catch {}
}

const checkedIds = ref<number[]>([])
const handleRowCheckboxChange = (records: OrderMatchRecord[]) => {
  checkedIds.value = records.map((item) => item.id!);
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await OrderMatchRecordApi.exportOrderMatchRecord(queryParams)
    download.excel(data, '订单匹配记录.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
