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
  import snackbarContextKey from '@components/snackbar/SnackbarContextKey'
  import type { SnackbarOptions } from '@components/snackbar/types'

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

  let formStore = createFormStore<FormValues>({
    initialValues,
    onSubmit,
  })

  const { form, state } = formStore
  setContext('form', formStore)

  const showSnackbar =
    getContext<(props: Partial<SnackbarOptions>) => { close: VoidFunction }>(snackbarContextKey)

  async function onSubmit(values: FormValues) {
    isLoading = true
    const response = await post('/users/password-change', {
      email: $userStore.email,
      oldPassword: values.oldPassword,
      newPassword: values.newPassword,
    })

    console.log(JSON.stringify(response, null, 2))

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
  <form on:submit|preventDefault={form.submit}>
    <h1>Change your password</h1>
    <label for="oldPassword">Current Password</label>
    <Field name="oldPassword" let:input let:meta>
      <input
        name={input.name}
        type="text"
        value={input.value}
        disabled={isLoading}
        on:blur={input.onBlur}
        on:focus={input.onFocus}
        on:input={({ target }) => input.onChange(target.value)}
      />
      <FieldError {meta} />
    </Field>
    <label for="newPassword">New Password</label>
    <Field name="newPassword" let:input let:meta>
      <input
        name={input.name}
        type="text"
        value={input.value}
        disabled={isLoading}
        on:blur={input.onBlur}
        on:focus={input.onFocus}
        on:input={({ target }) => input.onChange(target.value)}
      />
      <FieldError {meta} />
    </Field>
    <label for="passwordConfirmation">Confirm New Password</label>
    <Field name="passwordConfirmation" let:input let:meta>
      <input
        name={input.name}
        type="text"
        value={input.value}
        disabled={isLoading}
        on:blur={input.onBlur}
        on:focus={input.onFocus}
        on:input={({ target }) => input.onChange(target.value)}
      />
      <FieldError {meta} />
    </Field>
    <div class="form-btns">
      <Button type="submit" disabled={isLoading}>
        {#if isLoading}Logging in...{:else}Submit{/if}</Button
      >
      <Button reverse on:click={reset} disabled={isLoading}>Reset</Button>
    </div>
  </form>
</Layout>

<style>
  form {
    padding-top: 2em;
    display: grid;
    grid-template-columns: 1fr 2fr;
    grid-gap: 1em;
    width: 600px;
    margin: auto;
  }

  h1,
  .form-btns {
    grid-column: span 2;
  }

  .form-btns {
    margin-left: auto;
  }

  :global(button + button) {
    margin-left: 8px;
  }

  input {
    background: var(--light-gray);
  }

  input:focus {
    background: var(--lighter-gray);
  }

  input:disabled {
    opacity: 0.9;
    cursor: none;
  }
</style>
