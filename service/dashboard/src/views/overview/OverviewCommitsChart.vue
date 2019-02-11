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
    <div ref="chart" class="chart-wrapper" @click="clickOnCanvas"></div>
    <transition name="el-fade-in-linear">
      <el-popover
        ref="popover"
        width="150"
        trigger="manual"
        v-model="popoverVisible"
      >
      <div style="text-align: center; margin: 0">
        <el-button size="mini" type="text" @click="popoverVisible = false">Cancel</el-button>
        <el-button type="primary" size="mini" @click="redirectToInspect">Inspect</el-button>
      </div>
      </el-popover>
    </transition>
  </div>
</template>
<style scoped>
.chart-wrapper{
  height: 500px;
}
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
import InspectStore from "@/util/InspectSharedStore";

export default {
  props: ["name", "executions", "spans"],
  data() {
    return {
      popoverVisible: false,
      scales: null,
      currentScale: null,
      queries: null,
      queriesMap: null,
      chart: null,
      clickedPointArgs: null
    };
  },
  created() {
    window.onresize = () => {
      this.chart.resize();
    };
    // Compute array of unique queries that have been executed on this graph (this.spans contains spans for this graph only)
    this.queries = Array.from(
      new Set(this.spans.map(span => span.tags.query))
    );
    this.scales = Array.from(
      new Set(this.spans.map(span => span.tags.scale))
    );
    this.currentScale = this.scales[0];
    // queriesMap will map each full query to a legend identifier, e.g. { "match $x isa person; get;": "matchQuery1", ... }
    this.queriesMap = QueriesUtil.buildQueriesMap(this.queries);

    this.$nextTick(() => {
      this.drawChart();

      const popover = this.$refs.popover.$el;
      popover.style.position = "absolute";
      popover.style.display = "block";
      this.chart.on('click', (args) => {
        if(args.targetType){
            
        } else {
          this.clickedPointArgs = args;
          args.event.event.stopPropagation();
          popover.style.left = args.event.offsetX+"px";
          popover.style.top = args.event.offsetY+"px";
          popover.childNodes[0].style.transform = `translate(-50%, -${(25+(args.data.symbolSize)/2)+4}px)`;
          this.popoverVisible=true;
        }
      });
    });
  },
  methods:{
    clickOnCanvas(){
      this.popoverVisible=false;
      this.clickedPointArgs=null;
    },
    redirectToInspect(){
      const currentQuery = Object.keys(this.queriesMap).filter(
            x => this.queriesMap[x] === this.clickedPointArgs.seriesName
          )[0];
      
      InspectStore.setGraph(this.name);
      InspectStore.setScale(this.currentScale);
      InspectStore.setQuery(currentQuery);
      this.$router.push({ path: `inspect/${this.clickedPointArgs.data.executionId}`});
    },
    drawChart(){
      const chartComponent = this.$refs.chart;
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