package net.trafficlunar.optionsprofiles.neoforge.platform;

import net.neoforged.fml.ModList;
import net.trafficlunar.optionsprofiles.platform.services.IPlatformHelper;

public class NeoForgePlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}