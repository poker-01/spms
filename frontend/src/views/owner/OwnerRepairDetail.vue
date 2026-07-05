<template>
  <div class="detail-page">
    <!-- 返回按钮 -->
    <div class="detail-page__back">
      <button class="btn btn-ghost" type="button" @click="goBack">
        ← 返回报修列表
      </button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="detail-page__loading">加载中...</div>

    <!-- 详情内容 -->
    <div v-else-if="detail" class="detail-page__content">
      <!-- 头部：状态 + 订单号 -->
      <div class="detail-page__header">
        <div class="detail-page__header-left">
          <h2 class="detail-page__title">报修详情</h2>
          <span :class="['detail-page__status', getStatusClass(detail.status)]">
            {{ detail.statusName }}
          </span>
        </div>
        <span class="detail-page__order">订单号：{{ detail.orderNo }}</span>
      </div>

      <!-- 主体信息 -->
      <div class="detail-page__body">
        <!-- 报修信息 -->
        <div class="detail-card">
          <h3 class="detail-card__title">报修信息</h3>
          <div class="detail-card__grid">
            <div class="detail-card__item">
              <label>报修类型</label>
              <span>{{ detail.repairTypeName }}</span>
            </div>
            <div class="detail-card__item">
              <label>联系电话</label>
              <span>{{ detail.repairPhone }}</span>
            </div>
            <div class="detail-card__item">
              <label>提交时间</label>
              <span>{{ formatDate(detail.createTime) }}</span>
            </div>
            <div v-if="detail.repairTime" class="detail-card__item">
              <label>维修时间</label>
              <span>{{ formatDate(detail.repairTime) }}</span>
            </div>
          </div>
          <div class="detail-card__desc">
            <label>问题描述</label>
            <p>{{ detail.repairDesc }}</p>
          </div>
        </div>

        <!-- 维修结果（已完成时显示） -->
        <div v-if="detail.status === 2" class="detail-card">
          <h3 class="detail-card__title">维修结果</h3>
          <div class="detail-card__grid">
            <div class="detail-card__item">
              <label>维修费用</label>
              <span class="detail-card__cost">¥{{ detail.repairCost?.toFixed(2) || '0.00' }}</span>
            </div>
            <div v-if="detail.evaluateScore" class="detail-card__item">
              <label>评价评分</label>
              <span class="detail-card__score">{{ '⭐'.repeat(detail.evaluateScore) }}</span>
            </div>
            <div v-if="detail.evaluateComment" class="detail-card__item detail-card__item--full">
              <label>评价内容</label>
              <span>{{ detail.evaluateComment }}</span>
            </div>
          </div>
          <div v-if="!detail.evaluateScore" class="detail-card__action">
            <button class="btn btn-primary" type="button" @click="showEvaluate = true">
              去评价
            </button>
          </div>
        </div>

        <!-- 取消原因（已取消时显示） -->
        <div v-if="detail.status === 3" class="detail-card detail-card--cancelled">
          <h3 class="detail-card__title">⚠️ 已取消</h3>
          <p class="detail-card__hint">该报修单已被取消，如有疑问请联系物业。</p>
        </div>
      </div>

      <!-- 底部操作按钮（待处理状态可取消） -->
      <div v-if="detail.status === 0" class="detail-page__footer">
        <button class="btn btn-danger" type="button" @click="handleCancel">
          取消报修
        </button>
      </div>
    </div>

    <!-- 评价弹窗 -->
    <div v-if="showEvaluate" class="dialog-overlay" @click.self="showEvaluate = false">
      <div class="dialog dialog--evaluate">
        <h3 class="dialog__title">评价报修</h3>
        <form class="dialog__body" @submit.prevent="handleEvaluate">
          <div class="form-field">
            <label class="form-label">评分 <span class="form-required">*</span></label>
            <div class="evaluate-stars">
              <button
                v-for="i in 5"
                :key="i"
                class="evaluate-star"
                :class="{ 'evaluate-star--active': evaluateForm.score >= i }"
                type="button"
                @click="evaluateForm.score = i"
              >
                ⭐
              </button>
              <span class="evaluate-score-label">{{ evaluateForm.score }} 分</span>
            </div>
          </div>
          <div class="form-field">
            <label class="form-label">评价内容</label>
            <textarea
              v-model="evaluateForm.comment"
              class="form-input"
              rows="4"
              placeholder="请描述维修体验（选填）"
            />
          </div>
          <div class="dialog__footer">
            <button class="btn btn-ghost" type="button" @click="showEvaluate = false">
              取消
            </button>
            <button class="btn btn-primary" type="submit" :disabled="evaluating">
              {{ evaluating ? '提交中...' : '提交评价' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { formatDate } from '@/utils/format'
import { getRepairDetail, cancelRepair, evaluateRepair } from '@/api/repair'
import type { RepairVO } from '@/api/repair'

defineOptions({
  name: 'OwnerRepairDetail',
})

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref<RepairVO | null>(null)
const showEvaluate = ref(false)
const evaluating = ref(false)

const evaluateForm = reactive({
  score: 0,
  comment: '',
})

/**
 * 加载报修详情
 * 接口：GET /api/v1/repairs/{orderId}
 */
const loadDetail = async () => {
  const orderId = Number(route.params.id)
  if (!orderId) {
    alert('参数错误')
    router.back()
    return
  }

  loading.value = true
  try {
    const { data } = await getRepairDetail(orderId)
    detail.value = data
  } catch (error) {
    console.error('加载报修详情失败:', error)
    alert('加载失败，请稍后重试')
    router.back()
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/owner/repairs')
}

/**
 * 取消报修
 * 接口：PUT /api/v1/repairs/{orderId}/cancel
 */
const handleCancel = async () => {
  if (!detail.value) return
  if (!confirm('确定要取消该报修单吗？')) return

  try {
    await cancelRepair(detail.value.id)
    detail.value.status = 3
    detail.value.statusName = '已取消'
    alert('已取消报修')
  } catch (error) {
    console.error('取消报修失败:', error)
    alert('取消失败，请稍后重试')
  }
}

/**
 * 提交评价
 * 接口：PUT /api/v1/repairs/evaluate
 */
const handleEvaluate = async () => {
  if (!detail.value) return
  if (!evaluateForm.score) {
    alert('请选择评分')
    return
  }

  evaluating.value = true
  try {
    await evaluateRepair({
      orderId: detail.value.id,
      score: evaluateForm.score,
      comment: evaluateForm.comment,
    })
    detail.value.evaluateScore = evaluateForm.score
    detail.value.evaluateComment = evaluateForm.comment
    showEvaluate.value = false
    alert('评价提交成功！')
  } catch (error) {
    console.error('提交评价失败:', error)
    alert('提交失败，请稍后重试')
  } finally {
    evaluating.value = false
  }
}

const getStatusClass = (status: number): string => {
  const map: Record<number, string> = {
    0: 'status--warning',
    1: 'status--primary',
    2: 'status--success',
    3: 'status--info',
  }
  return map[status] || ''
}

onMounted(loadDetail)
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

.status--info {
  background: #f1f5f9;
  color: #64748b;
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

.detail-card--cancelled {
  background: #fef2f2;
  border-color: #fca5a5;
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

.detail-card__cost {
  color: var(--color-primary);
  font-size: 18px;
  font-weight: 700;
}

.detail-card__score {
  font-size: 18px;
}

.detail-card__desc {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--color-border);
}

.detail-card__desc label {
  display: block;
  margin-bottom: 8px;
  font-size: 13px;
  color: var(--color-text-secondary);
}

.detail-card__desc p {
  margin: 0;
  padding: 12px 16px;
  border-radius: var(--radius-sm);
  background: var(--color-bg);
  line-height: 1.8;
}

.detail-card__action {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--color-border);
}

.detail-card__hint {
  margin: 0;
  color: #dc2626;
}

.detail-page__footer {
  margin-top: 20px;
  display: flex;
  gap: 12px;
}

/* 评价弹窗 */
.dialog--evaluate {
  width: min(480px, calc(100% - 32px));
}

.evaluate-stars {
  display: flex;
  align-items: center;
  gap: 4px;
}

.evaluate-star {
  font-size: 32px;
  background: none;
  border: none;
  cursor: pointer;
  opacity: 0.3;
  transition: opacity 0.2s;
  padding: 4px;
}

.evaluate-star:hover {
  opacity: 0.7;
}

.evaluate-star--active {
  opacity: 1;
}

.evaluate-score-label {
  margin-left: 12px;
  font-size: 16px;
  font-weight: 600;
  color: var(--color-primary);
}

@media (max-width: 768px) {
  .detail-card__grid {
    grid-template-columns: 1fr;
  }

  .detail-page__header {
    flex-direction: column;
    align-items: flex-start;
  }

  .evaluate-stars {
    flex-wrap: wrap;
  }
}
</style>
