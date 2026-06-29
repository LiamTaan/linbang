<template>
    <view class="page-container">
        <view class="header">
            <view class="back-btn" @click="goBack">
                <text class="iconfont icon-youjiantou back-icon"></text>
            </view>
            <text class="title">申诉</text>
            <view class="placeholder"></view>
        </view>

        <scroll-view class="tip-bar" scroll-x :show-scrollbar="false">
            <view class="tip-content">
                <text class="tip-text">申诉需关联真实订单或单元</text>
                <text class="tip-divider">|</text>
                <text class="tip-text">提交后可在本页查看审核进度</text>
            </view>
        </scroll-view>

        <scroll-view class="content-scroll" scroll-y>
            <view class="punishment-card">
                <view class="card-header">
                    <image class="punishment-icon" src="/static/img/appeal/punishment@3x.png" />
                    <text class="header-title">申诉对象</text>
                    <text class="header-date">{{ $fmt.formatShortDateTime(selectedOrder.createTime) }}</text>
                </view>
                <view class="punishment-info">
                    <view class="info-row">
                        <text class="info-label">订单：</text>
                        <text class="info-value">{{ selectedOrderTitle }}</text>
                    </view>
                    <view class="info-row">
                        <text class="info-label">状态：</text>
                        <text class="info-value">{{ selectedOrderStatus }}</text>
                    </view>
                </view>
            </view>

            <view class="section-card">
                <text class="section-title">申诉订单</text>
                <view class="reason-select" @click="handleOrderSelect">
                    <text class="reason-text">{{ selectedOrderTitle }}</text>
                    <text class="reason-arrow">></text>
                </view>
            </view>

            <view class="section-card" v-if="unitOptions.length > 1">
                <text class="section-title">申诉单元</text>
                <view class="reason-select" @click="handleUnitSelect">
                    <text class="reason-text">{{ selectedUnitTitle }}</text>
                    <text class="reason-arrow">></text>
                </view>
            </view>

            <view class="section-card">
                <text class="section-title">申诉原因</text>
                <view class="option-group">
                    <view v-for="item in appealTypes" :key="item" class="option-item"
                        :class="{ active: selectedReason === item }" @click="selectedReason = item">
                        <text class="option-text">{{ item }}</text>
                    </view>
                </view>
            </view>

            <view class="section-card">
                <text class="section-title">详细说明</text>
                <textarea class="description-textarea" placeholder="请您详细描述申诉理由……" v-model="description"></textarea>
            </view>

            <view class="section-card">
                <text class="section-title">上传凭证（聊天记录/截图等）</text>
                <view class="upload-area">
                    <view v-for="(file, index) in uploads" :key="file.fileId || file.url || index" class="upload-btn preview">
                        <image class="upload-image" :src="file.url" mode="aspectFill" />
                        <text class="remove-upload" @click="removeUpload(index)">×</text>
                    </view>
                    <view class="upload-btn" @click="handleUpload">
                        <text class="upload-icon">{{ uploading ? '...' : '+' }}</text>
                    </view>
                </view>
            </view>

            <view class="section-card">
                <view class="progress-header">
                    <text class="section-title">申诉进度</text>
                    <view class="view-detail" @click="handleViewDetail">
                        <text class="detail-text">查看详情</text>
                        <text class="detail-arrow">></text>
                    </view>
                </view>
                <view class="progress-info" v-if="latestAppeal">
                    <view class="progress-row">
                        <text class="progress-label">当前状态：</text>
                        <text class="progress-value">{{ latestAppeal.status }}</text>
                    </view>
                    <view class="progress-row">
                        <text class="progress-label">提交时间：</text>
                        <text class="progress-value">{{ $fmt.formatDateTime(latestAppeal.createTime) }}</text>
                    </view>
                    <view class="progress-row" v-if="appealProgress.auditRemark || appealProgress.rejectReason">
                        <text class="progress-label">处理说明：</text>
                        <text class="progress-value">{{ appealProgress.auditRemark || appealProgress.rejectReason }}</text>
                    </view>
                </view>
                <text v-else class="empty-progress">暂无申诉记录</text>
            </view>

            <view class="submit-btn" @click="handleSubmit">
                <text class="submit-text">{{ submitting ? '提交中...' : '提交申诉' }}</text>
            </view>

            <view class="bottom-tip">
                <text class="tip-text">平台将在 3 个工作日内审核您的申诉</text>
            </view>

            <view class="bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
import { uploadAppFile } from '@/api/infra'
import { getAppealProgress, getOrderDetail, getOrderPage } from '@/api/order'
import { createAppeal, getAppealPage } from '@/api/review'
import { APPEAL_TYPE_OPTIONS, extractUploadedFile, getOrderStatusLabel } from '@/utils/linbang'

export default {
    data() {
        return {
            uploading: false,
            submitting: false,
            appealTypes: APPEAL_TYPE_OPTIONS,
            selectedReason: APPEAL_TYPE_OPTIONS[0],
            description: '',
            uploads: [],
            orderId: null,
            unitId: null,
            orderOptions: [],
            selectedOrder: {},
            appealRecords: [],
            appealProgress: {}
        }
    },
    computed: {
        unitOptions() {
            return this.selectedOrder.units || []
        },
        selectedOrderTitle() {
            return this.selectedOrder.requireDesc || this.selectedOrder.orderNo || '请选择申诉订单'
        },
        selectedOrderStatus() {
            return getOrderStatusLabel(this.selectedOrder.status)
        },
        selectedUnitTitle() {
            const target = this.unitOptions.find((item) => `${item.id}` === `${this.unitId}`)
            return target ? (target.unitTitle || target.unitNo) : '整单申诉'
        },
        latestAppeal() {
            return this.appealRecords[0] || null
        }
    },
    onLoad(options) {
        this.orderId = options && options.orderId ? Number(options.orderId) : null
        this.unitId = options && options.unitId ? Number(options.unitId) : null
    },
    onShow() {
        this.loadPageData()
    },
    methods: {
        goBack() {
            uni.navigateBack()
        },
        async loadPageData() {
            try {
                const [orderPage, appealPage] = await Promise.all([
                    getOrderPage({ pageNo: 1, pageSize: 20 }).catch(() => ({ list: [] })),
                    getAppealPage({ pageNo: 1, pageSize: 20 }).catch(() => ({ list: [] }))
                ])
                this.orderOptions = (orderPage && orderPage.list) || []
                this.appealRecords = (appealPage && appealPage.list) || []
                if (!this.orderId && this.orderOptions.length) {
                    this.orderId = this.orderOptions[0].id
                }
                if (this.orderId) {
                    this.selectedOrder = await getOrderDetail(this.orderId)
                }
                if (this.latestAppeal && this.latestAppeal.id) {
                    this.appealProgress = await getAppealProgress(this.latestAppeal.id).catch(() => ({}))
                }
            } catch (error) {
            }
        },
        handleOrderSelect() {
            if (!this.orderOptions.length) {
                uni.showToast({
                    title: '暂无可申诉订单',
                    icon: 'none'
                })
                return
            }
            uni.showActionSheet({
                itemList: this.orderOptions.map((item) => `${item.categoryName || '订单'} · ${getOrderStatusLabel(item.status)}`),
                success: async ({ tapIndex }) => {
                    const target = this.orderOptions[tapIndex]
                    if (!target) {
                        return
                    }
                    this.orderId = target.id
                    this.unitId = null
                    this.selectedOrder = await getOrderDetail(this.orderId)
                }
            })
        },
        handleUnitSelect() {
            uni.showActionSheet({
                itemList: ['整单申诉'].concat(this.unitOptions.map((item) => item.unitTitle || item.unitNo)),
                success: ({ tapIndex }) => {
                    this.unitId = tapIndex === 0 ? null : this.unitOptions[tapIndex - 1].id
                }
            })
        },
        async handleUpload() {
            if (this.uploading) {
                return
            }
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
                this.uploading = true
                for (let i = 0; i < paths.length; i++) {
                    const uploadResp = await uploadAppFile(paths[i])
                    const file = extractUploadedFile(uploadResp, paths[i])
                    if (file.fileId) {
                        this.uploads.push(file)
                    }
                }
            } catch (error) {
            } finally {
                this.uploading = false
            }
        },
        removeUpload(index) {
            this.uploads.splice(index, 1)
        },
        handleViewDetail() {
            if (!this.latestAppeal) {
                uni.showToast({
                    title: '暂无申诉详情',
                    icon: 'none'
                })
                return
            }
            uni.showModal({
                title: this.latestAppeal.appealType || '申诉详情',
                content: [
                    `状态：${this.appealProgress.status || this.latestAppeal.status}`,
                    `审核：${this.appealProgress.auditStatus || this.latestAppeal.auditStatus || '--'}`,
                    this.appealProgress.auditRemark || this.latestAppeal.auditRemark || this.appealProgress.rejectReason || this.latestAppeal.rejectReason || '暂无补充说明'
                ].join('\n'),
                showCancel: false
            })
        },
        async handleSubmit() {
            if (this.submitting) {
                return
            }
            if (!this.orderId) {
                uni.showToast({
                    title: '请选择申诉订单',
                    icon: 'none'
                })
                return
            }
            if (!this.description.trim()) {
                uni.showToast({
                    title: '请填写申诉说明',
                    icon: 'none'
                })
                return
            }
            try {
                this.submitting = true
                await createAppeal({
                    orderId: this.orderId,
                    unitId: this.unitId || undefined,
                    appealType: this.selectedReason,
                    content: this.description.trim(),
                    fileIds: this.uploads.map((item) => item.fileId).filter(Boolean)
                })
                uni.showToast({
                    title: '申诉已提交',
                    icon: 'success'
                })
                this.description = ''
                this.uploads = []
                await this.loadPageData()
            } catch (error) {
            } finally {
                this.submitting = false
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

    .tip-bar {
        white-space: nowrap;
        background: #E3EFFF;

        .tip-content {
            display: inline-flex;
            align-items: center;
            gap: 16rpx;
            padding: 16rpx 30rpx;
        }

        .tip-text,
        .tip-divider {
            font-size: 24rpx;
            color: #2E83F0;
        }
    }

    .content-scroll {
        flex: 1;
        padding: 20rpx;
        box-sizing: border-box;

        .punishment-card,
        .section-card {
            background: #fff;
            border-radius: 16rpx;
            padding: 24rpx;
            margin-bottom: 20rpx;
        }

        .punishment-card {
            background: #FFF7E6;
        }

        .card-header,
        .progress-header,
        .reason-select,
        .info-row,
        .progress-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .punishment-icon {
            width: 40rpx;
            height: 40rpx;
        }

        .header-title,
        .section-title {
            font-size: 28rpx;
            font-weight: bold;
            color: #333;
        }

        .header-date,
        .info-label,
        .progress-label,
        .tip-text,
        .empty-progress {
            font-size: 24rpx;
            color: #999;
        }

        .info-row,
        .progress-row {
            margin-top: 12rpx;
        }

        .info-value,
        .reason-text,
        .progress-value,
        .detail-text,
        .detail-arrow {
            font-size: 26rpx;
            color: #333;
        }

        .option-group {
            display: flex;
            flex-wrap: wrap;
            gap: 16rpx;
            margin-top: 16rpx;
        }

        .option-item {
            padding: 16rpx 28rpx;
            border: 2rpx solid #E8E8E8;
            border-radius: 28rpx;

            &.active {
                background: #4A90F0;
                border-color: #4A90F0;

                .option-text {
                    color: #fff;
                }
            }
        }

        .option-text {
            font-size: 26rpx;
            color: #666;
        }

        .description-textarea {
            width: 100%;
            height: 200rpx;
            background: #F5F7FA;
            border-radius: 12rpx;
            padding: 20rpx;
            font-size: 26rpx;
            color: #333;
            box-sizing: border-box;
            margin-top: 16rpx;
        }

        .upload-area {
            display: flex;
            gap: 16rpx;
            flex-wrap: wrap;
            margin-top: 16rpx;
        }

        .upload-btn {
            width: 120rpx;
            height: 120rpx;
            border: 2rpx dashed #2E83F0;
            border-radius: 12rpx;
            display: flex;
            align-items: center;
            justify-content: center;
            position: relative;
            overflow: hidden;

            &.preview {
                border-style: solid;
            }
        }

        .upload-image {
            width: 100%;
            height: 100%;
        }

        .remove-upload {
            position: absolute;
            top: 4rpx;
            right: 8rpx;
            color: #fff;
            font-size: 28rpx;
            background: rgba(0, 0, 0, 0.4);
            width: 32rpx;
            height: 32rpx;
            line-height: 32rpx;
            text-align: center;
            border-radius: 50%;
        }

        .upload-icon {
            font-size: 48rpx;
            color: #2E83F0;
        }

        .submit-btn {
            background: #4A90F0;
            border-radius: 12rpx;
            padding: 28rpx;
            text-align: center;
            margin-bottom: 20rpx;
        }

        .submit-text {
            font-size: 32rpx;
            color: #fff;
            font-weight: bold;
        }

        .bottom-tip {
            text-align: center;
            padding: 20rpx;
        }

        .bottom-space {
            height: 60rpx;
        }
    }
}
</style>
