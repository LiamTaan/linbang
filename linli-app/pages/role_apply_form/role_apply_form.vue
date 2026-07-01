<template>
    <view class="page-container">
        <view class="header">
            <view class="back-btn" @click="goBack">
                <text class="iconfont icon-youjiantou back-icon"></text>
            </view>
            <text class="title">{{ pageTitle }}</text>
            <view class="placeholder"></view>
        </view>

        <scroll-view class="content-scroll" scroll-y>
            <view class="hero-card">
                <text class="hero-title">{{ roleMeta.name }}</text>
                <text class="hero-desc">{{ roleMeta.desc }}</text>
                <view class="tag-row">
                    <text v-for="item in roleMeta.features" :key="item" class="tag-item">{{ item }}</text>
                </view>
            </view>

            <view v-if="latestRejectedReason" class="notice-card">
                <text class="notice-title">上次申请未通过</text>
                <text class="notice-desc">驳回原因：{{ latestRejectedReason }}</text>
            </view>

            <view class="form-card">
                <text class="section-title">申请资料</text>

                <view class="field-item">
                    <text class="field-label">申请原因</text>
                    <textarea
                        v-model="form.applyReason"
                        class="field-textarea"
                        maxlength="255"
                        placeholder="请简要说明你为什么适合申请这个身份" />
                </view>

                <view class="field-item">
                    <text class="field-label">资源说明</text>
                    <textarea
                        v-model="form.resourceDesc"
                        class="field-textarea"
                        maxlength="500"
                        :placeholder="resourcePlaceholder" />
                </view>

                <view v-if="roleCode === 'PROMOTER'" class="field-item">
                    <text class="field-label">预期转化说明</text>
                    <textarea
                        v-model="form.expectedConversionDesc"
                        class="field-textarea"
                        maxlength="500"
                        placeholder="例如：预计首月带来 20 个有效线索" />
                </view>
            </view>

            <view class="submit-area">
                <view class="submit-btn" :class="{ disabled: submitting }" @click="handleSubmit">
                    <text class="submit-text">{{ submitButtonText }}</text>
                </view>
            </view>

            <view class="bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
import { createRoleApply, getRoleApplyPage } from '@/api/member'

const ROLE_META = {
    PROMOTER: {
        name: '推广员申请',
        desc: '填写推广资源和转化预期，审核通过后自动开通推广员身份。',
        features: ['推广海报', '推广数据', '佣金结算']
    },
    PARTNER: {
        name: '区域合作商申请',
        desc: '补充本地资源和协同能力，提交后等待平台审核。',
        features: ['辖区查看', '入驻初审', '纠纷协调']
    }
}

function createDefaultForm() {
    return {
        applyReason: '',
        resourceDesc: '',
        expectedConversionDesc: ''
    }
}

export default {
    data() {
        return {
            roleCode: 'PROMOTER',
            form: createDefaultForm(),
            latestRejectedReason: '',
            latestApply: null,
            submitting: false
        }
    },
    computed: {
        roleMeta() {
            return ROLE_META[this.roleCode] || ROLE_META.PROMOTER
        },
        pageTitle() {
            return this.roleMeta.name
        },
        submitButtonText() {
            return this.latestApply ? '重新提交申请' : '提交申请'
        },
        resourcePlaceholder() {
            return this.roleCode === 'PROMOTER'
                ? '例如：社区群、线下门店、私域流量、地推点位'
                : '例如：本地社区资源、商户资源、运营协同资源'
        }
    },
    onLoad(options) {
        if (options.roleCode && ROLE_META[options.roleCode]) {
            this.roleCode = options.roleCode
        }
    },
    onShow() {
        this.loadLatestApply()
    },
    methods: {
        async loadLatestApply() {
            try {
                const page = await getRoleApplyPage({ pageNo: 1, pageSize: 20 })
                const matchedList = ((page && page.list) || []).filter((item) => item.applyRoleCode === this.roleCode)
                const latestApply = matchedList.length ? matchedList[0] : null
                this.latestApply = latestApply
                if (latestApply && latestApply.auditStatus === 'REJECTED') {
                    this.form = {
                        applyReason: latestApply.applyReason || '',
                        resourceDesc: latestApply.resourceDesc || '',
                        expectedConversionDesc: latestApply.expectedConversionDesc || ''
                    }
                    this.latestRejectedReason = latestApply.rejectReason || ''
                    return
                }
                this.form = createDefaultForm()
                this.latestRejectedReason = ''
            } catch (error) {
            }
        },
        validateForm() {
            if (!this.form.applyReason.trim()) {
                return '请填写申请原因'
            }
            if (!this.form.resourceDesc.trim()) {
                return '请填写资源说明'
            }
            if (this.roleCode === 'PROMOTER' && !this.form.expectedConversionDesc.trim()) {
                return '请填写预期转化说明'
            }
            return ''
        },
        async handleSubmit() {
            if (this.submitting) {
                return
            }
            const errorText = this.validateForm()
            if (errorText) {
                uni.showToast({
                    title: errorText,
                    icon: 'none'
                })
                return
            }
            const payload = {
                applyRoleCode: this.roleCode,
                applyReason: this.form.applyReason.trim(),
                resourceDesc: this.form.resourceDesc.trim(),
                expectedConversionDesc: this.roleCode === 'PROMOTER' ? this.form.expectedConversionDesc.trim() : '',
                abilityDesc: this.form.resourceDesc.trim(),
                availableTimeDesc: '可按需配合平台安排'
            }
            try {
                this.submitting = true
                await createRoleApply(payload)
                uni.showToast({
                    title: '申请已提交',
                    icon: 'success'
                })
                setTimeout(() => {
                    uni.navigateBack()
                }, 500)
            } catch (error) {
            } finally {
                this.submitting = false
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
    background: linear-gradient(180deg, #eef4ff 0, #f7f9fc 220rpx, #f5f7fb 100%);
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

.hero-card,
.notice-card,
.form-card {
    background: #fff;
    border-radius: 24rpx;
    padding: 24rpx;
    box-shadow: 0 14rpx 30rpx rgba(15, 47, 90, 0.06);
    margin-bottom: 20rpx;
}

.hero-card {
    background: linear-gradient(135deg, #edf5ff, #ffffff);
}

.hero-title {
    display: block;
    font-size: 34rpx;
    color: #1c2d4a;
    font-weight: bold;
    margin-bottom: 12rpx;
}

.hero-desc,
.notice-desc {
    display: block;
    font-size: 22rpx;
    color: #6f7b8f;
    line-height: 1.7;
}

.tag-row {
    display: flex;
    flex-wrap: wrap;
    gap: 12rpx;
    margin-top: 18rpx;
}

.tag-item {
    padding: 10rpx 16rpx;
    border-radius: 999rpx;
    background: #f0f5fb;
    font-size: 20rpx;
    color: #4b5f7b;
}

.notice-card {
    background: #fff8ef;
}

.notice-title {
    display: block;
    font-size: 24rpx;
    font-weight: 700;
    color: #b97312;
    margin-bottom: 10rpx;
}

.section-title {
    display: block;
    font-size: 28rpx;
    color: #1c2d4a;
    font-weight: 700;
    margin-bottom: 20rpx;
}

.field-item {
    margin-bottom: 20rpx;
}

.field-item:last-child {
    margin-bottom: 0;
}

.field-label {
    display: block;
    font-size: 22rpx;
    color: #5f6d81;
    margin-bottom: 10rpx;
}

.field-textarea {
    width: 100%;
    min-height: 180rpx;
    box-sizing: border-box;
    background: #f7f9fc;
    border-radius: 18rpx;
    padding: 20rpx 18rpx;
    font-size: 26rpx;
    color: #183153;
}

.submit-area {
    padding-top: 10rpx;
}

.submit-btn {
    height: 88rpx;
    border-radius: 18rpx;
    background: linear-gradient(135deg, #4a90f0, #2e83f0);
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 14rpx 28rpx rgba(46, 131, 240, 0.25);
}

.submit-btn.disabled {
    background: #c9d5e6;
    box-shadow: none;
}

.submit-text {
    color: #fff;
    font-size: 30rpx;
    font-weight: bold;
}

.bottom-space {
    height: 60rpx;
}
</style>
