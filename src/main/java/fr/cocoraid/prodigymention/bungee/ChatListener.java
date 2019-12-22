package fr.cocoraid.prodigymention.bungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;

public class ChatListener implements Listener {

    private ProdigyMentionBungee bungee;

    public ChatListener(ProdigyMentionBungee bungee) {
        this.bungee = bungee;
    }

    public void onChat(ChatEvent e){
        ProxiedPlayer player = (ProxiedPlayer) e.getSender();
        e.setMessage(bungee.mention(player.getUniqueId(),e.getMessage(),e.isCancelled()));
    }
}
