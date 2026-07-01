<template>
    <view class="page-container">
        <view class="header">
            <view class="back-btn" @click="goBack">
                <text class="iconfont icon-youjiantou back-icon"></text>
            </view>
            <text class="title">实名认证</text>
            <view class="placeholder"></view>
        </view>

        <scroll-view class="content-scroll" scroll-y>
            <view class="form-card">
                <view class="field-item">
                    <text class="field-label">真实姓名</text>
                    <input class="field-input" v-model="form.realName" placeholder="请输入真实姓名" />
                </view>
                <view class="field-item">
                    <text class="field-label">身份证号</text>
                    <input class="field-input" v-model="form.idCardNo" placeholder="请输入身份证号" />
                </view>
                <view class="field-item">
                    <text class="field-label">身份证有效期开始</text>
                    <picker mode="date" :value="form.idCardValidFrom" @change="handleDateChange('idCardValidFrom', $event)">
                        <view class="picker-field" :class="{ placeholder: !form.idCardValidFrom }">
                            {{ form.idCardValidFrom || '请选择开始日期' }}
                        </view>
                    </picker>
                </view>
                <view class="field-item">
                    <text class="field-label">身份证有效期结束</text>
                    <picker mode="date" :value="form.idCardValidEnd" @change="handleDateChange('idCardValidEnd', $event)">
                        <view class="picker-field" :class="{ placeholder: !form.idCardValidEnd }">
                            {{ form.idCardValidEnd || '请选择结束日期' }}
                        </view>
                    </picker>
                </view>
            </view>

            <view class="form-card">
                <text class="section-title">证件上传</text>
                <view class="upload-grid">
                    <view class="upload-item" @click="handleImageUpload('idCardFrontFileId')">
                        <text class="upload-label">身份证正面</text>
                        <text class="upload-value">{{ form.idCardFrontFileId ? '已上传' : '点击上传' }}</text>
                    </view>
                    <view class="upload-item" @click="handleImageUpload('idCardBackFileId')">
                        <text class="upload-label">身份证反面</text>
                        <text class="upload-value">{{ form.idCardBackFileId ? '已上传' : '点击上传' }}</text>
                    </view>
                    <view class="upload-item" @click="handleImageUpload('holdCardFileId')">
                        <text class="upload-label">手持证件照</text>
                        <text class="upload-value">{{ form.holdCardFileId ? '已上传' : '点击上传' }}</text>
                    </view>
                    <view class="upload-item" @click="handleVideoUpload">
                        <text class="upload-label">手持视频</text>
                        <text class="upload-value">{{ form.holdCardVideoFileId ? '已上传' : '点击上传' }}</text>
                    </view>
                </view>
            </view>

            <view class="submit-btn" :class="{ disabled: submitting }" @click="handleSubmit">
                <text class="submit-text">{{ submitting ? '提交中...' : '提交并发起核验' }}</text>
            </view>
            <view class="bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
import { uploadAppFile } from '@/api/infra'
import { createOrUpdateRealName, getRealName, startRealNameVerify } from '@/api/member'
import { extractUploadedFile } from '@/utils/linbang'

export default {
    data() {
        return {
            form: {
                realName: '',
                idCardNo: '',
                idCardFrontFileId: null,
                idCardBackFileId: null,
                holdCardFileId: null,
                holdCardVideoFileId: null,
                idCardValidFrom: '',
                idCardValidEnd: ''
            },
            submitting: false
        }
    },
    onShow() {
        this.loadDetail()
    },
    methods: {
        async loadDetail() {
            try {
                const detail = await getRealName({ silent: true }).catch(() => null)
                if (!detail) return
                this.form = {
                    realName: detail.realName || '',
                    idCardNo: detail.idCardNo || '',
                    idCardFrontFileId: detail.idCardFrontFileId || null,
                    idCardBackFileId: detail.idCardBackFileId || null,
                    holdCardFileId: detail.holdCardFileId || null,
                    holdCardVideoFileId: detail.holdCardVideoFileId || null,
                    idCardValidFrom: detail.idCardValidFrom || '',
                    idCardValidEnd: detail.idCardValidEnd || ''
                }
            } catch (error) {
            }
        },
        async handleImageUpload(field) {
            try {
                const result = await new Promise((resolve, reject) => {
                    uni.chooseImage({
                        count: 1,
                        success: resolve,
                        fail: reject
                    })
                })
                const path = (result.tempFilePaths || [])[0]
                if (!path) return
                const uploadResp = await uploadAppFile(path, 'linbang/realname')
                const file = extractUploadedFile(uploadResp, path)
                if (file.fileId) {
                    this.form[field] = file.fileId
                }
            } catch (error) {
            }
        },
        async handleVideoUpload() {
            try {
                const result = await new Promise((resolve, reject) => {
                    uni.chooseVideo({
                        sourceType: ['album', 'camera'],
                        success: resolve,
                        fail: reject
                    })
                })
                if (!result.tempFilePath) return
                const uploadResp = await uploadAppFile(result.tempFilePath, 'linbang/realname')
                const file = extractUploadedFile(uploadResp, result.tempFilePath)
                if (file.fileId) {
                    this.form.holdCardVideoFileId = file.fileId
                }
            } catch (error) {
            }
        },
        async handleSubmit() {
            if (this.submitting) return
            if (!this.form.realName || !this.form.idCardNo || !this.form.idCardFrontFileId || !this.form.idCardBackFileId
                || !this.form.holdCardFileId || !this.form.holdCardVideoFileId || !this.form.idCardValidFrom || !this.form.idCardValidEnd) {
                uni.showToast({
                    title: '请完整填写并上传实名资料',
                    icon: 'none'
                })
                return
            }
            try {
                this.submitting = true
                await createOrUpdateRealName({ ...this.form })
                await startRealNameVerify({})
                uni.showToast({
                    title: '实名认证已提交',
                    icon: 'success'
                })
                setTimeout(() => {
                    uni.navigateBack()
                }, 600)
            } catch (error) {
            } finally {
                this.submitting = false
            }
        },
        handleDateChange(field, event) {
            this.form[field] = event.detail.value || ''
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
    background: #F5F7FB;
}
.header {
    background: #fff;
    padding: 60rpx 30rpx 30rpx;
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.back-icon {
    font-size: 40rpx;
    color: #333;
    transform: rotate(180deg);
}
.title {
    font-size: 32rpx;
    color: #222;
    font-weight: 500;
}
.content-scroll {
    padding: 20rpx;
    box-sizing: border-box;
}
.form-card {
    background: #fff;
    border-radius: 18rpx;
    padding: 24rpx;
    margin-bottom: 20rpx;
}
.section-title,
.field-label {
    font-size: 28rpx;
    color: #22324A;
    font-weight: 600;
}
.field-item {
    margin-bottom: 20rpx;
}
.field-input {
    margin-top: 10rpx;
    height: 84rpx;
    padding: 0 24rpx;
    border-radius: 14rpx;
    background: #F7F9FC;
    font-size: 26rpx;
    color: #22324A;
}
.picker-field {
    margin-top: 10rpx;
    height: 84rpx;
    padding: 0 24rpx;
    border-radius: 14rpx;
    background: #F7F9FC;
    font-size: 26rpx;
    color: #22324A;
    display: flex;
    align-items: center;
}
.picker-field.placeholder {
    color: #9AA6B2;
}
.upload-grid {
    margin-top: 18rpx;
    display: flex;
    flex-wrap: wrap;
    gap: 16rpx;
}
.upload-item {
    width: calc(50% - 8rpx);
    min-height: 120rpx;
    background: #F7F9FC;
    border-radius: 14rpx;
    padding: 18rpx;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    justify-content: center;
}
.upload-label {
    font-size: 24rpx;
    color: #22324A;
}
.upload-value {
    margin-top: 10rpx;
    font-size: 22rpx;
    color: #2E83F0;
}
.submit-btn {
    height: 92rpx;
    border-radius: 46rpx;
    background: #2E83F0;
    display: flex;
    align-items: center;
    justify-content: center;
}
.submit-btn.disabled {
    opacity: 0.7;
}
.submit-text {
    font-size: 30rpx;
    color: #fff;
    font-weight: 600;
}
.bottom-space {
    height: 60rpx;
}
</style>
