<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="关键词" prop="word">
        <el-input v-model="formData.word" placeholder="请输入关键词" />
      </el-form-item>
      <el-form-item label="词库类型" prop="wordType">
        <el-select v-model="formData.wordType" placeholder="请选择词库类型">
          <el-option
            v-for="item in SENSITIVE_WORD_TYPE_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="匹配方式" prop="matchType">
        <el-select v-model="formData.matchType" placeholder="请选择匹配方式">
          <el-option
            v-for="item in SENSITIVE_WORD_MATCH_TYPE_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="拦截级别" prop="blockLevel">
        <el-select v-model="formData.blockLevel" placeholder="请选择拦截级别">
          <el-option label="直接拦截" value="BLOCK" />
          <el-option label="人工复核" value="REVIEW" />
        </el-select>
      </el-form-item>
      <el-form-item label="适用场景" prop="sceneType">
        <el-input v-model="formData.sceneType" placeholder="例如 MESSAGE,COMMENT,PROMOTE" />
      </el-form-item>
      <el-form-item label="替换文案" prop="replaceText">
        <el-input v-model="formData.replaceText" placeholder="过滤模式下的替换文案，例如 ***" />
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
import { reactive, ref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
import { SensitiveWordApi, type SensitiveWordFormData } from '@/api/linbang/sensitiveword'
import {
  ENABLE_STATUS_OPTIONS,
  SENSITIVE_WORD_MATCH_TYPE_OPTIONS,
  SENSITIVE_WORD_TYPE_OPTIONS
} from '../utils/display'

/** 敏感词表 表单 */
defineOptions({ name: 'SensitiveWordForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
type FormData = {
  id?: number
  word?: string
  wordType?: string
  matchType?: string
  blockLevel?: string
  sceneType?: string
  replaceText?: string
  status?: string
}

const createEmptyFormData = (): FormData => ({
  id: undefined,
  word: undefined,
  wordType: undefined,
  matchType: undefined,
  blockLevel: undefined,
  sceneType: undefined,
  replaceText: undefined,
  status: undefined
})
const formData = ref<FormData>(createEmptyFormData())
const formRules = reactive({
  word: [{ required: true, message: '关键词不能为空', trigger: 'blur' }],
  wordType: [{ required: true, message: '词库类型不能为空', trigger: 'change' }],
  matchType: [{ required: true, message: '匹配方式不能为空', trigger: 'change' }],
  blockLevel: [{ required: true, message: '拦截级别不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      const detail = await SensitiveWordApi.getSensitiveWord(id)
      formData.value = buildFormData(detail)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as SensitiveWordFormData
    if (formType.value === 'create') {
      await SensitiveWordApi.createSensitiveWord(data)
      message.success(t('common.createSuccess'))
    } else {
      await SensitiveWordApi.updateSensitiveWord(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = createEmptyFormData()
  formRef.value?.resetFields()
}

const buildFormData = (data: SensitiveWordFormData): FormData => ({
  id: data.id,
  word: data.word,
  wordType: data.wordType,
  matchType: data.matchType,
  blockLevel: data.blockLevel,
  sceneType: data.sceneType,
  replaceText: data.replaceText,
  status: data.status
})
</script>
