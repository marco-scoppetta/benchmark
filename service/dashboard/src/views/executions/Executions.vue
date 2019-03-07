<template>
  <section class="el-container is-vertical page-container">
    <!-- <header class="el-header">Header</header> -->
    <main class="el-main">
      <h2>Benchmark Executions</h2>
      <el-row>
          <el-popover
            placement="right-start"
            title="Create Execution"
            width="200"
            trigger="manual"
            v-model="popoverVisible">
            <el-row>
              Repo URL: <el-input v-model="newExecution.repoUrl"></el-input>
            </el-row>
            <el-row>
              Commit: <el-input v-model="newExecution.commit"></el-input>
            </el-row>
            <div style="text-align: right; margin: 0">
              <el-button size="mini" type="text" @click="popoverVisible=false">cancel</el-button>
              <el-button type="primary" size="mini" @click="triggerExecution">send</el-button>
            </div>
        <el-button type="success" circle icon="el-icon-plus" slot="reference" @click="popoverVisible=!popoverVisible"></el-button>
          </el-popover>
        <!-- <el-button type="success" circle icon="el-icon-plus"></el-button> -->
      </el-row>
      <execution-card
        v-for="exec in executions"
        v-bind:key="exec.id"
        :execution="exec"
      ></execution-card>
    </main>
  </section>
</template>

<script>
import ExecutionCard from "./ExecutionCard.vue";
import BenchmarkClient from "@/util/BenchmarkClient.js";

export default {
  name: "ExecutionsPage",
  data() {
    return {
      popoverVisible: false,
      executions: [],
      newExecution: {
        commit: undefined,
        repoUrl: undefined
      }
    };
  },
  components: { ExecutionCard },
  created() {
    BenchmarkClient.getExecutions(
      `{ executions{ 
          id
          prMergedAt 
          prNumber 
          prUrl 
          commit 
          status 
          executionInitialisedAt
          executionStartedAt 
          executionCompletedAt 
          vmName } }`
    ).then(execs => {
      this.executions = execs.data.executions;
    });
  },
  methods:{
    async triggerExecution(){
      await BenchmarkClient.triggerExecution(this.newExecution).then(() =>{ 
        // TODO re-fetch In Progress executions to update the page
        this.$notify({
          title: 'Success',
          message: 'New Execution triggered successfully!',
          type: 'success'
        });
      }).catch(() => {
         this.$notify.error({
            title: 'Error',
            message: 'It was not possible to trigger new Execution.'
          });
      });
      this.newExecution.commit = undefined;
    }
  }
};
</script>

<style scoped>
.el-container {
  padding: 0 30px;
  background-color: #f4f3ef;
}

.el-header {
  text-align: start;
  height: 30px;
}
.panel {
  background-color: white;
}
h2 {
  margin-bottom: 20px;
}
.cards-row {
  margin-bottom: 20px;
}
</style>
