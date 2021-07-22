import type { SvelteComponentTyped } from 'svelte'
import type { TransitionConfig } from 'svelte/transition'

export type SnackbarOptions = {
  component: SvelteComponentTyped<Record<string, any>>
  props: {
    text: string
    closeCallback?: VoidFunction
    action?: {
      text: string
      callback?: VoidFunction
    }
    closeOnAction?: boolean
    transition?: Function
    transitionOptions?: TransitionConfig
  }
  duration: number
}
