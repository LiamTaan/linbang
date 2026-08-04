<template>
    <view class="page-container">
        <view class="header">
            <view class="back-btn" @click="goBack">
                <text class="iconfont icon-youjiantou back-icon"></text>
            </view>
            <text class="title">系统设置</text>
            <view class="placeholder"></view>
        </view>

        <scroll-view class="content-scroll" scroll-y>
            <view class="set-card">
                <view class="card-header">
                    <text class="header-title">账号安全</text>
                </view>
                <view class="set-item clickable" @click="handleUpdatePassword">
                    <view class="item-main">
                        <image class="item-icon" src="/static/img/set/password@3x.png" />
                        <view class="item-copy">
                            <text class="item-title">修改密码</text>
                            <text class="item-desc">短信验证码校验后修改登录密码</text>
                        </view>
                    </view>
                    <text class="item-value action">立即修改</text>
                </view>
                <view class="set-item">
                    <view class="item-main">
                        <image class="item-icon" src="/static/img/set/phone@3x.png" />
                        <view class="item-copy">
                            <text class="item-title">绑定手机</text>
                            <text class="item-desc">当前登录账号手机号</text>
                        </view>
                    </view>
                    <text class="item-value">{{ $fmt.maskMobile(profile.mobile) || '未绑定' }}</text>
                </view>
                <view class="set-item disabled-item" @click="handleSocialTip">
                    <view class="item-main">
                        <image class="item-icon" src="/static/img/set/wechat@3x.png" />
                        <view class="item-copy">
                            <text class="item-title">绑定微信 / 支付宝</text>
                            <text class="item-desc">当前阶段暂不开放账号绑定</text>
                        </view>
                    </view>
                    <text class="item-tag">暂未开放</text>
                </view>
            </view>

            <view class="set-card">
                <view class="card-header">
                    <text class="header-title">消息与通知</text>
                </view>
                <view class="set-item clickable" @click="handleNotificationPermission">
                    <view class="item-main">
                        <image class="item-icon" src="/static/img/set/message@3x.png" />
                        <view class="item-copy">
                            <text class="item-title">系统通知权限</text>
                            <text class="item-desc">控制系统通知、角标和设备弹窗是否允许</text>
                        </view>
                    </view>
                    <text class="item-value" :class="deviceState.notificationEnabled ? 'status-on' : 'status-off'">
                        {{ deviceState.notificationLabel }}
                    </text>
                </view>
                <view class="set-item">
                    <view class="item-main">
                        <image class="item-icon" src="/static/img/set/order@3x.png" />
                        <view class="item-copy">
                            <text class="item-title">语音朗读</text>
                            <text class="item-desc">控制订单提醒声音播报与设备响铃反馈</text>
                        </view>
                    </view>
                    <switch :checked="messageSetting.voiceReadEnabled" color="#2F86F6" @change="toggleSetting('voiceReadEnabled', $event.detail.value)" />
                </view>
                <view class="set-item">
                    <view class="item-main">
                        <image class="item-icon" src="/static/img/set/fund@3x.png" />
                        <view class="item-copy">
                            <text class="item-title">弹窗提醒</text>
                            <text class="item-desc">控制 App 内推送弹窗和桌面提醒</text>
                        </view>
                    </view>
                    <switch :checked="messageSetting.popupEnabled" color="#2F86F6" @change="toggleSetting('popupEnabled', $event.detail.value)" />
                </view>
                <view class="set-item">
                    <view class="item-main">
                        <image class="item-icon" src="/static/img/set/message@3x.png" />
                        <view class="item-copy">
                            <text class="item-title">营销消息</text>
                            <text class="item-desc">控制活动、权益和推广类业务消息</text>
                        </view>
                    </view>
                    <switch :checked="messageSetting.marketingEnabled" color="#2F86F6" @change="toggleSetting('marketingEnabled', $event.detail.value)" />
                </view>
            </view>

            <view class="set-card">
                <view class="card-header">
                    <text class="header-title">隐私与协议</text>
                </view>
                <view class="set-item clickable" @click="openAgreement('privacy')">
                    <view class="item-main">
                        <image class="item-icon" src="/static/img/set/cache@3x.png" />
                        <view class="item-copy">
                            <text class="item-title">隐私政策</text>
                            <text class="item-desc">查看平台当前隐私协议正文</text>
                        </view>
                    </view>
                    <text class="item-value action">查看</text>
                </view>
                <view class="set-item clickable" @click="openAgreement('service')">
                    <view class="item-main">
                        <image class="item-icon" src="/static/img/set/about@3x.png" />
                        <view class="item-copy">
                            <text class="item-title">用户协议</text>
                            <text class="item-desc">查看平台注册与服务协议内容</text>
                        </view>
                    </view>
                    <text class="item-value action">
                        {{ agreement.registerAgreementVersion || '查看' }}
                    </text>
                </view>
            </view>

            <view class="set-card">
                <view class="card-header">
                    <text class="header-title">设备信息</text>
                </view>
                <view class="set-item clickable" @click="handleVersionUpdate">
                    <view class="item-main">
                        <image class="item-icon" src="/static/img/set/update@3x.png" />
                        <view class="item-copy">
                            <text class="item-title">应用版本</text>
                            <text class="item-desc">当前安装包版本，可跳转下载最新地址</text>
                        </view>
                    </view>
                    <text class="item-value action">{{ deviceState.versionName }}</text>
                </view>
                <view class="set-item clickable" @click="clearLocalCache">
                    <view class="item-main">
                        <image class="item-icon" src="/static/img/set/cache@3x.png" />
                        <view class="item-copy">
                            <text class="item-title">清理缓存</text>
                            <text class="item-desc">清理设备本地缓存文件，不影响登录状态</text>
                        </view>
                    </view>
                    <text class="item-value action">{{ deviceState.cacheSizeLabel }}</text>
                </view>
                <view class="set-item clickable" @click="showAboutUs">
                    <view class="item-main">
                        <image class="item-icon" src="/static/img/set/about@3x.png" />
                        <view class="item-copy">
                            <text class="item-title">关于我们</text>
                            <text class="item-desc">查看平台介绍与服务说明</text>
                        </view>
                    </view>
                    <text class="item-value action">查看</text>
                </view>
            </view>

            <view class="logout-btn" @click="handleLogout">
                <text class="logout-text">退出登录</text>
            </view>

            <view class="bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
import { logout, sendSmsCode } from '@/api/auth'
import { getProfile, updatePassword } from '@/api/member'
import { getMessageSetting, updateMessageSetting } from '@/api/message'
import { getAgreement } from '@/api/platform'
import { notifyReminderSettingChanged } from '@/services/app-order-reminder'
import { getPlatformSettings } from '@/utils/auth'
import { loadPlatformSettings } from '@/services/app-bootstrap'
import { logoutSession } from '@/services/session'
import { normalizeExternalHttpsUrl } from '@/utils/security'

function promptInput(title, placeholder = '') {
    return new Promise((resolve) => {
        uni.showModal({
            title,
            editable: true,
            placeholderText: placeholder,
            success: (res) => resolve(res.confirm ? (res.content || '') : '')
        })
    })
}

function isAppRuntimeReady() {
    return typeof plus !== 'undefined'
}

function formatSize(size) {
    const value = Number(size || 0)
    if (!Number.isFinite(value) || value <= 0) {
        return '0 KB'
    }
    const units = ['B', 'KB', 'MB', 'GB']
    let current = value
    let unitIndex = 0
    while (current >= 1024 && unitIndex < units.length - 1) {
        current = current / 1024
        unitIndex += 1
    }
    return `${current.toFixed(current >= 10 || unitIndex === 0 ? 0 : 1)} ${units[unitIndex]}`
}

function resolveVersionName() {
    if (isAppRuntimeReady() && plus.runtime && plus.runtime.version) {
        return `v${plus.runtime.version}`
    }
    return 'v1.0.0'
}

function getDownloadUrl(appSettings = {}) {
    if (!isAppRuntimeReady() || !plus.os) {
        return appSettings.androidDownloadUrl || appSettings.iosDownloadUrl || ''
    }
    const osName = String(plus.os.name || '').toLowerCase()
    return osName === 'ios'
        ? (appSettings.iosDownloadUrl || '')
        : (appSettings.androidDownloadUrl || '')
}

function getNotificationStatus() {
    if (!isAppRuntimeReady()) {
        return {
            enabled: true,
            label: '跟随浏览器'
        }
    }
    if (plus.os && String(plus.os.name || '').toLowerCase() === 'android' && plus.android) {
        try {
            const main = plus.android.runtimeMainActivity()
            const context = main.getApplicationContext ? main.getApplicationContext() : main
            let NotificationManagerCompat = null
            try {
                NotificationManagerCompat = plus.android.importClass('androidx.core.app.NotificationManagerCompat')
            } catch (error) {
                NotificationManagerCompat = plus.android.importClass('android.support.v4.app.NotificationManagerCompat')
            }
            const enabled = !!NotificationManagerCompat.from(context).areNotificationsEnabled()
            return {
                enabled,
                label: enabled ? '已开启' : '未开启'
            }
        } catch (error) {
        }
    }
    return {
        enabled: true,
        label: '去系统查看'
    }
}

function calculateCacheSize() {
    return new Promise((resolve) => {
        if (!isAppRuntimeReady() || !plus.cache || !plus.cache.calculate) {
            resolve('0 KB')
            return
        }
        plus.cache.calculate((size) => resolve(formatSize(size)))
    })
}

function clearAppCache() {
    return new Promise((resolve, reject) => {
        if (!isAppRuntimeReady() || !plus.cache || !plus.cache.clear) {
            resolve()
            return
        }
        plus.cache.clear(resolve, reject)
    })
}

export default {
    data() {
        return {
            profile: {},
            messageSetting: {
                voiceReadEnabled: false,
                popupEnabled: true,
                marketingEnabled: false
            },
            appSettings: {},
            agreement: {},
            deviceState: {
                notificationEnabled: true,
                notificationLabel: '检测中',
                versionName: 'v1.0.0',
                cacheSizeLabel: '0 KB'
            }
        }
    },
    onShow() {
        this.loadPageData()
    },
    methods: {
        async loadPageData() {
            try {
                const [profile, messageSetting, appSettings, agreement] = await Promise.all([
                    getProfile(),
                    getMessageSetting().catch(() => null),
                    loadPlatformSettings(true).catch(() => getPlatformSettings() || {}),
                    getAgreement().catch(() => ({}))
                ])
                this.profile = profile || {}
                this.messageSetting = messageSetting || this.messageSetting
                this.appSettings = appSettings || {}
                this.agreement = agreement || {}
                await this.refreshDeviceState()
            } catch (error) {
            }
        },
        async refreshDeviceState() {
            const notification = getNotificationStatus()
            const cacheSizeLabel = await calculateCacheSize()
            this.deviceState = {
                notificationEnabled: notification.enabled,
                notificationLabel: notification.label,
                versionName: resolveVersionName(),
                cacheSizeLabel
            }
        },
        async toggleSetting(field, value) {
            const previousValue = this.messageSetting[field]
            const nextValue = {
                ...this.messageSetting,
                [field]: value
            }
            this.messageSetting = nextValue
            try {
                await updateMessageSetting(nextValue)
                notifyReminderSettingChanged()
                if (field === 'popupEnabled' && value) {
                    await this.handleNotificationPermission(false)
                }
                uni.showToast({
                    title: '设置已保存',
                    icon: 'success'
                })
            } catch (error) {
                this.messageSetting = {
                    ...this.messageSetting,
                    [field]: previousValue
                }
            }
        },
        async handleUpdatePassword() {
            if (!this.profile.mobile) {
                uni.showToast({
                    title: '当前账号未绑定手机号',
                    icon: 'none'
                })
                return
            }
            try {
                await sendSmsCode({ mobile: this.profile.mobile })
                const code = await promptInput('请输入短信验证码', '验证码')
                if (!code) {
                    return
                }
                const password = await promptInput('请输入新密码', '4-16 位')
                if (!password) {
                    return
                }
                await updatePassword({
                    password,
                    code
                })
                uni.showToast({
                    title: '密码已更新',
                    icon: 'success'
                })
            } catch (error) {
            }
        },
        handleSocialTip() {
            uni.showToast({
                title: '微信和支付宝绑定暂未开放',
                icon: 'none'
            })
        },
        async handleNotificationPermission(showToast = true) {
            if (!isAppRuntimeReady()) {
                if (showToast) {
                    uni.showToast({
                        title: '请在设备系统设置中管理通知权限',
                        icon: 'none'
                    })
                }
                return
            }
            try {
                if (plus.os && String(plus.os.name || '').toLowerCase() === 'android' && plus.android && plus.android.requestPermissions) {
                    plus.android.requestPermissions(['android.permission.POST_NOTIFICATIONS'], () => {
                        this.refreshDeviceState()
                    }, () => {
                        this.refreshDeviceState()
                    })
                }
            } catch (error) {
            }
            const status = getNotificationStatus()
            if (!status.enabled) {
                uni.showModal({
                    title: '通知权限未开启',
                    content: '打开系统设置后开启通知，订单提醒和设备弹窗才能正常工作。',
                    success: (res) => {
                        if (res.confirm) {
                            this.openSystemSettings()
                        }
                    }
                })
                return
            }
            if (showToast) {
                uni.showToast({
                    title: '通知权限已开启',
                    icon: 'success'
                })
            }
            this.refreshDeviceState()
        },
        openSystemSettings() {
            if (!isAppRuntimeReady()) {
                return
            }
            try {
                if (plus.os && String(plus.os.name || '').toLowerCase() === 'ios') {
                    plus.runtime.openURL('app-settings:')
                    return
                }
                const main = plus.android.runtimeMainActivity()
                const Intent = plus.android.importClass('android.content.Intent')
                const Settings = plus.android.importClass('android.provider.Settings')
                const Uri = plus.android.importClass('android.net.Uri')
                const intent = new Intent()
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                const uri = Uri.fromParts('package', main.getPackageName(), null)
                intent.setData(uri)
                main.startActivity(intent)
            } catch (error) {
            }
        },
        openAgreement(type) {
            const isPrivacy = type === 'privacy'
            const title = isPrivacy ? '隐私政策' : (this.agreement.registerAgreementTitle || '用户协议')
            const content = isPrivacy
                ? (this.agreement.privacyAgreement || '平台暂未配置隐私协议')
                : (this.agreement.registerAgreementContent || this.agreement.serviceAgreement || '平台暂未配置用户协议')
            uni.showModal({
                title,
                content,
                showCancel: false,
                confirmText: '知道了'
            })
        },
        handleVersionUpdate() {
            const downloadUrl = normalizeExternalHttpsUrl(getDownloadUrl(this.appSettings))
            if (!downloadUrl) {
                uni.showToast({
                    title: '下载地址未配置或不安全',
                    icon: 'none'
                })
                return
            }
            if (isAppRuntimeReady() && plus.runtime && plus.runtime.openURL) {
                plus.runtime.openURL(downloadUrl)
                return
            }
            uni.setClipboardData({
                data: downloadUrl,
                success: () => {
                    uni.showToast({
                        title: '下载地址已复制',
                        icon: 'success'
                    })
                }
            })
        },
        clearLocalCache() {
            uni.showModal({
                title: '清理缓存',
                content: '将清理 App 本地缓存文件，不会退出当前登录。',
                success: async (res) => {
                    if (!res.confirm) {
                        return
                    }
                    try {
                        await clearAppCache()
                        await this.refreshDeviceState()
                        uni.showToast({
                            title: '缓存已清理',
                            icon: 'success'
                        })
                    } catch (error) {
                    }
                }
            })
        },
        showAboutUs() {
            uni.showModal({
                title: '关于我们',
                content: this.appSettings.aboutUs || '暂无说明',
                showCancel: false,
                confirmText: '知道了'
            })
        },
        handleLogout() {
            uni.showModal({
                title: '确认退出',
                content: '确定要退出登录吗？',
                success: async (res) => {
                    if (!res.confirm) {
                        return
                    }
                    try {
                        await logout().catch(() => null)
                    } finally {
                        logoutSession()
                        uni.reLaunch({
                            url: '/pages/login/login'
                        })
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
    background:
        radial-gradient(circle at top right, rgba(64, 145, 255, 0.22), transparent 34%),
        linear-gradient(180deg, #eef5ff 0%, #f8fbff 32%, #f4f6f8 100%);
}

.header {
    padding: 72rpx 32rpx 26rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.back-btn,
.placeholder {
    width: 60rpx;
}

.back-icon {
    font-size: 40rpx;
    color: #19324d;
    transform: rotate(180deg);
}

.title {
    font-size: 34rpx;
    font-weight: 700;
    color: #19324d;
}

.content-scroll {
    padding: 0 24rpx 36rpx;
    box-sizing: border-box;
}

.set-card {
    margin-bottom: 22rpx;
    background: rgba(255, 255, 255, 0.96);
    border: 1rpx solid rgba(208, 223, 239, 0.9);
    border-radius: 24rpx;
    overflow: hidden;
    box-shadow: 0 10rpx 30rpx rgba(55, 96, 147, 0.06);
}

.card-header {
    padding: 26rpx 30rpx 14rpx;
}

.header-title {
    font-size: 24rpx;
    font-weight: 700;
    color: #6a7f96;
    letter-spacing: 2rpx;
}

.set-item {
    padding: 24rpx 30rpx;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 18rpx;
    border-top: 1rpx solid #edf3f8;
}

.set-item.clickable:active {
    background: #f6faff;
}

.item-main {
    min-width: 0;
    flex: 1;
    display: flex;
    align-items: center;
    gap: 20rpx;
}

.item-icon {
    width: 34rpx;
    height: 34rpx;
}

.item-copy {
    min-width: 0;
    flex: 1;
}

.item-title {
    display: block;
    font-size: 29rpx;
    color: #20354d;
    font-weight: 600;
}

.item-desc {
    display: block;
    margin-top: 8rpx;
    font-size: 22rpx;
    line-height: 32rpx;
    color: #8a9caf;
}

.item-value {
    max-width: 240rpx;
    text-align: right;
    font-size: 24rpx;
    line-height: 34rpx;
    color: #7d8fa4;
}

.item-value.action {
    color: #2f86f6;
    font-weight: 600;
}

.item-tag {
    padding: 10rpx 18rpx;
    border-radius: 999rpx;
    background: #edf4ff;
    font-size: 22rpx;
    color: #5b8fd8;
}

.disabled-item {
    opacity: 0.92;
}

.status-on {
    color: #20a162;
    font-weight: 600;
}

.status-off {
    color: #e35d4f;
    font-weight: 600;
}

.logout-btn {
    margin-top: 36rpx;
    padding: 28rpx;
    border-radius: 20rpx;
    background: #fff;
    border: 2rpx solid rgba(227, 93, 79, 0.28);
    text-align: center;
    box-shadow: 0 10rpx 24rpx rgba(206, 86, 74, 0.08);
}

.logout-text {
    font-size: 30rpx;
    color: #e35d4f;
    font-weight: 700;
}

.bottom-space {
    height: 56rpx;
}
</style>
