package com.artyom.mod.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;

public class RenderUtils {

    /**
     * Utility for drawing lines made out of a series of quads in any direction
     * @param matrices  Matrix stack, used to track the current view transformations e.g. translation, rotation
     * @param consumer  Buffer to render the model to
     * @param tail Direction in which the line should be drawn
     * @param head Player position
     * @param entityYaw entity yaw
     * @param w Line width
     * @param l Line length
     * @param colour RGB colour array
     * @param a Colour alpha
     */
    public static void drawQuadLine(MatrixStack matrices, VertexConsumer consumer, Vec3d tail, Vec3d head, float entityYaw, float headYaw, float w, float l, float[] colour, float a) {
        // Get the transformation matrix and translate to the center
        Matrix4f transMatrix = matrices.peek().getModel();
        matrices.translate(0.5, 0.5, 0.5);

        float r = colour[0];
        float g = colour[1];
        float b = colour[2];

        // TODO - Switch to facing.getRotationQuaternion() approach at some point to optimise
        // Translations constructed from a north facing direction, hence rotate for other directions
        //switch(facing) {
        //    case UP -> matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
        //    case DOWN -> matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
        //    case SOUTH -> matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
        //    case EAST -> matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90));
        //    case WEST -> matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
        //}

        //float A = (float) Math.acos(facing.dotProduct(playerPos) / (facing.length() * playerPos.length()));
        //matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(A));

        double dx = head.x - tail.x;
        double dy = head.y - tail.y;
        double dz = head.z - tail.z;

        double renderSize = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double angleZ = 360 - (Math.atan2(dz, dx) * 180.0 / Math.PI + 180.0);
        dx = Math.sqrt(renderSize * renderSize - dy * dy);
        double angleY = -Math.atan2(dy, dx) * 180 / Math.PI;

        matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion((float) angleZ-(180-entityYaw)-90));

        // left side
        matrices.translate(-w/2, -w/2, 0);
        consumer.vertex(transMatrix, 0, 0, 0).color(r,g,b,a).next();
        consumer.vertex(transMatrix, 0, w, 0).color(r,g,b,a).next();
        consumer.vertex(transMatrix, 0, w, -l).color(r,g,b,a).next();
        consumer.vertex(transMatrix, 0, 0, -l).color(r,g,b,a).next();

        // right side
        matrices.translate(w, 0, 0);
        consumer.vertex(transMatrix, 0, 0, 0).color(r,g,b,a).next();
        consumer.vertex(transMatrix, 0, w, 0).color(r,g,b,a).next();
        consumer.vertex(transMatrix, 0, w, -l).color(r,g,b,a).next();
        consumer.vertex(transMatrix, 0, 0, -l).color(r,g,b,a).next();

        // bottom side
        consumer.vertex(transMatrix, 0, 0, 0).color(r,g,b,a).next();
        consumer.vertex(transMatrix, -w, 0, 0).color(r,g,b,a).next();
        consumer.vertex(transMatrix, -w, 0, -l).color(r,g,b,a).next();
        consumer.vertex(transMatrix, 0, 0, -l).color(r,g,b,a).next();

        // top side
        matrices.translate(0, w, 0);
        consumer.vertex(transMatrix, 0, 0, 0).color(r,g,b,a).next();
        consumer.vertex(transMatrix, -w, 0, 0).color(r,g,b,a).next();
        consumer.vertex(transMatrix, -w, 0, -l).color(r,g,b,a).next();
        consumer.vertex(transMatrix, 0, 0, -l).color(r,g,b,a).next();

        // end bit
        matrices.translate(0, -w, -l);
        consumer.vertex(transMatrix, 0, 0, 0).color(r,g,b,a).next();
        consumer.vertex(transMatrix, 0, w, 0).color(r,g,b,a).next();
        consumer.vertex(transMatrix, -w, w, 0).color(r,g,b,a).next();
        consumer.vertex(transMatrix, -w, 0, 0).color(r,g,b,a).next();

        matrices.translate(0.5, 0.5, 0.5);
    }

    public static BlockPos getRaycastPos(PlayerEntity player, double maxDistance) {
        HitResult result = player.raycast(maxDistance, 1f, false);
        Vec3d hitVec = result.getPos();
        return new BlockPos(hitVec.x, hitVec.y, hitVec.z);
    }
}
