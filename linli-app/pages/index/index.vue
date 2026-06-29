<template>
    <view class="page-container">
        <view class="header">
            <view class="location-btn" @click="handleSelectAddress">
                <image class="location-icon" src="/static/img/home/location@3x.png" />
                <text class="location-text">{{ locationText || '请选择服务地址' }}</text>
            </view>
            <view class="role-switch" @click="handleSwitchRole">
                <image class="switch-icon" src="/static/img/home/switch_role@3x.png" />
            </view>
        </view>

        <view class="map-area">
            <image class="map-bg" src="/static/img/home/map@3x.png" mode="aspectFill" />
        </view>

        <view class="main-content">
            <view class="top-gradient"></view>

            <view class="search-box">
                <image class="search-icon" src="/static/img/home/search@3x.png" />
                <input class="search-input" placeholder="搜索服务类目" v-model="searchText" confirm-type="search"
                    @confirm="handleSearch" />
                <view class="search-btn" @click="handleSearch">
                    <text class="search-text">搜索</text>
                </view>
            </view>

            <scroll-view class="content-scroll" scroll-y refresher-enabled :refresher-triggered="refreshing"
                @refresherrefresh="handleRefresh">
                <view class="category-section">
                    <scroll-view class="category-scroll" scroll-x :show-scrollbar="false">
                        <view class="category-scroll-content">
                            <view v-for="item in level1Categories" :key="item.id" class="category-item"
                                :class="{ active: selectedLevel1Id === item.id }" @click="selectCategory('level1', item)">
                                <text class="category-text">{{ item.categoryName }}</text>
                            </view>
                        </view>
                    </scroll-view>
                    <scroll-view v-if="level2Categories.length" class="category-scroll" scroll-x :show-scrollbar="false">
                        <view class="category-scroll-content">
                            <view v-for="item in level2Categories" :key="item.id" class="category-item"
                                :class="{ active: selectedLevel2Id === item.id }" @click="selectCategory('level2', item)">
                                <text class="category-text">{{ item.categoryName }}</text>
                            </view>
                        </view>
                    </scroll-view>
                    <scroll-view v-if="level3Categories.length" class="category-scroll" scroll-x :show-scrollbar="false">
                        <view class="category-scroll-content">
                            <view v-for="item in level3Categories" :key="item.id" class="category-item"
                                :class="{ active: selectedLevel3Id === item.id }" @click="selectCategory('level3', item)">
                                <text class="category-text">{{ item.categoryName }}</text>
                            </view>
                        </view>
                    </scroll-view>
                </view>

                <view class="pricing-section">
                    <text class="section-title">计价方式</text>
                    <view class="pricing-options">
                        <view v-for="item in pricingOptions" :key="item.value" class="pricing-item"
                            :class="{ active: form.pricingMode === item.value }" @click="selectPricing(item.value)">
                            <text class="pricing-text">{{ item.label }}</text>
                        </view>
                    </view>
                </view>

                <view class="order-section">
                    <text class="section-title">订单信息</text>

                    <view class="form-item">
                        <view class="select-box" @click="handleSelectAddress">
                            <text class="select-text">{{ selectedAddressLabel || '从地址簿选择服务地址' }}</text>
                            <text class="select-arrow">▼</text>
                        </view>
                    </view>

                    <view class="form-item">
                        <view class="select-box" @click="handleAgreement">
                            <text class="select-text">{{ form.province && form.city ? regionText : '查看交易保障与协议' }}</text>
                            <text class="select-arrow">▼</text>
                        </view>
                    </view>

                    <view class="form-item">
                        <input class="input-box" placeholder="请输入详细地址" v-model="form.detailAddress" />
                    </view>

                    <view class="form-row">
                        <view class="form-item small">
                            <input class="input-box" placeholder="工期/时长" v-model="form.serviceDurationDesc" />
                        </view>
                        <view class="form-item small">
                            <input class="input-box" type="digit" placeholder="数量" v-model="form.quantity" />
                        </view>
                        <view class="form-item small">
                            <input class="input-box" type="digit" placeholder="预算金额" v-model="form.budgetAmount" />
                        </view>
                    </view>

                    <view class="form-row">
                        <view class="form-item small">
                            <input class="input-box" type="number" placeholder="服务人数" v-model="form.workerCount" />
                        </view>
                        <view class="form-item small grow">
                            <input class="input-box" placeholder="价格明细名称（选填）" v-model="form.priceItemName" />
                        </view>
                    </view>

                    <view class="form-item">
                        <textarea class="textarea-box" placeholder="请输入具体需求" v-model="form.requireDesc" />
                    </view>

                    <view class="upload-list">
                        <view v-for="(file, index) in uploadedFiles" :key="file.fileId || file.url || index"
                            class="upload-preview">
                            <image class="upload-preview-image" :src="file.url" mode="aspectFill" />
                            <view class="remove-upload" @click="removeUpload(index)">×</view>
                        </view>
                        <view class="upload-box" @click="handleUpload">
                            <image class="upload-icon" src="/static/img/home/plus@3x.png" />
                            <text class="upload-text">{{ uploading ? '上传中' : '上传图片' }}</text>
                        </view>
                    </view>

                    <view class="preview-summary" v-if="previewResult.previewToken">
                        <text class="preview-title">预览结果</text>
                        <text class="preview-line">类目：{{ previewResult.categoryName || currentCategoryName }}</text>
                        <text class="preview-line">应付金额：¥{{ $fmt.formatMoney(previewResult.orderAmount) }}</text>
                        <text class="preview-line">协议：{{ previewResult.agreementTitle || guaranteeConfig.projectEscrowAgreementTitle || '工程托管协议' }}</text>
                        <text class="preview-line"
                            v-if="previewResult.invoiceImpactReminder">{{ previewResult.invoiceImpactReminder }}</text>
                    </view>

                    <view class="checkbox-list">
                        <view class="checkbox-item">
                            <view class="checkbox" :class="{ checked: form.needInvoice }" @click="toggleInvoice">
                                <text v-if="form.needInvoice" class="check-icon">✓</text>
                            </view>
                            <text class="checkbox-text">是否开票<text class="hint">(会按类目配置影响展示提醒)</text></text>
                        </view>

                        <view class="checkbox-item">
                            <view class="checkbox" :class="{ checked: form.needSplit }" @click="toggleSplit">
                                <text v-if="form.needSplit" class="check-icon">✓</text>
                            </view>
                            <text class="checkbox-text">希望拆单<text class="hint">(后端会按规则判断是否真正拆分)</text></text>
                        </view>
                    </view>

                    <view class="agreement-item">
                        <view class="checkbox" :class="{ checked: form.agreementConfirmed }" @click="toggleAgreement">
                            <text v-if="form.agreementConfirmed" class="check-icon">✓</text>
                        </view>
                        <text class="agreement-text">我已阅读并同意</text>
                        <text class="agreement-link" @click.stop="handleAgreement">
                            《{{ guaranteeConfig.projectEscrowAgreementTitle || '工程托管协议' }}》
                        </text>
                    </view>
                </view>

                <view class="bottom-btns">
                    <view class="bottom-btn preview-btn" @click="handlePreview">
                        <text class="btn-text">预览</text>
                    </view>
                    <view class="bottom-btn publish-btn" @click="handlePublish">
                        <text class="btn-text">{{ submitting ? '发布中...' : '发布' }}</text>
                    </view>
                </view>

                <view class="bottom-space"></view>
            </scroll-view>
        </view>

        <tab-bar :current-index="0"></tab-bar>
    </view>
</template>

<script>
import tabBar from '@/components/tabBar/tabBar.vue'
import { getAddressPage, getRoleContext, switchRole } from '@/api/member'
import { uploadAppFile } from '@/api/infra'
import { getServiceCategoryList } from '@/api/merchant'
import { createOrder, getGuaranteeConfig, previewOrder } from '@/api/order'
import {
    buildAddressText,
    extractUploadedFile,
    getPricingModeLabel,
    PRICING_MODE_OPTIONS
} from '@/utils/linbang'

function findCategoryById(list, id) {
    for (let i = 0; i < (list || []).length; i++) {
        const item = list[i]
        if (item.id === id) {
            return item
        }
        const child = findCategoryById(item.children || [], id)
        if (child) {
            return child
        }
    }
    return null
}

export default {
    components: {
        tabBar
    },
    data() {
        return {
            searchText: '',
            refreshing: false,
            uploading: false,
            submitting: false,
            categories: [],
            addressList: [],
            roleContext: {},
            guaranteeConfig: {},
            previewResult: {},
            uploadedFiles: [],
            selectedLevel1Id: null,
            selectedLevel2Id: null,
            selectedLevel3Id: null,
            form: {
                pricingMode: 'FIXED_PRICE',
                budgetAmount: '',
                quantity: '1',
                workerCount: '1',
                serviceDurationDesc: '',
                requireDesc: '',
                province: '',
                city: '',
                district: '',
                street: '',
                detailAddress: '',
                longitude: '',
                latitude: '',
                adcode: '',
                needInvoice: false,
                needSplit: false,
                agreementConfirmed: true,
                priceItemName: ''
            }
        }
    },
    computed: {
        level1Categories() {
            return this.categories || []
        },
        currentLevel1() {
            return findCategoryById(this.categories, this.selectedLevel1Id)
        },
        level2Categories() {
            return (this.currentLevel1 && this.currentLevel1.children) || []
        },
        currentLevel2() {
            return findCategoryById(this.level2Categories, this.selectedLevel2Id)
        },
        level3Categories() {
            return (this.currentLevel2 && this.currentLevel2.children) || []
        },
        currentCategory() {
            return findCategoryById(this.categories, this.selectedLevel3Id || this.selectedLevel2Id || this.selectedLevel1Id)
        },
        currentCategoryName() {
            return this.currentCategory ? this.currentCategory.categoryName : '--'
        },
        pricingOptions() {
            const current = this.currentCategory || {}
            const supported = current.supportedPricingModes && current.supportedPricingModes.length
                ? current.supportedPricingModes
                : [current.defaultPricingMode || 'FIXED_PRICE']
            return supported.map((value) => {
                const matched = PRICING_MODE_OPTIONS.find((item) => item.value === value)
                return matched || {
                    label: getPricingModeLabel(value),
                    value
                }
            })
        },
        selectedAddressLabel() {
            return buildAddressText(this.form)
        },
        locationText() {
            return this.form.district || this.form.city || this.form.province || ''
        },
        regionText() {
            return [this.form.province, this.form.city, this.form.district, this.form.street].filter(Boolean).join(' ')
        },
        switchableRoles() {
            return ((this.roleContext && this.roleContext.roleSummaries) || []).filter((item) => item.switchable)
        }
    },
    onShow() {
        uni.hideTabBar()
        this.loadPageData()
    },
    methods: {
        async loadPageData(keyword = '') {
            try {
                const [categories, addressPage, guaranteeConfig, roleContext] = await Promise.all([
                    getServiceCategoryList(keyword).catch(() => []),
                    getAddressPage({ pageNo: 1, pageSize: 50 }).catch(() => ({ list: [] })),
                    getGuaranteeConfig().catch(() => ({})),
                    getRoleContext().catch(() => ({}))
                ])
                this.categories = categories || []
                this.addressList = (addressPage && addressPage.list) || []
                this.guaranteeConfig = guaranteeConfig || {}
                this.roleContext = roleContext || {}
                this.ensureCategorySelection()
                this.ensureAddressSelection()
            } catch (error) {
            } finally {
                this.refreshing = false
            }
        },
        handleRefresh() {
            this.refreshing = true
            this.loadPageData(this.searchText)
        },
        ensureCategorySelection() {
            if (!this.categories.length) {
                this.selectedLevel1Id = null
                this.selectedLevel2Id = null
                this.selectedLevel3Id = null
                return
            }
            const level1 = this.currentLevel1 || this.categories[0]
            this.selectedLevel1Id = level1.id
            const level2 = (level1.children && level1.children[0]) || null
            if (!this.selectedLevel2Id || !findCategoryById(level1.children || [], this.selectedLevel2Id)) {
                this.selectedLevel2Id = level2 ? level2.id : null
            }
            const currentLevel2 = findCategoryById(level1.children || [], this.selectedLevel2Id)
            const level3 = (currentLevel2 && currentLevel2.children && currentLevel2.children[0]) || null
            if (!this.selectedLevel3Id || !findCategoryById((currentLevel2 && currentLevel2.children) || [], this.selectedLevel3Id)) {
                this.selectedLevel3Id = level3 ? level3.id : null
            }
            const current = this.currentCategory || level2 || level1
            if (current && current.defaultPricingMode) {
                this.form.pricingMode = current.defaultPricingMode
            } else if (this.pricingOptions.length) {
                this.form.pricingMode = this.pricingOptions[0].value
            }
            if (current && current.supportInvoice === false) {
                this.form.needInvoice = false
            }
            if (current && current.supportSplit === false) {
                this.form.needSplit = false
            }
        },
        ensureAddressSelection() {
            if (this.form.detailAddress || !this.addressList.length) {
                return
            }
            const defaultAddress = this.addressList.find((item) => item.defaultStatus) || this.addressList[0]
            if (defaultAddress) {
                this.applyAddress(defaultAddress)
            }
        },
        selectCategory(level, item) {
            if (level === 'level1') {
                this.selectedLevel1Id = item.id
                this.selectedLevel2Id = item.children && item.children.length ? item.children[0].id : null
                const level2 = item.children && item.children.length ? item.children[0] : null
                this.selectedLevel3Id = level2 && level2.children && level2.children.length ? level2.children[0].id : null
            } else if (level === 'level2') {
                this.selectedLevel2Id = item.id
                this.selectedLevel3Id = item.children && item.children.length ? item.children[0].id : null
            } else {
                this.selectedLevel3Id = item.id
            }
            this.previewResult = {}
            this.ensureCategorySelection()
        },
        selectPricing(value) {
            this.form.pricingMode = value
            this.previewResult = {}
        },
        handleSearch() {
            this.loadPageData(this.searchText.trim())
        },
        applyAddress(address) {
            this.form.province = address.province || ''
            this.form.city = address.city || ''
            this.form.district = address.district || ''
            this.form.street = address.street || ''
            this.form.detailAddress = address.detailAddress || ''
            this.form.longitude = address.longitude || ''
            this.form.latitude = address.latitude || ''
            this.form.adcode = address.adcode || ''
        },
        handleSelectAddress() {
            if (!this.addressList.length) {
                uni.showModal({
                    title: '暂无地址',
                    content: '还没有可用地址，是否前往地址管理新增？',
                    success: ({ confirm }) => {
                        if (confirm) {
                            uni.navigateTo({
                                url: '/pages/address_management/address_management'
                            })
                        }
                    }
                })
                return
            }
            const addressItems = this.addressList.map((item) => buildAddressText(item) || item.contactName || '未命名地址')
            addressItems.push('前往地址管理')
            uni.showActionSheet({
                itemList: addressItems,
                success: ({ tapIndex }) => {
                    if (tapIndex === addressItems.length - 1) {
                        uni.navigateTo({
                            url: '/pages/address_management/address_management'
                        })
                        return
                    }
                    const address = this.addressList[tapIndex]
                    if (address) {
                        this.applyAddress(address)
                        this.previewResult = {}
                    }
                }
            })
        },
        async handleUpload() {
            if (this.uploading) {
                return
            }
            const chooseImage = () => new Promise((resolve, reject) => {
                uni.chooseImage({
                    count: 6,
                    success: resolve,
                    fail: reject
                })
            })
            try {
                const result = await chooseImage()
                const paths = result.tempFilePaths || []
                if (!paths.length) {
                    return
                }
                this.uploading = true
                for (let i = 0; i < paths.length; i++) {
                    const uploadResp = await uploadAppFile(paths[i])
                    const file = extractUploadedFile(uploadResp, paths[i])
                    if (!file.fileId) {
                        throw new Error('上传成功但未返回 fileId')
                    }
                    this.uploadedFiles.push(file)
                }
            } catch (error) {
                if (error && error.message) {
                    uni.showToast({
                        title: error.message.length > 18 ? '上传失败，请稍后重试' : error.message,
                        icon: 'none'
                    })
                }
            } finally {
                this.uploading = false
            }
        },
        removeUpload(index) {
            this.uploadedFiles.splice(index, 1)
            this.previewResult = {}
        },
        toggleInvoice() {
            const category = this.currentCategory || {}
            if (category.supportInvoice === false) {
                uni.showToast({
                    title: '当前类目暂不支持开票',
                    icon: 'none'
                })
                return
            }
            this.form.needInvoice = !this.form.needInvoice
            this.previewResult = {}
        },
        toggleSplit() {
            const category = this.currentCategory || {}
            if (category.supportSplit === false) {
                uni.showToast({
                    title: '当前类目暂不支持拆单',
                    icon: 'none'
                })
                return
            }
            this.form.needSplit = !this.form.needSplit
            this.previewResult = {}
        },
        toggleAgreement() {
            this.form.agreementConfirmed = !this.form.agreementConfirmed
        },
        handleAgreement() {
            uni.showModal({
                title: this.guaranteeConfig.projectEscrowAgreementTitle || '工程托管协议',
                content: this.guaranteeConfig.projectEscrowAgreement || this.guaranteeConfig.serviceAgreement || '请在发布前确认交易保障协议内容。',
                showCancel: false
            })
        },
        validateForm() {
            const category = this.currentCategory
            if (!category) {
                uni.showToast({
                    title: '请选择服务类目',
                    icon: 'none'
                })
                return null
            }
            if (!this.form.pricingMode) {
                uni.showToast({
                    title: '请选择计价方式',
                    icon: 'none'
                })
                return null
            }
            if (!this.form.province || !this.form.city || !this.form.district || !this.form.detailAddress) {
                uni.showToast({
                    title: '请完善服务地址',
                    icon: 'none'
                })
                return null
            }
            if (!this.form.requireDesc.trim()) {
                uni.showToast({
                    title: '请填写具体需求',
                    icon: 'none'
                })
                return null
            }
            const budgetAmount = Number(this.form.budgetAmount)
            const quantity = Number(this.form.quantity)
            const workerCount = Number(this.form.workerCount || 1)
            if (Number.isNaN(budgetAmount) || budgetAmount <= 0) {
                uni.showToast({
                    title: '请输入正确的预算金额',
                    icon: 'none'
                })
                return null
            }
            if (Number.isNaN(quantity) || quantity <= 0) {
                uni.showToast({
                    title: '请输入正确的数量',
                    icon: 'none'
                })
                return null
            }
            if (Number.isNaN(workerCount) || workerCount <= 0) {
                uni.showToast({
                    title: '请输入正确的服务人数',
                    icon: 'none'
                })
                return null
            }
            const payload = {
                categoryId: category.id,
                pricingMode: this.form.pricingMode,
                budgetAmount,
                quantity,
                workerCount,
                serviceDurationDesc: this.form.serviceDurationDesc,
                requireDesc: this.form.requireDesc.trim(),
                province: this.form.province,
                city: this.form.city,
                district: this.form.district,
                street: this.form.street,
                detailAddress: this.form.detailAddress,
                longitude: this.form.longitude || undefined,
                latitude: this.form.latitude || undefined,
                adcode: this.form.adcode || undefined,
                needInvoice: !!this.form.needInvoice,
                needSplit: !!this.form.needSplit,
                attachmentFileIds: this.uploadedFiles.map((item) => item.fileId).filter(Boolean)
            }
            if (this.form.priceItemName.trim()) {
                payload.priceItems = [{
                    itemType: 'CUSTOM',
                    itemName: this.form.priceItemName.trim(),
                    itemAmount: budgetAmount,
                    sortNo: 1
                }]
            }
            return payload
        },
        async ensurePreviewPayload() {
            const payload = this.validateForm()
            if (!payload) {
                return null
            }
            const preview = await previewOrder(payload)
            this.previewResult = preview || {}
            if (preview && preview.riskStrategy === 'BLOCK') {
                uni.showModal({
                    title: '内容需调整',
                    content: (preview.sensitiveHitSummaries || []).join('；') || '当前需求内容命中风控规则，请修改后重试。',
                    showCancel: false
                })
                return null
            }
            return payload
        },
        async handlePreview() {
            try {
                const payload = await this.ensurePreviewPayload()
                if (!payload || !this.previewResult.previewToken) {
                    return
                }
                const summary = [
                    `类目：${this.previewResult.categoryName || this.currentCategoryName}`,
                    `计价：${this.previewResult.pricingModeName || getPricingModeLabel(payload.pricingMode)}`,
                    `应付金额：¥${this.$fmt.formatMoney(this.previewResult.orderAmount)}`,
                    this.previewResult.invoiceImpactReminder || this.guaranteeConfig.antiEscapeNotice || ''
                ].filter(Boolean).join('\n')
                uni.showModal({
                    title: '预览成功',
                    content: summary,
                    showCancel: false
                })
            } catch (error) {
            }
        },
        async handlePublish() {
            if (this.submitting) {
                return
            }
            if (!this.form.agreementConfirmed) {
                uni.showToast({
                    title: '请先勾选协议',
                    icon: 'none'
                })
                return
            }
            try {
                this.submitting = true
                const payload = await this.ensurePreviewPayload()
                if (!payload || !this.previewResult.previewToken) {
                    return
                }
                const orderId = await createOrder({
                    ...payload,
                    agreementConfirmed: true,
                    agreementVersion: this.guaranteeConfig.agreementVersion || 'v2026.06',
                    previewToken: this.previewResult.previewToken,
                    antiEscapeConfirmed: true
                })
                uni.showToast({
                    title: '发布成功',
                    icon: 'success'
                })
                setTimeout(() => {
                    uni.navigateTo({
                        url: `/pages/split_order_details/split_order_details?orderId=${orderId}`
                    })
                }, 400)
            } catch (error) {
            } finally {
                this.submitting = false
            }
        },
        async handleSwitchRole() {
            if (!this.switchableRoles.length) {
                uni.showToast({
                    title: '当前没有可切换角色',
                    icon: 'none'
                })
                return
            }
            uni.showActionSheet({
                itemList: this.switchableRoles.map((item) => item.roleName || item.roleCode),
                success: async ({ tapIndex }) => {
                    const target = this.switchableRoles[tapIndex]
                    if (!target) {
                        return
                    }
                    try {
                        await switchRole({
                            targetRoleCode: target.roleCode
                        })
                        uni.showToast({
                            title: '角色已切换',
                            icon: 'success'
                        })
                        this.loadPageData(this.searchText)
                    } catch (error) {
                    }
                }
            })
        }
    }
}
</script>

<style lang="scss" scoped>
.page-container {
    min-height: 100vh;
    background: #F5F5F5;
    padding-bottom: 120rpx;

    .header {
        padding: 60rpx 30rpx 20rpx;
        display: flex;
        justify-content: space-between;
        align-items: center;
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        z-index: 100;

        .location-btn {
            gap: 8rpx;
            background: #FFFFFF;
            border-radius: 25rpx;
            opacity: 0.9;
            padding: 13rpx 15rpx;
            display: flex;
            align-items: center;
            justify-content: center;
            max-width: 520rpx;

            .location-icon,
            .location-text {
                display: flex;
            }

            .location-icon {
                width: 16rpx;
                height: 20rpx;
            }

            .location-text {
                font-size: 24rpx;
                color: #333333;
            }
        }

        .role-switch {
            width: 43rpx;
            height: 45rpx;
            display: flex;
            align-items: center;
            justify-content: center;

            .switch-icon {
                width: 48rpx;
                height: 48rpx;
            }
        }
    }

    .map-area {
        height: 400rpx;
        width: 100%;
        background: #E8F4FD;

        .map-bg {
            width: 100%;
            height: 100%;
        }
    }

    .main-content {
        background: #fff;
        border-radius: 40rpx 40rpx 0 0;
        margin-top: -60rpx;
        position: relative;
        z-index: 10;
        min-height: calc(100vh - 340rpx);
        padding-top: 50rpx;

        .top-gradient {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 100rpx;
            background: linear-gradient(to bottom, rgba(255, 255, 255, 0), rgba(255, 255, 255, 1));
            z-index: 5;
        }

        .search-box {
            display: flex;
            align-items: center;
            background: #EDF5FF;
            border-radius: 12rpx;
            padding: 0 20rpx;
            margin: 0 30rpx 20rpx;
            position: relative;
            z-index: 10;

            .search-icon {
                width: 36rpx;
                height: 36rpx;
                margin-right: 16rpx;
            }

            .search-input {
                flex: 1;
                height: 80rpx;
                font-size: 28rpx;
                color: #333;
            }

            .search-btn {
                background: #FA9D3B;
                padding: 16rpx 32rpx;
                border-radius: 30rpx;

                .search-text {
                    font-size: 28rpx;
                    color: #fff;
                    font-weight: bold;
                }
            }
        }

        .content-scroll {
            flex: 1;
            padding: 0 30rpx;
            box-sizing: border-box;

            .category-section,
            .pricing-section,
            .order-section {
                background: #fff;
                border-radius: 16rpx;
                padding: 20rpx;
                margin-bottom: 20rpx;
            }

            .category-scroll {
                white-space: nowrap;
                margin-bottom: 16rpx;

                &:last-child {
                    margin-bottom: 0;
                }

                .category-scroll-content {
                    display: inline-flex;
                    gap: 16rpx;
                }

                .category-item {
                    padding: 7rpx 16rpx;
                    white-space: nowrap;
                    flex-shrink: 0;
                    border-radius: 50rpx;
                    display: flex;
                    align-items: center;
                    justify-content: center;

                    &.active {
                        background: #BEDAFF;

                        .category-text {
                            color: #2E83F0;
                        }
                    }

                    .category-text {
                        font-size: 24rpx;
                        color: #333333;
                    }
                }
            }

            .section-title {
                font-size: 28rpx;
                font-weight: bold;
                color: #333;
                display: block;
                margin-bottom: 20rpx;
            }

            .pricing-options {
                display: flex;
                gap: 16rpx;
                flex-wrap: wrap;

                .pricing-item {
                    padding: 10rpx 18rpx;
                    border-radius: 10rpx;
                    background: #FFDCB4;
                    display: flex;
                    align-items: center;

                    &.active {
                        background: #F9A23F;
                    }

                    .pricing-text {
                        font-size: 20rpx;
                        color: #FFFFFF;
                        line-height: 20rpx;
                    }
                }
            }

            .form-item {
                margin-bottom: 16rpx;

                &.small {
                    flex: 1;
                }

                &.grow {
                    flex: 2;
                }

                .select-box {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    min-height: 80rpx;
                    padding: 20rpx;
                    border: 1px solid #CDCDCD;
                    border-radius: 8rpx;

                    .select-text {
                        font-size: 28rpx;
                        color: #666;
                    }

                    .select-arrow {
                        font-size: 20rpx;
                        color: #999;
                    }
                }

                .input-box {
                    width: 100%;
                    height: 80rpx;
                    padding: 0 20rpx;
                    border: 1px solid #CDCDCD;
                    border-radius: 8rpx;
                    font-size: 28rpx;
                    color: #333;
                    box-sizing: border-box;
                }

                .textarea-box {
                    width: 100%;
                    height: 200rpx;
                    padding: 20rpx;
                    border: 1px solid #CDCDCD;
                    border-radius: 8rpx;
                    font-size: 28rpx;
                    color: #333;
                    box-sizing: border-box;
                }
            }

            .form-row {
                display: flex;
                gap: 16rpx;
                margin-bottom: 16rpx;
            }

            .upload-list {
                display: flex;
                flex-wrap: wrap;
                gap: 16rpx;
                margin-bottom: 20rpx;

                .upload-preview,
                .upload-box {
                    width: 140rpx;
                    height: 140rpx;
                    border-radius: 8rpx;
                }

                .upload-preview {
                    position: relative;
                    overflow: hidden;

                    .upload-preview-image {
                        width: 100%;
                        height: 100%;
                    }

                    .remove-upload {
                        position: absolute;
                        top: 8rpx;
                        right: 8rpx;
                        width: 32rpx;
                        height: 32rpx;
                        line-height: 32rpx;
                        text-align: center;
                        border-radius: 50%;
                        background: rgba(0, 0, 0, 0.55);
                        color: #fff;
                        font-size: 24rpx;
                    }
                }

                .upload-box {
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    justify-content: center;
                    gap: 12rpx;
                    border: 2rpx dashed #D9D9D9;

                    .upload-icon {
                        width: 40rpx;
                        height: 40rpx;
                    }

                    .upload-text {
                        font-size: 24rpx;
                        color: #999;
                    }
                }
            }

            .preview-summary {
                background: #F6FAFF;
                border-radius: 12rpx;
                padding: 20rpx;
                margin-bottom: 20rpx;

                .preview-title {
                    font-size: 26rpx;
                    color: #2E83F0;
                    font-weight: bold;
                    display: block;
                    margin-bottom: 12rpx;
                }

                .preview-line {
                    display: block;
                    font-size: 24rpx;
                    color: #666;
                    margin-bottom: 8rpx;

                    &:last-child {
                        margin-bottom: 0;
                    }
                }
            }

            .checkbox-list {
                margin-bottom: 20rpx;

                .checkbox-item {
                    display: flex;
                    align-items: center;
                    gap: 12rpx;
                    margin-bottom: 16rpx;

                    &:last-child {
                        margin-bottom: 0;
                    }

                    .checkbox {
                        width: 36rpx;
                        height: 36rpx;
                        border: 2rpx solid #FA9D3B;
                        border-radius: 6rpx;
                        display: flex;
                        align-items: center;
                        justify-content: center;

                        &.checked {
                            background: #FA9D3B;

                            .check-icon {
                                color: #fff;
                                font-size: 22rpx;
                            }
                        }
                    }

                    .checkbox-text {
                        font-size: 26rpx;
                        color: #333;

                        .hint {
                            font-size: 22rpx;
                            color: #999;
                        }
                    }
                }
            }

            .agreement-item {
                display: flex;
                align-items: center;
                gap: 8rpx;

                .checkbox {
                    width: 36rpx;
                    height: 36rpx;
                    border: 2rpx solid #4A90F0;
                    border-radius: 6rpx;
                    display: flex;
                    align-items: center;
                    justify-content: center;

                    &.checked {
                        background: #4A90F0;

                        .check-icon {
                            color: #fff;
                            font-size: 22rpx;
                        }
                    }
                }

                .agreement-text,
                .agreement-link {
                    font-size: 24rpx;
                }

                .agreement-text {
                    color: #666;
                }

                .agreement-link {
                    color: #4A90F0;
                }
            }

            .bottom-btns {
                display: flex;
                gap: 20rpx;
                margin-bottom: 20rpx;

                .bottom-btn {
                    flex: 1;
                    padding: 28rpx;
                    border-radius: 12rpx;
                    text-align: center;

                    &.preview-btn {
                        background: #fff;
                        border: 2rpx solid #4A90F0;

                        .btn-text {
                            color: #4A90F0;
                        }
                    }

                    &.publish-btn {
                        background: #4A90F0;

                        .btn-text {
                            color: #fff;
                        }
                    }

                    .btn-text {
                        font-size: 30rpx;
                        font-weight: bold;
                    }
                }
            }

            .bottom-space {
                height: 60rpx;
            }
        }
    }
}
</style>
