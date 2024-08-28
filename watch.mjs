import * as esbuild from 'esbuild'
import shared from './build_config.mjs'

let ctx = await esbuild.context({
  ...shared,
})

await ctx.watch()
