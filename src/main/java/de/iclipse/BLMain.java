package de.iclipse;

import de.iclipse.database.MySQL;
import de.iclipse.functions.BanCommands;
import de.iclipse.functions.BanListener;
import de.iclipse.functions.Dispatcher;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BLMain extends JavaPlugin {

    public static BLMain instance;
    public static Dispatcher dispatcher;
    public static MySQL mySQL;
    public static String prefix = "§3BL§8 » §7";
    public static String symbol = "§7»";


    @Override
    public void onLoad() {
        if (instance != null) instance = this;
        dispatcher = new Dispatcher(this.getLogger());
        loadDatabase();
        dispatcher.log("plugin.loaded");
    }

    @Override
    public void onEnable() {
        dispatcher.log("plugin.enabled");
        registerListener();
        registerCommands();
    }

    @Override
    public void onDisable() {
        dispatcher.log("plugin.disabled");
    }

    public void loadDatabase(){
        mySQL = new MySQL(this);
    }

    public void registerListener(){
        Bukkit.getPluginManager().registerEvents(new BanListener(), this);
    }

    public void registerCommands(){
        new BanCommands(this);
    }

}
