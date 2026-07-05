<template>
  <div class="detail-page">
    <div class="detail-page__back">
      <button class="btn btn-ghost" type="button" @click="goBack">
        ← 返回投诉列表
      </button>
    </div>

    <div v-if="loading" class="detail-page__loading">加载中...</div>

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
              <label>投诉类型</label>
              <span>{{ detail.typeName }}</span>
            </div>
            <div class="detail-card__item">
              <label>联系电话</label>
              <span>{{ detail.contactPhone || '-' }}</span>
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
          <p class="detail-card__hint">该投诉已被取消，如有疑问请联系物业。</p>
        </div>
      </div>

      <!-- 底部操作 -->
      <div v-if="detail.status === 0" class="detail-page__footer">
        <button class="btn btn-danger" type="button" @click="handleCancel">取消投诉</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { formatDate } from '@/utils/format'

defineOptions({
  name: 'OwnerComplaintDetail',
})

interface ComplaintDetail {
  id: number
  complaintNo: string
  type: number
  typeName: string
  title: string
  content: string
  contactPhone?: string
  status: number
  statusName: string
  replyContent?: string
  replyTime?: string
  createTime: string
}

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref<ComplaintDetail | null>(null)

const mockDetail: ComplaintDetail = {
  id: 1,
  complaintNo: 'TS20260705001',
  type: 1,
  typeName: '物业服务',
  title: '楼道卫生不干净',
  content: '最近一周楼道都没有人打扫，垃圾堆积，异味严重，希望物业尽快处理。',
  contactPhone: '13800138001',
  status: 0,
  statusName: '待处理',
  createTime: '2026-07-05 09:30:00',
}

const loadDetail = async () => {
  const id = Number(route.params.id)
  if (!id) { alert('参数错误'); router.back(); return }

  loading.value = true
  try {
    // 接口：GET /api/v1/owner/complaints/{id}
    // const { data } = await getComplaintDetail(id)
    // detail.value = data

    await new Promise((resolve) => setTimeout(resolve, 300))
    detail.value = { ...mockDetail, id }
  } catch (error) {
    console.error('加载失败:', error)
    alert('加载失败')
    router.back()
  } finally {
    loading.value = false
  }
}

const goBack = () => router.push('/owner/complaints')

const handleCancel = async () => {
  if (!detail.value || !confirm('确定取消吗？')) return
  try {
    // 接口：PUT /api/v1/owner/complaints/{id}/cancel
    await new Promise((resolve) => setTimeout(resolve, 500))
    detail.value.status = 3
    detail.value.statusName = '已取消'
    alert('已取消投诉')
  } catch (error) {
    alert('取消失败')
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
}

@media (max-width: 768px) {
  .detail-card__grid {
    grid-template-columns: 1fr;
  }
  .detail-page__header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
