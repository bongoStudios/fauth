package tk.bongostudios.fauth.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class Database {

    private final String loc;
    private Connection conn;
    public static final String version = "2";
    public static final String table = "CREATE TABLE IF NOT EXISTS users (\n"
        + "	uuid text PRIMARY KEY,\n"
        + "	hash text NOT NULL,\n"
        + " x real,\n"
        + " y real,\n"
        + " z real,\n"
        + " dimension text\n"
        + ");";
    public static final String v1ToV2 = "ALTER TABLE users \n"
        + "ADD pos text;";

    public Database(String connection) {
        this.loc = connection;
        try {
            this.conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet v1 = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='{users}';");
            boolean next = v1.next();
            if(next) {
                stmt.execute(v1ToV2);
            } else {
                stmt.execute(table);
            }
            stmt.execute("PRAGMA user_version = " + Database.version + ";");
            return;
        } catch(SQLException e) {
            System.err.println(e.getErrorCode());
            e.printStackTrace();
        }
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(this.loc);
    }

    public static final String newUser = "INSERT INTO users(uuid,hash) VALUES(?,?)";
    public void saveNewUser(UUID uuid, String hash) {
        try {
            PreparedStatement stmt = this.conn.prepareStatement(newUser);
            stmt.setString(1, uuid.toString());
            stmt.setString(2, hash);
            stmt.executeUpdate();
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static final String updatePassword = "UPDATE users SET hash = ? WHERE uuid = ?";
    public void updatePasswordByUUID(UUID uuid, String hash) {
        try {
            PreparedStatement stmt = this.conn.prepareStatement(updatePassword);
            stmt.setString(1, hash);
            stmt.setString(2, uuid.toString());
            stmt.executeUpdate();
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static final String updatePos = "UPDATE users SET x = ?, y = ?, z = ?, dimension = ? WHERE uuid = ?";
    public void updatePosByUUID(UUID uuid, double x, double y, double z, String dimension) {
        try {
            PreparedStatement stmt = this.conn.prepareStatement(updatePos);
            stmt.setDouble(1, x);
            stmt.setDouble(2, y);
            stmt.setDouble(3, z);
            stmt.setString(4, dimension);
            stmt.setString(5, uuid.toString());
            stmt.executeUpdate();
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static final String getUser = "SELECT * FROM users WHERE uuid = ?";
    public ResultSet getUserByUUID(UUID uuid) {
        try {
            PreparedStatement stmt = this.conn.prepareStatement(getUser);
            stmt.setString(1, uuid.toString());
            ResultSet results = stmt.executeQuery();
            return results;
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public static final String hasUser = "SELECT * FROM users WHERE uuid = ?";
    public boolean hasUserByUUID(UUID uuid) {
        try {
            PreparedStatement stmt = this.conn.prepareStatement(hasUser);
            stmt.setString(1, uuid.toString());
            ResultSet results = stmt.executeQuery();
            return results.next();
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public static final String delUser = "DELETE FROM users WHERE uuid = ?";
    public void delUserByUUID(UUID uuid) {
        try {
            PreparedStatement stmt = this.conn.prepareStatement(delUser);
            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    
    public void close() {
        try {
            this.conn.close();
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public String getLocation() {
        return this.loc;
    }
}