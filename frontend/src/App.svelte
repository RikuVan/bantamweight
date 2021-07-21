<script lang="ts">
  import { onDestroy, SvelteComponent, setContext } from 'svelte'
  import type { Router, Params } from 'navaid'
  import Login from '@pages/Login.svelte'
  import Me from '@pages/Me.svelte'
  import Admin from '@pages/Admin.svelte'
  import { routerStore } from '@store/router'
  import { userStore } from '@store/user'

  // create instance in main to ensure it is ready when accessed by stores
  export let router: Router
  let pageComponent = Login

  router
    .on('/', handleRoute('/', Login))
    .on('/me', handleAuthenticatedRoute('/me', Me))
    .on('/admin', handleAdminRoute('/admin', Admin))
    .listen()

  setContext('router', routerStore)
  setContext('navigate', router.route)

  function handleRoute(path: string, page: typeof SvelteComponent) {
    return (params: Params) => {
      pageComponent = page
      $routerStore = {
        path,
        params,
      }
    }
  }

  function handleAuthenticatedRoute(path: string, page: typeof SvelteComponent) {
    if ($userStore.accessToken) {
      return handleRoute(path, page)
    } else {
      router.route('/')
    }
  }

  function handleAdminRoute(path: string, page: typeof SvelteComponent) {
    if (Array.isArray($userStore.roles) && $userStore.roles.includes('admin')) {
      return handleRoute(path, page)
    } else {
      router.route('/me', true)
    }
  }

  onDestroy(() => {
    if (router) router.unlisten()
  })
</script>

<svelte:component this={pageComponent} navigate={router.route} />
