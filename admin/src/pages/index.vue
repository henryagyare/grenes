<script setup lang="ts">
import Default from '@/layouts/Default.vue'
import HomeOptionCard from '@/components/HomeOptionCard.vue'
</script>

<template>
  <Default>
    <div class="mx-auto max-w-7xl flex flex-col gap-8">
      <div class="w-full flex flex-col">
        <div class="my-8 grid sm:grid-cols-2 lg:grid-cols-3 gap-8">
          <HomeOptionCard title="Challenges" :to="{ name: 'challenges' }" is-favorite />
        </div>
      </div>
    </div>
  </Default>
</template>
