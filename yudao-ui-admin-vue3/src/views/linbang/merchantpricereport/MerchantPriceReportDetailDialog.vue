<template>
  <Dialog v-model="dialogVisible" title="价格申报详情" width="920px" :loading="detailLoading">
    <el-descriptions :column="2" label-class-name="desc-label">
      <el-descriptions-item label="服务商">{{ formatMerchantDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="合作商">{{ formatPartnerDisplay() }}</el-descriptions-item>
      <el-descriptions-item label="类目">{{ detailData.categoryName || (detailData.categoryId ?? '-') }}</el-descriptions-item>
      <el-descriptions-item label="区域编码">{{ detailData.regionCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="建议价格">{{ detailData.suggestedPrice ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="getPriceReportStatusTagType(detailData.status)">
          {{ formatPriceReportStatus(detailData.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="审核状态">
        <dict-tag
          v-if="detailData.auditStatus"
          :type="DICT_TYPE.LB_PRICE_REPORT_AUDIT_STATUS"
          :value="detailData.auditStatus"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDate(detailData.createTime) }}</el-descriptions-item>
      <el-descriptions-item label="更新时间">{{ formatDate(detailData.updateTime) }}</el-descriptions-item>
    </el-descriptions>
    <el-divider />
    <el-descriptions :column="1" border direction="vertical">
      <el-descriptions-item label="备注">{{ detailData.remark || '-' }}</el-descriptions-item>
      <el-descriptions-item label="审核备注">{{ detailData.auditRemark || '-' }}</el-descriptions-item>
      <el-descriptions-item label="驳回原因">{{ detailData.rejectReason || '-' }}</el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">合作商摘要</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="合作商名称">
        {{ detailData.partner?.partnerName || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="联系人">
        {{ detailData.partner?.contactName || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="联系手机">
        {{ detailData.partner?.contactMobile || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="合作商状态">
        {{ formatEnableStatus(detailData.partner?.status) }}
      </el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">服务商入驻摘要</el-divider>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="入驻单号">{{ detailData.merchantEntry?.entryNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="申请用户">
        {{ formatMerchantEntryUser() }}
      </el-descriptions-item>
      <el-descriptions-item label="入驻区域">{{ detailData.merchantEntry?.regionCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="初审状态">
        <dict-tag
          v-if="detailData.merchantEntry?.firstAuditStatus"
          :type="DICT_TYPE.LB_AUDIT_STATUS"
          :value="detailData.merchantEntry.firstAuditStatus"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="终审状态">
        <dict-tag
          v-if="detailData.merchantEntry?.finalAuditStatus"
          :type="DICT_TYPE.LB_AUDIT_STATUS"
          :value="detailData.merchantEntry.finalAuditStatus"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="入驻状态">
        <dict-tag
          v-if="detailData.merchantEntry?.status"
          :type="DICT_TYPE.LB_MERCHANT_ENTRY_STATUS"
          :value="detailData.merchantEntry.status"
        />
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ formatDate(detailData.merchantEntry?.createTime) }}
      </el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">关联统计</el-divider>
    <el-descriptions :column="4" border>
      <el-descriptions-item label="关联记录数">{{ detailData.summary?.totalRelatedCount ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="待处理">{{ detailData.summary?.pendingCount ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="已通过">{{ detailData.summary?.approvedCount ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="已驳回">{{ detailData.summary?.rejectedCount ?? 0 }}</el-descriptions-item>
      <el-descriptions-item label="平均建议价">
        {{ detailData.summary?.avgSuggestedPrice ?? 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="最低建议价">
        {{ detailData.summary?.minSuggestedPrice ?? 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="最高建议价">
        {{ detailData.summary?.maxSuggestedPrice ?? 0 }}
      </el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">最近关联申报</el-divider>
    <el-table :data="detailData.relatedReports || []" size="small" border>
      <el-table-column label="服务商" prop="merchantName" min-width="160" />
      <el-table-column label="类目" prop="categoryName" min-width="140" />
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
import { DICT_TYPE } from '@/utils/dict'
import { dateFormatter, formatDate } from '@/utils/formatTime'
import { MerchantPriceReportApi, type MerchantPriceReportDetail } from '@/api/linbang/merchantpricereport'
import { formatEnableStatus, formatPriceReportStatus, getPriceReportStatusTagType } from '../utils/display'

defineOptions({ name: 'MerchantPriceReportDetailDialog' })

const dialogVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref<MerchantPriceReportDetail>({} as MerchantPriceReportDetail)

const formatMerchantDisplay = () => {
  const merchant = detailData.value.merchant
  return [merchant?.merchantName, merchant?.contactName, merchant?.contactMobile].filter(Boolean).join(' / ')
    || (detailData.value.merchantId ? '服务商信息缺失' : '-')
}

const formatPartnerDisplay = () => {
  const partner = detailData.value.partner
  return [partner?.partnerName, partner?.contactName, partner?.contactMobile].filter(Boolean).join(' / ')
    || (detailData.value.partnerId ? '合作商信息缺失' : '-')
}

const formatMerchantEntryUser = () => {
  const merchantEntry = detailData.value.merchantEntry
  const summary = [merchantEntry?.userNickname, merchantEntry?.userMobile, merchantEntry?.userNo]
    .filter(Boolean)
    .join(' / ')
  return summary || (merchantEntry?.userId ? '用户信息缺失' : '-')
}

const open = async (id: number) => {
  dialogVisible.value = true
  detailLoading.value = true
  try {
    detailData.value = await MerchantPriceReportApi.getMerchantPriceReport(id)
    if (!detailData.value.relatedReports) {
      detailData.value.relatedReports = []
    }
  } finally {
    detailLoading.value = false
  }
}

defineExpose({ open })
</script>
