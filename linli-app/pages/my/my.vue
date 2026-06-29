<template>
    <view class="page-container">
        <view class="page-container-header">
            <view class="user-card">
                <view class="user-info">
                    <image class="avatar" :src="displayAvatar" />
                    <view class="user-detail">
                        <view class="user-name">
                            <text class="name">{{ profile.nickname || '未设置昵称' }}</text>
                            <view class="service-badge" @click="handleRoleSwitch">
                                <text class="badge-text">{{ currentRoleName }}</text>
                                <image
                                    v-if="switchableRoles.length"
                                    class="badge-icon"
                                    src="/static/img/my/switch@3x.png" />
                            </view>
                        </view>
                        <view class="credit-score">
                            <text class="label">信用分：</text>
                            <text class="score">{{ profile.creditScore || 0 }}</text>
                        </view>
                        <view class="verify-tags">
                            <view v-for="item in verifyTags" :key="item" class="tag-item">
                                <text class="tag-text">{{ item }}</text>
                            </view>
                        </view>
                    </view>
                </view>
                <view class="user-level">
                    <view class="level-badge">
                        <text class="level-text">
                            <image src="/static/img/my/level@3x.png" />
                            {{ profile.creditLevel || 'NORMAL' }}
                        </text>
                    </view>
                </view>
            </view>

            <view class="stats-card">
                <view class="stat-item">
                    <text class="stat-label">总订单</text>
                    <text class="stat-value">{{ orderStats.total }} 单</text>
                </view>
                <view class="stat-divider"></view>
                <view class="stat-item" @click="goToWallet">
                    <text class="stat-label">账户余额</text>
                    <text class="stat-value">¥{{ $fmt.formatMoney(wallet.availableAmount) }}</text>
                </view>
                <view class="stat-divider"></view>
                <view class="stat-item" @click="navigateTo('/pages/promotion_center/promotion_center')">
                    <text class="stat-label">推广收益</text>
                    <text class="stat-value">¥{{ $fmt.formatMoney(promote.totalCommissionAmount) }}</text>
                </view>
            </view>
        </view>

        <view class="quick-entry">
            <view class="entry-item" @click="navigateTo('/pages/order/order?mode=accept')">
                <view class="entry-icon-wrapper">
                    <image class="entry-icon" src="/static/img/my/pending-order@3x.png" />
                    <view v-if="orderStats.pending > 0" class="entry-badge">
                        <text class="badge-num">{{ orderStats.pending }}</text>
                    </view>
                </view>
                <text class="entry-text">待接单</text>
            </view>
            <view class="entry-item" @click="navigateTo('/pages/order/order?mode=serving')">
                <image class="entry-icon" src="/static/img/my/task@3x.png" />
                <view v-if="orderStats.serving > 0" class="entry-badge">
                    <text class="badge-num">{{ orderStats.serving }}</text>
                </view>
                <text class="entry-text">服务中</text>
            </view>
            <view class="entry-item" @click="navigateTo('/pages/evaluation_service/evaluation_service')">
                <image class="entry-icon" src="/static/img/my/review@3x.png" />
                <view v-if="orderStats.review > 0" class="entry-badge">
                    <text class="badge-num">{{ orderStats.review }}</text>
                </view>
                <text class="entry-text">待评价</text>
            </view>
            <view class="entry-item" @click="navigateTo('/pages/refund/refund')">
                <image class="entry-icon" src="/static/img/my/after-sales@3x.png" />
                <view v-if="orderStats.afterSale > 0" class="entry-badge">
                    <text class="badge-num">{{ orderStats.afterSale }}</text>
                </view>
                <text class="entry-text">退款售后</text>
            </view>
        </view>

        <view class="function-list">
            <view class="function-item" @click="navigateTo('/pages/order/order?mode=my')">
                <image class="func-icon" src="/static/img/my/order-management@3x.png" />
                <text class="func-text">订单管理</text>
                <image class="arrow-icon" src="/static/img/my/switch@3x.png" />
            </view>
            <view class="function-item" @click="navigateTo('/pages/my_wallet/my_wallet')">
                <image class="func-icon" src="/static/img/my/account-management@3x.png" />
                <text class="func-text">账户管理</text>
                <image class="arrow-icon" src="/static/img/my/switch@3x.png" />
            </view>
            <view class="function-item" @click="navigateTo('/pages/certificate/certificate')">
                <image class="func-icon" src="/static/img/my/verification@3x.png" />
                <text class="func-text">认证与证件</text>
                <image class="arrow-icon" src="/static/img/my/switch@3x.png" />
            </view>
        </view>

        <view class="function-list">
            <view class="function-item" @click="navigateTo('/pages/identity_application/identity_application')">
                <image class="func-icon" src="/static/img/my/identity@3x.png" />
                <text class="func-text">身份申请</text>
                <image class="arrow-icon" src="/static/img/my/switch@3x.png" />
            </view>
            <view class="function-item" @click="navigateTo('/pages/my_credit/my_credit?mode=benefit')">
                <image class="func-icon" src="/static/img/my/rights@3x.png" />
                <text class="func-text">我的权益</text>
                <image class="arrow-icon" src="/static/img/my/switch@3x.png" />
            </view>
            <view class="function-item" @click="navigateTo('/pages/promotion_center/promotion_center')">
                <image class="func-icon" src="/static/img/my/reward@3x.png" />
                <text class="func-text">推广中心</text>
                <image class="arrow-icon" src="/static/img/my/switch@3x.png" />
            </view>
            <view class="function-item" @click="navigateTo('/pages/news/news?category=SYSTEM')">
                <image class="func-icon" src="/static/img/my/alarm@3x.png" />
                <text class="func-text">系统提醒</text>
                <view class="entry-badge small" v-if="unreadCount > 0">
                    <text class="badge-num">{{ unreadCount }}</text>
                </view>
                <image class="arrow-icon" src="/static/img/my/switch@3x.png" />
            </view>
        </view>

        <view class="function-list">
            <view class="function-item" @click="navigateTo('/pages/set/set')">
                <image class="func-icon" src="/static/img/my/settings@3x.png" />
                <text class="func-text">系统设置</text>
                <image class="arrow-icon" src="/static/img/my/switch@3x.png" />
            </view>
            <view class="function-item" @click="navigateTo('/pages/feedback/feedback')">
                <image class="func-icon" src="/static/img/my/help-feedback@3x.png" />
                <text class="func-text">帮助与反馈</text>
                <image class="arrow-icon" src="/static/img/my/switch@3x.png" />
            </view>
        </view>

        <tab-bar :current-index="3"></tab-bar>
    </view>
</template>

<script>
import tabBar from '@/components/tabBar/tabBar.vue'
import { switchRole, getProfile, getRoleContext } from '@/api/member'
import { getUnreadCount } from '@/api/message'
import { getAcceptOrderPage, getOrderPage } from '@/api/order'
import { getPromoteCenter } from '@/api/promote'
import { getPendingReviewUnits } from '@/api/review'
import { getWalletAccount } from '@/api/wallet'

export default {
    components: {
        tabBar
    },
    data() {
        return {
            profile: {},
            roleContext: {},
            wallet: {},
            promote: {},
            unreadCount: 0,
            orderStats: {
                total: 0,
                pending: 0,
                serving: 0,
                review: 0,
                afterSale: 0
            }
        }
    },
    computed: {
        displayAvatar() {
            return this.profile.avatar || '/static/logo.png'
        },
        currentRoleName() {
            return this.roleContext.currentRoleName || this.profile.currentRoleName || '普通用户'
        },
        switchableRoles() {
            return (this.roleContext.roleSummaries || []).filter((item) => item.switchable)
        },
        verifyTags() {
            const tags = []
            if (this.profile.realNameStatus === 'APPROVED') {
                tags.push('已实名')
            }
            if ((this.roleContext.enabledRoleCodes || []).includes('MERCHANT')) {
                tags.push('服务商已开通')
            }
            if ((this.roleContext.enabledRoleCodes || []).includes('PROMOTER')) {
                tags.push('推广员已开通')
            }
            if (!tags.length) {
                tags.push('待完善资料')
            }
            return tags
        }
    },
    onShow() {
        uni.hideTabBar()
        this.loadPageData()
    },
    onPullDownRefresh() {
        this.loadPageData().finally(() => {
            uni.stopPullDownRefresh()
        })
    },
    methods: {
        async loadPageData() {
            try {
                const [profile, roleContext, wallet, unreadCount, acceptPage, orderTotal, servingPage, afterSalePage, pendingReview, promote] = await Promise.all([
                    getProfile(),
                    getRoleContext(),
                    getWalletAccount(),
                    getUnreadCount().catch(() => 0),
                    getAcceptOrderPage({ pageNo: 1, pageSize: 1 }).catch(() => ({ total: 0, list: [] })),
                    getOrderPage({ pageNo: 1, pageSize: 1 }).catch(() => ({ total: 0, list: [] })),
                    getOrderPage({ pageNo: 1, pageSize: 1, businessCategory: 'IN_SERVICE' }).catch(() => ({ total: 0, list: [] })),
                    getOrderPage({ pageNo: 1, pageSize: 1, businessCategory: 'AFTER_SALE' }).catch(() => ({ total: 0, list: [] })),
                    getPendingReviewUnits().catch(() => []),
                    getPromoteCenter().catch(() => ({}))
                ])
                this.profile = profile || {}
                this.roleContext = roleContext || {}
                this.wallet = wallet || {}
                this.promote = promote || {}
                this.unreadCount = Number(unreadCount || 0)
                this.orderStats = {
                    total: Number(orderTotal.total || 0),
                    pending: Number(acceptPage.total || 0),
                    serving: Number(servingPage.total || 0),
                    review: Array.isArray(pendingReview) ? pendingReview.length : Number((pendingReview && pendingReview.total) || 0),
                    afterSale: Number(afterSalePage.total || 0)
                }
            } catch (error) {
            }
        },
        navigateTo(url) {
            uni.navigateTo({
                url,
                fail: () => {
                    uni.showToast({
                        title: '页面暂不可达',
                        icon: 'none'
                    })
                }
            })
        },
        goToWallet() {
            uni.navigateTo({
                url: '/pages/my_wallet/my_wallet'
            })
        },
        async handleRoleSwitch() {
            if (!this.switchableRoles.length) {
                return
            }
            uni.showActionSheet({
                itemList: this.switchableRoles.map((item) => item.roleName || item.roleCode),
                success: async ({ tapIndex }) => {
                    const target = this.switchableRoles[tapIndex]
                    if (!target) {
                        return
                    }
                    try {
                        await switchRole({
                            targetRoleCode: target.roleCode
                        })
                        uni.showToast({
                            title: '角色已切换',
                            icon: 'success'
                        })
                        this.loadPageData()
                    } catch (error) {
                    }
                }
            })
        }
    }
}
</script>

<style lang="scss" scoped>
.page-container {
    min-height: 100vh;
    background: #F5F5F5;
    padding-bottom: 120rpx;

    .page-container-header {
        position: relative;
        padding-top: 40rpx;
    }

    .user-card {
        width: 90%;
        margin: 0 auto 60rpx;
        background: linear-gradient(158deg, #71AFFF, #2E83F0);
        border-radius: 14rpx;
        padding: 40rpx 30rpx 100rpx;
        position: relative;
        z-index: 1;

        .user-info {
            display: flex;
            align-items: center;

            .avatar {
                width: 120rpx;
                height: 120rpx;
                border-radius: 50%;
                border: 4rpx solid rgba(255, 255, 255, 0.3);
                margin-right: 24rpx;
            }

            .user-detail {
                flex: 1;

                .user-name {
                    display: flex;
                    align-items: center;
                    margin-bottom: 12rpx;

                    .name {
                        font-weight: bold;
                        font-size: 30rpx;
                        color: #FFFFFF;
                        margin-right: 16rpx;
                    }

                    .service-badge {
                        border-radius: 50rpx;
                        border: 2rpx solid #FFFFFF;
                        padding: 6rpx 12rpx;
                        display: flex;
                        align-items: center;
                        gap: 6rpx;

                        .badge-icon {
                            width: 20rpx;
                            height: 20rpx;
                        }

                        .badge-text {
                            font-size: 20rpx;
                            color: #FFFFFF;
                        }
                    }
                }

                .credit-score {
                    display: flex;
                    align-items: center;
                    margin-bottom: 12rpx;

                    .label {
                        font-size: 24rpx;
                        color: rgba(255, 255, 255, 0.8);
                    }

                    .score {
                        font-size: 28rpx;
                        font-weight: bold;
                        color: #fff;
                    }
                }

                .verify-tags {
                    display: flex;
                    gap: 16rpx;
                    flex-wrap: wrap;

                    .tag-item {
                        padding: 6rpx 16rpx;
                        background: #FFFFFF;
                        border-radius: 50rpx;
                        display: flex;
                        align-items: center;
                        justify-content: center;

                        .tag-text {
                            font-size: 14rpx;
                            color: #2E83F0;
                        }
                    }
                }
            }
        }

        .user-level {
            position: absolute;
            top: 50%;
            transform: translateY(-100%);
            right: 30rpx;

            .level-badge {
                background: #FFE8CD;
                padding: 8rpx 24rpx;
                border-radius: 32rpx;

                .level-text {
                    font-size: 20rpx;
                    color: #F9A23F;

                    image {
                        width: 16rpx;
                        height: 13rpx;
                    }
                }
            }
        }
    }

    .stats-card {
        width: 85%;
        margin: -140rpx auto 20rpx;
        background: #fff;
        border-radius: 24rpx;
        padding: 32rpx 40rpx;
        display: flex;
        justify-content: space-around;
        box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.12);
        z-index: 2;
        position: relative;

        .stat-item {
            display: flex;
            flex-direction: column;
            align-items: center;
            flex: 1;

            .stat-label {
                font-size: 20rpx;
                color: #333333;
                margin-bottom: 16rpx;
            }

            .stat-value {
                font-weight: bold;
                font-size: 30rpx;
                color: #333333;
            }
        }

        .stat-divider {
            width: 2rpx;
            height: 60rpx;
            background: #E5E5E5;
        }
    }

    .quick-entry {
        background: #fff;
        margin: 20rpx 30rpx;
        border-radius: 16rpx;
        padding: 32rpx;
        display: flex;
        justify-content: space-around;

        .entry-item {
            display: flex;
            flex-direction: column;
            align-items: center;
            position: relative;

            .entry-icon-wrapper {
                position: relative;
                margin-bottom: 12rpx;
            }

            .entry-icon {
                width: 80rpx;
                height: 80rpx;
            }

            .entry-badge {
                position: absolute;
                top: -8rpx;
                right: -8rpx;
                background: #E53935;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                min-width: 32rpx;
                height: 32rpx;
                padding: 0 8rpx;

                &.small {
                    position: static;
                    margin-right: 12rpx;
                }

                .badge-num {
                    font-size: 20rpx;
                    color: #fff;
                }
            }

            .entry-text {
                font-size: 24rpx;
                color: #666;
            }
        }
    }

    .function-list {
        background: #fff;
        margin: 20rpx 30rpx;
        border-radius: 16rpx;
        overflow: hidden;

        .function-item {
            display: flex;
            align-items: center;
            padding: 32rpx;
            border-bottom: 1rpx solid #F0F0F0;

            &:last-child {
                border-bottom: none;
            }

            .func-icon {
                width: 40rpx;
                height: 40rpx;
                margin-right: 24rpx;
            }

            .func-text {
                flex: 1;
                font-size: 28rpx;
                color: #333;
            }

            .arrow-icon {
                width: 32rpx;
                height: 32rpx;
            }
        }
    }
}
</style>
