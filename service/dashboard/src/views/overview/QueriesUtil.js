function buildQueriesMap(queries) {
  let matchQuery = 0;
  let matchInsertQuery = 0;
  let insertQuery = 0;
  let computeQuery = 0;
  let otherQuery = 0;
  const queriesMap = {};
  queries.forEach(query => {
    let value;
    if (query.includes("compute")) {
      value = `computeQuery${++computeQuery}`;
    } else if (query.includes("insert") && query.includes("match")) {
      value = `matchInsertQuery${++matchInsertQuery}`;
    } else if (query.includes("insert")) {
      value = `insertQuery${++insertQuery}`;
    } else if (query.includes("match")) {
      value = `matchQuery${++matchQuery}`;
    } else {
      value = `query${++otherQuery}`;
    }
    queriesMap[query] = value;
  });
  return queriesMap;
}

/**
 * queriesTime structure => [
 *                             {
 *                              query: "",
 *                              times: [ { commit: "", avgTime: "" }, ... ]
 *                              },
 *                              ...
 *                           ]
 */
function buildQueriesTimes(queries, spans, executions, currentScale) {
  return queries.map(query => {
    // Find all the spans related to the current query
    const querySpans = spans.filter(span => span.tags.query === query);
    // For each commit, compute the average time the current query took to execute
    const times = executions.map(exec => {
      // Collect all the spans relative to this current commit and query
      const executionQuerySpans = querySpans.filter(
        span =>
          span.tags.executionName === exec.id && span.tags.scale == currentScale
      );
      // Compute average time combining all the repetitions
      const avgTime = computeAvgTime(executionQuerySpans);
      const stdDeviation = computeStdDeviation(executionQuerySpans, avgTime);

      return {
        commit: exec.commit,
        avgTime: avgTime / 1000,
        stdDeviation: stdDeviation / 1000,
        repetitions: executionQuerySpans.length,
        executionId: exec.id
      };
    });
    return { query, times };
  });
}

function computeAvgTime(spans) {
  return spans.reduce((a, b) => a + b.duration, 0) / spans.length;
}

function computeStdDeviation(spans, avgTime) {
  const sum = spans
    .map(span => Math.pow(span.duration - avgTime, 2))
    .reduce((a, b) => a + b, 0);
  return Math.sqrt(sum / spans.length);
}

export default {
  buildQueriesMap,
  buildQueriesTimes
};
