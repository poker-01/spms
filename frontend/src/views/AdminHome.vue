<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from '@/components/AppHeader.vue'
import { useUserStore } from '@/stores/user'
import { formatRoleLabels } from '@/utils/role'

defineOptions({
  name: 'AdminHome',
})

const router = useRouter()
const userStore = useUserStore()

const roleText = computed(() => formatRoleLabels(userStore.userInfo?.roles))

// 模块卡片配置 - 统一使用完整路径
const moduleCards = [
  {
    title: '小区管理',
    desc: '维护小区、楼栋、房屋和业主档案',
    icon: '🏘️',
    path: '/admin/community'  // 修复：添加 /admin/ 前缀
  },
  {
    title: '楼栋管理',
    desc: '管理小区楼栋信息和单元配置',
    icon: '🏢',
    path: '/admin/building'
  },
  {
    title: '房屋管理',
    desc: '管理房屋信息、户型和入住状态',
    icon: '🏠',
    path: '/admin/house'
  },
  {
    title: '业主管理',
    desc: '管理业主信息、档案和联系记录',
    icon: '👤',
    path: '/admin/owner'
  },
]

// 跳转到对应页面
const goToPage = (path: string) => {
  console.log('跳转到:', path)  // 添加调试日志
  router.push(path)
}

onMounted(async () => {
  if (!userStore.userInfo) {
    await userStore.fetchUserInfo()
  }
})
</script>

<template>
  <div class="admin-home">
    <app-header portal-label="物业管理端" />

    <main class="page-container admin-home__main">
      <section class="admin-home__hero">
        <div>
          <p class="admin-home__badge">物业工作台</p>
          <h1 class="admin-home__title">欢迎回来，{{ userStore.userInfo?.fullName || '管理员' }}</h1>
          <p class="admin-home__desc">您已进入物业管理端，可在此处理小区日常运营业务。</p>
        </div>
      </section>

      <section class="admin-home__cards">
        <article class="admin-home-card">
          <h3 class="admin-home-card__title">账号信息</h3>
          <dl class="admin-home-card__list">
            <div class="admin-home-card__item">
              <dt>用户名</dt>
              <dd>{{ userStore.userInfo?.userName || '-' }}</dd>
            </div>
            <div class="admin-home-card__item">
              <dt>真实姓名</dt>
              <dd>{{ userStore.userInfo?.fullName || '-' }}</dd>
            </div>
            <div class="admin-home-card__item">
              <dt>手机号</dt>
              <dd>{{ userStore.userInfo?.phoneNumber || '未填写' }}</dd>
            </div>
            <div class="admin-home-card__item">
              <dt>邮箱</dt>
              <dd>{{ userStore.userInfo?.email || '未填写' }}</dd>
            </div>
          </dl>
        </article>

        <article class="admin-home-card">
          <h3 class="admin-home-card__title">角色权限</h3>
          <dl class="admin-home-card__list">
            <div class="admin-home-card__item">
              <dt>当前角色</dt>
              <dd>{{ roleText }}</dd>
            </div>
            <div class="admin-home-card__item">
              <dt>权限数量</dt>
              <dd>{{ userStore.userInfo?.permissions?.length ?? 0 }} 项</dd>
            </div>
          </dl>
          <p class="admin-home-card__hint">后续可在此接入侧边栏菜单与业务管理页面。</p>
        </article>
      </section>

      <!-- 功能模块卡片 - 可点击跳转 -->
      <section class="admin-home__modules">
        <article
          v-for="item in moduleCards"
          :key="item.title"
          class="admin-home-module"
          @click="goToPage(item.path)"
        >
          <div class="admin-home-module__icon">{{ item.icon }}</div>
          <h3 class="admin-home-module__title">{{ item.title }}</h3>
          <p class="admin-home-module__desc">{{ item.desc }}</p>
          <div class="admin-home-module__arrow">
            <span>进入管理 →</span>
          </div>
        </article>
      </section>
    </main>
  </div>
</template>

<style scoped>
.admin-home {
  min-height: 100vh;
  background:
    radial-gradient(circle at top right, rgba(29, 78, 216, 0.1), transparent 28%),
    var(--color-bg);
}

.admin-home__main {
  padding: 32px 0 48px;
}

.admin-home__hero {
  margin-bottom: 24px;
  padding: 32px;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, #dbeafe 0%, #ffffff 100%);
  border: 1px solid #bfdbfe;
}

.admin-home__badge {
  display: inline-flex;
  margin: 0 0 12px;
  padding: 6px 10px;
  border-radius: 999px;
  background: #dbeafe;
  color: #1d4ed8;
  font-size: 13px;
  font-weight: 600;
}

.admin-home__title {
  margin: 0 0 8px;
  font-size: 32px;
}

.admin-home__desc {
  margin: 0;
  color: var(--color-text-secondary);
}

.admin-home__cards {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.admin-home-card {
  padding: 24px;
  border-radius: var(--radius-lg);
  background: var(--color-card);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
}

.admin-home-card__title {
  margin: 0 0 16px;
  font-size: 18px;
}

.admin-home-card__list {
  margin: 0;
}

.admin-home-card__item {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 12px 0;
  border-bottom: 1px solid var(--color-border);
}

.admin-home-card__item:last-child {
  border-bottom: none;
}

.admin-home-card__item dt {
  color: var(--color-text-secondary);
  font-size: 14px;
}

.admin-home-card__item dd {
  margin: 0;
  font-weight: 600;
  text-align: right;
}

.admin-home-card__hint {
  margin: 16px 0 0;
  color: var(--color-text-secondary);
  font-size: 14px;
}

/* 功能模块卡片 - 可点击样式 */
.admin-home__modules {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 20px;
}

.admin-home-module {
  padding: 24px;
  border-radius: var(--radius-lg);
  background: var(--color-card);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.admin-home-module:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.1);
  border-color: var(--color-primary);
}

.admin-home-module:active {
  transform: translateY(-2px) scale(0.98);
}

.admin-home-module__icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.admin-home-module__title {
  margin: 0 0 8px;
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.admin-home-module__desc {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 14px;
  line-height: 1.6;
}

.admin-home-module__arrow {
  margin-top: 16px;
  font-size: 13px;
  color: var(--color-primary);
  font-weight: 500;
  opacity: 0;
  transform: translateX(-8px);
  transition: all 0.3s ease;
}

.admin-home-module:hover .admin-home-module__arrow {
  opacity: 1;
  transform: translateX(0);
}

/* 响应式 */
@media (max-width: 1024px) {
  .admin-home__modules {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .admin-home__cards,
  .admin-home__modules {
    grid-template-columns: 1fr;
  }

  .admin-home__hero {
    padding: 24px;
  }

  .admin-home__title {
    font-size: 26px;
  }

  .admin-home-module {
    padding: 20px;
  }
}
</style>
