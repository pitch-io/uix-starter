import * as esbuild from 'esbuild'
import shared from './build_config.mjs'
import * as fs from 'fs'

let result = await esbuild.build({
  ...shared,
  minify: true,
  entryNames: "[name]"
  // If you want to enable hashed names, you'd need to update/generate index.html with correct file names
  // entryNames: "[name].[hash]"
})

// fs.writeFileSync('public/js/manifest.json', JSON.stringify(result.metafile))
