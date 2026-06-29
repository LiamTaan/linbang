<template>
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :model="queryParams"
      :inline="true"
      label-width="88px"
      class="-mb-15px"
    >
      <el-form-item label="投诉单号" prop="complaintNo">
        <el-input v-model="queryParams.complaintNo" placeholder="请输入投诉单号" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="订单号" prop="orderNo">
        <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="单元号" prop="unitNo">
        <el-input v-model="queryParams.unitNo" placeholder="请输入单元号" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="投诉人" prop="complainantUserKeyword">
        <el-input v-model="queryParams.complainantUserKeyword" placeholder="请输入用户编号 / 昵称 / 手机号" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="被投诉人" prop="respondentUserKeyword">
        <el-input v-model="queryParams.respondentUserKeyword" placeholder="请输入用户编号 / 昵称 / 手机号" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-220px">
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_COMPLAINT_STATUS)"
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
          type="success"
          plain
          :loading="exportLoading"
          v-hasPermi="['linbang:review:complaint:export']"
          @click="handleExport"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="投诉单号" align="center" prop="complaintNo" min-width="180" />
      <el-table-column label="订单号" align="center" prop="orderNo" min-width="180" />
      <el-table-column label="单元号" align="center" prop="unitNo" min-width="160" />
      <el-table-column label="投诉人" align="center" min-width="220">
        <template #default="{ row }">
          <div class="font-600">{{ row.complainantUserNickname || '-' }}</div>
          <div class="text-[var(--el-text-color-secondary)]">{{ row.complainantUserMobile || '-' }}</div>
          <div class="text-[var(--el-text-color-secondary)]">{{ row.complainantUserNo || '-' }}</div>
        </template>
      </el-table-column>
      <el-table-column label="被投诉人" align="center" min-width="220">
        <template #default="{ row }">
          <div class="font-600">{{ row.respondentUserNickname || '-' }}</div>
          <div class="text-[var(--el-text-color-secondary)]">{{ row.respondentUserMobile || '-' }}</div>
          <div class="text-[var(--el-text-color-secondary)]">{{ row.respondentUserNo || '-' }}</div>
        </template>
      </el-table-column>
      <el-table-column label="投诉类型" align="center" prop="complaintType" min-width="120">
        <template #default="{ row }">
          {{ formatComplaintType(row.complaintType) }}
        </template>
      </el-table-column>
      <el-table-column label="投诉内容" align="center" prop="content" min-width="220" />
      <el-table-column label="状态" align="center" prop="status" width="110">
        <template #default="{ row }">
          <dict-tag v-if="row.status" :type="DICT_TYPE.LB_COMPLAINT_STATUS" :value="row.status" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="处理结果" align="center" prop="resultDesc" min-width="200" />
      <el-table-column label="处理人" align="center" prop="handleBy" width="100" />
      <el-table-column label="处理时间" align="center" prop="handleTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" align="center" fixed="right" width="170">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row.id)">详情</el-button>
          <el-button
            v-if="row.status !== 'FINISHED' && row.status !== 'REJECTED'"
            link
            type="primary"
            v-hasPermi="['linbang:review:complaint:process']"
            @click="openProcessDialog(row)"
          >
            处理
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

  <Dialog v-model="detailVisible" title="投诉详情" width="920px">
    <el-descriptions v-loading="detailLoading" :column="2" border>
      <el-descriptions-item label="投诉单号">{{ detailData?.complaintNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="订单号">{{ detailData?.order?.orderNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="单元号">{{ detailData?.unit?.unitNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="投诉类型">{{ formatComplaintType(detailData?.complaintType) }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <dict-tag v-if="detailData?.status" :type="DICT_TYPE.LB_COMPLAINT_STATUS" :value="detailData.status" />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="处理人">{{ detailData?.handleBy || '-' }}</el-descriptions-item>
      <el-descriptions-item label="处理时间">{{ formatDate(detailData?.handleTime) }}</el-descriptions-item>
      <el-descriptions-item label="投诉内容" :span="2">{{ detailData?.content || '-' }}</el-descriptions-item>
      <el-descriptions-item label="处理结果" :span="2">{{ detailData?.resultDesc || '-' }}</el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">订单与对象上下文</el-divider>
    <el-row :gutter="12" class="mb-16px">
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">主订单</div>
          <div class="mt-8px text-16px font-600">{{ detailData?.order?.orderNo || '-' }}</div>
          <div class="mt-6px text-13px">{{ detailData?.order?.title || '-' }}</div>
          <div class="mt-6px text-[var(--el-text-color-secondary)]">状态：{{ formatOrderStatus(detailData?.order?.status) }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">拆分单元</div>
          <div class="mt-8px text-16px font-600">{{ detailData?.unit?.unitNo || '-' }}</div>
          <div class="mt-6px text-13px">{{ detailData?.unit?.unitTitle || '-' }}</div>
          <div class="mt-6px text-[var(--el-text-color-secondary)]">状态：{{ formatOrderUnitStatus(detailData?.unit?.status) }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">被投诉服务商</div>
          <div class="mt-8px text-16px font-600">{{ detailData?.respondentMerchant?.merchantName || '-' }}</div>
          <div class="mt-6px text-13px">{{ detailData?.respondentMerchant?.contactName || '-' }}</div>
          <div class="mt-6px text-[var(--el-text-color-secondary)]">
            状态：{{ formatEnableStatus(detailData?.respondentMerchant?.status) }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-descriptions :column="2" border>
      <el-descriptions-item label="投诉人">
        {{ detailData?.complainantUser?.nickname || '-' }} / {{ detailData?.complainantUser?.mobile || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="被投诉人">
        {{ detailData?.respondentUser?.nickname || '-' }} / {{ detailData?.respondentUser?.mobile || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="下单用户">{{ formatOrderUserSummary(detailData?.order) }}</el-descriptions-item>
      <el-descriptions-item label="订单金额">{{ detailData?.order?.orderAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="单元序号">{{ detailData?.unit?.unitSeq || '-' }}</el-descriptions-item>
      <el-descriptions-item label="单元金额">{{ detailData?.unit?.unitAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="锁单状态">
        <span v-if="detailData?.unit">{{ detailData.unit.isLocked ? '已锁定' : '未锁定' }}</span>
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="锁单原因">{{ detailData?.unit?.lockReason || '-' }}</el-descriptions-item>
      <el-descriptions-item label="信用分 / 等级">
        {{ detailData?.respondentMerchant?.creditScore ?? '-' }} / {{ detailData?.respondentMerchant?.creditLevel || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="服务范围说明">{{ detailData?.respondentMerchant?.serviceScopeDesc || '-' }}</el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">统计概览</el-divider>
    <el-row :gutter="12" class="mb-16px">
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">附件数 / 同单投诉数</div>
          <div class="mt-8px text-18px font-600">
            {{ detailData?.summary?.attachmentCount ?? 0 }} / {{ detailData?.summary?.sameOrderComplaintCount ?? 0 }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">处理中 / 已完结</div>
          <div class="mt-8px text-18px font-600">
            {{ detailData?.summary?.processingCount ?? 0 }} / {{ detailData?.summary?.finishedCount ?? 0 }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">待受理 / 已驳回</div>
          <div class="mt-8px text-18px font-600">
            {{ detailData?.summary?.pendingCount ?? 0 }} / {{ detailData?.summary?.rejectedCount ?? 0 }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-divider content-position="left">附件文件</el-divider>
    <el-table v-if="detailData?.files?.length" :data="detailData.files" size="small" border max-height="220">
      <el-table-column label="附件文件" min-width="320">
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
    </el-table>
    <el-empty v-else description="暂无附件" :image-size="80" />

    <el-divider content-position="left">关联投诉记录</el-divider>
    <el-table
      v-if="detailData?.relatedComplaints?.length"
      :data="detailData.relatedComplaints"
      size="small"
      border
      max-height="240"
    >
      <el-table-column label="投诉单号" prop="complaintNo" min-width="150" />
      <el-table-column label="类型" prop="complaintType" width="120">
        <template #default="{ row }">
          {{ formatComplaintType(row.complaintType) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="110">
        <template #default="{ row }">
          <dict-tag v-if="row.status" :type="DICT_TYPE.LB_COMPLAINT_STATUS" :value="row.status" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="处理结果" prop="resultDesc" min-width="180" />
      <el-table-column label="处理时间" prop="handleTime" width="180">
        <template #default="{ row }">{{ formatDate(row.handleTime) }}</template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180">
        <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无关联投诉记录" :image-size="80" />

    <el-divider content-position="left">合作商协调记录</el-divider>
    <el-table
      v-if="detailData?.coordinationRecords?.length"
      :data="detailData.coordinationRecords"
      size="small"
      border
      max-height="240"
    >
      <el-table-column label="合作商ID" prop="partnerId" width="110" />
      <el-table-column label="协调状态" prop="status" width="120" />
      <el-table-column label="协调意见" prop="coordinationRemark" min-width="220" />
      <el-table-column label="升级说明" prop="escalateRemark" min-width="180" />
      <el-table-column label="发起人" prop="initiatedBy" width="100" />
      <el-table-column label="发起时间" prop="initiatedTime" width="180">
        <template #default="{ row }">{{ formatDate(row.initiatedTime) }}</template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无合作商协调记录" :image-size="80" />

    <el-divider content-position="left">订单操作日志</el-divider>
    <el-table
      v-if="detailData?.operateLogs?.length"
      :data="detailData.operateLogs"
      size="small"
      border
      max-height="240"
    >
      <el-table-column label="ID" prop="id" width="90" />
      <el-table-column label="操作类型" prop="operateType" width="140">
        <template #default="{ row }">{{ formatOperateType(row.operateType) }}</template>
      </el-table-column>
      <el-table-column label="操作角色" prop="operateRole" width="120">
        <template #default="{ row }">{{ formatOperateRole(row.operateRole) }}</template>
      </el-table-column>
      <el-table-column label="操作人" prop="operateBy" width="100" />
      <el-table-column label="状态变更" min-width="180">
        <template #default="{ row }">
          {{ formatComplaintStatus(row.beforeStatus) }} -> {{ formatComplaintStatus(row.afterStatus) }}
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="180" />
      <el-table-column label="操作时间" prop="operateTime" width="180">
        <template #default="{ row }">{{ formatDate(row.operateTime) }}</template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无操作日志" :image-size="80" />
  </Dialog>

  <Dialog v-model="processDialogVisible" title="投诉处理" width="520px">
    <el-form
      ref="processFormRef"
      :model="processFormData"
      :rules="processFormRules"
      label-width="88px"
      v-loading="processLoading"
    >
      <el-form-item label="投诉单号">
        <el-input :model-value="currentRow?.complaintNo" disabled />
      </el-form-item>
      <el-form-item label="当前状态">
        <dict-tag v-if="currentRow?.status" :type="DICT_TYPE.LB_COMPLAINT_STATUS" :value="currentRow.status" />
      </el-form-item>
      <el-form-item label="处理结果" prop="status">
        <el-radio-group v-model="processFormData.status">
          <el-radio value="PROCESSING">处理中</el-radio>
          <el-radio value="FINISHED">已完结</el-radio>
          <el-radio value="REJECTED">驳回</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="处理说明" prop="resultDesc">
        <el-input
          v-model="processFormData.resultDesc"
          type="textarea"
          :rows="4"
          maxlength="255"
          show-word-limit
          placeholder="请输入处理说明"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="processDialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="processLoading" @click="submitProcess">提交处理</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { getStrDictOptions, DICT_TYPE } from '@/utils/dict'
import { dateFormatter, formatDate } from '@/utils/formatTime'
import download from '@/utils/download'
import { useMessage } from '@/hooks/web/useMessage'
import { requestDynamicKeyToken } from '../shared/dynamic-key'
import {
  formatComplaintType,
  formatEnableStatus,
  formatOperateRole,
  formatOperateType,
  formatOrderStatus,
  formatOrderUnitStatus
} from '../utils/display'
import {
  ComplaintApi,
  type Complaint,
  type ComplaintDetail,
  type ComplaintProcessReqVO
} from '@/api/linbang/complaint'
import { formatFileBrief, loadFilesByIds, type FileLookupMap } from '../shared/file-display'

defineOptions({ name: 'Complaint' })

const message = useMessage()
const loading = ref(false)
const exportLoading = ref(false)
const detailVisible = ref(false)
const detailLoading = ref(false)
const list = ref<Complaint[]>([])
const detailData = ref<ComplaintDetail>()
const fileMap = reactive<FileLookupMap>({})
const total = ref(0)
const queryFormRef = ref<FormInstance>()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  complaintNo: undefined as string | undefined,
  orderNo: undefined as string | undefined,
  unitNo: undefined as string | undefined,
  complainantUserKeyword: undefined as string | undefined,
  respondentUserKeyword: undefined as string | undefined,
  status: undefined as string | undefined,
  createTime: [] as string[]
})

const formatComplaintStatus = (value?: string) => {
  if (value === 'PENDING') {
    return '待受理'
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

const formatOrderUserSummary = (order?: ComplaintDetail['order']) => {
  const summary = [order?.userNickname, order?.userMobile, order?.userNo].filter(Boolean).join(' / ')
  return summary || (order?.userId ? '用户信息缺失' : '-')
}

const getList = async () => {
  loading.value = true
  try {
    const data = await ComplaintApi.getComplaintPage(queryParams)
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

const handleExport = async () => {
  try {
    await message.exportConfirm()
    exportLoading.value = true
    const data = await ComplaintApi.exportComplaint(queryParams)
    download.excel(data, '投诉处理列表.xls')
  } finally {
    exportLoading.value = false
  }
}

const openDetail = async (id: number) => {
  detailVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await ComplaintApi.getComplaint(id)
    Object.assign(fileMap, await loadFilesByIds(detailData.value.files?.map((item) => item.fileId) || []))
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

const processDialogVisible = ref(false)
const processLoading = ref(false)
const processFormRef = ref<FormInstance>()
const currentRow = ref<Complaint>()
const processFormData = reactive<ComplaintProcessReqVO>({
  id: 0,
  status: 'PROCESSING',
  resultDesc: ''
})
const processFormRules = reactive<FormRules>({
  status: [{ required: true, message: '请选择处理结果', trigger: 'change' }],
  resultDesc: [{ required: true, message: '请输入处理说明', trigger: 'blur' }]
})

const openProcessDialog = (row: Complaint) => {
  currentRow.value = row
  processFormData.id = row.id
  processFormData.status = row.status === 'PROCESSING' ? 'FINISHED' : 'PROCESSING'
  processFormData.resultDesc = row.resultDesc || ''
  processDialogVisible.value = true
}

const submitProcess = async () => {
  await processFormRef.value?.validate()
  try {
    const verifyToken = await requestDynamicKeyToken('投诉处理')
    processLoading.value = true
    await ComplaintApi.processComplaint({ ...processFormData }, verifyToken)
    message.success('投诉处理成功')
    processDialogVisible.value = false
    await getList()
  } finally {
    processLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
