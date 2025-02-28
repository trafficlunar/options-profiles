package net.trafficlunar.optionsprofiles.profiles.loaders;

import com.seibel.distanthorizons.core.config.ConfigBase;

import java.nio.file.Path;

public class DistantHorizonsLoader {
    public static void load(Path file) {
        ConfigBase.INSTANCE.configFileINSTANCE.loadFromFile();
    }
}
