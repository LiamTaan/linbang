<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="规则名称" prop="ruleName">
        <el-input v-model="queryParams.ruleName" placeholder="请输入规则名称" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="规则编码" prop="ruleCode">
        <el-input v-model="queryParams.ruleCode" placeholder="请输入规则编码" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="计价方式" prop="pricingMode">
        <el-select v-model="queryParams.pricingMode" clearable class="!w-220px" placeholder="请选择计价方式">
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_PRICING_MODE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="拆分方式" prop="splitMode">
        <el-input v-model="queryParams.splitMode" placeholder="如 BY_PROGRESS" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" clearable class="!w-220px" placeholder="请选择状态">
          <el-option v-for="item in ENABLE_STATUS_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button type="primary" plain @click="openForm('create')" v-hasPermi="['linbang:order:split-rule:create']">
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="规则名称" prop="ruleName" min-width="180" />
      <el-table-column label="规则编码" prop="ruleCode" width="160" />
      <el-table-column label="类目" prop="categoryName" min-width="140" />
      <el-table-column label="计价方式" min-width="180">
        <template #default="{ row }">{{ (row.applicablePricingModes || []).join(' / ') || '全部' }}</template>
      </el-table-column>
      <el-table-column label="拆分方式" prop="splitMode" width="140" />
      <el-table-column label="金额阈值" prop="minOrderAmount" width="120" />
      <el-table-column label="人数阈值" prop="minWorkerCount" width="120" />
      <el-table-column label="默认单元数" prop="defaultUnitCount" width="120" />
      <el-table-column label="单元金额上限" prop="unitAmountLimit" width="130" />
      <el-table-column label="状态" prop="status" width="100" />
      <el-table-column label="操作" align="center" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openForm('update', row.id)" v-hasPermi="['linbang:order:split-rule:update']">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)" v-hasPermi="['linbang:order:split-rule:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </ContentWrap>

  <OrderSplitRuleForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { getStrDictOptions, DICT_TYPE } from '@/utils/dict'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
import { OrderSplitRuleApi, type OrderSplitRule } from '@/api/linbang/ordersplitrule'
import { ENABLE_STATUS_OPTIONS } from '../utils/display'
import OrderSplitRuleForm from './OrderSplitRuleForm.vue'

defineOptions({ name: 'OrderSplitRule' })

const { t } = useI18n()
const message = useMessage()
const loading = ref(false)
const list = ref<OrderSplitRule[]>([])
const total = ref(0)
const queryFormRef = ref()
const formRef = ref()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  ruleName: undefined as string | undefined,
  ruleCode: undefined as string | undefined,
  pricingMode: undefined as string | undefined,
  splitMode: undefined as string | undefined,
  status: undefined as string | undefined
})

const getList = async () => {
  loading.value = true
  try {
    const data = await OrderSplitRuleApi.getPage(queryParams)
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

const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await OrderSplitRuleApi.delete(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

onMounted(() => {
  getList()
})
</script>
