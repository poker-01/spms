<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// ============ API 接口 ============
const houseApi = {
  // 获取房屋列表（分页）
  getList: (params: any) => {
    // return request.get('/api/v1/houses', { params })
    return Promise.resolve({ data: { list: [], total: 0 } })
  },
  // 查询某楼栋所有房屋（下拉列表用）
  getByBuilding: (buildingId: number) => {
    // return request.get(`/api/v1/houses/by-building/${buildingId}`)
    return Promise.resolve({ data: [] })
  },
  // 查询某业主所有房屋（业主端显示）
  getByOwner: (ownerId: number) => {
    // return request.get(`/api/v1/houses/by-owner/${ownerId}`)
    return Promise.resolve({ data: [] })
  },
  // 新增房屋
  create: (data: any) => {
    // return request.post('/api/v1/houses', data)
    return Promise.resolve({ data: {} })
  },
  // 更新房屋
  update: (id: number, data: any) => {
    // return request.put(`/api/v1/houses/${id}`, data)
    return Promise.resolve({ data: {} })
  },
  // 删除房屋
  delete: (id: number) => {
    // return request.delete(`/api/v1/houses/${id}`)
    return Promise.resolve({ data: {} })
  }
}

const communityApi = {
  getOptions: () => {
    // return request.get('/api/v1/communities/options')
    return Promise.resolve({ data: [] })
  }
}

const buildingApi = {
  // 查询某小区所有楼栋（下拉列表用）
  getByCommunity: (communityId: number) => {
    // return request.get(`/api/v1/buildings/by-community/${communityId}`)
    return Promise.resolve({ data: [] })
  }
}
// =========================================

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增房屋')
const isEdit = ref(false)

const communityId = ref<number>()
const buildingId = ref<number>()
const communityOptions = ref<any[]>([])
const buildingOptions = ref<any[]>([])

const searchForm = reactive({
  houseNumber: '',
  status: undefined as number | undefined
})

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref<any[]>([])

const form = reactive({
  id: null as number | null,
  buildingId: null as number | null,
  houseNumber: '',
  floorNumber: 0,
  houseArea: 0,
  houseType: '',
  ownerName: '',
  ownerPhone: '',
  status: 1,
  remark: ''
})

const statusMap = [
  { value: 1, label: '空置', type: 'info' },
  { value: 2, label: '已售', type: 'success' },
  { value: 3, label: '已租', type: 'warning' },
  { value: 4, label: '已入住', type: 'primary' }
]

const loadCommunityOptions = async () => {
  try {
    const res = await communityApi.getOptions()
    communityOptions.value = res.data || []
    if (communityOptions.value.length > 0) {
      communityId.value = communityOptions.value[0].id
      await loadBuildingOptions()
    }
  } catch (error) {
    ElMessage.error('加载小区列表失败')
  }
}

const loadBuildingOptions = async () => {
  if (!communityId.value) return
  try {
    const res = await buildingApi.getByCommunity(communityId.value)
    buildingOptions.value = res.data || []
    if (buildingOptions.value.length > 0) {
      buildingId.value = buildingOptions.value[0].id
      loadData()
    }
  } catch (error) {
    ElMessage.error('加载楼栋列表失败')
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.current,
      pageSize: pagination.pageSize,
      buildingId: buildingId.value,
      ...searchForm
    }
    const res = await houseApi.getList(params)
    tableData.value = res.data.list || []
    pagination.total = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleCommunityChange = async () => {
  await loadBuildingOptions()
  pagination.current = 1
}

const handleBuildingChange = () => {
  pagination.current = 1
  loadData()
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.houseNumber = ''
  searchForm.status = undefined
  handleSearch()
}

const handleSizeChange = (val: number) => {
  pagination.pageSize = val
  loadData()
}

const handleCurrentChange = (val: number) => {
  pagination.current = val
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增房屋'
  resetForm()
  form.buildingId = buildingId.value || null
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  dialogTitle.value = '编辑房屋'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除房屋「${row.houseNumber}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await houseApi.delete(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      await houseApi.update(form.id!, form)
      ElMessage.success('更新成功')
    } else {
      await houseApi.create(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新失败' : '新增失败')
  }
}

const resetForm = () => {
  form.id = null
  form.buildingId = null
  form.houseNumber = ''
  form.floorNumber = 0
  form.houseArea = 0
  form.houseType = ''
  form.ownerName = ''
  form.ownerPhone = ''
  form.status = 1
  form.remark = ''
}

const getStatusLabel = (status: number) => {
  const found = statusMap.find(s => s.value === status)
  return found ? found.label : '未知'
}

const getStatusClass = (status: number) => {
  const found = statusMap.find(s => s.value === status)
  return found ? found.type : 'info'
}

onMounted(() => {
  loadCommunityOptions()
})
</script>

<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-header__left">
        <h2 class="page-title">房屋管理</h2>
        <span class="page-subtitle">管理各楼栋房屋信息</span>
      </div>
      <div class="page-header__actions">
        <select v-model="communityId" class="form-select" @change="handleCommunityChange">
          <option v-for="item in communityOptions" :key="item.id" :value="item.id">
            {{ item.communityName }}
          </option>
        </select>
        <select v-model="buildingId" class="form-select" @change="handleBuildingChange">
          <option v-for="item in buildingOptions" :key="item.id" :value="item.id">
            {{ item.buildingName }}
          </option>
        </select>
        <button class="btn btn-primary" @click="handleAdd">
          <svg class="icon" viewBox="0 0 24 24" width="16" height="16"><path fill="currentColor" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/></svg>
          新增房屋
        </button>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-section__inner">
        <div class="search-row">
          <div class="search-row__item">
            <label class="search-row__label">房屋编号</label>
            <input v-model="searchForm.houseNumber" placeholder="请输入房屋编号" class="form-input" />
          </div>
          <div class="search-row__item">
            <label class="search-row__label">状态</label>
            <select v-model="searchForm.status" class="form-select">
              <option :value="undefined">全部</option>
              <option v-for="item in statusMap" :key="item.value" :value="item.value">
                {{ item.label }}
              </option>
            </select>
          </div>
          <div class="search-row__actions">
            <button class="btn btn-primary" @click="handleSearch">搜索</button>
            <button class="btn btn-ghost" @click="handleReset">重置</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 表格 -->
    <div class="table-section">
      <div class="table-section__header">
        <span class="table-section__count">共 {{ pagination.total }} 条记录</span>
      </div>
      <div class="table-wrap">
        <table class="table">
          <thead>
          <tr>
            <th style="width: 60px;">ID</th>
            <th style="min-width: 110px;">房屋编号</th>
            <th style="width: 100px;">楼栋</th>
            <th style="width: 100px;">小区</th>
            <th style="width: 60px;">楼层</th>
            <th style="width: 90px;">面积(㎡)</th>
            <th style="width: 100px;">户型</th>
            <th style="width: 80px;">业主</th>
            <th style="width: 80px;">状态</th>
            <th style="width: 160px;">创建时间</th>
            <th style="width: 150px;">操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="row in tableData" :key="row.id">
            <td>{{ row.id }}</td>
            <td><span class="tag tag-code">{{ row.houseNumber }}</span></td>
            <td>{{ row.buildingName }}</td>
            <td>{{ row.communityName }}</td>
            <td>{{ row.floorNumber }}</td>
            <td>{{ row.houseArea }}</td>
            <td>{{ row.houseType }}</td>
            <td>{{ row.ownerName || '-' }}</td>
            <td>
                <span class="status-tag" :class="getStatusClass(row.status)">
                  {{ getStatusLabel(row.status) }}
                </span>
            </td>
            <td class="text-muted">{{ row.createTime }}</td>
            <td>
              <div class="action-group">
                <button class="btn-action btn-action--edit" @click="handleEdit(row)">编辑</button>
                <button class="btn-action btn-action--delete" @click="handleDelete(row)">删除</button>
              </div>
            </td>
          </tr>
          <tr v-if="!tableData.length">
            <td colspan="11" class="empty-state">
              <div class="empty-state__icon">📋</div>
              <p>暂无数据</p>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页 -->
      <div class="pagination">
        <span class="pagination__info">共 {{ pagination.total }} 条</span>
        <div class="pagination__controls">
          <button class="btn btn-sm btn-ghost" :disabled="pagination.current <= 1" @click="handleCurrentChange(pagination.current - 1)">上一页</button>
          <span class="pagination__current">{{ pagination.current }}</span>
          <span class="pagination__total">/ {{ Math.ceil(pagination.total / pagination.pageSize) || 1 }}</span>
          <button class="btn btn-sm btn-ghost" :disabled="pagination.current >= Math.ceil(pagination.total / pagination.pageSize)" @click="handleCurrentChange(pagination.current + 1)">下一页</button>
        </div>
      </div>
    </div>

    <!-- 弹窗 -->
    <div v-if="dialogVisible" class="dialog-overlay" @click.self="dialogVisible = false">
      <div class="dialog">
        <div class="dialog__header">
          <h3 class="dialog__title">{{ dialogTitle }}</h3>
          <button class="dialog__close" @click="dialogVisible = false">✕</button>
        </div>
        <div class="dialog__body">
          <div class="form-grid">
            <div class="form-field full-width">
              <label class="form-label required">所属楼栋</label>
              <select v-model="form.buildingId" class="form-select">
                <option v-for="item in buildingOptions" :key="item.id" :value="item.id">
                  {{ item.buildingName }}
                </option>
              </select>
            </div>
            <div class="form-field">
              <label class="form-label required">房屋编号</label>
              <input v-model="form.houseNumber" class="form-input" placeholder="请输入房屋编号" />
            </div>
            <div class="form-field">
              <label class="form-label">楼层</label>
              <input v-model.number="form.floorNumber" class="form-input" type="number" min="0" />
            </div>
            <div class="form-field">
              <label class="form-label">面积(㎡)</label>
              <input v-model.number="form.houseArea" class="form-input" type="number" min="0" step="0.01" />
            </div>
            <div class="form-field">
              <label class="form-label">户型</label>
              <select v-model="form.houseType" class="form-select">
                <option value="">请选择户型</option>
                <option value="一室一厅">一室一厅</option>
                <option value="两室一厅">两室一厅</option>
                <option value="两室两厅">两室两厅</option>
                <option value="三室一厅">三室一厅</option>
                <option value="三室两厅">三室两厅</option>
                <option value="四室两厅">四室两厅</option>
              </select>
            </div>
            <div class="form-field">
              <label class="form-label">业主姓名</label>
              <input v-model="form.ownerName" class="form-input" placeholder="请输入业主姓名" />
            </div>
            <div class="form-field">
              <label class="form-label">业主电话</label>
              <input v-model="form.ownerPhone" class="form-input" placeholder="请输入业主电话" />
            </div>
            <div class="form-field">
              <label class="form-label">状态</label>
              <div class="radio-group">
                <label class="radio-label" v-for="item in statusMap" :key="item.value">
                  <input type="radio" :value="item.value" v-model="form.status" /> {{ item.label }}
                </label>
              </div>
            </div>
            <div class="form-field full-width">
              <label class="form-label">备注</label>
              <textarea v-model="form.remark" class="form-input" rows="3" placeholder="请输入备注"></textarea>
            </div>
          </div>
        </div>
        <div class="dialog__footer">
          <button class="btn btn-ghost" @click="dialogVisible = false">取消</button>
          <button class="btn btn-primary" @click="handleSubmit">确定</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* ===== 页面容器 ===== */
.page-container {
  padding: 24px;
  max-width: 1440px;
  margin: 0 auto;
}

/* ===== 页面头部 ===== */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 12px;
}

.page-header__left {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
}

.page-subtitle {
  font-size: 14px;
  color: #94a3b8;
}

.page-header__actions {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

/* ===== 搜索区域 ===== */
.search-section {
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #e8edf4;
  padding: 20px 24px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.search-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: flex-end;
}

.search-row__item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1 1 160px;
  min-width: 140px;
}

.search-row__label {
  font-size: 13px;
  font-weight: 500;
  color: #475569;
}

.search-row__actions {
  display: flex;
  gap: 8px;
  padding-bottom: 1px;
}

/* ===== 表格区域 ===== */
.table-section {
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #e8edf4;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.table-section__header {
  padding: 14px 20px;
  border-bottom: 1px solid #f1f4f9;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-section__count {
  font-size: 14px;
  color: #64748b;
}

.table-wrap {
  overflow-x: auto;
  padding: 0 4px;
}

.table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.table thead th {
  padding: 12px 14px;
  text-align: left;
  font-weight: 600;
  color: #64748b;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  background: #f8fafc;
  border-bottom: 1px solid #e8edf4;
  white-space: nowrap;
}

.table tbody td {
  padding: 12px 14px;
  border-bottom: 1px solid #f1f4f9;
  color: #1e293b;
  vertical-align: middle;
}

.table tbody tr:hover {
  background: #f8fafc;
}

.table tbody tr:last-child td {
  border-bottom: none;
}

/* ===== 标签 ===== */
.tag {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.tag-code {
  background: #f1f4f9;
  color: #475569;
  font-family: monospace;
}

/* ===== 状态标签 ===== */
.status-tag {
  display: inline-block;
  padding: 3px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 500;
}

.status-tag.success {
  background: #e6f7e6;
  color: #16a34a;
}

.status-tag.danger {
  background: #fde8e8;
  color: #dc2626;
}

.status-tag.primary {
  background: #dbeafe;
  color: #2563eb;
}

.status-tag.warning {
  background: #fef3c7;
  color: #d97706;
}

.status-tag.info {
  background: #f1f5f9;
  color: #64748b;
}

/* ===== 操作按钮组 ===== */
.action-group {
  display: flex;
  gap: 4px;
}

.btn-action {
  padding: 4px 12px;
  border: none;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: transparent;
}

.btn-action--edit {
  color: #2563eb;
}

.btn-action--edit:hover {
  background: #eff6ff;
}

.btn-action--delete {
  color: #dc2626;
}

.btn-action--delete:hover {
  background: #fef2f2;
}

/* ===== 通用按钮 ===== */
.btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 18px;
  border: 1px solid transparent;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-sm {
  padding: 5px 14px;
  font-size: 13px;
}

.btn .icon {
  flex-shrink: 0;
}

.btn-primary {
  background: #2563eb;
  color: #fff;
}

.btn-primary:hover {
  background: #1d4ed8;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

.btn-ghost {
  background: transparent;
  border-color: #e2e8f0;
  color: #475569;
}

.btn-ghost:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
}

.btn-danger {
  background: #dc2626;
  color: #fff;
}

.btn-danger:hover {
  background: #b91c1c;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  box-shadow: none !important;
}

/* ===== 表单输入 ===== */
.form-input {
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  background: #ffffff;
  color: #1e293b;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  width: 100%;
}

.form-input:hover {
  border-color: #cbd5e1;
}

.form-input:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

textarea.form-input {
  resize: vertical;
  min-height: 80px;
  font-family: inherit;
}

/* ===== 下拉选择框 ===== */
.form-select {
  padding: 8px 36px 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  background: #ffffff url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%2364748b' d='M6 8L1 3h10z'/%3E%3C/svg%3E") no-repeat right 12px center;
  background-size: 12px;
  appearance: none;
  color: #1e293b;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  min-width: 140px;
}

.form-select:hover {
  border-color: #cbd5e1;
}

.form-select:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

/* ===== 单选框 ===== */
.radio-group {
  display: flex;
  gap: 20px;
  padding-top: 4px;
  flex-wrap: wrap;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  font-size: 14px;
  color: #475569;
}

.radio-label input[type="radio"] {
  width: 16px;
  height: 16px;
  accent-color: #2563eb;
  cursor: pointer;
}

/* ===== 分页 ===== */
.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px;
  border-top: 1px solid #f1f4f9;
  flex-wrap: wrap;
  gap: 12px;
}

.pagination__info {
  font-size: 14px;
  color: #94a3b8;
}

.pagination__controls {
  display: flex;
  gap: 6px;
  align-items: center;
}

.pagination__current {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 32px;
  height: 32px;
  padding: 0 8px;
  border-radius: 6px;
  background: #2563eb;
  color: #fff;
  font-size: 14px;
  font-weight: 600;
}

.pagination__total {
  color: #94a3b8;
  font-size: 14px;
  margin-left: 2px;
}

/* ===== 空状态 ===== */
.empty-state {
  text-align: center;
  padding: 60px 20px !important;
}

.empty-state__icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.empty-state p {
  margin: 0;
  color: #94a3b8;
  font-size: 14px;
}

/* ===== 弹窗 ===== */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(15, 23, 42, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease;
}

.dialog {
  background: #ffffff;
  border-radius: 16px;
  width: 720px;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 24px 80px rgba(0, 0, 0, 0.2);
  animation: slideUp 0.3s ease;
}

.dialog__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f1f4f9;
  flex-shrink: 0;
}

.dialog__title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.dialog__close {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  font-size: 18px;
  color: #94a3b8;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.dialog__close:hover {
  background: #f1f4f9;
  color: #475569;
}

.dialog__body {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

.dialog__footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 16px 24px;
  border-top: 1px solid #f1f4f9;
  flex-shrink: 0;
}

/* ===== 表单网格 ===== */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px 20px;
}

.form-grid .form-field.full-width {
  grid-column: 1 / -1;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: #475569;
}

.form-label.required::after {
  content: '*';
  color: #dc2626;
  margin-left: 2px;
}

.text-muted {
  color: #94a3b8;
  font-size: 13px;
}

/* ===== 动画 ===== */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.96);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .page-container {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .page-header__actions {
    width: 100%;
  }

  .page-header__actions select,
  .page-header__actions button {
    flex: 1;
    min-width: 0;
  }

  .search-row__item {
    flex: 1 1 100%;
    min-width: unset;
  }

  .search-row__actions {
    width: 100%;
  }

  .search-row__actions .btn {
    flex: 1;
    justify-content: center;
  }

  .dialog {
    width: 95%;
    max-height: 90vh;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .form-grid .form-field.full-width {
    grid-column: 1;
  }

  .pagination {
    flex-direction: column;
    align-items: center;
    gap: 8px;
  }

  .table-wrap {
    padding: 0;
  }

  .table thead th,
  .table tbody td {
    padding: 10px 12px;
    font-size: 13px;
  }
}
</style>
