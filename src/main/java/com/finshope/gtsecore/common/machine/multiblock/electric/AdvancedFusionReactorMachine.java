package com.finshope.gtsecore.common.machine.multiblock.electric;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.FusionReactorMachine;

import org.jetbrains.annotations.NotNull;

public class AdvancedFusionReactorMachine extends FusionReactorMachine {

    private static final int MAX_PARALLELS = 16;

    public AdvancedFusionReactorMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier);
    }

    @Override
    public long getMaxVoltage() {
        // multiply by MAX_PARALLELS to allow overclocking
        return Math.min(GTValues.V[this.getTier()] * MAX_PARALLELS, super.getOverclockVoltage());
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine,
                                                  @NotNull GTRecipe recipe) {
        if (machine instanceof AdvancedFusionReactorMachine advancedFusionReactorMachine) {

            if (RecipeHelper.getRecipeEUtTier(recipe) > advancedFusionReactorMachine.getTier() ||
                    !recipe.data.contains("eu_to_start") ||
                    recipe.data.getLong("eu_to_start") >
                            advancedFusionReactorMachine.energyContainer.getEnergyCapacity()) {
                return ModifierFunction.NULL;
            }

            int parallels = ParallelLogic.getParallelAmount(advancedFusionReactorMachine, recipe, MAX_PARALLELS);

            var modifier = ModifierFunction.builder()
                    .modifyAllContents(ContentModifier.multiplier(parallels))
                    .eutMultiplier(parallels)
                    .parallels(parallels)
                    .build();
            var newRecipe = modifier.apply(recipe);
            if (newRecipe == null) {
                return ModifierFunction.NULL;
            }

            long heatDiff = recipe.data.getLong("eu_to_start") - advancedFusionReactorMachine.heat;

            if (heatDiff <= 0) {
                return modifier.andThen(
                        FUSION_OC.getModifier(machine, newRecipe, advancedFusionReactorMachine.getMaxVoltage(), false));
            }

            if (advancedFusionReactorMachine.energyContainer.getEnergyStored() < heatDiff) return ModifierFunction.NULL;

            advancedFusionReactorMachine.energyContainer.removeEnergy(heatDiff);
            advancedFusionReactorMachine.heat += heatDiff;
            advancedFusionReactorMachine.updatePreHeatSubscription();

            return modifier.andThen(
                    FUSION_OC.getModifier(machine, newRecipe, advancedFusionReactorMachine.getMaxVoltage(), false));
        }
        return RecipeModifier.nullWrongType(AdvancedFusionReactorMachine.class, machine);
    }
}
