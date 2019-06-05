package grakn.benchmark.profiler.util;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import grakn.benchmark.common.configuration.BenchmarkConfiguration;
import grakn.benchmark.common.exception.BootupException;
import grakn.client.GraknClient;
import graql.lang.query.GraqlQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Stream;

import static graql.lang.Graql.parseList;

public class SchemaManager {
    private static final Logger LOG = LoggerFactory.getLogger(SchemaManager.class);

    private final BenchmarkConfiguration config;
    private final GraknClient tracingClient;

    public SchemaManager(BenchmarkConfiguration config, GraknClient tracingClient) {
        this.config = config;
        this.tracingClient = tracingClient;
    }

    public void loadSchema() {
        String keyspace = config.getKeyspace();
        GraknClient.Session session = traceInitKeyspace(tracingClient, keyspace);
        session.close();
    }

    private GraknClient.Session traceInitKeyspace(GraknClient client, String keyspace) {
        // time creation of keyspace and insertion of schema
        LOG.info("Adding schema to keyspace: " + keyspace);
        Span span = Tracing.currentTracer().newTrace().name("New Keyspace + schema: " + keyspace);
        span.start();

        GraknClient.Session session;
        try (Tracer.SpanInScope ws = Tracing.currentTracer().withSpanInScope(span)) {
            span.annotate("Opening new session");
            session = client.session(keyspace);
            span.annotate("Verifying keyspace does not exist");
            if (!keyspaceExists(keyspace)) {
                throw new BootupException("Keyspace " + keyspace + " already exists");
            }
            span.annotate("Loading qraql schema");
            loadSchema(session, config.getGraqlSchema());
        }

        span.finish();
        return session;
    }

    private void loadSchema(GraknClient.Session session, List<String> schemaQueries) {
        // load schema
        LOG.info("Initialising keyspace `" + session.keyspace() + "`...");
        try (GraknClient.Transaction tx = session.transaction().write()) {
            Stream<GraqlQuery> query = parseList(String.join("\n", schemaQueries));
            query.forEach(tx::execute);
            tx.commit();
        }
    }

    private boolean keyspaceExists(String keyspace){
        return tracingClient.keyspaces().retrieve().contains(keyspace);
    }
}
