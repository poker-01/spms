<!-- src/views/admin/ComplaintDetail.vue -->
<template>
  <div class="detail-page">
    <!-- 返回按钮 -->
    <div class="detail-page__back">
      <button class="btn btn-ghost" type="button" @click="goBack">
        ← 返回投诉列表
      </button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="detail-page__loading">加载中...</div>

    <!-- 详情内容 -->
    <div v-else-if="detail" class="detail-page__content">
      <!-- 头部 -->
      <div class="detail-page__header">
        <div class="detail-page__header-left">
          <h2 class="detail-page__title">{{ detail.title }}</h2>
          <span :class="['detail-page__status', getStatusClass(detail.status)]">
            {{ detail.statusName }}
          </span>
        </div>
        <span class="detail-page__order">编号：{{ detail.complaintNo }}</span>
      </div>

      <!-- 主体 -->
      <div class="detail-page__body">
        <!-- 投诉信息 -->
        <div class="detail-card">
          <h3 class="detail-card__title">投诉信息</h3>
          <div class="detail-card__grid">
            <div class="detail-card__item">
              <label>业主姓名</label>
              <span>{{ detail.ownerName || '-' }}</span>
            </div>
            <div class="detail-card__item">
              <label>联系电话</label>
              <span>{{ detail.contactPhone || '-' }}</span>
            </div>
            <div class="detail-card__item">
              <label>投诉类型</label>
              <span>{{ detail.typeName }}</span>
            </div>
            <div class="detail-card__item">
              <label>提交时间</label>
              <span>{{ formatDate(detail.createTime) }}</span>
            </div>
            <div v-if="detail.replyTime" class="detail-card__item">
              <label>回复时间</label>
              <span>{{ formatDate(detail.replyTime) }}</span>
            </div>
          </div>
          <div class="detail-card__desc">
            <label>投诉内容</label>
            <p>{{ detail.content }}</p>
          </div>
        </div>

        <!-- 物业回复 -->
        <div v-if="detail.status === 2 && detail.replyContent" class="detail-card detail-card--reply">
          <h3 class="detail-card__title">📌 物业回复</h3>
          <p class="detail-card__reply-content">{{ detail.replyContent }}</p>
        </div>

        <!-- 已取消 -->
        <div v-if="detail.status === 3" class="detail-card detail-card--cancelled">
          <h3 class="detail-card__title">⚠️ 已取消</h3>
          <p class="detail-card__hint">该投诉已被业主取消。</p>
        </div>
      </div>

      <!-- 底部操作按钮 -->
      <div class="detail-page__footer">
        <!-- 待处理/处理中 → 回复 -->
        <button
          v-if="detail.status === 0 || detail.status === 1"
          class="btn btn-primary"
          type="button"
          @click="openReplyDialog"
        >
          回复投诉
        </button>

        <!-- 已回复 → 查看回复 -->
        <button
          v-if="detail.status === 2"
          class="btn btn-ghost"
          type="button"
          @click="showReplyDetail = true"
        >
          查看回复
        </button>
      </div>
    </div>

    <!-- 回复弹窗 -->
    <div v-if="replyDialog.visible" class="dialog-overlay" @click.self="replyDialog.visible = false">
      <div class="dialog dialog--reply">
        <h3 class="dialog__title">回复投诉</h3>
        <div class="dialog__body">
          <p class="dialog__info">
            投诉编号：<strong>{{ detail?.complaintNo }}</strong>
          </p>
          <p class="dialog__info">
            投诉内容：{{ detail?.content }}
          </p>
          <div class="form-field">
            <label class="form-label">回复内容 <span class="form-required">*</span></label>
            <textarea
              v-model="replyDialog.content"
              class="form-input"
              rows="5"
              placeholder="请输入回复内容"
            />
          </div>
        </div>
        <div class="dialog__footer">
          <button class="btn btn-ghost" type="button" @click="replyDialog.visible = false">
            取消
          </button>
          <button class="btn btn-primary" type="button" :disabled="replyDialog.submitting" @click="handleReply">
            {{ replyDialog.submitting ? '提交中...' : '确认回复' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 查看回复弹窗 -->
    <div v-if="showReplyDetail" class="dialog-overlay" @click.self="showReplyDetail = false">
      <div class="dialog dialog--reply-detail">
        <h3 class="dialog__title">📌 物业回复</h3>
        <div class="dialog__body">
          <div class="reply-detail__content">
            <p>{{ detail?.replyContent }}</p>
          </div>
          <div class="reply-detail__meta">
            <span>回复时间：{{ formatDate(detail?.replyTime) }}</span>
          </div>
        </div>
        <div class="dialog__footer">
          <button class="btn btn-primary" type="button" @click="showReplyDetail = false">
            关闭
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { formatDate } from '@/utils/format'
import { getComplaintDetail, replyComplaint } from '@/api/complaint'
import type { ComplaintVO } from '@/api/complaint'

defineOptions({
  name: 'AdminComplaintDetail',
})

// ============================================================
// 状态
// ============================================================

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref<ComplaintVO | null>(null)
const showReplyDetail = ref(false)

const replyDialog = reactive({
  visible: false,
  content: '',
  submitting: false,
})

// ============================================================
// 方法
// ============================================================

/**
 * 加载投诉详情
 * 接口：GET /api/v1/complaints/{complaintId}
 */
const loadDetail = async () => {
  const complaintId = Number(route.params.id)
  if (!complaintId) {
    alert('参数错误')
    router.back()
    return
  }

  loading.value = true
  try {
    const { data } = await getComplaintDetail(complaintId)
    detail.value = data
  } catch (error) {
    console.error('加载投诉详情失败:', error)
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
  router.push('/admin/complaints')
}

/**
 * 打开回复弹窗
 */
const openReplyDialog = () => {
  replyDialog.content = ''
  replyDialog.visible = true
}

/**
 * 回复投诉
 * 接口：PUT /api/v1/complaints/reply
 */
const handleReply = async () => {
  if (!detail.value) return
  if (!replyDialog.content.trim()) {
    alert('请填写回复内容')
    return
  }

  replyDialog.submitting = true
  try {
    await replyComplaint({
      complaintId: detail.value.id,
      replyContent: replyDialog.content,
    })
    await loadDetail()
    replyDialog.visible = false
    alert('回复成功！')
  } catch (error) {
    console.error('回复失败:', error)
    alert('回复失败，请稍后重试')
  } finally {
    replyDialog.submitting = false
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
    3: 'status--info',
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
  font-size: 20px;
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

.detail-card--reply {
  background: #ecfdf5;
  border-color: #6ee7b7;
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

.detail-card__item label {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.detail-card__item span {
  font-weight: 500;
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

.detail-card__reply-content {
  margin: 0;
  font-size: 15px;
  line-height: 1.8;
}

.detail-card__hint {
  margin: 0;
  color: #dc2626;
}

.detail-page__footer {
  margin-top: 20px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

/* 弹窗样式 */
.dialog--reply,
.dialog--reply-detail {
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

.form-required {
  color: #dc2626;
}

.reply-detail__content {
  padding: 16px;
  border-radius: var(--radius-sm);
  background: #ecfdf5;
  border-left: 3px solid #059669;
  font-size: 15px;
  line-height: 1.8;
}

.reply-detail__meta {
  margin-top: 12px;
  font-size: 13px;
  color: var(--color-text-secondary);
}

@media (max-width: 768px) {
  .detail-card__grid {
    grid-template-columns: 1fr;
  }
  .detail-page__header {
    flex-direction: column;
    align-items: flex-start;
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
