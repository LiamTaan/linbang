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
                <view class="set-item" @click="handleUpdatePassword">
                    <image class="item-icon" src="/static/img/set/password@3x.png" />
                    <text class="item-title">修改密码</text>
                    <text class="item-value action">发送验证码并修改</text>
                </view>
                <view class="set-item">
                    <image class="item-icon" src="/static/img/set/phone@3x.png" />
                    <text class="item-title">绑定手机</text>
                    <text class="item-value">{{ $fmt.maskMobile(profile.mobile) || '未绑定' }}</text>
                </view>
                <view class="set-item" @click="handleWechatTip">
                    <image class="item-icon" src="/static/img/set/wechat@3x.png" />
                    <text class="item-title">社交登录</text>
                    <text class="item-value action">微信/支付宝可在登录页发起</text>
                </view>
            </view>

            <view class="set-card">
                <view class="card-header">
                    <text class="header-title">消息设置</text>
                </view>
                <view class="set-item">
                    <image class="item-icon" src="/static/img/set/order@3x.png" />
                    <text class="item-title">语音朗读</text>
                    <switch :checked="messageSetting.voiceReadEnabled" color="#4A90F0" @change="toggleSetting('voiceReadEnabled', $event.detail.value)" />
                </view>
                <view class="set-item">
                    <image class="item-icon" src="/static/img/set/fund@3x.png" />
                    <text class="item-title">弹窗提醒</text>
                    <switch :checked="messageSetting.popupEnabled" color="#4A90F0" @change="toggleSetting('popupEnabled', $event.detail.value)" />
                </view>
                <view class="set-item">
                    <image class="item-icon" src="/static/img/set/message@3x.png" />
                    <text class="item-title">营销消息</text>
                    <switch :checked="messageSetting.marketingEnabled" color="#4A90F0" @change="toggleSetting('marketingEnabled', $event.detail.value)" />
                </view>
            </view>

            <view class="set-card">
                <view class="card-header">
                    <text class="header-title">平台信息</text>
                </view>
                <view class="set-item">
                    <image class="item-icon" src="/static/img/set/about@3x.png" />
                    <text class="item-title">客服电话</text>
                    <text class="item-value">{{ appSettings.serviceHotline || '--' }}</text>
                </view>
                <view class="set-item" @click="copyWechat">
                    <image class="item-icon" src="/static/img/set/wechat@3x.png" />
                    <text class="item-title">客服微信</text>
                    <text class="item-value action">{{ appSettings.serviceWechat || '未配置' }}</text>
                </view>
                <view class="set-item">
                    <image class="item-icon" src="/static/img/set/update@3x.png" />
                    <text class="item-title">版本更新</text>
                    <text class="item-value">v1.0.0</text>
                </view>
                <view class="set-item">
                    <image class="item-icon" src="/static/img/set/cache@3x.png" />
                    <text class="item-title">关于我们</text>
                    <text class="item-value about">{{ appSettings.aboutUs || '暂无说明' }}</text>
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
import { getPlatformSettings } from '@/utils/auth'
import { logoutSession } from '@/services/session'

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

export default {
    data() {
        return {
            profile: {},
            messageSetting: {
                voiceReadEnabled: false,
                popupEnabled: true,
                marketingEnabled: false
            },
            appSettings: {}
        }
    },
    onShow() {
        this.loadPageData()
    },
    methods: {
        async loadPageData() {
            try {
                const [profile, messageSetting] = await Promise.all([
                    getProfile(),
                    getMessageSetting().catch(() => null)
                ])
                this.profile = profile || {}
                this.messageSetting = messageSetting || this.messageSetting
                this.appSettings = getPlatformSettings() || {}
            } catch (error) {
            }
        },
        async toggleSetting(field, value) {
            const nextValue = {
                ...this.messageSetting,
                [field]: value
            }
            this.messageSetting = nextValue
            try {
                await updateMessageSetting(nextValue)
            } catch (error) {
                this.loadPageData()
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
        handleWechatTip() {
            uni.showToast({
                title: '微信和支付宝授权请在登录页发起',
                icon: 'none'
            })
        },
        copyWechat() {
            if (!this.appSettings.serviceWechat) {
                return
            }
            uni.setClipboardData({
                data: this.appSettings.serviceWechat
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
            uni.navigateBack()
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

        .set-card {
            background: #fff;
            border-radius: 16rpx;
            margin-bottom: 20rpx;
            overflow: hidden;
        }

        .card-header {
            padding: 24rpx 32rpx;
            border-bottom: 1rpx solid #F0F0F0;

            .header-title {
                font-size: 26rpx;
                color: #999;
            }
        }

        .set-item {
            padding: 24rpx 32rpx;
            display: flex;
            align-items: center;
            border-bottom: 1rpx solid #F0F0F0;
            gap: 20rpx;

            &:last-child {
                border-bottom: none;
            }

            .item-icon {
                width: 30rpx;
                height: 30rpx;
            }

            .item-title {
                flex: 1;
                font-size: 28rpx;
                color: #333;
            }

            .item-value {
                font-size: 24rpx;
                color: #999;
                max-width: 340rpx;
                text-align: right;

                &.action {
                    color: #4A90F0;
                }

                &.about {
                    white-space: normal;
                    line-height: 1.5;
                }
            }
        }

        .logout-btn {
            margin-top: 40rpx;
            padding: 28rpx;
            border: 2rpx solid #E53935;
            border-radius: 12rpx;
            text-align: center;

            .logout-text {
                font-size: 30rpx;
                color: #E53935;
                font-weight: bold;
            }
        }

        .bottom-space {
            height: 60rpx;
        }
    }
}
</style>
