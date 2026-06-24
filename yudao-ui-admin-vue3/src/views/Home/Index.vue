<template>
  <div class="home-page">
    <section class="hero">
      <div class="hero-copy">
        <p class="eyebrow">邻里互助平台工作台</p>
        <h1>欢迎回来，{{ displayName }}</h1>
        <p class="summary">
          当前管理端统一收口为平台工作台，聚焦系统配置、权限治理、业务审核与审计留痕，
          支撑邻里互助核心模块的真实接入与联动。
        </p>
        <div class="hero-meta">
          <span>{{ todayText }}</span>
          <span>默认租户</span>
          <span>系统最小基线</span>
        </div>
      </div>
      <div class="hero-actions">
        <button class="primary-action" type="button" @click="go('/system/menu')">
          <Icon icon="ep:menu" class="action-icon" />
          <span>进入菜单管理</span>
        </button>
        <button class="secondary-action" type="button" @click="go('/system/user')">
          <Icon icon="ep:user" class="action-icon" />
          <span>查看管理员账号</span>
        </button>
      </div>
    </section>

    <section v-for="section in sections" :key="section.title" class="module-section">
      <div class="section-head">
        <div>
          <h2>{{ section.title }}</h2>
          <p>{{ section.description }}</p>
        </div>
      </div>
      <div class="entry-grid">
        <button
          v-for="entry in section.items"
          :key="entry.path"
          class="entry-card"
          type="button"
          @click="go(entry.path)"
        >
          <div class="entry-main">
            <span class="entry-title">{{ entry.title }}</span>
            <Icon :icon="entry.icon" class="entry-icon" />
          </div>
          <p class="entry-desc">{{ entry.description }}</p>
        </button>
      </div>
    </section>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'

defineOptions({ name: 'Index' })

const router = useRouter()
const userStore = useUserStore()

const displayName = computed(() => userStore.getUser.nickname || '管理员')

const todayText = computed(() =>
  new Intl.DateTimeFormat('zh-CN', {
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  }).format(new Date())
)

const sections = [
  {
    title: '系统权限',
    description: '维护当前阶段保留的管理员、角色、菜单与组织结构。',
    items: [
      {
        title: '用户管理',
        description: '当前仅保留 admin 管理员账号。',
        path: '/system/user',
        icon: 'ep:user'
      },
      {
        title: '角色管理',
        description: '保留最小权限角色集合，便于后续扩展。',
        path: '/system/role',
        icon: 'ep:avatar'
      },
      {
        title: '菜单管理',
        description: '当前侧边栏展示由数据库菜单和角色权限共同决定。',
        path: '/system/menu',
        icon: 'ep:menu'
      },
      {
        title: '部门管理',
        description: '当前仅保留一个顶级部门，统一关联使用。',
        path: '/system/dept',
        icon: 'ep:office-building'
      },
      {
        title: '岗位管理',
        description: '保留初始化管理员岗位，满足账号关联。',
        path: '/system/post',
        icon: 'ep:postcard'
      }
    ]
  },
  {
    title: '基础配置',
    description: '保留当前系统运行所需的基础字典与配置能力。',
    items: [
      {
        title: '字典管理',
        description: '维护邻里互助与系统基础状态字典。',
        path: '/system/dict',
        icon: 'ep:collection'
      },
      {
        title: '配置管理',
        description: '维护初始化密码等平台基础配置项。',
        path: '/infra/config',
        icon: 'ep:setting'
      }
    ]
  },
  {
    title: '审计留痕',
    description: '保留基础日志能力，方便初始化后的操作核查。',
    items: [
      {
        title: '操作日志',
        description: '查看后台操作记录与行为留痕。',
        path: '/system/operatelog',
        icon: 'ep:document-copy'
      },
      {
        title: '登录日志',
        description: '查看管理员登录记录与来源信息。',
        path: '/system/loginlog',
        icon: 'ep:promotion'
      }
    ]
  }
]

const go = (path: string) => {
  router.push(path)
}
</script>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.hero {
  align-items: end;
  background:
    linear-gradient(135deg, rgb(33 53 85 / 97%), rgb(49 87 44 / 92%)),
    linear-gradient(180deg, #f6f4ed, #eef3f0);
  border-radius: 8px;
  color: #fff;
  display: grid;
  gap: 20px;
  grid-template-columns: minmax(0, 1.8fr) minmax(280px, 1fr);
  padding: 28px;
}

.hero-copy {
  min-width: 0;
}

.eyebrow {
  font-size: 13px;
  font-weight: 600;
  margin: 0 0 10px;
  opacity: 0.9;
}

.hero h1 {
  font-size: 30px;
  line-height: 1.2;
  margin: 0;
}

.summary {
  font-size: 14px;
  line-height: 1.8;
  margin: 14px 0 0;
  max-width: 760px;
  opacity: 0.94;
}

.hero-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 18px;
}

.hero-meta span {
  background: rgb(255 255 255 / 12%);
  border: 1px solid rgb(255 255 255 / 16%);
  border-radius: 999px;
  font-size: 12px;
  padding: 6px 12px;
}

.hero-actions {
  display: grid;
  gap: 12px;
  width: 100%;
}

.primary-action,
.secondary-action {
  align-items: center;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  font-size: 15px;
  font-weight: 600;
  gap: 10px;
  justify-content: center;
  min-height: 52px;
  padding: 0 18px;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease,
    border-color 0.2s ease;
}

.primary-action {
  background: #fff;
  border: 1px solid #fff;
  color: #213555;
}

.secondary-action {
  background: transparent;
  border: 1px solid rgb(255 255 255 / 30%);
  color: #fff;
}

.primary-action:hover,
.secondary-action:hover,
.entry-card:hover {
  box-shadow: 0 10px 24px rgb(15 23 42 / 10%);
  transform: translateY(-1px);
}

.module-section {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.section-head h2 {
  color: var(--el-text-color-primary);
  font-size: 20px;
  margin: 0;
}

.section-head p {
  color: var(--el-text-color-secondary);
  font-size: 13px;
  line-height: 1.7;
  margin: 6px 0 0;
}

.entry-grid {
  display: grid;
  gap: 14px;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
}

.entry-card {
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color-light);
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  gap: 14px;
  min-height: 132px;
  padding: 18px;
  text-align: left;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease,
    border-color 0.2s ease;
}

.entry-card:hover {
  border-color: var(--el-color-primary-light-5);
}

.entry-main {
  align-items: center;
  display: flex;
  gap: 12px;
  justify-content: space-between;
}

.entry-title {
  color: var(--el-text-color-primary);
  font-size: 17px;
  font-weight: 600;
  line-height: 1.4;
}

.entry-icon,
.action-icon {
  color: var(--el-color-primary);
  flex: 0 0 auto;
  font-size: 22px;
}

.entry-desc {
  color: var(--el-text-color-secondary);
  font-size: 13px;
  line-height: 1.7;
  margin: 0;
}

@media (max-width: 960px) {
  .hero {
    grid-template-columns: 1fr;
  }

  .hero h1 {
    font-size: 24px;
  }
}
</style>
