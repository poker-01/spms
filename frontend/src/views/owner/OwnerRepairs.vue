<template>
  <div class="owner-page">
    <div class="owner-page__header">
      <h2 class="owner-page__title">报修服务</h2>
      <button class="btn btn-primary" type="button" @click="dialogVisible = true">
        + 提交报修
      </button>
    </div>

    <!-- 搜索栏 -->
    <div class="owner-page__filters">
      <div class="owner-page__filters-row">
        <input
          v-model="query.keyword"
          class="form-input"
          placeholder="搜索订单号/描述"
          @keyup.enter="loadRepairs"
        />
        <select v-model="query.status" class="form-input">
          <option :value="undefined">全部状态</option>
          <option v-for="item in statuses" :key="item.value" :value="item.value">
            {{ item.label }}
          </option>
        </select>
        <button class="btn btn-primary" type="button" @click="loadRepairs">查询</button>
        <button class="btn btn-ghost" type="button" @click="handleReset">重置</button>
      </div>
    </div>

    <!-- 列表 -->
    <div v-if="loading" class="owner-page__loading">加载中...</div>
    <div v-else-if="!repairList.length" class="owner-page__empty">
      <p>暂无报修记录</p>
    </div>
    <div v-else class="owner-page__list">
      <article
        v-for="item in repairList"
        :key="item.id"
        class="repair-card"
        @click="handleViewDetail(item)"
      >
        <div class="repair-card__header">
          <div class="repair-card__left">
            <span class="repair-card__order">{{ item.orderNo }}</span>
            <span :class="['repair-card__status', getStatusClass(item.status)]">
              {{ item.statusName }}
            </span>
          </div>
          <span class="repair-card__type">{{ item.repairTypeName }}</span>
        </div>

        <div class="repair-card__body">
          <p class="repair-card__desc">{{ item.repairDesc }}</p>
          <div class="repair-card__meta">
            <span>联系电话：{{ item.repairPhone }}</span>
            <span>提交时间：{{ formatDate(item.createTime) }}</span>
            <span v-if="item.repairTime">维修时间：{{ formatDate(item.repairTime) }}</span>
          </div>
          <div v-if="item.status === 2 && item.repairCost" class="repair-card__cost">
            费用：¥{{ item.repairCost.toFixed(2) }}
            <span v-if="item.evaluateScore" class="repair-card__score">
              评分：{{ '⭐'.repeat(item.evaluateScore) }}
            </span>
          </div>
        </div>

        <div class="repair-card__footer" @click.stop>
          <button
            v-if="item.status === 0"
            class="btn btn-sm btn-danger"
            type="button"
            @click="handleCancel(item)"
          >
            取消报修
          </button>
          <button
            v-if="item.status === 2 && !item.evaluateScore"
            class="btn btn-sm btn-primary"
            type="button"
            @click="handleViewDetail(item)"
          >
            去评价
          </button>
        </div>
      </article>
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="owner-page__pagination">
      <button
        class="btn btn-sm btn-ghost"
        :disabled="(query.pageNum ?? 1) <= 1"
        @click="changePage((query.pageNum ?? 1) - 1)"
      >
        上一页
      </button>
      <span class="pagination-info">
        第 {{ query.pageNum ?? 1 }} / {{ totalPages }} 页，共 {{ total }} 条
      </span>
      <button
        class="btn btn-sm btn-ghost"
        :disabled="(query.pageNum ?? 1) >= totalPages"
        @click="changePage((query.pageNum ?? 1) + 1)"
      >
        下一页
      </button>
    </div>

    <!-- 提交报修弹窗 -->
    <div v-if="dialogVisible" class="dialog-overlay" @click.self="dialogVisible = false">
      <div class="dialog dialog--repair">
        <h3 class="dialog__title">提交报修</h3>
        <form class="dialog__body" @submit.prevent="handleSubmit">
          <div class="form-field">
            <label class="form-label">报修类型 <span class="form-required">*</span></label>
            <select v-model="form.repairType" class="form-input">
              <option :value="0">请选择报修类型</option>
              <option v-for="item in repairTypes" :key="item.value" :value="item.value">
                {{ item.label }}
              </option>
            </select>
          </div>

          <div class="form-field">
            <label class="form-label">报修描述 <span class="form-required">*</span></label>
            <textarea
              v-model="form.repairDesc"
              class="form-input"
              rows="4"
              placeholder="请详细描述需要维修的问题"
            />
          </div>

          <div class="form-field">
            <label class="form-label">联系电话 <span class="form-required">*</span></label>
            <input
              v-model="form.repairPhone"
              class="form-input"
              type="tel"
              placeholder="请填写联系电话"
            />
          </div>

          <div class="dialog__footer">
            <button class="btn btn-ghost" type="button" @click="dialogVisible = false">
              取消
            </button>
            <button class="btn btn-primary" type="submit" :disabled="submitting">
              {{ submitting ? '提交中...' : '提交报修' }}
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
import { getRepairPage, applyRepair, cancelRepair } from '@/api/repair'
import type { RepairVO, RepairQuery } from '@/api/repair'

defineOptions({
  name: 'OwnerRepairs',
})

// ============================================================
// 状态
// ============================================================

const router = useRouter()
const loading = ref(false)
const repairList = ref<RepairVO[]>([])
const dialogVisible = ref(false)
const submitting = ref(false)
const total = ref(0)
const totalPages = ref(1)

// 报修类型（前端写死）
const repairTypes = [
  { value: 1, label: '水电维修' },
  { value: 2, label: '家具维修' },
  { value: 3, label: '家电维修' },
  { value: 4, label: '其他' },
]

// 报修状态（前端写死）
const statuses = [
  { value: 0, label: '待处理' },
  { value: 1, label: '处理中' },
  { value: 2, label: '已完成' },
  { value: 3, label: '已取消' },
]

// 查询参数
const query = reactive<RepairQuery>({
  status: undefined,
  keyword: '',
  pageNum: 1,
  pageSize: 10,
})

// 提交表单
const form = reactive({
  repairType: 0,
  repairDesc: '',
  repairPhone: '',
})

// ============================================================
// 辅助方法
// ============================================================

/** 获取当前页码（带默认值） */
const getCurrentPage = (): number => {
  return query.pageNum ?? 1
}

/** 获取每页大小（带默认值） */
const getPageSize = (): number => {
  return query.pageSize ?? 10
}

// ============================================================
// 方法
// ============================================================

/**
 * 加载报修列表
 */
const loadRepairs = async () => {
  loading.value = true
  try {
    const { data } = await getRepairPage({
      status: query.status,
      keyword: query.keyword,
      pageNum: getCurrentPage(),
      pageSize: getPageSize(),
    })
    repairList.value = data.records
    total.value = data.total
    totalPages.value = data.pages
  } catch (error) {
    console.error('加载报修列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 提交报修
 */
const handleSubmit = async () => {
  if (!form.repairDesc.trim()) {
    alert('请填写报修描述')
    return
  }
  if (!form.repairPhone.trim()) {
    alert('请填写联系电话')
    return
  }
  if (!form.repairType) {
    alert('请选择报修类型')
    return
  }

  submitting.value = true
  try {
    // 后端期望的字段是 content 和 contactPhone
    await applyRepair({
      content: form.repairDesc.trim(),
      contactPhone: form.repairPhone.trim(),
    })
    dialogVisible.value = false
    form.repairType = 0
    form.repairDesc = ''
    form.repairPhone = ''
    await loadRepairs()
    alert('报修提交成功！')
  } catch (error) {
    console.error('提交报修失败:', error)
    alert('提交失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

/**
 * 取消报修
 */
const handleCancel = async (item: RepairVO) => {
  if (!confirm('确定要取消该报修单吗？')) return

  try {
    await cancelRepair(item.id)
    await loadRepairs()
    alert('已取消报修')
  } catch (error) {
    console.error('取消报修失败:', error)
    alert('取消失败，请稍后重试')
  }
}

/**
 * 查看详情
 */
const handleViewDetail = (item: RepairVO) => {
  router.push(`/owner/repairs/${item.id}`)
}

/**
 * 切换页码
 */
const changePage = (page: number) => {
  if (page < 1 || page > totalPages.value) return
  query.pageNum = page
  loadRepairs()
}

/**
 * 重置搜索
 */
const handleReset = () => {
  query.status = undefined
  query.keyword = ''
  query.pageNum = 1
  loadRepairs()
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
  await loadRepairs()
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

.owner-page__pagination {
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

.repair-card {
  padding: 20px;
  border-radius: var(--radius-md);
  background: var(--color-card);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: box-shadow 0.2s;
}

.repair-card:hover {
  box-shadow: var(--shadow-md);
}

.repair-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.repair-card__left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.repair-card__order {
  font-weight: 600;
  font-size: 15px;
}

.repair-card__status {
  display: inline-flex;
  padding: 2px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.repair-card__type {
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

.repair-card__body {
  margin-bottom: 12px;
}

.repair-card__desc {
  margin: 0 0 8px;
  font-size: 15px;
  line-height: 1.6;
}

.repair-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 13px;
  color: var(--color-text-secondary);
}

.repair-card__cost {
  margin-top: 8px;
  font-weight: 600;
  color: var(--color-primary);
}

.repair-card__score {
  margin-left: 12px;
  color: #d97706;
  font-weight: 500;
}

.repair-card__footer {
  display: flex;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid var(--color-border);
}

.dialog--repair {
  width: min(540px, calc(100% - 32px));
}

.form-required {
  color: #dc2626;
}

@media (max-width: 768px) {
  .owner-page__filters-row .form-input {
    width: 100%;
  }

  .repair-card__header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .repair-card__meta {
    flex-direction: column;
    gap: 4px;
  }
}
</style>
