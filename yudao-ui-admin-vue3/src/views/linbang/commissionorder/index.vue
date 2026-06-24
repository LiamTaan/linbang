<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="推广员" prop="promoterKeyword">
        <el-input
          v-model="queryParams.promoterKeyword"
          placeholder="请输入用户编号 / 昵称 / 手机号"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="关联用户" prop="userKeyword">
        <el-input
          v-model="queryParams.userKeyword"
          placeholder="请输入用户编号 / 昵称 / 手机号"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="佣金类型" prop="commissionType">
        <el-input
          v-model="queryParams.commissionType"
          placeholder="请输入佣金类型"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-220px">
          <el-option
            v-for="item in COMMISSION_STATUS_OPTIONS"
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
      <el-table-column label="佣金单号" align="center" prop="commissionNo" min-width="160" />
      <el-table-column label="推广员" align="center" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.promoterUserNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.promoterUserMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.promoterUserNo || formatIdFallback(row.promoterId) }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="关联用户" align="center" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.userNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || formatIdFallback(row.userId) }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="来源订单号" align="center" prop="sourceOrderNo" min-width="150" />
      <el-table-column label="来源单元号" align="center" prop="sourceUnitNo" min-width="150" />
      <el-table-column label="佣金类型" align="center" prop="commissionType" width="120" />
      <el-table-column label="佣金金额" align="center" prop="commissionAmount" width="120" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="{ row }">
          <el-tag :type="getCommissionStatusTagType(row.status)">
            {{ formatCommissionStatus(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="结算时间" align="center" prop="settleTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" align="center" fixed="right" width="100">
        <template #default="{ row }">
          <el-button
            link
            type="primary"
            v-hasPermi="['linbang:promote:commission:query']"
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

  <CommissionOrderDetailDialog ref="detailDialogRef" />
</template>

<script setup lang="ts">
import type { FormInstance } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { dateFormatter } from '@/utils/formatTime'
import { CommissionOrderApi, type CommissionOrder } from '@/api/linbang/commissionorder'
import {
  COMMISSION_STATUS_OPTIONS,
  formatCommissionStatus,
  getCommissionStatusTagType
} from '../utils/display'
import CommissionOrderDetailDialog from './CommissionOrderDetailDialog.vue'

defineOptions({ name: 'CommissionOrder' })

const loading = ref(false)
const list = ref<CommissionOrder[]>([])
const total = ref(0)
const queryFormRef = ref<FormInstance>()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  promoterKeyword: undefined as string | undefined,
  userKeyword: undefined as string | undefined,
  commissionType: undefined as string | undefined,
  status: undefined as string | undefined,
  createTime: [] as string[]
})

const formatIdFallback = (value?: number) => {
  return value ? '用户信息缺失' : '-'
}

const getList = async () => {
  loading.value = true
  try {
    const data = await CommissionOrderApi.getCommissionOrderPage(queryParams)
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
