package com.artyom.mod.items.armor;
import com.artyom.mod.inventories.ArmorInventory;
import com.artyom.mod.screen.ArmorRenameScreen;
import com.artyom.mod.screen.ArmorScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
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

import static com.artyom.mod.RandomStuff.translate;

public class TestChestplate extends ArmorItem {

    private final int width;
    private final int height;

    public TestChestplate(int width, int height, ArmorMaterial material, EquipmentSlot slot, Settings settings) {
        super(material, slot, settings);
        this.width = width;
        this.height = height;
    }




    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        final ItemStack stack = user.getStackInHand(hand);

        if (!user.isSneaking()) {
            if (!world.isClient()) {
                openScreen(user, stack);
            } else {
                user.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.0F, 1.0F);
            }

            return TypedActionResult.consume(stack);
        } else if (world.isClient()) {
            openRenameScreen(hand, stack.getName());
            return TypedActionResult.pass(stack);
        }

        return super.use(world, user, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(translate("tooltip.size", this.width, this.height, this.size()).formatted(Formatting.GRAY));
        tooltip.add(translate("tooltip.how.to.rename").formatted(Formatting.GRAY));
    }

    /**
     * Return backpack inventory size
     *
     * @return Inventory size (number of slots)
     */
    public int size() {
        return this.width * this.height;
    }

    public static final void openScreen(PlayerEntity user, ItemStack stack) {
        final TestChestplate bp = (TestChestplate) stack.getItem();

        user.openHandledScreen(new ExtendedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                return stack.getName();
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                return new ArmorScreenHandler(inv, syncId, new ArmorInventory(bp.width, bp.height, stack));
            }

            @Override
            public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf buf) {
                buf.writeInt(bp.width);
                buf.writeInt(bp.height);
            }
        });
    }

    @Environment(EnvType.CLIENT)
    public static final void openRenameScreen(Hand hand, Text name) {
        MinecraftClient.getInstance().setScreen(new ArmorRenameScreen(hand, name));
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}