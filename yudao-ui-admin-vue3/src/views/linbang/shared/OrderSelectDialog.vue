<template>
  <Dialog v-model="dialogVisible" title="选择订单" width="980px">
    <ContentWrap>
      <el-form :inline="true" :model="queryParams" label-width="72px" class="-mb-15px">
        <el-form-item label="订单号">
          <el-input
            v-model="queryParams.orderNo"
            placeholder="请输入订单号"
            clearable
            class="!w-220px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="用户">
          <el-input
            v-model="queryParams.userKeyword"
            placeholder="请输入用户编号 / 昵称 / 手机号"
            clearable
            class="!w-220px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="标题">
          <el-input
            v-model="queryParams.title"
            placeholder="请输入订单标题"
            clearable
            class="!w-220px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择订单状态" clearable class="!w-220px">
            <el-option
              v-for="dict in getStrDictOptions(DICT_TYPE.LB_ORDER_STATUS)"
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
        <el-table-column label="订单号" prop="orderNo" min-width="180" />
        <el-table-column label="订单标题" prop="title" min-width="220" />
        <el-table-column label="下单用户" min-width="220">
          <template #default="{ row }">
            <div class="font-600">{{ row.userNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || '-' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="订单金额" prop="orderAmount" width="110" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            {{ formatOrderStatus(row.status) }}
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
import { OrderInfoApi, type OrderInfo } from '@/api/linbang/orderinfo'
import { formatOrderStatus } from '../utils/display'

defineOptions({ name: 'OrderSelectDialog' })

const message = useMessage()

const dialogVisible = ref(false)
const loading = ref(false)
const total = ref(0)
const list = ref<OrderInfo[]>([])
const selectedId = ref<number>()
const selectedRow = ref<OrderInfo>()

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  orderNo: undefined as string | undefined,
  userKeyword: undefined as string | undefined,
  title: undefined as string | undefined,
  status: undefined as string | undefined
})

const emit = defineEmits<{
  selected: [row: OrderInfo]
}>()

const getList = async () => {
  loading.value = true
  try {
    const data = await OrderInfoApi.getOrderInfoPage(queryParams)
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
  queryParams.orderNo = undefined
  queryParams.userKeyword = undefined
  queryParams.title = undefined
  queryParams.status = undefined
  handleQuery()
}

const handleRowClick = (row: OrderInfo) => {
  selectedId.value = row.id
  selectedRow.value = row
}

const handleRowDblClick = (row: OrderInfo) => {
  handleRowClick(row)
  confirmSelect()
}

const confirmSelect = () => {
  if (!selectedRow.value) {
    message.warning('请选择一条订单数据')
    return
  }
  emit('selected', selectedRow.value)
  dialogVisible.value = false
}

const open = async (row?: OrderInfo) => {
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
