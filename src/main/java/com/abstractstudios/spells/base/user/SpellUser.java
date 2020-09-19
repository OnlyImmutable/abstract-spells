package com.abstractstudios.spells.base.user;

import com.abstractstudios.spells.base.spells.Spell;
import com.abstractstudios.spells.utils.Logger;

import java.util.HashSet;
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
        this.currentSpell = null;
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
}
