<script lang="ts">
  import Button from '../Button.svelte'
  import { fly } from 'svelte/transition'

  let _class = null
  export { _class as class }
  export let textClass: string | false | null = null
  export let buttonClass: string | false | null = null
  export let text: string = ''
  export let action: { text: string; callback: () => void } | null = null
  export let closeOnAction = true
  export let closeCallback: () => void
  export let transitionOptions: any = { x: -20, duration: 150 }

  function classes(...args: (string | false | null)[]): string {
    return args.filter((cls) => !!cls).join(' ')
  }
</script>

<div class={classes('snackbar', _class)} transition:fly={transitionOptions}>
  <span class={classes('text', textClass)}>{text}</span>
  {#if action != null}
    <Button
      small={true}
      class="snackbar-btn {classes(buttonClass)}"
      on:click={() => {
        if (typeof action.callback === 'function') {
          action.callback()
        }

        if (closeOnAction) {
          closeCallback()
        }
      }}
    >
      {action.text}
    </Button>
  {/if}
</div>

<style>
  :root {
    --snackbar-radius: 0.625em;
    --snackbar-fg: #fff;
  }

  .snackbar {
    min-width: 16em;
    padding: 0.375em;
    margin-top: 0.5em;
    display: flex;
    align-items: center;
    border-radius: var(--radius);
    background: var(--black);
  }

  .snackbar .text {
    padding: 0.8em 0;
    margin: 0 0.625em;
    color: var(--snackbar-fg);
    flex: 1;
    font-size: 0.95em;
  }

  :global(.snackbar-btn) {
    margin: auto;
    margin-right: 10px;
  }
</style>
