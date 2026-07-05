<!-- src/views/admin/ComplaintManage.vue -->
<template>
  <div class="manage-page">
    <div class="manage-page__header">
      <h2 class="manage-page__title">投诉管理</h2>
    </div>

    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div class="stat-card" :class="{ 'stat-card--active': query.status === undefined }" @click="setStatusFilter(undefined)">
        <span class="stat-card__label">全部</span>
        <span class="stat-card__value">{{ statistics.total }}</span>
      </div>
      <div class="stat-card stat-card--warning" :class="{ 'stat-card--active': query.status === 0 }" @click="setStatusFilter(0)">
        <span class="stat-card__label">待处理</span>
        <span class="stat-card__value">{{ statistics.pending }}</span>
      </div>
      <div class="stat-card stat-card--primary" :class="{ 'stat-card--active': query.status === 1 }" @click="setStatusFilter(1)">
        <span class="stat-card__label">处理中</span>
        <span class="stat-card__value">{{ statistics.processing }}</span>
      </div>
      <div class="stat-card stat-card--success" :class="{ 'stat-card--active': query.status === 2 }" @click="setStatusFilter(2)">
        <span class="stat-card__label">已回复</span>
        <span class="stat-card__value">{{ statistics.replied }}</span>
      </div>
      <div class="stat-card stat-card--info" :class="{ 'stat-card--active': query.status === 3 }" @click="setStatusFilter(3)">
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
      <button class="btn btn-sm btn-ghost" :disabled="query.pageNum <= 1" @click="changePage(query.pageNum - 1)">
        上一页
      </button>
      <span class="pagination-info">第 {{ query.pageNum }} / {{ totalPages }} 页，共 {{ total }} 条</span>
      <button class="btn btn-sm btn-ghost" :disabled="query.pageNum >= totalPages" @click="changePage(query.pageNum + 1)">
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
          <button class="btn btn-primary" type="button" :disabled="replyDialog.submitting" @click="handleReply">
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

defineOptions({
  name: 'ComplaintManage',
})

interface ComplaintItem {
  id: number
  complaintNo: string
  ownerName?: string
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

interface DictItem {
  value: number
  label: string
}

const router = useRouter()
const loading = ref(false)
const list = ref<ComplaintItem[]>([])
const types = ref<DictItem[]>([])
const total = ref(0)
const totalPages = ref(1)

const statistics = reactive({
  total: 0,
  pending: 0,
  processing: 0,
  replied: 0,
  cancelled: 0,
})

const query = reactive({
  status: undefined as number | undefined,
  type: undefined as number | undefined,
  keyword: '',
  pageNum: 1,
  pageSize: 10,
})

const replyDialog = reactive({
  visible: false,
  item: null as ComplaintItem | null,
  content: '',
  submitting: false,
})

// Mock 数据
const mockList: ComplaintItem[] = [
  {
    id: 1,
    complaintNo: 'TS20260705001',
    ownerName: '张三',
    type: 1,
    typeName: '物业服务',
    title: '楼道卫生不干净',
    content: '最近一周楼道都没有人打扫，垃圾堆积，异味严重。',
    contactPhone: '13800138001',
    status: 0,
    statusName: '待处理',
    createTime: '2026-07-05 09:30:00',
  },
  {
    id: 2,
    complaintNo: 'TS20260704002',
    ownerName: '李四',
    type: 2,
    typeName: '设施维修',
    title: '小区路灯损坏',
    content: '小区中心花园的路灯坏了三天了，晚上散步很不方便。',
    contactPhone: '13800138002',
    status: 1,
    statusName: '处理中',
    createTime: '2026-07-04 14:20:00',
  },
  {
    id: 3,
    complaintNo: 'TS20260703003',
    ownerName: '王五',
    type: 3,
    typeName: '噪音扰民',
    title: '邻居装修噪音过大',
    content: '楼下邻居每天中午12-2点还在装修，严重影响休息。',
    contactPhone: '13800138003',
    status: 2,
    statusName: '已回复',
    createTime: '2026-07-03 10:15:00',
    replyContent: '已联系业主协调，装修时间已调整为工作日上午8-12点，下午2-6点。',
    replyTime: '2026-07-04 16:30:00',
  },
  {
    id: 4,
    complaintNo: 'TS20260702004',
    ownerName: '赵六',
    type: 4,
    typeName: '其他',
    title: '快递柜经常故障',
    content: '小区快递柜最近经常显示故障，取不了快递。',
    contactPhone: '13800138004',
    status: 3,
    statusName: '已取消',
    createTime: '2026-07-02 08:45:00',
  },
  {
    id: 5,
    complaintNo: 'TS20260701005',
    ownerName: '孙七',
    type: 1,
    typeName: '物业服务',
    title: '保安态度差',
    content: '今天早上出门，保安态度非常差，语气恶劣。',
    contactPhone: '13800138005',
    status: 0,
    statusName: '待处理',
    createTime: '2026-07-01 07:30:00',
  },
]

const loadDict = async () => {
  try {
    // 接口：GET /api/v1/dict/complaint_type
    types.value = [
      { value: 1, label: '物业服务' },
      { value: 2, label: '设施维修' },
      { value: 3, label: '噪音扰民' },
      { value: 4, label: '其他' },
    ]
  } catch (error) {
    console.error('加载字典失败:', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    // 接口：GET /api/v1/admin/complaints
    // const { data } = await getAdminComplaints(query)
    // list.value = data.records
    // total.value = data.total
    // totalPages.value = data.pages

    await new Promise((resolve) => setTimeout(resolve, 300))

    let data = [...mockList]
    if (query.status !== undefined) {
      data = data.filter((item) => item.status === query.status)
    }
    if (query.type !== undefined) {
      data = data.filter((item) => item.type === query.type)
    }
    if (query.keyword) {
      const kw = query.keyword.toLowerCase()
      data = data.filter(
        (item) =>
          item.title.includes(kw) ||
          item.content.includes(kw) ||
          (item.ownerName && item.ownerName.includes(kw)),
      )
    }

    total.value = data.length
    totalPages.value = Math.ceil(total.value / query.pageSize)
    const start = (query.pageNum - 1) * query.pageSize
    list.value = data.slice(start, start + query.pageSize)

    updateStatistics(mockList)
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

const updateStatistics = (data: ComplaintItem[]) => {
  statistics.total = data.length
  statistics.pending = data.filter((i) => i.status === 0).length
  statistics.processing = data.filter((i) => i.status === 1).length
  statistics.replied = data.filter((i) => i.status === 2).length
  statistics.cancelled = data.filter((i) => i.status === 3).length
}

const setStatusFilter = (status: number | undefined) => {
  query.status = status
  query.pageNum = 1
  loadData()
}

const changePage = (page: number) => {
  if (page < 1 || page > totalPages.value) return
  query.pageNum = page
  loadData()
}

const handleReset = () => {
  query.status = undefined
  query.type = undefined
  query.keyword = ''
  query.pageNum = 1
  loadData()
}

const handleViewDetail = (row: ComplaintItem) => {
  router.push(`/admin/complaints/${row.id}`)
}

const openReplyDialog = (row: ComplaintItem) => {
  replyDialog.item = row
  replyDialog.content = ''
  replyDialog.visible = true
}

const handleReply = async () => {
  if (!replyDialog.item) return
  if (!replyDialog.content.trim()) {
    alert('请填写回复内容')
    return
  }

  replyDialog.submitting = true
  try {
    // 接口：PUT /api/v1/admin/complaints/{id}/reply
    // 请求体：{ replyContent }
    // await replyComplaint(replyDialog.item.id, {
    //   replyContent: replyDialog.content,
    // })

    await new Promise((resolve) => setTimeout(resolve, 800))
    const target = mockList.find((r) => r.id === replyDialog.item!.id)
    if (target) {
      target.status = 2
      target.statusName = '已回复'
      target.replyContent = replyDialog.content
      target.replyTime = new Date().toISOString().replace('T', ' ').slice(0, 19)
    }
    replyDialog.visible = false
    loadData()
    alert('回复成功！')
  } catch (error) {
    alert('回复失败')
  } finally {
    replyDialog.submitting = false
  }
}

const getStatusClass = (status: number): string => {
  const map: Record<number, string> = {
    0: 'status-badge--warning',
    1: 'status-badge--primary',
    2: 'status-badge--success',
    3: 'status-badge--info',
  }
  return map[status] || ''
}

onMounted(async () => {
  await loadDict()
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
