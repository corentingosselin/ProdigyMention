package fr.cocoraid.prodigymention.bungee;

import fr.cocoraid.prodigymention.general.LocalDatabase;
import fr.cocoraid.prodigymention.general.ProdigyMentionConfig;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;



public class CMDListener extends Command {


    private ProdigyMentionBungee bungee;
    private ProdigyMentionConfig config;
    private LocalDatabase database;

    public CMDListener(ProdigyMentionBungee bungee) {
        super("prodigymention", "prodigymention.usage", "pm");
        this.bungee = bungee;
        this.config = bungee.getInstance().getConfig();
        this.database = bungee.getInstance().getDatabase();
    }

    public void execute(CommandSender sender, String[] args) {

        if(sender instanceof ProxiedPlayer){
            ProxiedPlayer pp = (ProxiedPlayer) sender;

            if (args.length <= 0) {
                for (String s : bungee.getInstance().getHelpMessage()) {
                    pp.sendMessage(TextComponent.fromLegacyText(s));
                }
            } else {

                switch (args[0].toLowerCase()) {
                        case "toggle":
                            if(!pp.hasPermission("prodigymention.toggle")) {
                                pp.sendMessage(TextComponent.fromLegacyText(config.permissionMessage));
                                break;
                            }
                            if (database.getToggledoff().contains(pp.getUniqueId())) {
                                database.removeToggled(pp.getUniqueId());
                                pp.sendMessage(TextComponent.fromLegacyText(config.toggleOnMessage));

                            } else {
                                database.setToggledOff(pp.getUniqueId());
                                pp.sendMessage(TextComponent.fromLegacyText(config.toggleOffMessage));
                            }

                        break;

                    case "reload":
                        if(!pp.hasPermission("prodigymention.reload")) {
                            pp.sendMessage(TextComponent.fromLegacyText(config.permissionMessage));
                            break;
                        }
                            try {
                                config.load();
                                pp.sendMessage(TextComponent.fromLegacyText("&aConfiguration file reloaded ! :D"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        break;
                    default:
                        for (String s : bungee.getInstance().getHelpMessage()) {
                            pp.sendMessage(TextComponent.fromLegacyText(s));
                        }
                        break;
                }
            }


        }

    }


}
