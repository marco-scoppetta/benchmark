<template>
  <el-container class="is-vertical overview-section">
    <el-main>
      <h2>Benchmark Overview</h2>
      <el-row
        v-for="graphType in graphTypes"
        :key="graphType"
        class="panel"
      >
        <overview-commits-chart
          :name="graphType"
          :executions="completedExecutions"
          :executionSpans="filterSpans(graphType)"
        />
      </el-row>
    </el-main>
  </el-container>
</template>

<script>
import BenchmarkClient from '@/util/BenchmarkClient';
import OverviewCommitsChart from './OverviewCommitsChart.vue';

export default {
  name: 'OverviewPage',
  components: { OverviewCommitsChart },
  data() {
    return {
      numberOfCompletedExecutions: 8,
      completedExecutions: null,
      graphTypes: [],
      executionSpans: null,
    };
  },
  async created() {
    // Get the last N completed executions
    // Note: we need to reverse the array because in the chart we want to show the most recent execution not as first but as last (rightmost)
    this.completedExecutions = (await BenchmarkClient.getLatestCompletedExecutions(
      this.numberOfCompletedExecutions,
    )).reverse();
    // Get all the execution spans relative to those executions
    this.executionSpans = await BenchmarkClient.getExecutionsSpans(this.completedExecutions);
    // Compute graph names from spans, for each graph name we draw a chart
    this.graphTypes = [...new Set(this.executionSpans.map(span => span.tags.graphType))];
  },
  methods: {
    filterSpans(name) {
      return this.executionSpans.filter(span => span.tags.graphType === name);
    },
  },
};
</script>
<style scoped>
.overview-section {
  overflow: scroll;
}
.el-main {
  padding: 20px 50px;
}
.el-container {
  background-color: #f4f3ef;
}
.panel {
  background-color: white;
  margin-bottom: 20px;
}
h2 {
  margin-bottom: 20px;
}
</style>
