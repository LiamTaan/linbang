<template>
    <view class="page-container">
        <view class="header">
            <view class="back-btn" @click="goBack">
                <text class="iconfont icon-youjiantou back-icon"></text>
            </view>
            <text class="title">拆分订单详情</text>
            <view class="placeholder"></view>
        </view>

        <scroll-view class="content-scroll" scroll-y refresher-enabled :refresher-triggered="refreshing"
            @refresherrefresh="handleRefresh">
            <view class="order-info-card">
                <text class="order-no">订单编号：{{ orderDetail.orderNo || '--' }}</text>
                <text class="order-title">{{ orderDetail.requireDesc || '订单详情' }}</text>
                <view class="amount-row">
                    <view class="amount-left">
                        <text class="amount-label">总金额：</text>
                        <text class="amount-value">¥{{ $fmt.formatMoney(orderDetail.orderAmount) }}</text>
                    </view>
                    <text class="split-status">{{ orderStatusText }} · {{ splitStatusText }}</text>
                </view>
                <text class="order-address">{{ orderAddress }}</text>
            </view>

            <view v-for="unit in orderDetail.units || []" :key="unit.id" class="unit-card"
                :class="resolveUnitCardClass(unit.status)">
                <view class="unit-header">
                    <view class="unit-tag">单元 {{ unit.unitSeq || 1 }}</view>
                    <text class="unit-status">{{ getOrderStatusLabel(unit.status) }}</text>
                </view>
                <text class="unit-title">{{ unit.unitTitle || unit.unitNo }}</text>
                <view class="unit-info">
                    <text class="info-item">金额：¥{{ $fmt.formatMoney(unit.unitAmount) }}</text>
                    <text class="info-item">核销：{{ getVerifyStatusLabel(unit.verifyStatus) }}</text>
                    <text class="info-item" v-if="unit.lockReason">锁定原因：{{ unit.lockReason }}</text>
                    <text class="info-item" v-if="unit.finishTime">完成时间：{{ $fmt.formatDateTime(unit.finishTime) }}</text>
                    <text class="info-item" v-else>创建时间：{{ $fmt.formatDateTime(unit.createTime) }}</text>
                </view>
                <view class="unit-actions">
                    <view class="action-btn view-btn" @click="handleViewVoucher(unit)">
                        <text class="btn-text">查看核销码</text>
                    </view>
                    <view class="action-btn upload-btn" @click="handleUploadVoucher(unit)">
                        <text class="btn-text">上传凭证</text>
                    </view>
                    <view class="action-btn confirm-btn" v-if="unit.status === 'ACCEPTED'" @click="handleStartService(unit)">
                        <text class="btn-text">开始服务</text>
                    </view>
                    <view class="action-btn confirm-btn" v-else-if="['SERVING', 'PENDING_CONFIRM'].includes(unit.status)"
                        @click="handleConfirmComplete(unit)">
                        <text class="btn-text">确认完工</text>
                    </view>
                </view>
            </view>

            <view class="rule-card" v-if="orderDetail.flowAdvice || (orderDetail.matchBatches || []).length">
                <text class="rule-title">调度与建议</text>
                <view class="rule-list">
                    <text class="rule-item" v-if="orderDetail.flowAdvice">· {{ orderDetail.flowAdvice }}</text>
                    <text class="rule-item" v-for="item in orderDetail.matchBatches || []" :key="`${item.stageNo}-${item.pushBatchNo}`">
                        · 第 {{ item.stageNo }} 阶段 / 第 {{ item.pushBatchNo }} 批：{{ item.status }}
                    </text>
                </view>
            </view>

            <view class="section-card" v-if="(orderDetail.refunds || []).length">
                <text class="section-title">退款记录</text>
                <view v-for="item in orderDetail.refunds" :key="item.id" class="record-item">
                    <text class="record-title">{{ item.reason }}</text>
                    <text class="record-desc">{{ item.statusName || item.auditStatus }} · {{ $fmt.formatShortDateTime(item.createTime) }}</text>
                </view>
            </view>

            <view class="section-card" v-if="(orderDetail.complaints || []).length">
                <text class="section-title">投诉记录</text>
                <view v-for="item in orderDetail.complaints" :key="item.id" class="record-item">
                    <text class="record-title">{{ item.complaintType }}</text>
                    <text class="record-desc">{{ item.status }} · {{ item.content }}</text>
                </view>
            </view>

            <view class="section-card" v-if="(orderDetail.appeals || []).length">
                <text class="section-title">申诉记录</text>
                <view v-for="item in orderDetail.appeals" :key="item.id" class="record-item">
                    <text class="record-title">{{ item.appealType }}</text>
                    <text class="record-desc">{{ item.status }} · {{ item.content }}</text>
                </view>
            </view>

            <view class="section-card" v-if="(orderDetail.timeline || []).length">
                <text class="section-title">订单时间线</text>
                <view v-for="item in orderDetail.timeline" :key="`${item.timelineType}-${item.bizId}-${item.eventTime}`" class="timeline-item">
                    <text class="timeline-title">{{ item.title }}</text>
                    <text class="timeline-desc">{{ item.content }}</text>
                    <text class="timeline-time">{{ $fmt.formatDateTime(item.eventTime) }}</text>
                </view>
            </view>

            <view class="bottom-actions">
                <view class="bottom-btn contact-btn" @click="handleComplaint">
                    <text class="btn-text">投诉反馈</text>
                </view>
                <view class="bottom-btn refund-btn" @click="handleRefund">
                    <text class="btn-text">申请退款</text>
                </view>
            </view>

            <view class="bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
import { uploadAppFile } from '@/api/infra'
import {
    confirmOrderUnit,
    getOrderDetail,
    getOrderUnitVerifyCode,
    startOrderUnitService,
    uploadDeliveryProof
} from '@/api/order'
import { buildAddressText, extractUploadedFile, getOrderStatusLabel, getVerifyStatusLabel } from '@/utils/linbang'

export default {
    data() {
        return {
            refreshing: false,
            orderId: null,
            orderDetail: {}
        }
    },
    computed: {
        orderStatusText() {
            return getOrderStatusLabel(this.orderDetail.status)
        },
        splitStatusText() {
            return this.orderDetail.splitStatus === 'SPLIT' ? '已拆单' : '未拆单'
        },
        orderAddress() {
            return buildAddressText(this.orderDetail) || '地址待补充'
        }
    },
    onLoad(options) {
        this.orderId = options && options.orderId ? Number(options.orderId) : null
    },
    onShow() {
        this.loadOrderDetail()
    },
    methods: {
        getOrderStatusLabel,
        getVerifyStatusLabel,
        goBack() {
            uni.navigateBack()
        },
        handleRefresh() {
            this.refreshing = true
            this.loadOrderDetail()
        },
        async loadOrderDetail() {
            if (!this.orderId) {
                this.refreshing = false
                return
            }
            try {
                this.orderDetail = await getOrderDetail(this.orderId)
            } catch (error) {
            } finally {
                this.refreshing = false
            }
        },
        resolveUnitCardClass(status) {
            if (status === 'FINISHED') {
                return 'green'
            }
            if (['SERVING', 'PENDING_CONFIRM', 'ACCEPTED'].includes(status)) {
                return 'orange'
            }
            return 'blue'
        },
        async handleViewVoucher(unit) {
            try {
                const verifyResp = await getOrderUnitVerifyCode(unit.id)
                uni.showModal({
                    title: verifyResp.unitNo || unit.unitNo || '核销码',
                    content: [
                        `核销码：${verifyResp.verifyCode || '暂无'}`,
                        `状态：${getVerifyStatusLabel(verifyResp.verifyStatus)}`,
                        verifyResp.verifyRemark || ''
                    ].filter(Boolean).join('\n'),
                    showCancel: false
                })
            } catch (error) {
            }
        },
        async handleUploadVoucher(unit) {
            const chooseImage = () => new Promise((resolve, reject) => {
                uni.chooseImage({
                    count: 6,
                    success: resolve,
                    fail: reject
                })
            })
            try {
                const result = await chooseImage()
                const paths = result.tempFilePaths || []
                const fileIds = []
                for (let i = 0; i < paths.length; i++) {
                    const uploadResp = await uploadAppFile(paths[i])
                    const file = extractUploadedFile(uploadResp, paths[i])
                    if (file.fileId) {
                        fileIds.push(file.fileId)
                    }
                }
                if (!fileIds.length) {
                    return
                }
                await uploadDeliveryProof({
                    unitId: unit.id,
                    proofType: 'DELIVERY_IMAGE',
                    proofDesc: 'App 上传交付凭证',
                    fileIds
                })
                uni.showToast({
                    title: '凭证已上传',
                    icon: 'success'
                })
                this.loadOrderDetail()
            } catch (error) {
            }
        },
        async handleStartService(unit) {
            try {
                await startOrderUnitService({
                    unitId: unit.id,
                    startRemark: 'App 发起开始服务'
                })
                uni.showToast({
                    title: '已开始服务',
                    icon: 'success'
                })
                this.loadOrderDetail()
            } catch (error) {
            }
        },
        async handleConfirmComplete(unit) {
            try {
                await confirmOrderUnit({
                    unitId: unit.id,
                    confirmRemark: 'App 确认完工'
                })
                uni.showToast({
                    title: '已确认完工',
                    icon: 'success'
                })
                this.loadOrderDetail()
            } catch (error) {
            }
        },
        handleRefund() {
            uni.navigateTo({
                url: `/pages/refund/refund?orderId=${this.orderId}`
            })
        },
        handleComplaint() {
            uni.navigateTo({
                url: `/pages/complaint/complaint?orderId=${this.orderId}`
            })
        }
    }
}
</script>

<style lang="scss" scoped>
.page-container {
    min-height: 100vh;
    background: #F5F5F5;

    .header {
        background: #fff;
        padding: 60rpx 30rpx 30rpx;
        display: flex;
        justify-content: space-between;
        align-items: center;
        border-bottom: 1rpx solid #F0F0F0;

        .back-btn,
        .placeholder {
            width: 60rpx;
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
    }

    .content-scroll {
        flex: 1;
        padding: 20rpx;
        box-sizing: border-box;

        .order-info-card,
        .unit-card,
        .rule-card,
        .section-card {
            background: #fff;
            border-radius: 16rpx;
            padding: 24rpx;
            margin-bottom: 16rpx;
        }

        .order-info-card {
            background: #E8F4FD;
        }

        .order-no,
        .order-address,
        .record-desc,
        .timeline-desc,
        .timeline-time {
            font-size: 24rpx;
            color: #666;
            display: block;
        }

        .order-title,
        .section-title,
        .rule-title {
            font-size: 32rpx;
            font-weight: bold;
            color: #333;
            display: block;
            margin-bottom: 16rpx;
        }

        .amount-row,
        .unit-header,
        .unit-actions,
        .bottom-actions {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .amount-left {
            display: flex;
            align-items: baseline;
            gap: 8rpx;
        }

        .amount-label,
        .split-status,
        .info-item,
        .rule-item {
            font-size: 26rpx;
            color: #666;
        }

        .amount-value {
            font-size: 36rpx;
            font-weight: bold;
            color: #E53935;
        }

        .unit-card {
            border: 2rpx solid #D6E4FF;

            &.green {
                border-color: #52C41A;
            }

            &.orange {
                border-color: #FA9D3B;
            }

            .unit-tag {
                padding: 6rpx 16rpx;
                border-radius: 6rpx;
                background: #4A90F0;
                font-size: 22rpx;
                color: #fff;
                font-weight: bold;
            }

            .unit-status,
            .record-title,
            .timeline-title {
                font-size: 26rpx;
                font-weight: bold;
                color: #333;
            }

            .unit-title {
                font-size: 30rpx;
                font-weight: bold;
                color: #333;
                display: block;
                margin: 16rpx 0;
            }

            .unit-info {
                display: flex;
                flex-direction: column;
                gap: 8rpx;
                margin-bottom: 20rpx;
            }

            .action-btn,
            .bottom-btn {
                padding: 16rpx 24rpx;
                border-radius: 8rpx;
                text-align: center;
            }

            .view-btn {
                background: #F5F5F5;
            }

            .upload-btn {
                background: #FFF7E6;
            }

            .confirm-btn {
                background: #4A90F0;
            }

            .btn-text {
                font-size: 24rpx;
                font-weight: bold;
                color: #333;
            }

            .confirm-btn .btn-text {
                color: #fff;
            }
        }

        .rule-list,
        .timeline-item {
            display: flex;
            flex-direction: column;
            gap: 8rpx;
        }

        .record-item,
        .timeline-item {
            padding: 16rpx 0;
            border-bottom: 1rpx solid #F0F0F0;

            &:last-child {
                border-bottom: none;
            }
        }

        .bottom-actions {
            gap: 20rpx;

            .bottom-btn {
                flex: 1;
                border: 2rpx solid #4A90F0;
            }

            .contact-btn {
                background: #fff;
            }

            .refund-btn {
                background: #4A90F0;
            }

            .contact-btn .btn-text {
                color: #4A90F0;
            }

            .refund-btn .btn-text {
                color: #fff;
            }
        }

        .bottom-space {
            height: 60rpx;
        }
    }
}
</style>
