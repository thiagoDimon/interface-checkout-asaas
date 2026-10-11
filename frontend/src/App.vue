<template>
  <div id="app-layout">
    <header id="app-header">
      <h1 id="app-title">Asaas Demo Checkout Store</h1>
    </header>

    <main id="app-content">
      <RouterView />
    </main>
  </div>
</template>

<script>
export default {
  name: 'App',
}
</script>

<style scoped>
#app-header {
  padding: var(--spacing-md) var(--spacing-lg);
  background-color: var(--color-header);
  color: var(--color-header-contrast);
}

#app-title {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 700;
}

#app-content {
  max-width: 75rem;
  margin: 0 auto;
  padding: var(--spacing-xl) var(--spacing-lg);
}
</style>
