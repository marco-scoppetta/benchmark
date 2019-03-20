<template>
    <el-row>
        <el-col :span="1"><i class="el-icon-circle-plus-outline" @click="expand=!expand"></i></el-col>
        <el-col :span="12">{{query}}</el-col>
        <el-col :span="3">{{min.duration | fixedMs}} ({{min.tags.repetition+1}})</el-col>
        <el-col :span="3">{{med | fixedMs}}</el-col>
        <el-col :span="3">{{max.duration | fixedMs}} ({{max.tags.repetition+1}})</el-col>
        <el-col :span="2">{{reps}}</el-col>
        <div v-show="expand">surprise mofooosss</div>
    </el-row>
</template>
<style scoped>
.el-row{
    margin: 10px 0px;
}
i {
    cursor: pointer;
}
</style>

<script>
export default {
    props: ["query", "spans", "currentScale"],
    data(){
        return {
            expand: false
        }
    },
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
            durations.sort((a, b) => a - b);
            const middle = (durations.length + 1) / 2;
            const isEven = durations.length % 2 === 0;
            return isEven ? (durations[middle - 1.5] + durations[middle - 0.5]) / 2 : durations[middle - 1];
        },
        reps(){
            return this.currentSpans.length;
        }
    }
}
</script>

