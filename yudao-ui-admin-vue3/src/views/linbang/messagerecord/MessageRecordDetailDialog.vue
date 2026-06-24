<template>
  <Dialog v-model="dialogVisible" title="消息记录详情" width="820px" :loading="detailLoading">
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item label="接收用户">{{ formatReceiverUserDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="渠道类型">{{ formatChannelType(detailData.channelType) }}</el-descriptions-item>
      <el-descriptions-item label="业务类型">{{ formatBizType(detailData.bizType) }}</el-descriptions-item>
      <el-descriptions-item label="发送状态">
        <el-tag :type="getSendStatusTagType(detailData.sendStatus)">
          {{ formatSendStatus(detailData.sendStatus) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="模板名称">{{ detailData.template?.templateName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="模板编码">{{ detailData.template?.templateCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="业务标识">{{ detailData.bizId ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="发送时间">{{ formatDate(detailData.sendTime) }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDate(detailData.createTime) }}</el-descriptions-item>
      <el-descriptions-item label="更新时间">{{ formatDate(detailData.updateTime) }}</el-descriptions-item>
    </el-descriptions>
    <el-divider />
    <el-descriptions :column="1" border direction="vertical">
      <el-descriptions-item label="失败原因">{{ detailData.failReason || '-' }}</el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">模板摘要</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="模板编码">
        {{ detailData.template?.templateCode || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="模板名称">
        {{ detailData.template?.templateName || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="模板类型">
        {{ formatTemplateType(detailData.template?.templateType) }}
      </el-descriptions-item>
      <el-descriptions-item label="模板渠道">
        {{ formatChannelType(detailData.template?.channelType) }}
      </el-descriptions-item>
      <el-descriptions-item label="模板状态">
        <el-tag :type="detailData.template?.status === 'ENABLE' ? 'success' : 'info'">
          {{ formatEnableStatus(detailData.template?.status) }}
        </el-tag>
      </el-descriptions-item>
    </el-descriptions>
  </Dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { formatDate } from '@/utils/formatTime'
import { MessageRecordApi, type MessageRecordDetail } from '@/api/linbang/messagerecord'
import {
  formatBizType,
  formatChannelType,
  formatEnableStatus,
  formatSendStatus,
  formatTemplateType,
  getSendStatusTagType
} from '../utils/display'

defineOptions({ name: 'MessageRecordDetailDialog' })

const dialogVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref<MessageRecordDetail>({} as MessageRecordDetail)

const formatReceiverUserDisplay = () => {
  const summary = [detailData.value.receiverUserNickname, detailData.value.receiverUserMobile, detailData.value.receiverUserNo]
    .filter(Boolean)
    .join(' / ')
  return summary || (detailData.value.receiverUserId ? '用户信息缺失' : '-')
}

const open = async (id: number) => {
  dialogVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await MessageRecordApi.getMessageRecord(id)
  } finally {
    detailLoading.value = false
  }
}

defineExpose({ open })
</script>
