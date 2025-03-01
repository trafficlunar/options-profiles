package net.trafficlunar.optionsprofiles.gui;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.trafficlunar.optionsprofiles.profiles.Profiles;

public class ProfilesScreen extends OptionsSubScreen {
    private final Screen lastScreen;
    public ProfilesList profilesList;

    public ProfilesScreen(Screen lastScreen) {
        super(lastScreen, null, Component.translatable("gui.optionsprofiles.profiles-menu"));
        this.lastScreen = lastScreen;
    }

    protected void addOptions() {}

    protected void addContents() {
        this.layout.setHeaderHeight(24);
        this.profilesList = this.layout.addToContents(new ProfilesList(this, this.minecraft));

        this.addRenderableWidget(Button.builder(
                        Component.translatable("gui.optionsprofiles.settings-button"),
                        (button) -> this.minecraft.setScreen(new SettingsScreen(this)))
                .width(75)
                .pos(1, 1)
                .build());
    }

    protected void addFooter() {
        LinearLayout linearLayout = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        linearLayout.addChild(
                Button.builder(
                                Component.translatable("gui.optionsprofiles.save-current-options"),
                                (button -> {
                                    Profiles.createProfile();
                                    this.profilesList.refreshEntries();
                                }))
                        .build()
        );
        linearLayout.addChild(
                Button.builder(
                                CommonComponents.GUI_DONE,
                                (button -> this.onClose()))
                        .build()
        );
    }

    public void onClose() {
        this.minecraft.setScreen(lastScreen);
    }

    protected void repositionElements() {
        this.layout.arrangeElements();
        this.profilesList.updateSize(this.width, this.layout);
    }
}