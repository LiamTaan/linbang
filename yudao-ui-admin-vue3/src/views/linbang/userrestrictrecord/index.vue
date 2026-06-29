<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="用户" prop="userKeyword">
        <el-input v-model="queryParams.userKeyword" placeholder="用户编号/昵称/手机号" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="限制类型" prop="restrictType">
        <el-input v-model="queryParams.restrictType" placeholder="如 PUBLISH/ACCEPT" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-input v-model="queryParams.status" placeholder="如 ACTIVE/RELEASED" clearable class="!w-220px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-260px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="用户" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.userNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="限制类型" prop="restrictType" width="120" />
      <el-table-column label="状态" prop="status" width="120" />
      <el-table-column label="来源规则" prop="sourceRuleCode" min-width="160" />
      <el-table-column label="来源业务" min-width="140">
        <template #default="{ row }">{{ row.sourceBizType || '-' }} / {{ row.sourceBizId || '-' }}</template>
      </el-table-column>
      <el-table-column label="生效时间" prop="startTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="结束时间" prop="endTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="原因" prop="reason" min-width="220" />
      <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="showDetail(row.id)">详情</el-button>
          <el-button
            v-if="row.status === 'ACTIVE'"
            link
            type="warning"
            v-hasPermi="['linbang:member-user:restrict']"
            @click="openReleaseDialog(row)"
          >
            解除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </ContentWrap>

  <Dialog v-model="detailVisible" title="限制记录详情" width="720px">
    <el-descriptions :column="2" border>
      <el-descriptions-item label="用户">{{ detailData?.userNickname || '-' }}</el-descriptions-item>
      <el-descriptions-item label="手机号">{{ detailData?.userMobile || '-' }}</el-descriptions-item>
      <el-descriptions-item label="限制类型">{{ detailData?.restrictType || '-' }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ detailData?.status || '-' }}</el-descriptions-item>
      <el-descriptions-item label="来源规则">{{ detailData?.sourceRuleCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="来源业务">{{ detailData?.sourceBizType || '-' }} / {{ detailData?.sourceBizId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="生效时间">{{ detailData?.startTime || '-' }}</el-descriptions-item>
      <el-descriptions-item label="结束时间">{{ detailData?.endTime || '-' }}</el-descriptions-item>
      <el-descriptions-item label="解除人">{{ detailData?.releasedBy || '-' }}</el-descriptions-item>
      <el-descriptions-item label="解除时间">{{ detailData?.releasedTime || '-' }}</el-descriptions-item>
      <el-descriptions-item label="原因" :span="2">{{ detailData?.reason || '-' }}</el-descriptions-item>
      <el-descriptions-item label="解除备注" :span="2">{{ detailData?.releaseRemark || '-' }}</el-descriptions-item>
    </el-descriptions>
  </Dialog>

  <Dialog v-model="releaseVisible" title="解除限制" width="520px">
    <el-form ref="releaseFormRef" :model="releaseFormData" :rules="releaseFormRules" label-width="88px">
      <el-form-item label="限制记录">
        <el-input :model-value="currentRow ? `${currentRow.userNickname || '-'} / ${currentRow.restrictType || '-'}` : ''" disabled />
      </el-form-item>
      <el-form-item label="解除备注" prop="releaseRemark">
        <el-input
          v-model="releaseFormData.releaseRemark"
          type="textarea"
          :rows="4"
          maxlength="255"
          show-word-limit
          placeholder="请输入解除备注"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="releaseVisible = false">取消</el-button>
      <el-button type="primary" :loading="releaseLoading" @click="submitRelease">提交</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { dateFormatter } from '@/utils/formatTime'
import { UserRestrictRecordApi, type UserRestrictRecord } from '@/api/linbang/userrestrictrecord'
import { MemberUserApi, type MemberUserReleaseRestrictReqVO } from '@/api/linbang/memberuser'
import { useMessage } from '@/hooks/web/useMessage'
import { requestDynamicKeyToken } from '../shared/dynamic-key'

defineOptions({ name: 'UserRestrictRecord' })

const message = useMessage()
const loading = ref(false)
const list = ref<UserRestrictRecord[]>([])
const detailVisible = ref(false)
const detailData = ref<UserRestrictRecord>()
const currentRow = ref<UserRestrictRecord>()
const releaseVisible = ref(false)
const releaseLoading = ref(false)
const total = ref(0)
const queryFormRef = ref()
const releaseFormRef = ref<FormInstance>()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userKeyword: undefined as string | undefined,
  restrictType: undefined as string | undefined,
  status: undefined as string | undefined,
  createTime: [] as string[]
})
const releaseFormData = reactive<MemberUserReleaseRestrictReqVO>({
  restrictRecordId: 0,
  releaseRemark: ''
})
const releaseFormRules = reactive<FormRules>({
  releaseRemark: [{ required: true, message: '请输入解除备注', trigger: 'blur' }]
})

const getList = async () => {
  loading.value = true
  try {
    const data = await UserRestrictRecordApi.getPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const resetQuery = () => {
  queryFormRef.value?.resetFields()
  handleQuery()
}

const showDetail = async (id: number) => {
  detailData.value = await UserRestrictRecordApi.get(id)
  detailVisible.value = true
}

const openReleaseDialog = (row: UserRestrictRecord) => {
  currentRow.value = row
  releaseFormData.restrictRecordId = row.id
  releaseFormData.releaseRemark = ''
  releaseVisible.value = true
}

const submitRelease = async () => {
  await releaseFormRef.value?.validate()
  try {
    const verifyToken = await requestDynamicKeyToken('解除账号限制')
    releaseLoading.value = true
    await MemberUserApi.releaseMemberUserRestrict({ ...releaseFormData }, verifyToken)
    message.success('限制解除成功')
    releaseVisible.value = false
    await getList()
  } finally {
    releaseLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
