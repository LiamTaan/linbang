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
            <text class="tip-text">即将到期资质 {{ summary.expiringQualificationCount || 0 }} 个，请及时处理</text>
        </view>

        <scroll-view class="content-scroll" scroll-y>
            <view class="cert-card">
                <view class="cert-item">
                    <text class="item-title">实名认证状态</text>
                    <text class="status-text verified">{{ progress.auditStatus || summary.realNameAuditStatus || '--' }}</text>
                </view>
                <view class="cert-item">
                    <text class="item-title">活体检测</text>
                    <text class="status-text verified">{{ progress.livenessStatus || '--' }}</text>
                </view>
                <view class="cert-item">
                    <text class="item-title">人脸核验</text>
                    <text class="status-text verified">{{ progress.faceVerifyStatus || '--' }}</text>
                </view>
                <view class="cert-item">
                    <text class="item-title">微信实名关联</text>
                    <text class="status-text">{{ progress.wechatRealNameStatus || summary.wechatRealNameStatus || '--' }}</text>
                </view>
                <view class="cert-item">
                    <text class="item-title">支付宝实名关联</text>
                    <text class="status-text">{{ progress.alipayRealNameStatus || summary.alipayRealNameStatus || '--' }}</text>
                </view>
                <text v-if="progress.currentStepDesc" class="progress-text">{{ progress.currentStepDesc }}</text>
                <text v-if="progress.rejectReason" class="progress-text">驳回原因：{{ progress.rejectReason }}</text>
            </view>

            <view class="cert-card">
                <view class="card-header">
                    <text class="header-title">可选认证（提升信用）</text>
                </view>
                <view class="cert-item">
                    <text class="item-title">营业执照</text>
                    <text class="status-text">{{ summary.businessLicenseAuditStatus || '--' }}</text>
                </view>
                <view class="cert-item">
                    <text class="item-title">行业资质</text>
                    <text class="status-text">{{ summary.industryQualificationAuditStatus || '--' }}</text>
                </view>
                <view class="cert-item">
                    <text class="item-title">保险保单</text>
                    <text class="status-text">{{ summary.insuranceAuditStatus || '--' }}</text>
                </view>
            </view>

            <view class="cert-card">
                <view class="card-header">
                    <text class="header-title">证件提醒</text>
                </view>
                <text v-for="item in reminders" :key="item.qualificationId || item.messageRecordId" class="reminder-text">
                    {{ item.reminderTitle }}：{{ item.reminderContent }}
                </text>
                <text v-if="!reminders.length" class="reminder-text">暂无证件提醒</text>
            </view>
        </scroll-view>
    </view>
</template>

<script>
import { getQualificationSummary, getRealNameProgress } from '@/api/member'

export default {
    data() {
        return {
            summary: {},
            progress: {}
        }
    },
    computed: {
        reminders() {
            return this.summary.reminders || []
        }
    },
    onShow() {
        this.loadPageData()
    },
    methods: {
        async loadPageData() {
            try {
                const [summary, progress] = await Promise.all([
                    getQualificationSummary(),
                    getRealNameProgress().catch(() => ({}))
                ])
                this.summary = summary || {}
                this.progress = progress || {}
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
    background: #F5F5F5;
}

.header {
    background: #fff;
    padding: 60rpx 30rpx 30rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.back-icon {
    font-size: 40rpx;
    color: #333;
    transform: rotate(180deg);
}

.tip-bar {
    background: #FFF8E1;
    padding: 20rpx 30rpx;
}

.tip-text {
    font-size: 24rpx;
    color: #FA9D3B;
}

.content-scroll {
    padding: 20rpx;
    box-sizing: border-box;
}

.cert-card {
    background: #fff;
    border-radius: 16rpx;
    margin-bottom: 20rpx;
    overflow: hidden;
    padding: 24rpx;
}

.card-header {
    margin-bottom: 16rpx;
}

.header-title,
.item-title {
    font-size: 28rpx;
    color: #333;
}

.cert-item {
    padding: 20rpx 0;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1rpx solid #F0F0F0;

    &:last-child {
        border-bottom: none;
    }
}

.status-text,
.progress-text,
.reminder-text {
    display: block;
    font-size: 24rpx;
    color: #666;
    line-height: 1.7;
}
</style>
