<template>
    <view class="tab-bar">
        <view 
            v-for="(item, index) in tabList" 
            :key="index"
            class="tab-item"
            :class="{ active: currentIndex === index }"
            @click="switchTab(index)"
        >
            <view class="tab-icon-wrap">
                <image class="tab-icon" :src="currentIndex === index ? item.selectedIcon : item.icon" />
                <view v-if="item.key === 'message' && unreadCount > 0" class="tab-badge">
                    <text v-if="unreadCount > 0" class="tab-badge-text">{{ unreadBadgeText }}</text>
                </view>
            </view>
            <text class="tab-text">{{ item.text }}</text>
        </view>
    </view>
</template>

<script>
import {
    MESSAGE_UNREAD_CHANGED_EVENT,
    getCachedMessageUnreadCount,
    syncMessageUnreadCount
} from '@/services/message-unread'

export default {
    props: {
        currentIndex: {
            type: Number,
            default: 0
        }
    },
    data() {
        return {
            tabList: [
                {
                    text: '首页',
                    icon: '/static/img/tabBar/home.png',
                    selectedIcon: '/static/img/tabBar/home_pitch_on.png',
                    path: '/pages/index/index'
                },
                {
                    text: '需求/接单',
                    icon: '/static/img/tabBar/order.png',
                    selectedIcon: '/static/img/tabBar/order_pitch_on.png',
                    path: '/pages/order/order'
                },
                {
                    text: '消息',
                    key: 'message',
                    icon: '/static/img/tabBar/news.png',
                    selectedIcon: '/static/img/tabBar/news_pitch_on.png',
                    path: '/pages/news/news'
                },
                {
                    text: '我的',
                    icon: '/static/img/tabBar/my.png',
                    selectedIcon: '/static/img/tabBar/my_pitch_on.png',
                    path: '/pages/my/my'
                }
            ],
            unreadCount: getCachedMessageUnreadCount()
        }
    },
    computed: {
        unreadBadgeText() {
            return this.unreadCount > 99 ? '99+' : `${this.unreadCount}`
        }
    },
    created() {
        this.handleUnreadChange = (count) => {
            this.unreadCount = Number(count || 0)
        }
        uni.$on(MESSAGE_UNREAD_CHANGED_EVENT, this.handleUnreadChange)
    },
    mounted() {
        syncMessageUnreadCount({ silent: true }).then((count) => {
            this.unreadCount = Number(count || 0)
        })
    },
    beforeUnmount() {
        if (this.handleUnreadChange) {
            uni.$off(MESSAGE_UNREAD_CHANGED_EVENT, this.handleUnreadChange)
        }
    },
    methods: {
        switchTab(index) {
            const item = this.tabList[index]
            if (item.path === '/pages/order/order') {
                uni.setStorageSync('linbang_order_tab_mode', 'accept')
            }
            uni.switchTab({
                url: item.path
            })
        }
    }
}
</script>

<style lang="scss" scoped>
.tab-bar {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    // height: 100rpx;
    background: rgba(255, 255, 255, 0.94);
    backdrop-filter: blur(18rpx);
    display: flex;
    align-items: center;
    justify-content: space-around;
    box-shadow: 0rpx -8rpx 24rpx rgba(74, 144, 240, 0.08);
    border-top: 1rpx solid rgba(205, 225, 247, 0.9);
    padding: 20rpx 0 calc(20rpx + env(safe-area-inset-bottom));
    z-index: 999;
    .tab-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        flex: 1;
        height: 100%;
        transition: all 0.3s;
        .tab-icon-wrap {
            position: relative;
            margin-bottom: 6rpx;
        }

        &.active {
            .tab-text {
                color: #4A90D9;
            }
        }

        .tab-icon {
            width: 48rpx;
            height: 48rpx;
        }

        .tab-badge {
            position: absolute;
            top: -10rpx;
            right: -18rpx;
            min-width: 32rpx;
            height: 32rpx;
            padding: 0 8rpx;
            border-radius: 16rpx;
            background: #FF4D4F;
            border: 2rpx solid #fff;
            box-sizing: border-box;
            display: flex;
            align-items: center;
            justify-content: center;

        }

        .tab-badge-text {
            font-size: 18rpx;
            line-height: 1;
            color: #fff;
        }

        .tab-text {
            font-size: 22rpx;
            color: #999;
        }
    }
}
</style>
