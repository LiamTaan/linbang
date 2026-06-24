<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="720px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="110px"
      v-loading="formLoading"
    >
      <el-form-item label="服务商" prop="merchantId">
        <el-input
          :model-value="selectedMerchantLabel"
          placeholder="请选择服务商"
          readonly
          @click="openMerchantDialog"
        >
          <template #append>
            <el-button @click="openMerchantDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="服务点名称" prop="pointName">
        <el-input v-model="formData.pointName" placeholder="请输入服务点名称" />
      </el-form-item>
      <el-row :gutter="12">
        <el-col :span="8">
          <el-form-item label="省" prop="province">
            <el-input v-model="formData.province" placeholder="请输入省" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="市" prop="city">
            <el-input v-model="formData.city" placeholder="请输入市" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="区" prop="district">
            <el-input v-model="formData.district" placeholder="请输入区" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="街道" prop="street">
        <el-input v-model="formData.street" placeholder="请输入街道" />
      </el-form-item>
      <el-form-item label="详细地址" prop="detailAddress">
        <el-input v-model="formData.detailAddress" placeholder="请输入详细地址" />
      </el-form-item>
      <el-row :gutter="12">
        <el-col :span="8">
          <el-form-item label="经度" prop="longitude">
            <el-input-number v-model="formData.longitude" :precision="6" class="!w-1/1" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="纬度" prop="latitude">
            <el-input-number v-model="formData.latitude" :precision="6" class="!w-1/1" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="服务半径" prop="serviceRadiusKm">
            <el-input-number
              v-model="formData.serviceRadiusKm"
              :precision="2"
              :min="0"
              class="!w-1/1"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="状态" prop="status">
        <el-select v-model="formData.status" placeholder="请选择状态" class="!w-1/1">
          <el-option
            v-for="item in ENABLE_STATUS_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
  <MerchantInfoSelectDialog ref="merchantSelectDialogRef" @selected="handleMerchantSelected" />
</template>

<script setup lang="ts">
import type { FormRules } from 'element-plus'
import { computed, reactive, ref } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
import {
  MerchantServicePointApi,
  type MerchantServicePointDetail
} from '@/api/linbang/merchantservicepoint'
import {
  MerchantInfoApi,
  type MerchantInfo,
  type MerchantInfoDetail
} from '@/api/linbang/merchantinfo'
import { ENABLE_STATUS_OPTIONS } from '../utils/display'
import MerchantInfoSelectDialog from '../shared/MerchantInfoSelectDialog.vue'

defineOptions({ name: 'MerchantServicePointForm' })

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')
const formRef = ref()
const merchantSelectDialogRef = ref()
type FormData = {
  id?: number
  merchantId?: number
  pointName?: string
  province?: string
  city?: string
  district?: string
  street?: string
  detailAddress?: string
  longitude?: number
  latitude?: number
  serviceRadiusKm?: number
  status?: string
}

const selectedMerchant = ref<MerchantInfo>()

const formData = ref<FormData>({
  id: undefined,
  merchantId: undefined,
  pointName: '',
  province: '',
  city: '',
  district: '',
  street: '',
  detailAddress: '',
  longitude: undefined,
  latitude: undefined,
  serviceRadiusKm: undefined,
  status: 'ENABLE'
})

const formRules = reactive<FormRules>({
  merchantId: [{ required: true, message: '服务商不能为空', trigger: 'change' }],
  pointName: [{ required: true, message: '服务点名称不能为空', trigger: 'blur' }],
  serviceRadiusKm: [{ required: true, message: '服务半径不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'change' }]
})

const selectedMerchantLabel = computed(() => {
  if (!selectedMerchant.value) {
    return ''
  }
  return [
    selectedMerchant.value.merchantName,
    selectedMerchant.value.contactName,
    selectedMerchant.value.contactMobile
  ]
    .filter(Boolean)
    .join(' / ')
})

const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  if (id) {
    formLoading.value = true
    try {
      const data = await MerchantServicePointApi.getMerchantServicePoint(id)
      formData.value = buildFormData(data)
      await loadSelectedMerchant(data.merchantId)
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
    const data = formData.value
    if (formType.value === 'create') {
      await MerchantServicePointApi.createMerchantServicePoint(data)
      message.success(t('common.createSuccess'))
    } else {
      await MerchantServicePointApi.updateMerchantServicePoint(data)
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
    merchantId: undefined,
    pointName: '',
    province: '',
    city: '',
    district: '',
    street: '',
    detailAddress: '',
    longitude: undefined,
    latitude: undefined,
    serviceRadiusKm: undefined,
    status: 'ENABLE'
  }
  selectedMerchant.value = undefined
  formRef.value?.resetFields()
}

const buildFormData = (data: MerchantServicePointDetail): FormData => ({
  id: data.id,
  merchantId: data.merchantId,
  pointName: data.pointName || '',
  province: data.province || '',
  city: data.city || '',
  district: data.district || '',
  street: data.street || '',
  detailAddress: data.detailAddress || '',
  longitude: data.longitude,
  latitude: data.latitude,
  serviceRadiusKm: data.serviceRadiusKm,
  status: data.status || 'ENABLE'
})

const openMerchantDialog = () => {
  merchantSelectDialogRef.value?.open(selectedMerchant.value)
}

const handleMerchantSelected = (row: MerchantInfo) => {
  selectedMerchant.value = row
  formData.value.merchantId = row.id
}

const loadSelectedMerchant = async (merchantId?: number) => {
  if (!merchantId) {
    selectedMerchant.value = undefined
    return
  }
  const detail = await MerchantInfoApi.getMerchantInfo(merchantId)
  selectedMerchant.value = buildSelectedMerchant(detail)
}

const buildSelectedMerchant = (detail: MerchantInfoDetail): MerchantInfo => ({
  id: detail.id,
  userId: detail.userId,
  userNo: detail.userNo,
  userNickname: detail.userNickname,
  userMobile: detail.userMobile,
  merchantName: detail.merchantName,
  contactName: detail.contactName,
  contactMobile: detail.contactMobile,
  serviceScopeDesc: detail.serviceScopeDesc,
  status: detail.status,
  acceptStatus: detail.acceptStatus,
  creditScore: detail.creditScore,
  creditLevel: detail.creditLevel,
  createTime: detail.createTime
})
</script>
