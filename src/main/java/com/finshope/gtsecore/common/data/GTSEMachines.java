package com.finshope.gtsecore.common.data;

import com.finshope.gtsecore.GTSECore;
import com.finshope.gtsecore.common.machine.electric.HarvesterMachine;
import com.finshope.gtsecore.common.machine.electric.MobSimulatorMachine;
import com.finshope.gtsecore.common.machine.electric.NetherCollectorMachine;
import com.finshope.gtsecore.common.machine.multiblock.electric.TreeFarmMachine;
import com.finshope.gtsecore.common.machine.multiblock.part.LargeSteamHatchPartMachine;
import com.finshope.gtsecore.common.machine.multiblock.steam.IndustrialSteamParallelMultiblockMachine;
import com.finshope.gtsecore.common.machine.multiblock.steam.SteamOreWaherMachine;
import com.finshope.gtsecore.common.machine.multiblock.steam.SteamVoidMinerMachine;
import com.finshope.gtsecore.config.GTSEConfig;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.*;
import com.gregtechceu.gtceu.api.machine.multiblock.CoilWorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockShapeInfo;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.gregtechceu.gtceu.common.data.*;
import com.gregtechceu.gtceu.common.machine.multiblock.part.SteamHatchPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.steam.SteamParallelMultiblockMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

import static com.finshope.gtsecore.api.recipe.OverclockingLogic.PERFECT_OVERCLOCK_SUBSECOND;
import static com.finshope.gtsecore.api.registries.GTSERegistires.REGISTRATE;
import static com.gregtechceu.gtceu.api.pattern.Predicates.*;
import static com.gregtechceu.gtceu.common.data.GCYMBlocks.*;
import static com.gregtechceu.gtceu.common.data.GCYMRecipeTypes.ALLOY_BLAST_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTBlocks.*;
import static com.gregtechceu.gtceu.common.data.GTMachines.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeModifiers.ELECTRIC_OVERCLOCK;
import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.*;
import static net.minecraft.world.level.block.Blocks.GLASS;
import static net.minecraft.world.level.block.Blocks.WATER;

public class GTSEMachines {

    public static final Int2IntFunction largeTankSizeFunction = (tier) -> Math.min(4 * (1 << tier - 1), 256) * 1000;

    public static final int[] NETHER_COLLECTOR_TIERS = GTValues.tiersBetween(GTValues.EV,
            GTCEuAPI.isHighTier() ? GTValues.MAX : GTValues.UHV);
    public static final int[] HARVESTER_TIERS = GTValues.tiersBetween(GTValues.LV, GTValues.IV);
    public final static MachineDefinition[] NETHER_COLLECTOR = registerTieredMachines("nether_collector",
            NetherCollectorMachine::new,
            (tier, builder) -> builder
                    .langValue("%s Nether collector".formatted(GTValues.VNF[tier]))
                    .editableUI(SimpleTieredMachine.EDITABLE_UI_CREATOR.apply(GTSECore.id("nether_collector"),
                            GTSERecipeTypes.NETHER_COLLECTOR_RECIPES))
                    .rotationState(RotationState.NON_Y_AXIS)
                    .workableTieredHullRenderer(GTSECore.id("block/machines/nether_collector"))
                    .recipeType(GTSERecipeTypes.NETHER_COLLECTOR_RECIPES)
                    .tooltips(workableTiered(tier, GTValues.V[tier], GTValues.V[tier] * 64,
                            GTSERecipeTypes.NETHER_COLLECTOR_RECIPES, defaultTankSizeFunction.apply(tier), true))
                    .register(),
            NETHER_COLLECTOR_TIERS);

    public final static MachineDefinition[] HARVESTER = registerTieredMachines("harvester", HarvesterMachine::new,
            (tier, builder) -> builder
                    .langValue("%s Harvester".formatted(GTValues.VNF[tier]))
                    .editableUI(SimpleTieredMachine.EDITABLE_UI_CREATOR.apply(GTSECore.id("harvester"),
                            GTSERecipeTypes.HARVESTER_RECIPES))
                    .rotationState(RotationState.NON_Y_AXIS)
                    .workableTieredHullRenderer(GTSECore.id("block/machines/harvester"))
                    .recipeType(GTSERecipeTypes.HARVESTER_RECIPES)
                    .tooltips(Component.translatable("gtse.machine.harvester.tooltip"))
                    .register(),
            HARVESTER_TIERS);

    public final static MachineDefinition[] MOB_SIMULATOR = registerTieredMachines("mob_simulator",
            MobSimulatorMachine::new,
            (tier, builder) -> builder
                    .langValue("%s Harvester".formatted(GTValues.VNF[tier]))
                    .editableUI(SimpleTieredMachine.EDITABLE_UI_CREATOR.apply(GTSECore.id("mob_simulator"),
                            GTSERecipeTypes.MOB_SIMULATOR_RECIPES))
                    .rotationState(RotationState.NON_Y_AXIS)
                    .workableTieredHullRenderer(GTSECore.id("block/machines/mob_simulator"))
                    .recipeModifiers(ELECTRIC_OVERCLOCK.apply(OverclockingLogic.NON_PERFECT_OVERCLOCK_SUBTICK))
                    .recipeType(GTSERecipeTypes.MOB_SIMULATOR_RECIPES)
                    .register(),
            ALL_TIERS);

    // generator
    public static final MachineDefinition[] COMBUSTION = registerSimpleGenerator("combustion",
            GTRecipeTypes.COMBUSTION_GENERATOR_FUELS, largeTankSizeFunction, 0.1f, GTValues.EV, GTValues.IV);
    public static final MachineDefinition[] GAS_TURBINE = registerSimpleGenerator("gas_turbine",
            GTRecipeTypes.GAS_TURBINE_FUELS, largeTankSizeFunction, 0.1f, GTValues.EV, GTValues.IV);
    public static final MachineDefinition[] PLASMA_TURBINE = registerSimpleGenerator("plasma_turbine",
            GTRecipeTypes.PLASMA_GENERATOR_FUELS, largeTankSizeFunction, 0.1f, GTValues.LuV, GTValues.ZPM, GTValues.UV);

    public static final MachineDefinition LARGE_STEAM_HATCH = REGISTRATE
            .machine("large_steam_input_hatch", LargeSteamHatchPartMachine::new)
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.STEAM)
            .overlaySteamHullRenderer("large_steam_hatch")
            .tooltips(Component.translatable("gtceu.universal.tooltip.fluid_storage_capacity",
                    SteamHatchPartMachine.INITIAL_TANK_CAPACITY * 64),
                    Component.translatable("gtceu.machine.steam.steam_hatch.tooltip"))
            .register();

    public static final MultiblockMachineDefinition STEAM_CENTRIFUGE = REGISTRATE
            .multiblock("steam_centrifuge", IndustrialSteamParallelMultiblockMachine::new)
            .rotationState(RotationState.ALL)
            .tooltips(Component.translatable("gtse.machine.large_steam_machine.tooltip"))
            .appearanceBlock(CASING_INDUSTRIAL_STEAM)
            .recipeType(GTRecipeTypes.CENTRIFUGE_RECIPES)
            .recipeModifier(GTSEMachines::industrialSteamMachineRecipeModifier, true)
            .addOutputLimit(ItemRecipeCapability.CAP, 4)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle(" FFF ", " FFF ", " FFF ", " FFF ", "     ", " FFF ")
                    .aisle("FFFFF", "FFPFF", "FFPFF", "FFPFF", "  P  ", "FFFFF")
                    .aisle("FFFFF", "FPGPF", "FPGPF", "FPGPF", " PGP ", "FFFFF")
                    .aisle("FFFFF", "FFPFF", "FFPFF", "FFPFF", "  P  ", "FFFFF")
                    .aisle(" FFF ", " FSF ", " FFF ", " FFF ", "     ", " FFF ")
                    .where('S', Predicates.controller(blocks(definition.getBlock())))
                    .where('P', Predicates.blocks(CASING_BRONZE_PIPE.get()))
                    .where('G', Predicates.blocks(CASING_BRONZE_GEARBOX.get()))
                    .where(' ', Predicates.any())
                    .where('F', blocks(CASING_INDUSTRIAL_STEAM.get()).setMinGlobalLimited(50)
                            .or(Predicates.abilities(PartAbility.STEAM_IMPORT_ITEMS).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.STEAM_EXPORT_ITEMS).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.STEAM).setExactLimit(1))
                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
                                    PartAbility.IMPORT_ITEMS, PartAbility.EXPORT_ITEMS)))
                    .build())
            .workableCasingRenderer(GTCEu.id("block/casings/gcym/industrial_steam_casing"),
                    GTSECore.id("block/multiblock/steam_centrifuge"))
            .register();

    public static final MultiblockMachineDefinition STEAM_ORE_WASHER = REGISTRATE
            .multiblock("steam_ore_washer", SteamOreWaherMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .tooltips(Component.translatable("gtse.machine.large_steam_machine.tooltip"))
            .appearanceBlock(CASING_INDUSTRIAL_STEAM)
            .recipeType(GTRecipeTypes.ORE_WASHER_RECIPES)
            .recipeModifier(GTSEMachines::industrialSteamMachineRecipeModifier, true)
            .addOutputLimit(ItemRecipeCapability.CAP, 4)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("FFFFF", "FFFFF", "FFFFF")
                    .aisle("FFFFF", "FPPPF", "F   F")
                    .aisle("FFFFF", "FWWWF", "F   F")
                    .aisle("FFFFF", "FPPPF", "F   F")
                    .aisle("FFFFF", "FFSFF", "FFFFF")
                    .where('S', Predicates.controller(blocks(definition.getBlock())))
                    .where('P', Predicates.blocks(CASING_BRONZE_PIPE.get()))
                    .where(' ', Predicates.any())
                    .where('W', Predicates.blocks(WATER))
                    .where('F', blocks(CASING_INDUSTRIAL_STEAM.get()).setMinGlobalLimited(40)
                            .or(Predicates.abilities(PartAbility.STEAM_IMPORT_ITEMS).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.STEAM_EXPORT_ITEMS).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.STEAM).setExactLimit(1))
                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
                                    PartAbility.IMPORT_ITEMS, PartAbility.EXPORT_ITEMS)))
                    .build())
            .workableCasingRenderer(GTCEu.id("block/casings/gcym/industrial_steam_casing"),
                    GTSECore.id("block/multiblock/steam_ore_washer"))
            .register();

    public static final MultiblockMachineDefinition STEAM_MIXER = REGISTRATE
            .multiblock("steam_mixer", IndustrialSteamParallelMultiblockMachine::new)
            .rotationState(RotationState.ALL)
            .tooltips(Component.translatable("gtse.machine.large_steam_machine.tooltip"))
            .appearanceBlock(CASING_INDUSTRIAL_STEAM)
            .recipeType(GTRecipeTypes.MIXER_RECIPES)
            .recipeModifier(GTSEMachines::industrialSteamMachineRecipeModifier, true)
            .addOutputLimit(ItemRecipeCapability.CAP, 4)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle(" FFF ", " FGF ", " FGF ", " FGF ", " FFF ", "  F  ")
                    .aisle("FFFFF", "F P F", "F   F", "F P F", "F   F", "  F  ")
                    .aisle("FFFFF", "GPBPG", "G B G", "GPBPG", "F B F", "FFBFF")
                    .aisle("FFFFF", "F P F", "F   F", "F P F", "F   F", "  F  ")
                    .aisle(" FFF ", " FSF ", " FGF ", " FGF ", " FFF ", "  F  ")
                    .where('S', Predicates.controller(blocks(definition.getBlock())))
                    .where(' ', Predicates.any())
                    .where('P', Predicates.blocks(CASING_BRONZE_PIPE.get()))
                    .where('B', Predicates.blocks(CASING_BRONZE_GEARBOX.get()))
                    .where('G', Predicates.blocks(GLASS))
                    .where('F', blocks(CASING_INDUSTRIAL_STEAM.get()).setMinGlobalLimited(50)
                            .or(Predicates.abilities(PartAbility.STEAM_IMPORT_ITEMS).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.STEAM_EXPORT_ITEMS).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.STEAM).setExactLimit(1))
                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
                                    PartAbility.IMPORT_ITEMS, PartAbility.EXPORT_ITEMS)))
                    .build())
            .workableCasingRenderer(GTCEu.id("block/casings/gcym/industrial_steam_casing"),
                    GTSECore.id("block/multiblock/steam_mixer"))
            .register();

    public static final MultiblockMachineDefinition STEAM_VOID_MINER = REGISTRATE
            .multiblock("steam_void_miner", SteamVoidMinerMachine::new)
            .rotationState(RotationState.ALL)
            .tooltips(Component.translatable("gtse.machine.large_steam_machine.tooltip"))
            .appearanceBlock(CASING_INDUSTRIAL_STEAM)
            .recipeType(GTSERecipeTypes.STEAM_VOID_MINER_RECIPES)
            .recipeModifier(GTSEMachines::steamVoidMinerMachineRecipeModifier, true)
            .addOutputLimit(ItemRecipeCapability.CAP, 6)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("F   F", "F   F", "F   F", "XXXXX", "     ", "     ", "     ", "     ", "     ", "     ",
                            "     ")
                    .aisle("     ", "     ", "     ", "XXXXX", " XXX ", "  F  ", "  F  ", "  F  ", "     ", "     ",
                            "     ")
                    .aisle("     ", "     ", "     ", "XXXXX", " XXX ", " FCF ", " FCF ", " FCF ", "  F  ", "  F  ",
                            "  F  ")
                    .aisle("     ", "     ", "     ", "XXXXX", " XXX ", "  F  ", "  F  ", "  F  ", "     ", "     ",
                            "     ")
                    .aisle("F   F", "F   F", "F   F", "XXSXX", "     ", "     ", "     ", "     ", "     ", "     ",
                            "     ")
                    .where('S', Predicates.controller(blocks(definition.getBlock())))
                    .where(' ', Predicates.any())
                    .where('C', blocks(CASING_BRONZE_PIPE.get()))
                    .where('F',
                            blocks(GTMaterialBlocks.MATERIAL_BLOCKS.get(TagPrefix.frameGt, GTMaterials.Steel).get()))
                    .where('X', blocks(CASING_INDUSTRIAL_STEAM.get()).setMinGlobalLimited(20)
                            .or(Predicates.abilities(PartAbility.STEAM_IMPORT_ITEMS).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.STEAM_EXPORT_ITEMS).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.STEAM).setExactLimit(1))
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS, PartAbility.EXPORT_ITEMS)))
                    .build())
            .workableCasingRenderer(GTCEu.id("block/casings/gcym/industrial_steam_casing"),
                    GTCEu.id("block/multiblock/bedrock_ore_miner"))
            .register();

    public static final MultiblockMachineDefinition TREE_FARM = REGISTRATE.multiblock("tree_farm", TreeFarmMachine::new)
            .rotationState(RotationState.ALL)
            .recipeType(GTSERecipeTypes.TREE_FARM_RECIPES)
            .appearanceBlock(CASING_STEEL_SOLID)
            .recipeModifiers(ELECTRIC_OVERCLOCK.apply(PERFECT_OVERCLOCK_SUBSECOND))
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("XXX", "XXX", "XXX")
                    .aisle("XXX", "X#X", "XXX")
                    .aisle("XXX", "XSX", "XXX")
                    .where('S', Predicates.controller(blocks(definition.getBlock())))
                    .where('X', blocks(CASING_STEEL_SOLID.get()).setMinGlobalLimited(10)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes())))
                    .where('#', Predicates.air())
                    .build())
            .workableCasingRenderer(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/multiblock/implosion_compressor"), false)
            .tooltips(Component.translatable("gtceu.machine.perfect_oc"))
            .register();

    public static final MultiblockMachineDefinition INDUSTRIAL_PYROLYSE_OVEN = REGISTRATE
            .multiblock("industrial_pyrolyse_oven", CoilWorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.ALL)
            .recipeType(GTRecipeTypes.PYROLYSE_RECIPES)
            .recipeModifiers(GTSERecipeModifiers::industrialPyrolyseOvenOverclock)
            .appearanceBlock(MACHINE_CASING_EV)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("XXX", "XXX", "XXX")
                    .aisle("CCC", "C#C", "CCC")
                    .aisle("CCC", "C#C", "CCC")
                    .aisle("XXX", "XSX", "XXX")
                    .where('S', Predicates.controller(blocks(definition.get())))
                    .where('X',
                            blocks(MACHINE_CASING_EV.get()).setMinGlobalLimited(6)
                                    .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                                    .or(Predicates.autoAbilities(true, true, false)))
                    .where('C', Predicates.heatingCoils())
                    .where('#', Predicates.air())
                    .build())
            .shapeInfos(definition -> {
                List<MultiblockShapeInfo> shapeInfo = new ArrayList<>();
                var builder = MultiblockShapeInfo.builder()
                        .aisle("IXO", "XSX", "FMD")
                        .aisle("CCC", "C#C", "CCC")
                        .aisle("CCC", "C#C", "CCC")
                        .aisle("EEX", "XHX", "XXX")
                        .where('S', definition, Direction.NORTH)
                        .where('X', MACHINE_CASING_EV.getDefaultState())
                        .where('E', ENERGY_INPUT_HATCH[GTValues.LV], Direction.SOUTH)
                        .where('I', ITEM_IMPORT_BUS[GTValues.LV], Direction.NORTH)
                        .where('O', ITEM_EXPORT_BUS[GTValues.LV], Direction.NORTH)
                        .where('F', FLUID_IMPORT_HATCH[GTValues.LV], Direction.NORTH)
                        .where('D', FLUID_EXPORT_HATCH[GTValues.LV], Direction.NORTH)
                        .where('H', MUFFLER_HATCH[GTValues.LV], Direction.SOUTH)
                        .where('M', MAINTENANCE_HATCH, Direction.NORTH)
                        .where('#', Blocks.AIR.defaultBlockState());
                GTCEuAPI.HEATING_COILS.entrySet().stream()
                        .sorted(Comparator.comparingInt(entry -> entry.getKey().getTier()))
                        .forEach(
                                coil -> shapeInfo.add(builder.shallowCopy().where('C', coil.getValue().get()).build()));
                return shapeInfo;
            })
            .workableCasingRenderer(GTCEu.id("block/casings/voltage/ev/side"),
                    GTSECore.id("block/multiblock/industrial_pyrolyse_oven"))
            .tooltips(Component.translatable("gtse.machine.industrial_pyrolyse_oven.tooltip.1"),
                    Component.translatable("gtceu.machine.perfect_oc"))
            .additionalDisplay((controller, components) -> {
                if (controller instanceof CoilWorkableElectricMultiblockMachine coilMachine && controller.isFormed()) {
                    components.add(Component.translatable("gtceu.multiblock.pyrolyse_oven.speed",
                            coilMachine.getCoilTier() == 0 ? 75 : 50 * (coilMachine.getCoilTier() + 1)));
                }
            })
            .register();

    public final static MultiblockMachineDefinition INDUSTRIAL_BLAST_ALLOY_SMELTER = REGISTRATE
            .multiblock("industrial_alloy_blast_smelter", CoilWorkableElectricMultiblockMachine::new)
            .langValue("Industrial Alloy Blast Smelter")
            .tooltips(Component.translatable("gtceu.machine.available_recipe_map_1.tooltip",
                    Component.translatable("gtceu.alloy_blast_smelter")))
            .tooltips(Component.translatable("gtceu.machine.electric_blast_furnace.tooltip.0"),
                    Component.translatable("gtceu.machine.perfect_oc"))
            .rotationState(RotationState.ALL)
            .recipeType(ALLOY_BLAST_RECIPES)
            .recipeModifiers(GTRecipeModifiers.PARALLEL_HATCH, GTSERecipeModifiers::industrialEbfOverclock)
            .appearanceBlock(CASING_HIGH_TEMPERATURE_SMELTING)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("#XXX#", "#CCC#", "#GGG#", "#CCC#", "#XXX#")
                    .aisle("XXXXX", "CAAAC", "GAAAG", "CAAAC", "XXXXX")
                    .aisle("XXXXX", "CAAAC", "GAAAG", "CAAAC", "XXMXX")
                    .aisle("XXXXX", "CAAAC", "GAAAG", "CAAAC", "XXXXX")
                    .aisle("#XSX#", "#CCC#", "#GGG#", "#CCC#", "#XXX#")
                    .where('S', controller(blocks(definition.get())))
                    .where('X', blocks(CASING_HIGH_TEMPERATURE_SMELTING.get()).setMinGlobalLimited(30)
                            .or(autoAbilities(definition.getRecipeTypes()))
                            .or(Predicates.autoAbilities(true, false, false)))
                    .where('C', heatingCoils())
                    .where('M', abilities(PartAbility.MUFFLER))
                    .where('G', blocks(HEAT_VENT.get()))
                    .where('A', air())
                    .where('#', any())
                    .build())
            .shapeInfos(definition -> {
                List<MultiblockShapeInfo> shapeInfo = new ArrayList<>();
                var builder = MultiblockShapeInfo.builder()
                        .aisle("#XSX#", "#CCC#", "#GGG#", "#CCC#", "#XMX#")
                        .aisle("IXXXX", "CAAAC", "GAAAG", "CAAAC", "XXXXX")
                        .aisle("XXXXD", "CAAAC", "GAAAG", "CAAAC", "XXHXX")
                        .aisle("FXXXX", "CAAAC", "GAAAG", "CAAAC", "XXXXX")
                        .aisle("#EXE#", "#CCC#", "#GGG#", "#CCC#", "#XXX#")
                        .where('X', CASING_HIGH_TEMPERATURE_SMELTING.getDefaultState())
                        .where('S', definition, Direction.NORTH)
                        .where('G', HEAT_VENT.getDefaultState())
                        .where('A', Blocks.AIR.defaultBlockState())
                        .where('E', ENERGY_INPUT_HATCH[GTValues.LV], Direction.SOUTH)
                        .where('I', ITEM_IMPORT_BUS[GTValues.LV], Direction.WEST)
                        .where('F', FLUID_IMPORT_HATCH[GTValues.LV], Direction.WEST)
                        .where('D', FLUID_EXPORT_HATCH[GTValues.LV], Direction.EAST)
                        .where('H', MUFFLER_HATCH[GTValues.LV], Direction.UP)
                        .where('M', MAINTENANCE_HATCH, Direction.NORTH);
                GTCEuAPI.HEATING_COILS.entrySet().stream()
                        .sorted(Comparator.comparingInt(entry -> entry.getKey().getTier()))
                        .forEach(
                                coil -> shapeInfo.add(builder.shallowCopy().where('C', coil.getValue().get()).build()));
                return shapeInfo;
            })
            .workableCasingRenderer(GTCEu.id("block/casings/gcym/high_temperature_smelting_casing"),
                    GTCEu.id("block/multiblock/gcym/blast_alloy_smelter"))
            .additionalDisplay((controller, components) -> {
                if (controller instanceof CoilWorkableElectricMultiblockMachine coilMachine && controller.isFormed()) {
                    components.add(Component.translatable("gtceu.multiblock.blast_furnace.max_temperature",
                            Component
                                    .translatable(
                                            FormattingUtil
                                                    .formatNumbers(coilMachine.getCoilType().getCoilTemperature() +
                                                            100L * Math.max(0, coilMachine.getTier() - GTValues.MV)) +
                                                    "K")
                                    .setStyle(Style.EMPTY.withColor(ChatFormatting.RED))));
                }
            })
            .register();

    public static final MultiblockMachineDefinition MACRO_BLAST_FURNACE = REGISTRATE
            .multiblock("macro_blast_furnace", CoilWorkableElectricMultiblockMachine::new)
            .rotationState(RotationState.ALL)
            .recipeType(GTRecipeTypes.BLAST_RECIPES)
            .recipeModifiers(GTSERecipeModifiers::macroBlastFurnaceParallel, GTRecipeModifiers::ebfOverclock)
            .appearanceBlock(CASING_INVAR_HEATPROOF)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("XXXXXXXXXXXXXXX", "XGGGGGGGGGGGGGX", "XGGGGGGGGGGGGGX", "XGGGGGGGGGGGGGX",
                            "XGGGGGGGGGGGGGX", "XGGGGGGGGGGGGGX", "XGGGGGGGGGGGGGX", "XGGGGGGGGGGGGGX",
                            "XGGGGGGGGGGGGGX", "XGGGGGGGGGGGGGX", "XGGGGGGGGGGGGGX", "XGGGGGGGGGGGGGX",
                            "XGGGGGGGGGGGGGX", "XGGGGGGGGGGGGGX", "XGGGGGGGGGGGGGX", "XGGGGGGGGGGGGGX",
                            "XGGGGGGGGGGGGGX", "XGGGGGGGGGGGGGX", "XGGGGGGGGGGGGGX", "XXXXXXXXXXXXXXX")
                    .aisle("XXXXXXXXXXXXXXX", "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG",
                            "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG",
                            "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG",
                            "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG",
                            "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG", "XXXXXXXXXXXXXXX")
                    .aisle("XXXXXXXXXXXXXXX", "GCCCCCCCCCCCCCG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GCCCCCCCCCCCCCG", "XXXXXXXXXXXXXXX")
                    .aisle("XXXXXXXXXXXXXXX", "GCCCCCCCCCCCCCG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GCCCCCCCCCCCCCG", "XXXXXXXXXXXXXXX")
                    .aisle("XXXXXXXXXXXXXXX", "GCCCCCCCCCCCCCG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GCCCCCCCCCCCCCG", "XXXXXXXXXXXXXXX")
                    .aisle("XXXXXXXXXXXXXXX", "GCCCCCCCCCCCCCG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GCCCCCCCCCCCCCG", "XXXXXXXXXXXXXXX")
                    .aisle("XXXXXXXXXXXXXXX", "GCCCCCCCCCCCCCG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GCCCCCCCCCCCCCG", "XXXXXXXXXXXXXXX")
                    .aisle("XXXXXXXXXXXXXXX", "GCCCCCCCCCCCCCG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GCCCCCCCCCCCCCG", "XXXXXXXMXXXXXXX")
                    .aisle("XXXXXXXXXXXXXXX", "GCCCCCCCCCCCCCG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GCCCCCCCCCCCCCG", "XXXXXXXXXXXXXXX")
                    .aisle("XXXXXXXXXXXXXXX", "GCCCCCCCCCCCCCG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GCCCCCCCCCCCCCG", "XXXXXXXXXXXXXXX")
                    .aisle("XXXXXXXXXXXXXXX", "GCCCCCCCCCCCCCG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GCCCCCCCCCCCCCG", "XXXXXXXXXXXXXXX")
                    .aisle("XXXXXXXXXXXXXXX", "GCCCCCCCCCCCCCG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GCCCCCCCCCCCCCG", "XXXXXXXXXXXXXXX")
                    .aisle("XXXXXXXXXXXXXXX", "GCCCCCCCCCCCCCG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GC           CG", "GC           CG",
                            "GC           CG", "GC           CG", "GCCCCCCCCCCCCCG", "XXXXXXXXXXXXXXX")
                    .aisle("XXXXXXXXXXXXXXX", "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG",
                            "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG",
                            "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG",
                            "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG",
                            "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG", "GCCCCCCCCCCCCCG", "XXXXXXXXXXXXXXX")
                    .aisle("XXXXXXXXXXXXXXX", "XGGGGGGGGGGGGGX", "XGGGGGGSGGGGGGX", "XGGGGGGGGGGGGGX",
                            "XGGGGGGGGGGGGGX", "XGGGGGGGGGGGGGX", "XGGGGGGGGGGGGGX", "XGGGGGGGGGGGGGX",
                            "XGGGGGGGGGGGGGX", "XGGGGGGGGGGGGGX", "XGGGGGGGGGGGGGX", "XGGGGGGGGGGGGGX",
                            "XGGGGGGGGGGGGGX", "XGGGGGGGGGGGGGX", "XGGGGGGGGGGGGGX", "XGGGGGGGGGGGGGX",
                            "XGGGGGGGGGGGGGX", "XGGGGGGGGGGGGGX", "XGGGGGGGGGGGGGX", "XXXXXXXXXXXXXXX")
                    .where('S', controller(blocks(definition.getBlock())))
                    .where('X', blocks(CASING_INVAR_HEATPROOF.get()).setMinGlobalLimited(450)
                            .or(autoAbilities(definition.getRecipeTypes()))
                            .or(autoAbilities(true, false, false)))
                    .where('M', abilities(PartAbility.MUFFLER))
                    .where('G', blocks(CASING_TEMPERED_GLASS.get()))
                    .where('C', heatingCoils())
                    .where(' ', any())
                    .build())
            .recoveryItems(
                    () -> new ItemLike[] {
                            GTMaterialItems.MATERIAL_ITEMS.get(TagPrefix.dustTiny, GTMaterials.Ash).get() })
            .workableCasingRenderer(GTCEu.id("block/casings/solid/machine_casing_heatproof"),
                    GTCEu.id("block/multiblock/electric_blast_furnace"))
            .tooltips(Component.translatable("gtceu.machine.electric_blast_furnace.tooltip.0"),
                    Component.translatable("gtceu.machine.electric_blast_furnace.tooltip.1"),
                    Component.translatable("gtceu.machine.electric_blast_furnace.tooltip.2"),
                    Component.translatable("gtse.machine.macro_blast_furnace.tooltip.0"),
                    Component.translatable("gtse.machine.macro_blast_furnace.tooltip.1"))
            .additionalDisplay((controller, components) -> {
                if (controller instanceof CoilWorkableElectricMultiblockMachine coilMachine && controller.isFormed()) {
                    components.add(Component.translatable("gtceu.multiblock.blast_furnace.max_temperature",
                            Component
                                    .translatable(
                                            FormattingUtil
                                                    .formatNumbers(coilMachine.getCoilType().getCoilTemperature() +
                                                            100L * Math.max(0, coilMachine.getTier() - GTValues.MV)) +
                                                    "K")
                                    .setStyle(Style.EMPTY.withColor(ChatFormatting.RED))));
                }
            })
            .register();

    public static Component[] workableTiered(int tier, long voltage, long energyCapacity, GTRecipeType recipeType,
                                             long tankCapacity, boolean input) {
        List<Component> tooltipComponents = new ArrayList<>();
        tooltipComponents
                .add(input ? Component.translatable("gtceu.universal.tooltip.voltage_in", voltage, GTValues.VNF[tier]) :
                        Component.translatable("gtceu.universal.tooltip.voltage_out", voltage, GTValues.VNF[tier]));
        tooltipComponents
                .add(Component.translatable("gtceu.universal.tooltip.energy_storage_capacity", energyCapacity));
        if (recipeType.getMaxInputs(FluidRecipeCapability.CAP) > 0 ||
                recipeType.getMaxOutputs(FluidRecipeCapability.CAP) > 0)
            tooltipComponents
                    .add(Component.translatable("gtceu.universal.tooltip.fluid_storage_capacity", tankCapacity));
        return tooltipComponents.toArray(Component[]::new);
    }

    public static MachineDefinition[] registerTieredMachines(String name,
                                                             BiFunction<IMachineBlockEntity, Integer, MetaMachine> factory,
                                                             BiFunction<Integer, MachineBuilder<MachineDefinition>, MachineDefinition> builder,
                                                             int... tiers) {
        MachineDefinition[] definitions = new MachineDefinition[tiers.length];
        for (int i = 0; i < tiers.length; i++) {
            int tier = tiers[i];
            var register = REGISTRATE
                    .machine(GTValues.VN[tier].toLowerCase() + "_" + name, holder -> factory.apply(holder, tier))
                    .tier(tier);
            definitions[i] = builder.apply(tier, register);
        }
        return definitions;
    }

    public static ModifierFunction industrialSteamMachineRecipeModifier(@NotNull MetaMachine machine,
                                                                        @NotNull GTRecipe recipe) {
        if (machine instanceof IndustrialSteamParallelMultiblockMachine) {
            if (RecipeHelper.getRecipeEUtTier(recipe) > GTValues.HV) {
                return ModifierFunction.NULL;
            } else {
                long eut = RecipeHelper.getInputEUt(recipe);
                int parallelAmount = ParallelLogic.getParallelAmount(machine, recipe,
                        GTSEConfig.INSTANCE.server.industrialSteamMachineMaxParallels);
                double eutMultiplier = (double) eut * 0.8888 * (double) parallelAmount <= 32.0 ?
                        0.8888 * (double) parallelAmount : 32.0 / (double) eut;
                return ModifierFunction.builder().inputModifier(ContentModifier.multiplier(parallelAmount))
                        .outputModifier(ContentModifier.multiplier(parallelAmount)).durationMultiplier(1.5)
                        .eutMultiplier(eutMultiplier).parallels(parallelAmount).build();
            }
        } else {
            return RecipeModifier.nullWrongType(IndustrialSteamParallelMultiblockMachine.class, machine);
        }
    }

    public static ModifierFunction steamVoidMinerMachineRecipeModifier(@NotNull MetaMachine machine,
                                                                       @NotNull GTRecipe recipe) {
        if (machine instanceof SteamParallelMultiblockMachine) {
            if (RecipeHelper.getRecipeEUtTier(recipe) > GTValues.HV) {
                return ModifierFunction.NULL;
            } else {
                return ModifierFunction.IDENTITY;
            }
        } else {
            return RecipeModifier.nullWrongType(SteamParallelMultiblockMachine.class, machine);
        }
    }

    public static void init() {}
}
