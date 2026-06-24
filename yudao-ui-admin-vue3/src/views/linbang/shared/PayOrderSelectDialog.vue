<template>
  <Dialog v-model="dialogVisible" title="选择支付订单" width="1080px">
    <ContentWrap>
      <el-form :inline="true" :model="queryParams" label-width="96px" class="-mb-15px">
        <el-form-item label="商户单号">
          <el-input
            v-model="queryParams.merchantOrderId"
            placeholder="请输入商户单号"
            clearable
            class="!w-240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="支付单号">
          <el-input
            v-model="queryParams.no"
            placeholder="请输入支付单号"
            clearable
            class="!w-240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="商品标题">
          <el-input
            v-model="queryParams.subject"
            placeholder="请输入商品标题"
            clearable
            class="!w-240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="支付状态">
          <el-select v-model="queryParams.status" placeholder="请选择支付状态" clearable class="!w-220px">
            <el-option
              v-for="dict in getIntDictOptions(DICT_TYPE.PAY_ORDER_STATUS)"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
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
        <el-table-column label="订单号" min-width="280">
          <template #default="{ row }">
            <div class="leading-20px">
              <div class="font-600">{{ row.merchantOrderId || '-' }}</div>
              <div class="text-[var(--el-text-color-secondary)]">{{ row.no || '-' }}</div>
              <div class="text-[var(--el-text-color-secondary)]">{{ row.subject || '-' }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="支付状态" width="120">
          <template #default="{ row }">
            <dict-tag
              v-if="row.status !== undefined && row.status !== null"
              :type="DICT_TYPE.PAY_ORDER_STATUS"
              :value="row.status"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="支付金额" width="120">
          <template #default="{ row }">
            {{ formatPayAmount(row.price) }}
          </template>
        </el-table-column>
        <el-table-column label="退款金额" width="120">
          <template #default="{ row }">
            {{ formatPayAmount(row.refundPrice) }}
          </template>
        </el-table-column>
        <el-table-column label="支付时间" prop="successTime" width="180" :formatter="dateFormatter" />
        <el-table-column label="创建时间" prop="createTime" width="180" :formatter="dateFormatter" />
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
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import { useMessage } from '@/hooks/web/useMessage'
import { getOrder, getOrderPage, type OrderDetailVO, type OrderVO } from '@/api/pay/order'

defineOptions({ name: 'PayOrderSelectDialog' })

export type PayOrderOption = Partial<OrderDetailVO & OrderVO> & { id?: number }

const message = useMessage()
const dialogVisible = ref(false)
const loading = ref(false)
const total = ref(0)
const list = ref<PayOrderOption[]>([])
const selectedId = ref<number>()
const selectedRow = ref<PayOrderOption>()

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  merchantOrderId: undefined as string | undefined,
  no: undefined as string | undefined,
  subject: undefined as string | undefined,
  status: undefined as number | undefined
})

const emit = defineEmits<{
  selected: [row: PayOrderOption]
}>()

const formatPayAmount = (amount?: number) => {
  if (amount === undefined || amount === null) {
    return '-'
  }
  return `￥${(amount / 100).toFixed(2)}`
}

const getList = async () => {
  loading.value = true
  try {
    const data = await getOrderPage(queryParams)
    list.value = data.list as PayOrderOption[]
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
  queryParams.merchantOrderId = undefined
  queryParams.no = undefined
  queryParams.subject = undefined
  queryParams.status = undefined
  handleQuery()
}

const handleRowClick = (row: PayOrderOption) => {
  selectedId.value = row.id
  selectedRow.value = row
}

const handleRowDblClick = (row: PayOrderOption) => {
  handleRowClick(row)
  confirmSelect()
}

const confirmSelect = () => {
  if (!selectedRow.value) {
    message.warning('请选择一个支付订单')
    return
  }
  emit('selected', selectedRow.value)
  dialogVisible.value = false
}

const open = async (row?: PayOrderOption) => {
  dialogVisible.value = true
  selectedId.value = row?.id
  selectedRow.value = row
  queryParams.pageNo = 1
  queryParams.merchantOrderId = row?.merchantOrderId || undefined
  await getList()
}

const loadById = async (id?: number) => {
  if (!id) {
    return undefined
  }
  return (await getOrder(id)) as PayOrderOption
}

defineExpose({ open, loadById })
</script>

<style scoped>
.radio-no-label :deep(.el-radio__label) {
  display: none;
}
</style>
