package com.finshope.gtsecore.data.recipe;

import com.finshope.gtsecore.common.data.GTSEMachines;
import com.finshope.gtsecore.common.data.GTSEMultiMachines;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.recipe.chance.logic.ChanceLogic;
import com.gregtechceu.gtceu.common.data.*;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.Consumer;

import static appeng.core.definitions.AEItems.*;
import static com.finshope.gtsecore.common.data.GTSEMachines.*;
import static com.finshope.gtsecore.common.data.GTSEMultiMachines.*;
import static com.finshope.gtsecore.common.data.GTSERecipeTypes.*;
import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GCYMBlocks.CASING_INDUSTRIAL_STEAM;
import static com.gregtechceu.gtceu.common.data.GTBlocks.RUBBER_LOG;
import static com.gregtechceu.gtceu.common.data.GTBlocks.RUBBER_SAPLING;
import static com.gregtechceu.gtceu.common.data.GTItems.*;
import static com.gregtechceu.gtceu.common.data.GTItems.NAQUADAH_WAFER;
import static com.gregtechceu.gtceu.common.data.GTMachines.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;
import static com.gregtechceu.gtceu.common.data.machines.GCYMMachines.BLAST_ALLOY_SMELTER;
import static com.gregtechceu.gtceu.common.data.machines.GTMultiMachines.*;
import static com.gregtechceu.gtceu.data.recipe.GTCraftingComponents.*;
import static com.gregtechceu.gtceu.data.recipe.GTCraftingComponents.HULL;
import static com.gregtechceu.gtceu.data.recipe.GTCraftingComponents.PISTON;
import static com.gregtechceu.gtceu.data.recipe.GTCraftingComponents.PUMP;
import static com.gregtechceu.gtceu.data.recipe.misc.MetaTileEntityLoader.registerMachineRecipe;
import static net.minecraft.tags.ItemTags.WOOL;
import static net.minecraft.world.item.Items.*;

public class MiscRecipeLoader {

    public static void init(Consumer<FinishedRecipe> provider) {
        createCustomRecipes(provider);
        createSteamMachineRecipes(provider);
        createMultiblockRecipes(provider);
        createGeneratorRecipes(provider);
        createMobSimulatorRecipes(provider);
        createSteamVoidMinerRecipe(provider);
        createLargeFisherRecipe(provider);
        createLargeGasCollectorRecipe(provider);
        createSolarPanelRecipe(provider);

        if (GTCEu.Mods.isAE2Loaded()) {
            createAE2Recipes(provider);
        }
    }

    private static void createSolarPanelRecipe(Consumer<FinishedRecipe> provider) {
        // solar panel
        VanillaRecipeHelper.addShapedRecipe(provider, "se_solar_panel_lv", COVER_SOLAR_PANEL_LV.asStack(), "WGW", "CAC",
                "SPS", 'W', SILICON_WAFER.asStack(), 'G', GTBlocks.CASING_TEMPERED_GLASS.asStack(), 'C',
                CustomTags.MV_CIRCUITS, 'P', new MaterialEntry(plate, GalliumArsenide), 'A',
                new MaterialEntry(wireGtDouble, ManganesePhosphide), 'S', new MaterialEntry(plateDouble, Silver));

        VanillaRecipeHelper.addShapedRecipe(provider, "se_solar_panel_mv", COVER_SOLAR_PANEL_MV.asStack(), "WGW", "CAC",
                "SPS", 'W', SILICON_WAFER.asStack(), 'G', GTBlocks.CASING_TEMPERED_GLASS.asStack(), 'C',
                CustomTags.HV_CIRCUITS, 'P', new MaterialEntry(plate, GalliumArsenide), 'A',
                new MaterialEntry(wireGtDouble, MagnesiumDiboride), 'S', COVER_SOLAR_PANEL_LV.asStack());

        VanillaRecipeHelper.addShapedRecipe(provider, "se_solar_panel_hv", COVER_SOLAR_PANEL_HV.asStack(), "WGW", "CAC",
                "SPS", 'W', PHOSPHORUS_WAFER.asStack(), 'G', GTBlocks.CASING_LAMINATED_GLASS.asStack(), 'C',
                CustomTags.EV_CIRCUITS, 'P', new MaterialEntry(plateDouble, GalliumArsenide), 'A',
                new MaterialEntry(wireGtDouble, MercuryBariumCalciumCuprate), 'S', COVER_SOLAR_PANEL_MV.asStack());

        VanillaRecipeHelper.addShapedRecipe(provider, "se_solar_panel_ev", COVER_SOLAR_PANEL_EV.asStack(), "WGW", "CAC",
                "SPS", 'W', PHOSPHORUS_WAFER.asStack(), 'G', GTBlocks.CASING_LAMINATED_GLASS.asStack(), 'C',
                CustomTags.IV_CIRCUITS, 'P', new MaterialEntry(plateDouble, GalliumArsenide), 'A',
                new MaterialEntry(wireGtDouble, UraniumTriplatinum), 'S', COVER_SOLAR_PANEL_HV.asStack());

        VanillaRecipeHelper.addShapedRecipe(provider, "se_solar_panel_iv", COVER_SOLAR_PANEL_IV.asStack(), "WGW", "CAC",
                "SPS", 'W', NAQUADAH_WAFER.asStack(), 'G', GTBlocks.FUSION_GLASS.asStack(), 'C',
                CustomTags.LuV_CIRCUITS, 'P', new MaterialEntry(plate, IndiumGalliumPhosphide), 'A',
                new MaterialEntry(wireGtDouble, SamariumIronArsenicOxide), 'S', COVER_SOLAR_PANEL_EV.asStack());

        VanillaRecipeHelper.addShapedRecipe(provider, "se_solar_panel_luv", COVER_SOLAR_PANEL_LuV.asStack(), "WGW",
                "CAC",
                "SPS", 'W', NAQUADAH_WAFER.asStack(), 'G', GTBlocks.FUSION_GLASS.asStack(), 'C',
                CustomTags.ZPM_CIRCUITS, 'P', new MaterialEntry(plate, IndiumGalliumPhosphide), 'A',
                new MaterialEntry(wireGtDouble, IndiumTinBariumTitaniumCuprate), 'S', COVER_SOLAR_PANEL_IV.asStack());

        VanillaRecipeHelper.addShapedRecipe(provider, "se_solar_panel_zpm", COVER_SOLAR_PANEL_ZPM.asStack(), "WGW",
                "CAC",
                "SPS", 'W', NEUTRONIUM_WAFER.asStack(), 'G', GTBlocks.FUSION_GLASS.asStack(), 'C',
                CustomTags.UV_CIRCUITS, 'P', new MaterialEntry(plateDouble, IndiumGalliumPhosphide), 'A',
                new MaterialEntry(wireGtDouble, UraniumRhodiumDinaquadide), 'S', COVER_SOLAR_PANEL_LuV.asStack());

        VanillaRecipeHelper.addShapedRecipe(provider, "se_solar_panel_uv", COVER_SOLAR_PANEL_UV.asStack(), "WGW", "CAC",
                "SPS", 'W', NEUTRONIUM_WAFER.asStack(), 'G', GTBlocks.FUSION_GLASS.asStack(), 'C',
                CustomTags.UHV_CIRCUITS, 'P', new MaterialEntry(plateDouble, IndiumGalliumPhosphide), 'A',
                new MaterialEntry(wireGtDouble, EnrichedNaquadahTriniumEuropiumDuranide), 'S',
                COVER_SOLAR_PANEL_ZPM.asStack());
    }

    private static void createLargeGasCollectorRecipe(Consumer<FinishedRecipe> provider) {
        // large gas collector
        VanillaRecipeHelper.addShapedRecipe(provider, true, "large_gas_collector",
                LARGE_GAS_COLLECTOR.asStack(), "PCP", "FGF", "PCP", 'C',
                CustomTags.ZPM_CIRCUITS, 'P', ELECTRIC_PUMP_LuV.asStack(), 'F',
                GTItems.FLUID_FILTER, 'G', GAS_COLLECTOR[LuV].asStack());
        LARGE_GAS_COLLECTOR_RECIPES.recipeBuilder("air")
                .circuitMeta(1)
                .outputFluids(Air.getFluid(10000))
                .duration(200).EUt(16).save(provider);

        LARGE_GAS_COLLECTOR_RECIPES.recipeBuilder("nether_air")
                .circuitMeta(2)
                .outputFluids(NetherAir.getFluid(10000))
                .duration(200).EUt(64).save(provider);

        LARGE_GAS_COLLECTOR_RECIPES.recipeBuilder("ender_air")
                .circuitMeta(3)
                .outputFluids(EnderAir.getFluid(10000))
                .duration(200).EUt(256).save(provider);
        LARGE_GAS_COLLECTOR_RECIPES.recipeBuilder("liquid_air")
                .circuitMeta(4)
                .outputFluids(LiquidAir.getFluid(10000))
                .duration(200).EUt(64).save(provider);
        LARGE_GAS_COLLECTOR_RECIPES.recipeBuilder("liquid_nether_air")
                .circuitMeta(5)
                .outputFluids(LiquidNetherAir.getFluid(10000))
                .duration(200).EUt(256).save(provider);
        LARGE_GAS_COLLECTOR_RECIPES.recipeBuilder("liquid_ender_air")
                .circuitMeta(6)
                .outputFluids(LiquidEnderAir.getFluid(10000))
                .duration(200).EUt(1024).save(provider);
    }

    static void createCustomRecipes(Consumer<FinishedRecipe> provider) {
        registerMachineRecipe(provider, GTSEMachines.NETHER_COLLECTOR, "WFW", "PMP", "CFC", 'M', HULL, 'P', PISTON, 'F',
                GTItems.ITEM_FILTER, 'C', CIRCUIT, 'W', CABLE_DOUBLE);
        registerMachineRecipe(provider, HARVESTER, "HCH", "PMP", "HCH", 'M', HULL, 'P', PUMP, 'C', CIRCUIT, 'H', CABLE);
        registerMachineRecipe(provider, MOB_SIMULATOR, "CEC", "SMS", "CEC", 'M', HULL, 'E', EMITTER, 'C',
                CIRCUIT, 'S', SENSOR);

        VanillaRecipeHelper.addShapedRecipe(provider, true, "processing_array",
                GTSEMultiMachines.PROCESSING_ARRAY[GTValues.IV].asStack(), "COC", "RHR", "CPC", 'C',
                CustomTags.IV_CIRCUITS, 'O', GTItems.TOOL_DATA_ORB.asStack(), 'R', GTItems.ROBOT_ARM_EV.asStack(), 'P',
                new MaterialEntry(pipeLargeFluid, StainlessSteel), 'H', GTMachines.HULL[GTValues.EV].asStack());
        VanillaRecipeHelper.addShapedRecipe(provider, true, "advanced_processing_array",
                GTSEMultiMachines.PROCESSING_ARRAY[GTValues.LuV].asStack(), "RCR", "SPE", "HNH", 'R',
                GTItems.ROBOT_ARM_LuV.asStack(), 'C', CustomTags.ZPM_CIRCUITS, 'S', GTItems.SENSOR_LuV, 'P',
                GTSEMultiMachines.PROCESSING_ARRAY[IV].asStack(), 'E', GTItems.EMITTER_LuV.asStack(), 'H',
                new MaterialEntry(plate, GTMaterials.HSSE), 'N',
                new MaterialEntry(pipeLargeFluid, GTMaterials.Naquadah));

        // vanilla recipe
        FORGE_HAMMER_RECIPES
                .recipeBuilder("hammer")
                .EUt(VA[LV])
                .duration(1)
                .inputItems(new ItemStack(Blocks.MAGMA_BLOCK))
                .outputItems(new ItemStack(MAGMA_CREAM, 4))
                .save(provider);

        // add chemical bath cool down recipe for magnesium diboride
        CHEMICAL_BATH_RECIPES
                .recipeBuilder("magnesium_diboride_cool_down")
                .EUt(VA[MV])
                .duration(20 * 20)
                .inputItems(ingotHot, MagnesiumDiboride, 1)
                .inputFluids(Water.getFluid(100))
                .outputItems(ingot, MagnesiumDiboride)
                .save(provider);

        CHEMICAL_BATH_RECIPES
                .recipeBuilder("magnesium_diboride_cool_down_distilled_water")
                .EUt(VA[MV])
                .inputItems(ingotHot, MagnesiumDiboride)
                .inputFluids(DistilledWater.getFluid(100))
                .outputItems(ingot, MagnesiumDiboride)
                .duration(250)
                .save(provider);

        // add crafting table recipe for hatch
        for (var machine : GTMachines.FLUID_IMPORT_HATCH) {
            if (machine == null) continue;
            int tier = machine.getTier();
            VanillaRecipeHelper.addShapedRecipe(provider, true,
                    "fluid_import_hatch_" + VN[tier].toLowerCase(Locale.ROOT),
                    machine.asStack(), "D  ", "H  ", "   ", 'H', HULL.get(tier), 'D', DRUM.get(tier));
        }
        for (var machine : GTMachines.FLUID_EXPORT_HATCH) {
            if (machine == null) continue;
            int tier = machine.getTier();
            VanillaRecipeHelper.addShapedRecipe(provider, true,
                    "fluid_export_hatch_" + VN[tier].toLowerCase(Locale.ROOT),
                    machine.asStack(), "H  ", "D  ", "   ", 'H', HULL.get(tier), 'D', DRUM.get(tier));
        }
        for (var machine : GTMachines.ITEM_IMPORT_BUS) {
            if (machine == null) continue;
            int tier = machine.getTier();
            VanillaRecipeHelper.addShapedRecipe(provider, true,
                    "item_import_hatch_" + VN[tier].toLowerCase(Locale.ROOT),
                    machine.asStack(), "C  ", "H  ", "   ", 'H', HULL.get(tier), 'C', CRATE.get(tier));
        }
        for (var machine : GTMachines.ITEM_EXPORT_BUS) {
            if (machine == null) continue;
            int tier = machine.getTier();
            VanillaRecipeHelper.addShapedRecipe(provider, true,
                    "item_export_hatch_" + VN[tier].toLowerCase(Locale.ROOT),
                    machine.asStack(), "H  ", "C  ", "   ", 'H', HULL.get(tier), 'C', CRATE.get(tier));
        }

        NETHER_COLLECTOR_RECIPES.recipeBuilder("nether_collector_1").circuitMeta(1).duration(20 * 10).EUt(VA[EV])
                .chancedOutput(TagPrefix.dust, NetherStar, 1000, 2000).save(provider);
        NETHER_COLLECTOR_RECIPES.recipeBuilder("nether_collector_2").circuitMeta(2).duration(20 * 10).EUt(VA[EV])
                .chancedOutput(dust, Netherite, 500, 500)
                .chancedOutput(dustSmall, Netherite, 1000, 1000)
                .chancedOutput(dustTiny, Netherite, 2000, 2000)
                .save(provider);
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

    static void createSteamMachineRecipes(Consumer<FinishedRecipe> provider) {
        VanillaRecipeHelper.addShapedRecipe(provider, true, "casing_industrial_steam",
                GCYMBlocks.CASING_INDUSTRIAL_STEAM.asStack(ConfigHolder.INSTANCE.recipes.casingsPerCraft), "PhP", "PBP",
                "PwP", 'P', new MaterialEntry(TagPrefix.plate, Steel), 'B',
                new ItemStack(Blocks.BRICKS));
        VanillaRecipeHelper.addShapedRecipe(provider, true, "steam_centrifuge",
                GTSEMultiMachines.STEAM_CENTRIFUGE.asStack(),
                "CGC",
                "FMF", "CGC", 'F', new MaterialEntry(rotor, Bronze), 'C', GTBlocks.CASING_STEEL_SOLID.asStack(),
                'M', CASING_INDUSTRIAL_STEAM.asStack(), 'G',
                new MaterialEntry(gear, Invar));
        VanillaRecipeHelper.addShapedRecipe(provider, true, "steam_ore_washer",
                GTSEMultiMachines.STEAM_ORE_WASHER.asStack(),
                "CGC",
                "FMF", "CGC", 'F', new MaterialEntry(rotor, Steel), 'C', GTBlocks.CASING_STEEL_SOLID.asStack(),
                'M', CASING_INDUSTRIAL_STEAM.asStack(), 'G',
                new MaterialEntry(pipeLargeFluid, Potin));
        VanillaRecipeHelper.addShapedRecipe(provider, true, "steam_mixer", GTSEMultiMachines.STEAM_MIXER.asStack(),
                "CGC",
                "FMF", "CGC", 'F', new MaterialEntry(rotor, Steel), 'C', GTBlocks.CASING_STEEL_SOLID.asStack(),
                'M', CASING_INDUSTRIAL_STEAM.asStack(), 'G',
                new MaterialEntry(rotor, Steel));

        COMPRESSOR_RECIPES.recipeBuilder("large_steam_hatch")
                .EUt(VA[LV])
                .duration(20 * 10)
                .inputItems(STEAM_HATCH.asStack(16))
                .outputItems(GTSEMultiMachines.LARGE_STEAM_HATCH)
                .save(provider);
        COMPRESSOR_RECIPES.recipeBuilder("steam_void_miner")
                .EUt(VA[LV])
                .duration(20 * 10)
                .inputItems(STEAM_MINER.right().asStack(16))
                .outputItems(GTSEMultiMachines.STEAM_VOID_MINER)
                .save(provider);
    }

    static void createMobSimulatorRecipes(Consumer<FinishedRecipe> provider) {
        //////////////////////////////////////
        // ********* monster *********//
        //////////////////////////////////////
        // ender man
        ASSEMBLER_RECIPES.recipeBuilder("ender_man_spawn_egg")
                .duration(20 * 10)
                .EUt(V[HV])
                .inputItems(new ItemStack(ENDER_PEARL, 8), new ItemStack(EGG))
                .outputItems(ENDERMAN_SPAWN_EGG)
                .save(provider);

        MOB_SIMULATOR_RECIPES.recipeBuilder("ender_man")
                .circuitMeta(1)
                .notConsumable(ENDERMAN_SPAWN_EGG)
                .duration(20 * 10)
                .EUt(VA[HV])
                .chancedOutput(new ItemStack(ENDER_PEARL), 1000, 2000)
                .save(provider);

        // blaze
        ASSEMBLER_RECIPES.recipeBuilder("blaze_spawn_egg")
                .duration(20 * 10)
                .EUt(V[MV])
                .inputItems(new ItemStack(BLAZE_ROD, 8), new ItemStack(EGG))
                .outputItems(BLAZE_SPAWN_EGG)
                .save(provider);
        MOB_SIMULATOR_RECIPES.recipeBuilder("blaze")
                .circuitMeta(1)
                .notConsumable(BLAZE_SPAWN_EGG)
                .duration(20 * 10)
                .EUt(VA[MV])
                .chancedOutput(new ItemStack(BLAZE_ROD), 1000, 2000)
                .save(provider);

        // witch
        ASSEMBLER_RECIPES.recipeBuilder("witch_spawn_egg")
                .duration(20 * 10)
                .EUt(V[MV])
                .inputItems(new ItemStack(REDSTONE, 2), new ItemStack(SUGAR, 2), new ItemStack(GLASS_BOTTLE, 2),
                        new ItemStack(GLOWSTONE, 2), new ItemStack(EGG))
                .outputItems(WITCH_SPAWN_EGG)
                .save(provider);

        MOB_SIMULATOR_RECIPES.recipeBuilder("witch")
                .circuitMeta(1)
                .notConsumable(WITCH_SPAWN_EGG)
                .duration(20 * 10)
                .EUt(VA[MV])
                .chancedOutput(new ItemStack(REDSTONE), 1000, 2000)
                .chancedOutput(new ItemStack(SUGAR), 1000, 2000)
                .chancedOutput(new ItemStack(GLOWSTONE), 1000, 2000)
                .save(provider);

        // skeleton
        ASSEMBLER_RECIPES.recipeBuilder("skeleton_spawn_egg")
                .duration(20 * 10)
                .EUt(V[MV])
                .inputItems(new ItemStack(BONE, 8), new ItemStack(EGG))
                .outputItems(SKELETON_SPAWN_EGG)
                .save(provider);
        MOB_SIMULATOR_RECIPES.recipeBuilder("skeleton")
                .circuitMeta(1)
                .notConsumable(SKELETON_SPAWN_EGG)
                .duration(20 * 10)
                .EUt(VA[MV])
                .chancedOutput(new ItemStack(BONE), 1000, 2000)
                .save(provider);

        // wither skeleton
        ASSEMBLER_RECIPES.recipeBuilder("wither_skeleton_spawn_egg")
                .duration(20 * 10)
                .EUt(VA[HV])
                .inputItems(new ItemStack(WITHER_SKELETON_SKULL, 8), new ItemStack(EGG))
                .outputItems(WITHER_SKELETON_SPAWN_EGG)
                .save(provider);
        MOB_SIMULATOR_RECIPES.recipeBuilder("wither_skeleton")
                .circuitMeta(1)
                .notConsumable(WITHER_SKELETON_SPAWN_EGG)
                .duration(20 * 10)
                .EUt(VA[HV])
                .chancedOutput(new ItemStack(WITHER_SKELETON_SKULL), 1000, 2000)
                .save(provider);

        // phantom
        ASSEMBLER_RECIPES.recipeBuilder("phantom_spawn_egg")
                .duration(20 * 10)
                .EUt(VA[HV])
                .inputItems(new ItemStack(PHANTOM_MEMBRANE, 8), new ItemStack(EGG))
                .outputItems(PHANTOM_SPAWN_EGG)
                .save(provider);
        MOB_SIMULATOR_RECIPES.recipeBuilder("phantom")
                .circuitMeta(1)
                .notConsumable(PHANTOM_SPAWN_EGG)
                .duration(20 * 10)
                .EUt(VA[HV])
                .chancedOutput(new ItemStack(PHANTOM_MEMBRANE), 1000, 2000)
                .save(provider);

        // shulker
        ASSEMBLER_RECIPES.recipeBuilder("shulker_spawn_egg")
                .duration(20 * 10)
                .EUt(VA[HV])
                .inputItems(new ItemStack(SHULKER_SHELL, 8), new ItemStack(EGG))
                .outputItems(SHULKER_SPAWN_EGG)
                .save(provider);
        MOB_SIMULATOR_RECIPES.recipeBuilder("shulker")
                .circuitMeta(1)
                .notConsumable(SHULKER_SPAWN_EGG)
                .duration(20 * 10)
                .EUt(VA[HV])
                .chancedOutput(new ItemStack(SHULKER_SHELL), 1000, 2000)
                .save(provider);

        // slime
        ASSEMBLER_RECIPES.recipeBuilder("slime_spawn_egg")
                .duration(20 * 10)
                .EUt(VA[LV])
                .inputItems(new ItemStack(SLIME_BALL, 4), new ItemStack(EGG))
                .outputItems(SLIME_SPAWN_EGG)
                .save(provider);
        MOB_SIMULATOR_RECIPES.recipeBuilder("slime")
                .circuitMeta(1)
                .notConsumable(SLIME_SPAWN_EGG)
                .duration(20 * 10)
                .EUt(VA[LV])
                .chancedOutput(new ItemStack(SLIME_BALL), 8000, 2000)
                .save(provider);

        //////////////////////////////////////
        // ********* animals *********//
        //////////////////////////////////////
        // rabbit
        ASSEMBLER_RECIPES.recipeBuilder("rabbit_spawn_egg")
                .duration(20 * 10)
                .EUt(VA[LV])
                .inputItems(new ItemStack(RABBIT_HIDE, 4), new ItemStack(RABBIT_FOOT, 4), new ItemStack(EGG))
                .outputItems(RABBIT_SPAWN_EGG)
                .save(provider);
        MOB_SIMULATOR_RECIPES.recipeBuilder("rabbit")
                .circuitMeta(1)
                .notConsumable(RABBIT_SPAWN_EGG)
                .duration(20 * 10)
                .EUt(VA[LV])
                .chancedOutput(new ItemStack(RABBIT_HIDE), 1000, 2000)
                .chancedOutput(new ItemStack(RABBIT_FOOT), 1000, 2000)
                .save(provider);

        // cow
        ASSEMBLER_RECIPES.recipeBuilder("cow_spawn_egg")
                .duration(20 * 10)
                .EUt(V[LV])
                .inputItems(new ItemStack(BEEF, 4), new ItemStack(LEATHER, 4), new ItemStack(EGG))
                .outputItems(COW_SPAWN_EGG)
                .save(provider);

        MOB_SIMULATOR_RECIPES.recipeBuilder("cow")
                .circuitMeta(1)
                .notConsumable(COW_SPAWN_EGG)
                .duration(20 * 10)
                .EUt(VA[LV])
                .chancedOutput(new ItemStack(BEEF), 8000, 2000)
                .chancedOutput(new ItemStack(LEATHER), 8000, 2000)
                .save(provider);
        // sheep
        ASSEMBLER_RECIPES.recipeBuilder("sheep_spawn_egg")
                .duration(20 * 10)
                .EUt(VA[LV])
                .inputItems(WOOL, 8)
                .inputItems(new ItemStack(EGG))
                .outputItems(SHEEP_SPAWN_EGG)
                .save(provider);
        MOB_SIMULATOR_RECIPES.recipeBuilder("sheep")
                .circuitMeta(1)
                .notConsumable(SHEEP_SPAWN_EGG)
                .duration(20 * 10)
                .EUt(VA[LV])
                .chancedOutput(new ItemStack(WHITE_WOOL), 8000, 2000)
                .save(provider);

        // chicken
        ASSEMBLER_RECIPES.recipeBuilder("chicken_spawn_egg")
                .duration(20 * 10)
                .EUt(VA[LV])
                .inputItems(new ItemStack(CHICKEN, 4), new ItemStack(EGG))
                .outputItems(CHICKEN_SPAWN_EGG)
                .save(provider);
        MOB_SIMULATOR_RECIPES.recipeBuilder("chicken")
                .circuitMeta(1)
                .notConsumable(CHICKEN_SPAWN_EGG)
                .duration(20 * 10)
                .EUt(VA[LV])
                .chancedOutput(new ItemStack(CHICKEN), 8000, 2000)
                .save(provider);
    }

    static void createMultiblockRecipes(Consumer<FinishedRecipe> provider) {
        VanillaRecipeHelper.addShapedRecipe(provider, true, "tree_farm",
                GTSEMultiMachines.TREE_FARM.asStack(), "PCP", "SAS", "PCP", 'S',
                new MaterialEntry(TagPrefix.rotor, GTMaterials.Steel), 'P', CustomTags.LV_CIRCUITS, 'A',
                GTMachines.HULL[GTValues.MV].asStack(), 'C', GTItems.ELECTRIC_PUMP_LV.asStack());

        VanillaRecipeHelper.addShapedRecipe(provider, true, "personal_beacon",
                PERSONAL_BEACON.asStack(), "PCP", "SAS", "PCP", 'S',
                ENDER_PEARL, 'P', CustomTags.LV_CIRCUITS, 'A',
                GTMachines.HULL[GTValues.LV].asStack(), 'C', FIELD_GENERATOR_LV.asStack());

        ASSEMBLER_RECIPES.recipeBuilder("macro_blast_furnace")
                .duration(20 * 100).EUt(VA[LV])
                .circuitMeta(16)
                .inputItems(ELECTRIC_BLAST_FURNACE.asStack(64))
                .outputItems(GTSEMultiMachines.MACRO_BLAST_FURNACE.asStack())
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder("industrial_pyrolyse_oven")
                .duration(20 * 100).EUt(VA[EV])
                .circuitMeta(16)
                .inputItems(PYROLYSE_OVEN.asStack(), 16)
                .outputItems(GTSEMultiMachines.INDUSTRIAL_PYROLYSE_OVEN.asStack())
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder("industrial_alloy_blast_smelter")
                .duration(20 * 100).EUt(VA[LuV])
                .circuitMeta(16)
                .inputItems(BLAST_ALLOY_SMELTER.asStack(), 16)
                .outputItems(GTSEMultiMachines.INDUSTRIAL_BLAST_ALLOY_SMELTER.asStack())
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder("industrial_cracker")
                .duration(20 * 100).EUt(VA[IV])
                .circuitMeta(16)
                .inputItems(CRACKER.asStack(), 16)
                .outputItems(INDUSTRIAL_CRACKER.asStack())
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder("advanced_fusion_reactor_mk1")
                .duration(20 * 100).EUt(VA[LuV])
                .circuitMeta(16)
                .inputItems(FUSION_REACTOR[LuV].asStack(), 16)
                .outputItems(ADVANCED_FUSION_REACTOR[LuV].asStack())
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder("advanced_fusion_reactor_mk2")
                .duration(20 * 100).EUt(VA[ZPM])
                .circuitMeta(16)
                .inputItems(FUSION_REACTOR[ZPM].asStack(), 16)
                .outputItems(ADVANCED_FUSION_REACTOR[ZPM].asStack())
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder("advanced_fusion_reactor_mk3")
                .duration(20 * 100).EUt(VA[UV])
                .circuitMeta(16)
                .inputItems(FUSION_REACTOR[UV].asStack(), 16)
                .outputItems(ADVANCED_FUSION_REACTOR[UV].asStack())
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

        VanillaRecipeHelper.addShapedRecipe(provider, true, "diesel_generator_ev",
                GTSEMachines.COMBUSTION[EV].asStack(),
                "PCP", "EME", "GWG", 'M', GTMachines.HULL[GTValues.EV].asStack(), 'P', GTItems.ELECTRIC_PISTON_EV, 'E',
                GTItems.ELECTRIC_MOTOR_EV, 'C', CustomTags.EV_CIRCUITS, 'W',
                new MaterialEntry(TagPrefix.cableGtSingle, GTMaterials.Aluminium), 'G',
                new MaterialEntry(TagPrefix.gear, Titanium));
        VanillaRecipeHelper.addShapedRecipe(provider, true, "diesel_generator_iv",
                GTSEMachines.COMBUSTION[IV].asStack(),
                "PCP", "EME", "GWG", 'M', GTMachines.HULL[GTValues.IV].asStack(), 'P', GTItems.ELECTRIC_PISTON_IV, 'E',
                GTItems.ELECTRIC_MOTOR_IV, 'C', CustomTags.IV_CIRCUITS, 'W',
                new MaterialEntry(TagPrefix.cableGtSingle, GTMaterials.Tungsten), 'G',
                new MaterialEntry(TagPrefix.gear, GTMaterials.TungstenSteel));

        VanillaRecipeHelper.addShapedRecipe(provider, true, "plasma_turbine_luv",
                GTSEMachines.PLASMA_TURBINE[LuV].asStack(), "PSP", "SAS", "CSC", 'S',
                new MaterialEntry(TagPrefix.rotor, GTMaterials.RhodiumPlatedPalladium), 'P', CustomTags.LuV_CIRCUITS,
                'A',
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

        VanillaRecipeHelper.addShapedRecipe(provider, true, "large_advanced_steam_turbine",
                LARGE_ADVANCED_STEAM_TURBINE.asStack(), " V ", "CMC", " V ", 'V',
                CONVEYOR.get(HV), 'C', CustomTags.HV_CIRCUITS, 'M',
                LARGE_STEAM_TURBINE.asStack());
        VanillaRecipeHelper.addShapedRecipe(provider, true, "large_advanced_gas_turbine",
                LARGE_ADVANCED_GAS_TURBINE.asStack(), " V ", "CMC", " V ", 'V',
                CONVEYOR.get(EV), 'C', CustomTags.EV_CIRCUITS, 'M',
                LARGE_GAS_TURBINE.asStack());
        VanillaRecipeHelper.addShapedRecipe(provider, true, "large_advanced_plasma_turbine",
                LARGE_ADVANCED_PLASMA_TURBINE.asStack(), " V ", "CMC", " V ", 'V',
                CONVEYOR.get(IV), 'C', CustomTags.IV_CIRCUITS, 'M',
                LARGE_PLASMA_TURBINE.asStack());

        VanillaRecipeHelper.addShapedRecipe(provider, true, "large_combustion_st",
                LARGE_COMBUSTION_SET.asStack(), "PCP", "EME", "GWG", 'M',
                EXTREME_COMBUSTION_ENGINE.asStack(), 'P', GTItems.EMITTER_LuV.asStack(), 'E',
                GTItems.ELECTRIC_MOTOR_LuV.asStack(), 'C', CustomTags.ZPM_CIRCUITS, 'W',
                new MaterialEntry(TagPrefix.cableGtSingle, NiobiumTitanium), 'G',
                GTItems.SENSOR_LuV.asStack());
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

        MIXER_RECIPES.recipeBuilder("ae2_fluix_crystal")
                .duration(20).EUt(VA[LV])
                .inputItems(CERTUS_QUARTZ_CRYSTAL_CHARGED.asItem())
                .inputItems(dust, Redstone)
                .inputItems(QUARTZ)
                .outputItems(FLUIX_CRYSTAL.stack(2))
                .save(provider);

        MACERATOR_RECIPES.recipeBuilder("ae2_certus_quartz_dust")
                .duration(20).EUt(VA[LV])
                .inputItems(CERTUS_QUARTZ_CRYSTAL.asItem())
                .outputItems(CERTUS_QUARTZ_DUST.asItem())
                .save(provider);

        MACERATOR_RECIPES.recipeBuilder("ae2_fluix_dust")
                .duration(20).EUt(VA[LV])
                .inputItems(FLUIX_CRYSTAL.asItem())
                .outputItems(FLUIX_DUST.asItem())
                .save(provider);
    }

    static void createTreeFarmRecipe(Consumer<FinishedRecipe> provider, String name, ItemLike sapling, ItemLike log,
                                     ItemLike fruit) {
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

    static void createSteamVoidMinerRecipe(Consumer<FinishedRecipe> provider) {
        int duration = 20 * 2;
        int EUt = 16;
        createChancedInput(STEAM_VOID_MINER_RECIPES
                .recipeBuilder("bronze_drill_mining"), toolHeadDrill, Bronze, 400, 0)
                .EUt(EUt)
                .duration(duration)
                .chancedOutput(ore, Silver, 1000, 0)
                .chancedOutput(ore, Iron, 1000, 0)
                .chancedOutput(ore, Tin, 1000, 0)
                .chancedOutput(ore, Copper, 1000, 0)
                .chancedOutput(ore, Coal, 2000, 0)
                .chancedOutput(new ItemStack(CLAY), 2000, 0)
                .save(provider);

        createChancedInput(STEAM_VOID_MINER_RECIPES
                .recipeBuilder("steel_drill_mining"), toolHeadDrill, Steel, 200, 0)
                .EUt(EUt)
                .duration(duration)
                .chancedOutput(ore, Diamond, 1000, 0)
                .chancedOutput(ore, Redstone, 1000, 0)
                .chancedOutput(ore, Lapis, 1000, 0)
                .chancedOutput(ore, Salt, 1000, 0)
                .chancedOutput(ore, Gold, 1000, 0)
                .save(provider);

        createChancedInput(STEAM_VOID_MINER_RECIPES
                .recipeBuilder("invar_drill_mining"), toolHeadDrill, Invar, 200, 0)
                .EUt(EUt)
                .duration(duration)
                .chancedOutput(ore, Lead, 1000, 0)
                .chancedOutput(ore, Nickel, 1000, 0)
                .chancedOutput(ore, Oilsands, 1000, 0)
                .save(provider);
    }

    static GTRecipeBuilder createChancedInput(GTRecipeBuilder builder, TagPrefix tagPrefix, @NotNull Material material,
                                              int chance, int tierChanceBoost) {
        if (0 >= chance || chance > ChanceLogic.getMaxChancedValue()) {
            GTCEu.LOGGER.error("Chance cannot be less or equal to 0 or more than {}. Actual: {}.",
                    ChanceLogic.getMaxChancedValue(), chance, new Throwable());
            return builder;
        }
        int lastChance = builder.chance;
        int lastTierChanceBoost = builder.tierChanceBoost;
        builder.chance = chance;
        builder.tierChanceBoost = tierChanceBoost;
        builder.inputItems(tagPrefix, material);
        builder.chance = lastChance;
        builder.tierChanceBoost = lastTierChanceBoost;
        return builder;
    }

    private static void createLargeFisherRecipe(Consumer<FinishedRecipe> provider) {
        VanillaRecipeHelper.addShapedRecipe(provider, true, "large_fisher", GTSEMultiMachines.LARGE_FISHER.asStack(),
                "FSF",
                "CMC", "FSF", 'F', FISHING_ROD, 'S', GTBlocks.CASING_STEEL_SOLID.asStack(),
                'M', HULL.get(MV), 'C',
                CustomTags.MV_CIRCUITS);

        LARGE_FISHER_RECIPES.recipeBuilder("fishing")
                .EUt(VA[MV])
                .circuitMeta(1)
                .duration(20 * 10)
                .notConsumable(FISHING_ROD)
                .chancedOutput(new ItemStack(COD), 6000, 1000)
                .chancedOutput(new ItemStack(SALMON), 2500, 1000)
                .chancedOutput(new ItemStack(TROPICAL_FISH), 200, 1000)
                .chancedOutput(new ItemStack(PUFFERFISH), 1300, 1000)
                .save(provider);
    }
}
