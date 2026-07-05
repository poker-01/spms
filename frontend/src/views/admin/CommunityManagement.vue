<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// ============ API 接口 ============
const communityApi = {
  // 获取小区列表（分页）
  getList: (params: any) => {
    // return request.get('/api/v1/communities/page', { params })
    return Promise.resolve({ data: { list: [], total: 0 } })
  },
  // 获取小区下拉列表
  getOptions: () => {
    // return request.get('/api/v1/communities/list')
    return Promise.resolve({ data: [] })
  },
  // 新增小区
  create: (data: any) => {
    // return request.post('/api/v1/communities', data)
    return Promise.resolve({ data: {} })
  },
  // 更新小区
  update: (id: number, data: any) => {
    // return request.put(`/api/v1/communities/${id}`, data)
    return Promise.resolve({ data: {} })
  },
  // 删除小区
  delete: (id: number) => {
    // return request.delete(`/api/v1/communities/${id}`)
    return Promise.resolve({ data: {} })
  },
  // 小区详情
  getDetail: (id: number) => {
    // return request.get(`/api/v1/communities/${id}`)
    return Promise.resolve({ data: {} })
  }
}
// =========================================

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增小区')
const isEdit = ref(false)

const searchForm = reactive({
  communityName: '',
  province: '',
  city: '',
  district: ''
})

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref<any[]>([])

const form = reactive({
  id: null as number | null,
  communityCode: '',
  communityName: '',
  address: '',
  province: '',
  city: '',
  district: '',
  totalBuildings: 0,
  totalUnits: 0,
  propertyCompany: '',
  managerName: '',
  managerPhone: '',
  status: 1,
  remark: ''
})

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.current,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const res = await communityApi.getList(params)
    tableData.value = res.data.list || []
    pagination.total = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.communityName = ''
  searchForm.province = ''
  searchForm.city = ''
  searchForm.district = ''
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
  dialogTitle.value = '新增小区'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = async (row: any) => {
  isEdit.value = true
  dialogTitle.value = '编辑小区'
  try {
    const res = await communityApi.getDetail(row.id)
    Object.assign(form, res.data)
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载小区信息失败')
  }
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除小区「${row.communityName}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await communityApi.delete(row.id)
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
      await communityApi.update(form.id!, form)
      ElMessage.success('更新成功')
    } else {
      await communityApi.create(form)
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
  form.communityCode = ''
  form.communityName = ''
  form.address = ''
  form.province = ''
  form.city = ''
  form.district = ''
  form.totalBuildings = 0
  form.totalUnits = 0
  form.propertyCompany = ''
  form.managerName = ''
  form.managerPhone = ''
  form.status = 1
  form.remark = ''
}

const getStatusLabel = (status: number) => {
  return status === 1 ? '启用' : '停用'
}

const getStatusClass = (status: number) => {
  return status === 1 ? 'success' : 'danger'
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-header__left">
        <h2 class="page-title">小区管理</h2>
        <span class="page-subtitle">管理所有小区信息</span>
      </div>
      <button class="btn btn-primary" @click="handleAdd">
        <svg class="icon" viewBox="0 0 24 24" width="16" height="16"><path fill="currentColor" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/></svg>
        新增小区
      </button>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-section__inner">
        <div class="search-row">
          <div class="search-row__item">
            <label class="search-row__label">小区名称</label>
            <input v-model="searchForm.communityName" placeholder="请输入小区名称" class="form-input" />
          </div>
          <div class="search-row__item">
            <label class="search-row__label">省份</label>
            <input v-model="searchForm.province" placeholder="请输入省份" class="form-input" />
          </div>
          <div class="search-row__item">
            <label class="search-row__label">城市</label>
            <input v-model="searchForm.city" placeholder="请输入城市" class="form-input" />
          </div>
          <div class="search-row__item">
            <label class="search-row__label">区域</label>
            <input v-model="searchForm.district" placeholder="请输入区域" class="form-input" />
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
            <th style="width: 120px;">小区编码</th>
            <th style="min-width: 120px;">小区名称</th>
            <th style="min-width: 150px;">地址</th>
            <th style="width: 120px;">区域</th>
            <th style="width: 80px;">楼栋数</th>
            <th style="width: 80px;">单元数</th>
            <th style="min-width: 120px;">物业公司</th>
            <th style="width: 100px;">负责人</th>
            <th style="width: 80px;">状态</th>
            <th style="width: 160px;">创建时间</th>
            <th style="width: 150px;">操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="row in tableData" :key="row.id">
            <td>{{ row.id }}</td>
            <td><span class="tag tag-code">{{ row.communityCode }}</span></td>
            <td><strong>{{ row.communityName }}</strong></td>
            <td>{{ row.address }}</td>
            <td>{{ row.province }}{{ row.city }}{{ row.district }}</td>
            <td>{{ row.totalBuildings }}</td>
            <td>{{ row.totalUnits }}</td>
            <td>{{ row.propertyCompany }}</td>
            <td>{{ row.managerName }}</td>
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
            <td colspan="12" class="empty-state">
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
            <div class="form-field">
              <label class="form-label required">小区编码</label>
              <input v-model="form.communityCode" class="form-input" placeholder="请输入小区编码" />
            </div>
            <div class="form-field">
              <label class="form-label required">小区名称</label>
              <input v-model="form.communityName" class="form-input" placeholder="请输入小区名称" />
            </div>
            <div class="form-field full-width">
              <label class="form-label">地址</label>
              <input v-model="form.address" class="form-input" placeholder="请输入详细地址" />
            </div>
            <div class="form-field">
              <label class="form-label">省份</label>
              <input v-model="form.province" class="form-input" placeholder="请输入省份" />
            </div>
            <div class="form-field">
              <label class="form-label">城市</label>
              <input v-model="form.city" class="form-input" placeholder="请输入城市" />
            </div>
            <div class="form-field">
              <label class="form-label">区域</label>
              <input v-model="form.district" class="form-input" placeholder="请输入区域" />
            </div>
            <div class="form-field">
              <label class="form-label">楼栋数</label>
              <input v-model.number="form.totalBuildings" class="form-input" type="number" min="0" />
            </div>
            <div class="form-field">
              <label class="form-label">单元数</label>
              <input v-model.number="form.totalUnits" class="form-input" type="number" min="0" />
            </div>
            <div class="form-field">
              <label class="form-label">物业公司</label>
              <input v-model="form.propertyCompany" class="form-input" placeholder="请输入物业公司" />
            </div>
            <div class="form-field">
              <label class="form-label">负责人</label>
              <input v-model="form.managerName" class="form-input" placeholder="请输入负责人" />
            </div>
            <div class="form-field">
              <label class="form-label">负责人电话</label>
              <input v-model="form.managerPhone" class="form-input" placeholder="请输入负责人电话" />
            </div>
            <div class="form-field">
              <label class="form-label">状态</label>
              <div class="radio-group">
                <label class="radio-label">
                  <input type="radio" :value="1" v-model="form.status" /> 启用
                </label>
                <label class="radio-label">
                  <input type="radio" :value="0" v-model="form.status" /> 停用
                </label>
              </div>
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

/* ===== 单选框 ===== */
.radio-group {
  display: flex;
  gap: 20px;
  padding-top: 4px;
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
