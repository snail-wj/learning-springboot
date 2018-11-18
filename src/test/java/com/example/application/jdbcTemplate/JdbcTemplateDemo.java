package com.example.application.jdbcTemplate;

import com.example.application.springboot.jdbcModel.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JdbcTemplateDemo {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testQuery(){
        String sql = "select id, name, age from person";
        List<Person> personList = jdbcTemplate.query(sql, new RowMapper<Person>() {

            @Autowired
            public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
                Person person = new Person();
                person.setId(rs.getInt("id"));
                person.setName(rs.getString("name"));
                person.setAge(rs.getInt("age"));
                return person;
            }

        });
        System.out.println(personList);
    }
}
