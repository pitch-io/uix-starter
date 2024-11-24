# uix-starter
Template project to start building a web app with [UIx](https://github.com/pitch-io/uix)

## Quick setup
```shell
npx create-uix-app@latest my-app # bare-bones project
npx create-uix-app@latest my-app --re-frame # adds re-frame setup
npx create-uix-app@latest my-app --fly-io # creates full stack app with Fly.io
npx create-uix-app@latest MyApp --react-native # setup cljs project in existing React Native project
npx create-uix-app@latest MyApp --expo # create a new React Native project using Expo
```

## Development
```shell
npm i # install NPM deps
npm run dev # run dev build in watch mode with CLJS REPL
```

## Production
```shell
npm run release # build production bundle
```
