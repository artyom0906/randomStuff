package com.artyom.mod.inventories;
import com.artyom.mod.items.ArmorInstallable;
import com.artyom.mod.items.BasicReactor;
import com.artyom.mod.screen.ArmorScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

@Environment(EnvType.CLIENT)
public class ArmorSlot extends Slot {

    private final int IdX;
    private final int IdY;
    private final Logger logger = LoggerFactory.getLogger(ArmorSlot.class);
    private boolean filled = false;

    public ArmorSlot(Inventory inventory, int index, int x, int y, int idX, int idY) {
        super(inventory, index, x, y);
        this.IdX = idX;
        this.IdY = idY;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        if(stack.getItem() instanceof ArmorInstallable armorInstallable && ((ArmorInventory)inventory).getContainer()!=null) {
            if(this.IdX==armorInstallable.getPosXInGrid() && this.IdY==armorInstallable.getPosYInGrid())return true;
            if(this.IdX+armorInstallable.getSlotsX()>((ArmorInventory) inventory).width() || this.IdY+armorInstallable.getSlotsY()>((ArmorInventory) inventory).height()){
                logger.info("overflow");
                return false;
            }
            for(int y = this.IdY; y<this.IdY+armorInstallable.getSlotsY(); y++){
                for (int x = this.IdX; x<this.IdX+armorInstallable.getSlotsX(); x++){
                    if(x==this.IdX && y==this.IdY) continue;
                    try {
                        Slot slot = MinecraftClient.getInstance().player.currentScreenHandler.slots.get(x + y * ((ArmorInventory) (inventory)).width());
                        ArmorSlot armorSlot = (ArmorSlot) slot;
                        if (armorSlot.filled){
                            logger.info("filled: ({};{})", x, y);
                            return false;
                        }
                    }catch (Exception e){
                        logger.info("err");
                        return false;
                    }
                }
            }
            if(this.IdY==0 && this.IdX==0) return true;
            /*for (int y = 0; y <= this.IdY; ++y) {
                for (int x = 0; x <= this.IdX; ++x) {
                    Slot slot = MinecraftClient.getInstance().player.currentScreenHandler.slots.get(x + y * ((ArmorInventory) (inventory)).width());
                    if(slot.hasStack() && slot.getStack().getItem() instanceof ArmorInstallable itemInSlot){
                        if(itemInSlot.getSlotsX()+x>this.IdX && itemInSlot.getSlotsY()+y>this.IdY){
                            logger.info("{}", itemInSlot);
                            return false;
                        }
                    }
                }
            }*/
            armorInstallable.setPosXInGrid(this.IdX);
            armorInstallable.setPosYInGrid(this.IdY);
            return true;
        }
        return false;
    }

    @Override
    public void setStack(ItemStack stack) {
        if(stack.getItem() instanceof ArmorInstallable armorInstallable && ((ArmorInventory)inventory).getContainer()!=null) {
            for (int y = this.IdY; y < this.IdY + armorInstallable.getSlotsY(); y++) {
                for (int x = this.IdX; x < this.IdX + armorInstallable.getSlotsX(); x++) {
                    //logger.info("fill: ({};{})", x, y);
                    Slot slot = MinecraftClient.getInstance().player.currentScreenHandler.slots.get(x + y * ((ArmorInventory) (inventory)).width());
                    ArmorSlot armorSlot = (ArmorSlot) slot;
                    ((ArmorSlot) slot).filled = true;
                }
            }
        }
        super.setStack(stack);
    }

    @Override
    public ItemStack takeStack(int amount) {
        if(this.inventory.getStack(id).getItem() instanceof ArmorInstallable armorInstallable  && ((ArmorInventory)inventory).getContainer()!=null) {
            for (int y = this.IdY; y < this.IdY + armorInstallable.getSlotsY(); y++) {
                for (int x = this.IdX; x < this.IdX + armorInstallable.getSlotsX(); x++) {
                    //logger.info("un fill: ({};{})", x, y);
                    Slot slot = MinecraftClient.getInstance().player.currentScreenHandler.slots.get(x + y * ((ArmorInventory) (inventory)).width());
                    ArmorSlot armorSlot = (ArmorSlot) slot;
                    ((ArmorSlot) slot).filled = false;
                }
            }
        }
        return super.takeStack(amount);
    }
}