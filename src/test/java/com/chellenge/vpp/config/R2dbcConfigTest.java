package com.chellenge.vpp.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class R2dbcConfigTest {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Test
    public void testConnectionPoolIsConfigured() {
        // Verify that the connection factory is a ConnectionPool
        assertTrue(connectionFactory instanceof ConnectionPool, 
                "ConnectionFactory should be an instance of ConnectionPool");
        
        ConnectionPool pool = (ConnectionPool) connectionFactory;
        
        // Verify that the connection pool metrics are available
        assertTrue(pool.getMetrics().isPresent(), "Connection pool metrics should be available");
        
        pool.getMetrics().ifPresent(metrics -> {
            // Log the initial state of the connection pool
            System.out.println("Initial Connection Pool Metrics:");
            System.out.println("Acquired: " + metrics.acquiredSize());
            System.out.println("Allocated: " + metrics.allocatedSize());
            System.out.println("Pending: " + metrics.pendingAcquireSize());
        });
        
        // Test that we can execute a simple query using the connection pool
        Mono<Integer> result = Mono.from(connectionFactory.create())
                .flatMap(connection -> Mono.from(connection.createStatement("SELECT 1")
                        .execute())
                        .flatMap(result1 -> Mono.from(result1.map((row, metadata) -> row.get(0, Integer.class))))
                        .doFinally(signalType -> connection.close()));
        
        // Verify the query result
        StepVerifier.create(result)
                .expectNext(1)
                .verifyComplete();
        
        // Log the state after query execution
        pool.getMetrics().ifPresent(metrics -> {
            System.out.println("Connection Pool Metrics After Query:");
            System.out.println("Acquired: " + metrics.acquiredSize());
            System.out.println("Allocated: " + metrics.allocatedSize());
            System.out.println("Pending: " + metrics.pendingAcquireSize());
        });
    }
}
