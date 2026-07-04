<template>
    <view class="page-container">
        <view class="header">
            <view class="back-btn" @click="goBack">
                <text class="iconfont icon-youjiantou back-icon"></text>
            </view>
            <text class="title">行业资质</text>
            <view class="header-action" @click="handleCreate">
                <text class="header-action-text">新增</text>
            </view>
        </view>

        <scroll-view class="content-scroll" scroll-y>
            <view v-if="!industryQualifications.length" class="empty-card">
                <text class="empty-title">暂无行业资质</text>
                <text class="empty-desc">可按真实业务连续维护多本证件，不再限制为单一资质。</text>
                <view class="empty-btn" @click="handleCreate">
                    <text class="empty-btn-text">新增资质</text>
                </view>
            </view>

            <view
                v-for="item in industryQualifications"
                :key="item.id"
                class="qualification-card"
                @click="openDetail(item.id)">
                <view class="card-head">
                    <text class="card-title">{{ item.qualificationName || item.typeLabel || '行业资质' }}</text>
                    <text class="card-status" :class="`status-${formatStatusKey(item.auditStatus)}`">{{ formatStatusText(item.auditStatus) }}</text>
                </view>
                <text class="card-subtitle">{{ item.typeLabel || item.qualificationType || '-' }}</text>
                <text class="card-line">资质编号：{{ item.qualificationNo || '-' }}</text>
                <text class="card-line">有效期：{{ item.validStartDate || '-' }} 至 {{ item.validEndDate || '-' }}</text>
                <text v-if="item.rejectReason" class="card-line danger">驳回原因：{{ item.rejectReason }}</text>
            </view>

            <view class="bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
import { getQualificationPage } from '@/api/member'

const INDUSTRY_TYPE_OPTIONS = [
    { value: 'ELECTRICIAN', label: '电工证' },
    { value: 'WELDER', label: '焊工证' },
    { value: 'HVAC_TECHNICIAN', label: '空调制冷证' },
    { value: 'PLUMBING_TECHNICIAN', label: '管道作业证' },
    { value: 'CLEANING_SERVICE', label: '保洁服务资质' },
    { value: 'INSTALLATION_SERVICE', label: '安装服务资质' },
    { value: 'SAFETY_CERTIFICATE', label: '安全生产证' },
    { value: 'SPECIAL_OPERATION', label: '特种作业操作证' },
    { value: 'HEALTH_CERTIFICATE', label: '健康证' }
]

export default {
    data() {
        return {
            list: []
        }
    },
    computed: {
        industryQualifications() {
            return this.list
                .filter((item) => this.isIndustryQualification(item.qualificationType))
                .map((item) => ({
                    ...item,
                    typeLabel: this.getTypeLabel(item.qualificationType)
                }))
        }
    },
    onShow() {
        this.loadList()
    },
    methods: {
        async loadList() {
            try {
                const page = await getQualificationPage({}, { silent: true })
                this.list = (page && page.list) || []
            } catch (error) {
                this.list = []
            }
        },
        isIndustryQualification(type) {
            return !!type && type !== 'BUSINESS_LICENSE' && type !== 'INSURANCE_POLICY'
        },
        getTypeLabel(type) {
            const matched = INDUSTRY_TYPE_OPTIONS.find((item) => item.value === type)
            return matched ? matched.label : type
        },
        formatStatusText(status) {
            if (status === 'APPROVED') return '已认证'
            if (status === 'PENDING') return '审核中'
            if (status === 'REJECTED') return '已驳回'
            return '未上传'
        },
        formatStatusKey(status) {
            if (status === 'APPROVED') return 'approved'
            if (status === 'PENDING') return 'pending'
            if (status === 'REJECTED') return 'rejected'
            return 'default'
        },
        handleCreate() {
            uni.navigateTo({
                url: '/pages/certificate/qualification_edit?type=industry&mode=create'
            })
        },
        openDetail(id) {
            uni.navigateTo({
                url: `/pages/certificate/qualification_edit?type=industry&bizId=${id}`
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
    background: #f5f7fb;
}

.header {
    height: 88rpx;
    padding: 0 32rpx;
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: #ffffff;
}

.back-btn,
.header-action {
    width: 88rpx;
    display: flex;
    align-items: center;
}

.back-btn {
    justify-content: flex-start;
}

.header-action {
    justify-content: flex-end;
}

.back-icon {
    font-size: 36rpx;
    color: #222222;
}

.header-action-text {
    font-size: 28rpx;
    color: #2f80ff;
}

.title {
    font-size: 34rpx;
    font-weight: 600;
    color: #222222;
}

.content-scroll {
    height: calc(100vh - 88rpx);
    padding: 24rpx;
    box-sizing: border-box;
}

.empty-card,
.qualification-card {
    background: #ffffff;
    border-radius: 24rpx;
    padding: 28rpx;
    box-sizing: border-box;
}

.empty-card {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding-top: 88rpx;
    padding-bottom: 88rpx;
}

.empty-title {
    font-size: 32rpx;
    font-weight: 600;
    color: #222222;
}

.empty-desc {
    margin-top: 16rpx;
    font-size: 26rpx;
    line-height: 40rpx;
    color: #8a94a6;
    text-align: center;
}

.empty-btn {
    margin-top: 32rpx;
    padding: 0 40rpx;
    height: 80rpx;
    border-radius: 40rpx;
    background: linear-gradient(135deg, #4f8cff 0%, #2f80ff 100%);
    display: flex;
    align-items: center;
    justify-content: center;
}

.empty-btn-text {
    font-size: 28rpx;
    color: #ffffff;
    font-weight: 600;
}

.qualification-card + .qualification-card {
    margin-top: 20rpx;
}

.card-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 24rpx;
}

.card-title {
    flex: 1;
    font-size: 30rpx;
    font-weight: 600;
    color: #222222;
}

.card-status {
    font-size: 24rpx;
}

.status-approved {
    color: #1f9d55;
}

.status-pending {
    color: #f59e0b;
}

.status-rejected {
    color: #ef4444;
}

.status-default {
    color: #8a94a6;
}

.card-subtitle {
    margin-top: 12rpx;
    font-size: 24rpx;
    color: #2f80ff;
}

.card-line {
    margin-top: 12rpx;
    font-size: 26rpx;
    color: #6b7280;
}

.danger {
    color: #ef4444;
}

.bottom-space {
    height: 40rpx;
}
</style>
