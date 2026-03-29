package net.trafficlunar.optionsprofiles;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.trafficlunar.optionsprofiles.profiles.ProfileConfiguration;
import net.trafficlunar.optionsprofiles.profiles.Profiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Keybinds {
    private static final KeyMapping[] PROFILE_KEYMAPPINGS = new KeyMapping[3];

    public static void registerKeybinds(Consumer<KeyMapping> consumer) {
        KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(OptionsProfilesMod.MOD_ID, "keys"));

        for (int i = 0; i < PROFILE_KEYMAPPINGS.length; i++) {
            PROFILE_KEYMAPPINGS[i] = new KeyMapping(
                    "key.optionsprofiles.profile_" + (i + 1),
                    InputConstants.Type.KEYSYM,
                    -1,
                    category
            );
            consumer.accept(PROFILE_KEYMAPPINGS[i]);
        }
    }

    public static void tick() {
        for (int i = 0; i < PROFILE_KEYMAPPINGS.length; i++) {
            while (PROFILE_KEYMAPPINGS[i].consumeClick()) {
                loadProfilesByKeybind(i + 1);
            }
        }
    }

    private static void loadProfilesByKeybind(int keybindIndex) {
        try (Stream<Path> paths = Files.list(Profiles.PROFILES_DIRECTORY)) {
            paths.filter(Files::isDirectory)
                    .forEach(path -> {
                        String profileName = path.getFileName().toString();

                        ProfileConfiguration profileConfiguration = ProfileConfiguration.get(profileName);
                        if (profileConfiguration.getKeybindIndex() == keybindIndex) {
                            Profiles.loadProfile(profileName);
                            OptionsProfilesMod.LOGGER.warn("[Profile '{}']: Loaded through keybind", profileName);
                        }
                    });
        } catch (IOException e) {
            OptionsProfilesMod.LOGGER.error("An error occurred when loading profiles through keybinds", e);
        }
    }
}
