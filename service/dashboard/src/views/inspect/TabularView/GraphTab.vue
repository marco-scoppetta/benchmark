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
        <el-row class="queries">
            <el-col :span="12" :offset="1">Query</el-col>
            <el-col :span="3">Min (rep)</el-col>
            <el-col :span="3">Med</el-col>
            <el-col :span="3">Max (rep)</el-col>
            <el-col :span="2">Reps</el-col>
        </el-row>
        <div v-for="query in queries" :key="query">
            <query-line :query="query" :currentScale="currentScale" :spans="filterSpans(query)"></query-line>
        </div>
    </div>
</template>
<script>
import QueryLine from "./QueryLine.vue";

export default {
    props: ["graph", "spans"],
    components: { QueryLine },
    data(){
        return {
            scales:[],
            queries: [],
            currentScale: null
        }
    },
    created(){
        this.scales = Array.from(new Set(this.spans.map(span => span.tags.scale)));
        this.queries = Array.from(new Set(this.spans.map(span => span.tags.query)));
        this.queries.sort();
        this.currentScale = this.scales[0];
    },
    computed:{
        currentSpans(){
            return this.spans.filter(span => span.tags.scale === this.currentScale);
        }
    },
    methods:{
        filterSpans(query){
            return this.spans.filter(span => span.tags.query === query);
        }
    }
}
</script>
<style scoped>
.queries{
    font-weight: bold;
    margin-top: 10px;
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

