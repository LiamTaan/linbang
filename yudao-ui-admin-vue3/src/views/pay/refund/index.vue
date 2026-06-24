<template>
  <doc-alert title="退款审核与原路退款" url="https://doc.iocoder.cn/pay/refund-demo/" />

  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :model="queryParams"
      :inline="true"
      label-width="100px"
      class="-mb-15px"
    >
      <el-form-item label="支付应用" prop="appId">
        <el-select
          v-model="queryParams.appId"
          clearable
          placeholder="请选择支付应用"
          class="!w-220px"
        >
          <el-option v-for="item in appList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="商户支付单号" prop="merchantOrderId">
        <el-input
          v-model="queryParams.merchantOrderId"
          placeholder="请输入商户支付单号"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商户退款单号" prop="merchantRefundId">
        <el-input
          v-model="queryParams.merchantRefundId"
          placeholder="请输入商户退款单号"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="退款渠道" prop="channelCode">
        <el-select
          v-model="queryParams.channelCode"
          clearable
          placeholder="请选择退款渠道"
          class="!w-220px"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.PAY_CHANNEL_CODE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select
          v-model="queryParams.auditStatus"
          clearable
          placeholder="请选择审核状态"
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
      <el-form-item label="退款状态" prop="status">
        <el-select
          v-model="queryParams.status"
          clearable
          placeholder="请选择退款状态"
          class="!w-220px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.PAY_REFUND_STATUS)"
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
          v-hasPermi="['pay:refund:export', 'linbang:pay:refund:audit']"
          @click="handleExport"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="ID" align="center" prop="id" width="90" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" :formatter="dateFormatter" />
      <el-table-column label="退款单号" align="left" min-width="260">
        <template #default="{ row }">
          <p class="order-font"><el-tag size="small">商户</el-tag> {{ row.merchantRefundId || '-' }}</p>
          <p class="order-font"><el-tag size="small" type="warning">系统</el-tag> {{ row.no || '-' }}</p>
          <p class="order-font" v-if="row.channelRefundNo">
            <el-tag size="small" type="success">渠道</el-tag> {{ row.channelRefundNo }}
          </p>
        </template>
      </el-table-column>
      <el-table-column label="支付单号" align="left" min-width="260">
        <template #default="{ row }">
          <p class="order-font"><el-tag size="small">商户</el-tag> {{ row.merchantOrderId || '-' }}</p>
          <p class="order-font" v-if="row.channelOrderNo">
            <el-tag size="small" type="success">渠道</el-tag> {{ row.channelOrderNo }}
          </p>
        </template>
      </el-table-column>
      <el-table-column label="支付应用" align="center" prop="appName" min-width="120" />
      <el-table-column label="支付金额" align="center" prop="payPrice" width="110">
        <template #default="{ row }">￥{{ ((row.payPrice || 0) / 100).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="退款金额" align="center" prop="refundPrice" width="110">
        <template #default="{ row }">￥{{ ((row.refundPrice || 0) / 100).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="审核状态" align="center" prop="auditStatus" width="110">
        <template #default="{ row }">
          <dict-tag v-if="row.auditStatus" :type="DICT_TYPE.LB_AUDIT_STATUS" :value="row.auditStatus" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="退款状态" align="center" prop="status" width="110">
        <template #default="{ row }">
          <dict-tag v-if="row.status !== undefined" :type="DICT_TYPE.PAY_REFUND_STATUS" :value="row.status" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="退款渠道" align="center" width="130">
        <template #default="{ row }">
          <dict-tag v-if="row.channelCode" :type="DICT_TYPE.PAY_CHANNEL_CODE" :value="row.channelCode" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="退款原因" align="center" prop="reason" min-width="180" />
      <el-table-column label="审核备注" align="center" prop="auditRemark" min-width="160" />
      <el-table-column label="驳回原因" align="center" prop="rejectReason" min-width="160" />
      <el-table-column label="审核时间" align="center" prop="auditTime" width="180" :formatter="dateFormatter" />
      <el-table-column label="成功时间" align="center" prop="successTime" width="180" :formatter="dateFormatter" />
      <el-table-column label="操作" align="center" fixed="right" width="150">
        <template #default="{ row }">
          <el-button
            v-if="canAudit(row)"
            link
            type="primary"
            v-hasPermi="['linbang:pay:refund:audit']"
            @click="openAuditDialog(row)"
          >
            审核
          </el-button>
          <el-button
            link
            type="primary"
            v-hasPermi="['pay:refund:query', 'linbang:pay:refund:audit']"
            @click="openDetail(row.id!)"
          >
            详情
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

  <Dialog v-model="auditDialogVisible" title="退款审核" width="520px">
    <el-form
      ref="auditFormRef"
      :model="auditFormData"
      :rules="auditFormRules"
      label-width="88px"
      v-loading="auditLoading"
    >
      <el-form-item label="退款单号">
        <el-input :model-value="currentRow?.merchantRefundId || currentRow?.no || ''" disabled />
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

  <RefundDetail ref="detailRef" />
</template>

<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { DICT_TYPE, getIntDictOptions, getStrDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as RefundApi from '@/api/pay/refund'
import * as AppApi from '@/api/pay/app'
import { useMessage } from '@/hooks/web/useMessage'
import { requestDynamicKeyToken } from '@/views/linbang/shared/dynamic-key'
import RefundDetail from './RefundDetail.vue'
import download from '@/utils/download'

defineOptions({ name: 'PayRefund' })

const message = useMessage()

const loading = ref(false)
const exportLoading = ref(false)
const total = ref(0)
const list = ref<RefundApi.RefundDetailVO[]>([])
const queryFormRef = ref<FormInstance>()
const appList = ref<AppApi.AppVO[]>([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  appId: undefined as number | undefined,
  channelCode: undefined as string | undefined,
  merchantOrderId: undefined as string | undefined,
  merchantRefundId: undefined as string | undefined,
  status: undefined as number | undefined,
  auditStatus: undefined as string | undefined,
  channelOrderNo: undefined as string | undefined,
  channelRefundNo: undefined as string | undefined,
  createTime: [] as string[]
})

const getList = async () => {
  loading.value = true
  try {
    const data = await RefundApi.getRefundPage(queryParams)
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
    const data = await RefundApi.exportRefund(queryParams)
    download.excel(data, '退款审核列表.xls')
  } finally {
    exportLoading.value = false
  }
}

const canAudit = (row: RefundApi.RefundDetailVO) =>
  row.needAudit === true && row.auditStatus === 'PENDING'

const auditDialogVisible = ref(false)
const auditLoading = ref(false)
const auditFormRef = ref<FormInstance>()
const currentRow = ref<RefundApi.RefundDetailVO>()
const auditFormData = reactive<RefundApi.RefundAuditReqVO>({
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

const openAuditDialog = (row: RefundApi.RefundDetailVO) => {
  currentRow.value = row
  auditFormData.id = row.id || 0
  auditFormData.auditStatus = row.auditStatus === 'REJECTED' ? 'REJECTED' : 'APPROVED'
  auditFormData.auditRemark = row.auditRemark || ''
  auditFormData.rejectReason = row.rejectReason || ''
  auditDialogVisible.value = true
}

const submitAudit = async () => {
  await auditFormRef.value?.validate()
  try {
    await message.confirm('确认提交退款审核结果？审核通过后将触发原路退款。')
    const verifyToken = await requestDynamicKeyToken('审核退款申请')
    auditLoading.value = true
    await RefundApi.auditRefund(
      {
        id: auditFormData.id,
        auditStatus: auditFormData.auditStatus,
        auditRemark: auditFormData.auditRemark,
        rejectReason: auditFormData.auditStatus === 'REJECTED' ? auditFormData.rejectReason : ''
      },
      verifyToken
    )
    message.success('退款审核成功')
    auditDialogVisible.value = false
    await getList()
  } finally {
    auditLoading.value = false
  }
}

const detailRef = ref()
const openDetail = (id: number) => {
  detailRef.value.open(id)
}

onMounted(async () => {
  await getList()
  appList.value = await AppApi.getAppList()
})
</script>

<style scoped>
.order-font {
  padding: 2px 0;
  font-size: 12px;
}
</style>
