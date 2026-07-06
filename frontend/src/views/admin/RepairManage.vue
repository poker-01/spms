<template>
  <div class="manage-page">
    <div class="manage-page__header">
      <h2 class="manage-page__title">报修管理</h2>
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
        <span class="stat-card__label">待派单</span>
        <span class="stat-card__value">{{ statistics.pending }}</span>
      </div>
      <div
        class="stat-card stat-card--primary"
        :class="{ 'stat-card--active': query.status === 2 }"
        @click="setStatusFilter(2)"
      >
        <span class="stat-card__label">处理中</span>
        <span class="stat-card__value">{{ statistics.processing }}</span>
      </div>
      <div
        class="stat-card stat-card--success"
        :class="{ 'stat-card--active': query.status === 3 }"
        @click="setStatusFilter(3)"
      >
        <span class="stat-card__label">已完成</span>
        <span class="stat-card__value">{{ statistics.completed }}</span>
      </div>
      <div
        class="stat-card stat-card--info"
        :class="{ 'stat-card--active': query.status === 4 }"
        @click="setStatusFilter(4)"
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
          placeholder="搜索订单号/描述"
          @keyup.enter="loadData"
        />
        <select v-model="query.repairType" class="form-input" @change="loadData">
          <option :value="undefined">全部类型</option>
          <option v-for="item in repairTypes" :key="item.value" :value="item.value">
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
          <th>订单号</th>
          <th>业主</th>
          <th>类型</th>
          <th>描述</th>
          <th>状态</th>
          <th>提交时间</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="row in repairList" :key="row.id">
          <td>
            <span class="table-link" @click="handleViewDetail(row)">{{ row.orderNo }}</span>
          </td>
          <td>{{ row.ownerName || '-' }}</td>
          <td>{{ row.repairTypeName }}</td>
          <td class="table-desc">{{ row.repairDesc }}</td>
          <td>
              <span :class="['status-badge', getStatusClass(row.status)]">
                {{ row.statusName }}
              </span>
          </td>
          <td>{{ formatDate(row.createTime) }}</td>
          <td>
            <button
              v-if="row.status === 0"
              class="btn btn-sm btn-primary"
              type="button"
              @click="openAssignDialog(row)"
            >
              派单
            </button>
            <button
              v-if="row.status === 1"
              class="btn btn-sm btn-primary"
              type="button"
              @click="handleStart(row)"
            >
              开始处理
            </button>
            <button
              v-if="row.status === 2"
              class="btn btn-sm btn-success"
              type="button"
              @click="openCompleteDialog(row)"
            >
              完成
            </button>
            <button
              class="btn btn-sm btn-ghost"
              type="button"
              @click="handleViewDetail(row)"
            >
              详情
            </button>
          </td>
        </tr>
        <tr v-if="!loading && !repairList.length">
          <td colspan="7" class="manage-page__empty">暂无报修记录</td>
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

    <!-- 派单弹窗 -->
    <div
      v-if="assignDialog.visible"
      class="dialog-overlay"
      @click.self="assignDialog.visible = false"
    >
      <div class="dialog dialog--assign">
        <h3 class="dialog__title">派单</h3>
        <div class="dialog__body">
          <p class="dialog__info">
            报修单：<strong>{{ assignDialog.repair?.orderNo }}</strong>
          </p>
          <p class="dialog__info">
            描述：{{ assignDialog.repair?.repairDesc }}
          </p>
          <div class="form-field">
            <label class="form-label">选择维修人员 <span class="form-required">*</span></label>
            <select v-model="assignDialog.repairerId" class="form-input">
              <option :value="0">请选择维修人员</option>
              <option v-for="user in repairers" :key="user.id" :value="user.id">
                {{ user.fullName || user.userName }}
              </option>
            </select>
          </div>
        </div>
        <div class="dialog__footer">
          <button class="btn btn-ghost" type="button" @click="assignDialog.visible = false">
            取消
          </button>
          <button
            class="btn btn-primary"
            type="button"
            :disabled="assignDialog.submitting"
            @click="handleAssign"
          >
            {{ assignDialog.submitting ? '提交中...' : '确认派单' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 完成报修弹窗 -->
    <div
      v-if="completeDialog.visible"
      class="dialog-overlay"
      @click.self="completeDialog.visible = false"
    >
      <div class="dialog dialog--complete">
        <h3 class="dialog__title">完成报修</h3>
        <div class="dialog__body">
          <p class="dialog__info">
            报修单：<strong>{{ completeDialog.repair?.orderNo }}</strong>
          </p>
          <div class="form-field">
            <label class="form-label">维修费用（元） <span class="form-required">*</span></label>
            <input
              v-model.number="completeDialog.repairCost"
              class="form-input"
              type="number"
              placeholder="请输入维修费用"
              min="0"
              step="0.01"
            />
          </div>
        </div>
        <div class="dialog__footer">
          <button class="btn btn-ghost" type="button" @click="completeDialog.visible = false">
            取消
          </button>
          <button
            class="btn btn-success"
            type="button"
            :disabled="completeDialog.submitting"
            @click="handleComplete"
          >
            {{ completeDialog.submitting ? '提交中...' : '确认完成' }}
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
import { getRepairPage, assignRepair, completeRepair, startRepair, getUsersByRole } from '@/api/repair'
import type { RepairVO, RepairQuery, RepairerInfo } from '@/api/repair'

defineOptions({
  name: 'RepairManage',
})

// ============================================================
// 状态
// ============================================================

const router = useRouter()
const loading = ref(false)
const repairList = ref<RepairVO[]>([])

// 报修类型（与数据库中存储的中文标签一致，用于筛选）
const repairTypes = [
  { value: '水电维修', label: '水电维修' },
  { value: '家具维修', label: '家具维修' },
  { value: '家电维修', label: '家电维修' },
  { value: '其他', label: '其他' },
]

const repairers = ref<RepairerInfo[]>([])
const total = ref(0)
const totalPages = ref(1)

const statistics = reactive({
  total: 0,
  pending: 0,
  processing: 0,
  completed: 0,
  cancelled: 0,
})

const query = reactive<RepairQuery>({
  status: undefined,
  repairType: undefined,
  keyword: '',
  page: 1,
  size: 10,
})

const assignDialog = reactive({
  visible: false,
  repair: null as RepairVO | null,
  repairerId: 0,
  submitting: false,
})

const completeDialog = reactive({
  visible: false,
  repair: null as RepairVO | null,
  repairCost: 0,
  submitting: false,
})

// ============================================================
// 辅助方法
// ============================================================

/** 获取当前页码（带默认值） */
const getCurrentPage = (): number => {
  return query.page ?? 1
}

/** 获取每页大小（带默认值） */
const getPageSize = (): number => {
  return query.size ?? 10
}

// ============================================================
// 方法
// ============================================================

/**
 * 加载维修人员列表
 */
const loadRepairers = async () => {
  try {
    const { data } = await getUsersByRole(4)
    repairers.value = data
  } catch (error) {
    console.error('加载维修人员失败:', error)
    repairers.value = []
  }
}

/**
 * 加载报修列表
 */
const loadData = async () => {
  loading.value = true
  try {
    const { data } = await getRepairPage({
      status: query.status,
      repairType: query.repairType,
      keyword: query.keyword,
      page: getCurrentPage(),
      size: getPageSize(),
    })
    repairList.value = data.records
    total.value = data.total
    totalPages.value = data.pages
    updateStatistics(data.records)
  } catch (error) {
    console.error('加载报修列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 更新统计
 */
const updateStatistics = (data: RepairVO[]) => {
  statistics.total = data.length
  statistics.pending = data.filter((i) => i.status === 0).length
  statistics.processing = data.filter((i) => i.status === 2).length
  statistics.completed = data.filter((i) => i.status === 3).length
  statistics.cancelled = data.filter((i) => i.status === 4).length
}

/**
 * 设置状态筛选
 */
const setStatusFilter = (status: number | undefined) => {
  query.status = status
  query.page = 1
  loadData()
}

/**
 * 切换页码
 */
const changePage = (page: number) => {
  if (page < 1 || page > totalPages.value) return
  query.page = page
  loadData()
}

/**
 * 重置搜索
 */
const handleReset = () => {
  query.status = undefined
  query.repairType = undefined
  query.keyword = ''
  query.page = 1
  loadData()
}

/**
 * 查看详情
 */
const handleViewDetail = (row: RepairVO) => {
  router.push(`/admin/repairs/${row.id}`)
}

/**
 * 打开派单弹窗
 */
const openAssignDialog = (row: RepairVO) => {
  assignDialog.repair = row
  assignDialog.repairerId = 0
  assignDialog.visible = true
}

/**
 * 派单
 */
const handleAssign = async () => {
  if (!assignDialog.repair) return
  if (!assignDialog.repairerId) {
    alert('请选择维修人员')
    return
  }

  assignDialog.submitting = true
  try {
    await assignRepair({
      orderId: assignDialog.repair.id,
      assigneeId: assignDialog.repairerId,
    })
    assignDialog.visible = false
    await loadData()
    alert('派单成功！')
  } catch (error) {
    console.error('派单失败:', error)
    alert('派单失败，请稍后重试')
  } finally {
    assignDialog.submitting = false
  }
}

/**
 * 开始处理
 */
const handleStart = async (row: RepairVO) => {
  try {
    await startRepair(row.id)
    await loadData()
    alert('已开始处理！')
  } catch (error) {
    console.error('开始处理失败:', error)
    alert('操作失败，请稍后重试')
  }
}

/**
 * 打开完成弹窗
 */
const openCompleteDialog = (row: RepairVO) => {
  completeDialog.repair = row
  completeDialog.repairCost = 0
  completeDialog.visible = true
}

/**
 * 完成报修
 */
const handleComplete = async () => {
  if (!completeDialog.repair) return
  if (!completeDialog.repairCost || completeDialog.repairCost < 0) {
    alert('请输入有效的维修费用')
    return
  }

  completeDialog.submitting = true
  try {
    await completeRepair({
      orderId: completeDialog.repair.id,
      repairCost: completeDialog.repairCost,

    })
    completeDialog.visible = false
    await loadData()
    alert('报修已完成！')
  } catch (error) {
    console.error('完成报修失败:', error)
    alert('操作失败，请稍后重试')
  } finally {
    completeDialog.submitting = false
  }
}

/**
 * 获取状态样式
 */
const getStatusClass = (status: number): string => {
  const map: Record<number, string> = {
    0: 'status-badge--warning',
    1: 'status-badge--primary',
    2: 'status-badge--primary',
    3: 'status-badge--success',
    4: 'status-badge--info',
    5: 'status-badge--info',
  }
  return map[status] || ''
}

// ============================================================
// 生命周期
// ============================================================

onMounted(async () => {
  await loadRepairers()
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

/* 统计卡片 */
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

/* 搜索栏 */
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

/* 表格 */
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

.manage-page__table td > .btn {
  margin-right: 4px;
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
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 状态标签 */
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

/* 分页 */
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

/* 弹窗 */
.dialog--assign,
.dialog--complete {
  width: min(460px, calc(100% - 32px));
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

/* 响应式 */
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

  .manage-page__table-wrap {
    overflow-x: auto;
  }

  .manage-page__table {
    font-size: 13px;
  }

  .manage-page__table th,
  .manage-page__table td {
    padding: 8px 10px;
  }
}

@media (max-width: 480px) {
  .stat-cards {
    grid-template-columns: 1fr 1fr;
    gap: 8px;
  }

  .stat-card__value {
    font-size: 20px;
  }
}
</style>
