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
                <picker
                    mode="multiSelector"
                    :range="areaColumns"
                    range-key="name"
                    :value="areaIndexes"
                    @change="handleAreaPickerChange"
                    @columnchange="handleAreaColumnChange">
                    <view class="form-input picker-input" :class="{ placeholder: !selectedAreaText }">
                        {{ selectedAreaText || '请选择省 / 市 / 区' }}
                    </view>
                </picker>
                <input class="form-input" v-model="form.street" placeholder="街道 / 乡镇" />
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
    getAreaTree,
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
        adcode: '',
        isDefault: false
    }
}

export default {
    data() {
        return {
            addressList: [],
            formVisible: false,
            editingId: null,
            form: createEmptyForm(),
            areaTree: [],
            areaColumns: [[], [], []],
            areaIndexes: [0, 0, 0]
        }
    },
    onShow() {
        this.loadAddresses()
        this.ensureAreaTree()
    },
    computed: {
        canCreateAddress() {
            return this.addressList.length < 10
        },
        selectedAreaText() {
            return [this.form.province, this.form.city, this.form.district].filter(Boolean).join(' / ')
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
        async handleAddAddress() {
            if (!this.canCreateAddress) {
                uni.showToast({
                    title: '最多只能保存10个地址',
                    icon: 'none'
                })
                return
            }
            this.editingId = null
            this.form = createEmptyForm()
            await this.ensureAreaTree()
            this.resetAreaPicker()
            this.formVisible = true
        },
        async handleEdit(item) {
            await this.ensureAreaTree()
            this.editingId = item.id
            this.form = {
                receiverName: item.receiverName || '',
                receiverMobile: item.receiverMobile || '',
                province: item.province || '',
                city: item.city || '',
                district: item.district || '',
                street: item.street || '',
                detailAddress: item.detailAddress || '',
                adcode: item.adcode || '',
                isDefault: !!item.isDefault
            }
            this.syncAreaPickerFromForm()
            this.formVisible = true
        },
        toggleFormDefault() {
            this.form.isDefault = !this.form.isDefault
        },
        cancelForm() {
            this.formVisible = false
            this.editingId = null
            this.form = createEmptyForm()
            this.resetAreaPicker()
        },
        async submitForm() {
            if (!this.form.receiverName || !this.form.receiverMobile || !this.form.province || !this.form.city || !this.form.district || !this.form.detailAddress) {
                uni.showToast({
                    title: '请填写完整地址信息',
                    icon: 'none'
                })
                return
            }
            if (!/^1[3-9]\d{9}$/.test(`${this.form.receiverMobile || ''}`)) {
                uni.showToast({
                    title: '请填写正确的联系电话',
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
                    adcode: item.adcode,
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
        async ensureAreaTree() {
            if (this.areaTree.length) {
                return
            }
            try {
                this.areaTree = (await getAreaTree({ silent: true })) || []
                this.resetAreaPicker()
            } catch (error) {
                this.areaTree = []
                this.areaColumns = [[], [], []]
            }
        },
        resetAreaPicker() {
            const provinceList = this.areaTree || []
            const cityList = (provinceList[0] && provinceList[0].children) || []
            const districtList = (cityList[0] && cityList[0].children) || []
            this.areaColumns = [provinceList, cityList, districtList]
            this.areaIndexes = [0, 0, 0]
        },
        buildAreaColumns(indexes = [0, 0, 0]) {
            const provinceList = this.areaTree || []
            const provinceIndex = Math.min(indexes[0] || 0, Math.max(provinceList.length - 1, 0))
            const cityList = (provinceList[provinceIndex] && provinceList[provinceIndex].children) || []
            const cityIndex = Math.min(indexes[1] || 0, Math.max(cityList.length - 1, 0))
            const districtList = (cityList[cityIndex] && cityList[cityIndex].children) || []
            return [provinceList, cityList, districtList]
        },
        handleAreaColumnChange(event) {
            const { column, value } = event.detail || {}
            const nextIndexes = [...this.areaIndexes]
            nextIndexes[column] = value
            if (column === 0) {
                nextIndexes[1] = 0
                nextIndexes[2] = 0
            } else if (column === 1) {
                nextIndexes[2] = 0
            }
            this.areaColumns = this.buildAreaColumns(nextIndexes)
            this.areaIndexes = nextIndexes
        },
        handleAreaPickerChange(event) {
            const indexes = (event.detail && event.detail.value) || [0, 0, 0]
            this.areaColumns = this.buildAreaColumns(indexes)
            this.areaIndexes = indexes
            const [provinceList, cityList, districtList] = this.areaColumns
            const province = provinceList[indexes[0]]
            const city = cityList[indexes[1]]
            const district = districtList[indexes[2]]
            this.form.province = province && province.name ? province.name : ''
            this.form.city = city && city.name ? city.name : ''
            this.form.district = district && district.name ? district.name : ''
            this.form.adcode = district && district.id ? `${district.id}` : ''
        },
        syncAreaPickerFromForm() {
            if (!this.areaTree.length) {
                return
            }
            const path = this.findAreaPath(this.areaTree, {
                adcode: this.form.adcode,
                province: this.form.province,
                city: this.form.city,
                district: this.form.district
            })
            if (!path) {
                this.resetAreaPicker()
                return
            }
            const indexes = [
                path.provinceIndex,
                path.cityIndex,
                path.districtIndex
            ]
            this.areaColumns = this.buildAreaColumns(indexes)
            this.areaIndexes = indexes
            this.form.adcode = path.district && path.district.id ? `${path.district.id}` : this.form.adcode
        },
        findAreaPath(provinceList, target) {
            for (let provinceIndex = 0; provinceIndex < provinceList.length; provinceIndex += 1) {
                const province = provinceList[provinceIndex]
                if (target.province && province.name !== target.province) {
                    continue
                }
                const cityList = province.children || []
                for (let cityIndex = 0; cityIndex < cityList.length; cityIndex += 1) {
                    const city = cityList[cityIndex]
                    if (target.city && city.name !== target.city) {
                        continue
                    }
                    const districtList = city.children || []
                    for (let districtIndex = 0; districtIndex < districtList.length; districtIndex += 1) {
                        const district = districtList[districtIndex]
                        const codeMatched = target.adcode && `${district.id}` === `${target.adcode}`
                        const nameMatched = target.district && district.name === target.district
                        if (codeMatched || nameMatched) {
                            return {
                                provinceIndex,
                                cityIndex,
                                districtIndex,
                                district
                            }
                        }
                    }
                }
            }
            return undefined
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
            this.$navigateBack()
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

    .picker-input {
        display: flex;
        align-items: center;
    }

    .picker-input.placeholder {
        color: #999;
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

