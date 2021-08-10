package com.artyom.mod;

import com.artyom.mod.items.armor.ModularArmorModel;
import com.artyom.mod.screen.ArmorScreen;
import com.artyom.mod.screen.BoxScreen;
import com.artyom.mod.utils.CustomRenderLayers;
import com.artyom.mod.utils.RenderUtils;
import dev.monarkhēs.myron.api.Myron;
import dev.monarkhēs.myron.impl.client.model.MyronBakedModel;
import dev.monarkhēs.myron.impl.client.model.MyronUnbakedModel;
import dev.monarkhēs.myron.impl.mixin.BakedModelManagerAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.impl.client.rendering.ArmorRendererRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Items;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class RandomStuffClient implements ClientModInitializer {

    private static KeyBinding armorConfigKeyBinding;
    private static final Logger logger = LoggerFactory.getLogger(RandomStuff.class);


    public static void triggerOpenArmorMenu() {
        //TArmorNetwork.OPEN_ARMOR_C2S.sendEmptyToServer();
        ClientPlayNetworking.send(RandomStuff.PACKET_OPEN_ARMOR_GUI, PacketByteBufs.empty());
    }


    private BipedEntityModel<LivingEntity> armorModel;
    private final Identifier texture = new Identifier("textures/protocol2.png");
    @Override
    public void onInitializeClient() {
        setupFluidRendering(RandomStuff.STILL_MFG, RandomStuff.FLOWING_MFG, new Identifier(RandomStuff.MOD_ID, "mfg"), 0x964b00);
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), RandomStuff.STILL_MFG, RandomStuff.FLOWING_MFG);
        ScreenRegistry.register(RandomStuff.BOX_SCREEN_HANDLER, BoxScreen::new);
        ScreenRegistry.register(RandomStuff.ARMOR_SCREEN_HANDLER, ArmorScreen::new);

        ArmorRenderer.register((matrices, vertexConsumers, stack, entity, slot, light, model) -> {
            if (armorModel == null) {
                armorModel = new ModularArmorModel();
            }
            model.setAttributes(armorModel);
            armorModel.setVisible(false);
            armorModel.body.visible = slot == EquipmentSlot.CHEST;
            armorModel.leftArm.visible = slot == EquipmentSlot.CHEST;
            armorModel.rightArm.visible = slot == EquipmentSlot.CHEST;
            armorModel.head.visible = slot == EquipmentSlot.HEAD;
            armorModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getSolid()), light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
            Vec3d vec3d = Vec3d.ofBottomCenter(entity.getBlockPos());
            List<HostileEntity> list = MinecraftClient.getInstance().world.getEntitiesByClass(HostileEntity.class,
                    new Box(vec3d.getX() - 8.0D, vec3d.getY() - 5.0D, vec3d.getZ() - 8.0D, vec3d.getX() + 8.0D, vec3d.getY() + 5.0D, vec3d.getZ() + 8.0D),
                    (hostileEntity) -> {
                return hostileEntity.isAngryAt(MinecraftClient.getInstance().player);
            });
            list.forEach(e->{
                matrices.push();
                RenderUtils.drawQuadLine(matrices, vertexConsumers.getBuffer(CustomRenderLayers.QUAD_LINES), e.getPos(), vec3d, entity.getYaw(), entity.getHeadYaw(), 0.05f, (float) e.getPos().subtract(vec3d).length(), new float[]{60f, 68f, 169f}, 1f);
                matrices.pop();
            });
        }, RandomStuff.customModeledArmor);
    }


    public static void setupFluidRendering(final Fluid still, final Fluid flowing, final Identifier textureFluidId, final int color) {
        final Identifier stillSpriteId = new Identifier(textureFluidId.getNamespace(), "block/" + textureFluidId.getPath() + "_still");
        final Identifier flowingSpriteId = new Identifier(textureFluidId.getNamespace(), "block/" + textureFluidId.getPath() + "_flow");

        // If they're not already present, add the sprites to the block atlas
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
            registry.register(stillSpriteId);
            registry.register(flowingSpriteId);
        });

        final Identifier fluidId = Registry.FLUID.getId(still);
        final Identifier listenerId = new Identifier(fluidId.getNamespace(), fluidId.getPath() + "_reload_listener");

        final Sprite[] fluidSprites = {null, null};

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {


            @Override
            public void reload(ResourceManager manager) {
                final Function<Identifier, Sprite> atlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
                fluidSprites[0] = atlas.apply(stillSpriteId);
                fluidSprites[1] = atlas.apply(flowingSpriteId);
            }

            @Override
            public Identifier getFabricId() {
                return listenerId;
            }
        });

        // The FluidRenderer gets the sprites and color from a FluidRenderHandler during rendering
        final FluidRenderHandler renderHandler = new FluidRenderHandler() {
            @Override
            public Sprite[] getFluidSprites(BlockRenderView view, BlockPos pos, FluidState state) {
                return fluidSprites;
            }

            @Override
            public int getFluidColor(BlockRenderView view, BlockPos pos, FluidState state) {
                return color;
            }
        };

        FluidRenderHandlerRegistry.INSTANCE.register(still, renderHandler);
        FluidRenderHandlerRegistry.INSTANCE.register(flowing, renderHandler);
    }

}