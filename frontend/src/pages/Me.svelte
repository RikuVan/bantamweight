<script>
  import { userStore } from '@store/user'
  import Layout from '@components/Layout.svelte'
</script>

<Layout>
  <h1>Me</h1>
  <pre>{JSON.stringify($userStore, null, 2)}</pre>
</Layout>
