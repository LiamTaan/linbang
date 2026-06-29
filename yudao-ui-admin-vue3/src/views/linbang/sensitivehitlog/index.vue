<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="场景" prop="sceneType">
        <el-input v-model="queryParams.sceneType" placeholder="如 MESSAGE/COMMENT" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="用户" prop="userKeyword">
        <el-input v-model="queryParams.userKeyword" placeholder="用户编号/昵称/手机号" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="业务类型" prop="bizType">
        <el-input v-model="queryParams.bizType" placeholder="如 REVIEW/PROMOTE" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="内容类型" prop="contentType">
        <el-input v-model="queryParams.contentType" placeholder="如 TEXT/IMAGE/QRCODE" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="策略" prop="strategy">
        <el-input v-model="queryParams.strategy" placeholder="如 BLOCK/REPLACE" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="人工审核" prop="manualAuditResult">
        <el-input v-model="queryParams.manualAuditResult" placeholder="如 PASS/REJECT" clearable class="!w-220px" @keyup.enter="handleQuery" />
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
      <el-table-column label="场景" prop="sceneType" width="120" />
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
      <el-table-column label="命中词" prop="hitWord" width="140" />
      <el-table-column label="拦截级别" prop="blockLevel" width="120" />
      <el-table-column label="处理策略" prop="strategy" width="120" />
      <el-table-column label="内容类型" prop="contentType" width="120" />
      <el-table-column label="文件ID" prop="fileId" width="100" />
      <el-table-column label="OCR/二维码" min-width="240">
        <template #default="{ row }">
          <div class="leading-20px">
            <div>{{ row.ocrTextSnapshot || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.qrContentSnapshot || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="人工审核" prop="manualAuditResult" width="120" />
      <el-table-column label="内容快照" prop="contentSnapshot" min-width="260" />
      <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" fixed="right" width="100">
        <template #default="{ row }">
          <el-button link type="primary" @click="showDetail(row.id)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </ContentWrap>

  <Dialog v-model="detailVisible" title="敏感词命中日志详情" width="760px">
    <el-descriptions :column="2" border>
      <el-descriptions-item label="场景">{{ detailData?.sceneType || '-' }}</el-descriptions-item>
      <el-descriptions-item label="命中词">{{ detailData?.hitWord || '-' }}</el-descriptions-item>
      <el-descriptions-item label="用户">{{ detailData?.userNickname || '-' }}</el-descriptions-item>
      <el-descriptions-item label="手机号">{{ detailData?.userMobile || '-' }}</el-descriptions-item>
      <el-descriptions-item label="业务">{{ detailData?.bizType || '-' }} / {{ detailData?.bizId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="敏感词ID">{{ detailData?.wordId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="拦截级别">{{ detailData?.blockLevel || '-' }}</el-descriptions-item>
      <el-descriptions-item label="处理策略">{{ detailData?.strategy || '-' }}</el-descriptions-item>
      <el-descriptions-item label="内容类型">{{ detailData?.contentType || '-' }}</el-descriptions-item>
      <el-descriptions-item label="文件ID">{{ detailData?.fileId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="人工审核结果">{{ detailData?.manualAuditResult || '-' }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ detailData?.createTime || '-' }}</el-descriptions-item>
      <el-descriptions-item label="内容快照" :span="2">{{ detailData?.contentSnapshot || '-' }}</el-descriptions-item>
      <el-descriptions-item label="OCR快照" :span="2">{{ detailData?.ocrTextSnapshot || '-' }}</el-descriptions-item>
      <el-descriptions-item label="二维码内容" :span="2">{{ detailData?.qrContentSnapshot || '-' }}</el-descriptions-item>
    </el-descriptions>
  </Dialog>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { dateFormatter } from '@/utils/formatTime'
import { SensitiveHitLogApi, type SensitiveHitLog } from '@/api/linbang/sensitivehitlog'

defineOptions({ name: 'SensitiveHitLog' })

const loading = ref(false)
const list = ref<SensitiveHitLog[]>([])
const total = ref(0)
const detailVisible = ref(false)
const detailData = ref<SensitiveHitLog>()
const queryFormRef = ref()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  sceneType: undefined as string | undefined,
  userKeyword: undefined as string | undefined,
  bizType: undefined as string | undefined,
  contentType: undefined as string | undefined,
  strategy: undefined as string | undefined,
  manualAuditResult: undefined as string | undefined,
  createTime: [] as string[]
})

const getList = async () => {
  loading.value = true
  try {
    const data = await SensitiveHitLogApi.getPage(queryParams)
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
  detailData.value = await SensitiveHitLogApi.get(id)
  detailVisible.value = true
}

onMounted(() => {
  getList()
})
</script>
