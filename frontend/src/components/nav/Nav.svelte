<script lang="ts">
  import { onMount } from 'svelte'
  import { isAdmin, userStore } from '@store/user'
  import { post } from '@utils/api'
  import { ApiResult } from '@utils/result'
  import { Logout } from '../icons'

  const mediaQuery = '(max-width: 600px)'

  onMount(() => {
    const mediaQueryList = window.matchMedia(mediaQuery)
    mediaQueryList.addEventListener('change', fixBody)
    return () => mediaQueryList.removeEventListener('change', fixBody)
  })

  function fixBody() {
    const body = document.querySelector('body')
    if (open && window.matchMedia(mediaQuery).matches) {
      body.style.position = 'fixed'
    } else {
      body.style.position = 'relative'
    }
  }

  async function logout() {
    const response = await post('/users/logout')
    ApiResult.match(response, {
      Ok: () => {
        $userStore = {}
      },
      default: () => {
        console.log('logout failed')
      },
    })
  }

  $: adminUser = isAdmin($userStore)
</script>

<nav>
  <ul>
    <li>
      <a href="/">Home</a>
    </li>
    <li>
      <a href="/me">Me</a>
    </li>
    {#if adminUser}
      <li>
        <a href="/admin">Admin</a>
      </li>
    {/if}
  </ul>
  <ul class="actions">
    <li>
      <button on:click={logout}>
        <Logout />
        Logout
      </button>
    </li>
  </ul>
</nav>

<style>
  li > button {
    background: none;
    color: inherit;
    border: none;
    padding: 0;
    font: inherit;
    cursor: pointer;
    outline: inherit;
    text-decoration: underline;
    cursor: pointer;
  }

  nav {
    display: grid;
    grid-gap: 1px;
    grid-template-rows: minmax(450px, auto) minmax(80px, auto);
    align-content: center;
    background: var(--black);
    font-weight: 600;
    position: fixed;
    height: 100vh;
    z-index: 60;
    right: 0;
    bottom: 0;
    top: 0;
    left: 0;
    overflow: hidden;
  }

  @media only screen and (min-width: 600px) {
    nav {
      position: absolute;
      grid-template-rows: 350px 100px;
      height: 480px;
      width: 376px;
      left: auto;
      top: 10px;
      right: 10px;
      box-shadow: var(--shadow);
    }

    ul:first-of-type {
      padding-top: 40px;
    }
  }

  ul {
    color: #fff;
    width: 100%;
    max-width: 360px;
    height: 100%;
    list-style: none;
    display: flex;
    margin: auto;
    flex-direction: column;
    padding: 0 40px;
  }

  li {
    cursor: pointer;
    flex: 1 1 0%;
  }

  ul.actions {
    padding-top: 40px;
    width: 100%;
    height: 100%;
    justify-content: center;
    position: relative;
    border-top: 1px solid var(--gray);
  }

  a,
  button {
    font-size: 18px;
    color: var(--light-gray);
  }

  a:hover,
  button:hover {
    color: #fff;
  }
</style>
