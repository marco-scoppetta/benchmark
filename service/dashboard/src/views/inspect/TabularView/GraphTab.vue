<template>
  <div>
    <el-row type="flex" justify="end" class="header-row">
      <span class="label">Scale:</span>
      <div v-for="scale in scales" :span="1" v-bind:key="scale">
        <div
          @click="currentScale = scale"
          class="scale-tab"
          :class="{ active: scale == currentScale }"
        >
          {{ scale }}
        </div>
      </div>
    </el-row>
    <el-row class="queries">
      <el-col :span="12" :offset="1">Query</el-col>
      <el-col :span="3">Min (rep)</el-col>
      <el-col :span="3">Med</el-col>
      <el-col :span="3">Max (rep)</el-col>
      <el-col :span="2">Reps</el-col>
    </el-row>
    <div v-for="query in queries" :key="query">
      <query-line
        :query="query"
        :currentScale="currentScale"
        :spans="filterSpans(query)"
      ></query-line>
    </div>
  </div>
</template>
<script>
import QueryLine from "./QueryLine.vue";
import BenchmarkClient from "@/util/BenchmarkClient.js";

export default {
  props: ["graph", "executionSpans"],
  components: { QueryLine },
  data() {
    return {
      scales: [],
      queries: [],
      querySpans: null,
      currentScale: null
    };
  },
  created() {
    this.scales = Array.from(
      new Set(this.executionSpans.map(span => span.tags.graphScale))
    );
    this.scales.sort();
    this.currentScale = this.scales[0];
  },
  watch: {
      currentScale(scale){
        this.queries = [];
          // Take all executionSpans with current scale (it's usually 2: 1 executionSpan for writes and 1 for reads)
          // and then update queries when scale changes
        this.executionSpans.filter(span => span.tags.graphScale == scale).forEach(executionSpan => {
            BenchmarkClient.getSpans(
            `{ querySpans( parentId: "${executionSpan.id}" limit: 500){ 
                    id name duration tags { query type repetition repetitions }} }`
            ).then((resp )=>{
                this.querySpans = resp.data.querySpans;
                this.queries.push(...Array.from(new Set(this.querySpans.map(span => span.tags.query))));
                this.queries.sort();
            });
        });
      }
  },
  methods: {
    filterSpans(query) {
      return this.querySpans.filter(span => span.tags.query === query);
    }
  }
};
</script>
<style scoped>
.queries {
  font-weight: bold;
  margin-top: 10px;
}
.scale-tab {
  padding-bottom: 5px;
  cursor: pointer;
  text-align: center;
  margin: 0 5px;
  -webkit-user-select: none; /* Chrome all / Safari all */
  -moz-user-select: none; /* Firefox all */
  -ms-user-select: none; /* IE 10+ */
  user-select: none;
}
.active {
  border-bottom: 2px solid #409eff;
  color: #409eff;
}
</style>
