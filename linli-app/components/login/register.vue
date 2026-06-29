<template>
    <view class="login-container">
        <view class="header">
            <view class="header-content">
                <view class="title">加入邻里互助</view>
                <view class="subtitle">享受本地便民服务</view>
            </view>
        </view>

        <view class="form-card">
            <view class="greeting">
                <view class="hello">Hello!</view>
                <view class="welcome">欢迎来到邻里互助</view>
            </view>

            <view class="form-item">
                <input class="input-field" placeholder="请输入用户名" v-model="username" />
            </view>

            <view class="form-item">
                <view class="phone-prefix">
                    <text class="prefix-text">+86</text>
                </view>
                <input class="input-field" type="number" placeholder="请输入手机号" v-model="phone" />
            </view>

            <view class="form-item">
                <input class="input-field small" type="number" placeholder="请输入验证码" v-model="code" />
                <view class="code-btn" :class="{ disabled: codeCountdown > 0 }" @click="getCode">
                    <view class="btn-text">{{ codeCountdown > 0 ? `${codeCountdown}s后重试` : '获取验证码' }}</view>
                </view>
            </view>

            <view class="form-item password">
                <input class="input-field" type="password" placeholder="请设置6-20位密码" v-model="password"
                    :password="!showPassword" />
            </view>

            <view class="form-item password">
                <input class="input-field" type="password" placeholder="请再次确认密码" v-model="confirmPassword"
                    :password="!showConfirmPassword" />

            </view>

            <view class="agreement">
                <view class="agreement-item" @click="toggleAgree">
                    <view class="checkbox" :class="{ checked: agreed }">
                        <image v-if="agreed" class="check-icon" src="/static/img/login/pitch_on.png" />
                    </view>
                    <text class="agreement-text">我已同意并阅读</text>
                    <text class="link">《用户协议》</text>
                    <text class="agreement-text">和</text>
                    <text class="link">《隐私政策》</text>
                </view>
            </view>

            <view class="register-btn" @click="register">
                <text class="btn-text">注册</text>
            </view>

            <view class="or-line">
                <text class="or-text">或使用以下方式注册</text>
            </view>

            <view class="social-btns">
                <view class="social-btn" @click="loginWithWechat">
                    <image class="social-icon" src="/static/img/login/WeChat.png" />
                </view>
                <view class="social-btn" @click="loginWithAlipay">
                    <image class="social-icon" src="/static/img/login/Alipay.png" />
                </view>
            </view>

            <view class="login-link" @click="switchToLogin">
                <text class="link-text">已有账号？</text>
                <text class="login-text">去登陆</text>
            </view>
        </view>
    </view>
</template>

<script>
import { accountRegister, sendSmsCode } from '@/api/auth'
import { applyLoginSession, redirectAfterLogin } from '@/services/session'
import { getAgreement } from '@/api/platform'
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
            username: '',
            phone: '',
            code: '',
            password: '',
            confirmPassword: '',
            agreed: false,
            showPassword: false,
            showConfirmPassword: false,
            codeCountdown: 0,
            agreement: null
        }
    },
    async created() {
        this.agreement = await getAgreement().catch(() => null)
    },
    methods: {
        toggleAgree() {
            this.agreed = !this.agreed
        },
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
        async register() {
            if (!this.username) {
                uni.showToast({
                    title: '请输入用户名',
                    icon: 'none'
                })
                return
            }
            if (!this.phone || this.phone.length !== 11) {
                uni.showToast({
                    title: '请输入正确的手机号',
                    icon: 'none'
                })
                return
            }
            if (!this.code) {
                uni.showToast({
                    title: '请输入验证码',
                    icon: 'none'
                })
                return
            }
            if (!this.password || this.password.length < 6 || this.password.length > 20) {
                uni.showToast({
                    title: '请设置6-20位密码',
                    icon: 'none'
                })
                return
            }
            if (this.password !== this.confirmPassword) {
                uni.showToast({
                    title: '两次输入的密码不一致',
                    icon: 'none'
                })
                return
            }
            if (!this.agreed) {
                uni.showToast({
                    title: '请先同意用户协议',
                    icon: 'none'
                })
                return
            }
            try {
                const loginResp = await accountRegister({
                    username: this.username,
                    mobile: this.phone,
                    smsCode: this.code,
                    password: this.password,
                    accountType: 'PERSONAL',
                    agreementVersion: this.agreement ? this.agreement.registerAgreementVersion : 'v2026.06',
                    agreementConfirmed: this.agreed
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
                    uni.redirectTo({
                        url: `/pages/login/login?mode=social-bind&redirect=${encodeURIComponent(this.redirect || '')}`
                    })
                }
            } catch (error) {}
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
            } catch (error) {}
        },
        switchToLogin() {
            this.$emit('switch-to-login')
        }
    }
}
</script>

<style lang="scss" scoped>
.login-container {
    background: linear-gradient(110deg, #3386F1, #A2B6FF);
    min-height: 100vh;
    display: flex;
    flex-direction: column;

    .header {
        padding: 180rpx 60rpx 100rpx;
        text-align: center;

        .header-content {
            font-family: Microsoft YaHei;
            font-weight: bold;
            font-size: 48rpx;
            color: #FFFFFF;
            text-shadow: 0rpx 6rpx 0rpx #155FBF;
            text-align: left;

            .title {
                margin-bottom: 20rpx;
            }
        }
    }

    .form-card {
        background: #fff;
        border-radius: 40rpx 40rpx 0 0;
        padding: 60rpx 40rpx;
        flex: 1;

        .greeting {
            margin-bottom: 60rpx;

            .hello {
                font-family: Comic Sans MS;
                font-weight: 400;
                font-size: 48rpx;
                color: #333333;
            }

            .welcome {
                font-family: Microsoft YaHei;
                font-weight: 400;
                font-size: 30rpx;
                color: #333333;
            }
        }

        .form-item {
            display: flex;
            align-items: center;
            padding: 0 24rpx;
            height: 88rpx;
            margin-bottom: 24rpx;

            &.password {
                position: relative;

                .eye-icon {
                    position: absolute;
                    right: 24rpx;
                    width: 40rpx;
                    height: 40rpx;
                }
            }

            .phone-prefix {
                padding-right: 20rpx;
                margin-right: 20rpx;

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
                background: #F5F5F5;
                border-radius: 14rpx;
                padding: 0 30rpx;

                &.small {
                    flex: 1;
                }
            }

            .code-btn {
                border-radius: 14rpx;
                border: 2rpx solid #2E83F0;
                height: 100%;
                display: flex;
                align-items: center;
                justify-content: center;
                margin-left: 50rpx;

                &.disabled {
                    border-color: #B7D3F8;

                    .btn-text {
                        color: #B7D3F8;
                    }
                }

                .btn-text {
                    padding: 0 56rpx;
                    font-family: Microsoft YaHei;
                    font-weight: 400;
                    font-size: 24rpx;
                    color: #2E83F0;
                }
            }
        }

        .agreement {
            display: flex;
            align-items: center;
            margin-bottom: 40rpx;
            flex-wrap: wrap;
            padding: 0 24rpx;

            .agreement-item {
                display: flex;
                align-items: center;
                justify-content: center;
            }

            .checkbox {
                width: 36rpx;
                height: 36rpx;
                border: 2rpx solid #DDD;
                border-radius: 50%;
                margin-right: 12rpx;
                display: flex;
                align-items: center;
                justify-content: center;

                &.checked {
                    border: 2rpx solid #2E83F0;
                }

                .check-icon {
                    width: 100%;
                    height: 100%;
                }
            }

            .agreement-text {
                font-size: 24rpx;
                color: #999;
            }

            .link {
                font-size: 24rpx;
                color: #2E83F0;
                margin: 0 4rpx;
                text-decoration: underline;
            }
        }

        .register-btn {
            background: #2E83F0;
            border-radius: 44rpx;
            height: 88rpx;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 40rpx;
            padding: 0 24rpx;

            .btn-text {
                font-size: 32rpx;
                color: #fff;
                font-weight: bold;
            }
        }

        .or-line {
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

        .login-link {
            text-align: center;
            font-family: Microsoft YaHei;
            font-weight: 400;
            font-size: 25rpx;
            color: #2E83F0;
        }
    }
}
</style>
