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
      <el-form-item label="申请角色" prop="applyRoleCode">
        <el-select v-model="queryParams.applyRoleCode" placeholder="请选择申请角色" clearable class="!w-220px">
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_ROLE_CODE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select v-model="queryParams.auditStatus" placeholder="请选择审核状态" clearable class="!w-220px">
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_ROLE_APPLY_STATUS)"
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
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="申请用户" align="center" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.userNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="申请角色" align="center" prop="applyRoleCode" width="120">
        <template #default="{ row }">
          <dict-tag v-if="row.applyRoleCode" :type="DICT_TYPE.LB_ROLE_CODE" :value="row.applyRoleCode" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="申请说明" align="center" prop="applyReason" min-width="200" />
      <el-table-column label="资源说明" align="center" prop="resourceDesc" min-width="200" />
      <el-table-column label="预期转化" align="center" prop="expectedConversionDesc" min-width="180" />
      <el-table-column label="能力说明" align="center" prop="abilityDesc" min-width="180" />
      <el-table-column label="可投入时间" align="center" prop="availableTimeDesc" min-width="160" />
      <el-table-column label="审核状态" align="center" prop="auditStatus" width="110">
        <template #default="{ row }">
          <dict-tag v-if="row.auditStatus" :type="DICT_TYPE.LB_ROLE_APPLY_STATUS" :value="row.auditStatus" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="审核备注" align="center" prop="auditRemark" min-width="160" />
      <el-table-column label="驳回原因" align="center" prop="rejectReason" min-width="160" />
      <el-table-column label="审核时间" align="center" prop="auditTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" align="center" fixed="right" width="120">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row.id)">详情</el-button>
          <el-button
            v-if="row.auditStatus === 'PENDING'"
            link
            type="primary"
            v-hasPermi="['linbang:member:role-apply:audit']"
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

  <Dialog v-model="detailVisible" title="身份申请详情" width="860px">
    <el-descriptions v-loading="detailLoading" :column="2" border>
      <el-descriptions-item label="申请角色">
        <dict-tag
          v-if="detailData?.applyRoleCode"
          :type="DICT_TYPE.LB_ROLE_CODE"
          :value="detailData.applyRoleCode"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="审核状态">
        <dict-tag
          v-if="detailData?.auditStatus"
          :type="DICT_TYPE.LB_ROLE_APPLY_STATUS"
          :value="detailData.auditStatus"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="申请说明" :span="2">{{ detailData?.applyReason || '-' }}</el-descriptions-item>
      <el-descriptions-item label="资源说明" :span="2">{{ detailData?.resourceDesc || '-' }}</el-descriptions-item>
      <el-descriptions-item label="预期转化说明" :span="2">{{ detailData?.expectedConversionDesc || '-' }}</el-descriptions-item>
      <el-descriptions-item label="能力说明" :span="2">{{ detailData?.abilityDesc || '-' }}</el-descriptions-item>
      <el-descriptions-item label="可投入时间说明" :span="2">{{ detailData?.availableTimeDesc || '-' }}</el-descriptions-item>
      <el-descriptions-item label="审核备注" :span="2">{{ detailData?.auditRemark || '-' }}</el-descriptions-item>
      <el-descriptions-item label="驳回原因" :span="2">{{ detailData?.rejectReason || '-' }}</el-descriptions-item>
      <el-descriptions-item label="审核时间">{{ formatDate(detailData?.auditTime) }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDate(detailData?.createTime) }}</el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">用户信息</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="用户编号">{{ detailData?.user?.userNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="昵称">{{ detailData?.user?.nickname || '-' }}</el-descriptions-item>
      <el-descriptions-item label="手机号">{{ detailData?.user?.mobile || '-' }}</el-descriptions-item>
      <el-descriptions-item label="当前角色">
        <dict-tag
          v-if="detailData?.user?.currentRoleCode"
          :type="DICT_TYPE.LB_ROLE_CODE"
          :value="detailData.user.currentRoleCode"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="用户状态">{{ formatEnableStatus(detailData?.user?.status) }}</el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">实名与资质</el-divider>
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
      <el-descriptions-item label="最新资质">
        {{ detailData?.latestQualification?.qualificationName || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="资质类型">
        <dict-tag
          v-if="detailData?.latestQualification?.qualificationType"
          :type="DICT_TYPE.LB_QUALIFICATION_TYPE"
          :value="detailData.latestQualification.qualificationType"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="资质到期日">
        {{ detailData?.latestQualification?.validEndDate || '-' }}
      </el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">推广员上下文</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="推广员档案">{{ detailData?.promoter?.id || '-' }}</el-descriptions-item>
      <el-descriptions-item label="邀请码">{{ detailData?.promoter?.inviteCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="等级">{{ detailData?.promoter?.levelCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ formatEnableStatus(detailData?.promoter?.status) }}</el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">角色申请重点</el-divider>
    <el-descriptions :column="1" border>
      <el-descriptions-item label="审核参考">
        {{ formatRoleApplyFocus(detailData) }}
      </el-descriptions-item>
    </el-descriptions>
  </Dialog>

  <Dialog v-model="auditDialogVisible" title="身份申请审核" width="520px">
    <el-form ref="auditFormRef" :model="auditFormData" :rules="auditFormRules" label-width="88px" v-loading="auditLoading">
      <el-form-item label="申请用户">
        <el-input :model-value="formatUserDisplay(currentRow)" disabled />
      </el-form-item>
      <el-form-item label="申请角色">
        <el-input :model-value="formatRoleDisplay(currentRow?.applyRoleCode)" disabled />
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
import { DICT_TYPE, getDictLabel, getStrDictOptions } from '@/utils/dict'
import { dateFormatter, formatDate } from '@/utils/formatTime'
import { useMessage } from '@/hooks/web/useMessage'
import {
  MemberRoleApplyApi,
  type MemberRoleApply,
  type MemberRoleApplyAuditReqVO,
  type MemberRoleApplyDetail
} from '@/api/linbang/memberroleapply'
import { requestDynamicKeyToken } from '../shared/dynamic-key'
import { formatEnableStatus } from '../utils/display'

defineOptions({ name: 'MemberRoleApply' })

const message = useMessage()
const loading = ref(false)
const detailVisible = ref(false)
const detailLoading = ref(false)
const list = ref<MemberRoleApply[]>([])
const detailData = ref<MemberRoleApplyDetail>()
const total = ref(0)
const queryFormRef = ref<FormInstance>()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userKeyword: undefined as string | undefined,
  applyRoleCode: undefined as string | undefined,
  auditStatus: undefined as string | undefined,
  createTime: [] as string[]
})

const getList = async () => {
  loading.value = true
  try {
    const data = await MemberRoleApplyApi.getMemberRoleApplyPage(queryParams)
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
    detailData.value = await MemberRoleApplyApi.getMemberRoleApply(id)
  } finally {
    detailLoading.value = false
  }
}

const formatUserDisplay = (
  row?: Pick<MemberRoleApply, 'userNickname' | 'userMobile' | 'userNo' | 'userId'>
) => {
  if (!row) {
    return '-'
  }
  const summary = [row.userNickname, row.userMobile, row.userNo].filter(Boolean).join(' / ')
  return summary || (row.userId ? '用户信息缺失' : '-')
}

const formatRoleDisplay = (value?: string) => getDictLabel(DICT_TYPE.LB_ROLE_CODE, value) || '-'

const formatRoleApplyFocus = (detail?: MemberRoleApplyDetail) => {
  if (!detail?.applyRoleCode) {
    return '-'
  }
  if (detail.applyRoleCode === 'PROMOTER') {
    return detail.expectedConversionDesc || detail.resourceDesc || '重点核对推广资源和预期转化能力'
  }
  if (detail.applyRoleCode === 'PARTNER') {
    return detail.resourceDesc || '重点核对代理资质与本地资源覆盖情况'
  }
  if (detail.applyRoleCode === 'PLATFORM_OPERATOR') {
    return [detail.abilityDesc, detail.availableTimeDesc].filter(Boolean).join(' / ') || '重点核对平台运营能力与可投入时间'
  }
  return [detail.resourceDesc, detail.expectedConversionDesc, detail.abilityDesc, detail.availableTimeDesc]
    .filter(Boolean)
    .join(' / ') || '-'
}

const auditDialogVisible = ref(false)
const auditLoading = ref(false)
const auditFormRef = ref<FormInstance>()
const currentRow = ref<MemberRoleApply>()
const auditFormData = reactive<MemberRoleApplyAuditReqVO>({
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

const openAuditDialog = (row: MemberRoleApply) => {
  currentRow.value = row
  auditFormData.id = row.id
  auditFormData.auditStatus = 'APPROVED'
  auditFormData.auditRemark = row.auditRemark || ''
  auditFormData.rejectReason = ''
  auditDialogVisible.value = true
}

const submitAudit = async () => {
  await auditFormRef.value?.validate()
  try {
    await message.confirm('确认提交身份申请审核结果？')
    const verifyToken = await requestDynamicKeyToken('审核身份申请')
    auditLoading.value = true
    await MemberRoleApplyApi.auditMemberRoleApply(
      {
        id: auditFormData.id,
        auditStatus: auditFormData.auditStatus,
        auditRemark: auditFormData.auditRemark,
        rejectReason: auditFormData.auditStatus === 'REJECTED' ? auditFormData.rejectReason : ''
      },
      verifyToken
    )
    message.success('身份申请审核成功')
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
