<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getOwnerBills } from '@/api/owner'
import type { OwnerBill } from '@/utils/api-types'

defineOptions({ name: 'OwnerBills' })

const bills = ref<OwnerBill[]>([])
const loading = ref(false)

const statusMap: Record<number, { label: string; cls: string }> = {
  0: { label: '待缴费', cls: 'status--pending' },
  1: { label: '已缴费', cls: 'status--paid' },
  2: { label: '已过期', cls: 'status--expired' },
}

const fetchBills = async () => {
  loading.value = true
  try {
    const { data } = await getOwnerBills()
    bills.value = data ?? []
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

onMounted(fetchBills)
</script>

<template>
  <div class="owner-page">
    <h2 class="owner-page__title">我的账单</h2>

    <div v-if="loading" class="owner-page__loading">加载中...</div>

    <div v-else-if="bills.length === 0" class="owner-page__empty">
      <p>暂无账单记录</p>
    </div>

    <div v-else class="owner-page__table-wrap">
      <table class="owner-page__table">
        <thead>
          <tr>
            <th>账单编号</th>
            <th>账期</th>
            <th>金额</th>
            <th>截止日期</th>
            <th>状态</th>
            <th>创建时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="bill in bills" :key="bill.id">
            <td>{{ bill.billNo }}</td>
            <td>{{ bill.itemName }}</td>
            <td class="owner-page__amount">¥{{ bill.amount?.toFixed(2) ?? '0.00' }}</td>
            <td>{{ bill.deadline || '-' }}</td>
            <td>
              <span :class="statusMap[bill.status]?.cls || 'status--pending'">
                {{ statusMap[bill.status]?.label || '未知' }}
              </span>
            </td>
            <td>{{ bill.createTime || '-' }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.owner-page__amount {
  font-weight: 600;
  color: #dc2626;
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

.status--paid {
  display: inline-flex;
  padding: 4px 10px;
  border-radius: 999px;
  background: #dcfce7;
  color: #15803d;
  font-size: 12px;
  font-weight: 600;
}

.status--expired {
  display: inline-flex;
  padding: 4px 10px;
  border-radius: 999px;
  background: #fee2e2;
  color: #b91c1c;
  font-size: 12px;
  font-weight: 600;
}
</style>
