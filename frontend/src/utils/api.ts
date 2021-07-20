import { ApiResult, toApiResult } from './result'

import { sessionStorage } from './browserStorage'

export const USER_STORAGE_KEY = 'welterweight_user'

export async function apiRequest(url: string, method: string, body: any, requestHeaders: any) {
  const auth = sessionStorage.get(USER_STORAGE_KEY)
  const token = auth?.accessToken
  const headers = { ...requestHeaders }
  if (token) {
    headers['X-BANTAMWEIGHT-ACCESS-TOKEN'] = token
  }

  const response = await fetch(url, {
    method,
    headers,
    body,
  })

  if (response.ok) {
    let data = {}

    if (response.headers.get('Content-Type')?.includes('json')) {
      data = await response.json()
    }
    return toApiResult(response.status, data)
  }
  return toApiResult(response.status, response)
}

function jsonRequest(url: string, method: string, data?: any): Promise<ApiResult> {
  const body = data ? JSON.stringify(data) : null
  return apiRequest(url, method, body, {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  })
}

export const get = (url: string) => jsonRequest(url, 'GET')
export const post = (url: string, data?: any) => jsonRequest(url, 'POST', data)
export const put = (url: string, data?: any) => jsonRequest(url, 'PUT', data)
export const deleteRequest = (url: string) => apiRequest(url, 'DELETE', undefined, {})
