import type { ApiResult } from '@/utils/api-types'
import { getToken, removeToken } from '@/utils/auth'

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? ''

interface RequestConfig {
  params?: Record<string, string | number | boolean | undefined | null>
  headers?: Record<string, string>
}

class RequestError extends Error {
  code: number

  constructor(message: string, code: number) {
    super(message)
    this.name = 'RequestError'
    this.code = code
  }
}

const buildUrl = (url: string, params?: RequestConfig['params']): string => {
  const fullUrl = `${BASE_URL}${url}`
  if (!params) {
    return fullUrl
  }

  const searchParams = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      searchParams.append(key, String(value))
    }
  })

  const query = searchParams.toString()
  return query ? `${fullUrl}?${query}` : fullUrl
}

const request = async <T>(
  url: string,
  options: RequestInit & RequestConfig = {},
): Promise<ApiResult<T>> => {
  const { params, headers, ...fetchOptions } = options
  const token = getToken()

  const response = await fetch(buildUrl(url, params), {
    ...fetchOptions,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...headers,
    },
  })

  let result: ApiResult<T>
  try {
    result = (await response.json()) as ApiResult<T>
  } catch {
    throw new RequestError('服务器响应异常', response.status)
  }

  if (result.code === 401) {
    removeToken()
    const { default: router } = await import('@/router')
    if (router.currentRoute.value.path !== '/login') {
      await router.replace({
        path: '/login',
        query: { redirect: router.currentRoute.value.fullPath },
      })
    }
    throw new RequestError(result.message || '未登录或登录已过期', 401)
  }

  if (result.code !== 200) {
    throw new RequestError(result.message || '请求失败', result.code)
  }

  return result
}

export default {
  get<T>(url: string, config?: RequestConfig) {
    return request<T>(url, { method: 'GET', ...config })
  },
  post<T>(url: string, data?: unknown, config?: RequestConfig) {
    return request<T>(url, {
      method: 'POST',
      body: data !== undefined ? JSON.stringify(data) : undefined,
      ...config,
    })
  },
  put<T>(url: string, data?: unknown, config?: RequestConfig) {
    return request<T>(url, {
      method: 'PUT',
      body: data !== undefined ? JSON.stringify(data) : undefined,
      ...config,
    })
  },
  delete<T>(url: string, config?: RequestConfig) {
    return request<T>(url, { method: 'DELETE', ...config })
  },
}

export { RequestError }
