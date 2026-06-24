<template>
  <ContentWrap>
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="84px"
    >
      <el-form-item label="上级类目" prop="parentName">
        <el-input
          v-model="queryParams.parentName"
          placeholder="请输入上级类目名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
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
        <el-button
          type="danger"
          plain
          :disabled="isEmpty(checkedIds)"
          @click="handleDeleteBatch"
          v-hasPermi="['linbang:merchant-service-category:delete']"
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
      <el-table-column label="上级类目" align="center" min-width="180">
        <template #default="{ row }">
          {{ formatParentCategoryName(row.parentId) }}
        </template>
      </el-table-column>
      <el-table-column label="类目名称" align="center" prop="categoryName" min-width="160" />
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
      <el-table-column label="是否支持开票" align="center" prop="supportInvoice" width="120">
        <template #default="scope">
          {{ formatBooleanYesNo(scope.row.supportInvoice) }}
        </template>
      </el-table-column>
      <el-table-column label="风险等级" align="center" prop="riskLevel" width="120">
        <template #default="{ row }">
          {{ formatRiskLevel(row.riskLevel) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          {{ formatEnableStatus(scope.row.status) }}
        </template>
      </el-table-column>
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column label="操作" align="center" min-width="120px">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['linbang:merchant-service-category:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['linbang:merchant-service-category:delete']"
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

  <MerchantServiceCategoryForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { isEmpty } from '@/utils/is'
import { getStrDictOptions, DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
import { MerchantServiceCategoryApi, MerchantServiceCategory } from '@/api/linbang/merchantcategory'
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
const allCategories = ref<MerchantServiceCategory[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  parentName: undefined as string | undefined,
  categoryName: undefined as string | undefined,
  categoryLevel: undefined as number | undefined,
  defaultPricingMode: undefined as string | undefined,
  supportSplit: undefined as boolean | undefined,
  supportInvoice: undefined as boolean | undefined,
  riskLevel: undefined as string | undefined,
  status: undefined as string | undefined,
  createTime: [] as string[]
})
const queryFormRef = ref()
const exportLoading = ref(false)

const formatParentCategoryName = (parentId?: number) => {
  if (parentId === undefined || parentId === null || parentId === 0) {
    return '顶级类目'
  }
  const parent = allCategories.value.find((item) => item.id === parentId)
  return parent?.categoryName || '上级类目信息缺失'
}

const getList = async () => {
  loading.value = true
  try {
    const categories = await MerchantServiceCategoryApi.getMerchantServiceCategoryAllList({
      categoryName: queryParams.categoryName,
      categoryLevel: queryParams.categoryLevel,
      defaultPricingMode: queryParams.defaultPricingMode,
      supportSplit: queryParams.supportSplit,
      supportInvoice: queryParams.supportInvoice,
      riskLevel: queryParams.riskLevel,
      status: queryParams.status,
      createTime: queryParams.createTime
    })
    allCategories.value = categories
    const filteredList = categories.filter((item) => {
      if (!queryParams.parentName) {
        return true
      }
      return formatParentCategoryName(item.parentId).includes(queryParams.parentName)
    })
    total.value = filteredList.length
    const start = (queryParams.pageNo - 1) * queryParams.pageSize
    const end = start + queryParams.pageSize
    list.value = filteredList.slice(start, end)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await MerchantServiceCategoryApi.deleteMerchantServiceCategory(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const checkedIds = ref<number[]>([])
const handleDeleteBatch = async () => {
  try {
    await message.delConfirm()
    await MerchantServiceCategoryApi.deleteMerchantServiceCategoryList(checkedIds.value)
    checkedIds.value = []
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const handleRowCheckboxChange = (records: MerchantServiceCategory[]) => {
  checkedIds.value = records.map((item) => item.id!)
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
      status: queryParams.status,
      createTime: queryParams.createTime
    })
    download.excel(data, '服务类目表.xls')
  } finally {
    exportLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
