<template>
    <view class="page-container">
        <view class="header-bg">
            <view class="header">
                <view class="back-btn" @click="goBack">
                    <text class="iconfont icon-youjiantou back-icon"></text>
                </view>
                <text class="title">区域合作商工作台</text>
                <view class="placeholder"></view>
            </view>
            <text class="district-text">辖区：{{ regionText }}</text>
        </view>

        <scroll-view class="content-scroll" scroll-y>
            <view class="stats-row">
                <view class="stat-card blue">
                    <text class="stat-label">待初审入驻</text>
                    <text class="stat-value">{{ workbench.pendingEntryAuditCount || 0 }}</text>
                </view>
                <view class="stat-card orange">
                    <text class="stat-label">待处理投诉</text>
                    <text class="stat-value">{{ workbench.pendingComplaintCount || 0 }}</text>
                </view>
            </view>

            <view class="function-card">
                <view class="card-header">
                    <text class="header-title">辖区概况</text>
                </view>
                <view class="function-grid">
                    <view class="function-item">
                        <text class="item-text">辖区数量</text>
                        <text class="item-value">{{ summary.regionCount || 0 }}</text>
                    </view>
                    <view class="function-item">
                        <text class="item-text">启用辖区</text>
                        <text class="item-value">{{ summary.enabledRegionCount || 0 }}</text>
                    </view>
                    <view class="function-item">
                        <text class="item-text">订单数</text>
                        <text class="item-value">{{ summary.orderCount || 0 }}</text>
                    </view>
                    <view class="function-item">
                        <text class="item-text">成交额</text>
                        <text class="item-value">¥{{ $fmt.formatMoney(summary.tradeAmount) }}</text>
                    </view>
                </view>
            </view>

            <view class="audit-card">
                <view class="card-header">
                    <text class="header-title">最近价格申报</text>
                </view>
                <view v-for="item in workbench.recentPriceReports || []" :key="item.id" class="audit-item">
                    <view class="audit-info">
                        <text class="audit-name">类目 {{ item.categoryId }} · {{ item.regionCode }}</text>
                        <text class="audit-time">{{ $fmt.formatDateTime(item.createTime) }}</text>
                    </view>
                    <view class="audit-btn">
                        <text class="audit-btn-text">{{ item.status }}</text>
                    </view>
                </view>
                <view v-if="!(workbench.recentPriceReports || []).length" class="audit-item">
                    <text class="audit-time">暂无价格申报记录</text>
                </view>
            </view>

            <view class="dispute-card">
                <view class="card-header">
                    <text class="header-title">辖区清单</text>
                </view>
                <text v-for="item in regions" :key="item.id" class="dispute-count">
                    {{ item.province }} {{ item.city }} {{ item.district }} {{ item.streetName || '' }} · {{ item.status }}
                </text>
            </view>

            <view class="bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
import { getPartnerRegion, getPartnerWorkbench } from '@/api/partner'

export default {
    data() {
        return {
            workbench: {},
            region: {}
        }
    },
    computed: {
        summary() {
            return this.workbench.summary || {}
        },
        regions() {
            return this.region.regions || []
        },
        regionText() {
            if (!this.regions.length) {
                return '暂无辖区配置'
            }
            return this.regions
                .slice(0, 2)
                .map((item) => `${item.city}${item.district}`)
                .join('、')
        }
    },
    onShow() {
        this.loadPageData()
    },
    methods: {
        async loadPageData() {
            try {
                const [workbench, region] = await Promise.all([
                    getPartnerWorkbench(),
                    getPartnerRegion()
                ])
                this.workbench = workbench || {}
                this.region = region || {}
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

    .header-bg {
        background: #4A90F0;
        padding-bottom: 60rpx;
        border-radius: 0 0 40rpx 40rpx;
    }

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
    .district-text {
        color: #fff;
    }

    .district-text {
        display: block;
        text-align: center;
        font-size: 26rpx;
    }

    .content-scroll {
        margin-top: -40rpx;
        padding: 0 20rpx;
        box-sizing: border-box;
    }

    .stats-row {
        display: flex;
        gap: 20rpx;
        margin-bottom: 20rpx;
    }

    .stat-card {
        flex: 1;
        border-radius: 16rpx;
        padding: 24rpx;
        display: flex;
        flex-direction: column;
        align-items: center;

        &.blue {
            background: #E3EFFF;
        }

        &.orange {
            background: #FFF2E3;
        }
    }

    .stat-value,
    .item-value {
        font-size: 40rpx;
        font-weight: bold;
    }

    .function-card,
    .audit-card,
    .dispute-card {
        background: #fff;
        border-radius: 16rpx;
        padding: 24rpx;
        margin-bottom: 20rpx;
    }

    .card-header {
        margin-bottom: 20rpx;
    }

    .function-grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 16rpx;
    }

    .function-item {
        background: #FAFAFA;
        border-radius: 12rpx;
        padding: 20rpx;
    }

    .item-text,
    .audit-time,
    .dispute-count {
        font-size: 24rpx;
        color: #666;
    }

    .audit-item {
        background: #FAFAFA;
        border-radius: 12rpx;
        padding: 20rpx;
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16rpx;
    }

    .audit-name {
        font-size: 28rpx;
        color: #333;
        font-weight: bold;
        display: block;
        margin-bottom: 8rpx;
    }

    .audit-btn-text {
        color: #4A90F0;
    }

    .dispute-count {
        display: block;
        line-height: 1.8;
    }

    .bottom-space {
        height: 60rpx;
    }
}
</style>
