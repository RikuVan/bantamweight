<script lang="ts">
  import { userStore } from '@store/user'
  import { BoxingGloves } from '@components/icons'
  import { Nav, Hamburger } from '@components/nav/index'

  let open = false

  $: fullName = $userStore.firstName ? `${$userStore.firstName} ${$userStore.lastName}` : ''
</script>

<header>
  <div class="header-start">
    <a href="/">
      <BoxingGloves height={50} width={50} />
    </a>
    <user->
      <h3>{fullName}</h3>
      <small>{$userStore.email}</small>
    </user->
  </div>
  <Hamburger bind:open />
  <Nav {open} close={() => (open = false)} />
</header>
<main>
  <slot />
</main>

<style>
  user- {
    padding-left: 1em;
    display: flex;
    flex-direction: column;
  }
  header {
    display: flex;
    position: relative;
    align-items: center;
    justify-content: space-between;
    width: 100vw;
    height: 6rem;
    padding: 0 1rem;
    background: var(--orange-gradient);
    box-shadow: 0 -0.4rem 0.9rem 0.2rem rgba(0, 0, 0, 0.5);
    z-index: 10;
    user-select: none;
  }

  h3 {
    font-weight: bold;
  }

  .header-start {
    display: flex;
  }
  :global(button.logout) {
    margin-left: 18px;
  }

  main {
    padding: 2em;
    max-width: 1200px;
    margin: auto;
  }
</style>
