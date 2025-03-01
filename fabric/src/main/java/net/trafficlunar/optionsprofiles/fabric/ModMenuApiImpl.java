package net.trafficlunar.optionsprofiles.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.trafficlunar.optionsprofiles.gui.ProfilesScreen;

public class ModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ProfilesScreen::new;
    }
}
