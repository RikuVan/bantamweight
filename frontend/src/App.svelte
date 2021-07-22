<script lang="ts">
  import { router } from '@utils/router'
  import type { Params } from 'navaid'
  import { onDestroy, SvelteComponent, setContext } from 'svelte'
  import Login from '@pages/Login.svelte'
  import Me from '@pages/Me.svelte'
  import Admin from '@pages/Admin.svelte'
  import { routerStore } from '@store/router'
  import { userStore } from '@store/user'
  import SnackbarContainer from '@components/snackbar/SnackbarContainer.svelte'

  let pageComponent = Login

  router
    .on('/', handleRoute('/', Login))
    .on('/me', handleRoute('/me', Me))
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

<SnackbarContainer>
  <svelte:component this={pageComponent} navigate={router.route} />
</SnackbarContainer>
