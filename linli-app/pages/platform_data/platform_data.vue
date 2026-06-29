<template>
    <view class="page-container">
        <view class="header">
            <view class="back-btn" @click="goBack">
                <text class="iconfont icon-youjiantou back-icon"></text>
            </view>
            <text class="title">平台数据看板</text>
            <view class="export-btn" @click="reloadData">
                <text class="export-text">刷新</text>
            </view>
        </view>

        <scroll-view class="tip-bar" scroll-x :show-scrollbar="false">
            <view class="tip-content">
                <text class="tip-text">待审核 {{ overview.pendingAuditCount || 0 }} 件</text>
                <text class="tip-divider">|</text>
                <text class="tip-text">异常订单 {{ overview.abnormalOrderCount || 0 }} 件</text>
                <text class="tip-divider">|</text>
                <text class="tip-text">退款待处理 {{ overview.refundPendingCount || 0 }} 件</text>
            </view>
        </scroll-view>

        <view class="date-bar">
            <view class="date-select">
                <image class="calendar-icon" src="/static/img/platform_data/calendar@3x.png" />
                <text class="date-text">最近趋势</text>
            </view>
        </view>

        <scroll-view class="content-scroll" scroll-y>
            <view class="stats-grid">
                <view class="stat-card">
                    <text class="stat-label">今日订单量</text>
                    <text class="stat-value">{{ overview.todayOrderCount || 0 }}</text>
                </view>
                <view class="stat-card">
                    <text class="stat-label">今日交易额</text>
                    <text class="stat-value">¥{{ $fmt.formatMoney(overview.todayTradeAmount) }}</text>
                </view>
                <view class="stat-card">
                    <text class="stat-label">新增用户</text>
                    <text class="stat-value">{{ overview.todayNewUserCount || 0 }}</text>
                </view>
                <view class="stat-card">
                    <text class="stat-label">完成率</text>
                    <text class="stat-value">{{ $fmt.formatMoney(overview.completionRate) }}%</text>
                </view>
            </view>

            <view class="chart-card">
                <view class="chart-header">
                    <text class="chart-title">订单趋势</text>
                </view>
                <view class="chart-content">
                    <view class="chart-bars">
                        <view class="bar-item" v-for="item in orderBars" :key="item.label">
                            <view class="bar" :style="{ height: item.height + '%' }"></view>
                            <text class="bar-label">{{ item.label }}</text>
                        </view>
                    </view>
                </view>
            </view>

            <view class="category-card">
                <view class="category-grid">
                    <view class="category-item">
                        <image class="category-icon" src="/static/img/platform_data/finance@3x.png" />
                        <view>
                            <text class="category-text">财务趋势</text>
                            <text class="category-sub">最近 {{ financeStat.length }} 个时间点</text>
                        </view>
                    </view>
                    <view class="category-item">
                        <image class="category-icon" src="/static/img/platform_data/order_data@3x.png" />
                        <view>
                            <text class="category-text">订单趋势</text>
                            <text class="category-sub">最近 {{ orderStat.length }} 个时间点</text>
                        </view>
                    </view>
                    <view class="category-item">
                        <image class="category-icon" src="/static/img/platform_data/user_data@3x.png" />
                        <view>
                            <text class="category-text">用户趋势</text>
                            <text class="category-sub">最近 {{ userStat.length }} 个时间点</text>
                        </view>
                    </view>
                </view>
            </view>

            <view class="bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
import {
    getDashboardFinanceStat,
    getDashboardOrderStat,
    getDashboardOverview,
    getDashboardUserStat
} from '@/api/dashboard'

function buildBars(list, key) {
    const max = Math.max(...list.map((item) => Number(item[key] || 0)), 1)
    return list.map((item) => ({
        label: (item.statDate || '').slice(5) || '--',
        height: Math.max(12, Math.round((Number(item[key] || 0) / max) * 100))
    }))
}

export default {
    data() {
        return {
            overview: {},
            orderStat: [],
            financeStat: [],
            userStat: []
        }
    },
    computed: {
        orderBars() {
            return buildBars(this.orderStat, 'orderCount')
        }
    },
    onShow() {
        this.reloadData()
    },
    methods: {
        async reloadData() {
            try {
                const [overview, orderStat, financeStat, userStat] = await Promise.all([
                    getDashboardOverview(),
                    getDashboardOrderStat(),
                    getDashboardFinanceStat(),
                    getDashboardUserStat()
                ])
                this.overview = overview || {}
                this.orderStat = orderStat || []
                this.financeStat = financeStat || []
                this.userStat = userStat || []
            } catch (error) {
            }
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
}

.header,
.date-bar {
    background: #fff;
}

.header {
    padding: 60rpx 30rpx 30rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.back-icon {
    font-size: 40rpx;
    color: #333;
    transform: rotate(180deg);
}

.tip-bar {
    background: #FFF8E1;
    white-space: nowrap;
}

.tip-content {
    display: inline-flex;
    gap: 16rpx;
    padding: 16rpx 30rpx;
}

.tip-text,
.tip-divider {
    font-size: 24rpx;
    color: #FA9D3B;
}

.date-bar {
    padding: 24rpx 30rpx;
}

.date-select {
    display: flex;
    align-items: center;
    gap: 12rpx;
}

.calendar-icon {
    width: 32rpx;
    height: 32rpx;
}

.date-text {
    font-size: 28rpx;
    color: #4A90F0;
    font-weight: bold;
}

.content-scroll {
    padding: 20rpx;
    box-sizing: border-box;
}

.stats-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20rpx;
    margin-bottom: 20rpx;
}

.stat-card,
.chart-card,
.category-card {
    background: #fff;
    border-radius: 16rpx;
    padding: 24rpx;
}

.stat-label,
.category-sub {
    font-size: 24rpx;
    color: #999;
}

.stat-value {
    font-size: 44rpx;
    font-weight: bold;
    color: #4A90F0;
    margin-top: 12rpx;
}

.chart-card,
.category-card {
    margin-bottom: 20rpx;
}

.chart-content {
    height: 300rpx;
    background: #F5F9FF;
    border-radius: 12rpx;
    padding: 20rpx;
    display: flex;
    align-items: flex-end;
}

.chart-bars {
    flex: 1;
    display: flex;
    justify-content: space-around;
    align-items: flex-end;
    height: 100%;
}

.bar-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    flex: 1;
}

.bar {
    width: 40rpx;
    background: #4A90F0;
    border-radius: 8rpx 8rpx 0 0;
    margin-bottom: 12rpx;
}

.bar-label {
    font-size: 20rpx;
    color: #999;
}

.category-grid {
    display: grid;
    grid-template-columns: repeat(1, 1fr);
    gap: 16rpx;
}

.category-item {
    display: flex;
    align-items: center;
    gap: 16rpx;
    padding: 20rpx;
    background: #FAFAFA;
    border-radius: 12rpx;
}

.category-icon {
    width: 44rpx;
    height: 44rpx;
}

.category-text {
    display: block;
    font-size: 28rpx;
    color: #333;
}

.bottom-space {
    height: 60rpx;
}
</style>
