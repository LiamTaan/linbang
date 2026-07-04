<template>
  <view class="partner-page">
    <view class="partner-header">
      <view class="partner-nav">
        <view class="partner-back" @click="goBack">‹</view>
        <text class="partner-nav-title">纠纷协调</text>
        <view class="partner-nav-placeholder"></view>
      </view>
      <text class="partner-header-title">辖区投诉 / 申诉</text>
      <text class="partner-header-desc">合作商只协调本辖区纠纷，真正处理动作进入详情页执行。</text>
    </view>

    <view class="partner-content">
      <view class="partner-card">
        <view class="partner-chip-row">
          <view v-for="item in statusTabs" :key="item.value" class="partner-chip" :class="{ active: query.status === item.value }" @click="switchStatus(item.value)">
            {{ item.label }}
          </view>
        </view>
        <view class="partner-divider"></view>
        <view class="partner-form-grid">
          <view>
            <text class="partner-field-label">纠纷类型</text>
            <view class="partner-chip-row">
              <view v-for="item in typeTabs" :key="item.value" class="partner-chip" :class="{ active: query.disputeType === item.value }" @click="query.disputeType = item.value">
                {{ item.label }}
              </view>
            </view>
          </view>
          <view>
            <text class="partner-field-label">订单号 / 纠纷单号</text>
            <input v-model="query.keyword" class="partner-input" placeholder="请输入订单号或纠纷单号" />
          </view>
          <view>
            <text class="partner-field-label">辖区编码</text>
            <input v-model="query.regionCode" class="partner-input" placeholder="请输入辖区编码" />
          </view>
        </view>
        <view class="partner-actions">
          <view class="partner-btn ghost" @click="resetQuery">重置</view>
          <view class="partner-btn primary" @click="loadList">查询</view>
        </view>
      </view>

      <view class="partner-card">
        <view class="partner-card-header">
          <text class="partner-card-title">纠纷列表</text>
          <text class="partner-card-link">共 {{ page.total || 0 }} 条</text>
        </view>
        <view v-if="list.length" class="partner-list">
          <view v-for="item in list" :key="`${item.disputeType}-${item.disputeId}`" class="partner-list-item" @click="openDetail(item)">
            <view class="partner-item-title-row">
              <text class="partner-item-title">{{ getDisputeTypeLabel(item.disputeType) }} · {{ item.orderNo || item.disputeNo || '--' }}</text>
              <text class="partner-tag" :class="statusClass(item.status)">{{ getDisputeStatusLabel(item.status) }}</text>
            </view>
            <text class="partner-item-meta">纠纷单号：{{ item.disputeNo || '--' }} · 辖区：{{ item.regionCode || '--' }}</text>
            <text class="partner-item-desc">{{ item.content || item.resultDesc || '暂无描述' }}</text>
            <text class="partner-item-time">{{ formatDateTime(item.createTime) }}</text>
          </view>
        </view>
        <view v-else class="partner-empty">暂无纠纷</view>
      </view>
    </view>
  </view>
</template>

<script>
import { getPartnerDisputePage } from '@/api/partner'
import { ensurePartnerPageAccess, formatDateTime, getDisputeStatusLabel, getDisputeTypeLabel, goBack, navigateTo } from './shared'

export default {
  data() {
    return {
      roleContext: {},
      query: {
        pageNo: 1,
        pageSize: 20,
        disputeType: '',
        keyword: '',
        regionCode: '',
        status: 'PENDING'
      },
      statusTabs: [
        { label: '待处理', value: 'PENDING' },
        { label: '处理中', value: 'PROCESSING' },
        { label: '已升级', value: 'ESCALATED' }
      ],
      typeTabs: [
        { label: '全部', value: '' },
        { label: '投诉', value: 'COMPLAINT' },
        { label: '申诉', value: 'APPEAL' }
      ],
      page: { total: 0, list: [] }
    }
  },
  computed: {
    list() {
      return this.page.list || []
    }
  },
  onShow() {
    this.loadList()
  },
  methods: {
    goBack,
    formatDateTime,
    getDisputeStatusLabel,
    getDisputeTypeLabel,
    statusClass(status) {
      if (status === 'ESCALATED') {
        return 'red'
      }
      return status === 'PROCESSING' ? 'blue' : 'orange'
    },
    switchStatus(status) {
      this.query.status = status
      this.loadList()
    },
    resetQuery() {
      this.query.disputeType = ''
      this.query.keyword = ''
      this.query.regionCode = ''
      this.query.status = 'PENDING'
      this.loadList()
    },
    async loadList() {
      const allowed = await ensurePartnerPageAccess(this, '纠纷协调列表')
      if (!allowed) {
        return
      }
      this.page = await getPartnerDisputePage(this.query).catch(() => ({ total: 0, list: [] }))
    },
    openDetail(item) {
      navigateTo(`/pages/regional_partner/dispute_detail?disputeType=${item.disputeType}&disputeId=${item.disputeId}`)
    }
  }
}
</script>

<style>
@import "./common.css";
</style>
