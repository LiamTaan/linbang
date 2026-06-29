<template>
    <view class="page-container">
        <view class="header">
            <view class="back-btn" @click="goBack">
                <text class="iconfont icon-youjiantou back-icon"></text>
            </view>
            <text class="title">接单状态管理</text>
            <view class="placeholder"></view>
        </view>
        <view class="background"></view>

        <scroll-view class="content-scroll" scroll-y refresher-enabled :refresher-triggered="refreshing"
            @refresherrefresh="handleRefresh">
            <view class="status-card">
                <view class="status-header">
                    <view class="status-left">
                        <text class="status-title">当前状态</text>
                        <view class="online-tag">
                            <view class="online-dot" :class="{ offline: !isOnline }"></view>
                            <text class="online-text">{{ isOnline ? '接单中' : '已暂停' }}</text>
                        </view>
                    </view>
                    <view class="status-switch" :class="{ active: isOnline }" @click="toggleOnline">
                        <view class="switch-dot"></view>
                    </view>
                </view>
                <text class="status-desc">
                    {{ acceptStatus.acceptEligible === false && !isOnline ? (acceptStatus.blockedReason || '暂不可接单') : '系统会按当前半径和派单设置推送订单' }}
                </text>
                <text class="status-tip" v-if="acceptStatus.nextAction">{{ acceptStatus.nextAction }}</text>
            </view>

            <view class="data-card">
                <text class="card-title">当前订单数据</text>
                <view class="data-grid">
                    <view class="data-item blue">
                        <text class="data-value">{{ stats.accepted }}</text>
                        <text class="data-label">待服务</text>
                    </view>
                    <view class="data-item green">
                        <text class="data-value">{{ stats.finished }}</text>
                        <text class="data-label">已完成</text>
                    </view>
                    <view class="data-item orange">
                        <text class="data-value">{{ stats.serving }}</text>
                        <text class="data-label">进行中</text>
                    </view>
                </view>
            </view>

            <view class="order-card">
                <view class="order-header">
                    <text class="card-title">进行中的订单</text>
                    <view class="view-all" @click="handleViewAll">
                        <text class="view-all-text">查看全部</text>
                        <text class="view-all-arrow">></text>
                    </view>
                </view>

                <view class="order-list" v-if="activeOrders.length">
                    <view v-for="item in activeOrders" :key="item.id" class="order-item"
                        :class="item.status === 'SERVING' ? 'orange-bg' : 'blue-bg'">
                        <view class="order-info">
                            <text class="order-name">{{ item.categoryName || item.orderNo }}</text>
                            <text class="order-desc">金额：¥{{ $fmt.formatMoney(item.orderAmount) }} · {{ getOrderStatusLabel(item.status) }}</text>
                        </view>
                        <text class="order-status">{{ getOrderStatusLabel(item.status) }}</text>
                    </view>
                </view>
                <view v-else class="empty-inline">当前没有进行中的订单</view>
            </view>

            <view class="settings-card">
                <text class="card-title">推送设置</text>
                <view class="settings-list">
                    <view class="setting-item" @click="handleDistance">
                        <view class="setting-left">
                            <image class="setting-icon" src="/static/img/order_receiving_status/distance@3x.png" />
                            <text class="setting-name">接收距离范围</text>
                        </view>
                        <view class="setting-right">
                            <text class="setting-value">{{ dispatchRadiusText }}</text>
                            <text class="setting-arrow">></text>
                        </view>
                    </view>

                    <view class="setting-item" @click="handleCategory">
                        <view class="setting-left">
                            <image class="setting-icon" src="/static/img/order_receiving_status/category@3x.png" />
                            <text class="setting-name">接收品类</text>
                        </view>
                        <view class="setting-right">
                            <text class="setting-value">{{ categorySummary }}</text>
                            <text class="setting-arrow">></text>
                        </view>
                    </view>

                    <view class="setting-item" @click="handleSound">
                        <view class="setting-left">
                            <image class="setting-icon" src="/static/img/order_receiving_status/sound@3x.png" />
                            <text class="setting-name">声音提醒</text>
                        </view>
                        <view class="setting-right">
                            <text class="setting-value">{{ dispatchSetting.voiceRemindEnabled ? '开启' : '关闭' }}</text>
                            <text class="setting-arrow">></text>
                        </view>
                    </view>
                </view>
            </view>

            <view class="pause-btn" @click="handlePause">
                <text class="pause-text">{{ isOnline ? '暂停接单' : '恢复接单' }}</text>
                <text class="pause-desc">{{ isOnline ? '暂停后系统将停止向您推送订单' : '恢复后系统将继续为您派单' }}</text>
            </view>

            <view class="bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
import { getServiceCategoryList, getMerchantAcceptStatus, getMerchantDispatchSetting, updateMerchantAcceptStatus, updateMerchantDispatchSetting } from '@/api/merchant'
import { getOrderPage } from '@/api/order'
import { getOrderStatusLabel } from '@/utils/linbang'

export default {
    data() {
        return {
            refreshing: false,
            acceptStatus: {},
            dispatchSetting: {},
            activeOrders: [],
            categories: [],
            stats: {
                accepted: 0,
                finished: 0,
                serving: 0
            }
        }
    },
    computed: {
        isOnline() {
            return this.acceptStatus.acceptStatus === 'ENABLE'
        },
        dispatchRadiusText() {
            const radius = this.dispatchSetting.maxAcceptRadiusKm || this.acceptStatus.maxAcceptRadiusKm
            return radius ? `${radius}km 内` : '未设置'
        },
        categorySummary() {
            if (!this.categories.length) {
                return '暂无类目'
            }
            return `平台已开放 ${this.categories.length} 类`
        }
    },
    onShow() {
        this.loadPageData()
    },
    methods: {
        getOrderStatusLabel,
        goBack() {
            uni.navigateBack()
        },
        handleRefresh() {
            this.refreshing = true
            this.loadPageData()
        },
        async loadPageData() {
            try {
                const [acceptStatus, dispatchSetting, servingPage, acceptedPage, finishedPage, categories] = await Promise.all([
                    getMerchantAcceptStatus().catch(() => ({})),
                    getMerchantDispatchSetting().catch(() => ({})),
                    getOrderPage({ pageNo: 1, pageSize: 3, businessCategory: 'IN_SERVICE' }).catch(() => ({ list: [], total: 0 })),
                    getOrderPage({ pageNo: 1, pageSize: 1, status: 'ACCEPTED' }).catch(() => ({ total: 0 })),
                    getOrderPage({ pageNo: 1, pageSize: 1, status: 'FINISHED' }).catch(() => ({ total: 0 })),
                    getServiceCategoryList().catch(() => [])
                ])
                this.acceptStatus = acceptStatus || {}
                this.dispatchSetting = {
                    dispatchEnabled: acceptStatus.dispatchEnabled,
                    maxAcceptRadiusKm: acceptStatus.maxAcceptRadiusKm,
                    voiceRemindEnabled: acceptStatus.voiceRemindEnabled,
                    ...(dispatchSetting || {})
                }
                this.activeOrders = (servingPage && servingPage.list) || []
                this.stats = {
                    accepted: Number((acceptedPage && acceptedPage.total) || 0),
                    finished: Number((finishedPage && finishedPage.total) || 0),
                    serving: Number((servingPage && servingPage.total) || 0)
                }
                this.categories = categories || []
            } catch (error) {
            } finally {
                this.refreshing = false
            }
        },
        async toggleOnline() {
            const nextStatus = this.isOnline ? 'DISABLE' : 'ENABLE'
            if (nextStatus === 'ENABLE' && this.acceptStatus.acceptEligible === false) {
                uni.showToast({
                    title: this.acceptStatus.blockedReason || '当前暂不可接单',
                    icon: 'none'
                })
                return
            }
            try {
                await updateMerchantAcceptStatus({
                    acceptStatus: nextStatus,
                    dispatchEnabled: !!this.dispatchSetting.dispatchEnabled,
                    maxAcceptRadiusKm: this.dispatchSetting.maxAcceptRadiusKm || 5,
                    voiceRemindEnabled: !!this.dispatchSetting.voiceRemindEnabled
                })
                uni.showToast({
                    title: nextStatus === 'ENABLE' ? '已开启接单' : '已暂停接单',
                    icon: 'success'
                })
                this.loadPageData()
            } catch (error) {
            }
        },
        handleViewAll() {
            uni.navigateTo({
                url: '/pages/order/order?mode=serving'
            })
        },
        handleCategory() {
            const list = (this.categories || []).slice(0, 12).map((item) => item.categoryName)
            uni.showModal({
                title: '当前可接品类',
                content: list.length ? list.join('、') : '暂无可接品类',
                showCancel: false
            })
        },
        handleDistance() {
            const options = ['3km 以内', '5km 以内', '10km 以内', '20km 以内']
            const values = [3, 5, 10, 20]
            uni.showActionSheet({
                itemList: options,
                success: async ({ tapIndex }) => {
                    try {
                        await updateMerchantDispatchSetting({
                            dispatchEnabled: this.dispatchSetting.dispatchEnabled !== false,
                            maxAcceptRadiusKm: values[tapIndex],
                            voiceRemindEnabled: !!this.dispatchSetting.voiceRemindEnabled
                        })
                        uni.showToast({
                            title: '接单范围已更新',
                            icon: 'success'
                        })
                        this.loadPageData()
                    } catch (error) {
                    }
                }
            })
        },
        handleSound() {
            this.saveDispatchSetting({
                voiceRemindEnabled: !this.dispatchSetting.voiceRemindEnabled
            }, '提醒设置已更新')
        },
        handlePause() {
            this.toggleOnline()
        },
        async saveDispatchSetting(patch, successTitle) {
            try {
                await updateMerchantDispatchSetting({
                    dispatchEnabled: patch.dispatchEnabled !== undefined ? patch.dispatchEnabled : (this.dispatchSetting.dispatchEnabled !== false),
                    maxAcceptRadiusKm: patch.maxAcceptRadiusKm !== undefined ? patch.maxAcceptRadiusKm : (this.dispatchSetting.maxAcceptRadiusKm || 5),
                    voiceRemindEnabled: patch.voiceRemindEnabled !== undefined ? patch.voiceRemindEnabled : !!this.dispatchSetting.voiceRemindEnabled
                })
                uni.showToast({
                    title: successTitle,
                    icon: 'success'
                })
                this.loadPageData()
            } catch (error) {
            }
        }
    }
}
</script>

<style lang="scss" scoped>
.page-container {
    min-height: 100vh;
    background: #F5F5F5;

    .header {
        background: #4A90F0;
        padding: 60rpx 30rpx 30rpx;
        display: flex;
        justify-content: space-between;
        align-items: center;

        .back-btn {
            width: 60rpx;
            height: 60rpx;
            display: flex;
            align-items: center;
            justify-content: center;

            .back-icon {
                font-size: 40rpx;
                color: #fff;
                transform: rotate(180deg);
            }
        }

        .title {
            font-size: 34rpx;
            font-weight: bold;
            color: #fff;
        }

        .placeholder {
            width: 60rpx;
        }
    }

    .background {
        background: #4A90F0;
        border-radius: 0 0 30rpx 30rpx;
        width: 100%;
        height: 50rpx;
        top: -1px;
        position: relative;
    }

    .content-scroll {
        flex: 1;
        padding: 20rpx;
        margin-top: -70rpx;
        box-sizing: border-box;

        .status-card,
        .data-card,
        .order-card,
        .settings-card,
        .pause-btn {
            background: #fff;
            border-radius: 20rpx;
            padding: 24rpx;
            margin-bottom: 20rpx;
        }

        .status-card {
            .status-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 16rpx;

                .status-left {
                    display: flex;
                    align-items: center;
                    gap: 16rpx;

                    .status-title {
                        font-size: 32rpx;
                        font-weight: bold;
                        color: #333;
                    }

                    .online-tag {
                        display: flex;
                        align-items: center;
                        gap: 8rpx;

                        .online-dot {
                            width: 16rpx;
                            height: 16rpx;
                            background: #52C41A;
                            border-radius: 50%;

                            &.offline {
                                background: #FA9D3B;
                            }
                        }

                        .online-text {
                            font-size: 24rpx;
                            color: #52C41A;
                        }
                    }
                }

                .status-switch {
                    width: 100rpx;
                    height: 56rpx;
                    background: #E8E8E8;
                    border-radius: 28rpx;
                    position: relative;

                    &.active {
                        background: #4A90F0;

                        .switch-dot {
                            transform: translateX(44rpx);
                        }
                    }

                    .switch-dot {
                        width: 48rpx;
                        height: 48rpx;
                        background: #fff;
                        border-radius: 50%;
                        position: absolute;
                        top: 4rpx;
                        left: 4rpx;
                        transition: transform 0.3s;
                    }
                }
            }

            .status-desc,
            .status-tip {
                font-size: 26rpx;
                color: #999;
                display: block;
            }

            .status-tip {
                margin-top: 10rpx;
                color: #2E83F0;
            }
        }

        .card-title {
            font-size: 28rpx;
            font-weight: bold;
            color: #333;
            margin-bottom: 20rpx;
            display: block;
        }

        .data-grid {
            display: flex;
            gap: 20rpx;

            .data-item {
                flex: 1;
                border-radius: 12rpx;
                padding: 24rpx 16rpx;
                text-align: center;

                &.blue {
                    background: #E8F4FD;
                }

                &.green {
                    background: #E8F8E8;
                }

                &.orange {
                    background: #FFF7E6;
                }

                .data-value {
                    font-size: 40rpx;
                    font-weight: bold;
                    display: block;
                    color: #333;
                }

                .data-label {
                    font-size: 24rpx;
                    color: #999;
                    margin-top: 8rpx;
                    display: block;
                }
            }
        }

        .order-header,
        .settings-list .setting-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .view-all {
            display: flex;
            align-items: center;
            gap: 4rpx;

            .view-all-text,
            .view-all-arrow {
                font-size: 24rpx;
                color: #999;
            }
        }

        .order-list {
            display: flex;
            flex-direction: column;
            gap: 16rpx;

            .order-item {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 20rpx;
                border-radius: 12rpx;

                &.orange-bg {
                    background: #FFF7E6;
                }

                &.blue-bg {
                    background: #E8F4FD;
                }

                .order-info {
                    .order-name {
                        font-size: 28rpx;
                        color: #333;
                        display: block;
                        margin-bottom: 8rpx;
                    }

                    .order-desc {
                        font-size: 24rpx;
                        color: #999;
                    }
                }

                .order-status {
                    font-size: 24rpx;
                    font-weight: bold;
                    color: #2E83F0;
                }
            }
        }

        .empty-inline {
            font-size: 24rpx;
            color: #999;
        }

        .settings-list {
            .setting-item {
                padding: 20rpx 0;
                border-bottom: 1rpx solid #F0F0F0;

                &:last-child {
                    border-bottom: none;
                }

                .setting-left,
                .setting-right {
                    display: flex;
                    align-items: center;
                    gap: 16rpx;
                }

                .setting-icon {
                    width: 30rpx;
                    height: 30rpx;
                }

                .setting-name {
                    font-size: 28rpx;
                    color: #333;
                }

                .setting-value,
                .setting-arrow {
                    font-size: 26rpx;
                    color: #999;
                }
            }
        }

        .pause-btn {
            border: 2rpx solid #FA9D3B;
            text-align: center;

            .pause-text {
                font-size: 32rpx;
                font-weight: bold;
                color: #FA9D3B;
                display: block;
                margin-bottom: 8rpx;
            }

            .pause-desc {
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
