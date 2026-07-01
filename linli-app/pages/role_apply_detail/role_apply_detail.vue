<template>
    <view class="page-container">
        <view class="header">
            <view class="back-btn" @click="goBack">
                <text class="iconfont icon-youjiantou back-icon"></text>
            </view>
            <text class="title">申请详情</text>
            <view class="placeholder"></view>
        </view>

        <scroll-view class="content-scroll" scroll-y>
            <view class="summary-card">
                <view class="summary-top">
                    <view>
                        <text class="summary-role">{{ roleName }}</text>
                        <text class="summary-no">申请单号：{{ detail.applyNo || '--' }}</text>
                    </view>
                    <view class="status-pill" :class="`status-${detail.auditStatus || 'PENDING'}`">
                        <text class="status-text">{{ statusLabel }}</text>
                    </view>
                </view>
                <text class="summary-line">当前节点：{{ detail.currentNodeName || '--' }}</text>
                <text class="summary-line">提交时间：{{ formatTime(detail.createTime) }}</text>
                <text v-if="detail.auditTime" class="summary-line">审核时间：{{ formatTime(detail.auditTime) }}</text>
                <text v-if="detail.auditRemark" class="summary-line">审核备注：{{ detail.auditRemark }}</text>
                <text v-if="detail.rejectReason" class="summary-line danger">驳回原因：{{ detail.rejectReason }}</text>
            </view>

            <view class="card">
                <text class="card-title">流程进度</text>
                <view
                    v-for="(node, index) in detail.processNodes || []"
                    :key="`${node.nodeCode}-${index}`"
                    class="timeline-item">
                    <view class="timeline-left">
                        <view class="timeline-dot" :class="`dot-${node.nodeStatus || 'PENDING'}`"></view>
                        <view v-if="index !== (detail.processNodes || []).length - 1" class="timeline-line"></view>
                    </view>
                    <view class="timeline-content">
                        <view class="timeline-header">
                            <text class="timeline-name">{{ node.nodeName }}</text>
                            <text class="timeline-status" :class="`text-${node.nodeStatus || 'PENDING'}`">{{ nodeStatusLabel(node.nodeStatus) }}</text>
                        </view>
                        <text v-if="node.nodeRemark" class="timeline-remark">{{ node.nodeRemark }}</text>
                        <text v-if="node.nodeTime" class="timeline-time">{{ formatTime(node.nodeTime) }}</text>
                    </view>
                </view>
            </view>

            <view class="card">
                <text class="card-title">申请资料</text>
                <text class="info-item">申请原因：{{ detail.applyReason || '--' }}</text>
                <text class="info-item">资源说明：{{ detail.resourceDesc || '--' }}</text>
                <text v-if="detail.expectedConversionDesc" class="info-item">预期转化：{{ detail.expectedConversionDesc }}</text>
                <text v-if="detail.abilityDesc" class="info-item">能力说明：{{ detail.abilityDesc }}</text>
                <text v-if="detail.availableTimeDesc" class="info-item">可投入时间：{{ detail.availableTimeDesc }}</text>
            </view>

            <view class="bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
import { getRoleApply } from '@/api/member'
import { getRoleApplyName, getRoleApplyStatusLabel } from '@/utils/linbang'

export default {
    data() {
        return {
            id: 0,
            detail: {}
        }
    },
    computed: {
        roleName() {
            return getRoleApplyName(this.detail.applyRoleCode)
        },
        statusLabel() {
            return getRoleApplyStatusLabel(this.detail.auditStatus)
        }
    },
    onLoad(options) {
        this.id = Number(options.id || 0)
    },
    onShow() {
        this.loadDetail()
    },
    methods: {
        async loadDetail() {
            if (!this.id) {
                uni.showToast({
                    title: '申请记录不存在',
                    icon: 'none'
                })
                return
            }
            try {
                this.detail = await getRoleApply(this.id)
            } catch (error) {
            }
        },
        formatTime(value) {
            return value ? this.$fmt.formatDateTime(value) : '--'
        },
        nodeStatusLabel(status) {
            const labels = {
                DONE: '已完成',
                CURRENT: '处理中',
                PENDING: '待处理',
                REJECTED: '已驳回'
            }
            return labels[status] || status || '--'
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

.summary-card,
.card {
    background: #fff;
    border-radius: 24rpx;
    padding: 24rpx;
    box-shadow: 0 14rpx 30rpx rgba(15, 47, 90, 0.06);
    margin-bottom: 20rpx;
}

.summary-top {
    display: flex;
    justify-content: space-between;
    gap: 18rpx;
    margin-bottom: 18rpx;
}

.summary-role {
    display: block;
    font-size: 34rpx;
    font-weight: bold;
    color: #1c2d4a;
    margin-bottom: 10rpx;
}

.summary-no,
.summary-line,
.timeline-remark,
.timeline-time,
.info-item {
    display: block;
    font-size: 22rpx;
    color: #6f7b8f;
    line-height: 1.7;
}

.summary-line.danger {
    color: #d9534f;
}

.status-pill {
    align-self: flex-start;
    padding: 10rpx 18rpx;
    border-radius: 999rpx;
    background: #eef4ff;
}

.status-text {
    font-size: 22rpx;
    color: #2e83f0;
    font-weight: 600;
}

.status-APPROVED {
    background: #eaf8f0;
}

.status-APPROVED .status-text {
    color: #1f8a57;
}

.status-REJECTED {
    background: #fdecec;
}

.status-REJECTED .status-text {
    color: #d9534f;
}

.card-title {
    display: block;
    font-size: 28rpx;
    color: #1c2d4a;
    font-weight: 700;
    margin-bottom: 20rpx;
}

.timeline-item {
    display: flex;
    gap: 18rpx;
}

.timeline-left {
    width: 24rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.timeline-dot {
    width: 20rpx;
    height: 20rpx;
    border-radius: 50%;
    background: #d6deea;
    margin-top: 8rpx;
    flex-shrink: 0;
}

.dot-DONE {
    background: #2e83f0;
}

.dot-CURRENT {
    background: #f39b25;
}

.dot-REJECTED {
    background: #d9534f;
}

.timeline-line {
    width: 2rpx;
    flex: 1;
    background: #e3eaf3;
    margin-top: 8rpx;
}

.timeline-content {
    flex: 1;
    padding-bottom: 26rpx;
}

.timeline-header {
    display: flex;
    justify-content: space-between;
    gap: 12rpx;
    margin-bottom: 8rpx;
}

.timeline-name {
    font-size: 26rpx;
    color: #24324b;
    font-weight: 600;
}

.timeline-status {
    font-size: 22rpx;
}

.text-DONE {
    color: #2e83f0;
}

.text-CURRENT {
    color: #f39b25;
}

.text-REJECTED {
    color: #d9534f;
}

.text-PENDING {
    color: #9aa6b7;
}

.bottom-space {
    height: 40rpx;
}
</style>
