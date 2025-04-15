package com.finshope.gtsecore.common.data;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;

import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ELECTRIC;
import static com.lowdragmc.lowdraglib.gui.texture.ProgressTexture.FillDirection.LEFT_TO_RIGHT;

public class GTSERecipeTypes {

    public static final GTRecipeType NETHER_COLLECTOR_RECIPES = GTRecipeTypes.register("nether_collector", ELECTRIC).setMaxIOSize(3, 6, 1, 0).setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_MACERATE, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.MOTOR);

    public static void init() {

    }
}
