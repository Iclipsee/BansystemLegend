package de.iclipse.functions;

import de.iclipse.database.BanTable;
import de.iclipse.util.TimeUtils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.UUID;

import static de.iclipse.BLMain.dispatcher;

public class BanUtils {

    public static void ban(UUID uuid, String reason, UUID authorUuid, int amount, char unit) {
        Ban ban = new Ban(uuid, reason, authorUuid, toMillis(amount, unit));
        BanTable.createBan(ban);
        kick(ban);
    }

    public static void permaban(UUID uuid, String reason, UUID authorUuid) {
        Ban ban = new Ban(uuid, reason, authorUuid);
        BanTable.createBan(ban);
        kick(ban);
    }

    private static void kick(Ban ban) {
        if (Bukkit.getPlayer(ban.getUuid()) != null) {
            String releaseDate;
            if (ban.isPermanent()) {
                releaseDate = "PERMANENT";
            } else {
                releaseDate = TimeUtils.convertTimestamp(ban.getBanEnd());
            }
            StringBuilder kickMessage = new StringBuilder();
            kickMessage.append(dispatcher.get("login.blocked.title", false)).append("\n");
            kickMessage.append(dispatcher.get("login.blocked.reason", false, ban.getReason())).append("\n\n");
            kickMessage.append(dispatcher.get("login.blocked.releaseDate", false, releaseDate));
            Bukkit.getPlayer(ban.getUuid()).kickPlayer(kickMessage.toString());
        }
    }

    public static void unban(UUID uuid) {
        if (!getActiveBan(uuid).isPermanent()) {
            BanTable.unban(uuid);
        } else {
            BanTable.unbanPerma(uuid);
        }
    }

    public static ArrayList<Ban> getBanhistory(UUID uuid) {
        return BanTable.getBans(uuid);
    }

    public static boolean isBanned(UUID uuid) {
        return BanTable.isBanned(uuid);
    }

    public static Ban getActiveBan(UUID uuid) {
        return BanTable.getActiveBan(uuid);
    }

    public static Ban getBan(int id) {
        return BanTable.getBan(id);
    }

    public static int getId(Ban ban) {
        return BanTable.getId(ban.getUuid(), ban.getBanEnd());
    }

    private static long toMillis(int amount, char unit) {
        return switch (unit) {
            case 'h' -> (long) amount * 60 * 60 * 1000;
            case 'd' -> toMillis(amount * 24, 'h');
            case 'w' -> toMillis(amount * 7, 'd');
            case 'm' -> toMillis(amount * 30, 'd');
            case 'y' -> toMillis(amount * 365, 'd');
            default -> -1;
        };
    }
}
