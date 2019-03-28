<template>
  <div>
    <el-row type="flex" justify="space-between" align="center" class="header-row">
      <div class="chart-title">{{name | formatTitle}}</div>
      <scale-selector
        :scales="scales"
        :currentScale="currentScale"
        v-on:selected-scale="(scale)=>{this.currentScale=scale}">
      </scale-selector>
    </el-row>
    <div ref="chart" class="chart-wrapper" @click="clickOnCanvas"/>
    <transition name="el-fade-in-linear">
      <el-popover ref="popover" v-model="popoverVisible" width="150" trigger="manual">
        <div style="text-align: center; margin: 0">
          <el-button size="mini" type="text" @click="popoverVisible = false">
            Cancel
          </el-button>
          <el-button type="primary" size="mini" @click="redirectToInspect">
            Inspect
          </el-button>
        </div>
      </el-popover>
    </transition>
  </div>
</template>
<script>
import BenchmarkClient from '@/util/BenchmarkClient';
import InspectStore from '@/util/InspectSharedStore';
import ScaleSelector from '@/components/ScaleSelector';
import ChartFactory from './ChartFactory';
import QueriesUtil from './QueriesUtil';

export default {
  props: ['name', 'executions', 'executionSpans'],
  components: { ScaleSelector },
  data() {
    return {
      popoverVisible: false,
      scales: null,
      currentScale: null,
      queries: null,
      queriesLegend: null,
      chart: null,
      clickedPointArgs: null,
    };
  },
  filters: {
    formatTitle(name) {
      const nameWithSpaces = name.split('_').map(word => word.charAt(0).toUpperCase() + word.slice(1));
      return nameWithSpaces.join(' ');
    },
  },
  watch: {
    currentScale(val, previous) {
      if (val === previous) return;
      this.drawChart();
    },
  },
  async created() {
    window.onresize = () => { this.chart.resize(); };
    // Compute array of unique queries that have been executed on this graph
    this.querySpans = await fetchQuerySpans(this.executionSpans);
    this.queries = uniqueQueriesSortedArray(this.querySpans);

    // queriesLegend will map each full query to a legend identifier, e.g. { "match $x isa person; get;": "matchQuery1", ... }
    this.queriesLegend = QueriesUtil.buildQueriesMap(this.queries);

    this.scales = [...new Set(this.executionSpans.map(span => span.tags.graphScale))].sort((a, b) => a - b);
    this.currentScale = this.scales[0];

    this.$nextTick(() => {
      this.drawChart();

      const popover = this.$refs.popover.$el;
      popover.style.position = 'absolute';
      popover.style.display = 'block';
      this.attachChartListeners(this.chart, popover);
    });
  },
  methods: {
    clickOnCanvas() {
      this.popoverVisible = false;
      this.clickedPointArgs = null;
    },
    redirectToInspect() {
      const currentQuery = Object.keys(this.queriesLegend).filter(
        x => this.queriesLegend[x] === this.clickedPointArgs.seriesName,
      )[0];

      InspectStore.setGraph(this.name);
      InspectStore.setScale(this.currentScale);
      InspectStore.setQuery(currentQuery);
      this.$router.push({
        path: `inspect/${this.clickedPointArgs.data.executionId}`,
      });
    },
    drawChart() {
      const chartComponent = this.$refs.chart;
      // queriesTimes will map a query legend identifier to its avgTime per commit
      const queriesTimes = QueriesUtil.buildQueriesTimes(
        this.queries,
        this.querySpans,
        this.executions,
        this.currentScale,
      );

      this.chart = ChartFactory.createChart(
        chartComponent,
        queriesTimes,
        this.queriesLegend,
      );
    },
    attachChartListeners(chart, popover) {
      chart.on('click', (args) => {
        if (args.targetType) {
          // TODO: finish this
        } else {
          this.clickedPointArgs = args;
          args.event.event.stopPropagation();
          popover.style.left = `${args.event.offsetX}px`;
          popover.style.top = `${args.event.offsetY}px`;
          popover.childNodes[0].style.transform = `translate(-50%, -${25 + args.data.symbolSize / 2 + 4}px)`;
          this.popoverVisible = true;
        }
      });
    },
  },
};

/**
 * Helper functions
 */
function getQuerySpansRequest(id) {
  return BenchmarkClient.getSpans(`{ querySpans( parentId: "${id}" limit: 500){ id name duration tags { query type repetition repetitions }} }`);
}
function uniqueQueriesSortedArray(querySpans) {
  return [...new Set(querySpans.map(span => span.tags.query))].sort();
}
async function fetchQuerySpans(executionSpans) {
  const querySpanPromises = executionSpans
    .map(executionSpan => getQuerySpansRequest(executionSpan.id)
      .then(resp => resp.data.querySpans
        .map(qs => Object.assign({
          executionName: executionSpan.tags.executionName,
          scale: executionSpan.tags.graphScale,
        }, qs))));
  const responses = await Promise.all(querySpanPromises);
  return responses.reduce((acc, resp) => acc.concat(resp), []);
}
</script>
<style scoped>
.chart-wrapper {
  height: 500px;
}
.chart-title{
  font-weight: 700;
  /* font-style: italic; */
  font-size: 110%;
}
.header-row {
  padding: 20px 20px 0px 20px;
}
</style>
