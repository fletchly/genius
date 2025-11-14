package org.fletchly.genius.util;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigUtil {
    /**
     * Enum representing the types of configuration values that can be validated.
     * <p>
     * ConfigType is used to specify the expected data type of a configuration path. It allows the
     * validation mechanism to enforce type-specific constraints for configuration properties.
     * <p>
     * The available types are:
     * - STRING: Represents a non-empty string value.
     * - INTEGER: Represents an integer value.
     * - DECIMAL: Represents a decimal (floating-point) value.
     */
    public enum ConfigType {
        STRING,
        INTEGER,
        DECIMAL
    }

    /**
     * Validates a configuration file against a set of specified paths and their required types,
     * identifying any errors in the configuration.
     *
     * @param configuration the configuration object to validate
     * @param paths a map of configuration paths and their corresponding expected types
     * @return a list of error messages indicating invalid or missing paths in the configuration
     */
    public static List<String> validate(FileConfiguration configuration, Map<String, ConfigType> paths) {
        ArrayList<String> errors = new ArrayList<>();

        paths.forEach((path, type) -> {
            if (type == ConfigType.STRING) {
                if (configuration.getString(path) == null)
                    errors.add(String.format("Config path %s must be a nonempty string", path));
            }
            if (type == ConfigType.INTEGER) {
                if (configuration.getInt(path) == 0)
                    errors.add(String.format("Config path %s must be an integer", path));
            }
            if (type == ConfigType.DECIMAL) {
                if (configuration.getDouble(path) == 0)
                    errors.add(String.format("Config path %s must be a decimal", path));
            }
        });

        return errors;
    }
}
