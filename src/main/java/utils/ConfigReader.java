package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Reads configuration values from src/test/resources/config.properties.
 * Loaded once and reused (singleton pattern).
 */
public class ConfigReader {

    private static Properties properties;
    private static final String CONFIG_PATH = "src/test/resources/config.properties";

    private ConfigReader() {
        // utility class
    }

    public static void loadConfig() {
        if (properties == null) {
            properties = new Properties();
            try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
                properties.load(fis);
            } catch (IOException e) {
                throw new RuntimeException(
                        "Could not load config file: " + CONFIG_PATH, e);
            }
        }
    }

    public static String get(String key) {
        loadConfig();
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Missing config key: " + key);
        }
        return value.trim();
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }
}
