package net.trafficlunar.optionsprofiles.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.trafficlunar.optionsprofiles.profiles.ProfileConfiguration;

public class OptionsToggleScreen extends OptionsSubScreen {
    private final Component profileName;
    private final ProfileConfiguration profileConfiguration;
    private OptionsToggleList optionsToggleList;
    private EditBox searchBox;

    protected OptionsToggleScreen(Screen lastScreen, Component profileName, ProfileConfiguration profileConfiguration) {
        super(lastScreen, null, Component.literal(Component.translatable("gui.optionsprofiles.options-toggle").append(": ").getString() + profileName.getString()));
        this.profileName = profileName;
        this.profileConfiguration = profileConfiguration;
    }

    protected void init() {
        this.searchBox = new EditBox(this.minecraft.font, 0, 0, 200, 20, Component.empty());
        this.searchBox.setHint(Component.translatable("gui.optionsprofiles.options-search-hint"));
        super.init();
    }

    protected void addTitle() {
        this.layout.addToHeader(new StringWidget(this.title, this.font), (settings) -> settings.alignVerticallyTop().paddingTop(6));
        this.layout.addToHeader(this.searchBox, (settings) -> settings.alignVerticallyBottom().padding(4));
    }

    protected void addOptions() {}

    protected void addContents() {
        this.layout.setHeaderHeight(42);
        this.optionsToggleList = this.layout.addToContents(new OptionsToggleList(this, this.minecraft, profileName.getString(), this.profileConfiguration, this.searchBox));
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
        this.profileConfiguration.save();
    }

    protected void repositionElements() {
        this.layout.arrangeElements();
        this.optionsToggleList.updateSize(this.width, this.layout);
    }
}
