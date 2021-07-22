import {
  Config,
  FieldConfig,
  FieldState,
  FieldValidator,
  FormState,
  Subscription,
  createForm,
  fieldSubscriptionItems,
  formSubscriptionItems,
} from 'final-form'

import type { Readable } from 'svelte/store'
import { readable } from 'svelte/store'

type FieldValue = string | number | Date | undefined
export type Validate = FieldValidator<FieldValue>
export type Parse = (value: any, name: string) => any
export type ArrayMutators = {
  insert: (index: number, value: any) => void
  concat: (value: Array<any>) => void
  move: (from: number, to: number) => void
  pop: () => any
  push: (value: any) => void
  remove: (index: number) => any
  removeBatch: (indexes: Array<number>) => any
  shift: () => any
  swap: (indexA: number, indexB: number) => void
  update: (index: number, value: any) => void
  unshift: (value: any) => void
}

function getSubscriptionItems(items: string[]) {
  return items.reduce((result, key) => {
    result[key] = true
    return result
  }, {})
}

const defaultParse = (value: any, _name: string) => (value === '' ? undefined : value)

export function createFormStore<T>(
  config: Config<T>,
  formSubscription?: { [key: string]: boolean }
) {
  const form = createForm(config)
  const fields = new Map<string, ReadableField>()

  const state = readable<FormState<T>>({} as FormState<T>, (set) => {
    const unsubscribe = form.subscribe((newState) => {
      set(newState)
    }, formSubscription ?? getSubscriptionItems(formSubscriptionItems))

    return () => {
      unsubscribe()
    }
  })

  function registerField(
    name: string,
    config: FieldConfig<T[keyof T]> & { parse?: Parse },
    subscription?: Subscription
  ) {
    const field = readable({}, (set) => {
      const parse = config?.parse ? config.parse : defaultParse
      const unsubscribe = form.registerField(
        name as keyof T,
        (fieldState) => {
          const { blur, change, focus, value, ...fieldMeta } = fieldState
          let field = {
            meta: fieldMeta,
            input: {
              name,
              onBlur: blur,
              onChange: (val: any) => {
                change(parse(val, name as string))
              },
              onFocus: focus,
              value: value === undefined ? '' : value,
            },
          }
          set(field)
        },
        subscription || getSubscriptionItems(fieldSubscriptionItems),
        config
      )

      return () => {
        unsubscribe()
      }
    })
    fields.set(name, field as ReadableField)
  }

  return { form, state, fields, registerField }
}

export type FormStore = ReturnType<typeof createFormStore>
export type ReadableField = Readable<{
  input: FieldInputProps
  meta: FieldState<any>
}>

type InputEvent = Event & { currentTarget: EventTarget & HTMLInputElement }
type Value = string | number | undefined | string[]
export type FieldInputProps = {
  name: string
  onBlur: (event?: InputEvent) => void
  onChange: (value: Value) => void
  onFocus: (event?: InputEvent) => void
  value: string | number | undefined | string[]
}
