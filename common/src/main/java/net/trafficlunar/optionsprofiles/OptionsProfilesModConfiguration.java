package net.trafficlunar.optionsprofiles.profiles;

import net.trafficlunar.optionsprofiles.OptionsProfilesMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class OptionsProfilesModConfiguration {
    private static Path configurationFile;

    private boolean showProfilesButton = true;

    public OptionsProfilesModConfiguration save() {
        OptionsProfilesModConfiguration configuration = new OptionsProfilesModConfiguration();

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        try (BufferedWriter writer = Files.newBufferedWriter(configurationFile)) {
            gson.toJson(this, writer);
            OptionsProfilesMod.LOGGER.info("Main configuration saved");
        } catch (IOException e) {
            OptionsProfilesMod.LOGGER.error("Unable to write main configuration.json!", e);
        }

        return configuration;
    }

    public static OptionsProfilesModConfiguration load() {
        OptionsProfilesModConfiguration configuration = new OptionsProfilesModConfiguration();
        configurationFile = Profiles.PROFILES_DIRECTORY.resolve("configuration.json");

        if (Files.notExists(configurationFile))
            configuration.save();

        try (BufferedReader reader = Files.newBufferedReader(configurationFile)) {
            Gson gson = new Gson();
            configuration = gson.fromJson(reader, OptionsProfilesModConfiguration.class);
        } catch (IOException e) {
            OptionsProfilesMod.LOGGER.error("An error occurred when reading the main configuration.json", e);
        }

        return configuration;
    }

    public boolean shouldShowProfilesButton() {
        return showProfilesButton;
    }

    public void setShowProfilesButton(boolean showProfilesButton) {
        this.showProfilesButton = showProfilesButton;
    }
}
