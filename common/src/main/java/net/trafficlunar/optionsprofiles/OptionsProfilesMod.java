package net.trafficlunar.optionsprofiles;

import net.trafficlunar.optionsprofiles.profiles.Profiles;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OptionsProfilesMod {
    public static final String MOD_ID = "optionsprofiles";
    public static final Logger LOGGER = LogManager.getLogger("Options Profiles");

    public static void init() {
        Path profilesDirectory = Paths.get("options-profiles");

        if (Files.notExists(profilesDirectory)) {
            try {
                Files.createDirectory(profilesDirectory);
            } catch (IOException e) {
                LOGGER.error("An error occurred when creating the 'options-profiles' directory.", e);
            }
        }

        // Update / add configuration for existing profiles
        Profiles.updateProfiles();
    }
}
