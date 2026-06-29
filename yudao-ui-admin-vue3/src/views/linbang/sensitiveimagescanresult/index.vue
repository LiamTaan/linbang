<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="场景" prop="sceneType">
        <el-input v-model="queryParams.sceneType" placeholder="如 ORDER_CREATE" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="用户ID" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入用户ID" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="业务类型" prop="bizType">
        <el-input v-model="queryParams.bizType" placeholder="如 ORDER" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="扫描状态" prop="scanStatus">
        <el-input v-model="queryParams.scanStatus" placeholder="如 BLOCKED/FAILED" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="场景" prop="sceneType" width="140" />
      <el-table-column label="用户" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.userNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="业务" min-width="140">
        <template #default="{ row }">{{ row.bizType || '-' }} / {{ row.bizId || '-' }}</template>
      </el-table-column>
      <el-table-column label="文件ID" prop="fileId" width="100" />
      <el-table-column label="扫描状态" prop="scanStatus" width="120" />
      <el-table-column label="命中结果" prop="hitWords" min-width="220" />
      <el-table-column label="OCR文本" prop="ocrText" min-width="220" />
      <el-table-column label="二维码内容" prop="qrContent" min-width="220" />
      <el-table-column label="失败原因" prop="failureReason" min-width="180" />
      <el-table-column label="创建时间" prop="createTime" width="180" />
    </el-table>
    <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </ContentWrap>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { SensitiveImageScanResultApi, type SensitiveImageScanResult } from '@/api/linbang/sensitiveimagescanresult'

defineOptions({ name: 'SensitiveImageScanResult' })

const loading = ref(false)
const list = ref<SensitiveImageScanResult[]>([])
const total = ref(0)
const queryFormRef = ref()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  sceneType: undefined as string | undefined,
  userId: undefined as number | undefined,
  bizType: undefined as string | undefined,
  scanStatus: undefined as string | undefined
})

const getList = async () => {
  loading.value = true
  try {
    const data = await SensitiveImageScanResultApi.getPage(queryParams)
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

onMounted(() => {
  getList()
})
</script>
