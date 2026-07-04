<template>
  <view class="partner-page">
    <view class="partner-header">
      <view class="partner-nav">
        <view class="partner-back" @click="goBack">‹</view>
        <text class="partner-nav-title">初审详情</text>
        <view class="partner-nav-placeholder"></view>
      </view>
      <text class="partner-header-title">{{ detail.merchantName || detail.userNickname || '入驻申请' }}</text>
      <text class="partner-header-desc">驳回必须填写原因，成功后返回列表并刷新状态计数。</text>
    </view>

    <view class="partner-content">
      <view class="partner-card">
        <view class="partner-card-header">
          <text class="partner-card-title">申请信息</text>
          <text class="partner-tag" :class="statusClass(detail.status)">{{ getEntryStatusLabel(detail.status) }}</text>
        </view>
        <view class="partner-info-grid">
          <view class="partner-info-row"><text class="partner-info-label">入驻单号</text><text class="partner-info-value">{{ detail.entryNo || '--' }}</text></view>
          <view class="partner-info-row"><text class="partner-info-label">申请人</text><text class="partner-info-value">{{ detail.userNickname || detail.userNo || '--' }}</text></view>
          <view v-if="detail.applicantRealName" class="partner-info-row"><text class="partner-info-label">实名姓名</text><text class="partner-info-value">{{ detail.applicantRealName }}</text></view>
          <view class="partner-info-row"><text class="partner-info-label">手机号</text><text class="partner-info-value">{{ detail.userMobile || '--' }}</text></view>
          <view class="partner-info-row"><text class="partner-info-label">服务商</text><text class="partner-info-value">{{ detail.merchantName || '--' }}</text></view>
          <view class="partner-info-row"><text class="partner-info-label">区域编码</text><text class="partner-info-value">{{ detail.regionCode || '--' }}</text></view>
          <view class="partner-info-row"><text class="partner-info-label">提交时间</text><text class="partner-info-value">{{ formatDateTime(detail.createTime) }}</text></view>
          <view v-if="detail.firstAuditTime" class="partner-info-row"><text class="partner-info-label">初审时间</text><text class="partner-info-value">{{ formatDateTime(detail.firstAuditTime) }}</text></view>
          <view v-if="detail.remark" class="partner-info-row full"><text class="partner-info-label">初审意见</text><text class="partner-info-value">{{ detail.remark }}</text></view>
          <view v-if="detail.rejectReason" class="partner-info-row full"><text class="partner-info-label">驳回原因</text><text class="partner-info-value">{{ detail.rejectReason }}</text></view>
        </view>
      </view>

      <view class="partner-card">
        <view class="partner-card-header">
          <text class="partner-card-title">服务商信息</text>
        </view>
        <view class="partner-info-grid">
          <view class="partner-info-row"><text class="partner-info-label">服务商名称</text><text class="partner-info-value">{{ detail.merchantName || '--' }}</text></view>
          <view v-if="detail.merchantContactName" class="partner-info-row"><text class="partner-info-label">联系人</text><text class="partner-info-value">{{ detail.merchantContactName }}</text></view>
          <view v-if="detail.merchantContactMobile" class="partner-info-row"><text class="partner-info-label">联系电话</text><text class="partner-info-value">{{ detail.merchantContactMobile }}</text></view>
          <view class="partner-info-row full"><text class="partner-info-label">服务范围说明</text><text class="partner-info-value">{{ detail.serviceScopeDesc || '--' }}</text></view>
        </view>
      </view>

      <view class="partner-card">
        <view class="partner-card-header">
          <text class="partner-card-title">申请内容</text>
        </view>
        <view class="partner-section-block">
          <text class="partner-section-label">服务类目</text>
          <view v-if="detail.categories && detail.categories.length" class="partner-chip-list">
            <text v-for="item in detail.categories" :key="item.categoryId" class="partner-chip">{{ item.categoryName || item.categoryId }}</text>
          </view>
          <text v-else class="partner-empty-text">未选择服务类目</text>
        </view>
        <view class="partner-section-block">
          <text class="partner-section-label">申请资质</text>
          <view v-if="detail.qualifications && detail.qualifications.length" class="partner-qualification-list">
            <view v-for="item in detail.qualifications" :key="item.id" class="partner-qualification-item">
              <view class="partner-qualification-head">
                <text class="partner-qualification-title">{{ item.qualificationName || item.qualificationType || '--' }}</text>
                <text class="partner-qualification-status">{{ getQualificationAuditLabel(item.auditStatus) }}</text>
              </view>
              <text v-if="item.qualificationNo" class="partner-qualification-meta">资质编号：{{ item.qualificationNo }}</text>
              <text v-if="item.validEndDate" class="partner-qualification-meta">有效期至：{{ formatQualificationDate(item.validEndDate) }}</text>
              <text v-if="item.fileUrl" class="partner-qualification-link" @click.stop="previewQualification(item)">查看附件</text>
            </view>
          </view>
          <text v-else class="partner-empty-text">未选择申请资质</text>
        </view>
      </view>

      <view v-if="detail.status === 'PENDING'" class="partner-card">
        <view class="partner-card-header">
          <text class="partner-card-title">审核动作</text>
        </view>
        <view class="partner-actions">
          <view class="partner-btn primary" @click="handleApprove">通过</view>
          <view class="partner-btn warn" @click="handleReject">驳回</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { auditPartnerEntry, getPartnerEntryAudit } from '@/api/partner'
import { confirmAction, ensurePartnerPageAccess, formatDateTime, getEntryStatusLabel, goBack, promptText } from './shared'

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
    getEntryStatusLabel,
    getQualificationAuditLabel(status) {
      const map = {
        PENDING: '待审核',
        APPROVED: '已通过',
        REJECTED: '已驳回'
      }
      return map[status] || status || '--'
    },
    formatQualificationDate(value) {
      if (!value) {
        return '--'
      }
      if (Array.isArray(value)) {
        return value.filter(Boolean).join('-')
      }
      return `${value}`
    },
    statusClass(status) {
      if (status === 'REJECTED') {
        return 'red'
      }
      return status === 'PENDING' ? 'orange' : 'green'
    },
    async loadDetail() {
      const allowed = await ensurePartnerPageAccess(this, '入驻初审详情')
      if (!allowed || !this.id) {
        return
      }
      this.detail = await getPartnerEntryAudit(this.id).catch(() => ({}))
    },
    async handleApprove() {
      const confirmed = await confirmAction({
        title: '确认通过',
        content: '通过后将进入平台后续审核流程。'
      })
      if (!confirmed) {
        return
      }
      await auditPartnerEntry({
        id: Number(this.id),
        auditStatus: 'APPROVED',
        auditRemark: '辖区初审通过'
      })
      uni.showToast({ title: '已通过', icon: 'success' })
      setTimeout(() => {
        uni.navigateBack()
      }, 300)
    },
    async handleReject() {
      const reason = await promptText({
        title: '填写驳回原因',
        placeholderText: '请输入驳回原因'
      })
      if (!reason) {
        uni.showToast({ title: '请填写驳回原因', icon: 'none' })
        return
      }
      await auditPartnerEntry({
        id: Number(this.id),
        auditStatus: 'REJECTED',
        rejectReason: reason
      })
      uni.showToast({ title: '已驳回', icon: 'success' })
      setTimeout(() => {
        uni.navigateBack()
      }, 300)
    },
    previewQualification(item) {
      if (!item || !item.fileUrl) {
        uni.showToast({
          title: '暂无附件',
          icon: 'none'
        })
        return
      }
      uni.previewImage({
        urls: [item.fileUrl],
        current: item.fileUrl
      })
    }
  }
}
</script>

<style>
@import "./common.css";

.partner-info-row.full {
  grid-column: 1 / -1;
}

.partner-section-block + .partner-section-block {
  margin-top: 24rpx;
}

.partner-section-label {
  display: block;
  margin-bottom: 16rpx;
  font-size: 26rpx;
  font-weight: 600;
  color: #2b4a6f;
}

.partner-chip-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.partner-chip {
  padding: 10rpx 20rpx;
  border-radius: 999rpx;
  background: #eef5ff;
  color: #2f6fbd;
  font-size: 24rpx;
}

.partner-empty-text {
  font-size: 24rpx;
  color: #98a6b8;
}

.partner-qualification-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.partner-qualification-item {
  padding: 20rpx;
  border-radius: 20rpx;
  background: #f7faff;
}

.partner-qualification-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24rpx;
  margin-bottom: 12rpx;
}

.partner-qualification-title {
  flex: 1;
  font-size: 26rpx;
  font-weight: 600;
  color: #23415f;
}

.partner-qualification-status {
  font-size: 24rpx;
  color: #f59e0b;
}

.partner-qualification-meta {
  display: block;
  font-size: 24rpx;
  line-height: 1.7;
  color: #6f8094;
}

.partner-qualification-link {
  display: inline-block;
  margin-top: 12rpx;
  font-size: 24rpx;
  color: #2f6fbd;
}
</style>
