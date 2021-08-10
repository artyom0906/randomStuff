package com.artyom.mod.items;

import net.minecraft.item.Item;

public class BasicExoskeleton extends Item implements ArmorInstallable{

    private final int slotsX;
    private final int slotsY;
    private int slotX = 0;
    private int slotY = 0;

    public BasicExoskeleton(int slotsX, int slotsY, Settings settings) {
        super(settings.maxCount(1));
        this.slotsX = slotsX;
        this.slotsY = slotsY;
    }

    @Override
    public int getSlotsX() {
        return slotsX;
    }

    @Override
    public int getSlotsY() {
        return slotsY;
    }

    @Override
    public int getPosXInGrid() {
        return slotX;
    }

    @Override
    public int getPosYInGrid() {
        return slotY;
    }

    @Override
    public void setPosXInGrid(int x) {
        slotX = x;
    }

    @Override
    public void setPosYInGrid(int y) {
        slotY = y;
    }
}