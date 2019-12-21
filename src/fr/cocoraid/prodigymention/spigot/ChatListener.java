package fr.cocoraid.prodigymention.spigot;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Created by cocoraid on 20/07/2017.
 */
public class ChatListener implements Listener {

    private ProdigyMentionSpigot spigot;
    public ChatListener(ProdigyMentionSpigot spigot) {
        this.spigot = spigot;
    }

    @EventHandler
    public void mention(AsyncPlayerChatEvent e) {
        e.setMessage(spigot.mention(e.getPlayer().getUniqueId(),e.getMessage(),e.isCancelled()));
    }
}
