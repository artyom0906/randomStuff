package com.artyom.mod.mixin;

import com.artyom.mod.RandomStuff;
import com.artyom.mod.items.ArmorInstallable;
import com.artyom.mod.registry.ModItems;
import com.artyom.mod.screen.ArmorScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.ItemRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.modify.LocalVariableDiscriminator;


@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Inject( method = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiItemModel(Lnet/minecraft/item/ItemStack;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;applyModelViewMatrix()V", shift = At.Shift.BEFORE),
    slice = @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;scale(FFF)V"),
            to = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;draw()V")
    ))
    public void hook_method_23179(ItemStack stack, int x, int y, BakedModel model, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player.currentScreenHandler instanceof ArmorScreenHandler)
            if (stack.getItem() instanceof ArmorInstallable item) {
                if (!isInPlayerInventory(stack)) {
                    MatrixStack matrixStack = RenderSystem.getModelViewStack();
                    matrixStack.pop();

                    matrixStack.push();
                    matrixStack.translate((double)x, (double)y, (double)(100.0F + ((ItemRendererAccessor)this).getZOffset()));
                    matrixStack.translate(6.0D, 6.0D, 0.0D);
                    matrixStack.scale(1.0F, -1.0F, 1.0F);
                    matrixStack.scale(12.0F, 12.0F, 12.0F);
                    matrixStack.scale(item.getSlotsX(), item.getSlotsY(), item.getSlotsX());

                }
            }
    }
    private boolean isInPlayerInventory(ItemStack stack){
        for(int i = 0; i < MinecraftClient.getInstance().player.getInventory().main.size(); ++i) {
            if (!MinecraftClient.getInstance().player.getInventory().main.get(i).isEmpty() &&
                    stack == MinecraftClient.getInstance().player.getInventory().main.get(i)) {
                return true;
            }
        }
        return false;
    }

    private static void fillGradient(MatrixStack matrices, int startX, int startY, int endX, int endY, int colorStart, int colorEnd, int z) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        fillGradient(matrices.peek().getModel(), bufferBuilder, startX, startY, endX, endY, z, colorStart, colorEnd);
        tessellator.draw();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    private static void fillGradient(Matrix4f matrix, BufferBuilder bufferBuilder, int startX, int startY, int endX, int endY, int z, int colorStart, int colorEnd) {
        float f = (float)(colorStart >> 24 & 255) / 255.0F;
        float g = (float)(colorStart >> 16 & 255) / 255.0F;
        float h = (float)(colorStart >> 8 & 255) / 255.0F;
        float i = (float)(colorStart & 255) / 255.0F;
        float j = (float)(colorEnd >> 24 & 255) / 255.0F;
        float k = (float)(colorEnd >> 16 & 255) / 255.0F;
        float l = (float)(colorEnd >> 8 & 255) / 255.0F;
        float m = (float)(colorEnd & 255) / 255.0F;
        bufferBuilder.vertex(matrix, (float)endX, (float)startY, (float)z).color(g, h, i, f).next();
        bufferBuilder.vertex(matrix, (float)startX, (float)startY, (float)z).color(g, h, i, f).next();
        bufferBuilder.vertex(matrix, (float)startX, (float)endY, (float)z).color(k, l, m, j).next();
        bufferBuilder.vertex(matrix, (float)endX, (float)endY, (float)z).color(k, l, m, j).next();
    }
}
