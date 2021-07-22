<script lang="ts">
  import type { SnackbarOptions } from './types'
  import { setContext } from 'svelte'
  import Snackbar from './index.svelte'
  import snackbarContextKey from './SnackbarContextKey'
  import { SnackbarPosition } from './SnackbarPosition'

  export let position: SnackbarPosition = SnackbarPosition.TOP_MIDDLE
  let registeredSnackbars = new Set<SnackbarOptions>()
  function removeSnackbar(key, closedEarly) {
    registeredSnackbars.delete(key)
    if (typeof key['resolveExpiredPromise'] === 'function') {
      key.resolveExpiredPromise(!closedEarly)
    }
    registeredSnackbars = registeredSnackbars
  }

  export function showSnackbar(options: SnackbarOptions): {
    close: VoidFunction
    expired: Promise<boolean>
  } {
    const {
      component = Snackbar,
      props = {} as SnackbarOptions['props'],
      duration = 8000,
    } = options
    const key = { component, props } as SnackbarOptions
    const originalCloseCallback = props.closeCallback
    key.props.closeCallback = function close() {
      clearTimeout(key['timeoutID'])
      removeSnackbar(key, true)
      if (typeof originalCloseCallback === 'function') {
        originalCloseCallback()
      }
    }
    key['timeoutID'] = setTimeout(removeSnackbar, duration, key, false)
    registeredSnackbars.add(key)
    registeredSnackbars = registeredSnackbars
    return {
      close: key.props.closeCallback,
      expired: new Promise((resolve) => (key['resolveExpiredPromise'] = resolve)),
    }
  }
  setContext(snackbarContextKey, showSnackbar)
</script>

<slot {showSnackbar} />
<div class="snackbar-stack {position}">
  {#each [...registeredSnackbars.values()] as key (key)}
    <svelte:component this={key.component} {...key.props} />
  {/each}
</div>

<style>
  :root {
    --snackbar-horizontal-offset: 5%;
    --snackbar-vertical-offset: 0;
  }
  .snackbar-stack {
    position: fixed;
    z-index: 2000;
    display: flex;
    flex-direction: column;
  }

  .snackbar-stack.left {
    left: var(--snackbar-horizontal-offset);
    align-items: flex-start;
  }

  .snackbar-stack.right {
    right: var(--snackbar-horizontal-offset);
    align-items: flex-end;
  }

  .snackbar-stack.h-middle {
    left: 50%;
    transform: translateX(-50%);
    align-items: center;
  }

  .snackbar-stack.h-middle.top {
    top: var(--snackbar-vertical-offset);
  }

  .snackbar-stack.h-middle.bottom {
    bottom: var(--snackbar-vertical-offset);
  }

  .snackbar-stack.v-middle {
    top: 50%;
    transform: translateY(-50%);
  }
</style>
