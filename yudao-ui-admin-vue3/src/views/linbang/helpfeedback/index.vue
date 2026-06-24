<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="用户" prop="userKeyword">
        <el-input
          v-model="queryParams.userKeyword"
          placeholder="请输入用户编号 / 昵称 / 手机号"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="反馈分类" prop="feedbackType">
        <el-input
          v-model="queryParams.feedbackType"
          placeholder="请输入反馈分类"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="联系电话" prop="contactMobile">
        <el-input
          v-model="queryParams.contactMobile"
          placeholder="请输入联系电话"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="处理状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择处理状态" clearable class="!w-220px">
          <el-option
            v-for="item in HELP_FEEDBACK_STATUS_OPTIONS"
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
        <el-button
          type="success"
          plain
          :loading="exportLoading"
          v-hasPermi="['linbang:help:feedback:export']"
          @click="handleExport"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="用户" align="center" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.userNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || formatIdFallback(row.userId) }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="反馈分类" align="center" prop="feedbackType" width="140" />
      <el-table-column label="反馈内容" align="center" prop="content" min-width="260" />
      <el-table-column label="联系电话" align="center" prop="contactMobile" width="140" />
      <el-table-column label="处理状态" align="center" prop="status" width="120">
        <template #default="{ row }">
          <el-tag :type="row.status === 'FINISHED' ? 'success' : row.status === 'PROCESSING' ? 'warning' : 'info'">
            {{ formatHelpFeedbackStatus(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="处理人" align="center" prop="handleBy" width="100" />
      <el-table-column label="处理备注" align="center" prop="handleRemark" min-width="180" />
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" align="center" fixed="right" width="100">
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

  <Dialog v-model="detailVisible" title="帮助反馈详情" width="760px">
    <el-descriptions v-loading="detailLoading" :column="2" border>
      <el-descriptions-item label="用户">{{ formatDetailUserDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="反馈分类">{{ detailData?.feedbackType || '-' }}</el-descriptions-item>
      <el-descriptions-item label="联系电话">{{ detailData?.contactMobile || '-' }}</el-descriptions-item>
      <el-descriptions-item label="处理状态">{{ formatHelpFeedbackStatus(detailData?.status) }}</el-descriptions-item>
      <el-descriptions-item label="处理人">{{ detailData?.handleBy || '-' }}</el-descriptions-item>
      <el-descriptions-item label="用户角色">
        <dict-tag
          v-if="detailData?.user?.currentRoleCode"
          :type="DICT_TYPE.LB_ROLE_CODE"
          :value="detailData.user.currentRoleCode"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="用户状态">{{ formatEnableStatus(detailData?.user?.status) }}</el-descriptions-item>
      <el-descriptions-item label="反馈内容" :span="2">
        {{ detailData?.content || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="附件地址" :span="2">
        {{ detailData?.attachmentUrls || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="处理备注" :span="2">
        {{ detailData?.handleRemark || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="创建时间" :span="2">
        {{ formatDate(detailData?.createTime) }}
      </el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">统计概览</el-divider>
    <el-row :gutter="12" class="mb-16px">
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">同用户反馈数</div>
          <div class="mt-8px text-24px font-600">{{ detailData?.summary?.sameUserFeedbackCount ?? 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">同分类反馈数</div>
          <div class="mt-8px text-24px font-600">{{ detailData?.summary?.sameTypeFeedbackCount ?? 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">待处理 / 处理中 / 已完成</div>
          <div class="mt-8px text-18px font-600">
            {{ detailData?.summary?.pendingCount ?? 0 }} / {{ detailData?.summary?.processingCount ?? 0 }} /
            {{ detailData?.summary?.finishedCount ?? 0 }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-divider content-position="left">近期同用户反馈</el-divider>
    <el-table
      v-if="detailData?.relatedFeedbacks?.length"
      :data="detailData.relatedFeedbacks"
      size="small"
      border
      max-height="260"
    >
      <el-table-column label="分类" prop="feedbackType" width="140" />
      <el-table-column label="联系电话" prop="contactMobile" width="140" />
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          {{ formatHelpFeedbackStatus(row.status) }}
        </template>
      </el-table-column>
      <el-table-column label="处理备注" prop="handleRemark" min-width="180" />
      <el-table-column label="创建时间" prop="createTime" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无相关反馈" :image-size="80" />
  </Dialog>
</template>

<script setup lang="ts">
import type { FormInstance } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { DICT_TYPE } from '@/utils/dict'
import { formatDate, dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { useMessage } from '@/hooks/web/useMessage'
import { HelpFeedbackApi, type HelpFeedback, type HelpFeedbackDetail } from '@/api/linbang/helpfeedback'
import {
  formatEnableStatus,
  formatHelpFeedbackStatus,
  HELP_FEEDBACK_STATUS_OPTIONS
} from '../utils/display'

defineOptions({ name: 'HelpFeedback' })

const message = useMessage()
const loading = ref(false)
const exportLoading = ref(false)
const detailVisible = ref(false)
const detailLoading = ref(false)
const list = ref<HelpFeedback[]>([])
const detailData = ref<HelpFeedbackDetail>()
const total = ref(0)
const queryFormRef = ref<FormInstance>()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userKeyword: undefined as string | undefined,
  feedbackType: undefined as string | undefined,
  contactMobile: undefined as string | undefined,
  status: undefined as string | undefined,
  createTime: [] as string[]
})

const formatIdFallback = (userId?: number) => {
  return userId ? '用户信息缺失' : '-'
}

const formatDetailUserDisplay = () => {
  const user = detailData.value?.user
  const summary = [user?.nickname, user?.mobile, user?.userNo].filter(Boolean).join(' / ')
  return summary || formatIdFallback(detailData.value?.userId)
}

const getList = async () => {
  loading.value = true
  try {
    const data = await HelpFeedbackApi.getHelpFeedbackPage(queryParams)
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

const handleExport = async () => {
  try {
    await message.exportConfirm()
    exportLoading.value = true
    const data = await HelpFeedbackApi.exportHelpFeedback(queryParams)
    download.excel(data, '帮助反馈列表.xls')
  } finally {
    exportLoading.value = false
  }
}

const openDetail = async (id: number) => {
  detailVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await HelpFeedbackApi.getHelpFeedback(id)
  } finally {
    detailLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
