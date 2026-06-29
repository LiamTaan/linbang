<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="处罚动作" prop="penaltyAction">
        <el-input v-model="queryParams.penaltyAction" placeholder="如 DEMOTE/SCORE" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-input v-model="queryParams.status" placeholder="如 ACTIVE" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="推广员" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.userNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="违规内容" prop="contentTitle" min-width="220" />
      <el-table-column label="处罚动作" prop="penaltyAction" width="140" />
      <el-table-column label="扣分值" prop="scoreChange" width="100" />
      <el-table-column label="原因" prop="reason" min-width="220" />
      <el-table-column label="状态" prop="status" width="100" />
      <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="showDetail(row.id)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </ContentWrap>

  <Dialog v-model="detailVisible" title="处罚记录详情" width="720px">
    <el-descriptions :column="2" border>
      <el-descriptions-item label="推广员">{{ detailData?.userNickname || '-' }}</el-descriptions-item>
      <el-descriptions-item label="手机号">{{ detailData?.userMobile || '-' }}</el-descriptions-item>
      <el-descriptions-item label="违规内容">{{ detailData?.contentTitle || '-' }}</el-descriptions-item>
      <el-descriptions-item label="处罚动作">{{ detailData?.penaltyAction || '-' }}</el-descriptions-item>
      <el-descriptions-item label="扣分值">{{ detailData?.scoreChange || '-' }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ detailData?.status || '-' }}</el-descriptions-item>
      <el-descriptions-item label="原因" :span="2">{{ detailData?.reason || '-' }}</el-descriptions-item>
    </el-descriptions>
  </Dialog>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { dateFormatter } from '@/utils/formatTime'
import { PromoterPenaltyRecordApi, type PromoterPenaltyRecord } from '@/api/linbang/promoterpenaltyrecord'

defineOptions({ name: 'PromoterPenaltyRecord' })

const loading = ref(false)
const list = ref<PromoterPenaltyRecord[]>([])
const detailVisible = ref(false)
const detailData = ref<PromoterPenaltyRecord>()
const total = ref(0)
const queryFormRef = ref()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  penaltyAction: undefined as string | undefined,
  status: undefined as string | undefined
})

const getList = async () => {
  loading.value = true
  try {
    const data = await PromoterPenaltyRecordApi.getPage(queryParams)
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

const showDetail = async (id: number) => {
  detailData.value = await PromoterPenaltyRecordApi.get(id)
  detailVisible.value = true
}

onMounted(() => {
  getList()
})
</script>
