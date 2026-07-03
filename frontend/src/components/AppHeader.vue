<script setup lang="ts">
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { useAuth } from '@/utils/useAuth'

defineOptions({
  name: 'AppHeader',
})

const props = defineProps<{
  portalLabel?: string
}>()

const userStore = useUserStore()
const { logout, loading } = useAuth()

const displayName = computed(() => {
  return userStore.userInfo?.fullName || userStore.userInfo?.userName || '用户'
})

const handleLogout = async () => {
  await logout()
}
</script>

<template>
  <header class="app-header">
    <div class="page-container app-header__inner">
      <div class="app-header__brand">
        <span class="app-header__logo">SPMS</span>
        <div class="app-header__titles">
          <span class="app-header__title">智慧物业管理系统</span>
          <span v-if="props.portalLabel" class="app-header__portal">{{ props.portalLabel }}</span>
        </div>
      </div>

      <div class="app-header__actions">
        <span class="app-header__user">欢迎，{{ displayName }}</span>
        <button class="btn btn-ghost" type="button" :disabled="loading" @click="handleLogout">
          退出登录
        </button>
      </div>
    </div>
  </header>
</template>

<style scoped>
.app-header {
  position: sticky;
  top: 0;
  z-index: 10;
  border-bottom: 1px solid var(--color-border);
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(8px);
}

.app-header__inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 64px;
  gap: 16px;
}

.app-header__brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.app-header__titles {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.app-header__portal {
  color: var(--color-text-secondary);
  font-size: 12px;
  font-weight: 500;
}

.app-header__logo {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: var(--color-primary);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
}

.app-header__title {
  font-size: 16px;
  font-weight: 700;
}

.app-header__actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.app-header__user {
  color: var(--color-text-secondary);
  font-size: 14px;
}
</style>
