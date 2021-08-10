package com.artyom.mod.items;

import com.artyom.mod.inventories.ArmorInventory;
import com.artyom.mod.screen.ArmorRenameScreen;
import com.artyom.mod.screen.ArmorScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

import static com.artyom.mod.RandomStuff.translate;

public class BasicReactor extends Item implements ArmorInstallable{

    private final int slotsX;
    private final int slotsY;
    private int slotX = 0;
    private int slotY = 0;

    public BasicReactor(int slotsX, int slotsY, Settings settings) {
        super(settings.maxCount(1));
        this.slotsX = slotsX;
        this.slotsY = slotsY;
    }

    @Override
    public int getSlotsX() {
        return slotsX;
    }

    @Override
    public int getSlotsY() {
        return slotsY;
    }

    @Override
    public int getPosXInGrid() {
        return slotX;
    }

    @Override
    public int getPosYInGrid() {
        return slotY;
    }

    @Override
    public void setPosXInGrid(int x) {
        slotX = x;
    }

    @Override
    public void setPosYInGrid(int y) {
        slotY = y;
    }
}