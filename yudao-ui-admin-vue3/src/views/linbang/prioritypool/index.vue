<template>
  <ContentWrap>
    <el-form :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="服务商 ID">
        <el-input v-model="queryParams.merchantId" placeholder="请输入服务商 ID" class="!w-220px" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-input v-model="queryParams.status" placeholder="请输入状态" class="!w-220px" clearable />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true">
      <el-table-column label="服务商 ID" prop="merchantId" width="110" />
      <el-table-column label="用户 ID" prop="userId" width="100" />
      <el-table-column label="状态" prop="status" width="120" />
      <el-table-column label="原因编码" prop="reasonCode" min-width="140" />
      <el-table-column label="原因说明" prop="reasonRemark" min-width="220" />
      <el-table-column label="当前生效" width="100">
        <template #default="{ row }">{{ formatBooleanYesNo(row.currentFlag) }}</template>
      </el-table-column>
      <el-table-column label="生效时间" prop="effectiveTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="失效时间" prop="expireTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="warning" @click="freeze(row)">冻结</el-button>
          <el-button link type="primary" @click="unfreeze(row)">解冻重算</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </ContentWrap>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useMessage } from '@/hooks/web/useMessage'
import { dateFormatter } from '@/utils/formatTime'
import { formatBooleanYesNo } from '../utils/display'
import { PriorityPoolApi, type PriorityPoolRecord } from '@/api/linbang/prioritypool'

defineOptions({ name: 'LinbangPriorityPool' })

const message = useMessage()
const loading = ref(false)
const list = ref<PriorityPoolRecord[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  merchantId: undefined as number | undefined,
  status: undefined as string | undefined
})

const getList = async () => {
  loading.value = true
  try {
    const data = await PriorityPoolApi.getPriorityPoolPage(queryParams)
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

const freeze = async (row: PriorityPoolRecord) => {
  await PriorityPoolApi.freezePriorityPool({ merchantId: row.merchantId!, reasonRemark: '后台手动冻结' })
  message.success('已冻结优先池资格')
  getList()
}

const unfreeze = async (row: PriorityPoolRecord) => {
  await PriorityPoolApi.unfreezePriorityPool({ merchantId: row.merchantId!, reasonRemark: '后台手动解冻' })
  message.success('已解冻并触发重算')
  getList()
}

onMounted(() => getList())
</script>
