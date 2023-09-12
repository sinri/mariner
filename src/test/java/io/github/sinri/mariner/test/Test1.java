package io.github.sinri.mariner.test;

import io.github.sinri.mariner.helper.PropertiesFileReader;
import io.github.sinri.mariner.mysql.MySQLDataSource;
import io.github.sinri.mariner.mysql.matrix.QueriedResult;

import java.sql.SQLException;
import java.util.UUID;

public class Test1 {
    public static void main(String[] args) {
        try {
            PropertiesFileReader propertiesFileReader = new PropertiesFileReader("config.properties");

//        System.out.println(propertiesFileReader.read("mariner.mysql.test1.host"));

            MySQLDataSource dataSource = MySQLDataSource.buildFromConfigProperties(propertiesFileReader, "test1");

            QueriedResult queriedResult = dataSource.executeForQueriedMatrix("select * from cas_login_session limit 5");
            queriedResult.getRowList().forEach(queriedRow -> {
                System.out.println("queried row: "+queriedRow.toString());
            });

            QueriedResult queriedResult1 = dataSource.executeForQueriedMatrix("insert into cas_login_session (username,cas_token,token,expire,create_time) values('tester','" + UUID.randomUUID() + "','" + UUID.randomUUID() + "'," + (System.currentTimeMillis() / 1000) + ",now())");
            System.out.println("inserted, afx="+queriedResult1.getAffectedRows()+", last inserted id="+queriedResult1.getLastInsertedId());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
