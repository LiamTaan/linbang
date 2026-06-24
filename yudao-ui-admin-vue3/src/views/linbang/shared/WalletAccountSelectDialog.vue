<template>
  <Dialog v-model="dialogVisible" title="选择钱包账户" width="980px">
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
        <el-form-item label="角色">
          <el-select v-model="queryParams.roleCode" placeholder="请选择角色" clearable class="!w-200px">
            <el-option
              v-for="dict in getStrDictOptions(DICT_TYPE.LB_ROLE_CODE)"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-180px">
            <el-option
              v-for="item in ENABLE_STATUS_OPTIONS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
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
        <el-table-column label="角色" width="120">
          <template #default="{ row }">
            <dict-tag :type="DICT_TYPE.LB_ROLE_CODE" :value="row.roleCode" />
          </template>
        </el-table-column>
        <el-table-column label="可提现金额" prop="availableAmount" width="120" />
        <el-table-column label="总资产" prop="totalAmount" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            {{ formatEnableStatus(row.status) }}
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
import { DICT_TYPE, getStrDictOptions } from '@/utils/dict'
import { WalletAccountApi, type WalletAccount } from '@/api/linbang/walletaccount'
import type { MemberUser } from '@/api/linbang/memberuser'
import { ENABLE_STATUS_OPTIONS, formatEnableStatus } from '../utils/display'
import { useMessage } from '@/hooks/web/useMessage'

defineOptions({ name: 'WalletAccountSelectDialog' })

const message = useMessage()
const dialogVisible = ref(false)
const loading = ref(false)
const total = ref(0)
const list = ref<WalletAccount[]>([])
const selectedId = ref<number>()
const selectedRow = ref<WalletAccount>()

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userKeyword: undefined as string | undefined,
  roleCode: undefined as string | undefined,
  status: undefined as string | undefined
})

const emit = defineEmits<{
  selected: [row: WalletAccount]
}>()

const getList = async () => {
  loading.value = true
  try {
    const data = await WalletAccountApi.getWalletAccountPage(queryParams)
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
  queryParams.roleCode = undefined
  queryParams.status = undefined
  handleQuery()
}

const handleRowClick = (row: WalletAccount) => {
  selectedId.value = row.id
  selectedRow.value = row
}

const handleRowDblClick = (row: WalletAccount) => {
  handleRowClick(row)
  confirmSelect()
}

const confirmSelect = () => {
  if (!selectedRow.value) {
    message.warning('请选择一个钱包账户')
    return
  }
  emit('selected', selectedRow.value)
  dialogVisible.value = false
}

const open = async (row?: WalletAccount, user?: MemberUser) => {
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
