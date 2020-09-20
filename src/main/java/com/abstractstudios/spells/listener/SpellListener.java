package com.abstractstudios.spells.listener;

import com.abstractstudios.spells.AbstractSpellsPlugin;
import com.abstractstudios.spells.base.spell.Spell;
import com.abstractstudios.spells.base.user.SpellUser;
import com.abstractstudios.spells.manager.UserManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SpellListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        // Make sure the user exists and is loaded successfully.
        SpellUser user = UserManager.getUser(player.getUniqueId());

        // Ensure the user exists
        if (user == null) return;

        if ((event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) && event.getMaterial() == Material.STICK) {
            // Cast the appropriate spell
            user.cast();
        } else if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && event.getMaterial() == Material.STICK) {
            // Handle spell selection in hand
            player.sendMessage(ChatColor.RED + "Allowing spell selection..");
        }
    }

    // Handles when the snowball used for spells hits.
    @EventHandler
    public void onSpellInteract(ProjectileHitEvent event) {

        Entity entity = event.getEntity();

        // Ensure projectile is a snowball.
        if (entity instanceof Snowball) {

            Snowball snowball = (Snowball) entity;

            // Ensure the snowball shooter was a player and not another entity, and that the snowball is silent to stop
            // Normal players using hand held snowballs as spells.
            if (snowball.getShooter() instanceof Player && snowball.isSilent()) {

                Player caster = (Player) snowball.getShooter();

                // Take the spell name from the snowball and parse it to a spell object.
                String spellName = ChatColor.stripColor(snowball.getCustomName());
                Spell spell = AbstractSpellsPlugin.getPlugin().getSpellConfig().getSpellByName(spellName);

                if (spell != null) {

                    Entity hitEntity = event.getHitEntity();
                    Block hitBlock = event.getHitBlock();

                    // Handles onHit for spells when colliding with entities.
                    if (hitEntity != null) {
                        spell.onHit(caster, hitEntity, null);
                    }

                    // Handles onHit for spells when colliding with blocks.
                    if (hitBlock != null) {
                        spell.onHit(caster, null, hitBlock);
                    }
                }
            }
        }
    }

    // Handles the damage caused by a spell on entities.
    @EventHandler
    public void onSpellOriginalDamage(EntityDamageByEntityEvent event) {

        // Ensure the projectile/damager was a snowball.
        if (event.getDamager() instanceof Snowball) {

            Snowball snowball = (Snowball) event.getDamager();

            // Make sure the caster was a player and not another entity.
            if (snowball.getShooter() instanceof Player) {

                // Parse the spell name from the snowball.
                String spellName = ChatColor.stripColor(snowball.getCustomName());
                Spell spell = AbstractSpellsPlugin.getPlugin().getSpellConfig().getSpellByName(spellName);

                // Ensure the spell exists.
                if (spell != null) {

                    // If the spell should not damage, then handle that and cancel.
                    if (spell.spellDamage() <= 0) {
                        event.setCancelled(true);
                        return;
                    }

                    // Damage the entity appropriately.
                    event.setDamage(spell.spellDamage());
                }
            }
        }
    }
}
