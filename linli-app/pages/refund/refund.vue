<template>
    <view class="page-container">
        <view class="header">
            <view class="back-btn" @click="goBack">
                <text class="iconfont icon-youjiantou back-icon"></text>
            </view>
            <text class="title">申请退款</text>
            <view class="placeholder"></view>
        </view>

        <scroll-view class="content-scroll" scroll-y refresher-enabled :refresher-triggered="refreshing"
            @refresherrefresh="handleRefresh">
            <view class="order-card" @click="selectOrder">
                <view class="card-header">
                    <text class="header-label">退款订单</text>
                    <text class="header-action">{{ orderOptions.length > 1 ? '切换订单' : '当前订单' }}</text>
                </view>
                <view class="order-info">
                    <text class="order-title">{{ currentOrderTitle }}</text>
                    <text class="order-status">{{ currentOrderStatus }}</text>
                </view>
                <view class="order-amount">
                    <text class="amount-label">订单金额：</text>
                    <text class="amount-value">￥{{ $fmt.formatMoney(refundAmount) }}</text>
                </view>
                <view class="unit-select" v-if="unitOptions.length > 1" @click.stop="selectUnit">
                    <text class="unit-label">退款单元：{{ currentUnitTitle }}</text>
                    <text class="unit-action">切换</text>
                </view>
            </view>

            <view class="form-item">
                <view class="item-header">
                    <text class="item-label">申请原因</text>
                </view>
                <view class="select-box" @click="showReasonPicker">
                    <text class="select-placeholder">{{ selectedReason || '请选择申请原因' }}</text>
                    <text class="iconfont icon-youjiantou select-arrow"></text>
                </view>
            </view>

            <view class="form-item">
                <view class="item-header">
                    <text class="item-label">申请说明</text>
                    <text class="item-optional">（选填）</text>
                </view>
                <textarea class="textarea" placeholder="请您填写申请说明" v-model="description"></textarea>
            </view>

            <view class="form-item">
                <view class="item-header">
                    <text class="item-label">退款金额</text>
                    <text class="item-disabled">（不可修改）</text>
                </view>
                <view class="amount-box">
                    <text class="refund-amount">￥{{ $fmt.formatMoney(refundAmount) }}</text>
                </view>
            </view>

            <view class="form-item">
                <view class="item-header">
                    <text class="item-label">退款路径</text>
                </view>
                <text class="path-text">{{ refundPathText }}</text>
            </view>

            <view class="form-item">
                <view class="item-header">
                    <text class="item-label">退款时效</text>
                </view>
                <view class="timeline-list">
                    <text class="timeline-item">• 待接单：1-3 个工作日原路退回</text>
                    <text class="timeline-item">• 已接单：进入审核后按处理结果原路退款</text>
                </view>
            </view>

            <view class="history-card">
                <text class="history-title">退款记录</text>
                <view v-if="refundRecords.length">
                    <view v-for="item in refundRecords" :key="item.id" class="history-item">
                        <view>
                            <text class="history-status">{{ item.statusName || item.auditStatus || '处理中' }}</text>
                            <text class="history-reason">{{ item.reason }}</text>
                        </view>
                        <view class="history-right">
                            <text class="history-amount">￥{{ $fmt.formatMoney(toYuanFromFen(item.refundPrice)) }}</text>
                            <text class="history-time">{{ $fmt.formatShortDateTime(item.createTime) }}</text>
                        </view>
                    </view>
                </view>
                <text v-else class="history-empty">暂无退款记录</text>
            </view>

            <view class="submit-btn" @click="submitRefund">
                <text class="submit-text">{{ submitting ? '提交中...' : '提交退款申请' }}</text>
            </view>

            <view class="bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
import { getOrderDetail, getOrderPage } from '@/api/order'
import { createRefund, getRefundPage } from '@/api/pay'
import { getOrderStatusLabel, REFUND_REASON_OPTIONS, toYuanFromFen, uniqueById } from '@/utils/linbang'

export default {
    data() {
        return {
            refreshing: false,
            submitting: false,
            orderId: null,
            unitId: null,
            selectedReason: '',
            description: '',
            orderOptions: [],
            orderDetail: {},
            refundRecords: []
        }
    },
    computed: {
        unitOptions() {
            return (this.orderDetail.units || []).filter((item) => ['PENDING_ACCEPT', 'ACCEPTED'].includes(item.status))
        },
        currentUnit() {
            return this.unitOptions.find((item) => `${item.id}` === `${this.unitId}`) || null
        },
        refundAmount() {
            if (this.currentUnit && this.currentUnit.unitAmount !== undefined) {
                return this.currentUnit.unitAmount
            }
            return this.orderDetail.orderAmount || 0
        },
        currentOrderTitle() {
            return this.orderDetail.requireDesc || this.orderDetail.orderNo || '请选择订单'
        },
        currentOrderStatus() {
            return getOrderStatusLabel(this.orderDetail.status)
        },
        currentUnitTitle() {
            return this.currentUnit ? (this.currentUnit.unitTitle || this.currentUnit.unitNo) : '整单退款'
        },
        refundPathText() {
            const payRecord = this.orderDetail.payRecord || {}
            if (!payRecord.channelCode) {
                return '退款金额将原路退回至原支付账户'
            }
            return `退款金额将原路退回至 ${payRecord.channelCode} 账户`
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
        toYuanFromFen,
        goBack() {
            uni.navigateBack()
        },
        handleRefresh() {
            this.refreshing = true
            this.loadPageData()
        },
        async loadPageData() {
            try {
                const [pendingAcceptPage, acceptedPage] = await Promise.all([
                    getOrderPage({ pageNo: 1, pageSize: 20, status: 'PENDING_ACCEPT' }).catch(() => ({ list: [] })),
                    getOrderPage({ pageNo: 1, pageSize: 20, status: 'ACCEPTED' }).catch(() => ({ list: [] }))
                ])
                this.orderOptions = uniqueById(((pendingAcceptPage && pendingAcceptPage.list) || []).concat((acceptedPage && acceptedPage.list) || []))
                if (!this.orderId && this.orderOptions.length) {
                    this.orderId = this.orderOptions[0].id
                }
                if (this.orderId) {
                    await this.loadOrderDetail()
                    await this.loadRefundRecords()
                }
            } catch (error) {
            } finally {
                this.refreshing = false
            }
        },
        async loadOrderDetail() {
            const detail = await getOrderDetail(this.orderId)
            this.orderDetail = detail || {}
            if (!this.unitId && this.unitOptions.length === 1) {
                this.unitId = this.unitOptions[0].id
            }
        },
        async loadRefundRecords() {
            const page = await getRefundPage({
                pageNo: 1,
                pageSize: 20,
                orderId: this.orderId
            }).catch(() => ({ list: [] }))
            this.refundRecords = (page && page.list) || []
        },
        selectOrder() {
            if (!this.orderOptions.length) {
                uni.showToast({
                    title: '暂无可退款订单',
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
                    await this.loadOrderDetail()
                    await this.loadRefundRecords()
                }
            })
        },
        selectUnit() {
            if (!this.unitOptions.length) {
                return
            }
            uni.showActionSheet({
                itemList: ['整单退款'].concat(this.unitOptions.map((item) => `${item.unitTitle || item.unitNo} · ${getOrderStatusLabel(item.status)}`)),
                success: ({ tapIndex }) => {
                    this.unitId = tapIndex === 0 ? null : this.unitOptions[tapIndex - 1].id
                }
            })
        },
        showReasonPicker() {
            uni.showActionSheet({
                itemList: REFUND_REASON_OPTIONS,
                success: ({ tapIndex }) => {
                    this.selectedReason = REFUND_REASON_OPTIONS[tapIndex]
                }
            })
        },
        async submitRefund() {
            if (this.submitting) {
                return
            }
            if (!this.orderId) {
                uni.showToast({
                    title: '请选择退款订单',
                    icon: 'none'
                })
                return
            }
            if (!this.selectedReason) {
                uni.showToast({
                    title: '请选择退款原因',
                    icon: 'none'
                })
                return
            }
            const reason = this.description.trim()
                ? `${this.selectedReason}：${this.description.trim()}`.slice(0, 120)
                : this.selectedReason
            try {
                this.submitting = true
                await createRefund({
                    orderId: this.orderId,
                    unitId: this.unitId || undefined,
                    applyAmount: Number(this.refundAmount),
                    reason
                })
                uni.showToast({
                    title: '申请已提交',
                    icon: 'success'
                })
                this.description = ''
                await this.loadRefundRecords()
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

    .content-scroll {
        flex: 1;
        padding: 20rpx;
        box-sizing: border-box;

        .order-card,
        .form-item,
        .history-card {
            background: #fff;
            border-radius: 16rpx;
            padding: 24rpx;
            margin-bottom: 20rpx;
        }

        .card-header,
        .order-info,
        .unit-select,
        .history-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .header-label,
        .header-action,
        .item-optional,
        .item-disabled,
        .history-time {
            font-size: 24rpx;
            color: #999;
        }

        .order-title,
        .item-label,
        .history-title {
            font-size: 28rpx;
            color: #333;
            font-weight: bold;
        }

        .order-status,
        .unit-action {
            font-size: 24rpx;
            color: #FA9D3B;
        }

        .order-amount,
        .item-header {
            display: flex;
            align-items: center;
            margin-top: 16rpx;
        }

        .amount-label,
        .unit-label,
        .path-text,
        .timeline-item,
        .history-reason {
            font-size: 26rpx;
            color: #666;
        }

        .amount-value,
        .refund-amount,
        .history-amount {
            font-size: 30rpx;
            color: #4A90F0;
            font-weight: bold;
        }

        .select-box,
        .textarea,
        .amount-box {
            background: #F5F7FA;
            border-radius: 12rpx;
            padding: 24rpx;
            margin-top: 16rpx;
        }

        .textarea {
            width: 100%;
            height: 160rpx;
            box-sizing: border-box;
            font-size: 26rpx;
            color: #333;
        }

        .timeline-list {
            display: flex;
            flex-direction: column;
            gap: 12rpx;
            margin-top: 16rpx;
        }

        .history-item {
            padding: 20rpx 0;
            border-bottom: 1rpx solid #F0F0F0;

            &:last-child {
                border-bottom: none;
            }
        }

        .history-status {
            display: block;
            font-size: 26rpx;
            color: #2E83F0;
            margin-bottom: 8rpx;
        }

        .history-right {
            text-align: right;
        }

        .history-empty {
            font-size: 24rpx;
            color: #999;
        }

        .submit-btn {
            background: #4A90F0;
            border-radius: 12rpx;
            padding: 28rpx;
            text-align: center;
        }

        .submit-text {
            font-size: 30rpx;
            color: #fff;
            font-weight: bold;
        }

        .bottom-space {
            height: 60rpx;
        }
    }
}
</style>
