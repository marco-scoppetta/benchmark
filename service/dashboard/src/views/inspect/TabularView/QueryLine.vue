<template>
    <el-row>
        <el-col :span="12">{{query}}</el-col>
        <el-col :span="3">{{min.duration | fixedMs}} ({{min.tags.repetition+1}})</el-col>
        <el-col :span="3">{{med | fixedMs}}</el-col>
        <el-col :span="3">{{max.duration | fixedMs}} ({{max.tags.repetition+1}})</el-col>
        <el-col :span="3">{{reps}}</el-col>
    </el-row>
</template>
<script>
export default {
    props: ["query", "spans", "currentScale"],
    filters: {
            fixedMs(num){
                return `${Number(num/1000).toFixed(3)} ms`;
            }
        },
    computed:{
        currentSpans(){
            return this.spans.filter(span => span.tags.scale === this.currentScale);
        },
        min(){
            let min = this.currentSpans[0];
            this.currentSpans.forEach(span => {
                if(span.duration < min.duration){
                    min = span;
                }
            })
            return min;
        },
        max(){
            let max = this.currentSpans[0];
            this.currentSpans.forEach(span => {
                if(span.duration > max.duration){
                    max = span;
                }
            })
            return max;
        },
        med(){
            const durations = this.currentSpans.map(span => span.duration);
            const middle = (durations.length + 1) / 2;
            const sorted = [...durations].sort(); // avoid mutating when sorting
            const isEven = sorted.length % 2 === 0;
            return isEven ? (sorted[middle - 1.5] + sorted[middle - 0.5]) / 2 : sorted[middle - 1];
        },
        reps(){
            return this.currentSpans.length;
        }
    }
}
</script>

