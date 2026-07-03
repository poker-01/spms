<script setup lang="ts">
import { computed, onMounted } from 'vue'
import AppHeader from '@/components/AppHeader.vue'
import { useUserStore } from '@/stores/user'
import { formatRoleLabels } from '@/utils/role'

defineOptions({
  name: 'Home',
})

const userStore = useUserStore()

const roleText = computed(() => formatRoleLabels(userStore.userInfo?.roles))

onMounted(async () => {
  if (!userStore.userInfo) {
    await userStore.fetchUserInfo()
  }
})
</script>

<template>
  <div class="home">
    <app-header portal-label="业主端" />

    <main class="page-container home__main">
      <section class="home__hero">
        <div>
          <p class="home__badge">登录成功</p>
          <h1 class="home__title">欢迎回来，{{ userStore.userInfo?.fullName || '用户' }}</h1>
          <p class="home__desc">您已进入业主端，可在此查询账单、提交报修和投诉建议。</p>
        </div>
      </section>

      <section class="home__cards">
        <article class="home-card">
          <h3 class="home-card__title">账号信息</h3>
          <dl class="home-card__list">
            <div class="home-card__item">
              <dt>用户名</dt>
              <dd>{{ userStore.userInfo?.userName || '-' }}</dd>
            </div>
            <div class="home-card__item">
              <dt>真实姓名</dt>
              <dd>{{ userStore.userInfo?.fullName || '-' }}</dd>
            </div>
            <div class="home-card__item">
              <dt>手机号</dt>
              <dd>{{ userStore.userInfo?.phoneNumber || '未填写' }}</dd>
            </div>
            <div class="home-card__item">
              <dt>邮箱</dt>
              <dd>{{ userStore.userInfo?.email || '未填写' }}</dd>
            </div>
          </dl>
        </article>

        <article class="home-card">
          <h3 class="home-card__title">角色权限</h3>
          <dl class="home-card__list">
            <div class="home-card__item">
              <dt>当前角色</dt>
              <dd>{{ roleText }}</dd>
            </div>
            <div class="home-card__item">
              <dt>权限数量</dt>
              <dd>{{ userStore.userInfo?.permissions?.length ?? 0 }} 项</dd>
            </div>
          </dl>
          <p class="home-card__hint">后续可在此扩展账单查询、报修申请和投诉提交入口。</p>
        </article>
      </section>
    </main>
  </div>
</template>

<style scoped>
.home {
  min-height: 100vh;
  background:
    radial-gradient(circle at top right, rgba(37, 99, 235, 0.08), transparent 28%),
    var(--color-bg);
}

.home__main {
  padding: 32px 0 48px;
}

.home__hero {
  margin-bottom: 24px;
  padding: 32px;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, #eff6ff 0%, #ffffff 100%);
  border: 1px solid #dbeafe;
}

.home__badge {
  display: inline-flex;
  margin: 0 0 12px;
  padding: 6px 10px;
  border-radius: 999px;
  background: var(--color-primary-light);
  color: var(--color-primary);
  font-size: 13px;
  font-weight: 600;
}

.home__title {
  margin: 0 0 8px;
  font-size: 32px;
}

.home__desc {
  margin: 0;
  color: var(--color-text-secondary);
}

.home__cards {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
}

.home-card {
  padding: 24px;
  border-radius: var(--radius-lg);
  background: var(--color-card);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
}

.home-card__title {
  margin: 0 0 16px;
  font-size: 18px;
}

.home-card__list {
  margin: 0;
}

.home-card__item {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 12px 0;
  border-bottom: 1px solid var(--color-border);
}

.home-card__item:last-child {
  border-bottom: none;
}

.home-card__item dt {
  color: var(--color-text-secondary);
  font-size: 14px;
}

.home-card__item dd {
  margin: 0;
  font-weight: 600;
  text-align: right;
}

.home-card__hint {
  margin: 16px 0 0;
  color: var(--color-text-secondary);
  font-size: 14px;
}

@media (max-width: 768px) {
  .home__cards {
    grid-template-columns: 1fr;
  }

  .home__hero {
    padding: 24px;
  }

  .home__title {
    font-size: 26px;
  }
}
</style>
