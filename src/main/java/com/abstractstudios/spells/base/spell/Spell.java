package com.abstractstudios.spells.base.spell;

import org.bukkit.Color;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface Spell {

    // Name of the spell
    String name();

    // Description of the spell
    String description();

    // Cost in xp to cast the spell.
    int xpCost();

    // The damage a spell does to an entity.
    double spellDamage();

    // Colour of the particles for the spell.
    Color spellColour();

    /**
     * Handle what happens when you hit an entity, this usually runs for coded spells.
     */
    void onHit(Player caster, Entity hitEntity, Block hitBlock);
}
