<template>
    <view class="page-container">
        <view class="header-bg">
            <view class="header">
                <view class="back-btn" @click="goBack">
                    <text class="iconfont icon-youjiantou back-icon"></text>
                </view>
                <text class="title">推广员中心</text>
                <view class="level-btn">
                    <text class="level-text">{{ center.levelName || '初级推广员' }}</text>
                </view>
            </view>

            <view class="hero-card">
                <view class="hero-top">
                    <view class="hero-copy">
                        <text class="hero-label">累计佣金（元）</text>
                        <text class="hero-amount">¥{{ $fmt.formatMoney(center.totalCommissionAmount) }}</text>
                    </view>
                    <view class="withdraw-btn" @click="handleWithdraw">
                        <text class="withdraw-text">提现</text>
                    </view>
                </view>

                <view class="hero-meta">
                    <view class="meta-item">
                        <text class="meta-label">可提现</text>
                        <text class="meta-value">¥{{ $fmt.formatMoney(center.availableCommissionAmount) }}</text>
                    </view>
                    <view class="meta-divider"></view>
                    <view class="meta-item">
                        <text class="meta-label">待转化</text>
                        <text class="meta-value">{{ center.convertCount || 0 }} 人</text>
                    </view>
                </view>
            </view>
        </view>

        <scroll-view class="content-scroll" scroll-y refresher-enabled :refresher-triggered="refreshing"
            @refresherrefresh="handleRefresh">
            <view class="surface-card level-card">
                <view class="card-header">
                    <text class="card-title">等级进度</text>
                    <text class="card-subtitle">{{ levelRuleText }}</text>
                </view>

                <view class="level-panel">
                    <view class="level-badge">
                        <text class="badge-label">当前等级</text>
                        <text class="badge-value">{{ center.levelName || '未开通' }}</text>
                    </view>
                    <view class="level-copy">
                        <text class="level-main">{{ progressText }}</text>
                        <text class="level-desc">{{ center.upgradeConditionDesc || '达到直推人数要求后自动升级推广员等级。' }}</text>
                    </view>
                </view>

                <view class="progress-track">
                    <view class="progress-fill" :style="{ width: progressPercent + '%' }"></view>
                </view>
                <view class="progress-footer">
                    <text class="progress-hint">直推 {{ directUserCount }} 人</text>
                    <text class="progress-hint">{{ nextLevelHint }}</text>
                </view>
            </view>

            <view class="surface-card stats-card">
                <view class="stat-item">
                    <text class="stat-value">{{ directUserCount }}</text>
                    <text class="stat-label">直推人数</text>
                </view>
                <view class="stat-divider"></view>
                <view class="stat-item">
                    <text class="stat-value">{{ teamStats.secondLevelUserCount || 0 }}</text>
                    <text class="stat-label">二级人数</text>
                </view>
                <view class="stat-divider"></view>
                <view class="stat-item">
                    <text class="stat-value">{{ center.convertCount || 0 }}</text>
                    <text class="stat-label">转化人数</text>
                </view>
            </view>

            <view class="surface-card invite-card">
                <view class="card-header between">
                    <text class="card-title">我的推广码</text>
                    <text class="copy-action" @click="copyInviteCode">复制邀请码</text>
                </view>

                <view class="invite-code-row">
                    <view class="invite-code-box">
                        <text class="invite-code-label">邀请码</text>
                        <text class="invite-code">{{ center.inviteCode || '--' }}</text>
                    </view>
                    <view class="share-button" @click="copyShareLink">
                        <text class="share-button-text">复制链接</text>
                    </view>
                </view>

                <view class="link-box" @click="copyShareLink">
                    <text class="link-label">推广链接</text>
                    <text class="link-text">{{ shareLink }}</text>
                </view>

                <view class="share-panel">
                    <text class="share-title">分享至</text>
                    <view class="share-row">
                        <view class="share-item" @click="copyShareLink">
                            <image class="share-icon" src="/static/img/promotion_center/wechat@3x.png" />
                            <text class="share-name">微信</text>
                        </view>
                        <view class="share-item" @click="copyShareLink">
                            <image class="share-icon" src="/static/img/promotion_center/moments@3x.png" />
                            <text class="share-name">朋友圈</text>
                        </view>
                        <view class="share-item" @click="copyShareLink">
                            <image class="share-icon" src="/static/img/promotion_center/qq@3x.png" />
                            <text class="share-name">QQ</text>
                        </view>
                    </view>
                </view>
            </view>

            <view class="surface-card record-card">
                <view class="card-header between">
                    <text class="card-title">最近佣金记录</text>
                    <text class="card-subtitle">最多展示 5 条</text>
                </view>

                <view v-if="recentCommissionOrders.length" class="record-list">
                    <view v-for="item in recentCommissionOrders" :key="item.id || item.commissionNo" class="record-item">
                        <view class="record-copy">
                            <text class="record-title">{{ item.commissionNo || item.orderNo || '推广佣金' }}</text>
                            <text class="record-desc">{{ formatCommissionDesc(item) }}</text>
                        </view>
                        <text class="record-amount">+¥{{ $fmt.formatMoney(item.commissionAmount) }}</text>
                    </view>
                </view>
                <view v-else class="empty-state">暂无佣金记录</view>
            </view>

            <view class="bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
import { getPromoteCenter, getTeamStats } from '@/api/promote'

export default {
    data() {
        return {
            center: {},
            teamStats: {},
            loading: false,
            refreshing: false
        }
    },
    computed: {
        directUserCount() {
            return this.teamStats.firstLevelUserCount || this.center.bindUserCount || 0
        },
        recentCommissionOrders() {
            return (this.center.recentCommissionOrders || []).slice(0, 5)
        },
        shareLink() {
            return this.center.inviteUrl || this.center.inviteShortLink || '暂无推广链接'
        },
        levelRuleText() {
            return '0-9 初级，10-49 中级，50+ 高级'
        },
        progressPercent() {
            const count = Number(this.directUserCount) || 0
            if (count >= 50) {
                return 100
            }
            if (count >= 10) {
                return Math.min(100, Math.round((count / 50) * 100))
            }
            return Math.min(100, Math.round((count / 10) * 100))
        },
        nextLevelHint() {
            const count = Number(this.directUserCount) || 0
            if (count >= 50) {
                return '已达到高级推广员'
            }
            if (count >= 10) {
                return `距高级还差 ${50 - count} 人`
            }
            return `距中级还差 ${10 - count} 人`
        },
        progressText() {
            if (this.center.nextLevelNeedMetric) {
                return this.center.nextLevelNeedMetric
            }
            const count = Number(this.directUserCount) || 0
            if (count >= 50) {
                return '已达到当前最高等级'
            }
            if (count >= 10) {
                return `已推广 ${count}/50 人，继续冲刺高级推广员`
            }
            return `已推广 ${count}/10 人，达到 10 人可升级中级推广员`
        }
    },
    onShow() {
        this.loadPageData()
    },
    methods: {
        async loadPageData() {
            if (this.loading) {
                return
            }
            this.loading = true
            try {
                const [center, teamStats] = await Promise.all([
                    getPromoteCenter(),
                    getTeamStats().catch(() => ({}))
                ])
                this.center = center || {}
                this.teamStats = teamStats || {}
            } catch (error) {
            } finally {
                this.refreshing = false
                this.loading = false
            }
        },
        handleRefresh() {
            this.refreshing = true
            this.loadPageData()
        },
        copyText(text, title) {
            if (!text) {
                return
            }
            uni.setClipboardData({
                data: text,
                success: () => {
                    uni.showToast({
                        title,
                        icon: 'success'
                    })
                }
            })
        },
        copyInviteCode() {
            this.copyText(this.center.inviteCode, '邀请码已复制')
        },
        copyShareLink() {
            const text = this.center.inviteUrl || this.center.inviteShortLink || this.center.inviteCode
            this.copyText(text, '推广信息已复制')
        },
        formatCommissionDesc(item) {
            const parts = [
                item.statusName || item.status,
                item.settleStatusName || item.settleStatus,
                item.createTime ? this.$fmt.formatDateTime(item.createTime) : ''
            ].filter(Boolean)
            return parts.length ? parts.join(' · ') : '佣金记录'
        },
        handleWithdraw() {
            uni.navigateTo({
                url: '/pages/withdraw_deposit/withdraw_deposit'
            })
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
    background: #f4f7fb;

    .header-bg {
        position: relative;
        background: linear-gradient(180deg, #3485f7 0%, #4a90f0 100%);
        padding-bottom: 110rpx;
        border-radius: 0 0 34rpx 34rpx;

        .header {
            padding: 60rpx 30rpx 26rpx;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .back-btn,
        .level-btn {
            width: 150rpx;
            min-height: 56rpx;
            display: flex;
            align-items: center;
        }

        .level-btn {
            justify-content: flex-end;
        }

        .back-icon {
            font-size: 40rpx;
            color: #fff;
            transform: rotate(180deg);
        }

        .title,
        .level-text {
            color: #fff;
        }

        .title {
            font-size: 34rpx;
            font-weight: 600;
        }

        .level-text {
            max-width: 150rpx;
            padding: 10rpx 14rpx;
            border-radius: 999rpx;
            background: rgba(255, 255, 255, 0.18);
            font-size: 22rpx;
            line-height: 1.25;
        }
    }

    .hero-card {
        margin: 8rpx 24rpx 0;
        padding: 30rpx;
        border-radius: 22rpx;
        background: rgba(255, 255, 255, 0.96);
        box-shadow: 0 14rpx 28rpx rgba(25, 79, 171, 0.18);
    }

    .hero-top {
        display: flex;
        align-items: flex-start;
        justify-content: space-between;
        gap: 24rpx;
    }

    .hero-copy {
        flex: 1;
        min-width: 0;
    }

    .hero-label,
    .meta-label,
    .card-subtitle,
    .level-desc,
    .progress-hint,
    .invite-code-label,
    .link-label,
    .record-desc,
    .share-name,
    .empty-state {
        font-size: 22rpx;
        color: #8290a3;
        line-height: 1.45;
    }

    .hero-amount {
        display: block;
        margin-top: 10rpx;
        font-size: 56rpx;
        line-height: 1.08;
        font-weight: 700;
        color: #226fde;
    }

    .withdraw-btn {
        min-width: 132rpx;
        padding: 18rpx 22rpx;
        border-radius: 14rpx;
        background: #2f7df2;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 8rpx 16rpx rgba(47, 125, 242, 0.22);
    }

    .withdraw-text,
    .share-button-text {
        color: #fff;
        font-size: 26rpx;
        font-weight: 600;
    }

    .hero-meta {
        margin-top: 28rpx;
        padding-top: 24rpx;
        border-top: 1rpx solid #edf2f8;
        display: flex;
        align-items: center;
    }

    .meta-item {
        flex: 1;
        min-width: 0;
    }

    .meta-value {
        display: block;
        margin-top: 8rpx;
        color: #303b4f;
        font-size: 30rpx;
        font-weight: 700;
    }

    .meta-divider,
    .stat-divider {
        width: 1rpx;
        background: #e8edf5;
        flex-shrink: 0;
    }

    .meta-divider {
        height: 54rpx;
        margin: 0 28rpx;
    }

    .content-scroll {
        margin-top: -74rpx;
        padding: 0 24rpx 36rpx;
        box-sizing: border-box;
    }

    .surface-card {
        background: #fff;
        border-radius: 18rpx;
        padding: 24rpx;
        margin-bottom: 22rpx;
        box-shadow: 0 10rpx 24rpx rgba(33, 60, 116, 0.06);
    }

    .card-header {
        display: flex;
        align-items: center;
        gap: 12rpx;
        margin-bottom: 22rpx;

        &.between {
            justify-content: space-between;
        }
    }

    .card-title {
        font-size: 30rpx;
        font-weight: 700;
        color: #303b4f;
    }

    .level-panel {
        display: flex;
        gap: 20rpx;
        align-items: stretch;
    }

    .level-badge {
        width: 152rpx;
        border-radius: 16rpx;
        background: linear-gradient(180deg, #eaf3ff 0%, #dcecff 100%);
        padding: 18rpx 14rpx;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        flex-shrink: 0;
    }

    .badge-label {
        font-size: 22rpx;
        color: #6c8fc9;
    }

    .badge-value {
        margin-top: 8rpx;
        color: #2f7df2;
        font-size: 32rpx;
        font-weight: 700;
        text-align: center;
        line-height: 1.25;
    }

    .level-copy {
        flex: 1;
        min-width: 0;
        display: flex;
        flex-direction: column;
        justify-content: center;
        gap: 10rpx;
    }

    .level-main {
        font-size: 30rpx;
        line-height: 1.45;
        color: #1f2d3d;
        font-weight: 600;
    }

    .progress-track {
        height: 14rpx;
        border-radius: 999rpx;
        background: #edf3fb;
        overflow: hidden;
        margin-top: 24rpx;
    }

    .progress-fill {
        height: 100%;
        border-radius: 999rpx;
        background: linear-gradient(90deg, #4a90f0 0%, #56c2ff 100%);
    }

    .progress-footer {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-top: 12rpx;
    }

    .stats-card {
        display: flex;
        align-items: center;
    }

    .stat-item {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 8rpx;
    }

    .stat-value {
        color: #1f2d3d;
        font-size: 40rpx;
        line-height: 1;
        font-weight: 700;
    }

    .stat-label {
        color: #78879a;
        font-size: 24rpx;
    }

    .stat-divider {
        height: 58rpx;
        margin: 0 12rpx;
    }

    .copy-action {
        font-size: 24rpx;
        color: #2f7df2;
    }

    .invite-code-row {
        display: flex;
        align-items: center;
        gap: 18rpx;
    }

    .invite-code-box {
        flex: 1;
        min-width: 0;
        border-radius: 16rpx;
        background: #f7faff;
        padding: 20rpx;
    }

    .invite-code {
        display: block;
        margin-top: 8rpx;
        font-size: 34rpx;
        color: #1f2d3d;
        font-weight: 700;
        word-break: break-all;
    }

    .share-button {
        min-width: 136rpx;
        height: 88rpx;
        border-radius: 16rpx;
        background: #2f7df2;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
    }

    .link-box {
        margin-top: 18rpx;
        border-radius: 16rpx;
        background: #f8fbff;
        padding: 20rpx;
    }

    .link-text {
        display: block;
        margin-top: 10rpx;
        color: #2f7df2;
        font-size: 26rpx;
        line-height: 1.5;
        word-break: break-all;
    }

    .share-panel {
        margin-top: 18rpx;
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 18rpx;
    }

    .share-title {
        font-size: 24rpx;
        color: #59677a;
        flex-shrink: 0;
    }

    .share-row {
        display: flex;
        align-items: center;
        gap: 18rpx;
    }

    .share-item {
        width: 108rpx;
        min-height: 94rpx;
        border-radius: 16rpx;
        background: #f7f9fd;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        gap: 8rpx;
    }

    .share-icon {
        width: 38rpx;
        height: 38rpx;
    }

    .record-list {
        display: flex;
        flex-direction: column;
        gap: 14rpx;
    }

    .record-item {
        border-radius: 16rpx;
        background: #f8fbff;
        padding: 20rpx;
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 20rpx;
    }

    .record-copy {
        flex: 1;
        min-width: 0;
    }

    .record-title {
        display: block;
        font-size: 28rpx;
        color: #303b4f;
        font-weight: 600;
        margin-bottom: 8rpx;
    }

    .record-amount {
        color: #21a66a;
        font-size: 30rpx;
        font-weight: 700;
        flex-shrink: 0;
    }

    .empty-state {
        padding: 26rpx 0 8rpx;
        text-align: center;
    }

    .bottom-space {
        height: 60rpx;
    }
}
</style>
