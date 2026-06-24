<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="规则编码" prop="ruleCode">
        <el-input v-model="formData.ruleCode" placeholder="请输入规则编码" />
      </el-form-item>
      <el-form-item label="规则名称" prop="ruleName">
        <el-input v-model="formData.ruleName" placeholder="请输入规则名称" />
      </el-form-item>
      <el-form-item label="分值变动" prop="scoreChange">
        <el-input v-model="formData.scoreChange" placeholder="请输入分值变动" />
      </el-form-item>
      <el-form-item label="触发类型" prop="triggerType">
        <el-select v-model="formData.triggerType" placeholder="请选择触发类型">
          <el-option
            v-for="item in TRIGGER_TYPE_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
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
import { CreditRuleApi, type CreditRuleDetail, type CreditRuleFormData } from '@/api/linbang/creditrule'
import { ENABLE_STATUS_OPTIONS, TRIGGER_TYPE_OPTIONS } from '../utils/display'

import { reactive, ref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
/** 信用分规则 表单 */
defineOptions({ name: 'CreditRuleForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
type FormData = {
  id?: number
  ruleCode?: string
  ruleName?: string
  scoreChange?: number
  triggerType?: string
  status?: string
}

const createEmptyFormData = (): FormData => ({
  id: undefined,
  ruleCode: undefined,
  ruleName: undefined,
  scoreChange: undefined,
  triggerType: undefined,
  status: undefined
})
const formData = ref<FormData>(createEmptyFormData())
const formRules = reactive({
  ruleCode: [{ required: true, message: '规则编码不能为空', trigger: 'blur' }],
  ruleName: [{ required: true, message: '规则名称不能为空', trigger: 'blur' }],
  scoreChange: [{ required: true, message: '分值变动不能为空', trigger: 'blur' }],
  triggerType: [{ required: true, message: '触发类型不能为空', trigger: 'change' }],
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
      const detail = await CreditRuleApi.getCreditRule(id)
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
    const data = formData.value as CreditRuleFormData
    if (formType.value === 'create') {
      await CreditRuleApi.createCreditRule(data)
      message.success(t('common.createSuccess'))
    } else {
      await CreditRuleApi.updateCreditRule(data)
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

const buildFormData = (data: CreditRuleDetail): FormData => ({
  id: data.id,
  ruleCode: data.ruleCode,
  ruleName: data.ruleName,
  scoreChange: data.scoreChange,
  triggerType: data.triggerType,
  status: data.status
})
</script>
