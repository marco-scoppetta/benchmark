<template>
  <el-tabs type="border-card" class="wrapper" v-model="activeGraph">
    <el-tab-pane v-for="graph in graphs" :key="graph" :label="graph">
      <graph-tab
        :graph="graph"
        :execution-spans="filterSpans(graph)"
        :overviewScale="getOverviewScale(graph)"
        :overviewQuery="getOverviewQuery(graph)"
      />
    </el-tab-pane>
  </el-tabs>
</template>
<script>
import GraphTab from './GraphTab.vue';

export default {
  name: 'TabularView',
  components: { GraphTab },
  props: ['graphs', 'executionSpans', 'currentGraph', 'currentQuery', 'currentScale'],
  data() {
    return {
      activeGraph: '0',
    };
  },
  watch: {
    graphs(values) {
      // Once the graphs are available check if we need to select Graph tab based on the
      // currentGraph parameter that comes from the Ovierview page.
      if (this.currentGraph) {
        this.activeGraph = values.indexOf(this.currentGraph).toString();
      }
    },
  },
  methods: {
    filterSpans(name) {
      return this.executionSpans.filter(span => span.tags.graphType === name);
    },
    getOverviewScale(graphType) {
      if (this.currentGraph === graphType) { return this.currentScale; }
      return null;
    },
    getOverviewQuery(graphType) {
      if (this.currentGraph === graphType) { return this.currentQuery; }
      return null;
    },
  },
};
</script>
<style scoped>
.wrapper {
  margin-top: 20px;
}
</style>
