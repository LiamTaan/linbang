<template>
    <view class="page-container">
        <view class="header">
            <view class="back-btn" @click="goBack">
                <text class="iconfont icon-youjiantou back-icon"></text>
            </view>
            <text class="title">提现</text>
            <view class="detail-btn" @click="goDetail">
                <text class="detail-text">明细</text>
            </view>
        </view>

        <scroll-view class="content-scroll" scroll-y>
            <view class="balance-card">
                <view class="balance-info">
                    <text class="balance-label">可提现金额</text>
                    <text class="balance-amount">¥{{ $fmt.formatMoney(wallet.availableAmount) }}</text>
                </view>
                <view class="balance-right">
                    <view class="yuan-circle">
                        <image class="yuan-icon" src="/static/img/withdraw_deposit/yuan@3x.png" />
                    </view>
                    <view class="all-withdraw-btn" @click="handleAllWithdraw">
                        <text class="all-withdraw-text">全部提现</text>
                    </view>
                </view>
            </view>

            <view class="content-scroll-item">
                <view class="amount-card">
                    <text class="card-title">提现金额</text>
                    <view class="amount-grid">
                        <view
                            v-for="amount in quickAmounts"
                            :key="amount.value"
                            class="amount-item"
                            :class="{ active: selectedAmount === amount.value }"
                            @click="selectAmount(amount.value)">
                            <text class="amount-text">{{ amount.label }}</text>
                        </view>
                        <view
                            class="amount-item custom-item"
                            :class="{ active: selectedAmountMode === 'custom' }"
                            @click="selectCustomAmount">
                            <text class="amount-text">自定义</text>
                            <image class="custom-icon" src="/static/img/withdraw_deposit/pen@3x.png" />
                        </view>
                    </view>
                    <input
                        v-if="selectedAmountMode === 'custom'"
                        class="custom-input"
                        type="digit"
                        v-model="customAmount"
                        placeholder="请输入提现金额" />
                </view>

                <view class="method-card">
                    <view class="method-item bank-section">
                        <view class="method-left">
                            <image class="method-icon" src="/static/img/withdraw_deposit/bank_card@3x.png" />
                            <text class="method-title">提现至银行卡</text>
                        </view>
                        <view class="bank-info-row">
                            <view class="bank-info-left">
                                <image class="bank-icon" src="/static/img/withdraw_deposit/icbc@3x.png" />
                                <text class="bank-text">{{ currentBankCardText }}</text>
                            </view>
                            <text class="change-btn" @click="chooseBankCard">更换></text>
                        </view>
                    </view>

                    <view class="method-item">
                        <view class="method-left">
                            <image class="method-icon" src="/static/img/withdraw_deposit/alipay@3x.png" />
                            <text class="method-title">提现至支付宝</text>
                        </view>
                        <text class="bind-btn" @click="showBankOnlyNotice">暂未开放></text>
                    </view>

                    <view class="method-item last">
                        <view class="method-left">
                            <image class="method-icon" src="/static/img/withdraw_deposit/wechat@3x.png" />
                            <text class="method-title">提现至微信</text>
                        </view>
                        <text class="bind-btn" @click="showBankOnlyNotice">暂未开放></text>
                    </view>
                </view>

                <view class="warning-bar">
                    <image class="warning-icon" src="/static/img/withdraw_deposit/warning@3x.png" />
                    <text class="warning-text">{{ withdrawNotice }}</text>
                </view>

                <view class="verify-tip">
                    <text class="verify-text">{{ wallet.realNameVerified ? '身份验证已通过' : '请先完成实名认证' }}</text>
                    <image class="verify-icon" src="/static/img/withdraw_deposit/passed@3x.png" />
                </view>

                <view class="submit-btn" @click="handleSubmit">
                    <text class="submit-text">确认提现</text>
                </view>

                <text class="bottom-tip">
                    提现将扣除相应税费，最低提现金额 ¥{{ $fmt.formatMoney(wallet.minWithdrawAmount || 10) }}
                </text>

                <view class="history-card">
                    <text class="history-title">最近提现记录</text>
                    <view v-for="item in withdrawList" :key="item.id" class="history-item">
                        <view>
                            <text class="history-amount">¥{{ $fmt.formatMoney(item.applyAmount) }}</text>
                            <text class="history-status">{{ item.status }}</text>
                        </view>
                        <text class="history-time">{{ $fmt.formatDateTime(item.createTime) }}</text>
                    </view>
                    <text v-if="!withdrawList.length" class="history-empty">暂无提现记录</text>
                </view>

                <view class="bottom-space"></view>
            </view>
        </scroll-view>
    </view>
</template>

<script>
import { getPlatformSettings } from '@/utils/auth'
import { createWithdraw, getBankCardPage, getWalletAccount, getWithdrawPage } from '@/api/wallet'

export default {
    data() {
        return {
            wallet: {},
            bankCards: [],
            selectedBankCardId: null,
            withdrawList: [],
            selectedAmount: '',
            selectedAmountMode: '',
            customAmount: '',
            quickAmounts: [
                { label: '10元', value: '10' },
                { label: '20元', value: '20' },
                { label: '50元', value: '50' },
                { label: '100元', value: '100' },
                { label: '200元', value: '200' },
                { label: '500元', value: '500' }
            ]
        }
    },
    computed: {
        currentBankCard() {
            return this.bankCards.find((item) => item.id === this.selectedBankCardId) || this.bankCards[0] || null
        },
        currentBankCardText() {
            if (!this.currentBankCard) {
                return '请先绑定银行卡'
            }
            return `${this.currentBankCard.bankName} (${this.currentBankCard.cardNoMask})`
        },
        withdrawNotice() {
            const settings = getPlatformSettings() || {}
            return settings.withdrawNotice || '提现审核通过后预计 T+1 到账'
        }
    },
    onShow() {
        this.loadPageData()
    },
    methods: {
        async loadPageData() {
            try {
                const [wallet, bankCardPage, withdrawPage] = await Promise.all([
                    getWalletAccount(),
                    getBankCardPage({ pageNo: 1, pageSize: 20 }),
                    getWithdrawPage({ pageNo: 1, pageSize: 5 })
                ])
                this.wallet = wallet || {}
                this.bankCards = bankCardPage.list || []
                const defaultCard = this.bankCards.find((item) => item.isDefault) || this.bankCards[0]
                this.selectedBankCardId = defaultCard ? defaultCard.id : null
                this.withdrawList = withdrawPage.list || []
            } catch (error) {
            }
        },
        selectedAmountValue() {
            return this.selectedAmountMode === 'custom' ? this.customAmount : this.selectedAmount
        },
        selectAmount(amount) {
            this.selectedAmount = amount
            this.selectedAmountMode = 'quick'
            this.customAmount = ''
        },
        selectCustomAmount() {
            this.selectedAmountMode = 'custom'
            this.selectedAmount = ''
        },
        handleAllWithdraw() {
            this.selectedAmountMode = 'custom'
            this.customAmount = this.$fmt.formatMoney(this.wallet.availableAmount)
        },
        chooseBankCard() {
            if (!this.bankCards.length) {
                uni.navigateTo({
                    url: '/pages/bank_card_management/bank_card_management'
                })
                return
            }
            uni.showActionSheet({
                itemList: this.bankCards.map((item) => `${item.bankName} ${item.cardNoMask}`),
                success: ({ tapIndex }) => {
                    const card = this.bankCards[tapIndex]
                    if (card) {
                        this.selectedBankCardId = card.id
                    }
                }
            })
        },
        showBankOnlyNotice() {
            uni.showToast({
                title: '当前提现能力仅开放银行卡',
                icon: 'none'
            })
        },
        async handleSubmit() {
            if (!this.wallet.realNameVerified) {
                uni.showToast({
                    title: '请先完成实名认证',
                    icon: 'none'
                })
                return
            }
            if (!this.currentBankCard) {
                uni.showToast({
                    title: '请先绑定银行卡',
                    icon: 'none'
                })
                return
            }
            const amount = this.selectedAmountValue()
            if (!amount || Number(amount) <= 0) {
                uni.showToast({
                    title: '请选择提现金额',
                    icon: 'none'
                })
                return
            }
            uni.showModal({
                title: '确认提现',
                content: `确认提现¥${amount}到${this.currentBankCard.bankName}？`,
                success: async (res) => {
                    if (!res.confirm) {
                        return
                    }
                    try {
                        await createWithdraw({
                            bankCardId: this.currentBankCard.id,
                            applyAmount: amount
                        })
                        uni.showToast({
                            title: '提现申请已提交',
                            icon: 'success'
                        })
                        this.selectedAmount = ''
                        this.customAmount = ''
                        this.selectedAmountMode = ''
                        this.loadPageData()
                    } catch (error) {
                    }
                }
            })
        },
        goBack() {
            uni.navigateBack()
        },
        goDetail() {
            uni.navigateTo({ url: '/pages/detail_of_earnings/detail_of_earnings' })
        }
    }
}
</script>

<style lang="scss" scoped>
.page-container {
    min-height: 100vh;
    background: #9cc7ff;

    .header {
        background: #9cc7ff;
        padding: 60rpx 30rpx 30rpx;
        display: flex;
        justify-content: space-between;
        align-items: center;

        .back-btn,
        .detail-btn {
            width: 80rpx;
        }

        .back-icon {
            font-size: 40rpx;
            color: #000;
            transform: rotate(180deg);
        }

        .title {
            font-size: 34rpx;
            font-weight: bold;
            color: #000;
        }

        .detail-text {
            font-size: 28rpx;
            color: #000;
        }
    }

    .content-scroll {
        box-sizing: border-box;

        .balance-card {
            background: linear-gradient(-49deg, #2E83F0, #7BB8FE);
            min-height: 300rpx;
            border-radius: 20rpx;
            padding: 30rpx;
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin: 50rpx 20rpx -20rpx;
            position: relative;
            z-index: 1;

            .balance-info {
                margin-top: 50rpx;
            }

            .balance-label {
                font-size: 26rpx;
                color: rgba(255, 255, 255, 0.8);
                display: block;
                margin-bottom: 12rpx;
            }

            .balance-amount {
                font-size: 56rpx;
                font-weight: bold;
                color: #fff;
            }

            .yuan-circle {
                position: absolute;
                top: -40rpx;
                right: 40rpx;
                z-index: 2;
            }

            .yuan-icon {
                width: 258rpx;
                height: 258rpx;
            }

            .all-withdraw-btn {
                background: #fff;
                padding: 12rpx 24rpx;
                border-radius: 24rpx;
                position: absolute;
                bottom: 80rpx;
                right: 40rpx;
                z-index: 3;
            }

            .all-withdraw-text {
                font-size: 26rpx;
                color: #4A90F0;
                font-weight: bold;
            }
        }

        .content-scroll-item {
            padding: 20rpx;
            background-color: #fff;
            border-radius: 38rpx 38rpx 0 0;
            min-height: calc(100vh - 200rpx);
            position: relative;
            z-index: 2;
        }

        .amount-card,
        .method-card,
        .history-card {
            border-radius: 16rpx;
            padding: 24rpx;
            margin-bottom: 20rpx;
        }

        .amount-card,
        .history-card {
            background: #fff;
        }

        .card-title,
        .history-title {
            font-size: 28rpx;
            font-weight: bold;
            color: #333;
            margin-bottom: 20rpx;
            display: block;
        }

        .amount-grid {
            display: flex;
            flex-wrap: wrap;
            gap: 16rpx;
        }

        .amount-item {
            width: calc(33.33% - 12rpx);
            padding: 20rpx 12rpx;
            border: 2rpx solid #E8E8E8;
            border-radius: 12rpx;
            text-align: center;

            &.active {
                border-color: #4A90F0;
                background: #E8F4FD;

                .amount-text {
                    color: #4A90F0;
                }
            }

            &.custom-item {
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 8rpx;
            }
        }

        .amount-text {
            font-size: 26rpx;
            color: #333;
        }

        .custom-icon {
            width: 35rpx;
            height: 30rpx;
        }

        .custom-input {
            height: 84rpx;
            border-radius: 12rpx;
            background: #F7F9FC;
            padding: 0 24rpx;
            font-size: 26rpx;
            color: #333;
            margin-top: 20rpx;
        }

        .method-card {
            background: linear-gradient(1deg, #F7FBFF, #D7EAFF);
        }

        .method-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 24rpx 0;
            border-bottom: 1rpx solid #C5DDFA;

            &.last {
                border-bottom: none;
            }

            &.bank-section {
                flex-direction: column;
                align-items: flex-start;
                gap: 16rpx;
            }
        }

        .method-left,
        .bank-info-left {
            display: flex;
            align-items: center;
            gap: 16rpx;
        }

        .bank-info-row {
            width: 100%;
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding-left: 64rpx;
        }

        .method-icon,
        .bank-icon {
            width: 48rpx;
            height: 48rpx;
        }

        .method-title {
            font-size: 28rpx;
            color: #333;
        }

        .bank-text,
        .change-btn,
        .bind-btn {
            font-size: 26rpx;
            color: #4A90F0;
        }

        .warning-bar {
            background: #FFF8E1;
            border-radius: 12rpx;
            padding: 20rpx;
            display: flex;
            align-items: center;
            gap: 12rpx;
            margin-bottom: 20rpx;
        }

        .warning-icon {
            width: 36rpx;
            height: 36rpx;
        }

        .warning-text {
            font-size: 24rpx;
            color: #FA9D3B;
        }

        .verify-tip {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8rpx;
            margin-bottom: 30rpx;
        }

        .verify-text {
            font-size: 24rpx;
            color: #2E83F0;
        }

        .verify-icon {
            width: 28rpx;
            height: 28rpx;
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
            font-size: 22rpx;
            color: #999;
            text-align: center;
            display: block;
            margin-bottom: 20rpx;
        }

        .history-item {
            padding: 18rpx 0;
            border-bottom: 1rpx solid #F0F0F0;

            &:last-child {
                border-bottom: none;
            }
        }

        .history-amount {
            display: block;
            font-size: 28rpx;
            color: #333;
            font-weight: bold;
            margin-bottom: 8rpx;
        }

        .history-status,
        .history-time,
        .history-empty {
            font-size: 24rpx;
            color: #999;
        }

        .history-empty {
            display: block;
            text-align: center;
            padding: 20rpx 0;
        }

        .bottom-space {
            height: 60rpx;
        }
    }
}
</style>
