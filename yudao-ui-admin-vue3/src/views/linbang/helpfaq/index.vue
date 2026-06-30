<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="问题标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入问题标题"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="问题分类" prop="categoryCode">
        <el-select v-model="queryParams.categoryCode" placeholder="请选择分类" clearable class="!w-220px">
          <el-option
            v-for="item in HELP_FAQ_CATEGORY_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-180px">
          <el-option v-for="item in ENABLE_STATUS_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" /> 搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" /> 重置
        </el-button>
        <el-button type="primary" plain v-hasPermi="['linbang:help:faq:create']" @click="openForm('create')">
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          :loading="exportLoading"
          v-hasPermi="['linbang:help:faq:export']"
          @click="handleExport"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="分类" align="center" min-width="150">
        <template #default="{ row }">
          <div class="leading-20px">
            <div>{{ row.categoryName || formatHelpFaqCategory(row.categoryCode) }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.categoryCode }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="问题标题" align="center" prop="title" min-width="220" />
      <el-table-column label="答案正文" align="center" prop="content" min-width="320" />
      <el-table-column label="图标" align="center" prop="icon" width="100" />
      <el-table-column label="排序" align="center" prop="sortNo" width="90" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'ENABLE' ? 'success' : 'info'">
            {{ formatEnableStatus(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" align="center" fixed="right" width="150">
        <template #default="{ row }">
          <el-button link type="primary" v-hasPermi="['linbang:help:faq:update']" @click="openForm('update', row.id)">
            编辑
          </el-button>
          <el-button link type="danger" v-hasPermi="['linbang:help:faq:delete']" @click="handleDelete(row.id)">
            删除
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

  <Dialog v-model="formVisible" :title="formTitle" width="720px">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="110px">
      <el-form-item label="问题分类" prop="categoryCode">
        <el-select v-model="formData.categoryCode" placeholder="请选择分类" class="!w-260px" @change="handleCategoryChange">
          <el-option
            v-for="item in HELP_FAQ_CATEGORY_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="分类名称" prop="categoryName">
        <el-input v-model="formData.categoryName" placeholder="请输入 App 展示分类名称" />
      </el-form-item>
      <el-form-item label="问题标题" prop="title">
        <el-input v-model="formData.title" placeholder="请输入问题标题" />
      </el-form-item>
      <el-form-item label="答案正文" prop="content">
        <el-input v-model="formData.content" type="textarea" :rows="6" placeholder="请输入问题答案、处理路径和注意事项" />
      </el-form-item>
      <el-form-item label="图标标识" prop="icon">
        <el-input v-model="formData.icon" placeholder="fund / order / verify / order_match / voice" />
      </el-form-item>
      <el-form-item label="排序号" prop="sortNo">
        <el-input-number v-model="formData.sortNo" :min="0" :max="9999" controls-position="right" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio-button v-for="item in ENABLE_STATUS_OPTIONS" :key="item.value" :label="item.value">
            {{ item.label }}
          </el-radio-button>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="formVisible = false">取消</el-button>
      <el-button type="primary" :loading="formLoading" @click="submitForm">确定</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { useMessage } from '@/hooks/web/useMessage'
import { HelpFaqApi, type HelpFaq } from '@/api/linbang/helpfeedback'
import {
  ENABLE_STATUS_OPTIONS,
  HELP_FAQ_CATEGORY_OPTIONS,
  formatEnableStatus,
  formatHelpFaqCategory
} from '../utils/display'

defineOptions({ name: 'HelpFaq' })

const message = useMessage()
const loading = ref(false)
const exportLoading = ref(false)
const formVisible = ref(false)
const formLoading = ref(false)
const formTitle = ref('新增常见问题')
const formType = ref<'create' | 'update'>('create')
const list = ref<HelpFaq[]>([])
const total = ref(0)
const queryFormRef = ref<FormInstance>()
const formRef = ref<FormInstance>()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  title: undefined as string | undefined,
  categoryCode: undefined as string | undefined,
  status: undefined as string | undefined
})

const formData = reactive<HelpFaq>({
  id: undefined,
  categoryCode: 'FUNDS',
  categoryName: '资金与提现',
  title: '',
  content: '',
  icon: 'fund',
  sortNo: 10,
  status: 'ENABLE'
})

const formRules: FormRules = {
  categoryCode: [{ required: true, message: '请选择问题分类', trigger: 'change' }],
  categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  title: [{ required: true, message: '请输入问题标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入答案正文', trigger: 'blur' }],
  sortNo: [{ required: true, message: '请输入排序号', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const categoryIconMap: Record<string, string> = {
  FUNDS: 'fund',
  ORDER: 'order',
  AUTH: 'verify',
  MATCH: 'order_match',
  PROMOTE: 'voice'
}

const resetFormData = () => {
  Object.assign(formData, {
    id: undefined,
    categoryCode: 'FUNDS',
    categoryName: '资金与提现',
    title: '',
    content: '',
    icon: 'fund',
    sortNo: 10,
    status: 'ENABLE'
  })
  formRef.value?.clearValidate()
}

const handleCategoryChange = (categoryCode: string) => {
  formData.categoryName = `${formatHelpFaqCategory(categoryCode)}`
  formData.icon = categoryIconMap[categoryCode] || 'info'
}

const getList = async () => {
  loading.value = true
  try {
    const data = await HelpFaqApi.getHelpFaqPage(queryParams)
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

const openForm = async (type: 'create' | 'update', id?: number) => {
  resetFormData()
  formType.value = type
  formTitle.value = type === 'create' ? '新增常见问题' : '编辑常见问题'
  formVisible.value = true
  if (type === 'update' && id) {
    formLoading.value = true
    try {
      Object.assign(formData, await HelpFaqApi.getHelpFaq(id))
    } finally {
      formLoading.value = false
    }
  }
}

const submitForm = async () => {
  await formRef.value?.validate()
  formLoading.value = true
  try {
    if (formType.value === 'create') {
      await HelpFaqApi.createHelpFaq(formData)
      message.success('新增成功')
    } else {
      await HelpFaqApi.updateHelpFaq(formData)
      message.success('修改成功')
    }
    formVisible.value = false
    await getList()
  } finally {
    formLoading.value = false
  }
}

const handleDelete = async (id: number) => {
  await message.delConfirm()
  await HelpFaqApi.deleteHelpFaq(id)
  message.success('删除成功')
  await getList()
}

const handleExport = async () => {
  try {
    await message.exportConfirm()
    exportLoading.value = true
    const data = await HelpFaqApi.exportHelpFaq(queryParams)
    download.excel(data, '常见问题列表.xls')
  } finally {
    exportLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
