package com.finshope.gtsecore.data.recipe;

import com.finshope.gtsecore.common.data.GTSEMachines;
import com.finshope.gtsecore.common.machine.multiblock.electric.TreeFarmMachine;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

import static appeng.core.definitions.AEItems.*;
import static com.finshope.gtsecore.common.data.GTSEMachines.*;
import static com.finshope.gtsecore.common.data.GTSERecipeTypes.*;
import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTBlocks.RUBBER_LOG;
import static com.gregtechceu.gtceu.common.data.GTBlocks.RUBBER_SAPLING;
import static com.gregtechceu.gtceu.common.data.GTItems.SHAPE_EMPTY;
import static com.gregtechceu.gtceu.common.data.GTItems.STICKY_RESIN;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.FORMING_PRESS_RECIPES;
import static com.gregtechceu.gtceu.common.data.machines.GCYMMachines.BLAST_ALLOY_SMELTER;
import static com.gregtechceu.gtceu.common.data.machines.GTMultiMachines.PYROLYSE_OVEN;
import static com.gregtechceu.gtceu.data.recipe.GTCraftingComponents.*;
import static com.gregtechceu.gtceu.data.recipe.GTCraftingComponents.CABLE;
import static com.gregtechceu.gtceu.data.recipe.GTCraftingComponents.PISTON;
import static com.gregtechceu.gtceu.data.recipe.misc.MetaTileEntityLoader.registerMachineRecipe;
import static net.minecraft.world.item.Items.*;

public class MiscRecipeLoader {
    public static void init(Consumer<FinishedRecipe> provider) {


        createCustomRecipes(provider);
        createMultiblockRecipes(provider);
        createGeneratorRecipes(provider);
        createAE2Recipes(provider);
    }

    static void createCustomRecipes(Consumer<FinishedRecipe> provider) {
        registerMachineRecipe(provider, GTSEMachines.NETHER_COLLECTOR, "WFW", "PMP", "CFC", 'M', HULL, 'P', PISTON, 'F',
                GTItems.ITEM_FILTER, 'C', CIRCUIT, 'W', CABLE_DOUBLE);
        registerMachineRecipe(provider, HARVESTER, "HCH", "PMP", "HCH", 'M', HULL, 'P', PUMP, 'C', CIRCUIT, 'H', CABLE);

        NETHER_COLLECTOR_RECIPES.recipeBuilder("nether_collector_1").circuitMeta(1).duration(20 * 10).EUt(VA[EV]).chancedOutput(TagPrefix.dust, NetherStar, 1000, 2000).save(provider);
        NETHER_COLLECTOR_RECIPES.recipeBuilder("nether_collector_2").circuitMeta(2).duration(20 * 10).EUt(VA[EV]).chancedOutput(dustTiny, Netherite, 1000, 2000).save(provider);
        HARVESTER_RECIPES
                .recipeBuilder("harvest")
                .circuitMeta(1)
                .duration(10 * 20)
                .inputFluids(Water.getFluid(100))
                .EUt(1).save(provider);

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
    }

    static void createMultiblockRecipes(Consumer<FinishedRecipe> provider) {
        VanillaRecipeHelper.addShapedRecipe(provider, true, "tree_farm",
                TREE_FARM.asStack(), "PCP", "SAS", "PCP", 'S',
                new MaterialEntry(TagPrefix.rotor, GTMaterials.Steel), 'P', CustomTags.LV_CIRCUITS, 'A',
                GTMachines.HULL[GTValues.MV].asStack(), 'C', GTItems.ELECTRIC_PUMP_LV.asStack());

        ASSEMBLER_RECIPES.recipeBuilder("industrial_pyrolyse_oven")
                .duration(200 * 10).EUt(VA[EV])
                .circuitMeta(16)
                .inputItems(PYROLYSE_OVEN.asStack(), 16)
                .outputItems(INDUSTRIAL_PYROLYSE_OVEN.asStack())
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder("industrial_alloy_blast_smelter")
                .duration(200 * 10).EUt(VA[LuV])
                .circuitMeta(16)
                .inputItems(BLAST_ALLOY_SMELTER.asStack(), 16)
                .outputItems(INDUSTRIAL_BLAST_ALLOY_SMELTER.asStack())
                .save(provider);
    }

    static void createGeneratorRecipes(Consumer<FinishedRecipe> provider) {
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

    static void createAE2Recipes(Consumer<FinishedRecipe> provider) {
        FORMING_PRESS_RECIPES.recipeBuilder("ae2_processor_calculation_print")
                .duration(20).EUt(VA[HV])
                .notConsumable(CALCULATION_PROCESSOR_PRESS.asItem())
                .inputItems(gem, CertusQuartz)
                .outputItems(CALCULATION_PROCESSOR_PRINT.asItem())
                .save(provider);

        FORMING_PRESS_RECIPES.recipeBuilder("ae2_processor_engineering_print")
                .duration(20).EUt(VA[HV])
                .notConsumable(ENGINEERING_PROCESSOR_PRESS.asItem())
                .inputItems(gem, Diamond)
                .outputItems(ENGINEERING_PROCESSOR_PRINT.asItem())
                .save(provider);

        FORMING_PRESS_RECIPES.recipeBuilder("ae2_processor_logic_print")
                .duration(20).EUt(VA[HV])
                .notConsumable(LOGIC_PROCESSOR_PRESS.asItem())
                .inputItems(ingot, Gold)
                .outputItems(LOGIC_PROCESSOR_PRINT.asItem())
                .save(provider);

        FORMING_PRESS_RECIPES.recipeBuilder("ae2_processor_silicon_print")
                .duration(20).EUt(VA[HV])
                .notConsumable(SILICON_PRESS.asItem())
                .inputItems(ingot, Silicon)
                .outputItems(SILICON_PRINT.asItem())
                .save(provider);

        FORMING_PRESS_RECIPES.recipeBuilder("ae2_processor_silicon_print2")
                .duration(20).EUt(VA[HV])
                .notConsumable(SILICON_PRESS.asItem())
                .inputItems(SILICON.asItem())
                .outputItems(SILICON_PRINT.asItem())
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder("ae2_processor_calculation")
                .duration(1).EUt(1)
                .inputItems(CALCULATION_PROCESSOR_PRINT.asItem())
                .inputItems(SILICON_PRINT.asItem())
                .inputItems(dust, Redstone)
                .outputItems(CALCULATION_PROCESSOR.asItem())
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder("ae2_processor_engineering")
                .duration(1).EUt(1)
                .inputItems(ENGINEERING_PROCESSOR_PRINT.asItem())
                .inputItems(SILICON_PRINT.asItem())
                .inputItems(dust, Redstone)
                .outputItems(ENGINEERING_PROCESSOR.asItem())
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder("ae2_processor_logic")
                .duration(1).EUt(1)
                .inputItems(LOGIC_PROCESSOR_PRINT.asItem())
                .inputItems(SILICON_PRINT.asItem())
                .inputItems(dust, Redstone)
                .outputItems(LOGIC_PROCESSOR.asItem())
                .save(provider);
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
