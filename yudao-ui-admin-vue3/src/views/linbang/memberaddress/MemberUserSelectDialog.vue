<template>
  <Dialog v-model="dialogVisible" title="选择用户" width="920px">
    <ContentWrap>
      <el-form :inline="true" :model="queryParams" label-width="72px" class="-mb-15px">
        <el-form-item label="用户编号">
          <el-input
            v-model="queryParams.userNo"
            placeholder="请输入用户编号"
            clearable
            class="!w-200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input
            v-model="queryParams.mobile"
            placeholder="请输入手机号"
            clearable
            class="!w-200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input
            v-model="queryParams.nickname"
            placeholder="请输入昵称"
            clearable
            class="!w-200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="当前角色">
          <el-select
            v-model="queryParams.currentRoleCode"
            placeholder="请选择当前角色"
            clearable
            class="!w-200px"
          >
            <el-option
              v-for="dict in getStrDictOptions(DICT_TYPE.LB_ROLE_CODE)"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
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
        <el-table-column label="用户编号" prop="userNo" min-width="160" />
        <el-table-column label="手机号" prop="mobile" width="140" />
        <el-table-column label="昵称" prop="nickname" min-width="140" />
        <el-table-column label="当前角色" prop="currentRoleCode" width="120">
          <template #default="{ row }">
            <dict-tag
              v-if="row.currentRoleCode"
              :type="DICT_TYPE.LB_ROLE_CODE"
              :value="row.currentRoleCode"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="100">
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
import {
  MemberUserApi,
  type MemberUser
} from '@/api/linbang/memberuser'
import { formatEnableStatus } from '../utils/display'

defineOptions({ name: 'MemberUserSelectDialog' })

const message = useMessage()

const dialogVisible = ref(false)
const loading = ref(false)
const total = ref(0)
const list = ref<MemberUser[]>([])
const selectedId = ref<number>()
const selectedRow = ref<MemberUser>()

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userNo: undefined as string | undefined,
  mobile: undefined as string | undefined,
  nickname: undefined as string | undefined,
  currentRoleCode: undefined as string | undefined
})

const emit = defineEmits<{
  selected: [row: MemberUser]
}>()

const getList = async () => {
  loading.value = true
  try {
    const data = await MemberUserApi.getMemberUserPage(queryParams)
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
  queryParams.userNo = undefined
  queryParams.mobile = undefined
  queryParams.nickname = undefined
  queryParams.currentRoleCode = undefined
  handleQuery()
}

const handleRowClick = (row: MemberUser) => {
  selectedId.value = row.id
  selectedRow.value = row
}

const handleRowDblClick = (row: MemberUser) => {
  handleRowClick(row)
  confirmSelect()
}

const confirmSelect = () => {
  if (!selectedRow.value) {
    message.warning('请选择一条用户数据')
    return
  }
  emit('selected', selectedRow.value)
  dialogVisible.value = false
}

const open = async (row?: MemberUser) => {
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
