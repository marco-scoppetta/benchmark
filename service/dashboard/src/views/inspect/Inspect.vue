<template>
    <el-container>
         <el-main>
            <h2>Inspect Execution</h2>
            <el-card :body-style="{padding: '15px'}">
                <el-row style="margin-bottom: 5px; font-weight: bold; text-align:center;">
                    <el-col :span="3">STATUS</el-col>
                    <el-col :span="4">REPOSITORY</el-col>
                    <el-col :span="7">COMMIT</el-col>
                    <el-col :span="2">PR</el-col>
                    <el-col :span="4">STARTED AT</el-col>
                    <el-col :span="4">COMPLETED AT</el-col>
                </el-row>
                <el-row type="flex" align="middle" style="text-align:center;">
                    <el-col :span="3">
                        <el-tag size="mini" :type="(execution.status=='COMPLETED') ? 'success' : 'danger'">{{execution.status}}</el-tag>
                    </el-col>
                    <el-col :span="4"><a :href="execution.repoUrl">{{execution.repoUrl | substringRepo}}</a></el-col>
                    <el-col :span="7"><a :href="execution.repoUrl+'/commit/'+execution.commit">{{execution.commit}}</a></el-col>
                    <el-col :span="2"><a :href="execution.prUrl">#{{execution.prNumber}}</a></el-col>
                    <el-col :span="4">{{execution.executionStartedAt}}</el-col>
                    <el-col :span="4">{{execution.executionCompletedAt}}</el-col>
                </el-row>
            </el-card>
            <!-- <el-card :body-style="{padding: '15px'}">
                <el-row type="flex" justify="end">
                    <el-select v-model="currentGraph" placeholder="Graph">
                        <el-option
                            v-for="graph in graphs"
                            :key="graph"
                            :label="graph"
                            :value="graph">
                        </el-option>
                    </el-select>
                    <el-select v-model="currentScale" placeholder="Scale" :disabled="currentGraph == null" class="scale-select">
                        <el-option
                            v-for="scale in scales"
                            :key="scale"
                            :label="scale"
                            :value="scale">
                        </el-option>
                    </el-select>
                    <el-select v-model="currentQuery" placeholder="Query" :disabled="currentGraph == null" class="query-select">
                        <el-option
                            v-for="query in queries"
                            :key="query"
                            :label="query"
                            :value="query">
                        </el-option>
                    </el-select>
                </el-row>
            </el-card> -->
            <tabular-view :graphs="graphs" :spans="spans"></tabular-view>
        </el-main>
    </el-container>
</template>
<script>
import InspectStore from "@/util/InspectSharedStore";
import BenchmarkClient from "@/util/BenchmarkClient.js";
import TabularView from "./TabularView/TabularView.vue";
export default {
    components:{ TabularView },
    filters:{
        substringRepo(repoUrl){
            if(!repoUrl) return;
            return repoUrl.substring(19);
        }
    },
    data(){
        return {
            executionId: this.$route.params.executionId,
            currentGraph: InspectStore.getGraph(),
            currentScale: InspectStore.getScale(),
            currentQuery: InspectStore.getQuery(),
            execution: {},
            spans: [],
            graphs: []
        }
    },
    created(){
        BenchmarkClient.getExecutions(
        `{ executionById(id: "${this.executionId}"){ 
            prMergedAt 
            prNumber 
            prUrl 
            commit 
            status 
            executionStartedAt 
            executionCompletedAt 
            repoUrl
            } }`
        ).then(resp => {
            this.execution = resp.data.executionById;
        });
        BenchmarkClient.getSpans( `{ querySpans( limit: 500, executionName: "${this.executionId}"){ 
            id name duration tags { graphName executionName query scale repetition }} }`)
            .then((resp) => {
              this.spans = resp.data.querySpans;
              this.graphs = Array.from(
                new Set(this.spans.map(span => span.tags.graphName))
                );
            })
    },
    computed:{
        scales(){
            if(!this.currentGraph) return [];
            return Array.from(
                new Set(this.spans.filter(span=> span.tags.graphName === this.currentGraph).map(span => span.tags.scale))
                );
        },
        queries(){
            if(!this.currentGraph) return [];
            return Array.from(
                new Set(this.spans.filter(span=> span.tags.graphName === this.currentGraph).map(span => span.tags.query))
                );
        }
    },
    watch:{
        currentGraph(current, previous){
            if(current !== previous){ 
                this.currentScale = null;
                this.currentQuery = null;
            }
        }
    }
}
</script>
<style scoped>
.el-container{
      background-color: #f4f3ef;
}
h2{
    margin-bottom: 10px;
}
.query-select{
    min-width: 400px;
}
.scale-select{
    max-width: 100px;
}
</style>


