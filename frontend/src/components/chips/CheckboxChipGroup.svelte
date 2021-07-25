<script lang="ts">
  import CheckboxChip from './CheckboxChip.svelte'
  export let items: { value: string | number; label: string; checked: boolean }[] = []
  export let name: string

  if (!items || items.length === 0) {
    console.error('Must have at least one item in the checkbox chip group')
  }
</script>

{#if items != null && items.length !== 0}
  <div role="group">
    {#each items as item (item.value)}
      <CheckboxChip
        {name}
        value={item.value}
        bind:checked={item.checked}
        on:change
        on:blur
        {...$$restProps}
      >
        {item.label || item.value}
      </CheckboxChip>
    {/each}
  </div>
{/if}

<style>
  div {
    display: flex;
    flex-wrap: wrap;
    position: relative;
    margin-bottom: 20px;
  }

  :global(.chip) {
    margin-right: 0.5em;
  }
</style>
