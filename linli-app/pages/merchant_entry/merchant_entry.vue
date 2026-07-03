<template>
    <view class="page-container">
        <view class="header">
            <view class="back-btn" @click="goBack">
                <text class="iconfont icon-youjiantou back-icon"></text>
            </view>
            <text class="title">服务商入驻</text>
            <view class="status-pill" :class="`status-${progressStatusKey}`">
                <text class="status-text">{{ progressStatusLabel }}</text>
            </view>
        </view>

        <scroll-view class="content-scroll" scroll-y>
            <view class="hero-card">
                <text class="hero-title">服务商是接单和抢单的人</text>
                <text class="hero-desc">完成入驻后可进入接单大厅，开启服务商身份、服务类目和履约能力管理。</text>
            </view>

            <view v-if="currentEntry || onboardingProgress" class="status-card">
                <view class="status-line">
                    <text class="status-stage">{{ currentStageName || '待提交入驻' }}</text>
                    <text class="status-badge">{{ progressStatusLabel }}</text>
                </view>
                <text v-if="entryNo" class="status-desc">入驻单号：{{ entryNo }}</text>
                <text v-if="rejectReason" class="status-desc danger">驳回原因：{{ rejectReason }}</text>
                <text v-if="blockedReason" class="status-desc">阻塞原因：{{ blockedReason }}</text>
                <text v-if="nextAction" class="status-desc">{{ nextAction }}</text>
            </view>

            <view class="form-card">
                <text class="section-title">基础信息</text>
                <view class="field-item">
                    <text class="field-label">服务商名称</text>
                    <input class="field-input" :disabled="!canEditForm" v-model="form.merchantName" placeholder="请输入服务商名称" />
                </view>
                <view class="field-item">
                    <text class="field-label">联系人</text>
                    <input class="field-input" :disabled="!canEditForm" v-model="form.contactName" placeholder="请输入联系人" />
                </view>
                <view class="field-item">
                    <text class="field-label">联系电话</text>
                    <input class="field-input" :disabled="!canEditForm" v-model="form.contactMobile" type="number" placeholder="请输入联系电话" />
                </view>
                <picker
                    v-if="canEditForm"
                    mode="multiSelector"
                    :range="areaColumns"
                    range-key="name"
                    :value="areaIndexes"
                    @change="handleAreaPickerChange"
                    @columnchange="handleAreaColumnChange">
                    <view class="field-item select-field">
                        <text class="field-label">服务区域</text>
                        <view class="select-box">
                            <text class="select-text" :class="{ placeholder: !selectedRegionTextValue }">{{ selectedRegionText }}</text>
                            <text class="select-arrow">▼</text>
                        </view>
                    </view>
                </picker>
                <view v-else class="field-item select-field">
                    <text class="field-label">服务区域</text>
                    <view class="select-box disabled">
                        <text class="select-text">{{ selectedRegionText }}</text>
                    </view>
                </view>
                <view class="field-item textarea-field">
                    <text class="field-label">服务范围说明</text>
                    <textarea
                        class="field-textarea"
                        :disabled="!canEditForm"
                        v-model="form.serviceScopeDesc"
                        placeholder="例如：覆盖某城区家政、维修、保洁等服务" />
                </view>
            </view>

            <view class="form-card">
                <text class="section-title">服务类目</text>
                <view v-if="!serviceCategories.length" class="empty-tip">服务类目加载中，请稍后重试</view>
                <view class="chip-grid">
                    <view
                        v-for="item in serviceCategories"
                        :key="item.id"
                        class="chip-item"
                        :class="{ active: isCategorySelected(item.id), disabled: !canEditForm }"
                        @click="toggleCategory(item.id)">
                        <text class="chip-text">{{ item.displayName }}</text>
                    </view>
                </view>
            </view>

            <view class="form-card">
                <text class="section-title">资质选择</text>
                <view v-if="!qualificationOptions.length" class="empty-tip">请先完善认证与证件信息</view>
                <view class="qualification-list">
                    <view
                        v-for="item in qualificationOptions"
                        :key="item.id"
                        class="qualification-item"
                        :class="{ active: isQualificationSelected(item.id), disabled: !canEditForm }"
                        @click="toggleQualification(item.id)">
                        <view class="qualification-main">
                            <text class="qualification-name">{{ item.qualificationName || item.qualificationType }}</text>
                            <text class="qualification-desc">{{ item.qualificationType }}</text>
                        </view>
                        <view class="check-box">
                            <text v-if="isQualificationSelected(item.id)" class="check-icon">✓</text>
                        </view>
                    </view>
                </view>
            </view>

            <view class="form-card">
                <text class="section-title">已选摘要</text>
                <text class="summary-line">类目：{{ selectedCategoryText }}</text>
                <text class="summary-line">资质：{{ selectedQualificationText }}</text>
                <text class="summary-line">区域：{{ selectedRegionText }}</text>
            </view>

            <view class="form-card" v-if="showBusinessManageSection">
                <view class="section-row">
                    <text class="section-title">经营服务范围</text>
                    <text class="manage-link" @click="goBusinessProfile">去管理</text>
                </view>
                <text class="summary-line">主服务区域：{{ selectedRegionText }}</text>
                <text class="summary-line">当前服务点：{{ serviceAreaSummary }}</text>
                <text class="summary-line">说明：入驻资料里的主区域用于审核归属，实际接单范围请在经营资料里按服务点维护。</text>
            </view>

            <view class="submit-area">
                <view
                    class="submit-btn"
                    :class="{ disabled: submitDisabled }"
                    @click="handleSubmit">
                    <text class="submit-text">{{ submitButtonText }}</text>
                </view>
            </view>

            <view class="bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
import { getAreaTree, getProfile, getQualificationPage } from '@/api/member'
import { createMerchantEntry, getMerchantEntry, getMerchantOnboardingProgress, getServiceCategoryList } from '@/api/merchant'

function flattenCategories(list, depth = 0, target = []) {
    ;(list || []).forEach((item) => {
        target.push({
            ...item,
            displayName: `${'　'.repeat(depth)}${item.categoryName || '--'}`
        })
        flattenCategories(item.children || [], depth + 1, target)
    })
    return target
}

const STATUS_LABELS = {
    PENDING_FIRST_AUDIT: '待初审',
    PENDING_FINAL_AUDIT: '待终审',
    APPROVED_WAIT_BANK_CARD: '待绑卡',
    APPROVED_ENABLED: '已开通',
    FIRST_APPROVED: '初审通过',
    APPROVED: '终审通过',
    REJECTED: '已驳回',
    PENDING: '待处理'
}

export default {
    data() {
        return {
            profile: {},
            currentEntry: null,
            onboardingProgress: null,
            serviceCategories: [],
            qualificationOptions: [],
            areaTree: [],
            areaColumns: [[], [], []],
            areaIndexes: [0, 0, 0],
            selectedRegion: {
                province: '',
                city: '',
                district: ''
            },
            form: {
                merchantName: '',
                contactName: '',
                contactMobile: '',
                regionCode: '',
                serviceScopeDesc: '',
                serviceCategoryIds: [],
                qualificationIds: [],
                bankCardId: null
            },
            submitting: false
        }
    },
    computed: {
        canEditForm() {
            return !this.currentEntry || this.currentEntry.status === 'REJECTED'
        },
        entryNo() {
            return this.currentEntry && this.currentEntry.entryNo
        },
        currentStageName() {
            return (this.onboardingProgress && this.onboardingProgress.currentStageName)
                || (this.currentEntry && this.currentEntry.currentStageName)
                || ''
        },
        rejectReason() {
            return (this.onboardingProgress && this.onboardingProgress.rejectReason)
                || (this.currentEntry && this.currentEntry.rejectReason)
                || ''
        },
        blockedReason() {
            return (this.onboardingProgress && this.onboardingProgress.blockedReason)
                || (this.currentEntry && this.currentEntry.onboardingBlockedReason)
                || ''
        },
        nextAction() {
            return (this.onboardingProgress && this.onboardingProgress.nextAction) || ''
        },
        progressStatusKey() {
            return (this.onboardingProgress && this.onboardingProgress.progressStatus)
                || (this.currentEntry && this.currentEntry.progressStatus)
                || (this.currentEntry && this.currentEntry.status)
                || 'PENDING'
        },
        progressStatusLabel() {
            return STATUS_LABELS[this.progressStatusKey] || this.progressStatusKey || '待处理'
        },
        selectedRegionTextValue() {
            return [this.selectedRegion.province, this.selectedRegion.city, this.selectedRegion.district].filter(Boolean).join(' / ')
        },
        selectedRegionText() {
            return this.selectedRegionTextValue || '请选择省 / 市 / 区'
        },
        selectedCategoryText() {
            const names = this.serviceCategories
                .filter((item) => this.form.serviceCategoryIds.includes(item.id))
                .map((item) => item.categoryName)
            return names.length ? names.join('、') : '未选择'
        },
        selectedQualificationText() {
            const names = this.qualificationOptions
                .filter((item) => this.form.qualificationIds.includes(item.id))
                .map((item) => item.qualificationName || item.qualificationType)
            return names.length ? names.join('、') : '未选择'
        },
        submitButtonText() {
            if (!this.canEditForm) {
                if (this.progressStatusKey === 'APPROVED_ENABLED') {
                    return '已开通接单'
                }
                if (this.progressStatusKey === 'APPROVED_WAIT_BANK_CARD') {
                    return '去绑定银行卡'
                }
                return '查看进度'
            }
            return this.currentEntry && this.currentEntry.status === 'REJECTED' ? '重新提交入驻申请' : '提交入驻申请'
        },
        submitDisabled() {
            return this.submitting
        },
        showBusinessManageSection() {
            return ['APPROVED_WAIT_BANK_CARD', 'APPROVED_ENABLED'].includes(this.progressStatusKey)
                || ((this.profile && this.profile.serviceAreas) || []).length > 0
        },
        serviceAreaSummary() {
            const areas = ((this.profile && this.profile.serviceAreas) || []).filter(Boolean)
            if (!areas.length) {
                return '暂无服务点'
            }
            return areas.length <= 2
                ? areas.map((item) => item.pointName || item.district || '服务点').join('、')
                : `${areas[0].pointName || areas[0].district || '服务点'} 等 ${areas.length} 个服务点`
        }
    },
    onShow() {
        this.loadPageData()
    },
    methods: {
        async loadPageData() {
            try {
                const [, profile, currentEntry, onboardingProgress, categories, qualificationPage] = await Promise.all([
                    this.ensureAreaTree(),
                    getProfile({ silent: true }).catch(() => ({})),
                    getMerchantEntry({ silent: true }).catch(() => null),
                    getMerchantOnboardingProgress({ silent: true }).catch(() => null),
                    getServiceCategoryList({ silent: true }).catch(() => []),
                    getQualificationPage({ pageNo: 1, pageSize: 100 }, { silent: true }).catch(() => ({ list: [] }))
                ])
                this.profile = profile || {}
                this.currentEntry = currentEntry || null
                this.onboardingProgress = onboardingProgress || null
                this.serviceCategories = flattenCategories(categories || [])
                this.qualificationOptions = ((qualificationPage && qualificationPage.list) || [])
                    .filter((item) => !item.auditStatus || item.auditStatus === 'APPROVED')
                this.syncForm()
            } catch (error) {
            }
        },
        syncForm() {
            if (this.currentEntry) {
                this.form = {
                    merchantName: this.currentEntry.merchantName || this.profile.nickname || '',
                    contactName: this.currentEntry.contactName || this.profile.nickname || '',
                    contactMobile: this.currentEntry.contactMobile || this.profile.mobile || '',
                    regionCode: this.currentEntry.regionCode || '',
                    serviceScopeDesc: this.currentEntry.serviceScopeDesc || '',
                    serviceCategoryIds: Array.isArray(this.currentEntry.serviceCategoryIds) ? [...this.currentEntry.serviceCategoryIds] : [],
                    qualificationIds: Array.isArray(this.currentEntry.qualificationIds) ? [...this.currentEntry.qualificationIds] : [],
                    bankCardId: this.currentEntry.bankCardId || null
                }
                this.syncRegionPickerFromCode()
                return
            }
            this.form = {
                merchantName: this.profile.nickname ? `${this.profile.nickname}服务商` : '',
                contactName: this.profile.nickname || '',
                contactMobile: this.profile.mobile || '',
                regionCode: '',
                serviceScopeDesc: '',
                serviceCategoryIds: [],
                qualificationIds: [],
                bankCardId: null
            }
            this.clearRegionSelection()
            this.resetAreaPicker()
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
                this.areaIndexes = [0, 0, 0]
            }
        },
        clearRegionSelection() {
            this.selectedRegion = {
                province: '',
                city: '',
                district: ''
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
            this.selectedRegion = {
                province: province && province.name ? province.name : '',
                city: city && city.name ? city.name : '',
                district: district && district.name ? district.name : ''
            }
            this.form.regionCode = district && district.id ? `${district.id}` : ''
        },
        syncRegionPickerFromCode() {
            if (!this.form.regionCode || !this.areaTree.length) {
                this.clearRegionSelection()
                this.resetAreaPicker()
                return
            }
            const path = this.findAreaPath(this.areaTree, this.form.regionCode)
            if (!path) {
                this.clearRegionSelection()
                this.resetAreaPicker()
                return
            }
            const indexes = [path.provinceIndex, path.cityIndex, path.districtIndex]
            this.areaColumns = this.buildAreaColumns(indexes)
            this.areaIndexes = indexes
            this.selectedRegion = {
                province: path.province.name || '',
                city: path.city.name || '',
                district: path.district.name || ''
            }
            this.form.regionCode = path.district.id ? `${path.district.id}` : this.form.regionCode
        },
        findAreaPath(provinceList, regionCode) {
            for (let provinceIndex = 0; provinceIndex < provinceList.length; provinceIndex += 1) {
                const province = provinceList[provinceIndex]
                const cityList = province.children || []
                for (let cityIndex = 0; cityIndex < cityList.length; cityIndex += 1) {
                    const city = cityList[cityIndex]
                    const districtList = city.children || []
                    for (let districtIndex = 0; districtIndex < districtList.length; districtIndex += 1) {
                        const district = districtList[districtIndex]
                        if (`${district.id}` === `${regionCode}`) {
                            return {
                                provinceIndex,
                                cityIndex,
                                districtIndex,
                                province,
                                city,
                                district
                            }
                        }
                    }
                }
            }
            return undefined
        },
        isCategorySelected(id) {
            return this.form.serviceCategoryIds.includes(id)
        },
        toggleCategory(id) {
            if (!this.canEditForm) {
                return
            }
            if (this.form.serviceCategoryIds.includes(id)) {
                this.form.serviceCategoryIds = this.form.serviceCategoryIds.filter((item) => item !== id)
            } else {
                this.form.serviceCategoryIds = this.form.serviceCategoryIds.concat(id)
            }
        },
        isQualificationSelected(id) {
            return this.form.qualificationIds.includes(id)
        },
        toggleQualification(id) {
            if (!this.canEditForm) {
                return
            }
            if (this.form.qualificationIds.includes(id)) {
                this.form.qualificationIds = this.form.qualificationIds.filter((item) => item !== id)
            } else {
                this.form.qualificationIds = this.form.qualificationIds.concat(id)
            }
        },
        validateForm() {
            if (!this.form.merchantName) {
                return '请填写服务商名称'
            }
            if (!this.form.contactName) {
                return '请填写联系人'
            }
            if (!this.form.contactMobile || this.form.contactMobile.length < 11) {
                return '请填写正确的联系电话'
            }
            if (!this.form.regionCode) {
                return '请选择服务区域'
            }
            if (!this.form.serviceCategoryIds.length) {
                return '请选择至少一个服务类目'
            }
            if (!this.form.qualificationIds.length) {
                return '请选择至少一个资质'
            }
            return ''
        },
        async handleSubmit() {
            if (!this.canEditForm) {
                if (this.progressStatusKey === 'APPROVED_ENABLED') {
                    uni.setStorageSync('linbang_order_tab_mode', 'accept')
                    uni.switchTab({
                        url: '/pages/order/order'
                    })
                    return
                }
                if (this.progressStatusKey === 'APPROVED_WAIT_BANK_CARD') {
                    uni.navigateTo({
                        url: '/pages/bank_card_management/bank_card_management'
                    })
                    return
                }
                uni.showToast({
                    title: '当前申请正在处理中',
                    icon: 'none'
                })
                return
            }
            const errorText = this.validateForm()
            if (errorText) {
                uni.showToast({
                    title: errorText,
                    icon: 'none'
                })
                return
            }
            try {
                this.submitting = true
                await createMerchantEntry({
                    ...this.form,
                    serviceCategoryIds: this.form.serviceCategoryIds,
                    qualificationIds: this.form.qualificationIds
                })
                uni.showToast({
                    title: '入驻申请已提交',
                    icon: 'success'
                })
                this.loadPageData()
            } catch (error) {
            } finally {
                this.submitting = false
            }
        },
        goBack() {
            this.$navigateBack()
        },
        goBusinessProfile() {
            uni.navigateTo({
                url: '/pages/merchant_business/merchant_business'
            })
        }
    }
}
</script>

<style lang="scss" scoped>
.page-container {
    min-height: 100vh;
    background: linear-gradient(180deg, #eaf3ff 0%, #f7f9fc 120rpx, #f5f7fb 100%);
}

.header {
    padding: 60rpx 30rpx 26rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: linear-gradient(180deg, #4a90f0, #6aa8ff);
}

.back-icon {
    font-size: 40rpx;
    color: #fff;
    transform: rotate(180deg);
}

.title {
    color: #fff;
    font-size: 34rpx;
    font-weight: bold;
}

.status-pill {
    padding: 8rpx 18rpx;
    border-radius: 999rpx;
    background: rgba(255, 255, 255, 0.18);
    min-width: 96rpx;
    min-height: 48rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    box-sizing: border-box;
}

.status-pill.status-PENDING_FIRST_AUDIT,
.status-pill.status-PENDING_FINAL_AUDIT {
    background: #fff4e5;
}

.status-pill.status-APPROVED_WAIT_BANK_CARD {
    background: #fff6e6;
}

.status-pill.status-FIRST_APPROVED,
.status-pill.status-APPROVED,
.status-pill.status-APPROVED_ENABLED {
    background: rgba(234, 248, 240, 0.22);
}

.status-pill.status-REJECTED {
    background: rgba(253, 236, 236, 0.22);
}

.status-text {
    color: #fff;
    font-size: 22rpx;
    line-height: 1;
    text-align: center;
}

.status-pill.status-PENDING_FIRST_AUDIT .status-text,
.status-pill.status-PENDING_FINAL_AUDIT .status-text {
    color: #d9822b;
}

.status-pill.status-APPROVED_WAIT_BANK_CARD .status-text {
    color: #b77b00;
}

.status-pill.status-FIRST_APPROVED .status-text,
.status-pill.status-APPROVED .status-text,
.status-pill.status-APPROVED_ENABLED .status-text {
    color: #1f8a57;
}

.status-pill.status-REJECTED .status-text {
    color: #d9534f;
}

.content-scroll {
    padding: 20rpx;
    box-sizing: border-box;
}

.hero-card,
.status-card,
.form-card {
    background: #fff;
    border-radius: 24rpx;
    padding: 24rpx;
    margin-bottom: 20rpx;
    box-shadow: 0 10rpx 24rpx rgba(21, 84, 166, 0.06);
}

.section-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.manage-link {
    color: #2e83f0;
    font-size: 24rpx;
}

.hero-title {
    display: block;
    font-size: 32rpx;
    font-weight: bold;
    color: #183153;
    margin-bottom: 10rpx;
}

.hero-desc,
.status-desc,
.summary-line,
.empty-tip {
    display: block;
    font-size: 24rpx;
    color: #6c7b90;
    line-height: 1.7;
}

.status-line {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10rpx;
}

.status-stage {
    font-size: 30rpx;
    font-weight: bold;
    color: #183153;
}

.status-badge {
    font-size: 22rpx;
    padding: 6rpx 16rpx;
    border-radius: 999rpx;
    background: #edf5ff;
    color: #2e83f0;
}

.status-APPROVED_WAIT_BANK_CARD {
    background: #fff6e6;
    color: #b77b00;
}

.status-PENDING_FIRST_AUDIT,
.status-PENDING_FINAL_AUDIT {
    background: #fff4e5;
    color: #d9822b;
}

.status-FIRST_APPROVED,
.status-APPROVED,
.status-APPROVED_ENABLED {
    background: #eaf8f0;
    color: #1f8a57;
}

.status-REJECTED {
    background: #fdecec;
    color: #d9534f;
}

.status-desc.danger {
    color: #d9534f;
}

.section-title {
    display: block;
    font-size: 30rpx;
    font-weight: bold;
    color: #183153;
    margin-bottom: 18rpx;
}

.field-item {
    margin-bottom: 18rpx;
}

.field-label {
    display: block;
    font-size: 24rpx;
    color: #6c7b90;
    margin-bottom: 10rpx;
}

.field-input,
.field-textarea {
    width: 100%;
    box-sizing: border-box;
    background: #f7f9fc;
    border-radius: 16rpx;
    padding: 20rpx 18rpx;
    font-size: 26rpx;
    color: #183153;
}

.field-input {
    height: 84rpx;
}

.field-textarea {
    min-height: 180rpx;
}

.select-box {
    width: 100%;
    box-sizing: border-box;
    background: #f7f9fc;
    border-radius: 16rpx;
    padding: 20rpx 18rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.select-text {
    font-size: 26rpx;
    color: #183153;
}

.select-text.placeholder {
    color: #92a0b5;
}

.select-arrow {
    font-size: 20rpx;
    color: #92a0b5;
}

.select-box.disabled {
    justify-content: flex-start;
}

.chip-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 14rpx;
}

.chip-item {
    padding: 14rpx 18rpx;
    border-radius: 999rpx;
    background: #f4f7fb;
    border: 1rpx solid transparent;
}

.chip-item.active {
    background: #e8f2ff;
    border-color: #4a90f0;
}

.chip-item.disabled,
.qualification-item.disabled {
    opacity: 0.65;
}

.chip-text {
    font-size: 24rpx;
    color: #183153;
}

.qualification-list {
    display: flex;
    flex-direction: column;
    gap: 14rpx;
}

.qualification-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 18rpx 20rpx;
    border-radius: 18rpx;
    background: #f7f9fc;
    border: 1rpx solid transparent;
}

.qualification-item.active {
    background: #eef6ff;
    border-color: #4a90f0;
}

.qualification-main {
    display: flex;
    flex-direction: column;
    gap: 6rpx;
}

.qualification-name {
    font-size: 26rpx;
    color: #183153;
    font-weight: 600;
}

.qualification-desc {
    font-size: 22rpx;
    color: #7f8ea3;
}

.check-box {
    width: 34rpx;
    height: 34rpx;
    border-radius: 50%;
    border: 2rpx solid #c9d5e6;
    display: flex;
    align-items: center;
    justify-content: center;
}

.qualification-item.active .check-box {
    border-color: #4a90f0;
    background: #4a90f0;
}

.check-icon {
    font-size: 22rpx;
    color: #fff;
    line-height: 1;
}

.submit-area {
    padding: 10rpx 0 20rpx;
}

.submit-btn {
    height: 88rpx;
    border-radius: 18rpx;
    background: linear-gradient(135deg, #4a90f0, #2e83f0);
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 14rpx 28rpx rgba(46, 131, 240, 0.25);
}

.submit-btn.disabled {
    background: #c9d5e6;
    box-shadow: none;
}

.submit-text {
    color: #fff;
    font-size: 30rpx;
    font-weight: bold;
}

.bottom-space {
    height: 80rpx;
}
</style>

