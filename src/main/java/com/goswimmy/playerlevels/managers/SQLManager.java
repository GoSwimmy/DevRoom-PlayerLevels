package com.goswimmy.playerlevels.managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.List;
import java.util.function.Function;

public class SQLManager {

    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS `%prefix%data` (\n" +
                    "\t`uuid` VARCHAR(36) NOT NULL,\n" +
                    "\t`quest_id` INT(255) NOT NULL,\n" +
                    "\t`task_index` INT(255),\n" +
                    "\t`progress` INT(255),\n" +
                    "\t`status` INT(255),\n" +
                    "\tPRIMARY KEY (`uuid`, `task_index`)\n" +
                    ");";
    private static final String INSERT_PLAYER_JOIN =
            "INSERT INTO `%prefix%data` (uuid, quest_id, task_index, progress, status) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE uuid=uuid";
    private static final String SELECT_PLAYER_DATA =
            "SELECT * FROM `%prefix%data` WHERE uuid = ? ORDER BY task_index ASC";
    private static final String SELECT_PLAYER_QUEST =
            "SELECT uuid, quest_id FROM `%prefix%data` WHERE uuid = ? LIMIT 1";
    private static final String SELECT_TASK_PROGRESS =
            "SELECT progress FROM `%prefix%data` WHERE uuid = ? AND task_index = ?";
    private static final String UPDATE_PLAYER_PROGRESS =
            "UPDATE `%prefix%data` SET progress = progress+1 WHERE uuid = ? AND task_index = ?";
    private static final String UPDATE_PLAYER_GIVE_PROGRESS =
            "UPDATE `%prefix%data` SET progress = ? WHERE uuid = ? AND task_index = ?";
    private static final String DELETE_PLAYER =
            "DELETE FROM `%prefix%data` WHERE uuid = ?";
    private static final String SET_TASK_STATUS =
            "UPDATE `%prefix%data` SET status = ? WHERE uuid = ? AND task_index = ?";


    private static HikariDataSource hikari;

    private static Function<String, String> statementProcessor;

    public static void connect() {
        FileConfiguration mconfig = DataManager.getConfig();
        String address = mconfig.getString("storage.host", "localhost");
        String port = mconfig.getString("storage.port", "3306");
        String database = mconfig.getString("storage.database", "database");
        String uri = "jdbc:mysql://"+address+":"+port+"/"+database;

        HikariConfig config = new HikariConfig();
        config.setPoolName("quests");

        config.setUsername(mconfig.getString("storage.username", "root"));
        config.setPassword(mconfig.getString("storage.password", "password"));
        config.setJdbcUrl(uri);
        config.setMaximumPoolSize(mconfig.getInt("storage.max-pool-size", 8));
        config.setMinimumIdle(mconfig.getInt("storage.min-idle", 8));
        config.setMaxLifetime(mconfig.getInt("storage.max-lifetime", 1800000));
        config.setConnectionTimeout(mconfig.getInt("storage.connection-timeout", 5000));

        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("useServerPrepStmts", true);
        config.addDataSourceProperty("useLocalSessionState", true);
        config.addDataSourceProperty("rewriteBatchedStatements", true);
        config.addDataSourceProperty("cacheResultSetMetadata", true);
        config.addDataSourceProperty("cacheServerConfiguration", true);
        config.addDataSourceProperty("elideSetAutoCommits", true);
        config.addDataSourceProperty("maintainTimeStats", false);

        try {
            hikari = new HikariDataSource(config);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        statementProcessor = s -> s.replace("%prefix%", DataManager.getConfig().getString("storage.table-prefix", "playerlevels_"));
        try(Connection connection = hikari.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.addBatch(statementProcessor.apply(CREATE_TABLE));
                statement.executeBatch();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static void disconnect() {
        if(hikari != null) hikari.close();
    }

    public static HikariDataSource getHikari() {
        return hikari;
    }

    public static int getPlayerQuest(Player player) {
        try (Connection connection = hikari.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(statementProcessor.apply(SELECT_PLAYER_DATA));
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getInt("quest_id");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public static int getTaskProgress(Player player, int task_index) {
        try (Connection connection = hikari.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(statementProcessor.apply(SELECT_TASK_PROGRESS));
            statement.setString(1, player.getUniqueId().toString());
            statement.setInt(2, task_index);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt("progress");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public static ResultSet getPlayerData(Player player) {
        try (Connection connection = hikari.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(statementProcessor.apply(SELECT_PLAYER_DATA));
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();
            return resultSet;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static boolean doesPlayerExist(Player player) {
        try {
            try (Connection connection = hikari.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(statementProcessor.apply(SELECT_PLAYER_QUEST));
                statement.setString(1, player.getUniqueId().toString());
                ResultSet resultSet = statement.executeQuery();
                if(resultSet.next()) {
                    return true;
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public static void setTaskStatus(Player player, int index, int status) {
        try {
            try (Connection connection = hikari.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(statementProcessor.apply(SET_TASK_STATUS));
                statement.setInt(1, status);
                statement.setString(2, player.getUniqueId().toString());
                statement.setInt(3, index);
                statement.executeUpdate();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static int getTaskStatus(Player player, int index) {
        ResultSet resultSet = getPlayerData(player);
        try {
            while(resultSet.next()) {
                if(resultSet.getInt("task_index") == index) {
                    return resultSet.getInt("status");
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public static void setupPlayer(OfflinePlayer player, int quest) {
        FileConfiguration quests = DataManager.getQuests();
        try (Connection connection = hikari.getConnection()) {
            PreparedStatement deletestatement = connection.prepareStatement(statementProcessor.apply(DELETE_PLAYER));
            deletestatement.setString(1, player.getUniqueId().toString());
            deletestatement.executeUpdate();
            List<String> tasks = quests.getStringList("quests."+quest+".tasks");
            for(int index = 0; index < tasks.size(); index++) {
                PreparedStatement statement = connection.prepareStatement(statementProcessor.apply(INSERT_PLAYER_JOIN));
                statement.setString(1, player.getUniqueId().toString());
                statement.setInt(2, quest);
                statement.setInt(3, index);
                statement.setInt(4, 0);
                statement.setInt(5, 0);
                statement.executeUpdate();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static void addProgress(Player player, int task_index, int max) {
        int current = getTaskProgress(player, task_index);
        if(current+1 <= max) {
            try {
                try (Connection connection = hikari.getConnection()) {
                    PreparedStatement statement = connection.prepareStatement(statementProcessor.apply(UPDATE_PLAYER_PROGRESS));
                    statement.setString(1, player.getUniqueId().toString());
                    statement.setInt(2, task_index);
                    statement.executeUpdate();
                    PlayerLevelManager.checkComplete(player, false);
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    public static void addGiveProgress(Player player, int task_index, int max) {
        try {
            try (Connection connection = hikari.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(statementProcessor.apply(UPDATE_PLAYER_GIVE_PROGRESS));
                statement.setInt(1, max);
                statement.setString(2, player.getUniqueId().toString());
                statement.setInt(3, task_index);
                statement.executeUpdate();
                PlayerLevelManager.checkComplete(player, false);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
