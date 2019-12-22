package fr.cocoraid.prodigymention.spigot;

import fr.cocoraid.prodigymention.general.LocalDatabase;
import fr.cocoraid.prodigymention.general.ProdigyMentionConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDListener implements CommandExecutor {

    private ProdigyMentionSpigot spigot;
    private ProdigyMentionConfig config;
    private LocalDatabase database;

    public CMDListener(ProdigyMentionSpigot spigot) {
        this.spigot = spigot;
        this.config = spigot.getInstance().getConfig();
        this.database = spigot.getInstance().getDatabase();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (command.getName().equalsIgnoreCase("prodigymention") || command.getName().equalsIgnoreCase("pm") ) {
                if (args.length == 0) {
                    if(p.hasPermission("prodigymention.help")) {
                        for (String s : spigot.getInstance().getHelpMessage()) {
                            p.sendMessage(s);
                        }
                    }else p.sendMessage(config.permissionMessage);

                } else if(args.length == 1) {
                    if(args[0].equalsIgnoreCase("reload")) {
                        if(p.hasPermission("prodigymention.reload")) {
                            config.load();
                            p.sendMessage("&aConfiguration file reloaded ! :D");
                        }else p.sendMessage(config.permissionMessage);

                    } else if(args[0].equalsIgnoreCase("toggle")) {
                        if(p.hasPermission("prodigymention.toggle")) {
                            if(database.getToggledoff().contains(p.getUniqueId())) {
                                database.removeToggled(p.getUniqueId());
                                p.sendMessage(config.toggleOnMessage);
                            } else {
                                database.setToggledOff(p.getUniqueId());
                                p.sendMessage(config.toggleOffMessage);
                            }
                        } else p.sendMessage(config.permissionMessage);

                    } else {
                        for (String s : spigot.getInstance().getHelpMessage())
                            p.sendMessage(s);
                    }

                } else {
                    for (String s : spigot.getInstance().getHelpMessage())
                        p.sendMessage(s);
                }
            }
        }
        return false;

    }

}
