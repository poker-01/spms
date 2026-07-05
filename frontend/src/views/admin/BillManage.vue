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
      <div class="stat-card stat-card--success" :class="{ 'stat-card--active': query.status === 1 }" @click="setStatusFilter(1)">
        <span class="stat-card__label">已缴费</span>
        <span class="stat-card__value">{{ statistics.paid }}</span>
      </div>
      <div class="stat-card stat-card--warning" :class="{ 'stat-card--active': query.status === 2 }" @click="setStatusFilter(2)">
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
        <select v-model="query.billType" class="form-input" @change="loadData">
          <option :value="undefined">全部类型</option>
          <option v-for="item in feeItems" :key="item.id" :value="item.id">
            {{ item.itemName }}
          </option>
        </select>
        <input v-model="query.startDate" class="form-input" type="date" @change="loadData" />
        <input v-model="query.endDate" class="form-input" type="date" @change="loadData" />
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
          <td>{{ row.houseInfo || '-' }}</td>
          <td>{{ row.itemName }}</td>
          <td>¥{{ row.amount.toFixed(2) }}</td>
          <td>
              <span :class="['status-badge', getStatusClass(row.status)]">
                {{ row.statusName }}
              </span>
          </td>
          <td>{{ formatDate(row.deadline) }}</td>
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
      <button class="btn btn-sm btn-ghost" :disabled="query.pageNum <= 1" @click="changePage(query.pageNum - 1)">
        上一页
      </button>
      <span class="pagination-info">第 {{ query.pageNum }} / {{ totalPages }} 页，共 {{ total }} 条</span>
      <button class="btn btn-sm btn-ghost" :disabled="query.pageNum >= totalPages" @click="changePage(query.pageNum + 1)">
        下一页
      </button>
    </div>

    <!-- 生成账单弹窗 -->
    <div v-if="generateDialog.visible" class="dialog-overlay" @click.self="generateDialog.visible = false">
      <div class="dialog dialog--generate">
        <h3 class="dialog__title">生成账单</h3>
        <div class="dialog__body">
          <div class="form-field">
            <label class="form-label">选择小区 <span class="form-required">*</span></label>
            <select v-model="generateDialog.communityId" class="form-input">
              <option :value="0">请选择小区</option>
              <option v-for="item in communities" :key="item.id" :value="item.id">
                {{ item.name }}
              </option>
            </select>
          </div>
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
            <label class="form-label">账单周期 <span class="form-required">*</span></label>
            <input v-model="generateDialog.period" class="form-input" type="month" />
          </div>
          <div class="form-field">
            <label class="form-label">截止日期 <span class="form-required">*</span></label>
            <input v-model="generateDialog.deadline" class="form-input" type="date" />
          </div>
          <p class="generate-hint">📌 将为该小区所有业主生成该费用项目的账单</p>
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

defineOptions({
  name: 'BillManage',
})

// ============================================================
// 类型定义
// ============================================================

interface BillItem {
  id: number
  billNo: string
  ownerName?: string
  houseInfo?: string
  itemName: string
  itemType: number
  amount: number
  status: number
  statusName: string
  deadline: string
  payTime?: string
  createTime: string
}

interface FeeItem {
  id: number
  itemName: string
  itemCode: string
}

interface Community {
  id: number
  name: string
}

// ============================================================
// 状态
// ============================================================

const router = useRouter()
const loading = ref(false)
const list = ref<BillItem[]>([])
const feeItems = ref<FeeItem[]>([])
const communities = ref<Community[]>([])
const total = ref(0)
const totalPages = ref(1)

const statistics = reactive({
  total: 0,
  unpaid: 0,
  paid: 0,
  overdue: 0,
})

const query = reactive({
  status: undefined as number | undefined,
  billType: undefined as number | undefined,
  keyword: '',
  startDate: '',
  endDate: '',
  pageNum: 1,
  pageSize: 10,
})

const generateDialog = reactive({
  visible: false,
  communityId: 0,
  feeItemId: 0,
  period: '',
  deadline: '',
  submitting: false,
})

// ============================================================
// Mock 数据
// ============================================================

const mockBillList: BillItem[] = [
  { id: 1, billNo: 'ZD20260705001', ownerName: '张三', houseInfo: 'A栋1单元101', itemName: '物业管理费', itemType: 1, amount: 320.50, status: 0, statusName: '待缴费', deadline: '2026-07-25 23:59:59', createTime: '2026-07-01 08:00:00' },
  { id: 2, billNo: 'ZD20260705002', ownerName: '张三', houseInfo: 'A栋1单元101', itemName: '水费', itemType: 2, amount: 45.80, status: 0, statusName: '待缴费', deadline: '2026-07-15 23:59:59', createTime: '2026-07-01 08:00:00' },
  { id: 3, billNo: 'ZD20260705003', ownerName: '李四', houseInfo: 'A栋1单元102', itemName: '物业管理费', itemType: 1, amount: 320.50, status: 1, statusName: '已缴费', deadline: '2026-07-25 23:59:59', payTime: '2026-07-05 10:00:00', createTime: '2026-07-01 08:00:00' },
  { id: 4, billNo: 'ZD20260605004', ownerName: '王五', houseInfo: 'B栋2单元201', itemName: '物业管理费', itemType: 1, amount: 320.50, status: 2, statusName: '已逾期', deadline: '2026-06-25 23:59:59', createTime: '2026-06-01 08:00:00' },
  { id: 5, billNo: 'ZD20260605005', ownerName: '王五', houseInfo: 'B栋2单元201', itemName: '车位管理费', itemType: 4, amount: 200.00, status: 1, statusName: '已缴费', deadline: '2026-06-25 23:59:59', payTime: '2026-06-20 14:30:00', createTime: '2026-06-01 08:00:00' },
]

const mockFeeItems: FeeItem[] = [
  { id: 1, itemName: '物业管理费', itemCode: 'PROPERTY_FEE' },
  { id: 2, itemName: '水费', itemCode: 'WATER_FEE' },
  { id: 3, itemName: '电费', itemCode: 'ELECTRIC_FEE' },
  { id: 4, itemName: '车位管理费', itemCode: 'PARKING_FEE' },
]

const mockCommunities: Community[] = [
  { id: 1, name: '翠湖花园' },
  { id: 2, name: '阳光新城' },
  { id: 3, name: '滨江御景' },
]

// ============================================================
// 方法
// ============================================================

const loadFeeItems = async () => {
  try {
    // 接口：GET /api/v1/admin/fee-items/all
    // const { data } = await getAllFeeItems()
    // feeItems.value = data
    feeItems.value = mockFeeItems
  } catch (error) {
    console.error('加载费用项目失败:', error)
  }
}

const loadCommunities = async () => {
  try {
    // 接口：GET /api/v1/admin/communities/all
    // const { data } = await getAllCommunities()
    // communities.value = data
    communities.value = mockCommunities
  } catch (error) {
    console.error('加载小区失败:', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    // 接口：GET /api/v1/admin/bills
    // const { data } = await getAdminBills(query)

    await new Promise((resolve) => setTimeout(resolve, 300))
    let data = [...mockBillList]
    if (query.status !== undefined) {
      data = data.filter((item) => item.status === query.status)
    }
    if (query.billType !== undefined) {
      data = data.filter((item) => item.itemType === query.billType)
    }
    if (query.keyword) {
      const kw = query.keyword.toLowerCase()
      data = data.filter((item) =>
        item.billNo.toLowerCase().includes(kw) ||
        item.itemName.includes(kw) ||
        (item.ownerName && item.ownerName.includes(kw))
      )
    }
    total.value = data.length
    totalPages.value = Math.ceil(total.value / query.pageSize)
    const start = (query.pageNum - 1) * query.pageSize
    list.value = data.slice(start, start + query.pageSize)
    updateStatistics(data)
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

const updateStatistics = (data: BillItem[]) => {
  statistics.total = data.length
  statistics.unpaid = data.filter((i) => i.status === 0).length
  statistics.paid = data.filter((i) => i.status === 1).length
  statistics.overdue = data.filter((i) => i.status === 2).length
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
  query.billType = undefined
  query.keyword = ''
  query.startDate = ''
  query.endDate = ''
  query.pageNum = 1
  loadData()
}

const handleViewDetail = (row: BillItem) => {
  router.push(`/admin/bills/${row.id}`)
}

const openGenerateDialog = () => {
  generateDialog.communityId = 0
  generateDialog.feeItemId = 0
  generateDialog.period = ''
  generateDialog.deadline = ''
  generateDialog.visible = true
}

const handleGenerate = async () => {
  if (!generateDialog.communityId) { alert('请选择小区'); return }
  if (!generateDialog.feeItemId) { alert('请选择费用项目'); return }
  if (!generateDialog.period) { alert('请选择账单周期'); return }
  if (!generateDialog.deadline) { alert('请选择截止日期'); return }

  generateDialog.submitting = true
  try {
    // 接口：POST /api/v1/admin/bills/generate
    // await generateBills({
    //   communityId: generateDialog.communityId,
    //   feeItemId: generateDialog.feeItemId,
    //   period: generateDialog.period,
    //   deadline: generateDialog.deadline,
    // })

    await new Promise((resolve) => setTimeout(resolve, 1500))
    generateDialog.visible = false
    loadData()
    alert('账单生成成功！')
  } catch (error) {
    alert('生成失败')
  } finally {
    generateDialog.submitting = false
  }
}

const handleDelete = async (row: BillItem) => {
  if (!confirm(`确定要删除账单「${row.billNo}」吗？`)) return
  try {
    // 接口：DELETE /api/v1/admin/bills/{id}
    await new Promise((resolve) => setTimeout(resolve, 500))
    loadData()
    alert('删除成功')
  } catch (error) {
    alert('删除失败')
  }
}

const getStatusClass = (status: number): string => {
  const map: Record<number, string> = {
    0: 'status-badge--warning',
    1: 'status-badge--success',
    2: 'status-badge--danger',
  }
  return map[status] || ''
}

onMounted(async () => {
  await loadFeeItems()
  await loadCommunities()
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
