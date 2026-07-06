<!-- src/views/admin/BillManage.vue -->
<template>
  <div class="manage-page">
    <div class="manage-page__header">
      <h2 class="manage-page__title">账单管理</h2>
      <button class="btn btn-primary" type="button" @click="openGenerateDialog">
        + 生成账单
      </button>
    </div>

    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div class="stat-card" :class="{ 'stat-card--active': query.status === undefined }" @click="setStatusFilter(undefined)">
        <span class="stat-card__label">全部</span>
        <span class="stat-card__value">{{ statistics.total }}</span>
      </div>
      <div class="stat-card stat-card--danger" :class="{ 'stat-card--active': query.status === 0 }" @click="setStatusFilter(0)">
        <span class="stat-card__label">待缴费</span>
        <span class="stat-card__value">{{ statistics.unpaid }}</span>
      </div>
      <div class="stat-card stat-card--success" :class="{ 'stat-card--active': query.status === 2 }" @click="setStatusFilter(2)">
        <span class="stat-card__label">已缴费</span>
        <span class="stat-card__value">{{ statistics.paid }}</span>
      </div>
      <div class="stat-card stat-card--warning" :class="{ 'stat-card--active': query.status === 3 }" @click="setStatusFilter(3)">
        <span class="stat-card__label">已逾期</span>
        <span class="stat-card__value">{{ statistics.overdue }}</span>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="manage-page__toolbar">
      <div class="manage-page__filters">
        <input
          v-model="query.keyword"
          class="form-input"
          placeholder="搜索账单号/项目/业主"
          @keyup.enter="loadData"
        />
        <select v-model="query.feeItemId" class="form-input" @change="loadData">
          <option :value="undefined">全部类型</option>
          <option v-for="item in feeItems" :key="item.id" :value="item.id">
            {{ item.itemName }}
          </option>
        </select>
        <input v-model="query.startTime" class="form-input" type="date" @change="loadData" />
        <input v-model="query.endTime" class="form-input" type="date" @change="loadData" />
        <button class="btn btn-primary" type="button" @click="loadData">查询</button>
        <button class="btn btn-ghost" type="button" @click="handleReset">重置</button>
      </div>
    </div>

    <!-- 表格 -->
    <div class="manage-page__table-wrap">
      <table class="manage-page__table">
        <thead>
        <tr>
          <th>账单号</th>
          <th>业主</th>
          <th>房屋</th>
          <th>费用项目</th>
          <th>金额</th>
          <th>状态</th>
          <th>截止日期</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="row in list" :key="row.id">
          <td>
            <span class="table-link" @click="handleViewDetail(row)">{{ row.billNo }}</span>
          </td>
          <td>{{ row.ownerName || '-' }}</td>
          <td>{{ formatHouse(row) }}</td>
          <td>{{ row.itemName }}</td>
          <td>¥{{ row.billAmount.toFixed(2) }}</td>
          <td>
              <span :class="['status-badge', getStatusClass(row.status)]">
                {{ row.statusName }}
              </span>
          </td>
          <td>{{ formatDate(row.payDeadline) }}</td>
          <td>
            <button class="btn btn-sm btn-ghost" type="button" @click="handleViewDetail(row)">
              详情
            </button>
            <button
              v-if="row.status === 0"
              class="btn btn-sm btn-danger"
              type="button"
              @click="handleDelete(row)"
            >
              删除
            </button>
          </td>
        </tr>
        <tr v-if="!loading && !list.length">
          <td colspan="8" class="manage-page__empty">暂无账单</td>
        </tr>
        </tbody>
      </table>
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="manage-page__pagination">
      <button class="btn btn-sm btn-ghost" :disabled="query.page <= 1" @click="changePage(query.page - 1)">
        上一页
      </button>
      <span class="pagination-info">第 {{ query.page }} / {{ totalPages }} 页，共 {{ total }} 条</span>
      <button class="btn btn-sm btn-ghost" :disabled="query.page >= totalPages" @click="changePage(query.page + 1)">
        下一页
      </button>
    </div>

    <!-- 生成账单弹窗 -->
    <div v-if="generateDialog.visible" class="dialog-overlay" @click.self="generateDialog.visible = false">
      <div class="dialog dialog--generate">
        <h3 class="dialog__title">生成账单</h3>
        <div class="dialog__body">
          <div class="form-field">
            <label class="form-label">费用项目 <span class="form-required">*</span></label>
            <select v-model="generateDialog.feeItemId" class="form-input">
              <option :value="0">请选择费用项目</option>
              <option v-for="item in feeItems" :key="item.id" :value="item.id">
                {{ item.itemName }}
              </option>
            </select>
          </div>
          <div class="form-field">
            <label class="form-label">选择业主 <span class="form-required">*</span></label>
            <div class="owner-select">
              <label
                v-for="owner in owners"
                :key="owner.id"
                class="owner-option"
              >
                <input v-model="generateDialog.ownerIds" type="checkbox" :value="owner.id" />
                <span>{{ owner.ownerName }}{{ owner.ownerPhone ? `（${owner.ownerPhone}）` : '' }}</span>
              </label>
              <p v-if="!owners.length" class="owner-select__empty">暂无业主数据</p>
            </div>
          </div>
          <div class="form-field">
            <label class="form-label">账单周期 <span class="form-required">*</span></label>
            <input v-model="generateDialog.billPeriod" class="form-input" type="month" placeholder="如：2026-07" />
          </div>
          <div class="form-field">
            <label class="form-label">账单金额（元） <span class="form-required">*</span></label>
            <input v-model.number="generateDialog.billAmount" class="form-input" type="number" min="0.01" step="0.01" placeholder="请输入账单金额" />
          </div>
          <div class="form-field">
            <label class="form-label">截止日期 <span class="form-required">*</span></label>
            <input v-model="generateDialog.payDeadline" class="form-input" type="date" />
          </div>
          <p class="generate-hint">📌 将为选中的业主生成该费用项目的账单</p>
        </div>
        <div class="dialog__footer">
          <button class="btn btn-ghost" type="button" @click="generateDialog.visible = false">取消</button>
          <button class="btn btn-primary" type="button" :disabled="generateDialog.submitting" @click="handleGenerate">
            {{ generateDialog.submitting ? '生成中...' : '确认生成' }}
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
import {
  getAdminBills,
  getAllFeeItems,
  getBillStatusStats,
  generateBills,
  deleteBill,
  getOwnerList,
  type BillItem,
  type FeeItem,
  type OwnerOption,
} from '@/api/bill-admin'

defineOptions({
  name: 'BillManage',
})

// ============================================================
// 状态
// ============================================================

const router = useRouter()
const loading = ref(false)
const list = ref<BillItem[]>([])
const feeItems = ref<FeeItem[]>([])
const owners = ref<OwnerOption[]>([])
const total = ref(0)
const totalPages = ref(1)

const statistics = reactive({
  total: 0,
  unpaid: 0,
  partial: 0,
  paid: 0,
  overdue: 0,
})

const query = reactive({
  status: undefined as number | undefined,
  feeItemId: undefined as number | undefined,
  keyword: '',
  startTime: '',
  endTime: '',
  page: 1,
  size: 10,
})

const generateDialog = reactive({
  visible: false,
  feeItemId: 0,
  ownerIds: [] as number[],
  billPeriod: '',
  billAmount: 0,
  payDeadline: '',
  submitting: false,
})

// ============================================================
// 方法
// ============================================================

const formatHouse = (row: BillItem): string => {
  const parts = [row.buildingName, row.houseNumber].filter(Boolean)
  return parts.length ? parts.join(' ') : '-'
}

const loadFeeItems = async () => {
  try {
    const { data } = await getAllFeeItems()
    feeItems.value = data || []
  } catch (error) {
    console.error('加载费用项目失败:', error)
  }
}

const loadOwners = async () => {
  try {
    const { data } = await getOwnerList()
    owners.value = data || []
  } catch (error) {
    console.error('加载业主列表失败:', error)
  }
}

const loadStatistics = async () => {
  try {
    const { data } = await getBillStatusStats()
    statistics.unpaid = data?.['待缴费'] ?? 0
    statistics.partial = data?.['部分缴费'] ?? 0
    statistics.paid = data?.['已缴费'] ?? 0
    statistics.overdue = data?.['已逾期'] ?? 0
    statistics.total = statistics.unpaid + statistics.partial + statistics.paid + statistics.overdue
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      ...query,
      feeItemId: query.feeItemId,
      status: query.status,
      keyword: query.keyword || undefined,
      startTime: query.startTime || undefined,
      endTime: query.endTime || undefined,
    }
    const { data } = await getAdminBills(params)
    list.value = data?.records || []
    total.value = data?.total || 0
    totalPages.value = data?.pages || 1
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

const setStatusFilter = (status: number | undefined) => {
  query.status = status
  query.page = 1
  loadData()
}

const changePage = (page: number) => {
  if (page < 1 || page > totalPages.value) return
  query.page = page
  loadData()
}

const handleReset = () => {
  query.status = undefined
  query.feeItemId = undefined
  query.keyword = ''
  query.startTime = ''
  query.endTime = ''
  query.page = 1
  loadData()
}

const handleViewDetail = (row: BillItem) => {
  router.push(`/admin/bills/${row.id}`)
}

const openGenerateDialog = () => {
  generateDialog.feeItemId = 0
  generateDialog.ownerIds = []
  generateDialog.billPeriod = ''
  generateDialog.billAmount = 0
  generateDialog.payDeadline = ''
  generateDialog.visible = true
}

const handleGenerate = async () => {
  if (!generateDialog.feeItemId) { alert('请选择费用项目'); return }
  if (!generateDialog.ownerIds.length) { alert('请至少选择一个业主'); return }
  if (!generateDialog.billPeriod) { alert('请选择账单周期'); return }
  if (!generateDialog.billAmount || generateDialog.billAmount <= 0) { alert('请输入有效的账单金额'); return }
  if (!generateDialog.payDeadline) { alert('请选择截止日期'); return }

  generateDialog.submitting = true
  try {
    await generateBills({
      feeItemId: generateDialog.feeItemId,
      ownerIds: generateDialog.ownerIds,
      billPeriod: generateDialog.billPeriod,
      billAmount: generateDialog.billAmount,
      payDeadline: generateDialog.payDeadline,
    })
    generateDialog.visible = false
    loadData()
    loadStatistics()
    alert('账单生成成功！')
  } catch {
    alert('生成失败')
  } finally {
    generateDialog.submitting = false
  }
}

const handleDelete = async (row: BillItem) => {
  if (!confirm(`确定要删除账单「${row.billNo}」吗？`)) return
  try {
    await deleteBill(row.id)
    loadData()
    loadStatistics()
    alert('删除成功')
  } catch {
    alert('删除失败')
  }
}

const getStatusClass = (status: number): string => {
  const map: Record<number, string> = {
    0: 'status-badge--warning',
    1: 'status-badge--primary',
    2: 'status-badge--success',
    3: 'status-badge--danger',
  }
  return map[status] || ''
}

onMounted(async () => {
  await loadFeeItems()
  await loadOwners()
  await loadStatistics()
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
  grid-template-columns: repeat(4, 1fr);
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
.stat-card--danger .stat-card__value {
  color: #dc2626;
}
.stat-card--success .stat-card__value {
  color: #059669;
}
.stat-card--warning .stat-card__value {
  color: #d97706;
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
  width: 180px;
}
.manage-page__filters input[type="date"] {
  width: 150px;
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
.status-badge--success {
  background: #d1fae5;
  color: #059669;
}
.status-badge--danger {
  background: #fef2f2;
  color: #dc2626;
}
.status-badge--primary {
  background: #dbeafe;
  color: #2563eb;
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

.dialog--generate {
  width: min(500px, calc(100% - 32px));
}
.generate-hint {
  margin: 8px 0 0;
  padding: 8px 12px;
  border-radius: var(--radius-sm);
  background: #eff6ff;
  font-size: 13px;
  color: #2563eb;
}
.owner-select {
  max-height: 180px;
  overflow-y: auto;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  padding: 8px 12px;
  background: var(--color-bg);
}
.owner-option {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 0;
  font-size: 14px;
  cursor: pointer;
}
.owner-option input[type="checkbox"] {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}
.owner-select__empty {
  margin: 0;
  padding: 8px 0;
  color: var(--color-text-secondary);
  font-size: 13px;
}
.form-required {
  color: #dc2626;
}

@media (max-width: 1024px) {
  .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 768px) {
  .stat-cards {
    grid-template-columns: 1fr 1fr;
  }
  .manage-page__filters .form-input {
    width: 100%;
  }
  .manage-page__filters input[type="date"] {
    width: 100%;
  }
  .manage-page__filters {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
