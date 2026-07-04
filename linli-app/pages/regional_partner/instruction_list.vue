<template>
  <view class="partner-page">
    <view class="partner-header">
      <view class="partner-nav">
        <view class="partner-back" @click="goBack">‹</view>
        <text class="partner-nav-title">会议通知</text>
        <view class="partner-nav-placeholder"></view>
      </view>
      <text class="partner-header-title">会议通知 / 上级指令</text>
      <text class="partner-header-desc">列表支持按会议通知与上级指令切换，阅读统一进入详情页，不再使用弹窗快照。</text>
    </view>

    <view class="partner-content">
      <view class="partner-card">
        <view class="partner-chip-row">
          <view v-for="item in tabs" :key="item.value" class="partner-chip" :class="{ active: query.messageCategory === item.value }" @click="switchTab(item.value)">
            {{ item.label }}
          </view>
        </view>
      </view>

      <view class="partner-card">
        <view class="partner-card-header">
          <text class="partner-card-title">消息列表</text>
          <text class="partner-card-link">共 {{ page.total || 0 }} 条</text>
        </view>
        <view v-if="list.length" class="partner-list">
          <view v-for="item in list" :key="item.id" class="partner-list-item" @click="openDetail(item)">
            <view class="partner-item-title-row">
              <text class="partner-item-title">{{ item.title || '未命名通知' }}</text>
              <text class="partner-tag" :class="item.readStatus === 'READ' ? 'green' : 'red'">{{ getReadStatusLabel(item.readStatus) }}</text>
            </view>
            <text class="partner-item-meta">{{ getInstructionCategoryLabel(item.messageCategory) }}</text>
            <text class="partner-item-desc">{{ item.contentSnapshot || '点击查看详情' }}</text>
            <text class="partner-item-time">{{ formatDateTime(item.createTime) }}</text>
          </view>
        </view>
        <view v-else class="partner-empty">暂无通知</view>
      </view>
    </view>
  </view>
</template>

<script>
import { getPartnerInstructionPage } from '@/api/partner'
import { ensurePartnerPageAccess, formatDateTime, getInstructionCategoryLabel, getReadStatusLabel, goBack, navigateTo } from './shared'

export default {
  data() {
    return {
      roleContext: {},
      query: {
        pageNo: 1,
        pageSize: 20,
        messageCategory: ''
      },
      tabs: [
        { label: '全部', value: '' },
        { label: '会议通知', value: 'MEETING_NOTICE' },
        { label: '上级指令', value: 'SUPERIOR_INSTRUCTION' }
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
    getInstructionCategoryLabel,
    getReadStatusLabel,
    switchTab(category) {
      this.query.messageCategory = category
      this.loadList()
    },
    async loadList() {
      const allowed = await ensurePartnerPageAccess(this, '合作商通知列表')
      if (!allowed) {
        return
      }
      this.page = await getPartnerInstructionPage(this.query).catch(() => ({ total: 0, list: [] }))
    },
    openDetail(item) {
      navigateTo(`/pages/regional_partner/instruction_detail?id=${item.id}`)
    }
  }
}
</script>

<style>
@import "./common.css";
</style>
