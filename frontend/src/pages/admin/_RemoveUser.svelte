<script lang="ts">
  import { getContext, setContext, createEventDispatcher } from 'svelte'
  import { createFormStore } from '@store/form'
  import Field from '@components/Field.svelte'
  import FieldError from '@components/FieldError.svelte'
  import Form from '@components/Form.svelte'
  import Button from '@components/Button.svelte'
  import FormButtons from '@components/FormButtons.svelte'
  import { SnackbarContextKey } from '@components/snackbar'
  import { deleteRequest } from '@utils/api'
  import { ApiResult } from '@utils/result'

  const showSnackbar: Function = getContext(SnackbarContextKey)

  const formStore = createFormStore({
    initialValues: { email: '' },
    onSubmit,
  })

  const dispatch = createEventDispatcher()

  const { form, state } = formStore
  setContext('form', formStore)

  export let email: string
  export let id: number
  export let loading = false

  async function onSubmit() {
    loading = true
    const result = await deleteRequest(`/users/${id}`)
    ApiResult.match(result, {
      Ok: () => {
        showSnackbar({ props: { text: 'User is removed' } })
        dispatch('done')
        loading = false
      },
      default: (e) => {
        showSnackbar({ props: { text: 'Removal failed' } })
        loading = false
      },
    })
  }

  function validateEmail(val: string) {
    return (val + '').trim() !== email && "Must match user's email"
  }
</script>

<Form onSubmit={form.submit}>
  <Field name="email" let:input let:meta validate={validateEmail}>
    <label for="email">Email</label>
    <div>
      <input
        name={input.name}
        type="email"
        value={input.value}
        disabled={loading}
        on:blur={input.onBlur}
        on:focus={input.onFocus}
        on:input={({ currentTarget }) => input.onChange(currentTarget.value)}
      />
      <FieldError {meta} />
    </div>
  </Field>
  <FormButtons>
    <Button type="submit" disabled={loading || $state.invalid}>Submit</Button>
    <Button reverse on:click={() => dispatch('done')} disabled={loading}>Reset</Button>
  </FormButtons>
</Form>
