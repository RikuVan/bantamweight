<script lang="ts">
  import { getContext, setContext, createEventDispatcher } from 'svelte'
  import { createFormStore } from '@store/form'
  import Field from '@components/Field.svelte'
  import FieldError from '@components/FieldError.svelte'
  import CheckboxChipGroup from '@components/chips/CheckboxChipGroup.svelte'
  import type { UserUpdateByAdmin } from './_ExistingUserAdmin.svelte'
  import Form from '@components/Form.svelte'
  import Button from '@components/Button.svelte'
  import FormButtons from '@components/FormButtons.svelte'
  import { post, put } from '@utils/api'
  import { ApiResult } from '@utils/result'
  import { SnackbarContextKey } from '@components/snackbar'
  import { isEmail } from '@utils/validation'

  const dispatch = createEventDispatcher()

  const showSnackbar: Function = getContext(SnackbarContextKey)

  export let inputs: 'all' | 'details' | 'password' = 'all'
  export let roles: string[]
  export let email: string | undefined = undefined

  let initialValues = {}
  const initialPassword = { password: '', passwordConfirmation: '' }
  let loading = false

  export let userDetails: Partial<UserUpdateByAdmin> = {
    email: '',
    firstName: '',
    lastName: '',
    roles: [],
  }

  $: if (inputs || userDetails) {
    if (inputs === 'password') initialValues = initialPassword
    else if (inputs == 'details') initialValues = userDetails
    else
      initialValues = {
        ...userDetails,
        ...initialPassword,
      }
  }

  let formStore = createFormStore<UserUpdateByAdmin>({
    initialValues,
    onSubmit,
    validate,
  })

  $: form.setConfig('initialValues', initialValues)

  const { form, state } = formStore
  setContext('form', formStore)

  function updateRoles(target: EventTarget, values: string[]) {
    const { checked, value } = target as HTMLInputElement
    if (!checked) {
      return values.filter((v) => v !== value)
    }
    return [...values, value]
  }

  const routes = {
    all: ({ passwordConfirmation, ...payload }: Partial<UserUpdateByAdmin>) =>
      post('/users', payload),
    details: (payload: Partial<UserUpdateByAdmin>) => put(`/users/${payload.id}`, payload),
    password: ({ password }: { password: string }) =>
      post('/users/password-change', {
        email,
        newPassword: password,
        oldPassword: null,
      }),
  }

  const notifications = {
    all: 'The user was created',
    details: 'The user details are updated',
    password: 'The password is updated',
  }

  async function onSubmit({
    passwordConfirmation,
    ...rest
  }: UserUpdateByAdmin & { passwordConfirmation: string }) {
    const result = await routes[inputs](rest as any)
    ApiResult.match(result, {
      Ok: () => {
        showSnackbar({ props: { text: notifications[inputs] } })
        dispatch('done')
      },
      default: (e) => {
        console.log('Failed course update', e)
        showSnackbar({ props: { text: 'The user creation/ update failed' } })
      },
    })
  }

  function validate(values: Partial<UserUpdateByAdmin>) {
    let errors = {}

    if (inputs === 'all' || inputs === 'details') {
      if (!values.email || !isEmail(values.email)) {
        errors['email'] = 'Valid email required'
      }
      if (!values.firstName) {
        errors['firstName'] = 'First name required'
      }
      if (!values.lastName) {
        errors['lastName'] = 'Last name required'
      }
      if (!values.roles || !values.roles.length) {
        errors['roles'] = 'At least one role required'
      }
    }

    if (inputs === 'all' || inputs === 'password') {
      if (!values.password || values.password.length < 8) {
        errors['password'] = 'Password must be at least 8 characters long'
      }
      if (!values.passwordConfirmation || values.passwordConfirmation !== values.password) {
        errors['passwordConfirmation'] = 'Passwords must match'
      }
    }

    return errors
  }

  function getChipOptions(initVals: Partial<UserUpdateByAdmin>) {
    return roles.map((value) => ({
      label: value,
      value,
      checked: (initVals.roles || []).some((role) => role === value),
    }))
  }

  function getTitle() {
    switch (inputs) {
      case 'all':
        return 'New User'
      case 'password':
        return 'User Password'
      default:
        return 'User'
    }
  }

  $: roleItems = roles && getChipOptions(initialValues)
</script>

<Form onSubmit={form.submit} title={getTitle()}>
  {#if inputs === 'all' || inputs === 'details'}
    <Field name="email" let:input let:meta>
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
    <Field name="firstName" let:input let:meta>
      <label for="firstName">First name</label>
      <div>
        <input
          name={input.name}
          type="text"
          value={input.value}
          disabled={loading}
          on:blur={input.onBlur}
          on:focus={input.onFocus}
          on:input={({ currentTarget }) => input.onChange(currentTarget.value)}
        />
        <FieldError {meta} />
      </div>
    </Field>
    <Field name="lastName" let:input let:meta>
      <label for="lastName">Last name</label>
      <div>
        <input
          name={input.name}
          type="text"
          value={input.value}
          disabled={loading}
          on:blur={input.onBlur}
          on:focus={input.onFocus}
          on:input={({ currentTarget }) => input.onChange(currentTarget.value)}
        />
        <FieldError {meta} />
      </div>
    </Field>
    <Field name="roles" let:input let:meta>
      <label for="roles">Roles</label>
      {#if roleItems.length}
        <div>
          <CheckboxChipGroup
            items={roleItems}
            name="roles"
            on:change={({ currentTarget }) =>
              input.onChange(updateRoles(currentTarget, input.value))}
            on:blur={() => input.onBlur()}
          />
          <FieldError {meta} />
        </div>
      {:else}
        loading roles...
      {/if}
    </Field>
  {/if}
  {#if inputs === 'all' || inputs === 'password'}
    <Field name="password" let:input let:meta>
      <label for="password">Password</label>
      <div>
        <input
          name={input.name}
          type="password"
          value={input.value}
          disabled={loading}
          on:blur={input.onBlur}
          on:focus={input.onFocus}
          on:input={({ currentTarget }) => input.onChange(currentTarget.value)}
        />
        <FieldError {meta} />
      </div>
    </Field>
    <Field name="passwordConfirmation" let:input let:meta>
      <label for="passwordConfirmation">Confirm password</label>
      <div>
        <input
          name={input.name}
          type="password"
          value={input.value}
          disabled={loading}
          on:blur={input.onBlur}
          on:focus={input.onFocus}
          on:input={({ currentTarget }) => input.onChange(currentTarget.value)}
        />
        <FieldError {meta} />
      </div>
    </Field>
  {/if}
  <FormButtons>
    <Button type="submit" disabled={loading || $state.invalid}>Submit</Button>
    <Button reverse on:click={() => dispatch('done')} disabled={loading}>Reset</Button>
  </FormButtons>
</Form>
