<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="640px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="模板编码" prop="templateCode">
        <el-input v-model="formData.templateCode" placeholder="请输入模板编码" />
      </el-form-item>
      <el-form-item label="模板名称" prop="templateName">
        <el-input v-model="formData.templateName" placeholder="请输入模板名称" />
      </el-form-item>
      <el-form-item label="模板类型" prop="templateType">
        <el-input v-model="formData.templateType" placeholder="请输入模板类型，如 ORDER / WALLET" />
      </el-form-item>
      <el-form-item label="渠道类型" prop="channelType">
        <el-select v-model="formData.channelType" placeholder="请选择渠道类型">
          <el-option
            v-for="item in CHANNEL_TYPE_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="模板内容" prop="content">
        <el-input
          v-model="formData.content"
          type="textarea"
          :rows="5"
          maxlength="1000"
          show-word-limit
          placeholder="请输入模板内容"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio v-for="item in ENABLE_STATUS_OPTIONS" :key="item.value" :value="item.value">
            {{ item.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import { reactive, ref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
import {
  MessageTemplateApi,
  type MessageTemplateDetail
} from '@/api/linbang/messagetemplate'
import { CHANNEL_TYPE_OPTIONS, ENABLE_STATUS_OPTIONS } from '../utils/display'

defineOptions({ name: 'MessageTemplateForm' })

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')
const formRef = ref<FormInstance>()
type FormData = {
  id?: number
  templateCode: string
  templateName: string
  templateType: string
  channelType: string
  content: string
  status: string
}

const formData = ref<FormData>({
  templateCode: '',
  templateName: '',
  templateType: '',
  channelType: 'IN_APP',
  content: '',
  status: 'ENABLE'
})

const formRules = reactive<FormRules>({
  templateCode: [{ required: true, message: '模板编码不能为空', trigger: 'blur' }],
  templateName: [{ required: true, message: '模板名称不能为空', trigger: 'blur' }],
  templateType: [{ required: true, message: '模板类型不能为空', trigger: 'blur' }],
  channelType: [{ required: true, message: '渠道类型不能为空', trigger: 'change' }],
  content: [{ required: true, message: '模板内容不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'change' }]
})

const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  if (id) {
    formLoading.value = true
    try {
      const data = await MessageTemplateApi.getMessageTemplate(id)
      formData.value = buildFormData(data)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open })

const emit = defineEmits(['success'])
const submitForm = async () => {
  await formRef.value?.validate()
  formLoading.value = true
  try {
    const data = formData.value
    if (formType.value === 'create') {
      await MessageTemplateApi.createMessageTemplate(data)
      message.success(t('common.createSuccess'))
    } else {
      await MessageTemplateApi.updateMessageTemplate(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}

const resetForm = () => {
  formData.value = {
    id: undefined,
    templateCode: '',
    templateName: '',
    templateType: '',
    channelType: 'IN_APP',
    content: '',
    status: 'ENABLE'
  }
  formRef.value?.resetFields()
}

const buildFormData = (data: MessageTemplateDetail): FormData => ({
  id: data.id,
  templateCode: data.templateCode,
  templateName: data.templateName,
  templateType: data.templateType,
  channelType: data.channelType,
  content: data.content,
  status: data.status
})
</script>
