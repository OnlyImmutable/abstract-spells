package com.abstractstudios.spells;

import com.abstractstudios.spells.base.wands.Wand;
import com.abstractstudios.spells.configuration.ConfigManager;
import com.abstractstudios.spells.configuration.configs.SpellConfig;
import com.abstractstudios.spells.configuration.configs.WandConfig;
import com.abstractstudios.spells.utils.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class AbstractSpellsPlugin extends JavaPlugin {

    public static final Gson GSON;

    static {
        GSON = new GsonBuilder().serializeNulls().create();
    }

    private SpellConfig spellConfig;
    private WandConfig wandConfig;

    @Override
    public void onEnable() {

        // Load appropriate configs.
        spellConfig = ConfigManager.loadConfigFile(SpellConfig.class, defaults -> new SpellConfig());
        wandConfig = ConfigManager.loadConfigFile(WandConfig.class, defaults -> new WandConfig(Arrays.asList(
                new Wand("Basic"),
                new Wand("Skilled"),
                new Wand("Professional")
        )));

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
