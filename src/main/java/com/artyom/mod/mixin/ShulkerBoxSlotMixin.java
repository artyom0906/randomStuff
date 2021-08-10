package com.artyom.mod.mixin;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.ShulkerBoxSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerBoxSlot.class)
public class ShulkerBoxSlotMixin {

    /**
     * Prevent for storing backpacks into shulkerbox
     */
    @Inject(method = "canInsert", at = @At("TAIL"))
    public boolean canInsert(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        return !(stack.getItem() instanceof ArmorItem);
    }
}