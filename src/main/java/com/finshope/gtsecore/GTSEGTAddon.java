package com.finshope.gtsecore;

import com.finshope.gtsecore.api.registries.GTSERegistires;
import com.finshope.gtsecore.common.data.GTSERecipes;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

@SuppressWarnings("unused")
@GTAddon
public class GTSEGTAddon implements IGTAddon {

    @Override
    public GTRegistrate getRegistrate() {
        return GTSERegistires.REGISTRATE;
    }

    @Override
    public void initializeAddon() {
        System.out.println("ExampleGTAddon initialized!");
        GTCEu.LOGGER.info("ExampleGTAddon initialized![]");
    }

    @Override
    public String addonModId() {
        return GTSECore.MOD_ID;
    }

    @Override
    public void registerTagPrefixes() {
        // CustomTagPrefixes.init();
        System.out.println("ExampleGTAddon TagPrefixes initialized!");
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        // CustomRecipes.init(provider);
        System.out.println("ExampleGTAddon Recipes initialized!");
        GTSERecipes.init(provider);
    }

    // If you have custom ingredient types, uncomment this & change to match your capability.
    // KubeJS WILL REMOVE YOUR RECIPES IF THESE ARE NOT REGISTERED.
    /*
     * public static final ContentJS<Double> PRESSURE_IN = new ContentJS<>(NumberComponent.ANY_DOUBLE,
     * GregitasRecipeCapabilities.PRESSURE, false);
     * public static final ContentJS<Double> PRESSURE_OUT = new ContentJS<>(NumberComponent.ANY_DOUBLE,
     * GregitasRecipeCapabilities.PRESSURE, true);
     * 
     * @Override
     * public void registerRecipeKeys(KJSRecipeKeyEvent event) {
     * event.registerKey(CustomRecipeCapabilities.PRESSURE, Pair.of(PRESSURE_IN, PRESSURE_OUT));
     * }
     */
}
