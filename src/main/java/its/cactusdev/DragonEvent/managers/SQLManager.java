package its.cactusdev.DragonEvent.managers;

import its.cactusdev.DragonEvent.Main;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;

public class SQLManager {
    private Connection connection;
    public SQLManager() throws SQLException {
        connectToDatabase();
        createTable();
    }

    private void connectToDatabase() throws SQLException {
        File dataFolder = new File(Main.getInstance().getDataFolder(), "database.db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                Main.getInstance().getLogger().severe("File write error: database.db");
            }
        }
        String url = "jdbc:sqlite:" + Main.getInstance().getDataFolder().getAbsolutePath() + "/database.db";
        connection = DriverManager.getConnection(url);
        Main.getInstance().getLogger().info("Succesfully connected to SQLLite database");
    }

    private void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS settings ("+
                "key TEXT NOT NULL," +
                "value TEXT NOT NULL" +
                ");";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.execute();
            Main.getInstance().getLogger().info("Table created as success.");
        }
    }

    public String getData(String key) {
        String sql = "SELECT value FROM settings WHERE key = ? LIMIT 1;";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, key);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("value");
                }
            }
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("Error retrieving data for key: " + key);
            e.printStackTrace();
        }

        return null;
    }
    public void setData(String key, String value) {
        String checkSql = "SELECT COUNT(*) FROM settings WHERE key = ?;";

        String upsertSql = "INSERT OR REPLACE INTO settings (key, value) VALUES (?, ?);";

        try (PreparedStatement checkStatement = connection.prepareStatement(checkSql);
             PreparedStatement upsertStatement = connection.prepareStatement(upsertSql)) {

            checkStatement.setString(1, key);
            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next()) {
                    upsertStatement.setString(1, key);
                    upsertStatement.setString(2, value);
                    upsertStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("Error setting data for key: " + key);
            e.printStackTrace();
        }
    }
    public boolean deleteData(String key) {
        String deleteSql = "DELETE FROM settings WHERE key = ?;";

        try (PreparedStatement statement = connection.prepareStatement(deleteSql)) {
            statement.setString(1, key);
            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                Main.getInstance().getLogger().info("Data deleted successfully for key: " + key);
                return true;
            } else {
                Main.getInstance().getLogger().info("No data found for key: " + key);
                return false;
            }
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("Error deleting data for key: " + key);
            e.printStackTrace();
            return false;
        }
    }
}
