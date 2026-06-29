<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="用户" prop="userKeyword">
        <el-input v-model="queryParams.userKeyword" placeholder="用户编号/昵称/手机号" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-input v-model="queryParams.status" placeholder="如 ACTIVE/RELEASED" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="来源业务" prop="sourceBizType">
        <el-input v-model="queryParams.sourceBizType" placeholder="如 ORDER/WITHDRAW" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-260px"
        />
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
      <el-table-column label="冻结金额" prop="frozenAmount" width="120" />
      <el-table-column label="已释放金额" prop="releasedAmount" width="120" />
      <el-table-column label="状态" prop="status" width="120" />
      <el-table-column label="来源业务" min-width="140">
        <template #default="{ row }">{{ row.sourceBizType || '-' }} / {{ row.sourceBizId || '-' }}</template>
      </el-table-column>
      <el-table-column label="原因" prop="reason" min-width="220" />
      <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="showDetail(row.id)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </ContentWrap>

  <Dialog v-model="detailVisible" title="冻结记录详情" width="720px">
    <el-descriptions :column="2" border>
      <el-descriptions-item label="用户">{{ detailData?.userNickname || '-' }}</el-descriptions-item>
      <el-descriptions-item label="手机号">{{ detailData?.userMobile || '-' }}</el-descriptions-item>
      <el-descriptions-item label="冻结金额">{{ detailData?.frozenAmount || '-' }}</el-descriptions-item>
      <el-descriptions-item label="已释放金额">{{ detailData?.releasedAmount || '-' }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ detailData?.status || '-' }}</el-descriptions-item>
      <el-descriptions-item label="来源业务">{{ detailData?.sourceBizType || '-' }} / {{ detailData?.sourceBizId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="解除人">{{ detailData?.releasedBy || '-' }}</el-descriptions-item>
      <el-descriptions-item label="解除时间">{{ detailData?.releasedTime || '-' }}</el-descriptions-item>
      <el-descriptions-item label="原因" :span="2">{{ detailData?.reason || '-' }}</el-descriptions-item>
      <el-descriptions-item label="解除备注" :span="2">{{ detailData?.releaseRemark || '-' }}</el-descriptions-item>
    </el-descriptions>
  </Dialog>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { dateFormatter } from '@/utils/formatTime'
import { UserFrozenFundRecordApi, type UserFrozenFundRecord } from '@/api/linbang/userfrozenfundrecord'

defineOptions({ name: 'UserFrozenFundRecord' })

const loading = ref(false)
const list = ref<UserFrozenFundRecord[]>([])
const detailVisible = ref(false)
const detailData = ref<UserFrozenFundRecord>()
const total = ref(0)
const queryFormRef = ref()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userKeyword: undefined as string | undefined,
  status: undefined as string | undefined,
  sourceBizType: undefined as string | undefined,
  createTime: [] as string[]
})

const getList = async () => {
  loading.value = true
  try {
    const data = await UserFrozenFundRecordApi.getPage(queryParams)
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
  detailData.value = await UserFrozenFundRecordApi.get(id)
  detailVisible.value = true
}

onMounted(() => {
  getList()
})
</script>
