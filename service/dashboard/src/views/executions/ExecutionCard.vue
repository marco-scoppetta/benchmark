<template>
  <el-card>
    <el-row>
      <el-col :span="2">{{ execution.status }}</el-col>
      <el-col :span="8" @click.native="inspectExecution(execution.id)">{{ execution.id}}</el-col>
      <el-col :span="4">{{ execution.executionInitialisedAt | parseDate }}</el-col>
      <el-col :span="3">{{ execution.executionStartedAt | parseDate }}</el-col>
      <el-col :span="3">{{ execution.executionCompletedAt | parseDate }}</el-col>
      <el-col :span="2">
        <el-dropdown split-button type="primary" trigger="click">
          Actions
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item :v-show="isInProgress">GCloud command</el-dropdown-item>
            <el-dropdown-item :v-show="isInProgress" @click.native="stopExecution(execution)">Stop</el-dropdown-item>
            <el-dropdown-item @click.native="deleteExecution(execution)">Delete</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </el-col>
    </el-row>
  </el-card>
</template>
<script>
import BenchmarkClient from "@/util/BenchmarkClient.js";
export default {
  props: ["execution"],
  methods:{
    inspectExecution(id){
        this.$router.push({ path: `inspect/${id}`});
    },
    deleteExecution(execution){
      BenchmarkClient.deleteExecution(execution).then(()=>{ console.log("execution deleted.");});
    },
    stopExecution(execution){
      BenchmarkClient.stopExecution(execution).then(()=>{ console.log("execution stopped.");});
    }
  },
  filters:{
        parseDate(ISOdate){
            if(!ISOdate) return "N/A";
            const epoch = Date.parse(ISOdate);
            return new Date(epoch).toLocaleString('en-GB', { hour12:false } );
        }
  },
  computed: {
    isInProgress(){
      return this.execution.status === 'INITIALISING' || this.execution.status === 'RUNNING'
    }
  }
};
</script>
<style scoped>
.el-card {
  margin-bottom: 2px;
}
</style>
