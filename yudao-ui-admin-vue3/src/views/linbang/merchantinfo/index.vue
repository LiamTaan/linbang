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
      <el-form-item label="服务商名称" prop="merchantName">
        <el-input
          v-model="queryParams.merchantName"
          placeholder="请输入服务商名称"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="联系人" prop="contactName">
        <el-input
          v-model="queryParams.contactName"
          placeholder="请输入联系人"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="联系人手机号" prop="contactMobile">
        <el-input
          v-model="queryParams.contactMobile"
          placeholder="请输入联系人手机号"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
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
      <el-form-item label="接单状态" prop="acceptStatus">
        <el-select v-model="queryParams.acceptStatus" placeholder="请选择接单状态" clearable class="!w-220px">
          <el-option
            v-for="item in ACCEPT_STATUS_OPTIONS"
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
          v-hasPermi="['linbang:merchant-info:create']"
          @click="openForm('create')"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          :loading="exportLoading"
          v-hasPermi="['linbang:merchant-info:export']"
          @click="handleExport"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="checkedIds.length === 0"
          v-hasPermi="['linbang:merchant-info:delete']"
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
      <el-table-column label="用户" align="center" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.userNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || formatIdFallback(row.userId) }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="服务商名称" align="center" prop="merchantName" min-width="180" />
      <el-table-column label="联系人" align="center" prop="contactName" width="120" />
      <el-table-column label="联系人手机号" align="center" prop="contactMobile" width="140" />
      <el-table-column label="服务范围说明" align="center" prop="serviceScopeDesc" min-width="220" />
      <el-table-column label="状态" align="center" prop="status" width="120">
        <template #default="{ row }">
          {{ formatEnableStatus(row.status) }}
        </template>
      </el-table-column>
      <el-table-column label="接单状态" align="center" prop="acceptStatus" width="120">
        <template #default="{ row }">
          {{ formatAcceptStatus(row.acceptStatus) }}
        </template>
      </el-table-column>
      <el-table-column label="信用分" align="center" prop="creditScore" width="100" />
      <el-table-column label="信用等级" align="center" prop="creditLevel" width="120">
        <template #default="{ row }">
          {{ formatCreditLevel(row.creditLevel) }}
        </template>
      </el-table-column>
      <el-table-column label="综合评分" align="center" prop="compositeScore" width="110" />
      <el-table-column label="好评率" align="center" width="110">
        <template #default="{ row }">
          {{ row.positiveRate !== undefined && row.positiveRate !== null ? `${row.positiveRate}%` : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="好评优先池" align="center" width="120">
        <template #default="{ row }">
          <el-tag :type="row.inPositivePriorityPool ? 'success' : 'info'">
            {{ formatBooleanYesNo(row.inPositivePriorityPool) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" align="center" fixed="right" width="190">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row.id)">详情</el-button>
          <el-button
            link
            type="primary"
            v-hasPermi="['linbang:merchant-info:update']"
            @click="openForm('update', row.id)"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            v-hasPermi="['linbang:merchant-info:delete']"
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

  <Dialog v-model="detailVisible" title="服务商详情" width="920px">
    <el-descriptions v-loading="detailLoading" :column="2" border>
      <el-descriptions-item label="用户">{{ formatDetailUserDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="服务商名称">{{ detailData?.merchantName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="联系人">{{ detailData?.contactName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="联系人手机号">{{ detailData?.contactMobile || '-' }}</el-descriptions-item>
      <el-descriptions-item label="服务商状态">{{ formatEnableStatus(detailData?.status) }}</el-descriptions-item>
      <el-descriptions-item label="接单状态">{{ formatAcceptStatus(detailData?.acceptStatus) }}</el-descriptions-item>
      <el-descriptions-item label="信用分 / 等级">
        {{ detailData?.creditScore ?? '-' }} / {{ formatCreditLevel(detailData?.creditLevel) }}
      </el-descriptions-item>
      <el-descriptions-item label="综合评分">{{ detailData?.compositeScore ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="好评率">
        {{ detailData?.positiveRate !== undefined && detailData?.positiveRate !== null ? `${detailData.positiveRate}%` : '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="好评优先池">
        {{ formatBooleanYesNo(detailData?.inPositivePriorityPool) }}
      </el-descriptions-item>
      <el-descriptions-item label="最近入驻单号">{{ detailData?.entryNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="最近入驻区域">{{ detailData?.regionCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="服务范围说明" :span="2">
        {{ detailData?.serviceScopeDesc || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDate(detailData?.createTime) }}</el-descriptions-item>
      <el-descriptions-item label="更新时间">{{ formatDate(detailData?.updateTime) }}</el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">入驻与经营概览</el-divider>
    <el-row :gutter="12" class="mb-16px">
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">最近入驻流程</div>
          <div class="mt-8px text-16px font-600">
            <dict-tag v-if="detailData?.entryStatus" :type="DICT_TYPE.LB_MERCHANT_ENTRY_STATUS" :value="detailData.entryStatus" />
            <span v-else>-</span>
          </div>
          <div class="mt-6px text-13px">
            初审：
            <dict-tag v-if="detailData?.firstAuditStatus" :type="DICT_TYPE.LB_AUDIT_STATUS" :value="detailData.firstAuditStatus" />
            <span v-else>-</span>
          </div>
          <div class="mt-6px text-13px">
            终审：
            <dict-tag v-if="detailData?.finalAuditStatus" :type="DICT_TYPE.LB_AUDIT_STATUS" :value="detailData.finalAuditStatus" />
            <span v-else>-</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">服务点总数</div>
          <div class="mt-8px text-24px font-600">{{ detailData?.servicePointCount ?? 0 }}</div>
          <div class="mt-6px text-[var(--el-text-color-secondary)]">
            启用中：{{ detailData?.enabledServicePointCount ?? 0 }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">服务类目数</div>
          <div class="mt-8px text-24px font-600">{{ detailData?.categories?.length ?? 0 }}</div>
          <div class="mt-6px text-[var(--el-text-color-secondary)]">接单状态：{{ formatAcceptStatus(detailData?.acceptStatus) }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-divider content-position="left">服务类目</el-divider>
    <el-table v-if="detailData?.categories?.length" :data="detailData.categories" size="small" border max-height="220">
      <el-table-column label="服务类目" min-width="180">
        <template #default="{ row }">
          {{ row.categoryName || (row.categoryId ? '类目信息缺失' : '-') }}
        </template>
      </el-table-column>
      <el-table-column label="层级" prop="categoryLevel" width="80" />
      <el-table-column label="默认计价方式" prop="defaultPricingMode" width="120">
        <template #default="{ row }">
          <dict-tag
            v-if="row.defaultPricingMode"
            :type="DICT_TYPE.LB_PRICING_MODE"
            :value="row.defaultPricingMode"
          />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="支持拆分" width="100">
        <template #default="{ row }">{{ formatBooleanYesNo(row.supportSplit) }}</template>
      </el-table-column>
      <el-table-column label="支持发票" width="100">
        <template #default="{ row }">{{ formatBooleanYesNo(row.supportInvoice) }}</template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无服务类目" :image-size="80" />

    <el-divider content-position="left">服务点列表</el-divider>
    <el-table
      v-if="detailData?.servicePoints?.length"
      :data="detailData.servicePoints"
      size="small"
      border
      max-height="260"
    >
      <el-table-column label="名称" prop="pointName" min-width="160" />
      <el-table-column label="省市区" min-width="220">
        <template #default="{ row }">
          {{ [row.province, row.city, row.district].filter(Boolean).join(' / ') || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="街道" prop="street" width="120" />
      <el-table-column label="详细地址" prop="detailAddress" min-width="220" />
      <el-table-column label="服务半径(km)" prop="serviceRadiusKm" width="120" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          {{ formatEnableStatus(row.status) }}
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无服务点" :image-size="80" />
  </Dialog>

  <MerchantInfoForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import type { FormInstance } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { DICT_TYPE } from '@/utils/dict'
import { dateFormatter, formatDate } from '@/utils/formatTime'
import download from '@/utils/download'
import { useMessage } from '@/hooks/web/useMessage'
import { MerchantInfoApi, type MerchantInfo, type MerchantInfoDetail } from '@/api/linbang/merchantinfo'
import {
  ACCEPT_STATUS_OPTIONS,
  ENABLE_STATUS_OPTIONS,
  formatAcceptStatus,
  formatBooleanYesNo,
  formatCreditLevel,
  formatEnableStatus
} from '../utils/display'
import MerchantInfoForm from './MerchantInfoForm.vue'

defineOptions({ name: 'MerchantInfo' })

const message = useMessage()
const loading = ref(false)
const exportLoading = ref(false)
const detailVisible = ref(false)
const detailLoading = ref(false)
const list = ref<MerchantInfo[]>([])
const detailData = ref<MerchantInfoDetail>()
const total = ref(0)
const queryFormRef = ref<FormInstance>()
const formRef = ref()
const checkedIds = ref<number[]>([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userKeyword: undefined as string | undefined,
  merchantName: undefined as string | undefined,
  contactName: undefined as string | undefined,
  contactMobile: undefined as string | undefined,
  status: undefined as string | undefined,
  acceptStatus: undefined as string | undefined,
  createTime: [] as string[]
})

const formatIdFallback = (userId?: number) => {
  return userId ? '用户信息缺失' : '-'
}

const formatDetailUserDisplay = () => {
  const summary = [detailData.value?.userNickname, detailData.value?.userMobile, detailData.value?.userNo]
    .filter(Boolean)
    .join(' / ')
  return summary || formatIdFallback(detailData.value?.userId)
}

const getList = async () => {
  loading.value = true
  try {
    const data = await MerchantInfoApi.getMerchantInfoPage(queryParams)
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
    detailData.value = await MerchantInfoApi.getMerchantInfo(id)
  } finally {
    detailLoading.value = false
  }
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await MerchantInfoApi.deleteMerchantInfo(id)
    message.success('删除成功')
    await getList()
  } catch {}
}

const handleDeleteBatch = async () => {
  try {
    await message.delConfirm()
    await MerchantInfoApi.deleteMerchantInfoList(checkedIds.value)
    checkedIds.value = []
    message.success('删除成功')
    await getList()
  } catch {}
}

const handleRowCheckboxChange = (records: MerchantInfo[]) => {
  checkedIds.value = records.map((item) => item.id)
}

const handleExport = async () => {
  try {
    await message.exportConfirm()
    exportLoading.value = true
    const data = await MerchantInfoApi.exportMerchantInfo(queryParams)
    download.excel(data, '服务商信息列表.xls')
  } finally {
    exportLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
