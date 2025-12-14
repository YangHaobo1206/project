<template>
  <section class="page-card">
    <header class="page-header">
      <div class="page-heading">
        <h1 class="page-title">{{ title }}</h1>
        <p v-if="subtitle" class="page-subtitle">{{ subtitle }}</p>
      </div>
      <div v-if="hasActions" class="page-actions">
        <slot name="actions" />
      </div>
    </header>
    <div class="page-body">
      <slot />
    </div>
  </section>
</template>

<script setup>
import { computed, useSlots } from 'vue'

defineProps({
  title: { type: String, required: true },
  subtitle: { type: String, default: '' }
})

const slots = useSlots()
const hasActions = computed(() => !!slots.actions)
</script>

<style scoped>
.page-body {
  padding-top: 4px;
}
</style>
