import { derived, writable } from 'svelte/store'

import { ApiResult } from '@utils/result'
import { USER_STORAGE_KEY } from '@utils/api'
import type { Writable } from 'svelte/store'
import { post } from '@utils/api'
import { routerStore } from './router'
import { sessionStorage } from '../utils/browserStorage'

export type UserStore = Writable<User>

export type User = {
  email?: string
  firstName?: string
  lastName?: string
  accessToken?: string
  roles?: string[]
}

const REFRESH_INTERVAL = 15000

export const createUserStore = (initialUser: User) => {
  const user = writable(initialUser)
  let job: number

  refresh()

  async function refresh() {
    const response = await post('/users/refresh')
    ApiResult.match(response, {
      Ok: ({ data }) => {
        user.set(data)
      },
      default: () => {
        user.set({})
      },
    })
  }

  const _shouldAuthenticate = derived(
    routerStore,
    ($routerStore) => {
      if ($routerStore.path === '/login') {
        return false
      }
      return true
    },
    false
  )

  _shouldAuthenticate.subscribe((shouldRefresh) => {
    if (!job && shouldRefresh) {
      job = window.setInterval(refresh, REFRESH_INTERVAL)
    } else if (job) {
      clearInterval(job)
    }
  })

  const subscribe = (fn: Function) => {
    const _unsubscribe = user.subscribe((current) => {
      if (current?.accessToken) {
        sessionStorage.save(USER_STORAGE_KEY, current)
      } else {
        sessionStorage.remove(USER_STORAGE_KEY)
      }
      fn(current)
    })

    const unsubscribe = () => {
      job && clearInterval(job)
      _unsubscribe()
    }
    return unsubscribe
  }

  return {
    ...user,
    subscribe,
  } as UserStore
}

const user = sessionStorage.get(USER_STORAGE_KEY) || {}
if (user) {
  delete user['savedAt']
}
export const userStore = createUserStore(user)

export const isAdmin = (user: User) => user?.roles && user.roles.includes('admin')
export const loggedIn = (user: User) => user?.accessToken
