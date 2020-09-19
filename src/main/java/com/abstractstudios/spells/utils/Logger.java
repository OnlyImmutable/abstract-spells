package com.abstractstudios.spells.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Logger {

    /**
     * Log a message to console in any colour.
     * @param message - message.
     */
    public static void display(String message) {
        display("Abstract Spells", message);
    }

    /**
     * Log a message to console in error format.
     * @param message - message.
     */
    public static void displayError(String message) {
        display(ChatColor.DARK_RED + "Error", ChatColor.RED + message);
    }

    /**
     * Log a message to console in any colour with a specific prefix.
     * @param prefix - prefix.
     * @param message - message.
     */
    public static void display(String prefix, String message) { Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "(" + prefix + ") " + ChatColor.GRAY + message); }
}
