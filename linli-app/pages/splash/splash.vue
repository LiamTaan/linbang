<template>
	<view class="splash-page">
		<view class="hero-block">
			<image class="brand-mark" src="/static/img/splash/launch-mark.png" mode="aspectFit" />
			<text class="brand-title">邻里互助</text>
			<text class="brand-subtitle">本 地 生 活 服 务 平 台</text>
		</view>
		<view class="tagline-block">
			<text class="tagline">让邻里互助，温暖每一刻</text>
		</view>
		<view class="footer-block">
			<view class="loading-dots" aria-hidden="true">
				<view
					v-for="dot in loadingDots"
					:key="dot"
					class="dot"
					:class="{ active: activeDotIndex === dot - 1 }"></view>
			</view>
			<text class="version-text">v1.0.0</text>
		</view>
	</view>
</template>

<script>
import { APP_CONFIG } from '@/config/app'
import { bootstrapSession } from '@/services/app-bootstrap'
import { hasLogin } from '@/utils/auth'

const MIN_DISPLAY_MS = 1200
const MAX_DISPLAY_MS = 2000
const DOT_INTERVAL_MS = 260

export default {
	data() {
		return {
			loadingDots: [1, 2, 3, 4],
			activeDotIndex: 0,
			dotTimer: null,
			navigationTriggered: false
		}
	},
	onLoad() {
		this.startDotAnimation()
		this.startBootstrapFlow()
	},
	onUnload() {
		this.clearDotTimer()
	},
	methods: {
		startDotAnimation() {
			this.clearDotTimer()
			this.dotTimer = setInterval(() => {
				this.activeDotIndex = (this.activeDotIndex + 1) % this.loadingDots.length
			}, DOT_INTERVAL_MS)
		},
		clearDotTimer() {
			if (this.dotTimer) {
				clearInterval(this.dotTimer)
				this.dotTimer = null
			}
		},
		async startBootstrapFlow() {
			const minDisplayTimer = new Promise((resolve) => setTimeout(resolve, MIN_DISPLAY_MS))
			const maxDisplayTimer = new Promise((resolve) => setTimeout(resolve, MAX_DISPLAY_MS))
			const bootstrapTask = bootstrapSession().catch(() => null)
			try {
				await Promise.race([
					Promise.all([bootstrapTask, minDisplayTimer]),
					maxDisplayTimer
				])
			} finally {
				this.navigateNext()
			}
		},
		navigateNext() {
			if (this.navigationTriggered) {
				return
			}
			this.navigationTriggered = true
			this.clearDotTimer()
			const url = hasLogin() ? '/pages/index/index' : APP_CONFIG.loginPage
			uni.reLaunch({ url })
		}
	}
}
</script>

<style lang="scss" scoped>
.splash-page {
	min-height: 100vh;
	padding: 0 56rpx calc(env(safe-area-inset-bottom) + 54rpx);
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	background: linear-gradient(180deg, #3c84e8 0%, #73a9f2 100%);
	position: relative;
	overflow: hidden;
}

.hero-block {
	display: flex;
	flex-direction: column;
	align-items: center;
	margin-top: -190rpx;
}

.brand-mark {
	width: 356rpx;
	height: 240rpx;
	margin-bottom: 36rpx;
}

.brand-title {
	font-size: 74rpx;
	font-weight: 700;
	line-height: 1;
	color: #ffffff;
	letter-spacing: 2rpx;
	font-family: STKaiti, KaiTi, serif;
	text-shadow: 0 8rpx 24rpx rgba(53, 111, 196, 0.24);
}

.brand-subtitle {
	margin-top: 24rpx;
	font-size: 22rpx;
	line-height: 1;
	color: rgba(255, 255, 255, 0.84);
	letter-spacing: 11rpx;
}

.tagline-block {
	margin-top: 142rpx;
}

.tagline {
	font-size: 42rpx;
	font-weight: 500;
	line-height: 1.2;
	color: rgba(255, 255, 255, 0.96);
	letter-spacing: 1rpx;
	font-family: STKaiti, KaiTi, serif;
	text-shadow: 0 6rpx 18rpx rgba(53, 111, 196, 0.2);
}

.footer-block {
	position: absolute;
	left: 0;
	right: 0;
	bottom: calc(env(safe-area-inset-bottom) + 92rpx);
	display: flex;
	flex-direction: column;
	align-items: center;
}

.loading-dots {
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 18rpx;
}

.dot {
	width: 16rpx;
	height: 16rpx;
	border-radius: 50%;
	background: rgba(255, 255, 255, 0.48);
	transform: scale(0.92);
	transition: transform 0.18s ease, opacity 0.18s ease, background-color 0.18s ease;
}

.dot.active {
	background: rgba(255, 255, 255, 0.98);
	transform: scale(1.14);
}

.version-text {
	margin-top: 24rpx;
	font-size: 18rpx;
	font-weight: 300;
	line-height: 1;
	color: rgba(255, 255, 255, 0.86);
	letter-spacing: 1rpx;
}
</style>
