<template>
    <view class="page-container">
        <view class="header-bg">
            <view class="header">
                <view class="back-btn" @click="goBack">
                    <text class="iconfont icon-youjiantou back-icon"></text>
                </view>
                <text class="title">区域合作商工作台</text>
                <view class="placeholder"></view>
            </view>

            <text class="district-text">辖区：{{ regionText }}</text>

            <view class="hero-stats">
                <view class="hero-stat blue">
                    <text class="hero-label">新增用户</text>
                    <text class="hero-value">{{ promoteStat.newUserCount || 0 }}</text>
                </view>
                <view class="hero-stat orange">
                    <text class="hero-label">待审核入驻</text>
                    <text class="hero-value">{{ summary.pendingEntryAuditCount || workbench.pendingEntryAuditCount || 0 }}</text>
                </view>
            </view>
        </view>

        <scroll-view class="content-scroll" scroll-y :scroll-into-view="scrollIntoView" refresher-enabled
            :refresher-triggered="refreshing" @refresherrefresh="handleRefresh">
            <view class="surface-card function-card">
                <view class="card-header">
                    <text class="header-title">功能入口</text>
                </view>
                <view class="function-grid">
                    <view
                        v-for="item in functionEntries"
                        :key="item.key"
                        class="function-item"
                        :class="item.className"
                        @click="handleFunctionEntry(item)">
                        <view class="function-icon">
                            <text class="function-icon-text">{{ item.icon }}</text>
                        </view>
                        <text class="function-text">{{ item.label }}</text>
                    </view>
                </view>
            </view>

            <view class="surface-card summary-card" id="region-section">
                <view class="card-header between">
                    <text class="header-title">辖区概况</text>
                    <text class="header-link" @click="showRegionDetail">查看辖区 ></text>
                </view>
                <view class="summary-grid">
                    <view class="summary-item">
                        <text class="summary-label">辖区数量</text>
                        <text class="summary-value">{{ summary.regionCount || 0 }}</text>
                    </view>
                    <view class="summary-item">
                        <text class="summary-label">启用辖区</text>
                        <text class="summary-value">{{ summary.enabledRegionCount || 0 }}</text>
                    </view>
                    <view class="summary-item">
                        <text class="summary-label">辖区订单</text>
                        <text class="summary-value">{{ summary.orderCount || workbench.orderCount || 0 }}</text>
                    </view>
                    <view class="summary-item">
                        <text class="summary-label">成交额</text>
                        <text class="summary-value money">¥{{ $fmt.formatMoney(summary.tradeAmount || workbench.tradeAmount) }}</text>
                    </view>
                </view>
            </view>

            <view class="surface-card list-card" id="entry-audit-section">
                <view class="card-header between">
                    <text class="header-title">待审核入驻申请</text>
                    <text class="header-link" @click="toggleEntryAudits">查看全部 ></text>
                </view>
                <view v-if="entryAudits.length" class="entry-list">
                    <view v-for="item in entryAudits" :key="item.id" class="entry-item">
                        <view class="entry-info">
                            <text class="entry-name">{{ item.merchantName || item.userNickname || '未命名申请' }}</text>
                            <text class="entry-desc">
                                {{ item.userNickname || item.userNo || '--' }} · {{ item.regionCode || '未填写区域' }}
                            </text>
                            <text class="entry-time">提交时间：{{ formatTime(item.createTime) }}</text>
                        </view>
                        <view class="entry-action" @click="handleEntryAudit(item)">
                            <text class="entry-action-text">审核</text>
                        </view>
                    </view>
                </view>
                <view v-else class="empty-state">暂无待审核入驻申请</view>
            </view>

            <view class="surface-card list-card" id="dispute-section">
                <view class="card-header between">
                    <text class="header-title">辖区纠纷</text>
                    <text class="header-link" @click="toggleDisputes">去处理 ></text>
                </view>
                <view v-if="disputes.length" class="dispute-list">
                    <view v-for="item in disputes" :key="`${item.disputeType}-${item.disputeId}`" class="dispute-item">
                        <view class="dispute-info">
                            <text class="dispute-name">{{ getDisputeTitle(item) }}</text>
                            <text class="dispute-desc">{{ item.content || item.resultDesc || '暂无详细描述' }}</text>
                            <text class="dispute-time">{{ formatTime(item.createTime) }}</text>
                        </view>
                        <view class="dispute-action" @click="handleDisputeAction(item)">
                            <text class="dispute-action-text">协调</text>
                        </view>
                    </view>
                </view>
                <view v-else class="empty-state">暂无待处理纠纷</view>
            </view>

            <view class="surface-card list-card" id="price-report-section">
                <view class="card-header between">
                    <text class="header-title">最近价格申报</text>
                    <text class="header-link" @click="togglePriceReports">查看全部 ></text>
                </view>
                <view v-if="priceReports.length" class="report-list">
                    <view v-for="item in priceReports" :key="item.id" class="report-item">
                        <view class="report-info">
                            <text class="report-name">{{ item.categoryName || `类目 ${item.categoryId || '--'}` }}</text>
                            <text class="report-desc">{{ item.regionCode || '--' }} · {{ formatMoney(item.suggestedPrice) }}</text>
                            <text class="report-time">{{ formatTime(item.createTime) }}</text>
                        </view>
                        <view class="report-badge" :class="reportStatusClass(item.status)">
                            <text class="report-badge-text">{{ getReportStatusLabel(item.status) }}</text>
                        </view>
                        <view
                            v-if="item.status === 'PENDING'"
                            class="report-action"
                            @click="handleWithdrawReport(item)">
                            <text class="report-action-text">撤回</text>
                        </view>
                    </view>
                </view>
                <view v-else class="empty-state">暂无价格申报记录</view>
            </view>

            <view class="surface-card list-card" id="instruction-section">
                <view class="card-header between">
                    <text class="header-title">会议通知 / 上级指令</text>
                    <text class="header-link" @click="toggleInstructions">查看全部 ></text>
                </view>
                <view v-if="instructions.length" class="notice-list">
                    <view
                        v-for="item in instructions"
                        :key="item.id"
                        class="notice-item"
                        @click="openInstruction(item)">
                        <view class="notice-copy">
                            <text class="notice-title">{{ item.title || '未命名通知' }}</text>
                            <text class="notice-desc">{{ item.contentSnapshot || '点击查看详情' }}</text>
                        </view>
                        <text class="notice-time">{{ formatTime(item.createTime) }}</text>
                    </view>
                </view>
                <view v-else class="empty-state">暂无会议通知</view>
            </view>

            <view class="bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
import {
    auditPartnerEntry,
    createPartnerCoordination,
    getPartnerDisputePage,
    getPartnerEntryAuditPage,
    getPartnerInstructionPage,
    getPartnerPromoteStat,
    getPartnerRegion,
    getPartnerWorkbench,
    withdrawPartnerPriceReport,
    getPartnerPriceReportPage
} from '@/api/partner'

function promptInput(title, placeholder = '', defaultValue = '') {
    return new Promise((resolve) => {
        uni.showModal({
            title,
            editable: true,
            placeholderText: placeholder,
            content: defaultValue,
            success: (res) => resolve(res.confirm ? (res.content || '') : '')
        })
    })
}

export default {
    data() {
        return {
            refreshing: false,
            loading: false,
            scrollIntoView: '',
            workbench: {},
            region: {},
            promoteStat: {},
            entryAuditPage: { list: [] },
            disputePage: { list: [] },
            priceReportPage: { list: [] },
            instructionPage: { list: [] },
            showAllEntryAudits: false,
            showAllDisputes: false,
            showAllPriceReports: false,
            showAllInstructions: false
        }
    },
    computed: {
        summary() {
            return this.workbench.summary || {}
        },
        regions() {
            return this.region.regions || []
        },
        regionText() {
            if (!this.regions.length) {
                return '暂无辖区配置'
            }
            return this.regions
                .slice(0, 2)
                .map((item) => [item.city, item.district].filter(Boolean).join('') || item.province || item.regionCode || '--')
                .join('、')
        },
        functionEntries() {
            return [
                { key: 'REGION', label: '辖区查看', icon: '区', className: 'purple' },
                { key: 'ENTRY_AUDIT', label: '入驻初审', icon: '审', className: 'blue' },
                { key: 'DISPUTE', label: '纠纷协调', icon: '协', className: 'orange' },
                { key: 'PRICE_REPORT', label: '价格申报', icon: '价', className: 'green' },
                { key: 'PROMOTE', label: '推广数据', icon: '推', className: 'pink' },
                { key: 'NOTICE', label: '会议通知', icon: '会', className: 'red' }
            ]
        },
        entryAudits() {
            const list = this.entryAuditPage.list || []
            return this.showAllEntryAudits ? list : list.slice(0, 3)
        },
        disputes() {
            const list = this.disputePage.list || []
            return this.showAllDisputes ? list : list.slice(0, 3)
        },
        priceReports() {
            const list = this.priceReportPage.list || []
            return this.showAllPriceReports ? list : list.slice(0, 3)
        },
        instructions() {
            const list = this.instructionPage.list || []
            return this.showAllInstructions ? list : list.slice(0, 3)
        }
    },
    onShow() {
        this.loadPageData()
    },
    methods: {
        async loadPageData() {
            if (this.loading) {
                return
            }
            this.loading = true
            try {
                const [workbench, region, promoteStat, entryAuditPage, disputePage, priceReportPage, instructionPage] = await Promise.all([
                    getPartnerWorkbench().catch(() => ({})),
                    getPartnerRegion().catch(() => ({})),
                    getPartnerPromoteStat().catch(() => ({})),
                    getPartnerEntryAuditPage({ pageNo: 1, pageSize: 10, status: 'PENDING' }).catch(() => ({ list: [] })),
                    getPartnerDisputePage({ pageNo: 1, pageSize: 10, status: 'PENDING' }).catch(() => ({ list: [] })),
                    getPartnerPriceReportPage({ pageNo: 1, pageSize: 10, status: 'PENDING' }).catch(() => ({ list: [] })),
                    getPartnerInstructionPage({ pageNo: 1, pageSize: 10 }).catch(() => ({ list: [] }))
                ])
                this.workbench = workbench || {}
                this.region = region || {}
                this.promoteStat = promoteStat || {}
                this.entryAuditPage = entryAuditPage || { list: [] }
                this.disputePage = disputePage || { list: [] }
                this.priceReportPage = priceReportPage || { list: [] }
                this.instructionPage = instructionPage || { list: [] }
            } catch (error) {
            } finally {
                this.refreshing = false
                this.loading = false
            }
        },
        handleRefresh() {
            this.refreshing = true
            this.loadPageData()
        },
        scrollToSection(id) {
            this.scrollIntoView = id
            this.$nextTick(() => {
                this.scrollIntoView = ''
            })
        },
        toggleEntryAudits() {
            this.showAllEntryAudits = !this.showAllEntryAudits
        },
        toggleDisputes() {
            this.showAllDisputes = !this.showAllDisputes
        },
        togglePriceReports() {
            this.showAllPriceReports = !this.showAllPriceReports
        },
        toggleInstructions() {
            this.showAllInstructions = !this.showAllInstructions
        },
        handleFunctionEntry(item) {
            if (!item) {
                return
            }
            if (item.key === 'REGION') {
                this.showRegionDetail()
                return
            }
            if (item.key === 'ENTRY_AUDIT') {
                this.scrollToSection('entry-audit-section')
                return
            }
            if (item.key === 'DISPUTE') {
                this.scrollToSection('dispute-section')
                return
            }
            if (item.key === 'PRICE_REPORT') {
                this.scrollToSection('price-report-section')
                return
            }
            if (item.key === 'PROMOTE') {
                this.showPromoteStat()
                return
            }
            if (item.key === 'NOTICE') {
                this.scrollToSection('instruction-section')
            }
        },
        showRegionDetail() {
            const lines = (this.regions || []).map((item) => {
                const parts = [item.province, item.city, item.district, item.streetName].filter(Boolean)
                return `${parts.join(' ')}${item.regionCode ? ` · ${item.regionCode}` : ''}`
            })
            uni.showModal({
                title: '辖区详情',
                content: lines.length ? lines.slice(0, 6).join('\n') : '暂无辖区配置',
                showCancel: false
            })
        },
        showPromoteStat() {
            const text = [
                `新增用户：${this.promoteStat.newUserCount || 0}`,
                `绑定推广员：${this.promoteStat.boundPromoterCount || 0}`,
                `转化订单：${this.promoteStat.convertOrderCount || 0}`,
                `推广成交额：¥${this.$fmt.formatMoney(this.promoteStat.tradeAmount)}`
            ].join('\n')
            uni.showModal({
                title: '推广数据',
                content: text,
                showCancel: false
            })
        },
        async handleEntryAudit(item) {
            if (!item || !item.id) {
                return
            }
            uni.showActionSheet({
                itemList: ['通过', '驳回'],
                success: async ({ tapIndex }) => {
                    try {
                        if (tapIndex === 0) {
                            await auditPartnerEntry({
                                id: item.id,
                                auditStatus: 'APPROVED',
                                auditRemark: '合作商辖区初审通过'
                            })
                        } else {
                            const rejectReason = await promptInput('驳回原因', '请输入驳回原因')
                            if (!rejectReason) {
                                return
                            }
                            await auditPartnerEntry({
                                id: item.id,
                                auditStatus: 'REJECTED',
                                auditRemark: rejectReason,
                                rejectReason
                            })
                        }
                        uni.showToast({
                            title: '审核已提交',
                            icon: 'success'
                        })
                        this.loadPageData()
                    } catch (error) {
                    }
                }
            })
        },
        async handleDisputeAction(item) {
            if (!item || !item.disputeId) {
                return
            }
            uni.showActionSheet({
                itemList: ['协调处理', '升级平台终审'],
                success: async ({ tapIndex }) => {
                    try {
                        const coordinationRemark = await promptInput('协调意见', '请输入协调意见')
                        if (!coordinationRemark) {
                            return
                        }
                        const payload = {
                            disputeType: item.disputeType,
                            disputeId: item.disputeId,
                            coordinationRemark,
                            escalateToPlatform: tapIndex === 1
                        }
                        if (tapIndex === 1) {
                            const escalateRemark = await promptInput('升级备注', '请输入升级平台终审的说明')
                            if (!escalateRemark) {
                                return
                            }
                            payload.escalateRemark = escalateRemark
                        }
                        await createPartnerCoordination(payload)
                        uni.showToast({
                            title: '协调记录已提交',
                            icon: 'success'
                        })
                        this.loadPageData()
                    } catch (error) {
                    }
                }
            })
        },
        async handleWithdrawReport(item) {
            if (!item || !item.id) {
                return
            }
            uni.showModal({
                title: '确认撤回',
                content: `确定撤回 ${item.categoryName || '该价格申报'} 吗？`,
                success: async (res) => {
                    if (!res.confirm) {
                        return
                    }
                    try {
                        await withdrawPartnerPriceReport(item.id)
                        uni.showToast({
                            title: '申报已撤回',
                            icon: 'success'
                        })
                        this.loadPageData()
                    } catch (error) {
                    }
                }
            })
        },
        openInstruction(item) {
            if (!item) {
                return
            }
            uni.showModal({
                title: item.title || '会议通知',
                content: item.contentSnapshot || '暂无详情',
                showCancel: false
            })
        },
        formatMoney(value) {
            return `¥${this.$fmt.formatMoney(value)}`
        },
        formatTime(value) {
            return value ? this.$fmt.formatDateTime(value) : '--'
        },
        getDisputeTitle(item) {
            const typeMap = {
                COMPLAINT: '投诉',
                APPEAL: '申诉'
            }
            return `${typeMap[item.disputeType] || item.disputeType || '纠纷'} · ${item.orderNo || item.disputeNo || '未命名'}`
        },
        getReportStatusLabel(status) {
            const map = {
                PENDING: '待审核',
                APPROVED: '已通过',
                REJECTED: '已驳回',
                WITHDRAWN: '已撤回'
            }
            return map[status] || status || '--'
        },
        reportStatusClass(status) {
            return {
                pending: status === 'PENDING',
                approved: status === 'APPROVED',
                rejected: status === 'REJECTED',
                withdrawn: status === 'WITHDRAWN'
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
    background: #f4f7fb;

    .header-bg {
        position: relative;
        background: linear-gradient(180deg, #3485f7 0%, #3f8cf8 56%, #4f95fb 100%);
        padding-bottom: 96rpx;
        border-radius: 0 0 34rpx 34rpx;
        overflow: hidden;
        box-shadow: inset 0 -1rpx 0 rgba(255, 255, 255, 0.12);
    }

    .header {
        padding: 60rpx 30rpx 26rpx;
        display: flex;
        justify-content: space-between;
        align-items: center;

        .back-btn,
        .placeholder {
            width: 64rpx;
            height: 64rpx;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .back-icon {
            font-size: 40rpx;
            color: #fff;
            transform: rotate(180deg);
        }

        .title {
            font-size: 34rpx;
            font-weight: 600;
            color: #fff;
        }
    }

    .district-text {
        display: block;
        text-align: center;
        color: rgba(255, 255, 255, 0.92);
        font-size: 24rpx;
        letter-spacing: 1rpx;
    }

    .hero-stats {
        position: relative;
        z-index: 2;
        margin: 24rpx 28rpx 0;
        display: flex;
        gap: 18rpx;

        .hero-stat {
            flex: 1;
            min-height: 112rpx;
            border-radius: 16rpx;
            padding: 20rpx 18rpx;
            box-shadow: 0 10rpx 24rpx rgba(25, 79, 171, 0.16);

            &.blue {
                background: linear-gradient(180deg, #dce9ff 0%, #cfe0ff 100%);
            }

            &.orange {
                background: linear-gradient(180deg, #fbeedb 0%, #f8e0c0 100%);
            }
        }

        .hero-label {
            display: block;
            font-size: 22rpx;
            color: #55677f;
            margin-bottom: 10rpx;
        }

        .hero-value {
            display: block;
            font-size: 54rpx;
            line-height: 1;
            font-weight: 700;
            color: #2f7df2;
        }

        .orange .hero-value {
            color: #f09a2b;
        }
    }

    .content-scroll {
        margin-top: -42rpx;
        padding: 0 24rpx 36rpx;
        box-sizing: border-box;
    }

    .surface-card {
        background: #fff;
        border-radius: 18rpx;
        padding: 24rpx;
        margin-bottom: 22rpx;
        box-shadow: 0 10rpx 24rpx rgba(33, 60, 116, 0.06);
    }

    .card-header {
        margin-bottom: 20rpx;

        &.between {
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
    }

    .header-title {
        font-size: 30rpx;
        font-weight: 600;
        color: #313b4d;
    }

    .header-link {
        font-size: 22rpx;
        color: #6f90d9;
    }

    .function-grid {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        gap: 16rpx;
    }

    .function-item {
        min-height: 132rpx;
        border-radius: 16rpx;
        padding: 16rpx 12rpx 14rpx;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        gap: 10rpx;
        box-shadow: inset 0 0 0 1rpx rgba(255, 255, 255, 0.7);

        &.purple {
            background: linear-gradient(180deg, #efe8ff 0%, #e4dbff 100%);
        }

        &.blue {
            background: linear-gradient(180deg, #e6f1ff 0%, #d7eaff 100%);
        }

        &.orange {
            background: linear-gradient(180deg, #fff2df 0%, #ffe8c2 100%);
        }

        &.green {
            background: linear-gradient(180deg, #e8f8e8 0%, #d9f0da 100%);
        }

        &.pink {
            background: linear-gradient(180deg, #f9e8f7 0%, #f3d7f0 100%);
        }

        &.red {
            background: linear-gradient(180deg, #ffe7ea 0%, #ffd7df 100%);
        }
    }

    .function-icon {
        width: 56rpx;
        height: 56rpx;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.75);
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 6rpx 16rpx rgba(0, 0, 0, 0.04);
    }

    .function-icon-text {
        font-size: 30rpx;
        font-weight: 700;
        color: #4a75e8;
    }

    .orange .function-icon-text {
        color: #f08e23;
    }

    .green .function-icon-text {
        color: #2ea44f;
    }

    .pink .function-icon-text {
        color: #c763bd;
    }

    .red .function-icon-text {
        color: #e45e75;
    }

    .function-text {
        font-size: 22rpx;
        color: #6a5c8e;
        font-weight: 600;
    }

    .summary-grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 14rpx;
    }

    .summary-item {
        background: #f7f9fd;
        border-radius: 14rpx;
        padding: 20rpx 18rpx;
    }

    .summary-label {
        display: block;
        font-size: 22rpx;
        color: #7b8798;
        margin-bottom: 8rpx;
    }

    .summary-value {
        display: block;
        font-size: 34rpx;
        font-weight: 700;
        color: #2f4b73;
    }

    .summary-value.money {
        font-size: 30rpx;
    }

    .entry-list,
    .dispute-list,
    .report-list,
    .notice-list {
        display: flex;
        flex-direction: column;
        gap: 14rpx;
    }

    .entry-item,
    .dispute-item,
    .report-item,
    .notice-item {
        border-radius: 14rpx;
        background: #f8fbff;
        padding: 20rpx 18rpx;
        display: flex;
        align-items: center;
        gap: 16rpx;
    }

    .entry-info,
    .dispute-info,
    .report-info,
    .notice-copy {
        flex: 1;
        min-width: 0;
    }

    .entry-name,
    .dispute-name,
    .report-name,
    .notice-title {
        display: block;
        font-size: 28rpx;
        font-weight: 600;
        color: #324155;
        margin-bottom: 8rpx;
    }

    .entry-desc,
    .entry-time,
    .dispute-desc,
    .dispute-time,
    .report-desc,
    .report-time,
    .notice-desc,
    .notice-time,
    .empty-state {
        display: block;
        font-size: 22rpx;
        color: #8a95a6;
        line-height: 1.5;
    }

    .entry-action,
    .dispute-action,
    .report-action {
        min-width: 104rpx;
        padding: 16rpx 18rpx;
        border-radius: 12rpx;
        background: #2f7df2;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
    }

    .report-action {
        min-width: 88rpx;
        background: #edf3ff;
        margin-left: auto;
    }

    .entry-action-text,
    .dispute-action-text {
        font-size: 24rpx;
        color: #fff;
        font-weight: 600;
    }

    .report-action-text {
        font-size: 22rpx;
        color: #2f7df2;
        font-weight: 600;
    }

    .report-badge {
        margin-left: 12rpx;
        padding: 12rpx 16rpx;
        border-radius: 999rpx;
        flex-shrink: 0;

        &.pending {
            background: #fff3e0;
        }

        &.approved {
            background: #e8f7ea;
        }

        &.rejected {
            background: #ffe8ea;
        }

        &.withdrawn {
            background: #edf1f6;
        }
    }

    .report-badge-text {
        font-size: 22rpx;
        font-weight: 600;
        color: #df8b24;
    }

    .approved .report-badge-text {
        color: #2f9e44;
    }

    .rejected .report-badge-text {
        color: #d64545;
    }

    .withdrawn .report-badge-text {
        color: #7a8596;
    }

    .empty-state {
        padding: 12rpx 0 4rpx;
        text-align: center;
    }

    .bottom-space {
        height: 60rpx;
    }
}
</style>
