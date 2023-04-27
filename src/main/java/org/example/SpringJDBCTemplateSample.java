package org.example;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpringJDBCTemplateSample {

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

//        with Primary Key non auto-generated

//        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource);
//        insert.withTableName("contact");
//        Map<String, Object> params = new HashMap<>();
//        params.put("name","Bill Gates");
//        params.put("email","bill@microsoft.com");
//        params.put("address","Seattle, USA");
//        int result = insert.execute(params);
//
//        if (result > 0){
//            System.out.println("Insert Successfully");
//        }

        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource);
        insert.withTableName("contact").usingGeneratedKeyColumns("contact_id");
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("name", "Steve Jobs").addValue("email", "steve@apple.com").addValue("address", "USA").addValue("telephone", "123123");
        Number newId = insert.executeAndReturnKey(parameterSource);
        System.out.println("Insert Successfully. New Id = " + newId.intValue());

//        saveUsingSimpleJdbcInsertAndBeanPropertySqlParameterSource(dataSource);

//        saveUsingNamedParameter(dataSource);
//        saveNamedParameterJdbcTemplateBeanPropertySqlParameterSource(dataSource);

//        selectContactNamedParameterJdbcTemplateSQLParameterSource(dataSource);
        System.out.println("==============");
//        queryUsingNamedParameterJdbcTemplateMapSqlParameterResourceBeanPropertyRowMapper(dataSource);
//        queryListUsingNamedParameterJdbcTemplateSqlParameterSourceRowMapper(dataSource);
        queryListUsingNamedParameterJdbcTemplateMapSqlParameterResourceBeanPropertyRowMapper(dataSource);
    }



    private static void saveUsingSimpleJdbcInsertAndBeanPropertySqlParameterSource(SimpleDriverDataSource dataSource) {
        Contact contact = new Contact("Steve Job 1", "Email1", "steave1@apple.com", "213124123");
        System.out.println("==============");
        SimpleJdbcInsert insert1 = new SimpleJdbcInsert(dataSource);
        insert1.withTableName("contact");
        BeanPropertySqlParameterSource parameterSource1 = new BeanPropertySqlParameterSource(contact);
        int result = insert1.execute(parameterSource1);
        if (result > 0) {
            System.out.println("Inser Successfully!");
        }
    }

    private static void saveUsingNamedParameter(SimpleDriverDataSource dataSource) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        String sqlNamedParam = "INSERT INTO Contact (name, email, address, telephone) VALUES (:name1, :email1, :address, :mobile)";

        Map<String, String> params = new HashMap<>();
        params.put("name1", "Tommy");
        params.put("email1", "Tommy@gmail.com");
        params.put("address", "Tommy's address");
        params.put("mobile", "23213123");

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("name1", "Tommy2").addValue("email1", "Tommy2@gmail.com").addValue("address", "Tommy2's address");
        mapSqlParameterSource.addValue("mobile", "113");

        template.update(sqlNamedParam, mapSqlParameterSource);
    }

    //    NamedParameterJdbcTemplate + BeanPropertySqlParameterSource
    public static void saveNamedParameterJdbcTemplateBeanPropertySqlParameterSource(DataSource dataSource) {
        Contact contact = new Contact("Steve Job 2", "Email2", "steave2@apple.com", "213124123");
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        String sql = "INSERT INTO Contact (name, email,address, telephone) values (:name,:email,:address,:telephone)";
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(contact);
        template.update(sql, parameterSource);
    }

    public static void selectContactNamedParameterJdbcTemplateSQLParameterSource(DataSource dataSource) {
        NamedParameterJdbcTemplate parameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String sqlSelect = "SELECT * FROM Contact WHERE contact_id=:id";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource("id", 32);
        Contact contact = parameterJdbcTemplate.query(sqlSelect, mapSqlParameterSource, new ResultSetExtractor<Contact>() {
            @Override
            public Contact extractData(ResultSet rs) throws SQLException, DataAccessException {
                return getContact(rs);
            }
        });

        Contact contact1 = parameterJdbcTemplate.query(sqlSelect, mapSqlParameterSource, (rs) -> {
            return getContact(rs);

        });
        System.out.println(contact);
        System.out.println(contact1);
    }

    private static Contact getContact(ResultSet rs) throws SQLException {
        if (rs.next()) {
            Contact contact2 = new Contact();
            contact2.setContact_id(rs.getInt("contact_id"));
            contact2.setName(rs.getString("name"));
            contact2.setEmail(rs.getString("email"));
            contact2.setTelephone(rs.getString("telephone"));
            contact2.setAddress(rs.getString("address"));
            return contact2;
        }
        return null;
    }


    private static void queryUsingNamedParameterJdbcTemplateMapSqlParameterResourceBeanPropertyRowMapper(DataSource dataSource) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String sql = "SELECT * FROM Contact WHERE contact_id=:id";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("id", 24);
        Contact contact = namedParameterJdbcTemplate.queryForObject(sql, sqlParameterSource, BeanPropertyRowMapper.newInstance(Contact.class));
        System.out.println(contact);
    }

    private static void queryListUsingNamedParameterJdbcTemplateSqlParameterSourceRowMapper(DataSource dataSource) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        String sql = "SELECT * FROM Contact WHERE name=:name";
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource("name", "Steve Jobs");
        List<Contact> contactList = template.query(sql, sqlParameterSource, new RowMapper<Contact>() {
            @Override
            public Contact mapRow(ResultSet rs, int rowNum) throws SQLException {
                if (rs != null) {
                    Contact contact = new Contact();
                    contact.setAddress(rs.getString("address"));
                    contact.setName(rs.getString("name"));
                    contact.setTelephone(rs.getString("telephone"));
                    contact.setEmail(rs.getString("email"));
                    contact.setContact_id(rs.getInt("contact_id"));
                    return contact;
                }
                return null;
            }
        });

        System.out.println(contactList);
    }


    private static void queryListUsingNamedParameterJdbcTemplateMapSqlParameterResourceBeanPropertyRowMapper(DataSource dataSource) {
        String sql = "SELECT * FROM Contact WHERE name=:name";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource("name", "Tom");
        List<Contact> contactList =  namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, BeanPropertyRowMapper.newInstance(Contact.class));
        System.out.println(contactList);

    }


}
