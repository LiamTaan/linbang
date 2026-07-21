<template>
  <view class="workbench-page">
    <view class="header-wrap">
      <view class="status-bar">
        <text class="status-time">9:41</text>
        <view class="status-icons">
          <view class="signal-bars">
            <text class="signal-bar short"></text>
            <text class="signal-bar medium"></text>
            <text class="signal-bar tall"></text>
          </view>
          <view class="battery-icon">
            <view class="battery-level"></view>
          </view>
        </view>
      </view>

      <view class="nav-bar">
        <view class="back-btn" @click="goBack">
          <text class="iconfont icon-youjiantou back-icon"></text>
        </view>
        <text class="page-title">区域合作商工作台</text>
        <view class="nav-placeholder"></view>
      </view>

      <text class="region-text">辖区：{{ regionSummary }}</text>
    </view>

    <view class="stats-floating">
      <view class="stat-card stat-blue">
        <text class="stat-label">今日新增用户</text>
        <text class="stat-value stat-blue-text">{{ promoteStat.todayNewUserCount || 0 }}</text>
      </view>
      <view class="stat-card stat-orange">
        <text class="stat-label">待审核入驻</text>
        <text class="stat-value stat-orange-text">{{ summary.pendingEntryAuditCount || 0 }}</text>
      </view>
    </view>

    <scroll-view scroll-y class="page-scroll">
      <view class="content-wrap">
        <view class="panel-card">
          <text class="panel-title">功能入口</text>
          <view class="entry-grid">
            <view
              v-for="item in entries"
              :key="item.key"
              class="entry-item"
              :class="item.className"
              @click="openEntry(item)">
              <view class="entry-icon">
                <image class="entry-icon-image" :src="item.iconSrc" mode="aspectFit" />
              </view>
              <text class="entry-label">{{ item.label }}</text>
            </view>
          </view>
        </view>

        <view class="panel-card">
          <view class="panel-header">
            <text class="panel-title">待审核入驻申请</text>
            <view class="panel-link" @click="navigateTo('/pages/regional_partner/entry_audit_list')">
              <text>查看全部</text>
              <text class="iconfont icon-youjiantou panel-link-icon"></text>
            </view>
          </view>
          <view v-if="entryAudits.length" class="audit-list">
            <view
              v-for="item in entryAudits"
              :key="item.id"
              class="audit-item"
              @click="navigateTo(`/pages/regional_partner/entry_audit_detail?id=${item.id}`)">
              <view class="audit-main">
                <text class="audit-name">{{ item.merchantName || item.userNickname || '未命名申请' }}</text>
                <text class="audit-time">提交时间：{{ formatDateTime(item.createTime) }}</text>
              </view>
              <view class="audit-action">
                <text class="audit-action-text">审核</text>
              </view>
            </view>
          </view>
          <view v-else class="empty-text">暂无待审核入驻</view>
        </view>

        <view class="panel-card compact-card" @click="navigateTo('/pages/regional_partner/dispute_list')">
          <view class="panel-header compact-header">
            <view>
              <text class="panel-title">辖区纠纷</text>
              <text class="compact-desc">当前待处理纠纷：{{ summary.pendingComplaintCount || 0 }}件</text>
            </view>
            <view class="panel-link">
              <text>去处理</text>
              <text class="iconfont icon-youjiantou panel-link-icon"></text>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import {
  getPartnerDisputePage,
  getPartnerEntryAuditPage,
  getPartnerPromoteStat,
  getPartnerRegion,
  getPartnerWorkbench
} from '@/api/partner'
import {
  ensurePartnerPageAccess,
  formatDateTime,
  getRegionDisplayName,
  goBack,
  navigateTo
} from './shared'

function buildEntryIcon(kind, color) {
  const iconMap = {
    region: `<path d="M32 13c-6.6 0-12 5.4-12 12 0 9.9 12 22 12 22s12-12.1 12-22c0-6.6-5.4-12-12-12zm0 16.5a4.5 4.5 0 1 1 0-9 4.5 4.5 0 0 1 0 9z" fill="${color}"/><path d="M15 46h34" stroke="${color}" stroke-width="3.2" stroke-linecap="round"/>`,
    entry: `<path d="M15 22h34l-3 9H18l-3-9z" fill="${color}" opacity="0.18"/><path d="M17 18h30l2 8H15l2-8zm4 8v15m22-15v15M20 41h24" stroke="${color}" stroke-width="3.2" stroke-linecap="round" stroke-linejoin="round"/><path d="M24 31h16v10H24z" fill="none" stroke="${color}" stroke-width="3.2" stroke-linejoin="round"/>`,
    dispute: `<path d="M19 16h26v32H19z" fill="${color}" opacity="0.14"/><path d="M23 16h18l4 4v28H19V16h4zm6 18 5 5 11-12" fill="none" stroke="${color}" stroke-width="3.4" stroke-linecap="round" stroke-linejoin="round"/>`,
    price: `<path d="M16 29l13-13h17v17L33 46 16 29z" fill="${color}" opacity="0.18"/><path d="M16 29l13-13h17v17L33 46 16 29zm20-7h.01" fill="none" stroke="${color}" stroke-width="3.2" stroke-linecap="round" stroke-linejoin="round"/>`,
    promote: `<path d="M17 21l15-8 15 8-15 8-15-8z" fill="${color}" opacity="0.18"/><path d="M17 29l15 8 15-8M17 37l15 8 15-8M17 21l15-8 15 8-15 8-15-8z" fill="none" stroke="${color}" stroke-width="3.2" stroke-linecap="round" stroke-linejoin="round"/>`,
    instruction: `<path d="M24 23a8 8 0 1 1 0-.1zm16 3a8 8 0 1 1 0-.1z" fill="${color}" opacity="0.18"/><path d="M12 43c1.7-5.8 7.1-10 13.5-10S37.3 37.2 39 43M27 43c1.3-4.5 5.5-7.8 10.5-7.8 4.2 0 7.9 2.3 9.9 5.8" fill="none" stroke="${color}" stroke-width="3.2" stroke-linecap="round" stroke-linejoin="round"/><circle cx="24" cy="23" r="6"/><circle cx="40" cy="26" r="6" fill="none" stroke="${color}" stroke-width="3.2"/>`
  }
  const body = iconMap[kind] || ''
  return `data:image/svg+xml;utf8,${encodeURIComponent(`<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 64 64">${body}</svg>`)}`
}

export default {
  data() {
    return {
      roleContext: {},
      workbench: {},
      region: {},
      promoteStat: {},
      entryPage: { list: [] },
      disputePage: { list: [] }
    }
  },
  computed: {
    summary() {
      return this.workbench.summary || {}
    },
    regions() {
      return this.region.regions || []
    },
    regionSummary() {
      if (!this.regions.length) {
        return '暂无辖区配置'
      }
      return this.regions
        .slice(0, 2)
        .map((item) => getRegionDisplayName(item))
        .join('、')
    },
    entryAudits() {
      return (this.entryPage.list || []).slice(0, 2)
    },
    entries() {
      return [
        {
          key: 'region',
          label: '辖区查看',
          iconSrc: buildEntryIcon('region', '#7b61ff'),
          className: 'entry-purple',
          url: '/pages/regional_partner/region_detail'
        },
        {
          key: 'entry',
          label: '入驻初审',
          iconSrc: buildEntryIcon('entry', '#2d9bf0'),
          className: 'entry-blue',
          url: '/pages/regional_partner/entry_audit_list'
        },
        {
          key: 'dispute',
          label: '纠纷协调',
          iconSrc: buildEntryIcon('dispute', '#ff9f1a'),
          className: 'entry-orange',
          url: '/pages/regional_partner/dispute_list'
        },
        {
          key: 'price',
          label: '价格建议',
          iconSrc: buildEntryIcon('price', '#25b34b'),
          className: 'entry-green',
          url: '/pages/regional_partner/price_report_list'
        },
        {
          key: 'promote',
          label: '推广数据',
          iconSrc: buildEntryIcon('promote', '#c94be9'),
          className: 'entry-pink',
          url: '/pages/regional_partner/promote_stat'
        },
        {
          key: 'instruction',
          label: '会议通知',
          iconSrc: buildEntryIcon('instruction', '#f05b74'),
          className: 'entry-red',
          url: '/pages/regional_partner/instruction_list'
        }
      ]
    }
  },
  onShow() {
    this.loadPageData()
  },
  methods: {
    formatDateTime,
    goBack,
    navigateTo,
    async loadPageData() {
      const allowed = await ensurePartnerPageAccess(this, '区域合作商工作台')
      if (!allowed) {
        return
      }
      const [workbench, region, promoteStat, entryPage, disputePage] = await Promise.all([
        getPartnerWorkbench().catch(() => ({})),
        getPartnerRegion().catch(() => ({})),
        getPartnerPromoteStat().catch(() => ({})),
        getPartnerEntryAuditPage({ pageNo: 1, pageSize: 2, status: 'PENDING' }).catch(() => ({ list: [] })),
        getPartnerDisputePage({ pageNo: 1, pageSize: 1, status: 'PENDING' }).catch(() => ({ list: [] }))
      ])
      this.workbench = workbench || {}
      this.region = region || {}
      this.promoteStat = promoteStat || {}
      this.entryPage = entryPage || { list: [] }
      this.disputePage = disputePage || { list: [] }
    },
    openEntry(item) {
      navigateTo(item.url)
    }
  }
}
</script>

<style scoped>
.workbench-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f5f5f5 0, #efefef 100%);
}

.header-wrap {
  height: 254rpx;
  background: linear-gradient(180deg, #3d87e7 0%, #2e78df 100%);
  border-bottom-left-radius: 56rpx;
  border-bottom-right-radius: 56rpx;
  padding: 0 24rpx;
  box-sizing: border-box;
}

.status-bar {
  height: 52rpx;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  padding-top: 10rpx;
}

.status-time {
  color: #ffffff;
  font-size: 26rpx;
  font-weight: 600;
}

.status-icons {
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.signal-bars {
  display: flex;
  align-items: flex-end;
  gap: 4rpx;
  height: 20rpx;
}

.signal-bar {
  width: 4rpx;
  border-radius: 4rpx;
  background: #ffffff;
  display: block;
}

.signal-bar.short {
  height: 8rpx;
}

.signal-bar.medium {
  height: 13rpx;
}

.signal-bar.tall {
  height: 18rpx;
}

.battery-icon {
  width: 34rpx;
  height: 18rpx;
  border: 2rpx solid #ffffff;
  border-radius: 4rpx;
  position: relative;
  box-sizing: border-box;
}

.battery-icon::after {
  content: '';
  position: absolute;
  right: -5rpx;
  top: 4rpx;
  width: 3rpx;
  height: 8rpx;
  border-radius: 0 2rpx 2rpx 0;
  background: #ffffff;
}

.battery-level {
  width: 20rpx;
  height: 10rpx;
  background: #ffffff;
  border-radius: 2rpx;
  margin: 2rpx 0 0 2rpx;
}

.nav-bar {
  margin-top: 16rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.back-btn,
.nav-placeholder {
  width: 52rpx;
  height: 52rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-icon {
  display: block;
  transform: rotate(180deg);
  color: #ffffff;
  font-size: 28rpx;
}

.page-title {
  color: #ffffff;
  font-size: 32rpx;
  font-weight: 500;
}

.region-text {
  display: block;
  margin-top: 20rpx;
  text-align: center;
  color: rgba(255, 255, 255, 0.9);
  font-size: 22rpx;
}

.stats-floating {
  margin: -46rpx 54rpx 0;
  display: flex;
  justify-content: space-between;
  gap: 18rpx;
  position: relative;
  z-index: 3;
}

.stat-card {
  flex: 1;
  height: 108rpx;
  border-radius: 12rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 20rpx rgba(46, 120, 223, 0.08);
}

.stat-blue {
  background: #edf3ff;
}

.stat-orange {
  background: #fff4e6;
}

.stat-label {
  color: #515151;
  font-size: 20rpx;
  line-height: 1;
}

.stat-value {
  margin-top: 12rpx;
  font-size: 52rpx;
  font-weight: 700;
  line-height: 1;
}

.stat-blue-text {
  color: #2f7ce8;
}

.stat-orange-text {
  color: #ef9a2c;
}

.page-scroll {
  height: calc(100vh - 208rpx);
}

.content-wrap {
  padding: 22rpx 24rpx 40rpx;
}

.panel-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 8rpx 20rpx rgba(27, 78, 146, 0.04);
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18rpx;
}

.panel-title {
  color: #333333;
  font-size: 28rpx;
  font-weight: 500;
}

.panel-link {
  color: #5c99ea;
  font-size: 20rpx;
  display: flex;
  align-items: center;
  gap: 4rpx;
}

.panel-link-icon {
  font-size: 20rpx;
}

.entry-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 18rpx 18rpx;
}

.entry-item {
  height: 112rpx;
  border-radius: 10rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.entry-purple {
  background: #f0eaff;
}

.entry-blue {
  background: #e8f3ff;
}

.entry-orange {
  background: #fff1e1;
}

.entry-green {
  background: #ebfaeb;
}

.entry-pink {
  background: #faecfb;
}

.entry-red {
  background: #ffecef;
}

.entry-icon {
  width: 44rpx;
  height: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.entry-icon-image {
  width: 44rpx;
  height: 44rpx;
  display: block;
}

.entry-label {
  margin-top: 10rpx;
  font-size: 22rpx;
  line-height: 1;
  font-weight: 500;
}

.entry-purple .entry-label {
  color: #6b4ae0;
}

.entry-blue .entry-label {
  color: #2a93e8;
}

.entry-orange .entry-label {
  color: #f1a03c;
}

.entry-green .entry-label {
  color: #31b64c;
}

.entry-pink .entry-label {
  color: #c34adc;
}

.entry-red .entry-label {
  color: #f06b7a;
}

.audit-list {
  display: flex;
  flex-direction: column;
}

.audit-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 0;
}

.audit-item + .audit-item {
  border-top: 1rpx solid #f2f2f2;
}

.audit-main {
  flex: 1;
  min-width: 0;
}

.audit-name {
  color: #333333;
  font-size: 28rpx;
  display: block;
}

.audit-time {
  color: #9b9b9b;
  font-size: 20rpx;
  display: block;
  margin-top: 10rpx;
}

.audit-action {
  width: 96rpx;
  height: 52rpx;
  border-radius: 8rpx;
  background: #3784e6;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 20rpx;
}

.audit-action-text {
  color: #ffffff;
  font-size: 24rpx;
}

.compact-card {
  padding-top: 22rpx;
  padding-bottom: 22rpx;
}

.compact-header {
  margin-bottom: 0;
}

.compact-desc {
  display: block;
  margin-top: 10rpx;
  color: #666666;
  font-size: 22rpx;
}

.empty-text {
  padding: 20rpx 0 8rpx;
  text-align: center;
  color: #999999;
  font-size: 22rpx;
}
</style>
