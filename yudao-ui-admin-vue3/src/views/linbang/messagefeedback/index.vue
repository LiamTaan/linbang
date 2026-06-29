<template>
  <ContentWrap>
    <el-form :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="场景编码">
        <el-input v-model="queryParams.sceneCode" placeholder="请输入场景编码" clearable class="!w-220px" />
      </el-form-item>
      <el-form-item label="消息分类">
        <el-select v-model="queryParams.messageCategory" clearable class="!w-180px">
          <el-option label="系统" value="SYSTEM" />
          <el-option label="资金" value="FINANCE" />
          <el-option label="订单" value="ORDER" />
          <el-option label="合规" value="COMPLIANCE" />
          <el-option label="纠纷" value="DISPUTE" />
          <el-option label="营销" value="MARKETING" />
        </el-select>
      </el-form-item>
      <el-form-item label="渠道">
        <el-select v-model="queryParams.channelType" clearable class="!w-220px">
          <el-option v-for="item in CHANNEL_TYPE_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" />搜索</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="统计日期" prop="statDate" width="120" />
      <el-table-column label="场景编码" prop="sceneCode" min-width="180" />
      <el-table-column label="消息分类" width="100">
        <template #default="{ row }">{{ formatMessageCategory(row.messageCategory) }}</template>
      </el-table-column>
      <el-table-column label="渠道" width="140">
        <template #default="{ row }">{{ formatChannelType(row.channelType) }}</template>
      </el-table-column>
      <el-table-column label="计划人数" prop="plannedAudienceCount" width="90" />
      <el-table-column label="触达人数" prop="reachedCount" width="90" />
      <el-table-column label="点击人数" prop="clickedCount" width="90" />
      <el-table-column label="已读人数" prop="readCount" width="90" />
      <el-table-column label="触达率" width="100">
        <template #default="{ row }">{{ formatPercent(row.reachRate) }}</template>
      </el-table-column>
      <el-table-column label="点击率" width="100">
        <template #default="{ row }">{{ formatPercent(row.clickRate) }}</template>
      </el-table-column>
      <el-table-column label="已读率" width="100">
        <template #default="{ row }">{{ formatPercent(row.readRate) }}</template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="90">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </ContentWrap>

  <Dialog v-model="detailVisible" title="反馈统计详情" width="640px">
    <el-descriptions :column="2" border>
      <el-descriptions-item label="统计日期">{{ detailData?.statDate || '-' }}</el-descriptions-item>
      <el-descriptions-item label="场景编码">{{ detailData?.sceneCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="消息分类">{{ formatMessageCategory(detailData?.messageCategory) }}</el-descriptions-item>
      <el-descriptions-item label="渠道">{{ formatChannelType(detailData?.channelType) }}</el-descriptions-item>
      <el-descriptions-item label="模板 ID">{{ detailData?.templateId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="活动 ID">{{ detailData?.campaignId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="任务 ID">{{ detailData?.pushTaskId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="计划人数">{{ detailData?.plannedAudienceCount || 0 }}</el-descriptions-item>
      <el-descriptions-item label="触达人数">{{ detailData?.reachedCount || 0 }}</el-descriptions-item>
      <el-descriptions-item label="点击人数">{{ detailData?.clickedCount || 0 }}</el-descriptions-item>
      <el-descriptions-item label="已读人数">{{ detailData?.readCount || 0 }}</el-descriptions-item>
      <el-descriptions-item label="语音播放">{{ detailData?.voicePlayedCount || 0 }}</el-descriptions-item>
      <el-descriptions-item label="触达率">{{ formatPercent(detailData?.reachRate) }}</el-descriptions-item>
      <el-descriptions-item label="点击率">{{ formatPercent(detailData?.clickRate) }}</el-descriptions-item>
      <el-descriptions-item label="已读率">{{ formatPercent(detailData?.readRate) }}</el-descriptions-item>
    </el-descriptions>
  </Dialog>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { MessageFeedbackApi, type MessageFeedbackStat } from '@/api/linbang/messagefeedback'
import { CHANNEL_TYPE_OPTIONS, formatChannelType, formatMessageCategory, formatPercent } from '../utils/display'

defineOptions({ name: 'MessageFeedbackIndex' })

const loading = ref(false)
const total = ref(0)
const list = ref<MessageFeedbackStat[]>([])
const detailVisible = ref(false)
const detailData = ref<MessageFeedbackStat>()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  sceneCode: undefined as string | undefined,
  messageCategory: undefined as string | undefined,
  channelType: undefined as string | undefined
})

const getList = async () => {
  loading.value = true
  try {
    const data = await MessageFeedbackApi.getPage(queryParams)
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

const openDetail = async (row: MessageFeedbackStat) => {
  detailData.value = await MessageFeedbackApi.get(row.id)
  detailVisible.value = true
}

onMounted(getList)
</script>
