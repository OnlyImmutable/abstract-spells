package com.abstractstudios.spells.utils;

import com.abstractstudios.spells.utils.database.exceptions.DatabaseException;

import java.sql.SQLException;

public interface Callback<T> {

    /**
     * Callback a response.
     * @param obj - object,
     */
    void call(T obj) throws SQLException, DatabaseException;
}
