package de.iclipse.functions;

import de.iclipse.BLMain;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Dispatcher {

    private final ResourceBundle bundle;
    private final Logger logger;
    private final ChatColor textcolor;
    private final ChatColor warning;
    private final ChatColor highlight;

    public Dispatcher(Logger logger){
        bundle = ResourceBundle.getBundle("i18n.messages");
        this.logger = logger;
        textcolor = ChatColor.of(bundle.getString("color.text"));
        warning = ChatColor.of(bundle.getString("color.warning"));
        highlight = ChatColor.of(bundle.getString("color.highlight"));
    }

    public String get(String key, String... args){
        return get(key, true, args);
    }

    public String get(String key, boolean prefix, String... args){
        try {
            StringBuilder builder = new StringBuilder();
            if (prefix) builder.append(BLMain.prefix);

            builder.append(MessageFormat.format(bundle.getString(key), args)
                    .replaceAll("%t", String.valueOf(textcolor))
                    .replaceAll("%w", String.valueOf(warning))
                    .replaceAll("%h", String.valueOf(highlight))
                    .replaceAll("%n", "\n" + BLMain.symbol + " "));
            return builder.toString();
        } catch (Exception e) {
            return key;
        }
    }

    public void log(String key, String... args){
        logger.log(Level.INFO, get(key, args));
    }

    public void broadcast(String key, String... args){
        logger.log(Level.INFO, get(key, args));
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(get(key, args)));
    }

    public void send(CommandSender sender, String key, String... args){
        sender.sendMessage(get(key, args));
    }
    public void send(CommandSender sender, String key, boolean prefix, String... args){
        sender.sendMessage(get(key, prefix, args));
    }

}
