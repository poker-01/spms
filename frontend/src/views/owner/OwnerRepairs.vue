<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { applyRepair, getOwnerRepairs } from '@/api/owner'
import type { OwnerRepair } from '@/utils/api-types'

defineOptions({ name: 'OwnerRepairs' })

const repairs = ref<OwnerRepair[]>([])
const loading = ref(false)
const showForm = ref(false)
const submitting = ref(false)
const errorMsg = ref('')

const form = ref({
  content: '',
  contactPhone: '',
})

const statusMap: Record<number, { label: string; cls: string }> = {
  0: { label: '待处理', cls: 'status--pending' },
  1: { label: '处理中', cls: 'status--processing' },
  2: { label: '已完成', cls: 'status--done' },
  3: { label: '已评价', cls: 'status--evaluated' },
}

const fetchRepairs = async () => {
  loading.value = true
  try {
    const { data } = await getOwnerRepairs()
    repairs.value = data ?? []
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!form.value.content.trim()) {
    errorMsg.value = '请填写报修内容'
    return
  }
  submitting.value = true
  errorMsg.value = ''
  try {
    await applyRepair({ content: form.value.content, contactPhone: form.value.contactPhone })
    form.value = { content: '', contactPhone: '' }
    showForm.value = false
    await fetchRepairs()
  } catch (e: any) {
    errorMsg.value = e.message || '提交失败'
  } finally {
    submitting.value = false
  }
}

onMounted(fetchRepairs)
</script>

<template>
  <div class="owner-page">
    <div class="owner-page__header">
      <h2 class="owner-page__title">报修服务</h2>
      <button class="btn btn-primary btn-sm" type="button" @click="showForm = !showForm">
        {{ showForm ? '取消' : '+ 提交报修' }}
      </button>
    </div>

    <!-- 报修表单 -->
    <div v-if="showForm" class="owner-page__form">
      <div v-if="errorMsg" class="error-message">{{ errorMsg }}</div>
      <div class="form-field">
        <label class="form-label">报修内容</label>
        <textarea
          v-model="form.content"
          class="form-input"
          rows="3"
          placeholder="请描述您的报修问题..."
        ></textarea>
      </div>
      <div class="form-field">
        <label class="form-label">联系电话</label>
        <input v-model="form.contactPhone" class="form-input" placeholder="请输入联系电话" />
      </div>
      <button
        class="btn btn-primary"
        type="button"
        :disabled="submitting"
        @click="handleSubmit"
      >
        {{ submitting ? '提交中...' : '提交' }}
      </button>
    </div>

    <div v-if="loading" class="owner-page__loading">加载中...</div>

    <div v-else-if="repairs.length === 0" class="owner-page__empty">
      <p>暂无报修记录</p>
    </div>

    <div v-else class="owner-page__table-wrap">
      <table class="owner-page__table">
        <thead>
          <tr>
            <th>工单号</th>
            <th>报修内容</th>
            <th>联系电话</th>
            <th>状态</th>
            <th>费用</th>
            <th>创建时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in repairs" :key="item.id">
            <td>{{ item.orderNo }}</td>
            <td class="owner-page__desc-cell">{{ item.repairDesc }}</td>
            <td>{{ item.repairPhone || '-' }}</td>
            <td>
              <span :class="statusMap[item.status]?.cls || 'status--pending'">
                {{ statusMap[item.status]?.label || '未知' }}
              </span>
            </td>
            <td>{{ item.repairCost != null ? '¥' + item.repairCost.toFixed(2) : '-' }}</td>
            <td>{{ item.createTime || '-' }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.owner-page__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.owner-page__form {
  padding: 20px;
  margin-bottom: 20px;
  border-radius: var(--radius-md);
  background: var(--color-card);
  border: 1px solid var(--color-border);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.owner-page__desc-cell {
  max-width: 260px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status--pending {
  display: inline-flex;
  padding: 4px 10px;
  border-radius: 999px;
  background: #fef3c7;
  color: #b45309;
  font-size: 12px;
  font-weight: 600;
}

.status--processing {
  display: inline-flex;
  padding: 4px 10px;
  border-radius: 999px;
  background: #dbeafe;
  color: #1d4ed8;
  font-size: 12px;
  font-weight: 600;
}

.status--done {
  display: inline-flex;
  padding: 4px 10px;
  border-radius: 999px;
  background: #dcfce7;
  color: #15803d;
  font-size: 12px;
  font-weight: 600;
}

.status--evaluated {
  display: inline-flex;
  padding: 4px 10px;
  border-radius: 999px;
  background: #f3e8ff;
  color: #7c3aed;
  font-size: 12px;
  font-weight: 600;
}
</style>
