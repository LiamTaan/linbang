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
      <el-form-item label="关键词" prop="word">
        <el-input
          v-model="queryParams.word"
          placeholder="请输入关键词"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="词库类型" prop="wordType">
        <el-select
          v-model="queryParams.wordType"
          placeholder="请选择词库类型"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="item in SENSITIVE_WORD_TYPE_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="匹配方式" prop="matchType">
        <el-select
          v-model="queryParams.matchType"
          placeholder="请选择匹配方式"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="item in SENSITIVE_WORD_MATCH_TYPE_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="拦截级别" prop="blockLevel">
        <el-select v-model="queryParams.blockLevel" placeholder="请选择拦截级别" clearable class="!w-240px">
          <el-option label="直接拦截" value="BLOCK" />
          <el-option label="人工复核" value="REVIEW" />
        </el-select>
      </el-form-item>
      <el-form-item label="适用场景" prop="sceneType">
        <el-input
          v-model="queryParams.sceneType"
          placeholder="如 MESSAGE/COMMENT/PROMOTE"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择状态"
          clearable
          class="!w-240px"
        >
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
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['linbang:sensitive-word:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['linbang:sensitive-word:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
        <el-button
            type="danger"
            plain
            :disabled="isEmpty(checkedIds)"
            @click="handleDeleteBatch"
            v-hasPermi="['linbang:sensitive-word:delete']"
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
      <el-table-column label="关键词" align="center" prop="word" />
      <el-table-column label="词库类型" align="center" prop="wordType">
        <template #default="{ row }">
          {{ formatSensitiveWordType(row.wordType) }}
        </template>
      </el-table-column>
      <el-table-column label="匹配方式" align="center" prop="matchType">
        <template #default="{ row }">
          {{ formatSensitiveWordMatchType(row.matchType) }}
        </template>
      </el-table-column>
      <el-table-column label="拦截级别" align="center" prop="blockLevel" />
      <el-table-column label="适用场景" align="center" prop="sceneType" min-width="180" />
      <el-table-column label="替换文案" align="center" prop="replaceText" width="120" />
      <el-table-column label="状态" align="center" prop="status">
        <template #default="{ row }">
          {{ formatEnableStatus(row.status) }}
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
            v-hasPermi="['linbang:sensitive-word:query']"
          >
            详情
          </el-button>
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['linbang:sensitive-word:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['linbang:sensitive-word:delete']"
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
  <SensitiveWordForm ref="formRef" @success="getList" />
  <SensitiveWordDetailDialog ref="detailDialogRef" />
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { isEmpty } from '@/utils/is'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
import { SensitiveWordApi, SensitiveWord } from '@/api/linbang/sensitiveword'
import {
  ENABLE_STATUS_OPTIONS,
  formatEnableStatus,
  formatSensitiveWordMatchType,
  formatSensitiveWordType,
  SENSITIVE_WORD_MATCH_TYPE_OPTIONS,
  SENSITIVE_WORD_TYPE_OPTIONS
} from '../utils/display'
import SensitiveWordForm from './SensitiveWordForm.vue'
import SensitiveWordDetailDialog from './SensitiveWordDetailDialog.vue'

/** 敏感词表 列表 */
defineOptions({ name: 'SensitiveWord' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<SensitiveWord[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  word: undefined,
  wordType: undefined,
  matchType: undefined,
  blockLevel: undefined,
  sceneType: undefined,
  status: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await SensitiveWordApi.getSensitiveWordPage(queryParams)
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
    await SensitiveWordApi.deleteSensitiveWord(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 批量删除敏感词表 */
const handleDeleteBatch = async () => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    await SensitiveWordApi.deleteSensitiveWordList(checkedIds.value);
    checkedIds.value = [];
    message.success(t('common.delSuccess'))
    await getList();
  } catch {}
}

const checkedIds = ref<number[]>([])
const handleRowCheckboxChange = (records: SensitiveWord[]) => {
  checkedIds.value = records.map((item) => item.id!);
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await SensitiveWordApi.exportSensitiveWord(queryParams)
    download.excel(data, '敏感词表.xls')
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
