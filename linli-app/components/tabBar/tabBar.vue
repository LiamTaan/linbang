<template>
    <view class="tab-bar">
        <view 
            v-for="(item, index) in tabList" 
            :key="index"
            class="tab-item"
            :class="{ active: currentIndex === index }"
            @click="switchTab(index)"
        >
            <image class="tab-icon" :src="currentIndex === index ? item.selectedIcon : item.icon" />
            <text class="tab-text">{{ item.text }}</text>
        </view>
    </view>
</template>

<script>
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
            ]
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
    background: #fff;
    display: flex;
    align-items: center;
    justify-content: space-around;
    box-shadow: 0rpx -2rpx 10rpx rgba(0, 0, 0, 0.05);
    // padding-bottom: env(safe-area-inset-bottom);
    padding: 20rpx 0;
    z-index: 999;
    .tab-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        flex: 1;
        height: 100%;
        transition: all 0.3s;

        &.active {
            .tab-text {
                color: #4A90D9;
            }
        }

        .tab-icon {
            width: 48rpx;
            height: 48rpx;
            margin-bottom: 6rpx;
        }

        .tab-text {
            font-size: 22rpx;
            color: #999;
        }
    }
}
</style>
