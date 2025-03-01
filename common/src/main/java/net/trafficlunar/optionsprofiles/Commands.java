package net.trafficlunar.optionsprofiles;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.minecraft.client.Minecraft;
import net.trafficlunar.optionsprofiles.gui.ProfilesScreen;

public class Commands {
    public static void init() {
        CommandRegistrationEvent.EVENT.register(((dispatcher, buildContext, selection) -> dispatcher.register(
                net.minecraft.commands.Commands
                        .literal("optionsprofiles")
                        .executes(context -> {
                            Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new ProfilesScreen(null)));
                            return 1;
                        })
        )));
    }
}
