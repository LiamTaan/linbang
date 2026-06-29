<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="标题" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入推广标题" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-input v-model="queryParams.status" placeholder="如 APPROVED/OFFLINE" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="系统审" prop="systemAuditResult">
        <el-input v-model="queryParams.systemAuditResult" placeholder="如 PASS/REJECT" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="人工审" prop="manualAuditResult">
        <el-input v-model="queryParams.manualAuditResult" placeholder="如 APPROVED/REJECTED" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="提交用户" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.userNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="标题" prop="title" min-width="220" />
      <el-table-column label="状态" prop="status" width="150" />
      <el-table-column label="系统审核" prop="systemAuditResult" width="120" />
      <el-table-column label="人工审核" prop="manualAuditResult" width="120" />
      <el-table-column label="系统备注" prop="systemAuditRemark" min-width="220" />
      <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" fixed="right" width="220">
        <template #default="{ row }">
          <el-button link type="primary" @click="showAudit(row, 'APPROVED')">通过</el-button>
          <el-button link type="danger" @click="showAudit(row, 'REJECTED')">驳回</el-button>
          <el-button link type="warning" @click="showOffline(row)">下架</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </ContentWrap>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import { useMessage } from '@/hooks/web/useMessage'
import { dateFormatter } from '@/utils/formatTime'
import { PromoteContentApi, type PromoteContent } from '@/api/linbang/promotecontent'

defineOptions({ name: 'PromoteContent' })

const loading = ref(false)
const list = ref<PromoteContent[]>([])
const total = ref(0)
const queryFormRef = ref()
const message = useMessage()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  title: undefined as string | undefined,
  status: undefined as string | undefined,
  systemAuditResult: undefined as string | undefined,
  manualAuditResult: undefined as string | undefined
})

const getList = async () => {
  loading.value = true
  try {
    const data = await PromoteContentApi.getPage(queryParams)
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

const showAudit = async (row: PromoteContent, auditResult: string) => {
  const auditRemark = await ElMessageBox.prompt(
    auditResult === 'APPROVED' ? '请输入审核备注' : '请输入驳回原因或审核备注',
    auditResult === 'APPROVED' ? '通过审核' : '驳回审核',
    { inputType: 'textarea' }
  )
  await PromoteContentApi.audit({
    id: row.id,
    auditResult,
    auditRemark: auditResult === 'APPROVED' ? auditRemark.value : undefined,
    rejectReason: auditResult === 'REJECTED' ? auditRemark.value : undefined
  })
  await message.success('审核成功')
  await getList()
}

const showOffline = async (row: PromoteContent) => {
  const offlineReason = await ElMessageBox.prompt('请输入下架原因', '下架推广内容', { inputType: 'textarea' })
  await PromoteContentApi.offline({
    id: row.id,
    offlineReason: offlineReason.value
  })
  await message.success('下架成功')
  await getList()
}

onMounted(() => {
  getList()
})
</script>
