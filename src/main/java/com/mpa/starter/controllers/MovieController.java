package com.mpa.starter.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.neo4j.driver.*;

import static org.neo4j.driver.Values.parameters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

//@Api(value = "Movies", description = "For testing and verification")
@RestController
@RequestMapping("/")
public class MovieController {
    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

    // Neo4j driver
    @Autowired
    Driver driver;

    @Autowired
    SessionConfig sessionConfig;
    @Operation(description = "Returns the current version of the application")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "The current version is...") })
    @GetMapping("version")
    public String getVersion(HttpServletResponse response) {
        response.setStatus(200);
        String [] path = this.getClass().getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath().split("/");

        String version = "{\"version\":\""+ path[path.length-3].replaceFirst(".jar!","") +"\"}";
        logger.debug(version);
        return version;
    }

    @Operation(description = "Returns the requested movie by title")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "The movie is...") })
    @GetMapping("movie/{title}")
    public Map<String, Object> getMovie(@PathVariable String title, HttpServletResponse response, @RequestHeader("Authorization") String auth ) {
        String bearerToken = auth.replace("Bearer ", "");

        response.setStatus(200);
        String query = "Match (m:Movie{title:$title}) Return m";

        logger.info(String.format("query=%s", query));
        // Make the query
        try (Session session = driver.session(Session.class,sessionConfig, AuthTokens.bearer(bearerToken))) {
            List<Map<String, Object>> result = session.executeRead(tx ->
                    tx.run(query,
                            parameters("title",title))
                            .list(row -> row.get("m").asMap()));

            if (result == null || result.size() == 0) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
            return result.get(0);
        }

    }
    @Operation(description = "Returns the requested actor by name")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "The person is...") })
    @GetMapping("person/{name}")
    public Map<String, Object> getActor(@PathVariable String name, HttpServletResponse response) {
        response.setStatus(200);
        String query = "Match (p:Person{name:$name}) Return p";

        logger.info(String.format("query=%s", query));
        // Make the query
        try (Session session = driver.session(sessionConfig)) {
            List<Map<String, Object>> result = session.executeRead(tx ->
                    tx.run(query,
                            parameters("name",name))
                            .list(row -> row.get("p").asMap()));

            if (result == null || result.size() == 0) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
            return result.get(0);
        }

    }
    @Operation(description = "Returns the tanks")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "The cast is...") })
    @GetMapping("tanks")
    public List<Map<String, Object>> getTanks(HttpServletResponse response, @RequestHeader("Authorization") String auth ) {
        String bearerToken = auth.replace("Bearer ", "");
        response.setStatus(200);
        String query = "Match (n:Tank) Return n";

        logger.info(String.format("query=%s", query));
        // Make the query
        try (Session session = driver.session(Session.class,sessionConfig, AuthTokens.bearer(bearerToken))) {
            List<Map<String, Object>> result = session.executeRead(tx ->
                    tx.run(query)
                            .list(row -> row.get("n").asMap()));

            if (result == null || result.size() == 0) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
            return result;
        }

    }

}
