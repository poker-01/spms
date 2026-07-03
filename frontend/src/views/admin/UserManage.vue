<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { deleteUser, pageUsers, resetUserPassword, saveUser, toggleUserStatus, updateUser } from '@/api/user'
import type { PageResult, UserItem, UserSave, UserStatus, UserUpdate } from '@/utils/api-types'
import { formatDate } from '@/utils/format'

defineOptions({
  name: 'UserManage',
})

const loading = ref(false)
const query = reactive({
  userName: '',
  fullName: '',
  status: undefined as number | undefined,
  pageNum: 1,
  pageSize: 10,
})
const pageData = ref<PageResult<UserItem> | null>(null)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive<UserSave | UserUpdate>({
  userName: '',
  password: '',
  fullName: '',
  phoneNumber: '',
  email: '',
  status: 1,
} as UserSave)

const loadData = async () => {
  loading.value = true
  try {
    const { data } = await pageUsers(query)
    pageData.value = data
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  query.pageNum = 1
  loadData()
}

const handleReset = () => {
  query.userName = ''
  query.fullName = ''
  query.status = undefined
  handleSearch()
}

const openAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    userName: '',
    password: '',
    fullName: '',
    phoneNumber: '',
    email: '',
    status: 1,
  })
  dialogVisible.value = true
}

const openEdit = (row: UserItem) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    fullName: row.fullName,
    phoneNumber: row.phoneNumber,
    email: row.email,
    status: row.status,
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (isEdit.value) {
    await updateUser(form as UserUpdate)
  } else {
    await saveUser(form as UserSave)
  }
  dialogVisible.value = false
  loadData()
}

const handleToggleStatus = async (row: UserItem) => {
  const data: UserStatus = { id: row.id, status: row.status === 1 ? 0 : 1 }
  await toggleUserStatus(data)
  loadData()
}

const handleResetPassword = async (row: UserItem) => {
  if (confirm(`确定重置用户 ${row.userName} 的密码为 123456 吗？`)) {
    await resetUserPassword(row.id)
  }
}

const handleDelete = async (row: UserItem) => {
  if (confirm(`确定删除用户 ${row.userName} 吗？`)) {
    await deleteUser(row.id)
    loadData()
  }
}

onMounted(loadData)
</script>

<template>
  <div class="manage-page">
    <h2 class="manage-page__title">用户管理</h2>

    <div class="manage-page__toolbar">
      <div class="manage-page__filters">
        <input v-model="query.userName" class="form-input" placeholder="用户名" />
        <input v-model="query.fullName" class="form-input" placeholder="真实姓名" />
        <select v-model="query.status" class="form-input">
          <option :value="undefined">全部状态</option>
          <option :value="1">启用</option>
          <option :value="0">禁用</option>
        </select>
        <button class="btn btn-primary" type="button" @click="handleSearch">查询</button>
        <button class="btn btn-ghost" type="button" @click="handleReset">重置</button>
      </div>
      <button v-permission="'system:user:add'" class="btn btn-primary" type="button" @click="openAdd">
        新增用户
      </button>
    </div>

    <div class="manage-page__table-wrap">
      <table class="manage-page__table">
        <thead>
          <tr>
            <th>用户名</th>
            <th>真实姓名</th>
            <th>手机号</th>
            <th>邮箱</th>
            <th>角色</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in pageData?.records" :key="row.id">
            <td>{{ row.userName }}</td>
            <td>{{ row.fullName || '-' }}</td>
            <td>{{ row.phoneNumber || '-' }}</td>
            <td>{{ row.email || '-' }}</td>
            <td>{{ row.roleNames?.join('、') || '-' }}</td>
            <td>
              <span :class="row.status === 1 ? 'status--enabled' : 'status--disabled'">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </span>
            </td>
            <td>{{ formatDate(row.createTime) }}</td>
            <td>
              <button
                v-permission="'system:user:edit'"
                class="btn btn-sm btn-ghost"
                type="button"
                @click="openEdit(row)"
              >
                编辑
              </button>
              <button
                v-permission="'system:user:edit'"
                class="btn btn-sm btn-ghost"
                type="button"
                @click="handleToggleStatus(row)"
              >
                {{ row.status === 1 ? '禁用' : '启用' }}
              </button>
              <button
                v-permission="'system:user:edit'"
                class="btn btn-sm btn-ghost"
                type="button"
                @click="handleResetPassword(row)"
              >
                重置密码
              </button>
              <button
                v-permission="'system:user:delete'"
                class="btn btn-sm btn-danger"
                type="button"
                @click="handleDelete(row)"
              >
                删除
              </button>
            </td>
          </tr>
          <tr v-if="!pageData?.records?.length">
            <td colspan="8" class="manage-page__empty">暂无数据</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="dialogVisible" class="dialog-overlay" @click.self="dialogVisible = false">
      <div class="dialog">
        <h3 class="dialog__title">{{ isEdit ? '编辑用户' : '新增用户' }}</h3>
        <div class="dialog__body">
          <div class="form-field">
            <label class="form-label">用户名</label>
            <input v-model="(form as UserSave).userName" class="form-input" :disabled="isEdit" />
          </div>
          <div v-if="!isEdit" class="form-field">
            <label class="form-label">密码</label>
            <input v-model="(form as UserSave).password" class="form-input" type="password" />
          </div>
          <div class="form-field">
            <label class="form-label">真实姓名</label>
            <input v-model="form.fullName" class="form-input" />
          </div>
          <div class="form-field">
            <label class="form-label">手机号</label>
            <input v-model="form.phoneNumber" class="form-input" />
          </div>
          <div class="form-field">
            <label class="form-label">邮箱</label>
            <input v-model="form.email" class="form-input" />
          </div>
          <div class="form-field">
            <label class="form-label">状态</label>
            <select v-model="form.status" class="form-input">
              <option :value="1">启用</option>
              <option :value="0">禁用</option>
            </select>
          </div>
        </div>
        <div class="dialog__footer">
          <button class="btn btn-ghost" type="button" @click="dialogVisible = false">取消</button>
          <button class="btn btn-primary" type="button" @click="handleSubmit">确定</button>
        </div>
      </div>
    </div>
  </div>
</template>
