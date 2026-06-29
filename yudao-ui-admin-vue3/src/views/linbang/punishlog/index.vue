<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="用户 ID" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入用户 ID" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="处罚类型" prop="punishType">
        <el-input v-model="queryParams.punishType" placeholder="如 RESTRICT_PUBLISH" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-input v-model="queryParams.status" placeholder="如 ACTIVE/RELEASED" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="用户" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.userNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="处罚类型" prop="punishType" width="180" />
      <el-table-column label="状态" prop="status" width="120" />
      <el-table-column label="来源业务" min-width="140">
        <template #default="{ row }">{{ row.sourceBizType || '-' }} / {{ row.sourceBizId || '-' }}</template>
      </el-table-column>
      <el-table-column label="原因" prop="reason" min-width="220" />
      <el-table-column label="开始时间" prop="startTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="结束时间" prop="endTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" fixed="right" width="100">
        <template #default="{ row }">
          <el-button link type="primary" @click="showDetail(row.id)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </ContentWrap>

  <Dialog v-model="detailVisible" title="处罚执行日志详情" width="760px">
    <el-descriptions :column="2" border>
      <el-descriptions-item label="用户">{{ detailData?.userNickname || '-' }}</el-descriptions-item>
      <el-descriptions-item label="手机号">{{ detailData?.userMobile || '-' }}</el-descriptions-item>
      <el-descriptions-item label="处罚类型">{{ detailData?.punishType || '-' }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ detailData?.status || '-' }}</el-descriptions-item>
      <el-descriptions-item label="来源业务">{{ detailData?.sourceBizType || '-' }} / {{ detailData?.sourceBizId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="来源记录">{{ detailData?.sourceRecordType || '-' }} / {{ detailData?.sourceRecordId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="执行人">{{ detailData?.operatorId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="执行时间">{{ detailData?.operateTime || '-' }}</el-descriptions-item>
      <el-descriptions-item label="生效时间">{{ detailData?.startTime || '-' }}</el-descriptions-item>
      <el-descriptions-item label="结束时间">{{ detailData?.endTime || '-' }}</el-descriptions-item>
      <el-descriptions-item label="解除人">{{ detailData?.releaseOperatorId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="解除时间">{{ detailData?.releaseTime || '-' }}</el-descriptions-item>
      <el-descriptions-item label="原因" :span="2">{{ detailData?.reason || '-' }}</el-descriptions-item>
      <el-descriptions-item label="解除备注" :span="2">{{ detailData?.releaseRemark || '-' }}</el-descriptions-item>
      <el-descriptions-item label="扩展信息" :span="2">{{ detailData?.extJson || '-' }}</el-descriptions-item>
    </el-descriptions>
  </Dialog>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { dateFormatter } from '@/utils/formatTime'
import { PunishLogApi, type PunishLog } from '@/api/linbang/punishlog'

defineOptions({ name: 'PunishLog' })

const loading = ref(false)
const list = ref<PunishLog[]>([])
const total = ref(0)
const detailVisible = ref(false)
const detailData = ref<PunishLog>()
const queryFormRef = ref()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userId: undefined as number | undefined,
  punishType: undefined as string | undefined,
  status: undefined as string | undefined
})

const getList = async () => {
  loading.value = true
  try {
    const data = await PunishLogApi.getPage(queryParams)
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

const showDetail = async (id?: number) => {
  if (!id) return
  detailData.value = await PunishLogApi.get(id)
  detailVisible.value = true
}

onMounted(() => {
  getList()
})
</script>
