<!-- src/views/admin/RepairDetail.vue -->
<template>
  <div class="detail-page">
    <!-- 返回按钮 -->
    <div class="detail-page__back">
      <button class="btn btn-ghost" type="button" @click="goBack">
        ← 返回报修列表
      </button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="detail-page__loading">加载中...</div>

    <!-- 详情内容 -->
    <div v-else-if="detail" class="detail-page__content">
      <!-- 头部：状态 + 订单号 -->
      <div class="detail-page__header">
        <div class="detail-page__header-left">
          <h2 class="detail-page__title">报修详情</h2>
          <span :class="['detail-page__status', getStatusClass(detail.status)]">
            {{ detail.statusName }}
          </span>
        </div>
        <span class="detail-page__order">订单号：{{ detail.orderNo }}</span>
      </div>

      <!-- 主体信息 -->
      <div class="detail-page__body">
        <!-- 报修信息 -->
        <div class="detail-card">
          <h3 class="detail-card__title">报修信息</h3>
          <div class="detail-card__grid">
            <div class="detail-card__item">
              <label>业主姓名</label>
              <span>{{ detail.ownerName || '-' }}</span>
            </div>
            <div class="detail-card__item">
              <label>联系电话</label>
              <span>{{ detail.repairPhone }}</span>
            </div>
            <div class="detail-card__item">
              <label>报修类型</label>
              <span>{{ detail.repairTypeName }}</span>
            </div>
            <div class="detail-card__item">
              <label>提交时间</label>
              <span>{{ formatDate(detail.createTime) }}</span>
            </div>
            <div v-if="detail.repairTime" class="detail-card__item">
              <label>维修时间</label>
              <span>{{ formatDate(detail.repairTime) }}</span>
            </div>
            <div v-if="detail.repairerName" class="detail-card__item">
              <label>维修人员</label>
              <span>{{ detail.repairerName }}</span>
            </div>
          </div>
          <div class="detail-card__desc">
            <label>问题描述</label>
            <p>{{ detail.repairDesc }}</p>
          </div>
        </div>

        <!-- 维修结果（已完成时显示） -->
        <div v-if="detail.status === 2" class="detail-card">
          <h3 class="detail-card__title">维修结果</h3>
          <div class="detail-card__grid">
            <div class="detail-card__item">
              <label>维修费用</label>
              <span class="detail-card__cost">¥{{ detail.repairCost?.toFixed(2) || '0.00' }}</span>
            </div>
            <div v-if="detail.evaluateScore" class="detail-card__item">
              <label>业主评价</label>
              <span class="detail-card__score">{{ '⭐'.repeat(detail.evaluateScore) }}</span>
            </div>
            <div v-if="detail.evaluateComment" class="detail-card__item detail-card__item--full">
              <label>评价内容</label>
              <span>{{ detail.evaluateComment }}</span>
            </div>
          </div>
        </div>

        <!-- 操作日志 -->
        <div v-if="detail.logs && detail.logs.length" class="detail-card">
          <h3 class="detail-card__title">操作日志</h3>
          <div class="log-list">
            <div v-for="(log, index) in detail.logs" :key="index" class="log-item">
              <span class="log-item__time">{{ formatDate(log.time) }}</span>
              <span class="log-item__content">{{ log.content }}</span>
              <span class="log-item__operator">{{ log.operator }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部操作按钮 -->
      <div class="detail-page__footer">
        <!-- 待处理 → 派单 -->
        <button
          v-if="detail.status === 0"
          class="btn btn-primary"
          type="button"
          @click="openAssignDialog"
        >
          派单
        </button>

        <!-- 处理中 → 完成 -->
        <button
          v-if="detail.status === 1"
          class="btn btn-success"
          type="button"
          @click="openCompleteDialog"
        >
          完成维修
        </button>

        <!-- 已完成 → 查看评价 -->
        <button
          v-if="detail.status === 2 && detail.evaluateScore"
          class="btn btn-ghost"
          type="button"
          @click="showEvaluateDetail = true"
        >
          查看评价
        </button>
      </div>
    </div>

    <!-- 派单弹窗 -->
    <div v-if="assignDialog.visible" class="dialog-overlay" @click.self="assignDialog.visible = false">
      <div class="dialog dialog--assign">
        <h3 class="dialog__title">派单</h3>
        <div class="dialog__body">
          <p class="dialog__info">
            报修单：<strong>{{ detail?.orderNo }}</strong>
          </p>
          <p class="dialog__info">
            描述：{{ detail?.repairDesc }}
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

    <!-- 完成维修弹窗 -->
    <div v-if="completeDialog.visible" class="dialog-overlay" @click.self="completeDialog.visible = false">
      <div class="dialog dialog--complete">
        <h3 class="dialog__title">完成维修</h3>
        <div class="dialog__body">
          <p class="dialog__info">
            报修单：<strong>{{ detail?.orderNo }}</strong>
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
            <label class="form-label">备注（选填）</label>
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
          <button class="btn btn-success" type="button" :disabled="completeDialog.submitting" @click="handleComplete">
            {{ completeDialog.submitting ? '提交中...' : '确认完成' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 评价详情弹窗 -->
    <div v-if="showEvaluateDetail" class="dialog-overlay" @click.self="showEvaluateDetail = false">
      <div class="dialog dialog--evaluate-detail">
        <h3 class="dialog__title">业主评价</h3>
        <div class="dialog__body">
          <div class="evaluate-detail__score">
            <span class="evaluate-detail__label">评分：</span>
            <span class="evaluate-detail__stars">{{ '⭐'.repeat(detail?.evaluateScore || 0) }}</span>
            <span class="evaluate-detail__score-num">{{ detail?.evaluateScore }} 分</span>
          </div>
          <div v-if="detail?.evaluateComment" class="evaluate-detail__comment">
            <span class="evaluate-detail__label">评价内容：</span>
            <p>{{ detail.evaluateComment }}</p>
          </div>
          <div v-else class="evaluate-detail__comment">
            <span class="evaluate-detail__label">评价内容：</span>
            <p class="evaluate-detail__empty">业主未填写评价内容</p>
          </div>
        </div>
        <div class="dialog__footer">
          <button class="btn btn-primary" type="button" @click="showEvaluateDetail = false">
            关闭
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { formatDate } from '@/utils/format'

defineOptions({
  name: 'AdminRepairDetail',
})

// ============================================================
// 类型定义
// ============================================================

interface RepairDetail {
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
  evaluateScore?: number
  evaluateComment?: string
  createTime: string
  repairTime?: string
  repairerName?: string
  logs?: Array<{
    time: string
    content: string
    operator: string
  }>
}

interface Repairer {
  id: number
  userName: string
  fullName?: string
}

// ============================================================
// 状态
// ============================================================

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref<RepairDetail | null>(null)
const repairers = ref<Repairer[]>([])
const showEvaluateDetail = ref(false)

// 派单弹窗
const assignDialog = reactive({
  visible: false,
  repairerId: 0,
  submitting: false,
})

// 完成弹窗
const completeDialog = reactive({
  visible: false,
  repairCost: 0,
  remark: '',
  submitting: false,
})

// ============================================================
// Mock 数据
// ============================================================

const mockDetail: RepairDetail = {
  id: 1,
  orderNo: 'BX20260705001',
  ownerName: '张三',
  repairType: 1,
  repairTypeName: '水电维修',
  repairDesc: '厨房水龙头漏水严重，需要更换新的水龙头，请尽快安排师傅上门维修。',
  repairPhone: '13800138001',
  status: 0,
  statusName: '待处理',
  createTime: '2026-07-05 09:30:00',
  logs: [
    { time: '2026-07-05 09:30:00', content: '业主提交报修申请', operator: '张三' },
  ],
}

const mockDetailProcessing: RepairDetail = {
  id: 2,
  orderNo: 'BX20260704002',
  ownerName: '李四',
  repairType: 3,
  repairTypeName: '家电维修',
  repairDesc: '空调不制冷，可能是缺氟，需要加氟并检查管路。',
  repairPhone: '13800138002',
  status: 1,
  statusName: '处理中',
  createTime: '2026-07-04 14:20:00',
  repairTime: '2026-07-05 08:00:00',
  repairerName: '张师傅',
  logs: [
    { time: '2026-07-04 14:20:00', content: '业主提交报修申请', operator: '李四' },
    { time: '2026-07-05 08:30:00', content: '派单给维修师傅：张师傅', operator: '管理员' },
    { time: '2026-07-05 09:00:00', content: '维修师傅接单，正在前往', operator: '张师傅' },
  ],
}

const mockDetailCompleted: RepairDetail = {
  id: 3,
  orderNo: 'BX20260703003',
  ownerName: '王五',
  repairType: 2,
  repairTypeName: '家具维修',
  repairDesc: '卧室衣柜门铰链松动，需要加固或更换铰链。',
  repairPhone: '13800138003',
  status: 2,
  statusName: '已完成',
  createTime: '2026-07-03 10:15:00',
  repairTime: '2026-07-04 16:30:00',
  repairCost: 80,
  evaluateScore: 5,
  evaluateComment: '师傅技术很好，处理很快，服务态度也很好！',
  repairerName: '李师傅',
  logs: [
    { time: '2026-07-03 10:15:00', content: '业主提交报修申请', operator: '王五' },
    { time: '2026-07-03 14:00:00', content: '派单给维修师傅：李师傅', operator: '管理员' },
    { time: '2026-07-04 09:00:00', content: '维修师傅开始维修', operator: '李师傅' },
    { time: '2026-07-04 16:30:00', content: '维修完成，费用 80.00 元', operator: '管理员' },
    { time: '2026-07-05 10:00:00', content: '业主评价：5星 - 师傅技术很好', operator: '王五' },
  ],
}

// ============================================================
// 方法
// ============================================================

/**
 * 加载报修详情
 * 接口：GET /api/v1/admin/repairs/{id}
 */
const loadDetail = async () => {
  const id = Number(route.params.id)
  if (!id) {
    alert('参数错误')
    router.back()
    return
  }

  loading.value = true
  try {
    // 接口：GET /api/v1/admin/repairs/{id}
    // const { data } = await getAdminRepairDetail(id)
    // detail.value = data

    // 临时Mock（后端接口完成后删除）
    await new Promise((resolve) => setTimeout(resolve, 300))
    // 根据id返回不同的mock数据
    if (id === 1) {
      detail.value = mockDetail
    } else if (id === 2) {
      detail.value = mockDetailProcessing
    } else if (id === 3) {
      detail.value = mockDetailCompleted
    } else {
      detail.value = { ...mockDetail, id }
    }
  } catch (error) {
    console.error('加载报修详情失败:', error)
    alert('加载失败，请稍后重试')
    router.back()
  } finally {
    loading.value = false
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
 * 返回列表
 */
const goBack = () => {
  router.push('/admin/repairs')
}

/**
 * 打开派单弹窗
 */
const openAssignDialog = () => {
  assignDialog.repairerId = 0
  assignDialog.visible = true
}

/**
 * 派单
 * 接口：PUT /api/v1/admin/repairs/{id}/assign
 */
const handleAssign = async () => {
  if (!detail.value) return
  if (!assignDialog.repairerId) {
    alert('请选择维修人员')
    return
  }

  assignDialog.submitting = true
  try {
    // 接口：PUT /api/v1/admin/repairs/{id}/assign
    // 请求体：{ repairerId }
    // await assignRepair(detail.value.id, {
    //   repairerId: assignDialog.repairerId,
    // })

    // 临时Mock（后端接口完成后删除）
    await new Promise((resolve) => setTimeout(resolve, 800))

    const repairer = repairers.value.find((r) => r.id === assignDialog.repairerId)
    detail.value.status = 1
    detail.value.statusName = '处理中'
    detail.value.repairerName = repairer?.fullName || repairer?.userName || '维修师傅'
    detail.value.logs?.push({
      time: new Date().toISOString().replace('T', ' ').slice(0, 19),
      content: `派单给维修师傅：${detail.value.repairerName}`,
      operator: '管理员',
    })

    assignDialog.visible = false
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
const openCompleteDialog = () => {
  completeDialog.repairCost = 0
  completeDialog.remark = ''
  completeDialog.visible = true
}

/**
 * 完成报修
 * 接口：PUT /api/v1/admin/repairs/{id}/complete
 */
const handleComplete = async () => {
  if (!detail.value) return
  if (!completeDialog.repairCost || completeDialog.repairCost < 0) {
    alert('请输入有效的维修费用')
    return
  }

  completeDialog.submitting = true
  try {
    // 接口：PUT /api/v1/admin/repairs/{id}/complete
    // 请求体：{ repairCost, remark? }
    // await completeRepair(detail.value.id, {
    //   repairCost: completeDialog.repairCost,
    //   remark: completeDialog.remark,
    // })

    // 临时Mock（后端接口完成后删除）
    await new Promise((resolve) => setTimeout(resolve, 800))

    detail.value.status = 2
    detail.value.statusName = '已完成'
    detail.value.repairCost = completeDialog.repairCost
    detail.value.repairTime = new Date().toISOString().replace('T', ' ').slice(0, 19)
    detail.value.logs?.push({
      time: detail.value.repairTime,
      content: `维修完成，费用 ${completeDialog.repairCost.toFixed(2)} 元${completeDialog.remark ? '，备注：' + completeDialog.remark : ''}`,
      operator: '管理员',
    })

    completeDialog.visible = false
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
  await loadRepairers()
  await loadDetail()
})
</script>

<style scoped>
.detail-page {
  max-width: 900px;
  margin: 0 auto;
}

.detail-page__back {
  margin-bottom: 20px;
}

.detail-page__loading {
  padding: 60px 20px;
  text-align: center;
  color: var(--color-text-secondary);
}

.detail-page__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 24px;
  padding: 20px 24px;
  border-radius: var(--radius-lg);
  background: var(--color-card);
  border: 1px solid var(--color-border);
}

.detail-page__header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.detail-page__title {
  margin: 0;
  font-size: 22px;
}

.detail-page__status {
  display: inline-flex;
  padding: 4px 14px;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 600;
}

.detail-page__order {
  color: var(--color-text-secondary);
  font-size: 14px;
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

.detail-page__body {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-card {
  padding: 24px;
  border-radius: var(--radius-lg);
  background: var(--color-card);
  border: 1px solid var(--color-border);
}

.detail-card__title {
  margin: 0 0 16px;
  font-size: 16px;
}

.detail-card__grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px 24px;
}

.detail-card__item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-card__item--full {
  grid-column: 1 / -1;
}

.detail-card__item label {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.detail-card__item span {
  font-weight: 500;
}

.detail-card__cost {
  color: var(--color-primary);
  font-size: 18px;
  font-weight: 700;
}

.detail-card__score {
  font-size: 18px;
}

.detail-card__desc {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--color-border);
}

.detail-card__desc label {
  display: block;
  margin-bottom: 8px;
  font-size: 13px;
  color: var(--color-text-secondary);
}

.detail-card__desc p {
  margin: 0;
  padding: 12px 16px;
  border-radius: var(--radius-sm);
  background: var(--color-bg);
  line-height: 1.8;
}

.detail-page__footer {
  margin-top: 20px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

/* 弹窗样式 */
.dialog--assign,
.dialog--complete,
.dialog--evaluate-detail {
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

/* 操作日志 */
.log-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.log-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px 12px;
  border-radius: var(--radius-sm);
  background: var(--color-bg);
  font-size: 14px;
}

.log-item__time {
  color: var(--color-text-secondary);
  font-size: 13px;
  min-width: 160px;
}

.log-item__content {
  flex: 1;
}

.log-item__operator {
  color: var(--color-text-secondary);
  font-size: 13px;
  background: var(--color-card);
  padding: 2px 10px;
  border-radius: 999px;
}

/* 评价详情 */
.evaluate-detail__score {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 0;
}

.evaluate-detail__label {
  color: var(--color-text-secondary);
  font-size: 14px;
}

.evaluate-detail__stars {
  font-size: 24px;
}

.evaluate-detail__score-num {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-primary);
}

.evaluate-detail__comment {
  padding: 12px 0;
  border-top: 1px solid var(--color-border);
}

.evaluate-detail__comment p {
  margin: 8px 0 0;
  padding: 12px 16px;
  border-radius: var(--radius-sm);
  background: var(--color-bg);
  line-height: 1.8;
}

.evaluate-detail__empty {
  color: var(--color-text-secondary);
}

.form-required {
  color: #dc2626;
}

/* 响应式 */
@media (max-width: 768px) {
  .detail-card__grid {
    grid-template-columns: 1fr;
  }

  .detail-page__header {
    flex-direction: column;
    align-items: flex-start;
  }

  .log-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .log-item__time {
    min-width: auto;
  }

  .detail-page__footer {
    flex-direction: column;
  }

  .detail-page__footer .btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
