<template>
  <div class="linbang-dashboard">
    <section class="dashboard-head">
      <div class="head-top">
        <div class="head-title">
          <p class="eyebrow">LinBang Command Dashboard</p>
          <div class="head-heading">
            <h1>平台运行指挥台</h1>
            <el-tag effect="dark" round type="success">真实 dashboard 接口</el-tag>
          </div>
        </div>

        <div class="head-toolbar">
          <div class="toolbar-meta">
            <span class="toolbar-label">最后刷新</span>
            <strong>{{ updatedAt || '--' }}</strong>
          </div>
          <el-button :loading="loading" plain type="primary" @click="loadDashboard">
            <Icon icon="ep:refresh" class="mr-6px" />
            刷新看板
          </el-button>
        </div>
      </div>

      <div class="head-strip">
        <article v-for="item in heroHighlights" :key="item.label" class="strip-card">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
          <small>{{ item.tip }}</small>
        </article>
        <article
          v-for="item in signalCards"
          :key="item.label"
          class="strip-card strip-card--signal"
        >
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
          <small>{{ item.tip }}</small>
        </article>
      </div>
    </section>

    <section class="metric-grid">
      <article v-for="card in metricCards" :key="card.key" class="metric-card">
        <div class="metric-top">
          <span>{{ card.label }}</span>
          <Icon :icon="card.icon" class="metric-icon" />
        </div>
        <strong>{{ card.value }}</strong>
        <div class="metric-bottom">
          <small>{{ card.tip }}</small>
          <span :class="['metric-badge', `metric-badge--${card.tone}`]">{{ card.badge }}</span>
        </div>
      </article>
    </section>

    <section class="board-grid">
      <div class="surface surface--trend">
        <div class="surface-head">
          <div>
            <h3>订单 / 交易 / 拉新总览</h3>
            <p>近 7 日平台核心运行趋势</p>
          </div>
          <el-tag round type="info">近 7 日</el-tag>
        </div>
        <el-skeleton :loading="loading" animated>
          <Echart :options="trendChartOptions" :height="360" />
        </el-skeleton>
      </div>

      <div class="surface surface--command">
        <div class="surface-head">
          <div>
            <h3>待办指挥舱</h3>
            <p>直接落到真实业务处理页面</p>
          </div>
          <el-tag round type="warning">{{ priorityItems.length }} 组重点</el-tag>
        </div>

        <div class="priority-list">
          <button
            v-for="item in priorityItems"
            :key="item.title"
            class="priority-item"
            type="button"
            @click="router.push(item.path)"
          >
            <div>
              <div class="priority-title">
                <span>{{ item.title }}</span>
                <el-tag :type="item.tagType" effect="plain" round size="small">
                  {{ item.level }}
                </el-tag>
              </div>
              <p>{{ item.description }}</p>
            </div>
            <strong>{{ formatCount(item.count) }}</strong>
          </button>
        </div>

        <div v-if="quickActions.length" class="action-grid">
          <button
            v-for="item in quickActions"
            :key="item.path"
            class="action-card"
            type="button"
            @click="router.push(item.path)"
          >
            <Icon :icon="item.icon" class="action-icon" />
            <div>
              <span>{{ item.title }}</span>
              <small>{{ item.description }}</small>
            </div>
          </button>
        </div>
        <el-empty v-else description="当前账号暂无工作台快捷入口" />
      </div>

      <div class="surface">
        <div class="surface-head">
          <div>
            <h3>审核与风险压力</h3>
            <p>当前待办与异常负载分布</p>
          </div>
          <el-tag round type="danger">即时压力</el-tag>
        </div>
        <el-skeleton :loading="loading" animated>
          <Echart :options="pressureChartOptions" :height="280" />
        </el-skeleton>
        <div class="queue-stack">
          <article v-for="item in queueBars" :key="item.label" class="queue-item">
            <div class="queue-meta">
              <span>{{ item.label }}</span>
              <strong>{{ formatCount(item.value) }}</strong>
            </div>
            <el-progress
              :color="item.color"
              :percentage="item.percentage"
              :show-text="false"
              :stroke-width="10"
            />
          </article>
        </div>
      </div>

      <div class="surface">
        <div class="surface-head">
          <div>
            <h3>资金脉冲</h3>
            <p>交易额与提现申请额对照</p>
          </div>
          <el-tag round type="success">资金链路</el-tag>
        </div>
        <el-skeleton :loading="loading" animated>
          <Echart :options="financeChartOptions" :height="280" />
        </el-skeleton>
        <div class="mini-summary">
          <article class="mini-summary__item">
            <span>7 日交易额</span>
            <strong>{{ formatCurrency(sevenDayTotals.tradeAmount) }}</strong>
          </article>
          <article class="mini-summary__item">
            <span>7 日提现申请额</span>
            <strong>{{ formatCurrency(sevenDayTotals.withdrawAmount) }}</strong>
          </article>
        </div>
      </div>

      <div class="surface">
        <div class="surface-head">
          <div>
            <h3>运行脉冲</h3>
            <p>对当前队列和近 7 日订单做快速判读</p>
          </div>
          <el-tag round type="info">运营判读</el-tag>
        </div>

        <div class="pulse-grid">
          <article class="pulse-card">
            <el-progress
              color="#14b8a6"
              :percentage="normalizedCompletionRate"
              :stroke-width="12"
              status="success"
              type="circle"
              :width="120"
            />
            <div class="pulse-card__copy">
              <span>订单完成率</span>
              <strong>{{ formatPercent(overview.completionRate) }}</strong>
              <small>主订单历史完成占比</small>
            </div>
          </article>

          <article class="pulse-card pulse-card--stack">
            <div v-for="item in pulseStats" :key="item.label" class="pulse-line">
              <span>{{ item.label }}</span>
              <strong>{{ item.value }}</strong>
              <small>{{ item.tip }}</small>
            </div>
          </article>
        </div>
      </div>

      <div class="surface surface--table">
        <div class="surface-head">
          <div>
            <h3>近 7 日运行明细</h3>
            <p>真实聚合字段落表，便于值班复盘</p>
          </div>
          <el-tag round type="info">{{ mergedTrendRows.length }} 天</el-tag>
        </div>
        <el-table :data="mergedTrendRows" :stripe="true" size="large">
          <el-table-column label="日期" prop="statDate" min-width="120" />
          <el-table-column label="订单量" min-width="100">
            <template #default="{ row }">
              {{ formatCount(row.orderCount) }}
            </template>
          </el-table-column>
          <el-table-column label="交易额" min-width="140">
            <template #default="{ row }">
              {{ formatCurrency(row.tradeAmount) }}
            </template>
          </el-table-column>
          <el-table-column label="新增用户" min-width="110">
            <template #default="{ row }">
              {{ formatCount(row.newUserCount) }}
            </template>
          </el-table-column>
          <el-table-column label="提现申请额" min-width="150">
            <template #default="{ row }">
              {{ formatCurrency(row.withdrawAmount) }}
            </template>
          </el-table-column>
          <el-table-column label="当日重点" min-width="180">
            <template #default="{ row }">
              <div class="focus-tags">
                <el-tag
                  v-for="tag in getRowFocus(row)"
                  :key="`${row.statDate}-${tag.label}`"
                  :type="tag.type"
                  effect="plain"
                  round
                  size="small"
                >
                  {{ tag.label }}
                </el-tag>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import type { EChartsOption } from 'echarts'
import { useRouter } from 'vue-router'
import {
  DashboardApi,
  type DashboardOverview,
  type DashboardTrendPoint
} from '@/api/linbang/dashboard'
import { hasPermission } from '@/directives/permission/hasPermi'

defineOptions({ name: 'LinbangDashboard' })

interface QuickAction {
  title: string
  description: string
  icon: string
  path: string
  permissions: string[]
}

interface TrendRow {
  statDate: string
  orderCount: number
  tradeAmount: number
  newUserCount: number
  withdrawAmount: number
}

interface FocusTag {
  label: string
  type: 'success' | 'warning' | 'danger' | 'info'
}

interface PrioritySeed {
  title: string
  description: string
  count: number
  routes: Array<{
    path: string
    permission: string
  }>
}

const router = useRouter()
const message = useMessage()

const loading = ref(false)
const updatedAt = ref('')

const overview = ref<DashboardOverview>({
  todayOrderCount: 0,
  todayTradeAmount: 0,
  todayNewUserCount: 0,
  completionRate: 0,
  pendingAuditCount: 0,
  abnormalOrderCount: 0,
  riskAlertCount: 0,
  refundPendingCount: 0,
  pendingRoleApplyCount: 0,
  expiringQualificationCount: 0,
  pendingPriceReportCount: 0,
  pendingPushTaskCount: 0,
  failedPushTaskCount: 0
})
const orderTrend = ref<DashboardTrendPoint[]>([])
const financeTrend = ref<DashboardTrendPoint[]>([])
const userTrend = ref<DashboardTrendPoint[]>([])

const quickActionSeed: QuickAction[] = [
  {
    title: '实名认证审核',
    description: '优先清实名与活体待办',
    icon: 'ep:checked',
    path: '/linbang-member/member-real-name',
    permissions: ['linbang:member-user-real-name:query']
  },
  {
    title: '服务商入驻审核',
    description: '处理入驻初审与终审',
    icon: 'ep:stamp',
    path: '/linbang-merchant/merchant-entry',
    permissions: ['linbang:merchant-entry:query']
  },
  {
    title: '提现审核',
    description: '跟进提现和打款链路',
    icon: 'ep:wallet-filled',
    path: '/linbang-wallet/wallet-withdraw',
    permissions: ['linbang:wallet:withdraw:query']
  },
  {
    title: '异常订单处理',
    description: '人工介入异常订单',
    icon: 'ep:warning',
    path: '/linbang-order/order-abnormal',
    permissions: ['linbang:order:abnormal:query']
  },
  {
    title: '投诉处理',
    description: '处理售后投诉工单',
    icon: 'ep:chat-dot-round',
    path: '/linbang-review/complaint',
    permissions: ['linbang:review:complaint:query']
  },
  {
    title: '申诉审核',
    description: '关注拆单申诉与复核',
    icon: 'ep:guide',
    path: '/linbang-review/appeal',
    permissions: ['linbang:review:appeal:query']
  },
  {
    title: '风控规则',
    description: '调整风控命中阈值',
    icon: 'ep:set-up',
    path: '/linbang-risk/risk-rule',
    permissions: ['linbang:risk-rule:query']
  },
  {
    title: '退款审核',
    description: '跟进支付退款闭环',
    icon: 'ep:refresh-left',
    path: '/linbang-wallet/pay-refund',
    permissions: ['pay:refund:query']
  },
  {
    title: '身份申请审核',
    description: '处理服务商、推广员、合作商身份申请',
    icon: 'ep:user',
    path: '/linbang-member/member-role-apply',
    permissions: ['linbang:member:role-apply:query']
  },
  {
    title: '价格申报审核',
    description: '复核合作商与服务商价格申报',
    icon: 'ep:money',
    path: '/linbang-partner/merchant-price-report',
    permissions: ['linbang:partner:price-report:query']
  },
  {
    title: '推送任务追踪',
    description: '查看失败与待执行推送任务',
    icon: 'ep:promotion',
    path: '/linbang-message/message-push-task',
    permissions: ['linbang:message:push-task:query']
  }
]

const toNumber = (value: unknown) => {
  const result = Number(value ?? 0)
  return Number.isFinite(result) ? result : 0
}

const clamp = (value: number, min: number, max: number) => Math.min(Math.max(value, min), max)

const formatCount = (value: unknown) =>
  new Intl.NumberFormat('zh-CN', { maximumFractionDigits: 0 }).format(toNumber(value))

const formatCurrency = (value: unknown) =>
  `¥ ${new Intl.NumberFormat('zh-CN', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 2
  }).format(toNumber(value))}`

const formatPercent = (value: unknown) => {
  const num = toNumber(value)
  const digits = Number.isInteger(num) ? 0 : 1
  return `${num.toFixed(digits)}%`
}

const mergedTrendRows = computed<TrendRow[]>(() => {
  const trendMap = new Map<string, TrendRow>()
  const mergeRows = (rows: DashboardTrendPoint[]) => {
    rows.forEach((item) => {
      const current = trendMap.get(item.statDate) ?? {
        statDate: item.statDate,
        orderCount: 0,
        tradeAmount: 0,
        newUserCount: 0,
        withdrawAmount: 0
      }
      current.orderCount = Math.max(current.orderCount, toNumber(item.orderCount))
      current.tradeAmount = Math.max(current.tradeAmount, toNumber(item.tradeAmount))
      current.newUserCount = Math.max(current.newUserCount, toNumber(item.newUserCount))
      current.withdrawAmount = Math.max(current.withdrawAmount, toNumber(item.withdrawAmount))
      trendMap.set(item.statDate, current)
    })
  }
  mergeRows(orderTrend.value)
  mergeRows(financeTrend.value)
  mergeRows(userTrend.value)
  return [...trendMap.values()].sort((a, b) => a.statDate.localeCompare(b.statDate))
})

const sevenDayTotals = computed(() =>
  mergedTrendRows.value.reduce(
    (acc, item) => {
      acc.orderCount += toNumber(item.orderCount)
      acc.tradeAmount += toNumber(item.tradeAmount)
      acc.newUserCount += toNumber(item.newUserCount)
      acc.withdrawAmount += toNumber(item.withdrawAmount)
      return acc
    },
    { orderCount: 0, tradeAmount: 0, newUserCount: 0, withdrawAmount: 0 }
  )
)

const peakTradeAmount = computed(() =>
  mergedTrendRows.value.reduce((max, item) => Math.max(max, toNumber(item.tradeAmount)), 0)
)

const peakOrderCount = computed(() =>
  mergedTrendRows.value.reduce((max, item) => Math.max(max, toNumber(item.orderCount)), 0)
)

const recentDelta = (field: keyof TrendRow) => {
  const rows = mergedTrendRows.value
  if (rows.length < 2) {
    return '近 2 日暂无波动'
  }
  const latest = toNumber(rows[rows.length - 1][field])
  const previous = toNumber(rows[rows.length - 2][field])
  if (previous === 0) {
    return latest === 0 ? '较昨日持平' : '较昨日放量'
  }
  const ratio = ((latest - previous) / previous) * 100
  const prefix = ratio > 0 ? '+' : ''
  return `较昨日 ${prefix}${ratio.toFixed(1)}%`
}

const heroHighlights = computed(() => [
  {
    label: '近 7 日订单总量',
    value: formatCount(sevenDayTotals.value.orderCount),
    tip: '订单与拆单链路的总观测面'
  },
  {
    label: '近 7 日交易额',
    value: formatCurrency(sevenDayTotals.value.tradeAmount),
    tip: '支付与履约联动总盘子'
  },
  {
    label: '近 7 日新增用户',
    value: formatCount(sevenDayTotals.value.newUserCount),
    tip: '拉新与转化基础面'
  }
])

const signalCards = computed(() => [
  {
    label: '待审核总量',
    value: formatCount(overview.value.pendingAuditCount),
    tip: '实名、资质、入驻、提现待办'
  },
  {
    label: '身份申请待审',
    value: formatCount(overview.value.pendingRoleApplyCount),
    tip: '推广员 / 服务商 / 合作商角色切换'
  },
  {
    label: '风险预警',
    value: formatCount(overview.value.riskAlertCount),
    tip: '当前命中风控规则的工单数'
  },
  {
    label: '退款待办',
    value: formatCount(overview.value.refundPendingCount),
    tip: '售后与退款链路需要继续跟进'
  },
  {
    label: '到期证件提醒',
    value: formatCount(overview.value.expiringQualificationCount),
    tip: '需尽快跟进资质续期与接单恢复'
  },
  {
    label: '失败推送任务',
    value: formatCount(overview.value.failedPushTaskCount),
    tip: '检查消息任务执行异常与补发'
  }
])

const metricCards = computed(() => [
  {
    key: 'todayOrderCount',
    label: '今日订单量',
    value: formatCount(overview.value.todayOrderCount),
    tip: '平台今日创建订单',
    badge: recentDelta('orderCount'),
    tone: 'info',
    icon: 'ep:document'
  },
  {
    key: 'todayTradeAmount',
    label: '今日交易额',
    value: formatCurrency(overview.value.todayTradeAmount),
    tip: '今日订单累计成交金额',
    badge: recentDelta('tradeAmount'),
    tone: 'success',
    icon: 'ep:wallet'
  },
  {
    key: 'todayNewUserCount',
    label: '今日新增用户',
    value: formatCount(overview.value.todayNewUserCount),
    tip: '今日新增注册用户数',
    badge: recentDelta('newUserCount'),
    tone: 'warning',
    icon: 'ep:user-filled'
  },
  {
    key: 'completionRate',
    label: '完成率',
    value: formatPercent(overview.value.completionRate),
    tip: '历史订单完成占比',
    badge: '履约核心指标',
    tone: 'success',
    icon: 'ep:circle-check'
  },
  {
    key: 'pendingAuditCount',
    label: '待审核事项',
    value: formatCount(overview.value.pendingAuditCount),
    tip: '审核队列当前堆积量',
    badge: '需要清队',
    tone: 'warning',
    icon: 'ep:checked'
  },
  {
    key: 'abnormalOrderCount',
    label: '异常订单',
    value: formatCount(overview.value.abnormalOrderCount),
    tip: '进入异常处置链路的订单',
    badge: '人工介入',
    tone: 'danger',
    icon: 'ep:warning'
  },
  {
    key: 'riskAlertCount',
    label: '风控预警',
    value: formatCount(overview.value.riskAlertCount),
    tip: '当前风险信号总量',
    badge: '实时监控',
    tone: 'danger',
    icon: 'ep:bell'
  },
  {
    key: 'refundPendingCount',
    label: '退款待办',
    value: formatCount(overview.value.refundPendingCount),
    tip: '退款与售后待跟进工单',
    badge: '售后链路',
    tone: 'info',
    icon: 'ep:refresh-left'
  },
  {
    key: 'pendingRoleApplyCount',
    label: '身份申请待审',
    value: formatCount(overview.value.pendingRoleApplyCount),
    tip: '角色升级申请待人工审核',
    badge: '角色流转',
    tone: 'warning',
    icon: 'ep:user'
  },
  {
    key: 'expiringQualificationCount',
    label: '到期证件提醒',
    value: formatCount(overview.value.expiringQualificationCount),
    tip: '即将到期或已到期的资质提醒',
    badge: '资质风险',
    tone: 'danger',
    icon: 'ep:warning-filled'
  },
  {
    key: 'pendingPriceReportCount',
    label: '价格申报待审',
    value: formatCount(overview.value.pendingPriceReportCount),
    tip: '合作商价格申报待处理',
    badge: '合作商协同',
    tone: 'warning',
    icon: 'ep:money'
  },
  {
    key: 'pendingPushTaskCount',
    label: '待执行推送',
    value: formatCount(overview.value.pendingPushTaskCount),
    tip: '尚未完成的站内消息推送任务',
    badge: '消息链路',
    tone: 'info',
    icon: 'ep:promotion'
  },
  {
    key: 'failedPushTaskCount',
    label: '失败推送',
    value: formatCount(overview.value.failedPushTaskCount),
    tip: '执行失败的推送任务需要排查',
    badge: '失败补发',
    tone: 'danger',
    icon: 'ep:circle-close'
  }
])

const quickActions = computed(() =>
  quickActionSeed.filter((item) => hasPermission(item.permissions))
)

const prioritySeed = computed<PrioritySeed[]>(() => [
  {
    title: '审核清队',
    description: '优先处理实名、资质、入驻、提现审核',
    count: overview.value.pendingAuditCount,
    routes: [
      {
        path: '/linbang-member/member-real-name',
        permission: 'linbang:member-user-real-name:query'
      },
      {
        path: '/linbang-member/member-role-apply',
        permission: 'linbang:member:role-apply:query'
      },
      { path: '/linbang-merchant/merchant-entry', permission: 'linbang:merchant-entry:query' },
      { path: '/linbang-wallet/wallet-withdraw', permission: 'linbang:wallet:withdraw:query' }
    ]
  },
  {
    title: '异常止损',
    description: '关注异常订单和拆单锁单问题',
    count: overview.value.abnormalOrderCount,
    routes: [{ path: '/linbang-order/order-abnormal', permission: 'linbang:order:abnormal:query' }]
  },
  {
    title: '风险复核',
    description: '复盘命中规则的高风险工单',
    count: overview.value.riskAlertCount,
    routes: [{ path: '/linbang-risk/risk-rule', permission: 'linbang:risk-rule:query' }]
  },
  {
    title: '退款售后',
    description: '处理退款审核、投诉和申诉流转',
    count: overview.value.refundPendingCount,
    routes: [
      { path: '/linbang-review/complaint', permission: 'linbang:review:complaint:query' },
      { path: '/linbang-review/appeal', permission: 'linbang:review:appeal:query' },
      { path: '/linbang-wallet/pay-refund', permission: 'pay:refund:query' }
    ]
  },
  {
    title: '价格申报审核',
    description: '复核合作商价格申报与区域价格变动',
    count: overview.value.pendingPriceReportCount,
    routes: [
      {
        path: '/linbang-partner/merchant-price-report',
        permission: 'linbang:partner:price-report:query'
      }
    ]
  },
  {
    title: '消息任务追踪',
    description: '跟进失败或待执行的站内推送任务',
    count: overview.value.pendingPushTaskCount + overview.value.failedPushTaskCount,
    routes: [
      {
        path: '/linbang-message/message-push-task',
        permission: 'linbang:message:push-task:query'
      }
    ]
  }
])

const priorityItems = computed(() =>
  prioritySeed.value
    .map((item) => {
      const accessibleRoute = item.routes.find((route) => hasPermission([route.permission]))
      return accessibleRoute ? { ...item, path: accessibleRoute.path } : undefined
    })
    .filter((item): item is PrioritySeed & { path: string } => Boolean(item))
    .map((item) => {
      const count = toNumber(item.count)
      const level = count > 20 ? '高优先级' : count > 0 ? '处理中' : '已清空'
      const tagType: 'danger' | 'warning' | 'success' =
        count > 20 ? 'danger' : count > 0 ? 'warning' : 'success'
      return { ...item, count, level, tagType }
    })
)

const queueBars = computed(() => {
  const values = [
    overview.value.pendingAuditCount,
    overview.value.pendingRoleApplyCount,
    overview.value.riskAlertCount,
    overview.value.abnormalOrderCount,
    overview.value.refundPendingCount,
    overview.value.pendingPriceReportCount,
    overview.value.failedPushTaskCount
  ].map((item) => toNumber(item))
  const max = Math.max(...values, 1)
  return [
    {
      label: '审核堆积',
      value: toNumber(overview.value.pendingAuditCount),
      percentage: clamp((toNumber(overview.value.pendingAuditCount) / max) * 100, 0, 100),
      color: '#f59e0b'
    },
    {
      label: '身份申请',
      value: toNumber(overview.value.pendingRoleApplyCount),
      percentage: clamp((toNumber(overview.value.pendingRoleApplyCount) / max) * 100, 0, 100),
      color: '#8b5cf6'
    },
    {
      label: '风险预警',
      value: toNumber(overview.value.riskAlertCount),
      percentage: clamp((toNumber(overview.value.riskAlertCount) / max) * 100, 0, 100),
      color: '#ef4444'
    },
    {
      label: '异常订单',
      value: toNumber(overview.value.abnormalOrderCount),
      percentage: clamp((toNumber(overview.value.abnormalOrderCount) / max) * 100, 0, 100),
      color: '#f97316'
    },
    {
      label: '退款待办',
      value: toNumber(overview.value.refundPendingCount),
      percentage: clamp((toNumber(overview.value.refundPendingCount) / max) * 100, 0, 100),
      color: '#0ea5e9'
    },
    {
      label: '价格申报',
      value: toNumber(overview.value.pendingPriceReportCount),
      percentage: clamp((toNumber(overview.value.pendingPriceReportCount) / max) * 100, 0, 100),
      color: '#22c55e'
    },
    {
      label: '推送失败',
      value: toNumber(overview.value.failedPushTaskCount),
      percentage: clamp((toNumber(overview.value.failedPushTaskCount) / max) * 100, 0, 100),
      color: '#f43f5e'
    }
  ]
})

const normalizedCompletionRate = computed(() =>
  clamp(toNumber(overview.value.completionRate), 0, 100)
)

const pressureIndex = computed(() => {
  const score =
    toNumber(overview.value.pendingAuditCount) * 7 +
    toNumber(overview.value.pendingRoleApplyCount) * 5 +
    toNumber(overview.value.riskAlertCount) * 12 +
    toNumber(overview.value.abnormalOrderCount) * 10 +
    toNumber(overview.value.refundPendingCount) * 9 +
    toNumber(overview.value.pendingPriceReportCount) * 6 +
    toNumber(overview.value.failedPushTaskCount) * 4
  return clamp(score, 0, 100)
})

const riskHitRate = computed(() => {
  const denominator = sevenDayTotals.value.orderCount
  if (denominator <= 0) {
    return 0
  }
  return (
    ((toNumber(overview.value.riskAlertCount) + toNumber(overview.value.abnormalOrderCount)) /
      denominator) *
    100
  )
})

const withdrawRate = computed(() => {
  const totalTrade = sevenDayTotals.value.tradeAmount
  if (totalTrade <= 0) {
    return 0
  }
  return (sevenDayTotals.value.withdrawAmount / totalTrade) * 100
})

const pulseStats = computed(() => [
  {
    label: '处置压力指数',
    value: `${pressureIndex.value}`,
    tip: '按审核、预警、异常、退款待办即时换算'
  },
  {
    label: '风险命中率',
    value: formatPercent(riskHitRate.value),
    tip: '按当前风险/异常数量对近 7 日订单估算'
  },
  {
    label: '提现申请占比',
    value: formatPercent(withdrawRate.value),
    tip: '近 7 日提现申请额 / 近 7 日交易额'
  },
  {
    label: '到期资质提醒',
    value: formatCount(overview.value.expiringQualificationCount),
    tip: '需优先推动资质补齐与恢复接单'
  }
])

const trendLabels = computed(() => mergedTrendRows.value.map((item) => item.statDate.slice(5)))

const trendChartOptions = computed<EChartsOption>(() => ({
  color: ['#14b8a6', '#f59e0b', '#38bdf8'],
  grid: { top: 54, left: 28, right: 28, bottom: 28, containLabel: true },
  legend: {
    top: 8,
    textStyle: {
      color: '#cbd5e1',
      fontFamily: "'Bahnschrift', 'Segoe UI', 'PingFang SC', sans-serif"
    }
  },
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    data: trendLabels.value,
    axisLine: { lineStyle: { color: 'rgba(148, 163, 184, 0.35)' } },
    axisLabel: { color: '#cbd5e1' }
  },
  yAxis: [
    {
      type: 'value',
      name: '订单 / 用户',
      minInterval: 1,
      axisLabel: { color: '#cbd5e1' },
      splitLine: { lineStyle: { color: 'rgba(148, 163, 184, 0.12)' } }
    },
    {
      type: 'value',
      name: '交易额',
      axisLabel: { color: '#cbd5e1' },
      splitLine: { show: false }
    }
  ],
  series: [
    {
      name: '订单量',
      type: 'bar',
      barMaxWidth: 22,
      data: mergedTrendRows.value.map((item) => toNumber(item.orderCount)),
      itemStyle: { borderRadius: [8, 8, 0, 0] }
    },
    {
      name: '交易额',
      type: 'line',
      yAxisIndex: 1,
      smooth: true,
      symbolSize: 8,
      data: mergedTrendRows.value.map((item) => toNumber(item.tradeAmount)),
      areaStyle: { color: 'rgba(245, 158, 11, 0.16)' }
    },
    {
      name: '新增用户',
      type: 'line',
      smooth: true,
      symbolSize: 7,
      data: mergedTrendRows.value.map((item) => toNumber(item.newUserCount)),
      areaStyle: { color: 'rgba(56, 189, 248, 0.12)' }
    }
  ]
}))

const pressureChartOptions = computed<EChartsOption>(() => ({
  color: ['#f59e0b', '#ef4444', '#f97316', '#0ea5e9'],
  grid: { top: 16, left: 90, right: 18, bottom: 8, containLabel: true },
  tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
  xAxis: {
    type: 'value',
    splitLine: { lineStyle: { color: '#e2e8f0' } },
    axisLabel: { color: '#64748b' }
  },
  yAxis: {
    type: 'category',
    data: queueBars.value.map((item) => item.label),
    axisLabel: { color: '#0f172a', fontWeight: 600 },
    axisTick: { show: false },
    axisLine: { show: false }
  },
  series: [
    {
      type: 'bar',
      barWidth: 18,
      data: queueBars.value.map((item) => item.value),
      showBackground: true,
      backgroundStyle: { color: '#f1f5f9' },
      itemStyle: {
        borderRadius: [0, 10, 10, 0],
        color: ({ dataIndex }) => queueBars.value[dataIndex]?.color || '#94a3b8'
      }
    }
  ]
}))

const financeChartOptions = computed<EChartsOption>(() => ({
  color: ['#14b8a6', '#f97316'],
  grid: { top: 42, left: 24, right: 18, bottom: 20, containLabel: true },
  legend: {
    top: 6,
    textStyle: {
      color: '#475569',
      fontFamily: "'Bahnschrift', 'Segoe UI', 'PingFang SC', sans-serif"
    }
  },
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    data: trendLabels.value,
    axisTick: { show: false },
    axisLabel: { color: '#64748b' },
    axisLine: { lineStyle: { color: '#cbd5e1' } }
  },
  yAxis: {
    type: 'value',
    axisLabel: { color: '#64748b' },
    splitLine: { lineStyle: { color: '#e2e8f0' } }
  },
  series: [
    {
      name: '交易额',
      type: 'line',
      smooth: true,
      symbolSize: 7,
      data: mergedTrendRows.value.map((item) => toNumber(item.tradeAmount)),
      areaStyle: { color: 'rgba(20, 184, 166, 0.12)' }
    },
    {
      name: '提现申请额',
      type: 'line',
      smooth: true,
      symbolSize: 7,
      data: mergedTrendRows.value.map((item) => toNumber(item.withdrawAmount)),
      areaStyle: { color: 'rgba(249, 115, 22, 0.12)' }
    }
  ]
}))

const getRowFocus = (row: TrendRow): FocusTag[] => {
  const tags: FocusTag[] = []
  if (toNumber(row.tradeAmount) > 0 && toNumber(row.tradeAmount) === peakTradeAmount.value) {
    tags.push({ label: '交易峰值', type: 'success' })
  }
  if (toNumber(row.orderCount) > 0 && toNumber(row.orderCount) === peakOrderCount.value) {
    tags.push({ label: '订单高位', type: 'warning' })
  }
  if (
    toNumber(row.withdrawAmount) > 0 &&
    toNumber(row.withdrawAmount) >= toNumber(row.tradeAmount) * 0.5
  ) {
    tags.push({ label: '提现抬升', type: 'danger' })
  }
  if (toNumber(row.newUserCount) > 0) {
    tags.push({ label: '新增活跃', type: 'info' })
  }
  return tags.length ? tags.slice(0, 2) : [{ label: '低波动', type: 'info' }]
}

const loadDashboard = async () => {
  loading.value = true
  try {
    const [overviewResp, orderResp, financeResp, userResp] = await Promise.all([
      DashboardApi.getOverview(),
      DashboardApi.getOrderStat(),
      DashboardApi.getFinanceStat(),
      DashboardApi.getUserStat()
    ])
    overview.value = overviewResp
    orderTrend.value = orderResp
    financeTrend.value = financeResp
    userTrend.value = userResp
    updatedAt.value = new Date().toLocaleString('zh-CN', { hour12: false })
  } catch (error) {
    console.error(error)
    message.error('平台工作台数据加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDashboard()
})
</script>

<style scoped>
.linbang-dashboard {
  --board-ink: #e2e8f0;
  --board-muted: rgb(226 232 240 / 72%);
  --surface-border: rgb(148 163 184 / 18%);
  --shadow-soft: 0 20px 48px rgb(15 23 42 / 8%);

  display: grid;
  gap: 18px;
}

.dashboard-head {
  position: relative;
  display: grid;
  padding: 20px 22px;
  overflow: hidden;
  background:
    radial-gradient(circle at top left, rgb(20 184 166 / 22%), transparent 34%),
    radial-gradient(circle at right center, rgb(245 158 11 / 20%), transparent 28%),
    linear-gradient(135deg, #04111d 0%, #0b2532 52%, #14384b 100%);
  border-radius: 24px;
  gap: 18px;
}

.dashboard-head::before {
  position: absolute;
  background-image:
    linear-gradient(rgb(255 255 255 / 5%) 1px, transparent 1px),
    linear-gradient(90deg, rgb(255 255 255 / 5%) 1px, transparent 1px);
  background-position: center;
  background-size: 28px 28px;
  content: '';
  inset: 0;
  mask-image: linear-gradient(135deg, rgb(0 0 0 / 95%), transparent 90%);
}

.head-top,
.head-strip {
  position: relative;
  z-index: 1;
}

.eyebrow {
  margin: 0 0 8px;
  font-family: Bahnschrift, 'Segoe UI', 'PingFang SC', sans-serif;
  font-size: 12px;
  letter-spacing: 0.24em;
  color: rgb(186 230 253 / 78%);
  text-transform: uppercase;
}

.head-top,
.head-heading,
.head-toolbar {
  align-items: center;
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.head-heading {
  flex-wrap: wrap;
  justify-content: flex-start;
}

.head-heading h1 {
  margin: 0;
  font-family: Bahnschrift, 'Segoe UI', 'PingFang SC', sans-serif;
  font-size: clamp(26px, 2.2vw, 34px);
  letter-spacing: 0.03em;
  color: #f8fafc;
}

.head-title {
  min-width: 0;
}

.head-toolbar {
  flex-wrap: wrap;
}

.toolbar-meta {
  display: grid;
  gap: 4px;
}

.head-strip {
  display: grid;
  gap: 12px;
  grid-template-columns: repeat(6, minmax(0, 1fr));
}

.strip-card {
  backdrop-filter: blur(12px);
  background: rgb(255 255 255 / 8%);
  border: 1px solid rgb(255 255 255 / 8%);
  border-radius: 18px;
}

.strip-card {
  display: grid;
  gap: 6px;
  min-height: 96px;
  padding: 14px 16px;
}

.strip-card span,
.toolbar-label {
  font-size: 12px;
  color: rgb(226 232 240 / 68%);
}

.strip-card strong,
.toolbar-meta strong {
  font-family: Bahnschrift, 'Segoe UI', 'PingFang SC', sans-serif;
  font-size: 22px;
  font-weight: 700;
  color: #fff;
}

.strip-card small {
  line-height: 1.5;
  color: rgb(226 232 240 / 68%);
}

.strip-card--signal {
  background: rgb(255 255 255 / 10%);
}

.metric-grid {
  display: grid;
  gap: 14px;
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.metric-card,
.surface {
  background:
    linear-gradient(180deg, rgb(255 255 255 / 98%), rgb(248 250 252 / 96%)),
    linear-gradient(135deg, rgb(20 184 166 / 6%), rgb(245 158 11 / 8%));
  border: 1px solid var(--surface-border);
  border-radius: 24px;
  box-shadow: var(--shadow-soft);
}

.metric-card {
  display: grid;
  gap: 14px;
  min-height: 158px;
  padding: 20px;
}

.metric-top,
.metric-bottom,
.surface-head,
.queue-meta,
.priority-title {
  align-items: center;
  display: flex;
  justify-content: space-between;
  gap: 10px;
}

.metric-top span {
  font-size: 13px;
  color: #64748b;
}

.metric-icon {
  font-size: 22px;
  color: #0f766e;
}

.metric-card strong {
  font-family: Bahnschrift, 'Segoe UI', 'PingFang SC', sans-serif;
  font-size: 34px;
  line-height: 1;
  letter-spacing: 0.02em;
  color: #0f172a;
}

.metric-bottom small {
  line-height: 1.5;
  color: #94a3b8;
}

.metric-badge {
  padding: 4px 10px;
  font-size: 12px;
  white-space: nowrap;
  border-radius: 999px;
}

.metric-badge--info {
  color: #0369a1;
  background: rgb(14 165 233 / 12%);
}

.metric-badge--success {
  color: #047857;
  background: rgb(16 185 129 / 12%);
}

.metric-badge--warning {
  color: #b45309;
  background: rgb(245 158 11 / 12%);
}

.metric-badge--danger {
  color: #b91c1c;
  background: rgb(239 68 68 / 12%);
}

.board-grid {
  display: grid;
  gap: 18px;
  grid-template-columns: repeat(12, minmax(0, 1fr));
}

.surface {
  display: grid;
  gap: 18px;
  grid-column: span 4;
  padding: 22px;
}

.surface--trend {
  color: #fff;
  background:
    radial-gradient(circle at top left, rgb(20 184 166 / 18%), transparent 32%),
    linear-gradient(160deg, #071827 0%, #0f2435 100%);
  grid-column: span 8;
}

.surface--command {
  grid-column: span 4;
}

.surface--table {
  grid-column: 1 / -1;
}

.surface-head h3 {
  margin: 0;
  font-size: 20px;
  color: inherit;
}

.surface-head p {
  margin: 6px 0 0;
  font-size: 13px;
  color: inherit;
  opacity: 0.7;
}

.surface:not(.surface--trend) .surface-head h3 {
  color: #0f172a;
}

.surface:not(.surface--trend) .surface-head p {
  color: #64748b;
}

.priority-list,
.queue-stack {
  display: grid;
  gap: 12px;
}

.priority-item,
.action-card {
  display: flex;
  padding: 16px 18px;
  text-align: left;
  cursor: pointer;
  background: rgb(15 23 42 / 3%);
  border: 1px solid rgb(148 163 184 / 18%);
  border-radius: 18px;
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    transform 0.2s ease;
  align-items: center;
  justify-content: space-between;
}

.priority-item:hover,
.action-card:hover {
  border-color: rgb(14 165 233 / 32%);
  transform: translateY(-1px);
  box-shadow: 0 12px 28px rgb(15 23 42 / 8%);
}

.priority-item p,
.action-card small,
.pulse-line small {
  margin: 6px 0 0;
  color: #64748b;
}

.priority-item strong {
  font-family: Bahnschrift, 'Segoe UI', 'PingFang SC', sans-serif;
  font-size: 30px;
  color: #0f172a;
}

.action-grid {
  display: grid;
  gap: 12px;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.action-card {
  align-items: flex-start;
  gap: 12px;
  justify-content: flex-start;
  min-height: 102px;
}

.action-card span {
  display: block;
  font-size: 15px;
  font-weight: 600;
  color: #0f172a;
}

.action-icon {
  margin-top: 2px;
  font-size: 22px;
  color: #0f766e;
}

.queue-item {
  display: grid;
  gap: 8px;
}

.queue-meta span {
  color: #475569;
}

.queue-meta strong,
.pulse-line strong {
  font-family: Bahnschrift, 'Segoe UI', 'PingFang SC', sans-serif;
  font-size: 18px;
  color: #0f172a;
}

.mini-summary,
.pulse-grid {
  display: grid;
  gap: 12px;
}

.mini-summary {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.mini-summary__item,
.pulse-card {
  padding: 16px;
  background: rgb(15 23 42 / 3%);
  border: 1px solid rgb(148 163 184 / 16%);
  border-radius: 18px;
}

.mini-summary__item span,
.pulse-card__copy span,
.pulse-line span {
  display: block;
  font-size: 13px;
  color: #64748b;
}

.mini-summary__item strong,
.pulse-card__copy strong {
  display: block;
  margin-top: 10px;
  font-family: Bahnschrift, 'Segoe UI', 'PingFang SC', sans-serif;
  font-size: 24px;
  color: #0f172a;
}

.pulse-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.pulse-card {
  align-items: center;
  display: flex;
  gap: 18px;
}

.pulse-card__copy small {
  display: block;
  margin-top: 8px;
  color: #94a3b8;
}

.pulse-card--stack {
  display: grid;
  gap: 16px;
}

.pulse-line {
  padding-bottom: 12px;
  border-bottom: 1px dashed rgb(148 163 184 / 26%);
}

.pulse-line:last-child {
  padding-bottom: 0;
  border-bottom: none;
}

.focus-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

@media (width <= 1400px) {
  .metric-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .head-strip {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .surface--trend,
  .surface--command {
    grid-column: span 6;
  }

  .surface {
    grid-column: span 6;
  }
}

@media (width <= 1080px) {
  .head-top {
    align-items: flex-start;
    flex-direction: column;
  }

  .head-strip,
  .pulse-grid,
  .mini-summary,
  .metric-grid,
  .action-grid {
    grid-template-columns: 1fr;
  }

  .board-grid {
    grid-template-columns: 1fr;
  }

  .surface--trend,
  .surface--command,
  .surface--table,
  .surface {
    grid-column: auto;
  }
}

@media (width <= 720px) {
  .dashboard-head,
  .surface,
  .metric-card {
    padding: 18px;
    border-radius: 20px;
  }

  .toolbar-card,
  .pulse-card,
  .priority-item,
  .action-card {
    align-items: flex-start;
    flex-direction: column;
  }

  .toolbar-card {
    gap: 12px;
  }
}
</style>
