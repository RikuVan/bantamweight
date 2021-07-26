<script lang="ts">
  import { setContext, getContext } from 'svelte'
  import { userStore } from '@store/user'
  import Layout from '@components/Layout.svelte'
  import { createFormStore } from '@store/form'
  import Field from '@components/Field.svelte'
  import FieldError from '@components/FieldError.svelte'
  import Button from '@components/Button.svelte'
  import { post } from '@utils/api'
  import { ApiResult } from '@utils/result'
  import { SnackbarContextKey } from '@components/snackbar'
  import type { SnackbarOptions } from '@components/snackbar/types'
  import Form from '@components/Form.svelte'
  import FormButtons from '@components/FormButtons.svelte'

  let isLoading = false

  type FormValues = {
    oldPassword: string
    newPassword: string
    passwordConfirmation: string
  }

  const initialValues = {
    oldPassword: '',
    newPassword: '',
    passwordConfirmation: '',
  }

  const formStore = createFormStore<FormValues>({
    initialValues,
    onSubmit,
  })

  const { form } = formStore
  setContext('form', formStore)

  const showSnackbar =
    getContext<(props: Partial<SnackbarOptions>) => { close: VoidFunction }>(SnackbarContextKey)

  async function onSubmit(values: FormValues) {
    isLoading = true
    const response = await post('/users/password-change', {
      email: $userStore.email,
      oldPassword: values.oldPassword,
      newPassword: values.newPassword,
    })

    ApiResult.match(response, {
      Ok: () => {
        isLoading = false
        reset()
        showSnackbar({
          props: {
            text: `Your password was updated`,
          },
          duration: 8000,
        })
      },
      default: (e: any) => {
        isLoading = false
        showSnackbar({
          props: {
            text: `An error occurred`,
          },
          duration: 8000,
        })
      },
    })
  }

  function reset() {
    form.reset(initialValues)
  }
</script>

<Layout>
  <Form onSubmit={form.submit} title="Change your password">
    <Field name="oldPassword" let:input let:meta>
      <label for="oldPassword">Current Password</label>
      <input
        name={input.name}
        type="password"
        value={input.value}
        disabled={isLoading}
        on:blur={input.onBlur}
        on:focus={input.onFocus}
        on:input={({ currentTarget }) => input.onChange(currentTarget.value)}
      />
      <FieldError {meta} />
    </Field>
    <Field name="newPassword" let:input let:meta>
      <label for="newPassword">New Password</label>
      <input
        name={input.name}
        type="password"
        value={input.value}
        disabled={isLoading}
        on:blur={input.onBlur}
        on:focus={input.onFocus}
        on:input={({ currentTarget }) => input.onChange(currentTarget.value)}
      />
      <FieldError {meta} />
    </Field>
    <Field name="passwordConfirmation" let:input let:meta>
      <label for="passwordConfirmation">Confirm New Password</label>
      <input
        name={input.name}
        type="password"
        value={input.value}
        disabled={isLoading}
        on:blur={input.onBlur}
        on:focus={input.onFocus}
        on:input={({ currentTarget }) => input.onChange(currentTarget.value)}
      />
      <FieldError {meta} />
    </Field>
    <FormButtons>
      <Button type="submit" disabled={isLoading}>
        {#if isLoading}Logging in...{:else}Submit{/if}</Button
      >
      <Button reverse on:click={reset} disabled={isLoading}>Quit</Button>
    </FormButtons>
  </Form>
</Layout>
