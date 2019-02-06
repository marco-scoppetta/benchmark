<template>
    <div>
        <div>{{executionId}}</div>
        <div>{{graphName}}</div>
        <div>{{currentScale}}</div>
        <div>{{query}}</div>
    </div>
</template>
<script>
import InspectStore from "@/util/InspectSharedStore";

export default {
    data(){
        return {
            executionId: this.$route.params.executionId,
            graphName: InspectStore.getGraph(),
            currentScale: InspectStore.getScale(),
            query: InspectStore.getQuery()
        }
    },
    created(){

    }
}
</script>

