<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="用户" prop="userKeyword">
        <el-input
          v-model="queryParams.userKeyword"
          placeholder="请输入用户编号 / 昵称 / 手机号"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="入驻单号" prop="entryNo">
        <el-input
          v-model="queryParams.entryNo"
          placeholder="请输入入驻单号"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="区域编码" prop="regionCode">
        <el-input
          v-model="queryParams.regionCode"
          placeholder="请输入区域编码"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="初审状态" prop="firstAuditStatus">
        <el-select
          v-model="queryParams.firstAuditStatus"
          placeholder="请选择初审状态"
          clearable
          class="!w-220px"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_AUDIT_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="终审状态" prop="finalAuditStatus">
        <el-select
          v-model="queryParams.finalAuditStatus"
          placeholder="请选择终审状态"
          clearable
          class="!w-220px"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_AUDIT_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="流程状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择流程状态"
          clearable
          class="!w-220px"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_MERCHANT_ENTRY_STATUS)"
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
        <el-button @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" /> 搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" /> 重置
        </el-button>
        <el-button
          type="success"
          plain
          :loading="exportLoading"
          v-hasPermi="['linbang:merchant-entry:export']"
          @click="handleExport"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="服务商" align="center" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.merchantName || (row.merchantId ? '服务商信息缺失' : '-') }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.merchantContactName || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.merchantContactMobile || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="申请用户" align="center" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.userNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || formatIdFallback(row.userId) }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="入驻单号" align="center" prop="entryNo" min-width="180" />
      <el-table-column label="区域编码" align="center" prop="regionCode" width="120" />
      <el-table-column label="初审状态" align="center" prop="firstAuditStatus" width="110">
        <template #default="{ row }">
          <dict-tag v-if="row.firstAuditStatus" :type="DICT_TYPE.LB_AUDIT_STATUS" :value="row.firstAuditStatus" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="初审人" align="center" prop="firstAuditBy" width="100" />
      <el-table-column label="初审时间" align="center" prop="firstAuditTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="终审状态" align="center" prop="finalAuditStatus" width="110">
        <template #default="{ row }">
          <dict-tag v-if="row.finalAuditStatus" :type="DICT_TYPE.LB_AUDIT_STATUS" :value="row.finalAuditStatus" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="终审人" align="center" prop="finalAuditBy" width="100" />
      <el-table-column label="终审时间" align="center" prop="finalAuditTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="流程状态" align="center" prop="status" width="120">
        <template #default="{ row }">
          <dict-tag v-if="row.status" :type="DICT_TYPE.LB_MERCHANT_ENTRY_STATUS" :value="row.status" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" min-width="180" />
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" align="center" fixed="right" width="120">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row.id)">详情</el-button>
          <el-button
            v-if="getAuditActionLabel(row)"
            link
            type="primary"
            v-hasPermi="['linbang:merchant:entry:audit']"
            @click="openAuditDialog(row)"
          >
            {{ getAuditActionLabel(row) }}
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

  <Dialog v-model="detailVisible" title="服务商入驻详情" width="920px">
    <el-descriptions v-loading="detailLoading" :column="2" border>
      <el-descriptions-item label="入驻单号">{{ detailData?.entryNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="申请用户">{{ formatDetailUserDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="服务商">{{ formatDetailMerchantDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="区域编码">{{ detailData?.regionCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="流程状态">
        <dict-tag v-if="detailData?.status" :type="DICT_TYPE.LB_MERCHANT_ENTRY_STATUS" :value="detailData.status" />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="进度状态">{{ detailData?.progressStatus || '-' }}</el-descriptions-item>
      <el-descriptions-item label="当前阶段">{{ detailData?.currentStageName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="阶段时间">{{ formatDate(detailData?.currentStageTime) }}</el-descriptions-item>
      <el-descriptions-item label="初审状态">
        <dict-tag
          v-if="detailData?.firstAuditStatus"
          :type="DICT_TYPE.LB_AUDIT_STATUS"
          :value="detailData.firstAuditStatus"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="终审状态">
        <dict-tag
          v-if="detailData?.finalAuditStatus"
          :type="DICT_TYPE.LB_AUDIT_STATUS"
          :value="detailData.finalAuditStatus"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="初审时间">{{ formatDate(detailData?.firstAuditTime) }}</el-descriptions-item>
      <el-descriptions-item label="终审时间">{{ formatDate(detailData?.finalAuditTime) }}</el-descriptions-item>
      <el-descriptions-item label="接单权限">{{ detailData?.acceptEnabled ? '已开通' : '未开通' }}</el-descriptions-item>
      <el-descriptions-item label="绑卡阻塞">{{ detailData?.bankCardRequired ? '需要先绑卡' : '否' }}</el-descriptions-item>
      <el-descriptions-item label="阻塞原因" :span="2">{{ detailData?.onboardingBlockedReason || '-' }}</el-descriptions-item>
      <el-descriptions-item label="驳回原因" :span="2">{{ detailData?.rejectReason || '-' }}</el-descriptions-item>
      <el-descriptions-item label="备注" :span="2">{{ detailData?.remark || '-' }}</el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">申请人信息</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="用户编号">{{ detailData?.applicant?.userNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="昵称">{{ detailData?.applicant?.nickname || '-' }}</el-descriptions-item>
      <el-descriptions-item label="手机号">{{ detailData?.applicant?.mobile || '-' }}</el-descriptions-item>
      <el-descriptions-item label="当前角色">
        <dict-tag v-if="detailData?.applicant?.currentRoleCode" :type="DICT_TYPE.LB_ROLE_CODE" :value="detailData.applicant.currentRoleCode" />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="用户状态">{{ formatEnableStatus(detailData?.applicant?.status) }}</el-descriptions-item>
      <el-descriptions-item label="最近登录时间">{{ formatDate(detailData?.applicant?.lastLoginTime) }}</el-descriptions-item>
      <el-descriptions-item label="最近登录 IP" :span="2">{{ detailData?.applicant?.lastLoginIp || '-' }}</el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">服务商信息</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="服务商名称">{{ detailData?.merchant?.merchantName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="联系人">{{ detailData?.merchant?.contactName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="联系电话">{{ detailData?.merchant?.contactMobile || '-' }}</el-descriptions-item>
      <el-descriptions-item label="服务商状态">{{ formatEnableStatus(detailData?.merchant?.status) }}</el-descriptions-item>
      <el-descriptions-item label="接单状态">{{ formatAcceptStatus(detailData?.merchant?.acceptStatus) }}</el-descriptions-item>
      <el-descriptions-item label="信用分 / 等级">
        {{ detailData?.merchant?.creditScore ?? '-' }} / {{ detailData?.merchant?.creditLevel || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="服务范围说明" :span="2">
        {{ detailData?.merchant?.serviceScopeDesc || '-' }}
      </el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">实名与统计</el-divider>
    <el-row :gutter="12" class="mb-16px">
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">历史入驻数</div>
          <div class="mt-8px text-24px font-600">{{ detailData?.summary?.historyEntryCount ?? 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">已通过 / 已驳回</div>
          <div class="mt-8px text-18px font-600">
            {{ detailData?.summary?.approvedEntryCount ?? 0 }} / {{ detailData?.summary?.rejectedEntryCount ?? 0 }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">类目 / 资质 / 已通过资质</div>
          <div class="mt-8px text-18px font-600">
            {{ detailData?.summary?.categoryCount ?? 0 }} / {{ detailData?.summary?.qualificationCount ?? 0 }} /
            {{ detailData?.summary?.approvedQualificationCount ?? 0 }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-descriptions :column="2" border>
      <el-descriptions-item label="实名姓名">{{ detailData?.realName?.realName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="实名状态">
        <dict-tag
          v-if="detailData?.realName?.auditStatus"
          :type="DICT_TYPE.LB_AUDIT_STATUS"
          :value="detailData.realName.auditStatus"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="身份证号">{{ detailData?.realName?.idCardNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="实名审核时间">{{ formatDate(detailData?.realName?.auditTime) }}</el-descriptions-item>
      <el-descriptions-item label="营业执照已上传">{{ detailData?.summary?.businessLicenseUploaded ? '是' : '否' }}</el-descriptions-item>
      <el-descriptions-item label="保险已上传">{{ detailData?.summary?.insuranceUploaded ? '是' : '否' }}</el-descriptions-item>
      <el-descriptions-item label="实名审核备注" :span="2">
        {{ detailData?.realName?.auditRemark || detailData?.realName?.rejectReason || '-' }}
      </el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">服务类目</el-divider>
    <el-table v-if="detailData?.categories?.length" :data="detailData.categories" size="small" border max-height="220">
      <el-table-column label="类目" min-width="180">
        <template #default="{ row }">
          {{ row.categoryName || (row.categoryId ? '类目信息缺失' : '-') }}
        </template>
      </el-table-column>
      <el-table-column label="层级" prop="categoryLevel" width="80" />
      <el-table-column label="默认计价" prop="defaultPricingMode" width="120">
        <template #default="{ row }">
          <dict-tag
            v-if="row.defaultPricingMode"
            :type="DICT_TYPE.LB_PRICING_MODE"
            :value="row.defaultPricingMode"
          />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="支持拆分" prop="supportSplit" width="100">
        <template #default="{ row }">{{ formatBooleanYesNo(row.supportSplit) }}</template>
      </el-table-column>
      <el-table-column label="支持发票" prop="supportInvoice" width="100">
        <template #default="{ row }">{{ formatBooleanYesNo(row.supportInvoice) }}</template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无服务类目" :image-size="80" />

    <el-divider content-position="left">资质列表</el-divider>
    <el-table
      v-if="detailData?.qualifications?.length"
      :data="detailData.qualifications"
      size="small"
      border
      max-height="240"
    >
      <el-table-column label="ID" prop="id" width="90" />
      <el-table-column label="资质类型" prop="qualificationType" width="120">
        <template #default="{ row }">
          <dict-tag
            v-if="row.qualificationType"
            :type="DICT_TYPE.LB_QUALIFICATION_TYPE"
            :value="row.qualificationType"
          />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="资质名称" prop="qualificationName" min-width="160" />
      <el-table-column label="资质编号" prop="qualificationNo" width="160" />
      <el-table-column label="审核状态" prop="auditStatus" width="110">
        <template #default="{ row }">
          <dict-tag v-if="row.auditStatus" :type="DICT_TYPE.LB_AUDIT_STATUS" :value="row.auditStatus" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="有效期" min-width="180">
        <template #default="{ row }">
          {{ row.validStartDate || '-' }} ~ {{ row.validEndDate || '-' }}
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无资质" :image-size="80" />

    <el-divider content-position="left">历史入驻记录</el-divider>
    <el-table
      v-if="detailData?.historyEntries?.length"
      :data="detailData.historyEntries"
      size="small"
      border
      max-height="240"
    >
      <el-table-column label="ID" prop="id" width="90" />
      <el-table-column label="入驻单号" prop="entryNo" min-width="160" />
      <el-table-column label="区域编码" prop="regionCode" width="120" />
      <el-table-column label="初审" width="100">
        <template #default="{ row }">
          <dict-tag v-if="row.firstAuditStatus" :type="DICT_TYPE.LB_AUDIT_STATUS" :value="row.firstAuditStatus" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="终审" width="100">
        <template #default="{ row }">
          <dict-tag v-if="row.finalAuditStatus" :type="DICT_TYPE.LB_AUDIT_STATUS" :value="row.finalAuditStatus" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="流程状态" width="120">
        <template #default="{ row }">
          <dict-tag v-if="row.status" :type="DICT_TYPE.LB_MERCHANT_ENTRY_STATUS" :value="row.status" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="160" />
      <el-table-column label="创建时间" prop="createTime" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无历史入驻记录" :image-size="80" />
  </Dialog>

  <Dialog v-model="auditDialogVisible" title="服务商入驻审核" width="560px">
    <el-form
      ref="auditFormRef"
      :model="auditFormData"
      :rules="auditFormRules"
      label-width="88px"
      v-loading="auditLoading"
    >
      <el-form-item label="入驻单号">
        <el-input :model-value="currentRow?.entryNo" disabled />
      </el-form-item>
      <el-form-item label="服务商">
        <el-input :model-value="formatRowMerchantDisplay(currentRow)" disabled />
      </el-form-item>
      <el-form-item label="申请用户">
        <el-input :model-value="formatRowUserDisplay(currentRow)" disabled />
      </el-form-item>
      <el-form-item label="审核动作" prop="auditStatus">
        <el-radio-group v-model="auditFormData.auditStatus">
          <el-radio v-for="option in currentAuditOptions" :key="option.value" :value="option.value">
            {{ option.label }}
          </el-radio>
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
import { computed, onMounted, reactive, ref } from 'vue'
import { getStrDictOptions, DICT_TYPE } from '@/utils/dict'
import { dateFormatter, formatDate } from '@/utils/formatTime'
import download from '@/utils/download'
import { useMessage } from '@/hooks/web/useMessage'
import {
  MerchantEntryApi,
  type MerchantEntry,
  type MerchantEntryDetail,
  type MerchantEntryAuditReqVO
} from '@/api/linbang/merchantentry'
import { requestDynamicKeyToken } from '../shared/dynamic-key'
import { formatAcceptStatus, formatBooleanYesNo, formatEnableStatus } from '../utils/display'

defineOptions({ name: 'MerchantEntry' })

const message = useMessage()
const loading = ref(false)
const exportLoading = ref(false)
const detailVisible = ref(false)
const detailLoading = ref(false)
const list = ref<MerchantEntry[]>([])
const detailData = ref<MerchantEntryDetail>()
const total = ref(0)
const queryFormRef = ref<FormInstance>()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  merchantId: undefined as number | undefined,
  userKeyword: undefined as string | undefined,
  entryNo: undefined as string | undefined,
  regionCode: undefined as string | undefined,
  firstAuditStatus: undefined as string | undefined,
  finalAuditStatus: undefined as string | undefined,
  status: undefined as string | undefined,
  createTime: [] as string[]
})

const getList = async () => {
  loading.value = true
  try {
    const data = await MerchantEntryApi.getMerchantEntryPage(queryParams)
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
    const data = await MerchantEntryApi.exportMerchantEntry(queryParams)
    download.excel(data, '服务商入驻审核列表.xls')
  } finally {
    exportLoading.value = false
  }
}

const formatIdFallback = (userId?: number) => {
  return userId ? '用户信息缺失' : '-'
}

const formatRowUserDisplay = (row?: Pick<MerchantEntry, 'userNickname' | 'userMobile' | 'userNo' | 'userId'>) => {
  if (!row) {
    return '-'
  }
  const summary = [row.userNickname, row.userMobile, row.userNo].filter(Boolean).join(' / ')
  return summary || formatIdFallback(row.userId)
}

const formatDetailUserDisplay = () => {
  const user = detailData.value?.applicant
  const summary = [user?.nickname, user?.mobile, user?.userNo].filter(Boolean).join(' / ')
  return summary || formatIdFallback(detailData.value?.userId)
}

const formatRowMerchantDisplay = (
  row?: Pick<MerchantEntry, 'merchantName' | 'merchantContactName' | 'merchantContactMobile' | 'merchantId'>
) => {
  if (!row) {
    return '-'
  }
  const summary = [row.merchantName, row.merchantContactName, row.merchantContactMobile]
    .filter(Boolean)
    .join(' / ')
  return summary || (row.merchantId ? '服务商信息缺失' : '-')
}

const formatDetailMerchantDisplay = () => {
  const merchant = detailData.value?.merchant
  const summary = [merchant?.merchantName, merchant?.contactName, merchant?.contactMobile]
    .filter(Boolean)
    .join(' / ')
  return summary || (detailData.value?.merchantId ? '服务商信息缺失' : '-')
}

const getAuditActionLabel = (row: MerchantEntry) => {
  if (row.status === 'PENDING') return '初审'
  if (row.status === 'FIRST_APPROVED') return '终审'
  return ''
}

const openDetail = async (id: number) => {
  detailVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await MerchantEntryApi.getMerchantEntry(id)
  } finally {
    detailLoading.value = false
  }
}

const auditDialogVisible = ref(false)
const auditLoading = ref(false)
const auditFormRef = ref<FormInstance>()
const currentRow = ref<MerchantEntry>()
const auditFormData = reactive<MerchantEntryAuditReqVO>({
  id: 0,
  auditStatus: 'FIRST_APPROVED',
  auditRemark: '',
  rejectReason: ''
})

const currentAuditOptions = computed(() => {
  if (currentRow.value?.status === 'FIRST_APPROVED') {
    return [
      { label: '终审通过', value: 'APPROVED' as const },
      { label: '驳回', value: 'REJECTED' as const }
    ]
  }
  return [
    { label: '初审通过', value: 'FIRST_APPROVED' as const },
    { label: '驳回', value: 'REJECTED' as const }
  ]
})

const auditFormRules = reactive<FormRules>({
  auditStatus: [{ required: true, message: '请选择审核动作', trigger: 'change' }],
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

const openAuditDialog = (row: MerchantEntry) => {
  currentRow.value = row
  auditFormData.id = row.id
  auditFormData.auditStatus = row.status === 'FIRST_APPROVED' ? 'APPROVED' : 'FIRST_APPROVED'
  auditFormData.auditRemark = row.remark || ''
  auditFormData.rejectReason = ''
  auditDialogVisible.value = true
}

const submitAudit = async () => {
  await auditFormRef.value?.validate()
  try {
    await message.confirm('确认提交服务商入驻审核结果？')
    const verifyToken = await requestDynamicKeyToken('审核服务商入驻')
    auditLoading.value = true
    await MerchantEntryApi.auditMerchantEntry(
      {
        id: auditFormData.id,
        auditStatus: auditFormData.auditStatus,
        auditRemark: auditFormData.auditRemark,
        rejectReason: auditFormData.auditStatus === 'REJECTED' ? auditFormData.rejectReason : ''
      },
      verifyToken
    )
    message.success('服务商入驻审核成功')
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
