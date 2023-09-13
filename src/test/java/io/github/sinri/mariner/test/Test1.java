package io.github.sinri.mariner.test;

import io.github.sinri.mariner.helper.MarinerPropertiesFileReader;
import io.github.sinri.mariner.mysql.MarinerMySQLDataSource;
import io.github.sinri.mariner.mysql.dao.MarinerQueriedResult;
import io.github.sinri.mariner.mysql.exception.MarinerMySQLDataSourceException;
import io.github.sinri.mariner.mysql.exception.MarinerMySQLTransactionRollbackOccurred;

import java.util.UUID;

public class Test1 {
    public static void main(String[] args) {
        try {
            MarinerPropertiesFileReader propertiesFileReader = new MarinerPropertiesFileReader("config.properties");

            v2(propertiesFileReader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void v2(MarinerPropertiesFileReader propertiesFileReader) throws MarinerMySQLDataSourceException {
        MarinerMySQLDataSource dataSource = MarinerMySQLDataSource.buildFromConfigProperties(propertiesFileReader, "test1");

        try {
            dataSource.transaction(transaction -> {
                MarinerQueriedResult queriedResult1 = transaction.execute("insert into cas_login_session (username,cas_token,token,expire,create_time) values('tester','" + UUID.randomUUID() + "','" + UUID.randomUUID() + "'," + (System.currentTimeMillis() / 1000) + ",now())");
                System.out.println("inserted, afx=" + queriedResult1.getAffectedRows() + ", last inserted id=" + queriedResult1.getLastInsertedId());
                if (queriedResult1.getLastInsertedId() > 1) {
                    throw new RuntimeException("should to rollback");
                }
                System.out.println("should to commit");
            });
        } catch (MarinerMySQLTransactionRollbackOccurred e) {
            System.out.println(e.getMessage());
        }

        dataSource.connection(connection -> {
            MarinerQueriedResult queriedResult = connection.query("select * from cas_login_session order by cas_login_session_id desc limit 3");
            queriedResult.getRowList().forEach(queriedRow -> {
                System.out.println("queried row: " + queriedRow.toString());
            });

            MarinerQueriedResult queriedResult1 = connection.execute("insert into cas_login_session (username,cas_token,token,expire,create_time) values('tester','" + UUID.randomUUID() + "','" + UUID.randomUUID() + "'," + (System.currentTimeMillis() / 1000) + ",now())");
            System.out.println("inserted, afx=" + queriedResult1.getAffectedRows() + ", last inserted id=" + queriedResult1.getLastInsertedId());

            MarinerQueriedResult queriedResult2 = connection.execute("update cas_login_session set cas_token='" + UUID.randomUUID() + "' where cas_login_session_id=" + queriedResult1.getLastInsertedId());
            System.out.println("updated, afx=" + queriedResult2.getAffectedRows() + ", last inserted id=" + queriedResult2.getLastInsertedId());

            MarinerQueriedResult queriedResult3 = connection.execute("replace into cas_login_session (username,cas_token,token,expire,create_time) values('tester','" + UUID.randomUUID() + "','" + UUID.randomUUID() + "'," + (System.currentTimeMillis() / 1000) + ",now())");
            System.out.println("inserted, afx=" + queriedResult3.getAffectedRows() + ", last inserted id=" + queriedResult3.getLastInsertedId());

            MarinerQueriedResult queriedResult4 = connection.execute("delete from cas_login_session where cas_login_session_id=" + queriedResult3.getLastInsertedId());
            System.out.println("deleted, afx=" + queriedResult4.getAffectedRows() + ", last inserted id=" + queriedResult4.getLastInsertedId());
        });
    }

}
