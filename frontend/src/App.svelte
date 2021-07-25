<script lang="ts">
  import { router } from '@utils/router'
  import type { Params } from 'navaid'
  import { onDestroy, SvelteComponent, setContext, onMount } from 'svelte'
  import Login from '@pages/Login.svelte'
  import Home from '@pages/Home.svelte'
  import Me from '@pages/Me.svelte'
  import Admin from '@pages/Admin.svelte'
  import { routerStore } from '@store/router'
  import { userStore, loggedIn, isAdmin } from '@store/user'
  import { redirectStore } from '@store/redirect'
  import { SnackbarContainer } from '@components/snackbar'
  import { USER_STORAGE_KEY } from '@utils/api'

  let pageComponent = Login

  router
    .on('/', handleRoute('/', Home))
    .on('/login', handleRoute('/login', Login, false))
    .on('/me', handleRoute('/me', Me))
    .on('/admin', handleAdminRoute('/admin', Admin))
    .listen()

  setContext('router', routerStore)

  $: if ($redirectStore) router.route($redirectStore)

  function handleRoute(path: string, page: typeof SvelteComponent, auth = true) {
    return (params: Params) => {
      const authenticated = !auth || loggedIn($userStore || sessionStorage.get(USER_STORAGE_KEY))
      if (authenticated) {
        pageComponent = page
        $routerStore = {
          path,
          params,
        }
      } else {
        router.route('/login')
      }
    }
  }

  function handleAdminRoute(path: string, page: typeof SvelteComponent) {
    return (params: Params) => {
      const authorized = isAdmin($userStore || sessionStorage.get(USER_STORAGE_KEY))
      if (authorized) {
        handleRoute(path, page)(params)
      } else {
        router.route('/')
      }
    }
  }

  onDestroy(() => {
    if (router) router.unlisten()
  })
</script>

<SnackbarContainer>
  <svelte:component this={pageComponent} />
</SnackbarContainer>
