<template>
    <view class="page-container">
        <view class="header">
            <view class="search-bar">
                <image class="search-icon" src="/static/img/order/search.png" />
                <input class="search-input" :placeholder="mode === 'accept' ? '搜索待接单需求' : '本页内筛选订单'"
                    v-model="searchText" confirm-type="search" @confirm="search" />
                <view class="search-btn" @click="search">
                    <text class="btn-text">搜索</text>
                </view>
            </view>
        </view>

        <scroll-view class="category-scroll" scroll-x :show-scrollbar="false">
            <view class="category-list">
                <view v-for="item in tabs" :key="item.value || item.id || item.label" class="category-item"
                    :class="{ active: isTabActive(item) }" @click="handleTabChange(item)">
                    <text class="category-text">{{ item.label || item.categoryName }}</text>
                    <view v-if="isTabActive(item)" class="category-line"></view>
                </view>
            </view>
        </scroll-view>

        <view class="filter-bar" v-if="mode === 'accept'">
            <view class="filter-item" @click="toggleFilter('distance')">
                <text class="filter-text">{{ filterLabels.distance }}</text>
                <image class="filter-icon" src="/static/img/order/arrow_down.png" />
            </view>
            <view class="filter-item" @click="toggleFilter('price')">
                <text class="filter-text">{{ filterLabels.price }}</text>
                <image class="filter-icon" src="/static/img/order/arrow_down.png" />
            </view>
            <view class="filter-item" @click="toggleFilter('time')">
                <text class="filter-text">{{ filterLabels.time }}</text>
                <image class="filter-icon" src="/static/img/order/arrow_down.png" />
            </view>
            <view class="filter-item" @click="toggleFilter('screen')">
                <text class="filter-text">筛选</text>
                <image class="filter-icon" src="/static/img/order/arrow_down.png" />
            </view>
        </view>

        <scroll-view class="order-list" scroll-y refresher-enabled :refresher-triggered="refreshing"
            @refresherrefresh="handleRefresh" @scrolltolower="loadMore" lower-threshold="80">
            <view v-if="!displayList.length && !loading" class="empty-state">
                <text class="empty-title">暂无数据</text>
                <text class="empty-desc">{{ mode === 'accept' ? '当前没有匹配的待接单需求' : '当前筛选条件下暂无订单' }}</text>
            </view>

            <view v-for="order in displayList" :key="order.orderId || order.id" class="order-card" @click="openDetail(order)">
                <view class="order-header">
                    <view class="tag-group">
                        <view class="order-tag" :class="resolveTagClass(order)">
                            <text class="tag-text">{{ resolveCategoryText(order) }}</text>
                        </view>
                        <text class="tag-divider">|</text>
                        <view class="price-type">
                            <text class="type-text">{{ resolvePricingText(order) }}</text>
                        </view>
                    </view>
                    <text class="order-price">¥{{ $fmt.formatMoney(resolveAmount(order)) }}</text>
                </view>

                <view class="order-title">{{ resolveTitle(order) }}</view>

                <view class="order-info-row">
                    <view class="order-info">
                        <view class="info-item">
                            <image class="info-icon" src="/static/img/order/distance.png" />
                            <text class="info-text">{{ resolveDistance(order) }}</text>
                        </view>
                        <view class="info-item">
                            <image class="info-icon" src="/static/img/order/time.png" />
                            <text class="info-text">{{ resolveTimeText(order) }}</text>
                        </view>
                        <view class="info-item" v-if="mode !== 'accept'">
                            <text class="status-chip">{{ resolveStatusText(order) }}</text>
                            <text class="business-chip" v-if="order.businessCategory">
                                {{ getBusinessCategoryLabel(order.businessCategory) }}
                            </text>
                        </view>
                    </view>
                    <view class="order-btn" :class="{ secondary: mode !== 'accept' }" @click.stop="handleOrder(order)">
                        <text class="btn-text">{{ mode === 'accept' ? '抢单' : '查看详情' }}</text>
                    </view>
                </view>

                <view class="order-meta" v-if="mode === 'accept'">
                    <text class="meta-text">{{ getDispatchStatusLabel(order.dispatchStatus) }}</text>
                    <text class="meta-text" v-if="order.countdownSeconds">剩余 {{ order.countdownSeconds }} 秒</text>
                    <text class="meta-text" v-if="order.priorityLayer">{{ order.priorityLayer }}</text>
                </view>
            </view>

            <view class="loading-tip" v-if="loading">
                <text class="loading-text">加载中...</text>
            </view>
            <view class="loading-tip" v-else-if="finished && displayList.length">
                <text class="loading-text">已经到底了</text>
            </view>
        </scroll-view>

        <tab-bar :current-index="1"></tab-bar>
    </view>
</template>

<script>
import tabBar from '@/components/tabBar/tabBar.vue'
import { acceptOrder, getAcceptOrderPage, getGuaranteeConfig, getOrderPage } from '@/api/order'
import { getServiceCategoryList } from '@/api/merchant'
import {
    getBusinessCategoryLabel,
    getDispatchStatusLabel,
    getOrderStatusLabel,
    getPricingModeLabel,
    ORDER_BUSINESS_TABS
} from '@/utils/linbang'

const FILTER_SCHEMAS = {
    distance: {
        labels: ['距离最近', '距离最远'],
        values: ['NEAREST', 'FARTHEST']
    },
    price: {
        labels: ['价格升序', '价格降序'],
        values: ['PRICE_ASC', 'PRICE_DESC']
    },
    time: {
        labels: ['最新发布', '最早发布'],
        values: ['NEWEST', 'OLDEST']
    },
    screen: {
        labels: ['全部价格', '100 元以上', '300 元以上'],
        values: ['', '100', '300']
    }
}

export default {
    components: {
        tabBar
    },
    data() {
        return {
            mode: 'accept',
            searchText: '',
            refreshing: false,
            loading: false,
            finished: false,
            pageNo: 1,
            pageSize: 10,
            total: 0,
            rawList: [],
            acceptTabs: [{ label: '全部', value: '' }],
            currentCategoryId: '',
            currentBusinessCategory: '',
            guaranteeConfig: {},
            filterState: {
                distanceSort: 'NEAREST',
                priceSort: '',
                publishTimeSort: 'NEWEST',
                minOrderAmount: ''
            }
        }
    },
    computed: {
        tabs() {
            return this.mode === 'accept' ? this.acceptTabs : ORDER_BUSINESS_TABS
        },
        displayList() {
            if (!this.searchText.trim() || this.mode === 'accept') {
                return this.rawList
            }
            const keyword = this.searchText.trim()
            return this.rawList.filter((item) => {
                const title = this.resolveTitle(item)
                return (title && title.indexOf(keyword) > -1)
                    || ((item.orderNo || '').indexOf(keyword) > -1)
                    || ((item.categoryName || '').indexOf(keyword) > -1)
            })
        },
        filterLabels() {
            return {
                distance: this.filterState.distanceSort === 'FARTHEST' ? '距离最远' : '距离最近',
                price: this.filterState.priceSort === 'PRICE_DESC' ? '价格降序' : (this.filterState.priceSort === 'PRICE_ASC' ? '价格升序' : '价格'),
                time: this.filterState.publishTimeSort === 'OLDEST' ? '最早发布' : '最新发布'
            }
        }
    },
    onLoad(options) {
        const mode = options && options.mode ? options.mode : 'accept'
        if (mode === 'serving') {
            this.mode = 'my'
            this.currentBusinessCategory = 'IN_SERVICE'
            return
        }
        this.mode = mode === 'my' ? 'my' : 'accept'
    },
    onShow() {
        uni.hideTabBar()
        this.loadMeta()
        this.reload()
    },
    methods: {
        getBusinessCategoryLabel,
        getDispatchStatusLabel,
        async loadMeta() {
            try {
                const [categories, guaranteeConfig] = await Promise.all([
                    getServiceCategoryList().catch(() => []),
                    getGuaranteeConfig().catch(() => ({}))
                ])
                this.acceptTabs = [{ label: '全部', value: '' }].concat((categories || []).map((item) => ({
                    label: item.categoryName,
                    value: item.id
                })))
                this.guaranteeConfig = guaranteeConfig || {}
            } catch (error) {
            }
        },
        reload() {
            this.pageNo = 1
            this.finished = false
            this.rawList = []
            this.loadOrders()
        },
        handleRefresh() {
            this.refreshing = true
            this.reload()
        },
        async loadOrders() {
            if (this.loading || this.finished) {
                this.refreshing = false
                return
            }
            try {
                this.loading = true
                const params = this.buildParams()
                const pageResp = this.mode === 'accept'
                    ? await getAcceptOrderPage(params)
                    : await getOrderPage(params)
                const list = (pageResp && pageResp.list) || []
                this.total = Number((pageResp && pageResp.total) || 0)
                this.rawList = this.pageNo === 1 ? list : this.rawList.concat(list)
                this.finished = this.rawList.length >= this.total || list.length < this.pageSize
                this.pageNo += 1
            } catch (error) {
            } finally {
                this.loading = false
                this.refreshing = false
            }
        },
        loadMore() {
            if (!this.finished) {
                this.loadOrders()
            }
        },
        buildParams() {
            if (this.mode === 'accept') {
                return {
                    pageNo: this.pageNo,
                    pageSize: this.pageSize,
                    keyword: this.searchText.trim(),
                    categoryId: this.currentCategoryId || undefined,
                    distanceSort: this.filterState.distanceSort || undefined,
                    priceSort: this.filterState.priceSort || undefined,
                    publishTimeSort: this.filterState.publishTimeSort || undefined,
                    minOrderAmount: this.filterState.minOrderAmount || undefined
                }
            }
            return {
                pageNo: this.pageNo,
                pageSize: this.pageSize,
                businessCategory: this.currentBusinessCategory || undefined
            }
        },
        isTabActive(item) {
            if (this.mode === 'accept') {
                return `${this.currentCategoryId}` === `${item.value || ''}`
            }
            return `${this.currentBusinessCategory}` === `${item.value || ''}`
        },
        handleTabChange(item) {
            if (this.mode === 'accept') {
                this.currentCategoryId = item.value || ''
            } else {
                this.currentBusinessCategory = item.value || ''
            }
            this.reload()
        },
        search() {
            if (this.mode !== 'accept' && this.rawList.length) {
                return
            }
            this.reload()
        },
        toggleFilter(type) {
            const schema = FILTER_SCHEMAS[type]
            if (!schema) {
                return
            }
            uni.showActionSheet({
                itemList: schema.labels,
                success: ({ tapIndex }) => {
                    const value = schema.values[tapIndex]
                    if (type === 'distance') {
                        this.filterState.distanceSort = value
                    } else if (type === 'price') {
                        this.filterState.priceSort = value
                    } else if (type === 'time') {
                        this.filterState.publishTimeSort = value
                    } else if (type === 'screen') {
                        this.filterState.minOrderAmount = value
                    }
                    this.reload()
                }
            })
        },
        resolveCategoryText(order) {
            return order.categoryName || '邻里需求'
        },
        resolvePricingText(order) {
            return getPricingModeLabel(order.pricingMode)
        },
        resolveAmount(order) {
            return order.orderAmount || 0
        },
        resolveTitle(order) {
            return order.requireDesc || order.orderNo || '邻里订单'
        },
        resolveDistance(order) {
            if (order.distanceKm === undefined || order.distanceKm === null || order.distanceKm === '') {
                return '距离待更新'
            }
            return `距您 ${this.$fmt.formatMoney(order.distanceKm)} km`
        },
        resolveTimeText(order) {
            if (this.mode === 'accept') {
                return order.serviceDurationDesc ? `工期：${order.serviceDurationDesc}` : '工期待确认'
            }
            const dateText = this.$fmt.formatShortDateTime(order.createTime)
            return `${getOrderStatusLabel(order.status)} · ${dateText}`
        },
        resolveStatusText(order) {
            return getOrderStatusLabel(order.status)
        },
        resolveTagClass(order) {
            const categoryName = `${order.categoryName || ''}`
            if (categoryName.indexOf('水') > -1 || categoryName.indexOf('电') > -1) {
                return 'tag-blue'
            }
            if (categoryName.indexOf('搬') > -1) {
                return 'tag-orange'
            }
            if (categoryName.indexOf('保洁') > -1 || categoryName.indexOf('家政') > -1) {
                return 'tag-green'
            }
            return 'tag-purple'
        },
        openDetail(order) {
            const orderId = order.orderId || order.id
            if (!orderId) {
                return
            }
            uni.navigateTo({
                url: `/pages/split_order_details/split_order_details?orderId=${orderId}`
            })
        },
        async handleOrder(order) {
            if (this.mode !== 'accept') {
                this.openDetail(order)
                return
            }
            const notice = this.guaranteeConfig.antiEscapeNotice || '确认接单后，请按约提供服务。'
            uni.showModal({
                title: '确认抢单',
                content: notice,
                success: async ({ confirm }) => {
                    if (!confirm) {
                        return
                    }
                    try {
                        await acceptOrder({
                            orderId: order.orderId,
                            unitId: order.unitId,
                            antiEscapeConfirmed: true
                        })
                        uni.showToast({
                            title: '抢单成功',
                            icon: 'success'
                        })
                        this.reload()
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
    display: flex;
    flex-direction: column;

    .header {
        background: #fff;
        padding: 20rpx 30rpx;

        .search-bar {
            display: flex;
            align-items: center;
            height: 60rpx;
            background: #EDF5FF;
            border-radius: 12rpx;
            padding: 0 30rpx;

            .search-icon {
                width: 32rpx;
                height: 32rpx;
                margin-right: 16rpx;
            }

            .search-input {
                flex: 1;
                height: 100%;
            }

            .search-btn {
                padding: 0 30rpx;
                height: 80%;
                background: #F9A23F;
                border-radius: 6rpx;
                display: flex;
                align-items: center;
                justify-content: center;

                .btn-text {
                    font-size: 28rpx;
                    color: #fff;
                    font-weight: bold;
                }
            }
        }
    }

    .category-scroll {
        background: #fff;
        white-space: nowrap;

        .category-list {
            display: inline-flex;
            padding: 24rpx 30rpx;
            gap: 40rpx;

            .category-item {
                display: flex;
                flex-direction: column;
                align-items: center;

                &.active {
                    .category-text {
                        color: #2E83F0;
                        font-weight: bold;
                    }
                }

                .category-text {
                    font-size: 28rpx;
                    color: #666;
                }

                .category-line {
                    width: 40rpx;
                    height: 6rpx;
                    background: #2E83F0;
                    border-radius: 3rpx;
                    margin-top: 8rpx;
                }
            }
        }
    }

    .filter-bar {
        display: flex;
        justify-content: space-between;
        background: #fff;
        padding: 20rpx 30rpx;
        border-bottom: 1rpx solid #F0F0F0;

        .filter-item {
            display: flex;
            align-items: center;
            gap: 6rpx;

            .filter-text {
                font-size: 26rpx;
                color: #666;
            }

            .filter-icon {
                width: 20rpx;
                height: 20rpx;
            }
        }
    }

    .order-list {
        flex: 1;
        padding: 20rpx 30rpx;
        box-sizing: border-box;

        .empty-state {
            background: #fff;
            border-radius: 16rpx;
            padding: 60rpx 40rpx;
            text-align: center;

            .empty-title {
                display: block;
                font-size: 30rpx;
                font-weight: bold;
                color: #333;
                margin-bottom: 12rpx;
            }

            .empty-desc {
                font-size: 24rpx;
                color: #999;
            }
        }

        .order-card {
            background: #fff;
            border-radius: 16rpx;
            padding: 24rpx;
            margin-bottom: 20rpx;
            border: 1rpx solid #E8E8E8;

            .order-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 16rpx;

                .tag-group {
                    display: flex;
                    gap: 12rpx;
                    align-items: center;

                    .order-tag {
                        padding: 8rpx 10rpx;
                        border-radius: 8rpx;

                        &.tag-blue {
                            background: #C2CBFF;

                            .tag-text {
                                color: #2E49F0;
                            }
                        }

                        &.tag-orange {
                            background: #FFDBC0;

                            .tag-text {
                                color: #ED6400;
                            }
                        }

                        &.tag-green {
                            background: #CAFBCB;

                            .tag-text {
                                color: #029504;
                            }
                        }

                        &.tag-purple {
                            background: #F2C0FF;

                            .tag-text {
                                color: #AF00DC;
                            }
                        }

                        .tag-text {
                            font-size: 20rpx;
                            line-height: 20rpx;
                        }
                    }

                    .price-type .type-text,
                    .tag-divider {
                        font-size: 22rpx;
                        color: #999;
                    }
                }

                .order-price {
                    font-size: 34rpx;
                    color: #E53935;
                    font-weight: bold;
                }
            }

            .order-title {
                font-size: 30rpx;
                color: #333;
                font-weight: bold;
                margin-bottom: 16rpx;
            }

            .order-info-row {
                display: flex;
                justify-content: space-between;
                align-items: center;
            }

            .order-info {
                display: flex;
                flex-direction: column;
                gap: 8rpx;

                .info-item {
                    display: flex;
                    align-items: center;
                    gap: 8rpx;
                    flex-wrap: wrap;

                    .info-icon {
                        width: 24rpx;
                        height: 24rpx;
                    }

                    .info-text,
                    .status-chip,
                    .business-chip {
                        font-size: 24rpx;
                    }

                    .info-text {
                        color: #999;
                    }

                    .status-chip {
                        color: #2E83F0;
                    }

                    .business-chip {
                        color: #FA9D3B;
                    }
                }
            }

            .order-btn {
                background: #2E83F0;
                border-radius: 32rpx;
                padding: 14rpx 40rpx;

                &.secondary {
                    background: #EDF5FF;

                    .btn-text {
                        color: #2E83F0;
                    }
                }

                .btn-text {
                    font-size: 26rpx;
                    color: #fff;
                    font-weight: bold;
                }
            }

            .order-meta {
                display: flex;
                gap: 16rpx;
                flex-wrap: wrap;
                margin-top: 16rpx;

                .meta-text {
                    font-size: 22rpx;
                    color: #999;
                }
            }
        }

        .loading-tip {
            text-align: center;
            padding: 20rpx 0 40rpx;

            .loading-text {
                font-size: 24rpx;
                color: #999;
            }
        }
    }
}
</style>
