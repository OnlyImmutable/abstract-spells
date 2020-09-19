package com.abstractstudios.spells.configuration.configs;

import com.abstractstudios.spells.base.wands.Wand;
import com.abstractstudios.spells.configuration.Config;

import java.util.*;

@Config(name = "wands")
public class WandConfig {

    /** List of all the available wands. */
    private final List<Wand> wands;

    /**
     * Create a list of wands for default values.
     * @param wands - wands.
     */
    public WandConfig(List<Wand> wands) {
        this.wands = wands;
    }

    /**
     * @return Get all the available wands.
     */
    public List<Wand> getWands() {
        return wands;
    }
}
