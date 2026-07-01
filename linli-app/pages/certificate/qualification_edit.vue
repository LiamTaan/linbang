<template>
    <view class="page-container">
        <view class="header">
            <view class="back-btn" @click="goBack">
                <text class="iconfont icon-youjiantou back-icon"></text>
            </view>
            <text class="title">{{ pageTitle }}</text>
            <view class="placeholder"></view>
        </view>

        <scroll-view class="content-scroll" scroll-y>
            <view class="form-card">
                <view class="field-item">
                    <text class="field-label">资质名称</text>
                    <input class="field-input" v-model="form.qualificationName" placeholder="请输入资质名称" />
                </view>
                <view class="field-item">
                    <text class="field-label">资质编号</text>
                    <input class="field-input" v-model="form.qualificationNo" placeholder="请输入资质编号" />
                </view>
                <view class="field-item">
                    <text class="field-label">有效开始日期</text>
                    <input class="field-input" v-model="form.validStartDate" placeholder="例如 2026-01-01" />
                </view>
                <view class="field-item">
                    <text class="field-label">有效结束日期</text>
                    <input class="field-input" v-model="form.validEndDate" placeholder="例如 2028-12-31" />
                </view>
            </view>

            <view class="form-card">
                <text class="section-title">资质凭证</text>
                <view class="upload-item" @click="handleMainUpload">
                    <text class="upload-label">主资质附件</text>
                    <text class="upload-value">{{ form.fileId ? '已上传' : '点击上传' }}</text>
                </view>
                <view class="upload-item" @click="handleVideoUpload">
                    <text class="upload-label">补充视频</text>
                    <text class="upload-value">{{ form.videoFileId ? '已上传' : '可选上传' }}</text>
                </view>
                <view class="upload-item" @click="handleEvidenceUpload">
                    <text class="upload-label">补充图片凭证</text>
                    <text class="upload-value">{{ evidenceCountText }}</text>
                </view>
            </view>

            <view class="submit-btn" :class="{ disabled: submitting }" @click="handleSubmit">
                <text class="submit-text">{{ submitting ? '提交中...' : submitText }}</text>
            </view>
            <view class="bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
import { uploadAppFile } from '@/api/infra'
import { createQualification, getQualification, getQualificationPage, updateQualification } from '@/api/member'
import { extractUploadedFile } from '@/utils/linbang'

const TYPE_MAP = {
    business: {
        title: '营业执照',
        qualificationType: 'BUSINESS_LICENSE',
        defaultName: '营业执照'
    },
    industry: {
        title: '行业资质',
        qualificationType: 'ELECTRICIAN',
        defaultName: '行业资质'
    },
    insurance: {
        title: '保险保单',
        qualificationType: 'INSURANCE_POLICY',
        defaultName: '保险保单'
    }
}

export default {
    data() {
        return {
            typeKey: 'business',
            currentDetail: null,
            form: {
                id: null,
                qualificationType: 'BUSINESS_LICENSE',
                qualificationName: '',
                qualificationNo: '',
                fileId: null,
                evidenceFileIdsJson: '[]',
                videoFileId: null,
                validStartDate: '',
                validEndDate: '',
                priorityEnabled: false
            },
            evidenceFileIds: [],
            submitting: false
        }
    },
    computed: {
        currentMeta() {
            return TYPE_MAP[this.typeKey] || TYPE_MAP.business
        },
        pageTitle() {
            return this.currentMeta.title
        },
        submitText() {
            return this.form.id ? '重新提交资质' : '提交资质'
        },
        evidenceCountText() {
            return this.evidenceFileIds.length ? `已上传 ${this.evidenceFileIds.length} 张` : '可选上传'
        }
    },
    onLoad(options) {
        if (options && options.type && TYPE_MAP[options.type]) {
            this.typeKey = options.type
        }
        this.form.qualificationType = this.currentMeta.qualificationType
        this.form.qualificationName = this.currentMeta.defaultName
        if (options && options.bizId) {
            this.loadById(Number(options.bizId))
            return
        }
        this.loadLatestByType()
    },
    methods: {
        async loadLatestByType() {
            try {
                const page = await getQualificationPage({}, { silent: true })
                const list = (page && page.list) || []
                const matched = list.find((item) => item.qualificationType === this.currentMeta.qualificationType)
                    || list.find((item) => this.typeKey === 'industry'
                        && item.qualificationType !== 'BUSINESS_LICENSE'
                        && item.qualificationType !== 'INSURANCE_POLICY')
                if (matched && matched.id) {
                    await this.loadById(matched.id)
                }
            } catch (error) {
            }
        },
        async loadById(id) {
            try {
                const detail = await getQualification(id)
                if (!detail) return
                this.currentDetail = detail
                this.form = {
                    id: detail.id || null,
                    qualificationType: detail.qualificationType || this.currentMeta.qualificationType,
                    qualificationName: detail.qualificationName || this.currentMeta.defaultName,
                    qualificationNo: detail.qualificationNo || '',
                    fileId: detail.fileId || null,
                    evidenceFileIdsJson: detail.evidenceFileIdsJson || '[]',
                    videoFileId: detail.videoFileId || null,
                    validStartDate: detail.validStartDate || '',
                    validEndDate: detail.validEndDate || '',
                    priorityEnabled: !!detail.priorityEnabled
                }
                try {
                    this.evidenceFileIds = JSON.parse(detail.evidenceFileIdsJson || '[]')
                } catch (e) {
                    this.evidenceFileIds = []
                }
            } catch (error) {
            }
        },
        async handleMainUpload() {
            const fileId = await this.uploadSingleImage('linbang/qualification')
            if (fileId) {
                this.form.fileId = fileId
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
                const uploadResp = await uploadAppFile(result.tempFilePath, 'linbang/qualification')
                const file = extractUploadedFile(uploadResp, result.tempFilePath)
                if (file.fileId) {
                    this.form.videoFileId = file.fileId
                }
            } catch (error) {
            }
        },
        async handleEvidenceUpload() {
            try {
                const result = await new Promise((resolve, reject) => {
                    uni.chooseImage({
                        count: 6,
                        success: resolve,
                        fail: reject
                    })
                })
                const paths = result.tempFilePaths || []
                for (let i = 0; i < paths.length; i++) {
                    const uploadResp = await uploadAppFile(paths[i], 'linbang/qualification')
                    const file = extractUploadedFile(uploadResp, paths[i])
                    if (file.fileId) {
                        this.evidenceFileIds.push(file.fileId)
                    }
                }
                this.form.evidenceFileIdsJson = JSON.stringify(this.evidenceFileIds)
            } catch (error) {
            }
        },
        async uploadSingleImage(directory) {
            try {
                const result = await new Promise((resolve, reject) => {
                    uni.chooseImage({
                        count: 1,
                        success: resolve,
                        fail: reject
                    })
                })
                const path = (result.tempFilePaths || [])[0]
                if (!path) return null
                const uploadResp = await uploadAppFile(path, directory)
                const file = extractUploadedFile(uploadResp, path)
                return file.fileId || null
            } catch (error) {
                return null
            }
        },
        async handleSubmit() {
            if (this.submitting) return
            if (!this.form.qualificationName || !this.form.fileId) {
                uni.showToast({
                    title: '请至少填写名称并上传主附件',
                    icon: 'none'
                })
                return
            }
            try {
                this.submitting = true
                const payload = {
                    ...this.form,
                    evidenceFileIdsJson: JSON.stringify(this.evidenceFileIds || [])
                }
                if (this.form.id) {
                    await updateQualification(payload)
                } else {
                    await createQualification(payload)
                }
                uni.showToast({
                    title: '资质已提交',
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
.field-item {
    margin-bottom: 20rpx;
}
.field-label,
.section-title {
    font-size: 28rpx;
    color: #22324A;
    font-weight: 600;
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
.upload-item {
    margin-top: 16rpx;
    min-height: 100rpx;
    background: #F7F9FC;
    border-radius: 14rpx;
    padding: 18rpx;
    display: flex;
    flex-direction: column;
    justify-content: center;
}
.upload-label {
    font-size: 24rpx;
    color: #22324A;
}
.upload-value {
    margin-top: 8rpx;
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
