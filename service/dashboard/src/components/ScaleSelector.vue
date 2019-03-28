<template>
    <div class="container">
        <span class="scale-label">Scale:</span>
        <div v-for="scale in scales" :key="scale" :span="1">
            <div class="scale-tab" :class="{ active: scale == currentScale }" @click="$emit('selected-scale', scale)">
            {{ scale }}
            </div>
        </div>
    </div>
</template>
<style scoped>
.scale-label{
  font-weight: bold;
  margin-right: 5px;
}
.scale-tab {
  padding-bottom: 5px;
  cursor: pointer;
  text-align: center;
  margin: 0 5px;
  user-select: none;
  margin-bottom: 6px;
}
.active {
  border-bottom: 2px solid #409eff;
  color: #409eff;
}
.container {
    display: flex;
    justify-content: center;
}
</style>
<script>
export default {
  name: 'ScaleSelector',
  props: ['scales', 'currentScale'],
};
</script>
