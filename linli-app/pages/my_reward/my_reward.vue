<template>
    <view class="page-container">
        <view class="header">
            <view class="back-btn" @click="goBack">
                <text class="iconfont icon-youjiantou back-icon"></text>
            </view>
            <text class="title">我的悬赏</text>
            <view class="placeholder"></view>
        </view>

        <view class="tab-bar">
            <view
                v-for="(item, index) in tabs"
                :key="item.key"
                class="tab-item"
                :class="{ active: currentTab === index }"
                @click="changeTab(index)">
                <text class="tab-text">{{ item.label }}</text>
            </view>
        </view>

        <scroll-view class="content-scroll" scroll-y @scrolltolower="loadMore">
            <view v-if="list.length" class="reward-list">
                <view v-for="item in list" :key="item.id" class="reward-card">
                    <view class="reward-top">
                        <text class="reward-title">{{ getTitle(item) }}</text>
                        <view class="status-badge" :class="getStatusClass(item)">
                            <text class="status-text">{{ getStatusLabel(item) }}</text>
                        </view>
                    </view>
                    <text class="reward-desc">{{ getDescription(item) }}</text>
                    <view class="reward-meta">
                        <text class="meta-text">{{ getMetaLine(item) }}</text>
                        <text class="meta-text">{{ formatTime(item.createTime) }}</text>
                    </view>
                </view>
            </view>

            <view v-else-if="!loading" class="empty-state">
                <text class="empty-title">暂无悬赏记录</text>
                <text class="empty-desc">当前标签下还没有可展示的数据</text>
            </view>

            <view v-if="loading" class="loading-state">
                <text class="loading-text">加载中...</text>
            </view>
            <view class="bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
import { getParticipatedRewardOrderPage, getRewardOrderPage } from '@/api/reward'

export default {
    data() {
        return {
            tabs: [
                { key: 'published', label: '我发布的' },
                { key: 'participated', label: '我参与的' }
            ],
            currentTab: 0,
            list: [],
            pageNo: 1,
            pageSize: 10,
            loading: false,
            finished: false
        }
    },
    onLoad(options) {
        if (options && options.tab === 'participated') {
            this.currentTab = 1
        }
    },
    onShow() {
        this.reloadList()
    },
    onPullDownRefresh() {
        this.reloadList().finally(() => {
            uni.stopPullDownRefresh()
        })
    },
    methods: {
        async reloadList() {
            this.pageNo = 1
            this.finished = false
            this.list = []
            await this.loadList()
        },
        async loadList() {
            if (this.loading || this.finished) {
                return
            }
            this.loading = true
            try {
                const params = {
                    pageNo: this.pageNo,
                    pageSize: this.pageSize
                }
                const page = this.currentTab === 0
                    ? await getRewardOrderPage(params, { silent: true })
                    : await getParticipatedRewardOrderPage(params, { silent: true })
                const rows = (page && page.list) || []
                this.list = this.pageNo === 1 ? rows : this.list.concat(rows)
                this.finished = rows.length < this.pageSize
                this.pageNo += 1
            } catch (error) {
            } finally {
                this.loading = false
            }
        },
        changeTab(index) {
            if (this.currentTab === index) {
                return
            }
            this.currentTab = index
            this.reloadList()
        },
        getTitle(item) {
            return item.title || item.rewardTitle || '未命名悬赏'
        },
        getDescription(item) {
            return item.description || item.participateRemark || item.auditRemark || '暂无说明'
        },
        getStatusLabel(item) {
            if (this.currentTab === 0) {
                const labels = {
                    PENDING: '待审核',
                    APPROVED: '已通过',
                    REJECTED: '已驳回'
                }
                return labels[item.auditStatus] || '处理中'
            }
            const labels = {
                PENDING: '待处理',
                ACCEPTED: '已接受',
                REJECTED: '已拒绝',
                CANCELLED: '已取消'
            }
            return labels[item.status] || '处理中'
        },
        getStatusClass(item) {
            const value = this.currentTab === 0 ? item.auditStatus : item.status
            if (value === 'APPROVED' || value === 'ACCEPTED') {
                return 'success'
            }
            if (value === 'REJECTED' || value === 'CANCELLED') {
                return 'danger'
            }
            return 'pending'
        },
        getMetaLine(item) {
            if (this.currentTab === 0) {
                return item.priorityEnabled ? '优先展示资格已生效' : '等待审核结果'
            }
            return item.participantNickname
                ? `参与人：${item.participantNickname}`
                : '参与记录'
        },
        formatTime(value) {
            return value ? this.$fmt.formatDateTime(value) : '--'
        },
        loadMore() {
            this.loadList()
        },
        goBack() {
            uni.navigateBack()
        }
    }
}
</script>

<style lang="scss" scoped>
.page-container {
    min-height: 100vh;
    background: #F5F7FB;
}

.header {
    padding: 72rpx 30rpx 24rpx;
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: #FFFFFF;
}

.back-btn,
.placeholder {
    width: 56rpx;
    height: 56rpx;
}

.back-icon {
    display: block;
    font-size: 38rpx;
    color: #2E83F0;
    transform: rotate(180deg);
}

.title {
    font-size: 34rpx;
    font-weight: 700;
    color: #22324A;
}

.tab-bar {
    margin: 20rpx 24rpx 0;
    padding: 8rpx;
    background: #FFFFFF;
    border-radius: 20rpx;
    display: flex;
}

.tab-item {
    flex: 1;
    height: 72rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 16rpx;
}

.tab-item.active {
    background: #2E83F0;
}

.tab-text {
    font-size: 26rpx;
    color: #6A788C;
}

.tab-item.active .tab-text {
    color: #FFFFFF;
    font-weight: 600;
}

.content-scroll {
    height: calc(100vh - 190rpx);
}

.reward-list {
    padding: 24rpx;
}

.reward-card {
    margin-bottom: 20rpx;
    padding: 28rpx 26rpx;
    background: #FFFFFF;
    border-radius: 20rpx;
    box-shadow: 0 10rpx 24rpx rgba(34, 50, 74, 0.06);
}

.reward-top {
    display: flex;
    align-items: flex-start;
    gap: 16rpx;
}

.reward-title {
    flex: 1;
    min-width: 0;
    font-size: 30rpx;
    line-height: 42rpx;
    font-weight: 700;
    color: #22324A;
}

.status-badge {
    flex-shrink: 0;
    min-width: 112rpx;
    height: 48rpx;
    padding: 0 18rpx;
    border-radius: 24rpx;
    display: flex;
    align-items: center;
    justify-content: center;
}

.status-badge.pending {
    background: #FFF5E8;
}

.status-badge.success {
    background: #EAF8F0;
}

.status-badge.danger {
    background: #FFF0F0;
}

.status-text {
    font-size: 22rpx;
    color: #F39A36;
}

.status-badge.success .status-text {
    color: #1BAA64;
}

.status-badge.danger .status-text {
    color: #E45D5D;
}

.reward-desc {
    margin-top: 16rpx;
    font-size: 24rpx;
    line-height: 36rpx;
    color: #6A788C;
}

.reward-meta {
    margin-top: 18rpx;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 20rpx;
}

.meta-text {
    font-size: 22rpx;
    color: #9AA7B8;
}

.empty-state,
.loading-state {
    padding: 140rpx 40rpx 0;
    text-align: center;
}

.empty-title {
    display: block;
    font-size: 30rpx;
    font-weight: 600;
    color: #4A5668;
}

.empty-desc,
.loading-text {
    display: block;
    margin-top: 12rpx;
    font-size: 24rpx;
    color: #9AA7B8;
}

.bottom-space {
    height: 60rpx;
}
</style>
