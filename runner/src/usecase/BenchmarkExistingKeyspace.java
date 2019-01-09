package grakn.benchmark.runner.usecase;

import grakn.benchmark.runner.executor.QueryExecutor;

public class BenchmarkExistingKeyspace {
    private final QueryExecutor queryExecutor;
    private final int repetitionsPerQuery;

    public BenchmarkExistingKeyspace(QueryExecutor queryExecutor, int repetitionsPerQuery) {
        this.queryExecutor = queryExecutor;
        this.repetitionsPerQuery = repetitionsPerQuery;
    }

    public void start(){
        int numConcepts = queryExecutor.aggregateCount();
        queryExecutor.processStaticQueries(repetitionsPerQuery, numConcepts, "Preconfigured DB - no data gen");
    }
}
