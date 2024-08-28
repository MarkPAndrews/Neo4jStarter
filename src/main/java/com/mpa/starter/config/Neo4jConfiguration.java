package com.mpa.starter.config;

import org.neo4j.driver.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class Neo4jConfiguration {

    @Autowired
    Neo4jProperties neo4jProperties;

    /**
     * Create a new Driver instance
     *
     * @return Driver
     */
    @Bean
    public Driver neo4jDriver() {
        AuthToken token = neo4jProperties.getAuthToken();
        return GraphDatabase.driver(neo4jProperties.getUri(), token);
    }

    @Bean
    public SessionConfig neo4jSessionConfig() {
        return SessionConfig.builder().withDatabase(neo4jProperties.getDatabaseName()).build();
    }


}


