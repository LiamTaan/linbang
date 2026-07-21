<template>
  <view class="partner-page">
    <view class="partner-header">
      <view class="partner-nav">
        <view class="partner-back" @click="goBack">‹</view>
        <text class="partner-nav-title">辖区推广数据</text>
        <view class="partner-nav-placeholder"></view>
      </view>
      <text class="partner-header-title">推广运营概览</text>
      <text class="partner-header-desc">数据按辖区用户的有效默认地址归属统计，与推广员上下级无关。</text>
    </view>

    <view class="partner-content">
      <view class="partner-card">
        <view class="partner-card-header"><text class="partner-card-title">用户与关系</text></view>
        <view class="partner-stat-grid">
          <view class="partner-stat-item"><text class="partner-stat-label">辖区用户</text><text class="partner-stat-value">{{ stat.newUserCount || 0 }}</text></view>
          <view class="partner-stat-item"><text class="partner-stat-label">今日新增</text><text class="partner-stat-value">{{ stat.todayNewUserCount || 0 }}</text></view>
          <view class="partner-stat-item"><text class="partner-stat-label">涉及推广员</text><text class="partner-stat-value">{{ stat.boundPromoterCount || 0 }}</text></view>
          <view class="partner-stat-item"><text class="partner-stat-label">绑定关系</text><text class="partner-stat-value">{{ stat.relationCount || 0 }}</text></view>
        </view>
      </view>

      <view class="partner-card">
        <view class="partner-card-header"><text class="partner-card-title">转化与佣金</text></view>
        <view class="partner-stat-grid">
          <view class="partner-stat-item"><text class="partner-stat-label">已转化用户</text><text class="partner-stat-value">{{ stat.convertedRelationCount || 0 }}</text></view>
          <view class="partner-stat-item"><text class="partner-stat-label">成交订单</text><text class="partner-stat-value">{{ stat.convertOrderCount || 0 }}</text></view>
          <view class="partner-stat-item"><text class="partner-stat-label">佣金单</text><text class="partner-stat-value">{{ stat.commissionOrderCount || 0 }}</text></view>
          <view class="partner-stat-item"><text class="partner-stat-label">有效佣金</text><text class="partner-stat-value money-value">{{ formatMoney(stat.commissionAmount) }}</text></view>
        </view>
        <view class="partner-divider"></view>
        <view class="partner-info-row"><text class="partner-info-label">推广成交额</text><text class="partner-info-value">{{ formatMoney(stat.tradeAmount) }}</text></view>
      </view>
    </view>
  </view>
</template>

<script>
import { getPartnerPromoteStat } from '@/api/partner'
import { ensurePartnerPageAccess, formatMoney, goBack } from './shared'

export default {
  data() {
    return { roleContext: {}, stat: {} }
  },
  onShow() {
    this.loadData()
  },
  methods: {
    goBack,
    formatMoney(value) {
      return formatMoney(this, value)
    },
    async loadData() {
      const allowed = await ensurePartnerPageAccess(this, '辖区推广数据')
      if (!allowed) return
      this.stat = await getPartnerPromoteStat().catch(() => ({}))
    }
  }
}
</script>

<style>
@import "./common.css";
.money-value { font-size: 28rpx; }
</style>
