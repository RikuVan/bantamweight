import { getStorageExpiration } from './dates'

export function BrowserStorage(type: 'session' | 'local' = 'session') {
  const storage = window[`${type}Storage`]

  const save = (key: string, payload: Record<string, any> | string) => {
    const data =
      typeof payload === 'string'
        ? payload
        : {
            ...payload,
            expires: getStorageExpiration(),
          }
    storage?.setItem(key, JSON.stringify(data))
  }

  const remove = (key: string) => storage?.removeItem(key)

  const get = (key: string) => {
    const data: { savedAt: Date; [key: string]: any } = JSON.parse(storage.getItem(key))
    if (data && new Date(data.expires).getTime() > new Date().getTime()) {
      return data
    }
    return null
  }

  return {
    save,
    get,
    remove,
  }
}

export const sessionStorage = BrowserStorage()
export const localStorage = BrowserStorage()
