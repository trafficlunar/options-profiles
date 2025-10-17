package net.trafficlunar.optionsprofiles.profiles.loaders;

import com.seibel.distanthorizons.core.config.ConfigHandler;

import java.nio.file.Path;

public class DistantHorizonsLoader {
    public static void load(Path file) {
        ConfigHandler.INSTANCE.configFileHandler.loadFromFile();
    }
}
