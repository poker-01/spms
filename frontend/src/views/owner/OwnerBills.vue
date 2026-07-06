<!-- src/views/owner/OwnerBills.vue -->
<template>
  <div class="owner-page">
    <div class="owner-page__header">
      <h2 class="owner-page__title">我的账单</h2>
      <div class="owner-page__summary">
        <span class="summary-item">
          待缴费：<strong class="text-danger">¥{{ unpaidTotal.toFixed(2) }}</strong>
        </span>
        <span class="summary-item">
          已缴费：<strong class="text-success">¥{{ paidTotal.toFixed(2) }}</strong>
        </span>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div class="stat-card" :class="{ 'stat-card--active': query.status === undefined }" @click="setStatusFilter(undefined)">
        <span class="stat-card__label">全部</span>
        <span class="stat-card__value">{{ statistics.total }}</span>
      </div>
      <div class="stat-card stat-card--danger" :class="{ 'stat-card--active': query.status === 0 }" @click="setStatusFilter(0)">
        <span class="stat-card__label">待缴费</span>
        <span class="stat-card__value">{{ statistics.unpaid }}</span>
      </div>
      <div class="stat-card stat-card--success" :class="{ 'stat-card--active': query.status === 2 }" @click="setStatusFilter(2)">
        <span class="stat-card__label">已缴费</span>
        <span class="stat-card__value">{{ statistics.paid }}</span>
      </div>
      <div class="stat-card stat-card--warning" :class="{ 'stat-card--active': query.status === 3 }" @click="setStatusFilter(3)">
        <span class="stat-card__label">已逾期</span>
        <span class="stat-card__value">{{ statistics.overdue }}</span>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="owner-page__filters">
      <div class="owner-page__filters-row">
        <input
          v-model="query.keyword"
          class="form-input"
          placeholder="搜索账单编号/项目名称"
          @keyup.enter="loadData"
        />
        <button class="btn btn-primary" type="button" @click="loadData">查询</button>
        <button class="btn btn-ghost" type="button" @click="handleReset">重置</button>
      </div>
    </div>

    <!-- 账单列表 -->
    <div v-if="loading" class="owner-page__loading">加载中...</div>
    <div v-else-if="!list.length" class="owner-page__empty">
      <p>暂无账单记录</p>
    </div>
    <div v-else class="owner-page__list">
      <article
        v-for="item in list"
        :key="item.id"
        class="bill-card"
        :class="{ 'bill-card--overdue': item.status === 3 }"
        @click="handleViewDetail(item)"
      >
        <div class="bill-card__header">
          <div class="bill-card__left">
            <span class="bill-card__no">{{ item.billNo }}</span>
            <span :class="['bill-card__status', getStatusClass(item.status)]">
              {{ getStatusName(item.status) }}
            </span>
          </div>
          <span class="bill-card__amount" :class="{ 'text-danger': item.status === 0 || item.status === 3 }">
            ¥{{ item.amount.toFixed(2) }}
          </span>
        </div>

        <div class="bill-card__body">
          <div class="bill-card__row">
            <span class="bill-card__label">费用项目</span>
            <span class="bill-card__value">{{ item.itemName }}</span>
          </div>
          <div class="bill-card__row">
            <span class="bill-card__label">缴费截止</span>
            <span class="bill-card__value" :class="{ 'text-danger': item.status === 3 }">
              {{ formatDate(item.deadline) }}
            </span>
          </div>
        </div>

        <div class="bill-card__footer" @click.stop>
          <!-- 待缴费/部分缴费 → 去缴费 -->
          <button
            v-if="item.status === 0 || item.status === 1"
            class="btn btn-primary"
            type="button"
            @click="openPayDialog(item)"
          >
            去缴费
          </button>
          <!-- 已逾期 → 去缴费（催缴） -->
          <button
            v-if="item.status === 3"
            class="btn btn-danger"
            type="button"
            @click="openPayDialog(item)"
          >
            立即缴费
          </button>
          <!-- 已缴费 → 查看详情 -->
          <button
            v-if="item.status === 2"
            class="btn btn-ghost"
            type="button"
            @click="handleViewDetail(item)"
          >
            查看详情
          </button>
        </div>
      </article>
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="owner-page__pagination">
      <button class="btn btn-sm btn-ghost" :disabled="query.pageNum <= 1" @click="changePage(query.pageNum - 1)">
        上一页
      </button>
      <span class="pagination-info">第 {{ query.pageNum }} / {{ totalPages }} 页，共 {{ total }} 条</span>
      <button class="btn btn-sm btn-ghost" :disabled="query.pageNum >= totalPages" @click="changePage(query.pageNum + 1)">
        下一页
      </button>
    </div>

    <!-- 缴费弹窗 -->
    <div v-if="payDialog.visible" class="dialog-overlay" @click.self="payDialog.visible = false">
      <div class="dialog dialog--pay">
        <h3 class="dialog__title">确认缴费</h3>
        <div class="dialog__body">
          <div class="pay-info">
            <div class="pay-info__row">
              <span class="pay-info__label">账单编号</span>
              <span class="pay-info__value">{{ payDialog.item?.billNo }}</span>
            </div>
            <div class="pay-info__row">
              <span class="pay-info__label">费用项目</span>
              <span class="pay-info__value">{{ payDialog.item?.itemName }}</span>
            </div>
            <div class="pay-info__row">
              <span class="pay-info__label">缴费金额</span>
              <span class="pay-info__value pay-info__amount">¥{{ payDialog.item?.amount.toFixed(2) }}</span>
            </div>
          </div>

          <div class="form-field">
            <label class="form-label">支付方式 <span class="form-required">*</span></label>
            <div class="pay-methods">
              <label
                v-for="method in payMethods"
                :key="method.value"
                class="pay-method"
                :class="{ 'pay-method--active': payDialog.payMethod === method.value }"
              >
                <input
                  v-model="payDialog.payMethod"
                  type="radio"
                  :value="method.value"
                  class="pay-method__input"
                />
                <span class="pay-method__icon">{{ method.icon }}</span>
                <span class="pay-method__label">{{ method.label }}</span>
              </label>
            </div>
          </div>
        </div>
        <div class="dialog__footer">
          <button class="btn btn-ghost" type="button" @click="payDialog.visible = false">取消</button>
          <button class="btn btn-primary" type="button" :disabled="payDialog.submitting" @click="handlePay">
            {{ payDialog.submitting ? '处理中...' : '确认支付' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 缴费成功弹窗 -->
    <div v-if="paySuccess.visible" class="dialog-overlay" @click.self="paySuccess.visible = false">
      <div class="dialog dialog--success">
        <div class="success-icon">✅</div>
        <h3 class="success-title">缴费成功！</h3>
        <p class="success-desc">账单 <strong>{{ paySuccess.billNo }}</strong> 已缴费成功</p>
        <div class="dialog__footer">
          <button class="btn btn-primary" type="button" @click="paySuccess.visible = false">知道了</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { formatDate } from '@/utils/format'
import { getOwnerBills, payOwnerBill } from '@/api/owner'
import type { OwnerBill } from '@/utils/api-types'

defineOptions({
  name: 'OwnerBills',
})

// ============================================================
// 状态
// ============================================================

const router = useRouter()
const loading = ref(false)
const allList = ref<OwnerBill[]>([])
const list = ref<OwnerBill[]>([])
const total = ref(0)
const totalPages = ref(1)

// 统计数据
const statistics = reactive({
  total: 0,
  unpaid: 0,
  partial: 0,
  paid: 0,
  overdue: 0,
})

// 汇总金额
const unpaidTotal = computed(() => {
  return list.value
    .filter((item) => item.status === 0 || item.status === 3)
    .reduce((sum, item) => sum + item.amount, 0)
})

const paidTotal = computed(() => {
  return list.value
    .filter((item) => item.status === 2)
    .reduce((sum, item) => sum + item.amount, 0)
})

// 查询参数
const query = reactive({
  status: undefined as number | undefined,
  keyword: '',
  pageNum: 1,
  pageSize: 10,
})

// 支付方式（与后端 PayMethod 枚举 code 对应：0-现金 1-银行转账 2-微信支付 3-支付宝）
const payMethods = [
  { value: 2, label: '微信支付', icon: '💚' },
  { value: 3, label: '支付宝', icon: '💙' },
  { value: 0, label: '现金', icon: '💰' },
  { value: 1, label: '银行转账', icon: '🏦' },
]

// 缴费弹窗
const payDialog = reactive({
  visible: false,
  item: null as OwnerBill | null,
  payMethod: 2,
  submitting: false,
})

// 缴费成功弹窗
const paySuccess = reactive({
  visible: false,
  billNo: '',
})

// ============================================================
// 方法
// ============================================================

/**
 * 获取状态名称（后端 BillStatus：0-待缴费 1-部分缴费 2-已缴费 3-已逾期）
 */
const getStatusName = (status: number): string => {
  const map: Record<number, string> = {
    0: '待缴费',
    1: '部分缴费',
    2: '已缴费',
    3: '已逾期',
  }
  return map[status] || '未知'
}

/**
 * 加载账单列表
 * 接口：GET /api/v1/owner/bills
 */
const loadData = async () => {
  loading.value = true
  try {
    const { data } = await getOwnerBills()
    allList.value = data || []

    const filtered = allList.value.filter((item) => {
      if (query.status !== undefined && item.status !== query.status) {
        return false
      }
      if (query.keyword) {
        const kw = query.keyword.toLowerCase()
        return (
          item.billNo.toLowerCase().includes(kw) ||
          item.itemName.includes(kw)
        )
      }
      return true
    })

    total.value = filtered.length
    totalPages.value = Math.ceil(total.value / query.pageSize)
    const start = (query.pageNum - 1) * query.pageSize
    list.value = filtered.slice(start, start + query.pageSize)

    updateStatistics(allList.value)
  } catch (error) {
    console.error('加载账单列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 更新统计
 */
const updateStatistics = (data: OwnerBill[]) => {
  statistics.total = data.length
  statistics.unpaid = data.filter((i) => i.status === 0).length
  statistics.partial = data.filter((i) => i.status === 1).length
  statistics.paid = data.filter((i) => i.status === 2).length
  statistics.overdue = data.filter((i) => i.status === 3).length
}

/**
 * 设置状态筛选
 */
const setStatusFilter = (status: number | undefined) => {
  query.status = status
  query.pageNum = 1
  loadData()
}

/**
 * 切换页码
 */
const changePage = (page: number) => {
  if (page < 1 || page > totalPages.value) return
  query.pageNum = page
  loadData()
}

/**
 * 重置搜索
 */
const handleReset = () => {
  query.status = undefined
  query.keyword = ''
  query.pageNum = 1
  loadData()
}

/**
 * 查看详情
 */
const handleViewDetail = (item: OwnerBill) => {
  router.push(`/owner/bills/${item.id}`)
}

/**
 * 打开缴费弹窗
 */
const openPayDialog = (item: OwnerBill) => {
  payDialog.item = item
  payDialog.payMethod = 2
  payDialog.visible = true
}

/**
 * 确认缴费
 * 接口：POST /api/v1/owner/bills/{id}/pay
 */
const handlePay = async () => {
  if (!payDialog.item) return
  if (payDialog.payMethod === undefined) {
    alert('请选择支付方式')
    return
  }

  payDialog.submitting = true
  try {
    await payOwnerBill(payDialog.item.id, payDialog.payMethod)

    // 保存账单号用于成功提示
    paySuccess.billNo = payDialog.item.billNo

    payDialog.visible = false
    paySuccess.visible = true

    // 刷新列表
    await loadData()
  } catch (error) {
    console.error('缴费失败:', error)
    alert('缴费失败，请稍后重试')
  } finally {
    payDialog.submitting = false
  }
}

/**
 * 获取状态样式
 */
const getStatusClass = (status: number): string => {
  const map: Record<number, string> = {
    0: 'status--warning',
    1: 'status--primary',
    2: 'status--success',
    3: 'status--danger',
  }
  return map[status] || ''
}

// ============================================================
// 生命周期
// ============================================================

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.owner-page__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 12px;
}

.owner-page__title {
  margin: 0;
  font-size: 22px;
}

.owner-page__summary {
  display: flex;
  gap: 24px;
  font-size: 14px;
}

.summary-item {
  color: var(--color-text-secondary);
}

.text-danger {
  color: #dc2626;
}

.text-success {
  color: #059669;
}

/* 统计卡片 */
.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.stat-card {
  padding: 14px 16px;
  border-radius: var(--radius-md);
  background: var(--color-card);
  border: 2px solid transparent;
  cursor: pointer;
  transition: all 0.2s;
  text-align: center;
}

.stat-card:hover {
  border-color: var(--color-border);
  transform: translateY(-2px);
}

.stat-card--active {
  border-color: var(--color-primary);
  background: #eff6ff;
}

.stat-card__label {
  display: block;
  font-size: 13px;
  color: var(--color-text-secondary);
}

.stat-card__value {
  display: block;
  font-size: 28px;
  font-weight: 700;
}

.stat-card--danger .stat-card__value {
  color: #dc2626;
}
.stat-card--success .stat-card__value {
  color: #059669;
}
.stat-card--warning .stat-card__value {
  color: #d97706;
}

/* 搜索栏 */
.owner-page__filters {
  margin-bottom: 20px;
}

.owner-page__filters-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.owner-page__filters-row .form-input {
  width: 200px;
}

.owner-page__loading,
.owner-page__empty {
  padding: 60px 20px;
  text-align: center;
  color: var(--color-text-secondary);
}

/* 账单列表 */
.owner-page__list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.bill-card {
  padding: 20px;
  border-radius: var(--radius-md);
  background: var(--color-card);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: box-shadow 0.2s;
}

.bill-card:hover {
  box-shadow: var(--shadow-md);
}

.bill-card--overdue {
  border-left: 4px solid #dc2626;
}

.bill-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.bill-card__left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.bill-card__no {
  font-weight: 600;
  font-size: 15px;
}

.bill-card__status {
  display: inline-flex;
  padding: 2px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.bill-card__amount {
  font-size: 22px;
  font-weight: 700;
}

.status--warning {
  background: #fef3c7;
  color: #d97706;
}
.status--primary {
  background: #dbeafe;
  color: #2563eb;
}
.status--success {
  background: #d1fae5;
  color: #059669;
}
.status--danger {
  background: #fef2f2;
  color: #dc2626;
}

.bill-card__body {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 6px 32px;
  margin-bottom: 14px;
}

.bill-card__row {
  display: flex;
  justify-content: space-between;
  padding: 4px 0;
  font-size: 14px;
}

.bill-card__label {
  color: var(--color-text-secondary);
}

.bill-card__value {
  font-weight: 500;
}

.bill-card__footer {
  display: flex;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid var(--color-border);
}

/* 分页 */
.owner-page__pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 16px;
  padding: 12px;
}

.pagination-info {
  color: var(--color-text-secondary);
  font-size: 14px;
}

/* 弹窗 */
.dialog--pay,
.dialog--success {
  width: min(480px, calc(100% - 32px));
}

/* 缴费弹窗 */
.pay-info {
  background: var(--color-bg);
  border-radius: var(--radius-sm);
  padding: 16px;
  margin-bottom: 16px;
}

.pay-info__row {
  display: flex;
  justify-content: space-between;
  padding: 6px 0;
  font-size: 14px;
}

.pay-info__row:not(:last-child) {
  border-bottom: 1px solid var(--color-border);
}

.pay-info__label {
  color: var(--color-text-secondary);
}

.pay-info__value {
  font-weight: 500;
}

.pay-info__amount {
  color: var(--color-primary);
  font-size: 20px;
  font-weight: 700;
}

/* 支付方式 */
.pay-methods {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
}

.pay-method {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 12px 8px;
  border: 2px solid var(--color-border);
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all 0.2s;
}

.pay-method:hover {
  border-color: var(--color-primary);
}

.pay-method--active {
  border-color: var(--color-primary);
  background: #eff6ff;
}

.pay-method__input {
  display: none;
}

.pay-method__icon {
  font-size: 24px;
}

.pay-method__label {
  font-size: 12px;
  color: var(--color-text-secondary);
}

/* 成功弹窗 */
.success-icon {
  font-size: 64px;
  text-align: center;
  margin-bottom: 8px;
}

.success-title {
  text-align: center;
  font-size: 22px;
  margin: 0 0 8px;
}

.success-desc {
  text-align: center;
  color: var(--color-text-secondary);
  margin: 0 0 16px;
}

.form-required {
  color: #dc2626;
}

/* 响应式 */
@media (max-width: 1024px) {
  .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  .pay-methods {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stat-cards {
    grid-template-columns: 1fr 1fr;
    gap: 8px;
  }

  .owner-page__filters-row .form-input {
    width: 100%;
  }
  .owner-page__filters-row {
    flex-direction: column;
    align-items: stretch;
  }

  .bill-card__body {
    grid-template-columns: 1fr;
    gap: 4px;
  }

  .owner-page__header {
    flex-direction: column;
    align-items: flex-start;
  }

  .owner-page__summary {
    width: 100%;
    justify-content: space-between;
  }

  .bill-card__header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .pay-methods {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 480px) {
  .stat-cards {
    grid-template-columns: 1fr 1fr;
    gap: 6px;
  }
  .stat-card__value {
    font-size: 20px;
  }
  .pay-methods {
    grid-template-columns: 1fr 1fr;
  }
}
</style>
