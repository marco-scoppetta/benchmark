const graphqlHTTP = require('express-graphql');
const { makeExecutableSchema } = require('graphql-tools');

function SpansController(esClient){
    this.client = esClient;
}


const typeDefs = `
  type Query {
    querySpans(
        parentId: String,
        offset: Int, 
        limit: Int): [QuerySpan]

    executionSpans(
        executionName: String,
        graphTypeName: String
    ): [ExecutionSpan]
  }

  type ExecutionSpan {
    id: String!
    duration: Int!
    name: String!
    tags: ExecutionSpanTag
  }

  type ExecutionSpanTag {
    configurationName: String,
    description: String,
    executionName: String,
    concurrentClient: Int,
    graphGeneratorDefinition: String,
    queryRepetitions: Int,
    graphScale: Int
  }

  type QuerySpan {
    id: String!
    parentId: String
    duration: Int!
    name: String!
    tags: QuerySpanTag
  }

  type QuerySpanTag {
    type: String,
    query: String,
    repetition: Int,
    repetitions: Int
  }
`;


function filterExecutionSpans(args){
    const must = [{ match: { name: "concurrent-execution"} } ];
    if(args.graphGeneratorDefinition) {
        must.push({ match: { "tags.graphGeneratorDefinition": args.graphGeneratorDefinition }});
    }
    if(args.executionName) {
        must.push({ match: { "tags.executionName": args.executionName }});
    }
    return { query: { bool: { must } } };
}

function limitQuery(offset, limit){
    return {from : offset || 0 , size : limit || 50};
}

function filterQuerySpans(args){
    const must = [{ match: { name: "query"} } ];
    if(args.parentId) {
        must.push({ match: { "parentId": args.parentId }});
    }
    return { query: { bool: { must } } };
}

const resolvers = {
    Query: {
        querySpans: (object, args, context, info) => {
            let body = {};
            Object.assign(body, limitQuery(args.offset, args.limit));
            Object.assign(body, filterQuerySpans(args));
            const queryObject = Object.assign({index: "benchmark*", type: "span"}, { body });
            return context.client.search(queryObject).then(result => result.hits.hits.map(res => res._source));
        },
        executionSpans: (object, args, context, info) => {
            let body = {};
            Object.assign(body, filterExecutionSpans(args));
            const queryObject = Object.assign({index: "benchmark*", type: "span"}, { body });
            return context.client.search(queryObject).then(result => result.hits.hits.map(res => res._source));
        }
    }
}

const schema = makeExecutableSchema({typeDefs, resolvers})

SpansController.prototype.query = function query(){
    return graphqlHTTP({
        schema,
        context: { client: this.client},
    });
}


module.exports = SpansController;