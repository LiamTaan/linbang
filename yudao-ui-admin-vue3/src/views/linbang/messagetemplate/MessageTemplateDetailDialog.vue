<template>
  <Dialog v-model="dialogVisible" title="消息模板详情" width="900px" :loading="detailLoading">
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item label="模板编码">{{ detailData.templateCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="模板名称">{{ detailData.templateName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="模板类型">{{ formatTemplateType(detailData.templateType) }}</el-descriptions-item>
      <el-descriptions-item label="渠道类型">{{ formatChannelType(detailData.channelType) }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="detailData.status === 'ENABLE' ? 'success' : 'info'">
          {{ formatEnableStatus(detailData.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDate(detailData.createTime) }}</el-descriptions-item>
      <el-descriptions-item label="更新时间">{{ formatDate(detailData.updateTime) }}</el-descriptions-item>
    </el-descriptions>
    <el-divider />
    <el-descriptions :column="1" border direction="vertical">
      <el-descriptions-item label="模板内容">{{ detailData.content || '-' }}</el-descriptions-item>
    </el-descriptions>
    <el-divider />
    <el-descriptions :column="4" border>
      <el-descriptions-item label="发送总数">{{ detailData.sendCount ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="成功数">{{ detailData.successCount ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="失败数">{{ detailData.failedCount ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="待发送数">{{ detailData.pendingCount ?? 0 }}</el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">渠道分布</el-divider>
    <el-table :data="detailData.channelStats || []" size="small" border>
      <el-table-column label="渠道类型" prop="channelType" min-width="160">
        <template #default="{ row }">
          {{ formatChannelType(row.channelType) }}
        </template>
      </el-table-column>
      <el-table-column label="记录数" prop="recordCount" width="120" />
    </el-table>
    <el-divider content-position="left">最近发送记录</el-divider>
    <el-table :data="detailData.recentRecords || []" size="small" border>
      <el-table-column label="接收用户" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.receiverUserNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.receiverUserMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">
              {{ row.receiverUserNo || formatIdFallback(row.receiverUserId) }}
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="渠道类型" prop="channelType" width="120">
        <template #default="{ row }">
          {{ formatChannelType(row.channelType) }}
        </template>
      </el-table-column>
      <el-table-column label="业务类型" prop="bizType" width="120">
        <template #default="{ row }">
          {{ formatBizType(row.bizType) }}
        </template>
      </el-table-column>
      <el-table-column label="业务标识" prop="bizId" width="100" />
      <el-table-column label="发送状态" prop="sendStatus" width="120">
        <template #default="{ row }">
          <el-tag :type="getSendStatusTagType(row.sendStatus)">
            {{ formatSendStatus(row.sendStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发送时间" prop="sendTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="失败原因" prop="failReason" min-width="180" />
    </el-table>
  </Dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { dateFormatter, formatDate } from '@/utils/formatTime'
import { MessageTemplateApi, type MessageTemplateDetail } from '@/api/linbang/messagetemplate'
import {
  formatBizType,
  formatChannelType,
  formatEnableStatus,
  formatSendStatus,
  formatTemplateType,
  getSendStatusTagType
} from '../utils/display'

defineOptions({ name: 'MessageTemplateDetailDialog' })

const dialogVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref<MessageTemplateDetail>({} as MessageTemplateDetail)

const formatIdFallback = (userId?: number) => {
  return userId ? '用户信息缺失' : '-'
}

const open = async (id: number) => {
  dialogVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await MessageTemplateApi.getMessageTemplate(id)
    if (!detailData.value.channelStats) {
      detailData.value.channelStats = []
    }
    if (!detailData.value.recentRecords) {
      detailData.value.recentRecords = []
    }
  } finally {
    detailLoading.value = false
  }
}

defineExpose({ open })
</script>
