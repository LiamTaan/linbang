<template>
  <Dialog v-model="dialogVisible" title="选择服务商" width="960px">
    <ContentWrap>
      <el-form :inline="true" :model="queryParams" label-width="84px" class="-mb-15px">
        <el-form-item label="服务商名称">
          <el-input
            v-model="queryParams.merchantName"
            placeholder="请输入服务商名称"
            clearable
            class="!w-220px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="用户">
          <el-input
            v-model="queryParams.userKeyword"
            placeholder="请输入用户编号 / 昵称 / 手机号"
            clearable
            class="!w-220px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input
            v-model="queryParams.contactName"
            placeholder="请输入联系人"
            clearable
            class="!w-200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="联系手机">
          <el-input
            v-model="queryParams.contactMobile"
            placeholder="请输入联系手机"
            clearable
            class="!w-200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
            class="!w-180px"
          >
            <el-option
              v-for="item in ENABLE_STATUS_OPTIONS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
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
        <el-table-column label="服务商名称" prop="merchantName" min-width="180" />
        <el-table-column label="联系人" prop="contactName" width="120" />
        <el-table-column label="联系手机" prop="contactMobile" width="140" />
        <el-table-column label="关联用户" min-width="220">
          <template #default="{ row }">
            <div class="leading-20px">
              <div class="font-600">{{ row.userNickname || '-' }}</div>
              <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
              <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || '-' }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="100">
          <template #default="{ row }">
            {{ formatEnableStatus(row.status) }}
          </template>
        </el-table-column>
        <el-table-column label="接单状态" prop="acceptStatus" width="120">
          <template #default="{ row }">
            {{ formatAcceptStatus(row.acceptStatus) }}
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

    <template #footer>
      <el-button type="primary" @click="confirmSelect">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { MerchantInfoApi, type MerchantInfo } from '@/api/linbang/merchantinfo'
import { ENABLE_STATUS_OPTIONS, formatAcceptStatus, formatEnableStatus } from '../utils/display'

defineOptions({ name: 'MerchantInfoSelectDialog' })

const message = useMessage()

const dialogVisible = ref(false)
const loading = ref(false)
const total = ref(0)
const list = ref<MerchantInfo[]>([])
const selectedId = ref<number>()
const selectedRow = ref<MerchantInfo>()

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  merchantName: undefined as string | undefined,
  userKeyword: undefined as string | undefined,
  contactName: undefined as string | undefined,
  contactMobile: undefined as string | undefined,
  status: undefined as string | undefined
})

const emit = defineEmits<{
  selected: [row: MerchantInfo]
}>()

const getList = async () => {
  loading.value = true
  try {
    const data = await MerchantInfoApi.getMerchantInfoPage(queryParams)
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
  queryParams.merchantName = undefined
  queryParams.userKeyword = undefined
  queryParams.contactName = undefined
  queryParams.contactMobile = undefined
  queryParams.status = undefined
  handleQuery()
}

const handleRowClick = (row: MerchantInfo) => {
  selectedId.value = row.id
  selectedRow.value = row
}

const handleRowDblClick = (row: MerchantInfo) => {
  handleRowClick(row)
  confirmSelect()
}

const confirmSelect = () => {
  if (!selectedRow.value) {
    message.warning('请选择一条服务商数据')
    return
  }
  emit('selected', selectedRow.value)
  dialogVisible.value = false
}

const open = async (row?: MerchantInfo) => {
  dialogVisible.value = true
  selectedId.value = row?.id
  selectedRow.value = row
  queryParams.pageNo = 1
  await getList()
}

defineExpose({ open })
</script>

<style scoped>
.radio-no-label :deep(.el-radio__label) {
  display: none;
}
</style>
