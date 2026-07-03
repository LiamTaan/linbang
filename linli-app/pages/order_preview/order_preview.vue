<template>
  <view class="page-container">
    <view class="header">
      <view class="back-btn" @click="$navigateBack()">
        <text class="iconfont icon-youjiantou back-icon"></text>
      </view>
      <text class="title">订单预览</text>
      <view class="placeholder"></view>
    </view>
    <scroll-view class="content-scroll" scroll-y>
      <view v-if="!snapshot" class="card">
        <text class="section-title">预览信息已失效</text>
      </view>
      <block v-else>
        <view class="card">
          <text class="badge">发布前确认</text>
          <text class="order-title">{{ payload.requireDesc || '邻里需求' }}</text>
          <text class="address">{{ addressText }}</text>
          <text class="sub">类目：{{ previewResult.categoryName || currentCategoryName || '--' }}</text>
          <text class="sub">计价方式：{{ previewResult.pricingModeName || payload.pricingMode || '--' }}</text>
          <text class="amount">¥{{ $fmt.formatMoney(previewResult.orderAmount || payload.budgetAmount || 0) }}</text>
        </view>

        <view class="card" v-if="(payload.priceItems || []).length">
          <text class="section-title">价格明细</text>
          <view v-for="(item, index) in payload.priceItems" :key="index" class="line-item">
            <text>{{ item.itemName || item.itemType || `价格项${index + 1}` }}</text>
            <text>¥{{ $fmt.formatMoney(item.itemAmount) }}</text>
          </view>
        </view>

        <view class="card">
          <text class="section-title">拆单说明</text>
          <text class="sub">数量口径：{{ previewResult.quantityUnitLabel || '份' }}</text>
          <text class="sub">是否参与拆单：{{ previewResult.quantitySplitEnabled ? '是' : '否' }}</text>
          <text class="sub" v-if="splitSummary">{{ splitSummary }}</text>
          <text v-for="(item, index) in splitReasons" :key="index" class="sub">· {{ item }}</text>
        </view>
      </block>
      <view class="bottom-space"></view>
    </scroll-view>

    <view class="bottom-actions" v-if="snapshot">
      <view class="bottom-btn ghost-btn" @click="$navigateBack()">
        <text>返回修改</text>
      </view>
      <view class="bottom-btn primary-btn" @click="handlePublish">
        <text>{{ publishing ? '发布中...' : '确认发布' }}</text>
      </view>
    </view>
  </view>
</template>

<script>
import { createOrder } from '@/api/order'

const ORDER_PREVIEW_STORAGE_KEY = 'linbang_order_preview_snapshot'

export default {
  data() {
    return {
      snapshot: null,
      publishing: false
    }
  },
  computed: {
    payload() {
      return (this.snapshot && this.snapshot.payload) || {}
    },
    previewResult() {
      return (this.snapshot && this.snapshot.previewResult) || {}
    },
    currentCategoryName() {
      return (this.snapshot && this.snapshot.currentCategoryName) || ''
    },
    splitReasons() {
      return (this.previewResult.splitPreview && this.previewResult.splitPreview.splitTriggerReasons) || []
    },
    splitSummary() {
      const preview = this.previewResult.splitPreview || {}
      return preview.splitRuleSummary || preview.ruleDesc || ''
    },
    addressText() {
      return [this.payload.province, this.payload.city, this.payload.district, this.payload.street, this.payload.detailAddress]
        .filter(Boolean)
        .join(' ')
    }
  },
  onShow() {
    this.snapshot = uni.getStorageSync(ORDER_PREVIEW_STORAGE_KEY) || null
  },
  methods: {
    resolveAgreementVersion() {
      const guaranteeConfig = (this.snapshot && this.snapshot.guaranteeConfig) || {}
      const previewResult = (this.snapshot && this.snapshot.previewResult) || {}
      const baseVersion = guaranteeConfig.agreementVersion || 'v2026.06'
      if (previewResult.agreementType !== 'PROJECT_ESCROW') {
        return baseVersion
      }
      return /project/i.test(baseVersion) ? baseVersion : `${baseVersion}-project`
    },
    async handlePublish() {
      if (!this.snapshot || !this.snapshot.payload || !this.previewResult.previewToken || this.publishing) {
        return
      }
      try {
        this.publishing = true
        const orderId = await createOrder({
          ...this.snapshot.payload,
          agreementConfirmed: true,
          agreementVersion: this.resolveAgreementVersion(),
          previewToken: this.previewResult.previewToken,
          antiEscapeConfirmed: true
        })
        uni.removeStorageSync(ORDER_PREVIEW_STORAGE_KEY)
        uni.showToast({ title: '发布成功', icon: 'success' })
        setTimeout(() => {
          uni.redirectTo({ url: `/pages/split_order_details/split_order_details?orderId=${orderId}` })
        }, 300)
      } catch (error) {
      } finally {
        this.publishing = false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.page-container { min-height: 100vh; background: #f5f7fb; }
.header { background: #fff; padding: 60rpx 30rpx 24rpx; display: flex; justify-content: space-between; align-items: center; border-bottom: 1rpx solid #eef2f7; }
.back-btn,.placeholder { width: 60rpx; }
.back-icon { font-size: 40rpx; color: #333; transform: rotate(180deg); }
.title { font-size: 34rpx; font-weight: bold; color: #1f2937; }
.content-scroll { padding: 20rpx; box-sizing: border-box; }
.card { background: #fff; border-radius: 24rpx; padding: 24rpx; margin-bottom: 20rpx; box-shadow: 0 8rpx 24rpx rgba(15,23,42,.05); }
.badge,.sub,.address { display: block; color: #64748b; font-size: 24rpx; line-height: 1.7; }
.badge { color: #2563eb; }
.order-title,.section-title { display: block; color: #0f172a; font-size: 30rpx; font-weight: 600; margin: 12rpx 0; }
.amount { display: block; margin-top: 16rpx; color: #e26d2f; font-size: 42rpx; font-weight: 700; }
.line-item { display: flex; justify-content: space-between; padding: 16rpx 0; border-bottom: 1rpx solid #f1f5f9; }
.line-item:last-child { border-bottom: none; }
.bottom-actions { position: fixed; left: 0; right: 0; bottom: 0; background: #fff; padding: 20rpx 24rpx calc(20rpx + env(safe-area-inset-bottom)); display: flex; gap: 16rpx; box-shadow: 0 -8rpx 24rpx rgba(15,23,42,.05); }
.bottom-btn { flex: 1; height: 84rpx; border-radius: 18rpx; display: flex; align-items: center; justify-content: center; font-size: 28rpx; }
.ghost-btn { background: #eef4ff; color: #2e83f0; }
.primary-btn { background: #2e83f0; color: #fff; }
.bottom-space { height: 140rpx; }
</style>
