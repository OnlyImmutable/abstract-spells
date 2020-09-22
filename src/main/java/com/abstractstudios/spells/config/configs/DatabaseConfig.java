package com.abstractstudios.spells.config.configs;

import com.abstractstudios.spells.config.Config;

@Config(name = "database")
public class DatabaseConfig {

    // Host name for the database.
    private final String host;
    // Port for the database.
    private final int port;

    // Database name.
    private final String database;

    // Username for the database.
    private final String username;
    // Password for the database.
    private final String password;

    public DatabaseConfig(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    /**
     * @return Database host name.
     */
    public String getHost() {
        return host;
    }

    /**
     * @return Database port.
     */
    public int getPort() {
        return port;
    }

    /**
     * @return Database name.
     */
    public String getDatabase() {
        return database;
    }

    /**
     * @return Database username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return Database password.
     */
    public String getPassword() {
        return password;
    }
}
