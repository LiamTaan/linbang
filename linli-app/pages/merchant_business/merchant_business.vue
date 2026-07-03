<template>
  <view class="page-container">
    <view class="header">
      <view class="back-btn" @click="$navigateBack()">
        <text class="iconfont icon-youjiantou back-icon"></text>
      </view>
      <text class="title">服务经营资料</text>
      <view class="placeholder"></view>
    </view>
    <scroll-view class="content-scroll" scroll-y refresher-enabled :refresher-triggered="refreshing" @refresherrefresh="loadPageData">
      <view class="card">
        <text class="section-title">当前经营类目</text>
        <text class="sub">{{ categoryAbilityText }}</text>
        <text class="sub">接单品类请在“接单状态管理”里设置，这里维护的是经营范围与参考价格。</text>
      </view>

      <view class="card">
        <view class="row between">
          <text class="section-title">服务范围管理</text>
          <text class="action-link" @click="openPointEditor()">新增</text>
        </view>
        <view v-if="!servicePoints.length" class="empty-text">暂无服务点</view>
        <view v-for="item in servicePoints" :key="item.id" class="list-item">
          <view class="row between">
            <text>{{ item.pointName }}</text>
            <text class="status" :class="{ off: item.status !== 'ENABLE' }">{{ item.status === 'ENABLE' ? '启用中' : '已停用' }}</text>
          </view>
          <text class="sub">{{ buildPointAddress(item) }}</text>
          <text class="sub">服务半径：{{ item.serviceRadiusKm }} km</text>
          <view class="actions">
            <text class="mini-link" @click="openPointEditor(item)">编辑</text>
            <text class="mini-link" @click="togglePointStatus(item)">{{ item.status === 'ENABLE' ? '停用' : '启用' }}</text>
            <text class="mini-link danger" @click="deletePoint(item)">删除</text>
          </view>
        </view>
      </view>

      <view class="card">
        <view class="row between">
          <text class="section-title">参考价格</text>
          <text class="action-link" @click="openPriceEditor()">新增</text>
        </view>
        <view v-if="!referencePrices.length" class="empty-text">暂无参考价格</view>
        <view v-for="item in referencePrices" :key="item.id" class="list-item">
          <view class="row between">
            <text>{{ item.categoryName || '--' }}</text>
            <text class="status" :class="{ off: item.status !== 'ENABLE' }">{{ item.status === 'ENABLE' ? '展示中' : '已停用' }}</text>
          </view>
          <text class="sub">{{ item.priceUnitLabel }} · ¥{{ $fmt.formatMoney(item.referencePriceMin) }} - ¥{{ $fmt.formatMoney(item.referencePriceMax) }}</text>
          <text class="sub" v-if="item.referencePriceDesc">{{ item.referencePriceDesc }}</text>
          <view class="actions">
            <text class="mini-link" @click="openPriceEditor(item)">编辑</text>
            <text class="mini-link" @click="togglePriceStatus(item)">{{ item.status === 'ENABLE' ? '停用' : '启用' }}</text>
            <text class="mini-link danger" @click="deletePrice(item)">删除</text>
          </view>
        </view>
      </view>
      <view class="bottom-space"></view>
    </scroll-view>

    <view v-if="pointEditorVisible" class="mask" @click="closePointEditor">
      <view class="sheet" @click.stop>
        <text class="sheet-title">{{ pointForm.id ? '编辑服务点' : '新增服务点' }}</text>
        <input v-model="pointForm.pointName" class="field-input" placeholder="服务点名称" />
        <input v-model="pointForm.province" class="field-input" placeholder="省" />
        <input v-model="pointForm.city" class="field-input" placeholder="市" />
        <input v-model="pointForm.district" class="field-input" placeholder="区" />
        <input v-model="pointForm.street" class="field-input" placeholder="街道（选填）" />
        <input v-model="pointForm.detailAddress" class="field-input" placeholder="详细地址" />
        <input v-model="pointForm.serviceRadiusKm" class="field-input" type="digit" placeholder="服务半径（km）" />
        <view class="sheet-actions">
          <view class="sheet-btn ghost" @click="closePointEditor">取消</view>
          <view class="sheet-btn primary" @click="savePoint">保存</view>
        </view>
      </view>
    </view>

    <view v-if="priceEditorVisible" class="mask" @click="closePriceEditor">
      <view class="sheet" @click.stop>
        <text class="sheet-title">{{ priceForm.id ? '编辑参考价格' : '新增参考价格' }}</text>
        <picker :range="categoryOptions" range-key="categoryName" :value="priceCategoryIndex" @change="handleCategoryChange">
          <view class="field-input picker-field">{{ selectedPriceCategoryText }}</view>
        </picker>
        <input v-model="priceForm.priceUnitLabel" class="field-input" placeholder="价格单位，如 元/次" />
        <input v-model="priceForm.referencePriceMin" class="field-input" type="digit" placeholder="参考最低价" />
        <input v-model="priceForm.referencePriceMax" class="field-input" type="digit" placeholder="参考最高价" />
        <textarea v-model="priceForm.referencePriceDesc" class="field-textarea" placeholder="参考价格说明（选填）"></textarea>
        <view class="sheet-actions">
          <view class="sheet-btn ghost" @click="closePriceEditor">取消</view>
          <view class="sheet-btn primary" @click="savePrice">保存</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import {
  createMerchantReferencePrice,
  createMerchantServicePoint,
  deleteMerchantReferencePrice,
  deleteMerchantServicePoint,
  getMerchantProfile,
  getMerchantReferencePriceList,
  getMerchantServicePointPage,
  updateMerchantReferencePrice,
  updateMerchantReferencePriceStatus,
  updateMerchantServicePoint,
  updateMerchantServicePointStatus
} from '@/api/merchant'

function createEmptyPointForm() {
  return {
    id: null,
    pointName: '',
    province: '',
    city: '',
    district: '',
    street: '',
    detailAddress: '',
    serviceRadiusKm: '5'
  }
}

function createEmptyPriceForm() {
  return {
    id: null,
    categoryId: null,
    priceUnitLabel: '',
    referencePriceMin: '',
    referencePriceMax: '',
    referencePriceDesc: ''
  }
}

export default {
  data() {
    return {
      refreshing: false,
      profile: {},
      servicePoints: [],
      referencePrices: [],
      pointEditorVisible: false,
      priceEditorVisible: false,
      pointForm: createEmptyPointForm(),
      priceForm: createEmptyPriceForm()
    }
  },
  computed: {
    categoryOptions() {
      return (this.profile.categories || []).filter((item) => item.categoryId)
    },
    categoryAbilityText() {
      const names = (this.profile.categories || []).map((item) => item.categoryName).filter(Boolean)
      return names.length ? names.join('、') : '暂无经营类目'
    },
    priceCategoryIndex() {
      const index = this.categoryOptions.findIndex((item) => `${item.categoryId}` === `${this.priceForm.categoryId}`)
      return index < 0 ? 0 : index
    },
    selectedPriceCategoryText() {
      const target = this.categoryOptions[this.priceCategoryIndex]
      return target ? target.categoryName : '请选择服务类目'
    }
  },
  onShow() {
    this.loadPageData()
  },
  methods: {
    async loadPageData() {
      try {
        this.refreshing = true
        const [profile, pointPage, referencePrices] = await Promise.all([
          getMerchantProfile({ silent: true }).catch(() => ({})),
          getMerchantServicePointPage({ pageNo: 1, pageSize: 100 }, { silent: true }).catch(() => ({ list: [] })),
          getMerchantReferencePriceList({ silent: true }).catch(() => [])
        ])
        this.profile = profile || {}
        this.servicePoints = (pointPage && pointPage.list) || []
        this.referencePrices = referencePrices || []
      } catch (error) {
      } finally {
        this.refreshing = false
      }
    },
    buildPointAddress(item) {
      return [item.province, item.city, item.district, item.street, item.detailAddress].filter(Boolean).join(' ')
    },
    openPointEditor(item) {
      this.pointForm = item ? {
        id: item.id,
        pointName: item.pointName || '',
        province: item.province || '',
        city: item.city || '',
        district: item.district || '',
        street: item.street || '',
        detailAddress: item.detailAddress || '',
        serviceRadiusKm: `${item.serviceRadiusKm || ''}`
      } : createEmptyPointForm()
      this.pointEditorVisible = true
    },
    closePointEditor() {
      this.pointEditorVisible = false
      this.pointForm = createEmptyPointForm()
    },
    async savePoint() {
      if (!this.pointForm.pointName || !this.pointForm.province || !this.pointForm.city || !this.pointForm.district || !this.pointForm.detailAddress || !this.pointForm.serviceRadiusKm) {
        uni.showToast({ title: '请完整填写服务点信息', icon: 'none' })
        return
      }
      const payload = {
        ...this.pointForm,
        serviceRadiusKm: Number(this.pointForm.serviceRadiusKm)
      }
      try {
        if (payload.id) {
          await updateMerchantServicePoint(payload)
        } else {
          await createMerchantServicePoint(payload)
        }
        uni.showToast({ title: '服务点已保存', icon: 'success' })
        this.closePointEditor()
        this.loadPageData()
      } catch (error) {
      }
    },
    async togglePointStatus(item) {
      try {
        await updateMerchantServicePointStatus({
          id: item.id,
          status: item.status === 'ENABLE' ? 'DISABLE' : 'ENABLE'
        })
        uni.showToast({ title: '服务点状态已更新', icon: 'success' })
        this.loadPageData()
      } catch (error) {
      }
    },
    deletePoint(item) {
      uni.showModal({
        title: '删除服务点',
        content: '删除后该服务区域将不再参与派单，是否继续？',
        success: async ({ confirm }) => {
          if (!confirm) {
            return
          }
          try {
            await deleteMerchantServicePoint(item.id)
            uni.showToast({ title: '服务点已删除', icon: 'success' })
            this.loadPageData()
          } catch (error) {
          }
        }
      })
    },
    openPriceEditor(item) {
      this.priceForm = item ? {
        id: item.id,
        categoryId: item.categoryId,
        priceUnitLabel: item.priceUnitLabel || '',
        referencePriceMin: `${item.referencePriceMin || ''}`,
        referencePriceMax: `${item.referencePriceMax || ''}`,
        referencePriceDesc: item.referencePriceDesc || ''
      } : {
        ...createEmptyPriceForm(),
        categoryId: this.categoryOptions.length ? this.categoryOptions[0].categoryId : null
      }
      this.priceEditorVisible = true
    },
    closePriceEditor() {
      this.priceEditorVisible = false
      this.priceForm = createEmptyPriceForm()
    },
    handleCategoryChange(event) {
      const index = Number((event.detail && event.detail.value) || 0)
      const target = this.categoryOptions[index]
      if (target) {
        this.priceForm.categoryId = target.categoryId
      }
    },
    async savePrice() {
      if (!this.priceForm.categoryId || !this.priceForm.priceUnitLabel || !this.priceForm.referencePriceMin || !this.priceForm.referencePriceMax) {
        uni.showToast({ title: '请完整填写参考价格信息', icon: 'none' })
        return
      }
      const payload = {
        ...this.priceForm,
        referencePriceMin: Number(this.priceForm.referencePriceMin),
        referencePriceMax: Number(this.priceForm.referencePriceMax)
      }
      try {
        if (payload.id) {
          await updateMerchantReferencePrice(payload)
        } else {
          await createMerchantReferencePrice(payload)
        }
        uni.showToast({ title: '参考价格已保存', icon: 'success' })
        this.closePriceEditor()
        this.loadPageData()
      } catch (error) {
      }
    },
    async togglePriceStatus(item) {
      try {
        await updateMerchantReferencePriceStatus({
          id: item.id,
          status: item.status === 'ENABLE' ? 'DISABLE' : 'ENABLE'
        })
        uni.showToast({ title: '参考价格状态已更新', icon: 'success' })
        this.loadPageData()
      } catch (error) {
      }
    },
    deletePrice(item) {
      uni.showModal({
        title: '删除参考价格',
        content: '删除后首页和经营资料将不再显示该条参考价格，是否继续？',
        success: async ({ confirm }) => {
          if (!confirm) {
            return
          }
          try {
            await deleteMerchantReferencePrice(item.id)
            uni.showToast({ title: '参考价格已删除', icon: 'success' })
            this.loadPageData()
          } catch (error) {
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.page-container { min-height: 100vh; background: #f5f7fb; }
.header { background: #fff; padding: 60rpx 30rpx 24rpx; display: flex; justify-content: space-between; align-items: center; border-bottom: 1rpx solid #eef2f7; }
.back-btn,.placeholder { width: 60rpx; }
.back-icon { font-size: 40rpx; color: #333; transform: rotate(180deg); }
.title { font-size: 34rpx; font-weight: bold; color: #1f2937; }
.content-scroll { padding: 20rpx; box-sizing: border-box; }
.card { background: #fff; border-radius: 24rpx; padding: 24rpx; margin-bottom: 20rpx; box-shadow: 0 8rpx 24rpx rgba(15,23,42,.05); }
.row { display: flex; align-items: center; }
.between { justify-content: space-between; }
.section-title { font-size: 30rpx; font-weight: 600; color: #0f172a; }
.sub,.empty-text { display: block; color: #64748b; font-size: 24rpx; line-height: 1.7; margin-top: 10rpx; }
.action-link,.mini-link { color: #2563eb; font-size: 24rpx; }
.status { color: #16a34a; font-size: 24rpx; }
.status.off { color: #f59e0b; }
.list-item { padding: 18rpx 0; border-bottom: 1rpx solid #f1f5f9; }
.list-item:last-child { border-bottom: none; }
.actions { display: flex; gap: 24rpx; margin-top: 12rpx; }
.danger { color: #dc2626; }
.mask { position: fixed; inset: 0; background: rgba(15,23,42,.35); display: flex; align-items: flex-end; }
.sheet { width: 100%; background: #fff; border-radius: 28rpx 28rpx 0 0; padding: 28rpx 24rpx calc(28rpx + env(safe-area-inset-bottom)); }
.sheet-title { display: block; font-size: 30rpx; font-weight: 600; color: #0f172a; margin-bottom: 20rpx; }
.field-input,.picker-field,.field-textarea { width: 100%; background: #f8fafc; border-radius: 16rpx; padding: 20rpx; box-sizing: border-box; font-size: 26rpx; color: #334155; margin-bottom: 16rpx; }
.field-textarea { min-height: 160rpx; }
.sheet-actions { display: flex; gap: 16rpx; margin-top: 12rpx; }
.sheet-btn { flex: 1; height: 84rpx; border-radius: 18rpx; display: flex; align-items: center; justify-content: center; font-size: 28rpx; }
.sheet-btn.ghost { background: #eef4ff; color: #2563eb; }
.sheet-btn.primary { background: #2e83f0; color: #fff; }
.bottom-space { height: 40rpx; }
</style>
