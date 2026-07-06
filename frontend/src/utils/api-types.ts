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
  permissionCode?: string
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
  roleIds?: number[]
  ownerId?: number
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

// ========== 小区 ==========
export interface CommunityItem {
  id: number
  communityCode: string
  communityName: string
  address?: string
  province?: string
  city?: string
  district?: string
  totalBuildings?: number
  totalUnits?: number
  propertyCompany?: string
  managerName?: string
  managerPhone?: string
  status: number
  createTime?: string
  updateTime?: string
}

export interface CommunityQuery {
  [key: string]: string | number | boolean | undefined | null
  communityName?: string
  city?: string
  status?: number
  pageNum?: number
  pageSize?: number
}

export interface CommunitySave {
  communityCode: string
  communityName: string
  address?: string
  province?: string
  city?: string
  district?: string
  totalBuildings?: number
  totalUnits?: number
  propertyCompany?: string
  managerName?: string
  managerPhone?: string
  status: number
  remark?: string
}

export interface CommunityUpdate {
  id: number
  communityCode?: string
  communityName?: string
  address?: string
  province?: string
  city?: string
  district?: string
  totalBuildings?: number
  totalUnits?: number
  propertyCompany?: string
  managerName?: string
  managerPhone?: string
  status?: number
  remark?: string
}

// ========== 楼栋 ==========
export interface BuildingItem {
  id: number
  communityId: number
  communityName?: string
  buildingCode: string
  buildingName: string
  totalFloors?: number
  totalUnits?: number
  status: number
  createTime?: string
  updateTime?: string
}

export interface BuildingQuery {
  [key: string]: string | number | boolean | undefined | null
  buildingName?: string
  communityId?: number
  status?: number
  pageNum?: number
  pageSize?: number
}

export interface BuildingSave {
  communityId: number
  buildingCode: string
  buildingName: string
  totalFloors?: number
  totalUnits?: number
  status: number
  remark?: string
}

export interface BuildingUpdate {
  id: number
  communityId?: number
  buildingCode?: string
  buildingName?: string
  totalFloors?: number
  totalUnits?: number
  status?: number
  remark?: string
}

// ========== 房屋 ==========
export interface HouseItem {
  id: number
  buildingId: number
  buildingName?: string
  communityName?: string
  houseNumber: string
  floorNumber?: number
  houseArea?: number
  houseType?: string
  ownerName?: string
  ownerPhone?: string
  status: number
  createTime?: string
  updateTime?: string
}

export interface HouseQuery {
  [key: string]: string | number | boolean | undefined | null
  houseNumber?: string
  buildingId?: number
  status?: number
  pageNum?: number
  pageSize?: number
}

export interface HouseSave {
  buildingId: number
  houseNumber: string
  floorNumber?: number
  houseArea?: number
  houseType?: string
  ownerName?: string
  ownerPhone?: string
  status: number
  remark?: string
}

export interface HouseUpdate {
  id: number
  buildingId?: number
  houseNumber?: string
  floorNumber?: number
  houseArea?: number
  houseType?: string
  ownerName?: string
  ownerPhone?: string
  status?: number
  remark?: string
}

// ========== 业主 ==========
export interface OwnerItem {
  id: number
  ownerName: string
  ownerPhone: string
  idCard?: string
  gender?: number
  email?: string
  status: number
  createTime?: string
  updateTime?: string
}

export interface OwnerHouseRel {
  id?: number
  ownerId?: number
  houseId?: number
  houseNumber?: string
  buildingName?: string
  communityName?: string
  fullAddress?: string
}

export interface OwnerQuery {
  [key: string]: string | number | boolean | undefined | null
  ownerName?: string
  ownerPhone?: string
  idCard?: string
  status?: number
  pageNum?: number
  pageSize?: number
}

export interface OwnerSave {
  ownerName: string
  ownerPhone: string
  idCard?: string
  gender?: number
  email?: string
  status: number
  remark?: string
  houseId?: number
}

export interface OwnerUpdate {
  id: number
  ownerName?: string
  ownerPhone?: string
  idCard?: string
  gender?: number
  email?: string
  status?: number
  remark?: string
}
