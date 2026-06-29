<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="状态" prop="status">
        <el-input v-model="queryParams.status" placeholder="如 PENDING/APPROVED" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="内容 ID" prop="contentId">
        <el-input v-model="queryParams.contentId" placeholder="请输入推广内容 ID" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="申诉用户" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.userNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="内容标题" prop="contentTitle" min-width="220" />
      <el-table-column label="申诉原因" prop="appealReason" min-width="260" />
      <el-table-column label="状态" prop="status" width="120" />
      <el-table-column label="审核备注" prop="auditRemark" min-width="220" />
      <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" fixed="right" width="180">
        <template #default="{ row }">
          <el-button link type="primary" @click="audit(row, 'APPROVED')">通过</el-button>
          <el-button link type="danger" @click="audit(row, 'REJECTED')">驳回</el-button>
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
import { PromoteAppealApi, type PromoteAppeal } from '@/api/linbang/promoteappeal'

defineOptions({ name: 'PromoteAppeal' })

const loading = ref(false)
const list = ref<PromoteAppeal[]>([])
const total = ref(0)
const queryFormRef = ref()
const message = useMessage()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  status: undefined as string | undefined,
  contentId: undefined as number | undefined
})

const getList = async () => {
  loading.value = true
  try {
    const data = await PromoteAppealApi.getPage(queryParams)
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

const audit = async (row: PromoteAppeal, auditResult: string) => {
  const result = await ElMessageBox.prompt(
    auditResult === 'APPROVED' ? '请输入审核备注' : '请输入驳回原因',
    auditResult === 'APPROVED' ? '通过申诉' : '驳回申诉',
    { inputType: 'textarea' }
  )
  await PromoteAppealApi.audit({
    id: row.id,
    auditResult,
    auditRemark: auditResult === 'APPROVED' ? result.value : undefined,
    rejectReason: auditResult === 'REJECTED' ? result.value : undefined
  })
  await message.success('申诉处理成功')
  await getList()
}

onMounted(() => {
  getList()
})
</script>
