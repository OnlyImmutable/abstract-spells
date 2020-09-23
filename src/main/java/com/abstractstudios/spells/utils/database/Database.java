package com.abstractstudios.spells.utils.database;

import com.abstractstudios.spells.AbstractSpellsPlugin;
import com.abstractstudios.spells.config.impl.DatabaseConfig;
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

        hikariConnectionPool.setMaximumPoolSize(25);

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
     * @param data - data.
     */
    public void preparedStatement(String query, Callback<ResultSet> data) throws DatabaseException {

        if (hikariConnectionPool == null || hikariConnectionPool.isClosed()) {
            throw new DatabaseException("Not connected to the database.");
        }

        new BukkitRunnable() {

            @Override
            public void run() {

                try (Connection connection = hikariConnectionPool.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {

                    boolean altering = (query.toUpperCase().contains("CREATE TABLE") || query.toUpperCase().contains("DROP TABLE") || query.toUpperCase().contains("INSERT INTO") || query.toUpperCase().contains("INSERT IGNORE INTO") || query.toUpperCase().contains("UPDATE") || query.toUpperCase().contains("ALTER TABLE"));

                    if (altering) {
                        statement.execute();
                        data.call(null);
                        return;
                    }

                    // Data
                    ResultSet set = statement.executeQuery();

                    if (!set.next()) {
                        data.call(null);
                        return;
                    }

                    data.call(set);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(AbstractSpellsPlugin.getPlugin());
    }

    /**
     * Create the default tables associated with the plugin.
     */
    public void createDefaultTables() {

        try {

            // Tables
            preparedStatement("CREATE TABLE IF NOT EXISTS `users` (uuid VARCHAR(37) PRIMARY KEY, firstJoined DATETIME, lastOnline DATETIME, xp BIGINT, currentSpell VARCHAR)", (result) -> {});
            preparedStatement("CREATE TABLE IF NOT EXISTS `spells` (uuid VARCHAR(37), spellName VARCHAR(50), FOREIGN KEY (uuid) REFERENCES users(uuid), INDEX OWNED_SPELLS(`uuid`, `spellName`));", (result) -> {});

            Logger.display("Attempted to create appropriate tables..");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return Get {@link HikariDataSource} pool.
     */
    public HikariDataSource getHikariConnectionPool() {
        return hikariConnectionPool;
    }
}
