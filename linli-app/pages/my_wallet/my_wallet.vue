<template>
    <view class="page-container">
        <view class="header-bg">
            <view class="header">
                <view class="back-btn" @click="goBack">
                    <text class="iconfont icon-youjiantou back-icon"></text>
                </view>
                <text class="title">我的钱包</text>
                <view class="detail-btn" @click="goToDetail">
                    <text class="detail-text">明细</text>
                </view>
            </view>

            <view class="asset-card">
                <view class="asset-header" @click="toggleAmountVisible">
                    <text class="asset-label">总资产（元）</text>
                    <image class="eye-icon" src="/static/img/my_wallet/eye@3x.png" />
                </view>
                <text class="asset-value">{{ displayAmount(wallet.totalAmount) }}</text>

                <view class="asset-stats">
                    <view class="stat-item">
                        <text class="stat-label">可提现</text>
                        <text class="stat-value green">{{ displayAmount(wallet.availableAmount) }}</text>
                    </view>
                    <view class="stat-divider"></view>
                    <view class="stat-item">
                        <text class="stat-label">冻结中</text>
                        <text class="stat-value orange">{{ displayAmount(wallet.frozenAmount) }}</text>
                    </view>
                    <view class="stat-divider"></view>
                    <view class="stat-item">
                        <text class="stat-label">托管中</text>
                        <text class="stat-value blue">{{ displayAmount(wallet.escrowAmount) }}</text>
                    </view>
                </view>

                <view class="asset-actions">
                    <view class="action-btn outline" @click="handleBankCard">
                        <text class="action-text">银行卡管理</text>
                    </view>
                    <view class="action-btn primary" @click="handleWithdraw">
                        <text class="action-text">提现</text>
                    </view>
                </view>
            </view>
        </view>

        <scroll-view class="content-scroll" scroll-y>
            <view class="benefit-card">
                <view class="benefit-item">
                    <text class="benefit-label">累计推广收益</text>
                    <text class="benefit-value">¥{{ $fmt.formatMoney(wallet.totalPromoteIncome) }}</text>
                </view>
                <view class="benefit-item">
                    <text class="benefit-label">待结算推广收益</text>
                    <text class="benefit-value">¥{{ $fmt.formatMoney(wallet.pendingPromoteIncome) }}</text>
                </view>
            </view>

            <view class="recent-card">
                <view class="card-header">
                    <text class="header-title">近期账单</text>
                    <view class="header-more" @click="goToDetail">
                        <text class="more-text">查看全部</text>
                        <text class="iconfont icon-youjiantou more-arrow"></text>
                    </view>
                </view>
                <view class="transaction-list">
                    <view v-for="item in transactions" :key="item.id" class="transaction-item">
                        <view class="transaction-info">
                            <text class="transaction-title">{{ item.title }}</text>
                            <text class="transaction-time">{{ item.time }}</text>
                        </view>
                        <text class="transaction-amount" :class="item.type">{{ item.amount }}</text>
                    </view>
                    <view v-if="!transactions.length" class="empty-text">暂无账单记录</view>
                </view>
            </view>

            <view class="bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
import { getWalletAccount, getWalletBillPage } from '@/api/wallet'

export default {
    data() {
        return {
            wallet: {},
            transactions: [],
            amountVisible: true
        }
    },
    onShow() {
        this.loadPageData()
    },
    methods: {
        async loadPageData() {
            try {
                const [wallet, billPage] = await Promise.all([
                    getWalletAccount(),
                    getWalletBillPage({ pageNo: 1, pageSize: 6 })
                ])
                this.wallet = wallet || {}
                this.transactions = (billPage.list || []).map((item) => ({
                    id: item.id,
                    title: item.billTitle || item.billType || '钱包账单',
                    time: this.$fmt.formatDateTime(item.createTime),
                    amount: `${item.amountDirection === 'OUT' ? '-' : '+'}¥${this.$fmt.formatMoney(item.amount)}`,
                    type: item.amountDirection === 'OUT' ? 'expense' : 'income'
                }))
            } catch (error) {
            }
        },
        displayAmount(value) {
            if (!this.amountVisible) {
                return '******'
            }
            return `¥${this.$fmt.formatMoney(value)}`
        },
        toggleAmountVisible() {
            this.amountVisible = !this.amountVisible
        },
        goBack() {
            uni.navigateBack()
        },
        goToDetail() {
            uni.navigateTo({ url: '/pages/detail_of_earnings/detail_of_earnings' })
        },
        handleBankCard() {
            uni.navigateTo({ url: '/pages/bank_card_management/bank_card_management' })
        },
        handleWithdraw() {
            uni.navigateTo({ url: '/pages/withdraw_deposit/withdraw_deposit' })
        }
    }
}
</script>

<style lang="scss" scoped>
.page-container {
    min-height: 100vh;
    background: #F5F5F5;

    .header-bg {
        background: linear-gradient(180deg, #4A90F0 0%, #6BA3F5 100%);
        padding-bottom: 40rpx;

        .header {
            padding: 60rpx 30rpx 30rpx;
            display: flex;
            justify-content: space-between;
            align-items: center;

            .back-btn,
            .detail-btn {
                width: 80rpx;
                display: flex;
                align-items: center;
                justify-content: center;
            }

            .back-icon,
            .detail-text {
                color: #fff;
            }

            .back-icon {
                font-size: 40rpx;
                transform: rotate(180deg);
            }

            .title {
                font-size: 34rpx;
                font-weight: bold;
                color: #fff;
            }
        }

        .asset-card {
            margin: 0 20rpx;
            background: #fff;
            border-radius: 20rpx;
            padding: 32rpx;

            .asset-header {
                display: flex;
                justify-content: center;
                align-items: center;
                gap: 12rpx;
                margin-bottom: 20rpx;

                .asset-label {
                    font-size: 26rpx;
                    color: #666;
                }

                .eye-icon {
                    width: 24rpx;
                    height: 18rpx;
                }
            }

            .asset-value {
                display: block;
                text-align: center;
                font-size: 56rpx;
                font-weight: bold;
                color: #4A90F0;
                margin-bottom: 32rpx;
            }

            .asset-stats {
                display: flex;
                justify-content: space-around;
                background: #F5F7FA;
                border-radius: 12rpx;
                padding: 24rpx;
                margin-bottom: 32rpx;

                .stat-item {
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    flex: 1;

                    .stat-label {
                        font-size: 24rpx;
                        color: #666;
                        margin-bottom: 8rpx;
                    }

                    .stat-value {
                        font-size: 28rpx;
                        font-weight: bold;

                        &.green {
                            color: #52C41A;
                        }

                        &.orange {
                            color: #FA9D3B;
                        }

                        &.blue {
                            color: #4A90F0;
                        }
                    }
                }

                .stat-divider {
                    width: 1rpx;
                    height: 40rpx;
                    background: #E0E0E0;
                }
            }

            .asset-actions {
                display: flex;
                gap: 20rpx;

                .action-btn {
                    flex: 1;
                    padding: 24rpx;
                    border-radius: 12rpx;
                    text-align: center;

                    &.outline {
                        border: 2rpx solid #4A90F0;

                        .action-text {
                            color: #4A90F0;
                        }
                    }

                    &.primary {
                        background: #4A90F0;

                        .action-text {
                            color: #fff;
                        }
                    }

                    .action-text {
                        font-size: 28rpx;
                        font-weight: bold;
                    }
                }
            }
        }
    }

    .content-scroll {
        padding: 20rpx;
        box-sizing: border-box;

        .benefit-card,
        .recent-card {
            background: #fff;
            border-radius: 16rpx;
            padding: 24rpx;
            margin-bottom: 20rpx;
        }

        .benefit-card {
            display: flex;
            gap: 20rpx;

            .benefit-item {
                flex: 1;
                background: #F7FAFF;
                border-radius: 12rpx;
                padding: 20rpx;

                .benefit-label {
                    display: block;
                    font-size: 24rpx;
                    color: #666;
                    margin-bottom: 12rpx;
                }

                .benefit-value {
                    font-size: 28rpx;
                    font-weight: bold;
                    color: #333;
                }
            }
        }

        .card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 24rpx;

            .header-title {
                font-size: 28rpx;
                font-weight: bold;
                color: #333;
            }

            .header-more {
                display: flex;
                align-items: center;
                gap: 8rpx;

                .more-text,
                .more-arrow {
                    font-size: 24rpx;
                    color: #4A90F0;
                }
            }
        }

        .transaction-list {
            .transaction-item {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 20rpx 0;
                border-bottom: 1rpx solid #F0F0F0;

                &:last-child {
                    border-bottom: none;
                }

                .transaction-info {
                    display: flex;
                    flex-direction: column;
                    gap: 8rpx;

                    .transaction-title {
                        font-size: 28rpx;
                        color: #333;
                    }

                    .transaction-time {
                        font-size: 24rpx;
                        color: #999;
                    }
                }

                .transaction-amount {
                    font-size: 28rpx;
                    font-weight: bold;

                    &.income {
                        color: #52C41A;
                    }

                    &.expense {
                        color: #E53935;
                    }
                }
            }

            .empty-text {
                padding: 32rpx 0 12rpx;
                text-align: center;
                font-size: 24rpx;
                color: #999;
            }
        }

        .bottom-space {
            height: 60rpx;
        }
    }
}
</style>
