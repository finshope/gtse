package com.finshope.gtsecore.common.data;

import com.finshope.gtsecore.GTSECore;
import com.finshope.gtsecore.common.machine.electric.NetherCollectorMachine;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.gregtechceu.gtceu.client.renderer.machine.TieredHullMachineRenderer;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import static com.finshope.gtsecore.api.registries.GTSERegistires.REGISTRATE;
import static com.gregtechceu.gtceu.api.GTValues.VNF;
import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.defaultTankSizeFunction;

public class GTSEMachines {
    public static final int[] NETHER_COLLECTOR_TIERS = GTValues.tiersBetween(GTValues.EV, GTCEuAPI.isHighTier() ? GTValues.MAX : GTValues.UHV);
    public final static MachineDefinition[] NETHER_COLLECTOR = registerTieredMachines("nether_collector", NetherCollectorMachine::new,
            (tier, builder) -> builder
                    .langValue("%s Nether collector".formatted(VNF[tier]))
                    .editableUI(SimpleTieredMachine.EDITABLE_UI_CREATOR.apply(GTSECore.id("nether_collector"),
                            GTSERecipeTypes.NETHER_COLLECTOR_RECIPES))
                    .rotationState(RotationState.NON_Y_AXIS)
                    .renderer(() -> new TieredHullMachineRenderer(tier, GTSECore.id("block/machine/nether_collector")))
                    .recipeType(GTSERecipeTypes.NETHER_COLLECTOR_RECIPES)
                    .tooltips(workableTiered(tier, GTValues.V[tier], GTValues.V[tier] * 64, GTSERecipeTypes.NETHER_COLLECTOR_RECIPES, defaultTankSizeFunction.apply(tier), true))
                    .register(),
            NETHER_COLLECTOR_TIERS);

    public static Component[] workableTiered(int tier, long voltage, long energyCapacity, GTRecipeType recipeType, long tankCapacity, boolean input) {
        List<Component> tooltipComponents = new ArrayList<>();
        tooltipComponents.add(input ? Component.translatable("gtceu.universal.tooltip.voltage_in", voltage, GTValues.VNF[tier]) :
                Component.translatable("gtceu.universal.tooltip.voltage_out", voltage, GTValues.VNF[tier]));
        tooltipComponents.add(Component.translatable("gtceu.universal.tooltip.energy_storage_capacity", energyCapacity));
        if (recipeType.getMaxInputs(FluidRecipeCapability.CAP) > 0 || recipeType.getMaxOutputs(FluidRecipeCapability.CAP) > 0)
            tooltipComponents.add(Component.translatable("gtceu.universal.tooltip.fluid_storage_capacity", tankCapacity));
        return tooltipComponents.toArray(Component[]::new);
    }
    public static MachineDefinition[] registerTieredMachines(String name,
                                                             BiFunction<IMachineBlockEntity, Integer, MetaMachine> factory,
                                                             BiFunction<Integer, MachineBuilder<MachineDefinition>, MachineDefinition> builder,
                                                             int... tiers) {
        MachineDefinition[] definitions = new MachineDefinition[tiers.length];
        for (int i = 0; i < tiers.length; i++) {
            int tier = tiers[i];
            var register =  REGISTRATE.machine(GTValues.VN[tier].toLowerCase() + "_" + name, holder -> factory.apply(holder, tier))
                    .tier(tier);
            definitions[i] = builder.apply(tier, register);
        }
        return definitions;
    }
    public static void init() {

    }
}
