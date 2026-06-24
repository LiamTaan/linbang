<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="任务名称" prop="taskName">
        <el-input
          v-model="queryParams.taskName"
          placeholder="请输入任务名称"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发送渠道" prop="channelType">
        <el-select v-model="queryParams.channelType" placeholder="请选择发送渠道" clearable class="!w-220px">
          <el-option v-for="item in CHANNEL_TYPE_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="任务状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择任务状态" clearable class="!w-220px">
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_MESSAGE_PUSH_TASK_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="业务类型" prop="bizType">
        <el-input
          v-model="queryParams.bizType"
          placeholder="请输入业务类型"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
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
        <el-button @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" /> 搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" /> 重置
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="任务名称" align="center" prop="taskName" min-width="200" />
      <el-table-column label="目标范围" align="center" prop="targetScope" min-width="140" />
      <el-table-column label="发送渠道" align="center" prop="channelType" width="120">
        <template #default="{ row }">
          {{ formatChannelType(row.channelType) }}
        </template>
      </el-table-column>
      <el-table-column label="消息模板" align="center" prop="templateName" min-width="160" />
      <el-table-column label="业务类型" align="center" prop="bizType" min-width="160">
        <template #default="{ row }">
          {{ formatBizType(row.bizType) }}
        </template>
      </el-table-column>
      <el-table-column label="任务状态" align="center" prop="status" width="110">
        <template #default="{ row }">
          <dict-tag
            v-if="row.status"
            :type="DICT_TYPE.LB_MESSAGE_PUSH_TASK_STATUS"
            :value="row.status"
          />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="计划时间" align="center" prop="plannedSendTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="执行时间" align="center" prop="executeTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="成功数" align="center" prop="successCount" width="100" />
      <el-table-column label="失败数" align="center" prop="failCount" width="100" />
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" align="center" fixed="right" width="80">
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

  <Dialog v-model="detailVisible" title="推送任务详情" width="920px">
    <el-descriptions v-loading="detailLoading" :column="2" border>
      <el-descriptions-item label="任务名称">{{ detailData?.taskName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="目标范围">{{ detailData?.targetScope || '-' }}</el-descriptions-item>
      <el-descriptions-item label="发送渠道">{{ formatChannelType(detailData?.channelType) }}</el-descriptions-item>
      <el-descriptions-item label="模板名称">{{ detailData?.templateName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="业务类型">{{ formatBizType(detailData?.bizType) }}</el-descriptions-item>
      <el-descriptions-item label="业务ID">{{ detailData?.bizId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="任务状态">
        <dict-tag
          v-if="detailData?.status"
          :type="DICT_TYPE.LB_MESSAGE_PUSH_TASK_STATUS"
          :value="detailData.status"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="计划时间">{{ formatDate(detailData?.plannedSendTime) }}</el-descriptions-item>
      <el-descriptions-item label="执行时间">{{ formatDate(detailData?.executeTime) }}</el-descriptions-item>
      <el-descriptions-item label="成功数 / 失败数">
        {{ detailData?.successCount ?? 0 }} / {{ detailData?.failCount ?? 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="任务备注" :span="2">{{ detailData?.creatorRemark || '-' }}</el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">最近消息记录</el-divider>
    <el-table v-if="detailData?.recentRecords?.length" :data="detailData.recentRecords" size="small" border max-height="320">
      <el-table-column label="接收用户" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.receiverUserNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.receiverUserMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.receiverUserNo || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="发送状态" align="center" prop="sendStatus" width="110">
        <template #default="{ row }">
          <el-tag :type="getSendStatusTagType(row.sendStatus)">
            {{ formatSendStatus(row.sendStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="失败原因" align="center" prop="failReason" min-width="180" />
      <el-table-column label="发送时间" align="center" prop="sendTime" :formatter="dateFormatter" width="180" />
    </el-table>
    <el-empty v-else description="暂无关联消息记录" :image-size="80" />
  </Dialog>
</template>

<script setup lang="ts">
import type { FormInstance } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { DICT_TYPE, getStrDictOptions } from '@/utils/dict'
import { dateFormatter, formatDate } from '@/utils/formatTime'
import { MessagePushTaskApi, type MessagePushTask, type MessagePushTaskDetail } from '@/api/linbang/messagepushtask'
import {
  CHANNEL_TYPE_OPTIONS,
  formatBizType,
  formatChannelType,
  formatSendStatus,
  getSendStatusTagType
} from '../utils/display'

defineOptions({ name: 'MessagePushTask' })

const loading = ref(false)
const detailVisible = ref(false)
const detailLoading = ref(false)
const list = ref<MessagePushTask[]>([])
const detailData = ref<MessagePushTaskDetail>()
const total = ref(0)
const queryFormRef = ref<FormInstance>()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  taskName: undefined as string | undefined,
  channelType: undefined as string | undefined,
  status: undefined as string | undefined,
  bizType: undefined as string | undefined,
  createTime: [] as string[]
})

const getList = async () => {
  loading.value = true
  try {
    const data = await MessagePushTaskApi.getMessagePushTaskPage(queryParams)
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
    detailData.value = await MessagePushTaskApi.getMessagePushTask(id)
  } finally {
    detailLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
