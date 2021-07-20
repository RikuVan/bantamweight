<script lang="ts">
  import { onDestroy } from 'svelte'
  import navaid from 'navaid'
  import type { Params } from 'navaid'
  import Login from './pages/Login.svelte'

  let currentPage = Login
  let params: Params = {}
  let active: string = 'login'

  const router = navaid().on('/', handleRoute('login', Login)).listen()

  function handleRoute(name: string, page: any) {
    return (routeParams: Params) => {
      currentPage = page
      params = routeParams
      active = name
    }
  }

  onDestroy(() => {
    if (router) router.unlisten()
  })
</script>

<svelte:component this={currentPage} navigate={router.route} />
