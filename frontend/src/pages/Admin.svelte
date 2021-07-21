<script>
  import { get } from '@utils/api'
  import { ApiResult } from '@utils/result'
  import Layout from '@components/Layout.svelte'

  async function getUsers() {
    const response = await get('/users')
    return ApiResult.match(response, {
      Ok: ({ data }) => data,
      NotFound: () => 'Not found',
      default: () => 'An error occurred',
    })
  }
  let promise = getUsers()
</script>

<Layout>
  <h1>Admin only</h1>
  {#await promise}
    ...loading
  {:then data}
    <pre>{JSON.stringify(data, null, 2)}</pre>
  {:catch err}
    <pre>{JSON.stringify(err, null, 2)}</pre>
  {/await}
</Layout>
