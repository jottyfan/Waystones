package net.blay09.mods.waystones.client.gui.screen;

import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.container.WaystoneSettingsContainer;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.EditWaystoneMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import org.lwjgl.glfw.GLFW;

public class WaystoneSettingsScreen extends ContainerScreen<WaystoneSettingsContainer> {

    private TextFieldWidget textField;
    private Button btnDone;
    private GuiCheckBox chkGlobal;

    public WaystoneSettingsScreen(WaystoneSettingsContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        xSize = 270;
        ySize = 200;
    }

    @Override
    public void init() {
        // Leave no space for JEI!
        xSize = width;

        super.init();
        IWaystone waystone = container.getWaystone();
        String oldText = waystone.getName();
        if (textField != null) {
            oldText = textField.getText();
        }

        textField = new TextFieldWidget(Minecraft.getInstance().fontRenderer, width / 2 - 100, height / 2 - 20, 200, 20, textField, "");
        textField.setMaxStringLength(128);
        textField.setText(oldText);
        textField.changeFocus(true);
        addButton(textField);
        setFocusedDefault(textField);

        btnDone = new Button(width / 2, height / 2 + 10, 100, 20, I18n.format("gui.done"), button -> {
            if (textField.getText().isEmpty()) {
                textField.changeFocus(true);
                setFocused(textField);
                return;
            }

            NetworkHandler.channel.sendToServer(new EditWaystoneMessage(waystone, textField.getText(), chkGlobal.isChecked()));
        });
        addButton(btnDone);

        chkGlobal = new GuiCheckBox(width / 2 - 100, height / 2 + 15, " " + I18n.format("gui.waystones.waystone_settings.is_global"), waystone.isGlobal());
        if (!PlayerWaystoneManager.mayEditGlobalWaystones(Minecraft.getInstance().player)) {
            chkGlobal.visible = false;
        }

        addButton(chkGlobal);

        getMinecraft().keyboardListener.enableRepeatEvents(true);
    }

    @Override
    public void removed() {
        super.removed();
        getMinecraft().keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (textField.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            btnDone.onPress();
            return true;
        }

        if (textField.keyPressed(keyCode, scanCode, modifiers) || textField.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                getMinecraft().player.closeScreen();
            }

            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void tick() {
        textField.tick();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        super.render(mouseX, mouseY, partialTicks);

        Minecraft.getInstance().fontRenderer.drawString(getTitle().getFormattedText(), width / 2f - 100, height / 2f - 35, 0xFFFFFF);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    }
}
