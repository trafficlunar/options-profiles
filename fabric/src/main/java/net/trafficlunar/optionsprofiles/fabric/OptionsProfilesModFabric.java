package net.trafficlunar.optionsprofiles.fabric;

import net.trafficlunar.optionsprofiles.OptionsProfilesMod;
import net.fabricmc.api.ModInitializer;

public class OptionsProfilesModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        OptionsProfilesMod.init();
    }
}
