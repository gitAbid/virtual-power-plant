package com.chellenge.vpp.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class R2dbcConfigTest {

    private static final Logger logger = LoggerFactory.getLogger(R2dbcConfigTest.class);

    @Autowired
    private ConnectionFactory connectionFactory;

    @Test
    public void testConnectionPoolIsConfigured() {
        // Verify that the connection factory is a ConnectionPool
        assertInstanceOf(ConnectionPool.class, connectionFactory, "ConnectionFactory should be an instance of ConnectionPool");
        
        ConnectionPool pool = (ConnectionPool) connectionFactory;
        
        // Log initial metrics
        logPoolMetrics(pool, "Initial");

        // Execute a simple query to test the connection
        Mono<Void> testQuery = pool.create()
            .flatMap(connection -> 
                Mono.from(connection.createStatement("SELECT 1").execute())
                    .doFinally(signalType -> connection.close())).then();

        StepVerifier.create(testQuery)
            .verifyComplete();

        // Log metrics after query
        logPoolMetrics(pool, "After Query");
    }

    private void logPoolMetrics(ConnectionPool pool, String phase) {
        pool.getMetrics().ifPresent(metrics -> {
            logger.info("Connection Pool Metrics - {}:", phase);
            logger.info("Acquired: {}", metrics.acquiredSize());
            logger.info("Allocated: {}", metrics.allocatedSize());
            logger.info("Pending: {}", metrics.pendingAcquireSize());
        });
    }
}
