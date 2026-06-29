<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="订单号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单号"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="下单用户" prop="userKeyword">
        <el-input
          v-model="queryParams.userKeyword"
          placeholder="请输入用户编号 / 昵称 / 手机号"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入订单标题"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="计价方式" prop="pricingMode">
        <el-select
          v-model="queryParams.pricingMode"
          placeholder="请选择计价方式"
          clearable
          class="!w-220px"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_PRICING_MODE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="订单状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择订单状态" clearable class="!w-220px">
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_ORDER_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
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
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          v-hasPermi="['linbang:order:info:create']"
          @click="openForm('create')"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          :loading="exportLoading"
          v-hasPermi="['linbang:order:info:export']"
          @click="handleExport"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="checkedIds.length === 0"
          v-hasPermi="['linbang:order:info:delete']"
          @click="handleDeleteBatch"
        >
          <Icon icon="ep:delete" class="mr-5px" /> 批量删除
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table
      v-loading="loading"
      :data="list"
      :stripe="true"
      :show-overflow-tooltip="true"
      row-key="id"
      @selection-change="handleRowCheckboxChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column label="订单号" align="center" prop="orderNo" min-width="180" />
      <el-table-column label="下单用户" align="center" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.userNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || formatIdFallback(row.userId) }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="服务商" align="center" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.merchantName || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.merchantContactName || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.merchantContactMobile || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="类目" align="center" prop="categoryName" min-width="140" />
      <el-table-column label="订单标题" align="center" prop="title" min-width="180" />
      <el-table-column label="计价方式" align="center" prop="pricingMode" width="120">
        <template #default="{ row }">
          <dict-tag v-if="row.pricingMode" :type="DICT_TYPE.LB_PRICING_MODE" :value="row.pricingMode" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="订单金额" align="center" prop="orderAmount" width="110" />
      <el-table-column label="拆单状态" align="center" prop="splitStatus" width="120">
        <template #default="{ row }">
          {{ formatSplitStatus(row.splitStatus) }}
        </template>
      </el-table-column>
      <el-table-column label="订单状态" align="center" prop="status" width="120">
        <template #default="{ row }">
          <dict-tag v-if="row.status" :type="DICT_TYPE.LB_ORDER_STATUS" :value="row.status" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" align="center" fixed="right" width="190">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row.id)">详情</el-button>
          <el-button
            link
            type="primary"
            v-hasPermi="['linbang:order:info:update']"
            @click="openForm('update', row.id)"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            v-hasPermi="['linbang:order:info:delete']"
            @click="handleDelete(row.id)"
          >
            删除
          </el-button>
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

  <Dialog v-model="detailVisible" title="订单详情" width="980px">
    <el-descriptions v-loading="detailLoading" :column="2" border>
      <el-descriptions-item label="订单号">{{ detailData?.orderNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="订单状态">
        <dict-tag v-if="detailData?.status" :type="DICT_TYPE.LB_ORDER_STATUS" :value="detailData.status" />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="下单用户">{{ formatDetailUserDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="服务商">{{ formatMerchantDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="类目">
        {{ detailData?.categoryName || (detailData?.categoryId ? '类目信息缺失' : '-') }}
      </el-descriptions-item>
      <el-descriptions-item label="计价方式">
        <dict-tag v-if="detailData?.pricingMode" :type="DICT_TYPE.LB_PRICING_MODE" :value="detailData.pricingMode" />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="预算金额">{{ detailData?.budgetAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="订单金额">{{ detailData?.orderAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="数量">{{ detailData?.quantity ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="工期描述">{{ detailData?.serviceDurationDesc || '-' }}</el-descriptions-item>
      <el-descriptions-item label="拆单状态">{{ formatSplitStatus(detailData?.splitStatus) }}</el-descriptions-item>
      <el-descriptions-item label="支付订单">
        {{ detailData?.payRecord?.merchantOrderId || (detailData?.payOrderId ? '支付订单信息缺失' : '-') }}
      </el-descriptions-item>
      <el-descriptions-item label="价格明细展示">
        {{ formatBooleanYesNo(detailData?.priceDetailEnabled) }}
      </el-descriptions-item>
      <el-descriptions-item label="是否开票">{{ formatBooleanYesNo(detailData?.needInvoice) }}</el-descriptions-item>
      <el-descriptions-item label="是否拆单">{{ formatBooleanYesNo(detailData?.needSplit) }}</el-descriptions-item>
      <el-descriptions-item label="协议确认">{{ detailData?.agreementConfirmed ? '已确认' : '未确认' }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDate(detailData?.createTime) }}</el-descriptions-item>
      <el-descriptions-item label="需求描述" :span="2">{{ detailData?.requireDesc || '-' }}</el-descriptions-item>
      <el-descriptions-item label="服务地址" :span="2">
        {{ [detailData?.province, detailData?.city, detailData?.district, detailData?.street, detailData?.detailAddress].filter(Boolean).join(' / ') || '-' }}
      </el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">商城关联与推广抵扣</el-divider>
    <el-row :gutter="12" class="mb-16px">
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">商城入口</div>
          <div class="mt-8px text-16px font-600">
            {{ detailData?.mallEntry?.enabled ? (detailData?.mallEntry?.title || '已开启') : '未开启' }}
          </div>
          <div class="mt-6px text-13px break-all">{{ detailData?.mallEntry?.url || '-' }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">商城消费关联</div>
          <div class="mt-8px text-16px font-600">{{ detailData?.mallConsumeRelation?.consumeRecordNo || '-' }}</div>
          <div class="mt-6px text-13px">
            {{ detailData?.mallConsumeRelation?.consumeAmount ?? '-' }} / {{ detailData?.mallConsumeRelation?.consumeStatus || '-' }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">推广抵扣</div>
          <div class="mt-8px text-16px font-600">{{ detailData?.promoteDeduct?.deductAmount ?? '-' }}</div>
          <div class="mt-6px text-13px">
            {{ [detailData?.promoteDeduct?.sourceType, detailData?.promoteDeduct?.sourceNo].filter(Boolean).join(' / ') || '-' }}
          </div>
          <div class="mt-6px text-13px text-[var(--el-text-color-secondary)]">
            抵扣后应付：{{ detailData?.promoteDeduct?.payableAmountAfterDeduct ?? '-' }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-divider content-position="left">支付与单元概览</el-divider>
    <el-row :gutter="12" class="mb-16px">
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">支付单状态</div>
          <div class="mt-8px text-16px font-600">
            <dict-tag
              v-if="detailData?.payRecord?.status"
              :type="DICT_TYPE.PAY_ORDER_STATUS"
              :value="detailData.payRecord.status"
            />
            <span v-else>-</span>
          </div>
          <div class="mt-6px text-13px">{{ detailData?.payRecord?.merchantOrderId || '-' }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">拆分单元数</div>
          <div class="mt-8px text-24px font-600">{{ detailData?.units?.length ?? 0 }}</div>
          <div class="mt-6px text-[var(--el-text-color-secondary)]">交付凭证：{{ detailData?.proofs?.length ?? 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">退款 / 投诉 / 申诉</div>
          <div class="mt-8px text-18px font-600">
            {{ detailData?.refunds?.length ?? 0 }} / {{ detailData?.complaints?.length ?? 0 }} / {{ detailData?.appeals?.length ?? 0 }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-divider content-position="left">价格明细</el-divider>
    <el-table v-if="detailData?.priceItems?.length" :data="detailData.priceItems" size="small" border max-height="220">
      <el-table-column label="类型" prop="itemType" width="120" />
      <el-table-column label="名称" prop="itemName" min-width="180" />
      <el-table-column label="金额" prop="itemAmount" width="120" />
      <el-table-column label="排序" prop="sortNo" width="100" />
    </el-table>
    <el-empty v-else description="暂无价格明细" :image-size="80" />

    <el-divider content-position="left">拆分单元</el-divider>
    <el-table v-if="detailData?.units?.length" :data="detailData.units" size="small" border max-height="240">
      <el-table-column label="单元号" prop="unitNo" min-width="160" />
      <el-table-column label="序号" prop="unitSeq" width="90" />
      <el-table-column label="标题" prop="unitTitle" min-width="160" />
      <el-table-column label="金额" prop="unitAmount" width="110" />
      <el-table-column label="锁定" width="90">
        <template #default="{ row }">{{ formatBooleanYesNo(row.isLocked) }}</template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="120">
        <template #default="{ row }">
          <dict-tag v-if="row.status" :type="DICT_TYPE.LB_ORDER_UNIT_STATUS" :value="row.status" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="核销状态" prop="verifyStatus" width="120">
        <template #default="{ row }">
          <span>{{ row.verifyStatus || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="核销码" prop="verifyCode" width="120" />
      <el-table-column label="核销时间" prop="verifyTime" width="180">
        <template #default="{ row }">{{ formatDate(row.verifyTime) }}</template>
      </el-table-column>
      <el-table-column label="截止时间" prop="acceptDeadlineTime" width="180">
        <template #default="{ row }">{{ formatDate(row.acceptDeadlineTime) }}</template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无拆分单元" :image-size="80" />

    <el-divider content-position="left">订单时间线</el-divider>
    <el-timeline v-if="detailData?.timeline?.length">
      <el-timeline-item
        v-for="item in detailData.timeline"
        :key="`${item.timelineType}-${item.bizId}-${item.eventTime}`"
        :timestamp="formatDate(item.eventTime)"
        placement="top"
      >
        <div class="font-600">{{ item.title || '-' }}</div>
        <div class="mt-4px text-[var(--el-text-color-secondary)]">
          {{ [item.timelineType, item.status].filter(Boolean).join(' / ') || '-' }}
        </div>
        <div class="mt-4px">{{ item.content || '-' }}</div>
      </el-timeline-item>
    </el-timeline>
    <el-empty v-else description="暂无订单时间线" :image-size="80" />

    <el-divider content-position="left">抢单记录</el-divider>
    <el-table v-if="detailData?.acceptRecords?.length" :data="detailData.acceptRecords" size="small" border max-height="220">
      <el-table-column label="ID" prop="id" width="90" />
      <el-table-column label="单元号" width="140">
        <template #default="{ row }">
          {{ formatAcceptRecordUnitDisplay(row.unitId) }}
        </template>
      </el-table-column>
      <el-table-column label="服务商" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.merchantName || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.merchantContactName || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.merchantContactMobile || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="抢单结果" prop="acceptResult" width="120">
        <template #default="{ row }">{{ formatAcceptResult(row.acceptResult) }}</template>
      </el-table-column>
      <el-table-column label="距离(km)" prop="distanceKm" width="110" />
      <el-table-column label="时间" prop="acceptTime" width="180">
        <template #default="{ row }">{{ formatDate(row.acceptTime) }}</template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无抢单记录" :image-size="80" />

    <el-divider content-position="left">退款记录</el-divider>
    <el-table v-if="detailData?.refunds?.length" :data="detailData.refunds" size="small" border max-height="220">
      <el-table-column label="ID" prop="id" width="90" />
      <el-table-column label="退款单号" prop="merchantRefundId" min-width="160" />
      <el-table-column label="退款金额" prop="refundPrice" width="110" />
      <el-table-column label="状态" prop="statusName" width="120">
        <template #default="{ row }">
          <dict-tag
            v-if="row.status !== undefined && row.status !== null"
            :type="DICT_TYPE.PAY_REFUND_STATUS"
            :value="row.status"
          />
          <span v-else>{{ row.statusName || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审核状态" prop="auditStatus" width="120">
        <template #default="{ row }">
          <dict-tag v-if="row.auditStatus" :type="DICT_TYPE.LB_AUDIT_STATUS" :value="row.auditStatus" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180">
        <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无退款记录" :image-size="80" />

    <el-divider content-position="left">投诉与申诉</el-divider>
    <el-row :gutter="12">
      <el-col :span="12">
        <el-table v-if="detailData?.complaints?.length" :data="detailData.complaints" size="small" border max-height="220">
          <el-table-column label="投诉单号" prop="complaintNo" min-width="150" />
          <el-table-column label="状态" prop="status" width="100">
            <template #default="{ row }">
              <dict-tag v-if="row.status" :type="DICT_TYPE.LB_COMPLAINT_STATUS" :value="row.status" />
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="处理时间" prop="handleTime" width="180">
            <template #default="{ row }">{{ formatDate(row.handleTime) }}</template>
          </el-table-column>
        </el-table>
        <el-empty v-else description="暂无投诉" :image-size="70" />
      </el-col>
      <el-col :span="12">
        <el-table v-if="detailData?.appeals?.length" :data="detailData.appeals" size="small" border max-height="220">
          <el-table-column label="申诉单号" prop="appealNo" min-width="150" />
          <el-table-column label="审核状态" prop="auditStatus" width="100">
            <template #default="{ row }">
              <dict-tag v-if="row.auditStatus" :type="DICT_TYPE.LB_AUDIT_STATUS" :value="row.auditStatus" />
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="审核时间" prop="auditTime" width="180">
            <template #default="{ row }">{{ formatDate(row.auditTime) }}</template>
          </el-table-column>
        </el-table>
        <el-empty v-else description="暂无申诉" :image-size="70" />
      </el-col>
    </el-row>

    <el-divider content-position="left">操作日志</el-divider>
    <el-table v-if="detailData?.operateLogs?.length" :data="detailData.operateLogs" size="small" border max-height="240">
      <el-table-column label="ID" prop="id" width="90" />
      <el-table-column label="类型" prop="operateType" width="140">
        <template #default="{ row }">{{ formatOperateType(row.operateType) }}</template>
      </el-table-column>
      <el-table-column label="角色" prop="operateRole" width="100">
        <template #default="{ row }">{{ formatOperateRole(row.operateRole) }}</template>
      </el-table-column>
      <el-table-column label="操作人" prop="operateBy" width="100" />
      <el-table-column label="状态变更" min-width="180">
        <template #default="{ row }">
          {{ formatOperateStatus(row.beforeStatus) }} -> {{ formatOperateStatus(row.afterStatus) }}
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="180" />
      <el-table-column label="时间" prop="operateTime" width="180">
        <template #default="{ row }">{{ formatDate(row.operateTime) }}</template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无操作日志" :image-size="80" />
  </Dialog>

  <OrderInfoForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { getStrDictOptions, DICT_TYPE } from '@/utils/dict'
import { dateFormatter, formatDate } from '@/utils/formatTime'
import download from '@/utils/download'
import { OrderInfoApi, type OrderInfo, type OrderInfoDetail } from '@/api/linbang/orderinfo'
import {
  formatAcceptResult,
  formatBooleanYesNo,
  formatOperateRole,
  formatOperateStatus,
  formatOperateType,
  formatSplitStatus
} from '../utils/display'
import OrderInfoForm from './OrderInfoForm.vue'
import { onMounted, reactive, ref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'

defineOptions({ name: 'OrderInfo' })

const message = useMessage()
const { t } = useI18n()
const loading = ref(false)
const exportLoading = ref(false)
const detailVisible = ref(false)
const detailLoading = ref(false)
const list = ref<OrderInfo[]>([])
const detailData = ref<OrderInfoDetail>()
const total = ref(0)
const queryFormRef = ref()
const formRef = ref()
const checkedIds = ref<number[]>([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  orderNo: undefined as string | undefined,
  userKeyword: undefined as string | undefined,
  merchantId: undefined as number | undefined,
  categoryId: undefined as number | undefined,
  title: undefined as string | undefined,
  pricingMode: undefined as string | undefined,
  status: undefined as string | undefined,
  createTime: [] as string[]
})

const formatIdFallback = (userId?: number) => {
  return userId ? '用户信息缺失' : '-'
}

const formatDetailUserDisplay = () => {
  const summary = [detailData.value?.userNickname, detailData.value?.userMobile, detailData.value?.userNo]
    .filter(Boolean)
    .join(' / ')
  return summary || formatIdFallback(detailData.value?.userId)
}

const formatMerchantDisplay = () => {
  const merchant = detailData.value?.merchant
  return [merchant?.merchantName, merchant?.contactName, merchant?.contactMobile].filter(Boolean).join(' / ')
    || (detailData.value?.merchantId ? '服务商信息缺失' : '-')
}

const formatAcceptRecordUnitDisplay = (unitId?: number) => {
  if (!unitId) {
    return '-'
  }
  const unit = detailData.value?.units?.find((item) => item.id === unitId)
  return unit?.unitNo || '单元信息缺失'
}

const getList = async () => {
  loading.value = true
  try {
    const data = await OrderInfoApi.getOrderInfoPage(queryParams)
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

const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const openDetail = async (id: number) => {
  detailVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await OrderInfoApi.getOrderInfo(id)
  } finally {
    detailLoading.value = false
  }
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await OrderInfoApi.deleteOrderInfo(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const handleDeleteBatch = async () => {
  try {
    await message.delConfirm()
    await OrderInfoApi.deleteOrderInfoList(checkedIds.value)
    checkedIds.value = []
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const handleRowCheckboxChange = (records: OrderInfo[]) => {
  checkedIds.value = records.map((item) => item.id)
}

const handleExport = async () => {
  try {
    await message.exportConfirm()
    exportLoading.value = true
    const data = await OrderInfoApi.exportOrderInfo(queryParams)
    download.excel(data, '订单列表.xls')
  } finally {
    exportLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
