import type { RouterStore } from './router'
import type { UserStore } from './user'
import { derived } from 'svelte/store'
import { routerStore } from './router'
import { userStore } from './user'

export const redirectStore = derived(
  [routerStore, userStore],
  ([$routerStore, $userStore]) => {
    console.log($routerStore, $userStore)
    if ($routerStore?.path === '/login' && $userStore?.accessToken) {
      return '/'
    } else if ($routerStore?.path !== '/login' && !$userStore?.accessToken) {
      return '/login'
    } else {
      return undefined
    }
  },
  undefined
)
