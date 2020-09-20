package com.abstractstudios.spells.listener;

import com.abstractstudios.spells.base.user.SpellUser;
import com.abstractstudios.spells.manager.UserManager;
import com.abstractstudios.spells.utils.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class UserListener implements Listener {

    @EventHandler
    public void onPreConnect(AsyncPlayerPreLoginEvent event) {

        UUID uuid = event.getUniqueId();
        SpellUser user = UserManager.getUser(uuid);

        if (user == null) return;

        // Get user triggers the load method to call defined in UserManager, causing user load.
        Logger.display("Loaded user " + user.getUuid().toString());
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {

        UUID uuid = event.getPlayer().getUniqueId();
        SpellUser user = UserManager.getUser(uuid);

        // Make sure the user has their data loaded.
        if (user == null) return;

        // Save the user.
        UserManager.removeUser(uuid);
        Logger.display("Unloaded user " + user.getUuid().toString());
    }
}
