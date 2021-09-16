package de.iclipse.database;

import de.iclipse.functions.Ban;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

import static de.iclipse.BLMain.mySQL;

public class BanTable {

    //mySQL.update("CREATE TABLE IF NOT EXISTS banTable (id MEDIUMINT NOT NULL AUTO_INCREMENT, uuid VARCHAR(40), reason VARCHAR(64), authorUuid VARCHAR(40), banStart TIMESTAMP, banDuration INTEGER(11), banEnd TIMESTAMP NULL, PRIMARY KEY(id))");

    public static void createBan(Ban ban){
        mySQL.update("INSERT INTO banTable (uuid, reason, authorUuid, banDuration, banEnd) VALUES ('" + ban.getUuid() + "', '" + ban.getReason() + "', '" + ban.getAuthorUuid() + "', " + ban.getBanDuration() + ", '" + ban.getBanEnd() + "')");
    }

    public static boolean isBanned(UUID uuid){
        ResultSet resultSet = mySQL.querry("SELECT id FROM banTable WHERE uuid = '" + uuid + "' AND (banEnd > CURRENT_TIMESTAMP OR banDuration = -1)");
        try {
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new NullPointerException();
    }

    public static Ban getBan(int id){
        ResultSet resultSet = mySQL.querry("SELECT * FROM banTable WHERE id = " + id);
        try {
            if (resultSet.next()) {
                return toBan(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new NullPointerException();
    }

    public static Ban getActiveBan(UUID uuid){
        ResultSet resultSet = mySQL.querry("SELECT * FROM banTable WHERE uuid = '" + uuid + "' AND (banEnd > CURRENT_TIMESTAMP OR banDuration > -1)");
        try {
            if(resultSet.next()){
                return toBan(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("SELECT * FROM banTable WHERE banEnd > CURRENT_TIMESTAMP OR banDuration > -1 AND uuid = '" + uuid + "'");
        throw new NullPointerException();
    }

    public static void deleteBan(int id){
        mySQL.update("DELETE * FROM banTable WHERE id = " + id);
    }

    public static void deleteActiveBan(UUID uuid){
        mySQL.update("DELETE * FROM banTable WHERE banEnd > CURRENT_TIMESTAMP AND uuid = '" + uuid + "'");
    }

    public static void unban(UUID uuid){
        mySQL.update("UPDATE banTable SET banEnd = CURRENT_TIMESTAMP WHERE banEnd > CURRENT_TIMESTAMP AND uuid = '" + uuid + "'");
    }

    public static void unbanPerma(UUID uuid){
        mySQL.update("UPDATE banTable SET banDuration = 0, banEnd = CURRENT_TIMESTAMP WHERE banEnd > CURRENT_TIMESTAMP AND uuid = '" + uuid + "'");
    }

    public static int getId(UUID uuid, Timestamp banEnd){
        ResultSet resultSet = mySQL.querry("SELECT id FROM banTable WHERE uuid = '" + uuid + "' AND banEnd = '" + banEnd + "'");
        try {
            if (resultSet.next()){
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new NullPointerException();
    }

    public static ArrayList<Ban> getBans(UUID uuid){
        ArrayList<Ban> arrayList = new ArrayList<>();
        ResultSet resultSet = mySQL.querry("SELECT * FROM banTable WHERE uuid = '" + uuid + "'");
        try {
            while (resultSet.next()){
                arrayList.add(toBan(resultSet));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return arrayList;
    }

    private static Ban toBan(ResultSet resultSet) throws SQLException {
        UUID uuid = UUID.fromString(resultSet.getString("uuid"));
        String reason = resultSet.getString("reason");
        UUID authorUuid = UUID.fromString(resultSet.getString("authorUuid"));
        Timestamp banStart = resultSet.getTimestamp("banStart");
        long banDuration = resultSet.getLong("banDuration");
        Timestamp banEnd = resultSet.getTimestamp("banEnd");
        return new Ban(uuid, reason, authorUuid, banStart, banDuration, banEnd);
    }
}
