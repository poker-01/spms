<!-- src/views/admin/FeeItemManage.vue -->
<template>
  <div class="manage-page">
    <div class="manage-page__header">
      <h2 class="manage-page__title">费用项目管理</h2>
      <button class="btn btn-primary" type="button" @click="openAddDialog">
        + 新增费用项目
      </button>
    </div>

    <!-- 搜索栏 -->
    <div class="manage-page__toolbar">
      <div class="manage-page__filters">
        <input
          v-model="query.keyword"
          class="form-input"
          placeholder="搜索项目名称"
          @keyup.enter="loadData"
        />
        <button class="btn btn-primary" type="button" @click="loadData">查询</button>
        <button class="btn btn-ghost" type="button" @click="handleReset">重置</button>
      </div>
    </div>

    <!-- 表格 -->
    <div class="manage-page__table-wrap">
      <table class="manage-page__table">
        <thead>
        <tr>
          <th>ID</th>
          <th>项目名称</th>
          <th>项目编码</th>
          <th>计费方式</th>
          <th>单价（元）</th>
          <th>状态</th>
          <th>创建时间</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="row in list" :key="row.id">
          <td>{{ row.id }}</td>
          <td><strong>{{ row.itemName }}</strong></td>
          <td>{{ row.itemCode }}</td>
          <td>{{ row.billTypeName }}</td>
          <td>¥{{ row.unitPrice.toFixed(2) }}</td>
          <td>
              <span :class="row.status === 1 ? 'status--enabled' : 'status--disabled'">
                {{ row.status === 1 ? '启用' : '停用' }}
              </span>
          </td>
          <td>{{ formatDate(row.createTime) }}</td>
          <td>
            <button class="btn btn-sm btn-ghost" type="button" @click="openEditDialog(row)">
              编辑
            </button>
            <button
              class="btn btn-sm"
              :class="row.status === 1 ? 'btn-danger' : 'btn-primary'"
              type="button"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '停用' : '启用' }}
            </button>
          </td>
        </tr>
        <tr v-if="!loading && !list.length">
          <td colspan="8" class="manage-page__empty">暂无费用项目</td>
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

    <!-- 新增/编辑弹窗 -->
    <div v-if="dialogVisible" class="dialog-overlay" @click.self="dialogVisible = false">
      <div class="dialog dialog--fee-item">
        <h3 class="dialog__title">{{ isEdit ? '编辑费用项目' : '新增费用项目' }}</h3>
        <form class="dialog__body" @submit.prevent="handleSubmit">
          <div class="form-field">
            <label class="form-label">项目名称 <span class="form-required">*</span></label>
            <input v-model="form.itemName" class="form-input" placeholder="请输入项目名称" />
          </div>
          <div class="form-field">
            <label class="form-label">项目编码 <span class="form-required">*</span></label>
            <input v-model="form.itemCode" class="form-input" placeholder="请输入项目编码" :disabled="isEdit" />
          </div>
          <div class="form-field">
            <label class="form-label">计费方式 <span class="form-required">*</span></label>
            <select v-model="form.billType" class="form-input">
              <option :value="1">按固定金额</option>
              <option :value="2">按面积（元/㎡）</option>
              <option :value="3">按户</option>
            </select>
          </div>
          <div class="form-field">
            <label class="form-label">单价（元） <span class="form-required">*</span></label>
            <input v-model.number="form.unitPrice" class="form-input" type="number" placeholder="请输入单价" min="0" step="0.01" />
          </div>
          <div class="form-field">
            <label class="form-label">状态</label>
            <select v-model="form.status" class="form-input">
              <option :value="1">启用</option>
              <option :value="0">停用</option>
            </select>
          </div>
          <div class="form-field">
            <label class="form-label">备注</label>
            <textarea v-model="form.remark" class="form-input" rows="3" placeholder="请输入备注（选填）" />
          </div>
        </form>
        <div class="dialog__footer">
          <button class="btn btn-ghost" type="button" @click="dialogVisible = false">取消</button>
          <button class="btn btn-primary" type="button" :disabled="submitting" @click="handleSubmit">
            {{ submitting ? '提交中...' : '确定' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { formatDate } from '@/utils/format'

defineOptions({
  name: 'FeeItemManage',
})

// ============================================================
// 类型定义
// ============================================================

interface FeeItem {
  id: number
  itemName: string
  itemCode: string
  billType: number
  billTypeName: string
  unitPrice: number
  status: number
  remark?: string
  createTime: string
}

interface FeeItemQuery {
  keyword?: string
  pageNum?: number
  pageSize?: number
  [key: string]: string | number | boolean | null | undefined
}

// ============================================================
// 状态
// ============================================================

const loading = ref(false)
const list = ref<FeeItem[]>([])
const total = ref(0)
const totalPages = ref(1)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)

const query = reactive<FeeItemQuery>({
  keyword: '',
  pageNum: 1,
  pageSize: 10,
})

const form = reactive({
  id: 0,
  itemName: '',
  itemCode: '',
  billType: 1,
  unitPrice: 0,
  status: 1,
  remark: '',
})

// ============================================================
// Mock 数据
// ============================================================

const mockList: FeeItem[] = [
  { id: 1, itemName: '物业管理费', itemCode: 'PROPERTY_FEE', billType: 1, billTypeName: '按固定金额', unitPrice: 320.50, status: 1, createTime: '2026-01-01 08:00:00' },
  { id: 2, itemName: '水费', itemCode: 'WATER_FEE', billType: 2, billTypeName: '按面积（元/㎡）', unitPrice: 2.50, status: 1, createTime: '2026-01-01 08:00:00' },
  { id: 3, itemName: '电费', itemCode: 'ELECTRIC_FEE', billType: 2, billTypeName: '按面积（元/㎡）', unitPrice: 0.80, status: 1, createTime: '2026-01-01 08:00:00' },
  { id: 4, itemName: '车位管理费', itemCode: 'PARKING_FEE', billType: 1, billTypeName: '按固定金额', unitPrice: 200.00, status: 1, createTime: '2026-01-01 08:00:00' },
  { id: 5, itemName: '燃气费', itemCode: 'GAS_FEE', billType: 3, billTypeName: '按户', unitPrice: 0, status: 0, createTime: '2026-01-01 08:00:00' },
]

// ============================================================
// 方法
// ============================================================

const loadData = async () => {
  loading.value = true
  try {
    // 接口：GET /api/v1/admin/fee-items
    // const { data } = await getFeeItems(query)
    // list.value = data.records
    // total.value = data.total
    // totalPages.value = data.pages

    await new Promise((resolve) => setTimeout(resolve, 300))
    let data = [...mockList]
    if (query.keyword) {
      data = data.filter((item) => item.itemName.includes(query.keyword!))
    }
    total.value = data.length
    totalPages.value = Math.ceil(total.value / query.pageSize!)
    const start = (query.pageNum! - 1) * query.pageSize!
    list.value = data.slice(start, start + query.pageSize!)
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  query.keyword = ''
  query.pageNum = 1
  loadData()
}

const changePage = (page: number) => {
  if (page < 1 || page > totalPages.value) return
  query.pageNum = page
  loadData()
}

const openAddDialog = () => {
  isEdit.value = false
  Object.assign(form, { id: 0, itemName: '', itemCode: '', billType: 1, unitPrice: 0, status: 1, remark: '' })
  dialogVisible.value = true
}

const openEditDialog = (row: FeeItem) => {
  isEdit.value = true
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.itemName.trim()) { alert('请输入项目名称'); return }
  if (!form.itemCode.trim()) { alert('请输入项目编码'); return }
  if (!form.unitPrice || form.unitPrice < 0) { alert('请输入有效的单价'); return }

  submitting.value = true
  try {
    // 接口：POST /api/v1/admin/fee-items 或 PUT /api/v1/admin/fee-items/{id}
    // if (isEdit.value) {
    //   await updateFeeItem(form)
    // } else {
    //   await createFeeItem(form)
    // }

    await new Promise((resolve) => setTimeout(resolve, 800))
    dialogVisible.value = false
    loadData()
    alert(isEdit.value ? '更新成功！' : '新增成功！')
  } catch (error) {
    alert('操作失败')
  } finally {
    submitting.value = false
  }
}

const handleToggleStatus = async (row: FeeItem) => {
  const action = row.status === 1 ? '停用' : '启用'
  if (!confirm(`确定要${action}费用项目「${row.itemName}」吗？`)) return

  try {
    // 接口：PUT /api/v1/admin/fee-items/{id}/status
    // await toggleFeeItemStatus(row.id)

    await new Promise((resolve) => setTimeout(resolve, 500))
    row.status = row.status === 1 ? 0 : 1
    alert(`${action}成功！`)
  } catch (error) {
    alert('操作失败')
  }
}

onMounted(loadData)
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
.status--enabled {
  display: inline-flex;
  padding: 4px 10px;
  border-radius: 999px;
  background: #dcfce7;
  color: #15803d;
  font-size: 12px;
  font-weight: 600;
}
.status--disabled {
  display: inline-flex;
  padding: 4px 10px;
  border-radius: 999px;
  background: #f1f5f9;
  color: var(--color-text-secondary);
  font-size: 12px;
  font-weight: 600;
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
.dialog--fee-item {
  width: min(500px, calc(100% - 32px));
}
.form-required {
  color: #dc2626;
}
@media (max-width: 768px) {
  .manage-page__filters .form-input {
    width: 100%;
  }
  .manage-page__filters {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
