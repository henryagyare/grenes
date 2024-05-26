<script setup lang="ts">
defineProps<{
  title: string
  icon?: string
  image?: string
  isFavorite: boolean
  to: { name: string; params?: Record<string, string | number> }
}>()
</script>

<template>
  <v-card variant="tonal" :to="to" class="w-full select-none">
    <template v-slot:title>
      <div class="flex flex-col">
        <div class="flex flex-row justify-between">
          <v-icon icon="$vuetify" />
          <div class="flex flex-row gap-2">
            <v-icon icon="$vuetify" />
            <v-icon icon="$vuetify" />
            <v-icon icon="$vuetify" />
          </div>
        </div>
        <v-card-title>
          {{ title }}
        </v-card-title>
      </div>
    </template>

    <template v-slot:actions>
      <v-icon class="ml-auto" icon="mdi-arrow-right" />
    </template>
  </v-card>
</template>

<style scoped></style>
