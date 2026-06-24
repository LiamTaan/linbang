<template>
  <Dialog v-model="dialogVisible" title="选择文件" width="1120px">
    <ContentWrap>
      <el-form :inline="true" :model="queryParams" label-width="84px" class="-mb-15px">
        <el-form-item label="文件名称">
          <el-input
            v-model="queryParams.path"
            placeholder="请输入文件名称或路径"
            clearable
            class="!w-260px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="文件类型">
          <el-input
            v-model="queryParams.type"
            placeholder="请输入文件类型"
            clearable
            class="!w-220px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
          <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
          <el-button type="primary" plain @click="openUploadDialog">
            <Icon icon="ep:upload" class="mr-5px" /> 上传文件
          </el-button>
        </el-form-item>
      </el-form>
    </ContentWrap>

    <ContentWrap>
      <el-table
        v-loading="loading"
        :data="list"
        :stripe="true"
        :show-overflow-tooltip="true"
        :highlight-current-row="true"
        row-key="id"
        @row-click="handleRowClick"
        @row-dblclick="handleRowDblClick"
      >
        <el-table-column width="52" align="center">
          <template #default="{ row }">
            <el-radio v-model="selectedId" :value="row.id" class="radio-no-label" />
          </template>
        </el-table-column>
        <el-table-column label="文件" min-width="360">
          <template #default="{ row }">
            <div class="leading-20px">
              <div class="font-600">{{ row.name || '-' }}</div>
              <div class="text-[var(--el-text-color-secondary)]">{{ row.path || '-' }}</div>
              <div class="text-[var(--el-text-color-secondary)]">{{ row.type || '-' }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="预览" width="120" align="center">
          <template #default="{ row }">
            <el-image
              v-if="isImage(row)"
              class="h-60px w-60px rounded-4px"
              fit="cover"
              :src="row.url"
              :preview-src-list="[row.url]"
              preview-teleported
            />
            <el-link v-else :href="row.url" target="_blank" type="primary" :underline="false">
              查看文件
            </el-link>
          </template>
        </el-table-column>
        <el-table-column label="文件大小" width="120" align="center">
          <template #default="{ row }">
            {{ formatFileSizeValue(row.size) }}
          </template>
        </el-table-column>
        <el-table-column label="上传时间" prop="createTime" width="180" :formatter="dateFormatter" />
      </el-table>

      <Pagination
        :total="total"
        v-model:page="queryParams.pageNo"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </ContentWrap>

    <template #footer>
      <el-button type="primary" @click="confirmSelect">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>

  <FileForm ref="uploadFormRef" @success="handleUploadSuccess" />
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { dateFormatter } from '@/utils/formatTime'
import { fileSizeFormatter } from '@/utils'
import { useMessage } from '@/hooks/web/useMessage'
import FileForm from '@/views/infra/file/FileForm.vue'
import { getFile, getFilePage, type FileVO } from '@/api/infra/file'

defineOptions({ name: 'FileSelectDialog' })

const message = useMessage()
const dialogVisible = ref(false)
const loading = ref(false)
const total = ref(0)
const list = ref<FileVO[]>([])
const selectedId = ref<number>()
const selectedRow = ref<FileVO>()
const uploadFormRef = ref()

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  path: undefined as string | undefined,
  type: undefined as string | undefined
})

const emit = defineEmits<{
  selected: [row: FileVO]
}>()

const isImage = (file: FileVO) => {
  return Boolean(file.type?.includes('image'))
}

const formatFileSizeValue = (size?: number) => {
  if (size === undefined || size === null) {
    return '-'
  }
  return fileSizeFormatter(null, null, size)
}

const getList = async () => {
  loading.value = true
  try {
    const data = await getFilePage(queryParams)
    list.value = data.list
    total.value = data.total
    if (selectedId.value) {
      selectedRow.value = list.value.find((item) => item.id === selectedId.value)
    }
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const resetQuery = () => {
  queryParams.path = undefined
  queryParams.type = undefined
  handleQuery()
}

const handleRowClick = (row: FileVO) => {
  selectedId.value = row.id
  selectedRow.value = row
}

const handleRowDblClick = (row: FileVO) => {
  handleRowClick(row)
  confirmSelect()
}

const confirmSelect = () => {
  if (!selectedRow.value) {
    message.warning('请选择一个文件')
    return
  }
  emit('selected', selectedRow.value)
  dialogVisible.value = false
}

const openUploadDialog = () => {
  uploadFormRef.value?.open()
}

const handleUploadSuccess = async () => {
  queryParams.pageNo = 1
  await getList()
}

const open = async (row?: FileVO) => {
  dialogVisible.value = true
  selectedId.value = row?.id
  selectedRow.value = row
  queryParams.pageNo = 1
  await getList()
}

const loadById = async (id?: number) => {
  if (!id) {
    return undefined
  }
  return await getFile(id)
}

defineExpose({ open, loadById })
</script>

<style scoped>
.radio-no-label :deep(.el-radio__label) {
  display: none;
}
</style>
