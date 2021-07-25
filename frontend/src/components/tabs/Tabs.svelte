<script>
  import { setContext } from 'svelte'
  import { writable } from 'svelte/store'
  export let active = 0
  export let full = false
  const activeTab = writable(active)
  let num = 0
  setContext('tabs:getid', () => num++)
  setContext('tabs:active', activeTab)
  $: active = $activeTab
</script>

<nav class:tabs={1} class:is-full={full} {...$$restProps} role="group">
  <slot />
</nav>

<style>
  .tabs {
    display: flex;
  }
</style>
