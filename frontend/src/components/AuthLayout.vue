<script setup lang="ts">
defineOptions({
  name: 'AuthLayout',
})
</script>

<template>
  <div class="auth-layout">
    <div class="auth-layout__panel auth-layout__panel--brand">
      <div class="auth-layout__brand">
        <div class="auth-layout__logo">SPMS</div>
        <h1 class="auth-layout__title">智慧物业管理系统</h1>
        <p class="auth-layout__subtitle">高效、便捷、安全的物业服务平台</p>
      </div>

    </div>

    <div class="auth-layout__panel auth-layout__panel--form">
      <div class="auth-layout__card">
        <slot />
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-layout {
  display: grid;
  min-height: 100vh;
  grid-template-columns: 1fr 1fr;
}

.auth-layout__panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 32px;
}

.auth-layout__panel--brand {
  color: #fff;
  background: linear-gradient(135deg, #1d4ed8 0%, #2563eb 45%, #3b82f6 100%);
}

.auth-layout__brand {
  max-width: 420px;
}

.auth-layout__logo {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  margin-bottom: 24px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.16);
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 1px;
}

.auth-layout__title {
  margin: 0 0 12px;
  font-size: 32px;
  line-height: 1.2;
}

.auth-layout__subtitle {
  margin: 0;
  color: rgba(255, 255, 255, 0.86);
  font-size: 16px;
}



.auth-layout__panel--form {
  background: var(--color-bg);
}

.auth-layout__card {
  width: min(420px, 100%);
  padding: 40px;
  border-radius: var(--radius-lg);
  background: var(--color-card);
  box-shadow: var(--shadow-md);
}

@media (max-width: 900px) {
  .auth-layout {
    grid-template-columns: 1fr;
  }

  .auth-layout__panel--brand {
    min-height: 280px;
    padding: 32px 24px;
  }

  .auth-layout__card {
    padding: 28px 24px;
  }
}
</style>
