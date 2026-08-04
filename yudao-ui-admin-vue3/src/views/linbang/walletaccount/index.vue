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
      <el-form-item label="角色编码" prop="roleCode">
        <el-select
          v-model="queryParams.roleCode"
          placeholder="请选择角色编码"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_ROLE_CODE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="总资产" prop="totalAmount">
        <el-input
          v-model="queryParams.totalAmount"
          placeholder="请输入总资产"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="可提现金额" prop="availableAmount">
        <el-input
          v-model="queryParams.availableAmount"
          placeholder="请输入可提现金额"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="冻结金额" prop="frozenAmount">
        <el-input
          v-model="queryParams.frozenAmount"
          placeholder="请输入冻结金额"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="托管金额" prop="escrowAmount">
        <el-input
          v-model="queryParams.escrowAmount"
          placeholder="请输入托管金额"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="佣金金额" prop="commissionAmount">
        <el-input
          v-model="queryParams.commissionAmount"
          placeholder="请输入佣金金额"
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
          v-hasPermi="['linbang:wallet:account:export']"
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
          <div class="leading-20px">
            <div class="font-600">{{ row.userNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="角色编码" align="center" prop="roleCode">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.LB_ROLE_CODE" :value="scope.row.roleCode" />
        </template>
      </el-table-column>
      <el-table-column label="总资产" align="center" prop="totalAmount" />
      <el-table-column label="可提现金额" align="center" prop="availableAmount" />
      <el-table-column label="冻结金额" align="center" prop="frozenAmount" />
      <el-table-column label="托管金额" align="center" prop="escrowAmount" />
      <el-table-column label="佣金金额" align="center" prop="commissionAmount" />
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          {{ formatEnableStatus(scope.row.status) }}
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
            v-hasPermi="['linbang:wallet:account:query']"
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

  <WalletAccountDetailDialog ref="detailDialogRef" />
</template>

<script setup lang="ts">
import { DICT_TYPE, getStrDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { WalletAccountApi, WalletAccount } from '@/api/linbang/walletaccount'
import { ENABLE_STATUS_OPTIONS, formatEnableStatus } from '../utils/display'
import WalletAccountDetailDialog from './WalletAccountDetailDialog.vue'

import { onMounted, reactive, ref } from 'vue'
import { useMessage } from '@/hooks/web/useMessage'
/** 钱包账户 列表 */
defineOptions({ name: 'WalletAccount' })

const message = useMessage() // 消息弹窗

const loading = ref(true) // 列表的加载中
const list = ref<WalletAccount[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userKeyword: undefined,
  roleCode: undefined,
  totalAmount: undefined,
  availableAmount: undefined,
  frozenAmount: undefined,
  escrowAmount: undefined,
  commissionAmount: undefined,
  status: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await WalletAccountApi.getWalletAccountPage(queryParams)
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
    const data = await WalletAccountApi.exportWalletAccount(queryParams)
    download.excel(data, '钱包账户.xls')
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
