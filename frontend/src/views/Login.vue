<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import AuthLayout from '@/components/AuthLayout.vue'
import { useAuth } from '@/utils/useAuth'
import type { LoginType } from '@/utils/role'

defineOptions({
  name: 'Login',
})

const route = useRoute()
const { login, loading, errorMessage } = useAuth()

const loginType = ref<LoginType>(
  route.query.type === 'property' ? 'property' : 'owner',
)

const form = reactive({
  userName: '',
  password: '',
})

const loginTabs: Array<{ value: LoginType; label: string; hint: string }> = [
  { value: 'owner', label: '业主登录', hint: '业主查询账单、提交报修和投诉' },
  { value: 'property', label: '物业登录', hint: '物业人员管理小区业务和工单' },
]

const currentHint = computed(() => {
  return loginTabs.find((tab) => tab.value === loginType.value)?.hint ?? ''
})

const handleSubmit = async () => {
  const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : undefined
  await login(
    {
      userName: form.userName,
      password: form.password,
      loginType: loginType.value,
    },
    redirect,
  )
}
</script>

<template>
  <auth-layout>
    <div class="login">
      <div class="login__header">
        <h2 class="login__title">欢迎登录</h2>
        <p class="login__subtitle">{{ currentHint }}</p>
      </div>

      <div class="login__tabs" role="tablist" aria-label="登录入口">
        <button
          v-for="tab in loginTabs"
          :key="tab.value"
          class="login__tab"
          :class="{ 'login__tab--active': loginType === tab.value }"
          type="button"
          role="tab"
          :aria-selected="loginType === tab.value"
          @click="loginType = tab.value"
        >
          {{ tab.label }}
        </button>
      </div>

      <form class="login__form" @submit.prevent="handleSubmit">
        <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>

        <div class="form-field">
          <label class="form-label" for="userName">用户名</label>
          <input
            id="userName"
            v-model.trim="form.userName"
            class="form-input"
            type="text"
            placeholder="请输入用户名"
            autocomplete="username"
            required
          />
        </div>

        <div class="form-field">
          <label class="form-label" for="password">密码</label>
          <input
            id="password"
            v-model="form.password"
            class="form-input"
            type="password"
            placeholder="请输入密码"
            autocomplete="current-password"
            required
          />
        </div>

        <button class="btn btn-primary login__submit" type="submit" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>

      <p v-if="loginType === 'property'" class="login__demo">
        测试账号：admin / property，密码：123456
      </p>
    </div>
  </auth-layout>
</template>

<style scoped>
.login__header {
  margin-bottom: 24px;
}

.login__title {
  margin: 0 0 8px;
  font-size: 28px;
}

.login__subtitle {
  margin: 0;
  color: var(--color-text-secondary);
}

.login__tabs {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px;
  margin-bottom: 24px;
  padding: 4px;
  border-radius: 12px;
  background: #f1f5f9;
}

.login__tab {
  border: none;
  border-radius: 10px;
  padding: 10px 12px;
  background: transparent;
  color: var(--color-text-secondary);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.login__tab--active {
  background: #fff;
  color: var(--color-primary);
  box-shadow: var(--shadow-sm);
}

.login__form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.login__submit {
  width: 100%;
  margin-top: 8px;
}

.login__demo {
  margin: 16px 0 0;
  text-align: center;
  color: var(--color-text-secondary);
  font-size: 13px;
}
</style>
