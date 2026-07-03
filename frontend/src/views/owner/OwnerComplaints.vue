<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { applyComplaint, getOwnerComplaints } from '@/api/owner'
import type { OwnerComplaint } from '@/utils/api-types'

defineOptions({ name: 'OwnerComplaints' })

const complaints = ref<OwnerComplaint[]>([])
const loading = ref(false)
const showForm = ref(false)
const submitting = ref(false)
const errorMsg = ref('')

const form = ref({
  type: 1,
  title: '',
  content: '',
  contactPhone: '',
})

const typeMap: Record<number, string> = {
  1: '投诉',
  2: '建议',
}

const statusMap: Record<number, { label: string; cls: string }> = {
  0: { label: '待处理', cls: 'status--pending' },
  1: { label: '已回复', cls: 'status--replied' },
  2: { label: '已关闭', cls: 'status--closed' },
}

const fetchComplaints = async () => {
  loading.value = true
  try {
    const { data } = await getOwnerComplaints()
    complaints.value = data ?? []
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!form.value.title.trim()) {
    errorMsg.value = '请填写标题'
    return
  }
  if (!form.value.content.trim()) {
    errorMsg.value = '请填写内容'
    return
  }
  submitting.value = true
  errorMsg.value = ''
  try {
    await applyComplaint({
      type: form.value.type,
      title: form.value.title,
      content: form.value.content,
      contactPhone: form.value.contactPhone,
    })
    form.value = { type: 1, title: '', content: '', contactPhone: '' }
    showForm.value = false
    await fetchComplaints()
  } catch (e: any) {
    errorMsg.value = e.message || '提交失败'
  } finally {
    submitting.value = false
  }
}

onMounted(fetchComplaints)
</script>

<template>
  <div class="owner-page">
    <div class="owner-page__header">
      <h2 class="owner-page__title">投诉建议</h2>
      <button class="btn btn-primary btn-sm" type="button" @click="showForm = !showForm">
        {{ showForm ? '取消' : '+ 提交投诉建议' }}
      </button>
    </div>

    <!-- 投诉建议表单 -->
    <div v-if="showForm" class="owner-page__form">
      <div v-if="errorMsg" class="error-message">{{ errorMsg }}</div>
      <div class="form-field">
        <label class="form-label">类型</label>
        <select v-model="form.type" class="form-input">
          <option :value="1">投诉</option>
          <option :value="2">建议</option>
        </select>
      </div>
      <div class="form-field">
        <label class="form-label">标题</label>
        <input v-model="form.title" class="form-input" placeholder="请输入标题" />
      </div>
      <div class="form-field">
        <label class="form-label">内容</label>
        <textarea
          v-model="form.content"
          class="form-input"
          rows="4"
          placeholder="请详细描述您的投诉或建议..."
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

    <div v-else-if="complaints.length === 0" class="owner-page__empty">
      <p>暂无投诉建议记录</p>
    </div>

    <div v-else class="owner-page__table-wrap">
      <table class="owner-page__table">
        <thead>
          <tr>
            <th>编号</th>
            <th>类型</th>
            <th>标题</th>
            <th>状态</th>
            <th>回复</th>
            <th>创建时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in complaints" :key="item.id">
            <td>{{ item.complaintNo }}</td>
            <td>{{ typeMap[item.type] || '未知' }}</td>
            <td class="owner-page__title-cell">{{ item.title }}</td>
            <td>
              <span :class="statusMap[item.status]?.cls || 'status--pending'">
                {{ statusMap[item.status]?.label || '未知' }}
              </span>
            </td>
            <td class="owner-page__reply-cell">
              <span v-if="item.replyContent">{{ item.replyContent }}</span>
              <span v-else class="owner-page__no-reply">暂无回复</span>
            </td>
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

.owner-page__title-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.owner-page__reply-cell {
  max-width: 200px;
}

.owner-page__no-reply {
  color: var(--color-text-secondary);
  font-size: 13px;
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

.status--replied {
  display: inline-flex;
  padding: 4px 10px;
  border-radius: 999px;
  background: #dcfce7;
  color: #15803d;
  font-size: 12px;
  font-weight: 600;
}

.status--closed {
  display: inline-flex;
  padding: 4px 10px;
  border-radius: 999px;
  background: #f1f5f9;
  color: var(--color-text-secondary);
  font-size: 12px;
  font-weight: 600;
}
</style>
