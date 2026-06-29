<template>
    <view class="login-container">
        <view class="header">
            <view class="avatar">
                <image class="avatar-img" src="/static/logo.png" />
            </view>
            <view class="welcome-text">
                <view class="title">欢迎回来</view>
                <view class="subtitle">登录后即可享受便民服务</view>
            </view>
        </view>

        <view class="form-card">
            <view class="form-card-item">
                <view class="tabs">
                    <view class="tab" :class="{ active: loginType === 'phone' }" @click="loginType = 'phone'">
                        <text class="tab-text">手机号登录</text>
                    </view>
                    <view class="tab" :class="{ active: loginType === 'password' }" @click="loginType = 'password'">
                        <text class="tab-text">密码登录</text>
                    </view>
                </view>

                <view class="form-item">
                    <view class="phone-prefix">
                        <text class="prefix-text">+86</text>
                    </view>
                    <input class="input-field" type="number" placeholder="请输入手机号" v-model="phone" />
                </view>

                <view v-if="loginType === 'phone'" class="form-item">
                    <view class="phone-prefix">
                        <text class="prefix-text">验证码</text>
                    </view>
                    <input class="input-field small" type="number" placeholder="验证码" v-model="code" />
                    <view class="code-btn" @click="getCode">
                        <text class="btn-text">{{ codeCountdown > 0 ? codeCountdown + 's' : '获取验证码' }}</text>
                    </view>
                </view>
                <view v-if="loginType === 'password'" class="form-item password">
                    <input class="input-field" type="password" placeholder="请输入密码" v-model="password" />
                </view>

                <view class="login-btn" @click="login">
                    <text class="btn-text">登录</text>
                </view>
            </view>


            <view class="or-line">
                <view class="line"></view>
                <text class="or-text">其他方式登录</text>
                <view class="line"></view>
            </view>

            <view class="social-btns">
                <view class="social-btn" @click="loginWithWechat">
                    <image class="social-icon" src="/static/img/login/WeChat.png" />
                </view>
                <view class="social-btn" @click="loginWithAlipay">
                    <image class="social-icon" src="/static/img/login/Alipay.png" />
                </view>
                <view class="social-btn">
                    <image class="social-icon" src="/static/img/login/anonymity.png" />
                </view>
            </view>

            <view class="register-link" @click="switchToRegister">
                <text class="link-text">还没有账号？</text>
                <text class="register-text">立即注册</text>
            </view>
        </view>
    </view>
</template>

<script>
import { accountLogin, loginBySms, sendSmsCode } from '@/api/auth'
import { applyLoginSession, redirectAfterLogin } from '@/services/session'
import { finishSocialLogin, SOCIAL_TYPES, startSocialAuthorize } from '@/utils/social'

export default {
    props: {
        redirect: {
            type: String,
            default: ''
        }
    },
    data() {
        return {
            loginType: 'phone',
            phone: '',
            code: '',
            password: '',
            codeCountdown: 0
        }
    },
    methods: {
        async getCode() {
            if (!this.phone || this.phone.length !== 11) {
                uni.showToast({
                    title: '请输入正确的手机号',
                    icon: 'none'
                })
                return
            }
            if (this.codeCountdown > 0) return
            try {
                await sendSmsCode({
                    mobile: this.phone
                })
                this.codeCountdown = 60
                const timer = setInterval(() => {
                    this.codeCountdown--
                    if (this.codeCountdown <= 0) {
                        clearInterval(timer)
                    }
                }, 1000)
            } catch (error) {}
        },
        async login() {
            if (this.loginType === 'phone' && (!this.phone || this.phone.length !== 11)) {
                uni.showToast({
                    title: '请输入正确的手机号',
                    icon: 'none'
                })
                return
            }
            if (this.loginType === 'phone' && !this.code) {
                uni.showToast({
                    title: '请输入验证码',
                    icon: 'none'
                })
                return
            }
            if (this.loginType === 'password' && !this.password) {
                uni.showToast({
                    title: '请输入密码',
                    icon: 'none'
                })
                return
            }
            try {
                const loginResp = this.loginType === 'phone'
                    ? await loginBySms({
                        mobile: this.phone,
                        code: this.code
                    })
                    : await accountLogin({
                        account: this.phone,
                        password: this.password
                    })
                await applyLoginSession(loginResp)
                redirectAfterLogin(this.redirect)
            } catch (error) {}
        },
        async loginWithWechat() {
            try {
                const authPayload = await startSocialAuthorize(SOCIAL_TYPES.WECHAT)
                if (authPayload.manualCallback) {
                    uni.showToast({
                        title: '请在授权完成后返回 App',
                        icon: 'none'
                    })
                    return
                }
                const result = await finishSocialLogin(authPayload, this.redirect)
                if (result.bindRequired) {
                    this.$emit('switch-to-social-bind')
                }
            } catch (error) {
                uni.showToast({
                    title: '微信登录暂不可用',
                    icon: 'none'
                })
            }
        },
        async loginWithAlipay() {
            try {
                const authPayload = await startSocialAuthorize(SOCIAL_TYPES.ALIPAY)
                if (authPayload.manualCallback) {
                    uni.showToast({
                        title: '已打开支付宝授权，请完成后返回',
                        icon: 'none'
                    })
                }
            } catch (error) {
                uni.showToast({
                    title: '支付宝登录暂不可用',
                    icon: 'none'
                })
            }
        },
        switchToRegister() {
            this.$emit('switch-to-register')
        }
    }
}
</script>

<style lang="scss" scoped>
.login-container {
    background: linear-gradient(180deg, #2E83F0, #FFFFFF);
    min-height: 100vh;
    display: flex;
    flex-direction: column;

    .header {
        padding: 180rpx 60rpx 80rpx;
        display: flex;
        flex-direction: column;
        align-items: center;

        .avatar {
            width: 160rpx;
            height: 160rpx;
            border-radius: 30rpx;
            border: 6rpx solid rgba(255, 255, 255, 0.5);
            margin-bottom: 30rpx;
            overflow: hidden;

            .avatar-img {
                width: 100%;
                height: 100%;
            }
        }

        .welcome-text {
            text-align: center;

            .title {
                font-family: Microsoft YaHei;
                font-weight: bold;
                font-size: 36rpx;
                color: #FFFFFF;
                text-shadow: 0rpx 5rpx 7rpx #2B74D6;
            }

            .subtitle {
                font-family: Microsoft YaHei;
                font-weight: 400;
                font-size: 25rpx;
                color: #FFFFFF;
            }
        }
    }

    .form-card {
        padding: 60rpx 40rpx;
        flex: 1;
        padding-top: 0;

        .form-card-item {
            background: #FFFFFF;
            box-shadow: 0rpx 1rpx 7rpx 0rpx #E7E7E7;
            border-radius: 30rpx;
            padding: 36rpx;
        }

        .tabs {
            display: flex;
            background: #F5F5F5;
            border-radius: 12rpx;
            padding: 8rpx;
            margin-bottom: 40rpx;

            .tab {
                flex: 1;
                height: 72rpx;
                display: flex;
                align-items: center;
                justify-content: center;
                border-radius: 10rpx;
                transition: all 0.3s;

                &.active {
                    background: #4A90D9;

                    .tab-text {
                        color: #fff;
                    }
                }

                .tab-text {
                    font-size: 28rpx;
                    color: #666;
                }
            }
        }

        .form-item {
            display: flex;
            align-items: center;
            padding: 0 24rpx;
            height: 88rpx;
            margin-bottom: 24rpx;
            border-radius: 14rpx;

            .phone-prefix {
                // padding-right: 20rpx;
                margin-right: 20rpx;
                width: 20%;

                .prefix-text {
                    font-family: Microsoft YaHei;
                    font-weight: 400;
                    font-size: 30rpx;
                    color: #333333;
                }
            }

            .input-field {
                flex: 1;
                height: 100%;
                font-size: 28rpx;
                color: #333;
                border: 2rpx solid #E2E2E2;
                padding: 0 36rpx;
                border-radius: 14rpx;

                &.small {
                    flex: 1;
                }
            }

            .code-btn {
                border-radius: 14rpx;
                border: 1rpx solid #2E83F0;
                height: 100%;
                display: flex;
                align-items: center;
                justify-content: center;
                margin-left: 20rpx;

                .btn-text {
                    padding: 0 36rpx;
                    font-family: Microsoft YaHei;
                    font-weight: 400;
                    font-size: 24rpx;
                    color: #2E83F0;
                }
            }
        }

        .login-btn {
            background: #4A90D9;
            border-radius: 44rpx;
            height: 88rpx;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-top: 40rpx;

            .btn-text {
                font-size: 32rpx;
                color: #fff;
                font-weight: bold;
            }
        }

        .or-line {
            margin-top: 70rpx;
            margin-bottom: 40rpx;
            display: flex;
            align-items: center;
            margin-bottom: 1.25rem;
            justify-content: center;

            .or-text {
                font-family: Microsoft YaHei;
                font-weight: 400;
                font-size: 25rpx;
                color: #999999;
            }
        }

        .social-btns {
            display: flex;
            justify-content: center;
            gap: 80rpx;
            margin-bottom: 60rpx;

            .social-btn {
                width: 49rpx;
                height: 39rpx;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;

                .social-icon {
                    width: 100%;
                    height: 100%;
                }
            }
        }

        .register-link {
            text-align: center;
            font-family: Microsoft YaHei;
            font-weight: 400;
            font-size: 25rpx;
            color: #2E83F0;
        }
    }
}
</style>
