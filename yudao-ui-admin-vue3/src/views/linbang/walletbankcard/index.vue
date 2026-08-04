<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="用户" prop="userKeyword">
        <el-input
          v-model="queryParams.userKeyword"
          placeholder="请输入用户编号 / 昵称 / 手机号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="银行名称" prop="bankName">
        <el-input
          v-model="queryParams.bankName"
          placeholder="请输入银行名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="脱敏卡号" prop="cardNoMask">
        <el-input
          v-model="queryParams.cardNoMask"
          placeholder="请输入脱敏卡号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="开户名" prop="accountName">
        <el-input
          v-model="queryParams.accountName"
          placeholder="请输入开户名"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="预留手机号" prop="reservedMobile">
        <el-input
          v-model="queryParams.reservedMobile"
          placeholder="请输入预留手机号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-240px">
          <el-option
            v-for="item in ENABLE_STATUS_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="是否默认" prop="isDefault">
        <el-select
          v-model="queryParams.isDefault"
          placeholder="请选择是否默认"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="item in BOOLEAN_YES_NO_OPTIONS"
            :key="String(item.value)"
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
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['linbang:wallet:bank-card:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table
      row-key="id"
      v-loading="loading"
      :data="list"
      :stripe="true"
      :show-overflow-tooltip="true"
    >
      <el-table-column label="用户" align="center" min-width="220">
        <template #default="{ row }">
          <div class="font-600">{{ row.userNickname || '-' }}</div>
          <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
          <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || '-' }}</div>
        </template>
      </el-table-column>
      <el-table-column label="银行名称" align="center" prop="bankName" />
      <el-table-column label="脱敏卡号" align="center" prop="cardNoMask" />
      <el-table-column label="开户名" align="center" prop="accountName" />
      <el-table-column label="开户省份" align="center" prop="bankProvince" />
      <el-table-column label="开户城市" align="center" prop="bankCity" />
      <el-table-column label="预留手机号" align="center" prop="reservedMobile" />
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          {{ formatEnableStatus(scope.row.status) }}
        </template>
      </el-table-column>
      <el-table-column label="是否默认" align="center" prop="isDefault">
        <template #default="scope">
          {{ formatBooleanYesNo(scope.row.isDefault) }}
        </template>
      </el-table-column>
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column label="操作" align="center" min-width="120px">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openDetail(scope.row.id)"
            v-hasPermi="['linbang:wallet:bank-card:query']"
          >
            详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>

  <WalletBankCardDetailDialog ref="detailDialogRef" />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { WalletBankCardApi, WalletBankCard } from '@/api/linbang/walletbankcard'
import {
  BOOLEAN_YES_NO_OPTIONS,
  ENABLE_STATUS_OPTIONS,
  formatBooleanYesNo,
  formatEnableStatus
} from '../utils/display'
import WalletBankCardDetailDialog from './WalletBankCardDetailDialog.vue'

import { onMounted, reactive, ref } from 'vue'
import { useMessage } from '@/hooks/web/useMessage'
/** 用户银行卡 列表 */
defineOptions({ name: 'WalletBankCard' })

const message = useMessage() // 消息弹窗

const loading = ref(true) // 列表的加载中
const list = ref<WalletBankCard[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userKeyword: undefined,
  bankName: undefined,
  cardNoMask: undefined,
  accountName: undefined,
  reservedMobile: undefined,
  status: undefined,
  isDefault: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await WalletBankCardApi.getWalletBankCardPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

const detailDialogRef = ref()
const openDetail = (id: number) => {
  detailDialogRef.value.open(id)
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await WalletBankCardApi.exportWalletBankCard(queryParams)
    download.excel(data, '用户银行卡.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
