<template>
  <view class="partner-page">
    <view class="partner-header">
      <view class="partner-nav">
        <view class="partner-back" @click="goBack">‹</view>
        <text class="partner-nav-title">通知详情</text>
        <view class="partner-nav-placeholder"></view>
      </view>
      <text class="partner-header-title">{{ detail.title || '通知详情' }}</text>
      <text class="partner-header-desc">消息正文优先展示详情正文，没有正文时回退显示模板内容或快照。</text>
    </view>

    <view class="partner-content">
      <view class="partner-card">
        <view class="partner-card-header">
          <text class="partner-card-title">消息信息</text>
          <text class="partner-tag" :class="detail.readStatus === 'READ' ? 'green' : 'red'">{{ getReadStatusLabel(detail.readStatus) }}</text>
        </view>
        <view class="partner-info-grid">
          <view class="partner-info-row"><text class="partner-info-label">消息分类</text><text class="partner-info-value">{{ getInstructionCategoryLabel(detail.messageCategory) }}</text></view>
          <view class="partner-info-row"><text class="partner-info-label">发送时间</text><text class="partner-info-value">{{ formatDateTime(detail.sendTime || detail.createTime) }}</text></view>
          <view class="partner-info-row"><text class="partner-info-label">业务类型</text><text class="partner-info-value">{{ detail.bizType || '--' }}</text></view>
          <view class="partner-info-row"><text class="partner-info-label">业务 ID</text><text class="partner-info-value">{{ detail.bizId || '--' }}</text></view>
        </view>
      </view>

      <view class="partner-card">
        <view class="partner-card-header">
          <text class="partner-card-title">正文</text>
        </view>
        <text class="partner-item-desc">{{ contentText }}</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getPartnerInstruction } from '@/api/partner'
import { ensurePartnerPageAccess, formatDateTime, getInstructionCategoryLabel, getReadStatusLabel, goBack } from './shared'

export default {
  data() {
    return {
      roleContext: {},
      id: '',
      detail: {}
    }
  },
  computed: {
    contentText() {
      return this.detail.contentSnapshot
        || (this.detail.template && this.detail.template.contentTemplate)
        || '暂无正文内容'
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
    getInstructionCategoryLabel,
    getReadStatusLabel,
    async loadDetail() {
      const allowed = await ensurePartnerPageAccess(this, '合作商通知详情')
      if (!allowed || !this.id) {
        return
      }
      this.detail = await getPartnerInstruction(this.id).catch(() => ({}))
    }
  }
}
</script>

<style>
@import "./common.css";
</style>
