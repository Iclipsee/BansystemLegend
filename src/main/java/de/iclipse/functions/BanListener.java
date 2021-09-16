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
            String releaseDate;
            if(activeBan.isPermanent()){
                releaseDate = "PERMANENT";
            }else{
                releaseDate = TimeUtils.convertTimestamp(activeBan.getBanEnd());
            }
            StringBuilder kickMessage = new StringBuilder();
            kickMessage.append(dispatcher.get("login.blocked.title", false)).append("\n");
            kickMessage.append(dispatcher.get("login.blocked.reason", false, activeBan.getReason())).append("\n\n");
            kickMessage.append(dispatcher.get("login.blocked.releaseDate", false, releaseDate));
            event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
            event.setKickMessage(kickMessage.toString());
        }
    }
}
