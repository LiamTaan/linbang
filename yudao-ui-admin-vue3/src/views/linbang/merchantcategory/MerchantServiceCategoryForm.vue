<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="上级类目" prop="parentId">
        <el-tree-select
          v-model="formData.parentId"
          :data="categoryTree"
          :props="treeSelectProps"
          check-strictly
          clearable
          filterable
          node-key="id"
          placeholder="请选择上级类目"
        />
      </el-form-item>
      <el-form-item label="类目名称" prop="categoryName">
        <el-input v-model="formData.categoryName" placeholder="请输入类目名称" />
      </el-form-item>
      <el-form-item label="层级" prop="categoryLevel">
        <el-input v-model="formData.categoryLevel" placeholder="请输入层级" />
      </el-form-item>
      <el-form-item label="排序" prop="sortNo">
        <el-input v-model="formData.sortNo" placeholder="请输入排序" />
      </el-form-item>
      <el-form-item label="图标" prop="icon">
        <el-input v-model="formData.icon" placeholder="请输入图标" />
      </el-form-item>
      <el-form-item label="默认计价方式" prop="defaultPricingMode">
        <el-select v-model="formData.defaultPricingMode" placeholder="请选择默认计价方式" clearable>
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.LB_PRICING_MODE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="是否支持拆单" prop="supportSplit">
        <el-radio-group v-model="formData.supportSplit">
          <el-radio
            v-for="item in BOOLEAN_YES_NO_OPTIONS"
            :key="`split-${String(item.value)}`"
            :value="item.value"
          >
            {{ item.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="是否支持开票" prop="supportInvoice">
        <el-radio-group v-model="formData.supportInvoice">
          <el-radio
            v-for="item in BOOLEAN_YES_NO_OPTIONS"
            :key="`invoice-${String(item.value)}`"
            :value="item.value"
          >
            {{ item.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="风险等级" prop="riskLevel">
        <el-input v-model="formData.riskLevel" placeholder="请输入风险等级" />
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
import { getStrDictOptions, DICT_TYPE } from '@/utils/dict'
import { reactive, ref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
import { MerchantServiceCategoryApi, MerchantServiceCategory } from '@/api/linbang/merchantcategory'
import { BOOLEAN_YES_NO_OPTIONS, ENABLE_STATUS_OPTIONS } from '../utils/display'
import { handleTree } from '@/utils/tree'

/** 服务类目表 表单 */
defineOptions({ name: 'MerchantServiceCategoryForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
type CategoryTreeNode = {
  id: number
  categoryName?: string
  children?: CategoryTreeNode[]
  disabled?: boolean
  [key: string]: any
} & {
  children?: CategoryTreeNode[]
}
const categoryTree = ref<CategoryTreeNode[]>([])
const treeSelectProps = {
  label: 'categoryName',
  children: 'children',
  value: 'id'
}
type FormData = {
  id?: number
  parentId?: number
  categoryName?: string
  categoryLevel?: number
  sortNo?: number
  icon?: string
  defaultPricingMode?: string
  supportSplit?: boolean
  supportInvoice?: boolean
  riskLevel?: string
  status?: string
}

const formData = ref<FormData>({
  id: undefined,
  parentId: 0,
  categoryName: undefined,
  categoryLevel: undefined,
  sortNo: undefined,
  icon: undefined,
  defaultPricingMode: undefined,
  supportSplit: undefined,
  supportInvoice: undefined,
  riskLevel: undefined,
  status: undefined
})
const formRules = reactive({
  categoryName: [{ required: true, message: '类目名称不能为空', trigger: 'blur' }],
  categoryLevel: [{ required: true, message: '层级不能为空', trigger: 'blur' }],
  sortNo: [{ required: true, message: '排序不能为空', trigger: 'blur' }],
  supportSplit: [{ required: true, message: '是否支持拆单不能为空', trigger: 'change' }],
  supportInvoice: [{ required: true, message: '是否支持开票不能为空', trigger: 'change' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'change' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  await loadCategoryTree()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      const data = await MerchantServiceCategoryApi.getMerchantServiceCategory(id)
      formData.value = buildFormData(data)
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
    const data = formData.value as unknown as MerchantServiceCategory
    if (formType.value === 'create') {
      await MerchantServiceCategoryApi.createMerchantServiceCategory(data)
      message.success(t('common.createSuccess'))
    } else {
      await MerchantServiceCategoryApi.updateMerchantServiceCategory(data)
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
    id: undefined,
    parentId: 0,
    categoryName: undefined,
    categoryLevel: undefined,
    sortNo: undefined,
    icon: undefined,
    defaultPricingMode: undefined,
    supportSplit: undefined,
    supportInvoice: undefined,
    riskLevel: undefined,
    status: undefined
  }
  formRef.value?.resetFields()
}

const loadCategoryTree = async () => {
  const categories = await MerchantServiceCategoryApi.getMerchantServiceCategoryAllList()
  const tree = handleTree(categories, 'id', 'parentId', 'children')
  categoryTree.value = [
    {
      id: 0,
      categoryName: '顶级类目',
      children: tree
    }
  ]
}

const buildFormData = (data: MerchantServiceCategory): FormData => ({
  id: data.id,
  parentId: data.parentId ?? 0,
  categoryName: data.categoryName,
  categoryLevel: data.categoryLevel,
  sortNo: data.sortNo,
  icon: data.icon,
  defaultPricingMode: data.defaultPricingMode,
  supportSplit: data.supportSplit,
  supportInvoice: data.supportInvoice,
  riskLevel: data.riskLevel,
  status: data.status
})
</script>
