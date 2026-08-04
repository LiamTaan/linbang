<template>
	<view class="login-container">
		<register
			v-if="mode === 'register'"
			:redirect="redirect"
			@switch-to-login="switchMode('login')"></register>
		<social-bind
			v-else-if="mode === 'social-bind'"
			:redirect="redirect"
			@switch-to-login="switchMode('login')"></social-bind>
		<login
			v-else
			:redirect="redirect"
			@switch-to-register="switchMode('register')"
			@switch-to-social-bind="switchMode('social-bind')"></login>
	</view>
</template>
<script>
import register from '@/components/login/register.vue'
import login from '@/components/login/login.vue'
import socialBind from '@/components/login/socialBind.vue'
import { normalizeAppRoute } from '@/utils/navigation'
export default {
  components: {
    register,
    login,
    socialBind
  },
  data() {
    return {
      mode: 'login',
      redirect: ''
    }
  },
  onLoad(options) {
    this.redirect = normalizeAppRoute(options && options.redirect ? options.redirect : '')
    // #ifdef MP-WEIXIN
    this.mode = 'login'
    return
    // #endif
    if (options && options.mode) {
      this.mode = options.mode
      return
    }
    this.mode = 'login'
  },
  methods: {
    switchMode(mode) {
      this.mode = mode
    }
  }
}
</script>
<style scoped>
</style>
