<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useAuth } from '@/utils/useAuth'
import { formatRoleLabels } from '@/utils/role'

defineOptions({
  name: 'AdminLayout',
})

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { logout } = useAuth()

const roleText = computed(() => formatRoleLabels(userStore.userInfo?.roles))

const isActive = (path?: string) => {
  if (!path) return false
  return route.path === path || route.path.startsWith(path + '/')
}

const navigate = (path?: string) => {
  if (path) {
    router.push(path)
  }
}

const handleLogout = async () => {
  await logout()
}
</script>

<template>
  <div class="admin-layout">
    <aside class="admin-layout__sidebar">
      <div class="admin-layout__brand">
        <span class="admin-layout__logo">SPMS</span>
        <span class="admin-layout__title">智慧物业</span>
      </div>

      <nav class="admin-layout__menu">
        <ul>
          <li
            v-for="menu in userStore.menus"
            :key="menu.id"
            :class="{ 'admin-layout__menu-item--active': isActive(menu.path) }"
          >
            <button class="admin-layout__menu-item" type="button" @click="navigate(menu.path)">
              <span v-if="menu.icon" class="admin-layout__menu-icon">{{ menu.icon }}</span>
              <span>{{ menu.name }}</span>
            </button>
            <ul v-if="menu.children && menu.children.length > 0" class="admin-layout__submenu">
              <li
                v-for="sub in menu.children"
                :key="sub.id"
                :class="{ 'admin-layout__submenu-item--active': isActive(sub.path) }"
              >
                <button
                  class="admin-layout__submenu-item"
                  type="button"
                  @click="navigate(sub.path)"
                >
                  <span>{{ sub.name }}</span>
                </button>
              </li>
            </ul>
          </li>
        </ul>
      </nav>
    </aside>

    <div class="admin-layout__main">
      <header class="admin-layout__header">
        <div class="admin-layout__header-info">
          <span class="admin-layout__role">{{ roleText }}</span>
          <span class="admin-layout__user">
            {{ userStore.userInfo?.fullName || userStore.userInfo?.userName || '用户' }}
          </span>
        </div>
        <button class="btn btn-ghost" type="button" @click="handleLogout">退出登录</button>
      </header>

      <main class="admin-layout__content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}

.admin-layout__sidebar {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  width: 220px;
  background: #1e293b;
  color: #fff;
  overflow-y: auto;
}

.admin-layout__brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.admin-layout__logo {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: var(--color-primary);
  font-size: 12px;
  font-weight: 700;
}

.admin-layout__title {
  font-size: 16px;
  font-weight: 700;
}

.admin-layout__menu ul,
.admin-layout__submenu {
  margin: 0;
  padding: 0;
  list-style: none;
}

.admin-layout__menu > ul {
  padding: 12px;
}

.admin-layout__menu-item,
.admin-layout__submenu-item {
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

.admin-layout__menu-item:hover,
.admin-layout__submenu-item:hover {
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
}

.admin-layout__menu-item--active > .admin-layout__menu-item,
.admin-layout__submenu-item--active > .admin-layout__submenu-item {
  background: var(--color-primary);
  color: #fff;
}

.admin-layout__menu-icon {
  font-size: 16px;
}

.admin-layout__submenu {
  padding-left: 20px;
}

.admin-layout__submenu-item {
  padding: 10px 12px;
  font-size: 13px;
}

.admin-layout__main {
  flex: 1;
  margin-left: 220px;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.admin-layout__header {
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

.admin-layout__header-info {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
}

.admin-layout__role {
  padding: 4px 10px;
  border-radius: 999px;
  background: var(--color-primary-light);
  color: var(--color-primary);
  font-size: 12px;
  font-weight: 600;
}

.admin-layout__user {
  color: var(--color-text-secondary);
}

.admin-layout__content {
  flex: 1;
  padding: 24px;
  background: var(--color-bg);
}
</style>
