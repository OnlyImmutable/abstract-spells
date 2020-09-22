package com.abstractstudios.spells;

import com.abstractstudios.spells.base.spell.Spell;
import com.abstractstudios.spells.base.spell.spells.ExpelliarmusSpell;
import com.abstractstudios.spells.base.user.SpellUser;
import com.abstractstudios.spells.config.ConfigManager;
import com.abstractstudios.spells.config.configs.DatabaseConfig;
import com.abstractstudios.spells.config.configs.SpellConfig;
import com.abstractstudios.spells.listener.SpellListener;
import com.abstractstudios.spells.listener.UserListener;
import com.abstractstudios.spells.manager.UserManager;
import com.abstractstudios.spells.provider.InterfaceProvider;
import com.abstractstudios.spells.utils.Logger;
import com.abstractstudios.spells.utils.database.Database;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class AbstractSpellsPlugin extends JavaPlugin {

    /** Create a {@link Gson} instance */
    public static final Gson GSON;

    /** Create a {@link Random} instance */
    public static final Random RANDOM;

    /** Plugin instance. */
    public static AbstractSpellsPlugin plugin;

    static {

        // Initialise GsonBuilder whilst serializing nulls to empty.
        GSON = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(Spell.class, new InterfaceProvider<Spell>())
                .create();

        // Initialise the Random.
        RANDOM = new Random();
    }

    /** {@link SpellConfig} */
    private SpellConfig spellConfig;

    /** {@link DatabaseConfig} */
    private DatabaseConfig databaseConfig;

    /** {@link Database} */
    private Database database;

    @Override
    public void onEnable() {

        // Set plugin instance.
        plugin = this;

        // Load appropriate configs.
        spellConfig = ConfigManager.loadConfigFile(SpellConfig.class, defaults -> new SpellConfig(getCodedSpells()));

        databaseConfig = ConfigManager.loadConfigFile(DatabaseConfig.class, defaults -> new DatabaseConfig(
           "127.0.0.1",
           3306,
           "abstractspells",
           "root",
           ""
        ));

        // Connect to the database.
        database = new Database();
        database.connect();

        // Create default tables
        database.createDefaultTables();

        // Load appropriate listeners
        Bukkit.getPluginManager().registerEvents(new UserListener(), this);
        Bukkit.getPluginManager().registerEvents(new SpellListener(), this);

        // Load appropriate commands
        // TODO

        // Load existing users (/reload)
        Bukkit.getOnlinePlayers().forEach(player -> {

            UUID uuid = player.getUniqueId();
            SpellUser user = UserManager.getUser(uuid);

            if (user == null) return;

            // Get user triggers the load method to call defined in UserManager, causing user load.
            Logger.display("Loaded user " + user.getUuid().toString());
        });

        Logger.display("Abstract Spells has loaded.");
    }

    @Override
    public void onDisable() {

        // Unload existing users (/reload)
        Bukkit.getOnlinePlayers().forEach(player -> {

            UUID uuid = player.getUniqueId();
            SpellUser user = UserManager.getUser(uuid);

            if (user == null) return;

            // Get user triggers the load method to call defined in UserManager, causing user load.
            Logger.display("Unloaded user " + user.getUuid().toString());
        });

        // Disconnect from the database.
        if (database != null) {
            database.disconnect();
        }

        Logger.display("Abstract Spells has unloaded.");
    }

    /**
     * @return Get the plugin.
     */
    public static AbstractSpellsPlugin getPlugin() {
        return plugin;
    }

    /**
     * @return Get the {@link Database}
     */
    public Database getDatabase() {
        return database;
    }

    /**
     * @return Get the {@link DatabaseConfig}.
     */
    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

    /**
     * @return Get the config associated with spells.
     */
    public SpellConfig getSpellConfig() {
        return spellConfig;
    }

    /**
     * @return Get all the coded spells to pass to the config.
     */
    private List<Spell> getCodedSpells() {
        return Collections.singletonList(
                new ExpelliarmusSpell()
        );
    }
}
