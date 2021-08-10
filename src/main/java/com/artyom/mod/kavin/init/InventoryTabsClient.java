package com.artyom.mod.kavin.init;

import com.artyom.mod.kavin.api.TabProviderRegistry;
import com.artyom.mod.kavin.interf.TabManagerContainer;
import com.artyom.mod.kavin.network.SyncClientConfigS2C;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class InventoryTabsClient implements ClientModInitializer {
    public static final KeyBinding NEXT_TAB_KEY_BIND =
        KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "inventorytabs.key.next_tab",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_TAB,
            "key.categories.inventory"
        ));

    public static boolean serverDoSightCheckFlag = true;

    @Override
    public void onInitializeClient() {
        TabProviderRegistry.init();

        SyncClientConfigS2C.register();

        // Handle state of tab managerInventoryTabsClient
        ClientTickEvents.START_WORLD_TICK.register(world -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.currentScreen != null) {
                TabManagerContainer tabManagerContainer = (TabManagerContainer) client;

                tabManagerContainer.getTabManager().update();
            }
        });
    }
}
