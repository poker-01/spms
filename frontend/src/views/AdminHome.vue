<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getDashboardStats, type DashboardStats } from '@/api/dashboard'

defineOptions({
  name: 'AdminHome',
})

const router = useRouter()
const userStore = useUserStore()
const stats = ref<DashboardStats | null>(null)
const loading = ref(true)

const navigate = (path: string) => router.push(path)

onMounted(async () => {
  try {
    if (!userStore.userInfo) {
      await userStore.initAuth()
    }
    const res = await getDashboardStats()
    stats.value = res.data
  } catch {
    // 静默处理，看板数据加载失败不阻断页面
  } finally {
    loading.value = false
  }
})

// 格式化金额
const fmtMoney = (v?: number) => {
  if (v == null) return '¥0.00'
  return '¥' + Number(v).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

// 统计卡片配置
interface StatCard {
  label: string
  key: keyof DashboardStats
  color: string
  icon: string
}

const communityCards: StatCard[] = [
  { label: '小区总数', key: 'totalCommunities', color: '#3b82f6', icon: '🏘️' },
  { label: '楼栋总数', key: 'totalBuildings', color: '#6366f1', icon: '🏢' },
  { label: '房屋总数', key: 'totalHouses', color: '#8b5cf6', icon: '🏠' },
  { label: '已入住', key: 'occupiedHouses', color: '#10b981', icon: '🔑' },
]

const ownerCards: StatCard[] = [
  { label: '业主总数', key: 'totalOwners', color: '#0ea5e9', icon: '👤' },
]

const repairCards: StatCard[] = [
  { label: '报修总数', key: 'totalRepairs', color: '#f59e0b', icon: '🔧' },
  { label: '待派单', key: 'pendingRepairs', color: '#ef4444', icon: '⏳' },
  { label: '处理中', key: 'processingRepairs', color: '#f97316', icon: '⚙️' },
  { label: '已完成', key: 'completedRepairs', color: '#22c55e', icon: '✅' },
]

const complaintCards: StatCard[] = [
  { label: '投诉总数', key: 'totalComplaints', color: '#ec4899', icon: '💬' },
  { label: '待处理', key: 'pendingComplaints', color: '#ef4444', icon: '📋' },
]

const financeCards: StatCard[] = [
  { label: '账单总数', key: 'totalBills', color: '#14b8a6', icon: '📄' },
  { label: '待缴费', key: 'unpaidBills', color: '#f59e0b', icon: '⏰' },
  { label: '已缴费', key: 'paidBills', color: '#22c55e', icon: '💰' },
  { label: '已逾期', key: 'overdueBills', color: '#ef4444', icon: '⚠️' },
]
</script>

<template>
  <div class="dashboard">
    <!-- 欢迎横幅 -->
    <section class="dashboard__hero">
      <div class="dashboard__hero-text">
        <p class="dashboard__badge">系统总览</p>
        <h1 class="dashboard__title">欢迎回来，{{ userStore.userInfo?.fullName || '管理员' }}</h1>
        <p class="dashboard__desc">智慧物业管理系统数据看板，实时查看各模块运营数据</p>
      </div>
      <div class="dashboard__hero-stats">
        <div class="dashboard__hero-stat">
          <span class="dashboard__hero-stat-value">{{ stats?.totalUsers ?? '-' }}</span>
          <span class="dashboard__hero-stat-label">系统用户</span>
        </div>
        <div class="dashboard__hero-stat">
          <span class="dashboard__hero-stat-value">{{ stats?.totalRoles ?? '-' }}</span>
          <span class="dashboard__hero-stat-label">系统角色</span>
        </div>
      </div>
    </section>

    <!-- 加载状态 -->
    <div v-if="loading" class="dashboard__loading">
      <div class="dashboard__loading-spinner"></div>
      <span>正在加载统计数据...</span>
    </div>

    <template v-else>
      <!-- 小区管理 -->
      <section class="dashboard__section">
        <div class="dashboard__section-header" @click="navigate('/admin/community')">
          <span class="dashboard__section-icon">🏘️</span>
          <h2 class="dashboard__section-title">小区管理</h2>
          <span class="dashboard__section-link">进入管理 →</span>
        </div>
        <div class="dashboard__cards">
          <div
            v-for="card in communityCards"
            :key="card.key"
            class="dashboard__stat-card"
            :style="{ '--accent': card.color }"
          >
            <div class="dashboard__stat-icon">{{ card.icon }}</div>
            <div class="dashboard__stat-info">
              <span class="dashboard__stat-value">{{ stats?.[card.key] ?? 0 }}</span>
              <span class="dashboard__stat-label">{{ card.label }}</span>
            </div>
          </div>
        </div>
      </section>

      <!-- 业主管理 -->
      <section class="dashboard__section">
        <div class="dashboard__section-header" @click="navigate('/admin/owner')">
          <span class="dashboard__section-icon">👤</span>
          <h2 class="dashboard__section-title">业主管理</h2>
          <span class="dashboard__section-link">进入管理 →</span>
        </div>
        <div class="dashboard__cards">
          <div
            v-for="card in ownerCards"
            :key="card.key"
            class="dashboard__stat-card"
            :style="{ '--accent': card.color }"
          >
            <div class="dashboard__stat-icon">{{ card.icon }}</div>
            <div class="dashboard__stat-info">
              <span class="dashboard__stat-value">{{ stats?.[card.key] ?? 0 }}</span>
              <span class="dashboard__stat-label">{{ card.label }}</span>
            </div>
          </div>
        </div>
      </section>

      <!-- 物业服务 -->
      <section class="dashboard__section">
        <div class="dashboard__section-header">
          <span class="dashboard__section-icon">🔧</span>
          <h2 class="dashboard__section-title">物业服务</h2>
        </div>

        <h3 class="dashboard__sub-title">报修工单</h3>
        <div class="dashboard__cards">
          <div
            v-for="card in repairCards"
            :key="card.key"
            class="dashboard__stat-card"
            :style="{ '--accent': card.color }"
            @click="navigate('/admin/community')"
          >
            <div class="dashboard__stat-icon">{{ card.icon }}</div>
            <div class="dashboard__stat-info">
              <span class="dashboard__stat-value">{{ stats?.[card.key] ?? 0 }}</span>
              <span class="dashboard__stat-label">{{ card.label }}</span>
            </div>
          </div>
        </div>

        <h3 class="dashboard__sub-title">投诉建议</h3>
        <div class="dashboard__cards">
          <div
            v-for="card in complaintCards"
            :key="card.key"
            class="dashboard__stat-card"
            :style="{ '--accent': card.color }"
          >
            <div class="dashboard__stat-icon">{{ card.icon }}</div>
            <div class="dashboard__stat-info">
              <span class="dashboard__stat-value">{{ stats?.[card.key] ?? 0 }}</span>
              <span class="dashboard__stat-label">{{ card.label }}</span>
            </div>
          </div>
        </div>

        <!-- 投诉类型分布 -->
        <div
          v-if="stats?.complaintTypeDistribution && Object.keys(stats.complaintTypeDistribution).length"
          class="dashboard__distribution"
        >
          <h4 class="dashboard__dist-title">投诉类型分布</h4>
          <div class="dashboard__dist-bars">
            <div
              v-for="(count, type) in stats.complaintTypeDistribution"
              :key="type"
              class="dashboard__dist-row"
            >
              <span class="dashboard__dist-label">{{ type }}</span>
              <div class="dashboard__dist-bar-bg">
                <div
                  class="dashboard__dist-bar"
                  :style="{
                    width: Math.max(8, (Number(count) / Math.max(...Object.values(stats!.complaintTypeDistribution), 1)) * 100) + '%',
                    background: '#ec4899',
                  }"
                ></div>
              </div>
              <span class="dashboard__dist-count">{{ count }}</span>
            </div>
          </div>
        </div>
      </section>

      <!-- 财务管理 -->
      <section class="dashboard__section">
        <div class="dashboard__section-header">
          <span class="dashboard__section-icon">💰</span>
          <h2 class="dashboard__section-title">财务管理</h2>
        </div>

        <div class="dashboard__cards">
          <div
            v-for="card in financeCards"
            :key="card.key"
            class="dashboard__stat-card"
            :style="{ '--accent': card.color }"
          >
            <div class="dashboard__stat-icon">{{ card.icon }}</div>
            <div class="dashboard__stat-info">
              <span class="dashboard__stat-value">{{ stats?.[card.key] ?? 0 }}</span>
              <span class="dashboard__stat-label">{{ card.label }}</span>
            </div>
          </div>
        </div>

        <!-- 财务金额汇总 -->
        <div class="dashboard__finance-summary">
          <div class="dashboard__finance-item">
            <span class="dashboard__finance-label">累计缴费金额</span>
            <span class="dashboard__finance-value" style="color: #22c55e">
              {{ fmtMoney(stats?.totalPaymentAmount) }}
            </span>
          </div>
          <div class="dashboard__finance-item">
            <span class="dashboard__finance-label">累计欠费金额</span>
            <span class="dashboard__finance-value" style="color: #ef4444">
              {{ fmtMoney(stats?.totalOverdueAmount) }}
            </span>
          </div>
        </div>

        <!-- 账单状态分布 -->
        <div
          v-if="stats?.billStatusDistribution && Object.keys(stats.billStatusDistribution).length"
          class="dashboard__distribution"
        >
          <h4 class="dashboard__dist-title">账单状态分布</h4>
          <div class="dashboard__dist-bars">
            <div
              v-for="(count, status) in stats.billStatusDistribution"
              :key="status"
              class="dashboard__dist-row"
            >
              <span class="dashboard__dist-label">{{ status }}</span>
              <div class="dashboard__dist-bar-bg">
                <div
                  class="dashboard__dist-bar"
                  :style="{
                    width: Math.max(8, (Number(count) / Math.max(...Object.values(stats!.billStatusDistribution), 1)) * 100) + '%',
                    background: status === '已缴费' ? '#22c55e' : status === '已逾期' ? '#ef4444' : '#f59e0b',
                  }"
                ></div>
              </div>
              <span class="dashboard__dist-count">{{ count }}</span>
            </div>
          </div>
        </div>
      </section>
    </template>
  </div>
</template>

<style scoped>
.dashboard {
  max-width: 1200px;
  margin: 0 auto;
}

/* ===== 欢迎横幅 ===== */
.dashboard__hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
  margin-bottom: 28px;
  padding: 28px 32px;
  border-radius: 16px;
  background: linear-gradient(135deg, #1e40af 0%, #3b82f6 100%);
  color: #fff;
  box-shadow: 0 8px 32px rgba(30, 64, 175, 0.25);
}

.dashboard__badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.2);
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.05em;
  margin-bottom: 8px;
}

.dashboard__title {
  margin: 0 0 6px;
  font-size: 26px;
  font-weight: 700;
}

.dashboard__desc {
  margin: 0;
  opacity: 0.8;
  font-size: 14px;
}

.dashboard__hero-stats {
  display: flex;
  gap: 32px;
  flex-shrink: 0;
}

.dashboard__hero-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.dashboard__hero-stat-value {
  font-size: 32px;
  font-weight: 800;
}

.dashboard__hero-stat-label {
  font-size: 13px;
  opacity: 0.75;
}

/* ===== 加载状态 ===== */
.dashboard__loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 64px 0;
  color: #64748b;
  font-size: 15px;
}

.dashboard__loading-spinner {
  width: 24px;
  height: 24px;
  border: 3px solid #e2e8f0;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ===== 分区 ===== */
.dashboard__section {
  margin-bottom: 32px;
}

.dashboard__section-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.dashboard__section-header:hover {
  opacity: 0.8;
}

.dashboard__section-icon {
  font-size: 22px;
}

.dashboard__section-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  flex: 1;
}

.dashboard__section-link {
  font-size: 13px;
  color: #3b82f6;
  font-weight: 500;
  opacity: 0;
  transition: opacity 0.2s;
}

.dashboard__section-header:hover .dashboard__section-link {
  opacity: 1;
}

.dashboard__sub-title {
  margin: 20px 0 12px;
  font-size: 14px;
  font-weight: 600;
  color: #475569;
  padding-left: 4px;
}

/* ===== 统计卡片 ===== */
.dashboard__cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.dashboard__stat-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 20px;
  border-radius: 12px;
  background: #fff;
  border: 1px solid #e8edf4;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  transition: all 0.2s ease;
  cursor: default;
}

.dashboard__stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  border-color: var(--accent, #3b82f6);
}

.dashboard__stat-icon {
  font-size: 28px;
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  background: color-mix(in srgb, var(--accent, #3b82f6) 12%, transparent);
}

.dashboard__stat-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.dashboard__stat-value {
  font-size: 28px;
  font-weight: 800;
  color: #1e293b;
  line-height: 1;
}

.dashboard__stat-label {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}

/* ===== 财务金额汇总 ===== */
.dashboard__finance-summary {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-top: 16px;
}

.dashboard__finance-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 22px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #e8edf4;
}

.dashboard__finance-label {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.dashboard__finance-value {
  font-size: 22px;
  font-weight: 800;
}

/* ===== 分布条形图 ===== */
.dashboard__distribution {
  margin-top: 20px;
  padding: 20px 22px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #e8edf4;
}

.dashboard__dist-title {
  margin: 0 0 14px;
  font-size: 14px;
  font-weight: 600;
  color: #334155;
}

.dashboard__dist-bars {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.dashboard__dist-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.dashboard__dist-label {
  width: 72px;
  flex-shrink: 0;
  font-size: 13px;
  color: #475569;
  text-align: right;
  font-weight: 500;
}

.dashboard__dist-bar-bg {
  flex: 1;
  height: 22px;
  border-radius: 6px;
  background: #e8edf4;
  overflow: hidden;
}

.dashboard__dist-bar {
  height: 100%;
  border-radius: 6px;
  transition: width 0.6s ease;
  min-width: 4px;
}

.dashboard__dist-count {
  width: 40px;
  flex-shrink: 0;
  font-size: 14px;
  font-weight: 700;
  color: #1e293b;
  text-align: right;
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .dashboard__hero {
    flex-direction: column;
    align-items: flex-start;
    padding: 20px;
  }

  .dashboard__hero-stats {
    gap: 20px;
  }

  .dashboard__hero-stat-value {
    font-size: 24px;
  }

  .dashboard__cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .dashboard__finance-summary {
    grid-template-columns: 1fr;
  }

  .dashboard__section-link {
    opacity: 1;
  }
}

@media (max-width: 480px) {
  .dashboard__cards {
    grid-template-columns: 1fr;
  }
}
</style>
