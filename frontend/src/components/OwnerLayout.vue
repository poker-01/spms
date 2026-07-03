<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useAuth } from '@/utils/useAuth'
import { formatRoleLabels } from '@/utils/role'

defineOptions({
  name: 'OwnerLayout',
})

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { logout } = useAuth()

const roleText = computed(() => formatRoleLabels(userStore.userInfo?.roles))

const ownerMenus = [
  { label: '首页概览', icon: '🏠', path: '/owner/home' },
  { label: '我的账单', icon: '📄', path: '/owner/bills' },
  { label: '报修服务', icon: '🔧', path: '/owner/repairs' },
  { label: '投诉建议', icon: '💬', path: '/owner/complaints' },
]

const isActive = (path: string) => {
  return route.path === path || route.path.startsWith(path + '/')
}

const navigate = (path: string) => {
  router.push(path)
}

const handleLogout = async () => {
  await logout()
}
</script>

<template>
  <div class="owner-layout">
    <aside class="owner-layout__sidebar">
      <div class="owner-layout__brand">
        <span class="owner-layout__logo">SPMS</span>
        <span class="owner-layout__title">业主服务</span>
      </div>

      <nav class="owner-layout__menu">
        <ul>
          <li
            v-for="menu in ownerMenus"
            :key="menu.path"
            :class="{ 'owner-layout__menu-item--active': isActive(menu.path) }"
          >
            <button class="owner-layout__menu-item" type="button" @click="navigate(menu.path)">
              <span class="owner-layout__menu-icon">{{ menu.icon }}</span>
              <span>{{ menu.label }}</span>
            </button>
          </li>
        </ul>
      </nav>
    </aside>

    <div class="owner-layout__main">
      <header class="owner-layout__header">
        <div class="owner-layout__header-info">
          <span class="owner-layout__role">{{ roleText }}</span>
          <span class="owner-layout__user">
            {{ userStore.userInfo?.fullName || userStore.userInfo?.userName || '用户' }}
          </span>
        </div>
        <button class="btn btn-ghost" type="button" @click="handleLogout">退出登录</button>
      </header>

      <main class="owner-layout__content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<style scoped>
.owner-layout {
  display: flex;
  min-height: 100vh;
}

.owner-layout__sidebar {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  width: 220px;
  background: #0f766e;
  color: #fff;
  overflow-y: auto;
}

.owner-layout__brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.owner-layout__logo {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.2);
  font-size: 12px;
  font-weight: 700;
}

.owner-layout__title {
  font-size: 16px;
  font-weight: 700;
}

.owner-layout__menu ul {
  margin: 0;
  padding: 12px;
  list-style: none;
}

.owner-layout__menu-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  border: none;
  border-radius: var(--radius-sm);
  background: transparent;
  color: rgba(255, 255, 255, 0.82);
  font-size: 14px;
  font-weight: 500;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s;
}

.owner-layout__menu-item:hover {
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
}

.owner-layout__menu-item--active > .owner-layout__menu-item {
  background: rgba(255, 255, 255, 0.18);
  color: #fff;
  font-weight: 600;
}

.owner-layout__menu-icon {
  font-size: 16px;
}

.owner-layout__main {
  flex: 1;
  margin-left: 220px;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.owner-layout__header {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 16px;
  padding: 0 24px;
  height: 64px;
  background: var(--color-card);
  border-bottom: 1px solid var(--color-border);
}

.owner-layout__header-info {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
}

.owner-layout__role {
  padding: 4px 10px;
  border-radius: 999px;
  background: #ccfbf1;
  color: #0f766e;
  font-size: 12px;
  font-weight: 600;
}

.owner-layout__user {
  color: var(--color-text-secondary);
}

.owner-layout__content {
  flex: 1;
  padding: 24px;
  background: var(--color-bg);
}
</style>
