package com.finshope.gtsecore.common.data;

import com.finshope.gtsecore.data.recipe.MiscRecipeLoader;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

public class GTSERecipes {
    public static void init(Consumer<FinishedRecipe> provider) {
        MiscRecipeLoader.init(provider);
    }
}
