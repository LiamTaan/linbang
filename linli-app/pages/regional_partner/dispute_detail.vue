<template>
  <view class="partner-page">
    <view class="partner-header">
      <view class="partner-nav">
        <view class="partner-back" @click="goBack">‹</view>
        <text class="partner-nav-title">纠纷详情</text>
        <view class="partner-nav-placeholder"></view>
      </view>
      <text class="partner-header-title">{{ getDisputeTypeLabel(detail.disputeType) }} · {{ detail.orderNo || detail.disputeNo || '--' }}</text>
      <text class="partner-header-desc">协调意见提交后会立即回显到时间线，升级平台终审必须填写说明。</text>
    </view>

    <view class="partner-content">
      <view class="partner-card">
        <view class="partner-card-header">
          <text class="partner-card-title">纠纷信息</text>
          <text class="partner-tag" :class="statusClass(detail.status)">{{ getDisputeStatusLabel(detail.status) }}</text>
        </view>
        <view class="partner-info-grid">
          <view class="partner-info-row"><text class="partner-info-label">纠纷类型</text><text class="partner-info-value">{{ getDisputeTypeLabel(detail.disputeType) }}</text></view>
          <view class="partner-info-row"><text class="partner-info-label">纠纷单号</text><text class="partner-info-value">{{ detail.disputeNo || '--' }}</text></view>
          <view class="partner-info-row"><text class="partner-info-label">订单号</text><text class="partner-info-value">{{ detail.orderNo || '--' }}</text></view>
          <view class="partner-info-row"><text class="partner-info-label">单元号</text><text class="partner-info-value">{{ detail.unitNo || '--' }}</text></view>
          <view class="partner-info-row"><text class="partner-info-label">辖区编码</text><text class="partner-info-value">{{ detail.regionCode || '--' }}</text></view>
          <view class="partner-info-row"><text class="partner-info-label">创建时间</text><text class="partner-info-value">{{ formatDateTime(detail.createTime) }}</text></view>
        </view>
        <view class="partner-divider"></view>
        <text class="partner-field-label">投诉 / 申诉内容</text>
        <text class="partner-item-desc">{{ detail.content || '--' }}</text>
        <text class="partner-field-label" style="margin-top: 20rpx;">当前结果摘要</text>
        <text class="partner-item-desc">{{ detail.resultDesc || '--' }}</text>
      </view>

      <view class="partner-card">
        <view class="partner-card-header">
          <text class="partner-card-title">协调时间线</text>
        </view>
        <view v-if="records.length" class="partner-timeline">
          <view v-for="item in records" :key="item.id" class="partner-timeline-item">
            <text class="partner-timeline-title">{{ getCoordinationStatusLabel(item.status) }}</text>
            <text class="partner-timeline-text">协调意见：{{ item.coordinationRemark || '--' }}</text>
            <text v-if="item.escalateRemark" class="partner-timeline-text">升级说明：{{ item.escalateRemark }}</text>
            <text class="partner-timeline-time">{{ formatDateTime(item.initiatedTime || item.finishedTime) }}</text>
          </view>
        </view>
        <view v-else class="partner-empty">暂无协调记录</view>
      </view>

      <view class="partner-card">
        <view class="partner-card-header">
          <text class="partner-card-title">处理动作</text>
        </view>
        <view class="partner-actions">
          <view class="partner-btn primary" @click="submitCoordination">提交协调意见</view>
          <view class="partner-btn warn" @click="escalateDispute">升级平台终审</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { createPartnerCoordination, getPartnerDispute } from '@/api/partner'
import {
  ensurePartnerPageAccess,
  formatDateTime,
  getCoordinationStatusLabel,
  getDisputeStatusLabel,
  getDisputeTypeLabel,
  goBack,
  promptText
} from './shared'

export default {
  data() {
    return {
      roleContext: {},
      disputeType: '',
      disputeId: '',
      detail: {}
    }
  },
  computed: {
    records() {
      return this.detail.coordinationRecords || []
    }
  },
  onLoad(options) {
    this.disputeType = options.disputeType || ''
    this.disputeId = options.disputeId || ''
  },
  onShow() {
    this.loadDetail()
  },
  methods: {
    goBack,
    formatDateTime,
    getCoordinationStatusLabel,
    getDisputeStatusLabel,
    getDisputeTypeLabel,
    statusClass(status) {
      if (status === 'ESCALATED') {
        return 'red'
      }
      return status === 'PROCESSING' ? 'blue' : 'orange'
    },
    async loadDetail() {
      const allowed = await ensurePartnerPageAccess(this, '纠纷协调详情')
      if (!allowed || !this.disputeType || !this.disputeId) {
        return
      }
      this.detail = await getPartnerDispute(this.disputeType, this.disputeId).catch(() => ({}))
    },
    async submitCoordination() {
      const remark = await promptText({
        title: '填写协调意见',
        placeholderText: '请输入协调意见'
      })
      if (!remark) {
        uni.showToast({ title: '请填写协调意见', icon: 'none' })
        return
      }
      await createPartnerCoordination({
        disputeType: this.disputeType,
        disputeId: Number(this.disputeId),
        coordinationRemark: remark,
        escalateToPlatform: false
      })
      uni.showToast({ title: '已提交', icon: 'success' })
      this.loadDetail()
    },
    async escalateDispute() {
      const remark = await promptText({
        title: '填写升级说明',
        placeholderText: '请输入升级说明'
      })
      if (!remark) {
        uni.showToast({ title: '请填写升级说明', icon: 'none' })
        return
      }
      await createPartnerCoordination({
        disputeType: this.disputeType,
        disputeId: Number(this.disputeId),
        coordinationRemark: remark,
        escalateToPlatform: true,
        escalateRemark: remark
      })
      uni.showToast({ title: '已升级', icon: 'success' })
      this.loadDetail()
    }
  }
}
</script>

<style>
@import "./common.css";
</style>
