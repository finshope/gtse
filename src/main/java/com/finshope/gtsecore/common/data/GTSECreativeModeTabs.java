package com.finshope.gtsecore.common.data;

import com.finshope.gtsecore.GTSECore;

import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;

import com.tterrag.registrate.util.entry.RegistryEntry;

import static com.finshope.gtsecore.api.registries.GTSERegistires.REGISTRATE;

public class GTSECreativeModeTabs {

    public static RegistryEntry<CreativeModeTab> GTSE = REGISTRATE.defaultCreativeTab(GTSECore.MOD_ID,
            builder -> builder
                    .displayItems(new GTCreativeModeTabs.RegistrateDisplayItemsGenerator(GTSECore.MOD_ID, REGISTRATE))
                    .icon(GTSEItems.COMPLETE_CARD::asStack)
                    .title(Component.literal("gtse core"))
                    .build())
            .register();

    public static void init() {}
}
