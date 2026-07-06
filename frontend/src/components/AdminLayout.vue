<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useAuth } from '@/utils/useAuth'
import { formatRoleLabels } from '@/utils/role'
import type { MenuItem } from '@/utils/api-types'

defineOptions({
  name: 'AdminLayout',
})

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { logout } = useAuth()

const roleText = computed(() => formatRoleLabels(userStore.userInfo?.roles))

// ============ 菜单展开/折叠控制 ============
const expandedMenus = ref<Set<number>>(new Set())

/** 切换一级菜单展开状态 */
const toggleExpand = (menuId: number) => {
  const s = new Set(expandedMenus.value)
  if (s.has(menuId)) {
    s.delete(menuId)
  } else {
    s.add(menuId)
  }
  expandedMenus.value = s
}

/** 判断菜单是否展开 */
const isExpanded = (menuId: number) => expandedMenus.value.has(menuId)

const isActive = (path?: string) => {
  if (!path) return false
  return route.path === path || route.path.startsWith(path + '/')
}

/** 判断某个一级菜单下是否有激活的子菜单 */
const hasActiveChild = (menu: MenuItem) => {
  if (!menu.children) return false
  return menu.children.some((child) => isActive(child.path))
}

// 路由变化时，自动展开包含当前页面的父菜单
watch(
  () => route.path,
  () => {
    for (const menu of userStore.menus) {
      if (menu.children && hasActiveChild(menu)) {
        const s = new Set(expandedMenus.value)
        s.add(menu.id)
        expandedMenus.value = s
      }
    }
  },
  { immediate: true },
)

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
            :class="{
              'admin-layout__menu-item--active': !menu.children?.length && isActive(menu.path),
              'admin-layout__menu-item--expanded': isExpanded(menu.id),
              'admin-layout__menu-item--has-children': menu.children && menu.children.length > 0,
            }"
          >
            <!-- 有子菜单的一级：点击切换展开/折叠 -->
            <button
              v-if="menu.children && menu.children.length > 0"
              class="admin-layout__menu-item"
              type="button"
              @click="toggleExpand(menu.id)"
            >
              <span v-if="menu.icon" class="admin-layout__menu-icon"></span>
              <span class="admin-layout__menu-label">{{ menu.name }}</span>
              <span class="admin-layout__menu-arrow" :class="{ 'admin-layout__menu-arrow--open': isExpanded(menu.id) }">
                <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
                  <path d="M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6z"/>
                </svg>
              </span>
            </button>
            <!-- 无子菜单的一级：直接导航 -->
            <button
              v-else
              class="admin-layout__menu-item"
              type="button"
              @click="navigate(menu.path)"
            >
              <span v-if="menu.icon" class="admin-layout__menu-icon">{{ menu.icon }}</span>
              <span class="admin-layout__menu-label">{{ menu.name }}</span>
            </button>

            <!-- 二级子菜单（可折叠） -->
            <Transition name="submenu">
              <ul
                v-if="menu.children && menu.children.length > 0 && isExpanded(menu.id)"
                class="admin-layout__submenu"
              >
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
            </Transition>
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
        <div class="admin-layout__overview-bar">
          <button class="admin-layout__overview-btn" type="button" @click="navigate('/admin/home')">
            <span class="admin-layout__overview-icon">📊</span>
            <span>系统总览</span>
          </button>
        </div>
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

/* ===== 菜单箭头指示器 ===== */
.admin-layout__menu-arrow {
  margin-left: auto;
  display: inline-flex;
  align-items: center;
  transition: transform 0.25s ease;
  color: rgba(255, 255, 255, 0.4);
}

.admin-layout__menu-arrow--open {
  transform: rotate(90deg);
  color: rgba(255, 255, 255, 0.7);
}

.admin-layout__menu-label {
  flex: 1;
}

/* ===== 子菜单折叠动画 ===== */
.submenu-enter-active,
.submenu-leave-active {
  transition: all 0.25s ease;
  overflow: hidden;
}

.submenu-enter-from,
.submenu-leave-to {
  opacity: 0;
  max-height: 0;
  transform: translateY(-4px);
}

.submenu-enter-to,
.submenu-leave-from {
  opacity: 1;
  max-height: 400px;
  transform: translateY(0);
}

/* ===== 展开态一级菜单高亮 ===== */
.admin-layout__menu-item--expanded > .admin-layout__menu-item {
  background: rgba(255, 255, 255, 0.05);
  color: #fff;
}

.admin-layout__menu-item--has-children.admin-layout__menu-item--active > .admin-layout__menu-item {
  background: rgba(255, 255, 255, 0.05);
  color: #fff;
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

.admin-layout__overview-bar {
  margin-bottom: 20px;
}

.admin-layout__overview-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border: none;
  border-radius: var(--radius-sm);
  background: var(--color-primary);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.25);
}

.admin-layout__overview-btn:hover {
  background: #2563eb;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.35);
}

.admin-layout__overview-icon {
  font-size: 16px;
}
</style>
