<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="配置分类" prop="category">
        <el-select v-model="queryParams.category" placeholder="请选择配置分类" clearable class="!w-220px">
          <el-option
            v-for="item in PLATFORM_CONFIG_CATEGORY_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="配置名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入配置名称"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="配置键" prop="key">
        <el-input
          v-model="queryParams.key"
          placeholder="请输入配置键"
          clearable
          class="!w-240px"
          @keyup.enter="handleQuery"
        />
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
          v-hasPermi="['linbang:platform-config:setting:create']"
          @click="openForm('create')"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          :loading="exportLoading"
          v-hasPermi="['linbang:platform-config:setting:export']"
          @click="handleExport"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="配置分类" align="center" prop="category" width="140">
        <template #default="{ row }">
          <el-tag :type="row.category === 'linbang_agreement' ? 'warning' : 'success'">
            {{ formatPlatformConfigCategory(row.category) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="配置名称" align="center" prop="name" min-width="160" />
      <el-table-column label="配置键" align="center" prop="key" min-width="220" />
      <el-table-column label="配置值" align="center" prop="value" min-width="260" />
      <el-table-column label="前台可见" align="center" prop="visible" width="100">
        <template #default="{ row }">
          <el-tag :type="row.visible ? 'success' : 'info'">{{ formatVisibleStatus(row.visible) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" min-width="180" />
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" align="center" fixed="right" width="100">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row.id)">详情</el-button>
          <el-button
            link
            type="primary"
            v-hasPermi="['linbang:platform-config:setting:update']"
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

  <PlatformConfigForm ref="formRef" @success="getList" />

  <Dialog v-model="detailVisible" title="平台配置详情" width="760px">
    <el-descriptions v-loading="detailLoading" :column="2" border>
      <el-descriptions-item label="配置分类">{{ formatPlatformConfigCategory(detailData?.category) }}</el-descriptions-item>
      <el-descriptions-item label="配置名称">{{ detailData?.name || '-' }}</el-descriptions-item>
      <el-descriptions-item label="配置键">{{ detailData?.key || '-' }}</el-descriptions-item>
      <el-descriptions-item label="前台可见">
        {{ formatVisibleStatus(detailData?.visible) }}
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDate(detailData?.createTime) }}</el-descriptions-item>
      <el-descriptions-item label="配置值" :span="2">{{ detailData?.value || '-' }}</el-descriptions-item>
      <el-descriptions-item label="备注" :span="2">{{ detailData?.remark || '-' }}</el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">分类统计</el-divider>
    <el-row :gutter="12" class="mb-16px">
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">同分类配置数</div>
          <div class="mt-8px text-24px font-600">{{ detailData?.summary?.sameCategoryCount ?? 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">可见配置数</div>
          <div class="mt-8px text-24px font-600">{{ detailData?.summary?.visibleCount ?? 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">不可见配置数</div>
          <div class="mt-8px text-24px font-600">{{ detailData?.summary?.hiddenCount ?? 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-divider content-position="left">同分类配置</el-divider>
    <el-table
      v-if="detailData?.relatedConfigs?.length"
      :data="detailData.relatedConfigs"
      size="small"
      border
      max-height="260"
    >
      <el-table-column label="名称" prop="name" min-width="150" />
      <el-table-column label="配置键" prop="key" min-width="220" />
      <el-table-column label="可见" prop="visible" width="100">
        <template #default="{ row }">
          {{ formatVisibleStatus(row.visible) }}
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="160" />
      <el-table-column label="创建时间" prop="createTime" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无相关配置" :image-size="80" />
  </Dialog>
</template>

<script setup lang="ts">
import type { FormInstance } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { dateFormatter, formatDate } from '@/utils/formatTime'
import download from '@/utils/download'
import { useMessage } from '@/hooks/web/useMessage'
import { PlatformConfigApi, type PlatformConfig, type PlatformConfigDetail } from '@/api/linbang/platformconfig'
import {
  formatPlatformConfigCategory,
  formatVisibleStatus,
  PLATFORM_CONFIG_CATEGORY_OPTIONS
} from '../utils/display'
import PlatformConfigForm from './PlatformConfigForm.vue'

defineOptions({ name: 'PlatformConfig' })

const message = useMessage()
const loading = ref(false)
const exportLoading = ref(false)
const detailVisible = ref(false)
const detailLoading = ref(false)
const list = ref<PlatformConfig[]>([])
const detailData = ref<PlatformConfigDetail>()
const total = ref(0)
const queryFormRef = ref<FormInstance>()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  category: undefined as string | undefined,
  name: undefined as string | undefined,
  key: undefined as string | undefined,
  createTime: [] as string[]
})

const getList = async () => {
  loading.value = true
  try {
    const data = await PlatformConfigApi.getPlatformConfigPage(queryParams)
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

const handleExport = async () => {
  try {
    await message.exportConfirm()
    exportLoading.value = true
    const data = await PlatformConfigApi.exportPlatformConfig(queryParams)
    download.excel(data, '平台配置列表.xls')
  } finally {
    exportLoading.value = false
  }
}

const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const openDetail = async (id: number) => {
  detailVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await PlatformConfigApi.getPlatformConfig(id)
  } finally {
    detailLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
