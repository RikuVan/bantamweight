<script lang="ts">
  import { getContext, onMount } from 'svelte'
  import type { GetFieldValidator, Subscription } from 'final-form'
  import type { FormStore, ReadableField } from '../store/form'

  export let validate: ReturnType<GetFieldValidator<any>> = () => undefined
  export let parse: (value: any, name: string) => any = undefined
  export let name: string
  export let subscription: Subscription = undefined

  let field: ReadableField

  const { registerField, fields }: FormStore = getContext('form')

  onMount(() => {
    registerField(
      name,
      {
        getValidator: () => validate,
        parse,
      },
      subscription
    )
    field = fields.get(name)
  })
</script>

{#if $field && $field.meta}
  <slot meta={$field.meta} input={$field.input} />
{/if}
