package com.spingboot.jdbc_api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * First, use the BookingService class to create a JDBC-based service that books people into the system by name.
 */
@Component
public class BookingService {

    private final static Logger logger = LoggerFactory.getLogger(BookingService.class);

    //    The code has an autowired JdbcTemplate, a handy template class that does all the database interactions needed by the code below.
    private final JdbcTemplate jdbcTemplate;

    public BookingService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * You also have a book method aimed at booking multiple people.
     * It loops through the list of people, and for each person, inserts them into the BOOKINGS table using the JdbcTemplate.
     * This method is tagged with @Transactional, meaning that any failure causes the entire operation to roll back to its previous state, and to re-throw the original exception.
     * This means that none of the people will be added to BOOKINGS if one person fails to be added.
     *
     * @param persons
     */
    @Transactional
    public void book(String... persons) {
        for (String person : persons) {
            logger.info("Booking " + person + " in a seat...");
            jdbcTemplate.update("insert into BOOKINGS(FIRST_NAME) values (?)", person);
        }
    }

    /**
     * You also have a findAllBookings method to query the database.
     * Each row fetched from the database is converted into a String and then assembled into a List.
     *
     * @return
     */
    public List<String> findAllBookings() {
        return jdbcTemplate.query("select FIRST_NAME from BOOKINGS",
                (rs, rowNum) -> rs.getString("FIRST_NAME"));
    }

}
