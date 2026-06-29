<template>
  <ContentWrap>
    <el-form :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="单元 ID">
        <el-input v-model="queryParams.unitId" placeholder="请输入单元 ID" class="!w-220px" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-input v-model="queryParams.status" placeholder="请输入状态" class="!w-220px" clearable />
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
      <el-table-column label="阶段号" prop="stageNo" width="90" />
      <el-table-column label="批次号" prop="pushBatchNo" width="90" />
      <el-table-column label="半径范围(km)" min-width="160">
        <template #default="{ row }">{{ row.radiusStartKm }} - {{ row.radiusEndKm }}</template>
      </el-table-column>
      <el-table-column label="计划时间" prop="plannedAt" :formatter="dateFormatter" width="180" />
      <el-table-column label="过期时间" prop="expiredAt" :formatter="dateFormatter" width="180" />
      <el-table-column label="状态" prop="status" width="120" />
      <el-table-column label="触发类型" prop="triggerType" min-width="120" />
    </el-table>
    <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </ContentWrap>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { dateFormatter } from '@/utils/formatTime'
import { MatchPushBatchApi, type MatchPushBatch } from '@/api/linbang/matchpushbatch'

defineOptions({ name: 'LinbangMatchPushBatch' })

const loading = ref(false)
const list = ref<MatchPushBatch[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  unitId: undefined as number | undefined,
  status: undefined as string | undefined
})

const getList = async () => {
  loading.value = true
  try {
    const data = await MatchPushBatchApi.getMatchPushBatchPage(queryParams)
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

onMounted(() => getList())
</script>
