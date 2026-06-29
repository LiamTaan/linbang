<template>
    <view class="page-container">
        <view class="header-bg">
            <view class="header">
                <view class="back-btn" @click="goBack">
                    <text class="iconfont icon-youjiantou back-icon"></text>
                </view>
                <text class="title">投诉与反馈</text>
                <view class="placeholder"></view>
            </view>

            <view class="tabs">
                <view v-for="(tab, index) in tabs" :key="tab" class="tab-item" :class="{ active: currentTab === index }"
                    @click="changeTab(index)">
                    <text class="tab-text">{{ tab }}</text>
                    <view v-if="currentTab === index" class="tab-indicator"></view>
                </view>
            </view>
        </view>

        <scroll-view class="content-scroll" scroll-y>
            <view v-if="currentTab === 0" class="form-card">
                <view class="form-title">
                    <text class="title-text">填写信息</text>
                </view>

                <view class="form-section">
                    <text class="section-label">关联订单</text>
                    <view class="order-select" @click="handleOrderSelect">
                        <text class="order-text">{{ selectedOrderTitle }}</text>
                        <text class="change-text">{{ orderOptions.length > 1 ? '更换' : '查看' }}</text>
                    </view>
                </view>

                <view class="form-section" v-if="unitOptions.length > 1">
                    <text class="section-label">关联单元</text>
                    <view class="order-select" @click="handleUnitSelect">
                        <text class="order-text">{{ selectedUnitTitle }}</text>
                        <text class="change-text">更换</text>
                    </view>
                </view>

                <view class="form-section">
                    <text class="section-label">投诉对象</text>
                    <view class="summary-box">
                        <text class="summary-text">服务商 / 用户 ID：{{ respondentUserId || '--' }}</text>
                    </view>
                </view>

                <view class="form-section">
                    <text class="section-label">投诉原因</text>
                    <view class="option-group">
                        <view v-for="item in complaintTypes" :key="item" class="option-item"
                            :class="{ active: selectedComplaintType === item }" @click="selectedComplaintType = item">
                            <text class="option-text">{{ item }}</text>
                        </view>
                    </view>
                </view>

                <view class="form-section">
                    <text class="section-label">投诉详情</text>
                    <textarea class="detail-textarea" placeholder="请您详细描述投诉内容……" v-model="detail"></textarea>
                </view>

                <view class="form-section">
                    <text class="section-label">上传图片/视频</text>
                    <view class="upload-area">
                        <view v-for="(file, index) in uploads" :key="file.fileId || file.url || index" class="upload-item preview">
                            <image class="upload-image" :src="file.url" mode="aspectFill" />
                            <text class="remove-upload" @click="removeUpload(index)">×</text>
                        </view>
                        <view class="upload-item add" @click="handleUpload">
                            <text class="add-icon">{{ uploading ? '...' : '+' }}</text>
                        </view>
                    </view>
                </view>
            </view>

            <view v-else-if="currentTab === 1" class="feedback-card">
                <text class="feedback-title">建议反馈统一收敛到帮助与反馈页</text>
                <text class="feedback-desc">这里保留投诉场景，普通建议、功能反馈和产品意见请前往帮助与反馈页提交。</text>
                <view class="feedback-btn" @click="goToFeedback">
                    <text class="feedback-btn-text">前往帮助与反馈</text>
                </view>
            </view>

            <view v-else class="progress-card">
                <text class="progress-title">处理进度</text>
                <view v-if="complaints.length">
                    <view v-for="item in complaints" :key="item.id" class="progress-item">
                        <view>
                            <text class="progress-type">{{ item.complaintType }}</text>
                            <text class="progress-content">{{ item.content }}</text>
                        </view>
                        <view class="progress-right">
                            <text class="progress-status">{{ item.status }}</text>
                            <text class="progress-time">{{ $fmt.formatShortDateTime(item.createTime) }}</text>
                        </view>
                    </view>
                </view>
                <text v-else class="progress-empty">暂无投诉记录</text>
            </view>

            <view class="submit-btn" v-if="currentTab === 0" @click="submitComplaint">
                <text class="submit-text">{{ submitting ? '提交中...' : '提交投诉' }}</text>
            </view>

            <view class="footer-tip" v-if="currentTab === 0">
                <text class="tip-text">我们将在 3 个工作日内处理您的投诉</text>
                <text class="tip-text">处理结果将通过消息通知您</text>
            </view>

            <view class="bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
import { uploadAppFile } from '@/api/infra'
import { getOrderDetail, getOrderPage } from '@/api/order'
import { createComplaint, getComplaintPage } from '@/api/review'
import { getTokenInfo } from '@/utils/auth'
import { COMPLAINT_TYPE_OPTIONS, extractUploadedFile, getOrderStatusLabel } from '@/utils/linbang'

export default {
    data() {
        return {
            tabs: ['我要投诉', '建议反馈', '处理进度'],
            currentTab: 0,
            uploading: false,
            submitting: false,
            complaintTypes: COMPLAINT_TYPE_OPTIONS,
            selectedComplaintType: COMPLAINT_TYPE_OPTIONS[0],
            detail: '',
            uploads: [],
            orderId: null,
            unitId: null,
            orderOptions: [],
            orderDetail: {},
            complaints: []
        }
    },
    computed: {
        unitOptions() {
            return this.orderDetail.units || []
        },
        respondentUserId() {
            const loginUserId = getTokenInfo().userId
            if (`${loginUserId}` === `${this.orderDetail.merchantId}`) {
                return this.orderDetail.userId || ''
            }
            return this.orderDetail.merchantId || this.orderDetail.userId || ''
        },
        selectedOrderTitle() {
            return this.orderDetail.requireDesc || this.orderDetail.orderNo || '请选择投诉订单'
        },
        selectedUnitTitle() {
            const target = this.unitOptions.find((item) => `${item.id}` === `${this.unitId}`)
            return target ? (target.unitTitle || target.unitNo) : '整单投诉'
        }
    },
    onLoad(options) {
        this.orderId = options && options.orderId ? Number(options.orderId) : null
        this.unitId = options && options.unitId ? Number(options.unitId) : null
        this.currentTab = options && options.mode === 'progress' ? 2 : 0
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
                const orderPage = await getOrderPage({ pageNo: 1, pageSize: 20 }).catch(() => ({ list: [] }))
                this.orderOptions = (orderPage && orderPage.list) || []
                if (!this.orderId && this.orderOptions.length) {
                    this.orderId = this.orderOptions[0].id
                }
                if (this.orderId) {
                    this.orderDetail = await getOrderDetail(this.orderId)
                }
                if (this.currentTab === 2 || !this.complaints.length) {
                    const complaintPage = await getComplaintPage({ pageNo: 1, pageSize: 20 }).catch(() => ({ list: [] }))
                    this.complaints = (complaintPage && complaintPage.list) || []
                }
            } catch (error) {
            }
        },
        changeTab(index) {
            this.currentTab = index
            if (index === 2 && !this.complaints.length) {
                this.loadPageData()
            }
        },
        handleOrderSelect() {
            if (!this.orderOptions.length) {
                uni.showToast({
                    title: '暂无可投诉订单',
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
                    this.orderDetail = await getOrderDetail(this.orderId)
                }
            })
        },
        handleUnitSelect() {
            if (!this.unitOptions.length) {
                return
            }
            uni.showActionSheet({
                itemList: ['整单投诉'].concat(this.unitOptions.map((item) => item.unitTitle || item.unitNo)),
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
        async submitComplaint() {
            if (this.submitting) {
                return
            }
            if (!this.orderId) {
                uni.showToast({
                    title: '请选择投诉订单',
                    icon: 'none'
                })
                return
            }
            if (!this.respondentUserId) {
                uni.showToast({
                    title: '当前订单缺少投诉对象',
                    icon: 'none'
                })
                return
            }
            if (!this.detail.trim()) {
                uni.showToast({
                    title: '请填写投诉详情',
                    icon: 'none'
                })
                return
            }
            try {
                this.submitting = true
                await createComplaint({
                    orderId: this.orderId,
                    unitId: this.unitId || undefined,
                    respondentUserId: this.respondentUserId,
                    complaintType: this.selectedComplaintType,
                    content: this.detail.trim(),
                    fileIds: this.uploads.map((item) => item.fileId).filter(Boolean)
                })
                uni.showToast({
                    title: '投诉已提交',
                    icon: 'success'
                })
                this.detail = ''
                this.uploads = []
                this.currentTab = 2
                await this.loadPageData()
            } catch (error) {
            } finally {
                this.submitting = false
            }
        },
        goToFeedback() {
            uni.navigateTo({
                url: '/pages/feedback/feedback'
            })
        }
    }
}
</script>

<style lang="scss" scoped>
.page-container {
    min-height: 100vh;
    background: #F5F5F5;

    .header-bg {
        background: linear-gradient(180deg, #4A90F0 0%, #6BA3F5 100%);
        padding-bottom: 20rpx;

        .header {
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
                color: #fff;
                transform: rotate(180deg);
            }

            .title {
                font-size: 34rpx;
                font-weight: bold;
                color: #fff;
            }
        }

        .tabs {
            display: flex;
            padding: 0 30rpx;

            .tab-item {
                flex: 1;
                display: flex;
                flex-direction: column;
                align-items: center;
                padding: 16rpx 0;

                .tab-text {
                    font-size: 28rpx;
                    color: rgba(255, 255, 255, 0.8);
                }

                &.active {
                    .tab-text {
                        color: #fff;
                        font-weight: bold;
                    }

                    .tab-indicator {
                        width: 48rpx;
                        height: 6rpx;
                        background: #fff;
                        border-radius: 3rpx;
                        margin-top: 12rpx;
                    }
                }
            }
        }
    }

    .content-scroll {
        flex: 1;
        padding: 20rpx;
        box-sizing: border-box;

        .form-card,
        .feedback-card,
        .progress-card {
            background: #fff;
            border-radius: 16rpx;
            padding: 24rpx;
        }

        .form-title,
        .progress-title,
        .feedback-title {
            margin-bottom: 24rpx;
            font-size: 28rpx;
            font-weight: bold;
            color: #333;
        }

        .form-section {
            margin-bottom: 24rpx;
        }

        .section-label {
            display: block;
            font-size: 26rpx;
            color: #666;
            margin-bottom: 16rpx;
        }

        .order-select,
        .summary-box {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 20rpx;
            background: #F5F7FA;
            border-radius: 12rpx;
        }

        .order-text,
        .summary-text,
        .detail-textarea,
        .feedback-desc,
        .progress-content {
            font-size: 26rpx;
            color: #333;
        }

        .change-text {
            font-size: 24rpx;
            color: #4A90F0;
        }

        .option-group {
            display: flex;
            flex-wrap: wrap;
            gap: 16rpx;
        }

        .option-item {
            padding: 16rpx 32rpx;
            border-radius: 8rpx;
            border: 2rpx solid #E0E0E0;

            &.active {
                border-color: #4A90F0;
                background: #E8F4FD;

                .option-text {
                    color: #4A90F0;
                    font-weight: bold;
                }
            }
        }

        .option-text,
        .progress-type,
        .progress-status {
            font-size: 26rpx;
            color: #666;
        }

        .detail-textarea {
            width: 100%;
            height: 200rpx;
            background: #F5F7FA;
            border-radius: 12rpx;
            padding: 20rpx;
            box-sizing: border-box;
        }

        .upload-area {
            display: flex;
            gap: 16rpx;
            flex-wrap: wrap;
        }

        .upload-item {
            width: 120rpx;
            height: 120rpx;
            border-radius: 12rpx;
            border: 2rpx dashed #DDD;
            display: flex;
            align-items: center;
            justify-content: center;

            &.add {
                background: #FAFAFA;
            }

            &.preview {
                position: relative;
                overflow: hidden;
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

        .add-icon {
            font-size: 48rpx;
            color: #999;
        }

        .feedback-card {
            text-align: center;

            .feedback-desc {
                color: #666;
                line-height: 1.6;
                margin-bottom: 24rpx;
            }

            .feedback-btn {
                background: #4A90F0;
                border-radius: 12rpx;
                padding: 24rpx;
            }

            .feedback-btn-text {
                font-size: 28rpx;
                color: #fff;
                font-weight: bold;
            }
        }

        .progress-item {
            display: flex;
            justify-content: space-between;
            gap: 20rpx;
            padding: 20rpx 0;
            border-bottom: 1rpx solid #F0F0F0;

            &:last-child {
                border-bottom: none;
            }
        }

        .progress-type {
            display: block;
            color: #2E83F0;
            margin-bottom: 8rpx;
        }

        .progress-right {
            text-align: right;
        }

        .progress-time,
        .progress-empty,
        .tip-text {
            font-size: 22rpx;
            color: #999;
        }

        .submit-btn {
            background: #D32F2F;
            border-radius: 12rpx;
            padding: 28rpx;
            text-align: center;
            margin-top: 20rpx;
        }

        .submit-text {
            font-size: 30rpx;
            color: #fff;
            font-weight: bold;
        }

        .footer-tip {
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 32rpx;
            gap: 8rpx;
        }

        .bottom-space {
            height: 60rpx;
        }
    }
}
</style>
