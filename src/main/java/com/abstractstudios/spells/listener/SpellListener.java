package com.abstractstudios.spells.listener;

import com.abstractstudios.spells.AbstractSpellsPlugin;
import com.abstractstudios.spells.base.spells.Spell;
import com.abstractstudios.spells.base.user.SpellUser;
import com.abstractstudios.spells.manager.UserManager;
import com.abstractstudios.spells.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
                        caster.sendMessage(ChatColor.RED + "You shot " + spell.getName() + " and hit a " + hitEntity.getType().name());
                    } else {
                        caster.sendMessage(ChatColor.RED + "You shot " + spell.getName() + " but missed!");
                    }
                }
            }
        }
    }
}
