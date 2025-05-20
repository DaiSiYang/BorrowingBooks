
import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import '@/style/common.css'
import "element-plus/dist/index.css"
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

const app = createApp(App)

app.use(createPinia())
app.use(router)
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
app.use(pinia)
app.mount('#app')
