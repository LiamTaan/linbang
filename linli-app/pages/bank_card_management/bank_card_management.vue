<template>
    <view class="page-container">
        <view class="header">
            <view class="back-btn" @click="goBack">
                <text class="iconfont icon-youjiantou back-icon"></text>
            </view>
            <text class="title">银行卡管理</text>
            <view class="placeholder"></view>
        </view>

        <scroll-view class="content-scroll" scroll-y>
            <view class="card-list">
                <view
                    v-for="card in bankCards"
                    :key="card.id"
                    class="bank-card"
                    :style="{ background: resolveCardBackground(card.bankCode) }">
                    <view class="card-content">
                        <view class="card-left">
                            <view class="card-tag">
                                <image class="bank-icon" :src="resolveBankIcon(card.bankCode)" />
                            </view>
                            <view class="bank-info">
                                <text class="bank-name">{{ card.bankName }}</text>
                                <text class="card-type">
                                    {{ card.accountName }} · {{ card.transferEnabled ? '可提现' : '仅展示' }}
                                </text>
                            </view>
                        </view>
                        <text v-if="card.isDefault" class="default-tag">默认卡</text>
                    </view>
                    <text class="card-number">{{ card.cardNoMask }}</text>
                    <view class="card-actions">
                        <text class="action-text" @click="startEdit(card)">编辑</text>
                        <text v-if="!card.isDefault" class="action-text" @click="setDefault(card)">设为默认</text>
                        <text class="action-text danger" @click="removeCard(card)">删除</text>
                    </view>
                </view>
                <view v-if="!bankCards.length" class="empty-card">
                    <text class="empty-text">暂未绑定银行卡</text>
                </view>
            </view>

            <view class="add-card-btn" @click="startCreate">
                <text class="add-text">+添加银行卡</text>
            </view>

            <view v-if="formVisible" class="form-card">
                <text class="form-title">{{ editingId ? '编辑银行卡' : '新增银行卡' }}</text>
                <input
                    v-if="!editingId"
                    class="form-input"
                    v-model="form.cardNo"
                    type="number"
                    maxlength="24"
                    placeholder="请输入银行卡号"
                    @input="handleCardNoInput" />
                <view class="form-input picker-input" :class="{ placeholder: !form.bankName }" @click="openBankSelector">
                    {{ selectedBankText }}
                </view>
                <view v-if="form.bankCode" class="inline-tip">银行编码：{{ form.bankCode }}</view>
                <input class="form-input" v-model="form.accountName" :disabled="accountNameReadonly" :placeholder="accountNameReadonly ? '已从实名认证带出' : '请输入开户名'" />
                <picker
                    mode="multiSelector"
                    :range="areaColumns"
                    range-key="name"
                    :value="areaIndexes"
                    @change="handleAreaPickerChange"
                    @columnchange="handleAreaColumnChange">
                    <view class="form-input picker-input" :class="{ placeholder: !selectedAreaText }">
                        {{ selectedAreaText || '请选择开户省 / 市' }}
                    </view>
                </picker>
                <input
                    class="form-input"
                    v-model="form.reservedMobile"
                    type="number"
                    maxlength="11"
                    placeholder="请输入银行预留手机号" />
                <view class="default-switch" @click="form.isDefault = !form.isDefault">
                    <text class="default-label">设为默认银行卡</text>
                    <view class="switch-box" :class="{ active: form.isDefault }">
                        <view class="switch-dot"></view>
                    </view>
                </view>
                <view class="form-actions">
                    <view class="form-btn ghost" @click="cancelForm">取消</view>
                    <view class="form-btn primary" @click="submitForm">保存</view>
                </view>
            </view>

            <view class="tip-card">
                <view class="tip-header">
                    <image class="tip-icon" src="/static/img/bank_card_management/warning@3x.png" />
                    <text class="tip-title">温馨提示</text>
                </view>
                <view class="tip-list">
                    <text class="tip-item">• 仅支持绑定本人名下的银行卡</text>
                    <text class="tip-item">• 银行卡信息将用于提现和退款</text>
                    <text class="tip-item">• 银行会根据卡号自动识别，也可手动校正银行名称</text>
                    <text class="tip-item">• 最多可绑定五张银行卡</text>
                </view>
            </view>

            <view class="bottom-space"></view>
        </scroll-view>

        <view v-if="bankSelectorVisible" class="selector-mask" @click="closeBankSelector">
            <view class="selector-panel" @click.stop>
                <view class="selector-header">
                    <text class="selector-title">选择银行</text>
                    <text class="selector-close" @click="closeBankSelector">关闭</text>
                </view>
                <input
                    class="selector-search"
                    v-model="bankSearchKeyword"
                    placeholder="搜索银行名称或编码"
                    confirm-type="search" />
                <scroll-view class="selector-list" scroll-y>
                    <view
                        v-for="item in filteredBankOptions"
                        :key="item.code"
                        class="selector-item"
                        :class="{ active: item.code === form.bankCode }"
                        @click="selectBank(item)">
                        <text class="selector-item-name">{{ item.name }}</text>
                        <text class="selector-item-code">{{ item.code }}</text>
                    </view>
                    <view v-if="!filteredBankOptions.length" class="selector-empty">没有找到匹配的银行</view>
                </scroll-view>
            </view>
        </view>
    </view>
</template>

<script>
import { getAreaTree, getProfile, getRealName } from '@/api/member'
import {
    createBankCard,
    deleteBankCard,
    getBankCardPage,
    setDefaultBankCard,
    updateBankCard
} from '@/api/wallet'

const BANK_OPTIONS = [
    { code: 'ICBC', name: '中国工商银行', prefixes: ['620200', '620202', '621226', '621558'] },
    { code: 'ABC', name: '中国农业银行', prefixes: ['622848', '622845', '621336', '621282'] },
    { code: 'BOC', name: '中国银行', prefixes: ['621661', '621660', '622760', '625928'] },
    { code: 'CCB', name: '中国建设银行', prefixes: ['621700', '436742', '622280', '621284'] },
    { code: 'BCM', name: '交通银行', prefixes: ['622260', '622258', '622259', '405512'] },
    { code: 'PSBC', name: '中国邮政储蓄银行', prefixes: ['621098', '622150', '622151', '621799'] },
    { code: 'CMB', name: '招商银行', prefixes: ['622575', '621286', '621483', '439225'] },
    { code: 'CMBC', name: '中国民生银行', prefixes: ['622622', '622600', '421869', '356827'] },
    { code: 'CITIC', name: '中信银行', prefixes: ['622690', '622691', '433670', '403391', '621767', '621768', '621769', '621770', '621771', '621772', '621773', '623280', '620527'] },
    { code: 'SPDB', name: '浦发银行', prefixes: ['622521', '622522', '621792', '970061'] },
    { code: 'CIB', name: '兴业银行', prefixes: ['622908', '622909', '438588', '486493'] },
    { code: 'CEB', name: '中国光大银行', prefixes: ['622666', '622667', '622668', '406252'] },
    { code: 'PAB', name: '平安银行', prefixes: ['622155', '622156', '621626', '623058'] },
    { code: 'HXB', name: '华夏银行', prefixes: ['622630', '622631', '539867', '528709'] },
    { code: 'BOB', name: '北京银行', prefixes: ['421317', '602969', '621030', '621420', '621468', '623111', '422160', '422161', '623561', '623562'] },
    { code: 'BRCB', name: '北京农村商业银行', prefixes: [] },
    { code: 'BOS', name: '上海银行', prefixes: ['622892', '940021', '621050', '622515'] },
    { code: 'SRCB', name: '上海农村商业银行', prefixes: [] },
    { code: 'CBHB', name: '渤海银行', prefixes: [] },
    { code: 'NBCB', name: '宁波银行', prefixes: [] },
    { code: 'NJCB', name: '南京银行', prefixes: [] },
    { code: 'JSBC', name: '江苏银行', prefixes: [] },
    { code: 'HZCB', name: '杭州银行', prefixes: [] },
    { code: 'GZCB', name: '广州银行', prefixes: [] },
    { code: 'CQCB', name: '重庆银行', prefixes: [] },
    { code: 'CDCB', name: '成都银行', prefixes: [] },
    { code: 'BOCD', name: '承德银行', prefixes: [] },
    { code: 'DLB', name: '大连银行', prefixes: [] },
    { code: 'QLB', name: '齐鲁银行', prefixes: [] },
    { code: 'JZB', name: '晋中银行', prefixes: [] },
    { code: 'HSB', name: '徽商银行', prefixes: [] },
    { code: 'LSB', name: '兰州银行', prefixes: [] },
    { code: 'BOD', name: '东莞银行', prefixes: [] }
]

function createEmptyForm() {
    return {
        bankName: '',
        bankCode: '',
        cardNo: '',
        accountName: '',
        bankProvince: '',
        bankCity: '',
        reservedMobile: '',
        isDefault: false
    }
}

export default {
    data() {
        return {
            bankCards: [],
            bankOptions: BANK_OPTIONS,
            formVisible: false,
            editingId: null,
            form: createEmptyForm(),
            bankPickerIndex: 0,
            bankSelectorVisible: false,
            bankSearchKeyword: '',
            areaTree: [],
            areaColumns: [[], []],
            areaIndexes: [0, 0],
            profile: {},
            realNameDetail: {}
        }
    },
    onShow() {
        this.loadCards()
        this.ensureAreaTree()
        this.loadUserBaseInfo()
    },
    computed: {
        selectedBankText() {
            return this.form.bankName ? `${this.form.bankName}（可点此校正）` : '请选择银行，输入卡号后会自动识别'
        },
        filteredBankOptions() {
            const keyword = `${this.bankSearchKeyword || ''}`.trim().toUpperCase()
            if (!keyword) {
                return this.bankOptions
            }
            return this.bankOptions.filter((item) => {
                return item.name.includes(keyword) || item.code.includes(keyword)
            })
        },
        selectedAreaText() {
            return [this.form.bankProvince, this.form.bankCity].filter(Boolean).join(' / ')
        },
        accountNameReadonly() {
            return !!(this.realNameDetail && this.realNameDetail.realName)
        }
    },
    methods: {
        async loadUserBaseInfo() {
            try {
                const [profile, realNameDetail] = await Promise.all([
                    getProfile({ silent: true }).catch(() => ({})),
                    getRealName({ silent: true }).catch(() => ({}))
                ])
                this.profile = profile || {}
                this.realNameDetail = realNameDetail || {}
            } catch (error) {
            }
        },
        async loadCards() {
            try {
                const page = await getBankCardPage({
                    pageNo: 1,
                    pageSize: 50
                })
                this.bankCards = page.list || []
            } catch (error) {
            }
        },
        resolveBankIcon(bankCode) {
            if (bankCode === 'CCB') {
                return '/static/img/bank_card_management/ccb@3x.png'
            }
            if (bankCode === 'PSBC') {
                return '/static/img/bank_card_management/psbc@3x.png'
            }
            if (bankCode === 'ABC') {
                return '/static/img/bank_card_management/psbc@3x.png'
            }
            return '/static/img/bank_card_management/icbc@3x.png'
        },
        resolveCardBackground(bankCode) {
            if (bankCode === 'CCB') {
                return 'linear-gradient(135deg, #004F9C 0%, #164A8F 100%)'
            }
            if (bankCode === 'PSBC') {
                return 'linear-gradient(135deg, #128B3E 0%, #23703D 100%)'
            }
            return 'linear-gradient(135deg, #D64C50 0%, #C43A3A 100%)'
        },
        async startCreate() {
            this.editingId = null
            this.form = createEmptyForm()
            await this.ensureAreaTree()
            this.resetAreaPicker()
            this.applyDefaultAccountInfo()
            this.bankPickerIndex = 0
            this.formVisible = true
        },
        async startEdit(card) {
            await this.ensureAreaTree()
            this.editingId = card.id
            this.form = {
                bankName: card.bankName || '',
                bankCode: card.bankCode || '',
                cardNo: '',
                accountName: card.accountName || '',
                bankProvince: card.bankProvince || '',
                bankCity: card.bankCity || '',
                reservedMobile: card.reservedMobile || '',
                isDefault: !!card.isDefault
            }
            this.syncBankPickerIndex()
            this.syncAreaPickerFromForm()
            this.formVisible = true
        },
        cancelForm() {
            this.formVisible = false
            this.editingId = null
            this.form = createEmptyForm()
            this.bankPickerIndex = 0
            this.resetAreaPicker()
        },
        applyDefaultAccountInfo() {
            if (this.realNameDetail && this.realNameDetail.realName) {
                this.form.accountName = this.realNameDetail.realName
            } else if (this.profile && this.profile.nickname) {
                this.form.accountName = this.profile.nickname
            }
            if (this.profile && this.profile.mobile) {
                this.form.reservedMobile = this.profile.mobile
            }
        },
        handleCardNoInput(event) {
            const rawValue = event && event.detail ? event.detail.value : this.form.cardNo
            this.form.cardNo = `${rawValue || ''}`.replace(/\s+/g, '')
            const detected = this.detectBankByCardNo(this.form.cardNo)
            if (!detected) {
                return
            }
            this.form.bankName = detected.name
            this.form.bankCode = detected.code
            this.syncBankPickerIndex()
        },
        detectBankByCardNo(cardNo) {
            const value = `${cardNo || ''}`.trim()
            if (value.length < 6) {
                return null
            }
            const matched = this.bankOptions.find((item) => {
                return (item.prefixes || []).some((prefix) => value.startsWith(prefix))
            })
            return matched || null
        },
        openBankSelector() {
            this.bankSearchKeyword = this.form.bankName || ''
            this.bankSelectorVisible = true
        },
        closeBankSelector() {
            this.bankSelectorVisible = false
            this.bankSearchKeyword = ''
        },
        selectBank(bank) {
            if (!bank) {
                return
            }
            this.form.bankName = bank.name
            this.form.bankCode = bank.code
            this.syncBankPickerIndex()
            this.closeBankSelector()
        },
        syncBankPickerIndex() {
            const index = this.bankOptions.findIndex((item) => item.code === this.form.bankCode)
            this.bankPickerIndex = index >= 0 ? index : 0
        },
        async submitForm() {
            if (!this.form.bankName || !this.form.bankCode || !this.form.accountName || !this.form.bankProvince || !this.form.bankCity || !this.form.reservedMobile) {
                uni.showToast({
                    title: '请填写完整银行卡信息',
                    icon: 'none'
                })
                return
            }
            if (!this.editingId && !/^\d{12,24}$/.test(`${this.form.cardNo || ''}`)) {
                uni.showToast({
                    title: '请输入正确的银行卡号',
                    icon: 'none'
                })
                return
            }
            if (!/^1[3-9]\d{9}$/.test(`${this.form.reservedMobile || ''}`)) {
                uni.showToast({
                    title: '请输入正确的预留手机号',
                    icon: 'none'
                })
                return
            }
            try {
                if (this.editingId) {
                    await updateBankCard({
                        id: this.editingId,
                        bankName: this.form.bankName,
                        bankCode: this.form.bankCode,
                        accountName: this.form.accountName,
                        bankProvince: this.form.bankProvince,
                        bankCity: this.form.bankCity,
                        reservedMobile: this.form.reservedMobile,
                        isDefault: this.form.isDefault
                    })
                } else {
                    await createBankCard({
                        bankName: this.form.bankName,
                        bankCode: this.form.bankCode,
                        cardNo: this.form.cardNo,
                        accountName: this.form.accountName,
                        bankProvince: this.form.bankProvince,
                        bankCity: this.form.bankCity,
                        reservedMobile: this.form.reservedMobile,
                        isDefault: this.form.isDefault
                    })
                }
                uni.showToast({
                    title: '保存成功',
                    icon: 'success'
                })
                this.cancelForm()
                this.loadCards()
            } catch (error) {
            }
        },
        async ensureAreaTree() {
            if (this.areaTree.length) {
                return
            }
            try {
                this.areaTree = (await getAreaTree({ silent: true })) || []
                this.resetAreaPicker()
            } catch (error) {
                this.areaTree = []
                this.areaColumns = [[], []]
            }
        },
        resetAreaPicker() {
            const provinceList = this.areaTree || []
            const cityList = (provinceList[0] && provinceList[0].children) || []
            this.areaColumns = [provinceList, cityList]
            this.areaIndexes = [0, 0]
        },
        buildAreaColumns(indexes = [0, 0]) {
            const provinceList = this.areaTree || []
            const provinceIndex = Math.min(indexes[0] || 0, Math.max(provinceList.length - 1, 0))
            const cityList = (provinceList[provinceIndex] && provinceList[provinceIndex].children) || []
            return [provinceList, cityList]
        },
        handleAreaColumnChange(event) {
            const { column, value } = event.detail || {}
            const nextIndexes = [...this.areaIndexes]
            nextIndexes[column] = value
            if (column === 0) {
                nextIndexes[1] = 0
            }
            this.areaColumns = this.buildAreaColumns(nextIndexes)
            this.areaIndexes = nextIndexes
        },
        handleAreaPickerChange(event) {
            const indexes = (event.detail && event.detail.value) || [0, 0]
            this.areaColumns = this.buildAreaColumns(indexes)
            this.areaIndexes = indexes
            const [provinceList, cityList] = this.areaColumns
            const province = provinceList[indexes[0]]
            const city = cityList[indexes[1]]
            this.form.bankProvince = province && province.name ? province.name : ''
            this.form.bankCity = city && city.name ? city.name : ''
        },
        syncAreaPickerFromForm() {
            if (!this.areaTree.length) {
                return
            }
            const path = this.findAreaPath(this.areaTree, {
                province: this.form.bankProvince,
                city: this.form.bankCity
            })
            if (!path) {
                this.resetAreaPicker()
                return
            }
            const indexes = [path.provinceIndex, path.cityIndex]
            this.areaColumns = this.buildAreaColumns(indexes)
            this.areaIndexes = indexes
        },
        findAreaPath(provinceList, target) {
            for (let provinceIndex = 0; provinceIndex < provinceList.length; provinceIndex += 1) {
                const province = provinceList[provinceIndex]
                if (target.province && province.name !== target.province) {
                    continue
                }
                const cityList = province.children || []
                for (let cityIndex = 0; cityIndex < cityList.length; cityIndex += 1) {
                    const city = cityList[cityIndex]
                    if (!target.city || city.name === target.city) {
                        return {
                            provinceIndex,
                            cityIndex
                        }
                    }
                }
            }
            return undefined
        },
        async setDefault(card) {
            try {
                await setDefaultBankCard({ id: card.id })
                uni.showToast({
                    title: '默认卡已更新',
                    icon: 'success'
                })
                this.loadCards()
            } catch (error) {
            }
        },
        removeCard(card) {
            uni.showModal({
                title: '确认删除',
                content: `确认删除 ${card.bankName} ${card.cardNoMask} 吗？`,
                success: async (res) => {
                    if (!res.confirm) {
                        return
                    }
                    try {
                        await deleteBankCard(card.id)
                        uni.showToast({
                            title: '删除成功',
                            icon: 'success'
                        })
                        this.loadCards()
                    } catch (error) {
                    }
                }
            })
        },
        goBack() {
            this.$navigateBack()
        }
    }
}
</script>

<style lang="scss" scoped>
.page-container {
    min-height: 100vh;
    background: #F5F5F5;

    .header {
        background: #fff;
        padding: 60rpx 30rpx 30rpx;
        display: flex;
        justify-content: space-between;
        align-items: center;

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
            font-weight: bold;
            color: #333;
        }
    }

    .content-scroll {
        padding: 20rpx;
        box-sizing: border-box;

        .card-list {
            .bank-card,
            .empty-card {
                border-radius: 16rpx;
                padding: 24rpx;
                margin-bottom: 20rpx;
            }

            .empty-card {
                background: #fff;
                text-align: center;

                .empty-text {
                    font-size: 24rpx;
                    color: #999;
                }
            }

            .bank-card {
                color: #fff;

                .card-content {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 20rpx;
                }

                .card-left {
                    display: flex;
                    align-items: center;
                    gap: 16rpx;
                }

                .card-tag {
                    width: 48rpx;
                    height: 48rpx;
                    border-radius: 50%;
                    background-color: #fff;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                }

                .bank-icon {
                    width: 30rpx;
                    height: 30rpx;
                }

                .bank-info {
                    display: flex;
                    flex-direction: column;
                    gap: 4rpx;
                }

                .bank-name {
                    font-size: 28rpx;
                    font-weight: bold;
                }

                .card-type {
                    font-size: 22rpx;
                    color: rgba(255, 255, 255, 0.8);
                }

                .default-tag {
                    font-size: 22rpx;
                    background: rgba(255, 255, 255, 0.2);
                    padding: 8rpx 16rpx;
                    border-radius: 20rpx;
                }

                .card-number {
                    display: block;
                    font-size: 28rpx;
                    font-weight: bold;
                    letter-spacing: 4rpx;
                    margin-bottom: 20rpx;
                }

                .card-actions {
                    display: flex;
                    justify-content: flex-end;
                    gap: 24rpx;

                    .action-text {
                        font-size: 24rpx;
                        color: #fff;

                        &.danger {
                            color: #FFD5D5;
                        }
                    }
                }
            }
        }

        .add-card-btn {
            border: 2rpx dashed #4A90F0;
            border-radius: 16rpx;
            padding: 36rpx;
            text-align: center;
            margin-bottom: 20rpx;

            .add-text {
                font-size: 28rpx;
                color: #4A90F0;
                font-weight: bold;
            }
        }

        .form-card,
        .tip-card {
            background: #fff;
            border-radius: 16rpx;
            padding: 24rpx;
            margin-bottom: 20rpx;
        }

        .form-title {
            display: block;
            font-size: 28rpx;
            font-weight: bold;
            color: #333;
            margin-bottom: 20rpx;
        }

        .form-input {
            height: 88rpx;
            border-radius: 12rpx;
            background: #F7F9FC;
            padding: 0 24rpx;
            font-size: 26rpx;
            color: #333;
            margin-bottom: 16rpx;
            box-sizing: border-box;
        }

        .picker-input {
            display: flex;
            align-items: center;
        }

        .picker-input.placeholder {
            color: #999;
        }

        .inline-tip {
            font-size: 22rpx;
            color: #8D6E63;
            margin: -6rpx 0 16rpx 6rpx;
        }

        .default-switch {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 12rpx 0 24rpx;

            .default-label {
                font-size: 26rpx;
                color: #333;
            }

            .switch-box {
                width: 100rpx;
                height: 56rpx;
                background: #E8E8E8;
                border-radius: 28rpx;
                position: relative;

                &.active {
                    background: #4A90F0;

                    .switch-dot {
                        transform: translateX(44rpx);
                    }
                }

                .switch-dot {
                    width: 48rpx;
                    height: 48rpx;
                    background: #fff;
                    border-radius: 50%;
                    position: absolute;
                    top: 4rpx;
                    left: 4rpx;
                    transition: transform 0.3s;
                }
            }
        }

        .form-actions {
            display: flex;
            gap: 16rpx;

            .form-btn {
                flex: 1;
                height: 84rpx;
                border-radius: 12rpx;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 28rpx;

                &.ghost {
                    background: #F2F4F7;
                    color: #333;
                }

                &.primary {
                    background: #4A90F0;
                    color: #fff;
                }
            }
        }

        .tip-header {
            display: flex;
            align-items: center;
            gap: 12rpx;
            margin-bottom: 16rpx;

            .tip-icon {
                width: 32rpx;
                height: 32rpx;
            }

            .tip-title {
                font-size: 26rpx;
                color: #E57373;
                font-weight: bold;
            }
        }

        .tip-list {
            display: flex;
            flex-direction: column;
            gap: 12rpx;

            .tip-item {
                font-size: 24rpx;
                color: #8D6E63;
                line-height: 1.5;
            }
        }

        .bottom-space {
            height: 60rpx;
        }
    }

    .selector-mask {
        position: fixed;
        inset: 0;
        background: rgba(0, 0, 0, 0.38);
        display: flex;
        align-items: flex-end;
        z-index: 30;
    }

    .selector-panel {
        width: 100%;
        background: #fff;
        border-radius: 24rpx 24rpx 0 0;
        padding: 24rpx;
        max-height: 70vh;
    }

    .selector-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 18rpx;
    }

    .selector-title {
        font-size: 30rpx;
        font-weight: bold;
        color: #333;
    }

    .selector-close {
        font-size: 24rpx;
        color: #4A90F0;
    }

    .selector-search {
        height: 80rpx;
        border-radius: 12rpx;
        background: #F7F9FC;
        padding: 0 24rpx;
        font-size: 26rpx;
        color: #333;
        margin-bottom: 18rpx;
    }

    .selector-list {
        max-height: 52vh;
    }

    .selector-item {
        padding: 22rpx 6rpx;
        border-bottom: 1rpx solid #F0F0F0;
        display: flex;
        justify-content: space-between;
        align-items: center;

        &.active .selector-item-name,
        &.active .selector-item-code {
            color: #2F86F6;
            font-weight: 600;
        }
    }

    .selector-item-name,
    .selector-item-code,
    .selector-empty {
        font-size: 26rpx;
        color: #333;
    }

    .selector-item-code {
        color: #999;
        margin-left: 20rpx;
    }

    .selector-empty {
        text-align: center;
        color: #999;
        padding: 40rpx 0;
    }
}
</style>

