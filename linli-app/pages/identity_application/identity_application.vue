<template>
    <view class="page-container">
        <view class="header">
            <view class="back-btn" @click="goBack">
                <text class="iconfont icon-youjiantou back-icon"></text>
            </view>
            <text class="title">身份申请</text>
            <view class="placeholder"></view>
        </view>

        <scroll-view class="content-scroll" scroll-y>
            <view class="current-card">
                <text class="current-label">当前身份</text>
                <text class="current-name">{{ currentRoleName }}</text>
                <text class="current-desc">已开通角色：{{ enabledRolesText }}</text>
            </view>

            <view class="section-title">
                <text class="title-text">最近申请记录</text>
            </view>

            <view class="apply-card" v-for="item in applications" :key="item.id">
                <view class="card-header">
                    <view class="card-title-wrap">
                        <text class="card-title">{{ item.applyRoleCode }}</text>
                        <text class="card-desc">{{ item.applyNo }}</text>
                    </view>
                    <view class="apply-btn applied">
                        <text class="apply-text">{{ item.auditStatus }}</text>
                    </view>
                </view>
                <view class="progress-section">
                    <text class="progress-info">{{ item.currentNodeName || '待处理' }}</text>
                    <text class="progress-time">{{ $fmt.formatDateTime(item.createTime) }}</text>
                    <text v-if="item.rejectReason" class="progress-time">驳回原因：{{ item.rejectReason }}</text>
                </view>
            </view>

            <view class="section-title">
                <text class="title-text">申请新身份</text>
            </view>

            <view class="apply-card" v-for="role in applyRoles" :key="role.code">
                <view class="card-header">
                    <view class="card-title-wrap">
                        <text class="card-title">{{ role.name }}</text>
                        <text class="card-desc">{{ role.desc }}</text>
                    </view>
                    <view class="apply-btn primary" @click="handleApply(role.code)">
                        <text class="apply-text">去申请</text>
                    </view>
                </view>
            </view>

            <view class="bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
import { createRoleApply, getRoleApplyPage, getRoleContext } from '@/api/member'

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
            roleContext: {},
            applications: [],
            applyRoles: [
                { code: 'PROMOTER', name: '推广员', desc: '分享推广码，赚取推广佣金' },
                { code: 'PARTNER', name: '区域合作商', desc: '负责辖区运营、初审和纠纷协调' },
                { code: 'MERCHANT', name: '服务商', desc: '接单服务、完成上门履约' }
            ]
        }
    },
    computed: {
        currentRoleName() {
            return this.roleContext.currentRoleName || '普通用户'
        },
        enabledRolesText() {
            return (this.roleContext.enabledRoleCodes || []).join('、') || 'USER'
        }
    },
    onShow() {
        this.loadPageData()
    },
    methods: {
        async loadPageData() {
            try {
                const [roleContext, page] = await Promise.all([
                    getRoleContext(),
                    getRoleApplyPage({ pageNo: 1, pageSize: 20 }).catch(() => ({ list: [] }))
                ])
                this.roleContext = roleContext || {}
                this.applications = page.list || []
            } catch (error) {
            }
        },
        async handleApply(roleCode) {
            try {
                const applyReason = await promptInput('申请说明', '请简要说明申请原因')
                if (!applyReason) {
                    return
                }
                const resourceDesc = await promptInput('资源说明', '请填写可投入资源')
                const expectedConversionDesc = roleCode === 'PROMOTER'
                    ? await promptInput('预期转化说明', '例如首月转化 20 个线索')
                    : ''
                await createRoleApply({
                    applyRoleCode: roleCode,
                    applyReason,
                    resourceDesc,
                    expectedConversionDesc,
                    abilityDesc: resourceDesc,
                    availableTimeDesc: '可按需配合平台安排'
                })
                uni.showToast({
                    title: '申请已提交',
                    icon: 'success'
                })
                this.loadPageData()
            } catch (error) {
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
    background: #F5F5F5;
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

.content-scroll {
    padding: 20rpx;
    box-sizing: border-box;
}

.current-card,
.apply-card {
    background: #fff;
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 20rpx;
}

.current-label,
.progress-time,
.card-desc {
    font-size: 24rpx;
    color: #999;
}

.current-name,
.card-title,
.title-text {
    font-size: 30rpx;
    font-weight: bold;
    color: #333;
}

.current-desc {
    display: block;
    margin-top: 12rpx;
    font-size: 24rpx;
    color: #666;
}

.section-title {
    margin-bottom: 16rpx;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16rpx;
}

.apply-btn {
    padding: 12rpx 28rpx;
    border-radius: 8rpx;

    &.applied {
        border: 1px solid #2E83F0;
    }

    &.primary {
        background: #4A90F0;
    }
}

.apply-text {
    color: #2E83F0;
}

.primary .apply-text {
    color: #fff;
}

.progress-info {
    display: block;
    font-size: 24rpx;
    color: #333;
    margin-bottom: 8rpx;
}

.bottom-space {
    height: 60rpx;
}
</style>
