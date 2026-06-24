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
      <el-form-item label="流水号" prop="flowNo">
        <el-input
          v-model="queryParams.flowNo"
          placeholder="请输入流水号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="用户" prop="userKeyword">
        <el-input
          v-model="queryParams.userKeyword"
          placeholder="请输入用户编号 / 昵称 / 手机号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="业务类型" prop="bizType">
        <el-input
          v-model="queryParams.bizType"
          placeholder="请输入业务类型"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="流水类型" prop="flowType">
        <el-select
          v-model="queryParams.flowType"
          placeholder="请选择流水类型"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="item in FLOW_TYPE_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="关联订单号" prop="relatedOrderNo">
        <el-input
          v-model="queryParams.relatedOrderNo"
          placeholder="请输入关联订单号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
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
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['linbang:wallet:flow:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['linbang:wallet:flow:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
        <el-button
            type="danger"
            plain
            :disabled="isEmpty(checkedIds)"
            @click="handleDeleteBatch"
            v-hasPermi="['linbang:wallet:flow:delete']"
        >
          <Icon icon="ep:delete" class="mr-5px" /> 批量删除
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
        @selection-change="handleRowCheckboxChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column label="流水号" align="center" prop="flowNo" />
      <el-table-column label="用户" align="center" min-width="220">
        <template #default="{ row }">
          <div class="font-600">{{ row.userNickname || '-' }}</div>
          <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
          <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || '-' }}</div>
        </template>
      </el-table-column>
      <el-table-column label="钱包账户" align="center" min-width="180">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ formatWalletAccountDisplay(row) }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ formatEnableStatus(row.walletStatus) }}</div>
            <div class="text-[var(--el-text-color-secondary)]">
              可提现：{{ row.walletAvailableAmount ?? '-' }}
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="业务类型" align="center" prop="bizType">
        <template #default="{ row }">
          {{ formatBizType(row.bizType) }}
        </template>
      </el-table-column>
      <el-table-column label="流水类型" align="center" prop="flowType">
        <template #default="{ row }">
          {{ formatFlowType(row.flowType) }}
        </template>
      </el-table-column>
      <el-table-column label="变动金额" align="center" prop="changeAmount" />
      <el-table-column label="变动前金额" align="center" prop="beforeAmount" />
      <el-table-column label="变动后金额" align="center" prop="afterAmount" />
      <el-table-column label="关联订单号" align="center" prop="relatedOrderNo" min-width="180" />
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
            v-hasPermi="['linbang:wallet:flow:query']"
          >
            详情
          </el-button>
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['linbang:wallet:flow:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['linbang:wallet:flow:delete']"
          >
            删除
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

  <!-- 表单弹窗：添加/修改 -->
  <WalletFlowForm ref="formRef" @success="getList" />
  <WalletFlowDetailDialog ref="detailDialogRef" />
</template>

<script setup lang="ts">
import { isEmpty } from '@/utils/is'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { WalletFlowApi, WalletFlow } from '@/api/linbang/walletflow'
import { FLOW_TYPE_OPTIONS, formatBizType, formatEnableStatus, formatFlowType } from '../utils/display'
import WalletFlowForm from './WalletFlowForm.vue'
import WalletFlowDetailDialog from './WalletFlowDetailDialog.vue'

import { onMounted, reactive, ref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
/** 钱包流水 列表 */
defineOptions({ name: 'WalletFlow' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<WalletFlow[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  flowNo: undefined,
  userKeyword: undefined,
  bizType: undefined,
  flowType: undefined,
  relatedOrderNo: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

const formatWalletAccountDisplay = (row?: Pick<WalletFlow, 'walletRoleCode' | 'walletAccountId'>) => {
  if (!row) {
    return '-'
  }
  return row.walletRoleCode || (row.walletAccountId ? '钱包账户信息缺失' : '-')
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await WalletFlowApi.getWalletFlowPage(queryParams)
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

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const detailDialogRef = ref()
const openDetail = (id: number) => {
  detailDialogRef.value.open(id)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await WalletFlowApi.deleteWalletFlow(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 批量删除钱包流水 */
const handleDeleteBatch = async () => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    await WalletFlowApi.deleteWalletFlowList(checkedIds.value);
    checkedIds.value = [];
    message.success(t('common.delSuccess'))
    await getList();
  } catch {}
}

const checkedIds = ref<number[]>([])
const handleRowCheckboxChange = (records: WalletFlow[]) => {
  checkedIds.value = records.map((item) => item.id!);
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await WalletFlowApi.exportWalletFlow(queryParams)
    download.excel(data, '钱包流水.xls')
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
