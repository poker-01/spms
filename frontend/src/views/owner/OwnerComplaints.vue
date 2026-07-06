<template>
  <div class="owner-page">
    <div class="owner-page__header">
      <h2 class="owner-page__title">投诉建议</h2>
      <button class="btn btn-primary" type="button" @click="dialogVisible = true">
        + 提交投诉
      </button>
    </div>

    <!-- 搜索栏 -->
    <div class="owner-page__filters">
      <div class="owner-page__filters-row">
        <input
          v-model="query.keyword"
          class="form-input"
          placeholder="搜索标题/内容"
          @keyup.enter="handleSearch"
        />
        <select v-model="query.status" class="form-input" @change="handleSearch">
          <option :value="undefined">全部状态</option>
          <option v-for="item in statuses" :key="item.value" :value="item.value">
            {{ item.label }}
          </option>
        </select>
        <button class="btn btn-primary" type="button" @click="handleSearch">查询</button>
        <button class="btn btn-ghost" type="button" @click="handleReset">重置</button>
      </div>
    </div>

    <!-- 列表 -->
    <div v-if="loading" class="owner-page__loading">加载中...</div>
    <div v-else-if="!complaintList.length" class="owner-page__empty">
      <p>暂无投诉记录</p>
    </div>
    <div v-else class="owner-page__list">
      <article
        v-for="item in complaintList"
        :key="item.id"
        class="complaint-card"
        @click="handleViewDetail(item)"
      >
        <div class="complaint-card__header">
          <div class="complaint-card__left">
            <span class="complaint-card__title">{{ item.title }}</span>
            <span :class="['complaint-card__status', getStatusClass(item.status)]">
              {{ item.statusName }}
            </span>
          </div>
          <span class="complaint-card__type">{{ item.typeName }}</span>
        </div>

        <div class="complaint-card__body">
          <p class="complaint-card__content">{{ item.content }}</p>
          <div class="complaint-card__meta">
            <span>提交时间：{{ formatDate(item.createTime) }}</span>
            <span v-if="item.replyTime">回复时间：{{ formatDate(item.replyTime) }}</span>
          </div>
          <div v-if="item.status === 2 && item.replyContent" class="complaint-card__reply">
            <span class="complaint-card__reply-label">📌 物业回复：</span>
            <span>{{ item.replyContent }}</span>
          </div>
        </div>

        <div class="complaint-card__footer" @click.stop>
          <button
            v-if="item.status === 0"
            class="btn btn-sm btn-danger"
            type="button"
            @click="handleCancel(item)"
          >
            取消投诉
          </button>
        </div>
      </article>
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="owner-page__pagination">
      <button
        class="btn btn-sm btn-ghost"
        :disabled="query.pageNum <= 1"
        @click="changePage(query.pageNum - 1)"
      >
        上一页
      </button>
      <span class="pagination-info">
        第 {{ query.pageNum }} / {{ totalPages }} 页，共 {{ total }} 条
      </span>
      <button
        class="btn btn-sm btn-ghost"
        :disabled="query.pageNum >= totalPages"
        @click="changePage(query.pageNum + 1)"
      >
        下一页
      </button>
    </div>

    <!-- 提交投诉弹窗 -->
    <div v-if="dialogVisible" class="dialog-overlay" @click.self="dialogVisible = false">
      <div class="dialog dialog--complaint">
        <h3 class="dialog__title">提交投诉</h3>
        <form class="dialog__body" @submit.prevent="handleSubmit">
          <div class="form-field">
            <label class="form-label">投诉类型 <span class="form-required">*</span></label>
            <select v-model="form.type" class="form-input">
              <option :value="0">请选择投诉类型</option>
              <option v-for="item in types" :key="item.value" :value="item.value">
                {{ item.label }}
              </option>
            </select>
          </div>

          <div class="form-field">
            <label class="form-label">标题 <span class="form-required">*</span></label>
            <input
              v-model="form.title"
              class="form-input"
              placeholder="请输入投诉标题"
            />
          </div>

          <div class="form-field">
            <label class="form-label">投诉内容 <span class="form-required">*</span></label>
            <textarea
              v-model="form.content"
              class="form-input"
              rows="5"
              placeholder="请详细描述投诉内容"
            />
          </div>

          <div class="form-field">
            <label class="form-label">联系电话</label>
            <input
              v-model="form.contactPhone"
              class="form-input"
              type="tel"
              placeholder="请填写联系电话（选填）"
            />
          </div>

          <div class="dialog__footer">
            <button class="btn btn-ghost" type="button" @click="dialogVisible = false">
              取消
            </button>
            <button class="btn btn-primary" type="submit" :disabled="submitting">
              {{ submitting ? '提交中...' : '提交投诉' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { formatDate } from '@/utils/format'
import { getOwnerComplaints, applyComplaint, cancelOwnerComplaint } from '@/api/owner'
import type { OwnerComplaint } from '@/utils/api-types'

defineOptions({
  name: 'OwnerComplaints',
})

// ============================================================
// 状态
// ============================================================

const router = useRouter()
const loading = ref(false)
const allComplaints = ref<OwnerComplaint[]>([])
const complaintList = ref<OwnerComplaint[]>([])
const dialogVisible = ref(false)
const submitting = ref(false)
const total = ref(0)
const totalPages = ref(1)

// 投诉类型（前端写死）
const types = [
  { value: 1, label: '物业服务' },
  { value: 2, label: '设施维修' },
  { value: 3, label: '噪音扰民' },
  { value: 4, label: '其他' },
]

// 投诉状态（前端写死）
const statuses = [
  { value: 0, label: '待处理' },
  { value: 1, label: '处理中' },
  { value: 2, label: '已回复' },
  { value: 3, label: '已关闭' },
  { value: 4, label: '已取消' },
]

// 查询参数
const query = reactive({
  status: undefined as number | undefined,
  keyword: '',
  pageNum: 1,
  pageSize: 10,
})

// 提交表单
const form = reactive({
  type: 0,
  title: '',
  content: '',
  contactPhone: '',
})

// ============================================================
// 客户端分页与筛选
// ============================================================

const filteredComplaints = computed(() => {
  let list = allComplaints.value
  if (query.status !== undefined) {
    list = list.filter(c => c.status === query.status)
  }
  if (query.keyword.trim()) {
    const kw = query.keyword.trim().toLowerCase()
    list = list.filter(c =>
      c.title.toLowerCase().includes(kw) ||
      c.content.toLowerCase().includes(kw)
    )
  }
  return list
})

const applyPagination = () => {
  const list = filteredComplaints.value
  total.value = list.length
  totalPages.value = Math.max(1, Math.ceil(list.length / query.pageSize))
  const start = (query.pageNum - 1) * query.pageSize
  complaintList.value = list.slice(start, start + query.pageSize)
}

// ============================================================
// 方法
// ============================================================

/**
 * 加载投诉列表
 */
const loadData = async () => {
  loading.value = true
  try {
    const { data } = await getOwnerComplaints()
    allComplaints.value = data
    applyPagination()
  } catch (error) {
    console.error('加载投诉列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 提交投诉
 */
const handleSubmit = async () => {
  if (!form.type) {
    alert('请选择投诉类型')
    return
  }
  if (!form.title.trim()) {
    alert('请填写投诉标题')
    return
  }
  if (!form.content.trim()) {
    alert('请填写投诉内容')
    return
  }

  submitting.value = true
  try {
    await applyComplaint({
      type: form.type,
      title: form.title,
      content: form.content,
      contactPhone: form.contactPhone,
    })
    dialogVisible.value = false
    form.type = 0
    form.title = ''
    form.content = ''
    form.contactPhone = ''
    await loadData()
    alert('投诉提交成功！')
  } catch (error) {
    console.error('提交投诉失败:', error)
    alert('提交失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

/**
 * 取消投诉
 */
const handleCancel = async (item: OwnerComplaint) => {
  if (!confirm('确定要取消该投诉吗？')) return

  try {
    await cancelOwnerComplaint(item.id)
    await loadData()
    alert('已取消投诉')
  } catch (error) {
    console.error('取消投诉失败:', error)
    alert('取消失败，请稍后重试')
  }
}

/**
 * 搜索（重置页码并重新筛选）
 */
const handleSearch = () => {
  query.pageNum = 1
  applyPagination()
}

/**
 * 查看详情
 */
const handleViewDetail = (item: OwnerComplaint) => {
  router.push(`/owner/complaints/${item.id}`)
}

/**
 * 切换页码
 */
const changePage = (page: number) => {
  if (page < 1 || page > totalPages.value) return
  query.pageNum = page
  applyPagination()
}

/**
 * 重置搜索
 */
const handleReset = () => {
  query.status = undefined
  query.keyword = ''
  query.pageNum = 1
  applyPagination()
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
    4: 'status--info',
  }
  return map[status] || ''
}

// ============================================================
// 生命周期
// ============================================================

onMounted(async () => {
  await loadData()
})
</script>

<!-- template 和 style 保持不变，不需要修改 -->

<style scoped>
.owner-page__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.owner-page__title {
  margin: 0;
  font-size: 22px;
}

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

.owner-page__list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

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

.complaint-card {
  padding: 20px;
  border-radius: var(--radius-md);
  background: var(--color-card);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: box-shadow 0.2s;
}

.complaint-card:hover {
  box-shadow: var(--shadow-md);
}

.complaint-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.complaint-card__left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.complaint-card__title {
  font-weight: 600;
  font-size: 15px;
}

.complaint-card__status {
  display: inline-flex;
  padding: 2px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.complaint-card__type {
  font-size: 13px;
  color: var(--color-text-secondary);
  background: var(--color-bg);
  padding: 2px 10px;
  border-radius: 999px;
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

.complaint-card__body {
  margin-bottom: 12px;
}

.complaint-card__content {
  margin: 0 0 8px;
  font-size: 14px;
  line-height: 1.6;
  color: var(--color-text-secondary);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.complaint-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 13px;
  color: var(--color-text-secondary);
}

.complaint-card__reply {
  margin-top: 10px;
  padding: 10px 14px;
  border-radius: var(--radius-sm);
  background: #ecfdf5;
  border-left: 3px solid #059669;
  font-size: 14px;
}

.complaint-card__reply-label {
  font-weight: 600;
  color: #059669;
}

.complaint-card__footer {
  display: flex;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid var(--color-border);
}

.dialog--complaint {
  width: min(540px, calc(100% - 32px));
}

.form-required {
  color: #dc2626;
}

@media (max-width: 768px) {
  .owner-page__filters-row .form-input {
    width: 100%;
  }
  .complaint-card__header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  .complaint-card__meta {
    flex-direction: column;
    gap: 4px;
  }
}
</style>
