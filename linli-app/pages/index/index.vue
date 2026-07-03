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
            <!-- #ifdef H5 -->
            <view id="home-amap" class="amap-container"></view>
            <view class="map-center-pin">
                <view class="map-center-pin-head"></view>
                <view class="map-center-pin-tail"></view>
            </view>
            <!-- #endif -->
            <!-- #ifndef H5 -->
            <map class="native-map" :longitude="mapCenter.longitude" :latitude="mapCenter.latitude"
                :markers="mapMarkers" scale="15" show-location></map>
            <!-- #endif -->
        </view>

        <view class="main-content">
            <view class="search-box">
                <view class="search-input-wrap">
                    <image class="search-icon" src="/static/img/home/search@3x.png" />
                    <input class="search-input" placeholder="快速搜索您的需求" v-model="searchText" confirm-type="search"
                        @confirm="handleSearch" />
                </view>
                <view class="search-btn" @click="handleSearch">
                    <text class="search-text">搜索</text>
                </view>
            </view>

            <scroll-view class="content-scroll" scroll-y refresher-enabled :refresher-triggered="refreshing"
                @refresherrefresh="handleRefresh">
                <view class="surface-card category-section" v-if="level1Categories.length">
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

                <view class="surface-card category-section category-empty" v-else>
                    <text class="category-empty-text">服务类目加载中，请稍后下拉刷新</text>
                </view>

                <view class="surface-card pricing-section">
                    <view class="section-title-row">
                        <text class="section-title compact">计价方式</text>
                        <view class="pricing-tip-trigger" @click="handlePricingTip">
                            <text class="pricing-tip-trigger-text">!</text>
                        </view>
                    </view>
                    <view class="pricing-options">
                        <view v-for="item in pricingOptions" :key="item.value" class="pricing-item"
                            :class="{ active: form.pricingMode === item.value }" @click="selectPricing(item.value)">
                            <text class="pricing-text">{{ item.label }}</text>
                        </view>
                    </view>
                </view>

                <view class="surface-card order-section">
                    <text class="section-title">订单信息</text>

                    <view class="form-item">
                        <view class="select-box" @click="handleSelectAddress">
                            <text class="select-text">{{ selectedAddressLabel || '请选择服务地址' }}</text>
                            <text class="select-arrow">▼</text>
                        </view>
                    </view>

                    <view class="form-item">
                        <input class="input-box" placeholder="请输入详细地址" v-model="form.detailAddress" />
                    </view>

                    <view class="form-row compact-row">
                        <view class="form-item small">
                            <input class="input-box compact-input" :placeholder="durationPlaceholder" v-model="form.serviceDurationDesc" />
                        </view>
                        <view class="form-item small">
                            <input class="input-box compact-input" type="digit" :placeholder="quantityPlaceholder" v-model="form.quantity" />
                        </view>
                        <view class="form-item small">
                            <input class="input-box compact-input" type="digit" :placeholder="budgetPlaceholder" v-model="form.budgetAmount" />
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
                            <text class="upload-icon">+</text>
                            <text class="upload-text">{{ uploading ? '上传中' : '+上传图片' }}</text>
                        </view>
                    </view>

                    <view class="safety-tip" @click="handleAgreement">
                        <text class="safety-tip-label">交易保障</text>
                        <text class="safety-tip-text">{{ currentAgreementTitle }}</text>
                        <text class="safety-tip-arrow">></text>
                    </view>

                    <view class="checkbox-list" v-if="showInvoiceOption || showSplitOption">
                        <view class="checkbox-item" v-if="showInvoiceOption">
                            <view class="checkbox" :class="{ checked: form.needInvoice }" @click="toggleInvoice">
                                <text v-if="form.needInvoice" class="check-icon">✓</text>
                            </view>
                            <text class="checkbox-text">是否开票</text>
                            <text class="hint">（注意是否开票会影响您的最终接单费率）</text>
                        </view>

                        <view class="checkbox-item" v-if="showSplitOption">
                            <view class="checkbox" :class="{ checked: form.needSplit }" @click="toggleSplit">
                                <text v-if="form.needSplit" class="check-icon">✓</text>
                            </view>
                            <text class="checkbox-text">订单拆分</text>
                            <text class="hint">（按条件拆分订单）</text>
                        </view>
                    </view>

                    <view class="agreement-item" v-if="showAgreementOption">
                        <view class="checkbox" :class="{ checked: form.agreementConfirmed }" @click="toggleAgreement">
                            <text v-if="form.agreementConfirmed" class="check-icon">✓</text>
                        </view>
                        <text class="agreement-text">我已阅读并同意</text>
                        <text class="agreement-link" @click.stop="handleAgreement">《{{ currentAgreementTitle }}》</text>
                    </view>

                    <view class="extra-fields">
                        <view class="form-row">
                            <view class="form-item small">
                                <input class="input-box" type="number" placeholder="服务人数" v-model="form.workerCount" />
                            </view>
                            <view class="price-total-box grow">
                                <text class="price-total-label">{{ hasCustomPriceItems ? '明细合计' : '预算金额' }}</text>
                                <text class="price-total-value">¥{{ hasCustomPriceItems ? priceItemsTotalText : (form.budgetAmount || '0.00') }}</text>
                            </view>
                        </view>
                        <view class="price-detail-card">
                            <view class="price-detail-header">
                                <text class="price-detail-title">价格明细</text>
                                <text class="price-detail-summary">{{ hasCustomPriceItems ? '合计 ¥' + priceItemsTotalText : '不填则按预算金额提交' }}</text>
                            </view>
                            <view v-if="!form.priceItems.length" class="price-detail-empty">
                                <text class="price-detail-empty-text">可按人工费、材料费、附加费拆分录入，方便预览和后续详情展示</text>
                            </view>
                            <view v-for="(item, index) in form.priceItems" :key="`price-item-${index}`" class="price-item-row">
                                <view class="price-item-top">
                                    <view class="price-type-picker" @click="handleSelectPriceItemType(index)">
                                        <text class="price-type-text">{{ getPriceItemTypeLabel(item.itemType) }}</text>
                                        <text class="price-type-arrow">▼</text>
                                    </view>
                                    <view class="price-item-remove" @click="removePriceItem(index)">
                                        <text class="price-item-remove-text">删除</text>
                                    </view>
                                </view>
                                <view class="form-row">
                                    <view class="form-item grow">
                                        <input class="input-box" placeholder="明细名称，如人工费、瓷砖材料" v-model="item.itemName" @input="handlePriceItemsChange" />
                                    </view>
                                    <view class="form-item small">
                                        <input class="input-box" type="digit" placeholder="金额" v-model="item.itemAmount" @input="handlePriceItemsChange" />
                                    </view>
                                </view>
                            </view>
                            <view class="price-item-add" @click="addPriceItem">
                                <text class="price-item-add-text">+ 新增价格明细</text>
                            </view>
                        </view>
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
import { getAddressPage, getNearbyAddressPois, getRoleContext, resolveAddressLocation, switchRole } from '@/api/member'
import { uploadAppFile } from '@/api/infra'
import { getServiceCategoryList } from '@/api/merchant'
import { createOrder, getGuaranteeConfig, previewOrder } from '@/api/order'
import { getAppSettings } from '@/api/platform'
import { getPlatformSettings, hasLogin, setPlatformSettings } from '@/utils/auth'
import { getAmapJsKey, getAmapSecurityJsCode } from '@/config/app'
import { syncMessageUnreadCount } from '@/services/message-unread'
import {
    buildAddressText,
    extractUploadedFile,
    getPricingModeLabel,
    PRICE_ITEM_TYPE_OPTIONS,
    PRICING_MODE_OPTIONS
} from '@/utils/linbang'

const ORDER_PREVIEW_STORAGE_KEY = 'linbang_order_preview_snapshot'

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

const DEFAULT_MAP_CENTER = {
    longitude: 113.94352,
    latitude: 22.540503
}

function createEmptyPriceItem() {
    return {
        itemType: 'CUSTOM',
        itemName: '',
        itemAmount: ''
    }
}

export default {
    components: {
        tabBar
    },
    data() {
        return {
            searchText: '',
            refreshing: false,
            locating: false,
            uploading: false,
            submitting: false,
            categories: [],
            addressList: [],
            roleContext: {},
            guaranteeConfig: {},
            previewResult: {},
            uploadedFiles: [],
            mapStatusText: '',
            mapCenter: { ...DEFAULT_MAP_CENTER },
            mapMarkers: [{
                id: 1,
                longitude: DEFAULT_MAP_CENTER.longitude,
                latitude: DEFAULT_MAP_CENTER.latitude,
                width: 28,
                height: 36
            }],
            amap: null,
            amapMarker: null,
            amapLoadingPromise: null,
            mapInitialized: false,
            mapResolveTimer: null,
            resolvingMapAddress: false,
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
                priceItems: []
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
        hasCustomPriceItems() {
            return this.normalizedPriceItems.length > 0
        },
        normalizedPriceItems() {
            return (this.form.priceItems || [])
                .map((item) => ({
                    itemType: item && item.itemType ? item.itemType : 'CUSTOM',
                    itemName: `${(item && item.itemName) || ''}`.trim(),
                    itemAmount: `${(item && item.itemAmount) || ''}`.trim()
                }))
                .filter((item) => item.itemName || item.itemAmount)
        },
        priceItemsTotal() {
            return this.normalizedPriceItems.reduce((sum, item) => {
                const amount = Number(item.itemAmount)
                return sum + (Number.isNaN(amount) ? 0 : amount)
            }, 0)
        },
        priceItemsTotalText() {
            return this.$fmt.formatMoney(this.priceItemsTotal || 0)
        },
        showInvoiceOption() {
            if (typeof this.previewResult.invoiceSupported === 'boolean') {
                return this.previewResult.invoiceSupported
            }
            const current = this.currentCategory || {}
            return current.supportInvoice !== false
        },
        showSplitOption() {
            if (typeof this.previewResult.splitSupported === 'boolean') {
                return this.previewResult.splitSupported
            }
            const current = this.currentCategory || {}
            return current.supportSplit !== false
        },
        showAgreementOption() {
            if (typeof this.previewResult.agreementRequired === 'boolean') {
                return this.previewResult.agreementRequired
            }
            return true
        },
        currentAgreementTitle() {
            if (this.previewResult.agreementTitle) {
                return this.previewResult.agreementTitle
            }
            if (this.previewResult.agreementType === 'TRADE_GUARANTEE') {
                return this.guaranteeConfig.agreementTitle || '交易保障协议'
            }
            return this.guaranteeConfig.projectEscrowAgreementTitle || '工程托管协议'
        },
        currentAgreementContent() {
            if (this.previewResult.agreementContent) {
                return this.previewResult.agreementContent
            }
            if (this.previewResult.agreementType === 'TRADE_GUARANTEE') {
                return this.guaranteeConfig.serviceAgreement || '请在发布前确认交易保障协议内容。'
            }
            return this.guaranteeConfig.projectEscrowAgreement || this.guaranteeConfig.serviceAgreement || '请在发布前确认交易保障协议内容。'
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
        quantityUnitLabel() {
            const current = this.currentCategory || {}
            return current.quantityUnitLabel || ''
        },
        quantitySplitEnabled() {
            const current = this.currentCategory || {}
            if (typeof current.quantitySplitEnabled === 'boolean') {
                return current.quantitySplitEnabled
            }
            return null
        },
        hasQuantitySplitConfig() {
            const current = this.currentCategory || {}
            return !!current.quantityUnitLabel || typeof current.quantitySplitEnabled === 'boolean'
        },
        durationPlaceholder() {
            const mapping = {
                HOURLY: '服务时段',
                BY_UNIT: '预计工期',
                CONTRACT: '承接周期',
                OUTSOURCING: '交付周期',
                FIXED_PRICE: '预计工期'
            }
            return mapping[this.form.pricingMode] || '工期说明'
        },
        quantityPlaceholder() {
            if (this.quantityUnitLabel) {
                return `请输入${this.quantityUnitLabel}数`
            }
            const mapping = {
                HOURLY: '服务小时数',
                BY_UNIT: '服务数量',
                CONTRACT: '份数/批次',
                OUTSOURCING: '任务数量',
                FIXED_PRICE: '服务数量'
            }
            return mapping[this.form.pricingMode] || '服务数量'
        },
        budgetPlaceholder() {
            const mapping = {
                HOURLY: '总预算金额',
                BY_UNIT: '预算金额',
                CONTRACT: '承包预算',
                OUTSOURCING: '外包预算',
                FIXED_PRICE: '一口价预算'
            }
            return mapping[this.form.pricingMode] || '预算金额'
        },
        durationExample() {
            const mapping = {
                HOURLY: '例：今天下午2点到5点',
                BY_UNIT: '例：周六上午上门',
                CONTRACT: '例：3天内完成',
                OUTSOURCING: '例：本周内交付',
                FIXED_PRICE: '例：今晚7点后上门'
            }
            return mapping[this.form.pricingMode] || '例：填写服务时间安排'
        },
        quantityExample() {
            if (this.quantitySplitEnabled === true && this.quantityUnitLabel) {
                return `例：按${this.quantityUnitLabel}填写`
            }
            const mapping = {
                HOURLY: '例：3小时填3',
                BY_UNIT: '例：6扇窗填6',
                CONTRACT: '例：1批填1',
                OUTSOURCING: '例：2项任务填2',
                FIXED_PRICE: '例：1单填1'
            }
            return mapping[this.form.pricingMode] || '例：按服务数量填写'
        },
        budgetExample() {
            const mapping = {
                HOURLY: '例：总预算180元',
                BY_UNIT: '例：预算300元',
                CONTRACT: '例：承包预算2000元',
                OUTSOURCING: '例：外包预算1500元',
                FIXED_PRICE: '例：一口价160元'
            }
            return mapping[this.form.pricingMode] || '例：填写愿意支付的总金额'
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
        syncMessageUnreadCount({ silent: true })
        this.loadPageData()
    },
    beforeUnmount() {
        if (this.mapResolveTimer) {
            clearTimeout(this.mapResolveTimer)
            this.mapResolveTimer = null
        }
        if (this.amap && this.amap.off) {
            this.amap.off('moveend', this.handleH5MapMoveEnd)
            this.amap.off('click', this.handleH5MapClick)
        }
        if (this.amap && this.amap.destroy) {
            this.amap.destroy()
        }
        this.amap = null
        this.amapMarker = null
        this.mapInitialized = false
    },
    methods: {
        initHomeMap() {
            this.updateMapCenter(this.mapCenter.longitude, this.mapCenter.latitude)
            if (this.mapInitialized) {
                return
            }
            this.mapInitialized = true
            // #ifdef H5
            this.initH5Amap()
            // #endif
        },
        initH5Amap() {
            this.loadAmapScript()
                .then((AMap) => {
                    if (this.amap) {
                        return
                    }
                    this.mapStatusText = ''
                    this.amap = new AMap.Map('home-amap', {
                        center: [this.mapCenter.longitude, this.mapCenter.latitude],
                        zoom: 15,
                        resizeEnable: true,
                        animateEnable: false,
                        jogEnable: false,
                        pitchEnable: false,
                        rotateEnable: false,
                        showIndoorMap: false,
                        features: ['bg', 'road', 'point'],
                        mapStyle: 'amap://styles/normal'
                    })
                    this.amap.on('moveend', this.handleH5MapMoveEnd)
                    this.amap.on('click', this.handleH5MapClick)
                    this.mapStatusText = '拖动地图可直接选择服务地址'
                })
                .catch((error) => {
                    this.mapStatusText = (error && error.message) || '地图加载失败'
                })
        },
        handleH5MapClick(event) {
            const lnglat = event && event.lnglat
            const longitude = lnglat && typeof lnglat.getLng === 'function' ? lnglat.getLng() : lnglat && lnglat.lng
            const latitude = lnglat && typeof lnglat.getLat === 'function' ? lnglat.getLat() : lnglat && lnglat.lat
            if (!longitude || !latitude) {
                return
            }
            if (this.amap && this.amap.setCenter) {
                this.amap.setCenter([longitude, latitude])
            }
            this.updateMapCenter(longitude, latitude)
            this.scheduleResolveMapCenter('正在更新服务地址')
        },
        handleH5MapMoveEnd() {
            if (!this.amap || !this.amap.getCenter) {
                return
            }
            const center = this.amap.getCenter()
            const longitude = center && typeof center.getLng === 'function' ? center.getLng() : center && center.lng
            const latitude = center && typeof center.getLat === 'function' ? center.getLat() : center && center.lat
            this.updateMapCenter(longitude, latitude)
            this.scheduleResolveMapCenter('正在解析地图中心地址')
        },
        scheduleResolveMapCenter(statusText = '正在解析地图中心地址') {
            if (this.mapResolveTimer) {
                clearTimeout(this.mapResolveTimer)
            }
            this.mapStatusText = statusText
            this.mapResolveTimer = setTimeout(() => {
                this.mapResolveTimer = null
                this.resolveMapCenterAddress()
            }, 350)
        },
        async resolveMapCenterAddress() {
            if (this.resolvingMapAddress) {
                return
            }
            const longitude = this.normalizeCoordinate(this.mapCenter.longitude)
            const latitude = this.normalizeCoordinate(this.mapCenter.latitude)
            if (!longitude || !latitude) {
                return
            }
            this.resolvingMapAddress = true
            try {
                const [resolved, nearbyPois] = await Promise.all([
                    resolveAddressLocation({
                        longitude,
                        latitude,
                        detailAddress: ''
                    }),
                    getNearbyAddressPois({
                        longitude,
                        latitude,
                        radiusMeters: 200
                    }).catch(() => [])
                ])
                const nearestPoi = Array.isArray(nearbyPois)
                    ? nearbyPois.find((item) => item && item.name && (!item.distanceMeters || item.distanceMeters <= 120))
                    : null
                const detailAddress = nearestPoi && nearestPoi.name
                    ? nearestPoi.name
                    : (resolved.detailAddress || '')
                this.applyAddress({
                    ...resolved,
                    longitude,
                    latitude,
                    detailAddress
                })
                this.mapStatusText = '拖动地图可直接选择服务地址'
            } catch (error) {
                this.mapStatusText = '地址解析失败，请继续拖动地图重试'
            } finally {
                this.resolvingMapAddress = false
            }
        },
        async loadAmapScript() {
            if (typeof window === 'undefined' || typeof document === 'undefined') {
                return Promise.reject(new Error('当前环境不支持 H5 地图'))
            }
            if (window.AMap) {
                return Promise.resolve(window.AMap)
            }
            if (this.amapLoadingPromise) {
                return this.amapLoadingPromise
            }
            let settings = getPlatformSettings() || {}
            if (!getAmapJsKey(settings)) {
                settings = await getAppSettings().then((value) => setPlatformSettings(value || {})).catch(() => settings)
            }
            const key = getAmapJsKey(settings)
            const securityJsCode = getAmapSecurityJsCode(settings)
            if (!key) {
                return Promise.reject(new Error('请配置高德 JS API Key'))
            }
            if (securityJsCode) {
                window._AMapSecurityConfig = {
                    securityJsCode
                }
            }
            this.mapStatusText = '地图加载中'
            this.amapLoadingPromise = new Promise((resolve, reject) => {
                const script = document.createElement('script')
                script.src = `https://webapi.amap.com/maps?v=1.4.15&key=${encodeURIComponent(key)}`
                script.async = true
                script.onload = () => {
                    if (window.AMap) {
                        resolve(window.AMap)
                        return
                    }
                    reject(new Error('高德地图 SDK 加载异常'))
                }
                script.onerror = () => reject(new Error('高德地图 SDK 加载失败'))
                document.head.appendChild(script)
            }).finally(() => {
                this.amapLoadingPromise = null
            })
            return this.amapLoadingPromise
        },
        updateMapCenter(longitude, latitude) {
            const lng = this.normalizeCoordinate(longitude)
            const lat = this.normalizeCoordinate(latitude)
            if (!lng || !lat) {
                return
            }
            const currentLng = this.normalizeCoordinate(this.mapCenter.longitude)
            const currentLat = this.normalizeCoordinate(this.mapCenter.latitude)
            const centerUnchanged = currentLng && currentLat
                && Math.abs(currentLng - lng) < 0.000001
                && Math.abs(currentLat - lat) < 0.000001
            this.mapCenter = {
                longitude: lng,
                latitude: lat
            }
            this.mapMarkers = [{
                ...this.mapMarkers[0],
                longitude: lng,
                latitude: lat
            }]
            // #ifdef H5
            if (this.amap && !centerUnchanged) {
                this.amap.setCenter([lng, lat])
            }
            // #endif
        },
        async loadPageData(keyword = '') {
            try {
                const loggedIn = hasLogin()
                const normalizedKeyword = `${keyword || ''}`.trim()
                const [categories, addressPage, guaranteeConfig, roleContext] = await Promise.all([
                    getServiceCategoryList(normalizedKeyword, { silent: true }).catch(() => []),
                    loggedIn
                        ? getAddressPage({ pageNo: 1, pageSize: 50 }, { silent: true }).catch(() => ({ list: [] }))
                        : Promise.resolve({ list: [] }),
                    loggedIn
                        ? getGuaranteeConfig({ silent: true }).catch(() => ({}))
                        : Promise.resolve({}),
                    loggedIn
                        ? getRoleContext({ silent: true }).catch(() => ({}))
                        : Promise.resolve({})
                ])
                this.categories = categories || []
                this.addressList = (addressPage && addressPage.list) || []
                this.guaranteeConfig = guaranteeConfig || {}
                this.roleContext = roleContext || {}
                this.ensureCategorySelection()
                this.ensureAddressSelection()
            } catch (error) {
            } finally {
                this.initHomeMap()
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
            if (!current || current.quantitySplitEnabled === false) {
                this.form.quantity = this.form.quantity || '1'
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
        getPriceItemTypeLabel(value) {
            const matched = PRICE_ITEM_TYPE_OPTIONS.find((item) => item.value === value)
            return matched ? matched.label : '其他'
        },
        addPriceItem() {
            this.form.priceItems.push(createEmptyPriceItem())
            this.previewResult = {}
        },
        removePriceItem(index) {
            this.form.priceItems.splice(index, 1)
            this.previewResult = {}
        },
        handleSelectPriceItemType(index) {
            uni.showActionSheet({
                itemList: PRICE_ITEM_TYPE_OPTIONS.map((item) => item.label),
                success: ({ tapIndex }) => {
                    const target = PRICE_ITEM_TYPE_OPTIONS[tapIndex]
                    if (!target || !this.form.priceItems[index]) {
                        return
                    }
                    this.form.priceItems[index].itemType = target.value
                    this.previewResult = {}
                }
            })
        },
        handlePriceItemsChange() {
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
            this.updateMapCenter(address.longitude, address.latitude)
            this.previewResult = {}
        },
        handleSelectAddress() {
            const itemList = []
            const actions = []
            itemList.push('使用当前位置')
            actions.push(() => this.handleUseCurrentLocation())
            itemList.push('地址管理')
            actions.push(() => {
                this.openAddressManagement()
            })
            uni.showActionSheet({
                itemList,
                success: ({ tapIndex }) => {
                    const action = actions[tapIndex]
                    if (action) {
                        action()
                    }
                }
            })
        },
        openAddressManagement() {
            return new Promise((resolve, reject) => {
                uni.navigateTo({
                    url: '/pages/address_management/address_management?selectMode=1',
                    success: (res) => {
                        const eventChannel = res.eventChannel
                        eventChannel.on('picked', (address) => {
                            if (address) {
                                this.applyAddress(address)
                            }
                            resolve(address)
                        })
                        eventChannel.on('cancel', () => reject(new Error('cancel')))
                    },
                    fail: reject
                })
            })
        },
        async handleUseCurrentLocation() {
            if (this.locating) {
                return
            }
            try {
                this.locating = true
                uni.showLoading({
                    title: '定位中',
                    mask: true
                })
                const location = await this.requestCurrentLocation()
                await this.resolvePickedLocation(location)
            } catch (error) {
                this.handleLocationError(error, '定位失败，请确认定位权限已开启')
            } finally {
                uni.hideLoading()
                this.locating = false
            }
        },
        requestCurrentLocation() {
            // #ifdef H5
            if (typeof navigator !== 'undefined' && navigator.geolocation) {
                return this.requestBrowserLocation()
            }
            // #endif
            return new Promise((resolve, reject) => {
                uni.getLocation({
                    type: 'gcj02',
                    geocode: true,
                    isHighAccuracy: true,
                    success: resolve,
                    fail: reject
                })
            })
        },
        requestBrowserLocation() {
            return new Promise((resolve, reject) => {
                navigator.geolocation.getCurrentPosition(
                    (position) => {
                        const coords = position && position.coords
                        resolve({
                            ...this.toGcj02Coordinate(coords && coords.longitude, coords && coords.latitude),
                            accuracy: coords && coords.accuracy,
                            source: 'browser'
                        })
                    },
                    (error) => {
                        reject(new Error(this.getBrowserLocationErrorMessage(error)))
                    },
                    {
                        enableHighAccuracy: true,
                        timeout: 10000,
                        maximumAge: 30000
                    }
                )
            })
        },
        toGcj02Coordinate(longitude, latitude) {
            const lng = Number(longitude)
            const lat = Number(latitude)
            if (Number.isNaN(lng) || Number.isNaN(lat)) {
                return {
                    longitude,
                    latitude
                }
            }
            if (lng < 72.004 || lng > 137.8347 || lat < 0.8293 || lat > 55.8271) {
                return {
                    longitude: lng,
                    latitude: lat
                }
            }
            const pi = 3.1415926535897932384626
            const a = 6378245.0
            const ee = 0.00669342162296594323
            const transformLat = (x, y) => {
                let ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x))
                ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0
                ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0
                ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0
                return ret
            }
            const transformLng = (x, y) => {
                let ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x))
                ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0
                ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0
                ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0
                return ret
            }
            let dLat = transformLat(lng - 105.0, lat - 35.0)
            let dLng = transformLng(lng - 105.0, lat - 35.0)
            const radLat = lat / 180.0 * pi
            let magic = Math.sin(radLat)
            magic = 1 - ee * magic * magic
            const sqrtMagic = Math.sqrt(magic)
            dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi)
            dLng = (dLng * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi)
            return {
                longitude: lng + dLng,
                latitude: lat + dLat
            }
        },
        async resolvePickedLocation(location) {
            const payload = {
                longitude: this.normalizeCoordinate(location.longitude),
                latitude: this.normalizeCoordinate(location.latitude),
                detailAddress: this.extractLocationDetail(location)
            }
            if (!payload.longitude || !payload.latitude) {
                throw new Error('未获取到有效坐标')
            }
            const resolved = await resolveAddressLocation(payload)
            this.applyAddress({
                ...resolved,
                detailAddress: resolved.detailAddress || payload.detailAddress
            })
        },
        extractLocationDetail(location) {
            if (!location) {
                return ''
            }
            const candidates = []
            const poiName = `${location.poiName || ''}`.trim()
            const name = `${location.name || ''}`.trim()
            const address = `${location.address || ''}`.trim()
            const streetLine = `${location.street || ''}${location.streetNum || ''}`.trim()
            if (poiName) {
                candidates.push(poiName)
            }
            if (name && !['当前位置', '我的位置'].includes(name)) {
                candidates.push(name)
            }
            if (streetLine) {
                candidates.push(streetLine)
            }
            if (address) {
                candidates.push(address)
            }
            return candidates.find(Boolean) || ''
        },
        normalizeCoordinate(value) {
            if (value === '' || value === undefined || value === null) {
                return ''
            }
            const numberValue = Number(value)
            if (Number.isNaN(numberValue)) {
                return ''
            }
            return numberValue
        },
        getBrowserLocationErrorMessage(error) {
            const code = error && error.code
            if (code === 1) {
                return '浏览器定位权限被拒绝，请在地址栏重新允许定位'
            }
            if (code === 2) {
                return '浏览器暂时无法获取当前位置，请检查系统定位服务'
            }
            if (code === 3) {
                return '浏览器定位超时，请稍后重试'
            }
            return '浏览器不支持定位或当前环境不可用'
        },
        handleLocationError(error, defaultMessage) {
            const message = `${(error && (error.errMsg || error.message)) || ''}`
            if (/cancel/i.test(message)) {
                return
            }
            uni.showToast({
                title: message || defaultMessage,
                icon: 'none'
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
            if (!this.showAgreementOption) {
                this.form.agreementConfirmed = true
                return
            }
            this.form.agreementConfirmed = !this.form.agreementConfirmed
        },
        handleAgreement() {
            uni.showModal({
                title: this.currentAgreementTitle,
                content: this.currentAgreementContent,
                showCancel: false
            })
        },
        resolveAgreementVersion() {
            const baseVersion = this.guaranteeConfig.agreementVersion || 'v2026.06'
            const requiresProjectAgreement = this.previewResult.agreementType === 'PROJECT_ESCROW'
                || !!(this.currentCategory && this.currentCategory.laborCategoryFlag)
            if (!requiresProjectAgreement) {
                return baseVersion
            }
            return /project/i.test(baseVersion) ? baseVersion : `${baseVersion}-project`
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
            const customPriceItems = this.normalizedPriceItems
            for (let i = 0; i < customPriceItems.length; i++) {
                const item = customPriceItems[i]
                const itemAmount = Number(item.itemAmount)
                if (!item.itemName) {
                    uni.showToast({
                        title: `请填写第${i + 1}条价格明细名称`,
                        icon: 'none'
                    })
                    return null
                }
                if (Number.isNaN(itemAmount) || itemAmount <= 0) {
                    uni.showToast({
                        title: `请填写第${i + 1}条价格明细金额`,
                        icon: 'none'
                    })
                    return null
                }
            }
            const budgetAmount = customPriceItems.length ? this.priceItemsTotal : Number(this.form.budgetAmount)
            const quantity = Number(this.form.quantity)
            const workerCount = Number(this.form.workerCount || 1)
            if (Number.isNaN(budgetAmount) || budgetAmount <= 0) {
                uni.showToast({
                    title: customPriceItems.length ? '请填写正确的价格明细金额' : '请输入正确的预算金额',
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
            if (customPriceItems.length) {
                payload.priceItems = customPriceItems.map((item, index) => ({
                    itemType: item.itemType || 'CUSTOM',
                    itemName: item.itemName,
                    itemAmount: Number(item.itemAmount),
                    sortNo: index + 1
                }))
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
            if (preview && preview.sensitiveHit && preview.riskStrategy === 'BLOCK') {
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
                uni.setStorageSync(ORDER_PREVIEW_STORAGE_KEY, {
                    payload,
                    previewResult: this.previewResult,
                    guaranteeConfig: this.guaranteeConfig,
                    currentCategoryName: this.currentCategoryName,
                    formSnapshot: {
                        ...this.form
                    },
                    uploadedFiles: this.uploadedFiles
                })
                uni.navigateTo({
                    url: '/pages/order_preview/order_preview'
                })
            } catch (error) {
            }
        },
        async handlePublish() {
            if (this.submitting) {
                return
            }
            if (this.showAgreementOption && !this.form.agreementConfirmed) {
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
                    agreementConfirmed: this.showAgreementOption ? !!this.form.agreementConfirmed : true,
                    agreementVersion: this.resolveAgreementVersion(),
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
        handlePricingTip() {
            const durationExample = this.durationExample || '按实际上门时间或工期填写'
            let quantityExample = ''
            if (!this.hasQuantitySplitConfig) {
                quantityExample = `当前类目的数量口径和拆单配置还未同步到页面，先按业务理解填写，例如：${this.quantityExample.replace(/^例：/, '')}。是否参与拆单以后端类目配置为准。`
            } else if (this.quantitySplitEnabled === true) {
                quantityExample = `数量按“${this.quantityUnitLabel}”填写，例如：${this.quantityExample.replace(/^例：/, '')}。填写后会参与拆单判断。`
            } else {
                quantityExample = `数量按“${this.quantityUnitLabel}”填写，例如：${this.quantityExample.replace(/^例：/, '')}。填写后只用于报价和需求说明，不会单独触发拆单。`
            }
            const budgetExample = this.budgetExample || '填写愿意支付的总金额'
            uni.showModal({
                title: '计价说明',
                content: `服务时段示例：${durationExample.replace(/^例：/, '')}\n${quantityExample}\n预算示例：${budgetExample.replace(/^例：/, '')}`,
                showCancel: false
            })
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
    background: linear-gradient(180deg, #eff3f8 0%, #f7f8fb 26%, #f3f5f9 100%);
    padding-bottom: 120rpx;

    .header {
        padding: 56rpx 26rpx 0;
        display: flex;
        justify-content: space-between;
        align-items: center;
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        z-index: 100;

        .location-btn {
            gap: 10rpx;
            background: rgba(255, 255, 255, 0.94);
            border-radius: 32rpx;
            padding: 14rpx 22rpx;
            display: flex;
            align-items: center;
            justify-content: center;
            max-width: 520rpx;
            box-shadow: 0 12rpx 26rpx rgba(34, 53, 86, 0.1);

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
                color: #2b3b51;
            }
        }

        .role-switch {
            width: 68rpx;
            height: 68rpx;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 34rpx;
            background: rgba(255, 255, 255, 0.9);
            box-shadow: 0 12rpx 26rpx rgba(34, 53, 86, 0.1);

            .switch-icon {
                width: 48rpx;
                height: 48rpx;
            }
        }
    }

    .map-area {
        height: 500rpx;
        width: 100%;
        position: relative;
        overflow: hidden;
        background: #e7f4ff;

        .amap-container,
        .native-map {
            position: absolute;
            inset: 0;
            width: 100%;
            height: 100%;
        }

        .amap-container :deep(.amap-logo) {
            bottom: 116rpx !important;
        }

        .amap-container :deep(.amap-copyright) {
            bottom: 112rpx !important;
        }

        .map-center-pin {
            position: absolute;
            left: 50%;
            top: 50%;
            z-index: 2;
            transform: translate(-50%, -78%);
            display: flex;
            flex-direction: column;
            align-items: center;
            pointer-events: none;
        }

        .map-center-pin-head {
            width: 30rpx;
            height: 30rpx;
            border: 10rpx solid #4a90f0;
            border-radius: 50% 50% 50% 0;
            background: #4a90f0;
            transform: rotate(-45deg);
            box-shadow: 0 10rpx 22rpx rgba(74, 144, 240, 0.22);
        }

        .map-center-pin-tail {
            width: 6rpx;
            height: 38rpx;
            margin-top: -4rpx;
            border-radius: 999rpx;
            background: #4a90f0;
        }

        &::after {
            content: '';
            position: absolute;
            left: 0;
            right: 0;
            bottom: 0;
            height: 170rpx;
            background: linear-gradient(180deg, rgba(231, 244, 255, 0) 0%, #f7f8fb 100%);
        }

    }

    .main-content {
        margin-top: -86rpx;
        position: relative;
        z-index: 10;
        min-height: calc(100vh - 414rpx);
        padding: 0 22rpx;

        .search-box {
            display: flex;
            align-items: center;
            gap: 18rpx;
            padding: 18rpx 20rpx;
            background: rgba(255, 255, 255, 0.96);
            border-radius: 22rpx;
            margin-bottom: 18rpx;
            box-shadow: 0 18rpx 36rpx rgba(64, 88, 122, 0.12);

            .search-input-wrap {
                flex: 1;
                height: 74rpx;
                display: flex;
                align-items: center;
                background: #eef4fd;
                border-radius: 16rpx;
                padding: 0 20rpx;

                .search-icon {
                    width: 32rpx;
                    height: 32rpx;
                    margin-right: 14rpx;
                }

                .search-input {
                    flex: 1;
                    height: 100%;
                    font-size: 27rpx;
                    color: #304258;
                }
            }

            .search-btn {
                width: 128rpx;
                height: 74rpx;
                background: linear-gradient(180deg, #ffbf72 0%, #fa9d3b 100%);
                border-radius: 16rpx;
                display: flex;
                align-items: center;
                justify-content: center;

                .search-text {
                    font-size: 28rpx;
                    color: #fff;
                    font-weight: 600;
                }
            }
        }

        .content-scroll {
            flex: 1;
            box-sizing: border-box;

            .surface-card {
                background: #fff;
                border-radius: 24rpx;
                padding: 22rpx 24rpx;
                margin-bottom: 18rpx;
                box-shadow: 0 12rpx 30rpx rgba(54, 74, 106, 0.08);
            }

            .category-section {
                padding-top: 18rpx;
                padding-bottom: 12rpx;
            }

            .category-empty {
                min-height: 112rpx;
                display: flex;
                align-items: center;

                .category-empty-text {
                    font-size: 24rpx;
                    color: #8d99aa;
                }
            }

            .category-scroll {
                white-space: nowrap;
                margin-bottom: 14rpx;

                &:last-child {
                    margin-bottom: 0;
                }

                .category-scroll-content {
                    display: inline-flex;
                    gap: 18rpx;
                    padding-right: 16rpx;
                }

                .category-item {
                    padding: 8rpx 18rpx;
                    white-space: nowrap;
                    flex-shrink: 0;
                    border-radius: 999rpx;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    background: transparent;

                    &.active {
                        background: #b9d9ff;

                        .category-text {
                            color: #2f7de8;
                            font-weight: 600;
                        }
                    }

                    .category-text {
                        font-size: 24rpx;
                        color: #2d3a4f;
                    }
                }
            }

            .section-title-row {
                display: flex;
                align-items: center;
                gap: 12rpx;
                margin-bottom: 18rpx;
            }

            .section-title {
                font-size: 30rpx;
                font-weight: 600;
                color: #253248;
                display: block;
                margin-bottom: 18rpx;

                &.compact {
                    margin-bottom: 0;
                }
            }

            .pricing-tip-trigger {
                width: 32rpx;
                height: 32rpx;
                border-radius: 50%;
                background: #ffd978;
                display: flex;
                align-items: center;
                justify-content: center;
                flex-shrink: 0;
                box-shadow: 0 6rpx 14rpx rgba(248, 177, 93, 0.18);

                .pricing-tip-trigger-text {
                    font-size: 22rpx;
                    line-height: 1;
                    color: #8e5600;
                    font-weight: 700;
                }
            }

            .pricing-options {
                display: flex;
                gap: 14rpx;
                flex-wrap: wrap;

                .pricing-item {
                    min-width: 92rpx;
                    padding: 8rpx 16rpx;
                    border-radius: 12rpx;
                    background: #ffe7c8;
                    display: flex;
                    align-items: center;
                    justify-content: center;

                    &.active {
                        background: #f8b15d;
                    }

                    .pricing-text {
                        font-size: 21rpx;
                        color: #FFFFFF;
                        line-height: 24rpx;
                    }
                }
            }

            .order-section {
                position: relative;
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
                    min-height: 74rpx;
                    padding: 0 22rpx;
                    border: 2rpx solid #e9edf3;
                    border-radius: 16rpx;
                    background: #fff;

                    .select-text {
                        flex: 1;
                        font-size: 26rpx;
                        color: #607087;
                        overflow: hidden;
                        text-overflow: ellipsis;
                        white-space: nowrap;
                    }

                    .select-arrow {
                        margin-left: 20rpx;
                        font-size: 18rpx;
                        color: #8f9caf;
                    }
                }

                .input-box {
                    width: 100%;
                    height: 74rpx;
                    padding: 0 22rpx;
                    border: 2rpx solid #e9edf3;
                    border-radius: 16rpx;
                    font-size: 26rpx;
                    color: #2f3b4f;
                    background: #fff;
                    box-sizing: border-box;
                }

                .compact-input {
                    height: 64rpx;
                    border-radius: 14rpx;
                    text-align: center;
                    padding: 0 12rpx;
                    font-size: 24rpx;
                }

                .textarea-box {
                    width: 100%;
                    height: 168rpx;
                    padding: 20rpx 22rpx;
                    border: 2rpx solid #e9edf3;
                    border-radius: 16rpx;
                    font-size: 26rpx;
                    color: #2f3b4f;
                    background: #fff;
                    box-sizing: border-box;
                }
            }

            .form-row {
                display: flex;
                gap: 14rpx;
                margin-bottom: 14rpx;
            }

            .compact-row {
                margin-bottom: 18rpx;
            }

            .upload-list {
                display: flex;
                flex-wrap: wrap;
                gap: 14rpx;
                margin-bottom: 18rpx;

                .upload-preview,
                .upload-box {
                    width: calc((100% - 28rpx) / 3);
                    aspect-ratio: 1 / 1;
                    min-height: 132rpx;
                    border-radius: 18rpx;
                    box-sizing: border-box;
                }

                .upload-preview {
                    position: relative;
                    overflow: hidden;
                    background: #f3f6fb;

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
                    border: 2rpx solid #dde5f1;
                    background: #fbfcfe;

                    .upload-icon {
                        width: 32rpx;
                        height: 32rpx;
                        border-radius: 50%;
                        line-height: 30rpx;
                        text-align: center;
                        border: 2rpx solid #6c8fbe;
                        color: #4f75aa;
                        font-size: 30rpx;
                        font-weight: 300;
                    }

                    .upload-text {
                        font-size: 20rpx;
                        color: #5d79a7;
                    }
                }
            }

            .safety-tip {
                min-height: 68rpx;
                border-radius: 16rpx;
                background: #f7fafc;
                display: flex;
                align-items: center;
                padding: 0 20rpx;
                margin-bottom: 18rpx;

                .safety-tip-label {
                    font-size: 24rpx;
                    color: #7b8b9d;
                    margin-right: 16rpx;
                }

                .safety-tip-text {
                    flex: 1;
                    font-size: 24rpx;
                    color: #2f3b4f;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                }

                .safety-tip-arrow {
                    margin-left: 16rpx;
                    font-size: 24rpx;
                    color: #8f9caf;
                }
            }

            .checkbox-list {
                margin-bottom: 20rpx;

                .checkbox-item {
                    display: flex;
                    align-items: flex-start;
                    gap: 10rpx;
                    margin-bottom: 16rpx;
                    flex-wrap: wrap;

                    &:last-child {
                        margin-bottom: 0;
                    }

                    .checkbox {
                        width: 30rpx;
                        height: 30rpx;
                        border: 2rpx solid #f4aa4a;
                        border-radius: 6rpx;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        margin-top: 4rpx;

                        &.checked {
                            background: #f4aa4a;

                            .check-icon {
                                color: #fff;
                                font-size: 18rpx;
                            }
                        }
                    }

                    .checkbox-text {
                        font-size: 24rpx;
                        color: #4b586c;
                        line-height: 34rpx;
                    }

                    .hint {
                        font-size: 22rpx;
                        color: #f0a44f;
                        line-height: 34rpx;
                    }
                }
            }

            .agreement-item {
                display: flex;
                align-items: center;
                gap: 10rpx;
                margin-bottom: 18rpx;

                .checkbox {
                    width: 28rpx;
                    height: 28rpx;
                    border: 2rpx solid #4385f4;
                    border-radius: 6rpx;
                    display: flex;
                    align-items: center;
                    justify-content: center;

                    &.checked {
                        background: #4385f4;

                        .check-icon {
                            color: #fff;
                            font-size: 18rpx;
                        }
                    }
                }

                .agreement-text,
                .agreement-link {
                    font-size: 24rpx;
                }

                .agreement-text {
                    color: #91a0b2;
                }

                .agreement-link {
                    color: #4385f4;
                }
            }

            .extra-fields {
                margin-top: -4rpx;
            }

            .price-total-box {
                flex: 1;
                min-height: 74rpx;
                padding: 0 22rpx;
                border: 2rpx solid #e9edf3;
                border-radius: 16rpx;
                background: #f8fbff;
                display: flex;
                align-items: center;
                justify-content: space-between;
                gap: 12rpx;

                .price-total-label {
                    font-size: 24rpx;
                    color: #7d8da3;
                }

                .price-total-value {
                    font-size: 26rpx;
                    font-weight: 600;
                    color: #2f3b4f;
                }
            }

            .price-detail-card {
                margin-top: 10rpx;
                padding: 20rpx;
                border: 2rpx solid #edf2f8;
                border-radius: 18rpx;
                background: #fbfcfe;

                .price-detail-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    gap: 16rpx;
                    margin-bottom: 16rpx;
                }

                .price-detail-title {
                    font-size: 28rpx;
                    font-weight: 600;
                    color: #2e3e55;
                }

                .price-detail-summary,
                .price-detail-empty-text,
                .price-item-remove-text,
                .price-item-add-text,
                .price-type-text,
                .price-type-arrow {
                    font-size: 24rpx;
                    color: #7d8da3;
                }

                .price-detail-empty {
                    padding: 10rpx 0 18rpx;
                }

                .price-item-row {
                    padding: 18rpx;
                    margin-bottom: 14rpx;
                    border-radius: 16rpx;
                    background: #fff;
                    border: 2rpx solid #edf2f8;
                }

                .price-item-top {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    gap: 16rpx;
                    margin-bottom: 14rpx;
                }

                .price-type-picker {
                    min-width: 170rpx;
                    height: 58rpx;
                    padding: 0 18rpx;
                    border-radius: 14rpx;
                    background: #f3f7fd;
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                    gap: 12rpx;
                }

                .price-item-remove {
                    padding: 10rpx 0 10rpx 20rpx;
                }

                .price-item-add {
                    height: 66rpx;
                    border: 2rpx dashed #d7e1ef;
                    border-radius: 16rpx;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    background: rgba(255, 255, 255, 0.8);
                }

                .price-item-add-text {
                    color: #4f7ee8;
                    font-weight: 600;
                }
            }

            .bottom-btns {
                display: flex;
                gap: 28rpx;
                margin: 8rpx 4rpx 20rpx;

                .bottom-btn {
                    flex: 1;
                    height: 72rpx;
                    border-radius: 12rpx;
                    text-align: center;
                    display: flex;
                    align-items: center;
                    justify-content: center;

                    &.preview-btn {
                        background: #fff;
                        border: 2rpx solid #75a9f9;

                        .btn-text {
                            color: #3e86f2;
                        }
                    }

                    &.publish-btn {
                        background: linear-gradient(180deg, #4f96f9 0%, #327dea 100%);

                        .btn-text {
                            color: #fff;
                        }
                    }

                    .btn-text {
                        font-size: 30rpx;
                        font-weight: 600;
                    }
                }
            }

            .bottom-space {
                height: 80rpx;
            }
        }
    }
}
</style>
