package tk.bongostudios.fauth;

import net.minecraft.entity.player.PlayerEntity;
import org.mindrot.jbcrypt.BCrypt;
import tk.bongostudios.fauth.utils.Descriptor;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Auth {
    public static final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
    private static final Set<PlayerEntity> loggedIn = new HashSet<PlayerEntity>();
    private static final Map<UUID, Descriptor> tasks = new HashMap<UUID, Descriptor>();

    public static void register(UUID uuid, String password) {
        if(FauthMod.db.hasUserByUUID(uuid)) return;
        String salt = BCrypt.gensalt();
        FauthMod.db.saveNewUser(uuid, BCrypt.hashpw(password, salt), salt);
    }

    public static boolean login(UUID uuid, String password) {
        if(!FauthMod.db.hasUserByUUID(uuid)) return false;
        ResultSet result = FauthMod.db.getUserByUUID(uuid);
        try {
            result.next();
            return BCrypt.checkpw(password, result.getString(2));
        } catch(SQLException e) {
            System.err.println(e);
            e.printStackTrace();
        }
        return false;
    }

    public static boolean changePassword(UUID uuid, String password) {
        if(!FauthMod.db.hasUserByUUID(uuid)) return false;
        String salt = BCrypt.gensalt();
        FauthMod.db.updateUserByUUID(uuid, BCrypt.hashpw(password, salt), salt);
        return true;
    }

    public static boolean hasAccount(UUID uuid) {
        return FauthMod.db.hasUserByUUID(uuid);
    }

    public static boolean hasLoggedIn(PlayerEntity player) {
        return loggedIn.contains(player);
    }

    public static void addLoggedIn(PlayerEntity player) {
        loggedIn.add(player);
    }

    public static void removeLoggedIn(PlayerEntity player) {
        loggedIn.remove(player);
    }

    public static boolean hasDescriptor(UUID uuid) {
        return tasks.containsKey(uuid);
    }
    
    public static Descriptor whichDescriptor(UUID uuid) {
        return tasks.get(uuid);
    }

    public static void addDescriptor(UUID uuid, Descriptor descriptor) {
        tasks.put(uuid, descriptor);
    }

    public static void removeDescriptor(UUID uuid) {
        tasks.remove(uuid);
    }
}