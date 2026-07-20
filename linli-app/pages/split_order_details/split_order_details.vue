<template>
  <view class="page-container">
    <view class="header">
      <view class="back-btn" @click="$navigateBack()">
        <text class="iconfont icon-youjiantou back-icon"></text>
      </view>
      <text class="title">订单详情</text>
      <view class="placeholder"></view>
    </view>

    <scroll-view class="content-scroll" scroll-y refresher-enabled :refresher-triggered="refreshing" @refresherrefresh="loadDetail">
      <view v-if="errorText" class="summary-card">
        <text class="card-title">当前订单暂时无法查看</text>
        <text class="summary-text">{{ errorText }}</text>
      </view>

      <block v-else-if="orderDetail.id">
        <view class="summary-card">
          <view class="summary-top">
            <view class="summary-main">
              <text class="order-no">订单编号：{{ orderDetail.orderNo || '--' }}</text>
              <text class="order-title">{{ orderDetail.requireDesc || '邻里订单' }}</text>
            </view>
            <view class="summary-status">{{ orderStatusText }}</view>
          </view>
          <view class="summary-row">
            <text class="total-amount">{{ totalAmountText }}</text>
            <text class="split-tip">{{ splitTipText }}</text>
          </view>
          <text class="summary-label">订单总额</text>
          <text class="summary-address">{{ orderAddress }}</text>
        </view>

        <view
          v-for="unit in displayUnits"
          :key="unit.id"
          class="unit-card"
          :class="resolveUnitCardClass(unit)">
          <view class="unit-head">
            <view class="unit-seq">{{ resolveUnitSeqLabel(unit) }}</view>
            <text class="unit-head-status">{{ buildUnitHeadStatus(unit) }}</text>
          </view>
          <text class="unit-title">{{ unit.unitTitle || unit.unitNo || '订单单元' }}</text>
          <view class="unit-grid">
            <view class="unit-cell">
              <text class="cell-label">单元金额</text>
              <text class="cell-value">{{ formatAmount(unit.unitAmount) }}</text>
            </view>
            <view class="unit-cell">
              <text class="cell-label">验收状态</text>
              <text class="cell-value">{{ getUnitAcceptanceText(unit) }}</text>
            </view>
            <view class="unit-cell cell-wide">
              <text class="cell-label">派单动态</text>
              <text class="cell-value">{{ buildUnitStatusDetail(unit) }}</text>
            </view>
            <view class="unit-cell cell-wide">
              <text class="cell-label">时间节点</text>
              <text class="cell-value">{{ buildUnitTimeText(unit) }}</text>
            </view>
          </view>
          <text class="unit-meta lock-text" v-if="unit.lockReason">锁定原因：{{ unit.lockReason }}</text>

          <view v-if="getUnitProofs(unit).length" class="proof-list">
            <view
              v-for="proof in getUnitProofs(unit)"
              :key="proof.id"
              class="proof-item">
              <image
                class="proof-image"
                :src="proof.fileUrl"
                mode="aspectFill"
                @click="previewProofs(getUnitProofs(unit), proof.fileUrl)" />
              <view
                v-if="canDeleteProof(unit, proof)"
                class="proof-delete"
                @click.stop="handleDeleteProof(unit, proof)">
                <text class="proof-delete-text">×</text>
              </view>
            </view>
          </view>

          <view v-if="getUnitReviews(unit).length" class="review-list">
            <view
              v-for="review in getUnitReviews(unit)"
              :key="review.id"
              class="review-card">
              <view class="review-head">
                <text class="review-title">{{ review.displayTitle || '评价记录' }}</text>
                <text class="review-meta">{{ formatReviewStars(review.starLevel) }}</text>
              </view>
              <text class="review-content">{{ review.content || '暂无评价内容' }}</text>
              <text class="review-time">{{ $fmt.formatDateTime(review.createTime) }}</text>
            </view>
          </view>

          <view class="hint-box" v-if="showUnitFlowHint(unit)">
            <text class="link-text">{{ getUnitFlowHint(unit) }}</text>
          </view>

          <view class="unit-actions" v-if="unitActionItems(unit).length">
            <view
              v-for="action in unitActionItems(unit)"
              :key="action.key"
              class="unit-btn"
              :class="action.variant"
              @click="action.onClick">
              <text>{{ action.label }}</text>
            </view>
          </view>
        </view>

        <view class="rule-card" v-if="splitExplainItems.length">
          <text class="rule-title">拆分规则说明</text>
          <text v-for="item in splitExplainItems" :key="item" class="rule-text">{{ item }}</text>
        </view>
      </block>

      <view class="bottom-space"></view>
    </scroll-view>

    <view class="footer-actions" v-if="footerActions.length">
      <view
        v-for="action in footerActions"
        :key="action.key"
        class="footer-btn"
        :class="action.variant"
        @click="action.onClick">
        <text>{{ action.label }}</text>
      </view>
    </view>
  </view>
</template>

<script>
import { uploadAppFile } from '@/api/infra'
import { getProfile } from '@/api/member'
import { getMerchantProfile } from '@/api/merchant'
import {
  getPayOrder,
  submitWechatMiniProgramPay
} from '@/api/pay'
import {
  confirmOrderUnit,
  deleteDeliveryProof,
  getOrderDetail,
  startOrderUnitService,
  uploadDeliveryProof
} from '@/api/order'
import { openPlatformContact } from '@/services/platform-contact'
import {
  buildAddressText,
  extractUploadedFile,
  formatOrderSplitModeLabel,
  getDispatchStatusLabel,
  getOrderStatusLabel,
  getOrderUnitStatusLabel
} from '@/utils/linbang'

const ORDER_REPUBLISH_STORAGE_KEY = 'linbang_order_republish_draft'

export default {
  data() {
    return {
      orderId: null,
      unitId: null,
      currentUserId: null,
      currentMerchantId: null,
      orderDetail: {},
      refreshing: false,
      errorText: ''
    }
  },
  computed: {
    isPublisherViewer() {
      return !!(this.currentUserId && this.orderDetail.userId && `${this.currentUserId}` === `${this.orderDetail.userId}`)
    },
    isAssignedMerchantViewer() {
      return !!(this.currentMerchantId && this.orderDetail.merchantId
        && `${this.currentMerchantId}` === `${this.orderDetail.merchantId}`)
    },
    isPendingPayOrder() {
      return this.orderDetail.status === 'PENDING_PAY'
    },
    displayUnits() {
      const units = Array.isArray(this.orderDetail.units) ? this.orderDetail.units.slice() : []
      return units.sort((a, b) => {
        const seqA = Number(a.unitSeq || 0)
        const seqB = Number(b.unitSeq || 0)
        if (seqA !== seqB) {
          return seqA - seqB
        }
        return Number(a.id || 0) - Number(b.id || 0)
      })
    },
    totalAmountText() {
      return `¥${this.$fmt.formatMoney(this.orderDetail.orderAmount)}`
    },
    splitTipText() {
      const unitCount = this.displayUnits.length
      if (unitCount <= 1) {
        return '未拆分'
      }
      return `已拆分为${unitCount}个单元`
    },
    hasRefundRecord() {
      return Array.isArray(this.orderDetail.refunds) && this.orderDetail.refunds.length > 0
    },
    latestRefundRecord() {
      const list = Array.isArray(this.orderDetail.refunds) ? this.orderDetail.refunds.slice() : []
      if (!list.length) {
        return null
      }
      return list.sort((a, b) => new Date(b.createTime || 0).getTime() - new Date(a.createTime || 0).getTime())[0]
    },
    isFullyFlowedOrder() {
      return this.orderDetail.status === 'AFTER_SALE' && !this.isRefundedOrder
    },
    isRefundProcessingOrder() {
      return this.orderDetail.status === 'AFTER_SALE' && this.orderDetail.autoRefundStatus === 'PROCESSING'
    },
    isRefundedOrder() {
      return this.orderDetail.status === 'REFUNDED' || this.orderDetail.autoRefundStatus === 'SUCCESS'
    },
    effectiveOrderStatusText() {
      if (this.isRefundedOrder) {
        return '已退款'
      }
      if (this.isRefundProcessingOrder) {
        return '退款处理中'
      }
      if (this.isFullyFlowedOrder) {
        return '流单中'
      }
      return getOrderStatusLabel(this.orderDetail.status)
    },
    orderStatusText() {
      return this.effectiveOrderStatusText
    },
    orderAddress() {
      return buildAddressText(this.orderDetail) || '服务地址待补充'
    },
    splitExplainItems() {
      const items = []
      const triggerReasons = Array.isArray(this.orderDetail.splitTriggerReasons) ? this.orderDetail.splitTriggerReasons : []
      triggerReasons.forEach((item) => {
        if (item) {
          items.push(item)
        }
      })
      if (this.orderDetail.splitMode) {
        items.push(`拆分方式：${formatOrderSplitModeLabel(this.orderDetail.splitMode)}`)
      }
      const unitLimit = this.resolveUnitAmountLimit()
      if (unitLimit) {
        items.push(`单元限额：每个单元≤ ${unitLimit}`)
      }
      if (this.orderDetail.splitRuleSummary) {
        items.unshift(this.orderDetail.splitRuleSummary)
      }
      return items
    },
    footerActions() {
      const actions = [
        {
          key: 'contact',
          label: '联系平台',
          variant: 'secondary',
          onClick: this.openPlatformService
        }
      ]
      if (this.isPublisherViewer && this.isPendingPayOrder) {
        actions.push({
          key: 'pay',
          label: '立即支付',
          variant: 'primary',
          onClick: this.handlePayOrder
        })
      } else if (this.isPublisherViewer && this.isFullyFlowedOrder) {
        actions.push({
          key: 'refund',
          label: this.hasRefundRecord ? '查看退款' : '查看退款进度',
          variant: 'secondary',
          onClick: this.openRefund
        })
        if (this.orderDetail.republishAllowed) {
          actions.push({
            key: 'republish',
            label: '调整需求重新发布',
            variant: 'primary',
            onClick: this.handleRepublish
          })
        }
      } else if (this.isPublisherViewer && ['PENDING_ACCEPT', 'ACCEPTED'].includes(this.orderDetail.status)) {
        actions.push({
          key: 'refund',
          label: this.hasRefundRecord ? '查看退款' : '申请退款',
          variant: 'secondary',
          onClick: this.openRefund
        })
      } else if (this.isPublisherViewer && ['SERVING', 'PENDING_CONFIRM', 'FINISHED', 'AFTER_SALE'].includes(this.orderDetail.status)) {
        actions.push({
          key: 'complaint',
          label: '投诉反馈',
          variant: 'secondary',
          onClick: this.openComplaint
        })
      }
      return actions
    }
  },
  onLoad(options) {
    this.orderId = options && options.orderId ? Number(options.orderId) : null
    this.unitId = options && options.unitId ? Number(options.unitId) : null
  },
  onShow() {
    this.loadDetail()
  },
  methods: {
    getOrderUnitStatusLabel,
    async loadDetail() {
      if (!this.orderId) {
        return
      }
      try {
        this.refreshing = true
        const [detail, profile, merchantProfile] = await Promise.all([
          getOrderDetail(this.orderId, { silent: true }),
          getProfile({ silent: true }).catch(() => ({})),
          getMerchantProfile({ silent: true }).catch(() => ({}))
        ])
        this.orderDetail = detail || {}
        this.currentUserId = (profile && profile.id) || null
        this.currentMerchantId = (merchantProfile && merchantProfile.merchantId) || null
        if (this.orderDetail.status === 'PENDING_PAY') {
          await this.loadPayStatus(true)
        }
        this.errorText = ''
      } catch (error) {
        this.errorText = (error && error.message) || '当前订单已无法访问'
      } finally {
        this.refreshing = false
      }
    },
    resolveUnitSeqLabel(unit) {
      const seq = unit && unit.unitSeq ? unit.unitSeq : ''
      return seq ? `单元${this.toChineseSeq(seq)}` : '单元'
    },
    toChineseSeq(value) {
      const map = ['零', '一', '二', '三', '四', '五', '六', '七', '八', '九']
      const number = Number(value || 0)
      if (number > 0 && number < 10) {
        return map[number]
      }
      return String(value || '')
    },
    resolveUnitCardClass(unit) {
      if (!unit) {
        return 'card-default'
      }
      if (this.isFullyFlowedOrder) {
        return ['FLOWED', 'EXPIRED'].includes(unit.dispatchStatus) || unit.flowTime ? 'card-flowed' : 'card-muted'
      }
      if (unit.status === 'FINISHED') {
        return 'card-finished'
      }
      if (['SERVING', 'PENDING_CONFIRM'].includes(unit.status)) {
        return 'card-serving'
      }
      return 'card-default'
    },
    buildUnitStatusDetail(unit) {
      if (!unit) {
        return '--'
      }
      if (this.isPendingPayOrder) {
        if (unit.isLocked || unit.status === 'PENDING_CREATE') {
          return '待支付完成后生成并解锁'
        }
        return '待支付完成后派单'
      }
      if (this.isFullyFlowedOrder) {
        if (['FLOWED', 'EXPIRED'].includes(unit.dispatchStatus) || unit.flowTime) {
          if (this.isRefundedOrder) {
            return '该单元已流单并完成退款'
          }
          if (this.isRefundProcessingOrder) {
            return '该单元已流单，系统正在自动退款'
          }
          return '该单元已流单，等待退款处理'
        }
        if (unit.status === 'PENDING_CREATE' || unit.isLocked) {
          return '整单已流单，后续单元不再继续派单'
        }
      }
      const statusTexts = []
      if (unit.dispatchStatus && unit.status === 'PENDING_ACCEPT') {
        statusTexts.push(getDispatchStatusLabel(unit.dispatchStatus))
      }
      if (unit.status) {
        statusTexts.push(getOrderUnitStatusLabel(unit.status))
      }
      return statusTexts.join(' · ')
    },
    buildUnitTimeText(unit) {
      if (!unit) {
        return '--'
      }
      if (this.isPendingPayOrder) {
        const expireTime = this.orderDetail.payRecord && this.orderDetail.payRecord.expireTime
        if (expireTime) {
          return `支付截止：${this.$fmt.formatDateTime(expireTime)}`
        }
      }
      if (this.isFullyFlowedOrder && unit.flowTime) {
        return `流单时间：${this.$fmt.formatDateTime(unit.flowTime)}`
      }
      if (unit.finishTime) {
        return `完成时间：${this.$fmt.formatDateTime(unit.finishTime)}`
      }
      if (unit.acceptDeadlineTime && unit.status === 'PENDING_ACCEPT') {
        return `接单截止：${this.$fmt.formatDateTime(unit.acceptDeadlineTime)}`
      }
      return `创建时间：${this.$fmt.formatDateTime(unit.createTime)}`
    },
    showUnitFlowHint(unit) {
      return !!this.getUnitFlowHint(unit)
    },
    getUnitFlowHint(unit) {
      if (!unit) {
        return ''
      }
      if (this.isPendingPayOrder) {
        return unit.isLocked || unit.status === 'PENDING_CREATE'
          ? '订单支付成功后，系统会按拆单顺序自动解锁后续单元'
          : '当前订单待支付，支付成功后系统才会开始派单'
      }
      if (this.isFullyFlowedOrder) {
        if (['FLOWED', 'EXPIRED'].includes(unit.dispatchStatus) || unit.flowTime) {
          if (this.isRefundedOrder) {
            return '当前订单已流单并完成自动退款'
          }
          if (this.isRefundProcessingOrder) {
            return unit.flowReason || this.orderDetail.flowReason || '当前订单无人接单，系统正在自动退款'
          }
          return unit.flowReason || this.orderDetail.flowReason || this.orderDetail.flowAdvice || '当前订单无人接单，已进入流单处理'
        }
        if (unit.status === 'PENDING_CREATE' || unit.isLocked) {
          return '前置单元已流单，后续单元自动终止'
        }
      }
      if (['FLOWED', 'EXPIRED'].includes(unit.dispatchStatus) && unit.flowReason) {
        return unit.flowReason
      }
      if (unit.dispatchStatus && unit.status === 'PENDING_ACCEPT') {
        return `派单动态：${getDispatchStatusLabel(unit.dispatchStatus)}`
      }
      if (unit.lockReason) {
        return `上一单元完成前暂不可继续`
      }
      return ''
    },
    getUnitProofs(unit) {
      const list = Array.isArray(this.orderDetail.proofs) ? this.orderDetail.proofs : []
      return list.filter((item) => `${item.unitId}` === `${unit && unit.id}`)
    },
    getUnitReviews(unit) {
      const list = Array.isArray(this.orderDetail.reviews) ? this.orderDetail.reviews : []
      return list.filter((item) => `${item.unitId}` === `${unit && unit.id}`)
    },
    formatReviewStars(starLevel) {
      const score = Number(starLevel || 0)
      if (!score) {
        return '未评分'
      }
      return `${'★'.repeat(score)}${'☆'.repeat(Math.max(0, 5 - score))}`
    },
    previewProofs(proofs, currentUrl) {
      const urls = (proofs || []).map((item) => item.fileUrl).filter(Boolean)
      if (!urls.length) {
        return
      }
      uni.previewImage({
        urls,
        current: currentUrl || urls[0]
      })
    },
    unitActionItems(unit) {
      if (!unit) {
        return []
      }
      if (this.isAssignedMerchantViewer) {
        return this.buildMerchantUnitActions(unit)
      }
      if (this.isPublisherViewer) {
        return this.buildPublisherUnitActions(unit)
      }
      return []
    },
    buildMerchantUnitActions(unit) {
      const actions = []
      if (unit.status === 'ACCEPTED') {
        actions.push(this.createAction('start-service', '开始服务', 'primary', () => this.handleStartService(unit)))
      }
      if (['ACCEPTED', 'SERVING', 'PENDING_CONFIRM'].includes(unit.status)) {
        actions.push(this.createAction('upload-proof', '上传凭证', 'secondary', () => this.handleUploadProof(unit)))
      }
      if (unit.status === 'PENDING_CONFIRM') {
        actions.push(this.createAction('wait-confirm', '待用户验收', 'primary-light'))
      } else if (unit.status === 'FINISHED') {
        actions.push(this.createAction('done', '已完成', 'disabled'))
      }
      return actions
    },
    buildPublisherUnitActions(unit) {
      const actions = []
      if (this.isPendingPayOrder) {
        return actions
      }
      const hasProof = this.getUnitProofs(unit).length > 0
      if (hasProof) {
        actions.push(this.createAction('view-proof', '查看凭证', 'secondary', () => this.previewFirstProof(unit)))
      }
      if (unit.status === 'PENDING_CONFIRM') {
        actions.push(this.createAction('confirm-complete', '确认完工', 'primary', () => this.handleConfirmComplete(unit)))
      } else if (unit.status === 'FINISHED') {
        actions.push(this.createAction('done', '已完成', 'disabled'))
      }
      return actions
    },
    createAction(key, label, variant, onClick) {
      return {
        key,
        label,
        variant,
        onClick: typeof onClick === 'function' ? onClick : () => {}
      }
    },
    previewFirstProof(unit) {
      const proofs = this.getUnitProofs(unit)
      if (!proofs.length) {
        uni.showToast({
          title: '当前还没有凭证',
          icon: 'none'
        })
        return
      }
      this.previewProofs(proofs, proofs[0].fileUrl)
    },
    async handleStartService(unit) {
      try {
        await startOrderUnitService({
          unitId: unit.id,
          startRemark: 'App 发起开始服务'
        })
        uni.showToast({ title: '已开始服务', icon: 'success' })
        this.loadDetail()
      } catch (error) {
      }
    },
    async handleUploadProof(unit) {
      try {
        const chooseResp = await new Promise((resolve, reject) => {
          uni.chooseImage({
            count: 3,
            success: resolve,
            fail: reject
          })
        })
        const tempFiles = chooseResp && chooseResp.tempFiles ? chooseResp.tempFiles : []
        if (!tempFiles.length) {
          return
        }
        const uploadedIds = []
        for (const file of tempFiles) {
          const uploadResp = await uploadAppFile(file.path)
          const uploadedFile = extractUploadedFile(uploadResp)
          const fileId = uploadedFile && (uploadedFile.id || uploadedFile.fileId)
          if (fileId) {
            uploadedIds.push(fileId)
          }
        }
        if (!uploadedIds.length) {
          return
        }
        await uploadDeliveryProof({
          unitId: unit.id,
          proofType: 'DELIVERY_IMAGE',
          proofDesc: 'App 上传交付凭证',
          fileIds: uploadedIds
        })
        uni.showToast({ title: '凭证已上传', icon: 'success' })
        this.loadDetail()
      } catch (error) {
      }
    },
    async handleConfirmComplete(unit) {
      uni.showModal({
        title: '确认完工',
        content: '确认后该单元将进入已完成，并触发对应单元结算，是否继续？',
        success: async ({ confirm }) => {
          if (!confirm) {
            return
          }
          try {
            await confirmOrderUnit({
              unitId: unit.id,
              confirmRemark: '用户在拆分单详情确认完工'
            })
            uni.showToast({ title: '已确认完工', icon: 'success' })
            this.loadDetail()
          } catch (error) {
          }
        }
      })
    },
    resolveUnitAmountLimit() {
      const unit = this.displayUnits.find((item) => item.maxAmountLimit !== undefined && item.maxAmountLimit !== null)
      if (!unit || unit.maxAmountLimit === undefined || unit.maxAmountLimit === null) {
        return ''
      }
      return this.formatAmount(unit.maxAmountLimit)
    },
    formatAmount(value) {
      return `¥ ${this.$fmt.formatMoney(value)}`
    },
    getUnitAcceptanceText(unit) {
      if (!unit) {
        return '--'
      }
      if (this.isPendingPayOrder) {
        return '待支付'
      }
      if (unit.verifyStatus === 'VERIFIED') {
        return '已验收'
      }
      if (this.isFullyFlowedOrder) {
        return this.isRefundedOrder ? '已退款' : '退款处理中'
      }
      if (unit.status === 'PENDING_CONFIRM') {
        return '待用户验收'
      }
      if (unit.status === 'FINISHED') {
        return '已验收'
      }
      if (unit.status === 'SERVING') {
        return '服务进行中'
      }
      if (unit.status === 'ACCEPTED') {
        return '待开始服务'
      }
      return getOrderUnitStatusLabel(unit.status)
    },
    buildUnitHeadStatus(unit) {
      if (!unit) {
        return '--'
      }
      if (this.isPendingPayOrder) {
        if (unit.isLocked || unit.status === 'PENDING_CREATE') {
          return '待生成'
        }
        return '待支付'
      }
      if (this.isFullyFlowedOrder) {
        if (this.isRefundedOrder) {
          return '已退款'
        }
        if (['FLOWED', 'EXPIRED'].includes(unit.dispatchStatus) || unit.flowTime) {
          return '流单中'
        }
        if (unit.status === 'PENDING_CREATE' || unit.isLocked) {
          return '未执行'
        }
      }
      return getOrderUnitStatusLabel(unit.status)
    },
    openRefund() {
      uni.navigateTo({
        url: `/pages/refund/refund?orderId=${this.orderId}`
      })
    },
    openComplaint() {
      uni.navigateTo({
        url: `/pages/complaint/complaint?orderId=${this.orderId}`
      })
    },
    handleRepublish() {
      const payload = {
        categoryId: this.orderDetail.categoryId,
        pricingMode: this.orderDetail.pricingMode,
        budgetAmount: this.orderDetail.budgetAmount || this.orderDetail.orderAmount,
        quantity: this.orderDetail.quantity,
        workerCount: this.orderDetail.workerCount,
        serviceDurationDesc: this.orderDetail.serviceDurationDesc,
        requireDesc: this.orderDetail.requireDesc,
        province: this.orderDetail.province,
        city: this.orderDetail.city,
        district: this.orderDetail.district,
        street: this.orderDetail.street,
        detailAddress: this.orderDetail.detailAddress,
        longitude: this.orderDetail.longitude,
        latitude: this.orderDetail.latitude,
        needInvoice: !!this.orderDetail.needInvoice,
        needSplit: !!this.orderDetail.needSplit,
        priceItems: Array.isArray(this.orderDetail.priceItems) ? this.orderDetail.priceItems : [],
        attachments: Array.isArray(this.orderDetail.attachments) ? this.orderDetail.attachments : []
      }
      uni.setStorageSync(ORDER_REPUBLISH_STORAGE_KEY, payload)
      uni.switchTab({
        url: '/pages/index/index'
      })
    },
    async handlePayOrder() {
      if (!this.orderId || !this.isPublisherViewer || !this.isPendingPayOrder) {
        return
      }
      try {
        const resp = await submitWechatMiniProgramPay({ orderId: this.orderId })
        if (resp && resp.displayMode === 'mock') {
          uni.showToast({ title: '模拟支付已提交', icon: 'success' })
          await this.loadPayStatus(true)
          return
        }
        const paymentParams = resp && resp.paymentParams
        if (!paymentParams) {
          throw new Error('未获取到微信支付参数')
        }
        await new Promise((resolve, reject) => {
          uni.requestPayment({
            timeStamp: paymentParams.timeStamp,
            nonceStr: paymentParams.nonceStr,
            package: paymentParams.packageValue,
            signType: paymentParams.signType,
            paySign: paymentParams.paySign,
            success: resolve,
            fail: reject
          })
        })
        uni.showToast({ title: '支付成功', icon: 'success' })
        await this.loadPayStatus(true)
      } catch (error) {
        const message = error && error.errMsg ? error.errMsg : ''
        if (message.includes('cancel')) {
          uni.showToast({ title: '已取消支付', icon: 'none' })
        }
      }
    },
    openExternalUrl(url) {
      if (!url) {
        return
      }
      // #ifdef APP-PLUS
      plus.runtime.openURL(url)
      // #endif
      // #ifndef APP-PLUS
      window.location.href = url
      // #endif
    },
    async loadPayStatus(sync = false) {
      if (!this.orderId) {
        return null
      }
      const payOrder = await getPayOrder({
        orderId: this.orderId,
        sync
      }, { silent: true }).catch(() => null)
      if (payOrder) {
        this.orderDetail = {
          ...this.orderDetail,
          payOrderId: payOrder.id || this.orderDetail.payOrderId,
          payRecord: {
            ...(this.orderDetail.payRecord || {}),
            ...payOrder
          }
        }
      }
      return payOrder
    },
    canDeleteProof(unit, proof) {
      if (!this.isAssignedMerchantViewer || !unit || !proof) {
        return false
      }
      return ['ACCEPTED', 'SERVING', 'PENDING_CONFIRM'].includes(unit.status)
    },
    async handleDeleteProof(unit, proof) {
      uni.showModal({
        title: '删除凭证',
        content: '删除后该图片将不再作为完工凭证，是否继续？',
        success: async ({ confirm }) => {
          if (!confirm) {
            return
          }
          try {
            await deleteDeliveryProof({
              proofId: proof.id
            })
            uni.showToast({ title: '已删除', icon: 'success' })
            this.loadDetail()
          } catch (error) {
          }
        }
      })
    },
    async openPlatformService() {
      await openPlatformContact()
    },
  }
}
</script>

<style lang="scss" scoped>
.page-container {
  min-height: 100vh;
  background: #f7f9fc;
}

.header {
  background: #fff;
  padding: 60rpx 30rpx 26rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1rpx solid #eef2f7;
}

.back-btn,
.placeholder {
  width: 60rpx;
}

.back-icon {
  font-size: 40rpx;
  color: #333;
  transform: rotate(180deg);
}

.title {
  font-size: 34rpx;
  font-weight: 600;
  color: #1f2937;
}

.content-scroll {
  padding: 20rpx 24rpx 0;
  box-sizing: border-box;
}

.summary-card,
.rule-card,
.unit-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.summary-card {
  background: linear-gradient(180deg, #eaf3ff 0%, #f8fbff 100%);
}

.order-no,
.summary-text,
.unit-meta,
.rule-text,
.link-text {
  display: block;
  font-size: 24rpx;
  color: #7b8794;
  line-height: 1.7;
}

.card-title,
.order-title,
.rule-title,
.unit-title {
  display: block;
  color: #111827;
  font-weight: 600;
}

.card-title,
.rule-title {
  font-size: 30rpx;
  margin-bottom: 12rpx;
}

.order-title {
  font-size: 34rpx;
  margin: 10rpx 0 16rpx;
}

.summary-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
}

.summary-main {
  flex: 1;
}

.summary-status {
  min-width: 120rpx;
  height: 52rpx;
  padding: 0 18rpx;
  border-radius: 16rpx;
  background: rgba(46, 131, 240, 0.12);
  color: #2e83f0;
  font-size: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.summary-row,
.unit-head,
.unit-actions,
.footer-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.total-amount {
  font-size: 44rpx;
  color: #ef4444;
  font-weight: 700;
}

.split-tip {
  font-size: 24rpx;
  color: #f59e0b;
  background: rgba(245, 158, 11, 0.12);
  padding: 10rpx 18rpx;
  border-radius: 14rpx;
}

.summary-label,
.summary-address {
  display: block;
  color: #7b8794;
  font-size: 24rpx;
  line-height: 1.7;
}

.summary-label {
  margin-top: 8rpx;
}

.summary-address {
  margin-top: 12rpx;
}

.unit-card {
  border: 2rpx solid #d8e1ee;
  box-shadow: 0 12rpx 28rpx rgba(31, 41, 55, 0.05);
}

.card-finished {
  border-color: #32b357;
}

.card-serving {
  border-color: #f4ab23;
}

.card-flowed {
  border-color: #f59e0b;
}

.card-muted {
  border-color: #d8e1ee;
  opacity: 0.9;
}

.unit-seq {
  min-width: 112rpx;
  padding: 8rpx 16rpx;
  border-radius: 10rpx;
  color: #fff;
  font-size: 24rpx;
  text-align: center;
  background: #64748b;
}

.card-finished .unit-seq {
  background: #21a048;
}

.card-serving .unit-seq {
  background: #f59e0b;
}

.card-flowed .unit-seq {
  background: #f59e0b;
}

.card-muted .unit-seq {
  background: #94a3b8;
}

.unit-head-status {
  font-size: 28rpx;
  font-weight: 600;
  color: #64748b;
}

.card-finished .unit-head-status {
  color: #21a048;
}

.card-serving .unit-head-status {
  color: #f59e0b;
}

.card-flowed .unit-head-status {
  color: #f59e0b;
}

.unit-title {
  font-size: 32rpx;
  margin: 20rpx 0 12rpx;
}

.unit-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.unit-cell {
  width: calc(50% - 8rpx);
  padding: 18rpx 20rpx;
  border-radius: 16rpx;
  background: #f8fafc;
  box-sizing: border-box;
}

.cell-wide {
  width: 100%;
}

.cell-label,
.cell-value {
  display: block;
}

.cell-label {
  font-size: 22rpx;
  color: #94a3b8;
}

.cell-value {
  margin-top: 8rpx;
  color: #334155;
  font-size: 25rpx;
  line-height: 1.6;
}

.lock-text,
.link-text {
  margin-top: 6rpx;
}

.link-text {
  color: #94a3b8;
}

.hint-box {
  margin-top: 18rpx;
  padding: 18rpx 20rpx;
  border-radius: 16rpx;
  background: #fff7e8;
}

.proof-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-top: 18rpx;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
  margin-top: 18rpx;
}

.review-card {
  padding: 18rpx 20rpx;
  border-radius: 16rpx;
  background: #f8fafc;
}

.review-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.review-title {
  font-size: 24rpx;
  color: #1e293b;
  font-weight: 600;
}

.review-meta {
  font-size: 22rpx;
  color: #f59e0b;
}

.review-content {
  display: block;
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 1.6;
  color: #334155;
}

.review-time {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #94a3b8;
}

.proof-item {
  position: relative;
}

.proof-image {
  width: 132rpx;
  height: 132rpx;
  border-radius: 14rpx;
  background: #eef2f7;
}

.proof-delete {
  position: absolute;
  top: -10rpx;
  right: -10rpx;
  width: 36rpx;
  height: 36rpx;
  border-radius: 50%;
  background: rgba(15, 23, 42, 0.78);
  display: flex;
  align-items: center;
  justify-content: center;
}

.proof-delete-text {
  color: #fff;
  font-size: 26rpx;
  line-height: 1;
}

.unit-actions {
  gap: 16rpx;
  margin-top: 22rpx;
}

.unit-btn {
  flex: 1;
  height: 72rpx;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26rpx;
}

.unit-btn.secondary {
  background: #fff7e8;
  color: #d9822b;
}

.unit-btn.primary {
  background: #2e83f0;
  color: #fff;
}

.unit-btn.primary-light {
  background: #eaf2ff;
  color: #90a4c2;
}

.unit-btn.disabled {
  background: #f3f4f6;
  color: #9ca3af;
}

.rule-card {
  border: 1rpx solid #e5edf8;
}

.rule-title {
  margin-bottom: 14rpx;
}

.rule-text {
  padding-left: 22rpx;
  position: relative;
}

.rule-text::before {
  content: '';
  position: absolute;
  left: 0;
  top: 16rpx;
  width: 8rpx;
  height: 8rpx;
  border-radius: 50%;
  background: #2e83f0;
}

.footer-actions {
  position: fixed;
  left: 24rpx;
  right: 24rpx;
  bottom: calc(24rpx + env(safe-area-inset-bottom));
  gap: 16rpx;
}

.footer-btn {
  flex: 1;
  height: 88rpx;
  border-radius: 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30rpx;
}

.footer-btn.secondary {
  background: #edf5ff;
  color: #4c89df;
}

.bottom-space {
  height: 160rpx;
}
</style>
