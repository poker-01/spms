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
          @keyup.enter="loadData"
        />
        <select v-model="query.status" class="form-input" @change="loadData">
          <option :value="undefined">全部状态</option>
          <option v-for="item in statuses" :key="item.value" :value="item.value">
            {{ item.label }}
          </option>
        </select>
        <button class="btn btn-primary" type="button" @click="loadData">查询</button>
        <button class="btn btn-ghost" type="button" @click="handleReset">重置</button>
      </div>
    </div>

    <!-- 列表 -->
    <div v-if="loading" class="owner-page__loading">加载中...</div>
    <div v-else-if="!list.length" class="owner-page__empty">
      <p>暂无投诉记录</p>
    </div>
    <div v-else class="owner-page__list">
      <article
        v-for="item in list"
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
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { formatDate } from '@/utils/format'

defineOptions({
  name: 'OwnerComplaints',
})

// ============================================================
// 类型定义
// ============================================================

interface ComplaintItem {
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

interface DictItem {
  value: number
  label: string
}

// ============================================================
// 状态
// ============================================================

const router = useRouter()
const loading = ref(false)
const list = ref<ComplaintItem[]>([])
const dialogVisible = ref(false)
const submitting = ref(false)

// 字典数据
const types = ref<DictItem[]>([])
const statuses = ref<DictItem[]>([])

// 查询参数
const query = reactive({
  status: undefined as number | undefined,
  keyword: '',
})

// 提交表单
const form = reactive({
  type: 0,
  title: '',
  content: '',
  contactPhone: '',
})

// ============================================================
// Mock 数据
// ============================================================

const mockList: ComplaintItem[] = [
  {
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
  },
  {
    id: 2,
    complaintNo: 'TS20260704002',
    type: 2,
    typeName: '设施维修',
    title: '小区路灯损坏',
    content: '小区中心花园的路灯坏了三天了，晚上散步很不方便，请尽快维修。',
    contactPhone: '13800138002',
    status: 1,
    statusName: '处理中',
    createTime: '2026-07-04 14:20:00',
  },
  {
    id: 3,
    complaintNo: 'TS20260703003',
    type: 3,
    typeName: '噪音扰民',
    title: '邻居装修噪音过大',
    content: '楼下邻居每天中午12-2点还在装修，严重影响休息，请协调处理。',
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
    type: 4,
    typeName: '其他',
    title: '快递柜经常故障',
    content: '小区快递柜最近经常显示故障，取不了快递，希望物业联系维修。',
    contactPhone: '13800138004',
    status: 3,
    statusName: '已取消',
    createTime: '2026-07-02 08:45:00',
  },
]

// ============================================================
// 方法
// ============================================================

/**
 * 加载字典数据
 * 接口：GET /api/v1/dict/complaint_type
 * 接口：GET /api/v1/dict/complaint_status
 */
const loadDict = async () => {
  try {
    // 接口：GET /api/v1/dict/complaint_type
    // const { data } = await getDict('complaint_type')
    // types.value = data

    // 接口：GET /api/v1/dict/complaint_status
    // const { data } = await getDict('complaint_status')
    // statuses.value = data

    // 临时Mock（后端接口完成后删除）
    types.value = [
      { value: 1, label: '物业服务' },
      { value: 2, label: '设施维修' },
      { value: 3, label: '噪音扰民' },
      { value: 4, label: '其他' },
    ]
    statuses.value = [
      { value: 0, label: '待处理' },
      { value: 1, label: '处理中' },
      { value: 2, label: '已回复' },
      { value: 3, label: '已取消' },
    ]
  } catch (error) {
    console.error('加载字典失败:', error)
  }
}

/**
 * 加载投诉列表
 * 接口：GET /api/v1/owner/complaints
 */
const loadData = async () => {
  loading.value = true
  try {
    // 接口：GET /api/v1/owner/complaints
    // 参数：{ status?, keyword? }
    // const { data } = await getOwnerComplaints(query)
    // list.value = data

    // 临时Mock（后端接口完成后删除）
    await new Promise((resolve) => setTimeout(resolve, 300))

    let data = [...mockList]
    if (query.status !== undefined) {
      data = data.filter((item) => item.status === query.status)
    }
    if (query.keyword) {
      const kw = query.keyword.toLowerCase()
      data = data.filter(
        (item) =>
          item.title.includes(kw) ||
          item.content.includes(kw) ||
          item.complaintNo.toLowerCase().includes(kw),
      )
    }
    list.value = data
  } catch (error) {
    console.error('加载投诉列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 提交投诉
 * 接口：POST /api/v1/owner/complaints
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
    // 接口：POST /api/v1/owner/complaints
    // 请求体：{ type, title, content, contactPhone }
    // await applyComplaint(form)

    // 临时Mock（后端接口完成后删除）
    await new Promise((resolve) => setTimeout(resolve, 800))

    const typeLabel = types.value.find((t) => t.value === form.type)?.label || '其他'
    const newItem: ComplaintItem = {
      id: Date.now(),
      complaintNo: `TS${new Date().toISOString().slice(0, 10).replace(/-/g, '')}${String(Math.floor(Math.random() * 1000)).padStart(3, '0')}`,
      type: form.type,
      typeName: typeLabel,
      title: form.title,
      content: form.content,
      contactPhone: form.contactPhone,
      status: 0,
      statusName: '待处理',
      createTime: new Date().toISOString().replace('T', ' ').slice(0, 19),
    }

    list.value = [newItem, ...list.value]
    dialogVisible.value = false
    form.type = 0
    form.title = ''
    form.content = ''
    form.contactPhone = ''

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
 * 接口：PUT /api/v1/owner/complaints/{id}/cancel
 */
const handleCancel = async (item: ComplaintItem) => {
  if (!confirm('确定要取消该投诉吗？')) return

  try {
    // 接口：PUT /api/v1/owner/complaints/{id}/cancel
    // await cancelComplaint(item.id)

    // 临时Mock（后端接口完成后删除）
    await new Promise((resolve) => setTimeout(resolve, 500))
    const target = list.value.find((r) => r.id === item.id)
    if (target) {
      target.status = 3
      target.statusName = '已取消'
    }
    alert('已取消投诉')
  } catch (error) {
    console.error('取消投诉失败:', error)
    alert('取消失败，请稍后重试')
  }
}

/**
 * 查看详情
 */
const handleViewDetail = (item: ComplaintItem) => {
  router.push(`/owner/complaints/${item.id}`)
}

/**
 * 重置搜索
 */
const handleReset = () => {
  query.status = undefined
  query.keyword = ''
  loadData()
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

onMounted(async () => {
  await loadDict()
  await loadData()
})
</script>

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
