<!-- src/views/admin/ComplaintManage.vue -->
<template>
  <div class="manage-page">
    <div class="manage-page__header">
      <h2 class="manage-page__title">投诉管理</h2>
    </div>

    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div
        class="stat-card"
        :class="{ 'stat-card--active': query.status === undefined }"
        @click="setStatusFilter(undefined)"
      >
        <span class="stat-card__label">全部</span>
        <span class="stat-card__value">{{ statistics.total }}</span>
      </div>
      <div
        class="stat-card stat-card--warning"
        :class="{ 'stat-card--active': query.status === 0 }"
        @click="setStatusFilter(0)"
      >
        <span class="stat-card__label">待处理</span>
        <span class="stat-card__value">{{ statistics.pending }}</span>
      </div>
      <div
        class="stat-card stat-card--primary"
        :class="{ 'stat-card--active': query.status === 1 }"
        @click="setStatusFilter(1)"
      >
        <span class="stat-card__label">处理中</span>
        <span class="stat-card__value">{{ statistics.processing }}</span>
      </div>
      <div
        class="stat-card stat-card--success"
        :class="{ 'stat-card--active': query.status === 2 }"
        @click="setStatusFilter(2)"
      >
        <span class="stat-card__label">已回复</span>
        <span class="stat-card__value">{{ statistics.replied }}</span>
      </div>
      <div
        class="stat-card stat-card--info"
        :class="{ 'stat-card--active': query.status === 3 }"
        @click="setStatusFilter(3)"
      >
        <span class="stat-card__label">已取消</span>
        <span class="stat-card__value">{{ statistics.cancelled }}</span>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="manage-page__toolbar">
      <div class="manage-page__filters">
        <input
          v-model="query.keyword"
          class="form-input"
          placeholder="搜索标题/内容"
          @keyup.enter="loadData"
        />
        <select v-model="query.type" class="form-input" @change="loadData">
          <option :value="undefined">全部类型</option>
          <option v-for="item in types" :key="item.value" :value="item.value">
            {{ item.label }}
          </option>
        </select>
        <button class="btn btn-primary" type="button" @click="loadData">查询</button>
        <button class="btn btn-ghost" type="button" @click="handleReset">重置</button>
      </div>
    </div>

    <!-- 表格 -->
    <div class="manage-page__table-wrap">
      <table class="manage-page__table">
        <thead>
        <tr>
          <th>编号</th>
          <th>业主</th>
          <th>类型</th>
          <th>标题</th>
          <th>状态</th>
          <th>提交时间</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="row in list" :key="row.id">
          <td>
            <span class="table-link" @click="handleViewDetail(row)">{{ row.complaintNo }}</span>
          </td>
          <td>{{ row.ownerName || '-' }}</td>
          <td>{{ row.typeName }}</td>
          <td class="table-desc">{{ row.title }}</td>
          <td>
              <span :class="['status-badge', getStatusClass(row.status)]">
                {{ row.statusName }}
              </span>
          </td>
          <td>{{ formatDate(row.createTime) }}</td>
          <td>
            <button
              v-if="row.status === 0 || row.status === 1"
              class="btn btn-sm btn-primary"
              type="button"
              @click="openReplyDialog(row)"
            >
              回复
            </button>
            <button
              v-if="row.status === 2"
              class="btn btn-sm btn-success"
              type="button"
              @click="handleClose(row)"
            >
              关闭
            </button>
            <button class="btn btn-sm btn-ghost" type="button" @click="handleViewDetail(row)">
              详情
            </button>
          </td>
        </tr>
        <tr v-if="!loading && !list.length">
          <td colspan="7" class="manage-page__empty">暂无投诉记录</td>
        </tr>
        </tbody>
      </table>
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="manage-page__pagination">
      <button
        class="btn btn-sm btn-ghost"
        :disabled="getCurrentPage() <= 1"
        @click="changePage(getCurrentPage() - 1)"
      >
        上一页
      </button>
      <span class="pagination-info">
        第 {{ getCurrentPage() }} / {{ totalPages }} 页，共 {{ total }} 条
      </span>
      <button
        class="btn btn-sm btn-ghost"
        :disabled="getCurrentPage() >= totalPages"
        @click="changePage(getCurrentPage() + 1)"
      >
        下一页
      </button>
    </div>

    <!-- 回复弹窗 -->
    <div v-if="replyDialog.visible" class="dialog-overlay" @click.self="replyDialog.visible = false">
      <div class="dialog dialog--reply">
        <h3 class="dialog__title">回复投诉</h3>
        <div class="dialog__body">
          <p class="dialog__info">
            投诉编号：<strong>{{ replyDialog.item?.complaintNo }}</strong>
          </p>
          <p class="dialog__info">
            投诉内容：{{ replyDialog.item?.content }}
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
          <button class="btn btn-ghost" type="button" @click="replyDialog.visible = false">取消</button>
          <button
            class="btn btn-primary"
            type="button"
            :disabled="replyDialog.submitting"
            @click="handleReply"
          >
            {{ replyDialog.submitting ? '提交中...' : '确认回复' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { formatDate } from '@/utils/format'
import { getComplaintPage, replyComplaint, closeComplaint } from '@/api/complaint'
import type { ComplaintVO, ComplaintQuery } from '@/api/complaint'

defineOptions({
  name: 'ComplaintManage',
})

// ============================================================
// 状态
// ============================================================

const router = useRouter()
const loading = ref(false)
const list = ref<ComplaintVO[]>([])

// 投诉类型（前端写死）
const types = [
  { value: 1, label: '物业服务' },
  { value: 2, label: '设施维修' },
  { value: 3, label: '噪音扰民' },
  { value: 4, label: '其他' },
]

const total = ref(0)
const totalPages = ref(1)

const statistics = reactive({
  total: 0,
  pending: 0,
  processing: 0,
  replied: 0,
  cancelled: 0,
})

const query = reactive<ComplaintQuery>({
  status: undefined,
  type: undefined,
  keyword: '',
  pageNum: 1,
  pageSize: 10,
})

const replyDialog = reactive({
  visible: false,
  item: null as ComplaintVO | null,
  content: '',
  submitting: false,
})

// ============================================================
// 辅助方法
// ============================================================

/** 获取当前页码（带默认值） */
const getCurrentPage = (): number => {
  return query.pageNum ?? 1
}

/** 获取每页大小（带默认值） */
const getPageSize = (): number => {
  return query.pageSize ?? 10
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
    const { data } = await getComplaintPage({
      status: query.status,
      type: query.type,
      keyword: query.keyword,
      pageNum: getCurrentPage(),
      pageSize: getPageSize(),
    })
    list.value = data.records
    total.value = data.total
    totalPages.value = data.pages
    updateStatistics(data.records)
  } catch (error) {
    console.error('加载投诉列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 更新统计
 */
const updateStatistics = (data: ComplaintVO[]) => {
  statistics.total = data.length
  statistics.pending = data.filter((i) => i.status === 0).length
  statistics.processing = data.filter((i) => i.status === 1).length
  statistics.replied = data.filter((i) => i.status === 2).length
  statistics.cancelled = data.filter((i) => i.status === 3).length
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
  query.type = undefined
  query.keyword = ''
  query.pageNum = 1
  loadData()
}

/**
 * 查看详情
 */
const handleViewDetail = (row: ComplaintVO) => {
  router.push(`/admin/complaints/${row.id}`)
}

/**
 * 打开回复弹窗
 */
const openReplyDialog = (row: ComplaintVO) => {
  replyDialog.item = row
  replyDialog.content = ''
  replyDialog.visible = true
}

/**
 * 回复投诉
 */
const handleReply = async () => {
  if (!replyDialog.item) return
  if (!replyDialog.content.trim()) {
    alert('请填写回复内容')
    return
  }

  replyDialog.submitting = true
  try {
    await replyComplaint({
      complaintId: replyDialog.item.id,
      replyContent: replyDialog.content,
    })
    replyDialog.visible = false
    await loadData()
    alert('回复成功！')
  } catch (error) {
    console.error('回复失败:', error)
    alert('回复失败，请稍后重试')
  } finally {
    replyDialog.submitting = false
  }
}

/**
 * 关闭投诉
 */
const handleClose = async (row: ComplaintVO) => {
  if (!confirm('确认关闭该投诉吗？关闭后不可恢复。')) return
  try {
    await closeComplaint(row.id)
    await loadData()
    alert('投诉已关闭！')
  } catch (error) {
    console.error('关闭投诉失败:', error)
    alert('操作失败，请稍后重试')
  }
}

/**
 * 获取状态样式
 */
const getStatusClass = (status: number): string => {
  const map: Record<number, string> = {
    0: 'status-badge--warning',
    1: 'status-badge--primary',
    2: 'status-badge--success',
    3: 'status-badge--info',
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

<style scoped>
.manage-page {
  max-width: 1200px;
  margin: 0 auto;
}
.manage-page__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}
.manage-page__title {
  margin: 0;
  font-size: 22px;
}

.stat-cards {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
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
.stat-card--warning .stat-card__value {
  color: #d97706;
}
.stat-card--primary .stat-card__value {
  color: #2563eb;
}
.stat-card--success .stat-card__value {
  color: #059669;
}
.stat-card--info .stat-card__value {
  color: #64748b;
}

.manage-page__toolbar {
  margin-bottom: 16px;
}
.manage-page__filters {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.manage-page__filters .form-input {
  width: 200px;
}

.manage-page__table-wrap {
  overflow-x: auto;
  border-radius: var(--radius-md);
  background: var(--color-card);
  border: 1px solid var(--color-border);
}
.manage-page__table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}
.manage-page__table th,
.manage-page__table td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid var(--color-border);
}
.manage-page__table th {
  background: #f8fafc;
  font-weight: 600;
  color: var(--color-text-secondary);
}
.manage-page__table tr:last-child td {
  border-bottom: none;
}
.manage-page__empty {
  padding: 40px;
  text-align: center;
  color: var(--color-text-secondary);
}

.table-link {
  color: var(--color-primary);
  cursor: pointer;
  font-weight: 500;
}
.table-link:hover {
  text-decoration: underline;
}
.table-desc {
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-badge {
  display: inline-flex;
  padding: 2px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}
.status-badge--warning {
  background: #fef3c7;
  color: #d97706;
}
.status-badge--primary {
  background: #dbeafe;
  color: #2563eb;
}
.status-badge--success {
  background: #d1fae5;
  color: #059669;
}
.status-badge--info {
  background: #f1f5f9;
  color: #64748b;
}

.manage-page__pagination {
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

.dialog--reply {
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

@media (max-width: 1024px) {
  .stat-cards {
    grid-template-columns: repeat(3, 1fr);
  }
}
@media (max-width: 768px) {
  .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  .manage-page__filters .form-input {
    width: 100%;
  }
  .manage-page__filters {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
