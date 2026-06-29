<template>
  <ContentWrap>
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="84px"
    >
      <el-form-item label="类目名称" prop="categoryName">
        <el-input
          v-model="queryParams.categoryName"
          placeholder="请输入类目名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="层级" prop="categoryLevel">
        <el-input
          v-model="queryParams.categoryLevel"
          placeholder="请输入层级"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="默认计价方式" prop="defaultPricingMode">
        <el-select
          v-model="queryParams.defaultPricingMode"
          placeholder="请选择默认计价方式"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_PRICING_MODE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="是否支持拆单" prop="supportSplit">
        <el-select
          v-model="queryParams.supportSplit"
          placeholder="请选择是否支持拆单"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="item in BOOLEAN_YES_NO_OPTIONS"
            :key="`split-${String(item.value)}`"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="是否支持开票" prop="supportInvoice">
        <el-select
          v-model="queryParams.supportInvoice"
          placeholder="请选择是否支持开票"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="item in BOOLEAN_YES_NO_OPTIONS"
            :key="`invoice-${String(item.value)}`"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="是否用工类" prop="laborCategoryFlag">
        <el-select
          v-model="queryParams.laborCategoryFlag"
          placeholder="请选择是否用工类"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="item in BOOLEAN_YES_NO_OPTIONS"
            :key="`labor-${String(item.value)}`"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="协议类型" prop="forceAgreementType">
        <el-select
          v-model="queryParams.forceAgreementType"
          placeholder="请选择协议类型"
          clearable
          class="!w-240px"
        >
          <el-option label="通用交易保障协议" value="TRADE_GUARANTEE" />
          <el-option label="工程托管协议" value="PROJECT_ESCROW" />
        </el-select>
      </el-form-item>
      <el-form-item label="风险等级" prop="riskLevel">
        <el-input
          v-model="queryParams.riskLevel"
          placeholder="请输入风险等级"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择状态"
          clearable
          class="!w-240px"
        >
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
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['linbang:merchant-service-category:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['linbang:merchant-service-category:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
        <el-button type="danger" plain @click="toggleExpandAll">
          <Icon icon="ep:sort" class="mr-5px" /> 展开/折叠
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table
      v-loading="loading"
      v-if="refreshTable"
      :data="list"
      row-key="id"
      :stripe="true"
      :show-overflow-tooltip="true"
      :default-expand-all="isExpandAll"
    >
      <el-table-column label="类目名称" align="left" prop="categoryName" min-width="220" />
      <el-table-column label="层级" align="center" prop="categoryLevel" width="90" />
      <el-table-column label="排序" align="center" prop="sortNo" width="90" />
      <el-table-column label="图标" align="center" prop="icon" min-width="140" />
      <el-table-column label="默认计价方式" align="center" prop="defaultPricingMode" width="140">
        <template #default="scope">
          <dict-tag
            v-if="scope.row.defaultPricingMode"
            :type="DICT_TYPE.LB_PRICING_MODE"
            :value="scope.row.defaultPricingMode"
          />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="是否支持拆单" align="center" prop="supportSplit" width="120">
        <template #default="scope">
          {{ formatBooleanYesNo(scope.row.supportSplit) }}
        </template>
      </el-table-column>
      <el-table-column label="支持计价方式" align="center" min-width="180">
        <template #default="{ row }">
          <span>{{ (row.supportedPricingModes || []).join(' / ') || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="是否支持开票" align="center" prop="supportInvoice" width="120">
        <template #default="scope">
          {{ formatBooleanYesNo(scope.row.supportInvoice) }}
        </template>
      </el-table-column>
      <el-table-column label="是否用工类" align="center" prop="laborCategoryFlag" width="120">
        <template #default="{ row }">
          {{ formatBooleanYesNo(row.laborCategoryFlag) }}
        </template>
      </el-table-column>
      <el-table-column label="协议类型" align="center" prop="forceAgreementType" width="150" />
      <el-table-column label="风险等级" align="center" prop="riskLevel" width="120">
        <template #default="{ row }">
          {{ formatRiskLevel(row.riskLevel) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="{ row }">
          <el-switch
            v-if="checkPermi(['linbang:merchant-service-category:update'])"
            v-model="row.status"
            active-value="ENABLE"
            inactive-value="DISABLE"
            :loading="statusUpdating[row.id!]"
            @change="(value) => handleStatusChanged(row, value)"
          />
          <span v-else>{{ formatEnableStatus(row.status) }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="更新时间"
        align="center"
        prop="updateTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column label="操作" align="center" min-width="240">
        <template #default="{ row }">
          <el-button
            link
            type="primary"
            @click="openForm('update', row.id)"
            v-hasPermi="['linbang:merchant-service-category:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="primary"
            @click="openForm('create', undefined, row.parentId ?? 0)"
            v-hasPermi="['linbang:merchant-service-category:create']"
          >
            新增同级
          </el-button>
          <el-button
            link
            type="primary"
            @click="openForm('create', undefined, row.id)"
            v-hasPermi="['linbang:merchant-service-category:create']"
          >
            新增子级
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(row.id!)"
            v-hasPermi="['linbang:merchant-service-category:delete']"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </ContentWrap>

  <MerchantServiceCategoryForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { nextTick, onMounted, reactive, ref } from 'vue'
import { getStrDictOptions, DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import { handleTree } from '@/utils/tree'
import download from '@/utils/download'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
import { MerchantServiceCategoryApi, MerchantServiceCategory } from '@/api/linbang/merchantcategory'
import { Icon } from '@/components/Icon'
import { checkPermi } from '@/utils/permission'
import {
  BOOLEAN_YES_NO_OPTIONS,
  ENABLE_STATUS_OPTIONS,
  formatBooleanYesNo,
  formatEnableStatus,
  formatRiskLevel
} from '../utils/display'
import MerchantServiceCategoryForm from './MerchantServiceCategoryForm.vue'

defineOptions({ name: 'MerchantServiceCategory' })

const message = useMessage()
const { t } = useI18n()

const loading = ref(true)
const list = ref<MerchantServiceCategory[]>([])
const queryParams = reactive({
  categoryName: undefined as string | undefined,
  categoryLevel: undefined as number | undefined,
  defaultPricingMode: undefined as string | undefined,
  supportSplit: undefined as boolean | undefined,
  supportInvoice: undefined as boolean | undefined,
  riskLevel: undefined as string | undefined,
  laborCategoryFlag: undefined as boolean | undefined,
  forceAgreementType: undefined as string | undefined,
  status: undefined as string | undefined,
  createTime: [] as string[]
})
const queryFormRef = ref()
const exportLoading = ref(false)
const refreshTable = ref(true)
const isExpandAll = ref(true)
const statusUpdating = ref<Record<number, boolean>>({})

const getList = async () => {
  loading.value = true
  try {
    const data = await MerchantServiceCategoryApi.getMerchantServiceCategoryList(queryParams)
    list.value = handleTree(data, 'id', 'parentId')
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  getList()
}

const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

const formRef = ref()
const openForm = (type: string, id?: number, parentId?: number) => {
  formRef.value.open(type, id, parentId)
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await MerchantServiceCategoryApi.deleteMerchantServiceCategory(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const handleExport = async () => {
  try {
    await message.exportConfirm()
    exportLoading.value = true
    const data = await MerchantServiceCategoryApi.exportMerchantServiceCategory({
      categoryName: queryParams.categoryName,
      categoryLevel: queryParams.categoryLevel,
      defaultPricingMode: queryParams.defaultPricingMode,
      supportSplit: queryParams.supportSplit,
      supportInvoice: queryParams.supportInvoice,
      riskLevel: queryParams.riskLevel,
      laborCategoryFlag: queryParams.laborCategoryFlag,
      forceAgreementType: queryParams.forceAgreementType,
      status: queryParams.status,
      createTime: queryParams.createTime
    })
    download.excel(data, '服务类目表.xls')
  } finally {
    exportLoading.value = false
  }
}

const handleStatusChanged = async (row: MerchantServiceCategory, value: string | number | boolean) => {
  const id = row.id
  if (!id) {
    return
  }
  const previousStatus = value === 'ENABLE' ? 'DISABLE' : 'ENABLE'
  statusUpdating.value[id] = true
  try {
    row.status = value as string
    await MerchantServiceCategoryApi.updateMerchantServiceCategory({
      id: row.id,
      parentId: row.parentId,
      categoryName: row.categoryName,
      categoryLevel: row.categoryLevel,
      sortNo: row.sortNo,
      icon: row.icon,
      defaultPricingMode: row.defaultPricingMode,
      supportedPricingModes: row.supportedPricingModes,
      supportSplit: row.supportSplit,
      supportInvoice: row.supportInvoice,
      riskLevel: row.riskLevel,
      laborCategoryFlag: row.laborCategoryFlag,
      forceAgreementType: row.forceAgreementType,
      invoiceRateReminderText: row.invoiceRateReminderText,
      status: row.status
    })
    message.success(t('common.updateSuccess'))
  } catch {
    row.status = previousStatus
  } finally {
    statusUpdating.value[id] = false
  }
}

const toggleExpandAll = async () => {
  refreshTable.value = false
  isExpandAll.value = !isExpandAll.value
  await nextTick()
  refreshTable.value = true
}

onMounted(() => {
  getList()
})
</script>
