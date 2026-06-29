<template>
  <ContentWrap>
    <el-form :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="引用类型">
        <el-select v-model="queryParams.refType" clearable class="!w-180px">
          <el-option label="模板" value="TEMPLATE" />
          <el-option label="活动" value="CAMPAIGN" />
        </el-select>
      </el-form-item>
      <el-form-item label="场景编码">
        <el-input v-model="queryParams.sceneCode" placeholder="请输入场景编码" clearable class="!w-220px" />
      </el-form-item>
      <el-form-item label="负责人">
        <el-input v-model="queryParams.owner" placeholder="请输入负责人" clearable class="!w-180px" />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" />搜索</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="引用类型" width="100">
        <template #default="{ row }">{{ formatOptimizationRefType(row.refType) }}</template>
      </el-table-column>
      <el-table-column label="场景编码" prop="sceneCode" min-width="160" />
      <el-table-column label="消息分类" width="100">
        <template #default="{ row }">{{ formatMessageCategory(row.messageCategory) }}</template>
      </el-table-column>
      <el-table-column label="渠道" width="140">
        <template #default="{ row }">{{ formatChannelType(row.channelType) }}</template>
      </el-table-column>
      <el-table-column label="触达率" width="100">
        <template #default="{ row }">{{ formatPercent(row.reachRate) }}</template>
      </el-table-column>
      <el-table-column label="点击率" width="100">
        <template #default="{ row }">{{ formatPercent(row.clickRate) }}</template>
      </el-table-column>
      <el-table-column label="优化备注" prop="optimizationNote" min-width="200" />
      <el-table-column label="下一步" prop="nextAction" min-width="180" />
      <el-table-column label="负责人" prop="owner" width="120" />
      <el-table-column label="截止时间" prop="deadline" width="180" />
      <el-table-column label="操作" fixed="right" width="90">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </ContentWrap>

  <Dialog v-model="dialogVisible" title="编辑优化项" width="620px">
    <el-form :model="formData" label-width="90px">
      <el-form-item label="场景编码">
        <el-input :model-value="formData.sceneCode" disabled />
      </el-form-item>
      <el-form-item label="渠道">
        <el-input :model-value="formatChannelType(formData.channelType)" disabled />
      </el-form-item>
      <el-form-item label="触达率">
        <el-input :model-value="formatPercent(formData.reachRate)" disabled />
      </el-form-item>
      <el-form-item label="点击率">
        <el-input :model-value="formatPercent(formData.clickRate)" disabled />
      </el-form-item>
      <el-form-item label="优化备注">
        <el-input v-model="formData.optimizationNote" type="textarea" :rows="3" placeholder="请输入优化备注" />
      </el-form-item>
      <el-form-item label="下一步">
        <el-input v-model="formData.nextAction" type="textarea" :rows="3" placeholder="请输入下一步动作" />
      </el-form-item>
      <el-form-item label="负责人">
        <el-input v-model="formData.owner" placeholder="请输入负责人" />
      </el-form-item>
      <el-form-item label="截止时间">
        <el-date-picker v-model="formData.deadline" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" class="!w-full" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="save">保存</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useMessage } from '@/hooks/web/useMessage'
import { MessageOptimizationApi, type MessageOptimization } from '@/api/linbang/messageoptimization'
import {
  formatChannelType,
  formatMessageCategory,
  formatOptimizationRefType,
  formatPercent
} from '../utils/display'

defineOptions({ name: 'MessageOptimizationIndex' })

const message = useMessage()
const loading = ref(false)
const total = ref(0)
const list = ref<MessageOptimization[]>([])
const dialogVisible = ref(false)
const formData = reactive<Partial<MessageOptimization>>({
  id: undefined,
  sceneCode: '',
  channelType: '',
  reachRate: undefined,
  clickRate: undefined,
  optimizationNote: '',
  nextAction: '',
  owner: '',
  deadline: ''
})

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  refType: undefined as string | undefined,
  sceneCode: undefined as string | undefined,
  owner: undefined as string | undefined
})

const getList = async () => {
  loading.value = true
  try {
    const data = await MessageOptimizationApi.getPage(queryParams)
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

const openDialog = (row: MessageOptimization) => {
  Object.assign(formData, row)
  dialogVisible.value = true
}

const save = async () => {
  await MessageOptimizationApi.save({
    id: formData.id as number,
    optimizationNote: formData.optimizationNote,
    nextAction: formData.nextAction,
    owner: formData.owner,
    deadline: formData.deadline
  })
  message.success('优化备注已保存')
  dialogVisible.value = false
  getList()
}

onMounted(getList)
</script>
