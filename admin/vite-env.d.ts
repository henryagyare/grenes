/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_APP_TITLE: string
  readonly VITE_API_KEY: string
  readonly VITE_AUTH_DOMAIN: string
  readonly VITE_PROJECT_ID: string
  readonly VITE_STORAGE_BUCKET: string
  readonly VITE_MESSAGING_SENDER_ID: string
  readonly VITE_APP_ID: string
  readonly VITE_MEASUREMENT_ID: string
  readonly VITE_SERVER_URL: string
  readonly VITE_SENTRY_DSN: string
  // more env variables...
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
