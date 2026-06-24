<template>
  <ContentWrap>
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="72px"
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
      <el-form-item label="单元序号" prop="unitSeq">
        <el-input
          v-model="queryParams.unitSeq"
          placeholder="请输入单元序号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="单元标题" prop="unitTitle">
        <el-input
          v-model="queryParams.unitTitle"
          placeholder="请输入单元标题"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="单元金额" prop="unitAmount">
        <el-input
          v-model="queryParams.unitAmount"
          placeholder="请输入单元金额"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="拆分模式" prop="splitMode">
        <el-select
          v-model="queryParams.splitMode"
          placeholder="请选择拆分模式"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="item in SPLIT_MODE_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="前置单元" prop="prevUnitNo">
        <el-input
          v-model="queryParams.prevUnitNo"
          placeholder="请输入前置单元号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="是否锁定" prop="isLocked">
        <el-select
          v-model="queryParams.isLocked"
          placeholder="请选择是否锁定"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="item in BOOLEAN_YES_NO_OPTIONS"
            :key="String(item.value)"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="锁定原因" prop="lockReason">
        <el-input
          v-model="queryParams.lockReason"
          placeholder="请输入锁定原因"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="单元状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择单元状态"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_ORDER_UNIT_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="接单截止" prop="acceptDeadlineTime">
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
      <el-form-item label="完成时间" prop="finishTime">
        <el-date-picker
          v-model="queryParams.finishTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['linbang:order:unit:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['linbang:order:unit:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="isEmpty(checkedIds)"
          @click="handleDeleteBatch"
          v-hasPermi="['linbang:order:unit:delete']"
        >
          <Icon icon="ep:delete" class="mr-5px" /> 批量删除
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

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
      <el-table-column label="订单号" align="center" prop="orderNo" min-width="160" />
      <el-table-column label="单元号" align="center" prop="unitNo" min-width="160" />
      <el-table-column label="单元序号" align="center" prop="unitSeq" width="100" />
      <el-table-column label="单元标题" align="center" prop="unitTitle" min-width="180" />
      <el-table-column label="单元金额" align="center" prop="unitAmount" width="120" />
      <el-table-column label="拆分模式" align="center" prop="splitMode" width="120">
        <template #default="{ row }">
          {{ formatSplitMode(row.splitMode) }}
        </template>
      </el-table-column>
      <el-table-column label="前置单元号" align="center" prop="prevUnitNo" min-width="160" />
      <el-table-column label="是否锁定" align="center" prop="isLocked" width="100">
        <template #default="{ row }">
          {{ formatBooleanYesNo(row.isLocked) }}
        </template>
      </el-table-column>
      <el-table-column label="锁定原因" align="center" prop="lockReason" min-width="160" />
      <el-table-column label="服务商" align="center" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.merchantName || (row.merchantId ? '服务商信息缺失' : '-') }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.merchantContactName || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.merchantContactMobile || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="单元状态" align="center" prop="status" width="120">
        <template #default="{ row }">
          <dict-tag :type="DICT_TYPE.LB_ORDER_UNIT_STATUS" :value="row.status" />
        </template>
      </el-table-column>
      <el-table-column
        label="接单截止时间"
        align="center"
        prop="acceptDeadlineTime"
        :formatter="dateFormatter"
        width="180"
      />
      <el-table-column
        label="完成时间"
        align="center"
        prop="finishTime"
        :formatter="dateFormatter"
        width="180"
      />
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="180"
      />
      <el-table-column label="操作" align="center" min-width="170">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row.id)">详情</el-button>
          <el-button
            link
            type="primary"
            @click="openForm('update', row.id)"
            v-hasPermi="['linbang:order:unit:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(row.id)"
            v-hasPermi="['linbang:order:unit:delete']"
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

    <Dialog v-model="detailVisible" title="拆分单元详情" width="980px">
    <el-descriptions v-loading="detailLoading" :column="2" border>
      <el-descriptions-item label="单元号">{{ detailData?.unitNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="单元序号">{{ detailData?.unitSeq ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="单元状态">
        <dict-tag v-if="detailData?.status" :type="DICT_TYPE.LB_ORDER_UNIT_STATUS" :value="detailData.status" />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="单元标题">{{ detailData?.unitTitle || '-' }}</el-descriptions-item>
      <el-descriptions-item label="单元金额">{{ detailData?.unitAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="拆分模式">{{ formatSplitMode(detailData?.splitMode) }}</el-descriptions-item>
      <el-descriptions-item label="前置单元号">{{ detailData?.prevUnitNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="是否锁定">{{ formatBooleanYesNo(detailData?.isLocked) }}</el-descriptions-item>
      <el-descriptions-item label="锁定原因">{{ detailData?.lockReason || '-' }}</el-descriptions-item>
      <el-descriptions-item label="服务商">{{ formatMerchantSummary() }}</el-descriptions-item>
      <el-descriptions-item label="接单截止时间">{{ formatDate(detailData?.acceptDeadlineTime) }}</el-descriptions-item>
      <el-descriptions-item label="完成时间">{{ formatDate(detailData?.finishTime) }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDate(detailData?.createTime) }}</el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">主订单上下文</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="订单号">{{ detailData?.orderNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="订单标题">{{ detailData?.orderTitle || '-' }}</el-descriptions-item>
      <el-descriptions-item label="订单状态">
        <dict-tag v-if="detailData?.orderStatus" :type="DICT_TYPE.LB_ORDER_STATUS" :value="detailData.orderStatus" />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="下单用户">{{ formatOrderUserSummary() }}</el-descriptions-item>
      <el-descriptions-item label="服务商">{{ formatMerchantSummary() }}</el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">单元概览</el-divider>
    <el-row :gutter="12" class="mb-16px">
      <el-col :span="6">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">交付凭证数</div>
          <div class="mt-8px text-24px font-600">{{ detailData?.proofs?.length ?? 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">抢单记录数</div>
          <div class="mt-8px text-24px font-600">{{ detailData?.acceptRecords?.length ?? 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">投诉 / 申诉</div>
          <div class="mt-8px text-18px font-600">
            {{ detailData?.complaints?.length ?? 0 }} / {{ detailData?.appeals?.length ?? 0 }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">操作日志数</div>
          <div class="mt-8px text-24px font-600">{{ detailData?.operateLogs?.length ?? 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-divider content-position="left">交付凭证</el-divider>
    <el-table v-if="detailData?.proofs?.length" :data="detailData.proofs" size="small" border max-height="220">
      <el-table-column label="ID" prop="id" width="90" />
      <el-table-column label="附件文件" min-width="240">
        <template #default="{ row }">
          <el-link
            v-if="row.fileId && fileMap[row.fileId]?.url"
            :href="fileMap[row.fileId].url"
            target="_blank"
            type="primary"
            :underline="false"
          >
            {{ formatAttachedFile(row.fileId) }}
          </el-link>
          <span v-else>{{ formatAttachedFile(row.fileId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="服务商" min-width="200">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.merchantName || (row.merchantId ? '服务商信息缺失' : '-') }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.merchantContactName || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.merchantContactMobile || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="凭证类型" prop="proofType" width="120">
        <template #default="{ row }">{{ formatProofType(row.proofType) }}</template>
      </el-table-column>
      <el-table-column label="说明" prop="proofDesc" min-width="180" />
      <el-table-column label="凭证时间" width="180">
        <template #default="{ row }">{{ formatDate(row.proofTime) }}</template>
      </el-table-column>
      <el-table-column label="坐标" min-width="180">
        <template #default="{ row }">{{ row.longitude ?? '-' }} / {{ row.latitude ?? '-' }}</template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无交付凭证" :image-size="80" />

    <el-divider content-position="left">抢单记录</el-divider>
    <el-table v-if="detailData?.acceptRecords?.length" :data="detailData.acceptRecords" size="small" border max-height="220">
      <el-table-column label="ID" prop="id" width="90" />
      <el-table-column label="服务商" min-width="200">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.merchantName || (row.merchantId ? '服务商信息缺失' : '-') }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.merchantContactName || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.merchantContactMobile || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="抢单结果" prop="acceptResult" width="120">
        <template #default="{ row }">{{ formatAcceptResult(row.acceptResult) }}</template>
      </el-table-column>
      <el-table-column label="距离(km)" prop="distanceKm" width="110" />
      <el-table-column label="备注" prop="remark" min-width="180" />
      <el-table-column label="抢单时间" width="180">
        <template #default="{ row }">{{ formatDate(row.acceptTime) }}</template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无抢单记录" :image-size="80" />

    <el-divider content-position="left">投诉与申诉</el-divider>
    <el-row :gutter="12">
      <el-col :span="12">
        <el-table v-if="detailData?.complaints?.length" :data="detailData.complaints" size="small" border max-height="220">
          <el-table-column label="投诉单号" prop="complaintNo" min-width="150" />
          <el-table-column label="投诉类型" prop="complaintType" width="110">
            <template #default="{ row }">{{ formatComplaintType(row.complaintType) }}</template>
          </el-table-column>
          <el-table-column label="状态" prop="status" width="100">
            <template #default="{ row }">
              <dict-tag v-if="row.status" :type="DICT_TYPE.LB_COMPLAINT_STATUS" :value="row.status" />
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="处理结果" prop="resultDesc" min-width="160" />
          <el-table-column label="处理时间" width="180">
            <template #default="{ row }">{{ formatDate(row.handleTime) }}</template>
          </el-table-column>
        </el-table>
        <el-empty v-else description="暂无投诉" :image-size="70" />
      </el-col>
      <el-col :span="12">
        <el-table v-if="detailData?.appeals?.length" :data="detailData.appeals" size="small" border max-height="220">
          <el-table-column label="申诉单号" prop="appealNo" min-width="150" />
          <el-table-column label="申诉类型" prop="appealType" width="110">
            <template #default="{ row }">{{ formatAppealType(row.appealType) }}</template>
          </el-table-column>
          <el-table-column label="审核状态" prop="auditStatus" width="110">
            <template #default="{ row }">
              <dict-tag v-if="row.auditStatus" :type="DICT_TYPE.LB_AUDIT_STATUS" :value="row.auditStatus" />
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="审核备注" prop="auditRemark" min-width="160" />
          <el-table-column label="审核时间" width="180">
            <template #default="{ row }">{{ formatDate(row.auditTime) }}</template>
          </el-table-column>
        </el-table>
        <el-empty v-else description="暂无申诉" :image-size="70" />
      </el-col>
    </el-row>

    <el-divider content-position="left">操作日志</el-divider>
    <el-table v-if="detailData?.operateLogs?.length" :data="detailData.operateLogs" size="small" border max-height="240">
      <el-table-column label="ID" prop="id" width="90" />
      <el-table-column label="操作类型" prop="operateType" width="120">
        <template #default="{ row }">{{ formatOperateType(row.operateType) }}</template>
      </el-table-column>
      <el-table-column label="操作角色" prop="operateRole" width="110">
        <template #default="{ row }">{{ formatOperateRole(row.operateRole) }}</template>
      </el-table-column>
      <el-table-column label="操作人" prop="operateBy" width="100" />
      <el-table-column label="状态变更" min-width="180">
        <template #default="{ row }">
          {{ formatOperateStatus(row.beforeStatus) }} -> {{ formatOperateStatus(row.afterStatus) }}
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="180" />
      <el-table-column label="操作时间" width="180">
        <template #default="{ row }">{{ formatDate(row.operateTime) }}</template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无操作日志" :image-size="80" />
  </Dialog>

  <OrderUnitForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { getStrDictOptions, DICT_TYPE } from '@/utils/dict'
import { isEmpty } from '@/utils/is'
import { dateFormatter, formatDate } from '@/utils/formatTime'
import download from '@/utils/download'
import { OrderUnitApi, OrderUnit, OrderUnitDetail } from '@/api/linbang/orderunit'
import {
  formatAcceptResult,
  formatAppealType,
  BOOLEAN_YES_NO_OPTIONS,
  formatBooleanYesNo,
  formatComplaintType,
  formatOperateRole,
  formatOperateStatus,
  formatOperateType,
  formatProofType,
  formatSplitMode,
  SPLIT_MODE_OPTIONS
} from '../utils/display'
import OrderUnitForm from './OrderUnitForm.vue'
import { formatFileBrief, loadFilesByIds, type FileLookupMap } from '../shared/file-display'

import { onMounted, reactive, ref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
/** 拆分单元 列表 */
defineOptions({ name: 'OrderUnit' })

const message = useMessage()
const { t } = useI18n()

const loading = ref(true)
const detailVisible = ref(false)
const detailLoading = ref(false)
const list = ref<OrderUnit[]>([])
const detailData = ref<OrderUnitDetail>()
const fileMap = reactive<FileLookupMap>({})
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  orderNo: undefined as string | undefined,
  unitNo: undefined as string | undefined,
  unitSeq: undefined as number | undefined,
  unitTitle: undefined as string | undefined,
  unitAmount: undefined as number | undefined,
  splitMode: undefined as string | undefined,
  prevUnitNo: undefined as string | undefined,
  isLocked: undefined as boolean | undefined,
  lockReason: undefined as string | undefined,
  merchantId: undefined as number | undefined,
  status: undefined as string | undefined,
  acceptDeadlineTime: [] as string[],
  finishTime: [] as string[],
  createTime: [] as string[]
})
const queryFormRef = ref()
const exportLoading = ref(false)

const formatOrderUserSummary = () => {
  const summary = [detailData.value?.userNickname, detailData.value?.userMobile, detailData.value?.userNo]
    .filter(Boolean)
    .join(' / ')
  return summary || (detailData.value?.userId ? '用户信息缺失' : '-')
}

const formatMerchantSummary = () => {
  const summary = [
    detailData.value?.merchantName,
    detailData.value?.merchantContactName,
    detailData.value?.merchantContactMobile
  ]
    .filter(Boolean)
    .join(' / ')
  return summary || (detailData.value?.merchantId ? '服务商信息缺失' : '-')
}

const getList = async () => {
  loading.value = true
  try {
    const data = await OrderUnitApi.getOrderUnitPage(queryParams)
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
  queryFormRef.value.resetFields()
  handleQuery()
}

const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const openDetail = async (id: number) => {
  detailVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await OrderUnitApi.getOrderUnit(id)
    Object.assign(fileMap, await loadFilesByIds(detailData.value.proofs?.map((item) => item.fileId) || []))
  } finally {
    detailLoading.value = false
  }
}

const formatAttachedFile = (fileId?: number) => {
  if (!fileId) {
    return '-'
  }
  return formatFileBrief(fileMap[fileId], '附件信息缺失')
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await OrderUnitApi.deleteOrderUnit(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const handleDeleteBatch = async () => {
  try {
    await message.delConfirm()
    await OrderUnitApi.deleteOrderUnitList(checkedIds.value)
    checkedIds.value = []
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const checkedIds = ref<number[]>([])
const handleRowCheckboxChange = (records: OrderUnit[]) => {
  checkedIds.value = records.map((item) => item.id)
}

const handleExport = async () => {
  try {
    await message.exportConfirm()
    exportLoading.value = true
    const data = await OrderUnitApi.exportOrderUnit(queryParams)
    download.excel(data, '拆分单元.xls')
  } finally {
    exportLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
