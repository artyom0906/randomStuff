package com.artyom.mod.kavin.tabs.provider;

import com.artyom.mod.kavin.tabs.tab.PlayerInventoryTab;
import com.artyom.mod.kavin.tabs.tab.Tab;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.List;

public class PlayerInventoryTabProvider implements TabProvider {
    @Override
    public void addAvailableTabs(ClientPlayerEntity player, List<Tab> tabs) {
        for (int i = 0; i < tabs.size(); i++) {
            if (tabs.get(i) instanceof PlayerInventoryTab) {
                return;
            }
        }

        tabs.add(new PlayerInventoryTab());
    }
}
