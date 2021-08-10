package com.artyom.mod.registry;


import com.artyom.mod.RandomStuff;
import com.artyom.mod.items.BasicExoskeleton;
import com.artyom.mod.items.BasicReactor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class ModItems {
    public static final Item RUBY = new Item(new Item.Settings().group(ItemGroup.MATERIALS));
    public static final Item BASIC_REACTOR = new BasicReactor(4, 4, new Item.Settings().group(ItemGroup.MISC).rarity(Rarity.RARE));
    public static final Item BASIC_EXOSKELETON = new BasicExoskeleton(2, 4, new Item.Settings().group(ItemGroup.MISC).rarity(Rarity.RARE));
    public static void registryItems(){
        Registry.register(Registry.ITEM, new Identifier(RandomStuff.MOD_ID, "ruby"), RUBY);
        Registry.register(Registry.ITEM, RandomStuff.identify("fusion_reactor_equipment"), BASIC_REACTOR);
        Registry.register(Registry.ITEM, RandomStuff.identify("exoskeleton_equipment"), BASIC_EXOSKELETON);
    }

}