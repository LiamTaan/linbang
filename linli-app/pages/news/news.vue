<template>
    <view class="page-container">
        <view class="header">
            <text class="title">消息</text>
            <view class="clear-btn" @click="clearUnread">
                <text class="btn-text">全部已读</text>
            </view>
        </view>

        <scroll-view class="tab-scroll" scroll-x>
            <view class="tab-list">
                <view
                    v-for="(item, index) in tabs"
                    :key="item.code || 'all'"
                    class="tab-item"
                    :class="{ active: currentTab === index }"
                    @click="changeTab(index)">
                    <text class="tab-text">{{ item.label }}</text>
                    <view v-if="currentTab === index" class="tab-line"></view>
                </view>
            </view>
        </scroll-view>

        <scroll-view class="news-list" scroll-y @scrolltolower="loadMore">
            <view
                v-for="(news, index) in newsList"
                :key="news.id || index"
                class="news-card"
                @click="openMessage(news)">
                <view class="news-left">
                    <view class="news-icon" :class="news.iconClass">
                        <image class="icon-img" :src="news.icon" />
                    </view>
                </view>
                <view class="news-content">
                    <view class="news-header">
                        <text class="news-title">{{ news.title }}</text>
                        <text class="news-time">{{ news.time }}</text>
                    </view>
                    <text class="news-desc">{{ news.desc }}</text>
                    <text class="news-detail">{{ news.detail }}</text>
                </view>
                <view v-if="news.unread" class="unread-dot"></view>
            </view>
            <view v-if="!newsList.length && !loading" class="empty-state">
                <text class="empty-text">暂无消息</text>
            </view>
            <view v-if="loading" class="loading-state">
                <text class="loading-text">加载中...</text>
            </view>
        </scroll-view>

        <tab-bar :current-index="2"></tab-bar>
    </view>
</template>

<script>
import tabBar from '@/components/tabBar/tabBar.vue'
import { getMessageRecordPage, markAllMessageRead, markMessageRead } from '@/api/message'

export default {
    components: {
        tabBar
    },
    data() {
        return {
            currentTab: 0,
            tabs: [
                { label: '全部', code: '' },
                { label: '订单消息', code: 'ORDER' },
                { label: '资金变动', code: 'FINANCE' },
                { label: '争议处理', code: 'DISPUTE' },
                { label: '系统通知', code: 'SYSTEM' }
            ],
            newsList: [],
            pageNo: 1,
            pageSize: 20,
            finished: false,
            loading: false
        }
    },
    onLoad(options) {
        const category = options && options.category ? options.category : ''
        const targetIndex = this.tabs.findIndex((item) => item.code === category)
        if (targetIndex >= 0) {
            this.currentTab = targetIndex
        }
    },
    onShow() {
        uni.hideTabBar()
        this.reloadList()
    },
    methods: {
        async reloadList() {
            this.pageNo = 1
            this.finished = false
            this.newsList = []
            await this.loadMessages()
        },
        async loadMessages() {
            if (this.loading || this.finished) {
                return
            }
            this.loading = true
            try {
                const current = this.tabs[this.currentTab] || this.tabs[0]
                const page = await getMessageRecordPage({
                    pageNo: this.pageNo,
                    pageSize: this.pageSize,
                    messageCategory: current.code || undefined
                })
                const list = (page.list || []).map((item) => this.mapMessageItem(item))
                this.newsList = this.pageNo === 1 ? list : this.newsList.concat(list)
                this.finished = list.length < this.pageSize
                this.pageNo += 1
            } catch (error) {
            } finally {
                this.loading = false
            }
        },
        mapMessageItem(item) {
            const config = this.getMessageConfig(item)
            return {
                ...item,
                icon: config.icon,
                iconClass: config.iconClass,
                title: item.title || '平台消息',
                time: this.$fmt.formatDateTime(item.createTime || item.sendTime),
                desc: item.contentSnapshot || '暂无内容',
                detail: `${item.bizType || item.messageCategory || 'SYSTEM'} · ${item.sendStatus || 'SUCCESS'}`,
                unread: item.readStatus !== 'READ'
            }
        },
        getMessageConfig(item) {
            if (item.messageCategory === 'FINANCE') {
                return {
                    icon: '/static/img/news/inform.png',
                    iconClass: 'icon-red'
                }
            }
            if (item.messageCategory === 'ORDER') {
                return {
                    icon: '/static/img/news/logistics.png',
                    iconClass: 'icon-blue'
                }
            }
            if (item.messageCategory === 'DISPUTE') {
                return {
                    icon: '/static/img/news/safety.png',
                    iconClass: 'icon-orange'
                }
            }
            return {
                icon: '/static/img/news/audit.png',
                iconClass: 'icon-green'
            }
        },
        async changeTab(index) {
            this.currentTab = index
            await this.reloadList()
        },
        async clearUnread() {
            const current = this.tabs[this.currentTab] || this.tabs[0]
            try {
                await markAllMessageRead(current.code || undefined)
                uni.showToast({
                    title: '未读已清空',
                    icon: 'success'
                })
                this.reloadList()
            } catch (error) {
            }
        },
        async openMessage(news) {
            try {
                if (news.unread && news.id) {
                    await markMessageRead(news.id)
                    news.unread = false
                }
            } catch (error) {
            }
            if (news.messageCategory === 'ORDER' && news.bizId) {
                uni.navigateTo({
                    url: `/pages/split_order_details/split_order_details?orderId=${news.bizId}`
                })
                return
            }
            if (news.messageCategory === 'DISPUTE' && news.bizId) {
                uni.navigateTo({
                    url: `/pages/complaint/complaint?id=${news.bizId}`
                })
                return
            }
            if (news.messageCategory === 'FINANCE') {
                uni.navigateTo({
                    url: '/pages/detail_of_earnings/detail_of_earnings'
                })
            }
        },
        loadMore() {
            this.loadMessages()
        }
    }
}
</script>

<style lang="scss" scoped>
.page-container {
    min-height: 100vh;
    padding-bottom: 120rpx;
    display: flex;
    flex-direction: column;

    .header {
        background: #fff;
        padding: 30rpx;
        display: flex;
        justify-content: space-between;
        align-items: center;

        .title {
            font-size: 36rpx;
            font-weight: bold;
            color: #333;
        }

        .clear-btn {
            .btn-text {
                font-size: 26rpx;
                color: #999;
            }
        }
    }

    .tab-scroll {
        white-space: nowrap;
        background: #EDF5FF;

        .tab-list {
            display: inline-flex;
            padding: 24rpx 30rpx 0;
            gap: 40rpx;

            .tab-item {
                display: flex;
                flex-direction: column;
                align-items: center;

                &.active {
                    .tab-text {
                        color: #2E83F0;
                        font-weight: bold;
                    }
                }

                .tab-text {
                    font-size: 28rpx;
                    color: #666;
                }

                .tab-line {
                    width: 40rpx;
                    height: 6rpx;
                    background: #2E83F0;
                    border-radius: 3rpx;
                    margin-top: 8rpx;
                }
            }
        }
    }

    .news-list {
        flex: 1;
        background-color: #fff;
        box-sizing: border-box;

        .news-card {
            display: flex;
            align-items: center;
            background: #fff;
            padding: 24rpx;
            position: relative;
            border-bottom: 1rpx solid #EDF5FF;

            .news-left {
                margin-right: 20rpx;

                .news-icon {
                    width: 80rpx;
                    height: 80rpx;
                    border-radius: 50%;
                    display: flex;
                    align-items: center;
                    justify-content: center;

                    &.icon-red {
                        background: #FFF0F0;
                    }

                    &.icon-blue {
                        background: #E3F2FD;
                    }

                    &.icon-green {
                        background: #E8F5E9;
                    }

                    &.icon-orange {
                        background: #FFF7E6;
                    }

                    .icon-img {
                        width: 44rpx;
                        height: 44rpx;
                    }
                }
            }

            .news-content {
                flex: 1;

                .news-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 8rpx;

                    .news-title {
                        font-size: 28rpx;
                        font-weight: bold;
                        color: #333;
                    }

                    .news-time {
                        font-size: 22rpx;
                        color: #999;
                    }
                }

                .news-desc {
                    display: block;
                    font-size: 26rpx;
                    color: #666;
                    margin-bottom: 6rpx;
                }

                .news-detail {
                    display: block;
                    font-size: 24rpx;
                    color: #999;
                }
            }

            .unread-dot {
                width: 16rpx;
                height: 16rpx;
                background: #2E83F0;
                border-radius: 50%;
                margin-left: 16rpx;
            }
        }

        .empty-state,
        .loading-state {
            padding: 48rpx 0;
            text-align: center;
        }

        .empty-text,
        .loading-text {
            font-size: 24rpx;
            color: #999;
        }
    }
}
</style>
