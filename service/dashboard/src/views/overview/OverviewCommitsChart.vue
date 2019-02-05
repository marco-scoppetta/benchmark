<template>
  <div>
    <el-row type="flex" justify="end" class="header-row">
      <span class="label">Scale:</span>
      <div 
        v-for="scale in scales" :span=1
        v-bind:key="scale"
      >
        <div @click="currentScale=scale" class="scale-tab" :class="{'active':scale==currentScale}">{{scale}}</div>
      </div>
    </el-row>
    <div ref="chart"></div>
    <el-popover
      ref="popover"
      placement="top"
      title="Title"
      width="200"
      trigger="manual"
      content="this is content, this is content, this is content"
      v-model="popoverVisible"
    >
    </el-popover>
  </div>
</template>
<style scoped>
.label{
  margin-right: 5px;
}
.header-row{
  padding: 10px;
}
.scale-tab{
  padding-bottom: 5px;
  cursor: pointer;
  text-align: center;
  margin: 0 5px;
  -webkit-user-select: none;  /* Chrome all / Safari all */
  -moz-user-select: none;     /* Firefox all */
  -ms-user-select: none;      /* IE 10+ */
  user-select: none;
}
.active{
  border-bottom: 2px solid #409EFF;
  color: #409EFF;
}
</style>
<script>
import ChartFactory from "./ChartFactory";
import QueriesUtil from "./QueriesUtil";

export default {
  props: ["name", "executions", "spans"],
  data() {
    return {
      popoverVisible: false,
      scales: null,
      currentScale: null,
      queries: null,
      queriesMap: null,
      chart: null
    };
  },
  created() {
    // Compute array of unique queries that have been executed on this graph (this.spans contains spans for this graph only)
    this.queries = Array.from(
      new Set(this.spans.map(span => span.tags.query))
    );
    this.scales = Array.from(
      new Set(this.spans.map(span => span.tags.scale))
    );
    this.currentScale = this.scales[0];
    // queriesMap will map a full query to a legend identifier, e.g. { "match $x isa person; get;": "matchQuery1", ... }
    this.queriesMap = QueriesUtil.buildQueriesMap(this.queries);

    this.$nextTick(() => {
      this.drawChart();

      //TODO decide on how to use the tooltip
      // const popover = this.$refs.popover.$el;
      // popover.style.position = "absolute";
      // popover.style.display = "block";
      // myChart.on('click', (e) => {
      //   popover.style.right = e.event.offsetX+"px";
      //   popover.style.top = e.event.offsetY+"px";
      //   // popover.style.transform = "translate(50%, 0%)";
      //   this.popoverVisible=!this.popoverVisible;
      // });
    });
  },
  methods:{
    drawChart(){
      const chartComponent = this.$refs.chart;
      chartComponent.style.height = "500px";

        // queriesTimes will map a query legend identifier to its avgTime per commit
      const queriesTimes = QueriesUtil.buildQueriesTimes(
        this.queries,
        this.spans,
        this.executions,
        this.currentScale
      );

      this.chart = ChartFactory.createChart(
        chartComponent,
        queriesTimes,
        this.queriesMap
      );
    }
  },
  watch:{
    currentScale(val, previous){
      if(val == previous) return;
      this.drawChart();
    }
  }
};
</script>