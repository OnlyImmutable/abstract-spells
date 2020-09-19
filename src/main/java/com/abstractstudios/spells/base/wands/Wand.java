package com.abstractstudios.spells.base.wands;

import org.bukkit.Material;

public class Wand {

    private final String name;
    private final Material item;

    /**
     * Create a new wand.
     * @param name - wand name.
     */
    public Wand(String name, Material item) {
        this.name = name;
        this.item = item;
    }


    /**
     * @return Get a wand name.
     */
    public String name() {
        return this.name;
    }

    /**
     * @return Get a wand item.
     */
    public Material item() {
        return this.item;
    }
}
