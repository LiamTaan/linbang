<template>
    <view class="page-container">
        <view class="notice-card">
            <text class="notice-title">平台数据看板</text>
            <text class="notice-text">这是管理端能力，不在移动端开放。</text>
            <text class="notice-text">平台管理员请前往后台使用工作台查看数据、审核和配置。</text>
            <view class="notice-btn" @click="goBack">
                <text class="notice-btn-text">返回</text>
            </view>
        </view>
    </view>
</template>

<script>
export default {
    methods: {
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
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 40rpx;
    box-sizing: border-box;
}

.notice-card {
    width: 100%;
    background: #fff;
    border-radius: 20rpx;
    padding: 40rpx 30rpx;
    box-shadow: 0 10rpx 30rpx rgba(36, 77, 145, 0.08);
}

.notice-title {
    display: block;
    font-size: 34rpx;
    font-weight: bold;
    color: #1f68d4;
    margin-bottom: 16rpx;
}

.notice-text {
    display: block;
    font-size: 24rpx;
    color: #6c7c92;
    line-height: 1.8;
    margin-bottom: 10rpx;
}

.notice-btn {
    margin-top: 28rpx;
    height: 72rpx;
    border-radius: 36rpx;
    background: #2e83f0;
    display: flex;
    align-items: center;
    justify-content: center;
}

.notice-btn-text {
    color: #fff;
    font-size: 26rpx;
    font-weight: bold;
}
</style>
