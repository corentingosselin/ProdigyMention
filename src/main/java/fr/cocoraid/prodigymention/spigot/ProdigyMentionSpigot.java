package fr.cocoraid.prodigymention.spigot;

import com.google.common.collect.Lists;
import fr.cocoraid.prodigymention.NativeExecutor;
import fr.cocoraid.prodigymention.general.ProdigyMention;
import fr.cocoraid.prodigymention.general.ProdigyMentionConfig;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Created by cocoraid on 19/07/2017.
 */
public class ProdigyMentionSpigot extends JavaPlugin implements NativeExecutor {


    public final ProdigyMention instance = new ProdigyMention(this);
    private ProdigyMentionSpigot local_instance;


    @Override
    public void onEnable() {

        local_instance = this;


        instance.reload();

        ConsoleCommandSender c = Bukkit.getServer().getConsoleSender();

        c.sendMessage(" ");
        c.sendMessage(" ");
        c.sendMessage("§2" + "The Prodigy is the man who knows how to mention someone...");
        c.sendMessage(" ");
        c.sendMessage(" ");
        c.sendMessage("§2" + "Optional Depencies: ");
        c.sendMessage("§a" + "    - FactionChatAPI: " +  (getServer().getPluginManager().getPlugin("FactionChat")!=null ? "§a✔" : "§4✘"));
        /*if(getServer().getPluginManager().getPlugin("FactionChat") != null) {

        }*/

    }

    @Override
    public void onDisable() {
        instance.disable();
    }





    @Override
    public void unregisterAllListeners() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public void log(Level level, String message) {
        this.getLogger().log(level, message);
    }

    @Override
    public String dataFolder() {
        return getDataFolder().getPath();
    }

    @Override
    public void disablePlugin() {
        this.getServer().getPluginManager().disablePlugin(this);
    }

    @Override
    public void registerListener() {
        this.getServer().getPluginManager().registerEvents(new ChatListener(local_instance), local_instance);
        this.getCommand("prodigymention").setExecutor(new CMDListener(local_instance));

    }


    @Override
    public ProdigyMention getInstance() {
        return this.instance;
    }

    @Override
    public String mention(UUID mentioner, String message, boolean isCancelled) {
        ProdigyMentionConfig config = instance.getConfig();
        Player s = Bukkit.getPlayer(mentioner);

        if(message.startsWith("/"))  return message;

        //everything concerning the sender....
        if(isCancelled && !s.hasPermission("prodigymention.chatmuted.bypass"))  return message;

        List<Player> targets = new ArrayList<>();

        if(s.hasPermission("prodigymention.everyone") &&
                StringUtils.containsIgnoreCase(message.toLowerCase(),config.needAtToMention ? "@everyone"  : "everyone")) {
            targets.addAll(Bukkit.getOnlinePlayers().stream()
                    .filter(cur -> !cur.equals(s))
                    .filter(cur -> !config.isBypassPermission || (config.isBypassPermission && !cur.hasPermission("prodigymention.bypass"))) //check if receiver can bypass
                    .filter(cur -> !instance.getDatabase().getToggledoff().contains(cur.getUniqueId()) || s.hasPermission("prodigymention.toggle.bypass")) //check if receiver is toggled, or if sender can bypass toggle
                    .collect(Collectors.toList()));
        } else {

            if (s.hasPermission("prodigymention.use")) {
                targets.addAll(Bukkit.getOnlinePlayers().stream()
                        .filter(cur -> !cur.equals(s))
                        .filter(cur -> StringUtils.containsIgnoreCase(message.toLowerCase(), config.needAtToMention ? "@" + cur.getName().toLowerCase() : cur.getName().toLowerCase())) //check name
                        .filter(cur -> !config.isBypassPermission || (config.isBypassPermission && !cur.hasPermission("prodigymention.bypass"))) //check if receiver can bypass
                        .filter(cur -> !instance.getDatabase().getToggledoff().contains(cur.getUniqueId()) || s.hasPermission("prodigymention.toggle.bypass")) //check if receiver is toggled, or if sender can bypass toggle
                        .collect(Collectors.toList()));

            }
        }

        if(targets.isEmpty()) return message;


        String finalMessage = message;
        //transform message into colored message
        if(config.isColoredName) {
            //String[] words = finalMessage.split("\\W+");
            String[] normal =  Arrays.stream(Lists.transform(targets, HumanEntity::getName).toArray()).toArray(String[]::new);
            String[] colored = Arrays.stream(normal).toArray(String[]::new);
            for (int index =0; index < colored.length; index++){
                colored[index] = colored[index].replace(colored[index], config.nameColor + colored[index] + ChatColor.RESET);
            }
            finalMessage = StringUtils.replaceEach(finalMessage, normal, colored);
        }

        //just send message if the player has spammed
        if(!s.hasPermission("prodigymention.antispam.bypass") && config.antiSpamMessageEnable && instance.getAntispam().contains(s.getUniqueId())) {
            s.sendMessage(config.antispamMessage.replace("%time", String.valueOf(config.antiSpamTime)));
            return finalMessage;
        }


        if(config.antiSpamMessageEnable) {
            if(!instance.getAntispam().contains(s.getUniqueId()))
                instance.getAntispam().add(s.getUniqueId());
            new BukkitRunnable() {
                public void run() {
                    instance.getAntispam().remove(s.getUniqueId());
                }
            }.runTaskLater(local_instance, 20L * config.antiSpamTime);
        }




        //let's notify targets
        targets.forEach(t -> {

            if(config.isParticleEnabled)
                t.getWorld().spawnParticle(Particle.NOTE,t.getLocation().add(0,1,0),10, 0.5,0.5,0.5,0.1F);


            if(config.isTitleMessageEnabled)
                t.sendTitle("", config.mentionMessage.replace("%player",s.getName()),20,60,20);
            if(config.isChatMessageEnabled)
                t.sendMessage(config.mentionMessage.replace("%player",s.getName()));

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
            }

        });


        return finalMessage;

    }



}
