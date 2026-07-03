<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { assignUserRoles, getUserRoles, pageUsers } from '@/api/user'
import { listRoles } from '@/api/role'
import type { PageResult, RoleItem, UserItem } from '@/utils/api-types'

defineOptions({
  name: 'UserRoleAssign',
})

const users = ref<UserItem[]>([])
const roles = ref<RoleItem[]>([])
const selectedUser = ref<UserItem | null>(null)
const selectedRoleIds = ref<number[]>([])

const loadUsers = async () => {
  const { data } = await pageUsers({ pageNum: 1, pageSize: 100 })
  users.value = (data as PageResult<UserItem>).records
}

const loadRoles = async () => {
  const { data } = await listRoles()
  roles.value = data
}

const selectUser = async (user: UserItem) => {
  selectedUser.value = user
  const { data } = await getUserRoles(user.id)
  selectedRoleIds.value = data
}

const handleSubmit = async () => {
  if (!selectedUser.value) return
  await assignUserRoles({
    userId: selectedUser.value.id,
    roleIds: selectedRoleIds.value,
  })
  alert('分配成功')
}

onMounted(() => {
  loadUsers()
  loadRoles()
})
</script>

<template>
  <div class="manage-page">
    <h2 class="manage-page__title">用户角色分配</h2>

    <div class="assign-page">
      <div class="assign-page__panel">
        <h3 class="assign-page__subtitle">选择用户</h3>
        <ul class="assign-page__list">
          <li
            v-for="user in users"
            :key="user.id"
            :class="{ 'assign-page__item--active': selectedUser?.id === user.id }"
            @click="selectUser(user)"
          >
            {{ user.userName }}（{{ user.fullName || '-' }}）
          </li>
        </ul>
      </div>

      <div class="assign-page__panel">
        <h3 class="assign-page__subtitle">分配角色</h3>
        <div v-if="!selectedUser" class="assign-page__placeholder">请先选择左侧用户</div>
        <div v-else class="assign-page__options">
          <label v-for="role in roles" :key="role.id" class="assign-page__checkbox">
            <input v-model="selectedRoleIds" type="checkbox" :value="role.id" />
            <span>{{ role.roleName }}</span>
          </label>
        </div>
        <button
          v-permission="'system:user:assign'"
          class="btn btn-primary assign-page__submit"
          type="button"
          :disabled="!selectedUser"
          @click="handleSubmit"
        >
          保存分配
        </button>
      </div>
    </div>
  </div>
</template>
