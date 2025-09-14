package com.finshope.gtsecore.common.data;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;

import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ELECTRIC;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.MULTIBLOCK;
import static com.lowdragmc.lowdraglib.gui.texture.ProgressTexture.FillDirection.LEFT_TO_RIGHT;

public class GTSERecipeTypes {

    public static final GTRecipeType NETHER_COLLECTOR_RECIPES = GTRecipeTypes.register("nether_collector", ELECTRIC)
            .setMaxIOSize(3, 6, 1, 0).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_MACERATE, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.MOTOR);

    public static final GTRecipeType TREE_FARM_RECIPES = GTRecipeTypes.register("tree_farm", MULTIBLOCK)
            .setMaxIOSize(3, 6, 1, 0).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_MACERATE, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.SCIENCE);

    public static final GTRecipeType HARVESTER_RECIPES = GTRecipeTypes.register("harvester", ELECTRIC)
            .setMaxIOSize(3, 6, 2, 0).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_MACERATE, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.SCIENCE);

    public static final GTRecipeType MOB_SIMULATOR_RECIPES = GTRecipeTypes.register("mob_simulator", ELECTRIC)
            .setMaxIOSize(3, 6, 2, 0).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_MACERATE, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.SCIENCE);

    public static final GTRecipeType STEAM_VOID_MINER_RECIPES = GTRecipeTypes.register("steam_void_miner", MULTIBLOCK)
            .setMaxIOSize(1, 6, 1, 0).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_MACERATE, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.SCIENCE);

    public static final GTRecipeType LARGE_FISHER_RECIPES = GTRecipeTypes.register("large_fisher", MULTIBLOCK)
            .setMaxIOSize(2, 16, 0, 0).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_MACERATE, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.SCIENCE);

    public final static GTRecipeType LARGE_GAS_COLLECTOR_RECIPES = GTRecipeTypes
            .register("large_gas_collector", ELECTRIC)
            .setMaxIOSize(1, 0, 0, 1).setEUIO(IO.IN)
            .setSlotOverlay(false, false, GuiTextures.INT_CIRCUIT_OVERLAY)
            .setSlotOverlay(true, true, GuiTextures.CENTRIFUGE_OVERLAY)
            .setProgressBar(GuiTextures.PROGRESS_BAR_GAS_COLLECTOR, LEFT_TO_RIGHT)
            .setMaxTooltips(4)
            .setOffsetVoltageText(true)
            .setSound(GTSoundEntries.COOLING);

    public static void init() {}
}
