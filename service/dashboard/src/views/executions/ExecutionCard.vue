<template>
  <el-card>
    <el-row>
      <el-col :span="2">
        {{ execution.status }}
      </el-col>
      <el-col :span="8" @click.native="inspectExecution(execution.id)">
        {{execution.id}}
      </el-col>
      <el-col :span="4">
        {{execution.executionInitialisedAt | parseDate}}
      </el-col>
      <el-col :span="3">
        {{ execution.executionStartedAt | parseDate }}
      </el-col>
      <el-col :span="3">
        {{ execution.executionCompletedAt | parseDate}}
      </el-col>
      <el-col :span="2">
        <el-dropdown split-button type="primary" trigger="click">
          Actions
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              :v-show="isInProgress"
              @click.native="copyStringToClipboard(execution.vmName)"
            >
              GCloud command
            </el-dropdown-item>
            <el-dropdown-item
              :v-show="isInProgress"
              @click.native="stopExecution(execution)"
            >
              Stop
            </el-dropdown-item>
            <el-dropdown-item
              @click.native="deleteExecution(execution)"
            >
              Delete
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </el-col>
    </el-row>
  </el-card>
</template>
<script>
import BenchmarkClient from '@/util/BenchmarkClient';

export default {
  filters: {
    parseDate(ISOdate) {
      if (!ISOdate) return 'N/A';
      const epoch = Date.parse(ISOdate);
      return new Date(epoch).toLocaleString('en-GB', { hour12: false });
    },
  },
  props: ['execution'],
  computed: {
    isInProgress() {
      return false;
      // return
      //   this.execution.status === 'INITIALISING'
      //   || this.execution.status === 'RUNNING'
      // ;
    },
  },
  methods: {
    inspectExecution(id) {
      this.$router.push({ path: `inspect/${id}` });
    },
    deleteExecution(execution) {
      BenchmarkClient.deleteExecution(execution).then(() => {
        console.log('execution deleted.');
      });
    },
    stopExecution(execution) {
      BenchmarkClient.stopExecution(execution).then(() => {
        console.log('execution stopped.');
      });
    },
    copyStringToClipboard(vmName) {
      copyToClipboard(
        `gcloud compute ssh ubuntu@${vmName} --zone=us-east1-b`,
      );
    },
  },
};
const copyToClipboard = (str) => {
  const el = document.createElement('textarea'); // Create a <textarea> element
  el.value = str; // Set its value to the string that you want copied
  el.setAttribute('readonly', ''); // Make it readonly to be tamper-proof
  el.style.position = 'absolute';
  el.style.left = '-9999px'; // Move outside the screen to make it invisible
  document.body.appendChild(el); // Append the <textarea> element to the HTML document
  const selected = document.getSelection().rangeCount > 0 // Check if there is any content selected previously
    ? document.getSelection().getRangeAt(0) // Store selection if found
    : false; // Mark as false to know no selection existed before
  el.select(); // Select the <textarea> content
  document.execCommand('copy'); // Copy - only works as a result of a user action (e.g. click events)
  document.body.removeChild(el); // Remove the <textarea> element
  if (selected) {
    // If a selection existed before copying
    document.getSelection().removeAllRanges(); // Unselect everything on the HTML document
    document.getSelection().addRange(selected); // Restore the original selection
  }
};
</script>
<style scoped>
.el-card {
  margin-bottom: 2px;
}
</style>
