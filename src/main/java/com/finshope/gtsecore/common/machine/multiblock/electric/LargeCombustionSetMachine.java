package com.finshope.gtsecore.common.machine.multiblock.electric;

import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.SimpleGeneratorMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;

import org.jetbrains.annotations.NotNull;

public class LargeCombustionSetMachine extends WorkableElectricMultiblockMachine {

    public LargeCombustionSetMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (machine instanceof LargeCombustionSetMachine generator) {
            var realEUt = RecipeHelper.getRealEUt(recipe);
            long EUt = realEUt.voltage();
            if (EUt <= 0L) {
                return ModifierFunction.NULL;
            } else {
                long maxParallel = generator.getOverclockVoltage() / EUt;
                int parallels = (int) Math.min(getParallelAmountFast(generator, recipe, maxParallel),
                        Integer.MAX_VALUE);
                return ModifierFunction.builder().inputModifier(ContentModifier.multiplier((double) parallels))
                        .outputModifier(ContentModifier.multiplier((double) parallels))
                        .eutMultiplier((double) parallels).parallels((int) Math.min(Integer.MAX_VALUE, parallels))
                        .build();
            }
        } else {
            return RecipeModifier.nullWrongType(SimpleGeneratorMachine.class, machine);
        }
    }

    public static long getParallelAmountFast(MetaMachine machine, @NotNull GTRecipe recipe, long parallelLimit) {
        if (parallelLimit <= 1) {
            return parallelLimit;
        } else if (machine instanceof IRecipeCapabilityHolder) {
            for (IRecipeCapabilityHolder holder = (IRecipeCapabilityHolder) machine; parallelLimit >
                    0; parallelLimit /= 2) {
                GTRecipe copied = recipe.copy(ContentModifier.multiplier((double) parallelLimit), false);
                if (RecipeHelper.matchRecipe(holder, copied).isSuccess() &&
                        RecipeHelper.matchTickRecipe(holder, copied).isSuccess()) {
                    return parallelLimit;
                }
            }

            return 1;
        } else {
            return 1;
        }
    }

    @Override
    public boolean regressWhenWaiting() {
        return false;
    }
}
