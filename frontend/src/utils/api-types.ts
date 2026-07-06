export interface ApiResult<T = unknown> {
  code: number
  message: string
  data: T
}

export interface PageResult<T> {
  total: number
  pages: number
  current: number
  size: number
  records: T[]
}

export interface UserInfo {
  id: number
  userName: string
  fullName: string
  phoneNumber?: string
  email?: string
  avatarAddress?: string
  roles: string[]
  permissions: string[]
}

export interface LoginData {
  token: string
  userInfo: UserInfo
}

export interface LoginParams {
  userName: string
  password: string
  loginType: 'owner' | 'property'
}

export interface MenuItem {
  id: number
  parentId: number
  name: string
  path: string
  component?: string
  icon?: string
  sortOrder: number
  children?: MenuItem[]
}

export interface UserItem {
  id: number
  userName: string
  fullName: string
  phoneNumber?: string
  email?: string
  status: number
  createTime: string
  roleNames: string[]
}

export interface UserQuery {
  [key: string]: string | number | boolean | undefined | null
  userName?: string
  fullName?: string
  status?: number
  pageNum?: number
  pageSize?: number
}

export interface UserSave {
  userName: string
  password: string
  fullName?: string
  phoneNumber?: string
  email?: string
  status: number
}

export interface UserUpdate {
  id: number
  fullName?: string
  phoneNumber?: string
  email?: string
  status?: number
}

export interface UserStatus {
  id: number
  status: number
}

export interface UserAssignRole {
  userId: number
  roleIds: number[]
}

export interface RoleItem {
  id: number
  roleCode: string
  roleName: string
}

export interface RoleSave {
  roleCode: string
  roleName: string
}

export interface RoleUpdate {
  id: number
  roleCode?: string
  roleName?: string
}

export interface RoleAssignPermission {
  roleId: number
  permissionIds: number[]
}

export interface PermissionItem {
  id: number
  parentId: number
  permissionCode: string
  permissionName: string
  permissionType: number
  permissionIcon?: string
  permissionPath?: string
  permissionComponent?: string
  permissionStr?: string
  sortOrder: number
  visible: number
  children?: PermissionItem[]
}

export interface PermissionSave {
  parentId?: number
  permissionCode: string
  permissionName: string
  permissionType: number
  permissionIcon?: string
  permissionPath?: string
  permissionComponent?: string
  permissionStr?: string
  sortOrder?: number
  visible?: number
}

export interface PermissionUpdate {
  id: number
  parentId?: number
  permissionCode?: string
  permissionName?: string
  permissionType?: number
  permissionIcon?: string
  permissionPath?: string
  permissionComponent?: string
  permissionStr?: string
  sortOrder?: number
  visible?: number
}

export interface OwnerHomeData {
  userId: number
  userName: string
  fullName: string
  phoneNumber?: string
  roles?: string[]
  pendingBillCount: number
  repairCount: number
  complaintCount: number
}

export interface OwnerBill {
  id: number
  billNo: string
  itemName: string
  amount: number
  status: number
  createTime: string
  deadline: string
}

export interface OwnerRepair {
  id: number
  orderNo: string
  repairType: string
  repairTypeName: string
  repairDesc: string
  repairPhone: string
  priority: number
  status: number
  statusName: string
  repairCost?: number
  evaluateScore?: number
  evaluateComment?: string
  createTime: string
  repairTime?: string
}

export interface OwnerComplaint {
  id: number
  complaintNo: string
  type: number
  typeName: string
  title: string
  content: string
  contactPhone?: string
  status: number
  statusName: string
  replyContent?: string
  replyTime?: string
  createTime: string
}

export interface RepairApplyData {
  content: string
  repairType: number
  contactPhone?: string
}

export interface ComplaintApplyData {
  type: number
  title: string
  content: string
  contactPhone?: string
}
