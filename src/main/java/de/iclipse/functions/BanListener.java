package de.iclipse.functions;

import de.iclipse.util.TimeUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;

import static de.iclipse.BLMain.dispatcher;

public class BanListener implements Listener {
    @EventHandler
    public void onLogin(PlayerLoginEvent event){
        UUID uuid = event.getPlayer().getUniqueId();
        if(BanUtils.isBanned(uuid)) {
            Ban activeBan = BanUtils.getActiveBan(uuid);

            StringBuilder kickMessage = new StringBuilder();
            kickMessage.append(dispatcher.get("login.blocked.title", false)).append("\n");
            kickMessage.append(dispatcher.get("login.blocked.reason", false, activeBan.getReason())).append("\n\n");
            if(activeBan.isPermanent()){
                kickMessage.append(dispatcher.get("login.blocked.releaseDate", false, "PERMANENT"));
            }else{
                kickMessage.append(dispatcher.get("login.blocked.releaseDate", false, TimeUtils.convertTimestamp(activeBan.getBanEnd())) + "\n");
                kickMessage.append(dispatcher.get("login.blocked.unbannedIn", false, TimeUtils.formatTime(activeBan.getBanEnd().getTime() - System.currentTimeMillis())));
            }
            event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
            event.setKickMessage(kickMessage.toString());
        }
    }
}
