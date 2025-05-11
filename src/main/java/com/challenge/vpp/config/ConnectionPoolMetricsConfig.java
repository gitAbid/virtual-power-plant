package com.challenge.vpp.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Configuration for monitoring R2DBC connection pool metrics.
 * Periodically logs connection pool statistics to provide visibility into pool usage.
 */
@Configuration
@EnableScheduling
public class ConnectionPoolMetricsConfig {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionPoolMetricsConfig.class);
    
    private final ConnectionFactory connectionFactory;
    
    public ConnectionPoolMetricsConfig(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        ConnectionFactoryMetadata metadata = connectionFactory.getMetadata();
        logger.info("Initialized connection pool metrics for: {}", metadata.getName());
    }
    
    /**
     * Logs connection pool metrics every minute.
     * This provides basic monitoring of the connection pool's state.
     */
    @Scheduled(fixedRate = 60000) // Log every minute
    public void logConnectionPoolMetrics() {
        if (connectionFactory instanceof ConnectionPool pool) {
            pool.getMetrics().ifPresent(metrics -> {
                logger.info("Connection Pool Metrics - Acquired: {}, Allocated: {}, Pending: {}",
                        metrics.acquiredSize(),
                        metrics.allocatedSize(),
                        metrics.pendingAcquireSize());
            });
        } else {
            logger.warn("Connection factory is not an instance of ConnectionPool, cannot retrieve metrics");
        }
    }
}
