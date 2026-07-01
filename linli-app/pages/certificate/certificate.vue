<template>
    <view class="page-container">
        <view class="header">
            <view class="back-btn" @click="goBack">
                <text class="iconfont icon-youjiantou back-icon"></text>
            </view>
            <text class="title">认证与证件</text>
            <view class="placeholder"></view>
        </view>

        <view class="tip-bar">
            <text class="tip-text">▲ 温馨提示 请您及时上传身份证信息，以免给您带来不便</text>
        </view>

        <scroll-view class="content-scroll" scroll-y>
            <view class="main-card">
                <view class="top-row">
                    <text class="row-title">实名认证状态</text>
                    <text class="top-status">{{ realNameHeaderText }}</text>
                </view>

                <view class="divider"></view>

                <view class="cert-row">
                    <text class="row-title">身份证信息</text>
                    <view class="row-action" @click="handleRealNameAction">
                        <text class="row-action-text">{{ realNameActionText }}</text>
                        <text class="row-arrow">›</text>
                    </view>
                </view>

                <view class="upload-panel">
                    <view class="upload-box" @click="handleRealNameAction">
                        <text class="upload-plus">+</text>
                        <text class="upload-text">{{ realNameFrontText }}</text>
                    </view>
                    <view class="upload-box" @click="handleRealNameAction">
                        <text class="upload-plus">+</text>
                        <text class="upload-text">{{ realNameBackText }}</text>
                    </view>
                </view>

                <view class="divider"></view>

                <view class="status-line" @click="handleFaceVerifyAction">
                    <view class="line-main">
                        <text class="line-title">人脸识别</text>
                        <text class="line-desc">{{ faceVerifyDesc }}</text>
                    </view>
                    <view class="row-action">
                        <text class="line-status">{{ faceVerifyStatusText }}</text>
                        <text class="row-arrow">›</text>
                    </view>
                </view>

                <view class="divider"></view>

                <view class="status-line">
                    <view class="line-main">
                        <text class="line-title">手机号绑定</text>
                        <text class="line-desc">{{ maskedMobile }}</text>
                    </view>
                    <view class="row-action" @click="handleMobileRebind">
                        <text class="row-action-text">换绑</text>
                        <text class="row-arrow">›</text>
                    </view>
                </view>
            </view>

            <view class="main-card optional-card">
                <view class="optional-head">
                    <text class="row-title">可选认证（提升信用）</text>
                </view>

                <view v-for="item in qualificationItems" :key="item.key" class="optional-row" @click="handleQualificationAction(item)">
                    <view class="optional-left">
                        <view class="optional-icon"></view>
                        <text class="optional-title">{{ item.title }}</text>
                    </view>
                    <view class="optional-right">
                        <text class="optional-status">{{ item.statusText }}</text>
                        <text class="row-arrow">›</text>
                    </view>
                </view>
            </view>
        </scroll-view>
    </view>
</template>

<script>
import { getProfile, getQualificationSummary, getRealNameProgress } from '@/api/member'

export default {
    data() {
        return {
            summary: {},
            progress: {},
            profile: {},
            realNameDetail: {}
        }
    },
    computed: {
        realNameHeaderText() {
            if (this.progress.auditStatus === 'APPROVED') {
                return `${this.realNameMaskedName}（已认证）`
            }
            if (this.progress.auditStatus === 'REJECTED') {
                return '已驳回'
            }
            if (this.progress.auditStatus === 'PENDING') {
                return '审核中'
            }
            return '未认证'
        },
        realNameActionText() {
            if (this.progress.auditStatus === 'APPROVED') return '已认证'
            if (this.progress.auditStatus === 'REJECTED') return '重新提交'
            if (this.progress.auditStatus === 'PENDING') return '去修改'
            return '待上传'
        },
        realNameMaskedName() {
            const name = (this.realNameDetail && this.realNameDetail.realName) || ''
            if (!name) return '**'
            if (name.length === 1) return `${name}*`
            return `${name.charAt(0)}*`
        },
        realNameFrontText() {
            return this.realNameDetail && this.realNameDetail.idCardFrontFileId ? '身份证人像面已上传' : '点击上传人像面'
        },
        realNameBackText() {
            return this.realNameDetail && this.realNameDetail.idCardBackFileId ? '点击上传国徽面' : '点击上传国徽面'
        },
        faceVerifyDesc() {
            if (this.progress.livenessStatus === 'PASS' && this.progress.faceVerifyStatus === 'PASS') {
                return '已完成人活体检测验证'
            }
            if (this.progress.auditStatus === 'REJECTED') {
                return this.progress.rejectReason || '请根据驳回原因重新提交'
            }
            if (this.progress.auditStatus === 'PENDING') {
                return this.progress.currentStepDesc || '平台正在校验中'
            }
            return '待完成人脸核验'
        },
        faceVerifyStatusText() {
            if (this.progress.livenessStatus === 'PASS' && this.progress.faceVerifyStatus === 'PASS') return '已验证'
            if (this.progress.auditStatus === 'REJECTED') return '未通过'
            if (this.progress.auditStatus === 'PENDING') return '审核中'
            return '待验证'
        },
        maskedMobile() {
            const mobile = this.profile.mobile || ''
            if (!mobile || mobile.length < 7) return '未绑定'
            return `${mobile.slice(0, 3)}****${mobile.slice(-4)}`
        },
        qualificationItems() {
            return [
                {
                    key: 'business',
                    title: '营业执照',
                    status: this.summary.businessLicenseAuditStatus
                },
                {
                    key: 'industry',
                    title: '行业资质（电工证等）',
                    status: this.summary.industryQualificationAuditStatus
                }
            ].map((item) => ({
                ...item,
                statusText: this.mapQualificationStatus(item.status)
            }))
        }
    },
    onShow() {
        this.loadPageData()
    },
    methods: {
        async loadPageData() {
            try {
                const [summary, progress, profile, realName] = await Promise.all([
                    getQualificationSummary({ silent: true }).catch(() => ({})),
                    getRealNameProgress({ silent: true }).catch(() => ({})),
                    getProfile({ silent: true }).catch(() => ({})),
                    this.fetchRealNameDetail()
                ])
                this.summary = summary || {}
                this.progress = progress || {}
                this.profile = profile || {}
                this.realNameDetail = realName || {}
            } catch (error) {
            }
        },
        async fetchRealNameDetail() {
            try {
                const { getRealName } = await import('@/api/member')
                return await getRealName({ silent: true }).catch(() => ({}))
            } catch (error) {
                return {}
            }
        },
        mapQualificationStatus(status) {
            if (status === 'APPROVED') return '已认证'
            if (status === 'PENDING') return '审核中'
            if (status === 'REJECTED') return '已驳回'
            return '未上传'
        },
        handleRealNameAction() {
            if (this.progress.auditStatus === 'APPROVED') {
                uni.showModal({
                    title: '实名认证',
                    content: '实名认证已通过，当前仅支持查看，不支持直接修改。',
                    showCancel: false
                })
                return
            }
            uni.navigateTo({
                url: '/pages/certificate/realname_edit'
            })
        },
        handleQualificationAction(item) {
            if (!item) return
            if (item.status === 'APPROVED') {
                uni.showModal({
                    title: item.title,
                    content: '当前资质已认证，暂不支持直接修改。',
                    showCancel: false
                })
                return
            }
            uni.navigateTo({
                url: `/pages/certificate/qualification_edit?type=${item.key}`
            })
        },
        handleFaceVerifyAction() {
            if (this.progress.auditStatus === 'APPROVED') {
                uni.showModal({
                    title: '人脸识别',
                    content: '当前人脸识别结果来自实名认证核验，暂不支持单独发起或修改。',
                    showCancel: false
                })
                return
            }
            uni.showModal({
                title: '人脸识别',
                content: '当前人脸识别需跟随实名认证资料一并提交，暂不支持单独操作，是否前往实名认证？',
                success: (res) => {
                    if (!res.confirm) return
                    uni.navigateTo({
                        url: '/pages/certificate/realname_edit'
                    })
                }
            })
        },
        handleMobileRebind() {
            uni.showModal({
                title: '手机号换绑',
                content: '当前版本暂未开放手机号换绑接口，请联系客服协助处理。',
                showCancel: false
            })
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
    background: #F5F7FB;
    display: flex;
    flex-direction: column;
}

.header {
    background: #FFFFFF;
    padding: 60rpx 30rpx 26rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-shrink: 0;
}

.back-btn,
.placeholder {
    width: 60rpx;
}

.back-icon {
    font-size: 40rpx;
    color: #333333;
    transform: rotate(180deg);
}

.title {
    font-size: 34rpx;
    font-weight: 500;
    color: #333333;
}

.tip-bar {
    height: 48rpx;
    padding: 0 24rpx;
    background: #FFF6E9;
    display: flex;
    align-items: center;
    flex-shrink: 0;
    overflow: hidden;
}

.tip-text {
    font-size: 20rpx;
    color: #F2A141;
    white-space: nowrap;
}

.content-scroll {
    flex: 1;
    padding: 22rpx 24rpx 40rpx;
    box-sizing: border-box;
}

.main-card {
    background: #FFFFFF;
    border-radius: 18rpx;
    box-shadow: 0 4rpx 18rpx rgba(0, 0, 0, 0.05);
    padding: 0 24rpx;
    margin-bottom: 20rpx;
}

.top-row,
.cert-row,
.status-line,
.optional-row {
    min-height: 92rpx;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 20rpx;
}

.row-title,
.line-title {
    font-size: 30rpx;
    color: #333333;
}

.top-status {
    font-size: 22rpx;
    color: #999999;
}

.divider {
    height: 1rpx;
    background: #F0F0F0;
}

.row-action,
.optional-right {
    display: flex;
    align-items: center;
    gap: 6rpx;
    flex-shrink: 0;
}

.row-action-text,
.optional-status {
    font-size: 24rpx;
    color: #F2A141;
}

.row-arrow {
    font-size: 28rpx;
    color: #D7B07D;
    line-height: 1;
}

.upload-panel {
    padding: 24rpx 0;
    display: flex;
    gap: 18rpx;
}

.upload-box {
    flex: 1;
    height: 154rpx;
    background: #DCEBFF;
    border-radius: 10rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
}

.upload-plus {
    width: 34rpx;
    height: 34rpx;
    border-radius: 50%;
    background: #2E83F0;
    color: #FFFFFF;
    font-size: 28rpx;
    line-height: 34rpx;
    text-align: center;
}

.upload-text {
    margin-top: 16rpx;
    font-size: 22rpx;
    color: #4B8CDE;
}

.line-main {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
}

.line-desc {
    margin-top: 8rpx;
    font-size: 22rpx;
    color: #999999;
}

.line-status {
    font-size: 24rpx;
    color: #999999;
    flex-shrink: 0;
}

.optional-card {
    padding-top: 6rpx;
    padding-bottom: 6rpx;
}

.optional-head {
    min-height: 86rpx;
    display: flex;
    align-items: center;
}

.optional-left {
    flex: 1;
    min-width: 0;
    display: flex;
    align-items: center;
}

.optional-icon {
    width: 28rpx;
    height: 20rpx;
    border: 2rpx solid #6BA6F6;
    border-radius: 4rpx;
    margin-right: 16rpx;
    position: relative;
}

.optional-icon::after {
    content: '';
    position: absolute;
    left: 6rpx;
    top: 4rpx;
    width: 12rpx;
    height: 8rpx;
    border: 2rpx solid #6BA6F6;
    border-radius: 2rpx;
}

.optional-title {
    font-size: 28rpx;
    color: #333333;
}
</style>
