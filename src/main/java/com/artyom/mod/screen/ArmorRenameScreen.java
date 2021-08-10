package com.artyom.mod.screen;

import com.artyom.mod.RandomStuff;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;


public class ArmorRenameScreen extends Screen {

    private static final Identifier ICONS = RandomStuff.identify("textures/gui/icons.png");
    private static final Identifier BACKGROUND = new Identifier("textures/gui/demo_background.png");

    private static final int NAME_MAX_CHARS = 32;

    private final Hand hand;
    private final Text placeHolder;
    private final OrderedText orderedTextTitle;
    private final TranslatableText maxCharsMsg;
    private TextFieldWidget textField;

    private int x;
    private int y;

    public ArmorRenameScreen(Hand hand, Text placeHolder) {
        super(RandomStuff.translate("screen.title.rename"));
        this.hand = hand;
        this.orderedTextTitle = this.getTitle().asOrderedText();
        this.placeHolder = placeHolder;
        this.maxCharsMsg = RandomStuff.translate("screen.rename.max.length", NAME_MAX_CHARS);
    }

    @Override
    protected void init() {
        super.init();

        this.x = (this.width - 248) / 2;
        this.y = (this.height - 120) / 2;

        this.textField = new TextFieldWidget(this.textRenderer, this.x + ((248 - 195) / 2), this.y + 50, 195, 20, this.placeHolder);
        this.textField.setMaxLength(NAME_MAX_CHARS);
        this.addDrawableChild(this.textField);

        this.textField.setText(this.placeHolder.asString());

        // Reset name
        this.addDrawable(new ButtonIconWidget(this.x + (248 - 50) / 2, this.y + 120 - 26, RandomStuff.translate("screen.button.default.name"), b -> {
            b.playDownSound(MinecraftClient.getInstance().getSoundManager());
            this.sendChange(true);
        }, ICONS, 32, 0, this::renderTooltip));

        // Rename
        this.addDrawable(new ButtonIconWidget(30 + this.x + (248 - 50) / 2, this.y + 120 - 26, RandomStuff.translate("screen.button.rename"), b -> {
            b.playDownSound(MinecraftClient.getInstance().getSoundManager());
            this.sendChange(false);
        }, ICONS, 0, 0, this::renderTooltip));

        // Close
        this.addDrawable(new ButtonIconWidget(this.x + 222, this.y + 6, RandomStuff.translate("screen.button.close"), b -> {
            b.playDownSound(MinecraftClient.getInstance().getSoundManager());
            this.onClose();
        }, ICONS, 16, 0, this::renderTooltip));

        this.setInitialFocus(this.textField);
    }

    public void sendChange(boolean def) {
        final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(def);
        buf.writeEnumConstant(hand);

        if (!def) {
            buf.writeString(this.textField.getText().trim());
        }

        ClientPlayNetworking.send(RandomStuff.PACKET_RENAME_ARMOR, buf);
        this.onClose();
    }

    @Override
    public void tick() {
        super.tick();
        this.textField.tick();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            this.sendChange(false);
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        // Background rendering
        this.client.getTextureManager().bindTexture(BACKGROUND);
        this.drawTexture(matrices, this.x, this.y, 0, 0, 248, 116);
        this.drawTexture(matrices, this.x, this.y + 116, 0, 162, 248, 4);

        this.textField.render(matrices, mouseX, mouseY, delta);

        // Title
        this.textRenderer.draw(matrices, this.orderedTextTitle, (this.width - this.textRenderer.getWidth(this.orderedTextTitle)) / 2, this.y + 8, 0x404040);

        // Max length message
        this.textRenderer.draw(matrices, this.maxCharsMsg, this.textField.x, this.textField.y - this.textRenderer.fontHeight - 5, 0xFFFF5252);
        super.render(matrices, mouseX, mouseY, delta);
    }
}