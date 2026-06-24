<template>
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :model="queryParams"
      :inline="true"
      label-width="88px"
      class="-mb-15px"
    >
      <el-form-item label="订单号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单号"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="单元号" prop="unitNo">
        <el-input
          v-model="queryParams.unitNo"
          placeholder="请输入单元号"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发起人" prop="fromUserKeyword">
        <el-input
          v-model="queryParams.fromUserKeyword"
          placeholder="请输入用户编号 / 昵称 / 手机号"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="目标用户" prop="toUserKeyword">
        <el-input
          v-model="queryParams.toUserKeyword"
          placeholder="请输入用户编号 / 昵称 / 手机号"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="星级" prop="starLevel">
        <el-input-number v-model="queryParams.starLevel" :min="1" :max="5" class="!w-220px" />
      </el-form-item>
      <el-form-item label="自动评价" prop="isAutoReview">
        <el-select v-model="queryParams.isAutoReview" placeholder="请选择是否自动评价" clearable class="!w-220px">
          <el-option
            v-for="item in AUTO_REVIEW_OPTIONS"
            :key="String(item.value)"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-220px">
          <el-option
            v-for="item in ENABLE_STATUS_OPTIONS"
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
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          v-hasPermi="['linbang:review:comment:create']"
          @click="openForm('create')"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          :loading="exportLoading"
          v-hasPermi="['linbang:review:comment:export']"
          @click="handleExport"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="checkedIds.length === 0"
          v-hasPermi="['linbang:review:comment:delete']"
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
      <el-table-column label="单元号" align="center" prop="unitNo" min-width="160" />
      <el-table-column label="发起人" align="center" min-width="220">
        <template #default="{ row }">
          <div class="font-600">{{ row.fromUserNickname || '-' }}</div>
          <div class="text-[var(--el-text-color-secondary)]">{{ row.fromUserMobile || '-' }}</div>
          <div class="text-[var(--el-text-color-secondary)]">{{ row.fromUserNo || '-' }}</div>
        </template>
      </el-table-column>
      <el-table-column label="目标用户" align="center" min-width="220">
        <template #default="{ row }">
          <div class="font-600">{{ row.toUserNickname || '-' }}</div>
          <div class="text-[var(--el-text-color-secondary)]">{{ row.toUserMobile || '-' }}</div>
          <div class="text-[var(--el-text-color-secondary)]">{{ row.toUserNo || '-' }}</div>
        </template>
      </el-table-column>
      <el-table-column label="星级" align="center" prop="starLevel" width="90">
        <template #default="{ row }">
          <el-rate :model-value="row.starLevel || 0" disabled show-score text-color="#ff9900" />
        </template>
      </el-table-column>
      <el-table-column label="评价内容" align="center" prop="content" min-width="220" />
      <el-table-column label="自动评价" align="center" prop="isAutoReview" width="110">
        <template #default="{ row }">
          <el-tag :type="row.isAutoReview ? 'warning' : 'success'">
            {{ formatAutoReviewLabel(row.isAutoReview) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" min-width="120">
        <template #default="{ row }">
          {{ formatEnableStatus(row.status) }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" align="center" fixed="right" width="190">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row.id)">详情</el-button>
          <el-button
            link
            type="primary"
            v-hasPermi="['linbang:review:comment:update']"
            @click="openForm('update', row.id)"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            v-hasPermi="['linbang:review:comment:delete']"
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

  <Dialog v-model="detailVisible" title="评价详情" width="920px">
    <el-descriptions v-loading="detailLoading" :column="2" border>
      <el-descriptions-item label="状态">{{ formatEnableStatus(detailData?.status) }}</el-descriptions-item>
      <el-descriptions-item label="订单号">{{ detailData?.order?.orderNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="单元号">{{ detailData?.unit?.unitNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="发起人">
        {{ detailData?.fromUser?.nickname || '-' }} / {{ detailData?.fromUser?.mobile || '-' }} / {{ detailData?.fromUser?.userNo || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="目标用户">
        {{ detailData?.toUser?.nickname || '-' }} / {{ detailData?.toUser?.mobile || '-' }} / {{ detailData?.toUser?.userNo || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="星级">
        <el-rate :model-value="detailData?.starLevel || 0" disabled show-score text-color="#ff9900" />
      </el-descriptions-item>
      <el-descriptions-item label="自动评价">
        <el-tag v-if="detailData" :type="detailData.isAutoReview ? 'warning' : 'success'">
          {{ formatAutoReviewLabel(detailData.isAutoReview) }}
        </el-tag>
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDate(detailData?.createTime) }}</el-descriptions-item>
      <el-descriptions-item label="目标服务商">
        {{ detailData?.toMerchant?.merchantName || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="评价内容" :span="2">{{ detailData?.content || '-' }}</el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">订单与对象上下文</el-divider>
    <el-row :gutter="12" class="mb-16px">
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">主订单</div>
          <div class="mt-8px text-16px font-600">{{ detailData?.order?.orderNo || '-' }}</div>
          <div class="mt-6px text-13px">{{ detailData?.order?.title || '-' }}</div>
          <div class="mt-6px text-[var(--el-text-color-secondary)]">
            状态：
            <dict-tag
              v-if="detailData?.order?.status"
              :type="DICT_TYPE.LB_ORDER_STATUS"
              :value="detailData.order.status"
            />
            <span v-else>-</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">拆分单元</div>
          <div class="mt-8px text-16px font-600">{{ detailData?.unit?.unitNo || '-' }}</div>
          <div class="mt-6px text-13px">{{ detailData?.unit?.unitTitle || '-' }}</div>
          <div class="mt-6px text-[var(--el-text-color-secondary)]">
            状态：
            <dict-tag
              v-if="detailData?.unit?.status"
              :type="DICT_TYPE.LB_ORDER_UNIT_STATUS"
              :value="detailData.unit.status"
            />
            <span v-else>-</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">目标服务商</div>
          <div class="mt-8px text-16px font-600">{{ detailData?.toMerchant?.merchantName || '-' }}</div>
          <div class="mt-6px text-13px">{{ detailData?.toMerchant?.contactName || '-' }}</div>
          <div class="mt-6px text-[var(--el-text-color-secondary)]">
            状态：{{ formatEnableStatus(detailData?.toMerchant?.status) }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-descriptions :column="2" border>
      <el-descriptions-item label="发起人">
        {{ detailData?.fromUser?.nickname || '-' }} / {{ detailData?.fromUser?.mobile || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="目标用户">
        {{ detailData?.toUser?.nickname || '-' }} / {{ detailData?.toUser?.mobile || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="主订单号">{{ detailData?.order?.orderNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="订单金额">{{ detailData?.order?.orderAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="单元序号">{{ detailData?.unit?.unitSeq || '-' }}</el-descriptions-item>
      <el-descriptions-item label="单元金额">{{ detailData?.unit?.unitAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="锁单状态">
        <span v-if="detailData?.unit">{{ detailData.unit.isLocked ? '已锁定' : '未锁定' }}</span>
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="锁单原因">{{ detailData?.unit?.lockReason || '-' }}</el-descriptions-item>
      <el-descriptions-item label="接单截止">{{ formatDate(detailData?.unit?.acceptDeadlineTime) }}</el-descriptions-item>
      <el-descriptions-item label="完工时间">{{ formatDate(detailData?.unit?.finishTime) }}</el-descriptions-item>
      <el-descriptions-item label="信用分 / 等级">
        {{ detailData?.toMerchant?.creditScore ?? '-' }} / {{ detailData?.toMerchant?.creditLevel || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="接单状态">{{ formatAcceptStatus(detailData?.toMerchant?.acceptStatus) }}</el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">统计概览</el-divider>
    <el-row :gutter="12" class="mb-16px">
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">同单评价 / 目标数</div>
          <div class="mt-8px text-18px font-600">
            {{ detailData?.summary?.sameOrderReviewCount ?? 0 }} / {{ detailData?.summary?.sameTargetReviewCount ?? 0 }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">好评 / 中评 / 差评</div>
          <div class="mt-8px text-18px font-600">
            {{ detailData?.summary?.positiveReviewCount ?? 0 }} / {{ detailData?.summary?.neutralReviewCount ?? 0 }} /
            {{ detailData?.summary?.negativeReviewCount ?? 0 }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">自动评价 / 信用记录</div>
          <div class="mt-8px text-18px font-600">
            {{ detailData?.summary?.autoReviewCount ?? 0 }} / {{ detailData?.summary?.creditRecordCount ?? 0 }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-divider content-position="left">关联评价记录</el-divider>
    <el-table
      v-if="detailData?.relatedReviews?.length"
      :data="detailData.relatedReviews"
      size="small"
      border
      max-height="240"
    >
      <el-table-column label="ID" prop="id" width="90" />
      <el-table-column label="发起人" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.fromUserNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.fromUserMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">
              {{ row.fromUserNo || formatIdFallback(row.fromUserId) }}
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="目标用户" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.toUserNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.toUserMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">
              {{ row.toUserNo || formatIdFallback(row.toUserId) }}
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="星级" prop="starLevel" width="90">
        <template #default="{ row }">
          <el-rate :model-value="row.starLevel || 0" disabled />
        </template>
      </el-table-column>
      <el-table-column label="自动评价" width="110">
        <template #default="{ row }">
          {{ formatBooleanYesNo(row.isAutoReview) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          {{ formatEnableStatus(row.status) }}
        </template>
      </el-table-column>
      <el-table-column label="内容" prop="content" min-width="200" />
      <el-table-column label="创建时间" prop="createTime" width="180">
        <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无关联评价记录" :image-size="80" />

    <el-divider content-position="left">信用记录</el-divider>
    <el-table
      v-if="detailData?.creditRecords?.length"
      :data="detailData.creditRecords"
      size="small"
      border
      max-height="240"
    >
      <el-table-column label="ID" prop="id" width="90" />
      <el-table-column label="规则编码" prop="ruleCode" min-width="140" />
      <el-table-column label="规则名称" prop="ruleName" min-width="160" />
      <el-table-column label="分值变动" prop="scoreChange" width="110" />
      <el-table-column label="前后分值" min-width="160">
        <template #default="{ row }">{{ row.beforeScore ?? '-' }} -> {{ row.afterScore ?? '-' }}</template>
      </el-table-column>
      <el-table-column label="触发类型" prop="triggerType" width="120">
        <template #default="{ row }">
          {{ formatTriggerType(row.triggerType) }}
        </template>
      </el-table-column>
      <el-table-column label="业务类型" prop="bizType" width="120">
        <template #default="{ row }">
          {{ formatBizType(row.bizType) }}
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="200" />
      <el-table-column label="创建时间" prop="createTime" width="180">
        <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无信用记录" :image-size="80" />
  </Dialog>

  <ReviewCommentForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import type { FormInstance } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { DICT_TYPE } from '@/utils/dict'
import { dateFormatter, formatDate } from '@/utils/formatTime'
import download from '@/utils/download'
import { useMessage } from '@/hooks/web/useMessage'
import {
  AUTO_REVIEW_OPTIONS,
  ENABLE_STATUS_OPTIONS,
  formatAcceptStatus,
  formatAutoReviewLabel,
  formatBizType,
  formatBooleanYesNo,
  formatEnableStatus,
  formatTriggerType
} from '../utils/display'
import {
  ReviewCommentApi,
  type ReviewComment,
  type ReviewCommentDetail
} from '@/api/linbang/reviewcomment'
import ReviewCommentForm from './ReviewCommentForm.vue'

defineOptions({ name: 'ReviewComment' })

const message = useMessage()
const loading = ref(false)
const exportLoading = ref(false)
const detailVisible = ref(false)
const detailLoading = ref(false)
const list = ref<ReviewComment[]>([])
const detailData = ref<ReviewCommentDetail>()
const total = ref(0)
const queryFormRef = ref<FormInstance>()
const formRef = ref()
const checkedIds = ref<number[]>([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  orderNo: undefined as string | undefined,
  unitNo: undefined as string | undefined,
  fromUserKeyword: undefined as string | undefined,
  toUserKeyword: undefined as string | undefined,
  starLevel: undefined as number | undefined,
  isAutoReview: undefined as boolean | undefined,
  status: undefined as string | undefined,
  createTime: [] as string[]
})

const formatIdFallback = (userId?: number) => {
  return userId ? '用户信息缺失' : '-'
}

const getList = async () => {
  loading.value = true
  try {
    const data = await ReviewCommentApi.getReviewCommentPage(queryParams)
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
    detailData.value = await ReviewCommentApi.getReviewComment(id)
  } finally {
    detailLoading.value = false
  }
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await ReviewCommentApi.deleteReviewComment(id)
    message.success('删除成功')
    await getList()
  } catch {}
}

const handleDeleteBatch = async () => {
  try {
    await message.delConfirm()
    await ReviewCommentApi.deleteReviewCommentList(checkedIds.value)
    checkedIds.value = []
    message.success('删除成功')
    await getList()
  } catch {}
}

const handleRowCheckboxChange = (records: ReviewComment[]) => {
  checkedIds.value = records.map((item) => item.id)
}

const handleExport = async () => {
  try {
    await message.exportConfirm()
    exportLoading.value = true
    const data = await ReviewCommentApi.exportReviewComment(queryParams)
    download.excel(data, '评价列表.xls')
  } finally {
    exportLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
