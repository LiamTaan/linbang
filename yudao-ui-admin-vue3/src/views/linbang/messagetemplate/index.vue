<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="模板编码" prop="templateCode">
        <el-input
          v-model="queryParams.templateCode"
          placeholder="请输入模板编码"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="模板名称" prop="templateName">
        <el-input
          v-model="queryParams.templateName"
          placeholder="请输入模板名称"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="模板类型" prop="templateType">
        <el-input
          v-model="queryParams.templateType"
          placeholder="请输入模板类型"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="渠道类型" prop="channelType">
        <el-select
          v-model="queryParams.channelType"
          placeholder="请选择渠道类型"
          clearable
          class="!w-220px"
        >
          <el-option
            v-for="item in CHANNEL_TYPE_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-220px">
          <el-option
            v-for="item in ENABLE_STATUS_OPTIONS"
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
          type="primary"
          plain
          v-hasPermi="['linbang:message:template:create']"
          @click="openForm('create')"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="模板编码" align="center" prop="templateCode" min-width="150" />
      <el-table-column label="模板名称" align="center" prop="templateName" min-width="160" />
      <el-table-column label="模板类型" align="center" prop="templateType" width="120">
        <template #default="{ row }">
          {{ formatTemplateType(row.templateType) }}
        </template>
      </el-table-column>
      <el-table-column label="渠道类型" align="center" prop="channelType" width="120">
        <template #default="{ row }">
          {{ formatChannelType(row.channelType) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'ENABLE' ? 'success' : 'info'">
            {{ formatEnableStatus(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="模板内容" align="center" prop="content" min-width="240" />
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" align="center" fixed="right" width="100">
        <template #default="{ row }">
          <el-button
            link
            type="primary"
            v-hasPermi="['linbang:message:template:query']"
            @click="openDetail(row.id)"
          >
            详情
          </el-button>
          <el-button
            link
            type="primary"
            v-hasPermi="['linbang:message:template:update']"
            @click="openForm('update', row.id)"
          >
            编辑
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

  <MessageTemplateForm ref="formRef" @success="getList" />
  <MessageTemplateDetailDialog ref="detailDialogRef" />
</template>

<script setup lang="ts">
import type { FormInstance } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { dateFormatter } from '@/utils/formatTime'
import { MessageTemplateApi, type MessageTemplate } from '@/api/linbang/messagetemplate'
import {
  CHANNEL_TYPE_OPTIONS,
  ENABLE_STATUS_OPTIONS,
  formatChannelType,
  formatEnableStatus,
  formatTemplateType
} from '../utils/display'
import MessageTemplateForm from './MessageTemplateForm.vue'
import MessageTemplateDetailDialog from './MessageTemplateDetailDialog.vue'

defineOptions({ name: 'MessageTemplate' })

const loading = ref(false)
const list = ref<MessageTemplate[]>([])
const total = ref(0)
const queryFormRef = ref<FormInstance>()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  templateCode: undefined as string | undefined,
  templateName: undefined as string | undefined,
  templateType: undefined as string | undefined,
  channelType: undefined as string | undefined,
  status: undefined as string | undefined,
  createTime: [] as string[]
})

const getList = async () => {
  loading.value = true
  try {
    const data = await MessageTemplateApi.getMessageTemplatePage(queryParams)
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

const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const detailDialogRef = ref()
const openDetail = (id: number) => {
  detailDialogRef.value.open(id)
}

onMounted(() => {
  getList()
})
</script>
