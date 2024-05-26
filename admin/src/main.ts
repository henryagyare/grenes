import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

// Vuetify
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import { aliases, mdi } from 'vuetify/iconsets/mdi'
import { md3 } from 'vuetify/blueprints'
import '@mdi/font/css/materialdesignicons.css'

import PrimeVue from 'primevue/config'
import 'primevue/resources/themes/aura-light-green/theme.css'

import { VueFire, VueFireAuth } from 'vuefire'
import { firebaseApp } from '@/firebase'

import * as Sentry from '@sentry/vue'

import axios from 'axios'

const app = createApp(App)

const BASE_URL = import.meta.env.DEV ? 'http://localhost:8080' : import.meta.env.VITE_SERVER_URL

export const http = axios.create({
  baseURL: BASE_URL
})

Sentry.init({
  app,
  dsn: import.meta.env.VITE_SENTRY_DSN,
  integrations: [Sentry.browserTracingIntegration({ router }), Sentry.replayIntegration()],

  // Set tracesSampleRate to 1.0 to capture 100%
  // of transactions for performance monitoring.
  // We recommend adjusting this value in production
  tracesSampleRate: 1.0,

  // Set `tracePropagationTargets` to control for which URLs distributed tracing should be enabled
  tracePropagationTargets: ['localhost', /^https:\/\/grenes-1759f\.uc\.r\.\/api/],

  // Capture Replay for 10% of all sessions,
  // plus for 100% of sessions with an error
  replaysSessionSampleRate: 0.1,
  replaysOnErrorSampleRate: 1.0
})

app
  .use(createPinia())
  .use(PrimeVue, { ripple: true })
  .use(
    createVuetify({
      blueprint: md3,
      theme: {
        defaultTheme: 'dark'
      },
      icons: {
        defaultSet: 'mdi',
        aliases,
        sets: {
          mdi
        }
      }
    })
  )
  .use(VueFire, {
    firebaseApp,
    modules: [VueFireAuth()]
  })
  .use(router)

  .mount('#app')
