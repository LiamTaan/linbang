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
        <el-select
          v-model="queryParams.currentRoleCode"
          placeholder="请选择当前角色"
          clearable
          class="!w-220px"
        >
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
        <el-button @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" /> 搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" /> 重置
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <el-row :gutter="16">
    <el-col :xs="24" :lg="8">
      <ContentWrap class="member-panel h-full">
        <template #header>
          <div class="flex items-center justify-between">
            <span class="text-15px font-600">用户选择</span>
            <span class="text-12px text-[var(--el-text-color-secondary)]">
              共 {{ total }} 人
            </span>
          </div>
        </template>

        <el-table
          ref="tableRef"
          v-loading="loading"
          :data="list"
          :show-overflow-tooltip="true"
          highlight-current-row
          row-key="id"
          max-height="720"
          @row-click="handleRowClick"
        >
          <el-table-column label="用户" min-width="220">
            <template #default="{ row }">
              <div class="leading-20px">
                <div class="font-600">{{ row.nickname || '-' }}</div>
                <div class="text-[var(--el-text-color-secondary)]">{{ row.mobile || '-' }}</div>
                <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || '-' }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="当前角色" prop="currentRoleCode" width="110">
            <template #default="{ row }">
              <dict-tag
                v-if="row.currentRoleCode"
                :type="DICT_TYPE.LB_ROLE_CODE"
                :value="row.currentRoleCode"
              />
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" prop="status" width="90">
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
    </el-col>

    <el-col :xs="24" :lg="16">
      <ContentWrap class="member-panel h-full">
        <template #header>
          <div class="flex items-center justify-between gap-12px">
            <div>
              <div class="text-15px font-600">用户中心概览</div>
              <div class="mt-4px text-12px text-[var(--el-text-color-secondary)]">
                当前查看：{{ detailTitle }}
              </div>
            </div>
            <el-button
              v-if="detailData?.id"
              type="primary"
              plain
              v-hasPermi="['linbang:member-user:update']"
              @click="openForm('update', detailData.id)"
            >
              <Icon icon="ep:edit" class="mr-5px" /> 编辑用户
            </el-button>
          </div>
        </template>

        <div v-loading="detailLoading" class="min-h-520px">
          <el-empty
            v-if="!detailData"
            description="请先从左侧选择一位用户"
            :image-size="96"
          />

          <template v-else>
            <div class="overview-hero">
              <div class="overview-user">
                <el-avatar :size="72" :src="detailData.avatar || undefined">
                  {{ (detailData.nickname || '用户').slice(0, 1) }}
                </el-avatar>
                <div class="overview-user__meta">
                  <div class="text-22px font-700">{{ detailData.nickname || '-' }}</div>
                  <div class="mt-6px text-14px text-[var(--el-text-color-secondary)]">
                    {{ detailData.mobile || '-' }} / {{ detailData.userNo || '-' }}
                  </div>
                  <div class="mt-10px flex flex-wrap gap-8px">
                    <dict-tag
                      v-if="detailData.currentRoleCode"
                      :type="DICT_TYPE.LB_ROLE_CODE"
                      :value="detailData.currentRoleCode"
                    />
                    <el-tag type="success" v-if="detailData.status === 'ENABLE'">启用中</el-tag>
                    <el-tag type="info" v-else>已停用</el-tag>
                    <el-tag type="warning" v-if="detailData.summary?.realNameApproved">实名已通过</el-tag>
                    <el-tag type="primary" v-if="detailData.summary?.merchantBound">已绑定服务商</el-tag>
                  </div>
                </div>
              </div>
              <div class="overview-side">
                <div class="overview-side__item">
                  <span>最后登录时间</span>
                  <strong>{{ formatDate(detailData.lastLoginTime) }}</strong>
                </div>
                <div class="overview-side__item">
                  <span>最后登录 IP</span>
                  <strong>{{ detailData.lastLoginIp || '-' }}</strong>
                </div>
                <div class="overview-side__item">
                  <span>注册来源</span>
                  <strong>{{ detailData.registerSource || '-' }}</strong>
                </div>
              </div>
            </div>

            <el-row :gutter="12" class="mb-16px">
              <el-col :xs="24" :sm="12" :xl="6">
                <el-card shadow="never" class="stat-card">
                  <div class="stat-card__label">资质总数 / 已通过</div>
                  <div class="stat-card__value">
                    {{ detailData.summary?.qualificationCount ?? 0 }} / {{ detailData.summary?.approvedQualificationCount ?? 0 }}
                  </div>
                </el-card>
              </el-col>
              <el-col :xs="24" :sm="12" :xl="6">
                <el-card shadow="never" class="stat-card">
                  <div class="stat-card__label">地址数 / 默认地址</div>
                  <div class="stat-card__value">
                    {{ detailData.summary?.addressCount ?? 0 }} / {{ detailData.summary?.defaultAddressCount ?? 0 }}
                  </div>
                </el-card>
              </el-col>
              <el-col :xs="24" :sm="12" :xl="6">
                <el-card shadow="never" class="stat-card">
                  <div class="stat-card__label">信用记录 / 最新分值</div>
                  <div class="stat-card__value">
                    {{ detailData.summary?.creditRecordCount ?? 0 }} / {{ detailData.summary?.latestCreditScore ?? 0 }}
                  </div>
                </el-card>
              </el-col>
              <el-col :xs="24" :sm="12" :xl="6">
                <el-card shadow="never" class="stat-card">
                  <div class="stat-card__label">驳回资质 / 信用等级</div>
                  <div class="stat-card__value">
                    {{ detailData.summary?.rejectedQualificationCount ?? 0 }} / {{ detailData.summary?.latestCreditLevel || '-' }}
                  </div>
                </el-card>
              </el-col>
            </el-row>

            <el-descriptions :column="2" border class="mb-16px">
              <el-descriptions-item label="生日">{{ detailData.birthday || '-' }}</el-descriptions-item>
              <el-descriptions-item label="性别">{{ formatGender(detailData.gender) }}</el-descriptions-item>
              <el-descriptions-item label="创建时间">{{ formatDate(detailData.createTime) }}</el-descriptions-item>
              <el-descriptions-item label="更新时间">{{ formatDate(detailData.updateTime) }}</el-descriptions-item>
              <el-descriptions-item label="备注" :span="2">
                {{ detailData.remark || '-' }}
              </el-descriptions-item>
            </el-descriptions>

            <el-divider content-position="left">实名、服务商、入驻上下文</el-divider>
            <el-row :gutter="12" class="mb-16px">
              <el-col :xs="24" :xl="8">
                <el-card shadow="never" class="context-card">
                  <div class="context-card__title">实名认证</div>
                  <div class="context-card__name">{{ detailData.realName?.realName || '-' }}</div>
                  <div class="context-card__meta">{{ detailData.realName?.idCardNo || '-' }}</div>
                  <div class="mt-10px">
                    <dict-tag
                      v-if="detailData.realName?.auditStatus"
                      :type="DICT_TYPE.LB_AUDIT_STATUS"
                      :value="detailData.realName.auditStatus"
                    />
                    <span v-else>-</span>
                  </div>
                  <div class="mt-10px text-12px text-[var(--el-text-color-secondary)]">
                    审核时间：{{ formatDate(detailData.realName?.auditTime) }}
                  </div>
                </el-card>
              </el-col>
              <el-col :xs="24" :xl="8">
                <el-card shadow="never" class="context-card">
                  <div class="context-card__title">服务商信息</div>
                  <div class="context-card__name">{{ detailData.merchant?.merchantName || '-' }}</div>
                  <div class="context-card__meta">
                    {{ detailData.merchant?.contactName || '-' }} / {{ detailData.merchant?.contactMobile || '-' }}
                  </div>
                  <div class="mt-10px text-13px">
                    状态：{{ formatEnableStatus(detailData.merchant?.status) }}
                  </div>
                  <div class="mt-6px text-13px">
                    接单：{{ formatAcceptStatus(detailData.merchant?.acceptStatus) }}
                  </div>
                  <div class="mt-6px text-12px text-[var(--el-text-color-secondary)]">
                    信用：{{ detailData.merchant?.creditScore ?? '-' }} / {{ detailData.merchant?.creditLevel || '-' }}
                  </div>
                </el-card>
              </el-col>
              <el-col :xs="24" :xl="8">
                <el-card shadow="never" class="context-card">
                  <div class="context-card__title">最近入驻申请</div>
                  <div class="context-card__name">{{ detailData.latestEntry?.entryNo || '-' }}</div>
                  <div class="context-card__meta">{{ detailData.latestEntry?.regionCode || '-' }}</div>
                  <div class="mt-10px">
                    <dict-tag
                      v-if="detailData.latestEntry?.status"
                      :type="DICT_TYPE.LB_MERCHANT_ENTRY_STATUS"
                      :value="detailData.latestEntry.status"
                    />
                    <span v-else>-</span>
                  </div>
                  <div class="mt-10px text-12px text-[var(--el-text-color-secondary)]">
                    创建时间：{{ formatDate(detailData.latestEntry?.createTime) }}
                  </div>
                </el-card>
              </el-col>
            </el-row>

            <el-descriptions :column="2" border class="mb-16px">
              <el-descriptions-item label="实名审核备注">
                {{ detailData.realName?.auditRemark || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="实名驳回原因">
                {{ detailData.realName?.rejectReason || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="服务范围说明" :span="2">
                {{ detailData.merchant?.serviceScopeDesc || '-' }}
              </el-descriptions-item>
            </el-descriptions>

            <el-divider content-position="left">资质列表</el-divider>
            <el-table
              v-if="detailData.qualifications?.length"
              :data="detailData.qualifications"
              size="small"
              border
              max-height="240"
              class="mb-16px"
            >
              <el-table-column label="ID" prop="id" width="90" />
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
                <template #default="{ row }">
                  {{ row.validStartDate || '-' }} ~ {{ row.validEndDate || '-' }}
                </template>
              </el-table-column>
            </el-table>
            <el-empty v-else description="暂无资质" :image-size="80" />

            <el-divider content-position="left">地址列表</el-divider>
            <el-table
              v-if="detailData.addresses?.length"
              :data="detailData.addresses"
              size="small"
              border
              max-height="240"
              class="mb-16px"
            >
              <el-table-column label="联系人" prop="receiverName" width="120" />
              <el-table-column label="联系电话" prop="receiverMobile" width="140" />
              <el-table-column label="省市区" min-width="220">
                <template #default="{ row }">
                  {{ [row.province, row.city, row.district].filter(Boolean).join(' / ') || '-' }}
                </template>
              </el-table-column>
              <el-table-column label="详细地址" min-width="260">
                <template #default="{ row }">
                  {{ [row.street, row.detailAddress].filter(Boolean).join(' ') || '-' }}
                </template>
              </el-table-column>
              <el-table-column label="默认地址" width="100">
                <template #default="{ row }">{{ formatBooleanYesNo(row.isDefault) }}</template>
              </el-table-column>
            </el-table>
            <el-empty v-else description="暂无地址" :image-size="80" />

            <el-divider content-position="left">信用记录</el-divider>
            <el-table
              v-if="detailData.creditRecords?.length"
              :data="detailData.creditRecords"
              size="small"
              border
              max-height="260"
            >
              <el-table-column label="规则编码" prop="ruleCode" min-width="140" />
              <el-table-column label="规则名称" prop="ruleName" min-width="160" />
              <el-table-column label="分值变动" prop="scoreChange" width="100" />
              <el-table-column label="前后分值" min-width="160">
                <template #default="{ row }">
                  {{ row.beforeScore ?? '-' }} -> {{ row.afterScore ?? '-' }}
                </template>
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
              <el-table-column label="创建时间" width="180">
                <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
              </el-table-column>
            </el-table>
            <el-empty v-else description="暂无信用记录" :image-size="80" />
          </template>
        </div>
      </ContentWrap>
    </el-col>
  </el-row>

  <MemberUserForm ref="formRef" @success="handleFormSuccess" />
</template>

<script setup lang="ts">
import type { FormInstance } from 'element-plus'
import { nextTick, onMounted, reactive, ref } from 'vue'
import { getStrDictOptions, DICT_TYPE } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import { MemberUserApi, type MemberUser, type MemberUserDetail } from '@/api/linbang/memberuser'
import {
  ENABLE_STATUS_OPTIONS,
  formatAcceptStatus,
  formatBizType,
  formatBooleanYesNo,
  formatEnableStatus,
  formatGender,
  formatTriggerType
} from '../utils/display'
import MemberUserForm from '../memberuser/MemberUserForm.vue'

defineOptions({ name: 'LinbangMember' })

const loading = ref(false)
const detailLoading = ref(false)
const total = ref(0)
const list = ref<MemberUser[]>([])
const detailData = ref<MemberUserDetail>()
const selectedUserId = ref<number>()
const queryFormRef = ref<FormInstance>()
const formRef = ref()
const tableRef = ref()

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userNo: undefined as string | undefined,
  mobile: undefined as string | undefined,
  nickname: undefined as string | undefined,
  currentRoleCode: undefined as string | undefined,
  status: undefined as string | undefined,
  createTime: [] as string[]
})

const detailTitle = computedTitle()

function computedTitle() {
  return ref('请先选择左侧用户')
}

const syncDetailTitle = () => {
  detailTitle.value = detailData.value
    ? [detailData.value.nickname, detailData.value.mobile, detailData.value.userNo].filter(Boolean).join(' / ') || '当前用户'
    : '请先选择左侧用户'
}

const setCurrentRow = async (id?: number) => {
  await nextTick()
  const row = list.value.find((item) => item.id === id)
  tableRef.value?.setCurrentRow(row)
}

const loadDetail = async (id?: number) => {
  if (!id) {
    detailData.value = undefined
    selectedUserId.value = undefined
    syncDetailTitle()
    return
  }
  detailLoading.value = true
  try {
    selectedUserId.value = id
    detailData.value = await MemberUserApi.getMemberUser(id)
    syncDetailTitle()
  } finally {
    detailLoading.value = false
  }
}

const getList = async () => {
  loading.value = true
  try {
    const data = await MemberUserApi.getMemberUserPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }

  if (!list.value.length) {
    await loadDetail(undefined)
    return
  }

  const nextId = list.value.some((item) => item.id === selectedUserId.value)
    ? selectedUserId.value
    : list.value[0].id
  await loadDetail(nextId)
  await setCurrentRow(nextId)
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const resetQuery = () => {
  queryFormRef.value?.resetFields()
  handleQuery()
}

const handleRowClick = async (row: MemberUser) => {
  if (row.id === selectedUserId.value) {
    return
  }
  await loadDetail(row.id)
  await setCurrentRow(row.id)
}

const openForm = (type: string, id?: number) => {
  formRef.value?.open(type, id)
}

const handleFormSuccess = async () => {
  await getList()
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.member-panel {
  min-height: 780px;
}

.overview-hero {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  padding: 22px 24px;
  margin-bottom: 16px;
  border-radius: 16px;
  background:
    radial-gradient(circle at top left, rgba(231, 244, 234, 0.95), transparent 38%),
    linear-gradient(135deg, #f8fbf5 0%, #eef7f0 48%, #f8fafc 100%);
  border: 1px solid rgba(152, 196, 159, 0.35);
}

.overview-user {
  display: flex;
  align-items: center;
  gap: 18px;
}

.overview-user__meta {
  min-width: 0;
}

.overview-side {
  display: grid;
  gap: 10px;
  min-width: 230px;
}

.overview-side__item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 10px 12px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.72);
}

.overview-side__item span {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.overview-side__item strong {
  font-size: 14px;
  color: var(--el-text-color-primary);
}

.stat-card {
  border-radius: 14px;
}

.stat-card__label {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.stat-card__value {
  margin-top: 10px;
  font-size: 22px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.context-card {
  height: 100%;
  border-radius: 14px;
}

.context-card__title {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.context-card__name {
  margin-top: 10px;
  font-size: 18px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.context-card__meta {
  margin-top: 8px;
  font-size: 13px;
  color: var(--el-text-color-secondary);
  word-break: break-all;
}

@media (max-width: 1200px) {
  .overview-hero {
    flex-direction: column;
  }

  .overview-side {
    min-width: 0;
  }
}
</style>
