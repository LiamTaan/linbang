<template>
    <view class="page-container">
        <view class="header">
            <view class="back-btn" @click="goBack">
                <text class="iconfont icon-youjiantou back-icon"></text>
            </view>
            <text class="title">银行卡管理</text>
            <view class="placeholder"></view>
        </view>

        <scroll-view class="content-scroll" scroll-y>
            <view class="card-list">
                <view
                    v-for="card in bankCards"
                    :key="card.id"
                    class="bank-card"
                    :style="{ background: resolveCardBackground(card.bankCode) }">
                    <view class="card-content">
                        <view class="card-left">
                            <view class="card-tag">
                                <image class="bank-icon" :src="resolveBankIcon(card.bankCode)" />
                            </view>
                            <view class="bank-info">
                                <text class="bank-name">{{ card.bankName }}</text>
                                <text class="card-type">
                                    {{ card.accountName }} · {{ card.transferEnabled ? '可提现' : '仅展示' }}
                                </text>
                            </view>
                        </view>
                        <text v-if="card.isDefault" class="default-tag">默认卡</text>
                    </view>
                    <text class="card-number">{{ card.cardNoMask }}</text>
                    <view class="card-actions">
                        <text class="action-text" @click="startEdit(card)">编辑</text>
                        <text v-if="!card.isDefault" class="action-text" @click="setDefault(card)">设为默认</text>
                        <text class="action-text danger" @click="removeCard(card)">删除</text>
                    </view>
                </view>
                <view v-if="!bankCards.length" class="empty-card">
                    <text class="empty-text">暂未绑定银行卡</text>
                </view>
            </view>

            <view class="add-card-btn" @click="startCreate">
                <text class="add-text">+添加银行卡</text>
            </view>

            <view v-if="formVisible" class="form-card">
                <text class="form-title">{{ editingId ? '编辑银行卡' : '新增银行卡' }}</text>
                <input class="form-input" v-model="form.bankName" placeholder="银行名称，例如 中国工商银行" />
                <input class="form-input" v-model="form.bankCode" placeholder="银行编码，例如 ICBC" />
                <input
                    v-if="!editingId"
                    class="form-input"
                    v-model="form.cardNo"
                    type="number"
                    placeholder="银行卡号" />
                <input class="form-input" v-model="form.accountName" placeholder="开户名" />
                <input
                    class="form-input"
                    v-model="form.reservedMobile"
                    type="number"
                    placeholder="预留手机号（可选）" />
                <view class="default-switch" @click="form.isDefault = !form.isDefault">
                    <text class="default-label">设为默认银行卡</text>
                    <view class="switch-box" :class="{ active: form.isDefault }">
                        <view class="switch-dot"></view>
                    </view>
                </view>
                <view class="form-actions">
                    <view class="form-btn ghost" @click="cancelForm">取消</view>
                    <view class="form-btn primary" @click="submitForm">保存</view>
                </view>
            </view>

            <view class="tip-card">
                <view class="tip-header">
                    <image class="tip-icon" src="/static/img/bank_card_management/warning@3x.png" />
                    <text class="tip-title">温馨提示</text>
                </view>
                <view class="tip-list">
                    <text class="tip-item">• 仅支持绑定本人名下的银行卡</text>
                    <text class="tip-item">• 银行卡信息将用于提现和退款</text>
                    <text class="tip-item">• 最多可绑定五张银行卡</text>
                </view>
            </view>

            <view class="bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
import {
    createBankCard,
    deleteBankCard,
    getBankCardPage,
    setDefaultBankCard,
    updateBankCard
} from '@/api/wallet'

function createEmptyForm() {
    return {
        bankName: '',
        bankCode: '',
        cardNo: '',
        accountName: '',
        reservedMobile: '',
        isDefault: false
    }
}

export default {
    data() {
        return {
            bankCards: [],
            formVisible: false,
            editingId: null,
            form: createEmptyForm()
        }
    },
    onShow() {
        this.loadCards()
    },
    methods: {
        async loadCards() {
            try {
                const page = await getBankCardPage({
                    pageNo: 1,
                    pageSize: 50
                })
                this.bankCards = page.list || []
            } catch (error) {
            }
        },
        resolveBankIcon(bankCode) {
            if (bankCode === 'CCB') {
                return '/static/img/bank_card_management/ccb@3x.png'
            }
            if (bankCode === 'PSBC') {
                return '/static/img/bank_card_management/psbc@3x.png'
            }
            return '/static/img/bank_card_management/icbc@3x.png'
        },
        resolveCardBackground(bankCode) {
            if (bankCode === 'CCB') {
                return 'linear-gradient(135deg, #004F9C 0%, #164A8F 100%)'
            }
            if (bankCode === 'PSBC') {
                return 'linear-gradient(135deg, #128B3E 0%, #23703D 100%)'
            }
            return 'linear-gradient(135deg, #D64C50 0%, #C43A3A 100%)'
        },
        startCreate() {
            this.editingId = null
            this.form = createEmptyForm()
            this.formVisible = true
        },
        startEdit(card) {
            this.editingId = card.id
            this.form = {
                bankName: card.bankName || '',
                bankCode: card.bankCode || '',
                cardNo: '',
                accountName: card.accountName || '',
                reservedMobile: card.reservedMobile || '',
                isDefault: !!card.isDefault
            }
            this.formVisible = true
        },
        cancelForm() {
            this.formVisible = false
            this.editingId = null
            this.form = createEmptyForm()
        },
        async submitForm() {
            if (!this.form.bankName || !this.form.accountName) {
                uni.showToast({
                    title: '请填写完整银行卡信息',
                    icon: 'none'
                })
                return
            }
            if (!this.editingId && !this.form.cardNo) {
                uni.showToast({
                    title: '请输入银行卡号',
                    icon: 'none'
                })
                return
            }
            try {
                if (this.editingId) {
                    await updateBankCard({
                        id: this.editingId,
                        bankName: this.form.bankName,
                        bankCode: this.form.bankCode,
                        accountName: this.form.accountName,
                        reservedMobile: this.form.reservedMobile,
                        isDefault: this.form.isDefault
                    })
                } else {
                    await createBankCard({
                        bankName: this.form.bankName,
                        bankCode: this.form.bankCode,
                        cardNo: this.form.cardNo,
                        accountName: this.form.accountName,
                        reservedMobile: this.form.reservedMobile,
                        isDefault: this.form.isDefault
                    })
                }
                uni.showToast({
                    title: '保存成功',
                    icon: 'success'
                })
                this.cancelForm()
                this.loadCards()
            } catch (error) {
            }
        },
        async setDefault(card) {
            try {
                await setDefaultBankCard({ id: card.id })
                uni.showToast({
                    title: '默认卡已更新',
                    icon: 'success'
                })
                this.loadCards()
            } catch (error) {
            }
        },
        removeCard(card) {
            uni.showModal({
                title: '确认删除',
                content: `确认删除 ${card.bankName} ${card.cardNoMask} 吗？`,
                success: async (res) => {
                    if (!res.confirm) {
                        return
                    }
                    try {
                        await deleteBankCard(card.id)
                        uni.showToast({
                            title: '删除成功',
                            icon: 'success'
                        })
                        this.loadCards()
                    } catch (error) {
                    }
                }
            })
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
        padding: 20rpx;
        box-sizing: border-box;

        .card-list {
            .bank-card,
            .empty-card {
                border-radius: 16rpx;
                padding: 24rpx;
                margin-bottom: 20rpx;
            }

            .empty-card {
                background: #fff;
                text-align: center;

                .empty-text {
                    font-size: 24rpx;
                    color: #999;
                }
            }

            .bank-card {
                color: #fff;

                .card-content {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 20rpx;
                }

                .card-left {
                    display: flex;
                    align-items: center;
                    gap: 16rpx;
                }

                .card-tag {
                    width: 48rpx;
                    height: 48rpx;
                    border-radius: 50%;
                    background-color: #fff;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                }

                .bank-icon {
                    width: 30rpx;
                    height: 30rpx;
                }

                .bank-info {
                    display: flex;
                    flex-direction: column;
                    gap: 4rpx;
                }

                .bank-name {
                    font-size: 28rpx;
                    font-weight: bold;
                }

                .card-type {
                    font-size: 22rpx;
                    color: rgba(255, 255, 255, 0.8);
                }

                .default-tag {
                    font-size: 22rpx;
                    background: rgba(255, 255, 255, 0.2);
                    padding: 8rpx 16rpx;
                    border-radius: 20rpx;
                }

                .card-number {
                    display: block;
                    font-size: 28rpx;
                    font-weight: bold;
                    letter-spacing: 4rpx;
                    margin-bottom: 20rpx;
                }

                .card-actions {
                    display: flex;
                    justify-content: flex-end;
                    gap: 24rpx;

                    .action-text {
                        font-size: 24rpx;
                        color: #fff;

                        &.danger {
                            color: #FFD5D5;
                        }
                    }
                }
            }
        }

        .add-card-btn {
            border: 2rpx dashed #4A90F0;
            border-radius: 16rpx;
            padding: 36rpx;
            text-align: center;
            margin-bottom: 20rpx;

            .add-text {
                font-size: 28rpx;
                color: #4A90F0;
                font-weight: bold;
            }
        }

        .form-card,
        .tip-card {
            background: #fff;
            border-radius: 16rpx;
            padding: 24rpx;
            margin-bottom: 20rpx;
        }

        .form-title {
            display: block;
            font-size: 28rpx;
            font-weight: bold;
            color: #333;
            margin-bottom: 20rpx;
        }

        .form-input {
            height: 88rpx;
            border-radius: 12rpx;
            background: #F7F9FC;
            padding: 0 24rpx;
            font-size: 26rpx;
            color: #333;
            margin-bottom: 16rpx;
        }

        .default-switch {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 12rpx 0 24rpx;

            .default-label {
                font-size: 26rpx;
                color: #333;
            }

            .switch-box {
                width: 100rpx;
                height: 56rpx;
                background: #E8E8E8;
                border-radius: 28rpx;
                position: relative;

                &.active {
                    background: #4A90F0;

                    .switch-dot {
                        transform: translateX(44rpx);
                    }
                }

                .switch-dot {
                    width: 48rpx;
                    height: 48rpx;
                    background: #fff;
                    border-radius: 50%;
                    position: absolute;
                    top: 4rpx;
                    left: 4rpx;
                    transition: transform 0.3s;
                }
            }
        }

        .form-actions {
            display: flex;
            gap: 16rpx;

            .form-btn {
                flex: 1;
                height: 84rpx;
                border-radius: 12rpx;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 28rpx;

                &.ghost {
                    background: #F2F4F7;
                    color: #333;
                }

                &.primary {
                    background: #4A90F0;
                    color: #fff;
                }
            }
        }

        .tip-header {
            display: flex;
            align-items: center;
            gap: 12rpx;
            margin-bottom: 16rpx;

            .tip-icon {
                width: 32rpx;
                height: 32rpx;
            }

            .tip-title {
                font-size: 26rpx;
                color: #E57373;
                font-weight: bold;
            }
        }

        .tip-list {
            display: flex;
            flex-direction: column;
            gap: 12rpx;

            .tip-item {
                font-size: 24rpx;
                color: #8D6E63;
                line-height: 1.5;
            }
        }

        .bottom-space {
            height: 60rpx;
        }
    }
}
</style>
