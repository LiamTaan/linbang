<template>
  <ContentWrap>
    <el-form :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="服务商 ID">
        <el-input v-model="queryParams.merchantId" placeholder="请输入服务商 ID" class="!w-220px" clearable />
      </el-form-item>
      <el-form-item label="审核状态">
        <el-input v-model="queryParams.auditStatus" placeholder="请输入审核状态" class="!w-220px" clearable />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true">
      <el-table-column label="服务商 ID" prop="merchantId" width="110" />
      <el-table-column label="标题" prop="title" min-width="180" />
      <el-table-column label="说明" prop="description" min-width="220" />
      <el-table-column label="审核状态" prop="auditStatus" width="120" />
      <el-table-column label="优先权生效" width="100">
        <template #default="{ row }">{{ formatBooleanYesNo(row.priorityEnabled) }}</template>
      </el-table-column>
      <el-table-column label="开始时间" prop="effectiveStartTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="结束时间" prop="effectiveEndTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="loadParticipations(row)">参与记录</el-button>
          <el-button link type="success" @click="audit(row, 'APPROVED')">通过</el-button>
          <el-button link type="danger" @click="audit(row, 'REJECTED')">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </ContentWrap>

  <ContentWrap v-if="selectedRewardId">
    <template #header>
      <span>参与记录</span>
    </template>
    <el-table :data="participationList" :stripe="true">
      <el-table-column label="参与用户 ID" prop="participantUserId" width="120" />
      <el-table-column label="昵称" prop="participantNickname" min-width="120" />
      <el-table-column label="手机号" prop="participantMobile" min-width="140" />
      <el-table-column label="状态" prop="status" width="120" />
      <el-table-column label="参与说明" prop="participateRemark" min-width="180" />
      <el-table-column label="审核备注" prop="auditRemark" min-width="160" />
      <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
    </el-table>
  </ContentWrap>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useMessage } from '@/hooks/web/useMessage'
import { dateFormatter } from '@/utils/formatTime'
import { formatBooleanYesNo } from '../utils/display'
import {
  ShowcaseRewardApi,
  type ShowcaseReward,
  type ShowcaseRewardParticipation
} from '@/api/linbang/showcasereward'

defineOptions({ name: 'LinbangShowcaseReward' })

const message = useMessage()
const loading = ref(false)
const list = ref<ShowcaseReward[]>([])
const participationList = ref<ShowcaseRewardParticipation[]>([])
const selectedRewardId = ref<number>()
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  merchantId: undefined as number | undefined,
  auditStatus: undefined as string | undefined
})

const getList = async () => {
  loading.value = true
  try {
    const data = await ShowcaseRewardApi.getShowcaseRewardPage(queryParams)
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

const audit = async (row: ShowcaseReward, auditStatus: string) => {
  await ShowcaseRewardApi.auditShowcaseReward({
    id: row.id,
    auditStatus,
    auditRemark: auditStatus === 'APPROVED' ? '审核通过' : '审核驳回',
    rejectReason: auditStatus === 'REJECTED' ? '资料不符合要求' : ''
  })
  message.success('审核已提交')
  getList()
}

const loadParticipations = async (row: ShowcaseReward) => {
  selectedRewardId.value = row.id
  participationList.value = await ShowcaseRewardApi.getParticipationList(row.id)
}

onMounted(() => getList())
</script>
