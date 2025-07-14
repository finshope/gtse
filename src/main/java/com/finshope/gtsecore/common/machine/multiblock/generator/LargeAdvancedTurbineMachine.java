package com.finshope.gtsecore.common.machine.multiblock.generator;

import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IRotorHolderMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.predicates.SimplePredicate;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterialItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.machine.multiblock.generator.LargeTurbineMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ItemBusPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.RotorHolderPartMachine;

import com.lowdragmc.lowdraglib.utils.BlockInfo;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

import static com.finshope.gtsecore.api.registries.GTSERegistires.REGISTRATE;
import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.pattern.Predicates.*;

public class LargeAdvancedTurbineMachine extends LargeTurbineMachine {

    @Nullable
    protected TickableSubscription transferRotorSubs;

    public LargeAdvancedTurbineMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        transferRotorSubs = subscribeServerTick(transferRotorSubs, this::transferRotors);
    }

    @Nullable
    private IRotorHolderMachine getRotorHolder() {
        for (IMultiPart part : getParts()) {
            if (part instanceof IRotorHolderMachine rotorHolder) {
                return rotorHolder;
            }
        }
        return null;
    }

    private boolean hasRotor() {
        var holder = getRotorHolder();
        if (holder == null) {
            return false;
        }
        if (!holder.hasRotor()) {
            return false;
        }
        if (holder.getRotorMaterial() == GTMaterials.NULL) {
            return false;
        }
        return true;
    }

    public void transferRotors() {
        if (getOffsetTimer() % 20 != 0) return;
        if (!isFormed()) return;

        var holder = getRotorHolder();
        if (holder != null && !hasRotor() && holder instanceof RotorHolderPartMachine rotorHolder) {
            for (IMultiPart part : getParts()) {
                if (part instanceof ItemBusPartMachine bus) {
                    var inventory = bus.getInventory();
                    if (inventory.isEmpty()) {
                        continue;
                    }

                    var storage = inventory.storage;
                    for (int i = 0; i < storage.getSlots(); i++) {
                        var item = storage.getStackInSlot(i);
                        if (item.isEmpty()) {
                            continue;
                        }
                        if (item.getItem() == GTItems.TURBINE_ROTOR.asItem()) {
                            // transfer the item to the rotor holder
                            rotorHolder.inventory.insertItem(0, item, false);
                            storage.extractItem(i, 1, false);
                            return;
                        }
                    }
                }
            }
        }
    }

    public static MultiblockMachineDefinition registerAdvancedLargeTurbine(String name, int tier,
                                                                           GTRecipeType recipeType,
                                                                           Supplier<? extends Block> casing,
                                                                           Supplier<? extends Block> gear,
                                                                           ResourceLocation casingTexture,
                                                                           ResourceLocation overlayModel,
                                                                           boolean needsMuffler) {
        return REGISTRATE.multiblock(name, holder -> new LargeAdvancedTurbineMachine(holder, tier))
                .rotationState(RotationState.ALL)
                .recipeType(recipeType)
                .generator(true)
                .recipeModifier(LargeTurbineMachine::recipeModifier, true)
                .appearanceBlock(casing)
                .pattern(definition -> FactoryBlockPattern.start()
                        .aisle("CCCC", "CHHC", "CCCC")
                        .aisle("CHHC", "RGGR", "CHHC")
                        .aisle("CCCC", "CSHC", "CCCC")
                        .where('S', controller(blocks(definition.getBlock())))
                        .where('G', blocks(gear.get()))
                        .where('C', blocks(casing.get()))
                        .where('R',
                                new TraceabilityPredicate(
                                        new SimplePredicate(
                                                state -> MetaMachine.getMachine(state.getWorld(),
                                                        state.getPos()) instanceof IRotorHolderMachine rotorHolder &&
                                                        state.getWorld()
                                                                .getBlockState(state.getPos()
                                                                        .relative(rotorHolder.self().getFrontFacing()))
                                                                .isAir(),
                                                () -> PartAbility.ROTOR_HOLDER.getAllBlocks().stream()
                                                        .map(BlockInfo::fromBlock).toArray(BlockInfo[]::new)))
                                        .addTooltips(Component.translatable("gtceu.multiblock.pattern.clear_amount_3"))
                                        .addTooltips(Component.translatable("gtceu.multiblock.pattern.error.limited.1",
                                                VN[tier]))
                                        .setExactLimit(1)
                                        .or(abilities(PartAbility.OUTPUT_ENERGY)).setExactLimit(1))
                        .where('H', blocks(casing.get())
                                .or(autoAbilities(definition.getRecipeTypes(), false, false, true, true, true, true))
                                .or(autoAbilities(true, needsMuffler, false))
                                .or(abilities(PartAbility.IMPORT_ITEMS)))
                        .build())
                .recoveryItems(
                        () -> new ItemLike[] {
                                GTMaterialItems.MATERIAL_ITEMS.get(TagPrefix.dustTiny, GTMaterials.Ash).get() })
                .workableCasingModel(casingTexture, overlayModel)
                .tooltips(
                        Component.translatable("gtceu.universal.tooltip.base_production_eut", V[tier] * 2),
                        Component.translatable("gtceu.multiblock.turbine.efficiency_tooltip", VNF[tier]),
                        Component.translatable("gtse.machine.advanced_large_turbine.tooltip"))
                .register();
    }
}
