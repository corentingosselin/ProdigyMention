package fr.cocoraid.prodigymention;

import fr.cocoraid.prodigymention.general.ProdigyMention;

import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Level;

public interface NativeExecutor {


	void unregisterAllListeners();

	void log(Level level, String message);

	void disablePlugin();

	void registerListener();

	String mention( UUID sender,String message, boolean isCancelled);

	String dataFolder();

	ProdigyMention getInstance();

}
