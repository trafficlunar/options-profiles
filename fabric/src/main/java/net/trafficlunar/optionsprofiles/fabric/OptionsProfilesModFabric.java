package net.trafficlunar.optionsprofiles.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.trafficlunar.optionsprofiles.Commands;
import net.trafficlunar.optionsprofiles.Keybinds;
import net.trafficlunar.optionsprofiles.OptionsProfilesMod;

public class OptionsProfilesModFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OptionsProfilesMod.init();
        Keybinds.registerKeybinds(KeyMappingHelper::registerKeyMapping);
        ClientTickEvents.END_CLIENT_TICK.register(_ -> Keybinds.tick());
        CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> Commands.registerCommands(dispatcher::register));

        ClientLifecycleEvents.CLIENT_STARTED.register(_ -> OptionsProfilesMod.handleClientLoad());

        ClientPlayConnectionEvents.JOIN.register((listener, sender, client) -> OptionsProfilesMod.handleClientPlayerEvent(client.player, false));
        ClientPlayConnectionEvents.DISCONNECT.register((listener, client) -> OptionsProfilesMod.handleClientPlayerEvent(client.player, true));
    }
}
