package de.iclipse.functions;

import de.iclipse.BLMain;
import de.iclipse.util.TimeUtils;
import de.iclipse.util.TypeUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

import static de.iclipse.BLMain.dispatcher;
import static de.iclipse.util.UUIDFetcher.getName;
import static de.iclipse.util.UUIDFetcher.getUUID;

public class BanCommands implements CommandExecutor {

    public BanCommands(BLMain blMain) {
        blMain.getCommand("ban").setExecutor(this);
        blMain.getCommand("permaban").setExecutor(this);
        blMain.getCommand("unban").setExecutor(this);
        blMain.getCommand("banhistory").setExecutor(this);
        blMain.getCommand("baninfo").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player player)) {
            dispatcher.send(commandSender, "command.noConsole");
            return true;
        }
        if (command.getName().equals("ban")) {
            ban(player, args);
        } else if (command.getName().equals("permaban")) {
            permaban(player, args);
        } else if (command.getName().equals("unban")) {
            unban(player, args);
        } else if (command.getName().equals("banhistory")) {
            banhistory(player, args);
        } else if (command.getName().equals("baninfo")) {
            baninfo(player, args);
        }
        return true;
    }

    /**
     * Description: Bans a player
     * Usage: /ban <Player> <Amount> <Unit> <Reason>
     */
    private void ban(Player player, String[] args) {
        //Check if it is the correct amount of arguments
        if (args.length != 4) {
            dispatcher.send(player, "ban.usage");
            return;
        }

        //Check if the player exists
        UUID uuid;
        try {
            uuid = getUUID(args[0]);
        } catch (NullPointerException exception) {
            dispatcher.send(player, "ban.invalidPlayer", args[0]);
            return;
        }

        //Check if a player is already banned
        if (BanUtils.isBanned(uuid)) {
            dispatcher.send(player, "ban.playerIsBanned", args[0]);
            return;
        }

        //Check if the argument is an integer
        if (!TypeUtils.isInt(args[1])) {
            dispatcher.send(player, "ban.usage");
            return;
        }
        int amount = Integer.parseInt(args[1]);

        //Check if amount > 0
        if(amount <= 0){
            dispatcher.send(player, "ban.noDuration");
            return;
        }

        //Check if the argument is a supported unit
        if (!isUnit(args[2])) {
            dispatcher.send(player, "ban.invalidUnit");
            return;
        }
        char unit = args[2].charAt(0);


        BanUtils.ban(uuid, args[3], player.getUniqueId(), amount, unit);
        dispatcher.send(player, "ban.successful");
    }

    /**
     * Description: Bans a player permanent
     * Usage: /permaban <Player> <Reason>
     */
    public void permaban(Player player, String[] args) {
        //Check if it is the correct amount of arguments
        if (args.length != 2) {
            dispatcher.send(player, "permaban.usage");
            return;
        }

        //Check if the player exists
        UUID uuid;
        try {
            uuid = getUUID(args[0]);
        } catch (NullPointerException exception) {
            dispatcher.send(player, "permaban.invalidPlayer", args[0]);
            return;
        }

        //Check if a player is already banned
        if (BanUtils.isBanned(uuid)) {
            dispatcher.send(player, "permaban.playerIsBanned", args[0]);
            return;
        }

        BanUtils.permaban(uuid, args[1], player.getUniqueId());
        dispatcher.send(player, "permaban.successful");
    }

    /**
     * Description: Unbans a player after a correct ban
     * Usage: /unban <Player>
     */
    private void unban(Player player, String[] args) {
        //Check if it is the correct amount of arguments
        if (args.length != 1) {
            dispatcher.send(player, "unban.usage");
            return;
        }

        //Check if the player exists
        UUID uuid;
        try {
            uuid = getUUID(args[0]);
        } catch (NullPointerException exception) {
            dispatcher.send(player, "unban.invalidPlayer", args[0]);
            return;
        }

        //Check if the player is currently banned
        if (!BanUtils.isBanned(uuid)) {
            dispatcher.send(player, "unban.playerIsNotBanned", args[0]);
            return;
        }

        BanUtils.unban(uuid);
        dispatcher.send(player, "unban.successful");
    }

    /**
     * Description: Displays a player's ban history
     * Usage: /banhistory <Player>
     */
    private void banhistory(Player player, String[] args) {
        //Check if it is the correct amount of arguments
        if (args.length != 1) {
            dispatcher.send(player, "banhistory.usage");
            return;
        }

        //Check if the player exists
        UUID uuid;
        try {
            uuid = getUUID(args[0]);
        } catch (NullPointerException exception) {
            dispatcher.send(player, "banhistory.invalidPlayer", args[0]);
            return;
        }

        ArrayList<Ban> banHistory = BanUtils.getBanhistory(uuid);
        if (banHistory.size() == 0) {
            dispatcher.send(player, "banhistory.noban");
        } else {
            dispatcher.send(player, "banhistory.title", true, getName(uuid));
            banHistory.forEach(ban -> {
                String message = dispatcher.get("banhistory.line", false, TimeUtils.convertTimestamp(ban.getBanStart()), ban.getReason());
                TextComponent component = new TextComponent(TextComponent.fromLegacyText(message));
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TextComponent.fromLegacyText(dispatcher.get("banhistory.hoverText", false))));
                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/baninfo " + BanUtils.getId(ban));
                component.setHoverEvent(hoverEvent);
                component.setClickEvent(clickEvent);
                player.spigot().sendMessage(component);
            });
        }
    }

    private void baninfo(Player player, String[] args) {
        if (args.length != 1) {
            dispatcher.send(player, "baninfo.usage");
            return;
        }

        //Check if the argument is an integer
        if (!TypeUtils.isInt(args[0])) {
            dispatcher.send(player, "baninfo.usage");
            return;
        }
        int id = Integer.parseInt(args[0]);
        Ban ban = BanUtils.getBan(id);

        StringBuilder baninfo = new StringBuilder();
        baninfo.append(dispatcher.get("baninfo.title", true, args[0])).append("\n");
        baninfo.append(dispatcher.get("baninfo.player", false, getName(ban.getUuid()))).append("\n");
        baninfo.append(dispatcher.get("baninfo.date", false, TimeUtils.convertTimestamp(ban.getBanStart()))).append("\n");
        baninfo.append(dispatcher.get("baninfo.reason", false, ban.getReason())).append("\n");
        baninfo.append(dispatcher.get("baninfo.duration", false, formatTime(ban.getBanDuration()))).append("\n");
        baninfo.append(dispatcher.get("baninfo.author", false, getName(ban.getAuthorUuid())));
        player.sendMessage(baninfo.toString());
    }

    private boolean isUnit(String unit) {
        if (unit.length() == 1) {
            return unit.equals("h") || unit.equals("d") || unit.equals("w") || unit.equals("m") || unit.equals("y");
        }
        return false;
    }

    public static String formatTime(long millis) {
        String s = "";
        int seconds = (int) (millis / 1000L);
        if (seconds == 0) return "PERMANENT";
        if ((seconds / (60.0 * 60.0)) > 1) {
            s += (int) (seconds / (60.0 * 60.0)) + " " + dispatcher.get("unit.hours", false) + " ";
        }
        if (seconds / (60.0 * 60.0 * 24) > 1) {
            s += (int) (seconds / (60.0 * 60.0 * 24)) + " " + dispatcher.get("unit.days", false) + " ";
        }
        if (seconds / (60.0 * 60.0 * 24 * 7) > 1) {
            s += (int) (seconds / (60.0 * 60.0 * 24 * 7)) + " " + dispatcher.get("unit.weeks", false) + " ";
        }
        if (seconds / (60.0 * 60.0 * 24 * 30) > 1) {
            s += (int) (seconds / (60.0 * 60.0 * 24 * 30)) + " " + dispatcher.get("unit.months", false) + " ";
        }
        if (seconds / (60.0 * 60.0 * 24 * 365) > 1) {
            s += (int) (seconds / (60.0 * 60.0 * 24 * 365)) + " " + dispatcher.get("unit.years", false) + " ";
        }
        return s;
    }

}
