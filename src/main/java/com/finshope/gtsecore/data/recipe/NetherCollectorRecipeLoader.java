package com.finshope.gtsecore.data.recipe;

import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

import static com.finshope.gtsecore.common.data.GTSERecipeTypes.NETHER_COLLECTOR_RECIPES;
import static com.gregtechceu.gtceu.api.GTValues.EV;
import static com.gregtechceu.gtceu.api.GTValues.VA;
import static com.gregtechceu.gtceu.common.data.GTMaterials.NetherStar;
import static net.minecraft.world.item.Items.NETHER_STAR;

public class NetherCollectorRecipeLoader {
    public  static  void init(Consumer<FinishedRecipe> provider) {
        NETHER_COLLECTOR_RECIPES.recipeBuilder("nether_collector_1")
                .circuitMeta(1)
                .duration(20 * 10)
                .EUt(VA[EV])
                .chancedOutput(TagPrefix.dust, NetherStar, 1000, 2000)
                .save(provider);
    }
}
