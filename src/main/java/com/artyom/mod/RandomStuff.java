package com.artyom.mod;

import com.artyom.mod.blocks.BoxBlock;
import com.artyom.mod.blocks.BoxBlockEntity;
import com.artyom.mod.fluids.MFGFluid;
import com.artyom.mod.inventories.ArmorInventory;
import com.artyom.mod.items.armor.TestChestplate;
import com.artyom.mod.kavin.init.InventoryTabsConfig;
import com.artyom.mod.registry.ModItems;
import com.artyom.mod.screen.ArmorScreenHandler;
import com.artyom.mod.screen.BoxScreenHandler;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RandomStuff implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "randomstuff";
    public static final String MOD_NAME = "RandomStuff";


    public static FlowableFluid STILL_MFG;
    public static FlowableFluid FLOWING_MFG;
    public static Item MFG_BUCKET;

    public static Block MFG;

    public static final Block BOX_BLOCK;
    public static final BlockItem BOX_BLOCK_ITEM;
    public static final BlockEntityType<BoxBlockEntity> BOX_BLOCK_ENTITY;
    public static final Identifier BOX = new Identifier(MOD_ID, "box_block");

    public static final ScreenHandlerType<BoxScreenHandler> BOX_SCREEN_HANDLER;
    public static final ScreenHandlerType<ArmorScreenHandler> ARMOR_SCREEN_HANDLER;

    public static final Identifier PACKET_RENAME_ARMOR = identify("packet_rename_armor");
    public static final Identifier PACKET_OPEN_ARMOR_GUI = identify("packer_open_armor_gui");
    public static final Identifier MODEL_BODY = new Identifier(MOD_ID, "models/misc/body.obj");
    public static final Identifier MODEL_ARMOR_R = new Identifier(MOD_ID, "models/misc/arm_r.obj");
    public static final Identifier MODEL_ARMOR_L = new Identifier(MOD_ID, "models/misc/arm_l.obj");
    public static Item customModeledArmor;

    static {
        BOX_BLOCK = Registry.register(Registry.BLOCK, BOX, new BoxBlock(FabricBlockSettings.copyOf(Blocks.CHEST)));
        BOX_BLOCK_ITEM = Registry.register(Registry.ITEM, BOX, new BlockItem(BOX_BLOCK, new Item.Settings().group(ItemGroup.MISC)));

        //The parameter of build at the very end is always null, do not worry about it
        BOX_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, BOX, FabricBlockEntityTypeBuilder.create(BoxBlockEntity::new, BOX_BLOCK).build(null));
        BOX_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(BOX, BoxScreenHandler::new);

        ARMOR_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(ArmorScreenHandler.IDENTIFIER, (i, pinv, buf) -> {
            return new ArmorScreenHandler(pinv, i, new ArmorInventory(buf.readInt(), buf.readInt(), null));
        });
    }

    @Override
    public void onInitialize() {
        AutoConfig.register(InventoryTabsConfig.class, JanksonConfigSerializer::new);
        log(Level.INFO, "Initializing");
        ModItems.registryItems();
        STILL_MFG   = Registry.register(Registry.FLUID, new Identifier(MOD_ID, "mfg"), new MFGFluid.Still());
        FLOWING_MFG = Registry.register(Registry.FLUID, new Identifier(MOD_ID, "flowing_mfg"), new MFGFluid.Flowing());
        MFG_BUCKET  = Registry.register(Registry.ITEM, new Identifier(RandomStuff.MOD_ID, "mfg_bucket"),
                new BucketItem(STILL_MFG,
                        new Item.Settings().group(ItemGroup.MATERIALS).recipeRemainder(Items.BUCKET).maxCount(1)));
        MFG = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "mfg"), new FluidBlock(STILL_MFG, FabricBlockSettings.copy(Blocks.WATER)){});
        Registry.register(Registry.ITEM, identify("test_chestplate"),
                customModeledArmor = new ArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.CHEST, new Item.Settings().group(ItemGroup.MISC).rarity(Rarity.COMMON)));

        Registry.register(Registry.ITEM, identify("test_chestplate_1"),
                new TestChestplate(9, 9, ArmorMaterials.DIAMOND, EquipmentSlot.CHEST, new Item.Settings().group(ItemGroup.MISC).rarity(Rarity.COMMON)));


        ServerPlayNetworking.registerGlobalReceiver(PACKET_RENAME_ARMOR, (server, player, handler, buf, responseSender) -> {
            final boolean def = buf.readBoolean();
            final Hand hand = buf.readEnumConstant(Hand.class);
            final ItemStack stack = player.getStackInHand(hand);

            if (!stack.isEmpty() && stack.getItem() instanceof TestChestplate) {
                if (def) {
                    stack.removeCustomName();
                } else {
                    final String name = buf.readString(32);
                    stack.setCustomName(new LiteralText(name));
                }
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(PACKET_OPEN_ARMOR_GUI, ((server, player, handler, buf, responseSender) -> {
            System.out.println("open armor tab");
            ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
            TestChestplate bp = (TestChestplate) stack.getItem();
            player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public Text getDisplayName() {
                    return stack.getName();
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new ArmorScreenHandler(inv, syncId, new ArmorInventory(bp.getWidth(), bp.getHeight(), stack));
                }

                @Override
                public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf buf) {
                    buf.writeInt(bp.getWidth());
                    buf.writeInt(bp.getHeight());
                }
            });
        }));
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }
    public static TranslatableText translate(String key, Object... params) {
        return new TranslatableText(MOD_ID + "." + key, params);
    }
    public static Identifier identify(String name) {
        return new Identifier(MOD_ID, name);
    }
    public static InventoryTabsConfig getConfig() {
        return AutoConfig.getConfigHolder(InventoryTabsConfig.class).getConfig();
    }
}