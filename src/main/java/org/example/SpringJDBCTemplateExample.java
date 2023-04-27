package org.example;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SpringJDBCTemplateExample {

    public static void main(String[] args) {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
        dataSource.setUrl("jdbc:sqlserver://localhost:1433;databaseName=contactdb;trustServerCertificate=true");
        dataSource.setUsername("sa");
        dataSource.setPassword("abcd1234");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String sqlInsert = "INSERT INTO contact (name, email, address, telephone) VALUES (?,?,?,?)";
        jdbcTemplate.update(sqlInsert, "Tom", "tome1@gmail.com", "USA", "12345");

        String sqlUpdate = "UPDATE contact set email = ? where name=?";
        jdbcTemplate.update(sqlUpdate, "tome2@gmail.com", "Tom");

        String sqlSelect = "select * from contact";
        List<Contact> listContact = jdbcTemplate.query(sqlSelect, new RowMapper<Contact>() {
            @Override
            public Contact mapRow(ResultSet rs, int rowNum) throws SQLException {
                Contact contact = new Contact();
                contact.setEmail(rs.getString("email"));
                contact.setName(rs.getString("name"));
                contact.setAddress(rs.getString("address"));
                contact.setTelephone(rs.getString("telephone"));
                return contact;
            }
        });
//        optimal
        List<Contact> listContacts = jdbcTemplate.query(sqlSelect, BeanPropertyRowMapper.newInstance(Contact.class));
        System.out.println("======");
        for (Contact contact : listContacts) {
            System.out.println(contact);
        }
        System.out.println("======");
        String sql = "SELECT * FROM contact WHERE contact_id=" + 12;
        Contact contactObject = jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(Contact.class));


        System.out.println(contactObject);
        System.out.println("======");
        for (Contact contact : listContact) {
            System.out.println(contact);
        }

        try {
            //        String sqlDelete = "DELETE FROM contact WHERE name=?";
//        jdbcTemplate.update(sqlDelete, "Tom");

        } catch (DataAccessException ex) {
            ex.printStackTrace();
        }


    }
}
