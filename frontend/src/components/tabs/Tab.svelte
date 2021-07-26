<script>
  import { getContext } from 'svelte'

  export let tabid = false
  const activeTab = getContext('tabs:active')
  const id = getContext('tabs:getid')()
  $: active = $activeTab === tabid || $activeTab === id
</script>

<span
  class="tab"
  class:active
  {...$$restProps}
  on:click|preventDefault={() => activeTab.set(tabid === false ? id : tabid)}
>
  <slot />
</span>

<style>
  .tab {
    text-decoration: none;
    padding: 1rem 2rem;
    flex: 0 1 auto;
    color: var(--black);
    border-bottom: 2px solid var(--light-gray);
    text-align: center;
  }

  .tab.active,
  .tab:hover {
    opacity: 1;
    border-bottom: 2px solid var(--gray);
  }

  .tab.active {
    color: var(--orange);
    border-color: var(--gray);
  }

  :global(.is-full > .tab) {
    flex: 1 1 auto;
  }
</style>
