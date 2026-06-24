<template>
  <Dialog v-model="dialogVisible" title="选择单元" width="960px">
    <ContentWrap>
      <el-form :inline="true" :model="queryParams" label-width="72px" class="-mb-15px">
        <el-form-item label="单元号">
          <el-input
            v-model="queryParams.unitNo"
            placeholder="请输入单元号"
            clearable
            class="!w-220px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="单元标题">
          <el-input
            v-model="queryParams.unitTitle"
            placeholder="请输入单元标题"
            clearable
            class="!w-220px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择单元状态" clearable class="!w-220px">
            <el-option
              v-for="dict in getStrDictOptions(DICT_TYPE.LB_ORDER_UNIT_STATUS)"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
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
        <el-table-column label="单元号" prop="unitNo" min-width="180" />
        <el-table-column label="单元标题" prop="unitTitle" min-width="220" />
        <el-table-column label="单元序号" prop="unitSeq" width="100" />
        <el-table-column label="单元金额" prop="unitAmount" width="110" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            {{ formatOrderUnitStatus(row.status) }}
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
import { OrderUnitApi, type OrderUnit } from '@/api/linbang/orderunit'
import { formatOrderUnitStatus } from '../utils/display'

defineOptions({ name: 'OrderUnitSelectDialog' })

const message = useMessage()

const dialogVisible = ref(false)
const loading = ref(false)
const total = ref(0)
const list = ref<OrderUnit[]>([])
const selectedId = ref<number>()
const selectedRow = ref<OrderUnit>()

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  orderId: undefined as number | undefined,
  unitNo: undefined as string | undefined,
  unitTitle: undefined as string | undefined,
  status: undefined as string | undefined
})

const emit = defineEmits<{
  selected: [row: OrderUnit]
}>()

const getList = async () => {
  loading.value = true
  try {
    const data = await OrderUnitApi.getOrderUnitPage(queryParams)
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
  queryParams.unitNo = undefined
  queryParams.unitTitle = undefined
  queryParams.status = undefined
  handleQuery()
}

const handleRowClick = (row: OrderUnit) => {
  selectedId.value = row.id
  selectedRow.value = row
}

const handleRowDblClick = (row: OrderUnit) => {
  handleRowClick(row)
  confirmSelect()
}

const confirmSelect = () => {
  if (!selectedRow.value) {
    message.warning('请选择一条单元数据')
    return
  }
  emit('selected', selectedRow.value)
  dialogVisible.value = false
}

const open = async (row?: OrderUnit, orderId?: number) => {
  dialogVisible.value = true
  selectedId.value = row?.id
  selectedRow.value = row
  queryParams.orderId = orderId
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
