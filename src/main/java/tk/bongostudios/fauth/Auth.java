package tk.bongostudios.fauth;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import org.mindrot.jbcrypt.BCrypt;
import tk.bongostudios.fauth.utils.Descriptor;
import tk.bongostudios.fauth.utils.PlayerPos;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Auth {
    public static ScheduledThreadPoolExecutor scheduler = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
    private static Set<PlayerEntity> loggedIn = new HashSet<PlayerEntity>();
    private static Map<UUID, Descriptor> tasks = new HashMap<UUID, Descriptor>();

    public static void register(UUID uuid, String password) {
        if(FauthMod.db.hasUserByUUID(uuid)) return;
        String salt = BCrypt.gensalt();
        FauthMod.db.saveNewUser(uuid, BCrypt.hashpw(password, salt));
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
        FauthMod.db.updatePasswordByUUID(uuid, BCrypt.hashpw(password, salt));
        return true;
    }
    
    public static boolean savePosition(UUID uuid, double x, double y, double z, String dim) {
        if(!FauthMod.db.hasUserByUUID(uuid)) return false;
        FauthMod.db.updatePosByUUID(uuid, x, y, z, dim);
        return true;
    }

    public static void delete(UUID uuid) {
        FauthMod.db.delUserByUUID(uuid);
    }

    public static boolean hasAccount(UUID uuid) {
        return FauthMod.db.hasUserByUUID(uuid);
    }

    public static boolean hasLoggedIn(PlayerEntity player) {
        return loggedIn.contains(player);
    }

    public static void addLoggedIn(ServerPlayerEntity player) {
        PlayerPos pos = getPosition(player.getUuid());
        if(pos != null) {
            player.teleport(FauthMod.server.getWorld(pos.dim), pos.x, pos.y, pos.z, player.yaw, player.pitch);
            Auth.savePosition(player.getUuid(), 0, 0, 0, null); // nullify previous pos
        }
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

    public static PlayerPos getPosition(UUID uuid) {
        if(!FauthMod.db.hasUserByUUID(uuid)) return null;
        ResultSet result = FauthMod.db.getUserByUUID(uuid);
        try {
            result.next();
            if(result.getString(6) == null) return null;
            return new PlayerPos(
                result.getDouble(3),
                result.getDouble(4),
                result.getDouble(5),
                result.getString(6)
            );
        } catch(SQLException e) {
            System.err.println(e);
            e.printStackTrace();
        }
        return null;
    }
    
    public static void clear() {
        Auth.scheduler.shutdownNow();
        loggedIn.clear();
        tasks.clear();
    }
}