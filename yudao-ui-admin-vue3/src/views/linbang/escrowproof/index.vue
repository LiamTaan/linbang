<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="凭证编号" prop="proofNo">
        <el-input v-model="queryParams.proofNo" placeholder="请输入托管凭证编号" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="订单 ID" prop="orderId">
        <el-input v-model="queryParams.orderId" placeholder="请输入订单 ID" clearable class="!w-200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="单元 ID" prop="unitId">
        <el-input v-model="queryParams.unitId" placeholder="请输入单元 ID" clearable class="!w-200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="proofStatus">
        <el-input v-model="queryParams.proofStatus" placeholder="如 LOCKED / UNLOCKED / REFUNDED" clearable class="!w-240px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="凭证编号" align="center" prop="proofNo" min-width="180" />
      <el-table-column label="订单/单元" align="center" min-width="160">
        <template #default="{ row }">
          <div class="leading-20px">
            <div>订单：{{ row.orderId || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">单元：{{ row.unitId || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="用户 ID" align="center" prop="userId" width="110" />
      <el-table-column label="服务商 ID" align="center" prop="merchantId" width="110" />
      <el-table-column label="托管金额" align="center" prop="escrowAmount" width="120" />
      <el-table-column label="状态" align="center" prop="proofStatus" width="120" />
      <el-table-column label="锁定原因" align="center" prop="lockReason" min-width="180" />
      <el-table-column label="解锁原因" align="center" prop="unlockReason" min-width="180" />
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

  <Dialog v-model="detailVisible" title="托管凭证详情" width="720px" :loading="detailLoading">
    <el-descriptions :column="2" border>
      <el-descriptions-item label="凭证编号">{{ detailData?.proofNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ detailData?.proofStatus || '-' }}</el-descriptions-item>
      <el-descriptions-item label="订单 ID">{{ detailData?.orderId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="单元 ID">{{ detailData?.unitId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="用户 ID">{{ detailData?.userId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="服务商 ID">{{ detailData?.merchantId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="托管金额">{{ detailData?.escrowAmount ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDate(detailData?.createTime) }}</el-descriptions-item>
      <el-descriptions-item label="锁定原因" :span="2">{{ detailData?.lockReason || '-' }}</el-descriptions-item>
      <el-descriptions-item label="解锁原因" :span="2">{{ detailData?.unlockReason || '-' }}</el-descriptions-item>
    </el-descriptions>
  </Dialog>
</template>

<script setup lang="ts">
import type { FormInstance } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { formatDate, dateFormatter } from '@/utils/formatTime'
import { EscrowProofApi, type EscrowProof } from '@/api/linbang/escrowproof'

defineOptions({ name: 'EscrowProof' })

const loading = ref(false)
const detailLoading = ref(false)
const detailVisible = ref(false)
const list = ref<EscrowProof[]>([])
const detailData = ref<EscrowProof>()
const total = ref(0)
const queryFormRef = ref<FormInstance>()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  proofNo: undefined as string | undefined,
  orderId: undefined as number | undefined,
  unitId: undefined as number | undefined,
  proofStatus: undefined as string | undefined
})

const getList = async () => {
  loading.value = true
  try {
    const data = await EscrowProofApi.getEscrowProofPage(queryParams)
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
    detailData.value = await EscrowProofApi.getEscrowProof(id)
  } finally {
    detailLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
