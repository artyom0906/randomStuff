package com.artyom.mod.utils;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CustomRenderLayers extends RenderLayer {

    //  Dummy
    public CustomRenderLayers(String nameIn, VertexFormat formatIn, VertexFormat.DrawMode drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }


   public static RenderLayer QUAD_LINES;
    static {
        try {
            Method privateMethod = RenderLayer.class.getDeclaredMethod("of", String.class, VertexFormat.class, VertexFormat.DrawMode.class, int.class, boolean.class, boolean.class, MultiPhaseParameters.class);
            privateMethod.setAccessible(true);
            QUAD_LINES = (RenderLayer) privateMethod.invoke(null, "territorial_quad_lines",
                    VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS, 256, false, false,
                    MultiPhaseParameters.builder()
                            .layering(VIEW_OFFSET_Z_LAYERING)
                            .transparency(TRANSLUCENT_TRANSPARENCY)
                            .texture(NO_TEXTURE)
                            .cull(DISABLE_CULLING)
                            .depthTest(LEQUAL_DEPTH_TEST)
                            .shader(COLOR_SHADER)
                            .texture(NO_TEXTURE)
                            .build(false));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

