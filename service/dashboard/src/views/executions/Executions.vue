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
    },
    copyStringToClipboard(vmName) {
      copyToClipboard(
        "gcloud compute ssh ubuntu@" + vmName + " --zone=us-east1-b"
      );
    }
  }
};

const copyToClipboard = str => {
  const el = document.createElement("textarea"); // Create a <textarea> element
  el.value = str; // Set its value to the string that you want copied
  el.setAttribute("readonly", ""); // Make it readonly to be tamper-proof
  el.style.position = "absolute";
  el.style.left = "-9999px"; // Move outside the screen to make it invisible
  document.body.appendChild(el); // Append the <textarea> element to the HTML document
  const selected =
    document.getSelection().rangeCount > 0 // Check if there is any content selected previously
      ? document.getSelection().getRangeAt(0) // Store selection if found
      : false; // Mark as false to know no selection existed before
  el.select(); // Select the <textarea> content
  document.execCommand("copy"); // Copy - only works as a result of a user action (e.g. click events)
  document.body.removeChild(el); // Remove the <textarea> element
  if (selected) {
    // If a selection existed before copying
    document.getSelection().removeAllRanges(); // Unselect everything on the HTML document
    document.getSelection().addRange(selected); // Restore the original selection
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
