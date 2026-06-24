<template>
  <Dialog v-model="dialogVisible" title="选择服务类目" width="980px">
    <ContentWrap>
      <el-form :inline="true" :model="queryParams" label-width="84px" class="-mb-15px">
        <el-form-item label="类目名称">
          <el-input
            v-model="queryParams.categoryName"
            placeholder="请输入类目名称"
            clearable
            class="!w-240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="层级">
          <el-input
            v-model="queryParams.categoryLevel"
            placeholder="请输入层级"
            clearable
            class="!w-180px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="默认计价">
          <el-select
            v-model="queryParams.defaultPricingMode"
            placeholder="请选择默认计价方式"
            clearable
            class="!w-200px"
          >
            <el-option
              v-for="dict in getStrDictOptions(DICT_TYPE.LB_PRICING_MODE)"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-180px">
            <el-option
              v-for="item in ENABLE_STATUS_OPTIONS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
          <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        </el-form-item>
      </el-form>
    </ContentWrap>

    <ContentWrap>
      <el-table
        v-loading="loading"
        :data="list"
        :stripe="true"
        :show-overflow-tooltip="true"
        :highlight-current-row="true"
        row-key="id"
        @row-click="handleRowClick"
        @row-dblclick="handleRowDblClick"
      >
        <el-table-column width="52" align="center">
          <template #default="{ row }">
            <el-radio v-model="selectedId" :value="row.id" class="radio-no-label" />
          </template>
        </el-table-column>
        <el-table-column label="类目名称" prop="categoryName" min-width="220" />
        <el-table-column label="层级" prop="categoryLevel" width="90" />
        <el-table-column label="默认计价方式" width="140">
          <template #default="{ row }">
            <dict-tag
              v-if="row.defaultPricingMode"
              :type="DICT_TYPE.LB_PRICING_MODE"
              :value="row.defaultPricingMode"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="支持拆单" width="100">
          <template #default="{ row }">
            {{ formatBooleanYesNo(row.supportSplit) }}
          </template>
        </el-table-column>
        <el-table-column label="支持开票" width="100">
          <template #default="{ row }">
            {{ formatBooleanYesNo(row.supportInvoice) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            {{ formatEnableStatus(row.status) }}
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

    <template #footer>
      <el-button type="primary" @click="confirmSelect">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { DICT_TYPE, getStrDictOptions } from '@/utils/dict'
import {
  MerchantServiceCategoryApi,
  type MerchantServiceCategory
} from '@/api/linbang/merchantcategory'
import { useMessage } from '@/hooks/web/useMessage'
import {
  ENABLE_STATUS_OPTIONS,
  formatBooleanYesNo,
  formatEnableStatus
} from '../utils/display'

defineOptions({ name: 'ServiceCategorySelectDialog' })

const message = useMessage()
const dialogVisible = ref(false)
const loading = ref(false)
const total = ref(0)
const list = ref<MerchantServiceCategory[]>([])
const selectedId = ref<number>()
const selectedRow = ref<MerchantServiceCategory>()

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  categoryName: undefined as string | undefined,
  categoryLevel: undefined as number | undefined,
  defaultPricingMode: undefined as string | undefined,
  status: undefined as string | undefined
})

const emit = defineEmits<{
  selected: [row: MerchantServiceCategory]
}>()

const getList = async () => {
  loading.value = true
  try {
    const data = await MerchantServiceCategoryApi.getMerchantServiceCategoryPage(queryParams)
    list.value = data.list
    total.value = data.total
    if (selectedId.value) {
      selectedRow.value = list.value.find((item) => item.id === selectedId.value)
    }
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const resetQuery = () => {
  queryParams.categoryName = undefined
  queryParams.categoryLevel = undefined
  queryParams.defaultPricingMode = undefined
  queryParams.status = undefined
  handleQuery()
}

const handleRowClick = (row: MerchantServiceCategory) => {
  selectedId.value = row.id
  selectedRow.value = row
}

const handleRowDblClick = (row: MerchantServiceCategory) => {
  handleRowClick(row)
  confirmSelect()
}

const confirmSelect = () => {
  if (!selectedRow.value) {
    message.warning('请选择一个服务类目')
    return
  }
  emit('selected', selectedRow.value)
  dialogVisible.value = false
}

const open = async (row?: MerchantServiceCategory) => {
  dialogVisible.value = true
  selectedId.value = row?.id
  selectedRow.value = row
  queryParams.pageNo = 1
  await getList()
}

defineExpose({ open })
</script>

<style scoped>
.radio-no-label :deep(.el-radio__label) {
  display: none;
}
</style>
