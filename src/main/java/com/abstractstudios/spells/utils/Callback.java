package com.abstractstudios.spells.utils;

public interface Callback<T> {

    /**
     * Callback a response.
     * @param obj - object,
     */
    void call(T obj) throws Exception;
}
