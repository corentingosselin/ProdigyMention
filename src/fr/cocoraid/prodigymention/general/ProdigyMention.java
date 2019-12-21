package fr.cocoraid.prodigymention.general;

import fr.cocoraid.prodigymention.NativeExecutor;

import java.util.*;
import java.util.logging.Level;

public class ProdigyMention {

    private List<UUID> antispam = new ArrayList<>();
    private LocalDatabase database;
    private ProdigyMentionConfig config;

    public final NativeExecutor nativeExecutor;


    public ProdigyMention(NativeExecutor nativeExecutor) {
        this.nativeExecutor = nativeExecutor;
    }

    public void disable() {
        if(database != null)
            database.saveDatabase();

    }

    public void reload() {

        //Close the database
        this.disable();


        database = new LocalDatabase(nativeExecutor.dataFolder());
        config = new ProdigyMentionConfig(nativeExecutor.dataFolder());

        this.nativeExecutor.log(Level.INFO, "Loading configuration...");


        //Kill any existing threads or listeners in case of a re-load
        this.nativeExecutor.unregisterAllListeners();


        this.nativeExecutor.log(Level.INFO, "Database is ready to go!");

        this.nativeExecutor.registerListener();

    }





    public String[] getHelpMessage() {
        String[] help = new String[] {"§6§lProdigy§b§lMention",
                "§aReload the config file: §c/pm reload",
                "§aToggle mode: §c/pm toggle" };
        return help;
    }


    public LocalDatabase getDatabase() {
        return database;
    }

    public ProdigyMentionConfig getConfig() {
        return config;
    }

    public List<UUID> getAntispam() {
        return antispam;
    }

    public void printDebugInformation() {
        /*this.nativeExecutor.log(Level.INFO, "Session HEAD check: " + CheckMethods.directSessionServerStatus(new Gson()));
        this.nativeExecutor.log(Level.INFO, "Help page check: " + CheckMethods.mojangHelpPage());
        this.nativeExecutor.log(Level.INFO, "Xpaw check: " + CheckMethods.xpaw());
        this.nativeExecutor.log(Level.INFO, "Mojang offline mode: " + MOJANG_OFFLINE_MODE);
        this.nativeExecutor.log(Level.INFO, "Check status: " + CHECK_SESSION_STATUS);*/
    }

}
