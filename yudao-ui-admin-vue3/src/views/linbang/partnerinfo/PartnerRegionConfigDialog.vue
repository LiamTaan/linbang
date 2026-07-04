<template>
  <Dialog v-model="dialogVisible" title="配置合作商辖区" width="820px">
    <el-alert
      title="辖区由后台分配；身份申请只开通合作商身份，不在申请时自选辖区。"
      type="info"
      :closable="false"
      class="mb-16px"
    />
    <el-form label-width="96px" v-loading="loading">
      <el-form-item label="合作商">
        <el-input :model-value="partnerLabel" disabled />
      </el-form-item>
      <el-form-item label="新增辖区">
        <div class="flex w-full gap-12px">
          <el-cascader
            v-model="selectedAreaCodes"
            :options="areaOptions"
            :props="areaProps"
            clearable
            class="flex-1"
            placeholder="请选择省 / 市 / 区"
          />
          <el-button type="primary" @click="handleAddRegion">添加</el-button>
        </div>
      </el-form-item>
    </el-form>
    <el-table :data="regions" border>
      <el-table-column label="省" prop="province" min-width="120" />
      <el-table-column label="市" prop="city" min-width="120" />
      <el-table-column label="区" prop="district" min-width="120" />
      <el-table-column label="行政区编码" prop="adcode" width="140" />
      <el-table-column label="操作" align="center" width="100">
        <template #default="{ $index }">
          <el-button link type="danger" @click="regions.splice($index, 1)">移除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="regions.length === 0" description="请至少配置一个辖区" :image-size="72" class="mt-12px" />
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitLoading" @click="submit">保存辖区</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { getAreaTree } from '@/api/system/area'
import { PartnerInfoApi, type PartnerInfo, type PartnerRegionUpdateItem } from '@/api/linbang/partnerinfo'
import { useMessage } from '@/hooks/web/useMessage'

defineOptions({ name: 'PartnerRegionConfigDialog' })

type AreaNode = {
  name: string
  id: number
  children?: AreaNode[]
}

const message = useMessage()
const dialogVisible = ref(false)
const loading = ref(false)
const submitLoading = ref(false)
const areaOptions = ref<AreaNode[]>([])
const selectedAreaCodes = ref<number[]>([])
const partner = ref<PartnerInfo>()
const regions = ref<PartnerRegionUpdateItem[]>([])

const areaProps = {
  label: 'name',
  value: 'id',
  children: 'children',
  emitPath: true,
  checkStrictly: false
}

const partnerLabel = computed(() => {
  if (!partner.value) {
    return ''
  }
  return [partner.value.partnerName, partner.value.userNickname, partner.value.userMobile].filter(Boolean).join(' / ')
})

const ensureAreaOptions = async () => {
  if (areaOptions.value.length > 0) {
    return
  }
  areaOptions.value = (await getAreaTree()) || []
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

const handleAddRegion = () => {
  if (!selectedAreaCodes.value?.length) {
    message.warning('请先选择辖区')
    return
  }
  const nodes = findNodesByIds(selectedAreaCodes.value, areaOptions.value)
  if (nodes.length < 3) {
    message.warning('请至少选择到区县层级')
    return
  }
  const region = {
    province: nodes[0].name,
    city: nodes[1].name,
    district: nodes[2].name,
    adcode: String(nodes[nodes.length - 1].id)
  }
  if (regions.value.some((item) => item.adcode === region.adcode)) {
    message.warning('该辖区已添加')
    return
  }
  regions.value.push(region)
  selectedAreaCodes.value = []
}

const open = async (row: PartnerInfo) => {
  dialogVisible.value = true
  loading.value = true
  partner.value = row
  selectedAreaCodes.value = []
  try {
    await ensureAreaOptions()
    const detail = await PartnerInfoApi.getPartnerInfo(row.id)
    regions.value = (detail.regions || []).map((item) => ({
      province: item.province || '',
      city: item.city || '',
      district: item.district || '',
      adcode: item.adcode || ''
    }))
  } finally {
    loading.value = false
  }
}

const emit = defineEmits(['success'])

const submit = async () => {
  if (!partner.value) {
    return
  }
  if (regions.value.length === 0) {
    message.warning('请至少保留一个辖区')
    return
  }
  submitLoading.value = true
  try {
    await PartnerInfoApi.updatePartnerRegions({
      id: partner.value.id,
      regions: regions.value
    })
    message.success('辖区配置成功')
    dialogVisible.value = false
    emit('success')
  } finally {
    submitLoading.value = false
  }
}

defineExpose({ open })
</script>
