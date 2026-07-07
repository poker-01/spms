<template>
  <div class="manage-page">
    <div class="manage-page__header">
      <h2 class="manage-page__title">我的维修任务</h2>
    </div>

    <!-- ============================================================ -->
    <!-- Tab 切换：待处理 / 历史记录 -->
    <!-- ============================================================ -->
    <div class="tab-bar">
      <button
        class="tab-bar__item"
        :class="{ 'tab-bar__item--active': activeTab === 'pending' }"
        @click="switchTab('pending')"
      >
        待处理工单
        <span v-if="pendingTotal > 0" class="tab-bar__badge">{{ pendingTotal }}</span>
      </button>
      <button
        class="tab-bar__item"
        :class="{ 'tab-bar__item--active': activeTab === 'history' }"
        @click="switchTab('history')"
      >
        历史维修记录
        <span v-if="historyTotal > 0" class="tab-bar__badge tab-bar__badge--muted">{{ historyTotal }}</span>
      </button>
    </div>

    <!-- ============================================================ -->
    <!-- 待处理工单 -->
    <!-- ============================================================ -->
    <template v-if="activeTab === 'pending'">
      <!-- 搜索栏 -->
      <div class="manage-page__toolbar">
        <div class="manage-page__filters">
          <input
            v-model="pendingQuery.keyword"
            class="form-input"
            placeholder="搜索订单号/描述"
            @keyup.enter="loadPendingData"
          />
          <select v-model="pendingQuery.repairType" class="form-input" @change="loadPendingData">
            <option :value="undefined">全部类型</option>
            <option v-for="item in repairTypes" :key="item.value" :value="item.value">
              {{ item.label }}
            </option>
          </select>
          <button class="btn btn-primary" type="button" @click="loadPendingData">查询</button>
          <button class="btn btn-ghost" type="button" @click="resetPending">重置</button>
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
              <th>派单时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in pendingList" :key="row.id">
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
              <td>{{ formatDate(row.assignTime || row.createTime) }}</td>
              <td>
                <!-- 已派单(1) → 开始处理 -->
                <button
                  v-if="row.status === 1"
                  class="btn btn-sm btn-primary"
                  type="button"
                  @click="handleStart(row)"
                >
                  开始处理
                </button>
                <!-- 处理中(2) → 完成 -->
                <button
                  v-if="row.status === 2"
                  class="btn btn-sm btn-success"
                  type="button"
                  @click="openCompleteDialog(row)"
                >
                  完成维修
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
            <tr v-if="!pendingLoading && !pendingList.length">
              <td colspan="7" class="manage-page__empty">暂无待处理工单</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页 -->
      <div v-if="pendingTotal > 0" class="manage-page__pagination">
        <button
          class="btn btn-sm btn-ghost"
          :disabled="getCurrentPage('pending') <= 1"
          @click="changePage('pending', getCurrentPage('pending') - 1)"
        >
          上一页
        </button>
        <span class="pagination-info">
          第 {{ getCurrentPage('pending') }} / {{ pendingPages }} 页，共 {{ pendingTotal }} 条
        </span>
        <button
          class="btn btn-sm btn-ghost"
          :disabled="getCurrentPage('pending') >= pendingPages"
          @click="changePage('pending', getCurrentPage('pending') + 1)"
        >
          下一页
        </button>
      </div>
    </template>

    <!-- ============================================================ -->
    <!-- 历史维修记录 -->
    <!-- ============================================================ -->
    <template v-if="activeTab === 'history'">
      <!-- 搜索栏 -->
      <div class="manage-page__toolbar">
        <div class="manage-page__filters">
          <input
            v-model="historyQuery.keyword"
            class="form-input"
            placeholder="搜索订单号/描述"
            @keyup.enter="loadHistoryData"
          />
            <select v-model="historyQuery.status" class="form-input" @change="loadHistoryData">
            <option :value="undefined">全部状态</option>
            <option :value="3">已完成</option>
            <option :value="4">已取消</option>
            <option :value="5">已关闭</option>
          </select>
          <select v-model="historyQuery.repairType" class="form-input" @change="loadHistoryData">
            <option :value="undefined">全部类型</option>
            <option v-for="item in repairTypes" :key="item.value" :value="item.value">
              {{ item.label }}
            </option>
          </select>
          <button class="btn btn-primary" type="button" @click="loadHistoryData">查询</button>
          <button class="btn btn-ghost" type="button" @click="resetHistory">重置</button>
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
              <th>费用</th>
              <th>评价</th>
              <th>完成时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in historyList" :key="row.id">
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
              <td>
                <span v-if="row.repairCost != null" class="cost-text">¥{{ row.repairCost.toFixed(2) }}</span>
                <span v-else>-</span>
              </td>
              <td>
                <span v-if="row.evaluateScore" class="stars-text">{{ '⭐'.repeat(row.evaluateScore) }}</span>
                <span v-else class="no-evaluate">未评价</span>
              </td>
              <td>{{ formatDate(row.repairTime || row.updateTime) }}</td>
              <td>
                <button
                  class="btn btn-sm btn-ghost"
                  type="button"
                  @click="handleViewDetail(row)"
                >
                  详情
                </button>
              </td>
            </tr>
            <tr v-if="!historyLoading && !historyList.length">
              <td colspan="9" class="manage-page__empty">暂无历史维修记录</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页 -->
      <div v-if="historyTotal > 0" class="manage-page__pagination">
        <button
          class="btn btn-sm btn-ghost"
          :disabled="getCurrentPage('history') <= 1"
          @click="changePage('history', getCurrentPage('history') - 1)"
        >
          上一页
        </button>
        <span class="pagination-info">
          第 {{ getCurrentPage('history') }} / {{ historyPages }} 页，共 {{ historyTotal }} 条
        </span>
        <button
          class="btn btn-sm btn-ghost"
          :disabled="getCurrentPage('history') >= historyPages"
          @click="changePage('history', getCurrentPage('history') + 1)"
        >
          下一页
        </button>
      </div>
    </template>

    <!-- ============================================================ -->
    <!-- 完成维修弹窗 -->
    <!-- ============================================================ -->
    <div
      v-if="completeDialog.visible"
      class="dialog-overlay"
      @click.self="completeDialog.visible = false"
    >
      <div class="dialog dialog--complete">
        <h3 class="dialog__title">完成维修</h3>
        <div class="dialog__body">
          <p class="dialog__info">
            报修单：<strong>{{ completeDialog.repair?.orderNo }}</strong>
          </p>
          <p class="dialog__info">
            描述：{{ completeDialog.repair?.repairDesc }}
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
          <div class="form-field">
            <label class="form-label">维修备注（选填）</label>
            <textarea
              v-model="completeDialog.remark"
              class="form-input"
              rows="3"
              placeholder="请输入维修备注，如更换配件等"
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
import { useUserStore } from '@/stores/user'
import { formatDate } from '@/utils/format'
import { getRepairPage, startRepair, completeRepair } from '@/api/repair'
import type { RepairVO, RepairQuery } from '@/api/repair'

defineOptions({
  name: 'RepairerTasks',
})

// ============================================================
// 状态
// ============================================================

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref<'pending' | 'history'>('pending')

// 报修类型
const repairTypes = [
  { value: '水电维修', label: '水电维修' },
  { value: '家具维修', label: '家具维修' },
  { value: '家电维修', label: '家电维修' },
  { value: '其他', label: '其他' },
]

// ---- 待处理工单 ----
const pendingLoading = ref(false)
const pendingList = ref<RepairVO[]>([])
const pendingTotal = ref(0)
const pendingPages = ref(1)
const pendingQuery = reactive<RepairQuery>({
  keyword: '',
  repairType: undefined,
  page: 1,
  size: 10,
})

// ---- 历史维修记录 ----
const historyLoading = ref(false)
const historyList = ref<RepairVO[]>([])
const historyTotal = ref(0)
const historyPages = ref(1)
const historyQuery = reactive<RepairQuery>({
  keyword: '',
  status: undefined,
  repairType: undefined,
  page: 1,
  size: 10,
})

// ---- 完成弹窗 ----
const completeDialog = reactive({
  visible: false,
  repair: null as RepairVO | null,
  repairCost: 0,
  remark: '',
  submitting: false,
})

// ============================================================
// 辅助方法
// ============================================================

/** 获取当前用户ID（维修人员ID） */
const getCurrentUserId = (): number => {
  return userStore.userInfo?.id ?? 0
}

/** 获取当前页码 */
const getCurrentPage = (tab: 'pending' | 'history'): number => {
  if (tab === 'pending') return pendingQuery.page ?? 1
  return historyQuery.page ?? 1
}

/** 构建按 assigneeId 查询的参数 */
const buildAssigneeParams = (query: RepairQuery): RepairQuery => {
  return {
    ...query,
    assigneeId: getCurrentUserId(),
  }
}

// ============================================================
// 数据加载
// ============================================================

/** 加载待处理工单（状态 1=已派单, 2=处理中） */
const loadPendingData = async () => {
  pendingLoading.value = true
  try {
    const params = buildAssigneeParams({
      keyword: pendingQuery.keyword || undefined,
      repairType: pendingQuery.repairType,
      page: getCurrentPage('pending'),
      size: pendingQuery.size,
    })
    // 待处理 = 已派单(1) + 处理中(2)，这里分别查询后合并
    // 后端接口支持按 status 筛选，不支持多状态 in，故分别查
    const [resultAssigned, resultProcessing] = await Promise.all([
      getRepairPage({ ...params, status: 1 }),
      getRepairPage({ ...params, status: 2 }),
    ])

    // 按时间倒序合并
    const allRecords = [
      ...(resultAssigned.data.records || []),
      ...(resultProcessing.data.records || []),
    ].sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime())

    pendingList.value = allRecords
    pendingTotal.value = allRecords.length
    pendingPages.value = Math.ceil(allRecords.length / (pendingQuery.size ?? 10)) || 1
  } catch (error) {
    console.error('加载待处理工单失败:', error)
  } finally {
    pendingLoading.value = false
  }
}

/** 加载历史维修记录（状态 3=已完成, 4=已取消, 5=已关闭） */
const loadHistoryData = async () => {
  historyLoading.value = true
  try {
    const params = buildAssigneeParams({
      keyword: historyQuery.keyword || undefined,
      repairType: historyQuery.repairType,
      page: getCurrentPage('history'),
      size: historyQuery.size,
    })

    // 如果用户选择了具体状态，按单个状态查；否则已完成+已取消+已关闭合并
    if (historyQuery.status !== undefined && historyQuery.status !== null) {
      const { data } = await getRepairPage({ ...params, status: historyQuery.status })
      historyList.value = data.records
      historyTotal.value = data.total
      historyPages.value = data.pages
    } else {
      const [resultCompleted, resultCancelled, resultClosed] = await Promise.all([
        getRepairPage({ ...params, status: 3 }),
        getRepairPage({ ...params, status: 4 }),
        getRepairPage({ ...params, status: 5 }),
      ])
      const allRecords = [
        ...(resultCompleted.data.records || []),
        ...(resultCancelled.data.records || []),
        ...(resultClosed.data.records || []),
      ].sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime())

      historyList.value = allRecords
      historyTotal.value = allRecords.length
      historyPages.value = Math.ceil(allRecords.length / (historyQuery.size ?? 10)) || 1
    }
  } catch (error) {
    console.error('加载历史维修记录失败:', error)
  } finally {
    historyLoading.value = false
  }
}

// ============================================================
// 操作
// ============================================================

/** 切换 Tab */
const switchTab = (tab: 'pending' | 'history') => {
  activeTab.value = tab
  if (tab === 'pending') {
    pendingQuery.page = 1
    loadPendingData()
  } else {
    historyQuery.page = 1
    loadHistoryData()
  }
}

/** 切换页码 */
const changePage = (tab: 'pending' | 'history', page: number) => {
  if (tab === 'pending') {
    const max = pendingPages.value
    if (page < 1 || page > max) return
    pendingQuery.page = page
    loadPendingData()
  } else {
    const max = historyPages.value
    if (page < 1 || page > max) return
    historyQuery.page = page
    loadHistoryData()
  }
}

/** 重置待处理筛选 */
const resetPending = () => {
  pendingQuery.keyword = ''
  pendingQuery.repairType = undefined
  pendingQuery.page = 1
  loadPendingData()
}

/** 重置历史记录筛选 */
const resetHistory = () => {
  historyQuery.keyword = ''
  historyQuery.status = undefined
  historyQuery.repairType = undefined
  historyQuery.page = 1
  loadHistoryData()
}

/** 查看详情 */
const handleViewDetail = (row: RepairVO) => {
  router.push(`/admin/repairs/${row.id}`)
}

/** 开始处理 */
const handleStart = async (row: RepairVO) => {
  try {
    await startRepair(row.id)
    alert('已开始处理！')
    await loadPendingData()
  } catch (error) {
    console.error('开始处理失败:', error)
    alert('操作失败，请稍后重试')
  }
}

/** 打开完成弹窗 */
const openCompleteDialog = (row: RepairVO) => {
  completeDialog.repair = row
  completeDialog.repairCost = 0
  completeDialog.remark = ''
  completeDialog.visible = true
}

/** 确认完成 */
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
      repairResult: completeDialog.remark || undefined,
    })
    completeDialog.visible = false
    alert('维修已完成！')
    await loadPendingData()
  } catch (error) {
    console.error('完成维修失败:', error)
    alert('操作失败，请稍后重试')
  } finally {
    completeDialog.submitting = false
  }
}

/** 获取状态样式 */
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

onMounted(() => {
  loadPendingData()
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

/* ============================================================ */
/* Tab 切换栏 */
/* ============================================================ */
.tab-bar {
  display: flex;
  gap: 0;
  margin-bottom: 20px;
  border-bottom: 2px solid var(--color-border);
}

.tab-bar__item {
  position: relative;
  padding: 10px 24px;
  border: none;
  background: transparent;
  color: var(--color-text-secondary);
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.tab-bar__item:hover {
  color: var(--color-text);
}

.tab-bar__item--active {
  color: var(--color-primary);
}

.tab-bar__item--active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--color-primary);
  border-radius: 1px;
}

.tab-bar__badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 22px;
  height: 22px;
  padding: 0 6px;
  border-radius: 999px;
  background: var(--color-primary);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
}

.tab-bar__badge--muted {
  background: var(--color-border);
  color: var(--color-text-secondary);
}

/* ============================================================ */
/* 搜索栏 */
/* ============================================================ */
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

/* ============================================================ */
/* 表格 */
/* ============================================================ */
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

/* ============================================================ */
/* 状态标签 */
/* ============================================================ */
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

/* ============================================================ */
/* 费用 & 评价 */
/* ============================================================ */
.cost-text {
  color: var(--color-primary);
  font-weight: 600;
}

.stars-text {
  font-size: 14px;
}

.no-evaluate {
  color: var(--color-text-secondary);
  font-size: 13px;
}

/* ============================================================ */
/* 分页 */
/* ============================================================ */
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

/* ============================================================ */
/* 弹窗 */
/* ============================================================ */
.dialog--complete {
  width: min(480px, calc(100% - 32px));
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

/* ============================================================ */
/* 响应式 */
/* ============================================================ */
@media (max-width: 768px) {
  .manage-page__filters {
    flex-direction: column;
    align-items: stretch;
  }

  .manage-page__filters .form-input {
    width: 100%;
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
  .tab-bar__item {
    padding: 10px 16px;
    font-size: 14px;
  }
}
</style>
