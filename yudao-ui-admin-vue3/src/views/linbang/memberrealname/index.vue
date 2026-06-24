<template>
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :model="queryParams"
      :inline="true"
      label-width="88px"
      class="-mb-15px"
    >
      <el-form-item label="用户" prop="userKeyword">
        <el-input
          v-model="queryParams.userKeyword"
          placeholder="请输入用户编号 / 昵称 / 手机号"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="真实姓名" prop="realName">
        <el-input
          v-model="queryParams.realName"
          placeholder="请输入真实姓名"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="身份证号" prop="idCardNo">
        <el-input
          v-model="queryParams.idCardNo"
          placeholder="请输入身份证号"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select
          v-model="queryParams.auditStatus"
          placeholder="请选择审核状态"
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
          v-hasPermi="['linbang:member-user-real-name:export']"
          @click="handleExport"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="用户" align="center" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.userNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="真实姓名" align="center" prop="realName" width="120" />
      <el-table-column label="身份证号" align="center" prop="idCardNo" min-width="180" />
      <el-table-column label="证件附件" align="center" min-width="260">
        <template #default="{ row }">
          <div class="leading-20px">
            <div>{{ formatFileDisplay('正面', row.idCardFrontFileId) }}</div>
            <div>{{ formatFileDisplay('反面', row.idCardBackFileId) }}</div>
            <div>{{ formatFileDisplay('手持', row.holdCardFileId) }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="活体结果" align="center" prop="livenessResult" min-width="120">
        <template #default="{ row }">
          {{ formatVerifyResult(row.livenessResult) }}
        </template>
      </el-table-column>
      <el-table-column label="人脸核验结果" align="center" prop="faceVerifyResult" min-width="120">
        <template #default="{ row }">
          {{ formatVerifyResult(row.faceVerifyResult) }}
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
            v-if="canAudit(row)"
            link
            type="primary"
            v-hasPermi="['linbang:member:real-name:audit']"
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

  <Dialog v-model="detailVisible" title="实名认证详情" width="920px">
    <el-descriptions v-loading="detailLoading" :column="2" border>
      <el-descriptions-item label="用户">{{ formatDetailUserDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="真实姓名">{{ detailData?.realName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="身份证号">{{ detailData?.idCardNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="审核状态">
        <dict-tag v-if="detailData?.auditStatus" :type="DICT_TYPE.LB_AUDIT_STATUS" :value="detailData.auditStatus" />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="审核时间">{{ formatDate(detailData?.auditTime) }}</el-descriptions-item>
      <el-descriptions-item label="身份证正面">
        {{ formatFileDisplay('正面', detailData?.idCardFrontFileId).replace('正面：', '') }}
      </el-descriptions-item>
      <el-descriptions-item label="身份证反面">
        {{ formatFileDisplay('反面', detailData?.idCardBackFileId).replace('反面：', '') }}
      </el-descriptions-item>
      <el-descriptions-item label="手持证件照">
        {{ formatFileDisplay('手持', detailData?.holdCardFileId).replace('手持：', '') }}
      </el-descriptions-item>
      <el-descriptions-item label="活体结果">{{ formatVerifyResult(detailData?.livenessResult) }}</el-descriptions-item>
      <el-descriptions-item label="人脸核验结果">{{ formatVerifyResult(detailData?.faceVerifyResult) }}</el-descriptions-item>
      <el-descriptions-item label="审核备注" :span="2">{{ detailData?.auditRemark || '-' }}</el-descriptions-item>
      <el-descriptions-item label="驳回原因" :span="2">{{ detailData?.rejectReason || '-' }}</el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">用户与服务商上下文</el-divider>
    <el-row :gutter="12" class="mb-16px">
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">用户信息</div>
          <div class="mt-8px text-16px font-600">{{ detailData?.user?.nickname || '-' }}</div>
          <div class="mt-6px text-13px">{{ detailData?.user?.mobile || '-' }}</div>
          <div class="mt-6px text-[var(--el-text-color-secondary)]">
            角色：
            <dict-tag v-if="detailData?.user?.currentRoleCode" :type="DICT_TYPE.LB_ROLE_CODE" :value="detailData.user.currentRoleCode" />
            <span v-else>-</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">服务商信息</div>
          <div class="mt-8px text-16px font-600">{{ detailData?.merchant?.merchantName || '-' }}</div>
          <div class="mt-6px text-13px">{{ detailData?.merchant?.contactName || '-' }}</div>
          <div class="mt-6px text-[var(--el-text-color-secondary)]">
            状态：{{ formatEnableStatus(detailData?.merchant?.status) }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">最近入驻申请</div>
          <div class="mt-8px text-16px font-600">{{ detailData?.latestEntry?.entryNo || '-' }}</div>
          <div class="mt-6px text-13px">{{ detailData?.latestEntry?.regionCode || '-' }}</div>
          <div class="mt-6px text-[var(--el-text-color-secondary)]">
            状态：
            <dict-tag v-if="detailData?.latestEntry?.status" :type="DICT_TYPE.LB_MERCHANT_ENTRY_STATUS" :value="detailData.latestEntry.status" />
            <span v-else>-</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-descriptions :column="2" border>
      <el-descriptions-item label="用户编号">{{ detailData?.user?.userNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="最近登录时间">{{ formatDate(detailData?.user?.lastLoginTime) }}</el-descriptions-item>
      <el-descriptions-item label="服务商联系人">
        {{ detailData?.merchant?.contactMobile || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="信用分 / 等级">
        {{ detailData?.merchant?.creditScore ?? '-' }} / {{ detailData?.merchant?.creditLevel || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="服务范围说明" :span="2">
        {{ detailData?.merchant?.serviceScopeDesc || '-' }}
      </el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">统计概览</el-divider>
    <el-row :gutter="12" class="mb-16px">
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">资质总数 / 已通过</div>
          <div class="mt-8px text-18px font-600">
            {{ detailData?.summary?.qualificationCount ?? 0 }} / {{ detailData?.summary?.approvedQualificationCount ?? 0 }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">已驳回 / 信用记录数</div>
          <div class="mt-8px text-18px font-600">
            {{ detailData?.summary?.rejectedQualificationCount ?? 0 }} / {{ detailData?.summary?.creditRecordCount ?? 0 }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">最新信用分 / 等级</div>
          <div class="mt-8px text-18px font-600">
            {{ detailData?.summary?.latestCreditScore ?? '-' }} / {{ detailData?.summary?.latestCreditLevel || '-' }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-divider content-position="left">关联资质</el-divider>
    <el-table
      v-if="detailData?.qualifications?.length"
      :data="detailData.qualifications"
      size="small"
      border
      max-height="240"
    >
      <el-table-column label="资质类型" prop="qualificationType" width="120" />
      <el-table-column label="资质名称" prop="qualificationName" min-width="160" />
      <el-table-column label="资质编号" prop="qualificationNo" width="160" />
      <el-table-column label="审核状态" prop="auditStatus" width="110">
        <template #default="{ row }">
          <dict-tag v-if="row.auditStatus" :type="DICT_TYPE.LB_AUDIT_STATUS" :value="row.auditStatus" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="有效期" min-width="180">
        <template #default="{ row }">{{ row.validStartDate || '-' }} ~ {{ row.validEndDate || '-' }}</template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无关联资质" :image-size="80" />

    <el-divider content-position="left">信用记录</el-divider>
    <el-table
      v-if="detailData?.creditRecords?.length"
      :data="detailData.creditRecords"
      size="small"
      border
      max-height="240"
    >
      <el-table-column label="ID" prop="id" width="90" />
      <el-table-column label="规则编码" prop="ruleCode" width="140" />
      <el-table-column label="规则名称" prop="ruleName" min-width="160" />
      <el-table-column label="分值变动" prop="scoreChange" width="100" />
      <el-table-column label="变动后分值" prop="afterScore" width="110" />
      <el-table-column label="触发类型" prop="triggerType" width="120">
        <template #default="{ row }">
          {{ formatTriggerType(row.triggerType) }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180">
        <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无信用记录" :image-size="80" />
  </Dialog>

  <Dialog v-model="auditDialogVisible" title="实名认证审核" width="520px">
    <el-form
      ref="auditFormRef"
      :model="auditFormData"
      :rules="auditFormRules"
      label-width="88px"
      v-loading="auditLoading"
    >
      <el-form-item label="用户">
        <el-input :model-value="formatUserDisplay(currentRow)" disabled />
      </el-form-item>
      <el-form-item label="真实姓名">
        <el-input :model-value="currentRow?.realName" disabled />
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
import { formatEnableStatus, formatTriggerType, formatVerifyResult } from '../utils/display'
import {
  MemberUserRealNameApi,
  type MemberUserRealNameDetail,
  type MemberUserRealName,
  type MemberUserRealNameAuditReqVO
} from '@/api/linbang/memberrealname'

defineOptions({ name: 'MemberUserRealName' })

const message = useMessage()
const loading = ref(false)
const exportLoading = ref(false)
const detailVisible = ref(false)
const detailLoading = ref(false)
const list = ref<MemberUserRealName[]>([])
const detailData = ref<MemberUserRealNameDetail>()
const total = ref(0)
const queryFormRef = ref<FormInstance>()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userKeyword: undefined as string | undefined,
  realName: undefined as string | undefined,
  idCardNo: undefined as string | undefined,
  auditStatus: undefined as string | undefined,
  createTime: [] as string[]
})

const getList = async () => {
  loading.value = true
  try {
    const data = await MemberUserRealNameApi.getMemberUserRealNamePage(queryParams)
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
    const data = await MemberUserRealNameApi.exportMemberUserRealName(queryParams)
    download.excel(data, '实名认证审核列表.xls')
  } finally {
    exportLoading.value = false
  }
}

const openDetail = async (id: number) => {
  detailVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await MemberUserRealNameApi.getMemberUserRealName(id)
  } finally {
    detailLoading.value = false
  }
}

const canAudit = (row: MemberUserRealName) => row.auditStatus !== 'APPROVED'

const formatUserDisplay = (row?: Pick<MemberUserRealName, 'userNickname' | 'userMobile' | 'userNo' | 'userId'>) => {
  if (!row) {
    return '-'
  }
  const summary = [row.userNickname, row.userMobile, row.userNo].filter(Boolean).join(' / ')
  return summary || (row.userId ? '用户信息缺失' : '-')
}

const formatDetailUserDisplay = () => {
  const user = detailData.value?.user
  const summary = [user?.nickname, user?.mobile, user?.userNo].filter(Boolean).join(' / ')
  return summary || (detailData.value?.userId ? '用户信息缺失' : '-')
}

const formatFileDisplay = (label: string, fileId?: number) => {
  if (!fileId) {
    return `${label}：-`
  }
  return `${label}：已上传`
}

const auditDialogVisible = ref(false)
const auditLoading = ref(false)
const auditFormRef = ref<FormInstance>()
const currentRow = ref<MemberUserRealName>()
const auditFormData = reactive<MemberUserRealNameAuditReqVO>({
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

const openAuditDialog = (row: MemberUserRealName) => {
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
    await message.confirm('确认提交实名认证审核结果？')
    auditLoading.value = true
    await MemberUserRealNameApi.auditMemberUserRealName({
      id: auditFormData.id,
      auditStatus: auditFormData.auditStatus,
      auditRemark: auditFormData.auditRemark,
      rejectReason: auditFormData.auditStatus === 'REJECTED' ? auditFormData.rejectReason : ''
    })
    message.success('实名认证审核成功')
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
