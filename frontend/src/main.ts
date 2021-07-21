import App from './App.svelte'
import navaid from 'navaid'

const app = new App({
  target: document.getElementById('app'),
  props: {
    router: navaid(),
  },
})

export default app
