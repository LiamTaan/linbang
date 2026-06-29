<template>
    <view class="page-container">
        <view class="header-bg">
            <view class="header">
                <view class="back-btn" @click="goBack">
                    <text class="iconfont icon-youjiantou back-icon"></text>
                </view>
                <text class="title">我的信用</text>
                <view class="placeholder"></view>
            </view>

            <view class="credit-card">
                <view class="score-center">
                    <text class="credit-score">{{ credit.creditScore || 0 }}</text>
                    <text class="level-text">信用等级：{{ credit.creditLevel || '--' }}</text>
                    <text class="next-text">距下一等级还需 {{ credit.nextLevelNeedScore || 0 }} 分</text>
                </view>
            </view>
        </view>

        <scroll-view class="content-scroll" scroll-y>
            <view class="change-card">
                <view class="card-header">
                    <text class="header-title">最近信用记录</text>
                </view>
                <view class="change-list">
                    <view v-for="item in recentRecords" :key="item.id" class="change-item">
                        <view class="change-info">
                            <text class="change-title">{{ item.ruleName || item.title }}</text>
                            <text class="change-desc">{{ $fmt.formatDateTime(item.createTime) }}</text>
                        </view>
                        <text class="change-score" :class="{ negative: (item.scoreChange || item.point || 0) < 0 }">
                            {{ (item.scoreChange || item.point || 0) > 0 ? '+' : '' }}{{ item.scoreChange || item.point || 0 }}
                        </text>
                    </view>
                    <view v-if="!recentRecords.length" class="empty-text">暂无信用记录</view>
                </view>
            </view>

            <view class="pool-card">
                <view class="card-header">
                    <text class="header-title">信用权益</text>
                </view>
                <view class="pool-content">
                    <text v-for="(item, index) in benefits" :key="`${item.levelCode}-${index}`" class="pool-desc">
                        {{ item.levelName }} · {{ item.benefitTitle }}：{{ item.benefitDesc }}
                    </text>
                    <text v-if="!benefits.length" class="pool-desc">暂无权益说明</text>
                </view>
            </view>

            <view class="level-info-bar">
                <text class="level-info-text">
                    共 {{ credit.recordCount || 0 }} 条信用记录，当前规则 {{ (credit.rules || []).length }} 条
                </text>
            </view>

            <view class="bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
import { getBenefitOverview } from '@/api/benefit'
import { getCredit, getCreditBenefits } from '@/api/review'

export default {
    data() {
        return {
            credit: {},
            benefitOverview: {},
            benefits: [],
            recentRecords: []
        }
    },
    onShow() {
        this.loadPageData()
    },
    methods: {
        async loadPageData() {
            try {
                const [credit, benefitOverview, creditBenefits] = await Promise.all([
                    getCredit(),
                    getBenefitOverview().catch(() => ({})),
                    getCreditBenefits().catch(() => ({}))
                ])
                this.credit = credit || {}
                this.benefitOverview = benefitOverview || {}
                this.benefits = (creditBenefits.benefits || []).concat(credit.benefits || [])
                this.recentRecords = credit.recentRecords || benefitOverview.recentPointRecords || []
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
    background: #E8F4FD;

    .header-bg {
        background: linear-gradient(180deg, #4A90F0 0%, #6BA3F5 100%);
        padding-bottom: 40rpx;

        .header {
            padding: 60rpx 30rpx 30rpx;
            display: flex;
            justify-content: space-between;
            align-items: center;

            .back-btn,
            .placeholder {
                width: 60rpx;
            }

            .back-icon {
                font-size: 40rpx;
                color: #fff;
                transform: rotate(180deg);
            }

            .title {
                font-size: 34rpx;
                font-weight: bold;
                color: #fff;
            }
        }

        .credit-card {
            margin: 0 20rpx;
            background: #fff;
            border-radius: 20rpx;
            padding: 32rpx;
        }

        .score-center {
            display: flex;
            flex-direction: column;
            align-items: center;

            .credit-score {
                font-size: 120rpx;
                font-weight: bold;
                color: #4A90F0;
                line-height: 1;
            }

            .level-text,
            .next-text {
                font-size: 26rpx;
                color: #4A90F0;
                margin-top: 8rpx;
            }
        }
    }

    .content-scroll {
        padding: 20rpx;
        box-sizing: border-box;

        .change-card,
        .pool-card {
            background: #fff;
            border-radius: 16rpx;
            padding: 24rpx;
            margin-bottom: 20rpx;
        }

        .card-header {
            margin-bottom: 24rpx;

            .header-title {
                font-size: 28rpx;
                font-weight: bold;
                color: #333;
            }
        }

        .change-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 20rpx 0;
            border-bottom: 1rpx solid #F0F0F0;

            &:last-child {
                border-bottom: none;
            }
        }

        .change-info {
            display: flex;
            flex-direction: column;
            gap: 8rpx;
        }

        .change-title {
            font-size: 28rpx;
            color: #333;
            font-weight: bold;
        }

        .change-desc {
            font-size: 24rpx;
            color: #999;
        }

        .change-score {
            font-size: 30rpx;
            color: #2E83F0;
            font-weight: bold;

            &.negative {
                color: #E53935;
            }
        }

        .pool-desc,
        .empty-text {
            display: block;
            font-size: 24rpx;
            color: #666;
            line-height: 1.7;
            margin-bottom: 8rpx;
        }

        .level-info-bar {
            background: #E8F4FD;
            border-radius: 8rpx;
            padding: 20rpx;
            margin-bottom: 20rpx;

            .level-info-text {
                font-size: 24rpx;
                color: #4A90F0;
                text-align: center;
            }
        }

        .bottom-space {
            height: 60rpx;
        }
    }
}
</style>
