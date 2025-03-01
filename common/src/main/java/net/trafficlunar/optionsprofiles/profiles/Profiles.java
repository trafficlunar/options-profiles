package net.trafficlunar.optionsprofiles.profiles;

import dev.architectury.platform.Platform;
import net.trafficlunar.optionsprofiles.OptionsProfilesMod;
import net.trafficlunar.optionsprofiles.profiles.loaders.DistantHorizonsLoader;
import net.trafficlunar.optionsprofiles.profiles.loaders.IrisLoader;
import net.trafficlunar.optionsprofiles.profiles.loaders.SodiumExtraLoader;
import net.trafficlunar.optionsprofiles.profiles.loaders.SodiumLoader;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Profiles {
    public static final Path PROFILES_DIRECTORY = Paths.get("options-profiles/");
    public static final Path OPTIONS_FILE = Paths.get("options.txt");
    public static final Path OPTIFINE_OPTIONS_FILE = Paths.get("optionsof.txt");
    public static final Path SODIUM_OPTIONS_FILE = Paths.get("config/sodium-options.json");
    public static final Path SODIUM_EXTRA_OPTIONS_FILE = Paths.get("config/sodium-extra-options.json");
    public static final Path IRIS_OPTIONS_FILE = Paths.get("config/iris.properties");
    public static final Path DISTANT_HORIZONS_OPTIONS_FILE = Paths.get("config/DistantHorizons.toml");

    public static void init() {
        try (Stream<Path> paths = Files.list(PROFILES_DIRECTORY)) {
            paths.filter(Files::isDirectory)
                    .forEach(path -> {
                        String profileName = path.getFileName().toString();

                        // This gets the configuration but also creates the configuration file if it is not there
                        ProfileConfiguration profileConfiguration = ProfileConfiguration.get(profileName);
                        if (profileConfiguration.shouldLoadOnStartup()) {
                            loadProfile(profileName);
                            OptionsProfilesMod.LOGGER.warn("[Profile '{}']: Loaded on startup", profileName);
                        }
                    });
        } catch (IOException e) {
            OptionsProfilesMod.LOGGER.error("An error occurred when initializing", e);
        }
    }

    public static void createProfile() {
        String profileName = "Profile 1";
        Path profile = PROFILES_DIRECTORY.resolve(profileName);

        // Increases the number in 'Profile 1' if it already exists
        for (int i = 1; Files.exists(profile); i++) {
            profileName = "Profile " + i;
            profile = Paths.get(PROFILES_DIRECTORY.toString(), profileName);
        }

        try {
            Files.createDirectory(profile);

            if (Files.exists(profile)) {
                OptionsProfilesMod.LOGGER.info("[Profile '{}']: created", profileName);
                writeProfile(profileName, false);
            } else {
                OptionsProfilesMod.LOGGER.warn("[Profile '{}']: Profile already exists?", profileName);
            }
        } catch (IOException e) {
            OptionsProfilesMod.LOGGER.error("[Profile '{}']: An error occurred when creating a profile", profileName, e);
        }
    }

    private static void copyOptionFile(Path profile, Path options) {
        if (Files.exists(options)) {
            Path profileOptions = profile.resolve(options.getFileName());

            try {
                Files.copy(options, profileOptions);
                OptionsProfilesMod.LOGGER.info("[Profile '{}']: Copied file '{}'", profile.getFileName().toString(), options.getFileName().toString());
            } catch (IOException e) {
                OptionsProfilesMod.LOGGER.error("[Profile '{}']: Unable to copy '{}'", profile.getFileName().toString(), options.getFileName().toString(), e);
            }
        }
    }

    public static void writeProfile(String profileName, boolean overwriting) {
        Path profile = PROFILES_DIRECTORY.resolve(profileName);
        Path profileOptions = profile.resolve("options.txt");

        if (overwriting) {
            try (Stream<Path> files = Files.list(profile)) {
                files.filter(file -> !file.getFileName().toString().equals("configuration.json"))
                        .forEach(file -> {
                            try {
                                Files.delete(file);
                                OptionsProfilesMod.LOGGER.info("[Profile '{}']: Deleted file '{}'", profileName, file.getFileName().toString());
                            } catch (IOException e) {
                                OptionsProfilesMod.LOGGER.error("[Profile '{}']: An error occurred when trying to delete the file '{}'", profileName, file.getFileName().toString(), e);
                            }
                        });
            } catch (IOException e) {
                OptionsProfilesMod.LOGGER.error("[Profile '{}']: An error occurred when deleting old options files.", profileName, e);
            }
        }

        copyOptionFile(profile, OPTIONS_FILE);
        copyOptionFile(profile, OPTIFINE_OPTIONS_FILE);
        if (Platform.isModLoaded("sodium"))
            copyOptionFile(profile, SODIUM_OPTIONS_FILE);
        if (Platform.isModLoaded("sodium-extra"))
            copyOptionFile(profile, SODIUM_EXTRA_OPTIONS_FILE);
        if (Platform.isModLoaded("iris"))
            copyOptionFile(profile, IRIS_OPTIONS_FILE);
        if (Platform.isModLoaded("distanthorizons"))
            copyOptionFile(profile, DISTANT_HORIZONS_OPTIONS_FILE);

        if (!overwriting) {
            ProfileConfiguration profileConfiguration = ProfileConfiguration.get(profileName);

            // Add every option value to configuration
            try (Stream<String> lines = Files.lines(profileOptions)) {
                List<String> optionsToLoad = profileConfiguration.getOptionsToLoad();

                lines.forEach((line) -> {
                    String[] option = line.split(":");
                    optionsToLoad.add(option[0]);
                });

                profileConfiguration.save();
            } catch (IOException e) {
                OptionsProfilesMod.LOGGER.error("[Profile '{}']: An error occurred when adding options to the configuration file", profileName, e);
            }
        }
    }

    public static boolean isProfileLoaded(String profileName) {
        Path profile = PROFILES_DIRECTORY.resolve(profileName);
        ProfileConfiguration profileConfiguration = ProfileConfiguration.get(profileName);

        List<Path> optionFiles = new ArrayList<>();
        optionFiles.add(OPTIONS_FILE);

        // The next few lines check if the specified file exists. If so, it adds it to the optionFiles ArrayList.
        Optional.of(OPTIFINE_OPTIONS_FILE).filter(Files::exists).ifPresent(optionFiles::add);
        Optional.of(SODIUM_OPTIONS_FILE).filter(file -> Platform.isModLoaded("sodium")).ifPresent(optionFiles::add);
        Optional.of(SODIUM_EXTRA_OPTIONS_FILE).filter(file -> Platform.isModLoaded("sodium-extra")).ifPresent(optionFiles::add);
        Optional.of(IRIS_OPTIONS_FILE).filter(file -> Platform.isModLoaded("iris")).ifPresent(optionFiles::add);
        Optional.of(DISTANT_HORIZONS_OPTIONS_FILE).filter(file -> Platform.isModLoaded("distanthorizons")).ifPresent(optionFiles::add);

        // Check if the original option file and the profile option file have the same content
        try {
            for (Path optionFile : optionFiles) {
                Path profileOptions = profile.resolve(optionFile.getFileName());

                if (optionFile.getFileName().equals(OPTIONS_FILE)) {
                    try (Stream<String> lines = Files.lines(optionFile)) {
                        List<String> optionsToLoad = profileConfiguration.getOptionsToLoad();
                        AtomicBoolean loaded = new AtomicBoolean(false);

                        lines.forEach((line) -> {
                            String[] option = line.split(":");

                            if (optionsToLoad.contains(option[0])) {
                                try (Stream<String> profileLines = Files.lines(profileOptions)) {
                                    loaded.set(profileLines.anyMatch(profileLine -> profileLine.equals(line)));
                                } catch (IOException e) {
                                    OptionsProfilesMod.LOGGER.error("[Profile '{}']: An error occurred when checking each line in options.txt if the profiles is loaded", profileName, e);
                                }
                            }
                        });

                        return loaded.get();
                    } catch (IOException e) {
                        OptionsProfilesMod.LOGGER.error("[Profile '{}']: An error occurred when opening options.txt to check if the profile is loaded", profileName, e);
                    }
                } else {
                    if (!FileUtils.contentEquals(optionFile.toFile(), profileOptions.toFile())) {
                        return false;
                    }
                }
            }
        } catch (IOException e) {
            OptionsProfilesMod.LOGGER.error("[Profile '{}']: An error occurred when checking if the profile is loaded", profileName, e);
            return false;
        }

        return true;
    }

    private static void loadOptionFile(String profileName, Path options) {
        ProfileConfiguration profileConfiguration = ProfileConfiguration.get(profileName);

        Path profile = PROFILES_DIRECTORY.resolve(profileName);
        Path profileOptions = profile.resolve(options.getFileName());

        if (Files.exists(profileOptions)) {
            // This if statement is for loading specific options.
            if (options.getFileName().toString().equals("options.txt")) {       // If file is options.txt - only doing options.txt for now
                Map<String, String> optionsToWrite = new HashMap<>();

                // Read options.txt
                try (Stream<String> lines = Files.lines(options)) {
                    lines.forEach(line -> {
                        String[] option = line.split(":");            // Split key and value

                        if (option.length > 1) {
                            optionsToWrite.put(option[0], option[1]);           // Add them to the map
                        } else {
                            optionsToWrite.put(option[0], "");
                        }
                    });
                } catch (IOException e) {
                    OptionsProfilesMod.LOGGER.error("[Profile '{}']: An error occurred reading options.txt to load the profile", profileName, e);
                }

                // Read profile options.txt
                try (Stream<String> lines = Files.lines(profileOptions)) {
                    lines.forEach(line -> {
                        String[] option = line.split(":");            // Split key and value

                        if (option.length > 1) {
                            if (profileConfiguration.getOptionsToLoad().contains(option[0])) {
                                optionsToWrite.put(option[0], option[1]);       // Updates old value set by reading options.txt
                            }
                        }
                    });
                } catch (IOException e) {
                    OptionsProfilesMod.LOGGER.error("[Profile '{}']: An error occurred reading profile options.txt to load the profile", profileName, e);
                }

                // Write into options.txt
                try {
                    Files.write(options, () ->
                            optionsToWrite
                                    .entrySet()
                                    .stream()
                                    .<CharSequence>map(entry -> entry.getKey() + ":" + entry.getValue())
                                    .iterator()
                    );

                    OptionsProfilesMod.LOGGER.info("[Profile '{}']: '{}' loaded with specific options", profileName, options.getFileName());
                } catch (IOException e) {
                    OptionsProfilesMod.LOGGER.error("[Profile '{}']: An error occurred writing hashmap into options.txt", profileName, e);
                }

                return;         // Return the function, thus not running the next few lines
            }

            try {
                // Replaces the original option file with the profile option file
                Files.copy(profileOptions, options, StandardCopyOption.REPLACE_EXISTING);
                OptionsProfilesMod.LOGGER.info("[Profile '{}']: '{}' loaded by copying", profileName, options.getFileName());
            } catch (IOException e) {
                OptionsProfilesMod.LOGGER.error("[Profile '{}']: An error occurred when loading the profile", profileName, e);
            }
        }
    }

    private static void loadOptionFile(String profileName, Path options, Consumer<Path> loader) {
        Path profile = PROFILES_DIRECTORY.resolve(profileName);
        Path profileOptions = profile.resolve(options.getFileName());

        if (Files.exists(profileOptions)) {
            loader.accept(profileOptions);
            OptionsProfilesMod.LOGGER.info("[Profile '{}']: '{}' loaded using loader class", profileName, options.getFileName());
        }
    }

    public static void loadProfile(String profileName) {
        loadOptionFile(profileName, OPTIONS_FILE);
        loadOptionFile(profileName, OPTIFINE_OPTIONS_FILE);

        if (Platform.isModLoaded("sodium"))
            loadOptionFile(profileName, SODIUM_OPTIONS_FILE, SodiumLoader::load);
        if (Platform.isModLoaded("sodium-extra"))
            loadOptionFile(profileName, SODIUM_EXTRA_OPTIONS_FILE, SodiumExtraLoader::load);
        if (Platform.isModLoaded("iris"))
            loadOptionFile(profileName, IRIS_OPTIONS_FILE, IrisLoader::load);
        if (Platform.isModLoaded("distanthorizons")) {
            loadOptionFile(profileName, DISTANT_HORIZONS_OPTIONS_FILE);                                 // Overwrite / load original Disant Horizons option file
            loadOptionFile(profileName, DISTANT_HORIZONS_OPTIONS_FILE, DistantHorizonsLoader::load);    // Tell Distant Horizons mod to reload configuration
        }
    }

    public static void renameProfile(String profileName, String newProfileName) {
        Path profile = PROFILES_DIRECTORY.resolve(profileName);
        Path newProfile = PROFILES_DIRECTORY.resolve(newProfileName);

        if (Files.exists(newProfile)) {
            OptionsProfilesMod.LOGGER.warn("[Profile '{}']: A profile with that name already exists!", profileName);
            return;
        }

        try {
            Files.move(profile, newProfile);
            OptionsProfilesMod.LOGGER.info("[Profile '{}']: renamed. Old name: {}", newProfileName, profileName);
        } catch (IOException e) {
            OptionsProfilesMod.LOGGER.error("[Profile '{}']: An error occurred when renaming the profile", profileName, e);
        }
    }

    public static void deleteProfile(String profileName) {
        Path profile = PROFILES_DIRECTORY.resolve(profileName);

        try {
            FileUtils.deleteDirectory(profile.toFile());
            OptionsProfilesMod.LOGGER.info("[Profile '{}']: deleted", profileName);
        } catch (IOException e) {
            OptionsProfilesMod.LOGGER.error("[Profile '{}']: Profile was not deleted", profileName, e);
        }
    }
}
