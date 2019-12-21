package fr.cocoraid.prodigymention.bungee;

import com.google.common.collect.Lists;
import fr.cocoraid.prodigymention.NativeExecutor;
import fr.cocoraid.prodigymention.general.ProdigyMention;
import fr.cocoraid.prodigymention.general.ProdigyMentionConfig;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.BungeeTitle;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ProdigyMentionBungee extends Plugin implements NativeExecutor {

    public final ProdigyMention instance = new ProdigyMention(this);
    private ProdigyMentionBungee local_instance;

    @Override
    public void onEnable() {
        local_instance = this;
        instance.reload();
        //Execute native setup
        this.getProxy().getPluginManager().registerCommand(this, new CMDListener(this));
    }

    @Override
    public void onDisable() {
        instance.disable();
    }


    @Override
    public String mention(UUID sender, String message, boolean isCancelled) {
        ProdigyMentionConfig config = instance.getConfig();
        ProxiedPlayer s = BungeeCord.getInstance().getPlayer(sender);

        if(message.startsWith("/"))  return message;

        //everything concerning the sender....
        if(isCancelled && !s.hasPermission("prodigymention.chatmuted.bypass"))  return message;
        if(!s.hasPermission("prodigymention.use")) return message;


        List<ProxiedPlayer> targets = BungeeCord.getInstance().getPlayers().stream()
                .filter(cur -> !cur.equals(s))
                .filter(cur ->  StringUtils.containsIgnoreCase(message.toLowerCase(),config.needAtToMention ? "@" + cur.getName().toLowerCase() : cur.getName().toLowerCase())) //check name
                .filter(cur -> config.isBypassPermission && cur.hasPermission("prodigymention.bypass")) //check if receiver can bypass
                .filter(cur -> instance.getDatabase().getToggledoff().contains(cur.getUniqueId()) && !s.hasPermission("prodigymention.toggle.bypass")) //check if receiver is toggled, or if sender can bypass toggle
                .collect(Collectors.toList());


        String finalMessage = message;
        //transform message into colored message
        if(config.isColoredName) {
            //String[] words = finalMessage.split("\\W+");
            String[] normal =  Arrays.stream(Lists.transform(targets, CommandSender::getName).toArray()).toArray(String[]::new);
            String[] colored = Arrays.stream(normal).toArray(String[]::new);
            for (int index =0; index < colored.length; index++){
                colored[index] = colored[index].replace(colored[index], config.nameColor + colored[index] + ChatColor.RESET);
            }
            StringUtils.replaceEach(finalMessage, normal, colored);
        }

        //just send message if the player has spammed
        if(!s.hasPermission("prodigymention.antispam.bypass") && config.antiSpamMessageEnable && instance.getAntispam().contains(s.getUniqueId())) {
            s.sendMessage(new TextComponent(config.antispamMessage.replace("%time", String.valueOf(config.antiSpamTime))));
            return finalMessage;
        }


        if(config.antiSpamMessageEnable) {
            if(!instance.getAntispam().contains(s.getUniqueId()))
                instance.getAntispam().add(s.getUniqueId());

            ProxyServer.getInstance().getScheduler().schedule(local_instance, new Runnable() {
                public void run() {
                    instance.getAntispam().remove(s.getUniqueId());
                }
            }, config.antiSpamTime, TimeUnit.SECONDS);
        }




        //let's notify targets
        targets.forEach(t -> {

            //Use custom packet for bungeecord
            /*
            if(config.isParticleEnabled)
                t.getWorld().spawnParticle(Particle.NOTE,t.getLocation().add(0,1,0),10, 0.5,0.5,0.5,0.1F);

            if(config.isSoundEnabled) {

                Sound sound = Sound.valueOf(config.customSound);
                t.playSound(t.getLocation(), sound, 2, 1);
                s.playSound(s.getLocation(), sound, 2, 2);

                new BukkitRunnable() {
                    public void run() {
                        t.playSound(t.getLocation(), sound, 2, 0);
                        s.playSound(s.getLocation(), sound, 2, 0);

                    }
                }.runTaskLater(local_instance, 4L);
            }*/


            if(config.isTitleMessageEnabled) {
               new BungeeTitle()
                       .subTitle(new TextComponent(config.mentionMessage.replace("%player", s.getName())))
                       .stay(60)
                       .fadeIn(10)
                       .fadeOut(10)
                       .send(t);
            }

            if(config.isChatMessageEnabled)
                t.sendMessage((new TextComponent(config.mentionMessage.replace("%player",s.getName()))));
        });


        return finalMessage;
    }

    @Override
    public void unregisterAllListeners() {
        this.getProxy().getPluginManager().unregisterListeners(this);
    }

    @Override
    public void log(Level level, String message) {
        this.getLogger().log(level, message);
    }

    @Override
    public void disablePlugin() {
        //Bungeecord not supported...
    }

    @Override
    public String dataFolder() {
        return this.getDataFolder().getPath();
    }

    @Override
    public void registerListener() {
        this.getProxy().getPluginManager().registerListener(this, new ChatListener(this));
    }


    @Override
    public ProdigyMention getInstance() {
        return instance;
    }
}
