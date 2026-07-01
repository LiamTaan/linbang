<template>
    <view class="page-container">
        <view class="header">
            <view class="back-btn" @click="goBack">
                <text class="iconfont icon-youjiantou back-icon"></text>
            </view>
            <text class="title">身份申请</text>
            <view class="placeholder"></view>
        </view>

        <scroll-view class="content-scroll" scroll-y>
            <view class="current-card">
                <view class="current-card-inner">
                    <view class="current-meta">
                        <text class="current-label">当前身份</text>
                        <view class="current-role-row">
                            <view class="current-icon">
                                <text class="current-icon-text">👤</text>
                            </view>
                            <text class="current-name">{{ currentRoleName }}</text>
                        </view>
                    </view>
                    <text class="current-hint">{{ currentRoleHint }}</text>
                </view>
            </view>

            <view class="section-title">
                <text class="title-text">可申请身份</text>
            </view>

            <view
                v-for="role in visibleRoleCards"
                :key="role.code"
                class="role-card"
                :class="`role-${role.code.toLowerCase()}`">
                <view class="role-card-header">
                    <view class="role-title-wrap">
                        <view class="role-name-row">
                            <view class="role-icon" :class="`role-icon-${role.code.toLowerCase()}`">
                                <text class="role-icon-text">{{ role.icon }}</text>
                            </view>
                            <text class="role-title">{{ role.name }}</text>
                        </view>
                        <text class="role-desc">{{ role.desc }}</text>
                    </view>
                    <view
                        class="role-btn"
                        :class="role.buttonClass"
                        @click="handleRoleAction(role)">
                        <text class="role-btn-text">{{ role.buttonText }}</text>
                    </view>
                </view>

                <view class="role-feature-list">
                    <text v-for="item in role.features" :key="item" class="role-feature">• {{ item }}</text>
                </view>

                <view
                    v-if="role.showProgress"
                    class="role-progress"
                    @click="handleViewProgress(role)">
                    <view class="progress-left">
                        <text class="progress-title">申请进度</text>
                        <text class="progress-sub">{{ role.progressTitle }}</text>
                        <text v-if="role.progressTime" class="progress-time">{{ $fmt.formatDateTime(role.progressTime) }}</text>
                    </view>
                    <text class="progress-link">查看 ></text>
                </view>

            </view>

            <view class="bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
import { getRoleApplyPage, getRoleContext } from '@/api/member'
import { getMerchantEntry, getMerchantOnboardingProgress } from '@/api/merchant'
import { getRoleApplyStatusLabel } from '@/utils/linbang'

const ROLE_META = {
    MERCHANT: {
        name: '服务商',
        desc: '入驻后可接单赚钱，需完成实名认证、入驻审核和银行卡绑定。',
        icon: '👤',
        features: ['抢单接单', '服务入驻', '提现结算']
    },
    PROMOTER: {
        name: '推广员',
        desc: '分享推广码，赚取推广佣金。',
        icon: '★',
        features: ['推广海报', '推广数据', '佣金结算']
    },
    PARTNER: {
        name: '区域合作商',
        desc: '负责辖区运营、初审和纠纷协调。',
        icon: '☰',
        features: ['辖区查看', '入驻初审', '纠纷协调']
    }
}

const ROLE_ORDER = ['MERCHANT', 'PROMOTER', 'PARTNER']

const MERCHANT_STATUS_LABELS = {
    PENDING_FIRST_AUDIT: '待初审',
    PENDING_FINAL_AUDIT: '待终审',
    APPROVED_WAIT_BANK_CARD: '待绑卡',
    APPROVED_ENABLED: '已开通',
    FIRST_APPROVED: '初审通过',
    APPROVED: '终审通过',
    REJECTED: '已驳回',
    PENDING: '已申请'
}

export default {
    data() {
        return {
            roleContext: {},
            merchantEntry: null,
            merchantProgress: null,
            applications: []
        }
    },
    computed: {
        currentRoleName() {
            return this.roleContext.currentRoleName || '普通用户'
        },
        currentRoleHint() {
            const roleCode = this.roleContext.currentRoleCode || 'USER'
            const hints = {
                USER: '可发单·不可接单',
                MERCHANT: '可接单·可履约',
                PROMOTER: '可推广·可赚佣',
                PARTNER: '可辖区管理·可协调',
                PLATFORM_OPERATOR: '管理端权限，请使用管理后台'
            }
            return hints[roleCode] || '待完善身份'
        },
        merchantProgressText() {
            return (this.merchantProgress && this.merchantProgress.currentStageName)
                || (this.merchantEntry && this.merchantEntry.currentStageName)
                || '待提交申请'
        },
        merchantProgressTime() {
            return (this.merchantProgress && this.merchantProgress.currentStageTime)
                || (this.merchantEntry && this.merchantEntry.currentStageTime)
                || ''
        },
        merchantStatusKey() {
            return (this.merchantProgress && this.merchantProgress.progressStatus)
                || (this.merchantEntry && this.merchantEntry.progressStatus)
                || (this.merchantEntry && this.merchantEntry.status)
                || (this.merchantEntry ? 'PENDING' : 'AVAILABLE')
        },
        latestRoleApplications() {
            return (this.applications || []).reduce((acc, item) => {
                if (!item || !item.applyRoleCode) {
                    return acc
                }
                const current = acc[item.applyRoleCode]
                if (!current || Number(item.id || 0) > Number(current.id || 0)) {
                    acc[item.applyRoleCode] = item
                }
                return acc
            }, {})
        },
        visibleRoleCards() {
            const summaries = this.roleContext.roleSummaries || []
            return ROLE_ORDER.map((code) => {
                const meta = ROLE_META[code]
                const summary = summaries.find((item) => item.roleCode === code) || {}
                const latestApply = this.latestRoleApplications[code] || null
                const roleStatus = code === 'MERCHANT'
                    ? this.merchantStatusKey
                    : this.resolveRoleStatus(summary, latestApply)
                const buttonMeta = this.resolveButtonMeta(code, roleStatus)
                return {
                    code,
                    name: meta.name,
                    desc: meta.desc,
                    icon: meta.icon,
                    features: meta.features,
                    latestApply,
                    roleStatus,
                    buttonText: buttonMeta.text,
                    buttonClass: buttonMeta.className,
                    showProgress: code === 'MERCHANT' ? !!(this.merchantEntry || this.merchantProgress) : !!latestApply,
                    progressTitle: code === 'MERCHANT'
                        ? this.merchantProgressText
                        : this.resolveRoleProgressTitle(latestApply),
                    progressTime: code === 'MERCHANT'
                        ? this.merchantProgressTime
                        : ((latestApply && (latestApply.auditTime || latestApply.updateTime || latestApply.createTime)) || ''),
                    statusLabel: code === 'MERCHANT'
                        ? (MERCHANT_STATUS_LABELS[roleStatus] || roleStatus)
                        : getRoleApplyStatusLabel(roleStatus)
                }
            })
        }
    },
    onShow() {
        this.loadPageData()
    },
    methods: {
        async loadPageData() {
            try {
                const [roleContext, page, merchantEntry, merchantProgress] = await Promise.all([
                    getRoleContext({ silent: true }).catch(() => ({})),
                    getRoleApplyPage({ pageNo: 1, pageSize: 20 }).catch(() => ({ list: [] })),
                    getMerchantEntry({ silent: true }).catch(() => null),
                    getMerchantOnboardingProgress({ silent: true }).catch(() => null)
                ])
                this.roleContext = roleContext || {}
                this.applications = page.list || []
                this.merchantEntry = merchantEntry || null
                this.merchantProgress = merchantProgress || null
            } catch (error) {
            }
        },
        resolveRoleStatus(summary, latestApply) {
            if (summary.roleStatus && summary.roleStatus !== 'AVAILABLE') {
                return summary.roleStatus
            }
            if (latestApply && latestApply.auditStatus) {
                return latestApply.auditStatus
            }
            return 'AVAILABLE'
        },
        resolveButtonMeta(code, roleStatus) {
            if (code === 'MERCHANT') {
                if (roleStatus === 'REJECTED') {
                    return { text: '重新申请', className: 'outline' }
                }
                if (roleStatus === 'APPROVED_ENABLED') {
                    return { text: '已开通', className: 'disabled' }
                }
                if (roleStatus === 'APPROVED_WAIT_BANK_CARD') {
                    return { text: '去绑卡', className: 'primary' }
                }
                if (roleStatus === 'PENDING' || roleStatus === 'PENDING_FIRST_AUDIT' || roleStatus === 'PENDING_FINAL_AUDIT' || roleStatus === 'FIRST_APPROVED' || roleStatus === 'APPROVED') {
                    return { text: '查看进度', className: 'outline' }
                }
                return { text: '去申请', className: 'primary' }
            }
            if (roleStatus === 'PENDING') {
                return { text: '查看进度', className: 'outline' }
            }
            if (roleStatus === 'ENABLED' || roleStatus === 'APPROVED') {
                return { text: '已开通', className: 'disabled' }
            }
            if (roleStatus === 'REJECTED') {
                return { text: '重新申请', className: 'outline' }
            }
            return { text: '去申请', className: 'primary' }
        },
        resolveRoleProgressTitle(latestApply) {
            if (!latestApply) {
                return '待提交申请'
            }
            if (latestApply.auditStatus === 'REJECTED' && latestApply.rejectReason) {
                return `已驳回：${latestApply.rejectReason}`
            }
            return `${this.getRoleApplyStatusLabel(latestApply.auditStatus)} · ${latestApply.currentNodeName || '平台审核中'}`
        },
        async handleRoleAction(role) {
            if (!role) {
                return
            }
            if (role.code === 'MERCHANT') {
                this.handleMerchantAction(role)
                return
            }
            if (role.roleStatus === 'PENDING' && role.latestApply) {
                this.handleViewProgress(role)
                return
            }
            if (role.roleStatus === 'ENABLED' || role.roleStatus === 'APPROVED') {
                uni.showToast({
                    title: '该身份已开通',
                    icon: 'none'
                })
                return
            }
            uni.navigateTo({
                url: `/pages/role_apply_form/role_apply_form?roleCode=${role.code}`
            })
        },
        handleMerchantAction(role) {
            if (role.roleStatus === 'APPROVED_ENABLED') {
                uni.navigateTo({
                    url: '/pages/order/order?mode=accept'
                })
                return
            }
            if (role.roleStatus === 'APPROVED_WAIT_BANK_CARD') {
                uni.navigateTo({
                    url: '/pages/bank_card_management/bank_card_management'
                })
                return
            }
            uni.navigateTo({
                url: '/pages/merchant_entry/merchant_entry'
            })
        },
        handleViewProgress(role) {
            if (!role) {
                return
            }
            if (role.code === 'MERCHANT') {
                uni.navigateTo({
                    url: '/pages/merchant_entry/merchant_entry'
                })
                return
            }
            if (!role.latestApply || !role.latestApply.id) {
                uni.showToast({
                    title: '暂无申请记录',
                    icon: 'none'
                })
                return
            }
            uni.navigateTo({
                url: `/pages/role_apply_detail/role_apply_detail?id=${role.latestApply.id}`
            })
        },
        getRoleApplyStatusLabel,
        goBack() {
            uni.navigateBack()
        }
    }
}
</script>

<style lang="scss" scoped>
.page-container {
    min-height: 100vh;
    background: linear-gradient(180deg, #eef4ff 0, #f6f8fc 220rpx, #f5f7fb 100%);
}

.header {
    background: rgba(255, 255, 255, 0.92);
    backdrop-filter: blur(8px);
    padding: 60rpx 30rpx 26rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.back-btn,
.placeholder {
    width: 48rpx;
}

.back-icon {
    font-size: 40rpx;
    color: #333;
    transform: rotate(180deg);
}

.title {
    font-size: 32rpx;
    color: #18243d;
    font-weight: 600;
}

.content-scroll {
    padding: 24rpx;
    box-sizing: border-box;
}

.current-card {
    border-radius: 28rpx;
    background: linear-gradient(135deg, #ebf4ff, #f8fbff);
    border: 2rpx solid rgba(77, 145, 247, 0.28);
    padding: 26rpx 24rpx;
    margin-bottom: 24rpx;
    box-shadow: 0 16rpx 34rpx rgba(63, 116, 196, 0.08);
}

.current-card-inner {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 20rpx;
}

.current-meta {
    display: flex;
    flex-direction: column;
    gap: 12rpx;
}

.current-label {
    font-size: 22rpx;
    color: #6c87a8;
}

.current-role-row {
    display: flex;
    align-items: center;
    gap: 14rpx;
}

.current-icon {
    width: 40rpx;
    height: 40rpx;
    border-radius: 50%;
    background: linear-gradient(135deg, #5d98f7, #2e83f0);
    display: flex;
    align-items: center;
    justify-content: center;
}

.current-icon-text {
    color: #fff;
    font-size: 22rpx;
    line-height: 1;
}

.current-name {
    font-size: 34rpx;
    font-weight: bold;
    color: #1f68d4;
}

.current-hint {
    font-size: 22rpx;
    color: #6c87a8;
    white-space: nowrap;
}

.section-title {
    margin: 12rpx 0 16rpx;
}

.title-text {
    font-size: 26rpx;
    color: #24324b;
    font-weight: bold;
}

.role-card {
    background: rgba(255, 255, 255, 0.98);
    border-radius: 26rpx;
    padding: 26rpx 24rpx;
    margin-bottom: 20rpx;
    box-shadow: 0 14rpx 34rpx rgba(15, 47, 90, 0.06);
}

.role-card-header {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 20rpx;
}

.role-title-wrap {
    flex: 1;
}

.role-name-row {
    display: flex;
    align-items: center;
    gap: 12rpx;
    margin-bottom: 10rpx;
}

.role-merchant .role-title {
    color: #eb8b12;
}

.role-promoter .role-title {
    color: #1ea65d;
}

.role-partner .role-title {
    color: #8a35f0;
}

.role-icon {
    width: 38rpx;
    height: 38rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
}

.role-icon-merchant {
    background: #fff3e6;
}

.role-icon-promoter {
    background: #eefaf2;
}

.role-icon-partner {
    background: #f4ecff;
}

.role-icon-text {
    font-size: 22rpx;
    line-height: 1;
}

.role-title {
    font-size: 30rpx;
    font-weight: bold;
}

.role-desc {
    display: block;
    font-size: 22rpx;
    color: #6f7b8f;
    line-height: 1.7;
}

.role-btn {
    min-width: 136rpx;
    height: 60rpx;
    border-radius: 16rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 18rpx;
    margin-top: 8rpx;
}

.role-btn.primary {
    background: linear-gradient(135deg, #4a90f0, #2e83f0);
    box-shadow: 0 10rpx 20rpx rgba(46, 131, 240, 0.22);
}

.role-btn.outline {
    background: #f8fbff;
    border: 2rpx solid #4d91f7;
}

.role-btn.disabled {
    background: #eaf0f7;
    border: 2rpx solid #d9e3ef;
}

.role-btn-text {
    font-size: 24rpx;
    font-weight: bold;
}

.role-btn.primary .role-btn-text {
    color: #fff;
}

.role-btn.outline .role-btn-text {
    color: #4d91f7;
}

.role-btn.disabled .role-btn-text {
    color: #8c9bad;
}

.role-feature-list {
    margin-top: 18rpx;
    display: flex;
    flex-direction: column;
    gap: 8rpx;
}

.role-feature {
    font-size: 22rpx;
    color: #7b8797;
    line-height: 1.6;
}

.role-progress {
    margin-top: 18rpx;
    padding: 20rpx 4rpx 2rpx;
    border-top: 1px solid #eef2f7;
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 20rpx;
}

.progress-left {
    display: flex;
    flex-direction: column;
    gap: 6rpx;
    min-width: 0;
}

.progress-title {
    font-size: 22rpx;
    color: #24324b;
    font-weight: bold;
}

.progress-sub,
.progress-time {
    font-size: 20rpx;
    color: #8d98a8;
    line-height: 1.5;
}

.progress-link {
    font-size: 22rpx;
    color: #4d91f7;
    white-space: nowrap;
}

.bottom-space {
    height: 60rpx;
}
</style>
