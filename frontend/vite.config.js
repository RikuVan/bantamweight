import { ViteAliases } from 'vite-aliases'
import { defineConfig } from 'vite'
import fs from 'fs'
import prettier from 'prettier'
import { svelte } from '@sveltejs/vite-plugin-svelte'
import svelteSVG from 'vite-plugin-svelte-svg'

const outDir = '../backend/src/main/resources/public'

const proxyOptions = {
  target: `http://localhost:9000`,
  changeOrigin: true,
  secure: false,
  ws: true,
}

const proxySettings = (...paths) =>
  paths.reduce((config, path) => {
    config[path] = proxyOptions
    return config
  }, {})

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  const production = mode === 'production'
  return {
    plugins: [
      ViteAliases({
        useTypescript: true,
      }),
      svelteSVG({
        svgoConfig: {},
      }),
      svelte({
        emitCss: production,
        compilerOptions: {
          dev: !production,
        },
      }),
      production && createAssetManifest(),
    ],
    server: {
      host: 'localhost',
      port: 5000,
      proxy: proxySettings('/api', '/users'),
    },
    build: {
      sourcemap: true,
      outDir,
    },
  }
})

// for use in service worker to precache
function createAssetManifest() {
  return {
    name: 'create-asset-manifest',
    enforce: 'post',
    apply: 'build',
    writeBundle() {
      let assets = []
      fs.readdir(outDir, (err, files) => {
        assets = files.filter((a) => a.includes('.') && !a.includes('manifest')).map(JSON.stringify)
      }),
        fs.readdir(outDir + '/assets', (err, files) => {
          assets = [...assets, ...files.map((f) => `'/assets/${f}'`)]
          const output = `const manifest = [
           ${assets.join(',\n')}
        ]`
          fs.writeFile(
            outDir + '/asset-manifest.js',
            prettier.format(output, { parser: 'typescript' }),
            (err) => {
              if (err) console.error('failed to create asset manifest ', err)
              console.log('assets > /public/asset-manifest')
            }
          )
        })
    },
  }
}
