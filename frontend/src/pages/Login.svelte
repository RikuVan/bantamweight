<script lang="ts">
  import { getContext, onMount } from 'svelte'
  import { post } from '@utils/api'
  import { ApiResult } from '@utils/result'
  import { LockOpen, BoxingGloves } from '@components/icons'
  import { userStore } from '@store/user'
  import type { User } from '../store/user'

  type Navigate = (uri: string, replace?: boolean) => void

  const navigate: Navigate = getContext('navigate')

  let email = ''
  let password = ''
  let isLoading = false
  let isSuccess = false
  let errorResponse = undefined
  let errors: Errors = {}

  type Errors = { email?: string; password?: string }

  onMount(() => {
    const unsubscribe = userStore.subscribe((user: User) => {
      if (user?.accessToken) {
        isSuccess = true
        navigate('/me')
      }
    })
    return () => unsubscribe()
  })

  async function handleSubmit() {
    errors = {}
    if (email.length === 0) {
      errors.email = 'Field should not be empty'
    }
    if (password.length === 0) {
      errors.password = 'Field should not be empty'
    }
    if (Object.keys(errors).length === 0) {
      isLoading = true
      errorResponse = undefined
      const response = await post('/users/login', { email, password })
      ApiResult.match(response, {
        Ok: ({ data }: { data: User }) => {
          $userStore = data
          isSuccess = true
          isLoading = false
          navigate('/me')
        },
        NotFound: () => {
          $userStore = {}
          errorResponse = 'Please check your email and password combination'
          isSuccess = false
          isLoading = false
        },
        Unauthorized: () => {
          $userStore = {}
          errorResponse = 'Please check your email and password combination'
          isSuccess = false
          isLoading = false
        },
        default: () => {
          $userStore = {}
          errorResponse = 'An error occurred'
          isSuccess = false
          isLoading = false
        },
      })
    }
  }
</script>

<main>
  <form on:submit|preventDefault={handleSubmit}>
    {#if isSuccess}
      <div class="success">
        <LockOpen width={24} height={24} />
        <br />
        You've been successfully logged in.
      </div>
    {:else}
      <header>
        <BoxingGloves />
        <h1>Bantamweight</h1>
        <h4>http4k + Svelte</h4>
      </header>

      <label for="email">Email</label>
      <input name="email" placeholder="name@example.com" bind:value={email} />

      <label for="password">Password</label>
      <input name="password" type="password" bind:value={password} />

      <button type="submit">
        {#if isLoading}Logging in...{:else}Log in <LockOpen />{/if}
      </button>

      {#if Object.keys(errors).length > 0}
        <ul class="errors">
          {#each Object.keys(errors) as field}
            <li>{field}: {errors[field]}</li>
          {/each}
        </ul>
      {/if}
    {/if}
    {#if errorResponse}
      <div class="backend-error">{errorResponse}</div>
    {/if}
  </form>
</main>

<style>
  :global(button > svg) {
    margin-bottom: -5px;
  }

  header,
  main {
    display: flex;
    align-items: center;
    flex-direction: column;
    justify-content: center;
  }

  header {
    padding-bottom: 16px;
  }

  main {
    height: 100vh;
  }

  form {
    background-color: var(--orange);
    background-image: var(--orange-gradient);
    padding: 50px;
    width: 320px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    box-shadow: var(--shadow);
    margin-left: auto;
    margin-right: auto;
  }

  label {
    margin: 4px 0;
    align-self: flex-start;
  }

  input {
    border: none;
    border-bottom: 1px solid #ccc;
    margin-bottom: 20px;
    transition: all 300ms ease-in-out;
    width: 100%;
    padding: 8px;
    background: #fff;
  }

  input:focus {
    outline: 0;
    border-bottom: 1px solid #666;
  }

  button {
    margin-top: 20px;
    background: var(--black);
    color: white;
    padding: 10px 0;
    width: 200px;
    border-radius: 25px;
    text-transform: uppercase;
    cursor: pointer;
    transition: all 300ms ease-in-out;
  }

  button:hover {
    transform: translateY(-1.5px);
    box-shadow: 0px 1px 10px 0px rgba(255, 255, 255, 0.58);
  }

  h1 {
    margin: 10px 20px 0 20px;
    font-size: 40px;
    color: var(--black);
  }

  .errors,
  .backend-error {
    list-style-type: none;
    padding: 10px;
    margin-top: 8px;
    color: #fff;
    background: var(--black);
  }

  .success {
    font-size: 24px;
    text-align: center;
  }
</style>
