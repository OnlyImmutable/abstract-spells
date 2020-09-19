package com.abstractstudios.spells.base.user;

import com.abstractstudios.spells.AbstractSpellsPlugin;
import com.abstractstudios.spells.base.spells.Spell;
import com.abstractstudios.spells.utils.Logger;
import com.abstractstudios.spells.utils.ReflectionUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class SpellUser {

    private final UUID uuid;

    private Spell currentSpell;
    private final HashSet<Spell> ownedSpells;

    /**
     * Create a new {@link SpellUser}
     * @param uuid - uuid.
     */
    public SpellUser(UUID uuid) {
        this.uuid = uuid;
        this.currentSpell = new Spell("Expelliarmus", "The disarming charm", 10, Color.GREEN);
        this.ownedSpells = new HashSet<>();
    }

    /**
     * @return Get UUID.
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * @return Get current spell selected by the user.
     */
    public Spell getCurrentSpell() {
        return currentSpell;
    }

    /**
     * Set the current spell a user is trying to cast.
     */
    public void setCurrentSpell(Spell currentSpell) {
        this.currentSpell = currentSpell;
    }

    /**
     * @return Get all owned spells by the user.
     */
    public HashSet<Spell> getOwnedSpells() {
        return ownedSpells;
    }

    /**
     * Add a new spell.
     * @param spell - spell.
     */
    public void addOwnedSpell(Spell spell) {
        if (ownedSpells.contains(spell)) return;
        ownedSpells.add(spell);
    }

    /**
     * Remove a spell.
     * @param spell - spell.
     */
    public void removeOwnedSpell(Spell spell) {
        if (!ownedSpells.contains(spell)) return;
        ownedSpells.remove(spell);
    }

    /**
     * Load profile data from database.
     */
    public void load() {
        Logger.display("Profiles", "Loading profile for " + uuid.toString());
    }

    /**
     * Save profile data to database.
     */
    public void save() {
        Logger.display("Profiles", "Saving profile for " + uuid.toString());
    }

    /**
     * Cast the current selected spell.
     */
    public void cast() {

        Player caster = Bukkit.getPlayer(uuid);

        if (caster == null || !caster.isOnline()) return;

        Location location = caster.getLocation();
        Vector direction = location.getDirection().normalize();

        final Snowball snowball = caster.getWorld().spawn(caster.getEyeLocation().subtract(0, 0.1, 0), Snowball.class);

        try {
            Class<?> packetPlayOutEntityDestroy = ReflectionUtil.PackageType.MINECRAFT_SERVER.getClass("PacketPlayOutEntityDestroy");
            Constructor<?> packetPlayOutEntityDestroyConstructor = packetPlayOutEntityDestroy.getConstructor(int[].class);

            Object packetPlayOutEntityDestroyObj = packetPlayOutEntityDestroyConstructor.newInstance((Object) new int[] { snowball.getEntityId() });

            Bukkit.getOnlinePlayers().forEach(player -> ReflectionUtil.sendPacket(player, packetPlayOutEntityDestroyObj));
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }

        snowball.setCustomNameVisible(false);
        snowball.setCustomName(ChatColor.RED + getCurrentSpell().getName());

        snowball.setShooter(caster);
        snowball.setBounce(false);
        snowball.setSilent(true);

        new BukkitRunnable() {

            public void run() {

                snowball.setVelocity(direction);

                Particle.DustOptions dust = new Particle.DustOptions(currentSpell.getSpellColour(), 1);
                caster.getWorld().spawnParticle(Particle.REDSTONE, snowball.getLocation().getX(), snowball.getLocation().getY() + 0.1, snowball.getLocation().getZ(), 0, (float) direction.getX(), (float) direction.getY(), (float) direction.getZ(), 2.5 , dust);

                if (snowball.isOnGround() || snowball.isDead()) this.cancel();
            }
        }.runTaskTimer(AbstractSpellsPlugin.getPlugin(), 0, 1);
    }
}
