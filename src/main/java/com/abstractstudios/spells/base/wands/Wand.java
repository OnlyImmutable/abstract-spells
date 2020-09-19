package com.abstractstudios.spells.base.wands;

public class Wand {

    private final String wandName;

    /**
     * Create a new wand.
     * @param wandName - wand name.
     */
    public Wand(String wandName) {
        this.wandName = wandName;
    }

    /**
     * @return Get a wand name.
     */
    public String getWandName() {
        return wandName;
    }
}
