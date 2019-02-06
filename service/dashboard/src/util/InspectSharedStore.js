export default {
  state: {
    graphName: null,
    currentScale: null,
    query: null
  },
  setGraph(graphName) {
    this.state.graphName = graphName;
  },
  setScale(scale) {
    this.state.currentScale = scale;
  },
  setQuery(query) {
    this.state.query = query;
  },
  getGraph() {
    return this.state.graphName;
  },
  getScale() {
    return this.state.currentScale;
  },
  getQuery() {
    return this.state.query;
  }
};
