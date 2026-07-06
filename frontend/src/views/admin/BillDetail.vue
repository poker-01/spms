<!-- src/views/admin/BillDetail.vue -->
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
            {{ detail.statusName }}
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
              <label>业主姓名</label>
              <span>{{ detail.ownerName || '-' }}</span>
            </div>
            <div class="detail-card__item">
              <label>所属房屋</label>
              <span>{{ formatHouse(detail) }}</span>
            </div>
            <div class="detail-card__item">
              <label>费用项目</label>
              <span>{{ detail.itemName }}</span>
            </div>
            <div class="detail-card__item">
              <label>账单金额</label>
              <span class="detail-card__amount">¥{{ detail.billAmount.toFixed(2) }}</span>
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
            <div v-if="detail.payMethod != null" class="detail-card__item">
              <label>支付方式</label>
              <span>{{ getPayMethodLabel(detail.payMethod) }}</span>
            </div>
            <div class="detail-card__item">
              <label>生成时间</label>
              <span>{{ formatDate(detail.createTime) }}</span>
            </div>
            <div v-if="detail.remark" class="detail-card__item detail-card__item--full">
              <label>备注</label>
              <span>{{ detail.remark }}</span>
            </div>
          </div>
        </div>

        <!-- 状态时间线 -->
        <div class="detail-card detail-card--status">
          <h3 class="detail-card__title">状态追踪</h3>
          <div class="status-timeline">
            <div class="status-step" :class="{ 'status-step--active': detail.status >= 0 }">
              <span class="status-step__dot">1</span>
              <div class="status-step__content">
                <span class="status-step__label">账单生成</span>
                <span class="status-step__time">{{ formatDate(detail.createTime) }}</span>
              </div>
            </div>
            <div class="status-step" :class="{ 'status-step--active': detail.status >= 2 }">
              <span class="status-step__dot">2</span>
              <div class="status-step__content">
                <span class="status-step__label">缴费完成</span>
                <span class="status-step__time">
                  {{ detail.payTime ? formatDate(detail.payTime) : '等待缴费' }}
                </span>
              </div>
            </div>
            <div v-if="detail.status === 3" class="status-step status-step--danger">
              <span class="status-step__dot">⚠️</span>
              <div class="status-step__content">
                <span class="status-step__label">已逾期</span>
                <span class="status-step__time status-step__time--danger">
                  已超过缴费截止日期
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- 缴费记录 -->
        <div v-if="detail.status === 2" class="detail-card detail-card--receipt">
          <h3 class="detail-card__title">📄 缴费凭证</h3>
          <div class="receipt-grid">
            <div class="receipt-item">
              <label>账单编号</label>
              <span>{{ detail.billNo }}</span>
            </div>
            <div class="receipt-item">
              <label>缴费金额</label>
              <span class="receipt-amount">¥{{ detail.billAmount.toFixed(2) }}</span>
            </div>
            <div class="receipt-item">
              <label>支付方式</label>
              <span>{{ getPayMethodLabel(detail.payMethod) }}</span>
            </div>
            <div class="receipt-item">
              <label>缴费时间</label>
              <span>{{ formatDate(detail.payTime) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部操作按钮 -->
      <div class="detail-page__footer">
        <!-- 待缴费/部分缴费 → 标记为已缴费（手动录入） -->
        <button
          v-if="detail.status === 0 || detail.status === 1"
          class="btn btn-success"
          type="button"
          @click="openPayDialog"
        >
          💳 标记为已缴费
        </button>
        <!-- 待缴费 → 删除 -->
        <button
          v-if="detail.status === 0"
          class="btn btn-danger"
          type="button"
          @click="handleDelete"
        >
          🗑️ 删除账单
        </button>
        <!-- 已缴费 → 打印凭证 -->
        <button
          v-if="detail.status === 2"
          class="btn btn-ghost"
          type="button"
          @click="handlePrint"
        >
          🖨️ 打印凭证
        </button>
      </div>
    </div>

    <!-- 标记缴费弹窗 -->
    <div v-if="payDialog.visible" class="dialog-overlay" @click.self="payDialog.visible = false">
      <div class="dialog dialog--pay">
        <h3 class="dialog__title">标记为已缴费</h3>
        <div class="dialog__body">
          <p class="dialog__info">
            账单编号：<strong>{{ detail?.billNo }}</strong>
          </p>
          <p class="dialog__info">
            缴费金额：<strong class="text-primary">¥{{ detail?.billAmount.toFixed(2) }}</strong>
          </p>
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
          <div class="form-field">
            <label class="form-label">备注（选填）</label>
            <input
              v-model="payDialog.remark"
              class="form-input"
              placeholder="请输入备注，如：现金收取"
            />
          </div>
        </div>
        <div class="dialog__footer">
          <button class="btn btn-ghost" type="button" @click="payDialog.visible = false">取消</button>
          <button class="btn btn-success" type="button" :disabled="payDialog.submitting" @click="handleMarkPaid">
            {{ payDialog.submitting ? '提交中...' : '确认缴费' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 删除确认弹窗 -->
    <div v-if="deleteDialog.visible" class="dialog-overlay" @click.self="deleteDialog.visible = false">
      <div class="dialog dialog--delete">
        <h3 class="dialog__title">⚠️ 确认删除</h3>
        <div class="dialog__body">
          <p class="delete-warning">
            确定要删除账单 <strong>{{ detail?.billNo }}</strong> 吗？
          </p>
          <p class="delete-hint">此操作不可恢复，请谨慎操作</p>
        </div>
        <div class="dialog__footer">
          <button class="btn btn-ghost" type="button" @click="deleteDialog.visible = false">取消</button>
          <button class="btn btn-danger" type="button" :disabled="deleteDialog.submitting" @click="handleConfirmDelete">
            {{ deleteDialog.submitting ? '删除中...' : '确认删除' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 打印凭证 -->
    <div v-if="printDialog.visible" class="dialog-overlay" @click.self="printDialog.visible = false">
      <div class="dialog dialog--print" id="print-area">
        <h3 class="dialog__title">📄 缴费凭证</h3>
        <div class="dialog__body">
          <div class="print-content">
            <div class="print-header">
              <h2>智慧物业管理系统</h2>
              <p>缴费凭证</p>
            </div>
            <div class="print-body">
              <div class="print-row">
                <span class="print-label">凭证编号：</span>
                <span class="print-value">{{ detail?.billNo }}</span>
              </div>
              <div class="print-row">
                <span class="print-label">业主姓名：</span>
                <span class="print-value">{{ detail?.ownerName || '-' }}</span>
              </div>
              <div class="print-row">
                <span class="print-label">房屋信息：</span>
                <span class="print-value">{{ formatHouse(detail) }}</span>
              </div>
              <div class="print-row">
                <span class="print-label">费用项目：</span>
                <span class="print-value">{{ detail?.itemName }}</span>
              </div>
              <div class="print-row">
                <span class="print-label">缴费金额：</span>
                <span class="print-value print-amount">¥{{ detail?.billAmount.toFixed(2) }}</span>
              </div>
              <div class="print-row">
                <span class="print-label">支付方式：</span>
                <span class="print-value">{{ getPayMethodLabel(detail?.payMethod) }}</span>
              </div>
              <div class="print-row">
                <span class="print-label">缴费时间：</span>
                <span class="print-value">{{ formatDate(detail?.payTime) }}</span>
              </div>
              <div class="print-row">
                <span class="print-label">账单周期：</span>
                <span class="print-value">{{ detail?.billPeriod || '-' }}</span>
              </div>
            </div>
            <div class="print-footer">
              <p>打印时间：{{ new Date().toLocaleString() }}</p>
              <p>本凭证仅作缴费证明，如有疑问请联系物业</p>
            </div>
          </div>
        </div>
        <div class="dialog__footer">
          <button class="btn btn-ghost" type="button" @click="printDialog.visible = false">关闭</button>
          <button class="btn btn-primary" type="button" @click="handlePrintReceipt">🖨️ 打印</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { formatDate } from '@/utils/format'
import {
  getAdminBillDetail,
  payBill,
  deleteBill,
  type BillDetail,
} from '@/api/bill-admin'

defineOptions({
  name: 'AdminBillDetail',
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
  { value: 0, label: '现金', icon: '💰' },
  { value: 1, label: '银行转账', icon: '🏦' },
  { value: 2, label: '微信支付', icon: '💚' },
  { value: 3, label: '支付宝', icon: '💙' },
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
  payMethod: 0,
  remark: '',
  submitting: false,
})

// 删除弹窗
const deleteDialog = reactive({
  visible: false,
  submitting: false,
})

// 打印弹窗
const printDialog = reactive({
  visible: false,
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
 * 加载账单详情
 * 接口：GET /api/v1/finance/bills/{id}
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
    const { data } = await getAdminBillDetail(id)
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
  router.push('/admin/bills')
}

/**
 * 打开缴费弹窗
 */
const openPayDialog = () => {
  payDialog.payMethod = 0
  payDialog.remark = ''
  payDialog.visible = true
}

/**
 * 缴费登记
 * 接口：POST /api/v1/finance/bills/pay
 */
const handleMarkPaid = async () => {
  if (!detail.value) return
  if (payDialog.payMethod === undefined) {
    alert('请选择支付方式')
    return
  }

  payDialog.submitting = true
  try {
    const remaining = Number(
      ((detail.value.billAmount || 0) - (detail.value.paidAmount || 0)).toFixed(2)
    )
    await payBill({
      billId: detail.value.id,
      payAmount: remaining > 0 ? remaining : 0,
      payMethod: payDialog.payMethod,
      receiptNo: payDialog.remark || undefined,
    })
    await loadDetail()
    payDialog.visible = false
    alert('缴费登记成功！')
  } catch (error) {
    console.error('标记缴费失败:', error)
    alert('操作失败，请稍后重试')
  } finally {
    payDialog.submitting = false
  }
}

/**
 * 打开删除确认弹窗
 */
const handleDelete = () => {
  deleteDialog.visible = true
}

/**
 * 确认删除
 * 接口：DELETE /api/v1/finance/bills/{id}
 */
const handleConfirmDelete = async () => {
  if (!detail.value) return

  deleteDialog.submitting = true
  try {
    await deleteBill(detail.value.id)
    deleteDialog.visible = false
    alert('删除成功！')
    router.push('/admin/bills')
  } catch (error) {
    console.error('删除失败:', error)
    alert('删除失败，请稍后重试')
  } finally {
    deleteDialog.submitting = false
  }
}

/**
 * 打开打印弹窗
 */
const handlePrint = () => {
  printDialog.visible = true
}

/**
 * 打印凭证
 */
const handlePrintReceipt = () => {
  const printContent = document.getElementById('print-area')
  if (!printContent) return

  const win = window.open('', '_blank')
  if (!win) {
    alert('请允许弹出窗口')
    return
  }

  const content = printContent.innerHTML
  win.document.write(`
    <html>
      <head>
        <title>缴费凭证</title>
        <style>
          body { font-family: 'Microsoft YaHei', sans-serif; padding: 40px; }
          .print-content { max-width: 600px; margin: 0 auto; }
          .print-header { text-align: center; border-bottom: 2px solid #333; padding-bottom: 16px; margin-bottom: 20px; }
          .print-header h2 { margin: 0; color: #1a2332; }
          .print-header p { margin: 4px 0 0; color: #6b7280; font-size: 14px; }
          .print-body { padding: 12px 0; }
          .print-row { display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px dashed #e5e7eb; }
          .print-label { color: #6b7280; }
          .print-value { font-weight: 500; }
          .print-amount { color: #2563eb; font-size: 20px; font-weight: 700; }
          .print-footer { margin-top: 20px; padding-top: 16px; border-top: 2px solid #333; text-align: center; color: #6b7280; font-size: 12px; }
          .print-footer p { margin: 4px 0; }
          @media print {
            body { padding: 20px; }
          }
        </style>
      </head>
      <body>
        <div class="print-content">
          ${content}
        </div>
        <script>
          window.onload = function() { window.print(); window.close(); }
        <\/script>
      </body>
    </html>
  `)
  win.document.close()

  printDialog.visible = false
}

/**
 * 获取支付方式标签
 */
const getPayMethodLabel = (method?: number) => {
  if (method === undefined || method === null) return '-'
  return payMethodLabels[method] || '其他'
}

/**
 * 获取状态样式（后端 BillStatus：0-待缴费 1-部分缴费 2-已缴费 3-已逾期）
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
.text-primary {
  color: #2563eb;
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

.detail-card--receipt {
  background: #ecfdf5;
  border-color: #6ee7b7;
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

.detail-card__item--full {
  grid-column: 1 / -1;
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

.status-step__time--danger {
  color: #dc2626;
}

.detail-page__footer {
  margin-top: 20px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

/* 缴费凭证 */
.receipt-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px 24px;
}

.receipt-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.receipt-item label {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.receipt-item span {
  font-weight: 500;
}

.receipt-amount {
  color: #059669;
  font-size: 18px;
  font-weight: 700;
}

/* 弹窗 */
.dialog--pay,
.dialog--delete,
.dialog--print {
  width: min(520px, calc(100% - 32px));
}

.dialog__info {
  margin: 4px 0 12px;
  font-size: 14px;
  color: var(--color-text-secondary);
}
.dialog__info strong {
  color: var(--color-text);
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

.delete-warning {
  font-size: 16px;
  text-align: center;
  margin: 8px 0;
}
.delete-hint {
  text-align: center;
  color: #dc2626;
  font-size: 14px;
  margin: 4px 0 0;
}

/* 打印内容 */
.print-content {
  max-width: 600px;
  margin: 0 auto;
  padding: 0 8px;
}

.print-header {
  text-align: center;
  border-bottom: 2px solid #333;
  padding-bottom: 16px;
  margin-bottom: 20px;
}

.print-header h2 {
  margin: 0;
  color: #1a2332;
  font-size: 20px;
}

.print-header p {
  margin: 4px 0 0;
  color: #6b7280;
  font-size: 14px;
}

.print-body {
  padding: 8px 0;
}

.print-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px dashed #e5e7eb;
}

.print-label {
  color: #6b7280;
  font-size: 14px;
}

.print-value {
  font-weight: 500;
  font-size: 14px;
}

.print-amount {
  color: #2563eb;
  font-size: 22px;
  font-weight: 700;
}

.print-footer {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 2px solid #333;
  text-align: center;
  color: #6b7280;
  font-size: 12px;
}

.print-footer p {
  margin: 4px 0;
}

.form-required {
  color: #dc2626;
}

/* 响应式 */
@media (max-width: 768px) {
  .detail-card__grid {
    grid-template-columns: 1fr;
  }

  .receipt-grid {
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

  .print-row {
    flex-direction: column;
    gap: 4px;
  }
}
</style>
