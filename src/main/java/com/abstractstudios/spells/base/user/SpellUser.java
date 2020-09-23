package com.abstractstudios.spells.base.user;

import com.abstractstudios.spells.AbstractSpellsPlugin;
import com.abstractstudios.spells.base.spell.Spell;
import com.abstractstudios.spells.utils.Logger;
import com.abstractstudios.spells.utils.ReflectionUtil;
import com.abstractstudios.spells.utils.database.exceptions.DatabaseException;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

public class SpellUser {

    private final UUID uuid;

    private Date firstJoined;
    private Date lastJoined;

    private int xp;

    private Spell currentSpell;
    private final HashSet<Spell> ownedSpells;

    /**
     * Create a new {@link SpellUser}
     * @param uuid - uuid.
     */
    public SpellUser(UUID uuid) {
        this.uuid = uuid;
        this.xp = 0;
        this.currentSpell = AbstractSpellsPlugin.getPlugin().getSpellConfig().getSpellByName("Expelliarmus");
        this.ownedSpells = new HashSet<>();
    }

    /**
     * @return Get UUID.
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * @return Get users current xp.
     */
    public int getXp() {
        return xp;
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
        
        if (ownedSpells.contains(spell)) 
            return;
        
        ownedSpells.add(spell);
    }

    /**
     * Remove a spell.
     * @param spell - spell.
     */
    public void removeOwnedSpell(Spell spell) {
        
        if (!ownedSpells.contains(spell)) 
            return;
        
        ownedSpells.remove(spell);
    }

    /**
     * Load profile data from database.
     */
    public void load() {

        Logger.display("Profile", "Loading profile for " + uuid.toString());

        try {

            AbstractSpellsPlugin.getPlugin().getDatabase().preparedStatement("SELECT * FROM `users` WHERE `uuid` = '" + uuid.toString() + "';", resultSet -> {

                // Data could not be found..
                if (resultSet == null) {

                    Timestamp date = new Timestamp(new Date().getTime());

                    // First time joining
                    AbstractSpellsPlugin.getPlugin().getDatabase().preparedStatement("INSERT INTO `users` (`uuid`, `firstJoined`, `lastOnline`, `xp`) VALUES ('" + uuid.toString() + "', '" + date + "', '" + date + "', 100);", callback -> {
                        Logger.display("Profile", "Saving profile on first join for " + uuid.toString());
                        load();
                    });

                    return;
                }


                this.firstJoined = resultSet.getDate("firstJoined");
                this.lastJoined = resultSet.getDate("lastOnline");

                this.xp = resultSet.getInt("xp");

                AbstractSpellsPlugin.getPlugin().getDatabase().preparedStatement("SELECT * FROM `spells` WHERE `uuid` = '" + uuid.toString() + "';", resultSetSpells -> {

                   if (resultSetSpells != null) {

                       HashSet<String> spellList = new HashSet<>();

                       // Somehow the first record won't appear in the while loop, so this fixes that.
                       spellList.add(resultSetSpells.getString("spellName"));

                       // Add rest of the spells to the list.
                       while (resultSetSpells.next()) spellList.add(resultSetSpells.getString("spellName"));

                       // Handle spells and add owned ones.
                       spellList.forEach(spell -> {

                           // Find the appropriate spells.
                           Spell foundSpell = AbstractSpellsPlugin.getPlugin().getSpellConfig().getSpellByName(spell);

                           // Ensure spell in the db exists.
                           if (foundSpell == null) return;

                           // Add owned spell.
                           addOwnedSpell(foundSpell);
                       });
                   }
                });

                Logger.display("Profile", "Loaded profile for " + uuid.toString());
            });
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save profile data to database.
     */
    public void save() {

        Logger.display("Profile", "Saving profile for " + uuid.toString());

        try {

            Timestamp date = new Timestamp(new Date().getTime());

            AbstractSpellsPlugin.getPlugin().getDatabase().preparedStatement("UPDATE `users` SET `uuid`='" + uuid.toString() + "', `lastOnline`='" + date + "' WHERE `uuid` = '" + uuid.toString() + "';", resultSet -> {

                if (getOwnedSpells().size() > 0) {

                    StringBuilder spellQuery = new StringBuilder("INSERT IGNORE INTO `spells` (`uuid`, `spellName`) VALUES ");

                    // Creates an insert for spells, won't replicate due to constraints.
                    getOwnedSpells().forEach(spell -> spellQuery.append("(").append("'").append(uuid.toString()).append("'").append(", ").append("'").append(spell).append("'").append("), "));

                    String finalQuery = spellQuery.toString().substring(0, spellQuery.length() - 2) + ";";

                    AbstractSpellsPlugin.getPlugin().getDatabase().preparedStatement(finalQuery, resultSetSpells -> {});
                }

                Logger.display("Profile", "Saved data for " + uuid.toString());
            });
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cast the current selected spell.
     */
    public void cast() {

        Player caster = Bukkit.getPlayer(uuid);

        // Ensure the caster is actually online.
        if (caster == null || !caster.isOnline()) return;

        // Location of the caster and direction vector.
        Location location = caster.getLocation();
        Vector direction = location.getDirection().normalize();

        // Create a snowball for the particles to follow.
        final Snowball snowball = caster.getWorld().spawn(caster.getEyeLocation().subtract(0, 0.1, 0), Snowball.class);

        try {

            // Send a packet to remove the snowball visually.
            Class<?> packetPlayOutEntityDestroy = ReflectionUtil.PackageType.MINECRAFT_SERVER.getClass("PacketPlayOutEntityDestroy");
            Constructor<?> packetPlayOutEntityDestroyConstructor = packetPlayOutEntityDestroy.getConstructor(int[].class);

            Object packetPlayOutEntityDestroyObj = packetPlayOutEntityDestroyConstructor.newInstance((Object) new int[] { snowball.getEntityId() });

            Bukkit.getOnlinePlayers().forEach(player -> ReflectionUtil.sendPacket(player, packetPlayOutEntityDestroyObj));
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }

        // Store spell name with custom colour to stop players renaming snowballs.
        snowball.setCustomNameVisible(false);
        snowball.setCustomName(ChatColor.RED + getCurrentSpell().name());

        // Set the snowball data.
        snowball.setShooter(caster);
        snowball.setBounce(false);
        snowball.setSilent(true);

        new BukkitRunnable() {

            public void run() {

                // Set snowball velocity so it does not fall
                snowball.setVelocity(direction);

                // Simple particle trail
                Particle.DustOptions dust = new Particle.DustOptions(currentSpell.spellColour(), 2);
                caster.getWorld().spawnParticle(Particle.REDSTONE, snowball.getLocation().getX(), snowball.getLocation().getY() + 0.1, snowball.getLocation().getZ(), 0, (float) direction.getX(), (float) direction.getY(), (float) direction.getZ(), 2.5 , dust);

                // Calculate distance between snowball and the casters original position at cast.
                double distance = snowball.getLocation().distance(caster.getLocation());

                // Check if the snowball is dead or the snowball proceeds past the defined distance from the user.
                if ((snowball.isOnGround() || snowball.isDead()) || distance > getCurrentSpell().maxDistance()) {
                    // Cancel runnable.
                    this.cancel();
                    // Remove snowball.
                    snowball.remove();
                }

            }
        }.runTaskTimer(AbstractSpellsPlugin.getPlugin(), 0, 1);
    }
}
