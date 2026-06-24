<template>
    <Dialog v-model="dialogVisible" title="区域合作商详情" width="920px" :loading="detailLoading">
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item label="用户">{{ formatUserDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="合作商名称">{{ detailData.partnerName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="联系人">{{ detailData.contactName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="联系手机">{{ detailData.contactMobile || '-' }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="detailData.status === 'ENABLE' ? 'success' : 'info'">
          {{ formatEnableStatus(detailData.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="辖区编码" :span="2">
        {{ formatRegion(detailData.regionAdcodes) }}
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDate(detailData.createTime) }}</el-descriptions-item>
      <el-descriptions-item label="更新时间">{{ formatDate(detailData.updateTime) }}</el-descriptions-item>
    </el-descriptions>
    <el-divider />
    <el-descriptions :column="4" border>
      <el-descriptions-item label="辖区数">{{ detailData.summary?.regionCount ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="启用辖区数">{{ detailData.summary?.enabledRegionCount ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="待初审入驻">{{ detailData.summary?.pendingEntryAuditCount ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="待处理投诉">{{ detailData.summary?.pendingComplaintCount ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="待处理价格申报">
        {{ detailData.summary?.pendingPriceReportCount ?? 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="辖区订单数">{{ detailData.summary?.orderCount ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="辖区成交额">{{ detailData.summary?.tradeAmount ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="已通过申报价">
        {{ detailData.summary?.approvedPriceReportCount ?? 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="已驳回申报价">
        {{ detailData.summary?.rejectedPriceReportCount ?? 0 }}
      </el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">辖区明细</el-divider>
    <el-table :data="detailData.regions || []" size="small" border>
      <el-table-column label="省" prop="province" min-width="120" />
      <el-table-column label="市" prop="city" min-width="120" />
      <el-table-column label="区" prop="district" min-width="120" />
      <el-table-column label="行政区编码" prop="adcode" width="140" />
      <el-table-column label="状态" prop="status" width="100">
        <template #default="{ row }">
          {{ formatEnableStatus(row.status) }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
    </el-table>
    <el-divider content-position="left">最近价格申报</el-divider>
    <el-table :data="detailData.recentPriceReports || []" size="small" border>
      <el-table-column label="服务商" min-width="220">
        <template #default="{ row }">
          <div class="leading-20px">
            <div class="font-600">{{ row.merchantName || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.merchantContactMobile || '-' }}</div>
            <div class="text-[var(--el-text-color-secondary)]">{{ row.merchantContactName || '-' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="服务类目" min-width="160">
        <template #default="{ row }">
          {{ row.categoryName || (row.categoryId ? '类目信息缺失' : '-') }}
        </template>
      </el-table-column>
      <el-table-column label="区域编码" prop="regionCode" width="120" />
      <el-table-column label="建议价格" prop="suggestedPrice" width="120" />
      <el-table-column label="状态" prop="status" width="100">
        <template #default="{ row }">
          {{ formatPriceReportStatus(row.status) }}
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="180" />
      <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
    </el-table>
  </Dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { dateFormatter, formatDate } from '@/utils/formatTime'
import { PartnerInfoApi, type PartnerInfoDetail } from '@/api/linbang/partnerinfo'
import { formatEnableStatus, formatPriceReportStatus } from '../utils/display'

defineOptions({ name: 'PartnerInfoDetailDialog' })

const dialogVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref<PartnerInfoDetail>({} as PartnerInfoDetail)

const formatRegion = (regionAdcodes?: string[]) => {
  if (!regionAdcodes || regionAdcodes.length === 0) {
    return '-'
  }
  return regionAdcodes.join(' / ')
}

const formatUserDisplay = () => {
  const summary = [detailData.value.userNickname, detailData.value.userMobile, detailData.value.userNo]
    .filter(Boolean)
    .join(' / ')
  return summary || (detailData.value.userId ? '用户信息缺失' : '-')
}

const open = async (id: number) => {
  dialogVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await PartnerInfoApi.getPartnerInfo(id)
    if (!detailData.value.regions) {
      detailData.value.regions = []
    }
    if (!detailData.value.recentPriceReports) {
      detailData.value.recentPriceReports = []
    }
  } finally {
    detailLoading.value = false
  }
}

defineExpose({ open })
</script>
