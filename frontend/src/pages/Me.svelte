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

  const { form } = formStore
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
    <Field name="oldPassword" let:input let:meta>
      <label for="oldPassword">Current Password</label>
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
    <Field name="newPassword" let:input let:meta>
      <label for="newPassword">New Password</label>
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
    <Field name="passwordConfirmation" let:input let:meta>
      <label for="passwordConfirmation">Confirm New Password</label>
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
    grid-gap: 1em;
    width: 100%;
    margin: auto;
  }

  @media only screen and (min-width: 600px) {
    form {
      grid-template-columns: 1fr 2fr;
      max-width: 600px;
    }

    h1,
    .form-btns {
      grid-column: span 2;
    }
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
