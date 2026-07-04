<template>
  <view class="partner-page">
    <view class="partner-header">
      <view class="partner-nav">
        <view class="partner-back" @click="goBack">‹</view>
        <text class="partner-nav-title">价格建议详情</text>
        <view class="partner-nav-placeholder"></view>
      </view>
      <text class="partner-header-title">{{ detail.categoryName || `类目${detail.categoryId || '--'}` }}</text>
      <text class="partner-header-desc">待审核可撤回，已通过 / 已驳回 / 已撤回状态只读展示。</text>
    </view>

    <view class="partner-content">
      <view class="partner-card">
        <view class="partner-card-header">
          <text class="partner-card-title">建议信息</text>
          <text class="partner-tag" :class="statusClass(detail.status)">{{ getPriceReportStatusLabel(detail.status) }}</text>
        </view>
        <view class="partner-info-grid">
          <view class="partner-info-row"><text class="partner-info-label">服务商</text><text class="partner-info-value">{{ detail.merchantName || '--' }}</text></view>
          <view class="partner-info-row"><text class="partner-info-label">类目</text><text class="partner-info-value">{{ detail.categoryName || detail.categoryId || '--' }}</text></view>
          <view class="partner-info-row"><text class="partner-info-label">辖区编码</text><text class="partner-info-value">{{ detail.regionCode || '--' }}</text></view>
          <view class="partner-info-row"><text class="partner-info-label">建议价格</text><text class="partner-info-value">{{ formatMoney(detail.suggestedPrice) }}</text></view>
          <view class="partner-info-row"><text class="partner-info-label">申报时间</text><text class="partner-info-value">{{ formatDateTime(detail.createTime) }}</text></view>
          <view class="partner-info-row"><text class="partner-info-label">申报备注</text><text class="partner-info-value">{{ detail.remark || '--' }}</text></view>
          <view class="partner-info-row"><text class="partner-info-label">审核备注</text><text class="partner-info-value">{{ detail.auditRemark || '--' }}</text></view>
          <view class="partner-info-row"><text class="partner-info-label">驳回原因</text><text class="partner-info-value">{{ detail.rejectReason || '--' }}</text></view>
        </view>
      </view>

      <view v-if="detail.status === 'PENDING'" class="partner-card">
        <view class="partner-card-header">
          <text class="partner-card-title">可执行动作</text>
        </view>
        <view class="partner-actions">
          <view class="partner-btn warn" @click="withdrawReport">撤回建议</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getPartnerPriceReport, withdrawPartnerPriceReport } from '@/api/partner'
import { confirmAction, ensurePartnerPageAccess, formatDateTime, formatMoney, getPriceReportStatusLabel, goBack } from './shared'

export default {
  data() {
    return {
      roleContext: {},
      id: '',
      detail: {}
    }
  },
  onLoad(options) {
    this.id = options.id || ''
  },
  onShow() {
    this.loadDetail()
  },
  methods: {
    goBack,
    formatDateTime,
    getPriceReportStatusLabel,
    formatMoney(value) {
      return formatMoney(this, value)
    },
    statusClass(status) {
      if (status === 'APPROVED') {
        return 'green'
      }
      if (status === 'REJECTED' || status === 'WITHDRAWN') {
        return 'red'
      }
      return 'orange'
    },
    async loadDetail() {
      const allowed = await ensurePartnerPageAccess(this, '价格建议详情')
      if (!allowed || !this.id) {
        return
      }
      this.detail = await getPartnerPriceReport(this.id).catch(() => ({}))
    },
    async withdrawReport() {
      const confirmed = await confirmAction({
        title: '确认撤回',
        content: '撤回后该价格建议将变为只读状态。'
      })
      if (!confirmed) {
        return
      }
      await withdrawPartnerPriceReport(Number(this.id))
      uni.showToast({ title: '已撤回', icon: 'success' })
      this.loadDetail()
    }
  }
}
</script>

<style>
@import "./common.css";
</style>
