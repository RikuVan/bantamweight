<script lang="ts">
  let _class = null
  export { _class as class }
  export let type = 'button'
  export let reverse = false
  export let disabled = false
</script>

<button on:click {type} class={_class} class:reverse {disabled}>
  <slot />
</button>

<style>
  button {
    display: inline-block;
    vertical-align: middle;
    background: var(--black);
    color: white;
    padding: 8px;
    border: 3px solid var(--black);
    border-radius: 8px;
    text-transform: uppercase;
    cursor: pointer;
  }

  button:disabled {
    opacity: 0.9;
    cursor: none;
  }

  .reverse {
    background: #fff;
    border: 3px solid var(--black);
    color: var(--black);
  }

  button:hover {
    opacity: 0.9;
  }
</style>
