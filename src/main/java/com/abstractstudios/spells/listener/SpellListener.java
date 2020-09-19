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

        SpellUser user = UserManager.getUser(player.getUniqueId());

        if (user == null) return;

        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getMaterial() == Material.STICK) {
            user.cast();
        }
    }

    @EventHandler
    public void onSpellInteract(ProjectileHitEvent event) {

        Entity entity = event.getEntity();

        if (entity instanceof Snowball) {
            Snowball snowball = (Snowball) entity;

            if (snowball.getShooter() instanceof Player) {

                Player caster = (Player) snowball.getShooter();

                String spellName = ChatColor.stripColor(snowball.getCustomName());
                Spell spell = AbstractSpellsPlugin.getPlugin().getSpellConfig().getSpellByName(spellName);

                if (spell != null) {

                    Entity hitEntity = event.getHitEntity();
                    Block hitBlock = event.getHitBlock();

                    if (hitEntity != null) {
                        spell.onHit(caster, hitEntity, null);
                        caster.sendMessage(ChatColor.RED + "You shot " + spell.name() + " and hit " + hitEntity.getType().name().toLowerCase().replace("_", " ") + ".");
                    }

                    if (hitBlock != null) {
                        spell.onHit(caster, null, hitBlock);
                        caster.sendMessage(ChatColor.RED + "You shot " + spell.name() + " but missed and hit " + hitBlock.getType().name().toLowerCase().replace("_", " ") + "!");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSpellOriginalDamage(EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Snowball) {

            Snowball snowball = (Snowball) event.getDamager();

            if (snowball.getShooter() instanceof Player) {

                String spellName = ChatColor.stripColor(snowball.getCustomName());
                Spell spell = AbstractSpellsPlugin.getPlugin().getSpellConfig().getSpellByName(spellName);

                if (spell != null) {

                    if (spell.spellDamage() <= 0) {
                        event.setCancelled(true);
                        return;
                    }

                    event.setDamage(spell.spellDamage());
                }
            }
        }
    }
}
