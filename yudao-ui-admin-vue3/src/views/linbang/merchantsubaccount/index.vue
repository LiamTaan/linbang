<template>
  <ContentWrap>
    <el-form :model="queryParams" :inline="true" label-width="88px" class="-mb-15px">
      <el-form-item label="商户 ID">
        <el-input v-model="queryParams.merchantId" placeholder="请输入商户 ID" class="!w-220px" clearable />
      </el-form-item>
      <el-form-item label="用户">
        <el-input v-model="queryParams.userKeyword" placeholder="请输入用户编号 / 昵称 / 手机号" class="!w-220px" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" class="!w-220px" clearable placeholder="请选择状态">
          <el-option label="启用" value="ENABLE" />
          <el-option label="禁用" value="DISABLE" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true">
      <el-table-column label="商户" min-width="160" prop="merchantName" />
      <el-table-column label="用户" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.userNickname || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.mobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.userNo || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="权限" min-width="220">
        <template #default="{ row }">{{ (row.permissionCodes || []).join(' / ') || '-' }}</template>
      </el-table-column>
      <el-table-column label="服务点" min-width="220">
        <template #default="{ row }">{{ (row.servicePointNames || []).join(' / ') || '-' }}</template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="100" />
      <el-table-column label="备注" prop="remark" min-width="160" />
      <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
      <el-table-column label="操作" fixed="right" width="180">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEditDialog(row)">编辑</el-button>
          <el-button link type="primary" @click="openPointDialog(row)">服务点</el-button>
          <el-button link type="warning" @click="toggleStatus(row)">
            {{ row.status === 'ENABLE' ? '禁用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </ContentWrap>

  <Dialog v-model="editVisible" title="商户子账号" width="560px">
    <el-form ref="editFormRef" :model="editFormData" :rules="editRules" label-width="100px">
      <el-form-item label="商户 ID" prop="merchantId">
        <el-input-number v-model="editFormData.merchantId" :min="1" class="!w-220px" />
      </el-form-item>
      <el-form-item label="用户 ID" prop="userId">
        <el-input-number v-model="editFormData.userId" :min="1" class="!w-220px" />
      </el-form-item>
      <el-form-item label="手机号" prop="mobile">
        <el-input v-model="editFormData.mobile" class="!w-220px" />
      </el-form-item>
      <el-form-item label="权限" prop="permissionCodes">
        <el-checkbox-group v-model="editFormData.permissionCodes">
          <el-checkbox value="ORDER_ACCEPT">接单</el-checkbox>
          <el-checkbox value="MERCHANT_MANAGE">商户管理</el-checkbox>
          <el-checkbox value="SERVICE_POINT_MANAGE">服务点管理</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="editFormData.remark" type="textarea" :rows="3" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="editVisible = false">取消</el-button>
      <el-button type="primary" @click="submitEdit">提交</el-button>
    </template>
  </Dialog>

  <Dialog v-model="pointVisible" title="配置服务点" width="560px">
    <el-form label-width="100px">
      <el-form-item label="服务点 ID 列表">
        <el-select v-model="pointFormData.servicePointIds" multiple filterable class="!w-360px" placeholder="请选择服务点">
          <el-option
            v-for="item in pointCandidates"
            :key="item.id"
            :label="`${item.pointName || '未命名服务点'} (${item.id})`"
            :value="item.id!"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="pointVisible = false">取消</el-button>
      <el-button type="primary" @click="submitPoints">提交</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { dateFormatter } from '@/utils/formatTime'
import { useMessage } from '@/hooks/web/useMessage'
import {
  MerchantSubAccountApi,
  type MerchantSubAccount,
  type MerchantSubAccountSaveReqVO,
  type MerchantSubAccountServicePointUpdateReqVO
} from '@/api/linbang/merchantsubaccount'
import { MerchantServicePointApi, type MerchantServicePoint } from '@/api/linbang/merchantservicepoint'

defineOptions({ name: 'LinbangMerchantSubAccount' })

const message = useMessage()
const loading = ref(false)
const editVisible = ref(false)
const pointVisible = ref(false)
const total = ref(0)
const list = ref<MerchantSubAccount[]>([])
const currentRow = ref<MerchantSubAccount>()
const editFormRef = ref<FormInstance>()
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  merchantId: undefined as number | undefined,
  userKeyword: undefined as string | undefined,
  status: undefined as string | undefined
})
const editFormData = reactive<MerchantSubAccountSaveReqVO>({
  merchantId: 1,
  userId: 1,
  mobile: '',
  permissionCodes: [],
  remark: ''
})
const pointFormData = reactive<MerchantSubAccountServicePointUpdateReqVO>({
  id: 0,
  servicePointIds: []
})
const pointCandidates = ref<MerchantServicePoint[]>([])
const editRules = reactive<FormRules>({
  merchantId: [{ required: true, message: '请输入商户 ID', trigger: 'blur' }],
  userId: [{ required: true, message: '请输入用户 ID', trigger: 'blur' }],
  mobile: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  permissionCodes: [{ required: true, message: '请选择权限', trigger: 'change' }]
})

const getList = async () => {
  loading.value = true
  try {
    const data = await MerchantSubAccountApi.getMerchantSubAccountPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const openEditDialog = (row?: MerchantSubAccount) => {
  currentRow.value = row
  editFormData.id = row?.id
  editFormData.merchantId = row?.merchantId || 1
  editFormData.userId = row?.userId || 1
  editFormData.mobile = row?.mobile || ''
  editFormData.permissionCodes = [...(row?.permissionCodes || [])]
  editFormData.remark = row?.remark || ''
  editVisible.value = true
}

const submitEdit = async () => {
  await editFormRef.value?.validate()
  if (editFormData.id) {
    await MerchantSubAccountApi.updateMerchantSubAccount(editFormData)
  } else {
    await MerchantSubAccountApi.createMerchantSubAccount(editFormData)
  }
  message.success('商户子账号保存成功')
  editVisible.value = false
  await getList()
}

const openPointDialog = (row: MerchantSubAccount) => {
  currentRow.value = row
  pointFormData.id = row.id!
  pointFormData.servicePointIds = [...(row.servicePointIds || [])]
  loadPointCandidates(row.merchantId)
  pointVisible.value = true
}

const loadPointCandidates = async (merchantId?: number) => {
  if (!merchantId) {
    pointCandidates.value = []
    return
  }
  const data = await MerchantServicePointApi.getMerchantServicePointPage({
    pageNo: 1,
    pageSize: 200,
    merchantId
  })
  pointCandidates.value = data.list || []
}

const submitPoints = async () => {
  await MerchantSubAccountApi.updateMerchantSubAccountServicePoints(pointFormData)
  message.success('服务点配置成功')
  pointVisible.value = false
  await getList()
}

const toggleStatus = async (row: MerchantSubAccount) => {
  await MerchantSubAccountApi.updateMerchantSubAccountStatus({
    id: row.id!,
    status: row.status === 'ENABLE' ? 'DISABLE' : 'ENABLE'
  })
  message.success('状态更新成功')
  await getList()
}

onMounted(() => getList())
</script>
