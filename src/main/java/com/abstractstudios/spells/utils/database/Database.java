package com.abstractstudios.spells.utils.database;

import com.abstractstudios.spells.AbstractSpellsPlugin;
import com.abstractstudios.spells.config.configs.DatabaseConfig;
import com.abstractstudios.spells.utils.Callback;
import com.abstractstudios.spells.utils.Logger;
import com.abstractstudios.spells.utils.database.exceptions.DatabaseException;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Database {

    // Connection pool
    private HikariDataSource hikariConnectionPool;

    /**
     * Connect to the database.
     */
    public void connect() {

        DatabaseConfig config = AbstractSpellsPlugin.getPlugin().getDatabaseConfig();

        hikariConnectionPool = new HikariDataSource();

        hikariConnectionPool.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");

        hikariConnectionPool.addDataSourceProperty("serverName", config.getHost());
        hikariConnectionPool.addDataSourceProperty("port", config.getPort());
        hikariConnectionPool.addDataSourceProperty("databaseName", config.getDatabase());
        hikariConnectionPool.addDataSourceProperty("user", config.getUsername());
        hikariConnectionPool.addDataSourceProperty("password", config.getPassword());

        hikariConnectionPool.setMaximumPoolSize(20);

        hikariConnectionPool.setConnectionTimeout(3000);
        hikariConnectionPool.setValidationTimeout(1000);

        Logger.display("Connecting to database.");
    }


    /**
     * Disconnect from the database.
     */
    public void disconnect() {

        if (hikariConnectionPool != null && hikariConnectionPool.isClosed()) {
            hikariConnectionPool.close();
            Logger.display("Disconnecting from database.");
        }
    }

    /**
     * Send a prepared statement to the database async and return the result.
     * @param query - query.
     * @param callback - callback.
     */
    public void preparedStatement(String query, Callback<ResultSet> callback) throws DatabaseException {

        if (hikariConnectionPool == null || hikariConnectionPool.isClosed()) {
            throw new DatabaseException("Not connected to the database.");
        }

        new BukkitRunnable() {

            @Override
            public void run() {

                try (Connection connection = hikariConnectionPool.getConnection()) {

                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        callback.call(statement.executeQuery());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(AbstractSpellsPlugin.getPlugin());
    }

    /**
     * @return Get {@link HikariDataSource} pool.
     */
    public HikariDataSource getHikariConnectionPool() {
        return hikariConnectionPool;
    }
}
