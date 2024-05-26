<script setup lang="ts">
import { ref } from 'vue'

const isDrawerRail = ref(true)

defineProps({
  styleClass: {
    default: 'mx-4 mt-8',
    type: String
  }
})
</script>

<template>
  <v-app>
    <v-app-bar color="primary" prominent>
      <v-app-bar-nav-icon
        variant="text"
        @click.stop="isDrawerRail = !isDrawerRail"
      ></v-app-bar-nav-icon>

      <v-toolbar-title>SHB Admin</v-toolbar-title>

      <v-spacer></v-spacer>

      <v-btn variant="text" icon="mdi-magnify"></v-btn>

      <v-btn variant="text" icon="mdi-filter"></v-btn>

      <v-avatar @click="console.log('dfd')" color="surface-variant"></v-avatar>
    </v-app-bar>

    <v-navigation-drawer expand-on-hover permanent :rail="isDrawerRail">
      <v-list>
        <v-list-item
          prepend-avatar="https://randomuser.me/api/portraits/women/85.jpg"
          title="Sandra Adams"
          subtitle="sandaradsaf@gmail.com"
        ></v-list-item>

        <v-list-item prepend-icon="mdi-spanner" title="Challenges"></v-list-item>
      </v-list>
    </v-navigation-drawer>

    <v-main :class="styleClass">
      <slot />
    </v-main>
  </v-app>
</template>

<style scoped></style>
