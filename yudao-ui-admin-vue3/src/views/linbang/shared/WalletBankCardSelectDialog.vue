<template>
  <Dialog v-model="dialogVisible" title="选择银行卡" width="980px">
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
        <el-form-item label="银行名称">
          <el-input
            v-model="queryParams.bankName"
            placeholder="请输入银行名称"
            clearable
            class="!w-220px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="卡号">
          <el-input
            v-model="queryParams.cardNoMask"
            placeholder="请输入脱敏卡号"
            clearable
            class="!w-200px"
            @keyup.enter="handleQuery"
          />
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
        <el-form-item label="默认卡">
          <el-select v-model="queryParams.isDefault" placeholder="请选择默认卡" clearable class="!w-180px">
            <el-option
              v-for="item in BOOLEAN_YES_NO_OPTIONS"
              :key="String(item.value)"
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
        <el-table-column label="银行名称" prop="bankName" width="140" />
        <el-table-column label="脱敏卡号" prop="cardNoMask" width="160" />
        <el-table-column label="开户名" prop="accountName" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            {{ formatEnableStatus(row.status) }}
          </template>
        </el-table-column>
        <el-table-column label="默认卡" width="100">
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
import { WalletBankCardApi, type WalletBankCard } from '@/api/linbang/walletbankcard'
import type { MemberUser } from '@/api/linbang/memberuser'
import {
  BOOLEAN_YES_NO_OPTIONS,
  ENABLE_STATUS_OPTIONS,
  formatBooleanYesNo,
  formatEnableStatus
} from '../utils/display'
import { useMessage } from '@/hooks/web/useMessage'

defineOptions({ name: 'WalletBankCardSelectDialog' })

const message = useMessage()
const dialogVisible = ref(false)
const loading = ref(false)
const total = ref(0)
const list = ref<WalletBankCard[]>([])
const selectedId = ref<number>()
const selectedRow = ref<WalletBankCard>()

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userKeyword: undefined as string | undefined,
  bankName: undefined as string | undefined,
  cardNoMask: undefined as string | undefined,
  status: undefined as string | undefined,
  isDefault: undefined as boolean | undefined
})

const emit = defineEmits<{
  selected: [row: WalletBankCard]
}>()

const getList = async () => {
  loading.value = true
  try {
    const data = await WalletBankCardApi.getWalletBankCardPage(queryParams)
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
  queryParams.bankName = undefined
  queryParams.cardNoMask = undefined
  queryParams.status = undefined
  queryParams.isDefault = undefined
  handleQuery()
}

const handleRowClick = (row: WalletBankCard) => {
  selectedId.value = row.id
  selectedRow.value = row
}

const handleRowDblClick = (row: WalletBankCard) => {
  handleRowClick(row)
  confirmSelect()
}

const confirmSelect = () => {
  if (!selectedRow.value) {
    message.warning('请选择一张银行卡')
    return
  }
  emit('selected', selectedRow.value)
  dialogVisible.value = false
}

const open = async (row?: WalletBankCard, user?: MemberUser) => {
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
