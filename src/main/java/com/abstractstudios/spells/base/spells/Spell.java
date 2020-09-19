package com.abstractstudios.spells.base.spells;

import org.bukkit.Color;

public class Spell {

    // Name of the spell
    private final String name;
    // Description of the spell
    private final String description;

    // Cost in xp to cast the spell.
    private final int xpCost;

    // Colour of the particles for the spell.
    private final Color spellColour;

    /**
     * Create a new spell.
     * @param name - name.
     * @param description - description.
     * @param xpCost - xp cost.
     * @param spellColour - spell colour.
     */
    public Spell(String name, String description, int xpCost, Color spellColour) {
        this.name = name;
        this.description = description;
        this.xpCost = xpCost;
        this.spellColour = spellColour;
    }

    /**
     * @return Get spell name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Get spell description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return Get spell xp cost.
     */
    public int getXpCost() {
        return xpCost;
    }

    /**
     * @return Get spell colour.
     */
    public Color getSpellColour() {
        return spellColour;
    }
}
