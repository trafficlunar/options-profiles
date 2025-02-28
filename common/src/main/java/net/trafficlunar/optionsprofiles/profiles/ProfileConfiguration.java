package net.trafficlunar.optionsprofiles.profiles;

import net.trafficlunar.optionsprofiles.OptionsProfilesMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProfileConfiguration {
    private static Path configurationFile;
    private static String profileName;

    public static int configurationVersion = 1;     // Used to update configuration in later revisions
    private int version = configurationVersion;     // ^ same here - this variable is used to show it in the configuration.json file
    private List<String> optionsToLoad = new ArrayList<>();

    public ProfileConfiguration save() {
        ProfileConfiguration configuration = new ProfileConfiguration();

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        try (BufferedWriter writer = Files.newBufferedWriter(configurationFile)) {
            gson.toJson(this, writer);
            OptionsProfilesMod.LOGGER.info("[Profile '{}']: Profile configuration saved", profileName);
        } catch (IOException e) {
            OptionsProfilesMod.LOGGER.error("Unable to write configuration.json to profile!", e);
        }

        return configuration;
    }

    public static ProfileConfiguration get(String profile_name) {
        ProfileConfiguration configuration = new ProfileConfiguration();

        configurationFile = Profiles.PROFILES_DIRECTORY.resolve(profile_name).resolve("configuration.json");
        profileName = profile_name;

        if (Files.notExists(configurationFile))
            configuration.save();

        try (BufferedReader reader = Files.newBufferedReader(configurationFile)) {
            Gson gson = new Gson();
            configuration = gson.fromJson(reader, ProfileConfiguration.class);
        } catch (IOException e) {
            OptionsProfilesMod.LOGGER.error("[Profile '{}']: An error occurred when reading configuration.json", profileName, e);
        }

        return configuration;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<String> getOptionsToLoad() {
        return optionsToLoad;
    }

    public void setOptionsToLoad(List<String> optionsToLoad) {
        this.optionsToLoad = optionsToLoad;
    }
}
