<script context="module" lang="ts">
  export type UserUpdateByAdmin = {
    id: number
    email: string
    firstName: string
    lastName: string
    roles: string[]
    password?: string
    passwordConfirmation?: string
  }
</script>

<script lang="ts">
  import { createEventDispatcher } from 'svelte'
  import UserAdminForm from './_UserAdminForm.svelte'
  import { Tabs, Tab } from '@components/tabs'
  import RemoveUser from './_RemoveUser.svelte'

  const dispatch = createEventDispatcher()

  let activeTab = 0
  export let roles = []
  export let email: string = undefined

  export let userDetails: Partial<UserUpdateByAdmin> = {
    email: '',
    firstName: '',
    lastName: '',
    roles: [],
  }

  function handleDone() {
    dispatch('done')
  }
</script>

<div class="tab-container">
  <Tabs bind:active={activeTab} full>
    <Tab>Update details</Tab>
    <Tab>Change password</Tab>
    <Tab>Remove</Tab>
  </Tabs>
</div>

<div class="panes">
  {#if activeTab === 0}
    <UserAdminForm inputs="details" {userDetails} on:done={handleDone} {roles} />
  {/if}
  {#if activeTab === 1}
    <UserAdminForm {email} inputs="password" on:done={handleDone} {roles} />
  {/if}
  {#if activeTab === 2}
    <RemoveUser id={userDetails.id} email={userDetails.email} on:done={handleDone} />
  {/if}
</div>

<style>
  .tab-container {
    padding-top: 2em;
    display: flex;
    margin: auto;
    width: 100%;
    max-width: 600px;
  }
</style>
