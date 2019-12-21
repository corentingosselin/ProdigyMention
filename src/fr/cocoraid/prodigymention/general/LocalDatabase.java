package fr.cocoraid.prodigymention.general;



import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by cocoraid on 13/02/2017.
 */
public class LocalDatabase {

    private List<UUID> toggledoff = new ArrayList<>();
    private File file;
    private String folderPath;


    public LocalDatabase(String folderPath) {
        this.folderPath = folderPath;
        file = getFile();

        try {

            List<String> list = null;
            if(VersionChecker.isHigherOrEqualThan(VersionChecker.v1_14_R1))
                org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils.readLines(file, "utf-8");
            else list = org.apache.commons.io.FileUtils.readLines(file, "utf-8");
            list.forEach(s -> {
                toggledoff.add(UUID.fromString(s));
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveDatabase() {
        try{
            PrintWriter writer = new PrintWriter(file, "UTF-8");
            toggledoff.forEach(uuid -> {
                writer.println(uuid.toString());
            });
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void setToggledOff(UUID uuid) {
      toggledoff.add(uuid);
    }


    public void removeToggled(UUID uuid) {
        toggledoff.remove(uuid);
    }



    public File getFile() {
        File file = new File(folderPath, "database" + ".txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public List<UUID> getToggledoff() {
        return toggledoff;
    }
}
