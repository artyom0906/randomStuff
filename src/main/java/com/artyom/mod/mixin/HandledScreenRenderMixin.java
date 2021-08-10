package com.artyom.mod.mixin;

import com.artyom.mod.inventories.ArmorInventory;
import com.artyom.mod.inventories.ArmorSlot;
import com.artyom.mod.items.ArmorInstallable;
import com.artyom.mod.screen.ArmorScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = HandledScreen.class)
public abstract class HandledScreenRenderMixin extends Screen{

    @Shadow protected abstract boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY);

    @Shadow protected int backgroundWidth;

    protected HandledScreenRenderMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawSlotHighlight(Lnet/minecraft/client/util/math/MatrixStack;III)V"))
    private void hover(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci){
        Slot slot = ((HandledScreenAccessor)this).getFocusedSlot();
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        if(slot instanceof ArmorSlot armorSlot){
            //armorSlot.onHover();
            fillGradient(matrices, slot.x, slot.y, slot.x + 11, slot.y + 11, -2130706433, -2130706433, this.getZOffset());
        }else {
            fillGradient(matrices, slot.x, slot.y, slot.x + 16, slot.y + 16, -2130706433, -2130706433, this.getZOffset());
        }
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }

    @Redirect(method = "render", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawSlotHighlight(Lnet/minecraft/client/util/math/MatrixStack;III)V"))
    private void inject(MatrixStack matrices, int x, int y, int z){

    }
    @Redirect(method = "render", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;isPointOverSlot(Lnet/minecraft/screen/slot/Slot;DD)Z"))
    private boolean isPointOverSlot(HandledScreen screen, Slot slot, double pointX, double pointY){
        if(slot instanceof ArmorSlot ) {
            return this.isPointWithinBounds(slot.x, slot.y, 10, 10, pointX, pointY);
        }
        return this.isPointWithinBounds(slot.x, slot.y, 16, 16, pointX, pointY);
    }

    @Redirect(method = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawSlot(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/screen/slot/Slot;)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderInGuiWithOverrides(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;III)V"))
    private void renderItemF(ItemRenderer itemRenderer, LivingEntity entity, ItemStack stack, int x, int y, int seed){
    }


    @Inject(method = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawSlot(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/screen/slot/Slot;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderInGuiWithOverrides(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;III)V"))
    private void renderItem(MatrixStack matrices, Slot slot, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player.currentScreenHandler instanceof ArmorScreenHandler && slot.inventory instanceof ArmorInventory ){
            if (slot.getStack().getItem() instanceof ArmorInstallable item) {
                this.itemRenderer.renderInGuiWithOverrides(this.client.player, slot.getStack(), slot.x + item.getSlotsX()*6-6, slot.y+item.getSlotsY()*6-6, slot.x + slot.y * this.backgroundWidth);
            }
        }
        else {
            this.itemRenderer.renderInGuiWithOverrides(this.client.player, slot.getStack(), slot.x , slot.y, slot.x + slot.y * this.backgroundWidth);
        }
    }

    @Redirect(method = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;getSlotAt(DD)Lnet/minecraft/screen/slot/Slot;",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;isPointOverSlot(Lnet/minecraft/screen/slot/Slot;DD)Z"))
    private boolean isPointOverSlot1(HandledScreen screen, Slot slot, double pointX, double pointY){
        if(slot instanceof ArmorSlot slot1) {
            if(slot1.hasStack()) {
                ArmorInstallable component = (ArmorInstallable) slot1.getStack().getItem();
                return this.isPointWithinBounds(slot.x, slot.y, 10 + (component.getSlotsX() - 1) * 12, 10 + (component.getSlotsY() - 1) * 12, pointX, pointY);
            }
            return this.isPointWithinBounds(slot.x, slot.y, 10, 10, pointX, pointY);
        }
        return this.isPointWithinBounds(slot.x, slot.y, 16, 16, pointX, pointY);
    }

    @Inject(method = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawSlot(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/screen/slot/Slot;)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderInGuiWithOverrides(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;III)V"))
    private void drawBack(MatrixStack matrices, Slot slot, CallbackInfo ci){
        //matrices.push();
        //matrices.translate(this.x, this.y, 0.0D);
        //RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, true);
        if(slot instanceof ArmorSlot && slot.getStack().getItem() instanceof ArmorInstallable item){
            fillGradient(matrices, slot.x, slot.y, slot.x+12*item.getSlotsX(), slot.y+12*item.getSlotsY(), 0xA0000010, 0xA0000010);
        }
        RenderSystem.colorMask(true, true, true, true);
        //RenderSystem.enableDepthTest();
       // matrices.pop();
    }
}
