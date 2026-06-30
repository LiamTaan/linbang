<template>
    <view class="page-container">
        <view class="hero">
            <view class="hero-smile">
                <view class="smile-eye left"></view>
                <view class="smile-eye right"></view>
                <view class="smile-mouth"></view>
            </view>

            <view class="header">
                <view class="back-btn" @click="goBack">
                    <text class="iconfont icon-youjiantou back-icon"></text>
                </view>
                <text class="nav-title">帮助与反馈</text>
                <view class="placeholder"></view>
            </view>

            <view class="title-section">
                <text class="main-title">帮助与反馈</text>
                <text class="sub-title">给出您的建议反馈吧~</text>
            </view>

            <view class="search-box">
                <image class="search-icon" src="/static/img/feedback/search@3x.png" />
                <input
                    class="search-input"
                    placeholder="快速搜索您遇到的问题"
                    placeholder-class="search-placeholder"
                    v-model="searchText"
                    confirm-type="search"
                    @confirm="loadFaqList"
                />
                <view class="search-btn" @click="loadFaqList">
                    <text class="search-btn-text">搜索</text>
                </view>
            </view>

            <view class="faq-section">
                <text class="section-title">常见问题</text>
                <view v-if="faqLoading" class="faq-empty">正在加载常见问题...</view>
                <view v-else-if="!faqList.length" class="faq-empty">暂无匹配问题</view>
                <view v-else>
                    <view v-for="item in faqList" :key="item.id || item.categoryCode" class="faq-card" @click="openFaq(item)">
                        <image class="faq-icon" :src="getFaqIcon(item.icon)" />
                        <view class="faq-copy">
                            <text class="faq-title">{{ item.categoryName || item.title }}</text>
                            <text class="faq-desc">{{ item.title }}</text>
                        </view>
                        <text class="iconfont icon-youjiantou faq-arrow"></text>
                    </view>
                </view>
            </view>
        </view>

        <view class="content">
            <view class="feedback-card">
                <text class="feedback-title">意见反馈</text>
                <textarea
                    class="feedback-textarea"
                    placeholder="请描述您遇到的问题或建议……"
                    placeholder-class="textarea-placeholder"
                    maxlength="2000"
                    v-model="feedbackContent"
                ></textarea>
                <view class="feedback-meta">
                    <text class="type-label">反馈分类</text>
                    <view class="type-picker" @click="pickType">
                        <text>{{ selectedType || '请选择' }}</text>
                        <text class="iconfont icon-youjiantou type-arrow"></text>
                    </view>
                </view>
            </view>

            <view class="submit-btn" :class="{ disabled: submitting }" @click="handleSubmit">
                <text class="submit-text">{{ submitting ? '提交中...' : '提交反馈' }}</text>
            </view>

            <view class="contact-row">
                <view class="contact-btn" @click="openOnlineService">
                    <image class="contact-icon" src="/static/img/feedback/feedback@3x.png" />
                    <text>在线客服</text>
                </view>
                <view class="contact-btn" @click="callService">
                    <image class="contact-icon" src="/static/img/feedback/phone@3x.png" />
                    <text>电话客服</text>
                </view>
                <view class="contact-btn" @click="loadHistory">
                    <image class="contact-icon" src="/static/img/feedback/info@3x.png" />
                    <text>意见反馈</text>
                </view>
            </view>

            <view class="hotline-section">
                <text class="hotline-text">客服热线：{{ appSettings.serviceHotline || '400-888-8888' }}</text>
            </view>
            <view class="bottom-space"></view>
        </view>
    </view>
</template>

<script>
import { createHelpFeedback, getHelpFaqList, getHelpFeedbackPage } from '@/api/help'
import { getAppSettings } from '@/api/platform'
import { getPlatformSettings, setPlatformSettings } from '@/utils/auth'
import { getProfile } from '@/api/member'

const DEFAULT_FAQS = [
    {
        id: 'default-funds',
        categoryCode: 'FUNDS',
        categoryName: '资金与提现',
        title: '提现、托管金额、冻结金额相关问题',
        icon: 'fund'
    },
    {
        id: 'default-order',
        categoryCode: 'ORDER',
        categoryName: '订单相关',
        title: '下单、接单、取消、退款和订单状态问题',
        icon: 'order'
    },
    {
        id: 'default-auth',
        categoryCode: 'AUTH',
        categoryName: '认证与入驻',
        title: '实名、服务商入驻、资质认证和证件到期问题',
        icon: 'verify'
    },
    {
        id: 'default-match',
        categoryCode: 'MATCH',
        categoryName: '抢单与匹配',
        title: '抢单规则、匹配失败、接单状态管理问题',
        icon: 'order_match'
    },
    {
        id: 'default-promote',
        categoryCode: 'PROMOTE',
        categoryName: '推广与佣金',
        title: '推广收益、佣金结算、邀请关系问题',
        icon: 'voice'
    }
]

export default {
    data() {
        return {
            searchText: '',
            feedbackContent: '',
            selectedType: '',
            types: [],
            appSettings: {},
            profile: {},
            faqList: [],
            faqLoading: false,
            submitting: false
        }
    },
    onShow() {
        this.loadPageData()
    },
    methods: {
        async loadPageData() {
            this.appSettings = getPlatformSettings() || {}
            this.types = this.appSettings.feedbackTypes || ['功能建议', '异常反馈', '投诉建议']
            this.selectedType = this.selectedType || this.types[0]
            await Promise.all([this.loadSettings(), this.loadProfile(), this.loadFaqList()])
        },
        async loadSettings() {
            try {
                const settings = await getAppSettings()
                this.appSettings = settings || this.appSettings
                setPlatformSettings(this.appSettings)
                this.types = this.appSettings.feedbackTypes && this.appSettings.feedbackTypes.length
                    ? this.appSettings.feedbackTypes
                    : this.types
                this.selectedType = this.types.includes(this.selectedType) ? this.selectedType : this.types[0]
            } catch (error) {
            }
        },
        async loadProfile() {
            try {
                this.profile = await getProfile()
            } catch (error) {
                this.profile = {}
            }
        },
        async loadFaqList() {
            this.faqLoading = true
            try {
                const list = await getHelpFaqList({ keyword: this.searchText.trim() })
                this.faqList = list && list.length ? list : DEFAULT_FAQS
            } catch (error) {
                this.faqList = DEFAULT_FAQS
            } finally {
                this.faqLoading = false
            }
        },
        getFaqIcon(icon) {
            const iconMap = {
                fund: '/static/img/feedback/fund@3x.png',
                order: '/static/img/feedback/order@3x.png',
                verify: '/static/img/feedback/verify@3x.png',
                order_match: '/static/img/feedback/order_match@3x.png',
                voice: '/static/img/feedback/voice@3x.png',
                info: '/static/img/feedback/info@3x.png'
            }
            return iconMap[icon] || iconMap.info
        },
        openFaq(item) {
            uni.showModal({
                title: item.title || item.categoryName || '常见问题',
                content: item.content || '该问题已纳入后台常见问题配置，可在管理端维护详细说明。',
                showCancel: false,
                confirmText: '知道了'
            })
        },
        pickType() {
            uni.showActionSheet({
                itemList: this.types,
                success: ({ tapIndex }) => {
                    this.selectedType = this.types[tapIndex]
                }
            })
        },
        async handleSubmit() {
            if (this.submitting) {
                return
            }
            if (!this.selectedType || !this.feedbackContent.trim()) {
                uni.showToast({
                    title: '请选择分类并填写内容',
                    icon: 'none'
                })
                return
            }
            this.submitting = true
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
            } finally {
                this.submitting = false
            }
        },
        async loadHistory() {
            try {
                const page = await getHelpFeedbackPage({ pageNo: 1, pageSize: 5 })
                const count = (page && page.total) || 0
                uni.showToast({
                    title: count ? `已有 ${count} 条反馈记录` : '暂无反馈记录',
                    icon: 'none'
                })
            } catch (error) {
            }
        },
        openOnlineService() {
            const serviceWechat = this.appSettings.serviceWechat
            uni.showToast({
                title: serviceWechat ? `客服微信：${serviceWechat}` : '在线客服暂未配置',
                icon: 'none'
            })
        },
        callService() {
            const phone = this.appSettings.serviceHotline
            if (!phone) {
                uni.showToast({
                    title: '客服电话暂未配置',
                    icon: 'none'
                })
                return
            }
            uni.makePhoneCall({ phoneNumber: phone })
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
    background: #FFFFFF;
}

.hero {
    position: relative;
    overflow: hidden;
    background: linear-gradient(180deg, #2F86F6 0%, #7DB6F9 58%, #EAF5FF 100%);
    padding-bottom: 18rpx;
}

.hero-smile {
    position: absolute;
    top: 88rpx;
    right: 44rpx;
    width: 240rpx;
    height: 230rpx;
    opacity: 0.36;
}

.smile-eye {
    position: absolute;
    top: 38rpx;
    width: 42rpx;
    height: 76rpx;
    border-radius: 28rpx;
    background: rgba(255, 255, 255, 0.82);
}

.smile-eye.left {
    left: 32rpx;
}

.smile-eye.right {
    right: 32rpx;
}

.smile-mouth {
    position: absolute;
    left: 32rpx;
    right: 32rpx;
    bottom: 36rpx;
    height: 72rpx;
    border-bottom: 24rpx solid rgba(255, 255, 255, 0.82);
    border-radius: 0 0 130rpx 130rpx;
    transform: rotate(3deg);
}

.header {
    position: relative;
    z-index: 1;
    padding: 72rpx 30rpx 24rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.back-btn,
.placeholder {
    width: 56rpx;
    height: 56rpx;
}

.back-icon {
    display: block;
    font-size: 38rpx;
    color: #fff;
    transform: rotate(180deg);
}

.nav-title {
    font-size: 34rpx;
    font-weight: 700;
    color: #fff;
}

.title-section {
    position: relative;
    z-index: 1;
    padding: 16rpx 58rpx 28rpx;
}

.main-title {
    display: block;
    font-size: 38rpx;
    font-weight: 700;
    color: #fff;
    line-height: 52rpx;
}

.sub-title {
    display: block;
    margin-top: 4rpx;
    font-size: 24rpx;
    color: rgba(255, 255, 255, 0.84);
}

.search-box {
    position: relative;
    z-index: 1;
    margin: 0 32rpx 28rpx;
    height: 78rpx;
    padding: 0 8rpx 0 26rpx;
    display: flex;
    align-items: center;
    background: rgba(255, 255, 255, 0.8);
    border-radius: 10rpx;
}

.search-icon {
    width: 32rpx;
    height: 32rpx;
    margin-right: 14rpx;
}

.search-input {
    flex: 1;
    height: 78rpx;
    font-size: 26rpx;
    color: #32445C;
}

.search-placeholder {
    color: #8BB4E5;
}

.search-btn {
    width: 108rpx;
    height: 58rpx;
    border-radius: 8rpx;
    background: #FFA941;
    display: flex;
    align-items: center;
    justify-content: center;
}

.search-btn-text {
    font-size: 26rpx;
    font-weight: 600;
    color: #fff;
}

.faq-section {
    position: relative;
    z-index: 1;
    margin: 0 32rpx;
}

.section-title {
    display: block;
    margin-bottom: 16rpx;
    font-size: 28rpx;
    font-weight: 700;
    color: #fff;
}

.faq-card {
    height: 84rpx;
    margin-bottom: 22rpx;
    padding: 0 28rpx;
    display: flex;
    align-items: center;
    background: #fff;
    border-radius: 10rpx;
    box-shadow: 0 10rpx 24rpx rgba(43, 111, 188, 0.08);
}

.faq-icon {
    width: 34rpx;
    height: 34rpx;
    margin-right: 18rpx;
}

.faq-copy {
    flex: 1;
    min-width: 0;
}

.faq-title {
    display: block;
    font-size: 28rpx;
    font-weight: 600;
    color: #3B4657;
    line-height: 36rpx;
}

.faq-desc {
    display: block;
    margin-top: 2rpx;
    font-size: 21rpx;
    color: #9AA7B6;
    line-height: 28rpx;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}

.faq-arrow {
    font-size: 28rpx;
    color: #B9C1CC;
}

.faq-empty {
    height: 88rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(255, 255, 255, 0.78);
    border-radius: 10rpx;
    font-size: 26rpx;
    color: #6D87A8;
}

.content {
    margin-top: -2rpx;
    padding: 0 32rpx 32rpx;
    background: #FFFFFF;
}

.feedback-card {
    padding: 24rpx;
    background: #fff;
    border-radius: 10rpx;
    box-shadow: 0 10rpx 24rpx rgba(43, 111, 188, 0.08);
}

.feedback-title {
    display: block;
    margin-bottom: 16rpx;
    font-size: 26rpx;
    font-weight: 700;
    color: #495565;
}

.feedback-textarea {
    width: 100%;
    height: 152rpx;
    padding: 18rpx;
    box-sizing: border-box;
    background: #F5F6F8;
    border-radius: 8rpx;
    font-size: 24rpx;
    color: #38465A;
    line-height: 34rpx;
}

.textarea-placeholder {
    color: #B6BDC8;
}

.feedback-meta {
    margin-top: 18rpx;
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.type-label {
    font-size: 24rpx;
    color: #687587;
}

.type-picker {
    min-width: 180rpx;
    height: 52rpx;
    padding: 0 16rpx;
    border-radius: 8rpx;
    background: #EDF5FF;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24rpx;
    color: #2F86F6;
}

.type-arrow {
    margin-left: 8rpx;
    font-size: 22rpx;
    transform: rotate(90deg);
}

.submit-btn {
    margin-top: 28rpx;
    height: 92rpx;
    border-radius: 10rpx;
    background: #2F86F6;
    display: flex;
    align-items: center;
    justify-content: center;
}

.submit-btn.disabled {
    opacity: 0.68;
}

.submit-text {
    font-size: 34rpx;
    font-weight: 700;
    color: #fff;
}

.contact-row {
    margin-top: 34rpx;
    display: flex;
    justify-content: space-between;
}

.contact-btn {
    width: 150rpx;
    height: 54rpx;
    border-radius: 6rpx;
    border: 2rpx solid #9DC7F3;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 22rpx;
    color: #2F86F6;
    background: #fff;
}

.contact-icon {
    width: 24rpx;
    height: 24rpx;
    margin-right: 6rpx;
}

.hotline-section {
    margin-top: 26rpx;
    text-align: center;
}

.hotline-text {
    font-size: 21rpx;
    color: #A4ADB8;
}

.bottom-space {
    height: 42rpx;
}
</style>
