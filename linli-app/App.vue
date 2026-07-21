<script>
import { APP_CONFIG } from '@/config/app'
import { bootstrapSession, loadPlatformSettings } from '@/services/app-bootstrap'
import { refreshAppOrderReminder, startAppOrderReminder } from '@/services/app-order-reminder'
import { captureInviteContext, consumePendingInviteContext } from '@/services/invite-context'

let lastRuntimeArgs = ''

function parseQueryString(queryString) {
	const result = {}
	queryString.split('&').forEach((item) => {
		if (!item) {
			return
		}
		const [key, value] = item.split('=')
		result[decodeURIComponent(key)] = decodeURIComponent(value || '')
	})
	return result
}

function parseRuntimeCallback(args) {
	if (!args || args === lastRuntimeArgs || args.indexOf('code=') === -1) {
		return null
	}
	const query = args.includes('?') ? args.split('?')[1] : args
	const options = parseQueryString(query)
	if (!options.code || !options.state) {
		return null
	}
	lastRuntimeArgs = args
	return options
}

export default {
	onLaunch: function (options) {
		captureInviteContext(options)
		loadPlatformSettings().catch(() => null)
		bootstrapSession().then(() => consumePendingInviteContext()).catch(() => null)
		startAppOrderReminder()
	},
	onShow: function (options) {
		captureInviteContext(options)
		consumePendingInviteContext().catch(() => null)
		refreshAppOrderReminder().catch(() => null)
		// #ifdef APP-PLUS
		const options = parseRuntimeCallback(plus.runtime.arguments)
		if (options) {
			const query = Object.keys(options)
				.map((key) => `${encodeURIComponent(key)}=${encodeURIComponent(options[key])}`)
				.join('&')
			uni.navigateTo({
				url: `${APP_CONFIG.socialCallbackPage}?${query}`
			})
		}
		// #endif
	},
	onHide: function () {
	}
}
</script>

<style>
@import './static/iconfont/iconfont.css';
/*每个页面公共css */
:root {
	--app-bg-top: #eef6ff;
	--app-bg-mid: #f7fbff;
	--app-bg-bottom: #f2f7fd;
	--app-surface: rgba(255, 255, 255, 0.96);
	--app-surface-soft: #edf5ff;
	--app-primary: #4a90f0;
}

page {
	background: linear-gradient(180deg, var(--app-bg-top) 0%, var(--app-bg-mid) 180rpx, var(--app-bg-bottom) 100%);
}

html,
body {
	background: linear-gradient(180deg, var(--app-bg-top) 0%, var(--app-bg-mid) 180rpx, var(--app-bg-bottom) 100%);
}

view {
	box-sizing: border-box;
}

/* Custom-navigation business pages start below the native status bar. */
page .page-container:not(.home-page):not(.immersive-page) {
	/* All custom-navigation pages reserve the status/menu-button area. */
	padding-top: 84px !important;
}

page .page-container.home-page,
page .page-container.immersive-page {
	padding-top: 0 !important;
}

.back-icon {
	display: inline-block;
}
</style>
