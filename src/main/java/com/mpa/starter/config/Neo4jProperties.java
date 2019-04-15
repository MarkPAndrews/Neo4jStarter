package com.mpa.starter.config;

import org.neo4j.driver.v1.AuthToken;
import org.neo4j.driver.v1.AuthTokens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
public class Neo4jProperties {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${neo4j.scheme}")
    private String scheme;

    @Value("${neo4j.host}")
    private String host;

    @Value("${neo4j.port}")
    private String port;

    @Value("${neo4j.policy:#{null}}")
    private String routingPolicy;

    @Value("${neo4j.auth.type:basic}")
    private String authType;

    @Value("${neo4j.auth.username:#{null}}")
    private String username;

    @Value("${neo4j.auth.password:#{null}}")
    private String password;

    @Value("${neo4j.auth.ticket:#{null}}")
    private String ticket;

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getRoutingPolicy() {
        return routingPolicy;
    }

    public void setRoutingPolicy(String routingPolicy) {
        this.routingPolicy = routingPolicy;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }


    /**
     * Get the URI for the Neo4j Server
     *
     * @return String
     */
    public String getUri() {

        // Get the base URI (ie bolt://localhost:7474)
        String uri = String.format("%s://%s:%s", scheme, host, port);

        // If there is a CC routing policy, append it to the end of the string
        if (scheme.equals("bolt+routing") && routingPolicy != null) {
            uri += "?policy=" + routingPolicy;
        }

        return uri;
    }

    /**
     * Use the neo4j.auth.type property to create an appropriate Auth Token
     *
     * @return AuthToken
     */
    public AuthToken getAuthToken() {
        switch (authType) {
            case "basic":
                return AuthTokens.basic(username, password);
            case "kerberos":
                return AuthTokens.kerberos(ticket);
            default:
                return AuthTokens.none();
        }
    }
}
