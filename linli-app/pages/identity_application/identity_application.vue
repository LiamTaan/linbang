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
                    v-if="role.code === 'MERCHANT' && (merchantEntry || merchantProgress)"
                    class="role-progress"
                    @click="handleRoleAction(role)">
                    <view class="progress-left">
                        <text class="progress-title">申请进度</text>
                        <text class="progress-sub">{{ merchantProgressText }}</text>
                        <text v-if="merchantProgressTime" class="progress-time">{{ $fmt.formatDateTime(merchantProgressTime) }}</text>
                    </view>
                    <text class="progress-link">查看 ></text>
                </view>
            </view>

            <view class="bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
import { createRoleApply, getRoleApplyPage, getRoleContext } from '@/api/member'
import { getMerchantEntry, getMerchantOnboardingProgress } from '@/api/merchant'
import { getRoleApplyName, getRoleApplyStatusLabel } from '@/utils/linbang'

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

function promptInput(title, placeholder = '') {
    return new Promise((resolve) => {
        uni.showModal({
            title,
            editable: true,
            placeholderText: placeholder,
            success: (res) => resolve(res.confirm ? (res.content || '') : '')
        })
    })
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
        visibleRoleCards() {
            const summaries = this.roleContext.roleSummaries || []
            return ROLE_ORDER.map((code) => {
                const meta = ROLE_META[code]
                const summary = summaries.find((item) => item.roleCode === code) || {}
                const roleStatus = code === 'MERCHANT'
                    ? this.merchantStatusKey
                    : (summary.roleStatus || 'AVAILABLE')
                let buttonText = '去申请'
                let buttonClass = 'primary'
                if (code === 'MERCHANT') {
                    if (roleStatus === 'REJECTED') {
                        buttonText = '重新申请'
                    } else if (roleStatus === 'APPROVED_ENABLED') {
                        buttonText = '已开通'
                    } else if (roleStatus === 'APPROVED_WAIT_BANK_CARD') {
                        buttonText = '去绑卡'
                    } else if (roleStatus === 'PENDING' || roleStatus === 'PENDING_FIRST_AUDIT' || roleStatus === 'PENDING_FINAL_AUDIT') {
                        buttonText = '已申请'
                    } else {
                        buttonText = '去申请'
                    }
                    buttonClass = 'outline'
                } else if (roleStatus === 'PENDING') {
                    buttonText = '审核中'
                    buttonClass = 'disabled'
                } else if (roleStatus === 'ENABLED') {
                    buttonText = '已开通'
                    buttonClass = 'disabled'
                }
                return {
                    code,
                    name: meta.name,
                    desc: meta.desc,
                    icon: meta.icon,
                    features: meta.features,
                    roleStatus,
                    statusLabel: code === 'MERCHANT'
                        ? (MERCHANT_STATUS_LABELS[roleStatus] || roleStatus)
                        : getRoleApplyStatusLabel(roleStatus),
                    buttonText,
                    buttonClass
                }
            })
        }
    },
    onShow() {
        this.loadPageData()
    },
    methods: {
        getRoleApplyName,
        getRoleApplyStatusLabel,
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
        async handleRoleAction(role) {
            if (!role) {
                return
            }
            if (role.code === 'MERCHANT') {
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
                return
            }
            if (role.roleStatus === 'PENDING' || role.roleStatus === 'ENABLED') {
                uni.showToast({
                    title: role.roleStatus === 'PENDING' ? '当前状态审核中' : '该身份已开通',
                    icon: 'none'
                })
                return
            }
            await this.handleApply(role)
        },
        async handleApply(role) {
            try {
                const applyReason = await promptInput(`${role.name}申请说明`, '请简要说明申请原因')
                if (!applyReason) {
                    return
                }
                const payload = {
                    applyRoleCode: role.code,
                    applyReason
                }
                if (role.code === 'PROMOTER') {
                    payload.resourceDesc = await promptInput('资源说明', '请填写可投入资源')
                    if (!payload.resourceDesc) {
                        return
                    }
                    payload.expectedConversionDesc = await promptInput('预期转化说明', '例如首月转化 20 个线索')
                    if (!payload.expectedConversionDesc) {
                        return
                    }
                    payload.abilityDesc = payload.resourceDesc
                    payload.availableTimeDesc = '可按需配合平台安排'
                } else if (role.code === 'PARTNER') {
                    payload.resourceDesc = await promptInput('资源说明', '请填写可投入资源')
                    if (!payload.resourceDesc) {
                        return
                    }
                    payload.abilityDesc = payload.resourceDesc
                    payload.availableTimeDesc = '可按需配合平台安排'
                } else {
                    uni.showToast({
                        title: '当前角色暂不支持申请',
                        icon: 'none'
                    })
                    return
                }
                await createRoleApply(payload)
                uni.showToast({
                    title: '申请已提交',
                    icon: 'success'
                })
                this.loadPageData()
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
    background: #f5f7fb;
}

.header {
    background: #fff;
    padding: 60rpx 30rpx 26rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.back-icon {
    font-size: 40rpx;
    color: #333;
    transform: rotate(180deg);
}

.title {
    font-size: 32rpx;
    color: #222;
    font-weight: 500;
}

.content-scroll {
    padding: 22rpx 24rpx 30rpx;
    box-sizing: border-box;
}

.current-card {
    border: 1px solid #4d91f7;
    border-radius: 18rpx;
    background: #edf5ff;
    padding: 24rpx 22rpx;
    margin-bottom: 22rpx;
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
    gap: 10rpx;
}

.current-label {
    font-size: 20rpx;
    color: #6c87a8;
}

.current-role-row {
    display: flex;
    align-items: center;
    gap: 12rpx;
}

.current-icon {
    width: 34rpx;
    height: 34rpx;
    border-radius: 50%;
    background: #4d91f7;
    display: flex;
    align-items: center;
    justify-content: center;
}

.current-icon-text {
    color: #fff;
    font-size: 20rpx;
    line-height: 1;
}

.current-name {
    font-size: 32rpx;
    font-weight: bold;
    color: #1f68d4;
}

.current-hint {
    font-size: 20rpx;
    color: #6c87a8;
    white-space: nowrap;
}

.section-title {
    margin: 18rpx 0 14rpx;
}

.title-text {
    font-size: 24rpx;
    color: #333;
    font-weight: bold;
}

.role-card {
    background: #fff;
    border-radius: 18rpx;
    padding: 22rpx;
    margin-bottom: 18rpx;
    box-shadow: 0 6rpx 20rpx rgba(15, 47, 90, 0.06);
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
    color: #f39b25;
}

.role-promoter .role-title {
    color: #2ab05d;
}

.role-partner .role-title {
    color: #8f3df2;
}

.role-icon {
    width: 32rpx;
    height: 32rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    background: #f4f7fb;
}

.role-icon-merchant {
    background: #fff3e6;
}

.role-icon-promoter {
    background: #eef9f0;
}

.role-icon-partner {
    background: #f3ebff;
}

.role-icon-text {
    font-size: 20rpx;
    line-height: 1;
}

.role-icon-merchant .role-icon-text {
    color: #f39b25;
}

.role-icon-promoter .role-icon-text {
    color: #2ab05d;
}

.role-icon-partner .role-icon-text {
    color: #8f3df2;
}

.role-title {
    font-size: 30rpx;
    font-weight: bold;
}

.role-desc {
    display: block;
    font-size: 22rpx;
    color: #818b9a;
    line-height: 1.6;
}

.role-merchant .role-desc {
    color: #7f6d55;
}

.role-promoter .role-desc {
    color: #567d5f;
}

.role-partner .role-desc {
    color: #6c5a89;
}

.role-btn {
    min-width: 128rpx;
    height: 54rpx;
    border-radius: 10rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 14rpx;
    margin-top: 8rpx;
}

.role-btn.primary {
    background: #2e83f0;
}

.role-btn.outline {
    background: #fff;
    border: 2rpx solid #4d91f7;
}

.role-btn.disabled {
    background: #e9eff7;
    border: 2rpx solid #d8e2ee;
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
    margin-top: 14rpx;
    display: flex;
    flex-direction: column;
    gap: 6rpx;
}

.role-feature {
    font-size: 20rpx;
    color: #818b9a;
    line-height: 1.5;
}

.role-progress {
    margin-top: 16rpx;
    padding-top: 16rpx;
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
}

.progress-title {
    font-size: 22rpx;
    color: #333;
    font-weight: bold;
}

.progress-sub,
.progress-time {
    font-size: 20rpx;
    color: #9099a8;
}

.progress-link {
    font-size: 20rpx;
    color: #4d91f7;
    white-space: nowrap;
}

.bottom-space {
    height: 60rpx;
}
</style>
