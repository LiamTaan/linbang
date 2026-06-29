<template>
  <ContentWrap>
    <el-form :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="场景编码">
        <el-input v-model="queryParams.sceneCode" placeholder="请输入场景编码" clearable class="!w-220px" />
      </el-form-item>
      <el-form-item label="场景名称">
        <el-input v-model="queryParams.sceneName" placeholder="请输入场景名称" clearable class="!w-220px" />
      </el-form-item>
      <el-form-item label="消息分类">
        <el-select v-model="queryParams.messageCategory" clearable class="!w-220px">
          <el-option label="系统" value="SYSTEM" />
          <el-option label="资金" value="FINANCE" />
          <el-option label="订单" value="ORDER" />
          <el-option label="合规" value="COMPLIANCE" />
          <el-option label="纠纷" value="DISPUTE" />
          <el-option label="营销" value="MARKETING" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" />搜索</el-button>
        <el-button type="primary" @click="openCreateDialog"><Icon icon="ep:plus" class="mr-5px" />新增场景</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="场景编码" prop="sceneCode" min-width="180" />
      <el-table-column label="场景名称" prop="sceneName" min-width="160" />
      <el-table-column label="消息分类" min-width="100">
        <template #default="{ row }">{{ formatMessageCategory(row.messageCategory) }}</template>
      </el-table-column>
      <el-table-column label="默认渠道" prop="defaultChannels" min-width="220" />
      <el-table-column label="强制短信" width="100">
        <template #default="{ row }">{{ formatBooleanYesNo(row.mandatorySms) }}</template>
      </el-table-column>
      <el-table-column label="语音朗读" width="100">
        <template #default="{ row }">{{ formatBooleanYesNo(row.voiceEnabled) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 'ENABLE' ? 'success' : 'info'">{{ formatEnableStatus(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="业务类型" prop="bizType" min-width="120" />
      <el-table-column label="备注" prop="remark" min-width="200" />
      <el-table-column label="操作" fixed="right" width="180">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEditDialog(row)">编辑</el-button>
          <el-button link type="primary" @click="toggleStatus(row)">
            {{ row.status === 'ENABLE' ? '停用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </ContentWrap>

  <Dialog v-model="formVisible" :title="formMode === 'create' ? '新增消息场景' : '编辑消息场景'" width="680px">
    <el-form :model="sceneForm" label-width="110px">
      <el-form-item label="场景编码" required>
        <el-input v-model="sceneForm.sceneCode" :disabled="formMode === 'edit'" placeholder="如 ORDER_STATUS_CHANGED" />
      </el-form-item>
      <el-form-item label="场景名称" required>
        <el-input v-model="sceneForm.sceneName" placeholder="请输入场景名称" />
      </el-form-item>
      <el-form-item label="消息分类" required>
        <el-select v-model="sceneForm.messageCategory" class="!w-full">
          <el-option label="系统" value="SYSTEM" />
          <el-option label="资金" value="FINANCE" />
          <el-option label="订单" value="ORDER" />
          <el-option label="合规" value="COMPLIANCE" />
          <el-option label="纠纷" value="DISPUTE" />
          <el-option label="营销" value="MARKETING" />
        </el-select>
      </el-form-item>
      <el-form-item label="默认渠道" required>
        <el-select v-model="channelValue" multiple class="!w-full" placeholder="请选择默认渠道">
          <el-option label="站内弹窗/消息中心" value="APP_POPUP" />
          <el-option label="公众号模板" value="WECHAT_MP_TEMPLATE" />
          <el-option label="短信" value="SMS" />
          <el-option label="App 语音朗读" value="APP_VOICE" />
        </el-select>
      </el-form-item>
      <el-form-item label="强制短信">
        <el-switch v-model="sceneForm.mandatorySms" />
      </el-form-item>
      <el-form-item label="语音朗读">
        <el-switch v-model="sceneForm.voiceEnabled" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="sceneForm.status" class="!w-full">
          <el-option label="启用" value="ENABLE" />
          <el-option label="禁用" value="DISABLE" />
        </el-select>
      </el-form-item>
      <el-form-item label="业务类型">
        <el-input v-model="sceneForm.bizType" placeholder="如 ORDER / MARKETING" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="sceneForm.remark" type="textarea" :rows="3" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="formVisible = false">取消</el-button>
      <el-button type="primary" @click="submitForm">保存</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useMessage } from '@/hooks/web/useMessage'
import { MessageSceneApi, type MessageScene } from '@/api/linbang/messagescene'
import { formatBooleanYesNo, formatEnableStatus, formatMessageCategory } from '../utils/display'

defineOptions({ name: 'MessageSceneIndex' })

const message = useMessage()
const loading = ref(false)
const total = ref(0)
const list = ref<MessageScene[]>([])
const formVisible = ref(false)
const formMode = ref<'create' | 'edit'>('create')

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  sceneCode: undefined as string | undefined,
  sceneName: undefined as string | undefined,
  messageCategory: undefined as string | undefined
})

const sceneForm = reactive<MessageScene>({
  sceneCode: '',
  sceneName: '',
  messageCategory: 'SYSTEM',
  defaultChannels: 'APP_POPUP',
  mandatorySms: false,
  voiceEnabled: false,
  status: 'ENABLE',
  bizType: '',
  remark: ''
})

const channelValue = computed({
  get: () => (sceneForm.defaultChannels ? sceneForm.defaultChannels.split(',').filter(Boolean) : []),
  set: (value: string[]) => {
    sceneForm.defaultChannels = value.join(',')
  }
})

const getList = async () => {
  loading.value = true
  try {
    const data = await MessageSceneApi.getPage(queryParams)
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

const resetForm = () => {
  Object.assign(sceneForm, {
    id: undefined,
    sceneCode: '',
    sceneName: '',
    messageCategory: 'SYSTEM',
    defaultChannels: 'APP_POPUP',
    mandatorySms: false,
    voiceEnabled: false,
    status: 'ENABLE',
    bizType: '',
    remark: ''
  })
}

const openCreateDialog = () => {
  formMode.value = 'create'
  resetForm()
  formVisible.value = true
}

const openEditDialog = async (row: MessageScene) => {
  formMode.value = 'edit'
  const detail = await MessageSceneApi.get(row.id!)
  Object.assign(sceneForm, detail)
  formVisible.value = true
}

const submitForm = async () => {
  if (sceneForm.messageCategory === 'FINANCE' && !channelValue.value.includes('SMS')) {
    message.warning('金额变动类场景必须包含短信通道')
    return
  }
  if (formMode.value === 'create') {
    await MessageSceneApi.create({ ...sceneForm })
    message.success('消息场景已创建')
  } else {
    await MessageSceneApi.update({ ...sceneForm })
    message.success('消息场景已更新')
  }
  formVisible.value = false
  getList()
}

const toggleStatus = async (row: MessageScene) => {
  await MessageSceneApi.updateStatus(row.id!, row.status === 'ENABLE' ? 'DISABLE' : 'ENABLE')
  message.success('状态已更新')
  getList()
}

watch(
  () => sceneForm.messageCategory,
  (value) => {
    if (value !== 'FINANCE') return
    sceneForm.mandatorySms = true
    const channels = new Set(channelValue.value)
    channels.add('SMS')
    channelValue.value = Array.from(channels)
  }
)

onMounted(getList)
</script>
