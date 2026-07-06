import request from '@/utils/request'
import type { ApiResult } from '@/utils/api-types'

/**
 * 字典项
 */
export interface DictItem {
  /** 字典值 */
  value: number
  /** 字典标签 */
  label: string
}

/**
 * 接口：GET /api/v1/dict/{dictType}
 * 功能：获取字典数据
 * 参数：{ dictType: 'repair_type' | 'repair_status' }
 * 返回：ApiResult<DictItem[]>
 */
export const getDict = (dictType: string) => {
  return request.get<DictItem[]>(`/api/v1/dict/${dictType}`)
}
