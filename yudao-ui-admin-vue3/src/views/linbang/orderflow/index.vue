<template>
  <ContentWrap>
    <el-form :model="queryParams" :inline="true" label-width="100px" class="-mb-15px">
      <el-form-item label="订单 ID">
        <el-input v-model="queryParams.orderId" placeholder="请输入订单 ID" class="!w-220px" clearable />
      </el-form-item>
      <el-form-item label="单元 ID">
        <el-input v-model="queryParams.unitId" placeholder="请输入单元 ID" class="!w-220px" clearable />
      </el-form-item>
      <el-form-item label="派单状态">
        <el-input v-model="queryParams.dispatchStatus" placeholder="请输入派单状态" class="!w-220px" clearable />
      </el-form-item>
      <el-form-item label="退款状态">
        <el-input v-model="queryParams.autoRefundStatus" placeholder="请输入退款状态" class="!w-220px" clearable />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true">
      <el-table-column label="订单 ID" prop="orderId" width="100" />
      <el-table-column label="单元 ID" prop="unitId" width="100" />
      <el-table-column label="金额" prop="unitAmount" width="120" />
      <el-table-column label="派单状态" prop="dispatchStatus" width="120" />
      <el-table-column label="当前批次" prop="currentBatchNo" width="100" />
      <el-table-column label="流单时间" prop="flowTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="流单原因" prop="flowReason" min-width="220" />
      <el-table-column label="退款状态" prop="autoRefundStatus" width="120" />
      <el-table-column label="退款单 ID" prop="autoRefundId" width="120" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="retryRefund(row.unitId)">重试退款</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </ContentWrap>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useMessage } from '@/hooks/web/useMessage'
import { dateFormatter } from '@/utils/formatTime'
import { OrderFlowApi, type OrderFlowRecord } from '@/api/linbang/orderflow'

defineOptions({ name: 'LinbangOrderFlow' })

const message = useMessage()
const loading = ref(false)
const list = ref<OrderFlowRecord[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  orderId: undefined as number | undefined,
  unitId: undefined as number | undefined,
  dispatchStatus: undefined as string | undefined,
  autoRefundStatus: undefined as string | undefined
})

const getList = async () => {
  loading.value = true
  try {
    const data = await OrderFlowApi.getOrderFlowPage(queryParams)
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

const retryRefund = async (unitId: number) => {
  await OrderFlowApi.retryRefund(unitId)
  message.success('已触发退款重试')
  getList()
}

onMounted(() => getList())
</script>
