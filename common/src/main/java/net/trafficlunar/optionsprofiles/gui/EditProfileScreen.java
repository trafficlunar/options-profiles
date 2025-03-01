package net.trafficlunar.optionsprofiles.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.trafficlunar.optionsprofiles.profiles.ProfileConfiguration;
import net.trafficlunar.optionsprofiles.profiles.Profiles;

public class EditProfileScreen extends Screen {
    private final ProfilesScreen profilesScreen;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 24, 33);

    private final Component profileName;
    private final ProfileConfiguration profileConfiguration;
    private EditBox profileNameEdit;

    public EditProfileScreen(ProfilesScreen profilesScreen, Component profileName) {
        super(Component.literal(Component.translatable("gui.optionsprofiles.editing-profile-title").getString() + profileName.getString()));
        this.profilesScreen = profilesScreen;
        this.profileName = profileName;
        this.profileConfiguration = ProfileConfiguration.get(profileName.getString());
    }

    protected void init() {
        LinearLayout linearLayoutHeader = this.layout.addToHeader(LinearLayout.vertical());
        linearLayoutHeader.addChild(new StringWidget(this.title, this.font), LayoutSettings::alignHorizontallyCenter);

        this.profileNameEdit = new EditBox(this.font, this.width / 2 - 102, 116, 204, 20, Component.empty());
        this.profileNameEdit.setValue(profileName.getString());

        LinearLayout linearLayoutContent = this.layout.addToContents(LinearLayout.vertical().spacing(12), LayoutSettings::alignHorizontallyCenter);

        LinearLayout linearLayoutEditBox = linearLayoutContent.addChild(LinearLayout.vertical().spacing(6), LayoutSettings::alignHorizontallyCenter);
        linearLayoutEditBox.addChild(new StringWidget(Component.translatable("gui.optionsprofiles.profile-name-text"), this.font), LayoutSettings::alignHorizontallyCenter);
        linearLayoutEditBox.addChild(this.profileNameEdit);

        LinearLayout linearLayoutButtons = linearLayoutContent.addChild(LinearLayout.vertical().spacing(1), LayoutSettings::alignHorizontallyCenter);
        linearLayoutButtons.addChild(
                Button.builder(
                                Component.translatable("gui.optionsprofiles.overwrite-options"),
                                (button) -> {
                                    Profiles.writeProfile(profileName.getString(), true);
                                    this.onClose();
                                })
                        .size(150, 20)
                        .pos(this.width / 2 - 75, 145)
                        .tooltip(Tooltip.create(Component.translatable("gui.optionsprofiles.overwrite-options.tooltip")))
                        .build(),
                LayoutSettings::alignHorizontallyCenter
        );
        linearLayoutButtons.addChild(
                Button.builder(
                                Component.translatable("gui.optionsprofiles.rename-profile"),
                                (button) -> {
                                    Profiles.renameProfile(profileName.getString(), this.profileNameEdit.getValue());
                                    this.minecraft.setScreen(new EditProfileScreen(profilesScreen, Component.literal(this.profileNameEdit.getValue())));
                                })
                        .size(150, 20)
                        .pos(this.width / 2 - 75, 166)
                        .build(),
                LayoutSettings::alignHorizontallyCenter
        );
        linearLayoutButtons.addChild(
                Button.builder(
                                Component.translatable("gui.optionsprofiles.options-toggle").append("..."),
                                (button) -> this.minecraft.setScreen(new OptionsToggleScreen(this, profileName)))
                        .size(150, 20)
                        .pos(this.width / 2 - 75, 187)
                        .tooltip(Tooltip.create(Component.translatable("gui.optionsprofiles.options-toggle.tooltip")))
                        .build(),
                LayoutSettings::alignHorizontallyCenter
        );

        LinearLayout linearLayoutSettings = linearLayoutContent.addChild(LinearLayout.vertical().spacing(1), LayoutSettings::alignHorizontallyCenter);
        linearLayoutSettings.addChild(
                CycleButton.<Integer>builder(value -> Component.literal(value.toString()))
                        .withValues(0, 1, 2, 3)
                        .withInitialValue(this.profileConfiguration.getKeybindIndex())
                        .create(0, 0, 150, 20, Component.translatable("gui.optionsprofiles.keybind-index"), (button, keybindIndex) -> {
                            this.profileConfiguration.setKeybindIndex(keybindIndex);
                        }),
                LayoutSettings::alignHorizontallyCenter
        );
        linearLayoutSettings.addChild(
                CycleButton.onOffBuilder(this.profileConfiguration.shouldLoadOnStartup())
                        .create(0, 0, 150, 20, Component.translatable("gui.optionsprofiles.load-on-startup"), (button, boolean_) -> {
                            this.profileConfiguration.setLoadOnStartup(boolean_);
                        }),
                LayoutSettings::alignHorizontallyCenter
        );

        this.layout.addToFooter(
                Button.builder(
                                CommonComponents.GUI_DONE,
                                (button) -> this.onClose())
                        .width(200)
                        .build()
        );
        this.layout.addToFooter(
                Button.builder(
                                Component.translatable("gui.optionsprofiles.delete-profile")
                                        .withStyle(ChatFormatting.RED),
                                (button) -> {
                                    Profiles.deleteProfile(profileName.getString());
                                    this.onClose();
                                })
                        .width(50)
                        .build(),
                (layoutSettings) -> layoutSettings.alignHorizontallyLeft().paddingLeft(5)
        );

        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    protected void repositionElements() {
        this.layout.arrangeElements();
    }

    public void onClose() {
        this.profileConfiguration.save();
        this.minecraft.setScreen(this.profilesScreen);
        this.profilesScreen.profilesList.refreshEntries();
    }
}