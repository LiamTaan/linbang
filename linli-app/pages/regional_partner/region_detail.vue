<template>
  <view class="partner-page">
    <view class="partner-header">
      <view class="partner-nav">
        <view class="partner-back" @click="goBack">‹</view>
        <text class="partner-nav-title">辖区查看</text>
        <view class="partner-nav-placeholder"></view>
      </view>
      <text class="partner-header-title">{{ region.partnerName || '我的辖区' }}</text>
      <text class="partner-header-desc">这里只看后台分配给当前合作商的辖区范围，不提供编辑能力。</text>
    </view>

    <view class="partner-content">
      <view class="partner-card">
        <view class="partner-card-header">
          <text class="partner-card-title">辖区摘要</text>
        </view>
        <view class="partner-stat-grid">
          <view class="partner-stat-item">
            <text class="partner-stat-label">辖区数量</text>
            <text class="partner-stat-value">{{ summary.regionCount }}</text>
          </view>
          <view class="partner-stat-item">
            <text class="partner-stat-label">启用辖区</text>
            <text class="partner-stat-value">{{ summary.enabledRegionCount }}</text>
          </view>
          <view class="partner-stat-item">
            <text class="partner-stat-label">辖区订单数</text>
            <text class="partner-stat-value">{{ summary.orderCount }}</text>
          </view>
          <view class="partner-stat-item">
            <text class="partner-stat-label">辖区成交额</text>
            <text class="partner-stat-value">{{ formatMoney(summary.tradeAmount) }}</text>
          </view>
        </view>
      </view>

      <view class="partner-card">
        <view class="partner-card-header">
          <text class="partner-card-title">状态筛选</text>
        </view>
        <view class="partner-chip-row">
          <view v-for="item in statusTabs" :key="item.value" class="partner-chip" :class="{ active: filterStatus === item.value }" @click="filterStatus = item.value">
            {{ item.label }}
          </view>
        </view>
      </view>

      <view class="partner-card">
        <view class="partner-card-header">
          <text class="partner-card-title">辖区列表</text>
        </view>
        <view v-if="filteredRegions.length" class="partner-list">
          <view v-for="item in filteredRegions" :key="item.id" class="partner-list-item">
            <view class="partner-item-title-row">
              <text class="partner-item-title">{{ getRegionDisplayName(item) }}</text>
              <text class="partner-tag" :class="item.status === 'ENABLE' ? 'green' : 'red'">{{ getRegionStatusLabel(item.status) }}</text>
            </view>
            <text class="partner-item-meta">区域编码：{{ item.regionCode || '--' }}</text>
            <text class="partner-item-desc">街道/乡镇：{{ item.streetName || '--' }} · 排序：{{ item.sort || 0 }}</text>
          </view>
        </view>
        <view v-else class="partner-empty">暂无辖区配置</view>
      </view>
    </view>
  </view>
</template>

<script>
import { getPartnerRegion, getPartnerWorkbench } from '@/api/partner'
import {
  ensurePartnerPageAccess,
  formatMoney,
  getRegionDisplayName,
  getRegionStatusLabel,
  goBack
} from './shared'

export default {
  data() {
    return {
      roleContext: {},
      region: {},
      workbench: {},
      filterStatus: '',
      statusTabs: [
        { label: '全部', value: '' },
        { label: '启用', value: 'ENABLE' },
        { label: '停用', value: 'DISABLE' }
      ]
    }
  },
  computed: {
    summary() {
      return this.workbench.summary || {
        regionCount: 0,
        enabledRegionCount: 0,
        orderCount: 0,
        tradeAmount: 0
      }
    },
    filteredRegions() {
      const list = this.region.regions || []
      if (!this.filterStatus) {
        return list
      }
      return list.filter((item) => item.status === this.filterStatus)
    }
  },
  onShow() {
    this.loadData()
  },
  methods: {
    goBack,
    getRegionDisplayName,
    getRegionStatusLabel,
    formatMoney(value) {
      return formatMoney(this, value)
    },
    async loadData() {
      const allowed = await ensurePartnerPageAccess(this, '辖区查看')
      if (!allowed) {
        return
      }
      const [region, workbench] = await Promise.all([
        getPartnerRegion().catch(() => ({})),
        getPartnerWorkbench().catch(() => ({}))
      ])
      this.region = region || {}
      this.workbench = workbench || {}
    }
  }
}
</script>

<style>
@import "./common.css";
</style>
