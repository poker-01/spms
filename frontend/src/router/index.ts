
import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'
import { getDefaultHomePath, isOwnerUser, isPropertyUser } from '@/utils/role'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: () => {
        const token = getToken()
        return token ? getDefaultHomePath() : '/login'
      },
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: { guestOnly: true },
    },
    {
      path: '/home',
      redirect: '/owner/home',
    },
    {
      path: '/owner',
      component: () => import('@/components/OwnerLayout.vue'),
      meta: { requiresAuth: true, portal: 'owner' },
      children: [
        {
          path: 'home',
          name: 'OwnerHome',
          component: () => import('@/views/owner/OwnerHome.vue'),
          meta: { requiresAuth: true, portal: 'owner' },
        },
        {
          path: 'bills',
          name: 'OwnerBills',
          component: () => import('@/views/owner/OwnerBills.vue'),
          meta: { requiresAuth: true, portal: 'owner' },
        },
        {
          path: 'repairs',
          name: 'OwnerRepairs',
          component: () => import('@/views/owner/OwnerRepairs.vue'),
          meta: { requiresAuth: true, portal: 'owner' },
        },
        {
          path: 'complaints',
          name: 'OwnerComplaints',
          component: () => import('@/views/owner/OwnerComplaints.vue'),
          meta: { requiresAuth: true, portal: 'owner' },
        },
      ],
    },
    {
      path: '/admin',
      component: () => import('@/components/AdminLayout.vue'),
      meta: { requiresAuth: true, portal: 'property' },
      children: [
        {
          path: 'home',
          name: 'AdminHome',
          component: () => import('@/views/AdminHome.vue'),
          meta: { requiresAuth: true, portal: 'property' },
        },
        {
          path: 'users',
          name: 'UserManage',
          component: () => import('@/views/admin/UserManage.vue'),
          meta: { requiresAuth: true, portal: 'property' },
        },
        {
          path: 'roles',
          name: 'RoleManage',
          component: () => import('@/views/admin/RoleManage.vue'),
          meta: { requiresAuth: true, portal: 'property' },
        },
        {
          path: 'permissions',
          name: 'PermissionManage',
          component: () => import('@/views/admin/PermissionManage.vue'),
          meta: { requiresAuth: true, portal: 'property' },
        },
        {
          path: 'user-roles',
          name: 'UserRoleAssign',
          component: () => import('@/views/admin/UserRoleAssign.vue'),
          meta: { requiresAuth: true, portal: 'property' },
        },
        {
          path: 'role-permissions',
          name: 'RolePermissionAssign',
          component: () => import('@/views/admin/RolePermissionAssign.vue'),
          meta: { requiresAuth: true, portal: 'property' },
        },
      ],
    },
  ],
})

router.beforeEach(async (to) => {
  const token = getToken()
  const userStore = useUserStore()

  if (to.meta.requiresAuth) {
    if (!token) {
      return {
        path: '/login',
        query: { redirect: to.fullPath },
      }
    }

    if (!userStore.userInfo) {
      const isValid = await userStore.initAuth()
      if (!isValid) {
        return {
          path: '/login',
          query: { redirect: to.fullPath },
        }
      }
    }

    const roles = userStore.userInfo?.roles ?? []
    if (to.meta.portal === 'property' && !isPropertyUser(roles)) {
      return '/owner/home'
    }
    if (to.meta.portal === 'owner' && !isOwnerUser(roles)) {
      return '/admin/home'
    }
  }

  if (to.meta.guestOnly && token) {
    if (!userStore.userInfo) {
      const isValid = await userStore.initAuth()
      if (!isValid) {
        return true
      }
    }
    return getDefaultHomePath(userStore.userInfo?.roles)
  }

  return true
})

export default router
