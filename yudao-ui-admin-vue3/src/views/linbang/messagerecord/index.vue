<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="接收用户" prop="receiverUserKeyword">
        <el-input
          v-model="queryParams.receiverUserKeyword"
          placeholder="请输入用户编号 / 昵称 / 手机号"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="渠道类型" prop="channelType">
        <el-select
          v-model="queryParams.channelType"
          placeholder="请选择渠道类型"
          clearable
          class="!w-220px"
        >
          <el-option
            v-for="item in CHANNEL_TYPE_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
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
      <el-form-item label="发送状态" prop="sendStatus">
        <el-select v-model="queryParams.sendStatus" placeholder="请选择发送状态" clearable class="!w-220px">
          <el-option
            v-for="item in SEND_STATUS_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
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
      <el-table-column label="接收用户" align="center" min-width="220">
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
      <el-table-column label="渠道类型" align="center" prop="channelType" width="120">
        <template #default="{ row }">
          {{ formatChannelType(row.channelType) }}
        </template>
      </el-table-column>
      <el-table-column label="业务类型" align="center" prop="bizType" width="120">
        <template #default="{ row }">
          {{ formatBizType(row.bizType) }}
        </template>
      </el-table-column>
      <el-table-column label="发送状态" align="center" prop="sendStatus" width="110">
        <template #default="{ row }">
          <el-tag :type="getSendStatusTagType(row.sendStatus)">
            {{ formatSendStatus(row.sendStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发送时间" align="center" prop="sendTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="失败原因" align="center" prop="failReason" min-width="200" />
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" align="center" fixed="right" width="100">
        <template #default="{ row }">
          <el-button
            link
            type="primary"
            v-hasPermi="['linbang:message:record:query']"
            @click="openDetail(row.id)"
          >
            详情
          </el-button>
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

  <MessageRecordDetailDialog ref="detailDialogRef" />
</template>

<script setup lang="ts">
import type { FormInstance } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { dateFormatter } from '@/utils/formatTime'
import { MessageRecordApi, type MessageRecord } from '@/api/linbang/messagerecord'
import {
  CHANNEL_TYPE_OPTIONS,
  formatBizType,
  formatChannelType,
  formatSendStatus,
  getSendStatusTagType,
  SEND_STATUS_OPTIONS
} from '../utils/display'
import MessageRecordDetailDialog from './MessageRecordDetailDialog.vue'

defineOptions({ name: 'MessageRecord' })

const loading = ref(false)
const list = ref<MessageRecord[]>([])
const total = ref(0)
const queryFormRef = ref<FormInstance>()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  templateId: undefined as number | undefined,
  receiverUserKeyword: undefined as string | undefined,
  channelType: undefined as string | undefined,
  bizType: undefined as string | undefined,
  sendStatus: undefined as string | undefined,
  createTime: [] as string[]
})

const formatIdFallback = (userId?: number) => {
  return userId ? '用户信息缺失' : '-'
}

const getList = async () => {
  loading.value = true
  try {
    const data = await MessageRecordApi.getMessageRecordPage(queryParams)
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

const detailDialogRef = ref()
const openDetail = (id: number) => {
  detailDialogRef.value.open(id)
}

onMounted(() => {
  getList()
})
</script>
