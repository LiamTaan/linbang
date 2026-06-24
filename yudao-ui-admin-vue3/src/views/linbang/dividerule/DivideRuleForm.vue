<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="规则名称" prop="ruleName">
        <el-input v-model="formData.ruleName" placeholder="请输入规则名称" />
      </el-form-item>
      <el-form-item label="城市等级" prop="cityLevel">
        <el-input v-model="formData.cityLevel" placeholder="请输入城市等级" />
      </el-form-item>
      <el-form-item label="服务类目" prop="categoryId">
        <el-input :model-value="selectedCategoryLabel" placeholder="请选择服务类目" readonly @click="openCategoryDialog">
          <template #append>
            <el-button @click="openCategoryDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="服务商比例" prop="merchantRate">
        <el-input v-model="formData.merchantRate" placeholder="请输入服务商比例" />
      </el-form-item>
      <el-form-item label="平台比例" prop="platformRate">
        <el-input v-model="formData.platformRate" placeholder="请输入平台比例" />
      </el-form-item>
      <el-form-item label="合作商比例" prop="partnerRate">
        <el-input v-model="formData.partnerRate" placeholder="请输入合作商比例" />
      </el-form-item>
      <el-form-item label="推广员比例" prop="promoterRate">
        <el-input v-model="formData.promoterRate" placeholder="请输入推广员比例" />
      </el-form-item>
      <el-form-item label="个税代扣比例" prop="taxWithholdRate">
        <el-input v-model="formData.taxWithholdRate" placeholder="请输入个税代扣比例" />
      </el-form-item>
      <el-form-item label="最低提现金额" prop="minWithdrawAmount">
        <el-input v-model="formData.minWithdrawAmount" placeholder="请输入最低提现金额" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio v-for="item in ENABLE_STATUS_OPTIONS" :key="item.value" :value="item.value">
            {{ item.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="生效时间" prop="effectiveTime">
        <el-date-picker
          v-model="formData.effectiveTime"
          type="date"
          value-format="x"
          placeholder="选择生效时间"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
  <ServiceCategorySelectDialog ref="categorySelectDialogRef" @selected="handleCategorySelected" />
</template>
<script setup lang="ts">
import { DivideRuleApi, type DivideRuleDetail, type DivideRuleFormData } from '@/api/linbang/dividerule'
import {
  MerchantServiceCategoryApi,
  type MerchantServiceCategory
} from '@/api/linbang/merchantcategory'
import { ENABLE_STATUS_OPTIONS } from '../utils/display'
import ServiceCategorySelectDialog from '../shared/ServiceCategorySelectDialog.vue'

import { computed, reactive, ref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
import { requestDynamicKeyToken } from '../shared/dynamic-key'
/** 分账规则 表单 */
defineOptions({ name: 'DivideRuleForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const categorySelectDialogRef = ref()
const selectedCategory = ref<MerchantServiceCategory>()
type FormData = {
  id?: number
  ruleName?: string
  cityLevel?: string
  categoryId?: number
  merchantRate?: number
  platformRate?: number
  partnerRate?: number
  promoterRate?: number
  taxWithholdRate?: number
  minWithdrawAmount?: number
  status?: string
  effectiveTime?: string | number
}

const createEmptyFormData = (): FormData => ({
  id: undefined,
  ruleName: undefined,
  cityLevel: undefined,
  categoryId: undefined,
  merchantRate: undefined,
  platformRate: undefined,
  partnerRate: undefined,
  promoterRate: undefined,
  taxWithholdRate: undefined,
  minWithdrawAmount: undefined,
  status: undefined,
  effectiveTime: undefined
})
const selectedCategoryLabel = computed(() => {
  if (!selectedCategory.value) {
    return ''
  }
  return [selectedCategory.value.categoryName, selectedCategory.value.categoryLevel && `L${selectedCategory.value.categoryLevel}`]
    .filter(Boolean)
    .join(' / ')
})
const formData = ref<FormData>(createEmptyFormData())
const formRules = reactive({
  ruleName: [{ required: true, message: '规则名称不能为空', trigger: 'blur' }],
  categoryId: [{ required: true, message: '服务类目不能为空', trigger: 'change' }],
  merchantRate: [{ required: true, message: '服务商比例不能为空', trigger: 'blur' }],
  platformRate: [{ required: true, message: '平台比例不能为空', trigger: 'blur' }],
  partnerRate: [{ required: true, message: '合作商比例不能为空', trigger: 'blur' }],
  promoterRate: [{ required: true, message: '推广员比例不能为空', trigger: 'blur' }],
  taxWithholdRate: [{ required: true, message: '个税代扣比例不能为空', trigger: 'blur' }],
  minWithdrawAmount: [{ required: true, message: '最低提现金额不能为空', trigger: 'blur' }],
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
      const detail = await DivideRuleApi.getDivideRule(id)
      formData.value = buildFormData(detail)
      await loadSelectedCategory(detail.categoryId)
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
    const data = formData.value as DivideRuleFormData
    const verifyToken = await requestDynamicKeyToken(formType.value === 'create' ? '新增分账规则' : '修改分账规则')
    if (formType.value === 'create') {
      await DivideRuleApi.createDivideRule(data, verifyToken)
      message.success(t('common.createSuccess'))
    } else {
      await DivideRuleApi.updateDivideRule(data, verifyToken)
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
  formData.value = {
    ...createEmptyFormData()
  }
  selectedCategory.value = undefined
  formRef.value?.resetFields()
}

const openCategoryDialog = () => {
  categorySelectDialogRef.value?.open(selectedCategory.value)
}

const handleCategorySelected = (row: MerchantServiceCategory) => {
  selectedCategory.value = row
  formData.value.categoryId = row.id
}

const loadSelectedCategory = async (categoryId?: number) => {
  if (!categoryId) {
    selectedCategory.value = undefined
    return
  }
  selectedCategory.value = await MerchantServiceCategoryApi.getMerchantServiceCategory(categoryId)
}

const buildFormData = (data: DivideRuleDetail): FormData => ({
  id: data.id,
  ruleName: data.ruleName,
  cityLevel: data.cityLevel,
  categoryId: data.categoryId,
  merchantRate: data.merchantRate,
  platformRate: data.platformRate,
  partnerRate: data.partnerRate,
  promoterRate: data.promoterRate,
  taxWithholdRate: data.taxWithholdRate,
  minWithdrawAmount: data.minWithdrawAmount,
  status: data.status,
  effectiveTime: data.effectiveTime
})
</script>
