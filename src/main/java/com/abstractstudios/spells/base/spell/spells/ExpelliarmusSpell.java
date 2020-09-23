package com.abstractstudios.spells.base.spell.spells;

import com.abstractstudios.spells.AbstractSpellsPlugin;
import com.abstractstudios.spells.base.spell.Spell;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ExpelliarmusSpell implements Spell {

    /** Name of the spell */
    private final String name;

    // Description of the spell
    private final String description;

    // The max distance a spell can travel.
    private final int maxDistance;

    // Cost in xp to cast the spell.
    private final int xpCost;

    /** The damage a spell does to an entity */
    private final double spellDamage;

    // Colour of the particles for the spell.
    private final Color spellColour;

    public ExpelliarmusSpell() {
        this.name = "Expelliarmus";
        this.description = "The disarming charm";
        this.maxDistance = 80;
        this.xpCost = 10;
        this.spellDamage = 0;
        this.spellColour = Color.fromRGB(235, 147, 255);
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public int maxDistance() {
        return this.maxDistance;
    }

    @Override
    public int xpCost() {
        return this.xpCost;
    }

    @Override
    public double spellDamage() {
        return this.spellDamage;
    }

    @Override
    public Color spellColour() {
        return this.spellColour;
    }

    @Override
    public void onHit(Player caster, Entity hitEntity, Block hitBlock) {

        // Entity instance of Player
        if (hitEntity instanceof Player) {

            // Get the player.
            Player hit = (Player) hitEntity;

            // Get all their slots.
            int totalSlots = hit.getInventory().getSize();

            // Get the old item and the new item slot.
            int oldSlot = hit.getInventory().getHeldItemSlot();
            int newSlot = AbstractSpellsPlugin.RANDOM.nextInt(totalSlots);

            // Get the old item and new item from slot and current hand.
            ItemStack currentItem = hit.getInventory().getItemInMainHand();
            ItemStack newSlotItem = hit.getInventory().getItem(newSlot);

            // If its air just ignore.
            if (currentItem.getType() == Material.AIR) return;

            // Switch item.
            if (newSlotItem != null) {
                hit.getInventory().remove(Objects.requireNonNull(hit.getInventory().getItem(newSlot)));
                hit.getInventory().setItem(oldSlot, newSlotItem);
            }

            // Switch item.
            hit.getInventory().remove(hit.getInventory().getItemInMainHand());
            hit.getInventory().setItem(newSlot, currentItem);
        }
    }
}
