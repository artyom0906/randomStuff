package com.artyom.mod.tabs;

import com.artyom.mod.RandomStuffClient;
import com.artyom.mod.kavin.tabs.tab.Tab;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class ArmorTab extends Tab {
    public ArmorTab() {
        super(getRenderItemStack());
    }

    @Override
    public void open() {
        RandomStuffClient.triggerOpenArmorMenu();
        /*client.player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public Text getDisplayName() {
                    return client.player.getStackInHand(Hand.MAIN_HAND).getName();
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    System.out.println("12345");
                    return new ArmorScreenHandler(inv, syncId, new ArmorInventory(bp.getWidth(), bp.getHeight(), client.player.getStackInHand(Hand.MAIN_HAND)));
                }

                @Override
                public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf buf) {
                    buf.writeInt(bp.getWidth());
                    buf.writeInt(bp.getHeight());
                }
            });*/
    }

    @Override
    public boolean shouldBeRemoved() {
        return false;
    }

    @Override
    public Text getHoverText() {
        return new LiteralText("Inventory");
    }

    @Override
    public String toString() {
        return "PLAYER INVENTORY TAB";
    }

    @Override
    public int getPriority() {
        return 100;
    }

    private static ItemStack getRenderItemStack() {
        ItemStack itemStack = new ItemStack(Items.DIAMOND_CHESTPLATE);
        itemStack.getOrCreateNbt().putString(
                "SkullOwner",
                MinecraftClient.getInstance().player.getGameProfile().getName()
        );

        return itemStack;
    }
}
