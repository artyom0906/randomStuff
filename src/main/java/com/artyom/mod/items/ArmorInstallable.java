package com.artyom.mod.items;

import java.util.UUID;

public interface ArmorInstallable {
    int getSlotsX();
    int getSlotsY();
    int getPosXInGrid();
    int getPosYInGrid();
    void setPosXInGrid(int x);
    void setPosYInGrid(int y);
}
