package net.trafficlunar.optionsprofiles;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.trafficlunar.optionsprofiles.gui.ProfilesScreen;
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
        Path profilesDirectory = Paths.get("options-profiles");

        if (Files.notExists(profilesDirectory)) {
            try {
                Files.createDirectory(profilesDirectory);
            } catch (IOException e) {
                LOGGER.error("An error occurred when creating the 'options-profiles' directory.", e);
            }
        }

        CONFIG = OptionsProfilesModConfiguration.load();

        // Update / add configuration for existing profiles
        Profiles.updateProfiles();

        // Add /optionsprofiles command
        CommandRegistrationEvent.EVENT.register(((dispatcher, buildContext, selection) -> dispatcher.register(
                Commands
                        .literal("optionsprofiles")
                        .executes(context -> {
                            Minecraft.getInstance().setScreen(new ProfilesScreen(null));
                            return 1;
                        })
                    )));
    }

    public static OptionsProfilesModConfiguration config() {
        if (CONFIG == null) {
            throw new IllegalStateException("Config not yet available");
        } else {
            return CONFIG;
        }
    }
}
