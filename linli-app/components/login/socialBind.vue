<template>
    <view class="bind-container">
        <view class="header">
            <text class="title">绑定手机号</text>
            <text class="subtitle">完成绑定后即可继续使用第三方登录</text>
        </view>

        <view class="form-card">
            <view class="social-summary" v-if="pendingSocialAuth">
                <image v-if="pendingSocialAuth.socialAvatar" class="avatar" :src="pendingSocialAuth.socialAvatar" />
                <text class="nickname">{{ pendingSocialAuth.socialNickname || '第三方账号' }}</text>
                <text class="hint">已完成授权，请绑定手机号</text>
            </view>

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
                <input class="input-field code-input" type="number" placeholder="请输入验证码" v-model="codeSms" />
                <view class="code-btn" :class="{ disabled: codeCountdown > 0 }" @click="getCode">
                    <text class="btn-text">{{ codeCountdown > 0 ? codeCountdown + 's' : '获取验证码' }}</text>
                </view>
            </view>

            <view class="submit-btn" @click="submit">
                <text class="btn-text">完成绑定并登录</text>
            </view>

            <view class="back-link" @click="$emit('switch-to-login')">
                <text class="back-text">返回登录</text>
            </view>
        </view>
    </view>
</template>

<script>
import { sendSmsCode, socialBindMobile } from '@/api/auth'
import { applyLoginSession, redirectAfterLogin } from '@/services/session'
import { clearPendingSocialAuth, getPendingSocialAuth } from '@/utils/auth'

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
            codeSms: '',
            codeCountdown: 0,
            pendingSocialAuth: null
        }
    },
    created() {
        this.pendingSocialAuth = getPendingSocialAuth()
        if (!this.pendingSocialAuth || !this.pendingSocialAuth.code || !this.pendingSocialAuth.state) {
            uni.showToast({
                title: '请先重新发起第三方登录',
                icon: 'none'
            })
            this.$emit('switch-to-login')
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
            if (this.codeCountdown > 0) {
                return
            }
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
        async submit() {
            if (!this.pendingSocialAuth) {
                uni.showToast({
                    title: '授权信息已失效',
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
            if (!this.codeSms) {
                uni.showToast({
                    title: '请输入验证码',
                    icon: 'none'
                })
                return
            }
            try {
                const loginResp = await socialBindMobile({
                    type: this.pendingSocialAuth.type,
                    code: this.pendingSocialAuth.code,
                    state: this.pendingSocialAuth.state,
                    mobile: this.phone,
                    codeSms: this.codeSms
                })
                await applyLoginSession(loginResp)
                redirectAfterLogin(this.redirect)
            } catch (error) {
                if ((error.message || '').includes('state')) {
                    clearPendingSocialAuth()
                }
            }
        }
    }
}
</script>

<style lang="scss" scoped>
.bind-container {
    background: linear-gradient(180deg, #2e83f0, #eef5ff);
    min-height: 100vh;
    padding: 140rpx 40rpx 40rpx;
    box-sizing: border-box;
}

.header {
    text-align: center;
    margin-bottom: 48rpx;

    .title {
        display: block;
        font-size: 40rpx;
        color: #fff;
        font-weight: bold;
        margin-bottom: 16rpx;
    }

    .subtitle {
        display: block;
        font-size: 26rpx;
        color: rgba(255, 255, 255, 0.88);
    }
}

.form-card {
    background: #fff;
    border-radius: 28rpx;
    padding: 40rpx 32rpx;
}

.social-summary {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 32rpx;

    .avatar {
        width: 112rpx;
        height: 112rpx;
        border-radius: 50%;
        margin-bottom: 16rpx;
    }

    .nickname {
        font-size: 30rpx;
        color: #1f2d3d;
        font-weight: bold;
    }

    .hint {
        margin-top: 8rpx;
        font-size: 24rpx;
        color: #7b8a97;
    }
}

.form-item {
    display: flex;
    align-items: center;
    margin-bottom: 24rpx;
    gap: 20rpx;

    &.phone-item {
        gap: 0;
    }

    &.code-item {
        gap: 20rpx;
    }

    .phone-prefix {
        min-width: 88rpx;
        text-align: center;
    }

    .prefix-text {
        font-size: 28rpx;
        color: #333;
    }

    .input-field {
        flex: 1;
        height: 88rpx;
        border: 2rpx solid #e6ebf1;
        border-radius: 14rpx;
        padding: 0 28rpx;
        font-size: 28rpx;
        color: #333;
        box-sizing: border-box;

        &.code-input {
            min-width: 0;
            background: #fff;
        }

        &.bare {
            border: none;
            background: transparent;
            padding: 0;
        }
    }

    .phone-input-wrap {
        width: 100%;
        height: 88rpx;
        display: flex;
        align-items: center;
        background: #fff;
        border: 2rpx solid #e6ebf1;
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
        font-size: 28rpx;
        font-weight: 600;
        color: #2f3a4a;
    }

    .phone-divider {
        width: 2rpx;
        height: 28rpx;
        background: #d8e1ee;
        margin: 0 24rpx;
    }

    .code-btn {
        height: 88rpx;
        border: 2rpx solid #2e83f0;
        border-radius: 14rpx;
        min-width: 220rpx;
        background: #f8fbff;
        display: flex;
        align-items: center;
        justify-content: center;

        &.disabled {
            border-color: #b7d3f8;
            background: #f5f9ff;

            .btn-text {
                color: #b7d3f8;
            }
        }
    }

    .btn-text {
        font-size: 24rpx;
        font-weight: 500;
        color: #2e83f0;
    }
}

.submit-btn {
    height: 88rpx;
    border-radius: 44rpx;
    background: #2e83f0;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: 36rpx;

    .btn-text {
        font-size: 30rpx;
        color: #fff;
        font-weight: bold;
    }
}

.back-link {
    margin-top: 28rpx;
    text-align: center;

    .back-text {
        font-size: 24rpx;
        color: #2e83f0;
    }
}
</style>
