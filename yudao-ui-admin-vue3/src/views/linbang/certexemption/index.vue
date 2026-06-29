<template>
  <ContentWrap>
    <el-form :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="用户">
        <el-input v-model="queryParams.userKeyword" placeholder="请输入用户编号 / 昵称 / 手机号" class="!w-220px" clearable />
      </el-form-item>
      <el-form-item label="豁免类型">
        <el-input v-model="queryParams.exemptionType" placeholder="请输入豁免类型" class="!w-220px" clearable />
      </el-form-item>
      <el-form-item label="审核状态">
        <el-select v-model="queryParams.auditStatus" class="!w-220px" clearable placeholder="请选择审核状态">
          <el-option v-for="dict in getStrDictOptions(DICT_TYPE.LB_AUDIT_STATUS)" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true">
      <el-table-column label="用户" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.userNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="服务商" prop="merchantName" min-width="160" />
      <el-table-column label="资质" prop="qualificationName" min-width="160" />
      <el-table-column label="豁免类型" prop="exemptionType" width="160" />
      <el-table-column label="审核状态" prop="auditStatus" width="110">
        <template #default="{ row }">
          <dict-tag v-if="row.auditStatus" :type="DICT_TYPE.LB_AUDIT_STATUS" :value="row.auditStatus" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="生效时间" min-width="220">
        <template #default="{ row }">
          {{ formatDate(row.effectiveStartTime) }} ~ {{ formatDate(row.effectiveEndTime) }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row.id)">详情</el-button>
          <el-button v-if="row.auditStatus !== 'APPROVED'" link type="primary" @click="openAuditDialog(row)">审核</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </ContentWrap>

  <Dialog v-model="detailVisible" title="证件豁免详情" width="900px">
    <el-descriptions v-loading="detailLoading" :column="2" border>
      <el-descriptions-item label="用户">{{ [detailData?.user?.nickname, detailData?.user?.mobile, detailData?.user?.userNo].filter(Boolean).join(' / ') || '-' }}</el-descriptions-item>
      <el-descriptions-item label="服务商">{{ detailData?.merchant?.merchantName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="资质">{{ detailData?.qualification?.qualificationName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="豁免类型">{{ detailData?.exemptionType || '-' }}</el-descriptions-item>
      <el-descriptions-item label="审核状态">
        <dict-tag v-if="detailData?.auditStatus" :type="DICT_TYPE.LB_AUDIT_STATUS" :value="detailData.auditStatus" />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="审核时间">{{ formatDate(detailData?.auditTime) }}</el-descriptions-item>
      <el-descriptions-item label="申请原因" :span="2">{{ detailData?.reason || '-' }}</el-descriptions-item>
      <el-descriptions-item label="审核备注" :span="2">{{ detailData?.auditRemark || '-' }}</el-descriptions-item>
      <el-descriptions-item label="驳回原因" :span="2">{{ detailData?.rejectReason || '-' }}</el-descriptions-item>
    </el-descriptions>
  </Dialog>

  <Dialog v-model="auditVisible" title="证件豁免审核" width="520px">
    <el-form ref="auditFormRef" :model="auditFormData" :rules="auditFormRules" label-width="88px">
      <el-form-item label="审核结果" prop="auditStatus">
        <el-radio-group v-model="auditFormData.auditStatus">
          <el-radio value="APPROVED">通过</el-radio>
          <el-radio value="REJECTED">驳回</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="审核备注" prop="auditRemark">
        <el-input v-model="auditFormData.auditRemark" type="textarea" :rows="3" />
      </el-form-item>
      <el-form-item v-if="auditFormData.auditStatus === 'REJECTED'" label="驳回原因" prop="rejectReason">
        <el-input v-model="auditFormData.rejectReason" type="textarea" :rows="3" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="auditVisible = false">取消</el-button>
      <el-button type="primary" @click="submitAudit">提交</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { dateFormatter, formatDate } from '@/utils/formatTime'
import { DICT_TYPE, getStrDictOptions } from '@/utils/dict'
import { useMessage } from '@/hooks/web/useMessage'
import { CertExemptionApi, type CertExemption, type CertExemptionAuditReqVO, type CertExemptionDetail } from '@/api/linbang/certexemption'

defineOptions({ name: 'LinbangCertExemption' })

const message = useMessage()
const loading = ref(false)
const detailLoading = ref(false)
const detailVisible = ref(false)
const auditVisible = ref(false)
const total = ref(0)
const list = ref<CertExemption[]>([])
const detailData = ref<CertExemptionDetail>()
const currentRow = ref<CertExemption>()
const auditFormRef = ref<FormInstance>()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userKeyword: undefined as string | undefined,
  exemptionType: undefined as string | undefined,
  auditStatus: undefined as string | undefined
})
const auditFormData = reactive<CertExemptionAuditReqVO>({
  id: 0,
  auditStatus: 'APPROVED',
  auditRemark: '',
  rejectReason: ''
})
const auditFormRules = reactive<FormRules>({
  auditStatus: [{ required: true, message: '请选择审核结果', trigger: 'change' }]
})

const getList = async () => {
  loading.value = true
  try {
    const data = await CertExemptionApi.getCertExemptionPage(queryParams)
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

const openDetail = async (id: number) => {
  detailVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await CertExemptionApi.getCertExemption(id)
  } finally {
    detailLoading.value = false
  }
}

const openAuditDialog = (row: CertExemption) => {
  currentRow.value = row
  auditFormData.id = row.id
  auditFormData.auditStatus = row.auditStatus === 'REJECTED' ? 'REJECTED' : 'APPROVED'
  auditFormData.auditRemark = row.auditRemark || ''
  auditFormData.rejectReason = row.rejectReason || ''
  auditVisible.value = true
}

const submitAudit = async () => {
  await auditFormRef.value?.validate()
  await CertExemptionApi.auditCertExemption({
    id: auditFormData.id,
    auditStatus: auditFormData.auditStatus,
    auditRemark: auditFormData.auditRemark,
    rejectReason: auditFormData.auditStatus === 'REJECTED' ? auditFormData.rejectReason : ''
  })
  message.success('证件豁免审核成功')
  auditVisible.value = false
  await getList()
}

onMounted(() => getList())
</script>
