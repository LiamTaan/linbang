<template>
    <view class="page-container">
        <view class="header">
            <view class="back-btn" @click="goBack">
                <text class="iconfont icon-youjiantou back-icon"></text>
            </view>
            <text class="title">地址管理</text>
            <view class="placeholder"></view>
        </view>

        <scroll-view class="content-scroll" scroll-y>
            <view class="address-list">
                <view
                    v-for="item in addressList"
                    :key="item.id"
                    class="address-card"
                    :class="{ 'default-card': item.isDefault }">
                    <view class="card-top">
                        <view class="card-user">
                            <text class="name">{{ item.receiverName || '未命名联系人' }}</text>
                            <text class="phone">{{ formatMobile(item.receiverMobile) }}</text>
                        </view>
                        <view class="close-btn" @click.stop="handleDelete(item)">
                            <text class="close-text">×</text>
                        </view>
                    </view>
                    <text class="address-text">{{ buildAddressText(item) || '请补充完整地址信息' }}</text>
                    <view class="card-footer">
                        <view
                            class="default-wrap"
                            :class="{ active: item.isDefault }"
                            @click.stop="handleSetDefault(item)">
                            <view class="default-dot" :class="{ checked: item.isDefault }"></view>
                            <text class="default-text">{{ item.isDefault ? '已设默认' : '默认' }}</text>
                        </view>
                        <text class="edit-text" @click.stop="handleEdit(item)">编辑</text>
                    </view>
                </view>
            </view>

            <view v-if="!addressList.length" class="empty-card">
                <text class="empty-text">还没有保存地址，点击下方按钮新增</text>
            </view>

            <view class="add-address-btn" :class="{ disabled: !canCreateAddress }" @click="handleAddAddress">
                <text class="add-text">+ 添加新地址</text>
            </view>

            <view v-if="formVisible" class="form-card">
                <text class="form-title">{{ editingId ? '编辑地址' : '新增地址' }}</text>
                <input class="form-input" v-model="form.receiverName" placeholder="联系人" />
                <input class="form-input" v-model="form.receiverMobile" type="number" placeholder="联系电话" />
                <input class="form-input" v-model="form.province" placeholder="省" />
                <input class="form-input" v-model="form.city" placeholder="市" />
                <input class="form-input" v-model="form.district" placeholder="区" />
                <input class="form-input" v-model="form.street" placeholder="街道" />
                <input class="form-input" v-model="form.detailAddress" placeholder="详细地址" />
                <view class="default-switch" @click="toggleFormDefault">
                    <text class="default-label">设为默认地址</text>
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
                    <text class="tip-item">默认地址将作为发单时的首选地址</text>
                    <text class="tip-item">最多可保存10个地址</text>
                </view>
            </view>

            <view class="bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
import {
    createAddress,
    deleteAddress,
    getAddressPage,
    updateAddress
} from '@/api/member'

function createEmptyForm() {
    return {
        receiverName: '',
        receiverMobile: '',
        province: '',
        city: '',
        district: '',
        street: '',
        detailAddress: '',
        isDefault: false
    }
}

export default {
    data() {
        return {
            addressList: [],
            formVisible: false,
            editingId: null,
            form: createEmptyForm()
        }
    },
    onShow() {
        this.loadAddresses()
    },
    computed: {
        canCreateAddress() {
            return this.addressList.length < 10
        }
    },
    methods: {
        async loadAddresses() {
            try {
                const page = await getAddressPage({
                    pageNo: 1,
                    pageSize: 50
                })
                this.addressList = (page.list || []).map((item) => this.normalizeAddressItem(item))
            } catch (error) {
            }
        },
        handleAddAddress() {
            if (!this.canCreateAddress) {
                uni.showToast({
                    title: '最多只能保存10个地址',
                    icon: 'none'
                })
                return
            }
            this.editingId = null
            this.form = createEmptyForm()
            this.formVisible = true
        },
        handleEdit(item) {
            this.editingId = item.id
            this.form = {
                receiverName: item.receiverName || '',
                receiverMobile: item.receiverMobile || '',
                province: item.province || '',
                city: item.city || '',
                district: item.district || '',
                street: item.street || '',
                detailAddress: item.detailAddress || '',
                isDefault: !!item.isDefault
            }
            this.formVisible = true
        },
        toggleFormDefault() {
            this.form.isDefault = !this.form.isDefault
        },
        cancelForm() {
            this.formVisible = false
            this.editingId = null
            this.form = createEmptyForm()
        },
        async submitForm() {
            if (!this.form.receiverName || !this.form.receiverMobile || !this.form.detailAddress) {
                uni.showToast({
                    title: '请填写完整地址信息',
                    icon: 'none'
                })
                return
            }
            if (!this.editingId && !this.canCreateAddress) {
                uni.showToast({
                    title: '最多只能保存10个地址',
                    icon: 'none'
                })
                return
            }
            try {
                if (this.editingId) {
                    await updateAddress({
                        id: this.editingId,
                        ...this.form
                    })
                } else {
                    await createAddress(this.form)
                }
                uni.showToast({
                    title: '地址已保存',
                    icon: 'success'
                })
                this.cancelForm()
                this.loadAddresses()
            } catch (error) {
            }
        },
        async handleSetDefault(item) {
            if (item.isDefault) {
                return
            }
            try {
                await updateAddress({
                    id: item.id,
                    receiverName: item.receiverName,
                    receiverMobile: item.receiverMobile,
                    province: item.province,
                    city: item.city,
                    district: item.district,
                    street: item.street,
                    detailAddress: item.detailAddress,
                    isDefault: true
                })
                uni.showToast({
                    title: '默认地址已更新',
                    icon: 'success'
                })
                this.loadAddresses()
            } catch (error) {
            }
        },
        handleDelete(item) {
            uni.showModal({
                title: '确认删除',
                content: `确定删除 ${item.receiverName} 的地址吗？`,
                success: async (res) => {
                    if (!res.confirm) {
                        return
                    }
                    try {
                        await deleteAddress(item.id)
                        uni.showToast({
                            title: '删除成功',
                            icon: 'success'
                        })
                        this.loadAddresses()
                    } catch (error) {
                    }
                }
            })
        },
        normalizeAddressItem(item) {
            const normalized = { ...(item || {}) }
            const defaultFlag = normalized.isDefault ?? normalized.defaultStatus ?? false
            normalized.isDefault = !!defaultFlag
            return normalized
        },
        buildAddressText(item) {
            if (!item) {
                return ''
            }
            return [
                item.province,
                item.city,
                item.district,
                item.street,
                item.detailAddress
            ].filter(Boolean).join(' ')
        },
        formatMobile(mobile) {
            const value = `${mobile || ''}`.trim()
            if (!value) {
                return '未绑定手机号'
            }
            if (value.length !== 11) {
                return value
            }
            return `${value.slice(0, 3)}****${value.slice(7)}`
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
    background: #f6f7fb;

    .header {
        background: #fff;
        padding: 60rpx 30rpx 24rpx;
        display: flex;
        justify-content: space-between;
        align-items: center;

        .back-btn,
        .placeholder {
            width: 60rpx;
        }

        .back-icon {
            font-size: 40rpx;
            color: #222;
            transform: rotate(180deg);
        }

        .title {
            font-size: 34rpx;
            font-weight: 600;
            color: #333;
        }
    }

    .content-scroll {
        padding: 24rpx 24rpx 40rpx;
        box-sizing: border-box;
    }

    .address-list {
        display: flex;
        flex-direction: column;
        gap: 22rpx;
        margin-bottom: 22rpx;
    }

    .address-card,
    .form-card,
    .tip-card,
    .empty-card {
        background: #fff;
        border-radius: 18rpx;
    }

    .address-card {
        padding: 22rpx 22rpx 20rpx;
        border: 2rpx solid #d2d6de;
        position: relative;
        overflow: hidden;

        &.default-card {
            border-color: #5d97ff;
            background: #dbe9ff;
            box-shadow: 0 8rpx 24rpx rgba(74, 144, 240, 0.08);
        }
    }

    .card-top {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
    }

    .card-user {
        display: flex;
        align-items: baseline;
        gap: 18rpx;
        flex-wrap: wrap;
        padding-right: 16rpx;
    }

    .name {
        font-size: 32rpx;
        font-weight: 600;
        color: #333;
    }

    .phone {
        font-size: 24rpx;
        color: #7a7f87;
    }

    .close-btn {
        width: 32rpx;
        height: 32rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
    }

    .close-text {
        font-size: 36rpx;
        line-height: 1;
        color: #8f959e;
    }

    .address-text {
        display: block;
        margin-top: 16rpx;
        font-size: 24rpx;
        line-height: 1.7;
        color: #666;
    }

    .card-footer {
        margin-top: 18rpx;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .default-wrap {
        display: flex;
        align-items: center;
        gap: 10rpx;
        color: #8a9099;

        &.active {
            color: #4a90f0;
        }
    }

    .default-dot {
        width: 24rpx;
        height: 24rpx;
        border-radius: 50%;
        border: 2rpx solid #b7bcc5;
        box-sizing: border-box;
        position: relative;

        &.checked {
            border-color: #4a90f0;
            background: #4a90f0;

            &::after {
                content: '';
                width: 8rpx;
                height: 8rpx;
                border-radius: 50%;
                background: #fff;
                position: absolute;
                top: 50%;
                left: 50%;
                transform: translate(-50%, -50%);
            }
        }
    }

    .default-text,
    .edit-text,
    .tip-item,
    .empty-text {
        font-size: 24rpx;
    }

    .edit-text {
        color: #7d9ff8;
        font-weight: 500;
    }

    .empty-card {
        padding: 30rpx;
        text-align: center;
        margin-bottom: 22rpx;
    }

    .empty-text {
        color: #8a9099;
    }

    .add-address-btn {
        padding: 30rpx 24rpx;
        text-align: center;
        background: #fff;
        border-radius: 18rpx;
        border: 2rpx dashed #f1b65c;
        margin-bottom: 24rpx;

        &.disabled {
            opacity: 0.85;
        }
    }

    .add-text {
        font-size: 34rpx;
        color: #3f7fe7;
        font-weight: 700;
        letter-spacing: 1rpx;
    }

    .form-card,
    .tip-card {
        padding: 24rpx;
        margin-bottom: 24rpx;
    }

    .form-title {
        display: block;
        font-size: 28rpx;
        font-weight: 600;
        color: #333;
        margin-bottom: 18rpx;
    }

    .form-input {
        height: 84rpx;
        border-radius: 14rpx;
        background: #f7f9fc;
        padding: 0 24rpx;
        font-size: 26rpx;
        color: #333;
        margin-bottom: 16rpx;
        box-sizing: border-box;
    }

    .default-switch {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 10rpx 2rpx 24rpx;
    }

    .default-label {
        font-size: 26rpx;
        color: #333;
    }

    .switch-box {
        width: 88rpx;
        height: 48rpx;
        border-radius: 24rpx;
        background: #dcdfe6;
        position: relative;
        transition: background 0.2s ease;

        &.active {
            background: #4a90f0;

            .switch-dot {
                transform: translateX(40rpx);
            }
        }
    }

    .switch-dot {
        width: 40rpx;
        height: 40rpx;
        border-radius: 50%;
        background: #fff;
        position: absolute;
        top: 4rpx;
        left: 4rpx;
        transition: transform 0.2s ease;
    }

    .form-actions {
        display: flex;
        gap: 16rpx;
    }

    .form-btn {
        flex: 1;
        height: 84rpx;
        border-radius: 14rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 28rpx;
        font-weight: 500;

        &.ghost {
            background: #f2f4f7;
            color: #333;
        }

        &.primary {
            background: linear-gradient(135deg, #4f8df6 0%, #2f74eb 100%);
            color: #fff;
        }
    }

    .tip-card {
        background: #fff8ef;
        border: 2rpx solid #f2c586;
    }

    .tip-header {
        display: flex;
        align-items: center;
        gap: 10rpx;
        margin-bottom: 12rpx;
    }

    .tip-icon {
        width: 30rpx;
        height: 30rpx;
    }

    .tip-title {
        font-size: 26rpx;
        font-weight: 600;
        color: #f29e34;
    }

    .tip-list {
        display: flex;
        flex-direction: column;
        gap: 8rpx;
        padding-left: 6rpx;
    }

    .tip-item {
        color: #e2a34f;
        line-height: 1.6;
    }

    .bottom-space {
        height: 60rpx;
    }
}
</style>
