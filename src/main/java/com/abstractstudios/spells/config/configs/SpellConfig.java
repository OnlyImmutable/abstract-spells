package com.abstractstudios.spells.config.configs;

import com.abstractstudios.spells.base.spells.Spell;
import com.abstractstudios.spells.config.Config;

import java.util.List;

@Config(name = "spells")
public class SpellConfig {

    /** List of all the available wands. */
    private final List<Spell> spells;

    /**
     * Create a list of spells for default values.
     * @param spells - spells.
     */
    public SpellConfig(List<Spell> spells) {
        this.spells = spells;
    }

    /**
     * Get a spell by its name in the config.
     * @param name - name.
     * @return Spell
     */
    public Spell getSpellByName(String name) { return spells.stream().filter(spell -> spell.getName().equalsIgnoreCase(name)).findFirst().orElse(null); }

    /**
     * @return Get all the available wands.
     */
    public List<Spell> getSpells() {
        return spells;
    }
}
