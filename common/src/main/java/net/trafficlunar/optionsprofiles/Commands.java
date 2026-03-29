package net.trafficlunar.optionsprofiles;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.trafficlunar.optionsprofiles.gui.ProfilesScreen;

import java.util.function.Consumer;

public class Commands {
    private static final LiteralArgumentBuilder<CommandSourceStack> PROFILE_COMMAND = net.minecraft.commands.Commands
            .literal("optionsprofiles")
            .executes(context -> {
                Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new ProfilesScreen(null)));
                return 1;
            });

    public static void registerCommands(Consumer<LiteralArgumentBuilder<CommandSourceStack>> consumer) {
        consumer.accept(PROFILE_COMMAND);
    }
}
