<template>
    <el-row>
        <el-col :span="12">{{query}}</el-col>
        <el-col :span="4">{{min}}</el-col>
        <el-col :span="4">{{med}}</el-col>
        <el-col :span="4">{{max}}</el-col>
    </el-row>
</template>
<script>
export default {
    props: ["query", "spans", "currentScale"],
    computed:{
        currentSpans(){
            return this.spans.filter(span => span.tags.scale === this.currentScale);
        },
        min(){
            return Math.min(...this.currentSpans.map(span => span.duration));
        },
        max(){
            return Math.max(...this.currentSpans.map(span => span.duration));
        },
        med(){
            const numbers = this.currentSpans.map(span => span.duration);
            const middle = (numbers.length + 1) / 2;
            const sorted = [...numbers].sort(); // avoid mutating when sorting
            const isEven = sorted.length % 2 === 0;
            return isEven ? (sorted[middle - 1.5] + sorted[middle - 0.5]) / 2 : sorted[middle - 1];
        }
    }
}
</script>

