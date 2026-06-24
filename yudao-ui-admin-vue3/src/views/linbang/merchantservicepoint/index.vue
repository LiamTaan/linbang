<template>
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :model="queryParams"
      :inline="true"
      label-width="80px"
      class="-mb-15px"
    >
      <el-form-item label="服务点名称" prop="pointName">
        <el-input
          v-model="queryParams.pointName"
          placeholder="请输入服务点名称"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="区域" prop="district">
        <el-input
          v-model="queryParams.district"
          placeholder="请输入区/县"
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
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['linbang:merchant:service-point:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['linbang:merchant:service-point:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="isEmpty(checkedIds)"
          @click="handleDeleteBatch"
          v-hasPermi="['linbang:merchant:service-point:delete']"
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
      <el-table-column label="服务商" align="center" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.merchantName || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.contactName || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.contactMobile || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="服务点名称" align="center" prop="pointName" min-width="160" />
      <el-table-column label="省市区" align="center" min-width="180">
        <template #default="{ row }">
          {{ [row.province, row.city, row.district].filter(Boolean).join(' / ') || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="详细地址" align="center" prop="detailAddress" min-width="220" />
      <el-table-column label="服务半径(km)" align="center" prop="serviceRadiusKm" width="130" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'ENABLE' ? 'success' : 'info'">
            {{ formatEnableStatus(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="180"
      />
      <el-table-column label="操作" align="center" fixed="right" min-width="120">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row.id)">详情</el-button>
          <el-button
            link
            type="primary"
            @click="openForm('update', row.id)"
            v-hasPermi="['linbang:merchant:service-point:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(row.id)"
            v-hasPermi="['linbang:merchant:service-point:delete']"
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

  <MerchantServicePointForm ref="formRef" @success="getList" />

  <Dialog v-model="detailVisible" title="服务点详情" width="900px">
    <el-descriptions v-loading="detailLoading" :column="2" border>
      <el-descriptions-item label="服务商">{{ formatMerchantDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="服务点名称">{{ detailData?.pointName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        {{ formatEnableStatus(detailData?.status) }}
      </el-descriptions-item>
      <el-descriptions-item label="省市区" :span="2">
        {{ [detailData?.province, detailData?.city, detailData?.district].filter(Boolean).join(' / ') || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="街道">{{ detailData?.street || '-' }}</el-descriptions-item>
      <el-descriptions-item label="服务半径(km)">{{ detailData?.serviceRadiusKm ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="详细地址" :span="2">{{ detailData?.detailAddress || '-' }}</el-descriptions-item>
      <el-descriptions-item label="经纬度" :span="2">
        {{ detailData?.longitude ?? '-' }} / {{ detailData?.latitude ?? '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDate(detailData?.createTime) }}</el-descriptions-item>
      <el-descriptions-item label="更新时间">{{ formatDate(detailData?.updateTime) }}</el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">服务商信息</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="服务商名称">
        {{ detailData?.merchant?.merchantName || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="联系人">
        {{ detailData?.merchant?.contactName || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="联系电话">
        {{ detailData?.merchant?.contactMobile || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="服务商状态">
        {{ formatEnableStatus(detailData?.merchant?.status) }}
      </el-descriptions-item>
      <el-descriptions-item label="接单状态">
        {{ formatAcceptStatus(detailData?.merchant?.acceptStatus) }}
      </el-descriptions-item>
      <el-descriptions-item label="信用分 / 等级">
        {{ detailData?.merchant?.creditScore ?? '-' }} / {{ detailData?.merchant?.creditLevel || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="最近入驻单号">
        {{ detailData?.merchant?.latestEntryNo || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="最近入驻状态">
        <dict-tag
          v-if="detailData?.merchant?.latestEntryStatus"
          :type="DICT_TYPE.LB_MERCHANT_ENTRY_STATUS"
          :value="detailData.merchant.latestEntryStatus"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="最近入驻区域" :span="2">
        {{ detailData?.merchant?.latestRegionCode || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="服务范围说明" :span="2">
        {{ detailData?.merchant?.serviceScopeDesc || '-' }}
      </el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">统计概览</el-divider>
    <el-row :gutter="12" class="mb-16px">
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">服务点总数</div>
          <div class="mt-8px text-24px font-600">{{ detailData?.summary?.servicePointCount ?? 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">启用 / 停用</div>
          <div class="mt-8px text-18px font-600">
            {{ detailData?.summary?.enabledServicePointCount ?? 0 }} /
            {{ detailData?.summary?.disabledServicePointCount ?? 0 }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">同区县 / 同城市 / 服务类目</div>
          <div class="mt-8px text-18px font-600">
            {{ detailData?.summary?.sameDistrictPointCount ?? 0 }} /
            {{ detailData?.summary?.sameCityPointCount ?? 0 }} /
            {{ detailData?.summary?.categoryCount ?? 0 }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-divider content-position="left">服务类目</el-divider>
    <el-table v-if="detailData?.categories?.length" :data="detailData.categories" size="small" border max-height="240">
      <el-table-column label="类目" prop="categoryName" min-width="160" />
      <el-table-column label="层级" prop="categoryLevel" width="80" />
      <el-table-column label="默认计价模式" prop="defaultPricingMode" width="140">
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
        <template #default="{ row }">
          {{ formatBooleanYesNo(row.supportSplit) }}
        </template>
      </el-table-column>
      <el-table-column label="支持发票" prop="supportInvoice" width="100">
        <template #default="{ row }">
          {{ formatBooleanYesNo(row.supportInvoice) }}
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无服务类目" :image-size="80" />

    <el-divider content-position="left">同服务商其他服务点</el-divider>
    <el-table
      v-if="detailData?.relatedPoints?.length"
      :data="detailData.relatedPoints"
      size="small"
      border
      max-height="260"
    >
      <el-table-column label="ID" prop="id" width="90" />
      <el-table-column label="名称" prop="pointName" min-width="150" />
      <el-table-column label="区域" min-width="180">
        <template #default="{ row }">
          {{ [row.province, row.city, row.district].filter(Boolean).join(' / ') || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="详细地址" prop="detailAddress" min-width="180" />
      <el-table-column label="服务半径(km)" prop="serviceRadiusKm" width="120" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          {{ formatEnableStatus(row.status) }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无其他服务点" :image-size="80" />
  </Dialog>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { isEmpty } from '@/utils/is'
import { dateFormatter, formatDate } from '@/utils/formatTime'
import download from '@/utils/download'
import { DICT_TYPE } from '@/utils/dict'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
import {
  MerchantServicePointApi,
  type MerchantServicePoint,
  type MerchantServicePointDetail
} from '@/api/linbang/merchantservicepoint'
import {
  ENABLE_STATUS_OPTIONS,
  formatAcceptStatus,
  formatBooleanYesNo,
  formatEnableStatus
} from '../utils/display'
import MerchantServicePointForm from './MerchantServicePointForm.vue'

defineOptions({ name: 'MerchantServicePoint' })

const message = useMessage()
const { t } = useI18n()

const loading = ref(true)
const exportLoading = ref(false)
const detailVisible = ref(false)
const detailLoading = ref(false)
const list = ref<MerchantServicePoint[]>([])
const detailData = ref<MerchantServicePointDetail>()
const total = ref(0)
const checkedIds = ref<number[]>([])
const queryFormRef = ref()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  merchantId: undefined as number | undefined,
  pointName: undefined as string | undefined,
  district: undefined as string | undefined,
  status: undefined as string | undefined,
  createTime: [] as string[]
})

const formatMerchantDisplay = () => {
  const merchant = detailData.value?.merchant
  return [merchant?.merchantName, merchant?.contactName, merchant?.contactMobile].filter(Boolean).join(' / ')
    || (detailData.value?.merchantId ? '服务商信息缺失' : '-')
}

const getList = async () => {
  loading.value = true
  try {
    const data = await MerchantServicePointApi.getMerchantServicePointPage(queryParams)
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

const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const openDetail = async (id?: number) => {
  if (!id) {
    return
  }
  detailVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await MerchantServicePointApi.getMerchantServicePoint(id)
  } finally {
    detailLoading.value = false
  }
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await MerchantServicePointApi.deleteMerchantServicePoint(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const handleRowCheckboxChange = (rows: MerchantServicePoint[]) => {
  checkedIds.value = rows.map((row) => row.id!).filter(Boolean)
}

const handleDeleteBatch = async () => {
  try {
    await message.delConfirm()
    await MerchantServicePointApi.deleteMerchantServicePointList(checkedIds.value)
    message.success(t('common.delSuccess'))
    checkedIds.value = []
    await getList()
  } catch {}
}

const handleExport = async () => {
  try {
    await message.exportConfirm()
    exportLoading.value = true
    const data = await MerchantServicePointApi.exportMerchantServicePoint(queryParams)
    download.excel(data, '服务点列表.xls')
  } finally {
    exportLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
