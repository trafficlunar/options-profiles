package net.trafficlunar.optionsprofiles.gui;

import net.trafficlunar.optionsprofiles.OptionsProfilesMod;
import net.trafficlunar.optionsprofiles.profiles.ProfileConfiguration;
import net.trafficlunar.optionsprofiles.profiles.Profiles;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ProfilesList extends ContainerObjectSelectionList<ProfilesList.ProfileEntry> {
    private final ProfilesScreen profilesScreen;

    public ProfilesList(ProfilesScreen profilesScreen, Minecraft minecraft) {
        super(minecraft, profilesScreen.width, profilesScreen.layout.getContentHeight(), profilesScreen.layout.getHeaderHeight(), 20);
        this.profilesScreen = profilesScreen;

        refreshEntries();
    }

    public void refreshEntries() {
        this.clearEntries();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Profiles.PROFILES_DIRECTORY)) {
            List<Path> profileList = new ArrayList<>();
            for (Path profile : directoryStream) {
                if (Files.isDirectory(profile)) {
                    profileList.add(profile);
                }
            }

            // Sort the list alphabetically based on the profile names
            profileList.sort(Comparator.comparing(p -> p.getFileName().toString()));

            for (Path profile : profileList) {
                this.addEntry(new ProfilesList.ProfileEntry(Component.literal(profile.getFileName().toString())));
            }
        } catch (Exception e) {
            OptionsProfilesMod.LOGGER.error("An error occurred when listing profiles", e);
        }
    }

    protected int scrollBarX() {
        return super.scrollBarX() + 15;
    }

    public int getRowWidth() {
        return 340;
    }

    public class ProfileEntry extends ContainerObjectSelectionList.Entry<ProfilesList.ProfileEntry> {
        private final Component profileName;
        private final Button editButton;
        private final Button loadButton;

        ProfileEntry(Component profileName) {
            this.profileName = profileName;

            this.editButton = Button.builder(
                            Component.translatable("gui.optionsprofiles.edit-profile"),
                            (button) -> minecraft.setScreen(new EditProfileScreen(profilesScreen, profileName)))
                    .size(75, 20)
                    .build();

            this.loadButton = Button.builder(
                            Component.translatable("gui.optionsprofiles.load-profile"),
                            (button) -> {
                                Profiles.loadProfile(profileName.getString());

                                minecraft.options.load();

                                if (ProfileConfiguration.get(profileName.getString()).getOptionsToLoad().contains("resourcePacks")) {
                                    minecraft.options.loadSelectedResourcePacks(minecraft.getResourcePackRepository());
                                    minecraft.reloadResourcePacks();
                                }

                                minecraft.options.save();
                                minecraft.levelRenderer.allChanged();
                            })
                    .size(75, 20)
                    .build();
        }

        public void render(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            Font fontRenderer = ProfilesList.this.minecraft.font;

            int posX = ProfilesList.this.scrollBarX() - this.loadButton.getWidth() - 10;
            int posY = y - 2;
            int textY = y + entryHeight / 2;

            guiGraphics.drawString(fontRenderer, this.profileName, x, textY - 9 / 2, 16777215, false);

            this.editButton.setPosition(posX - this.editButton.getWidth(), posY);
            this.editButton.render(guiGraphics, mouseX, mouseY, tickDelta);

            this.loadButton.setPosition(posX, posY);
            this.loadButton.render(guiGraphics, mouseX, mouseY, tickDelta);
        }

        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.editButton, this.loadButton);
        }

        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(this.editButton, this.loadButton);
        }
    }
}