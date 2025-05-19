<script setup>

</script>

<template>
  <keep-alive>
    <router-view />
  </keep-alive>
</template>

<style scoped lang="scss">
.main-container {
  padding: 16px;
  background-color: #f5f7fa;
  height: 100%;
  overflow-y: auto; // 确保主容器允许垂直滚动
}
</style>