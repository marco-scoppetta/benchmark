export default {
  state: {
    graphName: null,
    currentScale: null,
    query: null,
  },
  setGraph(graphName) {
    this.state.graphName = graphName;
  },
  setScale(scale) {
    this.state.currentScale = scale;
  },
  setQuery(query) {
    this.state.currentQuery = query;
  },
  // We want to get the values only once and then reset them.
  // These values should only be used as a transition between Overview and Inspect page.
  getGraph() {
    const graph = this.state.graphName;
    this.state.graphName = null;
    return graph;
  },
  getScale() {
    const scale = this.state.currentScale;
    this.state.currentScale = null;
    return scale;
  },
  getQuery() {
    const query = this.state.currentQuery;
    this.state.currentQuery = null;
    return query;
  },
};
