<template>
  <Dialog v-model="dialogVisible" title="选择地址" width="1080px">
    <ContentWrap>
      <el-form :inline="true" :model="queryParams" label-width="84px" class="-mb-15px">
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
            v-model="queryParams.receiverName"
            placeholder="请输入联系人"
            clearable
            class="!w-200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input
            v-model="queryParams.receiverMobile"
            placeholder="请输入联系电话"
            clearable
            class="!w-200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input
            v-model="queryParams.detailAddress"
            placeholder="请输入详细地址"
            clearable
            class="!w-260px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="默认地址">
          <el-select v-model="queryParams.isDefault" placeholder="请选择是否默认" clearable class="!w-180px">
            <el-option label="是" :value="true" />
            <el-option label="否" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
          <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
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
        <el-table-column label="用户" min-width="220">
          <template #default="{ row }">
            <div class="leading-20px">
              <div class="font-600">{{ row.userNickname || '-' }}</div>
              <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
              <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || '-' }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="联系人" prop="receiverName" width="120" />
        <el-table-column label="联系电话" prop="receiverMobile" width="140" />
        <el-table-column label="地址" min-width="320">
          <template #default="{ row }">
            {{ formatAddress(row) }}
          </template>
        </el-table-column>
        <el-table-column label="默认地址" width="100">
          <template #default="{ row }">
            {{ formatBooleanYesNo(row.isDefault) }}
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
import { useMessage } from '@/hooks/web/useMessage'
import { MemberUserAddressApi, type MemberUserAddress } from '@/api/linbang/memberaddress'
import type { MemberUser } from '@/api/linbang/memberuser'
import { formatBooleanYesNo } from '../utils/display'

defineOptions({ name: 'MemberAddressSelectDialog' })

const message = useMessage()
const dialogVisible = ref(false)
const loading = ref(false)
const total = ref(0)
const list = ref<MemberUserAddress[]>([])
const selectedId = ref<number>()
const selectedRow = ref<MemberUserAddress>()

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userKeyword: undefined as string | undefined,
  receiverName: undefined as string | undefined,
  receiverMobile: undefined as string | undefined,
  detailAddress: undefined as string | undefined,
  isDefault: undefined as boolean | undefined
})

const emit = defineEmits<{
  selected: [row: MemberUserAddress]
}>()

const formatAddress = (row: MemberUserAddress) => {
  return [row.province, row.city, row.district, row.street, row.detailAddress].filter(Boolean).join(' / ') || '-'
}

const getList = async () => {
  loading.value = true
  try {
    const data = await MemberUserAddressApi.getMemberUserAddressPage(queryParams)
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
  queryParams.userKeyword = undefined
  queryParams.receiverName = undefined
  queryParams.receiverMobile = undefined
  queryParams.detailAddress = undefined
  queryParams.isDefault = undefined
  handleQuery()
}

const handleRowClick = (row: MemberUserAddress) => {
  selectedId.value = row.id
  selectedRow.value = row
}

const handleRowDblClick = (row: MemberUserAddress) => {
  handleRowClick(row)
  confirmSelect()
}

const confirmSelect = () => {
  if (!selectedRow.value) {
    message.warning('请选择一个地址')
    return
  }
  emit('selected', selectedRow.value)
  dialogVisible.value = false
}

const open = async (row?: MemberUserAddress, user?: MemberUser) => {
  dialogVisible.value = true
  selectedId.value = row?.id
  selectedRow.value = row
  queryParams.pageNo = 1
  queryParams.userKeyword = user ? user.userNo || user.mobile || user.nickname || undefined : undefined
  await getList()
}

defineExpose({ open })
</script>

<style scoped>
.radio-no-label :deep(.el-radio__label) {
  display: none;
}
</style>
