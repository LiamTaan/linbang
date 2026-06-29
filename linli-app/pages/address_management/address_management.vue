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
            <view
                v-for="item in addressList"
                :key="item.id"
                class="address-card"
                :class="{ 'default-card': item.isDefault }">
                <view class="address-header">
                    <text class="name">{{ item.receiverName }}</text>
                    <text class="phone">{{ item.receiverMobile }}</text>
                </view>
                <text class="address-text">
                    {{ item.province }} {{ item.city }} {{ item.district }} {{ item.street }} {{ item.detailAddress }}
                </text>
                <view class="address-footer">
                    <view class="default-wrap" @click="handleSetDefault(item)">
                        <text class="default-text">{{ item.isDefault ? '已设默认' : '设为默认' }}</text>
                    </view>
                    <view class="footer-actions">
                        <text class="edit-text" @click="handleEdit(item)">编辑</text>
                        <text class="edit-text delete" @click="handleDelete(item)">删除</text>
                    </view>
                </view>
            </view>

            <view class="add-address-btn" @click="handleAddAddress">
                <text class="add-text">+添加新地址</text>
            </view>

            <view v-if="formVisible" class="form-card">
                <input class="form-input" v-model="form.receiverName" placeholder="联系人" />
                <input class="form-input" v-model="form.receiverMobile" type="number" placeholder="联系电话" />
                <input class="form-input" v-model="form.province" placeholder="省" />
                <input class="form-input" v-model="form.city" placeholder="市" />
                <input class="form-input" v-model="form.district" placeholder="区" />
                <input class="form-input" v-model="form.street" placeholder="街道" />
                <input class="form-input" v-model="form.detailAddress" placeholder="详细地址" />
                <view class="default-switch" @click="form.isDefault = !form.isDefault">
                    <text class="default-label">设为默认地址</text>
                    <switch :checked="form.isDefault" color="#4A90F0" />
                </view>
                <view class="form-actions">
                    <view class="form-btn ghost" @click="cancelForm">取消</view>
                    <view class="form-btn primary" @click="submitForm">保存</view>
                </view>
            </view>

            <view class="tip-card">
                <text class="tip-title">温馨提示</text>
                <text class="tip-text">默认地址将作为发单时的首选地址</text>
                <text class="tip-text">最多可保存十个地址</text>
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
    methods: {
        async loadAddresses() {
            try {
                const page = await getAddressPage({
                    pageNo: 1,
                    pageSize: 50
                })
                this.addressList = page.list || []
            } catch (error) {
            }
        },
        handleAddAddress() {
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
                        this.loadAddresses()
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
}

.header {
    background: #fff;
    padding: 60rpx 30rpx 30rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.back-icon {
    font-size: 40rpx;
    color: #333;
    transform: rotate(180deg);
}

.content-scroll {
    padding: 20rpx;
    box-sizing: border-box;
}

.address-card,
.form-card,
.tip-card {
    background: #fff;
    border-radius: 12rpx;
    padding: 24rpx;
    margin-bottom: 20rpx;
}

.default-card {
    border: 2rpx solid #2E83F0;
    background: #F3F8FF;
}

.address-header,
.address-footer,
.footer-actions,
.default-switch,
.form-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.name {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
}

.phone,
.address-text,
.default-text,
.edit-text,
.tip-text {
    font-size: 24rpx;
    color: #666;
}

.address-text {
    display: block;
    margin: 20rpx 0;
    line-height: 1.7;
}

.edit-text.delete {
    color: #E53935;
}

.add-address-btn {
    padding: 30rpx;
    text-align: center;
    background: #FFFFFF;
    border-radius: 12rpx;
    border: 2px dashed #F9A23F;
    margin-bottom: 20rpx;
}

.add-text {
    font-size: 32rpx;
    color: #2E83F0;
    font-weight: bold;
}

.form-input {
    height: 84rpx;
    border-radius: 12rpx;
    background: #F7F9FC;
    padding: 0 24rpx;
    margin-bottom: 16rpx;
}

.form-btn {
    flex: 1;
    height: 84rpx;
    border-radius: 12rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: 10rpx;

    &.ghost {
        background: #F2F4F7;
    }

    &.primary {
        background: #4A90F0;
        color: #fff;
    }
}

.tip-title {
    display: block;
    font-size: 26rpx;
    font-weight: bold;
    color: #FA9D3B;
    margin-bottom: 8rpx;
}

.bottom-space {
    height: 60rpx;
}
</style>
