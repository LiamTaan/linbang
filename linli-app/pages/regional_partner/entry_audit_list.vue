<template>
  <view class="partner-page">
    <view class="partner-header">
      <view class="partner-nav">
        <view class="partner-back" @click="goBack">‹</view>
        <text class="partner-nav-title">入驻初审</text>
        <view class="partner-nav-placeholder"></view>
      </view>
      <text class="partner-header-title">辖区入驻初审</text>
      <text class="partner-header-desc">合作商只处理自己辖区内的首层审核，审核动作统一在详情页完成。</text>
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
            <text class="partner-field-label">用户关键词</text>
            <input v-model="query.userKeyword" class="partner-input" placeholder="用户编号 / 昵称 / 手机号" />
          </view>
          <view>
            <text class="partner-field-label">入驻单号</text>
            <input v-model="query.entryNo" class="partner-input" placeholder="请输入入驻单号" />
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
      </view>

      <view class="partner-card">
        <view class="partner-card-header">
          <text class="partner-card-title">审核列表</text>
          <text class="partner-card-link">共 {{ page.total || 0 }} 条</text>
        </view>
        <view v-if="list.length" class="partner-list">
          <view v-for="item in list" :key="item.id" class="partner-list-item" @click="openDetail(item)">
            <view class="partner-item-title-row">
              <text class="partner-item-title">{{ item.merchantName || item.userNickname || '未命名申请' }}</text>
              <text class="partner-tag" :class="statusClass(item.status)">{{ getEntryStatusLabel(item.status) }}</text>
            </view>
            <text class="partner-item-meta">{{ item.entryNo || '--' }} · {{ item.regionCode || '--' }}</text>
            <text class="partner-item-desc">申请人：{{ item.userNickname || item.userNo || '--' }} / {{ item.userMobile || '--' }}</text>
            <text class="partner-item-time">提交时间：{{ formatDateTime(item.createTime) }}</text>
          </view>
        </view>
        <view v-else class="partner-empty">暂无待审核</view>
      </view>
    </view>
  </view>
</template>

<script>
import { getPartnerEntryAuditPage } from '@/api/partner'
import { ensurePartnerPageAccess, formatDateTime, getEntryStatusLabel, goBack, navigateTo } from './shared'

export default {
  data() {
    return {
      roleContext: {},
      tabs: [
        { label: '待审核', value: 'PENDING' },
        { label: '已通过', value: 'FIRST_APPROVED' },
        { label: '已驳回', value: 'REJECTED' }
      ],
      query: {
        pageNo: 1,
        pageSize: 20,
        userKeyword: '',
        entryNo: '',
        regionCode: '',
        status: 'PENDING'
      },
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
    getEntryStatusLabel,
    statusClass(status) {
      if (status === 'REJECTED') {
        return 'red'
      }
      return status === 'PENDING' ? 'orange' : 'green'
    },
    switchTab(status) {
      this.query.status = status
      this.loadList()
    },
    resetQuery() {
      this.query.userKeyword = ''
      this.query.entryNo = ''
      this.query.regionCode = ''
      this.query.status = 'PENDING'
      this.loadList()
    },
    async loadList() {
      const allowed = await ensurePartnerPageAccess(this, '入驻初审列表')
      if (!allowed) {
        return
      }
      this.page = await getPartnerEntryAuditPage(this.query).catch(() => ({ total: 0, list: [] }))
    },
    openDetail(item) {
      navigateTo(`/pages/regional_partner/entry_audit_detail?id=${item.id}`)
    }
  }
}
</script>

<style>
@import "./common.css";
</style>
