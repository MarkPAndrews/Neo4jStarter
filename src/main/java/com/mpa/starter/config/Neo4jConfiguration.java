package com.mpa.starter.config;

import org.neo4j.driver.v1.AuthToken;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
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

}


