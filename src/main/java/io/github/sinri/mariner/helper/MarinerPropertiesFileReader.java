package io.github.sinri.mariner.helper;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class MarinerPropertiesFileReader {
    Properties properties = new Properties();

    public MarinerPropertiesFileReader(String propertiesFileName) {
        try {
            // here, the file named as `propertiesFileName` should be put along with JAR
            properties.load(new FileReader(propertiesFileName, StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("Cannot find the file config.properties. Use the embedded one.");
            try {
                properties.load(this.getClass().getClassLoader().getResourceAsStream(propertiesFileName));
            } catch (IOException ex) {
                throw new RuntimeException("Cannot find the embedded file config.properties.", ex);
            }
        }
    }

    public String readWithChainedKeys(String... keys) {
        StringBuilder joinedKey = new StringBuilder();
        for (var key : keys) {
            if (joinedKey.length() > 0) {
                joinedKey.append(".");
            }
            joinedKey.append(key);
        }
        return this.properties.getProperty(joinedKey.toString());
    }

    public String read(String key) {
        return this.properties.getProperty(key);
    }

    public String read(String key, String defaultValue) {
        return this.properties.getProperty(key, defaultValue);
    }
}
