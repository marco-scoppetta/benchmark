<template>
  <el-tabs
    type="border-card"
    class="wrapper"
  >
    <el-tab-pane
      v-for="graph in graphs"
      :key="graph"
      :label="graph"
    >
      <graph-tab
        :graph="graph"
        :execution-spans="filterSpans(graph)"
      />
    </el-tab-pane>
  </el-tabs>
</template>
<script>
import GraphTab from './GraphTab.vue';

export default {
  components: { GraphTab },
  props: ['graphs', 'executionSpans'],
  methods: {
    filterSpans(name) {
      return this.executionSpans.filter(span => span.tags.graphType === name);
    },
  },
};
</script>
<style scoped>
.wrapper {
  margin-top: 20px;
}
</style>
