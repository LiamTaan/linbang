<template>
  <div class="linbang-dashboard">
    <section class="overview-section">
      <div class="section-title section-title--light">
        <div class="section-title__main">
          <Icon icon="ep:data-analysis" class="section-title__icon" />
          <div>
            <h2>今日数据概览</h2>
            <p>管理端实时工作台，聚焦订单、审核、风控与资金链路</p>
          </div>
        </div>
        <div class="overview-toolbar">
          <span>Data updated: {{ updatedAtLabel }}</span>
          <el-button :loading="loading" plain type="primary" @click="loadDashboard">
            <Icon icon="ep:refresh" class="mr-6px" />
            刷新看板
          </el-button>
        </div>
      </div>

      <div class="overview-grid">
        <article
          v-for="card in desktopSummaryCards"
          :key="card.label"
          :class="['overview-card', `overview-card--${card.tone}`]"
        >
          <div class="overview-card__top">
            <span>{{ card.label }}</span>
            <em :class="['overview-card__trend', `is-${card.trendTone}`]">{{ card.trend }}</em>
          </div>
          <strong>{{ card.value }}</strong>
          <small>{{ card.tip }}</small>
          <div class="overview-card__track">
            <span :style="{ width: `${card.progress}%` }"></span>
          </div>
        </article>
      </div>
    </section>

    <section class="command-layout">
      <div class="surface surface--command-center">
        <div class="surface-head surface-head--flat">
          <div class="section-title__main">
            <Icon icon="ep:operation" class="surface-head__icon" />
            <div>
              <h3>待办指挥舱</h3>
              <p>直接落到真实业务处理页面</p>
            </div>
          </div>
          <span class="surface-head__link">Batch Process</span>
        </div>

        <div v-if="desktopCommandItems.length" class="command-list">
          <button
            v-for="item in desktopCommandItems"
            :key="item.title"
            class="command-item"
            type="button"
            @click="router.push(item.path)"
          >
            <div :class="['command-item__icon', `is-${item.accent}`]">
              <Icon :icon="item.icon" />
            </div>
            <div class="command-item__body">
              <strong>{{ item.title }}</strong>
              <p>{{ item.description }}</p>
            </div>
            <div class="command-item__meta">
              <strong>{{ formatCount(item.count) }}</strong>
              <span>{{ item.metricLabel }}</span>
            </div>
            <em :class="['command-item__badge', `is-${item.badgeTone}`]">{{ item.statusText }}</em>
            <Icon icon="ep:arrow-right" class="command-item__arrow" />
          </button>
        </div>
        <el-empty v-else description="当前账号暂无工作台快捷入口" />
      </div>

      <div class="surface surface--pressure-panel">
        <div class="surface-head surface-head--inverse">
          <div class="section-title__main">
            <Icon icon="ep:histogram" class="surface-head__icon" />
            <div>
              <h3>运营压力监测</h3>
              <p>审核、身份申请与风险预警的即时负载</p>
            </div>
          </div>
        </div>

        <div class="pressure-list">
          <article v-for="item in desktopPressureItems" :key="item.label" class="pressure-item">
            <div class="pressure-item__top">
              <span>{{ item.label }}</span>
              <strong>{{ Math.round(item.percentage) }}%</strong>
            </div>
            <div class="pressure-item__bar">
              <span :style="{ width: `${item.percentage}%`, background: item.color }"></span>
            </div>
          </article>
        </div>

        <div class="pressure-footer">
          <article>
            <span>Queue Status</span>
            <strong>{{ queueStatusLabel }}</strong>
          </article>
          <article>
            <span>Risk Level</span>
            <strong>{{ riskLevelLabel }}</strong>
          </article>
        </div>
      </div>
    </section>

    <section class="insight-layout">
      <div class="surface surface--world">
        <div class="surface-head surface-head--inverse">
          <div>
            <h3>平台交易流</h3>
            <p>近 7 日订单、交易额与新增用户趋势</p>
          </div>
        </div>
        <el-skeleton :loading="loading" animated>
          <Echart :options="trendChartOptions" :height="320" />
        </el-skeleton>
        <div class="world-meta">
          <span>Active Nodes: {{ formatCount(sevenDayTotals.orderCount) }}</span>
          <span>Latency: {{ formatPercent(withdrawRate) }}</span>
        </div>
      </div>

      <div class="surface surface--health">
        <div class="surface-head surface-head--flat">
          <div>
            <h3>System Health Summary</h3>
            <p>核心履约、提现与风控指标的快速判读</p>
          </div>
        </div>
        <div class="health-grid">
          <article
            v-for="item in desktopHealthStats"
            :key="item.label"
            :class="['health-item', `health-item--${item.tone}`]"
          >
            <div class="health-item__ring">
              <strong>{{ item.value }}</strong>
            </div>
            <span>{{ item.label }}</span>
          </article>
        </div>
      </div>
    </section>

    <section class="signal-layout">
      <div class="surface surface--signal-cluster">
        <div class="surface-head surface-head--flat">
          <div>
            <h3>重点运营信号</h3>
            <p>把最需要人工介入的事项压到一屏内快速判读</p>
          </div>
          <el-tag round type="warning">6 Key Signals</el-tag>
        </div>

        <div class="signal-grid">
          <article v-for="card in focusMetricCards" :key="card.key" class="signal-card">
            <div class="signal-card__head">
              <span :class="['signal-card__icon', `signal-card__icon--${card.tone}`]">
                <Icon :icon="card.icon" />
              </span>
              <span :class="['metric-badge', `metric-badge--${card.tone}`]">{{ card.badge }}</span>
            </div>
            <div class="signal-card__main">
              <strong>{{ card.value }}</strong>
              <h4>{{ card.label }}</h4>
            </div>
            <p>{{ card.tip }}</p>
          </article>
        </div>
      </div>

      <aside class="surface surface--signal-rail">
        <div class="surface-head surface-head--flat">
          <div>
            <h3>其他提醒</h3>
            <p>低频协同项收成提醒列，避免再铺 9 张同质卡片</p>
          </div>
          <el-tag round type="info">{{ compactSignalCards.length }} Items</el-tag>
        </div>

        <div class="signal-rail">
          <article v-for="card in compactSignalCards" :key="card.key" class="signal-rail__item">
            <div class="signal-rail__meta">
              <span :class="['signal-rail__dot', `signal-rail__dot--${card.tone}`]"></span>
              <div>
                <strong>{{ card.label }}</strong>
                <p>{{ card.tip }}</p>
              </div>
            </div>
            <div class="signal-rail__value">
              <strong>{{ card.value }}</strong>
              <span :class="['metric-badge', `metric-badge--${card.tone}`]">{{ card.badge }}</span>
            </div>
          </article>
        </div>
      </aside>
    </section>

    <section class="chart-layout">
      <div class="surface">
        <div class="surface-head surface-head--flat">
          <div>
            <h3>资金脉冲</h3>
            <p>交易额与提现申请额对照</p>
          </div>
          <el-tag round type="success">资金链路</el-tag>
        </div>
        <el-skeleton :loading="loading" animated>
          <Echart :options="financeChartOptions" :height="280" />
        </el-skeleton>
      </div>

      <div class="surface">
        <div class="surface-head surface-head--flat">
          <div>
            <h3>运行脉冲</h3>
            <p>对当前队列和近 7 日订单做快速判读</p>
          </div>
          <el-tag round type="info">运营判读</el-tag>
        </div>

        <div class="pulse-grid">
          <article class="pulse-card">
            <el-progress
              color="#1768c6"
              :percentage="normalizedCompletionRate"
              :stroke-width="12"
              type="circle"
              :width="118"
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
    </section>

    <section class="surface surface--table">
      <div class="surface-head surface-head--flat">
        <div>
          <h3>近 7 日运行明细</h3>
          <p>真实聚合字段落表，便于值班复盘</p>
        </div>
        <el-tag round type="info">{{ mergedTrendRows.length }} 天</el-tag>
      </div>
      <div class="table-shell">
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

interface DashboardSummaryCard {
  label: string
  value: string
  tip: string
  trend: string
  progress: number
  tone: 'blue' | 'green' | 'slate' | 'danger'
  trendTone: 'rise' | 'fall' | 'neutral'
}

interface DashboardCommandItem extends PrioritySeed {
  path: string
  icon: string
  accent: 'blue' | 'red' | 'cyan' | 'slate'
  badgeTone: 'danger' | 'warning' | 'info' | 'neutral'
  metricLabel: string
  statusText: string
}

interface DashboardHealthStat {
  label: string
  value: string
  tone: 'blue' | 'navy' | 'slate'
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

const priorityVisualMap: Record<string, { icon: string; accent: DashboardCommandItem['accent'] }> =
  {
    审核清队: { icon: 'ep:checked', accent: 'blue' },
    异常止损: { icon: 'ep:warning-filled', accent: 'cyan' },
    风险复核: { icon: 'ep:bell-filled', accent: 'red' },
    退款售后: { icon: 'ep:refresh-left', accent: 'slate' },
    价格申报审核: { icon: 'ep:money', accent: 'blue' },
    消息任务追踪: { icon: 'ep:promotion', accent: 'cyan' }
  }

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

const peakNewUserCount = computed(() =>
  mergedTrendRows.value.reduce((max, item) => Math.max(max, toNumber(item.newUserCount)), 0)
)

const deltaInfo = (field: keyof TrendRow) => {
  const rows = mergedTrendRows.value
  if (rows.length < 2) {
    return { text: '刚同步', tone: 'neutral' as const }
  }
  const latest = toNumber(rows[rows.length - 1][field])
  const previous = toNumber(rows[rows.length - 2][field])
  if (previous === 0) {
    return latest === 0
      ? { text: '持平', tone: 'neutral' as const }
      : { text: '↑ 新高', tone: 'rise' as const }
  }
  const ratio = ((latest - previous) / previous) * 100
  if (Math.abs(ratio) < 0.05) {
    return { text: '持平', tone: 'neutral' as const }
  }
  return {
    text: `${ratio > 0 ? '↑' : '↓'} ${Math.abs(ratio).toFixed(1)}%`,
    tone: ratio > 0 ? ('rise' as const) : ('fall' as const)
  }
}

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

const updatedAtLabel = computed(() => updatedAt.value || '等待同步')

const desktopSummaryCards = computed<DashboardSummaryCard[]>(() => {
  const tradeDelta = deltaInfo('tradeAmount')
  const orderDelta = deltaInfo('orderCount')
  const userDelta = deltaInfo('newUserCount')
  const completionBenchmark = 85
  const completionGap = normalizedCompletionRate.value - completionBenchmark
  return [
    {
      label: '今日交易额',
      value: formatCurrency(overview.value.todayTradeAmount),
      tip: '支付与履约联动金额',
      trend: tradeDelta.text,
      progress: clamp(
        peakTradeAmount.value <= 0
          ? 0
          : (toNumber(overview.value.todayTradeAmount) / peakTradeAmount.value) * 100,
        6,
        100
      ),
      tone: 'blue',
      trendTone: tradeDelta.tone
    },
    {
      label: '今日订单量',
      value: formatCount(overview.value.todayOrderCount),
      tip: '近 7 日日峰值对照',
      trend: orderDelta.text,
      progress: clamp(
        peakOrderCount.value <= 0
          ? 0
          : (toNumber(overview.value.todayOrderCount) / peakOrderCount.value) * 100,
        6,
        100
      ),
      tone: 'green',
      trendTone: orderDelta.tone
    },
    {
      label: '今日新增用户',
      value: formatCount(overview.value.todayNewUserCount),
      tip: '拉新转化即时表现',
      trend: userDelta.text,
      progress: clamp(
        peakNewUserCount.value <= 0
          ? 0
          : (toNumber(overview.value.todayNewUserCount) / peakNewUserCount.value) * 100,
        6,
        100
      ),
      tone: 'blue',
      trendTone: userDelta.tone
    },
    {
      label: '订单完成率',
      value: formatPercent(overview.value.completionRate),
      tip: `履约基准 ${completionBenchmark}%`,
      trend:
        completionGap >= 0
          ? `↑ 高于基准 ${completionGap.toFixed(1)}%`
          : `↓ 低于基准 ${Math.abs(completionGap).toFixed(1)}%`,
      progress: clamp(normalizedCompletionRate.value, 6, 100),
      tone: completionGap >= 0 ? 'slate' : 'danger',
      trendTone: completionGap >= 0 ? 'rise' : 'fall'
    }
  ]
})

const secondaryMetricCards = computed(() => metricCards.value.slice(4))

const focusMetricCardKeySet = new Set([
  'pendingAuditCount',
  'abnormalOrderCount',
  'riskAlertCount',
  'refundPendingCount',
  'pendingRoleApplyCount',
  'pendingPriceReportCount'
])

const focusMetricCards = computed(() =>
  secondaryMetricCards.value.filter((card) => focusMetricCardKeySet.has(card.key))
)

const compactSignalCards = computed(() =>
  secondaryMetricCards.value.filter((card) => !focusMetricCardKeySet.has(card.key))
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

const desktopCommandItems = computed<DashboardCommandItem[]>(() =>
  priorityItems.value.slice(0, 4).map((item) => {
    const count = toNumber(item.count)
    const visual = priorityVisualMap[item.title] ?? {
      icon: 'ep:data-analysis',
      accent: 'blue' as const
    }
    return {
      ...item,
      icon: visual.icon,
      accent: visual.accent,
      badgeTone: count > 20 ? 'danger' : count > 0 ? 'warning' : 'neutral',
      metricLabel:
        item.title === '审核清队'
          ? 'Pending Cases'
          : item.title === '风险复核'
            ? 'Risk Alerts'
            : item.title === '异常止损'
              ? 'Anomalies'
              : 'Open Tickets',
      statusText: count > 20 ? '需要清队' : count > 0 ? '处理中' : '队列正常'
    }
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

const desktopPressureItems = computed(() =>
  queueBars.value.filter((item) => ['审核堆积', '身份申请', '风险预警'].includes(item.label))
)

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

const queueStatusLabel = computed(() => {
  if (pressureIndex.value >= 80) {
    return '高负载'
  }
  if (pressureIndex.value >= 45) {
    return '处理中'
  }
  return '平稳'
})

const riskLevelLabel = computed(() => {
  if (riskHitRate.value >= 12) {
    return '高风险'
  }
  if (riskHitRate.value >= 6) {
    return '中风险'
  }
  return '低风险'
})

const desktopHealthStats = computed<DashboardHealthStat[]>(() => [
  {
    label: '履约完成率',
    value: formatPercent(overview.value.completionRate),
    tone: 'blue'
  },
  {
    label: '提现申请占比',
    value: formatPercent(withdrawRate.value),
    tone: 'navy'
  },
  {
    label: '风险命中率',
    value: formatPercent(riskHitRate.value),
    tone: 'slate'
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
  --surface-border: rgb(15 23 42 / 8%);
  --shadow-soft: 0 18px 42px rgb(15 23 42 / 8%);
  --blue-600: #1464d2;
  --blue-700: #123d88;
  --blue-900: #0e2c64;
  --text-main: #183153;
  --text-soft: #7b8aa5;
  --line-soft: #dbe4f1;

  display: grid;
  gap: 18px;
}

.overview-section,
.surface {
  background:
    linear-gradient(180deg, rgb(255 255 255 / 99%), rgb(248 250 252 / 97%)),
    linear-gradient(135deg, rgb(20 119 216 / 4%), rgb(12 74 110 / 6%));
  border: 1px solid var(--surface-border);
  border-radius: 24px;
  box-shadow: var(--shadow-soft);
}

.overview-section,
.surface {
  display: grid;
  gap: 18px;
  padding: 22px;
}

.section-title,
.surface-head,
.overview-toolbar,
.overview-card__top,
.pressure-item__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.section-title__main {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.section-title__icon,
.surface-head__icon {
  margin-top: 2px;
  font-size: 22px;
  color: var(--blue-600);
}

.section-title h2,
.surface-head h3 {
  margin: 0;
  font-family: Bahnschrift, 'Segoe UI', 'PingFang SC', sans-serif;
  color: var(--text-main);
}

.section-title h2 {
  font-size: 22px;
}

.section-title p,
.surface-head p {
  margin: 6px 0 0;
  font-size: 13px;
  line-height: 1.6;
  color: var(--text-soft);
}

.overview-toolbar {
  flex-wrap: wrap;
}

.overview-toolbar span {
  font-size: 12px;
  color: var(--text-soft);
}

.overview-grid,
.chart-layout,
.signal-grid,
.signal-rail {
  display: grid;
  gap: 16px;
}

.overview-grid {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.overview-card {
  position: relative;
  display: grid;
  gap: 8px;
  min-height: 136px;
  padding: 16px 18px;
  overflow: hidden;
  background: #fff;
  border: 1px solid var(--line-soft);
  border-radius: 20px;
}

.overview-card::before {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  width: 4px;
  background: var(--blue-600);
  content: '';
}

.overview-card--green::before {
  background: #16a34a;
}

.overview-card--slate::before {
  background: #67748a;
}

.overview-card--danger::before {
  background: #dc2626;
}

.overview-card__top span,
.overview-card small {
  font-size: 12px;
  color: var(--text-soft);
}

.overview-card__trend {
  font-size: 12px;
  font-style: normal;
  font-weight: 700;
}

.overview-card__trend.is-rise {
  color: #16a34a;
}

.overview-card__trend.is-fall {
  color: #dc2626;
}

.overview-card__trend.is-neutral {
  color: #64748b;
}

.overview-card strong {
  font-family: Bahnschrift, 'Segoe UI', 'PingFang SC', sans-serif;
  font-size: 40px;
  line-height: 1;
  color: var(--text-main);
}

.overview-card__track {
  height: 4px;
  margin-top: auto;
  overflow: hidden;
  background: #e6edf7;
  border-radius: 999px;
}

.overview-card__track span {
  display: block;
  height: 100%;
  background: linear-gradient(90deg, var(--blue-600), #73aef7);
  border-radius: inherit;
}

.overview-card--green .overview-card__track span {
  background: linear-gradient(90deg, #16a34a, #7ddf99);
}

.overview-card--slate .overview-card__track span {
  background: linear-gradient(90deg, #67748a, #b0b8c6);
}

.overview-card--danger .overview-card__track span {
  background: linear-gradient(90deg, #dc2626, #f59e9e);
}

.command-layout,
.insight-layout {
  display: grid;
  gap: 18px;
}

.command-layout {
  grid-template-columns: minmax(0, 2.3fr) minmax(320px, 1fr);
}

.insight-layout,
.chart-layout {
  grid-template-columns: minmax(0, 1.35fr) minmax(320px, 1fr);
}

.surface--command-center {
  background: linear-gradient(180deg, #fbfdff, #f5f8fd);
}

.surface--pressure-panel,
.surface--world {
  color: #fff;
  background:
    radial-gradient(circle at top left, rgb(56 189 248 / 15%), transparent 30%),
    linear-gradient(160deg, var(--blue-700) 0%, var(--blue-900) 100%);
}

.surface-head--inverse h3,
.surface-head--inverse p,
.surface--world .world-meta span,
.pressure-item__top span,
.pressure-footer span {
  color: rgb(255 255 255 / 78%);
}

.surface-head--inverse h3,
.pressure-item__top strong,
.pressure-footer strong {
  color: #fff;
}

.surface-head--flat {
  align-items: flex-start;
}

.surface-head__link {
  font-size: 12px;
  color: var(--blue-700);
}

.command-list,
.pressure-list {
  display: grid;
  gap: 12px;
}

.command-item {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto auto auto;
  gap: 14px;
  align-items: center;
  padding: 16px;
  text-align: left;
  cursor: pointer;
  background: #fff;
  border: 1px solid var(--line-soft);
  border-radius: 18px;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease,
    border-color 0.2s ease;
}

.command-item:hover {
  border-color: rgb(20 100 210 / 22%);
  transform: translateY(-1px);
  box-shadow: 0 12px 28px rgb(15 23 42 / 6%);
}

.command-item__icon {
  display: grid;
  place-items: center;
  width: 44px;
  height: 44px;
  font-size: 20px;
  border-radius: 50%;
}

.command-item__icon.is-blue {
  color: var(--blue-600);
  background: rgb(20 100 210 / 10%);
}

.command-item__icon.is-red {
  color: #cf2f25;
  background: rgb(207 47 37 / 10%);
}

.command-item__icon.is-cyan {
  color: #1076b8;
  background: rgb(16 118 184 / 10%);
}

.command-item__icon.is-slate {
  color: #67748a;
  background: rgb(103 116 138 / 12%);
}

.command-item__body strong {
  display: block;
  font-size: 18px;
  color: var(--text-main);
}

.command-item__body p {
  margin: 6px 0 0;
  font-size: 13px;
  line-height: 1.6;
  color: var(--text-soft);
}

.command-item__meta {
  min-width: 84px;
  text-align: center;
}

.command-item__meta strong {
  display: block;
  font-family: Bahnschrift, 'Segoe UI', 'PingFang SC', sans-serif;
  font-size: 34px;
  line-height: 1;
  color: var(--blue-700);
}

.command-item__meta span {
  display: block;
  margin-top: 6px;
  font-size: 11px;
  color: var(--text-soft);
}

.command-item__badge {
  padding: 8px 14px;
  font-size: 13px;
  font-style: normal;
  font-weight: 700;
  white-space: nowrap;
  border-radius: 999px;
}

.command-item__badge.is-danger {
  color: #fff;
  background: #cf2f25;
}

.command-item__badge.is-warning {
  color: #8a4b00;
  background: rgb(245 158 11 / 18%);
}

.command-item__badge.is-info {
  color: #155eef;
  background: rgb(21 94 239 / 10%);
}

.command-item__badge.is-neutral {
  color: #667085;
  background: rgb(148 163 184 / 15%);
}

.command-item__arrow {
  font-size: 18px;
  color: #93a2b9;
}

.pressure-item {
  display: grid;
  gap: 8px;
}

.pressure-item__top strong {
  font-family: Bahnschrift, 'Segoe UI', 'PingFang SC', sans-serif;
  font-size: 28px;
}

.pressure-item__bar {
  height: 8px;
  overflow: hidden;
  background: rgb(255 255 255 / 14%);
  border-radius: 999px;
}

.pressure-item__bar span {
  display: block;
  height: 100%;
  border-radius: inherit;
}

.pressure-footer {
  display: grid;
  gap: 12px;
  margin-top: auto;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.pressure-footer article {
  display: grid;
  gap: 6px;
  padding: 16px 14px;
  background: rgb(255 255 255 / 8%);
  border: 1px solid rgb(255 255 255 / 8%);
  border-radius: 16px;
}

.world-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  font-size: 12px;
}

.health-grid,
.pulse-grid {
  display: grid;
  gap: 16px;
}

.health-grid {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.health-item {
  display: grid;
  justify-items: center;
  gap: 10px;
}

.health-item__ring {
  display: grid;
  place-items: center;
  width: 96px;
  height: 96px;
  background: #fff;
  border: 8px solid var(--blue-600);
  border-radius: 50%;
}

.health-item--navy .health-item__ring {
  border-color: var(--blue-700);
}

.health-item--slate .health-item__ring {
  border-color: #8f98a8;
}

.health-item__ring strong {
  font-family: Bahnschrift, 'Segoe UI', 'PingFang SC', sans-serif;
  font-size: 28px;
  color: var(--text-main);
}

.health-item span {
  font-size: 12px;
  color: var(--text-soft);
}

.signal-layout {
  display: grid;
  gap: 16px;
  grid-template-columns: minmax(0, 1.7fr) minmax(300px, 0.85fr);
}

.surface--signal-cluster {
  gap: 20px;
  background:
    linear-gradient(180deg, rgb(255 255 255 / 99%), rgb(247 250 255 / 98%)),
    radial-gradient(circle at top right, rgb(20 100 210 / 10%), transparent 32%);
}

.surface--signal-rail {
  gap: 18px;
  background:
    linear-gradient(180deg, #f8fbff, #f4f8fd),
    linear-gradient(135deg, rgb(14 165 233 / 6%), rgb(15 23 42 / 4%));
}

.signal-grid {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.signal-card {
  display: grid;
  gap: 12px;
  min-height: 148px;
  padding: 18px;
  background: linear-gradient(180deg, #fff, #f8fbff);
  border: 1px solid rgb(219 228 241 / 90%);
  border-radius: 20px;
  transition:
    transform 0.2s ease,
    border-color 0.2s ease,
    box-shadow 0.2s ease;
}

.signal-card:hover {
  border-color: rgb(20 100 210 / 18%);
  transform: translateY(-1px);
  box-shadow: 0 16px 28px rgb(15 23 42 / 6%);
}

.signal-card__head,
.signal-rail__item,
.signal-rail__meta,
.signal-rail__value {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.signal-card__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  font-size: 18px;
  border-radius: 12px;
}

.signal-card__icon--info {
  color: #0369a1;
  background: rgb(14 165 233 / 12%);
}

.signal-card__icon--success {
  color: #047857;
  background: rgb(16 185 129 / 12%);
}

.signal-card__icon--warning {
  color: #b45309;
  background: rgb(245 158 11 / 14%);
}

.signal-card__icon--danger {
  color: #b91c1c;
  background: rgb(239 68 68 / 12%);
}

.signal-card__main {
  display: grid;
  gap: 4px;
}

.signal-card__main strong,
.signal-rail__value strong {
  font-family: Bahnschrift, 'Segoe UI', 'PingFang SC', sans-serif;
  line-height: 1;
  color: var(--text-main);
}

.signal-card__main strong {
  font-size: 34px;
}

.signal-card__main h4,
.signal-rail__meta strong {
  margin: 0;
  font-size: 16px;
  color: var(--text-main);
}

.signal-card p,
.signal-rail__meta p {
  margin: 0;
  font-size: 13px;
  line-height: 1.6;
  color: var(--text-soft);
}

.signal-rail {
  gap: 12px;
}

.signal-rail__item {
  padding: 14px 16px;
  background: rgb(255 255 255 / 84%);
  border: 1px solid rgb(219 228 241 / 90%);
  border-radius: 18px;
}

.signal-rail__meta {
  align-items: flex-start;
  justify-content: flex-start;
  min-width: 0;
  flex: 1;
}

.signal-rail__meta > div {
  min-width: 0;
}

.signal-rail__dot {
  width: 10px;
  height: 10px;
  margin-top: 5px;
  flex: 0 0 auto;
  border-radius: 999px;
}

.signal-rail__dot--info {
  background: #0ea5e9;
}

.signal-rail__dot--success {
  background: #10b981;
}

.signal-rail__dot--warning {
  background: #f59e0b;
}

.signal-rail__dot--danger {
  background: #ef4444;
}

.signal-rail__value {
  flex-direction: column;
  align-items: flex-end;
  gap: 10px;
}

.signal-rail__value strong {
  font-size: 30px;
}

.metric-badge {
  padding: 6px 12px;
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

.pulse-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.pulse-card {
  display: flex;
  gap: 18px;
  align-items: center;
  padding: 16px;
  background: rgb(15 23 42 / 3%);
  border: 1px solid rgb(148 163 184 / 16%);
  border-radius: 18px;
}

.pulse-card__copy span,
.pulse-line span {
  display: block;
  font-size: 13px;
  color: var(--text-soft);
}

.pulse-card__copy strong,
.pulse-line strong {
  display: block;
  margin-top: 8px;
  font-family: Bahnschrift, 'Segoe UI', 'PingFang SC', sans-serif;
  font-size: 24px;
  color: var(--text-main);
}

.pulse-card__copy small,
.pulse-line small {
  display: block;
  margin-top: 8px;
  color: #96a3b8;
}

.pulse-card--stack {
  display: grid;
  gap: 14px;
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

.table-shell {
  width: 100%;
  overflow-x: auto;
}

.table-shell :deep(.el-table) {
  min-width: 760px;
}

@media (width <= 1280px) {
  .overview-grid,
  .signal-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .command-layout,
  .insight-layout,
  .chart-layout,
  .signal-layout {
    grid-template-columns: 1fr;
  }

  .command-item {
    grid-template-columns: auto minmax(0, 1fr) auto;
  }

  .command-item__badge,
  .command-item__arrow {
    display: none;
  }
}

@media (width <= 900px) {
  .linbang-dashboard {
    gap: 14px;
  }

  .overview-section,
  .surface,
  .signal-card {
    padding: 18px;
    border-radius: 20px;
  }

  .section-title,
  .surface-head,
  .overview-toolbar,
  .command-item,
  .pulse-card,
  .pressure-footer {
    align-items: flex-start;
    flex-direction: column;
  }

  .overview-grid,
  .signal-grid,
  .health-grid,
  .pulse-grid,
  .signal-rail {
    grid-template-columns: 1fr;
  }

  .command-item {
    grid-template-columns: 1fr;
  }

  .command-item__meta {
    text-align: left;
  }

  .health-item {
    justify-items: flex-start;
  }

  .signal-rail__item,
  .signal-rail__value {
    align-items: flex-start;
  }

  .signal-rail__item {
    flex-direction: column;
  }

  .table-shell {
    margin: 0 -4px;
  }
}
</style>
