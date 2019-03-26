<template>
  <el-container class="main-super">
    <side-nav />
    <router-view />
  </el-container>
</template>

<script>
// @ is an alias to /src
import SideNav from '@/components/SideNav.vue';

export default {
  name: 'Home',
  components: { SideNav },
};
</script>
<style>
.main-super {
  height: 100%;
}
</style>
