<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { deletePermission, getPermissionTree, savePermission, updatePermission } from '@/api/permission'
import type { PermissionItem, PermissionSave, PermissionUpdate } from '@/utils/api-types'

defineOptions({
  name: 'PermissionManage',
})

const loading = ref(false)
const treeData = ref<PermissionItem[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive<PermissionSave | PermissionUpdate>({
  parentId: 0,
  permissionCode: '',
  permissionName: '',
  permissionType: 1,
  permissionIcon: '',
  permissionPath: '',
  permissionComponent: '',
  permissionStr: '',
  sortOrder: 0,
  visible: 1,
})

const flatten = (list: PermissionItem[] = []): PermissionItem[] => {
  return list.flatMap((item) => [item, ...(item.children ? flatten(item.children) : [])])
}

const loadData = async () => {
  loading.value = true
  try {
    const { data } = await getPermissionTree()
    treeData.value = data
  } finally {
    loading.value = false
  }
}

const openAdd = (parent?: PermissionItem) => {
  isEdit.value = false
  Object.assign(form, {
    parentId: parent?.id ?? 0,
    permissionCode: '',
    permissionName: '',
    permissionType: parent ? 2 : 1,
    permissionIcon: '',
    permissionPath: '',
    permissionComponent: '',
    permissionStr: '',
    sortOrder: 0,
    visible: 1,
  })
  dialogVisible.value = true
}

const openEdit = (row: PermissionItem) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    parentId: row.parentId,
    permissionCode: row.permissionCode,
    permissionName: row.permissionName,
    permissionType: row.permissionType,
    permissionIcon: row.permissionIcon,
    permissionPath: row.permissionPath,
    permissionComponent: row.permissionComponent,
    permissionStr: row.permissionStr,
    sortOrder: row.sortOrder,
    visible: row.visible,
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (isEdit.value) {
    await updatePermission(form as PermissionUpdate)
  } else {
    await savePermission(form as PermissionSave)
  }
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (row: PermissionItem) => {
  if (confirm(`确定删除权限 ${row.permissionName} 吗？`)) {
    await deletePermission(row.id)
    loadData()
  }
}

onMounted(loadData)
</script>

<template>
  <div class="manage-page">
    <h2 class="manage-page__title">菜单权限管理</h2>

    <div class="manage-page__toolbar">
      <button v-permission="'system:permission:add'" class="btn btn-primary" type="button" @click="openAdd()">
        新增菜单
      </button>
    </div>

    <div class="manage-page__table-wrap">
      <table class="manage-page__table">
        <thead>
          <tr>
            <th>权限名称</th>
            <th>权限编码</th>
            <th>类型</th>
            <th>路径</th>
            <th>权限标识</th>
            <th>排序</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <template v-for="row in flatten(treeData)" :key="row.id">
            <tr :style="{ paddingLeft: `${(row.parentId ? 24 : 0) + 16}px` }">
              <td>
                <span :style="{ marginLeft: row.parentId ? '24px' : '0' }">
                  {{ row.permissionName }}
                </span>
              </td>
              <td>{{ row.permissionCode }}</td>
              <td>{{ row.permissionType === 1 ? '菜单' : '按钮' }}</td>
              <td>{{ row.permissionPath || '-' }}</td>
              <td>{{ row.permissionStr || '-' }}</td>
              <td>{{ row.sortOrder }}</td>
              <td>
                <button
                  v-permission="'system:permission:add'"
                  class="btn btn-sm btn-ghost"
                  type="button"
                  @click="openAdd(row)"
                >
                  新增子项
                </button>
                <button
                  v-permission="'system:permission:edit'"
                  class="btn btn-sm btn-ghost"
                  type="button"
                  @click="openEdit(row)"
                >
                  编辑
                </button>
                <button
                  v-permission="'system:permission:delete'"
                  class="btn btn-sm btn-danger"
                  type="button"
                  @click="handleDelete(row)"
                >
                  删除
                </button>
              </td>
            </tr>
          </template>
          <tr v-if="!treeData.length">
            <td colspan="7" class="manage-page__empty">暂无数据</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="dialogVisible" class="dialog-overlay" @click.self="dialogVisible = false">
      <div class="dialog">
        <h3 class="dialog__title">{{ isEdit ? '编辑权限' : '新增权限' }}</h3>
        <div class="dialog__body">
          <div class="form-field">
            <label class="form-label">父级ID</label>
            <input v-model.number="form.parentId" class="form-input" type="number" />
          </div>
          <div class="form-field">
            <label class="form-label">权限编码</label>
            <input v-model="form.permissionCode" class="form-input" />
          </div>
          <div class="form-field">
            <label class="form-label">权限名称</label>
            <input v-model="form.permissionName" class="form-input" />
          </div>
          <div class="form-field">
            <label class="form-label">类型</label>
            <select v-model.number="form.permissionType" class="form-input">
              <option :value="1">菜单</option>
              <option :value="2">按钮</option>
            </select>
          </div>
          <div class="form-field">
            <label class="form-label">图标</label>
            <input v-model="form.permissionIcon" class="form-input" />
          </div>
          <div class="form-field">
            <label class="form-label">路由路径</label>
            <input v-model="form.permissionPath" class="form-input" />
          </div>
          <div class="form-field">
            <label class="form-label">组件路径</label>
            <input v-model="form.permissionComponent" class="form-input" />
          </div>
          <div class="form-field">
            <label class="form-label">权限标识</label>
            <input v-model="form.permissionStr" class="form-input" />
          </div>
          <div class="form-field">
            <label class="form-label">排序</label>
            <input v-model.number="form.sortOrder" class="form-input" type="number" />
          </div>
          <div class="form-field">
            <label class="form-label">是否可见</label>
            <select v-model.number="form.visible" class="form-input">
              <option :value="1">是</option>
              <option :value="0">否</option>
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
