<script lang="ts">
  import { post } from '../utils/api'
  import { ApiResult } from '../utils/result'
  import LockOpen from '../../public/lock-open.svg?component'
  import User from '../../public/user.svg?component'

  let email = ''
  let password = ''
  let isLoading = false
  let isSuccess = false
  let errors: Errors = {}

  type Errors = { email?: string; password?: string }

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
      const response = await post('/user/login', { email, password })
      ApiResult.match(response, {
        OK: (data: any) => {
          console.log(data)
          isSuccess = true
        },
        default: (e: any) => console.log(e),
      })
      isSuccess = true
    }
  }
</script>

<main>
  <form on:submit|preventDefault={handleSubmit}>
    {#if isSuccess}
      <div class="success">
        <LockOpen />
        <br />
        You've been successfully logged in.
      </div>
    {:else}
      <User />
      <h1>Bantamweight</h1>
      <h4>http4k + Svelte</h4>

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
  </form>
</main>

<style>
  :global(button > svg) {
    margin-bottom: -5px;
  }

  main {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100vh;
  }

  form {
    background-color: var(--orange);
    background-image: var(--orange-gradient);
    padding: 50px;
    width: 250px;
    height: 400px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    box-shadow: var(--shadow);
    margin-left: auto;
    margin-right: auto;
  }

  label {
    margin: 8px -8px;
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

  .errors {
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
