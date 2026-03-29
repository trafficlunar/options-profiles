package net.trafficlunar.optionsprofiles.fabric.platform;

import net.fabricmc.loader.api.FabricLoader;
import net.trafficlunar.optionsprofiles.platform.services.IPlatformHelper;

public class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
