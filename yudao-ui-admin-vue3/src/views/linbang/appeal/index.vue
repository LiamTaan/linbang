<template>
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :model="queryParams"
      :inline="true"
      label-width="88px"
      class="-mb-15px"
    >
      <el-form-item label="申诉单号" prop="appealNo">
        <el-input v-model="queryParams.appealNo" placeholder="请输入申诉单号" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="订单号" prop="orderNo">
        <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="单元号" prop="unitNo">
        <el-input v-model="queryParams.unitNo" placeholder="请输入单元号" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="申诉人" prop="userKeyword">
        <el-input v-model="queryParams.userKeyword" placeholder="请输入用户编号 / 昵称 / 手机号" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-220px">
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_APPEAL_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select v-model="queryParams.auditStatus" placeholder="请选择审核状态" clearable class="!w-220px">
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_AUDIT_STATUS)"
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
          v-hasPermi="['linbang:review:appeal:export']"
          @click="handleExport"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="申诉单号" align="center" prop="appealNo" min-width="180" />
      <el-table-column label="订单号" align="center" prop="orderNo" min-width="180" />
      <el-table-column label="单元号" align="center" prop="unitNo" min-width="160" />
      <el-table-column label="申诉人" align="center" min-width="220">
        <template #default="{ row }">
          <div class="font-600">{{ row.userNickname || '-' }}</div>
          <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
          <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || '-' }}</div>
        </template>
      </el-table-column>
      <el-table-column label="申诉类型" align="center" prop="appealType" min-width="120">
        <template #default="{ row }">{{ formatAppealType(row.appealType) }}</template>
      </el-table-column>
      <el-table-column label="申诉内容" align="center" prop="content" min-width="220" />
      <el-table-column label="状态" align="center" prop="status" width="110">
        <template #default="{ row }">
          <dict-tag v-if="row.status" :type="DICT_TYPE.LB_APPEAL_STATUS" :value="row.status" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="审核状态" align="center" prop="auditStatus" width="110">
        <template #default="{ row }">
          <dict-tag v-if="row.auditStatus" :type="DICT_TYPE.LB_AUDIT_STATUS" :value="row.auditStatus" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="审核备注" align="center" prop="auditRemark" min-width="160" />
      <el-table-column label="驳回原因" align="center" prop="rejectReason" min-width="160" />
      <el-table-column label="审核人" align="center" prop="auditBy" width="100" />
      <el-table-column label="审核时间" align="center" prop="auditTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" align="center" fixed="right" width="170">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row.id)">详情</el-button>
          <el-button
            v-if="row.auditStatus !== 'APPROVED' && row.status !== 'REJECTED'"
            link
            type="primary"
            v-hasPermi="['linbang:review:appeal:audit']"
            @click="openAuditDialog(row)"
          >
            审核
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

  <Dialog v-model="detailVisible" title="申诉详情" width="920px">
    <el-descriptions v-loading="detailLoading" :column="2" border>
      <el-descriptions-item label="申诉单号">{{ detailData?.appealNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="订单号">{{ detailData?.order?.orderNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="单元号">{{ detailData?.unit?.unitNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="申诉类型">{{ formatAppealType(detailData?.appealType) }}</el-descriptions-item>
      <el-descriptions-item label="流程状态">
        <dict-tag v-if="detailData?.status" :type="DICT_TYPE.LB_APPEAL_STATUS" :value="detailData.status" />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="审核状态">
        <dict-tag v-if="detailData?.auditStatus" :type="DICT_TYPE.LB_AUDIT_STATUS" :value="detailData.auditStatus" />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="审核时间">{{ formatDate(detailData?.auditTime) }}</el-descriptions-item>
      <el-descriptions-item label="申诉内容" :span="2">{{ detailData?.content || '-' }}</el-descriptions-item>
      <el-descriptions-item label="审核备注" :span="2">{{ detailData?.auditRemark || '-' }}</el-descriptions-item>
      <el-descriptions-item label="驳回原因" :span="2">{{ detailData?.rejectReason || '-' }}</el-descriptions-item>
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
          <div class="text-14px text-[var(--el-text-color-secondary)]">申诉服务商</div>
          <div class="mt-8px text-16px font-600">{{ detailData?.merchant?.merchantName || '-' }}</div>
          <div class="mt-6px text-13px">{{ detailData?.merchant?.contactName || '-' }}</div>
          <div class="mt-6px text-[var(--el-text-color-secondary)]">状态：{{ formatEnableStatus(detailData?.merchant?.status) }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-descriptions :column="2" border>
      <el-descriptions-item label="申诉人">
        {{ detailData?.user?.nickname || '-' }} / {{ detailData?.user?.mobile || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="服务商联系人">
        {{ detailData?.merchant?.contactName || '-' }} / {{ detailData?.merchant?.contactMobile || '-' }}
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
        {{ detailData?.merchant?.creditScore ?? '-' }} / {{ detailData?.merchant?.creditLevel || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="服务范围说明">{{ detailData?.merchant?.serviceScopeDesc || '-' }}</el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">统计概览</el-divider>
    <el-row :gutter="12" class="mb-16px">
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">附件数 / 同单申诉数</div>
          <div class="mt-8px text-18px font-600">
            {{ detailData?.summary?.attachmentCount ?? 0 }} / {{ detailData?.summary?.sameOrderAppealCount ?? 0 }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">待审核 / 处理中</div>
          <div class="mt-8px text-18px font-600">
            {{ detailData?.summary?.pendingAuditCount ?? 0 }} / {{ detailData?.summary?.processingCount ?? 0 }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">通过 / 驳回 / 完结</div>
          <div class="mt-8px text-18px font-600">
            {{ detailData?.summary?.approvedCount ?? 0 }} / {{ detailData?.summary?.rejectedCount ?? 0 }} /
            {{ detailData?.summary?.finishedCount ?? 0 }}
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

    <el-divider content-position="left">关联申诉记录</el-divider>
    <el-table
      v-if="detailData?.relatedAppeals?.length"
      :data="detailData.relatedAppeals"
      size="small"
      border
      max-height="240"
    >
      <el-table-column label="申诉单号" prop="appealNo" min-width="150" />
      <el-table-column label="类型" prop="appealType" width="120">
        <template #default="{ row }">{{ formatAppealType(row.appealType) }}</template>
      </el-table-column>
      <el-table-column label="流程状态" prop="status" width="110">
        <template #default="{ row }">
          <dict-tag v-if="row.status" :type="DICT_TYPE.LB_APPEAL_STATUS" :value="row.status" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="审核状态" prop="auditStatus" width="110">
        <template #default="{ row }">
          <dict-tag v-if="row.auditStatus" :type="DICT_TYPE.LB_AUDIT_STATUS" :value="row.auditStatus" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="审核备注" prop="auditRemark" min-width="180" />
      <el-table-column label="审核时间" prop="auditTime" width="180">
        <template #default="{ row }">{{ formatDate(row.auditTime) }}</template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180">
        <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无关联申诉记录" :image-size="80" />

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
          {{ formatAppealStatus(row.beforeStatus) }} -> {{ formatAppealStatus(row.afterStatus) }}
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="180" />
      <el-table-column label="操作时间" prop="operateTime" width="180">
        <template #default="{ row }">{{ formatDate(row.operateTime) }}</template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无操作日志" :image-size="80" />
  </Dialog>

  <Dialog v-model="auditDialogVisible" title="申诉审核" width="520px">
    <el-form
      ref="auditFormRef"
      :model="auditFormData"
      :rules="auditFormRules"
      label-width="88px"
      v-loading="auditLoading"
    >
      <el-form-item label="申诉单号">
        <el-input :model-value="currentRow?.appealNo" disabled />
      </el-form-item>
      <el-form-item label="审核结果" prop="auditStatus">
        <el-radio-group v-model="auditFormData.auditStatus">
          <el-radio value="APPROVED">通过</el-radio>
          <el-radio value="REJECTED">驳回</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="审核备注" prop="auditRemark">
        <el-input
          v-model="auditFormData.auditRemark"
          type="textarea"
          :rows="3"
          maxlength="255"
          show-word-limit
          placeholder="请输入审核备注"
        />
      </el-form-item>
      <el-form-item v-if="auditFormData.auditStatus === 'REJECTED'" label="驳回原因" prop="rejectReason">
        <el-input
          v-model="auditFormData.rejectReason"
          type="textarea"
          :rows="3"
          maxlength="255"
          show-word-limit
          placeholder="请输入驳回原因"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="auditDialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="auditLoading" @click="submitAudit">提交审核</el-button>
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
import { AppealApi, type Appeal, type AppealAuditReqVO, type AppealDetail } from '@/api/linbang/appeal'
import {
  formatAppealStatus,
  formatAppealType,
  formatEnableStatus,
  formatOperateRole,
  formatOperateType,
  formatOrderStatus,
  formatOrderUnitStatus
} from '../utils/display'
import { formatFileBrief, loadFilesByIds, type FileLookupMap } from '../shared/file-display'

defineOptions({ name: 'Appeal' })

const message = useMessage()
const loading = ref(false)
const exportLoading = ref(false)
const detailVisible = ref(false)
const detailLoading = ref(false)
const list = ref<Appeal[]>([])
const detailData = ref<AppealDetail>()
const fileMap = reactive<FileLookupMap>({})
const total = ref(0)
const queryFormRef = ref<FormInstance>()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  appealNo: undefined as string | undefined,
  orderNo: undefined as string | undefined,
  unitNo: undefined as string | undefined,
  userKeyword: undefined as string | undefined,
  status: undefined as string | undefined,
  auditStatus: undefined as string | undefined,
  createTime: [] as string[]
})

const formatOrderUserSummary = (order?: AppealDetail['order']) => {
  const summary = [order?.userNickname, order?.userMobile, order?.userNo].filter(Boolean).join(' / ')
  return summary || (order?.userId ? '用户信息缺失' : '-')
}

const getList = async () => {
  loading.value = true
  try {
    const data = await AppealApi.getAppealPage(queryParams)
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
    const data = await AppealApi.exportAppeal(queryParams)
    download.excel(data, '申诉审核列表.xls')
  } finally {
    exportLoading.value = false
  }
}

const openDetail = async (id: number) => {
  detailVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await AppealApi.getAppeal(id)
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

const auditDialogVisible = ref(false)
const auditLoading = ref(false)
const auditFormRef = ref<FormInstance>()
const currentRow = ref<Appeal>()
const auditFormData = reactive<AppealAuditReqVO>({
  id: 0,
  auditStatus: 'APPROVED',
  auditRemark: '',
  rejectReason: ''
})
const auditFormRules = reactive<FormRules>({
  auditStatus: [{ required: true, message: '请选择审核结果', trigger: 'change' }],
  rejectReason: [
    {
      validator: (_rule, value, callback) => {
        if (auditFormData.auditStatus === 'REJECTED' && !value) {
          callback(new Error('请输入驳回原因'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ]
})

const openAuditDialog = (row: Appeal) => {
  currentRow.value = row
  auditFormData.id = row.id
  auditFormData.auditStatus = row.auditStatus === 'REJECTED' ? 'REJECTED' : 'APPROVED'
  auditFormData.auditRemark = row.auditRemark || ''
  auditFormData.rejectReason = row.rejectReason || ''
  auditDialogVisible.value = true
}

const submitAudit = async () => {
  await auditFormRef.value?.validate()
  try {
    await message.confirm('确认提交申诉审核结果？')
    auditLoading.value = true
    await AppealApi.auditAppeal({
      id: auditFormData.id,
      auditStatus: auditFormData.auditStatus,
      auditRemark: auditFormData.auditRemark,
      rejectReason: auditFormData.auditStatus === 'REJECTED' ? auditFormData.rejectReason : ''
    })
    message.success('申诉审核成功')
    auditDialogVisible.value = false
    await getList()
  } finally {
    auditLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
