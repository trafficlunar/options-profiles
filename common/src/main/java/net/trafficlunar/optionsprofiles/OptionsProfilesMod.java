package net.trafficlunar.optionsprofiles;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import net.minecraft.world.entity.player.Player;
import net.trafficlunar.optionsprofiles.profiles.ProfileConfiguration;
import net.trafficlunar.optionsprofiles.profiles.Profiles;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

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
    }

    public static OptionsProfilesModConfiguration config() {
        if (CONFIG == null) {
            throw new IllegalStateException("Config not yet available");
        } else {
            return CONFIG;
        }
    }

    public static void handleClientLoad() {
        try (Stream<Path> paths = Files.list(Profiles.PROFILES_DIRECTORY)) {
            paths.filter(Files::isDirectory)
                    .forEach(path -> {
                        String profileName = path.getFileName().toString();

                        // This gets the configuration but also creates the configuration file if it is not there
                        ProfileConfiguration profileConfiguration = ProfileConfiguration.get(profileName);
                        if (profileConfiguration.shouldLoadOnStartup()) {
                            Profiles.loadProfile(profileName);
                            OptionsProfilesMod.LOGGER.info("[Profile '{}']: Loaded on startup", profileName);
                        }
                    });
        } catch (IOException e) {
            OptionsProfilesMod.LOGGER.error("An error occurred when initializing", e);
        }
    }

    public static void handleClientPlayerEvent(LocalPlayer player, boolean isOnLeave) {
        if (player == null) return;
        Connection connection = player.connection.getConnection();

        // Check if it's not an integrated server
        if (!connection.isMemoryConnection()) {
            // Get IP address
            SocketAddress address = connection.getRemoteAddress();
            if (address instanceof InetSocketAddress inetAddress) {
                String ip = inetAddress.getHostString().trim();

                // Go through all profiles and check what profiles to load
                try (Stream<Path> paths = Files.list(Profiles.PROFILES_DIRECTORY)) {
                    paths.filter(Files::isDirectory)
                            .forEach(path -> {
                                String profileName = path.getFileName().toString();
                                ProfileConfiguration profileConfiguration = ProfileConfiguration.get(profileName);
                                String[] servers = profileConfiguration.getServers().split(",");

                                if (servers.length == 0) return;

                                // Check if "leave" for the leave event or IP for the join event is in profile's servers
                                if (Arrays.asList(servers).contains(isOnLeave ? "leave" : ip)) {
                                    Profiles.loadProfile(profileName);
                                    OptionsProfilesMod.LOGGER.info("[Profile '{}']: Loaded on server ({})" + (isOnLeave ? "leave" : ""), profileName, ip);
                                }
                            });
                } catch (IOException e) {
                    OptionsProfilesMod.LOGGER.error("An error occurred when initializing", e);
                }
            }
        }
    }
}
