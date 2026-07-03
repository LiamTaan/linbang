<template>
  <view class="redirect-page">
    <text class="redirect-text">正在跳转订单详情...</text>
  </view>
</template>

<script>
export default {
  onLoad(options) {
    const orderId = options && options.orderId ? Number(options.orderId) : null
    const unitId = options && options.unitId ? Number(options.unitId) : null
    const query = [`orderId=${orderId || ''}`]
    if (unitId) {
      query.push(`unitId=${unitId}`)
    }
    uni.redirectTo({
      url: `/pages/split_order_details/split_order_details?${query.join('&')}`
    })
  }
}
</script>

<style lang="scss" scoped>
.redirect-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fb;
}

.redirect-text {
  color: #7b8794;
  font-size: 26rpx;
}
</style>
