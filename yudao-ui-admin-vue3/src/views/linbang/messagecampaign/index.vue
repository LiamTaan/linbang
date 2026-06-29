<template>
  <ContentWrap>
    <el-form :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="投放名称">
        <el-input v-model="queryParams.campaignName" placeholder="请输入投放名称" clearable class="!w-220px" />
      </el-form-item>
      <el-form-item label="审核状态">
        <el-select v-model="queryParams.auditStatus" clearable class="!w-180px">
          <el-option label="待审核" value="PENDING" />
          <el-option label="已通过" value="APPROVED" />
          <el-option label="已驳回" value="REJECTED" />
          <el-option label="已取消" value="CANCELLED" />
        </el-select>
      </el-form-item>
      <el-form-item label="执行状态">
        <el-select v-model="queryParams.executeStatus" clearable class="!w-180px">
          <el-option label="待执行" value="PENDING" />
          <el-option label="执行中" value="PROCESSING" />
          <el-option label="成功" value="SUCCESS" />
          <el-option label="部分失败" value="PARTIAL_FAILED" />
          <el-option label="失败" value="FAILED" />
          <el-option label="已取消" value="CANCELLED" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" />搜索</el-button>
        <el-button type="primary" @click="openCreateDialog"><Icon icon="ep:plus" class="mr-5px" />新建投放</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="投放名称" prop="campaignName" min-width="180" />
      <el-table-column label="来源" min-width="120">
        <template #default="{ row }">{{ formatCampaignSourceType(row.sourceType) }}</template>
      </el-table-column>
      <el-table-column label="目标模式" min-width="100">
        <template #default="{ row }">{{ formatCampaignTargetMode(row.targetMode) }}</template>
      </el-table-column>
      <el-table-column label="场景" prop="sceneCode" min-width="160" />
      <el-table-column label="分类" width="90">
        <template #default="{ row }">{{ formatMessageCategory(row.messageCategory) }}</template>
      </el-table-column>
      <el-table-column label="审核状态" width="100">
        <template #default="{ row }">{{ formatCampaignAuditStatus(row.auditStatus) }}</template>
      </el-table-column>
      <el-table-column label="执行状态" width="110">
        <template #default="{ row }">{{ formatExecuteStatus(row.executeStatus) }}</template>
      </el-table-column>
      <el-table-column label="计划人数" prop="plannedAudienceCount" width="90" />
      <el-table-column label="触达/点击/已读" min-width="140">
        <template #default="{ row }">
          {{ row.reachedCount || 0 }}/{{ row.clickedCount || 0 }}/{{ row.readCount || 0 }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" fixed="right" width="220">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetailDialog(row)">详情</el-button>
          <el-button v-if="row.auditStatus === 'PENDING'" link type="primary" @click="openApproveDialog(row)">通过</el-button>
          <el-button v-if="row.auditStatus === 'PENDING'" link type="danger" @click="openRejectDialog(row)">驳回</el-button>
          <el-button v-if="row.auditStatus === 'APPROVED'" link type="primary" @click="executeNow(row)">执行</el-button>
          <el-button
            v-if="row.executeStatus !== 'SUCCESS' && row.executeStatus !== 'PARTIAL_FAILED' && row.executeStatus !== 'CANCELLED'"
            link
            type="danger"
            @click="openCancelDialog(row)"
          >
            取消
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </ContentWrap>

  <Dialog v-model="createVisible" title="新建投放活动" width="720px">
    <el-form ref="campaignFormRef" :model="campaignForm" :rules="campaignRules" label-width="110px">
      <el-form-item label="投放名称" required>
        <el-input v-model="campaignForm.campaignName" placeholder="请输入投放名称" />
      </el-form-item>
      <el-form-item label="来源类型" required>
        <el-select v-model="campaignForm.sourceType" class="!w-full">
          <el-option label="管理员定向" value="ADMIN_DIRECTED" />
          <el-option label="广告投放" value="AD" />
        </el-select>
      </el-form-item>
      <el-form-item label="目标模式" required>
        <el-select v-model="campaignForm.targetMode" class="!w-full">
          <el-option label="全平台" value="FULL_PLATFORM" />
          <el-option label="辖区" value="JURISDICTION" />
          <el-option label="自定义筛选" value="CUSTOM_FILTER" />
        </el-select>
      </el-form-item>
      <el-form-item label="区域编码">
        <el-input v-model="campaignForm.targetRegionCodes" placeholder="多个用逗号分隔" />
      </el-form-item>
      <el-form-item label="类目 ID">
        <el-input v-model="campaignForm.targetCategoryIds" placeholder="多个用逗号分隔" />
      </el-form-item>
      <el-form-item label="角色编码">
        <el-input v-model="campaignForm.targetRoleCodes" placeholder="多个用逗号分隔" />
      </el-form-item>
      <el-form-item label="投放时段">
        <el-input v-model="campaignForm.deliveryTimeWindows" placeholder="如 09:00-12:00,14:00-18:00" />
      </el-form-item>
      <el-form-item label="定时执行">
        <el-date-picker v-model="campaignForm.scheduleTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" class="!w-full" />
      </el-form-item>
      <el-form-item label="场景编码" prop="sceneCode" required>
        <el-select v-model="campaignForm.sceneCode" filterable class="!w-full" placeholder="请选择消息场景">
          <el-option v-for="item in sceneOptions" :key="item.id" :label="`${item.sceneName} (${item.sceneCode})`" :value="item.sceneCode" />
        </el-select>
      </el-form-item>
      <el-form-item label="消息分类" prop="messageCategory" required>
        <el-select v-model="campaignForm.messageCategory" class="!w-full">
          <el-option label="营销" value="MARKETING" />
          <el-option label="系统" value="SYSTEM" />
          <el-option label="订单" value="ORDER" />
          <el-option label="资金" value="FINANCE" />
          <el-option label="合规" value="COMPLIANCE" />
          <el-option label="纠纷" value="DISPUTE" />
        </el-select>
      </el-form-item>
      <el-form-item label="业务类型">
        <el-input v-model="campaignForm.bizType" placeholder="如 MARKETING" />
      </el-form-item>
      <el-form-item label="业务 ID">
        <el-input-number v-model="campaignForm.bizId" :min="0" class="!w-full" />
      </el-form-item>
      <el-form-item label="内容快照" prop="contentSnapshot" required>
        <el-input v-model="campaignForm.contentSnapshot" type="textarea" :rows="4" placeholder="请输入消息内容快照" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="createVisible = false">取消</el-button>
      <el-button type="primary" @click="submitCreate">保存</el-button>
    </template>
  </Dialog>

  <Dialog v-model="detailVisible" title="投放活动详情" width="720px">
    <el-descriptions :column="2" border>
      <el-descriptions-item label="投放名称">{{ currentRow?.campaignName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="来源">{{ formatCampaignSourceType(currentRow?.sourceType) }}</el-descriptions-item>
      <el-descriptions-item label="目标模式">{{ formatCampaignTargetMode(currentRow?.targetMode) }}</el-descriptions-item>
      <el-descriptions-item label="场景编码">{{ currentRow?.sceneCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="消息分类">{{ formatMessageCategory(currentRow?.messageCategory) }}</el-descriptions-item>
      <el-descriptions-item label="投放时段">{{ currentRow?.deliveryTimeWindows || '-' }}</el-descriptions-item>
      <el-descriptions-item label="区域编码">{{ currentRow?.targetRegionCodes || '-' }}</el-descriptions-item>
      <el-descriptions-item label="类目 ID">{{ currentRow?.targetCategoryIds || '-' }}</el-descriptions-item>
      <el-descriptions-item label="角色编码">{{ currentRow?.targetRoleCodes || '-' }}</el-descriptions-item>
      <el-descriptions-item label="计划执行">{{ currentRow?.scheduleTime || '-' }}</el-descriptions-item>
      <el-descriptions-item label="计划人数">{{ currentRow?.plannedAudienceCount || 0 }}</el-descriptions-item>
      <el-descriptions-item label="触达/点击/已读">
        {{ currentRow?.reachedCount || 0 }}/{{ currentRow?.clickedCount || 0 }}/{{ currentRow?.readCount || 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="内容快照" :span="2">{{ currentRow?.contentSnapshot || '-' }}</el-descriptions-item>
    </el-descriptions>
  </Dialog>

  <Dialog v-model="auditVisible" :title="auditMode === 'approve' ? '审核通过' : auditMode === 'reject' ? '驳回投放' : '取消投放'" width="520px">
    <el-form :model="auditForm" label-width="90px">
      <el-form-item :label="auditLabel" required>
        <el-input v-model="auditForm.reason" type="textarea" :rows="4" :placeholder="auditPlaceholder" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="auditVisible = false">取消</el-button>
      <el-button type="primary" @click="submitAudit">确定</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useMessage } from '@/hooks/web/useMessage'
import { MessageCampaignApi, type MessageCampaign } from '@/api/linbang/messagecampaign'
import { MessageSceneApi, type MessageScene } from '@/api/linbang/messagescene'
import {
  formatCampaignAuditStatus,
  formatCampaignSourceType,
  formatCampaignTargetMode,
  formatExecuteStatus,
  formatMessageCategory
} from '../utils/display'

defineOptions({ name: 'MessageCampaignIndex' })

const message = useMessage()
const loading = ref(false)
const total = ref(0)
const list = ref<MessageCampaign[]>([])
const sceneOptions = ref<MessageScene[]>([])
const createVisible = ref(false)
const detailVisible = ref(false)
const auditVisible = ref(false)
const currentRow = ref<MessageCampaign>()
const auditMode = ref<'approve' | 'reject' | 'cancel'>('approve')
const campaignFormRef = ref<FormInstance>()

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  campaignName: undefined as string | undefined,
  auditStatus: undefined as string | undefined,
  executeStatus: undefined as string | undefined
})

const campaignForm = reactive<MessageCampaign>({
  campaignName: '',
  sourceType: 'ADMIN_DIRECTED',
  targetMode: 'FULL_PLATFORM',
  targetRegionCodes: '',
  targetCategoryIds: '',
  targetRoleCodes: '',
  deliveryTimeWindows: '',
  scheduleTime: '',
  sceneCode: 'MARKETING_BROADCAST',
  messageCategory: 'MARKETING',
  bizType: 'MARKETING',
  bizId: undefined,
  contentSnapshot: ''
})

const campaignRules: FormRules = {
  campaignName: [{ required: true, message: '请输入投放名称', trigger: 'blur' }],
  sourceType: [{ required: true, message: '请选择来源类型', trigger: 'change' }],
  targetMode: [{ required: true, message: '请选择目标模式', trigger: 'change' }],
  sceneCode: [{ required: true, message: '请选择消息场景', trigger: 'change' }],
  messageCategory: [{ required: true, message: '请选择消息分类', trigger: 'change' }],
  contentSnapshot: [{ required: true, message: '请输入内容快照', trigger: 'blur' }]
}

const auditForm = reactive({
  reason: ''
})

const auditLabel = computed(() => {
  if (auditMode.value === 'approve') return '审核备注'
  if (auditMode.value === 'reject') return '驳回原因'
  return '取消原因'
})

const auditPlaceholder = computed(() => {
  if (auditMode.value === 'approve') return '请输入审核备注'
  if (auditMode.value === 'reject') return '请输入驳回原因'
  return '请输入取消原因'
})

const getList = async () => {
  loading.value = true
  try {
    const data = await MessageCampaignApi.getPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const loadSceneOptions = async () => {
  const data = await MessageSceneApi.getPage({ pageNo: 1, pageSize: 100 })
  sceneOptions.value = data.list || []
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const resetCampaignForm = () => {
  Object.assign(campaignForm, {
    campaignName: '',
    sourceType: 'ADMIN_DIRECTED',
    targetMode: 'FULL_PLATFORM',
    targetRegionCodes: '',
    targetCategoryIds: '',
    targetRoleCodes: '',
    deliveryTimeWindows: '',
    scheduleTime: '',
    sceneCode: 'MARKETING_BROADCAST',
    messageCategory: 'MARKETING',
    bizType: 'MARKETING',
    bizId: undefined,
    contentSnapshot: ''
  })
}

const openCreateDialog = () => {
  resetCampaignForm()
  createVisible.value = true
}

const submitCreate = async () => {
  await campaignFormRef.value?.validate()
  await MessageCampaignApi.create({ ...campaignForm })
  message.success('投放活动已创建')
  createVisible.value = false
  getList()
}

const openDetailDialog = async (row: MessageCampaign) => {
  currentRow.value = await MessageCampaignApi.get(row.id!)
  detailVisible.value = true
}

const openApproveDialog = (row: MessageCampaign) => {
  currentRow.value = row
  auditMode.value = 'approve'
  auditForm.reason = '定向条件和文案已确认'
  auditVisible.value = true
}

const openRejectDialog = (row: MessageCampaign) => {
  currentRow.value = row
  auditMode.value = 'reject'
  auditForm.reason = ''
  auditVisible.value = true
}

const openCancelDialog = (row: MessageCampaign) => {
  currentRow.value = row
  auditMode.value = 'cancel'
  auditForm.reason = ''
  auditVisible.value = true
}

const submitAudit = async () => {
  if (!currentRow.value?.id) return
  if (auditMode.value === 'approve') {
    await MessageCampaignApi.approve(currentRow.value.id, auditForm.reason)
    message.success('已审核通过')
  } else if (auditMode.value === 'reject') {
    await MessageCampaignApi.reject(currentRow.value.id, auditForm.reason)
    message.success('已驳回')
  } else {
    await MessageCampaignApi.cancel(currentRow.value.id, auditForm.reason)
    message.success('投放活动已取消')
  }
  auditVisible.value = false
  getList()
}

const executeNow = async (row: MessageCampaign) => {
  await MessageCampaignApi.executeNow(row.id!)
  message.success('已触发执行')
  getList()
}

watch(
  () => campaignForm.sceneCode,
  (sceneCode) => {
    const matched = sceneOptions.value.find((item) => item.sceneCode === sceneCode)
    if (!matched) return
    campaignForm.messageCategory = matched.messageCategory
    if (!campaignForm.bizType) {
      campaignForm.bizType = matched.bizType || matched.messageCategory
    }
  }
)

onMounted(async () => {
  await loadSceneOptions()
  await getList()
})
</script>
