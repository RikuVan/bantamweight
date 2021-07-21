<script lang="ts">
  import { getContext } from 'svelte'
  import { isAdmin, userStore } from '@store/user'
  import { routerStore } from '@store/router'
  import { User, BoxingGloves } from '@components/icons'
  import Button from './Button.svelte'
  import { post } from '@utils/api'
  import { ApiResult } from '@utils/result'

  type Navigate = (uri: string, replace?: boolean) => void
  const navigate: Navigate = getContext('navigate')

  async function logout() {
    const response = await post('/users/logout')
    ApiResult.match(response, {
      Ok: () => {
        $userStore = {}
        navigate('/')
      },
      default: () => {
        console.log('logout failed')
      },
    })
  }

  $: fullName = $userStore.firstName ? `${$userStore.firstName} ${$userStore.lastName}` : ''
  $: adminUser = isAdmin($userStore)
  $: adminPage = $routerStore.path === '/admin'
</script>

<header>
  <a href="/">
    <BoxingGloves height={60} width={60} />
  </a>
  <nav class:admin={adminPage}>
    {#if adminUser && $routerStore.path === '/me'}
      <a class="page-link" href="/admin">Administration</a>
    {/if}
    <div class="name-with-icon">
      <User height={20} width={20} />
      {#if adminPage}
        <h3><a class="page-link admin-link" href="/me">{fullName}</a></h3>
      {:else}
        <h3>{fullName}</h3>
      {/if}
    </div>
    <Button on:click={logout} class="logout">Log out</Button>
  </nav>
</header>
<main>
  <slot />
</main>

<style>
  header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100vw;
    height: 6rem;
    padding: 0 4.8rem;
    background: var(--orange-gradient);
    box-shadow: 0 -0.4rem 0.9rem 0.2rem rgba(0, 0, 0, 0.5);
    z-index: 10;
    user-select: none;
  }

  nav {
    height: 6rem;
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
  }

  .name-with-icon {
    display: flex;
    margin-left: auto;
  }

  .name-with-icon > h3 {
    margin-left: 8px;
  }

  :global(button.logout) {
    margin-left: 18px;
  }

  a.page-link {
    padding: 0 2em 0.5em;
    color: var(--black);
    font-size: 18px;
    margin: auto;
  }

  a.admin-link {
    padding: 0;
  }

  a:hover {
    opacity: 0.9;
  }

  main {
    padding: 2em;
    max-width: 1200px;
  }
</style>
