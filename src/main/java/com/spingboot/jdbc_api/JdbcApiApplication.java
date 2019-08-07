package com.spingboot.jdbc_api;

import com.spingboot.jdbc_api.entity.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @SpringBootApplication is a convenience annotation that adds all of the following:
 * @Configuration tags the class as a source of bean definitions for the application context.
 * @EnableAutoConfiguration tells Spring Boot to start adding beans based on classpath settings, other beans, and various property settings.
 * @ComponentScan tells Spring to look for other components, configurations, and services in the the hello package. In this case, there aren’t any.
 * <p>
 * This Application class implements Spring Boot’s CommandLineRunner,
 * which means it will execute the run() method after the application context is loaded up.
 */
@SpringBootApplication
public class JdbcApiApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(JdbcApiApplication.class);

    /**
     * The main() method uses Spring Boot’s SpringApplication.run() method to launch an application.
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(JdbcApiApplication.class, args);
    }

    /**
     * Spring provides a template class called JdbcTemplate that makes it easy to work with SQL relational databases and JDBC.
     * Most JDBC code is mired in resource acquisition, connection management, exception handling, and general error checking that is wholly unrelated to what the code is meant to achieve.
     * The JdbcTemplate takes care of all of that for you. All you have to do is focus on the task at hand.
     */
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        log.info("Creating tables");

        // First, you install some DDL using JdbcTemplate’s `execute method.
        jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE customers(" + "id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");

        // Second, you take a list of strings and using Java 8 streams, split them into firstname/lastname pairs in a Java array.
        // Split up the array of whole names into an array of first/last names
        List<Object[]> splitUpNames = Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long").stream()
                .map(name -> name.split(" "))
                .collect(Collectors.toList());

        // Use a Java 8 stream to print out each tuple of the list
        splitUpNames.forEach(name -> log.info(String.format("Inserting customer record for %s %s", name[0], name[1])));

        // Then you install some records in your newly created table using JdbcTemplate’s `batchUpdate method.
        // The first argument to the method call is the query string,
        // the last argument (the array of Object s) holds the variables to be substituted into the query where the “?” characters are.
        // For single insert statements, JdbcTemplate’s `insert method is good. But for multiple inserts, it’s better to use batchUpdate.
        // Use ? for arguments to avoid SQL injection attacks by instructing JDBC to bind variables.
        // Uses JdbcTemplate's batchUpdate operation to bulk load data
        jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);

        log.info("Querying for customer records where first_name = 'Josh':");
        //Finally you use the query method to search your table for records matching the criteria.
        // You again use the “?” arguments to create parameters for the query, passing in the actual values when you make the call.
        // The last argument is a Java 8 lambda used to convert each result row into a new Customer object.
        jdbcTemplate.query(
                "SELECT id, first_name, last_name FROM customers WHERE first_name = ?", new Object[]{"Josh"},
                (rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"))
        ).forEach(customer -> log.info(customer.toString()));
    }
}
