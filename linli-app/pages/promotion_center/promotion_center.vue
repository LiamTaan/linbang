<template>
    <view class="page-container">
        <view class="header-bg">
            <view class="header">
                <view class="back-btn" @click="goBack">
                    <text class="iconfont icon-youjiantou back-icon"></text>
                </view>
                <text class="title">推广员中心</text>
                <view class="level-btn">
                    <text class="level-text">{{ center.levelName || '--' }}</text>
                </view>
            </view>
        </view>

        <scroll-view class="content-scroll" scroll-y>
            <view class="level-card">
                <view class="level-tag">
                    <text class="tag-label">当前等级</text>
                    <text class="tag-value">{{ center.levelCode || '--' }}</text>
                </view>
                <view class="level-info">
                    <view class="level-row">
                        <text class="level-name">{{ center.levelName || '未开通推广员' }}</text>
                        <text class="upgrade-link">{{ center.upgradeConditionDesc || '暂无升级条件' }}</text>
                    </view>
                    <view class="progress-row">
                        <text class="progress-text">{{ center.nextLevelNeedMetric || '已达到当前等级要求' }}</text>
                    </view>
                </view>
            </view>

            <view class="commission-card">
                <view class="commission-main">
                    <text class="commission-label">累计佣金</text>
                    <text class="commission-value">￥{{ $fmt.formatMoney(center.totalCommissionAmount) }}</text>
                </view>
                <view class="commission-divider"></view>
                <view class="commission-right">
                    <text class="withdraw-label">可提现</text>
                    <text class="withdraw-value">￥{{ $fmt.formatMoney(center.availableCommissionAmount) }}</text>
                </view>
                <view class="withdraw-btn" @click="handleWithdraw">
                    <text class="withdraw-text">提现</text>
                </view>
            </view>

            <view class="stats-card">
                <view class="stat-item">
                    <text class="stat-label">一级人数</text>
                    <text class="stat-value">{{ teamStats.firstLevelUserCount || center.bindUserCount || 0 }}</text>
                </view>
                <view class="stat-divider"></view>
                <view class="stat-item">
                    <text class="stat-label">二级人数</text>
                    <text class="stat-value">{{ teamStats.secondLevelUserCount || 0 }}</text>
                </view>
                <view class="stat-divider"></view>
                <view class="stat-item">
                    <text class="stat-label">转化人数</text>
                    <text class="stat-value">{{ center.convertCount || 0 }}</text>
                </view>
            </view>

            <view class="detail-card">
                <view class="detail-header">
                    <text class="detail-title">最近佣金记录</text>
                </view>
                <view v-for="item in center.recentCommissionOrders || []" :key="item.id" class="detail-item">
                    <text class="detail-name">{{ item.commissionNo || item.status }}</text>
                    <text class="detail-amount">+￥{{ $fmt.formatMoney(item.commissionAmount) }}</text>
                </view>
                <view v-if="!(center.recentCommissionOrders || []).length" class="detail-item">
                    <text class="detail-name">暂无佣金记录</text>
                </view>
            </view>

            <view class="qrcode-card">
                <view class="qrcode-header">
                    <text class="qrcode-title">我的推广码</text>
                    <view class="qrcode-link">
                        <text class="link-label">邀请码：</text>
                        <text class="link-text">{{ center.inviteCode || '--' }}</text>
                        <text class="copy-text" @click="copyInvite">复制</text>
                    </view>
                </view>
                <view class="qrcode-content">
                    <text class="share-label">推广链接</text>
                    <text class="share-link">{{ center.inviteUrl || center.inviteShortLink || '暂无链接' }}</text>
                    <view class="share-row">
                        <image class="share-icon" src="/static/img/promotion_center/wechat@3x.png" @click="copyInvite" />
                        <image class="share-icon" src="/static/img/promotion_center/moments@3x.png" @click="copyInvite" />
                        <image class="share-icon" src="/static/img/promotion_center/qq@3x.png" @click="copyInvite" />
                    </view>
                </view>
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
            teamStats: {}
        }
    },
    onShow() {
        this.loadPageData()
    },
    methods: {
        async loadPageData() {
            try {
                const [center, teamStats] = await Promise.all([
                    getPromoteCenter(),
                    getTeamStats().catch(() => ({}))
                ])
                this.center = center || {}
                this.teamStats = teamStats || {}
            } catch (error) {
            }
        },
        copyInvite() {
            const text = this.center.inviteUrl || this.center.inviteShortLink || this.center.inviteCode
            if (!text) {
                return
            }
            uni.setClipboardData({
                data: text
            })
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
    background: #F5F5F5;

    .header-bg {
        background: #4A90F0;
        padding-bottom: 80rpx;

        .header {
            padding: 60rpx 30rpx 30rpx;
            display: flex;
            justify-content: space-between;
            align-items: center;
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
            font-weight: bold;
        }
    }

    .content-scroll {
        margin-top: -60rpx;
        padding: 0 20rpx;
        box-sizing: border-box;
    }

    .level-card,
    .commission-card,
    .stats-card,
    .detail-card,
    .qrcode-card {
        background: #fff;
        border-radius: 16rpx;
        padding: 24rpx;
        margin-bottom: 20rpx;
    }

    .level-card {
        display: flex;
        gap: 24rpx;
    }

    .level-tag {
        background: #E8F4FD;
        border-radius: 12rpx;
        padding: 16rpx 20rpx;
        display: flex;
        flex-direction: column;
        align-items: center;
        min-width: 120rpx;
    }

    .tag-label,
    .tag-value,
    .upgrade-link {
        color: #4A90F0;
    }

    .tag-value,
    .commission-value,
    .stat-value {
        font-weight: bold;
    }

    .level-row,
    .detail-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12rpx;
    }

    .commission-card,
    .stats-card {
        display: flex;
        align-items: center;
    }

    .commission-divider,
    .stat-divider {
        width: 1rpx;
        height: 60rpx;
        background: #F0F0F0;
        margin: 0 24rpx;
    }

    .withdraw-btn {
        background: #4A90F0;
        border-radius: 8rpx;
        padding: 16rpx 32rpx;

        .withdraw-text {
            color: #fff;
        }
    }

    .stat-item {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    .detail-item {
        background: #F8FBFF;
        border-radius: 12rpx;
        padding: 20rpx 24rpx;
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12rpx;
    }

    .detail-amount {
        color: #4A90F0;
        font-weight: bold;
    }

    .qrcode-link,
    .share-row {
        display: flex;
        align-items: center;
        gap: 8rpx;
        flex-wrap: wrap;
    }

    .share-link {
        display: block;
        margin: 20rpx 0;
        color: #4A90F0;
        word-break: break-all;
    }

    .share-icon {
        width: 34rpx;
        height: 34rpx;
    }

    .copy-text {
        color: #4A90F0;
    }

    .bottom-space {
        height: 60rpx;
    }
}
</style>
