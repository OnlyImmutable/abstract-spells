package com.abstractstudios.spells.manager;

import com.abstractstudios.spells.base.user.SpellUser;
import com.abstractstudios.spells.utils.Logger;
import com.google.common.cache.*;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class UserManager {

    // Users
    private static final LoadingCache<UUID, SpellUser> users;

    static {

        CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS);

        // Create a removal listener to handle saving data upon removal of user.
        builder.removalListener(notification -> {

            SpellUser user = (SpellUser) notification.getValue();
            user.save();

            Logger.display("Profiles", "Removing profile from cache for " + ((UUID) notification.getKey()).toString());
        });

        // Build the cache loader to handle loading user data.
        users = builder.build(new CacheLoader<UUID, SpellUser>() {

            @Override
            public SpellUser load(@Nonnull UUID uuid) {
                SpellUser user = new SpellUser(uuid);
                user.load();
                return user;
            }
        });
    }

    /**
     * Get a {@link SpellUser} from the cache.
     * @param uuid - uuid.
     * @return SpellUser
     */
    public static SpellUser getUser(@Nonnull UUID uuid) {

        try {
            return users.get(uuid);
        } catch (ExecutionException e) {
            Logger.displayError("Failed to load profile for " + uuid.toString());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Remove a user from the cache and save the appropriate data.
     * @param uuid - uuid.
     */
    public static void removeUser(@Nonnull UUID uuid) {

        SpellUser user = getUser(uuid);

        if (user == null) return;

        users.invalidate(uuid);
        Logger.display("Profiles", "Invalidated profile.");
    }
}
