package net.trafficlunar.optionsprofiles.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.trafficlunar.optionsprofiles.OptionsProfilesMod;
import net.trafficlunar.optionsprofiles.profiles.Profiles;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
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

//        checkEntriesLoaded();
    }

//    public void checkEntriesLoaded() {
//        this.children().forEach(ProfileEntry::checkLoaded);
//    }

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
                                OptionsProfilesMod.LOGGER.warn("[Profile '{}']: Loaded through button", profileName);

//                                ProfilesList.this.checkEntriesLoaded();
//                                button.active = false;
                            })
                    .size(75, 20)
                    .build();
        }

        @Override
        public void extractContent(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int posX = ProfilesList.this.scrollBarX() - this.loadButton.getWidth() - 10;
            int posY = this.getContentY() - 2;

            this.editButton.setPosition(posX - this.editButton.getWidth(), posY);
            this.editButton.extractRenderState(guiGraphics, mouseX, mouseY, tickDelta);

            this.loadButton.setPosition(posX, posY);
            this.loadButton.extractRenderState(guiGraphics, mouseX, mouseY, tickDelta);

            guiGraphics.centeredText(ProfilesList.this.minecraft.font, this.profileName, this.getContentX(), this.getContentYMiddle() - 4, -1);
        }

        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.editButton, this.loadButton);
        }

        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(this.editButton, this.loadButton);
        }

//        protected void checkLoaded() {
//            this.loadButton.active = !Profiles.isProfileLoaded(profileName.getString());
//        }
    }
}