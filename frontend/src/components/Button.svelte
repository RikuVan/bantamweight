<script lang="ts">
  let _class = null
  export { _class as class }
  export let type = 'button'
</script>

<button on:click {type} class={_class}>
  <slot />
</button>

<style>
  button {
    display: inline-block;
    vertical-align: middle;
    background: var(--black);
    color: white;
    padding: 8px;
    border-radius: 8px;
    text-transform: uppercase;
    cursor: pointer;
  }

  button:hover {
    opacity: 0.9;
  }
</style>
