package io.github.sinri.mariner.test;

import io.github.sinri.mariner.helper.MarinerPropertiesFileReader;
import io.github.sinri.mariner.mysql.MarinerMySQLDataSource;
import io.github.sinri.mariner.mysql.dao.MarinerTableRowClassGenerator;

public class AutoGenerateTableClassTest {
    public static void main(String[] args) {
        try {
            MarinerPropertiesFileReader propertiesFileReader = new MarinerPropertiesFileReader("config.properties");
            MarinerMySQLDataSource dataSource = MarinerMySQLDataSource.buildFromConfigProperties(propertiesFileReader, "test1");

            dataSource.connection(connection -> {
                new MarinerTableRowClassGenerator(connection)
                        .forSchema("lefan")
                        .generate("io.github.sinri.mariner.test.ag", propertiesFileReader.read("mariner.test.auto_generate_table_class.dir"));
            });
            System.out.println("DONE");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
