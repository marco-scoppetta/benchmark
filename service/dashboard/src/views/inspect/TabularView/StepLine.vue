<template>
  <div>
    <el-row class="currentRow">
      <el-col :span="14" class="name-column" :style="styleObject">
        <i :class="{ 'el-icon-circle-plus-outline': !expand, 'el-icon-remove-outline': expand }" @click="expandLine"/>
        <div>{{ spans[0].name }}</div>
      </el-col>
      <el-col :span="3">
        {{ min.duration | fixedMs }} ({{ min.repetition + 1 }})
      </el-col>
      <el-col :span="3">
        {{ med | fixedMs }}
      </el-col>
      <el-col :span="3">
        {{ max.duration | fixedMs }} ({{ max.repetition + 1 }})
      </el-col>
      <el-col :span="1">
        {{ reps }}
      </el-col>
    </el-row>
    <el-row v-if="expand">
      <step-line
        v-for="stepName in stepNames"
        :key="stepName"
        :spans="filterSpansByStep(stepName)"
        :padding="parseInt(padding)+10"
      />
    </el-row>
  </div>
</template>
<style scoped>
.currentRow{
  margin: 3px 0px;
  padding: 3px;
}
.currentRow:hover {
  background-color: #fd9789;
}
.name-column{
  display: flex;
  justify-content: start;
}
i {
  cursor: pointer;
  margin-right: 5px;
}
.el-col{
    text-align: center;
}
</style>

<script>
import BenchmarkClient from '@/util/BenchmarkClient';

export default {
  name: 'StepLine',
  filters: {
    fixedMs(num) {
      return `${Number(num / 1000).toFixed(3)} ms`;
    },
  },
  props: ['spans', 'padding'],
  data() {
    return {
      expand: false,
      children: null,
      stepNames: null,
      styleObject: {
        'padding-left': `${this.padding}px`,
        'text-align': 'left',
        'font-style': 'italic',
      },
    };
  },
  computed: {
    min() {
      let min = this.spans[0];
      this.spans.forEach((span) => {
        if (span.duration < min.duration) {
          min = span;
        }
      });
      return min;
    },
    max() {
      let max = this.spans[0];
      this.spans.forEach((span) => {
        if (span.duration > max.duration) {
          max = span;
        }
      });
      return max;
    },
    med() {
      const durations = this.spans.map(span => span.duration);
      durations.sort((a, b) => a - b);
      const middle = (durations.length + 1) / 2;
      const isEven = durations.length % 2 === 0;
      return isEven
        ? (durations[middle - 1.5] + durations[middle - 0.5]) / 2
        : durations[middle - 1];
    },
    reps() {
      return this.spans.length;
    },
  },
  methods: {
    expandLine() {
      this.expand = !this.expand;
      if (!this.expand) return;
      this.fetchChildrenSpans();
    },
    fetchChildrenSpans() {
      BenchmarkClient.getSpans(
        `{ childrenSpans( parentId: [${
          this.spans.map(span => `"${span.id}"`).join()
        }]){ id name duration parentId timestamp tags { childNumber }} }`,
      ).then((resp) => {
        this.children = this.attachRepetition(resp.data.childrenSpans);
        this.stepNames = sortStepNames(this.children);
      });
    },
    attachRepetition(childrenSpans) {
      // Children spans don't have the tags repetition and repetitions, so we attach them here taking the values from parent
      return childrenSpans.map((span) => {
        const parentSpan = this.spans.filter(parent => parent.id === span.parentId)[0];
        return Object.assign({ repetition: parentSpan.repetition, repetitions: parentSpan.repetitions }, span);
      });
    },
    filterSpansByStep(stepName) {
      return this.children.filter(child => child.name === stepName);
    },
  },
};

function sortStepNames(children) {
  const { parentId } = children[0];
  return children
    .filter(c => c.parentId === parentId)
    .sort((a, b) => a.timestamp > b.timestamp)
    .map(child => child.name);
}
</script>
