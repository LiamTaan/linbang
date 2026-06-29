<template>
    <view class="page-container">
        <view class="header-bg">
            <view class="header">
                <view class="back-btn" @click="goBack">
                    <text class="iconfont icon-youjiantou back-icon"></text>
                </view>
                <text class="title">帮助与反馈</text>
                <view class="placeholder"></view>
            </view>

            <view class="title-section">
                <text class="main-title">帮助与反馈</text>
                <text class="sub-title">真实提交到帮助反馈中心</text>
            </view>

            <view class="search-box">
                <image class="search-icon" src="/static/img/feedback/search@3x.png" />
                <input class="search-input" placeholder="搜索反馈记录" v-model="searchText" />
                <view class="search-btn" @click="filterHistory">
                    <text class="search-btn-text">筛选</text>
                </view>
            </view>

            <view class="faq-section">
                <view class="section-header">
                    <text class="section-title">反馈分类</text>
                    <text class="pick-text" @click="pickType">{{ selectedType || '请选择' }}</text>
                </view>
            </view>

            <view class="feedback-section">
                <textarea class="feedback-textarea" placeholder="请描述您遇到的问题或建议……" v-model="feedbackContent"></textarea>
            </view>

            <view class="submit-btn" @click="handleSubmit">
                <text class="submit-text">提交反馈</text>
            </view>
        </view>

        <view class="bottom-section">
            <view class="history-card">
                <text class="history-title">我的反馈记录</text>
                <view v-for="item in historyList" :key="item.id" class="history-item">
                    <text class="history-type">{{ item.feedbackType }}</text>
                    <text class="history-status">{{ item.status }}</text>
                    <text class="history-content">{{ item.content }}</text>
                </view>
                <text v-if="!historyList.length" class="history-empty">暂无反馈记录</text>
            </view>

            <view class="hotline-section">
                <text class="hotline-text">客服热线：{{ appSettings.serviceHotline || '--' }}</text>
            </view>
            <view class="bottom-space"></view>
        </view>
    </view>
</template>

<script>
import { createHelpFeedback, getHelpFeedbackPage } from '@/api/help'
import { getPlatformSettings } from '@/utils/auth'
import { getProfile } from '@/api/member'

export default {
    data() {
        return {
            searchText: '',
            feedbackContent: '',
            selectedType: '',
            types: [],
            appSettings: {},
            profile: {},
            sourceHistory: [],
            historyList: []
        }
    },
    onShow() {
        this.loadPageData()
    },
    methods: {
        async loadPageData() {
            try {
                const [profile, page] = await Promise.all([
                    getProfile(),
                    getHelpFeedbackPage({ pageNo: 1, pageSize: 20 }).catch(() => ({ list: [] }))
                ])
                this.appSettings = getPlatformSettings() || {}
                this.types = this.appSettings.feedbackTypes || ['功能建议', '异常反馈', '投诉建议']
                this.selectedType = this.selectedType || this.types[0]
                this.profile = profile || {}
                this.sourceHistory = page.list || []
                this.filterHistory()
            } catch (error) {
            }
        },
        pickType() {
            uni.showActionSheet({
                itemList: this.types,
                success: ({ tapIndex }) => {
                    this.selectedType = this.types[tapIndex]
                }
            })
        },
        filterHistory() {
            const keyword = this.searchText.trim()
            this.historyList = this.sourceHistory.filter((item) => !keyword || (item.content || '').includes(keyword))
        },
        async handleSubmit() {
            if (!this.selectedType || !this.feedbackContent.trim()) {
                uni.showToast({
                    title: '请选择分类并填写内容',
                    icon: 'none'
                })
                return
            }
            try {
                await createHelpFeedback({
                    feedbackType: this.selectedType,
                    content: this.feedbackContent.trim(),
                    contactMobile: this.profile.mobile || ''
                })
                uni.showToast({
                    title: '反馈已提交',
                    icon: 'success'
                })
                this.feedbackContent = ''
                this.loadPageData()
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
    background: #E8F4FD;
}

.header-bg {
    background: linear-gradient(180deg, #4A90F0 0%, #fff 100%);
    padding-bottom: 40rpx;
}

.header {
    padding: 60rpx 30rpx 30rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.back-icon {
    font-size: 40rpx;
    color: #fff;
    transform: rotate(180deg);
}

.title,
.main-title,
.sub-title {
    color: #fff;
}

.title-section {
    padding: 40rpx 30rpx 20rpx;
}

.main-title {
    display: block;
    font-size: 40rpx;
    font-weight: bold;
    margin-bottom: 12rpx;
}

.sub-title {
    font-size: 26rpx;
    color: rgba(255, 255, 255, 0.8);
}

.search-box,
.feedback-section,
.history-card {
    margin: 0 30rpx 20rpx;
    background: #fff;
    border-radius: 12rpx;
}

.search-box {
    padding: 0 24rpx;
    display: flex;
    align-items: center;
    height: 80rpx;
}

.search-icon {
    width: 36rpx;
    height: 36rpx;
    margin-right: 12rpx;
}

.search-input {
    flex: 1;
}

.search-btn {
    background: #F9A23F;
    border-radius: 6rpx;
    padding: 10rpx 24rpx;
}

.search-btn-text,
.submit-text {
    color: #fff;
}

.faq-section {
    margin: 0 30rpx 20rpx;
}

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.section-title,
.pick-text,
.history-title {
    font-size: 28rpx;
    font-weight: bold;
}

.pick-text {
    color: #fff;
}

.feedback-section {
    padding: 24rpx;
}

.feedback-textarea {
    width: 100%;
    height: 200rpx;
    background: #F5F7FA;
    border-radius: 12rpx;
    padding: 20rpx;
    box-sizing: border-box;
}

.submit-btn {
    margin: 20rpx 30rpx;
    background: #4A90F0;
    border-radius: 12rpx;
    padding: 28rpx;
    text-align: center;
}

.bottom-section {
    padding: 0 30rpx 30rpx;
}

.history-card {
    padding: 24rpx;
}

.history-item {
    padding: 16rpx 0;
    border-bottom: 1rpx solid #F0F0F0;
}

.history-type,
.history-status,
.history-content,
.history-empty,
.hotline-text {
    display: block;
    font-size: 24rpx;
    color: #666;
    line-height: 1.7;
}

.hotline-section {
    text-align: center;
}

.bottom-space {
    height: 60rpx;
}
</style>
