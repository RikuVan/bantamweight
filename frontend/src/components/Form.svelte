<script lang="ts">
  export let title = ''
  export let onSubmit: HTMLFormElement['onsubmit']
</script>

<form on:submit|preventDefault={onSubmit}>
  <h1>{title}</h1>
  <slot />
</form>

<style>
  form {
    padding-top: 2em;
    display: grid;
    grid-gap: 1em;
    width: 100%;
    margin: auto;
  }

  @media only screen and (min-width: 600px) {
    form {
      grid-template-columns: 1fr 2fr;
      max-width: 600px;
    }

    h1 {
      grid-column: span 2;
    }
  }

  :global(input) {
    background: var(--light-gray);
  }

  :global(input:focus) {
    background: var(--lighter-gray);
  }

  :global(input:disabled) {
    opacity: 0.9;
    cursor: none;
  }
</style>
