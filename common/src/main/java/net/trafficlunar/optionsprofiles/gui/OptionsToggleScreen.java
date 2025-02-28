package net.trafficlunar.optionsprofiles.gui;

import net.trafficlunar.optionsprofiles.profiles.ProfileConfiguration;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class OptionsToggleScreen extends OptionsSubScreen {
    private final Component profileName;
    private OptionsToggleList optionsToggleList;
    public ProfileConfiguration profileConfiguration;

    protected OptionsToggleScreen(Screen lastScreen, Component profileName) {
        super(lastScreen, null, Component.literal(Component.translatable("gui.optionsprofiles.options-toggle").append(": ").getString() + profileName.getString()));
        this.profileName = profileName;
        this.profileConfiguration = ProfileConfiguration.get(profileName.getString());
    }

    protected void addOptions() {}

    protected void addContents() {
        this.layout.setHeaderHeight(24);
        this.optionsToggleList = this.layout.addToContents(new OptionsToggleList(this, this.minecraft, profileName.getString()));
    }

    protected void addFooter() {
        LinearLayout linearLayout = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));

        LinearLayout linearLayoutAllButtons = linearLayout.addChild(LinearLayout.horizontal().spacing(1));
        linearLayoutAllButtons.addChild(
                Button.builder(
                                Component.translatable("gui.optionsprofiles.all-off").withStyle(ChatFormatting.RED),
                                (button) -> this.optionsToggleList.refreshEntries(true, false))
                        .size(75, 20)
                        .build()
        );
        linearLayoutAllButtons.addChild(
                Button.builder(
                                Component.translatable("gui.optionsprofiles.all-on").withStyle(ChatFormatting.GREEN),
                                (button) -> this.optionsToggleList.refreshEntries(true, true))
                        .size(75, 20)
                        .build()
        );

        linearLayout.addChild(
                Button.builder(
                        CommonComponents.GUI_DONE,
                        (button -> this.onClose())
                ).build()
        );
    }

    public void removed() {
        profileConfiguration.save();
    }

    protected void repositionElements() {
        this.layout.arrangeElements();
        this.optionsToggleList.updateSize(this.width, this.layout);
    }
}
