<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  pageCommunities,
  saveCommunity,
  updateCommunity,
  deleteCommunity,
  getCommunity,
} from '@/api/community'
import { getUsersByRole } from '@/api/repair'
import type { CommunitySave, CommunityUpdate } from '@/utils/api-types'

// ============ 省市区数据 ============
interface AreaOption {
  value: string
  label: string
  children?: AreaOption[]
}

// 使用简化的省市区数据（主要省会城市和直辖市）
const regionData: AreaOption[] = [
  { value: '北京市', label: '北京市', children: [
    { value: '北京市', label: '北京市', children: [
      { value: '东城区', label: '东城区' }, { value: '西城区', label: '西城区' },
      { value: '朝阳区', label: '朝阳区' }, { value: '丰台区', label: '丰台区' },
      { value: '石景山区', label: '石景山区' }, { value: '海淀区', label: '海淀区' },
      { value: '顺义区', label: '顺义区' }, { value: '通州区', label: '通州区' },
      { value: '大兴区', label: '大兴区' }, { value: '房山区', label: '房山区' },
      { value: '昌平区', label: '昌平区' }, { value: '怀柔区', label: '怀柔区' },
      { value: '密云区', label: '密云区' }, { value: '延庆区', label: '延庆区' },
      { value: '平谷区', label: '平谷区' }, { value: '门头沟区', label: '门头沟区' },
    ]}
  ]},
  { value: '上海市', label: '上海市', children: [
    { value: '上海市', label: '上海市', children: [
      { value: '黄浦区', label: '黄浦区' }, { value: '徐汇区', label: '徐汇区' },
      { value: '长宁区', label: '长宁区' }, { value: '静安区', label: '静安区' },
      { value: '普陀区', label: '普陀区' }, { value: '虹口区', label: '虹口区' },
      { value: '杨浦区', label: '杨浦区' }, { value: '浦东新区', label: '浦东新区' },
      { value: '闵行区', label: '闵行区' }, { value: '宝山区', label: '宝山区' },
      { value: '嘉定区', label: '嘉定区' }, { value: '金山区', label: '金山区' },
      { value: '松江区', label: '松江区' }, { value: '青浦区', label: '青浦区' },
      { value: '奉贤区', label: '奉贤区' }, { value: '崇明区', label: '崇明区' },
    ]}
  ]},
  { value: '天津市', label: '天津市', children: [
    { value: '天津市', label: '天津市', children: [
      { value: '和平区', label: '和平区' }, { value: '河东区', label: '河东区' },
      { value: '河西区', label: '河西区' }, { value: '南开区', label: '南开区' },
      { value: '河北区', label: '河北区' }, { value: '红桥区', label: '红桥区' },
      { value: '东丽区', label: '东丽区' }, { value: '西青区', label: '西青区' },
      { value: '津南区', label: '津南区' }, { value: '北辰区', label: '北辰区' },
      { value: '武清区', label: '武清区' }, { value: '宝坻区', label: '宝坻区' },
      { value: '滨海新区', label: '滨海新区' }, { value: '宁河区', label: '宁河区' },
      { value: '静海区', label: '静海区' }, { value: '蓟州区', label: '蓟州区' },
    ]}
  ]},
  { value: '重庆市', label: '重庆市', children: [
    { value: '重庆市', label: '重庆市', children: [
      { value: '渝中区', label: '渝中区' }, { value: '江北区', label: '江北区' },
      { value: '南岸区', label: '南岸区' }, { value: '沙坪坝区', label: '沙坪坝区' },
      { value: '九龙坡区', label: '九龙坡区' }, { value: '大渡口区', label: '大渡口区' },
      { value: '渝北区', label: '渝北区' }, { value: '巴南区', label: '巴南区' },
      { value: '北碚区', label: '北碚区' }, { value: '涪陵区', label: '涪陵区' },
      { value: '万州区', label: '万州区' }, { value: '黔江区', label: '黔江区' },
      { value: '长寿区', label: '长寿区' }, { value: '江津区', label: '江津区' },
      { value: '合川区', label: '合川区' }, { value: '永川区', label: '永川区' },
    ]}
  ]},
  { value: '河北省', label: '河北省', children: [
    { value: '石家庄市', label: '石家庄市', children: [
      { value: '长安区', label: '长安区' }, { value: '桥西区', label: '桥西区' },
      { value: '新华区', label: '新华区' }, { value: '裕华区', label: '裕华区' },
    ]},
    { value: '唐山市', label: '唐山市', children: [
      { value: '路北区', label: '路北区' }, { value: '路南区', label: '路南区' },
    ]},
    { value: '保定市', label: '保定市', children: [
      { value: '竞秀区', label: '竞秀区' }, { value: '莲池区', label: '莲池区' },
    ]},
  ]},
  { value: '山西省', label: '山西省', children: [
    { value: '太原市', label: '太原市', children: [
      { value: '小店区', label: '小店区' }, { value: '迎泽区', label: '迎泽区' },
      { value: '杏花岭区', label: '杏花岭区' }, { value: '尖草坪区', label: '尖草坪区' },
      { value: '万柏林区', label: '万柏林区' }, { value: '晋源区', label: '晋源区' },
    ]},
  ]},
  { value: '辽宁省', label: '辽宁省', children: [
    { value: '沈阳市', label: '沈阳市', children: [
      { value: '和平区', label: '和平区' }, { value: '沈河区', label: '沈河区' },
      { value: '皇姑区', label: '皇姑区' }, { value: '铁西区', label: '铁西区' },
    ]},
    { value: '大连市', label: '大连市', children: [
      { value: '中山区', label: '中山区' }, { value: '西岗区', label: '西岗区' },
      { value: '沙河口区', label: '沙河口区' }, { value: '甘井子区', label: '甘井子区' },
    ]},
  ]},
  { value: '吉林省', label: '吉林省', children: [
    { value: '长春市', label: '长春市', children: [
      { value: '南关区', label: '南关区' }, { value: '朝阳区', label: '朝阳区' },
      { value: '绿园区', label: '绿园区' }, { value: '二道区', label: '二道区' },
    ]},
  ]},
  { value: '黑龙江省', label: '黑龙江省', children: [
    { value: '哈尔滨市', label: '哈尔滨市', children: [
      { value: '道里区', label: '道里区' }, { value: '南岗区', label: '南岗区' },
      { value: '道外区', label: '道外区' }, { value: '香坊区', label: '香坊区' },
    ]},
  ]},
  { value: '江苏省', label: '江苏省', children: [
    { value: '南京市', label: '南京市', children: [
      { value: '玄武区', label: '玄武区' }, { value: '秦淮区', label: '秦淮区' },
      { value: '建邺区', label: '建邺区' }, { value: '鼓楼区', label: '鼓楼区' },
    ]},
    { value: '苏州市', label: '苏州市', children: [
      { value: '姑苏区', label: '姑苏区' }, { value: '吴中区', label: '吴中区' },
      { value: '相城区', label: '相城区' }, { value: '虎丘区', label: '虎丘区' },
    ]},
    { value: '无锡市', label: '无锡市', children: [
      { value: '梁溪区', label: '梁溪区' }, { value: '锡山区', label: '锡山区' },
    ]},
  ]},
  { value: '浙江省', label: '浙江省', children: [
    { value: '杭州市', label: '杭州市', children: [
      { value: '上城区', label: '上城区' }, { value: '拱墅区', label: '拱墅区' },
      { value: '西湖区', label: '西湖区' }, { value: '滨江区', label: '滨江区' },
    ]},
    { value: '宁波市', label: '宁波市', children: [
      { value: '海曙区', label: '海曙区' }, { value: '江北区', label: '江北区' },
    ]},
    { value: '温州市', label: '温州市', children: [
      { value: '鹿城区', label: '鹿城区' }, { value: '龙湾区', label: '龙湾区' },
    ]},
  ]},
  { value: '安徽省', label: '安徽省', children: [
    { value: '合肥市', label: '合肥市', children: [
      { value: '瑶海区', label: '瑶海区' }, { value: '庐阳区', label: '庐阳区' },
      { value: '蜀山区', label: '蜀山区' }, { value: '包河区', label: '包河区' },
    ]},
  ]},
  { value: '福建省', label: '福建省', children: [
    { value: '福州市', label: '福州市', children: [
      { value: '鼓楼区', label: '鼓楼区' }, { value: '台江区', label: '台江区' },
      { value: '仓山区', label: '仓山区' }, { value: '晋安区', label: '晋安区' },
    ]},
    { value: '厦门市', label: '厦门市', children: [
      { value: '思明区', label: '思明区' }, { value: '湖里区', label: '湖里区' },
    ]},
  ]},
  { value: '江西省', label: '江西省', children: [
    { value: '南昌市', label: '南昌市', children: [
      { value: '东湖区', label: '东湖区' }, { value: '西湖区', label: '西湖区' },
      { value: '青云谱区', label: '青云谱区' }, { value: '青山湖区', label: '青山湖区' },
    ]},
  ]},
  { value: '山东省', label: '山东省', children: [
    { value: '济南市', label: '济南市', children: [
      { value: '历下区', label: '历下区' }, { value: '市中区', label: '市中区' },
      { value: '槐荫区', label: '槐荫区' }, { value: '天桥区', label: '天桥区' },
    ]},
    { value: '青岛市', label: '青岛市', children: [
      { value: '市南区', label: '市南区' }, { value: '市北区', label: '市北区' },
      { value: '李沧区', label: '李沧区' }, { value: '崂山区', label: '崂山区' },
    ]},
  ]},
  { value: '河南省', label: '河南省', children: [
    { value: '郑州市', label: '郑州市', children: [
      { value: '中原区', label: '中原区' }, { value: '二七区', label: '二七区' },
      { value: '金水区', label: '金水区' }, { value: '惠济区', label: '惠济区' },
    ]},
  ]},
  { value: '湖北省', label: '湖北省', children: [
    { value: '武汉市', label: '武汉市', children: [
      { value: '江岸区', label: '江岸区' }, { value: '江汉区', label: '江汉区' },
      { value: '硚口区', label: '硚口区' }, { value: '汉阳区', label: '汉阳区' },
      { value: '武昌区', label: '武昌区' }, { value: '洪山区', label: '洪山区' },
    ]},
  ]},
  { value: '湖南省', label: '湖南省', children: [
    { value: '长沙市', label: '长沙市', children: [
      { value: '芙蓉区', label: '芙蓉区' }, { value: '天心区', label: '天心区' },
      { value: '岳麓区', label: '岳麓区' }, { value: '开福区', label: '开福区' },
      { value: '雨花区', label: '雨花区' }, { value: '望城区', label: '望城区' },
    ]},
  ]},
  { value: '广东省', label: '广东省', children: [
    { value: '广州市', label: '广州市', children: [
      { value: '越秀区', label: '越秀区' }, { value: '海珠区', label: '海珠区' },
      { value: '荔湾区', label: '荔湾区' }, { value: '天河区', label: '天河区' },
      { value: '白云区', label: '白云区' }, { value: '黄埔区', label: '黄埔区' },
    ]},
    { value: '深圳市', label: '深圳市', children: [
      { value: '罗湖区', label: '罗湖区' }, { value: '福田区', label: '福田区' },
      { value: '南山区', label: '南山区' }, { value: '宝安区', label: '宝安区' },
      { value: '龙岗区', label: '龙岗区' }, { value: '龙华区', label: '龙华区' },
    ]},
    { value: '东莞市', label: '东莞市', children: [
      { value: '莞城区', label: '莞城区' }, { value: '南城区', label: '南城区' },
    ]},
    { value: '佛山市', label: '佛山市', children: [
      { value: '禅城区', label: '禅城区' }, { value: '南海区', label: '南海区' },
      { value: '顺德区', label: '顺德区' }, { value: '三水区', label: '三水区' },
    ]},
  ]},
  { value: '四川省', label: '四川省', children: [
    { value: '成都市', label: '成都市', children: [
      { value: '锦江区', label: '锦江区' }, { value: '青羊区', label: '青羊区' },
      { value: '金牛区', label: '金牛区' }, { value: '武侯区', label: '武侯区' },
      { value: '成华区', label: '成华区' }, { value: '高新区', label: '高新区' },
    ]},
  ]},
  { value: '贵州省', label: '贵州省', children: [
    { value: '贵阳市', label: '贵阳市', children: [
      { value: '南明区', label: '南明区' }, { value: '云岩区', label: '云岩区' },
      { value: '花溪区', label: '花溪区' }, { value: '观山湖区', label: '观山湖区' },
    ]},
  ]},
  { value: '云南省', label: '云南省', children: [
    { value: '昆明市', label: '昆明市', children: [
      { value: '五华区', label: '五华区' }, { value: '盘龙区', label: '盘龙区' },
      { value: '官渡区', label: '官渡区' }, { value: '西山区', label: '西山区' },
    ]},
  ]},
  { value: '陕西省', label: '陕西省', children: [
    { value: '西安市', label: '西安市', children: [
      { value: '未央区', label: '未央区' }, { value: '新城区', label: '新城区' },
      { value: '碑林区', label: '碑林区' }, { value: '莲湖区', label: '莲湖区' },
      { value: '雁塔区', label: '雁塔区' }, { value: '长安区', label: '长安区' },
    ]},
  ]},
  { value: '甘肃省', label: '甘肃省', children: [
    { value: '兰州市', label: '兰州市', children: [
      { value: '城关区', label: '城关区' }, { value: '七里河区', label: '七里河区' },
      { value: '西固区', label: '西固区' }, { value: '安宁区', label: '安宁区' },
    ]},
  ]},
  { value: '青海省', label: '青海省', children: [
    { value: '西宁市', label: '西宁市', children: [
      { value: '城东区', label: '城东区' }, { value: '城中区', label: '城中区' },
      { value: '城西区', label: '城西区' }, { value: '城北区', label: '城北区' },
    ]},
  ]},
  { value: '内蒙古', label: '内蒙古', children: [
    { value: '呼和浩特市', label: '呼和浩特市', children: [
      { value: '新城区', label: '新城区' }, { value: '回民区', label: '回民区' },
      { value: '玉泉区', label: '玉泉区' }, { value: '赛罕区', label: '赛罕区' },
    ]},
  ]},
  { value: '广西', label: '广西', children: [
    { value: '南宁市', label: '南宁市', children: [
      { value: '青秀区', label: '青秀区' }, { value: '兴宁区', label: '兴宁区' },
      { value: '江南区', label: '江南区' }, { value: '西乡塘区', label: '西乡塘区' },
    ]},
  ]},
  { value: '西藏', label: '西藏', children: [
    { value: '拉萨市', label: '拉萨市', children: [
      { value: '城关区', label: '城关区' }, { value: '堆龙德庆区', label: '堆龙德庆区' },
    ]},
  ]},
  { value: '宁夏', label: '宁夏', children: [
    { value: '银川市', label: '银川市', children: [
      { value: '兴庆区', label: '兴庆区' }, { value: '西夏区', label: '西夏区' },
      { value: '金凤区', label: '金凤区' }, { value: '灵武市', label: '灵武市' },
    ]},
  ]},
  { value: '新疆', label: '新疆', children: [
    { value: '乌鲁木齐市', label: '乌鲁木齐市', children: [
      { value: '天山区', label: '天山区' }, { value: '沙依巴克区', label: '沙依巴克区' },
      { value: '新市区', label: '新市区' }, { value: '水磨沟区', label: '水磨沟区' },
    ]},
  ]},
  { value: '海南省', label: '海南省', children: [
    { value: '海口市', label: '海口市', children: [
      { value: '秀英区', label: '秀英区' }, { value: '龙华区', label: '龙华区' },
      { value: '琼山区', label: '琼山区' }, { value: '美兰区', label: '美兰区' },
    ]},
    { value: '三亚市', label: '三亚市', children: [
      { value: '吉阳区', label: '吉阳区' }, { value: '天涯区', label: '天涯区' },
    ]},
  ]},
]

// ============ API 接口 ============
// =========================================

// 物业管理员角色ID
const ADMIN_ROLE_ID = 2

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增小区')
const isEdit = ref(false)

const searchForm = reactive({
  communityName: ''
})

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref<any[]>([])

// 负责人下拉列表
interface ManagerOption {
  id: number
  fullName: string
  phoneNumber?: string
}

const managerList = ref<ManagerOption[]>([])

const form = reactive({
  id: null as number | null,
  communityCode: '',
  communityName: '',
  address: '',
  province: '',
  city: '',
  district: '',
  regionSelected: [] as string[], // 级联选择器的值
  totalBuildings: 0,
  totalUnits: 0,
  propertyCompany: '',
  managerId: null as number | null,
  managerName: '',
  managerPhone: '',
  status: 1,
  remark: ''
})

// 加载物业管理员列表
const loadManagerList = async () => {
  try {
    const res = await getUsersByRole(ADMIN_ROLE_ID)
    managerList.value = (res.data || []).map((u: any) => ({
      id: u.id,
      fullName: u.fullName,
      phoneNumber: u.phoneNumber,
    }))
  } catch (error) {
    console.error('加载物业管理员列表失败', error)
  }
}

// 级联选择器值变化时，自动更新 province/city/district
watch(() => form.regionSelected, (val) => {
  if (val && val.length >= 3) {
    form.province = val[0]
    form.city = val[1]
    form.district = val[2]
  }
})

// 选择负责人时，自动填充姓名和电话
const handleManagerChange = (managerId: number | null) => {
  if (managerId) {
    const manager = managerList.value.find(m => m.id === managerId)
    if (manager) {
      form.managerName = manager.fullName
      form.managerPhone = manager.phoneNumber || ''
    }
  } else {
    form.managerName = ''
    form.managerPhone = ''
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const res = await pageCommunities(params)
    tableData.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.communityName = ''
  handleSearch()
}

const handleSizeChange = (val: number) => {
  pagination.pageSize = val
  loadData()
}

const handleCurrentChange = (val: number) => {
  pagination.current = val
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增小区'
  resetForm()
  loadManagerList()
  dialogVisible.value = true
}

const handleEdit = async (row: any) => {
  isEdit.value = true
  dialogTitle.value = '编辑小区'
  loadManagerList()
  try {
    const res = await getCommunity(row.id)
    const data = res.data
    Object.assign(form, data)
    // 设置级联选择器的值
    if (data.province && data.city && data.district) {
      form.regionSelected = [data.province, data.city, data.district]
    } else {
      form.regionSelected = []
    }
    // 如果后端返回了managerId，设置到form中
    form.managerId = (data as any).managerId || null
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载小区信息失败')
  }
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除小区「${row.communityName}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCommunity(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      const updateData: any = { ...form }
      if (updateData.managerId) {
        updateData.managerId = updateData.managerId
      }
      await updateCommunity(updateData as CommunityUpdate)
      ElMessage.success('更新成功')
    } else {
      const saveData: any = { ...form }
      if (saveData.managerId) {
        saveData.managerId = saveData.managerId
      }
      await saveCommunity(saveData as CommunitySave)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新失败' : '新增失败')
  }
}

const resetForm = () => {
  form.id = null
  form.communityCode = ''
  form.communityName = ''
  form.address = ''
  form.province = ''
  form.city = ''
  form.district = ''
  form.regionSelected = []
  form.totalBuildings = 0
  form.totalUnits = 0
  form.propertyCompany = ''
  form.managerId = null
  form.managerName = ''
  form.managerPhone = ''
  form.status = 1
  form.remark = ''
}

const getStatusLabel = (status: number) => {
  return status === 1 ? '启用' : '停用'
}

const getStatusClass = (status: number) => {
  return status === 1 ? 'success' : 'danger'
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="page-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-header__left">
        <h2 class="page-title">小区管理</h2>
        <span class="page-subtitle">管理所有小区信息</span>
      </div>
      <button class="btn btn-primary" @click="handleAdd">
        <svg class="icon" viewBox="0 0 24 24" width="16" height="16"><path fill="currentColor" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/></svg>
        新增小区
      </button>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-section__inner">
        <div class="search-row">
          <div class="search-row__item">
            <label class="search-row__label">小区名称</label>
            <input v-model="searchForm.communityName" placeholder="请输入小区名称" class="form-input" />
          </div>
          <div class="search-row__actions">
            <button class="btn btn-primary" @click="handleSearch">搜索</button>
            <button class="btn btn-ghost" @click="handleReset">重置</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 表格 -->
    <div class="table-section">
      <div class="table-section__header">
        <span class="table-section__count">共 {{ pagination.total }} 条记录</span>
      </div>
      <div class="table-wrap">
        <table class="table">
          <thead>
          <tr>
            <th style="width: 60px;">ID</th>
            <th style="width: 120px;">小区编码</th>
            <th style="min-width: 120px;">小区名称</th>
            <th style="min-width: 150px;">地址</th>
            <th style="width: 120px;">区域</th>
            <th style="width: 80px;">楼栋数</th>
            <th style="width: 80px;">单元数</th>
            <th style="min-width: 120px;">物业公司</th>
            <th style="width: 100px;">负责人</th>
            <th style="width: 80px;">状态</th>
            <th style="width: 160px;">创建时间</th>
            <th style="width: 150px;">操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="row in tableData" :key="row.id">
            <td>{{ row.id }}</td>
            <td><span class="tag tag-code">{{ row.communityCode }}</span></td>
            <td><strong>{{ row.communityName }}</strong></td>
            <td>{{ row.address }}</td>
            <td>{{ row.province }}{{ row.city }}{{ row.district }}</td>
            <td>{{ row.totalBuildings }}</td>
            <td>{{ row.totalUnits }}</td>
            <td>{{ row.propertyCompany }}</td>
            <td>{{ row.managerName }}</td>
            <td>
                <span class="status-tag" :class="getStatusClass(row.status)">
                  {{ getStatusLabel(row.status) }}
                </span>
            </td>
            <td class="text-muted">{{ row.createTime }}</td>
            <td>
              <div class="action-group">
                <button class="btn-action btn-action--edit" @click="handleEdit(row)">编辑</button>
                <button class="btn-action btn-action--delete" @click="handleDelete(row)">删除</button>
              </div>
            </td>
          </tr>
          <tr v-if="!tableData.length">
            <td colspan="12" class="empty-state">
              <div class="empty-state__icon">📋</div>
              <p>暂无数据</p>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页 -->
      <div class="pagination">
        <span class="pagination__info">共 {{ pagination.total }} 条</span>
        <div class="pagination__controls">
          <button class="btn btn-sm btn-ghost" :disabled="pagination.current <= 1" @click="handleCurrentChange(pagination.current - 1)">上一页</button>
          <span class="pagination__current">{{ pagination.current }}</span>
          <span class="pagination__total">/ {{ Math.ceil(pagination.total / pagination.pageSize) || 1 }}</span>
          <button class="btn btn-sm btn-ghost" :disabled="pagination.current >= Math.ceil(pagination.total / pagination.pageSize)" @click="handleCurrentChange(pagination.current + 1)">下一页</button>
        </div>
      </div>
    </div>

    <!-- 弹窗 -->
    <div v-if="dialogVisible" class="dialog-overlay" @click.self="dialogVisible = false">
      <div class="dialog">
        <div class="dialog__header">
          <h3 class="dialog__title">{{ dialogTitle }}</h3>
          <button class="dialog__close" @click="dialogVisible = false">✕</button>
        </div>
        <div class="dialog__body">
          <div class="form-grid">
            <div class="form-field">
              <label class="form-label required">小区编码</label>
              <input v-model="form.communityCode" class="form-input" placeholder="请输入小区编码" />
            </div>
            <div class="form-field">
              <label class="form-label required">小区名称</label>
              <input v-model="form.communityName" class="form-input" placeholder="请输入小区名称" />
            </div>
            <div class="form-field full-width">
              <label class="form-label">地址</label>
              <input v-model="form.address" class="form-input" placeholder="请输入详细地址" />
            </div>
            <div class="form-field full-width">
              <label class="form-label">省/市/区</label>
              <el-cascader
                v-model="form.regionSelected"
                :options="regionData"
                :props="{ checkStrictly: false }"
                placeholder="请选择省/市/区"
                clearable
                class="form-cascader"
              />
            </div>
            <div class="form-field">
              <label class="form-label">楼栋数</label>
              <input v-model.number="form.totalBuildings" class="form-input" type="number" min="0" />
            </div>
            <div class="form-field">
              <label class="form-label">单元数</label>
              <input v-model.number="form.totalUnits" class="form-input" type="number" min="0" />
            </div>
            <div class="form-field">
              <label class="form-label">物业公司</label>
              <input v-model="form.propertyCompany" class="form-input" placeholder="请输入物业公司" />
            </div>
            <div class="form-field">
              <label class="form-label">负责人</label>
              <el-select
                v-model="form.managerId"
                placeholder="请选择物业管理员"
                clearable
                @change="handleManagerChange"
                class="form-select"
              >
                <el-option
                  v-for="m in managerList"
                  :key="m.id"
                  :label="m.fullName"
                  :value="m.id"
                />
              </el-select>
            </div>
            <div class="form-field">
              <label class="form-label">负责人电话</label>
              <input v-model="form.managerPhone" class="form-input" placeholder="自动填充" readonly />
            </div>
            <div class="form-field">
              <label class="form-label">状态</label>
              <div class="radio-group">
                <label class="radio-label">
                  <input type="radio" :value="1" v-model="form.status" /> 启用
                </label>
                <label class="radio-label">
                  <input type="radio" :value="0" v-model="form.status" /> 停用
                </label>
              </div>
            </div>
          </div>
        </div>
        <div class="dialog__footer">
          <button class="btn btn-ghost" @click="dialogVisible = false">取消</button>
          <button class="btn btn-primary" @click="handleSubmit">确定</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* ===== 页面容器 ===== */
.page-container {
  padding: 24px;
  max-width: 1440px;
  margin: 0 auto;
}

/* ===== 页面头部 ===== */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 12px;
}

.page-header__left {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
}

.page-subtitle {
  font-size: 14px;
  color: #94a3b8;
}

/* ===== 搜索区域 ===== */
.search-section {
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #e8edf4;
  padding: 20px 24px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.search-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: flex-end;
}

.search-row__item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1 1 160px;
  min-width: 140px;
}

.search-row__label {
  font-size: 13px;
  font-weight: 500;
  color: #475569;
}

.search-row__actions {
  display: flex;
  gap: 8px;
  padding-bottom: 1px;
}

/* ===== 表格区域 ===== */
.table-section {
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #e8edf4;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.table-section__header {
  padding: 14px 20px;
  border-bottom: 1px solid #f1f4f9;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-section__count {
  font-size: 14px;
  color: #64748b;
}

.table-wrap {
  overflow-x: auto;
  padding: 0 4px;
}

.table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.table thead th {
  padding: 12px 14px;
  text-align: left;
  font-weight: 600;
  color: #64748b;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  background: #f8fafc;
  border-bottom: 1px solid #e8edf4;
  white-space: nowrap;
}

.table tbody td {
  padding: 12px 14px;
  border-bottom: 1px solid #f1f4f9;
  color: #1e293b;
  vertical-align: middle;
}

.table tbody tr:hover {
  background: #f8fafc;
}

.table tbody tr:last-child td {
  border-bottom: none;
}

/* ===== 标签 ===== */
.tag {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.tag-code {
  background: #f1f4f9;
  color: #475569;
  font-family: monospace;
}

/* ===== 状态标签 ===== */
.status-tag {
  display: inline-block;
  padding: 3px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 500;
}

.status-tag.success {
  background: #e6f7e6;
  color: #16a34a;
}

.status-tag.danger {
  background: #fde8e8;
  color: #dc2626;
}

/* ===== 操作按钮组 ===== */
.action-group {
  display: flex;
  gap: 4px;
}

.btn-action {
  padding: 4px 12px;
  border: none;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: transparent;
}

.btn-action--edit {
  color: #2563eb;
}

.btn-action--edit:hover {
  background: #eff6ff;
}

.btn-action--delete {
  color: #dc2626;
}

.btn-action--delete:hover {
  background: #fef2f2;
}

/* ===== 通用按钮 ===== */
.btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 18px;
  border: 1px solid transparent;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-sm {
  padding: 5px 14px;
  font-size: 13px;
}

.btn .icon {
  flex-shrink: 0;
}

.btn-primary {
  background: #2563eb;
  color: #fff;
}

.btn-primary:hover {
  background: #1d4ed8;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

.btn-ghost {
  background: transparent;
  border-color: #e2e8f0;
  color: #475569;
}

.btn-ghost:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
}

.btn-danger {
  background: #dc2626;
  color: #fff;
}

.btn-danger:hover {
  background: #b91c1c;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  box-shadow: none !important;
}

/* ===== 表单输入 ===== */
.form-input {
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  background: #ffffff;
  color: #1e293b;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  width: 100%;
}

.form-input:hover {
  border-color: #cbd5e1;
}

.form-input:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

textarea.form-input {
  resize: vertical;
  min-height: 80px;
  font-family: inherit;
}

/* ===== 单选框 ===== */
.radio-group {
  display: flex;
  gap: 20px;
  padding-top: 4px;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  font-size: 14px;
  color: #475569;
}

.radio-label input[type="radio"] {
  width: 16px;
  height: 16px;
  accent-color: #2563eb;
  cursor: pointer;
}

/* ===== 分页 ===== */
.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px;
  border-top: 1px solid #f1f4f9;
  flex-wrap: wrap;
  gap: 12px;
}

.pagination__info {
  font-size: 14px;
  color: #94a3b8;
}

.pagination__controls {
  display: flex;
  gap: 6px;
  align-items: center;
}

.pagination__current {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 32px;
  height: 32px;
  padding: 0 8px;
  border-radius: 6px;
  background: #2563eb;
  color: #fff;
  font-size: 14px;
  font-weight: 600;
}

.pagination__total {
  color: #94a3b8;
  font-size: 14px;
  margin-left: 2px;
}

/* ===== 空状态 ===== */
.empty-state {
  text-align: center;
  padding: 60px 20px !important;
}

.empty-state__icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.empty-state p {
  margin: 0;
  color: #94a3b8;
  font-size: 14px;
}

/* ===== 弹窗 ===== */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(15, 23, 42, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease;
}

.dialog {
  background: #ffffff;
  border-radius: 16px;
  width: 720px;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 24px 80px rgba(0, 0, 0, 0.2);
  animation: slideUp 0.3s ease;
}

.dialog__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f1f4f9;
  flex-shrink: 0;
}

.dialog__title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.dialog__close {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  font-size: 18px;
  color: #94a3b8;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.dialog__close:hover {
  background: #f1f4f9;
  color: #475569;
}

.dialog__body {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

.dialog__footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 16px 24px;
  border-top: 1px solid #f1f4f9;
  flex-shrink: 0;
}

/* ===== 表单网格 ===== */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px 20px;
}

.form-grid .form-field.full-width {
  grid-column: 1 / -1;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: #475569;
}

.form-label.required::after {
  content: '*';
  color: #dc2626;
  margin-left: 2px;
}

/* ===== 级联选择器和下拉框 ===== */
.form-cascader {
  width: 100%;
}

.form-select {
  width: 100%;
}

.form-select :deep(.el-input__wrapper) {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  box-shadow: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.form-select :deep(.el-input__wrapper:hover) {
  border-color: #cbd5e1;
}

.form-select :deep(.el-input__wrapper.is-focus) {
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.form-cascader :deep(.el-input__wrapper) {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  box-shadow: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.form-cascader :deep(.el-input__wrapper:hover) {
  border-color: #cbd5e1;
}

.form-cascader :deep(.el-input__wrapper.is-focus) {
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.text-muted {
  color: #94a3b8;
  font-size: 13px;
}

/* ===== 动画 ===== */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.96);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .page-container {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .search-row__item {
    flex: 1 1 100%;
    min-width: unset;
  }

  .search-row__actions {
    width: 100%;
  }

  .search-row__actions .btn {
    flex: 1;
    justify-content: center;
  }

  .dialog {
    width: 95%;
    max-height: 90vh;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .form-grid .form-field.full-width {
    grid-column: 1;
  }

  .pagination {
    flex-direction: column;
    align-items: center;
    gap: 8px;
  }

  .table-wrap {
    padding: 0;
  }

  .table thead th,
  .table tbody td {
    padding: 10px 12px;
    font-size: 13px;
  }
}
</style>
