package com.artyom.mod.kavin.api;

import com.artyom.mod.RandomStuff;
import com.artyom.mod.kavin.tabs.provider.*;
import com.artyom.mod.tabs.provider.ArmorTabProvider;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry for tab providers.
 */
public class TabProviderRegistry {
    private static final Map<Identifier, TabProvider> TAB_PROVIDERS = new HashMap();

    public static final PlayerInventoryTabProvider PLAYER_INVENTORY_TAB_PROVIDER =
        (PlayerInventoryTabProvider) register(RandomStuff.identify("player_inventory_tab_provider"),
            new PlayerInventoryTabProvider());
    public static final ArmorTabProvider ARMOR_TAB_PROVIDER =
            (ArmorTabProvider) register(RandomStuff.identify("armor_tab_provider"),
                    new ArmorTabProvider());
    public static void init() {
    }

    public static TabProvider register(Identifier id, TabProvider tabProvider) {
        TAB_PROVIDERS.put(id, tabProvider);

        return tabProvider;
    }

    public static List<TabProvider> getTabProviders() {
        return new ArrayList(TAB_PROVIDERS.values());
    }
}
