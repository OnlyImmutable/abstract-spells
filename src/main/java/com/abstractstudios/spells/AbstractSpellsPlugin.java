package com.abstractstudios.spells;

import com.abstractstudios.spells.base.spells.Spell;
import com.abstractstudios.spells.base.user.SpellUser;
import com.abstractstudios.spells.base.wands.Wand;
import com.abstractstudios.spells.config.ConfigManager;
import com.abstractstudios.spells.config.configs.SpellConfig;
import com.abstractstudios.spells.config.configs.WandConfig;
import com.abstractstudios.spells.listener.SpellListener;
import com.abstractstudios.spells.listener.UserListener;
import com.abstractstudios.spells.manager.UserManager;
import com.abstractstudios.spells.utils.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.UUID;

public class AbstractSpellsPlugin extends JavaPlugin {

    /** Create a {@link Gson} instance */
    public static final Gson GSON;

    public static AbstractSpellsPlugin plugin;

    static {
        // Initialise GsonBuilder whilst serializing nulls to empty.
        GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    }

    /** {@link SpellConfig} */
    private SpellConfig spellConfig;

    /** {@link WandConfig} */
    private WandConfig wandConfig;

    @Override
    public void onEnable() {

        plugin = this;

        // Load appropriate configs.
        spellConfig = ConfigManager.loadConfigFile(SpellConfig.class, defaults -> new SpellConfig(Arrays.asList(
            new Spell("Expelliarmus", "The disarming charm", 10, Color.BLUE)
        )));

        wandConfig = ConfigManager.loadConfigFile(WandConfig.class, defaults -> new WandConfig(Arrays.asList(
                new Wand("Basic", Material.STICK),
                new Wand("Skilled", Material.STICK),
                new Wand("Professional", Material.BLAZE_ROD)
        )));

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

        Logger.display("Abstract Spells has unloaded.");
    }

    /**
     * @return Get the plugin.
     */
    public static AbstractSpellsPlugin getPlugin() {
        return plugin;
    }

    /**
     * @return Get the config associated with spells.
     */
    public SpellConfig getSpellConfig() {
        return spellConfig;
    }

    /**
     * @return Get the config associated with wands.
     */
    public WandConfig getWandConfig() {
        return wandConfig;
    }
}
