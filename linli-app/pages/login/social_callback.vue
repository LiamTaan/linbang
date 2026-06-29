<template>
  <view class="callback-page">
    <view class="card">
      <text class="title">{{ title }}</text>
      <text class="desc">{{ desc }}</text>
    </view>
  </view>
</template>

<script>
import { finishSocialLogin } from '@/utils/social'

export default {
  data() {
    return {
      title: '正在处理授权结果',
      desc: '请稍候，正在登录中...'
    }
  },
  async onLoad(options) {
    const { type, code, state, redirect, error } = options || {}
    if (error) {
      this.title = '授权失败'
      this.desc = error
      return
    }
    if (!type || !code || !state) {
      this.title = '授权信息缺失'
      this.desc = '请返回登录页重新发起授权'
      return
    }
    try {
      const result = await finishSocialLogin({
        type: Number(type),
        code,
        state
      }, redirect)
      if (result.bindRequired) {
        this.title = '请绑定手机号'
        this.desc = '授权成功，但还需要完成手机号绑定'
        uni.redirectTo({
          url: `/pages/login/login?mode=social-bind&redirect=${encodeURIComponent(redirect || '')}`
        })
      }
    } catch (error) {
      this.title = '登录失败'
      this.desc = error.message || '请返回登录页重试'
    }
  }
}
</script>

<style scoped>
.callback-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #2e83f0, #eef5ff);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40rpx;
}

.card {
  width: 100%;
  background: #fff;
  border-radius: 24rpx;
  padding: 48rpx 36rpx;
  text-align: center;
  box-shadow: 0 16rpx 48rpx rgba(46, 131, 240, 0.15);
}

.title {
  display: block;
  font-size: 36rpx;
  color: #1f2d3d;
  font-weight: bold;
  margin-bottom: 20rpx;
}

.desc {
  display: block;
  font-size: 28rpx;
  color: #5c6b77;
  line-height: 1.6;
}
</style>
