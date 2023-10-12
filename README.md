# uix-starter
Template project to start building a web app with [UIx2](https://github.com/pitch-io/uix)

## Quick setup
```shell
npx create-uix-app@latest my-app # bare-bones project
npx create-uix-app@latest my-app --re-frame # adds re-frame setup
npx create-uix-app@latest MyApp --react-native # setup cljs project in existing React Native project
npx create-uix-app@latest MyApp --expo # create a new React Native project using Expo
```

## Development
```shell
yarn # install NPM deps
yarn dev # run dev build in watch mode with CLJS REPL
```

## Production
```shell
yarn release # build production bundle
```
