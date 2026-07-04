<template>
  <view class="partner-page">
    <view class="partner-header">
      <view class="partner-nav">
        <view class="partner-back" @click="goBack">‹</view>
        <text class="partner-nav-title">新建价格建议</text>
        <view class="partner-nav-placeholder"></view>
      </view>
      <text class="partner-header-title">提交辖区价格建议</text>
      <text class="partner-header-desc">本期先按最小闭环提交类目、辖区、建议价格和备注，提交后进入待审核状态。</text>
    </view>

    <view class="partner-content">
      <view class="partner-card">
        <view class="partner-form-grid">
          <view>
            <text class="partner-field-label">类目 ID</text>
            <input v-model="form.categoryId" class="partner-input" placeholder="请输入类目 ID" />
          </view>
          <view>
            <text class="partner-field-label">辖区编码</text>
            <input v-model="form.regionCode" class="partner-input" placeholder="请输入辖区编码" />
          </view>
          <view>
            <text class="partner-field-label">建议价格</text>
            <input v-model="form.suggestedPrice" class="partner-input" type="digit" placeholder="请输入建议价格" />
          </view>
          <view>
            <text class="partner-field-label">备注</text>
            <textarea v-model="form.remark" class="partner-textarea" placeholder="请输入申报备注"></textarea>
          </view>
        </view>
        <view class="partner-actions">
          <view class="partner-btn primary" @click="submitForm">提交建议</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { createPartnerPriceReport, getPartnerRegion } from '@/api/partner'
import { ensurePartnerPageAccess, goBack } from './shared'

export default {
  data() {
    return {
      roleContext: {},
      regions: [],
      form: {
        categoryId: '',
        regionCode: '',
        suggestedPrice: '',
        remark: ''
      }
    }
  },
  onShow() {
    this.loadRegions()
  },
  methods: {
    goBack,
    async loadRegions() {
      const allowed = await ensurePartnerPageAccess(this, '新建价格建议')
      if (!allowed) {
        return
      }
      const region = await getPartnerRegion().catch(() => ({}))
      this.regions = region.regions || []
      if (!this.form.regionCode && this.regions.length) {
        this.form.regionCode = this.regions[0].regionCode || ''
      }
    },
    async submitForm() {
      if (!this.form.categoryId || !this.form.regionCode || !this.form.suggestedPrice) {
        uni.showToast({ title: '请先填写完整信息', icon: 'none' })
        return
      }
      const id = await createPartnerPriceReport({
        categoryId: Number(this.form.categoryId),
        regionCode: this.form.regionCode,
        suggestedPrice: Number(this.form.suggestedPrice),
        remark: this.form.remark
      })
      uni.showToast({ title: '提交成功', icon: 'success' })
      setTimeout(() => {
        uni.redirectTo({
          url: `/pages/regional_partner/price_report_detail?id=${id}`
        })
      }, 300)
    }
  }
}
</script>

<style>
@import "./common.css";
</style>
