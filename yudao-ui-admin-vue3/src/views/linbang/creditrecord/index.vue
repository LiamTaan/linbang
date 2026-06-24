<template>
  <ContentWrap>
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="82px"
    >
      <el-form-item label="用户" prop="userKeyword">
        <el-input
          v-model="queryParams.userKeyword"
          placeholder="请输入用户编号 / 昵称 / 手机号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item label="规则编码" prop="ruleCode">
        <el-input
          v-model="queryParams.ruleCode"
          placeholder="请输入规则编码"
          clearable
          @keyup.enter="handleQuery"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item label="规则名称" prop="ruleName">
        <el-input
          v-model="queryParams.ruleName"
          placeholder="请输入规则名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item label="触发类型" prop="triggerType">
        <el-select
          v-model="queryParams.triggerType"
          placeholder="请选择触发类型"
          clearable
          class="!w-220px"
        >
          <el-option
            v-for="item in TRIGGER_TYPE_OPTIONS"
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
          @keyup.enter="handleQuery"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['linbang:review:credit-record:export']"
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
          <div class="font-600">{{ row.userNickname || '-' }}</div>
          <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
          <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || '-' }}</div>
        </template>
      </el-table-column>
      <el-table-column label="服务商" align="center" min-width="220">
        <template #default="{ row }">
          <div class="font-600">{{ row.merchantName || '-' }}</div>
          <div class="text-[var(--el-text-color-secondary)]">{{ row.merchantContactMobile || '-' }}</div>
          <div class="text-[var(--el-text-color-secondary)]">{{ row.merchantContactName || '-' }}</div>
        </template>
      </el-table-column>
      <el-table-column label="规则编码" align="center" prop="ruleCode" min-width="140" />
      <el-table-column label="规则名称" align="center" prop="ruleName" min-width="180" />
      <el-table-column label="分值变动" align="center" prop="scoreChange" width="100" />
      <el-table-column label="变动前分值" align="center" prop="beforeScore" width="110" />
      <el-table-column label="变动后分值" align="center" prop="afterScore" width="110" />
      <el-table-column label="触发类型" align="center" prop="triggerType" width="120">
        <template #default="{ row }">
          {{ formatTriggerType(row.triggerType) }}
        </template>
      </el-table-column>
      <el-table-column label="业务类型" align="center" prop="bizType" width="120">
        <template #default="{ row }">
          {{ formatBizType(row.bizType) }}
        </template>
      </el-table-column>
      <el-table-column label="业务对象" align="center" prop="bizDisplay" min-width="180" />
      <el-table-column label="备注" align="center" prop="remark" min-width="180" />
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="180"
      />
      <el-table-column label="操作" align="center" width="100" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openDetail(scope.row.id)"
            v-hasPermi="['linbang:review:credit-record:query']"
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

  <CreditRecordDetailDialog ref="detailDialogRef" />
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { useMessage } from '@/hooks/web/useMessage'
import { CreditRecordApi, type CreditRecord } from '@/api/linbang/creditrecord'
import { formatBizType, formatTriggerType, TRIGGER_TYPE_OPTIONS } from '../utils/display'
import CreditRecordDetailDialog from './CreditRecordDetailDialog.vue'

defineOptions({ name: 'CreditRecord' })

const message = useMessage()

const loading = ref(false)
const exportLoading = ref(false)
const list = ref<CreditRecord[]>([])
const total = ref(0)
const queryFormRef = ref()
const detailDialogRef = ref()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userKeyword: undefined,
  ruleCode: undefined,
  ruleName: undefined,
  triggerType: undefined,
  bizType: undefined,
  createTime: []
})

const getList = async () => {
  loading.value = true
  try {
    const data = await CreditRecordApi.getCreditRecordPage(queryParams)
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
  queryFormRef.value.resetFields()
  handleQuery()
}

const openDetail = (id: number) => {
  detailDialogRef.value.open(id)
}

const handleExport = async () => {
  try {
    await message.exportConfirm()
    exportLoading.value = true
    const data = await CreditRecordApi.exportCreditRecord(queryParams)
    download.excel(data, '信用记录.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
