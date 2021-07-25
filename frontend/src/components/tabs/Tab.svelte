<script>
  import { getContext } from 'svelte'

  export let tabid = false
  const activeTab = getContext('tabs:active')
  const id = getContext('tabs:getid')()
  $: active = $activeTab === tabid || $activeTab === id
</script>

<!--TODO: write issue to chota to replace a by span-->
<a
  href="/"
  class:active
  {...$$restProps}
  on:click|preventDefault={() => activeTab.set(tabid === false ? id : tabid)}
>
  <slot />
</a>

<style>
  a {
    text-decoration: none;
    padding: 1rem 2rem;
    flex: 0 1 auto;
    color: var(--light-gray);
    border-bottom: 2px solid var(--light-gray);
    text-align: center;
  }

  a[aria-current='page'],
  a.active,
  a:hover {
    opacity: 1;
    border-bottom: 2px solid var(--gray);
  }

  a[aria-current='page'],
  a.active {
    border-color: var(--orange);
  }

  a {
    flex: 1 1 auto;
  }
</style>
