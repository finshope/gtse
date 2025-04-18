package com.finshope.gtsecore.data.recipe;

import com.finshope.gtsecore.common.data.GTSEMachines;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.machines.GTMultiMachines;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

import static com.finshope.gtsecore.common.data.GTSERecipeTypes.NETHER_COLLECTOR_RECIPES;
import static com.finshope.gtsecore.common.data.GTSERecipeTypes.TREE_FARM_RECIPES;
import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.common.data.GTBiomeModifiers.RUBBER;
import static com.gregtechceu.gtceu.common.data.GTBlocks.RUBBER_LOG;
import static com.gregtechceu.gtceu.common.data.GTBlocks.RUBBER_SAPLING;
import static com.gregtechceu.gtceu.common.data.GTItems.STICKY_RESIN;
import static com.gregtechceu.gtceu.common.data.GTMaterials.NetherStar;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Water;
import static net.minecraft.world.item.Items.*;
import static net.minecraft.world.level.block.Blocks.BAMBOO_SAPLING;

public class NetherCollectorRecipeLoader {
    public static void init(Consumer<FinishedRecipe> provider) {
        NETHER_COLLECTOR_RECIPES.recipeBuilder("nether_collector_1").circuitMeta(1).duration(20 * 10).EUt(VA[EV]).chancedOutput(TagPrefix.dust, NetherStar, 1000, 2000).save(provider);

        createTreeFarmRecipe(provider, "tree_farm_oak", OAK_SAPLING, OAK_LOG, APPLE);
        createTreeFarmRecipe(provider, "tree_farm_birch", BIRCH_SAPLING, BIRCH_LOG, null);
        createTreeFarmRecipe(provider, "tree_farm_spruce", SPRUCE_SAPLING, SPRUCE_LOG, null);
        createTreeFarmRecipe(provider, "tree_farm_jungle", JUNGLE_SAPLING, JUNGLE_LOG, COCOA_BEANS);
        createTreeFarmRecipe(provider, "tree_farm_acacia", ACACIA_SAPLING, ACACIA_LOG, null);
        createTreeFarmRecipe(provider, "tree_farm_dark_oak", DARK_OAK_SAPLING, DARK_OAK_LOG, null);
        createTreeFarmRecipe(provider, "tree_farm_cherry", CHERRY_SAPLING, CHERRY_LOG, null);
        createTreeFarmRecipe(provider, "tree_farm_rubber", RUBBER_SAPLING, RUBBER_LOG, STICKY_RESIN);
        createTreeFarmRecipe(provider, "tree_farm_bamboo", null, BAMBOO, null);
        createTreeFarmRecipe(provider, "tree_farm_cactus", null, CACTUS, null);

        VanillaRecipeHelper.addShapedRecipe(provider, true, "gas_turbine_ev", GTSEMachines.GAS_TURBINE[EV].asStack(),
                "CRC", "RMR", "EWE", 'M', GTMachines.HULL[GTValues.EV].asStack(), 'E', GTItems.ELECTRIC_MOTOR_EV, 'R',
                new MaterialEntry(TagPrefix.rotor, GTMaterials.StainlessSteel), 'C', CustomTags.EV_CIRCUITS, 'W',
                new MaterialEntry(TagPrefix.cableGtSingle, GTMaterials.Aluminium));
        VanillaRecipeHelper.addShapedRecipe(provider, true, "gas_turbine_iv", GTSEMachines.GAS_TURBINE[IV].asStack(),
                "CRC", "RMR", "EWE", 'M', GTMachines.HULL[GTValues.IV].asStack(), 'E', GTItems.ELECTRIC_MOTOR_IV, 'R',
                new MaterialEntry(TagPrefix.rotor, GTMaterials.TungstenSteel), 'C', CustomTags.IV_CIRCUITS, 'W',
                new MaterialEntry(TagPrefix.cableGtSingle, GTMaterials.Tungsten));

        VanillaRecipeHelper.addShapedRecipe(provider, true, "diesel_generator_ev", GTSEMachines.COMBUSTION[EV].asStack(),
                "PCP", "EME", "GWG", 'M', GTMachines.HULL[GTValues.EV].asStack(), 'P', GTItems.ELECTRIC_PISTON_EV, 'E',
                GTItems.ELECTRIC_MOTOR_EV, 'C', CustomTags.EV_CIRCUITS, 'W',
                new MaterialEntry(TagPrefix.cableGtSingle, GTMaterials.Aluminium), 'G',
                new MaterialEntry(TagPrefix.gear, GTMaterials.StainlessSteel));
        VanillaRecipeHelper.addShapedRecipe(provider, true, "diesel_generator_iv", GTSEMachines.COMBUSTION[IV].asStack(),
                "PCP", "EME", "GWG", 'M', GTMachines.HULL[GTValues.IV].asStack(), 'P', GTItems.ELECTRIC_PISTON_IV, 'E',
                GTItems.ELECTRIC_MOTOR_IV, 'C', CustomTags.IV_CIRCUITS, 'W',
                new MaterialEntry(TagPrefix.cableGtSingle, GTMaterials.Tungsten), 'G',
                new MaterialEntry(TagPrefix.gear, GTMaterials.TungstenSteel));

        VanillaRecipeHelper.addShapedRecipe(provider, true, "plasma_turbine_luv",
                GTSEMachines.PLASMA_TURBINE[LuV].asStack(), "PSP", "SAS", "CSC", 'S',
                new MaterialEntry(TagPrefix.rotor, GTMaterials.RhodiumPlatedPalladium), 'P', CustomTags.LuV_CIRCUITS, 'A',
                GTMachines.HULL[GTValues.LuV].asStack(), 'C',
                new MaterialEntry(TagPrefix.pipeSmallFluid, GTMaterials.NiobiumTitanium));
        VanillaRecipeHelper.addShapedRecipe(provider, true, "plasma_turbine_zpm",
                GTSEMachines.PLASMA_TURBINE[ZPM].asStack(), "PSP", "SAS", "CSC", 'S',
                new MaterialEntry(TagPrefix.rotor, GTMaterials.NaquadahAlloy), 'P', CustomTags.ZPM_CIRCUITS, 'A',
                GTMachines.HULL[GTValues.ZPM].asStack(), 'C',
                new MaterialEntry(TagPrefix.pipeNormalFluid, GTMaterials.Polybenzimidazole));
        VanillaRecipeHelper.addShapedRecipe(provider, true, "plasma_turbine_uv",
                GTSEMachines.PLASMA_TURBINE[UV].asStack(), "PSP", "SAS", "CSC", 'S',
                new MaterialEntry(TagPrefix.rotor, GTMaterials.Darmstadtium), 'P', CustomTags.UV_CIRCUITS, 'A',
                GTMachines.HULL[GTValues.UV].asStack(), 'C',
                new MaterialEntry(TagPrefix.pipeLargeFluid, GTMaterials.Naquadah));
    }

    static void createTreeFarmRecipe(Consumer<FinishedRecipe> provider, String name, ItemLike sapling, ItemLike log, ItemLike fruit) {
        var builder = TREE_FARM_RECIPES.recipeBuilder(name)
                .circuitMeta(1)
                .duration(20 * 10)
                .EUt(VA[MV])
                .notConsumable(new ItemStack(sapling == null ? log : sapling, 1))
                .inputFluids(Water.getFluid(1000))
                .outputItems(log, 16);
        if (sapling != null) {
            builder.chancedOutput(new ItemStack(sapling, 1), 5000, 2000);
        }
        if (fruit != null) {
            builder.chancedOutput(new ItemStack(fruit, 8), 1000, 2000);
        }

        builder.save(provider);
    }
}
