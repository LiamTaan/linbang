<template>
  <view class="partner-page">
    <view class="partner-header">
      <view class="partner-nav">
        <view class="partner-back" @click="goBack">‹</view>
        <text class="partner-nav-title">价格建议</text>
        <view class="partner-nav-placeholder"></view>
      </view>
      <text class="partner-header-title">辖区价格建议</text>
      <text class="partner-header-desc">待审核可撤回，其他状态只读；新增建议单独进入创建页。</text>
    </view>

    <view class="partner-content">
      <view class="partner-card">
        <view class="partner-chip-row">
          <view v-for="item in tabs" :key="item.value" class="partner-chip" :class="{ active: query.status === item.value }" @click="switchTab(item.value)">
            {{ item.label }}
          </view>
        </view>
        <view class="partner-divider"></view>
        <view class="partner-form-grid">
          <view>
            <text class="partner-field-label">类目 ID</text>
            <input v-model="query.categoryId" class="partner-input" placeholder="请输入类目 ID" />
          </view>
          <view>
            <text class="partner-field-label">区域编码</text>
            <input v-model="query.regionCode" class="partner-input" placeholder="请输入区域编码" />
          </view>
        </view>
        <view class="partner-actions">
          <view class="partner-btn ghost" @click="resetQuery">重置</view>
          <view class="partner-btn primary" @click="loadList">查询</view>
        </view>
        <view class="partner-actions">
          <view class="partner-btn primary" @click="navigateTo('/pages/regional_partner/price_report_create')">新建价格建议</view>
        </view>
      </view>

      <view class="partner-card">
        <view class="partner-card-header">
          <text class="partner-card-title">建议列表</text>
          <text class="partner-card-link">共 {{ filteredList.length }} 条</text>
        </view>
        <view v-if="filteredList.length" class="partner-list">
          <view v-for="item in filteredList" :key="item.id" class="partner-list-item" @click="openDetail(item)">
            <view class="partner-item-title-row">
              <text class="partner-item-title">{{ item.categoryName || `类目${item.categoryId || '--'}` }}</text>
              <text class="partner-tag" :class="statusClass(item.status)">{{ getPriceReportStatusLabel(item.status) }}</text>
            </view>
            <text class="partner-item-meta">{{ item.regionCode || '--' }} · {{ formatMoney(item.suggestedPrice) }}</text>
            <text class="partner-item-desc">服务商：{{ item.merchantName || '--' }}</text>
            <text class="partner-item-time">{{ formatDateTime(item.createTime) }}</text>
          </view>
        </view>
        <view v-else class="partner-empty">暂无价格建议</view>
      </view>
    </view>
  </view>
</template>

<script>
import { getPartnerPriceReportPage } from '@/api/partner'
import { ensurePartnerPageAccess, formatDateTime, formatMoney, getPriceReportStatusLabel, goBack, navigateTo } from './shared'

export default {
  data() {
    return {
      roleContext: {},
      query: {
        pageNo: 1,
        pageSize: 50,
        regionCode: '',
        status: 'PENDING',
        categoryId: ''
      },
      tabs: [
        { label: '待审核', value: 'PENDING' },
        { label: '已通过', value: 'APPROVED' },
        { label: '已驳回', value: 'REJECTED' },
        { label: '已撤回', value: 'WITHDRAWN' }
      ],
      page: { total: 0, list: [] }
    }
  },
  computed: {
    filteredList() {
      const list = this.page.list || []
      if (!this.query.categoryId) {
        return list
      }
      return list.filter((item) => `${item.categoryId || ''}` === `${this.query.categoryId}`)
    }
  },
  onShow() {
    this.loadList()
  },
  methods: {
    goBack,
    navigateTo,
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
    switchTab(status) {
      this.query.status = status
      this.loadList()
    },
    resetQuery() {
      this.query.categoryId = ''
      this.query.regionCode = ''
      this.query.status = 'PENDING'
      this.loadList()
    },
    async loadList() {
      const allowed = await ensurePartnerPageAccess(this, '价格建议列表')
      if (!allowed) {
        return
      }
      const params = {
        pageNo: this.query.pageNo,
        pageSize: this.query.pageSize,
        regionCode: this.query.regionCode,
        status: this.query.status
      }
      this.page = await getPartnerPriceReportPage(params).catch(() => ({ total: 0, list: [] }))
    },
    openDetail(item) {
      navigateTo(`/pages/regional_partner/price_report_detail?id=${item.id}`)
    }
  }
}
</script>

<style>
@import "./common.css";
</style>
