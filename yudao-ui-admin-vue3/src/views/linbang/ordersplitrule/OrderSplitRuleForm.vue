<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="120px" v-loading="formLoading">
      <el-form-item label="规则名称" prop="ruleName">
        <el-input v-model="formData.ruleName" placeholder="请输入规则名称" />
      </el-form-item>
      <el-form-item label="规则编码" prop="ruleCode">
        <el-input v-model="formData.ruleCode" placeholder="请输入规则编码" />
      </el-form-item>
      <el-form-item label="命中模式" prop="matchMode">
        <el-select v-model="formData.matchMode" placeholder="请选择命中模式">
          <el-option label="任一条件命中" value="ANY" />
          <el-option label="全部条件命中" value="ALL" />
        </el-select>
      </el-form-item>
      <el-form-item label="适用品类" prop="categoryId">
        <el-tree-select
          v-model="formData.categoryId"
          :data="categoryTree"
          :props="{ label: 'categoryName', children: 'children' }"
          check-strictly
          clearable
          default-expand-all
          filterable
          node-key="id"
          placeholder="请选择适用品类"
        />
      </el-form-item>
      <el-form-item label="适用计价方式" prop="applicablePricingModes">
        <el-select v-model="formData.applicablePricingModes" multiple clearable placeholder="为空表示全部适用">
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_PRICING_MODE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="最小金额" prop="minOrderAmount">
        <el-input-number v-model="formData.minOrderAmount" :min="0" :precision="2" class="!w-220px" />
      </el-form-item>
      <el-form-item label="最小数量" prop="minQuantity">
        <el-input-number v-model="formData.minQuantity" :min="0" :precision="2" class="!w-220px" />
      </el-form-item>
      <el-form-item label="最小人数" prop="minWorkerCount">
        <el-input-number v-model="formData.minWorkerCount" :min="1" class="!w-220px" />
      </el-form-item>
      <el-form-item label="拆分方式" prop="splitMode">
        <el-select v-model="formData.splitMode" placeholder="请选择拆分方式">
          <el-option label="直接单" value="DIRECT" />
          <el-option label="按进度拆分" value="BY_PROGRESS" />
          <el-option label="按工序拆分" value="BY_PROCESS" />
          <el-option label="按内容拆分" value="BY_CONTENT" />
          <el-option label="按多人拆分" value="BY_PERSON" />
        </el-select>
      </el-form-item>
      <el-form-item label="默认单元数" prop="defaultUnitCount">
        <el-input-number v-model="formData.defaultUnitCount" :min="1" class="!w-220px" />
      </el-form-item>
      <el-form-item label="单元金额上限" prop="unitAmountLimit">
        <el-input-number v-model="formData.unitAmountLimit" :min="0.01" :precision="2" class="!w-220px" />
      </el-form-item>
      <el-form-item label="标题前缀">
        <el-input v-model="formData.unitTemplate.titlePrefix" placeholder="如 工程单元" />
      </el-form-item>
      <el-form-item label="内容模板">
        <el-input v-model="formData.unitTemplate.contentTemplate" placeholder="支持 {seq}、{splitMode}" />
      </el-form-item>
      <el-form-item label="锁定原因">
        <el-input v-model="formData.unitTemplate.lockReasonTemplate" placeholder="如 待前序单元完成" />
      </el-form-item>
      <el-form-item label="排序号" prop="sortNo">
        <el-input-number v-model="formData.sortNo" :min="0" class="!w-220px" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio v-for="item in ENABLE_STATUS_OPTIONS" :key="item.value" :value="item.value">
            {{ item.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button type="primary" :disabled="formLoading" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { getStrDictOptions, DICT_TYPE } from '@/utils/dict'
import { handleTree } from '@/utils/tree'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
import { MerchantServiceCategoryApi } from '@/api/linbang/merchantcategory'
import { OrderSplitRuleApi, type OrderSplitRule } from '@/api/linbang/ordersplitrule'
import { ENABLE_STATUS_OPTIONS } from '../utils/display'

defineOptions({ name: 'OrderSplitRuleForm' })

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')
const formRef = ref()
const categoryTree = ref<any[]>([])
const formData = ref<OrderSplitRule & { unitTemplate: Record<string, string> }>({
  matchMode: 'ANY',
  applicablePricingModes: [],
  splitMode: 'BY_PROGRESS',
  defaultUnitCount: 2,
  unitAmountLimit: 200,
  sortNo: 0,
  status: 'ENABLE',
  unitTemplate: {}
})
const formRules = reactive({
  ruleName: [{ required: true, message: '规则名称不能为空', trigger: 'blur' }],
  ruleCode: [{ required: true, message: '规则编码不能为空', trigger: 'blur' }],
  matchMode: [{ required: true, message: '命中模式不能为空', trigger: 'change' }],
  splitMode: [{ required: true, message: '拆分方式不能为空', trigger: 'change' }],
  defaultUnitCount: [{ required: true, message: '默认单元数不能为空', trigger: 'blur' }],
  unitAmountLimit: [{ required: true, message: '单元金额上限不能为空', trigger: 'blur' }],
  sortNo: [{ required: true, message: '排序号不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'change' }]
})

const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  await loadCategoryTree()
  if (id) {
    formLoading.value = true
    try {
      const data = await OrderSplitRuleApi.get(id)
      formData.value = {
        ...data,
        applicablePricingModes: data.applicablePricingModes || [],
        unitTemplate: data.unitTemplate || {}
      }
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open })

const emit = defineEmits(['success'])

const submitForm = async () => {
  await formRef.value.validate()
  formLoading.value = true
  try {
    const data = {
      ...formData.value,
      applicablePricingModes: formData.value.applicablePricingModes || [],
      unitTemplate: formData.value.unitTemplate || {}
    }
    if (formType.value === 'create') {
      await OrderSplitRuleApi.create(data)
      message.success(t('common.createSuccess'))
    } else {
      await OrderSplitRuleApi.update(data)
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
    matchMode: 'ANY',
    applicablePricingModes: [],
    splitMode: 'BY_PROGRESS',
    defaultUnitCount: 2,
    unitAmountLimit: 200,
    sortNo: 0,
    status: 'ENABLE',
    unitTemplate: {}
  }
  formRef.value?.resetFields()
}

const loadCategoryTree = async () => {
  const categories = await MerchantServiceCategoryApi.getMerchantServiceCategoryList()
  categoryTree.value = handleTree(categories, 'id', 'parentId', 'children')
}
</script>
