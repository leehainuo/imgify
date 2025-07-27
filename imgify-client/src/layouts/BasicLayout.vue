<script setup lang="ts">
import GlobalHeader from '../components/GlobalHeader.vue'

</script>

<template>
  <div id="basicLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header">
        <GlobalHeader />
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
      <a-layout-footer class="footer">
        <a href="https://www.lihainuo.com" target="_blank"> 欢迎前往 lihainuo.com 来了解我  </a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<style scoped>
#basicLayout .footer {
  background: #efefef;
  padding: 16px;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
}

#basicLayout .content {
  background: linear-gradient(to right, #fefefe, #fff);
  margin-bottom: 28px;
  padding: 20px;
  position: relative;
}
#basicLayout .header {
  padding-inline: 20px;
  margin-bottom: 32px;
  color: unset;
  background: white;
}
</style>
