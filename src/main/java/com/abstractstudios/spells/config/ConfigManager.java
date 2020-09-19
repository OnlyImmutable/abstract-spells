package com.abstractstudios.spells.config;

import com.abstractstudios.spells.AbstractSpellsPlugin;
import com.abstractstudios.spells.utils.Logger;
import org.apache.commons.lang3.Validate;

import java.io.*;
import java.util.function.Function;

/**
 * Handles Configuration files.
 */
public class ConfigManager {

    /**
     * Load the configuration file data.
     * @param clazz - clazz.
     * @param defaults - default data.
     * @return Config
     */
    public static <T> T loadConfigFile(Class<T> clazz, Function<Class<T>, T> defaults) {

        // Store the config data.
        T configObj = null;

        // Get the config file based on class and create string builder to build json.
        File file = getConfigFile(clazz);
        StringBuilder builder = new StringBuilder();

        // Create streams and buffers to read the appropriate data.
        try(FileInputStream inputStream = new FileInputStream(file); final InputStreamReader streamReader = new InputStreamReader(inputStream); final BufferedReader buffer = new BufferedReader(streamReader)) {
            String readLine;
            // Load in the appropriate data line by line into the StringBuilder
            while((readLine = buffer.readLine()) != null) builder.append(readLine);
            // Load the data loaded through the StringBuilder into json.
            configObj = AbstractSpellsPlugin.GSON.fromJson(builder.toString(), clazz);
        } catch (IOException e) {

            // If the file does not exist, pre load the default data.
            if (e instanceof FileNotFoundException) {
                configObj = defaults.apply(clazz);
                saveConfigFile(clazz, configObj);
                return configObj;
            }

            Logger.displayError("Failed to load config file - " + file.getName());
        }

        // Return the config data for the user.
        return configObj;
    }

    /**
     * Save a configuration file/
     * @param clazz - clazz.
     * @param obj - data.
     */
    public static <T> void saveConfigFile(Class<T> clazz, T obj) {

        // Get the config file based on class.
        File file = getConfigFile(clazz);

        // Create directories if they do not exist already.
        if (file.getParentFile() != null) file.getParentFile().mkdirs();

        // Create a FileWriter instance to write the json.
        try (FileWriter writer = new FileWriter(file)) {
            // Write json.
            AbstractSpellsPlugin.GSON.toJson(obj, writer);
            Logger.display("Saved config file - " + file.getName());
        } catch (IOException e) {
            Logger.displayError("Failed to save config file - " + file.getName());
            e.printStackTrace();
        }
    }

    /**
     * Get a configuration file.
     * @param clazz - clazz.
     * @return File
     */
    public static File getConfigFile(Class<?> clazz) {

        // Validate that the config annotation exists
        Config config = Validate.notNull(
                clazz.getAnnotation(Config.class),
                "Config annotation` cannot be found."
        );

        // Return the default configurations file.
        return new File("plugins/AbstractSpells/" + config.dir(), config.name() + ".json");
    }
}
