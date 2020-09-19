package com.abstractstudios.spells;

import com.abstractstudios.spells.base.wands.Wand;
import com.abstractstudios.spells.config.ConfigManager;
import com.abstractstudios.spells.config.configs.SpellConfig;
import com.abstractstudios.spells.config.configs.WandConfig;
import com.abstractstudios.spells.listener.UserListener;
import com.abstractstudios.spells.utils.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class AbstractSpellsPlugin extends JavaPlugin {

    /** Create a {@link Gson} instance */
    public static final Gson GSON;

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

        // Load appropriate configs.
        spellConfig = ConfigManager.loadConfigFile(SpellConfig.class, defaults -> new SpellConfig());
        wandConfig = ConfigManager.loadConfigFile(WandConfig.class, defaults -> new WandConfig(Arrays.asList(
                new Wand("Basic", Material.STICK),
                new Wand("Skilled", Material.STICK),
                new Wand("Professional", Material.BLAZE_ROD)
        )));

        Bukkit.getPluginManager().registerEvents(new UserListener(), this);

        Logger.display("Abstract Spells has loaded.");
    }

    @Override
    public void onDisable() {
        Logger.display("Abstract Spells has unloaded.");
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
