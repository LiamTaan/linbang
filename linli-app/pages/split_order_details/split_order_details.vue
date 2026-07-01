<template>
    <view class="page-container">
        <view class="header">
            <view class="back-btn" @click="goBack">
                <text class="iconfont icon-youjiantou back-icon"></text>
            </view>
            <text class="title">{{ pageTitle }}</text>
            <view class="placeholder"></view>
        </view>

        <scroll-view class="content-scroll" scroll-y :refresher-enabled="!isPreviewMode" :refresher-triggered="refreshing"
            @refresherrefresh="handleRefresh">
            <view v-if="isPreviewMode" class="preview-banner">
                <text class="preview-banner-title">订单预览</text>
                <text class="preview-banner-desc">当前为发布前预览页，请确认金额、协议和拆单信息后再提交。</text>
            </view>

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
                <view class="preview-extra" v-if="isPreviewMode">
                    <text class="preview-extra-item">类目：{{ orderDetail.categoryName || '--' }}</text>
                    <text class="preview-extra-item">计价方式：{{ pricingModeText }}</text>
                    <text class="preview-extra-item" v-if="invoiceReminder">{{ invoiceReminder }}</text>
                    <text class="preview-extra-item" v-if="agreementTitle">协议：{{ agreementTitle }}</text>
                </view>
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
                <view class="unit-actions" v-if="!isPreviewMode">
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

            <view class="bottom-actions" v-if="isPreviewMode">
                <view class="bottom-btn contact-btn" @click="goBack">
                    <text class="btn-text">返回修改</text>
                </view>
                <view class="bottom-btn refund-btn" @click="handlePublishPreview">
                    <text class="btn-text">{{ publishing ? '发布中...' : '确认发布' }}</text>
                </view>
            </view>

            <view class="bottom-actions" v-else>
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
    createOrder,
    confirmOrderUnit,
    getOrderDetail,
    getOrderUnitVerifyCode,
    startOrderUnitService,
    uploadDeliveryProof
} from '@/api/order'
import {
    buildAddressText,
    extractUploadedFile,
    getOrderStatusLabel,
    getPricingModeLabel,
    getVerifyStatusLabel
} from '@/utils/linbang'

const ORDER_PREVIEW_STORAGE_KEY = 'linbang_order_preview_snapshot'

export default {
    data() {
        return {
            refreshing: false,
            orderId: null,
            orderDetail: {},
            isPreviewMode: false,
            previewSnapshot: null,
            publishing: false
        }
    },
    computed: {
        pageTitle() {
            return this.isPreviewMode ? '订单预览' : '拆分订单详情'
        },
        orderStatusText() {
            return getOrderStatusLabel(this.orderDetail.status)
        },
        splitStatusText() {
            return this.orderDetail.splitStatus === 'SPLIT' ? '已拆单' : '未拆单'
        },
        orderAddress() {
            return buildAddressText(this.orderDetail) || '地址待补充'
        },
        pricingModeText() {
            const previewResult = (this.previewSnapshot && this.previewSnapshot.previewResult) || {}
            return previewResult.pricingModeName || getPricingModeLabel(this.orderDetail.pricingMode)
        },
        invoiceReminder() {
            const previewResult = (this.previewSnapshot && this.previewSnapshot.previewResult) || {}
            return previewResult.invoiceImpactReminder || ''
        },
        agreementTitle() {
            const previewResult = (this.previewSnapshot && this.previewSnapshot.previewResult) || {}
            const guaranteeConfig = (this.previewSnapshot && this.previewSnapshot.guaranteeConfig) || {}
            return previewResult.agreementTitle || guaranteeConfig.projectEscrowAgreementTitle || guaranteeConfig.agreementTitle || ''
        }
    },
    onLoad(options) {
        this.isPreviewMode = `${options && options.preview}` === '1'
        this.orderId = options && options.orderId ? Number(options.orderId) : null
        if (this.isPreviewMode) {
            this.previewSnapshot = uni.getStorageSync(ORDER_PREVIEW_STORAGE_KEY) || null
        }
    },
    onShow() {
        if (this.isPreviewMode) {
            this.loadPreviewDetail()
            return
        }
        this.loadOrderDetail()
    },
    methods: {
        getOrderStatusLabel,
        getVerifyStatusLabel,
        goBack() {
            if (this.isPreviewMode && !this.publishing) {
                this.previewSnapshot = uni.getStorageSync(ORDER_PREVIEW_STORAGE_KEY) || this.previewSnapshot
            }
            uni.navigateBack()
        },
        handleRefresh() {
            if (this.isPreviewMode) {
                this.refreshing = false
                return
            }
            this.refreshing = true
            this.loadOrderDetail()
        },
        loadPreviewDetail() {
            const snapshot = this.previewSnapshot || uni.getStorageSync(ORDER_PREVIEW_STORAGE_KEY)
            if (!snapshot || !snapshot.previewResult || !snapshot.payload) {
                uni.showToast({
                    title: '预览信息已失效',
                    icon: 'none'
                })
                setTimeout(() => {
                    uni.navigateBack()
                }, 300)
                return
            }
            this.previewSnapshot = snapshot
            const previewResult = snapshot.previewResult || {}
            const payload = snapshot.payload || {}
            const categoryName = previewResult.categoryName || snapshot.currentCategoryName || '邻里需求'
            const createTime = new Date().toISOString()
            const splitEnabled = !!payload.needSplit || !!previewResult.splitPreview
            this.orderDetail = {
                orderNo: '预览生成后显示',
                requireDesc: payload.requireDesc,
                orderAmount: previewResult.orderAmount || payload.budgetAmount || 0,
                categoryName,
                pricingMode: payload.pricingMode,
                province: payload.province,
                city: payload.city,
                district: payload.district,
                street: payload.street,
                detailAddress: payload.detailAddress,
                status: 'PENDING_PAY',
                splitStatus: splitEnabled ? 'SPLIT' : 'UNSPLIT',
                flowAdvice: previewResult.splitPreview && previewResult.splitPreview.ruleDesc
                    ? previewResult.splitPreview.ruleDesc
                    : '',
                matchBatches: [],
                timeline: [
                    {
                        timelineType: 'PREVIEW',
                        bizId: 0,
                        unitId: 0,
                        title: '发单预览生成',
                        content: '请确认需求描述、金额、协议与拆单结果',
                        status: 'PREVIEW',
                        eventTime: createTime
                    }
                ],
                units: this.buildPreviewUnits(payload, previewResult, createTime)
            }
        },
        buildPreviewUnits(payload, previewResult, createTime) {
            const splitPreview = previewResult.splitPreview || {}
            const splitUnits = Array.isArray(splitPreview.units) ? splitPreview.units : []
            if (splitUnits.length) {
                return splitUnits.map((unit, index) => ({
                    id: `preview-${index + 1}`,
                    unitSeq: unit.unitSeq || index + 1,
                    unitNo: unit.unitNo || `预览单元-${index + 1}`,
                    unitTitle: unit.unitTitle || `${payload.requireDesc || '订单需求'}-${index + 1}`,
                    unitAmount: unit.unitAmount || unit.amount || previewResult.orderAmount || payload.budgetAmount || 0,
                    status: 'PENDING_ACCEPT',
                    verifyStatus: 'PENDING',
                    createTime,
                    lockReason: unit.lockReason || ''
                }))
            }
            return [{
                id: 'preview-1',
                unitSeq: 1,
                unitNo: '预览单元-1',
                unitTitle: payload.requireDesc || '订单需求',
                unitAmount: previewResult.orderAmount || payload.budgetAmount || 0,
                status: 'PENDING_ACCEPT',
                verifyStatus: 'PENDING',
                createTime
            }]
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
        },
        async handlePublishPreview() {
            const snapshot = this.previewSnapshot || uni.getStorageSync(ORDER_PREVIEW_STORAGE_KEY)
            if (!snapshot || !snapshot.payload || !snapshot.previewResult || !snapshot.previewResult.previewToken) {
                uni.showToast({
                    title: '预览信息已失效',
                    icon: 'none'
                })
                return
            }
            if (this.publishing) {
                return
            }
            try {
                this.publishing = true
                const guaranteeConfig = snapshot.guaranteeConfig || {}
                const orderId = await createOrder({
                    ...snapshot.payload,
                    agreementConfirmed: true,
                    agreementVersion: guaranteeConfig.agreementVersion || 'v2026.06',
                    previewToken: snapshot.previewResult.previewToken,
                    antiEscapeConfirmed: true
                })
                uni.removeStorageSync(ORDER_PREVIEW_STORAGE_KEY)
                uni.showToast({
                    title: '发布成功',
                    icon: 'success'
                })
                setTimeout(() => {
                    uni.redirectTo({
                        url: `/pages/split_order_details/split_order_details?orderId=${orderId}`
                    })
                }, 300)
            } catch (error) {
            } finally {
                this.publishing = false
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

        .preview-banner {
            background: linear-gradient(180deg, #fef7e8 0%, #fff9ef 100%);
            border: 1rpx solid #f7d48b;
            border-radius: 16rpx;
            padding: 20rpx 24rpx;
            margin-bottom: 16rpx;

            .preview-banner-title {
                display: block;
                font-size: 28rpx;
                font-weight: 600;
                color: #a86a00;
                margin-bottom: 8rpx;
            }

            .preview-banner-desc {
                font-size: 24rpx;
                color: #8c6a2b;
                line-height: 34rpx;
            }
        }

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

        .preview-extra {
            margin-top: 20rpx;
            padding-top: 16rpx;
            border-top: 1rpx solid rgba(74, 144, 240, 0.12);
            display: flex;
            flex-direction: column;
            gap: 8rpx;

            .preview-extra-item {
                font-size: 24rpx;
                line-height: 34rpx;
                color: #5c6980;
            }
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
            margin-top: 24rpx;

            .bottom-btn {
                flex: 1;
                min-width: 0;
                min-height: 72rpx;
                padding: 0 24rpx;
                border: 2rpx solid #4A90F0;
                border-radius: 12rpx;
                box-sizing: border-box;
                display: flex;
                align-items: center;
                justify-content: center;
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

            .btn-text {
                font-size: 30rpx;
                font-weight: 600;
                line-height: 1;
                white-space: nowrap;
            }
        }

        .bottom-space {
            height: 60rpx;
        }
    }
}
</style>
