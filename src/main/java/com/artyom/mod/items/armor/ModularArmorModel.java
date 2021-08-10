package com.artyom.mod.items.armor;

import com.artyom.mod.RandomStuff;
import dev.monarkhēs.myron.api.Myron;
import dev.monarkhēs.myron.impl.client.model.MyronBakedModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

import java.util.Collections;
import java.util.Map;

public class ModularArmorModel extends BipedEntityModel<LivingEntity> {
    ModelPart part;
    public ModularArmorModel() {
        super(createFull());
        part = createFull();
        part.setPivot(0F, 0F, 0F);
    }

    private static ModelPart createFull() {
        //MinecraftClient.getInstance().player.world.getEntitiesByType(EntityType.SKELETON, new Box())
        return new ModelPart(Collections.singletonList(new ModelPart.Cuboid(0, 0, -4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F,
                0F, 0F, 0F, true, 64F, 64F) {
            @Override
            public void renderCuboid(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {

            }
        }),
                Map.of("head", createPart(),
                        "hat", createPart(),
                        "body", createBody(),
                        "right_arm", createHand(RandomStuff.MODEL_ARMOR_R),
                        "left_arm", createHand(RandomStuff.MODEL_ARMOR_L),
                        "right_leg", createPart(),
                        "left_leg", createPart()));
    }

    private static ModelPart createPart() {
        return new ModelPart(Collections.singletonList(new ModelPart.Cuboid(1, 0, 0, 0F, 10F, .1F, .1F, .1F,
                0F, 0F, 0F, true, 1F, 1F)), Collections.emptyMap());
    }

    private static ModelPart createBody(){
        return new ModelPart(Collections.singletonList(new ModelPart.Cuboid(1, 0, 0, 0F, 10F, .1F, .1F, .1F,
                0F, 0F, 0F, true, 1F, 1F){
            @Override
            public void renderCuboid(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
                if (Myron.getModel(RandomStuff.MODEL_BODY) instanceof MyronBakedModel model) {
                    assert MinecraftClient.getInstance().player != null;
                    model.getQuads(null, null, null)
                            .forEach(quad -> {
                                vertexConsumer.quad(entry, quad, 1F, 1F, 1F, light, overlay);
                            });
                }
            }
        }), Collections.emptyMap());
    }
    private static ModelPart createHand(Identifier identifier){
        return new ModelPart(Collections.singletonList(new ModelPart.Cuboid(1, 0, 0, 0F, 10F, .1F, .1F, .1F,
                0F, 0F, 0F, true, 1F, 1F){
            @Override
            public void renderCuboid(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
                if (Myron.getModel(identifier) instanceof MyronBakedModel model) {
                    assert MinecraftClient.getInstance().player != null;
                    model.getQuads(null, null, null)
                            .forEach(quad -> {
                                vertexConsumer.quad(entry, quad, 1F, 1F, 1F, light, overlay);
                            });
                }
            }
        }), Collections.emptyMap());
    }
    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return Collections::emptyIterator;
    }

}