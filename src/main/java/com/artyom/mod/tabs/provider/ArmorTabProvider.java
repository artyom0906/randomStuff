package com.artyom.mod.tabs.provider;

import com.artyom.mod.kavin.tabs.provider.TabProvider;
import com.artyom.mod.kavin.tabs.tab.PlayerInventoryTab;
import com.artyom.mod.kavin.tabs.tab.Tab;
import com.artyom.mod.tabs.ArmorTab;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.List;

public class ArmorTabProvider implements TabProvider {
    @Override
    public void addAvailableTabs(ClientPlayerEntity player, List<Tab> tabs) {
        for (int i = 0; i < tabs.size(); i++) {
            if (tabs.get(i) instanceof ArmorTab) {
                return;
            }
        }

        tabs.add(new ArmorTab());
    }
}
