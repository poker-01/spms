<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { assignRolePermissions, getRolePermissions, listRoles } from '@/api/role'
import { getPermissionTree } from '@/api/permission'
import type { PermissionItem, RoleItem } from '@/utils/api-types'

defineOptions({
  name: 'RolePermissionAssign',
})

const roles = ref<RoleItem[]>([])
const permissions = ref<PermissionItem[]>([])
const selectedRole = ref<RoleItem | null>(null)
const selectedPermissionIds = ref<number[]>([])

const loadRoles = async () => {
  const { data } = await listRoles()
  roles.value = data
}

const loadPermissions = async () => {
  const { data } = await getPermissionTree()
  permissions.value = data
}

const flatten = (list: PermissionItem[] = []): PermissionItem[] => {
  return list.flatMap((item) => [item, ...(item.children ? flatten(item.children) : [])])
}

const selectRole = async (role: RoleItem) => {
  selectedRole.value = role
  const { data } = await getRolePermissions(role.id)
  selectedPermissionIds.value = data
}

const handleSubmit = async () => {
  if (!selectedRole.value) return
  await assignRolePermissions({
    roleId: selectedRole.value.id,
    permissionIds: selectedPermissionIds.value,
  })
  alert('分配成功')
}

onMounted(() => {
  loadRoles()
  loadPermissions()
})
</script>

<template>
  <div class="manage-page">
    <h2 class="manage-page__title">角色权限分配</h2>

    <div class="assign-page">
      <div class="assign-page__panel">
        <h3 class="assign-page__subtitle">选择角色</h3>
        <ul class="assign-page__list">
          <li
            v-for="role in roles"
            :key="role.id"
            :class="{ 'assign-page__item--active': selectedRole?.id === role.id }"
            @click="selectRole(role)"
          >
            {{ role.roleName }}
          </li>
        </ul>
      </div>

      <div class="assign-page__panel">
        <h3 class="assign-page__subtitle">分配权限</h3>
        <div v-if="!selectedRole" class="assign-page__placeholder">请先选择左侧角色</div>
        <div v-else class="assign-page__options">
          <label v-for="perm in flatten(permissions)" :key="perm.id" class="assign-page__checkbox">
            <input v-model="selectedPermissionIds" type="checkbox" :value="perm.id" />
            <span>{{ perm.permissionName }}（{{ perm.permissionStr || perm.permissionCode }}）</span>
          </label>
        </div>
        <button
          v-permission="'system:role:assign'"
          class="btn btn-primary assign-page__submit"
          type="button"
          :disabled="!selectedRole"
          @click="handleSubmit"
        >
          保存分配
        </button>
      </div>
    </div>
  </div>
</template>
