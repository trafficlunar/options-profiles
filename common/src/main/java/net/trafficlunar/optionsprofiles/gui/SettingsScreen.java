package net.trafficlunar.optionsprofiles.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.trafficlunar.optionsprofiles.OptionsProfilesMod;

public class SettingsScreen extends Screen {
    private final Screen lastScreen;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 24, 33);

    public SettingsScreen(Screen lastScreen) {
        super(Component.translatable("gui.optionsprofiles.settings-menu"));
        this.lastScreen = lastScreen;
    }

    protected void init() {
        LinearLayout linearLayoutHeader = this.layout.addToHeader(LinearLayout.vertical());
        linearLayoutHeader.addChild(new StringWidget(this.title, this.font), LayoutSettings::alignHorizontallyCenter);

        CycleButton<Boolean> showProfilesButtonButton = CycleButton.onOffBuilder(OptionsProfilesMod.config().shouldShowProfilesButton())
                .create(0, 0, 150, 20, Component.translatable("gui.optionsprofiles.show-profiles-button"), (button, boolean_) -> {
            OptionsProfilesMod.config().setShowProfilesButton(boolean_);
        });

        showProfilesButtonButton.setTooltip(Tooltip.create(Component.translatable("gui.optionsprofiles.show-profiles-button.tooltip")));
        this.layout.addToContents(showProfilesButtonButton);

        this.layout.addToFooter(
                Button.builder(
                                CommonComponents.GUI_DONE,
                                (button) -> this.onClose())
                        .width(200)
                        .build()
        );

        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    protected void repositionElements() {
        this.layout.arrangeElements();
    }

    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
        OptionsProfilesMod.config().save();
    }
}