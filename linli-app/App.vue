<script>
import { APP_CONFIG } from '@/config/app'
import { bootstrapSession, loadPlatformSettings } from '@/services/app-bootstrap'

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
	onLaunch: function () {
		loadPlatformSettings().catch(() => null)
		bootstrapSession().catch(() => null)
	},
	onShow: function () {
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
view {
	box-sizing: border-box;
}
</style>
