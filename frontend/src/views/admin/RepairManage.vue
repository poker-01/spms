<template>
  <div class="manage-page">
    <div class="manage-page__header">
      <h2 class="manage-page__title">报修管理</h2>
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
        <span class="stat-card__label">已完成</span>
        <span class="stat-card__value">{{ statistics.completed }}</span>
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
            <!-- 待处理 → 派单 -->
            <button
              v-if="row.status === 0"
              class="btn btn-sm btn-primary"
              type="button"
              @click="openAssignDialog(row)"
            >
              派单
            </button>
            <!-- 处理中 → 完成 -->
            <button
              v-if="row.status === 1"
              class="btn btn-sm btn-success"
              type="button"
              @click="openCompleteDialog(row)"
            >
              完成
            </button>
            <!-- 查看详情 -->
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

    <!-- 派单弹窗 -->
    <div v-if="assignDialog.visible" class="dialog-overlay" @click.self="assignDialog.visible = false">
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
          <button class="btn btn-primary" type="button" :disabled="assignDialog.submitting" @click="handleAssign">
            {{ assignDialog.submitting ? '提交中...' : '确认派单' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 完成报修弹窗 -->
    <div v-if="completeDialog.visible" class="dialog-overlay" @click.self="completeDialog.visible = false">
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
          <button class="btn btn-success" type="button" :disabled="completeDialog.submitting" @click="handleComplete">
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

defineOptions({
  name: 'RepairManage',
})

// ============================================================
// 类型定义
// ============================================================

interface RepairItem {
  id: number
  orderNo: string
  ownerName?: string
  repairType: number
  repairTypeName: string
  repairDesc: string
  repairPhone: string
  status: number
  statusName: string
  repairCost?: number
  createTime: string
  repairTime?: string
}

interface DictItem {
  value: number
  label: string
}

interface Repairer {
  id: number
  userName: string
  fullName?: string
}

// ============================================================
// 状态
// ============================================================

const router = useRouter()
const loading = ref(false)
const repairList = ref<RepairItem[]>([])
const repairTypes = ref<DictItem[]>([])
const repairers = ref<Repairer[]>([])
const total = ref(0)
const totalPages = ref(1)

// 统计
const statistics = reactive({
  total: 0,
  pending: 0,
  processing: 0,
  completed: 0,
  cancelled: 0,
})

// 查询参数
const query = reactive({
  status: undefined as number | undefined,
  repairType: undefined as number | undefined,
  keyword: '',
  pageNum: 1,
  pageSize: 10,
})

// 派单弹窗
const assignDialog = reactive({
  visible: false,
  repair: null as RepairItem | null,
  repairerId: 0,
  submitting: false,
})

// 完成弹窗
const completeDialog = reactive({
  visible: false,
  repair: null as RepairItem | null,
  repairCost: 0,
  submitting: false,
})

// ============================================================
// Mock 数据
// ============================================================

const mockRepairList: RepairItem[] = [
  {
    id: 1,
    orderNo: 'BX20260705001',
    ownerName: '张三',
    repairType: 1,
    repairTypeName: '水电维修',
    repairDesc: '厨房水龙头漏水，需要更换',
    repairPhone: '13800138001',
    status: 0,
    statusName: '待处理',
    createTime: '2026-07-05 09:30:00',
  },
  {
    id: 2,
    orderNo: 'BX20260704002',
    ownerName: '李四',
    repairType: 3,
    repairTypeName: '家电维修',
    repairDesc: '空调不制冷，可能是缺氟',
    repairPhone: '13800138002',
    status: 1,
    statusName: '处理中',
    createTime: '2026-07-04 14:20:00',
    repairTime: '2026-07-05 08:00:00',
  },
  {
    id: 3,
    orderNo: 'BX20260703003',
    ownerName: '王五',
    repairType: 2,
    repairTypeName: '家具维修',
    repairDesc: '卧室衣柜门铰链松动',
    repairPhone: '13800138003',
    status: 2,
    statusName: '已完成',
    createTime: '2026-07-03 10:15:00',
    repairTime: '2026-07-04 16:30:00',
    repairCost: 80,
  },
  {
    id: 4,
    orderNo: 'BX20260702004',
    ownerName: '赵六',
    repairType: 4,
    repairTypeName: '其他',
    repairDesc: '客厅吊灯闪烁，需要检查电路',
    repairPhone: '13800138004',
    status: 3,
    statusName: '已取消',
    createTime: '2026-07-02 08:45:00',
  },
  {
    id: 5,
    orderNo: 'BX20260701005',
    ownerName: '孙七',
    repairType: 1,
    repairTypeName: '水电维修',
    repairDesc: '卫生间马桶堵塞，需要疏通',
    repairPhone: '13800138005',
    status: 0,
    statusName: '待处理',
    createTime: '2026-07-01 16:00:00',
  },
]

// ============================================================
// 方法
// ============================================================

/**
 * 加载字典数据
 * 接口：GET /api/v1/dict/repair_type
 */
const loadDict = async () => {
  try {
    // 接口：GET /api/v1/dict/repair_type
    // const { data } = await getDict('repair_type')
    // repairTypes.value = data

    // 临时Mock（后端接口完成后删除）
    repairTypes.value = [
      { value: 1, label: '水电维修' },
      { value: 2, label: '家具维修' },
      { value: 3, label: '家电维修' },
      { value: 4, label: '其他' },
    ]
  } catch (error) {
    console.error('加载字典失败:', error)
  }
}

/**
 * 加载维修人员列表
 * 接口：GET /api/v1/admin/repairers
 */
const loadRepairers = async () => {
  try {
    // 接口：GET /api/v1/admin/repairers
    // const { data } = await getRepairers()
    // repairers.value = data

    // 临时Mock（后端接口完成后删除）
    repairers.value = [
      { id: 1, userName: 'repairer1', fullName: '张师傅' },
      { id: 2, userName: 'repairer2', fullName: '李师傅' },
      { id: 3, userName: 'repairer3', fullName: '王师傅' },
    ]
  } catch (error) {
    console.error('加载维修人员失败:', error)
  }
}

/**
 * 加载报修列表
 * 接口：GET /api/v1/admin/repairs
 */
const loadData = async () => {
  loading.value = true
  try {
    // 接口：GET /api/v1/admin/repairs
    // 参数：{ status?, repairType?, keyword?, pageNum?, pageSize? }
    // const { data } = await getAdminRepairs(query)
    // repairList.value = data.records
    // total.value = data.total
    // totalPages.value = data.pages

    // 临时Mock（后端接口完成后删除）
    await new Promise((resolve) => setTimeout(resolve, 300))

    let data = [...mockRepairList]

    if (query.status !== undefined) {
      data = data.filter((item) => item.status === query.status)
    }
    if (query.repairType !== undefined) {
      data = data.filter((item) => item.repairType === query.repairType)
    }
    if (query.keyword) {
      const kw = query.keyword.toLowerCase()
      data = data.filter(
        (item) =>
          item.orderNo.toLowerCase().includes(kw) ||
          item.repairDesc.includes(kw) ||
          (item.ownerName && item.ownerName.includes(kw)),
      )
    }

    total.value = data.length
    totalPages.value = Math.ceil(total.value / query.pageSize)

    const start = (query.pageNum - 1) * query.pageSize
    const end = start + query.pageSize
    repairList.value = data.slice(start, end)

    // 更新统计
    updateStatistics(mockRepairList)
  } catch (error) {
    console.error('加载报修列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 更新统计
 */
const updateStatistics = (data: RepairItem[]) => {
  statistics.total = data.length
  statistics.pending = data.filter((i) => i.status === 0).length
  statistics.processing = data.filter((i) => i.status === 1).length
  statistics.completed = data.filter((i) => i.status === 2).length
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
  query.repairType = undefined
  query.keyword = ''
  query.pageNum = 1
  loadData()
}

/**
 * 查看详情
 */
const handleViewDetail = (row: RepairItem) => {
  router.push(`/admin/repairs/${row.id}`)
}

/**
 * 打开派单弹窗
 */
const openAssignDialog = (row: RepairItem) => {
  assignDialog.repair = row
  assignDialog.repairerId = 0
  assignDialog.visible = true
}

/**
 * 派单
 * 接口：PUT /api/v1/admin/repairs/{id}/assign
 */
const handleAssign = async () => {
  if (!assignDialog.repair) return
  if (!assignDialog.repairerId) {
    alert('请选择维修人员')
    return
  }

  assignDialog.submitting = true
  try {
    // 接口：PUT /api/v1/admin/repairs/{id}/assign
    // 请求体：{ repairerId }
    // await assignRepair(assignDialog.repair.id, {
    //   repairerId: assignDialog.repairerId,
    // })

    // 临时Mock（后端接口完成后删除）
    await new Promise((resolve) => setTimeout(resolve, 800))
    const target = mockRepairList.find((r) => r.id === assignDialog.repair!.id)
    if (target) {
      target.status = 1
      target.statusName = '处理中'
    }
    assignDialog.visible = false
    loadData()
    alert('派单成功！')
  } catch (error) {
    console.error('派单失败:', error)
    alert('派单失败，请稍后重试')
  } finally {
    assignDialog.submitting = false
  }
}

/**
 * 打开完成弹窗
 */
const openCompleteDialog = (row: RepairItem) => {
  completeDialog.repair = row
  completeDialog.repairCost = 0
  completeDialog.visible = true
}

/**
 * 完成报修
 * 接口：PUT /api/v1/admin/repairs/{id}/complete
 */
const handleComplete = async () => {
  if (!completeDialog.repair) return
  if (!completeDialog.repairCost || completeDialog.repairCost < 0) {
    alert('请输入有效的维修费用')
    return
  }

  completeDialog.submitting = true
  try {
    // 接口：PUT /api/v1/admin/repairs/{id}/complete
    // 请求体：{ repairCost }
    // await completeRepair(completeDialog.repair.id, {
    //   repairCost: completeDialog.repairCost,
    // })

    // 临时Mock（后端接口完成后删除）
    await new Promise((resolve) => setTimeout(resolve, 800))
    const target = mockRepairList.find((r) => r.id === completeDialog.repair!.id)
    if (target) {
      target.status = 2
      target.statusName = '已完成'
      target.repairCost = completeDialog.repairCost
    }
    completeDialog.visible = false
    loadData()
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
    2: 'status-badge--success',
    3: 'status-badge--info',
  }
  return map[status] || ''
}

// ============================================================
// 生命周期
// ============================================================

onMounted(async () => {
  await loadDict()
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
