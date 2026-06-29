<template>
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :model="queryParams"
      :inline="true"
      label-width="88px"
      class="-mb-15px"
    >
      <el-form-item label="用户编号" prop="userNo">
        <el-input
          v-model="queryParams.userNo"
          placeholder="请输入用户编号"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="手机号" prop="mobile">
        <el-input
          v-model="queryParams.mobile"
          placeholder="请输入手机号"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="昵称" prop="nickname">
        <el-input
          v-model="queryParams.nickname"
          placeholder="请输入昵称"
          clearable
          class="!w-220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="当前角色" prop="currentRoleCode">
        <el-select v-model="queryParams.currentRoleCode" placeholder="请选择当前角色" clearable class="!w-220px">
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_ROLE_CODE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-220px">
          <el-option
            v-for="item in ENABLE_STATUS_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="最后登录时间" prop="lastLoginTime">
        <el-date-picker
          v-model="queryParams.lastLoginTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-260px"
        />
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
        <el-button
          type="primary"
          plain
          v-hasPermi="['linbang:member-user:create']"
          @click="openForm('create')"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          :loading="exportLoading"
          v-hasPermi="['linbang:member-user:export']"
          @click="handleExport"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
        <el-button
          type="danger"
          plain
          :disabled="checkedIds.length === 0"
          v-hasPermi="['linbang:member-user:delete']"
          @click="handleDeleteBatch"
        >
          <Icon icon="ep:delete" class="mr-5px" /> 批量删除
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
      row-key="id"
      @selection-change="handleRowCheckboxChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column label="用户编号" align="center" prop="userNo" min-width="150" />
      <el-table-column label="手机号" align="center" prop="mobile" width="140" />
      <el-table-column label="昵称" align="center" prop="nickname" min-width="140" />
      <el-table-column label="头像" align="center" prop="avatar" min-width="180" />
      <el-table-column label="性别" align="center" prop="gender" width="90">
        <template #default="{ row }">
          {{ formatGender(row.gender) }}
        </template>
      </el-table-column>
      <el-table-column label="生日" align="center" prop="birthday" width="120" />
      <el-table-column label="注册来源" align="center" prop="registerSource" width="120" />
      <el-table-column label="当前角色" align="center" prop="currentRoleCode" width="120">
        <template #default="{ row }">
          <dict-tag v-if="row.currentRoleCode" :type="DICT_TYPE.LB_ROLE_CODE" :value="row.currentRoleCode" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="120">
        <template #default="{ row }">
          {{ formatEnableStatus(row.status) }}
        </template>
      </el-table-column>
      <el-table-column label="最后登录时间" align="center" prop="lastLoginTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="最后登录IP" align="center" prop="lastLoginIp" width="140" />
      <el-table-column label="备注" align="center" prop="remark" min-width="180" />
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" align="center" fixed="right" width="190">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row.id)">详情</el-button>
          <el-button
            link
            type="warning"
            v-hasPermi="['linbang:member-user:restrict']"
            @click="openRestrictDialog(row)"
          >
            限制
          </el-button>
          <el-button
            link
            type="primary"
            v-hasPermi="['linbang:member-user:update']"
            @click="openForm('update', row.id)"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            v-hasPermi="['linbang:member-user:delete']"
            @click="handleDelete(row.id)"
          >
            删除
          </el-button>
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

  <Dialog v-model="detailVisible" title="用户详情" width="960px">
    <el-descriptions v-loading="detailLoading" :column="2" border>
      <el-descriptions-item label="用户编号">{{ detailData?.userNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="手机号">{{ detailData?.mobile || '-' }}</el-descriptions-item>
      <el-descriptions-item label="昵称">{{ detailData?.nickname || '-' }}</el-descriptions-item>
      <el-descriptions-item label="当前角色">
        <dict-tag v-if="detailData?.currentRoleCode" :type="DICT_TYPE.LB_ROLE_CODE" :value="detailData.currentRoleCode" />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="状态">{{ formatEnableStatus(detailData?.status) }}</el-descriptions-item>
      <el-descriptions-item label="注册来源">{{ detailData?.registerSource || '-' }}</el-descriptions-item>
      <el-descriptions-item label="生日">{{ detailData?.birthday || '-' }}</el-descriptions-item>
      <el-descriptions-item label="最后登录时间">{{ formatDate(detailData?.lastLoginTime) }}</el-descriptions-item>
      <el-descriptions-item label="最后登录IP">{{ detailData?.lastLoginIp || '-' }}</el-descriptions-item>
      <el-descriptions-item label="备注" :span="2">{{ detailData?.remark || '-' }}</el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">账号概览</el-divider>
    <el-row :gutter="12" class="mb-16px">
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">资质数 / 已通过</div>
          <div class="mt-8px text-18px font-600">
            {{ detailData?.summary?.qualificationCount ?? 0 }} / {{ detailData?.summary?.approvedQualificationCount ?? 0 }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">地址数 / 默认地址</div>
          <div class="mt-8px text-18px font-600">
            {{ detailData?.summary?.addressCount ?? 0 }} / {{ detailData?.summary?.defaultAddressCount ?? 0 }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div class="text-14px text-[var(--el-text-color-secondary)]">信用记录 / 最新分值</div>
          <div class="mt-8px text-18px font-600">
            {{ detailData?.summary?.creditRecordCount ?? 0 }} / {{ detailData?.summary?.latestCreditScore ?? 0 }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-divider content-position="left">实名与服务商上下文</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="实名姓名">{{ detailData?.realName?.realName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="实名状态">
        <dict-tag
          v-if="detailData?.realName?.auditStatus"
          :type="DICT_TYPE.LB_AUDIT_STATUS"
          :value="detailData.realName.auditStatus"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="身份证号">{{ detailData?.realName?.idCardNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="实名审核时间">{{ formatDate(detailData?.realName?.auditTime) }}</el-descriptions-item>
      <el-descriptions-item label="服务商名称">{{ detailData?.merchant?.merchantName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="服务商状态">
        {{ formatEnableStatus(detailData?.merchant?.status) }}
      </el-descriptions-item>
      <el-descriptions-item label="接单状态">
        {{ formatAcceptStatus(detailData?.merchant?.acceptStatus) }}
      </el-descriptions-item>
      <el-descriptions-item label="信用等级">
        {{ detailData?.merchant?.creditScore ?? '-' }} / {{ detailData?.merchant?.creditLevel || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="最近入驻单号">{{ detailData?.latestEntry?.entryNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="最近入驻状态">
        <dict-tag
          v-if="detailData?.latestEntry?.status"
          :type="DICT_TYPE.LB_MERCHANT_ENTRY_STATUS"
          :value="detailData.latestEntry.status"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="服务范围说明" :span="2">
        {{ detailData?.merchant?.serviceScopeDesc || '-' }}
      </el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">资质列表</el-divider>
    <el-table
      v-if="detailData?.qualifications?.length"
      :data="detailData.qualifications"
      size="small"
      border
      max-height="240"
    >
      <el-table-column label="资质类型" prop="qualificationType" width="120">
        <template #default="{ row }">
          <dict-tag
            v-if="row.qualificationType"
            :type="DICT_TYPE.LB_QUALIFICATION_TYPE"
            :value="row.qualificationType"
          />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="资质名称" prop="qualificationName" min-width="160" />
      <el-table-column label="资质编号" prop="qualificationNo" width="160" />
      <el-table-column label="审核状态" prop="auditStatus" width="110">
        <template #default="{ row }">
          <dict-tag v-if="row.auditStatus" :type="DICT_TYPE.LB_AUDIT_STATUS" :value="row.auditStatus" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="有效期" min-width="180">
        <template #default="{ row }">{{ row.validStartDate || '-' }} ~ {{ row.validEndDate || '-' }}</template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无资质" :image-size="80" />

    <el-divider content-position="left">地址列表</el-divider>
    <el-table
      v-if="detailData?.addresses?.length"
      :data="detailData.addresses"
      size="small"
      border
      max-height="240"
    >
      <el-table-column label="ID" prop="id" width="90" />
      <el-table-column label="联系人" prop="receiverName" width="120" />
      <el-table-column label="联系电话" prop="receiverMobile" width="140" />
      <el-table-column label="省市区" min-width="220">
        <template #default="{ row }">
          {{ [row.province, row.city, row.district].filter(Boolean).join(' / ') || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="详细地址" prop="detailAddress" min-width="220" />
      <el-table-column label="默认地址" width="100">
        <template #default="{ row }">{{ formatBooleanYesNo(row.isDefault) }}</template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无地址" :image-size="80" />

    <el-divider content-position="left">信用记录</el-divider>
    <el-table
      v-if="detailData?.creditRecords?.length"
      :data="detailData.creditRecords"
      size="small"
      border
      max-height="240"
    >
      <el-table-column label="ID" prop="id" width="90" />
      <el-table-column label="规则编码" prop="ruleCode" min-width="140" />
      <el-table-column label="规则名称" prop="ruleName" min-width="160" />
      <el-table-column label="分值变动" prop="scoreChange" width="110" />
      <el-table-column label="前后分值" min-width="160">
        <template #default="{ row }">{{ row.beforeScore ?? '-' }} -> {{ row.afterScore ?? '-' }}</template>
      </el-table-column>
      <el-table-column label="触发类型" prop="triggerType" width="120">
        <template #default="{ row }">
          {{ formatTriggerType(row.triggerType) }}
        </template>
      </el-table-column>
      <el-table-column label="业务类型" prop="bizType" width="120">
        <template #default="{ row }">
          {{ formatBizType(row.bizType) }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180">
        <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="暂无信用记录" :image-size="80" />
  </Dialog>

  <Dialog v-model="restrictVisible" title="账号限制与处置" width="560px">
    <el-form ref="restrictFormRef" :model="restrictFormData" :rules="restrictFormRules" label-width="100px">
      <el-form-item label="目标用户">
        <el-input :model-value="currentRow ? `${currentRow.nickname || '-'} / ${currentRow.mobile || '-'}` : ''" disabled />
      </el-form-item>
      <el-form-item label="动作类型" prop="actionType">
        <el-radio-group v-model="restrictFormData.actionType">
          <el-radio value="RESTRICT">限制</el-radio>
          <el-radio value="BAN">封禁</el-radio>
          <el-radio value="BLACKLIST">拉黑</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="限制类型" prop="restrictType">
        <el-input v-model="restrictFormData.restrictType" placeholder="如 LOGIN / ORDER / COMMENT" />
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker
          v-model="restrictFormData.endTime"
          type="datetime"
          value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="不填表示长期生效"
          class="!w-full"
        />
      </el-form-item>
      <el-form-item label="限制原因" prop="reason">
        <el-input
          v-model="restrictFormData.reason"
          type="textarea"
          :rows="4"
          maxlength="255"
          show-word-limit
          placeholder="请输入限制原因"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="restrictVisible = false">取消</el-button>
      <el-button type="primary" :loading="restrictLoading" @click="submitRestrict">提交</el-button>
    </template>
  </Dialog>

  <MemberUserForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import type { FormInstance } from 'element-plus'
import type { FormRules } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { getStrDictOptions, DICT_TYPE } from '@/utils/dict'
import { dateFormatter, formatDate } from '@/utils/formatTime'
import download from '@/utils/download'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
import {
  MemberUserApi,
  type MemberUser,
  type MemberUserDetail,
  type MemberUserRestrictReqVO
} from '@/api/linbang/memberuser'
import {
  ENABLE_STATUS_OPTIONS,
  formatAcceptStatus,
  formatBizType,
  formatBooleanYesNo,
  formatEnableStatus,
  formatGender,
  formatTriggerType
} from '../utils/display'
import MemberUserForm from './MemberUserForm.vue'
import { requestDynamicKeyToken } from '../shared/dynamic-key'

defineOptions({ name: 'MemberUser' })

const message = useMessage()
const { t } = useI18n()
const loading = ref(false)
const exportLoading = ref(false)
const detailVisible = ref(false)
const detailLoading = ref(false)
const list = ref<MemberUser[]>([])
const detailData = ref<MemberUserDetail>()
const total = ref(0)
const queryFormRef = ref<FormInstance>()
const formRef = ref()
const checkedIds = ref<number[]>([])
const currentRow = ref<MemberUser>()
const restrictVisible = ref(false)
const restrictLoading = ref(false)
const restrictFormRef = ref<FormInstance>()
const restrictFormData = reactive<MemberUserRestrictReqVO & { endTime?: string }>({
  userId: 0,
  actionType: 'RESTRICT',
  restrictType: '',
  reason: '',
  endTime: undefined
})
const restrictFormRules = reactive<FormRules>({
  actionType: [{ required: true, message: '请选择动作类型', trigger: 'change' }],
  restrictType: [{ required: true, message: '请输入限制类型', trigger: 'blur' }],
  reason: [{ required: true, message: '请输入限制原因', trigger: 'blur' }]
})
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userNo: undefined as string | undefined,
  mobile: undefined as string | undefined,
  nickname: undefined as string | undefined,
  currentRoleCode: undefined as string | undefined,
  status: undefined as string | undefined,
  lastLoginTime: [] as string[],
  createTime: [] as string[]
})

const getList = async () => {
  loading.value = true
  try {
    const data = await MemberUserApi.getMemberUserPage(queryParams)
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

const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const openRestrictDialog = (row: MemberUser) => {
  currentRow.value = row
  restrictFormData.userId = row.id
  restrictFormData.actionType = 'RESTRICT'
  restrictFormData.restrictType = ''
  restrictFormData.reason = ''
  restrictFormData.endTime = undefined
  restrictVisible.value = true
}

const openDetail = async (id: number) => {
  detailVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await MemberUserApi.getMemberUser(id)
  } finally {
    detailLoading.value = false
  }
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await MemberUserApi.deleteMemberUser(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const handleDeleteBatch = async () => {
  try {
    await message.delConfirm()
    await MemberUserApi.deleteMemberUserList(checkedIds.value)
    checkedIds.value = []
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const handleRowCheckboxChange = (records: MemberUser[]) => {
  checkedIds.value = records.map((item) => item.id)
}

const handleExport = async () => {
  try {
    await message.exportConfirm()
    exportLoading.value = true
    const data = await MemberUserApi.exportMemberUser(queryParams)
    download.excel(data, '用户列表.xls')
  } finally {
    exportLoading.value = false
  }
}

const submitRestrict = async () => {
  await restrictFormRef.value?.validate()
  try {
    const verifyToken = await requestDynamicKeyToken('账号限制处置')
    restrictLoading.value = true
    await MemberUserApi.restrictMemberUser({ ...restrictFormData }, verifyToken)
    message.success('账号处置成功')
    restrictVisible.value = false
    await getList()
  } finally {
    restrictLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
