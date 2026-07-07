<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getOwnerHome } from '@/api/owner'
import type { OwnerHomeData } from '@/utils/api-types'

defineOptions({ name: 'OwnerHome' })

const userStore = useUserStore()
const router = useRouter()
const homeData = ref<OwnerHomeData | null>(null)
const loading = ref(false)

const statCards = [
  { key: 'pendingBillCount', label: '待缴账单', icon: '📄', color: '#f59e0b', path: '/owner/bills' },
  { key: 'repairCount', label: '报修工单', icon: '🔧', color: '#3b82f6', path: '/owner/repairs' },
  { key: 'complaintCount', label: '投诉建议', icon: '💬', color: '#8b5cf6', path: '/owner/complaints' },
]

const fetchData = async () => {
  loading.value = true
  try {
    const { data } = await getOwnerHome()
    homeData.value = data
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  if (!userStore.userInfo) {
    await userStore.fetchUserInfo()
  }
  await fetchData()
})
</script>

<template>
  <div class="owner-home">
    <section class="owner-home__hero">
      <div>
        <p class="owner-home__badge">业主端</p>
        <h1 class="owner-home__title">
          欢迎回来，{{ userStore.userInfo?.fullName || homeData?.fullName || '业主' }}
        </h1>
        <p class="owner-home__desc">在此查看账单、提交报修和投诉建议。</p>
      </div>
    </section>

    <section class="owner-home__stats">
      <article
        v-for="card in statCards"
        :key="card.key"
        class="owner-home__stat-card"
        @click="router.push(card.path)"
      >
        <span class="owner-home__stat-icon">{{ card.icon }}</span>
        <div>
          <p class="owner-home__stat-value">
            {{ homeData ? (homeData as any)[card.key] ?? 0 : '-' }}
          </p>
          <p class="owner-home__stat-label">{{ card.label }}</p>
        </div>
      </article>
    </section>

    <section class="owner-home__info">
      <article class="owner-home-card">
        <h3 class="owner-home-card__title">房屋信息</h3>
        <dl class="owner-home-card__list">
          <template v-if="homeData?.houseAddresses?.length">
            <div
              v-for="(addr, idx) in homeData.houseAddresses"
              :key="idx"
              class="owner-home-card__item"
            >
              <dt>房产{{ homeData.houseAddresses.length > 1 ? idx + 1 : '' }}</dt>
              <dd>{{ addr }}</dd>
            </div>
          </template>
          <div v-else class="owner-home-card__item">
            <dt>暂无关联房产</dt>
            <dd>-</dd>
          </div>
        </dl>
      </article>

      <article class="owner-home-card">
        <h3 class="owner-home-card__title">个人信息</h3>
        <dl class="owner-home-card__list">
          <div class="owner-home-card__item">
            <dt>用户名</dt>
            <dd>{{ homeData?.userName || userStore.userInfo?.userName || '-' }}</dd>
          </div>
          <div class="owner-home-card__item">
            <dt>真实姓名</dt>
            <dd>{{ homeData?.fullName || userStore.userInfo?.fullName || '-' }}</dd>
          </div>
          <div class="owner-home-card__item">
            <dt>手机号</dt>
            <dd>{{ homeData?.phoneNumber || userStore.userInfo?.phoneNumber || '未填写' }}</dd>
          </div>
          <div class="owner-home-card__item">
            <dt>邮箱</dt>
            <dd>{{ homeData?.email || userStore.userInfo?.email || '未填写' }}</dd>
          </div>
        </dl>
      </article>
    </section>
  </div>
</template>

<style scoped>
.owner-home__hero {
  margin-bottom: 24px;
  padding: 32px;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, #ccfbf1 0%, #ffffff 100%);
  border: 1px solid #99f6e4;
}

.owner-home__badge {
  display: inline-flex;
  margin: 0 0 12px;
  padding: 6px 10px;
  border-radius: 999px;
  background: #ccfbf1;
  color: #0f766e;
  font-size: 13px;
  font-weight: 600;
}

.owner-home__title {
  margin: 0 0 8px;
  font-size: 28px;
}

.owner-home__desc {
  margin: 0;
  color: var(--color-text-secondary);
}

.owner-home__stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.owner-home__stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  border-radius: var(--radius-lg);
  background: var(--color-card);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.owner-home__stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.owner-home__stat-icon {
  font-size: 32px;
}

.owner-home__stat-value {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
}

.owner-home__stat-label {
  margin: 4px 0 0;
  color: var(--color-text-secondary);
  font-size: 14px;
}

.owner-home__info {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.owner-home-card {
  padding: 24px;
  border-radius: var(--radius-lg);
  background: var(--color-card);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
}

.owner-home-card__title {
  margin: 0 0 16px;
  font-size: 18px;
}

.owner-home-card__list {
  margin: 0;
}

.owner-home-card__item {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 12px 0;
  border-bottom: 1px solid var(--color-border);
}

.owner-home-card__item:last-child {
  border-bottom: none;
}

.owner-home-card__item dt {
  color: var(--color-text-secondary);
  font-size: 14px;
}

.owner-home-card__item dd {
  margin: 0;
  font-weight: 600;
  text-align: right;
}

@media (max-width: 768px) {
  .owner-home__stats {
    grid-template-columns: 1fr;
  }

  .owner-home__info {
    grid-template-columns: 1fr;
  }
}
</style>
