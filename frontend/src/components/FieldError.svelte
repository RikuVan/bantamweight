<script lang="ts">
  import type { FieldState } from 'final-form'
  export let meta: FieldState<any>
  export let showWhen: (meta: FieldState<any>) => boolean = showError

  function showError(meta: FieldState<any>) {
    return meta.touched && meta.error
  }

  $: show = showWhen(meta)
</script>

{#if show}
  <div class="error">{meta.error}</div>
{/if}

<style>
  .error {
    color: var(--error-color);
    padding: 8px;
  }
</style>
