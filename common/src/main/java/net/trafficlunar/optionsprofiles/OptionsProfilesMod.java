package net.trafficlunar.optionsprofiles;

import dev.architectury.event.events.client.ClientLifecycleEvent;
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
    private static OptionsProfilesModConfiguration CONFIG;

    public static void init() {
        // Create options-profiles directory
        Path profilesDirectory = Paths.get("options-profiles");

        if (Files.notExists(profilesDirectory)) {
            try {
                Files.createDirectory(profilesDirectory);
            } catch (IOException e) {
                LOGGER.error("An error occurred when creating the 'options-profiles' directory.", e);
            }
        }

        // Load mod config
        CONFIG = OptionsProfilesModConfiguration.load();

        // Init profiles (for loading on startup)
        ClientLifecycleEvent.CLIENT_STARTED.register(client -> {
            Profiles.init();
        });

        Keybinds.init();
        Commands.init();
    }

    public static OptionsProfilesModConfiguration config() {
        if (CONFIG == null) {
            throw new IllegalStateException("Config not yet available");
        } else {
            return CONFIG;
        }
    }
}
