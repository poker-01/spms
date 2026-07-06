<!-- src/views/owner/OwnerBillDetail.vue -->
<template>
  <div class="detail-page">
    <!-- 返回按钮 -->
    <div class="detail-page__back">
      <button class="btn btn-ghost" type="button" @click="goBack">
        ← 返回账单列表
      </button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="detail-page__loading">加载中...</div>

    <!-- 详情内容 -->
    <div v-else-if="detail" class="detail-page__content">
      <!-- 头部：状态 + 账单号 -->
      <div class="detail-page__header">
        <div class="detail-page__header-left">
          <h2 class="detail-page__title">{{ detail.itemName }}</h2>
          <span :class="['detail-page__status', getStatusClass(detail.status)]">
            {{ getStatusName(detail.status) }}
          </span>
        </div>
        <span class="detail-page__order">账单号：{{ detail.billNo }}</span>
      </div>

      <!-- 主体信息 -->
      <div class="detail-page__body">
        <!-- 账单信息 -->
        <div class="detail-card">
          <h3 class="detail-card__title">账单信息</h3>
          <div class="detail-card__grid">
            <div class="detail-card__item">
              <label>费用项目</label>
              <span>{{ detail.itemName }}</span>
            </div>
            <div class="detail-card__item">
              <label>账单金额</label>
              <span class="detail-card__amount">¥{{ detail.billAmount.toFixed(2) }}</span>
            </div>
            <div class="detail-card__item">
              <label>所属房屋</label>
              <span>{{ formatHouse(detail) }}</span>
            </div>
            <div class="detail-card__item">
              <label>账单周期</label>
              <span>{{ detail.billPeriod || '-' }}</span>
            </div>
            <div class="detail-card__item">
              <label>缴费截止</label>
              <span :class="{ 'text-danger': detail.status === 3 }">
                {{ formatDate(detail.payDeadline) }}
              </span>
            </div>
            <div v-if="detail.payTime" class="detail-card__item">
              <label>缴费时间</label>
              <span>{{ formatDate(detail.payTime) }}</span>
            </div>
            <div class="detail-card__item">
              <label>生成时间</label>
              <span>{{ formatDate(detail.createTime) }}</span>
            </div>
            <div v-if="detail.payMethod" class="detail-card__item">
              <label>支付方式</label>
              <span>{{ getPayMethodLabel(detail.payMethod) }}</span>
            </div>
          </div>
        </div>

        <!-- 状态说明 -->
        <div class="detail-card detail-card--status">
          <h3 class="detail-card__title">状态说明</h3>
          <div class="status-timeline">
            <div class="status-step" :class="{ 'status-step--active': detail.status >= 0 }">
              <span class="status-step__dot">1</span>
              <div class="status-step__content">
                <span class="status-step__label">账单生成</span>
                <span class="status-step__time">{{ formatDate(detail.createTime) }}</span>
              </div>
            </div>
            <div class="status-step" :class="{ 'status-step--active': detail.status === 1 }">
              <span class="status-step__dot">2</span>
              <div class="status-step__content">
                <span class="status-step__label">缴费完成</span>
                <span class="status-step__time">{{ detail.payTime ? formatDate(detail.payTime) : '等待缴费' }}</span>
              </div>
            </div>
            <div v-if="detail.status === 2" class="status-step status-step--danger">
              <span class="status-step__dot">⚠️</span>
              <div class="status-step__content">
                <span class="status-step__label">已逾期</span>
                <span class="status-step__time">请尽快缴费，避免产生滞纳金</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部操作按钮 -->
      <div class="detail-page__footer">
        <!-- 待缴费/部分缴费 → 去缴费 -->
        <button
          v-if="detail.status === 0 || detail.status === 1"
          class="btn btn-primary"
          type="button"
          @click="openPayDialog"
        >
          💳 去缴费
        </button>
        <!-- 已逾期 → 立即缴费 -->
        <button
          v-if="detail.status === 3"
          class="btn btn-danger"
          type="button"
          @click="openPayDialog"
        >
          🔔 立即缴费
        </button>
        <!-- 已缴费 → 查看凭证 -->
        <button
          v-if="detail.status === 2"
          class="btn btn-ghost"
          type="button"
          @click="handleViewReceipt"
        >
          📄 查看凭证
        </button>
      </div>
    </div>

    <!-- 缴费弹窗 -->
    <div v-if="payDialog.visible" class="dialog-overlay" @click.self="payDialog.visible = false">
      <div class="dialog dialog--pay">
        <h3 class="dialog__title">确认缴费</h3>
        <div class="dialog__body">
          <div class="pay-info">
            <div class="pay-info__row">
              <span class="pay-info__label">账单编号</span>
              <span class="pay-info__value">{{ detail?.billNo }}</span>
            </div>
            <div class="pay-info__row">
              <span class="pay-info__label">费用项目</span>
              <span class="pay-info__value">{{ detail?.itemName }}</span>
            </div>
            <div class="pay-info__row">
              <span class="pay-info__label">缴费金额</span>
              <span class="pay-info__value pay-info__amount">¥{{ detail?.billAmount.toFixed(2) }}</span>
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
          <button class="btn btn-primary" type="button" @click="handlePaySuccessConfirm">知道了</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { formatDate } from '@/utils/format'
import { getOwnerBillDetail, payOwnerBill, type BillDetail } from '@/api/owner'

defineOptions({
  name: 'OwnerBillDetail',
})

// ============================================================
// 状态
// ============================================================

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref<BillDetail | null>(null)

// 支付方式（与后端 PayMethod 枚举 code 对应：0-现金 1-银行转账 2-微信支付 3-支付宝）
const payMethods = [
  { value: 2, label: '微信支付', icon: '💚' },
  { value: 3, label: '支付宝', icon: '💙' },
  { value: 0, label: '现金', icon: '💰' },
  { value: 1, label: '银行转账', icon: '🏦' },
]

const payMethodLabels: Record<number, string> = {
  0: '现金',
  1: '银行转账',
  2: '微信支付',
  3: '支付宝',
}

// 缴费弹窗
const payDialog = reactive({
  visible: false,
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
 * 格式化房屋信息
 */
const formatHouse = (row?: BillDetail | null): string => {
  if (!row) return '-'
  const parts = [row.buildingName, row.houseNumber].filter(Boolean)
  return parts.length ? parts.join(' ') : '-'
}

/**
 * 获取状态名称（后端 BillStatus：0-待缴费 1-部分缴费 2-已缴费 3-已逾期）
 */
const getStatusName = (status?: number): string => {
  if (status === undefined) return '-'
  const map: Record<number, string> = {
    0: '待缴费',
    1: '部分缴费',
    2: '已缴费',
    3: '已逾期',
  }
  return map[status] || '未知'
}

/**
 * 加载账单详情
 * 接口：GET /api/v1/owner/bills/{id}
 */
const loadDetail = async () => {
  const id = Number(route.params.id)
  if (!id) {
    alert('参数错误')
    router.back()
    return
  }

  loading.value = true
  try {
    const { data } = await getOwnerBillDetail(id)
    detail.value = data
  } catch (error) {
    console.error('加载账单详情失败:', error)
    alert('加载失败，请稍后重试')
    router.back()
  } finally {
    loading.value = false
  }
}

/**
 * 返回列表
 */
const goBack = () => {
  router.push('/owner/bills')
}

/**
 * 打开缴费弹窗
 */
const openPayDialog = () => {
  payDialog.payMethod = 2
  payDialog.visible = true
}

/**
 * 确认缴费
 * 接口：POST /api/v1/owner/bills/{id}/pay
 */
const handlePay = async () => {
  if (!detail.value) return
  if (payDialog.payMethod === undefined) {
    alert('请选择支付方式')
    return
  }

  payDialog.submitting = true
  try {
    await payOwnerBill(detail.value.id, payDialog.payMethod)

    paySuccess.billNo = detail.value.billNo
    payDialog.visible = false
    paySuccess.visible = true
  } catch (error) {
    console.error('缴费失败:', error)
    alert('缴费失败，请稍后重试')
  } finally {
    payDialog.submitting = false
  }
}

/**
 * 缴费成功确认
 */
const handlePaySuccessConfirm = () => {
  paySuccess.visible = false
  // 刷新详情
  loadDetail()
}

/**
 * 查看凭证
 */
const handleViewReceipt = () => {
  alert(`📄 缴费凭证\n账单号：${detail.value?.billNo}\n金额：¥${detail.value?.billAmount.toFixed(2)}\n缴费时间：${formatDate(detail.value?.payTime)}`)
}

/**
 * 获取支付方式标签
 */
const getPayMethodLabel = (method?: number) => {
  if (method === undefined || method === null) return '-'
  return payMethodLabels[method] || '其他'
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
  loadDetail()
})
</script>

<style scoped>
.detail-page {
  max-width: 900px;
  margin: 0 auto;
}

.detail-page__back {
  margin-bottom: 20px;
}

.detail-page__loading {
  padding: 60px 20px;
  text-align: center;
  color: var(--color-text-secondary);
}

.detail-page__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 24px;
  padding: 20px 24px;
  border-radius: var(--radius-lg);
  background: var(--color-card);
  border: 1px solid var(--color-border);
}

.detail-page__header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.detail-page__title {
  margin: 0;
  font-size: 22px;
}

.detail-page__status {
  display: inline-flex;
  padding: 4px 14px;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 600;
}

.detail-page__order {
  color: var(--color-text-secondary);
  font-size: 14px;
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

.text-danger {
  color: #dc2626;
}

.detail-page__body {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-card {
  padding: 24px;
  border-radius: var(--radius-lg);
  background: var(--color-card);
  border: 1px solid var(--color-border);
}

.detail-card--status {
  background: #f8fafc;
}

.detail-card__title {
  margin: 0 0 16px;
  font-size: 16px;
}

.detail-card__grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px 24px;
}

.detail-card__item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-card__item label {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.detail-card__item span {
  font-weight: 500;
}

.detail-card__amount {
  color: var(--color-primary);
  font-size: 20px;
  font-weight: 700;
}

/* 状态时间线 */
.status-timeline {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 4px 0;
}

.status-step {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  opacity: 0.5;
}

.status-step--active {
  opacity: 1;
}

.status-step--danger {
  opacity: 1;
}

.status-step--danger .status-step__dot {
  background: #fef2f2;
  color: #dc2626;
  border-color: #dc2626;
}

.status-step__dot {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--color-bg);
  border: 2px solid var(--color-border);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  color: var(--color-text-secondary);
  flex-shrink: 0;
}

.status-step--active .status-step__dot {
  background: #dbeafe;
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.status-step__content {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding-top: 2px;
}

.status-step__label {
  font-size: 14px;
  font-weight: 500;
}

.status-step__time {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.detail-page__footer {
  margin-top: 20px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

/* 缴费弹窗 */
.dialog--pay,
.dialog--success {
  width: min(480px, calc(100% - 32px));
}

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

@media (max-width: 768px) {
  .detail-card__grid {
    grid-template-columns: 1fr;
  }

  .detail-page__header {
    flex-direction: column;
    align-items: flex-start;
  }

  .pay-methods {
    grid-template-columns: 1fr 1fr;
  }

  .detail-page__footer {
    flex-direction: column;
  }
  .detail-page__footer .btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
