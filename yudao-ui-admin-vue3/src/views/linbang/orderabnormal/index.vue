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
      <el-form-item label="订单号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="单元号" prop="unitNo">
        <el-input
          v-model="queryParams.unitNo"
          placeholder="请输入单元号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="异常单号" prop="abnormalNo">
        <el-input
          v-model="queryParams.abnormalNo"
          placeholder="请输入异常单号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="异常类型" prop="abnormalType">
        <el-input
          v-model="queryParams.abnormalType"
          placeholder="请输入异常类型"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="风险等级" prop="riskLevel">
        <el-input
          v-model="queryParams.riskLevel"
          placeholder="请输入风险等级"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="命中规则编码" prop="hitRuleCode">
        <el-input
          v-model="queryParams.hitRuleCode"
          placeholder="请输入命中规则编码"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="处理状态" prop="handleStatus">
        <el-select
          v-model="queryParams.handleStatus"
          placeholder="请选择处理状态"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="item in HANDLE_STATUS_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="处理人" prop="handleBy">
        <el-input
          v-model="queryParams.handleBy"
          placeholder="请输入处理人"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="处理时间" prop="handleTime">
        <el-date-picker
          v-model="queryParams.handleTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="queryParams.remark"
          placeholder="请输入备注"
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
          v-hasPermi="['linbang:order:abnormal:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['linbang:order:abnormal:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
        <el-button
            type="danger"
            plain
            :disabled="isEmpty(checkedIds)"
            @click="handleDeleteBatch"
            v-hasPermi="['linbang:order:abnormal:delete']"
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
      <el-table-column label="订单号" align="center" prop="orderNo" min-width="180" />
      <el-table-column label="单元号" align="center" prop="unitNo" min-width="160" />
      <el-table-column label="异常单号" align="center" prop="abnormalNo" />
      <el-table-column label="异常类型" align="center" prop="abnormalType" />
      <el-table-column label="风险等级" align="center" prop="riskLevel">
        <template #default="scope">
          {{ formatRiskLevel(scope.row.riskLevel) }}
        </template>
      </el-table-column>
      <el-table-column label="命中规则编码" align="center" prop="hitRuleCode" />
      <el-table-column label="处理状态" align="center" prop="handleStatus">
        <template #default="scope">
          {{ formatHandleStatus(scope.row.handleStatus) }}
        </template>
      </el-table-column>
      <el-table-column label="终审状态" align="center" prop="finalAuditStatus" width="120">
        <template #default="scope">
          {{ formatFinalAuditStatus(scope.row.finalAuditStatus) }}
        </template>
      </el-table-column>
      <el-table-column label="处理人" align="center" prop="handleBy" />
      <el-table-column
        label="处理时间"
        align="center"
        prop="handleTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column label="备注" align="center" prop="remark" />
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
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['linbang:order:abnormal:update']"
          >
            编辑
          </el-button>
          <el-button
            v-if="scope.row.finalAuditStatus !== 'APPROVED'"
            link
            type="warning"
            @click="openFinalAuditDialog(scope.row)"
            v-hasPermi="['linbang:order:abnormal:final-audit']"
          >
            终审
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['linbang:order:abnormal:delete']"
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
  <OrderAbnormalForm ref="formRef" @success="getList" />

  <Dialog v-model="finalAuditVisible" title="异常订单终审" width="520px">
    <el-form ref="finalAuditFormRef" :model="finalAuditFormData" :rules="finalAuditFormRules" label-width="88px">
      <el-form-item label="异常单号">
        <el-input :model-value="currentRow?.abnormalNo" disabled />
      </el-form-item>
      <el-form-item label="当前状态">
        <el-input :model-value="formatHandleStatus(currentRow?.handleStatus)" disabled />
      </el-form-item>
      <el-form-item label="终审结果" prop="finalAuditStatus">
        <el-radio-group v-model="finalAuditFormData.finalAuditStatus">
          <el-radio value="APPROVED">通过</el-radio>
          <el-radio value="REJECTED">驳回</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="终审意见" prop="finalAuditRemark">
        <el-input
          v-model="finalAuditFormData.finalAuditRemark"
          type="textarea"
          :rows="4"
          maxlength="255"
          show-word-limit
          placeholder="请输入终审意见"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="finalAuditVisible = false">取消</el-button>
      <el-button type="primary" :loading="finalAuditLoading" @click="submitFinalAudit">提交终审</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import { isEmpty } from '@/utils/is'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import {
  OrderAbnormalApi,
  OrderAbnormal,
  type OrderAbnormalFinalAuditReqVO
} from '@/api/linbang/orderabnormal'
import { formatHandleStatus, formatRiskLevel, HANDLE_STATUS_OPTIONS } from '../utils/display'
import OrderAbnormalForm from './OrderAbnormalForm.vue'
import { requestDynamicKeyToken } from '../shared/dynamic-key'

import { onMounted, reactive, ref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
/** 异常订单 列表 */
defineOptions({ name: 'OrderAbnormal' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<OrderAbnormal[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  orderNo: undefined,
  unitNo: undefined,
  abnormalNo: undefined,
  abnormalType: undefined,
  riskLevel: undefined,
  hitRuleCode: undefined,
  handleStatus: undefined,
  handleBy: undefined,
  handleTime: [],
  remark: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中
const currentRow = ref<OrderAbnormal>()
const finalAuditVisible = ref(false)
const finalAuditLoading = ref(false)
const finalAuditFormRef = ref<FormInstance>()
const finalAuditFormData = reactive<OrderAbnormalFinalAuditReqVO>({
  id: 0,
  finalAuditStatus: 'APPROVED',
  finalAuditRemark: ''
})
const finalAuditFormRules = reactive<FormRules>({
  finalAuditStatus: [{ required: true, message: '请选择终审结果', trigger: 'change' }],
  finalAuditRemark: [{ required: true, message: '请输入终审意见', trigger: 'blur' }]
})

const formatFinalAuditStatus = (value?: string) => {
  if (value === 'APPROVED') {
    return '已通过'
  }
  if (value === 'REJECTED') {
    return '已驳回'
  }
  return value || '-'
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await OrderAbnormalApi.getOrderAbnormalPage(queryParams)
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

const openFinalAuditDialog = (row: OrderAbnormal) => {
  currentRow.value = row
  finalAuditFormData.id = row.id
  finalAuditFormData.finalAuditStatus = row.finalAuditStatus === 'REJECTED' ? 'REJECTED' : 'APPROVED'
  finalAuditFormData.finalAuditRemark = row.finalAuditRemark || row.remark || ''
  finalAuditVisible.value = true
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await OrderAbnormalApi.deleteOrderAbnormal(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 批量删除异常订单 */
const handleDeleteBatch = async () => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    await OrderAbnormalApi.deleteOrderAbnormalList(checkedIds.value);
    checkedIds.value = [];
    message.success(t('common.delSuccess'))
    await getList();
  } catch {}
}

const checkedIds = ref<number[]>([])
const handleRowCheckboxChange = (records: OrderAbnormal[]) => {
  checkedIds.value = records.map((item) => item.id!);
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await OrderAbnormalApi.exportOrderAbnormal(queryParams)
    download.excel(data, '异常订单.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

const submitFinalAudit = async () => {
  await finalAuditFormRef.value?.validate()
  try {
    const verifyToken = await requestDynamicKeyToken('异常订单终审')
    finalAuditLoading.value = true
    await OrderAbnormalApi.finalAuditOrderAbnormal({ ...finalAuditFormData }, verifyToken)
    message.success('异常订单终审成功')
    finalAuditVisible.value = false
    await getList()
  } finally {
    finalAuditLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
