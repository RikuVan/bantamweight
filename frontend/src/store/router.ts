import type { Params } from 'navaid'
import { writable } from 'svelte/store'

export const routerStore = writable<{ path: string; params: Params }>({
  path: '/',
  params: {},
})
