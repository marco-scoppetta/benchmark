<template>
  <div>
    <el-row type="flex" justify="end">
      <scale-selector
        :scales="scales"
        :currentScale="currentScale"
        v-on:selected-scale="(scale)=>{this.currentScale=scale}">
      </scale-selector>
    </el-row>
    <el-row class="header" type="flex" align="center">
      <el-col :span="14">Query</el-col>
      <el-col :span="3">Min (rep)</el-col>
      <el-col :span="3">Med</el-col>
      <el-col :span="3">Max (rep)</el-col>
      <el-col :span="1">Reps</el-col>
    </el-row>
    <div v-for="query in queries" :key="query">
      <query-line
        :query="query"
        :isOverviewQuery="query===overviewQuery"
        :spans="filterQuerySpans(query)"
      />
    </div>
  </div>
</template>
<script>
import BenchmarkClient from '@/util/BenchmarkClient';
import QueryLine from './QueryLine.vue';
import ScaleSelector from '@/components/ScaleSelector.vue';

export default {
  name: 'GraphTab',
  components: { QueryLine, ScaleSelector },
  props: ['graph', 'executionSpans', 'overviewScale', 'overviewQuery'],
  data() {
    return {
      scales: [],
      queries: [],
      querySpans: [],
      currentScale: null,
    };
  },
  watch: {
    // Every time the current scale changes -> recompute queries and query spans to show in table
    currentScale(scale) {
      this.computeQueriesAndSpans(scale);
    },
  },
  created() {
    this.scales = [...new Set(this.executionSpans.map(span => span.tags.graphScale))];
    this.scales.sort((a, b) => a - b);
    this.currentScale = (this.overviewScale) ? this.overviewScale : this.scales[0];
  },
  methods: {
    // Filter query spans by query so that each QueryLine component only receives the relevant query spans to compute max min med etc..
    filterQuerySpans(query) {
      return this.querySpans.filter(span => span.tags.query === query);
    },
    computeQueriesAndSpans(scale) {
      // Take all executionSpans with current scale (it's usually 2: 1 executionSpan for writes and 1 for reads)
      // and load all the query spans associated to those.
      const querySpanPromises = this.executionSpans
        .filter(span => span.tags.graphScale === scale)
        .map(executionSpan => getQuerySpansRequest(executionSpan.id));
      // Wait on all promises and extract queries and query spans from responses
      Promise.all(querySpanPromises)
        .then((responses) => {
          this.querySpans = flatMapResponse(responses);
          this.queries = uniqueQueriesSortedArray(this.querySpans);
        });
    },
  },
};

/**
 * Helper functions
 */
function flatMapResponse(responses) {
  return responses.reduce((acc, resp) => acc.concat(resp.data.querySpans), []);
}
function getQuerySpansRequest(id) {
  return BenchmarkClient.getSpans(`{ querySpans( parentId: "${id}" limit: 500){ id name duration tags { query type repetition repetitions }} }`);
}
function uniqueQueriesSortedArray(querySpans) {
  return [...new Set(querySpans.map(span => span.tags.query))].sort();
}
</script>
<style scoped>
.header {
  font-weight: bold;
  margin-top: 10px;
  margin-bottom: 10px;
}
.el-col{
    text-align: center;
}
</style>
