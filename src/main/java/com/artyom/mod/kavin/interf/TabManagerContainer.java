package com.artyom.mod.kavin.interf;

import com.artyom.mod.kavin.tabs.TabManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Interface for holding the tab manager.
 * Gets injected into {@link net.minecraft.client.MinecraftClient}.
 */
@Environment(EnvType.CLIENT)
public interface TabManagerContainer {
    TabManager getTabManager();
}
