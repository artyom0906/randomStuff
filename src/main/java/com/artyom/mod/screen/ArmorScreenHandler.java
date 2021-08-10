package com.artyom.mod.screen;
import com.artyom.mod.RandomStuff;
import com.artyom.mod.inventories.ArmorInventory;
import com.artyom.mod.inventories.ArmorSlot;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

import java.util.List;

public class ArmorScreenHandler extends ScreenHandler {

    public static final Identifier IDENTIFIER = new Identifier(RandomStuff.MOD_ID, "generic_container");
    private final ArmorInventory inventory;

    public ArmorScreenHandler(PlayerInventory playerInv, int sync, ArmorInventory inventory) {
        super(RandomStuff.ARMOR_SCREEN_HANDLER, sync);
        this.inventory = inventory;

        // Backpack inventory
        for (int n = 0; n < this.inventory.height(); ++n) {
            for (int m = 0; m < this.inventory.width(); ++m) {
                addSlot(new ArmorSlot(inventory, m + n * this.inventory.width(), 8 + m * 12, 18 + n * 12, m, n));
            }
        }

        // Player inventory
        for (int n = 0; n < 3; ++n) {
            for (int m = 0; m < 9; ++m) {
                this.addSlot(new Slot(playerInv, m + n * 9 + 9, 8 + m * 18, 31 + (this.inventory.height() + n) * 18));
            }
        }

        // Player hotbar
        for (int n = 0; n < 9; ++n) {
            this.addSlot(new Slot(playerInv, n, 8 + n * 18, 89 + this.inventory.height() * 18));
        }

        this.inventory.onOpen(playerInv.player);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasStack()) {
            final ItemStack stack2 = slot.getStack();
            stack = stack2.copy();

            if (index < this.inventory.size()) {
                if (!this.insertItem(stack2, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(stack2, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (stack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return stack;
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.inventory.onClose(player);
    }

    public ArmorInventory inventory() {
        return this.inventory;
    }
}