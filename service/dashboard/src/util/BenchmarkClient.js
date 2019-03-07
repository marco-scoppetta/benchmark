function postData(url = ``, data = {}) {
  // Default options are marked with *
  return fetch(url, {
    method: "POST", // *GET, POST, PUT, DELETE, etc.
    mode: "cors", // no-cors, cors, *same-origin
    cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
    credentials: "same-origin", // include, *same-origin, omit
    headers: { "Content-Type": "application/json" },
    redirect: "follow", // manual, *follow, error
    referrer: "no-referrer", // no-referrer, *client
    body: JSON.stringify(data) // body data type must match "Content-Type" header
  }).then(response => response.json()); // parses response to JSON
}

function getSpans(query) {
  return postData("/span/query", { query });
}
function getExecutions(query) {
  return postData("/execution/query", { query });
}
function stopExecution(execution) {
  return postData("/execution/stop", execution);
}
function deleteExecution(execution) {
  return postData("/execution/delete", execution);
}
function triggerExecution(execution) {
  return postData("/execution/new", execution);
}
function getLatestCompletedExecutions(number){
  return getExecutions(
    `{ executions(status: ["COMPLETED"], orderBy: "prMergedAt", order:"desc", limit: ${number}){ id commit prMergedAt} }`
  ).then(executions => executions.data.executions);
}
function getExecutionsSpans(executions){
  return Promise.all(
    executions.map(exec =>
      getSpans(
        `{ querySpans( limit: 300, executionName: "${
          exec.id
        }"){ id name duration tags { graphName executionName query scale }} }`
      ).then(res => res.data.querySpans)
    )
  ).then(nestedSpans => nestedSpans.flatMap(x => x));
}

export default {
  getExecutions,
  stopExecution,
  deleteExecution,
  getSpans,
  getExecutionsSpans,
  getLatestCompletedExecutions,
  triggerExecution
};
