<script lang="ts">
  export let value: string | number
  export let name: string
  export let checked = false
  export let disabled = false
  export let title = null
</script>

<label>
  <input
    bind:checked
    {value}
    {name}
    type="checkbox"
    {disabled}
    on:change
    on:blur
    {...$$restProps}
  />
  <div {title} class="chip">
    <slot />
  </div>
</label>

<style>
  input {
    clip: rect(0, 0, 0, 0);
    width: 1px;
    height: 1px;
    margin: -1px;
    overflow: hidden;
    position: absolute;
  }

  .chip {
    cursor: pointer;
    user-select: none;
    position: relative;
    transition: padding-left 50ms;
    border: 2px solid var(--black);
    border-radius: 2px;
    padding: 0.1em 0.6em;
    color: var(--black);
    background: var(--light-gray);
  }

  .chip:hover {
    border: 2px solid var(--black);
    background-clip: padding-box;
    background: var(--lighter-gray);
  }

  .chip::before {
    transition: width 50ms;
    border: 0 solid var(--black);
    width: 0;
    height: 0.5625em;
    content: '';
    transform: translateY(-50%) rotate(45deg);
    position: absolute;
    left: 0.9em;
    top: 45%;
  }

  input:checked + .chip {
    padding-left: 2em;
    background: #fff;
  }

  input:checked + .chip::before {
    width: 0.3125em;
    border-bottom-width: 0.125em;
    border-right-width: 0.125em;
  }
</style>
