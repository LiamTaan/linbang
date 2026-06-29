<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="100px" class="-mb-15px">
      <el-form-item label="用户关键词" prop="userKeyword">
        <el-input v-model="queryParams.userKeyword" placeholder="用户编号/昵称/手机号" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="脱敏词" prop="word">
        <el-input v-model="queryParams.word" placeholder="请输入脱敏词" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="适用场景" prop="sceneType">
        <el-input v-model="queryParams.sceneType" placeholder="如 ORDER_CREATE" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" clearable class="!w-220px" placeholder="请选择状态">
          <el-option v-for="item in ENABLE_STATUS_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="用户" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.userNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="脱敏词" prop="word" min-width="180" />
      <el-table-column label="适用场景" prop="sceneType" width="160" />
      <el-table-column label="状态" prop="status" width="100" />
      <el-table-column label="备注" prop="remark" min-width="180" />
      <el-table-column label="创建时间" prop="createTime" width="180" />
    </el-table>
    <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </ContentWrap>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { UserSensitiveCustomWordApi, type UserSensitiveCustomWord } from '@/api/linbang/usersensitivecustomword'
import { ENABLE_STATUS_OPTIONS } from '../utils/display'

defineOptions({ name: 'UserSensitiveCustomWord' })

const loading = ref(false)
const list = ref<UserSensitiveCustomWord[]>([])
const total = ref(0)
const queryFormRef = ref()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userKeyword: undefined as string | undefined,
  word: undefined as string | undefined,
  sceneType: undefined as string | undefined,
  status: undefined as string | undefined
})

const getList = async () => {
  loading.value = true
  try {
    const data = await UserSensitiveCustomWordApi.getPage(queryParams)
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

onMounted(() => {
  getList()
})
</script>
