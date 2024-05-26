<script setup lang="ts">
import { useFirebaseAuth } from 'vuefire'
import { signInWithEmailAndPassword } from 'firebase/auth'
import { reactive, ref } from 'vue'

import { useRoute, useRouter } from 'vue-router'

const router = useRouter()

const auth = useFirebaseAuth()

const credentials = reactive({
  email: '',
  password: ''
})

const route = useRoute()

const visible = ref(false)

const signIn = async () => {
  try {
    if (auth) {
      await signInWithEmailAndPassword(auth, credentials.email, credentials.password).then(
        async () => {
          const user = auth.currentUser
          const idTokenResult = await user?.getIdTokenResult()
          const roles = idTokenResult?.claims.roles as Array<String>

          if (!roles.includes('superAdmin')) {
            await auth.signOut()
            await router.push({ name: 'login' })
          } else {
            route.query.redirect
              ? await router.push(route.query.redirect as string)
              : await router.push({ name: 'home' })
          }
        }
      )
    }
  } catch (e) {
    console.error(e)
  }
}
</script>

<template>
  <div>
    <v-img
      class="mx-auto my-6"
      max-width="228"
      src="https://cdn.vuetifyjs.com/docs/images/logos/vuetify-logo-v3-slim-text-light.svg"
    ></v-img>

    <v-card class="mx-auto pa-12 pb-8" elevation="8" max-width="448" rounded="lg">
      <div class="text-subtitle-1 text-medium-emphasis">Account</div>

      <v-text-field
        density="compact"
        placeholder="Email address"
        prepend-inner-icon="mdi-email-outline"
        variant="outlined"
        v-model="credentials.email"
      ></v-text-field>

      <div class="text-subtitle-1 text-medium-emphasis d-flex align-center justify-space-between">
        Password

        <a
          class="text-caption text-decoration-none text-blue"
          href="#"
          rel="noopener noreferrer"
          target="_blank"
        >
          Forgot login password?</a
        >
      </div>

      <v-text-field
        :append-inner-icon="visible ? 'mdi-eye-off' : 'mdi-eye'"
        :type="visible ? 'text' : 'password'"
        density="compact"
        placeholder="Enter your password"
        prepend-inner-icon="mdi-lock-outline"
        variant="outlined"
        v-model="credentials.password"
        @click:append-inner="visible = !visible"
      ></v-text-field>

      <v-btn block class="mb-8" color="blue" size="large" variant="tonal" @click="signIn">
        Log In
      </v-btn>

      <v-card-text class="text-center">
        <a
          class="text-blue text-decoration-none"
          href="#"
          rel="noopener noreferrer"
          target="_blank"
        >
          Sign up now
          <v-icon icon="mdi-chevron-right"></v-icon>
        </a>
      </v-card-text>
    </v-card>
  </div>
</template>

<style scoped>
header {
  line-height: 1.5;
  max-height: 100vh;
}

.logo {
  display: block;
  margin: 0 auto 2rem;
}

nav {
  width: 100%;
  font-size: 12px;
  text-align: center;
  margin-top: 2rem;
}

nav a.router-link-exact-active:hover {
  background-color: transparent;
}

nav a:first-of-type {
  border: 0;
}

@media (min-width: 1024px) {
  header {
    display: flex;
    place-items: center;
  }

  .logo {
    margin: 0 2rem 0 0;
  }

  header .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
  }

  nav {
    text-align: left;
    margin-left: -1rem;
    font-size: 1rem;

    padding: 1rem 0;
    margin-top: 1rem;
  }
}
</style>
