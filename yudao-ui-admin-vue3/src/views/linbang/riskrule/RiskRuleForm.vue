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
      <el-form-item label="规则分组" prop="ruleGroup">
        <el-select v-model="formData.ruleGroup" placeholder="请选择规则分组">
          <el-option
            v-for="item in RISK_RULE_GROUP_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="规则值" prop="ruleValue">
        <el-input v-model="formData.ruleValue" placeholder="请输入规则值" />
      </el-form-item>
      <el-form-item label="值类型" prop="valueType">
        <el-select v-model="formData.valueType" placeholder="请选择值类型">
          <el-option
            v-for="item in RISK_RULE_VALUE_TYPE_OPTIONS"
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
      <el-form-item label="备注" prop="remark">
        <el-input v-model="formData.remark" placeholder="请输入备注" />
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
import { RiskRuleApi, type RiskRuleFormData } from '@/api/linbang/riskrule'
import { requestDynamicKeyToken } from '../shared/dynamic-key'
import {
  ENABLE_STATUS_OPTIONS,
  RISK_RULE_GROUP_OPTIONS,
  RISK_RULE_VALUE_TYPE_OPTIONS
} from '../utils/display'

/** 风控规则表 表单 */
defineOptions({ name: 'RiskRuleForm' })

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
  ruleGroup?: string
  ruleValue?: string
  valueType?: string
  status?: string
  remark?: string
}

const createEmptyFormData = (): FormData => ({
  id: undefined,
  ruleCode: undefined,
  ruleName: undefined,
  ruleGroup: undefined,
  ruleValue: undefined,
  valueType: undefined,
  status: undefined,
  remark: undefined
})
const formData = ref<FormData>(createEmptyFormData())
const formRules = reactive({
  ruleCode: [{ required: true, message: '规则编码不能为空', trigger: 'blur' }],
  ruleName: [{ required: true, message: '规则名称不能为空', trigger: 'blur' }],
  ruleGroup: [{ required: true, message: '规则分组不能为空', trigger: 'blur' }],
  ruleValue: [{ required: true, message: '规则值不能为空', trigger: 'blur' }],
  valueType: [{ required: true, message: '值类型不能为空', trigger: 'change' }],
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
      const detail = await RiskRuleApi.getRiskRule(id)
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
    const data = formData.value as RiskRuleFormData
    const verifyToken = await requestDynamicKeyToken(formType.value === 'create' ? '新增风控规则' : '修改风控规则')
    if (formType.value === 'create') {
      await RiskRuleApi.createRiskRule(data, verifyToken)
      message.success(t('common.createSuccess'))
    } else {
      await RiskRuleApi.updateRiskRule(data, verifyToken)
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

const buildFormData = (data: RiskRuleFormData): FormData => ({
  id: data.id,
  ruleCode: data.ruleCode,
  ruleName: data.ruleName,
  ruleGroup: data.ruleGroup,
  ruleValue: data.ruleValue,
  valueType: data.valueType,
  status: data.status,
  remark: data.remark
})
</script>
