package com.mpa.starter.controllers;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.neo4j.driver.v1.AccessMode;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import static org.neo4j.driver.v1.Values.parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Api(value = "Movies", description = "For testing and verification")
@RestController
@RequestMapping("/")
public class MovieController {
    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

    // Neo4j driver
    @Autowired
    Driver driver;


    @ApiOperation(value = "Returns the current version of the application", response = String.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "The current version is...") })
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

    @ApiOperation(value = "Returns the requested movie by title", response = String.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "The movie is...") })
    @GetMapping("movie/{title}")
    public Map<String, Object> getMovie(@PathVariable String title, HttpServletResponse response) {
        response.setStatus(200);
        String query = "Match (m:Movie{title:$title}) Return m";

        logger.info(String.format("query=%s", query));
        // Make the query
        try (Session session = driver.session(AccessMode.READ)) {
            List<Map<String, Object>> result = session.readTransaction(tx ->
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
    @ApiOperation(value = "Returns the requested actor by name", response = String.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "The person is...") })
    @GetMapping("person/{name}")
    public Map<String, Object> getActor(@PathVariable String name, HttpServletResponse response) {
        response.setStatus(200);
        String query = "Match (p:Person{name:$name}) Return p";

        logger.info(String.format("query=%s", query));
        // Make the query
        try (Session session = driver.session(AccessMode.READ)) {
            List<Map<String, Object>> result = session.readTransaction(tx ->
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
    @ApiOperation(value = "Returns the cast of the movie", response = String.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "The cast is...") })
    @GetMapping("movie/{title}/cast")
    public List<Map<String, Object>> getCast(@PathVariable String title, HttpServletResponse response) {
        response.setStatus(200);
        String query = "Match (m:Movie{title:$title})<-[:ACTED_IN]-(p) Return p";

        logger.info(String.format("query=%s", query));
        // Make the query
        try (Session session = driver.session(AccessMode.READ)) {
            List<Map<String, Object>> result = session.readTransaction(tx ->
                    tx.run(query,
                            parameters("title",title))
                            .list(row -> row.get("p").asMap()));

            if (result == null || result.size() == 0) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
            return result;
        }

    }

}
