<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="720px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="110px"
      v-loading="formLoading"
    >
      <el-alert
        title="区域编码会根据省市区自动带出；经纬度应由后端高德服务自动计算，不再允许后台手填。"
        type="info"
        :closable="false"
        class="mb-16px"
      />
      <el-form-item label="用户" prop="userId">
        <el-input
          :model-value="selectedUserLabel"
          placeholder="请选择用户"
          readonly
          @click="openUserDialog"
        >
          <template #append>
            <el-button @click="openUserDialog">选择</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="联系人" prop="receiverName">
        <el-input v-model="formData.receiverName" placeholder="请输入联系人" />
      </el-form-item>
      <el-form-item label="联系电话" prop="receiverMobile">
        <el-input v-model="formData.receiverMobile" placeholder="请输入联系电话" />
      </el-form-item>
      <el-form-item label="省市区" prop="areaCodes">
        <el-cascader
          v-model="formData.areaCodes"
          :options="areaOptions"
          :props="areaProps"
          clearable
          class="!w-1/1"
          placeholder="请选择省 / 市 / 区"
          @change="handleAreaChange"
        />
      </el-form-item>
      <el-form-item label="街道 / 乡镇" prop="street">
        <el-input v-model="formData.street" placeholder="请输入街道或乡镇" />
      </el-form-item>
      <el-form-item label="详细地址" prop="detailAddress">
        <el-input v-model="formData.detailAddress" placeholder="请输入详细地址" />
      </el-form-item>
      <el-row :gutter="12">
        <el-col :span="8">
          <el-form-item label="区域编码">
            <el-input :model-value="formData.adcode || ''" placeholder="按省市区自动带出" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="经度">
            <el-input :model-value="formatCoordinate(formData.longitude)" placeholder="由后端自动计算" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="纬度">
            <el-input :model-value="formatCoordinate(formData.latitude)" placeholder="由后端自动计算" disabled />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="设为默认" prop="isDefault">
        <el-switch
          v-model="formData.isDefault"
          inline-prompt
          active-text="是"
          inactive-text="否"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
  <MemberUserSelectDialog ref="userSelectDialogRef" @selected="handleUserSelected" />
</template>
<script setup lang="ts">
import type { FormRules } from 'element-plus'
import { computed, ref } from 'vue'
import { getAreaTree } from '@/api/system/area'
import { useI18n } from '@/hooks/web/useI18n'
import { useMessage } from '@/hooks/web/useMessage'
import { MemberUserAddressApi, type MemberUserAddress } from '@/api/linbang/memberaddress'
import { MemberUserApi, type MemberUser, type MemberUserDetail } from '@/api/linbang/memberuser'
import MemberUserSelectDialog from './MemberUserSelectDialog.vue'

/** 用户地址表 表单 */
defineOptions({ name: 'MemberUserAddressForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const userSelectDialogRef = ref()

type AreaNode = {
  name: string
  id: number
  children?: AreaNode[]
}

type FormData = {
  id?: number
  userId?: number
  receiverName?: string
  receiverMobile?: string
  province?: string
  city?: string
  district?: string
  street?: string
  detailAddress?: string
  areaCodes?: number[]
  longitude?: number
  latitude?: number
  adcode?: string
  isDefault?: boolean
}

const formData = ref<FormData>({
  id: undefined,
  userId: undefined,
  receiverName: undefined,
  receiverMobile: undefined,
  province: undefined,
  city: undefined,
  district: undefined,
  street: undefined,
  detailAddress: undefined,
  areaCodes: [],
  longitude: undefined,
  latitude: undefined,
  adcode: undefined,
  isDefault: false
})
const formRules = ref<FormRules>({
  userId: [{ required: true, message: '用户不能为空', trigger: 'change' }],
  receiverName: [{ required: true, message: '联系人不能为空', trigger: 'blur' }],
  receiverMobile: [
    { required: true, message: '联系电话不能为空', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的联系电话', trigger: 'blur' }
  ],
  areaCodes: [{ required: true, message: '省市区不能为空', trigger: 'change' }],
  detailAddress: [{ required: true, message: '详细地址不能为空', trigger: 'blur' }],
  isDefault: [{ required: true, message: '是否默认不能为空', trigger: 'change' }]
})
const formRef = ref() // 表单 Ref
const areaOptions = ref<AreaNode[]>([])

const areaProps = {
  label: 'name',
  value: 'id',
  children: 'children',
  emitPath: true,
  checkStrictly: false
}

const selectedUser = ref<MemberUser | undefined>()

const selectedUserLabel = computed(() => {
  if (!selectedUser.value) {
    return ''
  }
  return [selectedUser.value.userNo, selectedUser.value.nickname, selectedUser.value.mobile]
    .filter(Boolean)
    .join(' / ')
})

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  await ensureAreaOptions()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      const data = await MemberUserAddressApi.getMemberUserAddress(id)
      formData.value = buildFormData(data)
      await loadSelectedUser(data.userId)
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
    const data = {
      id: formData.value.id,
      userId: formData.value.userId,
      receiverName: formData.value.receiverName,
      receiverMobile: formData.value.receiverMobile,
      province: formData.value.province,
      city: formData.value.city,
      district: formData.value.district,
      street: formData.value.street,
      detailAddress: formData.value.detailAddress,
      longitude: formData.value.longitude,
      latitude: formData.value.latitude,
      adcode: formData.value.adcode,
      isDefault: formData.value.isDefault
    } as MemberUserAddress
    if (formType.value === 'create') {
      await MemberUserAddressApi.createMemberUserAddress(data)
      message.success(t('common.createSuccess'))
    } else {
      await MemberUserAddressApi.updateMemberUserAddress(data)
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
    userId: undefined,
    receiverName: undefined,
    receiverMobile: undefined,
    province: undefined,
    city: undefined,
    district: undefined,
    street: undefined,
    detailAddress: undefined,
    areaCodes: [],
    longitude: undefined,
    latitude: undefined,
    adcode: undefined,
    isDefault: false
  }
  selectedUser.value = undefined
  formRef.value?.resetFields()
}

const buildFormData = (data: MemberUserAddress): FormData => ({
  id: data.id,
  userId: data.userId,
  receiverName: data.receiverName,
  receiverMobile: data.receiverMobile,
  province: data.province,
  city: data.city,
  district: data.district,
  street: data.street,
  detailAddress: data.detailAddress,
  areaCodes: buildAreaCodes(data),
  longitude: data.longitude,
  latitude: data.latitude,
  adcode: data.adcode,
  isDefault: data.isDefault ?? false
})

const openUserDialog = () => {
  userSelectDialogRef.value.open(selectedUser.value)
}

const handleUserSelected = (row: MemberUser) => {
  selectedUser.value = row
  formData.value.userId = row.id
}

const ensureAreaOptions = async () => {
  if (areaOptions.value.length > 0) {
    return
  }
  areaOptions.value = (await getAreaTree()) || []
}

const handleAreaChange = (codes?: number[]) => {
  if (!codes || codes.length === 0) {
    formData.value.province = undefined
    formData.value.city = undefined
    formData.value.district = undefined
    formData.value.adcode = undefined
    return
  }
  const nodes = findNodesByIds(codes, areaOptions.value)
  formData.value.province = nodes[0]?.name
  formData.value.city = nodes[1]?.name
  formData.value.district = nodes[2]?.name
  formData.value.adcode = nodes.length > 0 ? String(nodes[nodes.length - 1].id) : undefined
}

const findNodesByIds = (ids: number[], nodes: AreaNode[]) => {
  const path: AreaNode[] = []
  let currentNodes = nodes
  ids.forEach((id) => {
    const match = currentNodes.find((item) => item.id === id)
    if (!match) {
      return
    }
    path.push(match)
    currentNodes = match.children || []
  })
  return path
}

const findIdPathByAdcode = (nodes: AreaNode[], adcode?: string, path: number[] = []): number[] | undefined => {
  if (!adcode) {
    return undefined
  }
  for (const node of nodes) {
    const currentPath = [...path, node.id]
    if (String(node.id) === adcode) {
      return currentPath
    }
    if (node.children?.length) {
      const childPath = findIdPathByAdcode(node.children, adcode, currentPath)
      if (childPath) {
        return childPath
      }
    }
  }
  return undefined
}

const buildAreaCodes = (data: MemberUserAddress) => {
  const idPath = findIdPathByAdcode(areaOptions.value, data.adcode)
  return idPath || []
}

const loadSelectedUser = async (userId?: number) => {
  if (!userId) {
    selectedUser.value = undefined
    return
  }
  const detail = await MemberUserApi.getMemberUser(userId)
  selectedUser.value = buildSelectedUser(detail)
}

const buildSelectedUser = (detail: MemberUserDetail): MemberUser => ({
  id: detail.id,
  userNo: detail.userNo,
  mobile: detail.mobile,
  nickname: detail.nickname,
  avatar: detail.avatar,
  gender: detail.gender,
  birthday: detail.birthday,
  registerSource: detail.registerSource,
  currentRoleCode: detail.currentRoleCode,
  status: detail.status,
  lastLoginTime: detail.lastLoginTime,
  lastLoginIp: detail.lastLoginIp,
  remark: detail.remark,
  createTime: detail.createTime
})

const formatCoordinate = (value?: number) => {
  return value === undefined || value === null ? '' : String(value)
}
</script>
