package com.finshope.gtsecore.common.data;

import com.finshope.gtsecore.GTSECore;
import com.finshope.gtsecore.common.machine.electric.HarvesterMachine;
import com.finshope.gtsecore.common.machine.electric.MobSimulatorMachine;
import com.finshope.gtsecore.common.machine.electric.NetherCollectorMachine;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.*;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.gregtechceu.gtceu.common.data.*;

import net.minecraft.network.chat.Component;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;

import java.util.function.BiFunction;

import static com.finshope.gtsecore.api.registries.GTSERegistires.REGISTRATE;
import static com.gregtechceu.gtceu.common.data.GTRecipeModifiers.ELECTRIC_OVERCLOCK;
import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.*;

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
                    .tooltips(GTMultiMachines.workableTiered(tier, GTValues.V[tier], GTValues.V[tier] * 64,
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

    public static void init() {}
}
