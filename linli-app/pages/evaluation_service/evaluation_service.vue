<template>
    <view class="page-container">
        <view class="header">
            <view class="back-btn" @click="goBack">
                <text class="iconfont icon-youjiantou back-icon"></text>
            </view>
            <text class="title">评价服务</text>
            <view class="placeholder"></view>
        </view>

        <scroll-view class="content-scroll" scroll-y>
            <view class="empty-card" v-if="!pendingUnits.length">
                <text class="empty-title">暂无待评价单元</text>
                <text class="empty-desc">订单完成后，这里会出现待评价服务单元。</text>
            </view>

            <template v-else>
                <view class="order-card" @click="handleUnitSelect">
                    <text class="order-label">评价订单</text>
                    <view class="order-info">
                        <text class="order-name">{{ currentUnit.unitTitle || currentUnit.orderNo }}</text>
                        <text class="order-detail">
                            {{ currentUnit.toUserNickname || '服务对象' }} · ¥{{ $fmt.formatMoney(currentUnit.unitAmount) }}
                        </text>
                    </view>
                    <text class="order-sub">完成时间：{{ $fmt.formatDateTime(currentUnit.finishTime) }}</text>
                </view>

                <view class="rating-card">
                    <text class="rating-title">您对此次服务满意吗？</text>
                    <view class="star-container">
                        <view v-for="index in 5" :key="index" class="star-item" :class="{ active: currentRating >= index }"
                            @click="setRating(index)">
                            <image class="star-icon" :src="starIcon" />
                        </view>
                    </view>
                    <text class="rating-text">{{ ratingText }}</text>
                </view>

                <view class="tag-card">
                    <text class="tag-title">服务标签</text>
                    <view class="tag-grid">
                        <view v-for="tag in serviceTags" :key="tag" class="tag-item"
                            :class="{ active: selectedTags.includes(tag) }" @click="toggleTag(tag)">
                            <text class="tag-text">{{ tag }}</text>
                        </view>
                    </view>
                </view>

                <view class="comment-card">
                    <text class="comment-title">评价内容</text>
                    <view class="input-wrap">
                        <textarea class="comment-input" v-model="comment" placeholder="请描述您的服务体验……" :maxlength="200" />
                        <text class="char-count">{{ comment.length }}/200字</text>
                    </view>
                </view>

                <view class="anonymous-card">
                    <view class="checkbox-wrap" @click="toggleAnonymous">
                        <view class="checkbox" :class="{ checked: isAnonymous }">
                            <text v-if="isAnonymous" class="check-icon">✓</text>
                        </view>
                        <text class="checkbox-text">匿名评价（仅前端展示偏好，本轮不单独传后端）</text>
                    </view>
                </view>

                <view class="submit-btn" @click="handleSubmit">
                    <text class="submit-text">{{ submitting ? '提交中...' : '提交评价' }}</text>
                </view>

                <text class="bottom-tip">超时未评价会按平台规则自动评价</text>
            </template>

            <view class="bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
import { createReview, getPendingReviewUnits } from '@/api/review'

export default {
    data() {
        return {
            pendingUnits: [],
            selectedUnitId: null,
            currentRating: 5,
            comment: '',
            isAnonymous: true,
            selectedTags: ['准时上门', '技术专业'],
            serviceTags: ['准时上门', '技术专业', '态度友好', '价格合理', '干净整洁'],
            starIcon: '/static/img/evaluation_service/star@3x.png',
            submitting: false
        }
    },
    computed: {
        currentUnit() {
            return this.pendingUnits.find((item) => `${item.unitId}` === `${this.selectedUnitId}`) || this.pendingUnits[0] || {}
        },
        ratingText() {
            const texts = ['非常不满意', '不满意', '一般', '满意', '非常满意']
            return texts[this.currentRating - 1] || ''
        }
    },
    onLoad(options) {
        this.selectedUnitId = options && options.unitId ? Number(options.unitId) : null
    },
    onShow() {
        this.loadPendingUnits()
    },
    methods: {
        goBack() {
            uni.navigateBack()
        },
        async loadPendingUnits() {
            try {
                const list = await getPendingReviewUnits().catch(() => [])
                this.pendingUnits = Array.isArray(list) ? list : []
                if (!this.selectedUnitId && this.pendingUnits.length) {
                    this.selectedUnitId = this.pendingUnits[0].unitId
                }
            } catch (error) {
            }
        },
        handleUnitSelect() {
            if (!this.pendingUnits.length) {
                return
            }
            uni.showActionSheet({
                itemList: this.pendingUnits.map((item) => `${item.unitTitle || item.orderNo} · ${item.toUserNickname || '服务对象'}`),
                success: ({ tapIndex }) => {
                    const target = this.pendingUnits[tapIndex]
                    if (target) {
                        this.selectedUnitId = target.unitId
                    }
                }
            })
        },
        setRating(rating) {
            this.currentRating = rating
        },
        toggleTag(tagName) {
            const index = this.selectedTags.indexOf(tagName)
            if (index > -1) {
                this.selectedTags.splice(index, 1)
            } else {
                this.selectedTags.push(tagName)
            }
        },
        toggleAnonymous() {
            this.isAnonymous = !this.isAnonymous
        },
        async handleSubmit() {
            if (this.submitting) {
                return
            }
            if (!this.currentUnit.unitId) {
                uni.showToast({
                    title: '请选择待评价单元',
                    icon: 'none'
                })
                return
            }
            const contentParts = []
            if (this.selectedTags.length) {
                contentParts.push(`标签：${this.selectedTags.join('、')}`)
            }
            if (this.comment.trim()) {
                contentParts.push(this.comment.trim())
            }
            const content = contentParts.join('；')
            if (!content) {
                uni.showToast({
                    title: '请填写评价内容或选择标签',
                    icon: 'none'
                })
                return
            }
            try {
                this.submitting = true
                await createReview({
                    orderId: this.currentUnit.orderId,
                    unitId: this.currentUnit.unitId,
                    toUserId: this.currentUnit.toUserId,
                    starLevel: this.currentRating,
                    content
                })
                uni.showToast({
                    title: '评价提交成功',
                    icon: 'success'
                })
                this.comment = ''
                this.selectedTags = ['准时上门', '技术专业']
                await this.loadPendingUnits()
                if (!this.pendingUnits.length) {
                    setTimeout(() => {
                        uni.navigateBack()
                    }, 600)
                }
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

        .empty-card,
        .order-card,
        .rating-card,
        .tag-card,
        .comment-card,
        .anonymous-card {
            background: #fff;
            border-radius: 16rpx;
            padding: 24rpx;
            margin-bottom: 20rpx;
        }

        .empty-card {
            text-align: center;
        }

        .empty-title {
            display: block;
            font-size: 30rpx;
            font-weight: bold;
            color: #333;
            margin-bottom: 12rpx;
        }

        .empty-desc,
        .order-label,
        .order-detail,
        .order-sub,
        .char-count,
        .bottom-tip {
            font-size: 24rpx;
            color: #999;
        }

        .order-name,
        .tag-title,
        .comment-title {
            font-size: 30rpx;
            font-weight: bold;
            color: #333;
            display: block;
            margin-bottom: 8rpx;
        }

        .rating-card {
            text-align: center;

            .rating-title {
                font-size: 28rpx;
                color: #333;
                display: block;
                margin-bottom: 24rpx;
            }

            .star-container {
                display: flex;
                justify-content: center;
                gap: 20rpx;
                margin-bottom: 16rpx;
            }

            .star-item .star-icon {
                width: 56rpx;
                height: 56rpx;
                opacity: 0.3;
            }

            .star-item.active .star-icon {
                opacity: 1;
            }

            .rating-text {
                font-size: 26rpx;
                color: #FA9D3B;
            }
        }

        .tag-grid {
            display: flex;
            flex-wrap: wrap;
            gap: 16rpx;
        }

        .tag-item {
            padding: 16rpx 28rpx;
            border: 2rpx solid #E8E8E8;
            border-radius: 28rpx;

            &.active {
                background: #4A90F0;
                border-color: #4A90F0;

                .tag-text {
                    color: #fff;
                }
            }
        }

        .tag-text,
        .checkbox-text {
            font-size: 26rpx;
            color: #666;
        }

        .input-wrap {
            position: relative;
        }

        .comment-input {
            width: 100%;
            height: 200rpx;
            background: #F8F8F8;
            border-radius: 12rpx;
            padding: 20rpx 20rpx 50rpx;
            font-size: 28rpx;
            color: #333;
            box-sizing: border-box;
        }

        .char-count {
            position: absolute;
            right: 20rpx;
            bottom: 16rpx;
        }

        .checkbox-wrap {
            display: flex;
            align-items: center;
            gap: 16rpx;
        }

        .checkbox {
            width: 40rpx;
            height: 40rpx;
            border: 2rpx solid #D9D9D9;
            border-radius: 6rpx;
            display: flex;
            align-items: center;
            justify-content: center;

            &.checked {
                background: #4A90F0;
                border-color: #4A90F0;
            }
        }

        .check-icon {
            color: #fff;
            font-size: 24rpx;
        }

        .submit-btn {
            background: #4A90F0;
            border-radius: 12rpx;
            padding: 30rpx;
            text-align: center;
            margin-bottom: 20rpx;
        }

        .submit-text {
            font-size: 32rpx;
            font-weight: bold;
            color: #fff;
        }

        .bottom-tip {
            text-align: center;
            display: block;
        }

        .bottom-space {
            height: 60rpx;
        }
    }
}
</style>
