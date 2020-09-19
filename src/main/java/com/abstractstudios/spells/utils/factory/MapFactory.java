package com.abstractstudios.spells.utils.factory;

import java.util.HashMap;
import java.util.Map;

public class MapFactory<K, V> {

    private final Map<K, V> map;

    public MapFactory() {
        this.map = new HashMap<>();
    }

    public MapFactory put(K key, V value) {
        this.map.put(key, value);
        return this;
    }

    public Map<K, V> build() {
        return this.map;
    }
}
