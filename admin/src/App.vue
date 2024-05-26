<script setup lang="ts">
import { RouterView } from 'vue-router'

import { useFirebaseAuth } from 'vuefire'
import { connectAuthEmulator } from 'firebase/auth'

const auth = useFirebaseAuth()

if (import.meta.env.DEV) {
  if (auth) {
    connectAuthEmulator(auth, 'http://localhost:9099')
  }
}
</script>

<template>
  <Suspense>
    <RouterView />
  </Suspense>
</template>
