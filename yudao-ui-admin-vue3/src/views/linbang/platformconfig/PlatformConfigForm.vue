<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="700px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="配置分类" prop="category">
        <el-select v-model="formData.category" placeholder="请选择配置分类">
          <el-option
            v-for="item in PLATFORM_CONFIG_CATEGORY_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="配置名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入配置名称" />
      </el-form-item>
      <el-form-item label="配置键" prop="key">
        <el-input v-model="formData.key" placeholder="请输入配置键" />
      </el-form-item>
      <el-form-item label="配置值" prop="value">
        <el-input
          v-model="formData.value"
          type="textarea"
          :rows="5"
          maxlength="4000"
          show-word-limit
          placeholder="请输入配置值"
        />
      </el-form-item>
      <el-form-item label="前台可见" prop="visible">
        <el-radio-group v-model="formData.visible">
          <el-radio :label="true">可见</el-radio>
          <el-radio :label="false">不可见</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="formData.remark"
          type="textarea"
          :rows="3"
          maxlength="255"
          show-word-limit
          placeholder="请输入备注"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button type="primary" :disabled="formLoading" @click="submitForm">确 定</el-button>
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
  PlatformConfigApi,
  type PlatformConfigDetail
} from '@/api/linbang/platformconfig'
import { requestDynamicKeyToken } from '../shared/dynamic-key'
import { PLATFORM_CONFIG_CATEGORY_OPTIONS } from '../utils/display'

defineOptions({ name: 'PlatformConfigForm' })

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')
const formRef = ref<FormInstance>()
type FormData = {
  id?: number
  category: string
  name: string
  key: string
  value: string
  visible: boolean
  remark?: string
}

const formData = ref<FormData>({
  category: 'linbang_platform',
  name: '',
  key: '',
  value: '',
  visible: true,
  remark: ''
})

const formRules = reactive<FormRules>({
  category: [{ required: true, message: '配置分类不能为空', trigger: 'change' }],
  name: [{ required: true, message: '配置名称不能为空', trigger: 'blur' }],
  key: [{ required: true, message: '配置键不能为空', trigger: 'blur' }],
  value: [{ required: true, message: '配置值不能为空', trigger: 'blur' }],
  visible: [{ required: true, message: '请选择是否可见', trigger: 'change' }]
})

const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  if (id) {
    formLoading.value = true
    try {
      const data = await PlatformConfigApi.getPlatformConfig(id)
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
    const verifyToken = await requestDynamicKeyToken(formType.value === 'create' ? '新增平台配置' : '修改平台配置')
    if (formType.value === 'create') {
      await PlatformConfigApi.createPlatformConfig(data, verifyToken)
      message.success(t('common.createSuccess'))
    } else {
      await PlatformConfigApi.updatePlatformConfig(data, verifyToken)
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
    category: 'linbang_platform',
    name: '',
    key: '',
    value: '',
    visible: true,
    remark: ''
  }
  formRef.value?.resetFields()
}

const buildFormData = (data: PlatformConfigDetail): FormData => ({
  id: data.id,
  category: data.category,
  name: data.name,
  key: data.key,
  value: data.value,
  visible: data.visible,
  remark: data.remark || ''
})
</script>
