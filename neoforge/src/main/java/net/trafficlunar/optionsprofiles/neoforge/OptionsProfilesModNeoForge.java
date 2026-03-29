package net.trafficlunar.optionsprofiles.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.lifecycle.ClientStartedEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.trafficlunar.optionsprofiles.Commands;
import net.trafficlunar.optionsprofiles.Keybinds;
import net.trafficlunar.optionsprofiles.OptionsProfilesMod;
import net.neoforged.fml.common.Mod;

@Mod(value = OptionsProfilesMod.MOD_ID, dist = Dist.CLIENT)
public class OptionsProfilesModNeoForge {
    public OptionsProfilesModNeoForge(IEventBus bus) {
        OptionsProfilesMod.init();

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            NeoForge.EVENT_BUS.addListener(OptionsProfilesModNeoForge::onClientStarted);
            NeoForge.EVENT_BUS.addListener(OptionsProfilesModNeoForge::onClientPlayerJoin);
            NeoForge.EVENT_BUS.addListener(OptionsProfilesModNeoForge::onClientPlayerQuit);
            NeoForge.EVENT_BUS.addListener(OptionsProfilesModNeoForge::registerKeybinds);
            NeoForge.EVENT_BUS.addListener(OptionsProfilesModNeoForge::registerKeybindTick);
            NeoForge.EVENT_BUS.addListener(OptionsProfilesModNeoForge::registerCommands);
        }
    }

    private static void onClientStarted(ClientStartedEvent event) {
        OptionsProfilesMod.handleClientLoad();
    }

    private static void onClientPlayerJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        OptionsProfilesMod.handleClientPlayerEvent(event.getPlayer(), false);
    }

    private static void onClientPlayerQuit(ClientPlayerNetworkEvent.LoggingOut event) {
        OptionsProfilesMod.handleClientPlayerEvent(event.getPlayer(), true);
    }

    private static void registerKeybinds(RegisterKeyMappingsEvent event) {
        Keybinds.registerKeybinds(event::register);
    }

    private static void registerKeybindTick(ClientTickEvent.Post event) {
        Keybinds.tick();
    }

    private static void registerCommands(RegisterCommandsEvent event) {
        Commands.registerCommands(event.getDispatcher()::register);
    }
}
