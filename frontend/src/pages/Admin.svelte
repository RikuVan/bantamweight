<script lang="ts">
  import { get } from '@utils/api'
  import { ApiResult } from '@utils/result'
  import Layout from '@components/Layout.svelte'
  import { List, Item } from '@components/list/index'
  import { Edit } from '@components/icons'
  import Button from '@components/Button.svelte'
  import Loading from '@components/Loading.svelte'
  import { onMount } from 'svelte'
  import ExistingUserAdmin from './_ExistingUserAdmin.svelte'
  import UserAdminForm from './_UserAdminForm.svelte'

  let roles = []

  onMount(async () => {
    const response = await get('/users/roles')
    ApiResult.match(response, {
      Ok: ({ data }) => {
        roles = data
      },
      default: () => {
        console.error('Could not load user roles')
      },
    })
  })

  type UserItem = {
    id: string
    email?: string
    firstName?: string
    lastName?: string
    roles?: string[]
  }

  let userLookup = new Map()
  let newUser = false
  let editing = false

  const initialValues = {
    email: '',
    firstNames: '',
    lastName: '',
    roles: [],
  }

  let values = initialValues

  function startEditing(id?: number) {
    const user = userLookup.get(id) || {}
    values = { ...values, ...user }
    editing = true
    newUser = !id
  }

  async function getUsers() {
    const response = await get('/users')
    return ApiResult.match(response, {
      Ok: ({ data }) => {
        data.forEach((user: UserItem) => {
          userLookup.set(user.id, user)
        })
        return data
      },
      NotFound: () => 'Not found',
      default: () => 'An error occurred',
    })
  }

  function reset() {
    editing = false
    values = initialValues
    promise = getUsers()
  }

  let promise = getUsers()
</script>

<Layout>
  <h1>Admin</h1>
  {#await promise}
    <Loading />
  {:then users}
    {#if !editing}
      <div class="user-list-header">
        <h3>Users</h3>
        <Button on:click={() => startEditing()}>Add User</Button>
      </div>
      <List>
        {#each users as { id, firstName, lastName, email, roles }}
          <Item>
            <div>{id}</div>
            <div>{firstName} {lastName}</div>
            <div>{email}</div>
            {#if Array.isArray(roles)}
              <div>{roles.join(', ')}</div>
            {:else}
              <div />
            {/if}
            <span on:click={() => startEditing(id)}>
              <Edit width="20" height="20" />
            </span>
          </Item>
        {/each}
      </List>
    {/if}
    {#if editing}
      {#if newUser}
        <UserAdminForm {roles} on:done={reset} />
      {:else}
        <ExistingUserAdmin userDetails={values} on:done={reset} {roles} />
      {/if}
    {/if}
  {:catch err}
    <h3>Error:</h3>
    <pre>{JSON.stringify(err, null, 2)}</pre>
  {/await}
</Layout>

<style>
  .user-list-header {
    display: flex;
    justify-content: space-between;
    padding-bottom: 1em;
  }
</style>
