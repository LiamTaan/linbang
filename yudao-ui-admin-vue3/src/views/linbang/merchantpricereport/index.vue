<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="区域编码" prop="regionCode">
        <el-input
          v-model="queryParams.regionCode"
          placeholder="请输入区域编码"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申报状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择申报状态" clearable class="!w-220px">
          <el-option
            v-for="item in PRICE_REPORT_STATUS_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select v-model="queryParams.auditStatus" placeholder="请选择审核状态" clearable class="!w-220px">
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_PRICE_REPORT_AUDIT_STATUS)"
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
      <el-table-column label="服务商" align="center" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.merchantName || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.merchantContactName || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.merchantContactMobile || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="合作商" align="center" prop="partnerName" min-width="160" />
      <el-table-column label="类目" align="center" prop="categoryName" min-width="160" />
      <el-table-column label="区域编码" align="center" prop="regionCode" width="120" />
      <el-table-column label="建议价格" align="center" prop="suggestedPrice" width="120" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="{ row }">
          <el-tag :type="getPriceReportStatusTagType(row.status)">
            {{ formatPriceReportStatus(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="审核状态" align="center" prop="auditStatus" width="110">
        <template #default="{ row }">
          <dict-tag
            v-if="row.auditStatus"
            :type="DICT_TYPE.LB_PRICE_REPORT_AUDIT_STATUS"
            :value="row.auditStatus"
          />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" min-width="200" />
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" align="center" fixed="right" width="140">
        <template #default="{ row }">
          <el-button
            link
            type="primary"
            v-hasPermi="['linbang:partner:price-report:query']"
            @click="openDetail(row.id)"
          >
            详情
          </el-button>
          <el-button
            v-if="row.auditStatus === 'PENDING'"
            link
            type="primary"
            v-hasPermi="['linbang:partner:price-report:audit']"
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

  <MerchantPriceReportDetailDialog ref="detailDialogRef" />

  <Dialog v-model="auditDialogVisible" title="价格申报审核" width="520px">
    <el-form ref="auditFormRef" :model="auditFormData" :rules="auditFormRules" label-width="88px" v-loading="auditLoading">
      <el-form-item label="服务商">
        <el-input :model-value="formatMerchantDisplay(currentRow)" disabled />
      </el-form-item>
      <el-form-item label="建议价格">
        <el-input :model-value="currentRow?.suggestedPrice ?? ''" disabled />
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
import { DICT_TYPE, getStrDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import { useMessage } from '@/hooks/web/useMessage'
import {
  MerchantPriceReportApi,
  type MerchantPriceReport,
  type MerchantPriceReportAuditReqVO
} from '@/api/linbang/merchantpricereport'
import { requestDynamicKeyToken } from '../shared/dynamic-key'
import {
  formatPriceReportStatus,
  getPriceReportStatusTagType,
  PRICE_REPORT_STATUS_OPTIONS
} from '../utils/display'
import MerchantPriceReportDetailDialog from './MerchantPriceReportDetailDialog.vue'

defineOptions({ name: 'MerchantPriceReport' })

const message = useMessage()
const loading = ref(false)
const list = ref<MerchantPriceReport[]>([])
const total = ref(0)
const queryFormRef = ref<FormInstance>()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  merchantId: undefined as number | undefined,
  partnerId: undefined as number | undefined,
  categoryId: undefined as number | undefined,
  regionCode: undefined as string | undefined,
  status: undefined as string | undefined,
  auditStatus: undefined as string | undefined,
  createTime: [] as string[]
})

const getList = async () => {
  loading.value = true
  try {
    const data = await MerchantPriceReportApi.getMerchantPriceReportPage(queryParams)
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

const detailDialogRef = ref()
const openDetail = (id: number) => {
  detailDialogRef.value.open(id)
}

const formatMerchantDisplay = (
  row?: Pick<MerchantPriceReport, 'merchantName' | 'merchantContactName' | 'merchantContactMobile' | 'merchantId'>
) => {
  if (!row) {
    return '-'
  }
  const summary = [row.merchantName, row.merchantContactName, row.merchantContactMobile].filter(Boolean).join(' / ')
  return summary || (row.merchantId ? '服务商信息缺失' : '-')
}

const auditDialogVisible = ref(false)
const auditLoading = ref(false)
const auditFormRef = ref<FormInstance>()
const currentRow = ref<MerchantPriceReport>()
const auditFormData = reactive<MerchantPriceReportAuditReqVO>({
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

const openAuditDialog = (row: MerchantPriceReport) => {
  currentRow.value = row
  auditFormData.id = row.id
  auditFormData.auditStatus = 'APPROVED'
  auditFormData.auditRemark = row.auditRemark || ''
  auditFormData.rejectReason = row.rejectReason || ''
  auditDialogVisible.value = true
}

const submitAudit = async () => {
  await auditFormRef.value?.validate()
  try {
    await message.confirm('确认提交价格申报审核结果？')
    const verifyToken = await requestDynamicKeyToken('审核价格申报')
    auditLoading.value = true
    await MerchantPriceReportApi.auditMerchantPriceReport(
      {
        id: auditFormData.id,
        auditStatus: auditFormData.auditStatus,
        auditRemark: auditFormData.auditRemark,
        rejectReason: auditFormData.auditStatus === 'REJECTED' ? auditFormData.rejectReason : ''
      },
      verifyToken
    )
    message.success('价格申报审核成功')
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
