package com.finshope.gtsecore.common.vanilla;

import com.finshope.gtsecore.GTSECore;

import net.minecraft.world.item.Items;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GTSECore.MOD_ID)
public class ItemProperties {

    public static void init() {}

    @SubscribeEvent
    public static void onFuelCheck(FurnaceFuelBurnTimeEvent event) {
        if (event.getItemStack().getItem() == Items.DIAMOND) {
            event.setBurnTime(512 * 10 * 20); // 512 items
        }
        if (event.getItemStack().getItem() == Items.DIAMOND_BLOCK) { // 检查是否为钻石块
            event.setBurnTime(5120 * 10 * 20); // 5120 items
        }
    }
}
