<template>
    <view class="login-container">
        <!-- #ifdef MP-WEIXIN -->
        <view class="mini-program-login">
            <view class="mini-program-login-header">
                <view class="mini-program-avatar">
                    <image class="mini-program-avatar-img" src="/static/img/login/anonymity.png" mode="aspectFill" />
                </view>
                <text class="mini-program-title">欢迎回来</text>
                <text class="mini-program-subtitle">登录后即可享受便民服务</text>
            </view>
            <view class="mini-program-login-actions">
                <button class="wechat-phone-login-btn" open-type="getPhoneNumber"
                    :disabled="wechatPhoneLoading" @getphonenumber="loginByWechatMiniProgramPhone">
                    <text class="btn-text">{{ wechatPhoneLoading ? '登录中...' : '微信手机号授权登录' }}</text>
                </button>
            </view>
        </view>
        <!-- #endif -->

        <!-- #ifndef MP-WEIXIN -->
        <view class="header">
            <view class="avatar">
                <image class="avatar-img" src="/static/img/login/anonymity.png" />
            </view>
            <view class="welcome-text">
                <view class="title">欢迎回来</view>
                <view class="subtitle">登录后即可享受便民服务</view>
            </view>
        </view>

        <view class="form-card">
            <view class="form-card-item">
                <view class="form-item phone-item">
                    <view class="phone-input-wrap">
                        <view class="phone-region">
                            <text class="region-text">+86</text>
                        </view>
                        <view class="phone-divider"></view>
                        <input class="input-field bare" type="number" placeholder="请输入手机号" v-model="phone" />
                    </view>
                </view>

                <view class="form-item code-item">
                    <input class="input-field code-input" type="number" placeholder="请输入验证码" v-model="code" />
                    <view class="code-btn" :class="{ disabled: codeCountdown > 0 }" @click="getCode">
                        <text class="btn-text">{{ codeCountdown > 0 ? codeCountdown + 's' : '获取验证码' }}</text>
                    </view>
                </view>

                <view class="login-btn" @click="login">
                    <text class="btn-text">登录</text>
                </view>
            </view>

            <view class="register-link" @click="switchToRegister">
                <text class="link-text">还没有账号？</text>
                <text class="register-text">立即注册</text>
            </view>
        </view>
        <!-- #endif -->
    </view>
</template>

<script>
import { loginBySms, loginByWechatMiniProgramPhone, sendSmsCode } from '@/api/auth'
import { applyLoginSession, redirectAfterLogin } from '@/services/session'

export default {
    props: {
        redirect: {
            type: String,
            default: ''
        }
    },
    data() {
        return {
            phone: '',
            code: '',
            codeCountdown: 0,
            wechatPhoneLoading: false
        }
    },
    methods: {
        async loginByWechatMiniProgramPhone(event) {
            const phoneCode = event && event.detail && event.detail.code
            if (!phoneCode) {
                uni.showToast({
                    title: '未完成微信手机号授权',
                    icon: 'none'
                })
                return
            }
            if (this.wechatPhoneLoading) return
            this.wechatPhoneLoading = true
            try {
                const loginCode = await new Promise((resolve, reject) => {
                    uni.login({
                        provider: 'weixin',
                        success: ({ code }) => code ? resolve(code) : reject(new Error('未获取到微信登录凭证')),
                        fail: reject
                    })
                })
                const loginResp = await loginByWechatMiniProgramPhone({ phoneCode, loginCode })
                await applyLoginSession(loginResp)
                redirectAfterLogin(this.redirect)
            } catch (error) {
            } finally {
                this.wechatPhoneLoading = false
            }
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
        async login() {
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
            try {
                const loginResp = await loginBySms({
                    mobile: this.phone,
                    code: this.code
                })
                await applyLoginSession(loginResp)
                redirectAfterLogin(this.redirect)
            } catch (error) {}
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

    .mini-program-login {
        position: relative;
        width: 100vw;
        height: 100vh;
        overflow: hidden;
        background: linear-gradient(180deg, #2E83F0 0%, #78B1F8 46%, #FFFFFF 100%);
    }

    .mini-program-login-header {
        position: absolute;
        left: 0;
        right: 0;
        top: calc(env(safe-area-inset-top) + 150rpx);
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    .mini-program-avatar {
        width: 160rpx;
        height: 160rpx;
        margin-bottom: 30rpx;
        overflow: hidden;
        border: 6rpx solid rgba(255, 255, 255, 0.5);
        border-radius: 30rpx;
    }

    .mini-program-avatar-img {
        width: 100%;
        height: 100%;
    }

    .mini-program-title {
        font-size: 36rpx;
        font-weight: 700;
        line-height: 1.4;
        color: #FFFFFF;
        text-shadow: 0 5rpx 7rpx #2B74D6;
    }

    .mini-program-subtitle {
        margin-top: 8rpx;
        font-size: 25rpx;
        line-height: 1.5;
        color: #FFFFFF;
    }

    .mini-program-login-actions {
        position: absolute;
        left: 64rpx;
        right: 64rpx;
        top: 58%;
        transform: translateY(-50%);
    }

    .mini-program-login-actions .wechat-phone-login-btn {
        width: 100%;
        height: 96rpx;
        margin: 0;
        border: 0;
        border-radius: 14rpx;
        background: #07C160;
        color: #FFFFFF;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 12rpx 28rpx rgba(17, 92, 68, 0.22);

        &::after {
            border: 0;
        }

        &[disabled] {
            background: #07C160;
            color: #FFFFFF;
            opacity: 0.72;
        }

        .btn-text {
            font-size: 30rpx;
            font-weight: 500;
        }
    }

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
            // background: #FFFFFF;
            // box-shadow: 0rpx 1rpx 7rpx 0rpx #E7E7E7;
			display: flex;
			align-items: center; 
            border-radius: 30rpx;
            padding: 44rpx 36rpx 52rpx;
            min-height: 520rpx;
            box-sizing: border-box;
        }

        .form-item {
            display: flex;
            align-items: center;
            padding: 0 24rpx;
            height: 96rpx;
            margin-bottom: 28rpx;
            border-radius: 14rpx;

            &.phone-item {
                padding: 0;
            }

            &.code-item {
                padding: 0;
                gap: 20rpx;
            }

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

                &.code-input {
                    background: #FFFFFF;
                    border: 2rpx solid #E2E8F3;
                    min-width: 0;
                }

                &.bare {
                    border: none;
                    background: transparent;
                    padding: 0;
                }
            }

            .phone-input-wrap {
                width: 100%;
                height: 100%;
                display: flex;
                align-items: center;
                background: #FFFFFF;
                border: 2rpx solid #E2E8F3;
                border-radius: 14rpx;
                padding: 0 28rpx;
                box-sizing: border-box;
            }

            .phone-region {
                min-width: 72rpx;
                display: flex;
                align-items: center;
                justify-content: center;
            }

            .region-text {
                font-family: Microsoft YaHei;
                font-weight: 600;
                font-size: 28rpx;
                color: #2F3A4A;
            }

            .phone-divider {
                width: 2rpx;
                height: 28rpx;
                background: #D8E1EE;
                margin: 0 24rpx;
            }

            .code-btn {
                border-radius: 14rpx;
                border: 2rpx solid #2E83F0;
                height: 100%;
                display: flex;
                align-items: center;
                justify-content: center;
                min-width: 220rpx;
                background: #F8FBFF;

                &.disabled {
                    border-color: #B7D3F8;
                    background: #F5F9FF;

                    .btn-text {
                        color: #B7D3F8;
                    }
                }

                .btn-text {
                    padding: 0 28rpx;
                    font-family: Microsoft YaHei;
                    font-weight: 500;
                    font-size: 24rpx;
                    color: #2E83F0;
                }
            }
        }

        .login-btn {
            background: #4A90D9;
            border-radius: 44rpx;
            height: 96rpx;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-top: 52rpx;

            .btn-text {
                font-size: 32rpx;
                color: #fff;
                font-weight: bold;
            }
        }

        .register-link {
            margin-top: 60rpx;
            text-align: center;
            font-family: Microsoft YaHei;
            font-weight: 400;
            font-size: 25rpx;
            color: #2E83F0;
        }
    }
}
</style>
