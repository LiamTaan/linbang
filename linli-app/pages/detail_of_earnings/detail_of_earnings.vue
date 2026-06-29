<template>
    <view class="page-container">
        <view class="header">
            <view class="back-btn" @click="goBack">
                <text class="iconfont icon-youjiantou back-icon"></text>
            </view>
            <text class="title">收支明细</text>
            <view class="filter-btn" @click="reloadData">
                <text class="filter-text">刷新</text>
            </view>
        </view>

        <view class="tabs">
            <view
                v-for="(item, index) in tabs"
                :key="item.value"
                class="tab-item"
                :class="{ active: currentTab === index }"
                @click="changeTab(index)">
                <text class="tab-text">{{ item.label }}</text>
                <view v-if="currentTab === index" class="tab-line"></view>
            </view>
        </view>

        <scroll-view class="content-scroll" scroll-y @scrolltolower="loadMore">
            <view v-for="monthGroup in earningsData" :key="monthGroup.month" class="month-group">
                <view class="month-header">
                    <view class="month-info">
                        <text class="month-text">{{ monthGroup.month }}</text>
                    </view>
                    <view class="month-stat">
                        <text class="stat-text">收入 <text class="income-text">¥{{ monthGroup.income }}</text></text>
                        <text class="stat-divider">支出</text>
                        <text class="expense-text">¥{{ monthGroup.expense }}</text>
                    </view>
                </view>

                <view class="transaction-list">
                    <view v-for="item in monthGroup.transactions" :key="item.id" class="transaction-item">
                        <view class="icon-wrapper" :class="item.iconClass">
                            <image class="transaction-icon" :src="item.icon" />
                        </view>
                        <view class="transaction-info">
                            <text class="transaction-title">{{ item.title }}</text>
                            <text class="transaction-time">{{ item.time }}</text>
                        </view>
                        <text class="transaction-amount" :class="item.amountClass">{{ item.amount }}</text>
                    </view>
                </view>
            </view>
            <view v-if="!earningsData.length && !loading" class="empty-state">
                <text class="empty-text">暂无账单明细</text>
            </view>
        </scroll-view>
    </view>
</template>

<script>
import { getWalletBillPage } from '@/api/wallet'

export default {
    data() {
        return {
            currentTab: 0,
            tabs: [
                { label: '全部', value: 'ALL' },
                { label: '支出', value: 'OUT' },
                { label: '收入', value: 'IN' }
            ],
            earningsData: [],
            sourceList: [],
            pageNo: 1,
            pageSize: 30,
            finished: false,
            loading: false
        }
    },
    onShow() {
        this.reloadData()
    },
    methods: {
        async reloadData() {
            this.pageNo = 1
            this.finished = false
            this.sourceList = []
            this.earningsData = []
            await this.loadBills()
        },
        async loadBills() {
            if (this.loading || this.finished) {
                return
            }
            this.loading = true
            try {
                const page = await getWalletBillPage({
                    pageNo: this.pageNo,
                    pageSize: this.pageSize
                })
                const list = page.list || []
                this.sourceList = this.sourceList.concat(list)
                this.finished = list.length < this.pageSize
                this.pageNo += 1
                this.buildGroups()
            } catch (error) {
            } finally {
                this.loading = false
            }
        },
        buildGroups() {
            const direction = this.tabs[this.currentTab].value
            const groups = {}
            this.sourceList
                .filter((item) => direction === 'ALL' || item.amountDirection === direction)
                .forEach((item) => {
                    const month = this.$fmt.formatDateTime(item.createTime).slice(0, 7)
                    if (!groups[month]) {
                        groups[month] = {
                            month,
                            income: 0,
                            expense: 0,
                            transactions: []
                        }
                    }
                    const amountValue = Number(item.amount || 0)
                    if (item.amountDirection === 'OUT') {
                        groups[month].expense += amountValue
                    } else {
                        groups[month].income += amountValue
                    }
                    groups[month].transactions.push({
                        id: item.id,
                        icon: this.getIcon(item),
                        iconClass: this.getIconClass(item),
                        title: item.billTitle || item.billType || '钱包账单',
                        time: this.$fmt.formatDateTime(item.createTime),
                        amount: `${item.amountDirection === 'OUT' ? '-' : '+'}${this.$fmt.formatMoney(item.amount)}`,
                        amountClass: item.amountDirection === 'OUT' ? 'expense' : 'income'
                    })
                })
            this.earningsData = Object.keys(groups)
                .sort((a, b) => (a < b ? 1 : -1))
                .map((key) => ({
                    ...groups[key],
                    income: this.$fmt.formatMoney(groups[key].income),
                    expense: this.$fmt.formatMoney(groups[key].expense)
                }))
        },
        getIcon(item) {
            if (item.billType === 'WITHDRAW') {
                return '/static/img/detail_of_earnings/withdraw@3x.png'
            }
            if (item.bizStatus === 'PENDING') {
                return '/static/img/detail_of_earnings/freeze@3x.png'
            }
            return '/static/img/detail_of_earnings/income@3x.png'
        },
        getIconClass(item) {
            if (item.billType === 'WITHDRAW') {
                return 'icon-black'
            }
            if (item.bizStatus === 'PENDING') {
                return 'icon-orange'
            }
            return 'icon-blue'
        },
        changeTab(index) {
            this.currentTab = index
            this.buildGroups()
        },
        loadMore() {
            this.loadBills()
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
    background: #E5F0FF;

    .header {
        background: #fff;
        padding: 60rpx 30rpx 30rpx;
        display: flex;
        justify-content: space-between;
        align-items: center;

        .back-btn,
        .filter-btn {
            width: 80rpx;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .back-icon {
            font-size: 40rpx;
            color: #333;
            transform: rotate(180deg);
        }

        .title {
            font-size: 34rpx;
            font-weight: bold;
            color: #333;
        }

        .filter-text {
            font-size: 28rpx;
            color: #666;
        }
    }

    .tabs {
        background: #fff;
        padding: 0 30rpx 24rpx;
        display: flex;
        gap: 40rpx;

        .tab-item {
            display: flex;
            flex-direction: column;
            align-items: center;

            &.active {
                .tab-text {
                    color: #2E83F0;
                    font-weight: bold;
                }
            }

            .tab-text {
                font-size: 28rpx;
                color: #666;
            }

            .tab-line {
                width: 40rpx;
                height: 6rpx;
                background: #2E83F0;
                border-radius: 3rpx;
                margin-top: 8rpx;
            }
        }
    }

    .content-scroll {
        padding: 20rpx 30rpx;
        box-sizing: border-box;

        .month-group {
            margin-bottom: 20rpx;

            .month-header {
                padding: 20rpx 24rpx;
                display: flex;
                justify-content: space-between;
                align-items: center;

                .month-text {
                    font-size: 28rpx;
                    font-weight: bold;
                    color: #333;
                }

                .month-stat {
                    display: flex;
                    align-items: center;
                    gap: 8rpx;
                    font-size: 20rpx;
                    color: #666666;
                }
            }

            .transaction-list {
                padding: 0 24rpx;
                background: #fff;
                border-radius: 16rpx;

                .transaction-item {
                    display: flex;
                    align-items: center;
                    padding: 24rpx 0;
                    border-bottom: 1rpx solid #F0F0F0;

                    &:last-child {
                        border-bottom: none;
                    }

                    .icon-wrapper {
                        width: 64rpx;
                        height: 64rpx;
                        border-radius: 50%;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        margin-right: 20rpx;

                        &.icon-blue {
                            background: #E8F4FD;
                        }

                        &.icon-orange {
                            background: #FFF7E6;
                        }

                        &.icon-black {
                            background: #F1F3F6;
                        }

                        .transaction-icon {
                            width: 36rpx;
                            height: 36rpx;
                        }
                    }

                    .transaction-info {
                        flex: 1;

                        .transaction-title {
                            display: block;
                            font-size: 28rpx;
                            color: #333;
                            margin-bottom: 8rpx;
                        }

                        .transaction-time {
                            font-size: 24rpx;
                            color: #999;
                        }
                    }

                    .transaction-amount {
                        font-size: 30rpx;
                        font-weight: bold;

                        &.income {
                            color: #2E83F0;
                        }

                        &.expense {
                            color: #E53935;
                        }
                    }
                }
            }
        }

        .empty-state {
            padding: 60rpx 0;
            text-align: center;

            .empty-text {
                font-size: 24rpx;
                color: #999;
            }
        }
    }
}
</style>
