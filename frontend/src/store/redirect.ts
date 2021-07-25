import { derived } from 'svelte/store'
import { routerStore } from './router'
import { userStore } from './user'

export const redirectStore = derived(
  [routerStore, userStore],
  ([$routerStore, $userStore]) => {
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
