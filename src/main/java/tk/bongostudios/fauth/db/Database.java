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
    public static final String table = "CREATE TABLE IF NOT EXISTS users (\n"
        + "	uuid text PRIMARY KEY,\n"
        + "	hash text NOT NULL,\n"
        + "	salt text NOT NULL\n"
        + ");";
    public static final String newUser = "INSERT INTO users VALUES(?,?,?)";

    public Database(String connection) {
        this.loc = connection;
        try {
            Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            stmt.execute(Database.table);
        } catch(SQLException e) {
            System.err.println(e.getErrorCode());
            e.printStackTrace();
        }
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(this.loc);
    }

    public void saveNewUser(UUID uuid, String hash, String salt) {
        try {
            PreparedStatement stmt = this.connect().prepareStatement(Database.newUser);
            stmt.setString(1, uuid.toString());
            stmt.setString(2, hash);
            stmt.setString(3, salt);
            stmt.executeUpdate();
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ResultSet getUserByUUID(UUID uuid) {
        String sql = "SELECT * FROM users WHERE uuid = ?";
        try {
            PreparedStatement stmt = this.connect().prepareStatement(sql);
            stmt.setString(1, uuid.toString());
            ResultSet results = stmt.executeQuery();
            return results;
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public boolean hasUserByUUID(UUID uuid) {
        String sql = "SELECT * FROM users WHERE uuid = ?";
        try {
            PreparedStatement stmt = this.connect().prepareStatement(sql);
            stmt.setString(1, uuid.toString());
            ResultSet results = stmt.executeQuery();
            return results.next();
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public String getLocation() {
        return this.loc;
    }
}