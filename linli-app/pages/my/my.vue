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
                        <image class="level-icon" src="/static/img/my/level@3x.png" />
                        <text class="level-text">{{ creditLevelLabel }}</text>
                    </view>
                </view>
            </view>

            <view class="stats-card">
                <template v-for="(item, index) in summaryItems" :key="item.label">
                    <view
                        class="stat-item"
                        @click="handleSummaryItem(item)">
                        <text class="stat-label">{{ item.label }}</text>
                        <text class="stat-value">{{ item.value }}</text>
                    </view>
                    <view
                        v-if="index < summaryItems.length - 1"
                        class="stat-divider"></view>
                </template>
            </view>
        </view>

        <view class="quick-entry">
            <view v-for="item in quickEntries" :key="item.key" class="entry-item" @click="handleQuickEntry(item)">
                <view class="entry-icon-wrapper">
                    <image class="entry-icon" :src="item.icon" />
                    <view v-if="item.badge > 0" class="entry-badge">
                        <text class="badge-num">{{ item.badge }}</text>
                    </view>
                </view>
                <text class="entry-text">{{ item.label }}</text>
            </view>
        </view>

        <view v-for="(group, groupIndex) in functionGroups" :key="`group-${groupIndex}`" class="function-list">
            <view v-for="item in group" :key="item.key" class="function-item" @click="handleFunctionItem(item)">
                <image class="func-icon" :src="item.icon" />
                <text class="func-text">{{ item.label }}</text>
                <view class="entry-badge small" v-if="item.badge > 0">
                    <text class="badge-num">{{ item.badge }}</text>
                </view>
                <image class="arrow-icon" src="/static/img/my/switch@3x.png" />
            </view>
        </view>

        <tab-bar :current-index="3"></tab-bar>
    </view>
</template>

<script>
import tabBar from '@/components/tabBar/tabBar.vue'
import { switchRole, getProfile, getQualificationPage, getRoleContext } from '@/api/member'
import { getUnreadCount } from '@/api/message'
import { getAcceptOrderPage, getOrderPage } from '@/api/order'
import { getMerchantAcceptStatus, getMerchantOnboardingProgress } from '@/api/merchant'
import { getPromoteCenter, getTeamStats } from '@/api/promote'
import { getPartnerWorkbench } from '@/api/partner'
import { getPendingReviewUnits } from '@/api/review'
import { getWalletAccount } from '@/api/wallet'
import { hasLogin } from '@/utils/auth'
import { getCreditLevelLabel } from '@/utils/linbang'

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
            teamStats: {},
            merchantAcceptStatus: {},
            merchantProgress: {},
            partnerWorkbench: {},
            qualificationCertified: false,
            unreadCount: 0,
            isLoggedIn: false,
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
            return this.profile.avatar || '/static/img/login/anonymity.png'
        },
        creditLevelLabel() {
            return getCreditLevelLabel(this.profile.creditLevel)
        },
        currentRoleName() {
            return this.roleContext.currentRoleName || this.profile.currentRoleName || '普通用户'
        },
        currentRoleCode() {
            return this.roleContext.currentRoleCode || this.profile.currentRoleCode || 'USER'
        },
        switchableRoles() {
            return (this.roleContext.roleSummaries || []).filter((item) => item.switchable)
        },
        summaryItems() {
            if (this.currentRoleCode === 'MERCHANT') {
                return [
                    {
                        label: '待接需求',
                        value: `${this.orderStats.pending} 单`,
                        action: () => this.goToOrderTab('accept')
                    },
                    {
                        label: '接单状态',
                        value: this.merchantAcceptStatus.acceptStatus === 'ENABLE' ? '接单中' : '已暂停',
                        action: () => this.navigateTo('/pages/order_receiving_status/order_receiving_status')
                    },
                    {
                        label: '账户余额',
                        value: `¥${this.$fmt.formatMoney(this.wallet.availableAmount)}`,
                        action: () => this.goToWallet()
                    }
                ]
            }
            if (this.currentRoleCode === 'PROMOTER') {
                return [
                    {
                        label: '直推人数',
                        value: `${this.promote.bindUserCount || 0} 人`,
                        action: () => this.navigateTo('/pages/promotion_center/promotion_center')
                    },
                    {
                        label: '累计收益',
                        value: `¥${this.$fmt.formatMoney(this.promote.totalCommissionAmount)}`,
                        action: () => this.navigateTo('/pages/promotion_center/promotion_center')
                    },
                    {
                        label: '团队人数',
                        value: `${(this.teamStats.firstLevelUserCount || 0) + (this.teamStats.secondLevelUserCount || 0)} 人`,
                        action: () => this.navigateTo('/pages/promotion_center/promotion_center')
                    }
                ]
            }
            if (this.currentRoleCode === 'PARTNER') {
                const summary = this.partnerWorkbench.summary || {}
                return [
                    {
                        label: '待初审入驻',
                        value: `${summary.pendingEntryAuditCount || this.partnerWorkbench.pendingEntryAuditCount || 0} 单`,
                        action: () => this.navigateTo('/pages/regional_partner/regional_partner')
                    },
                    {
                        label: '待处理纠纷',
                        value: `${summary.pendingComplaintCount || this.partnerWorkbench.pendingComplaintCount || 0} 单`,
                        action: () => this.navigateTo('/pages/regional_partner/regional_partner')
                    },
                    {
                        label: '辖区成交额',
                        value: `¥${this.$fmt.formatMoney(summary.tradeAmount || this.partnerWorkbench.tradeAmount)}`,
                        action: () => this.navigateTo('/pages/regional_partner/regional_partner')
                    }
                ]
            }
            return [
                {
                    label: '总订单',
                    value: `${this.orderStats.total} 单`,
                    action: () => this.goToOrderTab('my')
                },
                {
                    label: '账户余额',
                    value: `¥${this.$fmt.formatMoney(this.wallet.availableAmount)}`,
                    action: () => this.goToWallet()
                },
                {
                    label: '推广收益',
                    value: `¥${this.$fmt.formatMoney(this.promote.totalCommissionAmount)}`,
                    action: () => this.navigateTo('/pages/promotion_center/promotion_center')
                }
            ]
        },
        quickEntries() {
            if (this.currentRoleCode === 'MERCHANT') {
                return [
                    { key: 'merchant_accept', label: '待接需求', badge: this.orderStats.pending, icon: '/static/img/my/pending-order@3x.png' },
                    { key: 'merchant_status', label: '接单状态', badge: 0, icon: '/static/img/my/task@3x.png' },
                    { key: 'merchant_entry', label: '入驻进度', badge: 0, icon: '/static/img/my/review@3x.png' },
                    { key: 'merchant_service', label: '服务设置', badge: 0, icon: '/static/img/my/after-sales@3x.png' }
                ]
            }
            if (this.currentRoleCode === 'PROMOTER') {
                return [
                    { key: 'promote_center', label: '推广中心', badge: 0, icon: '/static/img/my/pending-order@3x.png' },
                    { key: 'promote_team', label: '团队数据', badge: 0, icon: '/static/img/my/task@3x.png' },
                    { key: 'wallet', label: '佣金提现', badge: 0, icon: '/static/img/my/review@3x.png' },
                    { key: 'reward', label: '我的悬赏', badge: 0, icon: '/static/img/my/after-sales@3x.png' }
                ]
            }
            if (this.currentRoleCode === 'PARTNER') {
                const summary = this.partnerWorkbench.summary || {}
                return [
                    { key: 'partner_workbench', label: '合作商工作台', badge: 0, icon: '/static/img/my/pending-order@3x.png' },
                    { key: 'partner_entry_audit', label: '入驻初审', badge: Number(summary.pendingEntryAuditCount || this.partnerWorkbench.pendingEntryAuditCount || 0), icon: '/static/img/my/task@3x.png' },
                    { key: 'partner_dispute', label: '纠纷协调', badge: Number(summary.pendingComplaintCount || this.partnerWorkbench.pendingComplaintCount || 0), icon: '/static/img/my/review@3x.png' },
                    { key: 'partner_price_report', label: '价格申报', badge: Number(summary.pendingPriceReportCount || this.partnerWorkbench.pendingPriceReportCount || 0), icon: '/static/img/my/after-sales@3x.png' }
                ]
            }
            return [
                { key: 'user_wait_accept', label: '待接单', badge: this.orderStats.pending, icon: '/static/img/my/pending-order@3x.png' },
                { key: 'user_serving', label: '服务中', badge: this.orderStats.serving, icon: '/static/img/my/task@3x.png' },
                { key: 'user_review', label: '待评价', badge: this.orderStats.review, icon: '/static/img/my/review@3x.png' },
                { key: 'user_after_sale', label: '退款售后', badge: this.orderStats.afterSale, icon: '/static/img/my/after-sales@3x.png' }
            ]
        },
        functionGroups() {
            const commonGroup = [
                { key: 'wallet', label: '账户管理', icon: '/static/img/my/account-management@3x.png' },
                { key: 'certificate', label: '认证与证件', icon: '/static/img/my/verification@3x.png' },
                { key: 'identity', label: '身份申请', icon: '/static/img/my/identity@3x.png' }
            ]
            const supportGroup = [
                { key: 'notice', label: '闹钟提醒', icon: '/static/img/my/alarm@3x.png', badge: this.unreadCount },
                { key: 'settings', label: '系统设置', icon: '/static/img/my/settings@3x.png' },
                { key: 'feedback', label: '帮助与反馈', icon: '/static/img/my/help-feedback@3x.png' },
                { key: 'service', label: '我的客服', icon: '/static/img/my/help-feedback@3x.png' }
            ]
            if (this.currentRoleCode === 'MERCHANT') {
                return [
                    [
                        { key: 'merchant_accept_status', label: '接单状态管理', icon: '/static/img/my/order-management@3x.png' },
                        { key: 'merchant_order_hall', label: '待接需求大厅', icon: '/static/img/my/account-management@3x.png' },
                        { key: 'merchant_entry_progress', label: '服务商入驻进度', icon: '/static/img/my/verification@3x.png' }
                    ],
                    commonGroup,
                    [
                        { key: 'benefit', label: '我的权益', icon: '/static/img/my/rights@3x.png' },
                        { key: 'reward', label: '我的悬赏', icon: '/static/img/my/reward@3x.png' },
                        { key: 'promote_center', label: '推广中心', icon: '/static/img/my/reward@3x.png' }
                    ],
                    supportGroup
                ]
            }
            if (this.currentRoleCode === 'PROMOTER') {
                return [
                    [
                        { key: 'promote_center', label: '推广中心', icon: '/static/img/my/order-management@3x.png' },
                        { key: 'benefit', label: '我的权益', icon: '/static/img/my/rights@3x.png' },
                        { key: 'reward', label: '我的悬赏', icon: '/static/img/my/reward@3x.png' }
                    ],
                    commonGroup,
                    supportGroup
                ]
            }
            if (this.currentRoleCode === 'PARTNER') {
                return [
                    [
                        { key: 'partner_workbench', label: '区域合作商工作台', icon: '/static/img/my/order-management@3x.png' },
                        { key: 'promote_center', label: '推广中心', icon: '/static/img/my/reward@3x.png' },
                        { key: 'benefit', label: '我的权益', icon: '/static/img/my/rights@3x.png' }
                    ],
                    commonGroup,
                    supportGroup
                ]
            }
            return [
                [
                    { key: 'user_orders', label: '订单管理', icon: '/static/img/my/order-management@3x.png' },
                    { key: 'wallet', label: '账户管理', icon: '/static/img/my/account-management@3x.png' },
                    { key: 'certificate', label: '认证与证件', icon: '/static/img/my/verification@3x.png' }
                ],
                [
                    { key: 'identity', label: '身份申请', icon: '/static/img/my/identity@3x.png' },
                    { key: 'benefit', label: '我的权益', icon: '/static/img/my/rights@3x.png' },
                    { key: 'reward', label: '我的悬赏', icon: '/static/img/my/reward@3x.png' },
                    { key: 'notice', label: '闹钟提醒', icon: '/static/img/my/alarm@3x.png', badge: this.unreadCount }
                ],
                [
                    { key: 'promote_center', label: '推广中心', icon: '/static/img/my/reward@3x.png' },
                    { key: 'settings', label: '系统设置', icon: '/static/img/my/settings@3x.png' },
                    { key: 'feedback', label: '帮助与反馈', icon: '/static/img/my/help-feedback@3x.png' },
                    { key: 'service', label: '我的客服', icon: '/static/img/my/help-feedback@3x.png' }
                ]
            ]
        },
        verifyTags() {
            const tags = []
            if (this.profile.realNameStatus === 'APPROVED') {
                tags.push('已实名')
            }
            if (this.qualificationCertified) {
                tags.push('已资质认证')
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
            this.isLoggedIn = hasLogin()
            if (!this.isLoggedIn) {
                this.profile = {}
                this.roleContext = {}
                this.wallet = {}
                this.promote = {}
                this.teamStats = {}
                this.merchantAcceptStatus = {}
                this.merchantProgress = {}
                this.partnerWorkbench = {}
                this.qualificationCertified = false
                this.unreadCount = 0
                this.orderStats = {
                    total: 0,
                    pending: 0,
                    serving: 0,
                    review: 0,
                    afterSale: 0
                }
                return
            }
            try {
                const [profile, roleContext, wallet, unreadCount, merchantProgress, qualificationPage] = await Promise.all([
                    getProfile({ silent: true }),
                    getRoleContext({ silent: true }),
                    getWalletAccount({ silent: true }),
                    getUnreadCount({ silent: true }).catch(() => 0),
                    getMerchantOnboardingProgress({ silent: true }).catch(() => ({})),
                    getQualificationPage({ pageNo: 1, pageSize: 1 }, { silent: true }).catch(() => ({ total: 0, list: [] }))
                ])
                this.profile = profile || {}
                this.roleContext = roleContext || {}
                this.wallet = wallet || {}
                this.merchantProgress = merchantProgress || {}
                this.qualificationCertified = Number((qualificationPage && qualificationPage.total) || 0) > 0
                    || (((qualificationPage && qualificationPage.list) || []).length > 0)
                this.unreadCount = Number(unreadCount || 0)
                this.promote = {}
                this.teamStats = {}
                this.merchantAcceptStatus = {}
                this.partnerWorkbench = {}
                this.orderStats = { total: 0, pending: 0, serving: 0, review: 0, afterSale: 0 }

                if (this.currentRoleCode === 'MERCHANT') {
                    const [acceptPage, merchantAcceptStatus] = await Promise.all([
                        getAcceptOrderPage({ pageNo: 1, pageSize: 1 }, { silent: true }).catch(() => ({ total: 0, list: [] })),
                        getMerchantAcceptStatus().catch(() => ({}))
                    ])
                    this.merchantAcceptStatus = merchantAcceptStatus || {}
                    this.orderStats.pending = Number((acceptPage && acceptPage.total) || 0)
                    return
                }

                if (this.currentRoleCode === 'PROMOTER') {
                    const [promote, teamStats] = await Promise.all([
                        getPromoteCenter({ silent: true }).catch(() => ({})),
                        getTeamStats().catch(() => ({}))
                    ])
                    this.promote = promote || {}
                    this.teamStats = teamStats || {}
                    return
                }

                if (this.currentRoleCode === 'PARTNER') {
                    this.partnerWorkbench = await getPartnerWorkbench().catch(() => ({}))
                    return
                }

                const [orderTotal, waitAcceptPage, servingPage, afterSalePage, pendingReview] = await Promise.all([
                    getOrderPage({ pageNo: 1, pageSize: 1 }, { silent: true }).catch(() => ({ total: 0, list: [] })),
                    getOrderPage({ pageNo: 1, pageSize: 1, businessCategory: 'WAIT_ACCEPT' }, { silent: true }).catch(() => ({ total: 0, list: [] })),
                    getOrderPage({ pageNo: 1, pageSize: 1, businessCategory: 'IN_SERVICE' }, { silent: true }).catch(() => ({ total: 0, list: [] })),
                    getOrderPage({ pageNo: 1, pageSize: 1, businessCategory: 'AFTER_SALE' }, { silent: true }).catch(() => ({ total: 0, list: [] })),
                    getPendingReviewUnits({ silent: true }).catch(() => [])
                ])
                this.promote = ((roleContext && roleContext.enabledRoleCodes) || []).includes('PROMOTER')
                    ? await getPromoteCenter({ silent: true }).catch(() => ({}))
                    : {}
                this.orderStats = {
                    total: Number(orderTotal.total || 0),
                    pending: Number(waitAcceptPage.total || 0),
                    serving: Number(servingPage.total || 0),
                    review: Array.isArray(pendingReview) ? pendingReview.length : Number((pendingReview && pendingReview.total) || 0),
                    afterSale: Number(afterSalePage.total || 0)
                }
            } catch (error) {
            }
        },
        handleSummaryItem(item) {
            if (item && typeof item.action === 'function') {
                item.action()
            }
        },
        handleQuickEntry(item) {
            if (!item) {
                return
            }
            const actionMap = {
                user_wait_accept: () => this.goToOrderTab('my'),
                user_serving: () => this.goToOrderTab('serving'),
                user_review: () => this.navigateTo('/pages/evaluation_service/evaluation_service'),
                user_after_sale: () => this.navigateTo('/pages/refund/refund'),
                merchant_accept: () => this.goToOrderTab('accept'),
                merchant_status: () => this.navigateTo('/pages/order_receiving_status/order_receiving_status'),
                merchant_entry: () => this.navigateTo('/pages/merchant_entry/merchant_entry'),
                merchant_service: () => this.navigateTo('/pages/order_receiving_status/order_receiving_status'),
                promote_center: () => this.navigateTo('/pages/promotion_center/promotion_center'),
                promote_team: () => this.navigateTo('/pages/promotion_center/promotion_center'),
                wallet: () => this.goToWallet(),
                reward: () => this.navigateTo('/pages/my_reward/my_reward'),
                partner_workbench: () => this.navigateTo('/pages/regional_partner/regional_partner'),
                partner_entry_audit: () => this.navigateTo('/pages/regional_partner/regional_partner'),
                partner_dispute: () => this.navigateTo('/pages/regional_partner/regional_partner'),
                partner_price_report: () => this.navigateTo('/pages/regional_partner/regional_partner')
            }
            const action = actionMap[item.key]
            if (action) {
                action()
            }
        },
        handleFunctionItem(item) {
            if (!item) {
                return
            }
            const actionMap = {
                user_orders: () => this.goToOrderTab('my'),
                wallet: () => this.goToWallet(),
                certificate: () => this.navigateTo('/pages/certificate/certificate'),
                identity: () => this.navigateTo('/pages/identity_application/identity_application'),
                benefit: () => this.navigateTo('/pages/my_credit/my_credit?mode=benefit'),
                reward: () => this.navigateTo('/pages/my_reward/my_reward'),
                notice: () => this.goToSystemNotice(),
                promote_center: () => this.navigateTo('/pages/promotion_center/promotion_center'),
                settings: () => this.navigateTo('/pages/set/set'),
                feedback: () => this.navigateTo('/pages/feedback/feedback'),
                service: () => this.navigateTo('/pages/feedback/feedback?mode=service'),
                merchant_accept_status: () => this.navigateTo('/pages/order_receiving_status/order_receiving_status'),
                merchant_order_hall: () => this.goToOrderTab('accept'),
                merchant_entry_progress: () => this.navigateTo('/pages/merchant_entry/merchant_entry'),
                partner_workbench: () => this.navigateTo('/pages/regional_partner/regional_partner')
            }
            const action = actionMap[item.key]
            if (action) {
                action()
            }
        },
        getMerchantProgressLabel(status) {
            const labels = {
                PENDING_FIRST_AUDIT: '待初审',
                PENDING_FINAL_AUDIT: '待终审',
                APPROVED_WAIT_BANK_CARD: '待绑卡',
                APPROVED_ENABLED: '已开通',
                FIRST_APPROVED: '初审通过',
                APPROVED: '终审通过',
                REJECTED: '已驳回'
            }
            return labels[status] || '审核中'
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
        goToOrderTab(mode) {
            uni.setStorageSync('linbang_order_tab_mode', mode || 'accept')
            uni.switchTab({
                url: '/pages/order/order',
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
        goToSystemNotice() {
            uni.switchTab({
                url: '/pages/news/news',
                success: () => {
                    uni.setStorageSync('linbang_news_category', 'SYSTEM')
                },
                fail: () => {
                    uni.showToast({
                        title: '页面暂不可达',
                        icon: 'none'
                    })
                }
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
            padding-right: 132rpx;

            .avatar {
                width: 120rpx;
                height: 120rpx;
                border-radius: 50%;
                border: 4rpx solid rgba(255, 255, 255, 0.3);
                margin-right: 24rpx;
            }

            .user-detail {
                flex: 1;
                min-width: 0;

                .user-name {
                    display: flex;
                    align-items: center;
                    flex-wrap: nowrap;
                    gap: 10rpx;
                    margin-bottom: 12rpx;
                    min-width: 0;

                    .name {
                        flex: 1;
                        min-width: 0;
                        font-weight: bold;
                        font-size: 30rpx;
                        color: #FFFFFF;
                        overflow: hidden;
                        text-overflow: ellipsis;
                        white-space: nowrap;
                    }

                    .service-badge {
                        flex-shrink: 0;
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
            top: 72rpx;
            right: 30rpx;

            .level-badge {
                background: #FFE8CD;
                min-width: 88rpx;
                height: 44rpx;
                padding: 0 18rpx;
                border-radius: 32rpx;
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 6rpx;

                .level-icon {
                    width: 16rpx;
                    height: 13rpx;
                    display: block;
                    flex-shrink: 0;
                }

                .level-text {
                    font-size: 20rpx;
                    line-height: 20rpx;
                    color: #F9A23F;
                    white-space: nowrap;
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
            padding: 30rpx 18rpx 28rpx;
            display: flex;
            justify-content: space-between;

            .entry-item {
                width: 25%;
                display: flex;
                flex-direction: column;
                align-items: center;
                position: relative;

                .entry-icon-wrapper {
                    position: relative;
                    width: 92rpx;
                    height: 92rpx;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    margin-bottom: 10rpx;
                }

                .entry-icon {
                    width: 80rpx;
                    height: 80rpx;
                    display: block;
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
                line-height: 32rpx;
                text-align: center;
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
