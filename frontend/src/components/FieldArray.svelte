<script lang="ts">
  import { getContext, onMount } from 'svelte'
  import type { GetFieldValidator } from 'final-form'
  import type { ArrayMutators } from '../stores/form'
  import type { FormStore, ReadableField } from '../stores/form'

  export let validate: ReturnType<GetFieldValidator<any>> = () => undefined
  export let parse: (value: any, name: string) => any = undefined
  export let name: string

  let field: ReadableField

  const { registerField, fields: fieldsMap, form }: FormStore = getContext('form')

  const mutators = Object.keys(form.mutators).reduce((result, key) => {
    result[key] = (...args: any) => form.mutators[key](name, ...args)
    return result
  }, {}) as ArrayMutators

  onMount(() => {
    registerField(name, {
      getValidator: () => validate,
      parse,
    })
    field = fieldsMap.get(name)
  })
</script>

{#if $field && $field.meta}
  <slot
    meta={$field.meta}
    input={$field.input}
    {mutators}
    fields={[...Array($field.meta.length).keys()].map((i) => `${name}[${i}]`)}
  />
{/if}
