import type { Params } from 'navaid'
import type { Writable } from 'svelte/store'
import { writable } from 'svelte/store'

export type RouterStore = Writable<{ path: string; params: Partial<Record<string, string>> }>
export const routerStore = writable<{ path: string; params: Params }>({
  path: '/',
  params: {},
})
