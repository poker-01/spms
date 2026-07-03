<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { deleteRole, listRoles, saveRole, updateRole } from '@/api/role'
import type { RoleItem, RoleSave, RoleUpdate } from '@/utils/api-types'

defineOptions({
  name: 'RoleManage',
})

const loading = ref(false)
const roles = ref<RoleItem[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive<RoleSave | RoleUpdate>({
  roleCode: '',
  roleName: '',
})

const loadData = async () => {
  loading.value = true
  try {
    const { data } = await listRoles()
    roles.value = data
  } finally {
    loading.value = false
  }
}

const openAdd = () => {
  isEdit.value = false
  Object.assign(form, { roleCode: '', roleName: '' })
  dialogVisible.value = true
}

const openEdit = (row: RoleItem) => {
  isEdit.value = true
  Object.assign(form, { id: row.id, roleCode: row.roleCode, roleName: row.roleName })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (isEdit.value) {
    await updateRole(form as RoleUpdate)
  } else {
    await saveRole(form as RoleSave)
  }
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (row: RoleItem) => {
  if (confirm(`确定删除角色 ${row.roleName} 吗？`)) {
    await deleteRole(row.id)
    loadData()
  }
}

onMounted(loadData)
</script>

<template>
  <div class="manage-page">
    <h2 class="manage-page__title">角色管理</h2>

    <div class="manage-page__toolbar">
      <button v-permission="'system:role:add'" class="btn btn-primary" type="button" @click="openAdd">
        新增角色
      </button>
    </div>

    <div class="manage-page__table-wrap">
      <table class="manage-page__table">
        <thead>
          <tr>
            <th>角色编码</th>
            <th>角色名称</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in roles" :key="row.id">
            <td>{{ row.roleCode }}</td>
            <td>{{ row.roleName }}</td>
            <td>
              <button
                v-permission="'system:role:edit'"
                class="btn btn-sm btn-ghost"
                type="button"
                @click="openEdit(row)"
              >
                编辑
              </button>
              <button
                v-permission="'system:role:delete'"
                class="btn btn-sm btn-danger"
                type="button"
                @click="handleDelete(row)"
              >
                删除
              </button>
            </td>
          </tr>
          <tr v-if="!roles.length">
            <td colspan="3" class="manage-page__empty">暂无数据</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="dialogVisible" class="dialog-overlay" @click.self="dialogVisible = false">
      <div class="dialog">
        <h3 class="dialog__title">{{ isEdit ? '编辑角色' : '新增角色' }}</h3>
        <div class="dialog__body">
          <div class="form-field">
            <label class="form-label">角色编码</label>
            <input v-model="form.roleCode" class="form-input" :disabled="isEdit" />
          </div>
          <div class="form-field">
            <label class="form-label">角色名称</label>
            <input v-model="form.roleName" class="form-input" />
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
