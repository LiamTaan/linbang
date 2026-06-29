<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="订单 ID" prop="orderId">
        <el-input v-model="queryParams.orderId" placeholder="请输入订单 ID" clearable class="!w-200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="单元 ID" prop="unitId">
        <el-input v-model="queryParams.unitId" placeholder="请输入单元 ID" clearable class="!w-200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="去向类型" prop="targetType">
        <el-input v-model="queryParams.targetType" placeholder="如 MERCHANT / PLATFORM" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="结算状态" prop="settleStatus">
        <el-input v-model="queryParams.settleStatus" placeholder="如 PENDING / REFUNDED" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="城市等级" prop="cityLevel">
        <el-input v-model="queryParams.cityLevel" placeholder="请输入城市等级" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="分账编号" align="center" prop="divideNo" min-width="180" />
      <el-table-column label="订单/单元" align="center" min-width="160">
        <template #default="{ row }">
          <div class="leading-20px">
            <div>订单：{{ row.orderId || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">单元：{{ row.unitId || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="去向类型" align="center" prop="targetType" width="130" />
      <el-table-column label="目标业务 ID" align="center" prop="targetBizId" width="120" />
      <el-table-column label="分账比例(%)" align="center" prop="divideRate" width="120" />
      <el-table-column label="分账金额" align="center" prop="divideAmount" width="120" />
      <el-table-column label="税务扣减" align="center" prop="taxAmount" width="120" />
      <el-table-column label="结算状态" align="center" prop="settleStatus" width="120" />
      <el-table-column label="城市等级" align="center" prop="cityLevel" width="120" />
      <el-table-column label="备注" align="center" prop="remark" min-width="180" />
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" align="center" width="100" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row.id)">详情</el-button>
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

  <Dialog v-model="detailVisible" title="分账明细详情" width="720px" :loading="detailLoading">
    <el-descriptions :column="2" border>
      <el-descriptions-item label="分账编号">{{ detailData?.divideNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="分账规则 ID">{{ detailData?.divideRuleId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="订单 ID">{{ detailData?.orderId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="单元 ID">{{ detailData?.unitId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="去向类型">{{ detailData?.targetType || '-' }}</el-descriptions-item>
      <el-descriptions-item label="目标业务 ID">{{ detailData?.targetBizId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="分账比例">{{ detailData?.divideRate ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="分账金额">{{ detailData?.divideAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="税务扣减">{{ detailData?.taxAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="结算状态">{{ detailData?.settleStatus || '-' }}</el-descriptions-item>
      <el-descriptions-item label="城市等级">{{ detailData?.cityLevel || '-' }}</el-descriptions-item>
      <el-descriptions-item label="类目 ID">{{ detailData?.categoryId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="创建时间" :span="2">{{ formatDate(detailData?.createTime) }}</el-descriptions-item>
      <el-descriptions-item label="备注" :span="2">{{ detailData?.remark || '-' }}</el-descriptions-item>
    </el-descriptions>
  </Dialog>
</template>

<script setup lang="ts">
import type { FormInstance } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { formatDate, dateFormatter } from '@/utils/formatTime'
import { OrderDivideRecordApi, type OrderDivideRecord } from '@/api/linbang/orderdividerecord'

defineOptions({ name: 'OrderDivideRecord' })

const loading = ref(false)
const detailLoading = ref(false)
const detailVisible = ref(false)
const list = ref<OrderDivideRecord[]>([])
const detailData = ref<OrderDivideRecord>()
const total = ref(0)
const queryFormRef = ref<FormInstance>()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  orderId: undefined as number | undefined,
  unitId: undefined as number | undefined,
  targetType: undefined as string | undefined,
  settleStatus: undefined as string | undefined,
  cityLevel: undefined as string | undefined
})

const getList = async () => {
  loading.value = true
  try {
    const data = await OrderDivideRecordApi.getOrderDivideRecordPage(queryParams)
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

const openDetail = async (id: number) => {
  detailVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await OrderDivideRecordApi.getOrderDivideRecord(id)
  } finally {
    detailLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
