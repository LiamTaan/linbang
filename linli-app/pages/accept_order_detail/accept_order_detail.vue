<template>
  <view class="page-container">
    <view class="hero">
      <view class="header">
        <view class="back-btn" @click="$navigateBack()">
          <text class="iconfont icon-youjiantou back-icon"></text>
        </view>
        <text class="title">订单详情</text>
        <view class="placeholder"></view>
      </view>

      <view v-if="!errorText" class="hero-center">
        <text class="hero-tip">抢单倒计时</text>
        <text class="hero-countdown">{{ countdownText }}</text>
        <text class="hero-sub">{{ dispatchStatusText }}</text>
      </view>
    </view>

    <scroll-view class="content-scroll" scroll-y refresher-enabled :refresher-triggered="refreshing" @refresherrefresh="loadDetail">
      <view v-if="errorText" class="card error-card">
        <text class="section-title">当前需求暂时无法抢单</text>
        <text class="section-desc">{{ errorText }}</text>
      </view>

      <block v-else>
        <view class="card main-card">
          <view class="category-chip">
            <text>{{ detail.categoryName || '邻里服务' }}</text>
          </view>
          <text class="order-title">{{ detail.requireDesc || '邻里需求' }}</text>

          <view class="info-line">
            <text class="info-label">地址</text>
            <text class="info-value">{{ addressText }}</text>
          </view>
          <view class="info-line">
            <text class="info-label">工期</text>
            <text class="info-value">{{ detail.serviceDurationDesc || '待确认' }}</text>
          </view>
          <view class="info-line">
            <text class="info-label">计价方式</text>
            <text class="info-value">{{ pricingModeText }}</text>
          </view>
          <view class="info-line" v-if="detail.distanceKm !== null && detail.distanceKm !== undefined">
            <text class="info-label">距离</text>
            <text class="info-value">距您约 {{ $fmt.formatMoney(detail.distanceKm) }} km</text>
          </view>
        </view>

        <view class="card" v-if="detail.requireDesc || attachmentUrls.length">
          <text class="section-title">详情描述</text>
          <text class="section-desc">{{ detail.requireDesc || '暂无补充说明' }}</text>
          <view v-if="attachmentUrls.length" class="image-list">
            <image
              v-for="(url, index) in attachmentUrls"
              :key="`${url}-${index}`"
              class="detail-image"
              :src="url"
              mode="aspectFill"
              @click="previewImages(url)" />
          </view>
        </view>

        <view class="card">
          <text class="section-title">价格明细</text>
          <view v-if="priceItems.length">
            <view v-for="(item, index) in priceItems" :key="`${item.itemType}-${index}`" class="price-line">
              <text class="price-name">{{ getPriceItemTypeLabel(item.itemType) }}<text v-if="item.itemName"> · {{ item.itemName }}</text></text>
              <text class="price-value">¥ {{ $fmt.formatMoney(item.itemAmount) }}</text>
            </view>
          </view>
          <view v-else class="price-line">
            <text class="price-name">订单金额</text>
            <text class="price-value">¥ {{ $fmt.formatMoney(detail.orderAmount) }}</text>
          </view>
          <view class="price-total">
            <text class="price-total-label">合计</text>
            <text class="price-total-value">¥ {{ $fmt.formatMoney(detail.orderAmount) }}</text>
          </view>
        </view>

        <view v-if="splitNotice" class="split-banner">
          <text>{{ splitNotice }}</text>
        </view>

        <view class="agree-line">
          <text class="agree-dot"></text>
          <text class="agree-text">我已同意并阅读《交易履约协议》</text>
        </view>
      </block>

      <view class="bottom-space"></view>
    </scroll-view>

    <view v-if="!errorText" class="bottom-actions">
      <view class="bottom-btn" :class="{ disabled: accepting || !detail.canAccept }" @click="handleAccept">
        <text class="btn-title">{{ accepting ? '抢单中...' : (detail.canAccept ? '立即抢单' : '当前不可抢') }}</text>
        <text class="btn-sub">抢单后请尽快联系客户并上门时间</text>
      </view>
    </view>
  </view>
</template>

<script>
import { acceptOrder, getAcceptOrderDetail, getGuaranteeConfig } from '@/api/order'
import {
  getDispatchStatusLabel,
  getPriceItemTypeLabel,
  getPricingModeLabel
} from '@/utils/linbang'

export default {
  data() {
    return {
      orderId: null,
      unitId: null,
      detail: {},
      guaranteeConfig: {},
      refreshing: false,
      accepting: false,
      errorText: ''
    }
  },
  computed: {
    addressText() {
      return [this.detail.province, this.detail.city, this.detail.district, this.detail.street, this.detail.detailAddress]
        .filter(Boolean)
        .join(' ') || '地址待补充'
    },
    dispatchStatusText() {
      return getDispatchStatusLabel(this.detail.dispatchStatus)
    },
    countdownText() {
      return this.formatCountdown(this.detail.countdownSeconds)
    },
    pricingModeText() {
      return getPricingModeLabel(this.detail.pricingMode)
    },
    priceItems() {
      return Array.isArray(this.detail.priceItems) ? this.detail.priceItems : []
    },
    attachmentUrls() {
      const list = Array.isArray(this.detail.attachments) ? this.detail.attachments : []
      return list.map((item) => item.fileUrl).filter(Boolean)
    },
    splitNotice() {
      if (this.detail.splitStatus !== 'SPLIT' || !this.detail.unitCount || this.detail.unitCount < 2) {
        return ''
      }
      return `此订单已自动拆分为 ${this.detail.unitCount} 个单元，抢单成功后按单元顺序履约。`
    }
  },
  onLoad(options) {
    this.orderId = options && options.orderId ? Number(options.orderId) : null
    this.unitId = options && options.unitId ? Number(options.unitId) : null
  },
  onShow() {
    this.loadDetail()
  },
  methods: {
    getPriceItemTypeLabel,
    async loadDetail() {
      if (!this.orderId) {
        return
      }
      try {
        this.refreshing = true
        const [detail, guaranteeConfig] = await Promise.all([
          getAcceptOrderDetail(this.orderId, this.unitId, { silent: true }),
          getGuaranteeConfig({ silent: true }).catch(() => ({}))
        ])
        this.detail = {
          ...(detail || {}),
          antiEscapeNotice: (detail && detail.antiEscapeNotice) || (guaranteeConfig && guaranteeConfig.antiEscapeNotice) || ''
        }
        this.guaranteeConfig = guaranteeConfig || {}
        this.errorText = ''
      } catch (error) {
        this.errorText = (error && error.message) || '当前需求已结束或不在您的抢单范围内'
      } finally {
        this.refreshing = false
      }
    },
    formatCountdown(totalSeconds) {
      if (!totalSeconds || totalSeconds <= 0) {
        return '00:00'
      }
      const minutes = Math.floor(totalSeconds / 60)
      const seconds = totalSeconds % 60
      return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
    },
    previewImages(currentUrl) {
      if (!this.attachmentUrls.length) {
        return
      }
      uni.previewImage({
        urls: this.attachmentUrls,
        current: currentUrl || this.attachmentUrls[0]
      })
    },
    handleAccept() {
      if (!this.detail.canAccept || this.accepting) {
        return
      }
      uni.showModal({
        title: '确认抢单',
        content: this.detail.antiEscapeNotice || '确认接单后，请按约提供服务。',
        success: async ({ confirm }) => {
          if (!confirm) {
            return
          }
          try {
            this.accepting = true
            await acceptOrder({
              orderId: this.detail.orderId,
              unitId: this.detail.unitId,
              antiEscapeConfirmed: true
            })
            uni.showToast({ title: '抢单成功', icon: 'success' })
            setTimeout(() => {
              uni.redirectTo({
                url: `/pages/split_order_details/split_order_details?orderId=${this.detail.orderId}&unitId=${this.detail.unitId}`
              })
            }, 300)
          } catch (error) {
          } finally {
            this.accepting = false
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.page-container {
  min-height: 100vh;
  background: #f4f7fb;
}

.hero {
  background: linear-gradient(180deg, #3384f6 0%, #4e97ff 100%);
  border-bottom-left-radius: 32rpx;
  border-bottom-right-radius: 32rpx;
  padding-bottom: 32rpx;
}

.header {
  padding: 60rpx 30rpx 24rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.back-btn,
.placeholder {
  width: 60rpx;
}

.back-icon {
  font-size: 40rpx;
  color: #fff;
  transform: rotate(180deg);
}

.title {
  font-size: 34rpx;
  font-weight: 600;
  color: #fff;
}

.hero-center {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10rpx;
}

.hero-tip,
.hero-sub {
  color: rgba(255, 255, 255, 0.9);
  font-size: 24rpx;
}

.hero-countdown {
  font-size: 72rpx;
  line-height: 1;
  font-weight: 700;
  color: #fff;
}

.content-scroll {
  margin-top: -22rpx;
  padding: 0 24rpx;
  box-sizing: border-box;
}

.card {
  background: #fff;
  border-radius: 24rpx;
  padding: 28rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 12rpx 36rpx rgba(32, 84, 170, 0.08);
}

.main-card {
  margin-top: 0;
}

.category-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 108rpx;
  height: 42rpx;
  padding: 0 16rpx;
  border-radius: 18rpx;
  background: #edf3ff;
  color: #5684d8;
  font-size: 22rpx;
}

.order-title,
.section-title {
  display: block;
  color: #1f2937;
  font-weight: 600;
}

.order-title {
  margin: 18rpx 0 22rpx;
  font-size: 38rpx;
  line-height: 1.45;
}

.section-title {
  font-size: 30rpx;
  margin-bottom: 16rpx;
}

.section-desc {
  display: block;
  color: #7b8794;
  font-size: 24rpx;
  line-height: 1.8;
}

.info-line,
.price-line,
.price-total {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20rpx;
}

.info-line + .info-line,
.price-line + .price-line {
  margin-top: 16rpx;
}

.info-label,
.price-name {
  color: #9aa5b1;
  font-size: 24rpx;
}

.info-value,
.price-value {
  flex: 1;
  text-align: right;
  color: #334e68;
  font-size: 24rpx;
  line-height: 1.7;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-top: 18rpx;
}

.detail-image {
  width: 156rpx;
  height: 156rpx;
  border-radius: 18rpx;
  background: #eef2f7;
}

.price-total {
  margin-top: 24rpx;
  padding-top: 24rpx;
  border-top: 1rpx solid #eef2f7;
  align-items: center;
}

.price-total-label {
  color: #1f2937;
  font-size: 32rpx;
  font-weight: 600;
}

.price-total-value {
  color: #ef4444;
  font-size: 42rpx;
  font-weight: 700;
}

.split-banner {
  margin-bottom: 18rpx;
  border-radius: 16rpx;
  border: 1rpx solid #ffd8a8;
  background: #fff5e6;
  padding: 20rpx 24rpx;
  color: #db8b1b;
  font-size: 24rpx;
  line-height: 1.7;
}

.agree-line {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 0 8rpx;
}

.agree-dot {
  width: 20rpx;
  height: 20rpx;
  border-radius: 50%;
  background: #3b82f6;
  flex-shrink: 0;
}

.agree-text {
  color: #7b8794;
  font-size: 22rpx;
}

.bottom-actions {
  position: fixed;
  left: 24rpx;
  right: 24rpx;
  bottom: calc(24rpx + env(safe-area-inset-bottom));
}

.bottom-btn {
  height: 104rpx;
  border-radius: 20rpx;
  background: linear-gradient(180deg, #3384f6 0%, #2e73eb 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 18rpx 36rpx rgba(46, 115, 235, 0.25);
}

.bottom-btn.disabled {
  opacity: 0.45;
}

.btn-title {
  font-size: 38rpx;
  font-weight: 700;
  line-height: 1.2;
}

.btn-sub {
  margin-top: 6rpx;
  font-size: 20rpx;
  color: rgba(255, 255, 255, 0.82);
}

.bottom-space {
  height: 176rpx;
}
</style>
