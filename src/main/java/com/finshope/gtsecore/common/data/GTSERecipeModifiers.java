package com.finshope.gtsecore.common.data;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.CoilWorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;

import org.jetbrains.annotations.NotNull;

import static com.finshope.gtsecore.api.recipe.OverclockingLogic.PERFECT_OVERCLOCK_SUBSECOND;
import static com.finshope.gtsecore.api.recipe.OverclockingLogic.industrialHeatingCoilOC;

public class GTSERecipeModifiers {

    public static @NotNull ModifierFunction industrialPyrolyseOvenOverclock(@NotNull MetaMachine machine,
                                                                            @NotNull GTRecipe recipe) {
        if (machine instanceof CoilWorkableElectricMultiblockMachine coilMachine) {
            if (RecipeHelper.getRecipeEUtTier(recipe) > coilMachine.getTier()) {
                return ModifierFunction.NULL;
            } else {
                int tier = coilMachine.getCoilTier();
                double durationMultiplier = tier == 0 ? 1.3333333333333333 : 2.0 / (double) (tier + 1);
                ModifierFunction durationModifier = ModifierFunction.builder().durationMultiplier(durationMultiplier)
                        .build();
                ModifierFunction oc = PERFECT_OVERCLOCK_SUBSECOND.getModifier(machine, recipe,
                        coilMachine.getOverclockVoltage());
                return oc.andThen(durationModifier);
            }
        } else {
            return RecipeModifier.nullWrongType(CoilWorkableElectricMultiblockMachine.class, machine);
        }
    }

    public static @NotNull ModifierFunction industrialEbfOverclock(@NotNull MetaMachine machine,
                                                                   @NotNull GTRecipe recipe) {
        if (machine instanceof CoilWorkableElectricMultiblockMachine coilMachine) {
            int blastFurnaceTemperature = coilMachine.getCoilType().getCoilTemperature() +
                    100 * Math.max(0, coilMachine.getTier() - 2);
            int recipeTemp = recipe.data.getInt("ebf_temp");
            if (recipe.data.contains("ebf_temp") && recipeTemp <= blastFurnaceTemperature) {
                if (RecipeHelper.getRecipeEUtTier(recipe) > coilMachine.getTier()) {
                    return ModifierFunction.NULL;
                } else {
                    ModifierFunction discount = ModifierFunction.builder()
                            .eutMultiplier(OverclockingLogic.getCoilEUtDiscount(recipeTemp, blastFurnaceTemperature))
                            .build();
                    OverclockingLogic logic = (p, v) -> {
                        return industrialHeatingCoilOC(p, v, recipeTemp, blastFurnaceTemperature);
                    };
                    ModifierFunction oc = logic.getModifier(machine, recipe, coilMachine.getOverclockVoltage());
                    return oc.compose(discount);
                }
            } else {
                return ModifierFunction.NULL;
            }
        } else {
            return RecipeModifier.nullWrongType(CoilWorkableElectricMultiblockMachine.class, machine);
        }
    }

    public static @NotNull ModifierFunction industrialCrackerOverclock(@NotNull MetaMachine machine,
                                                                       @NotNull GTRecipe recipe) {
        if (!(machine instanceof CoilWorkableElectricMultiblockMachine coilMachine)) {
            return RecipeModifier.nullWrongType(CoilWorkableElectricMultiblockMachine.class, machine);
        }
        if (RecipeHelper.getRecipeEUtTier(recipe) > coilMachine.getTier()) return ModifierFunction.NULL;

        var oc = PERFECT_OVERCLOCK_SUBSECOND.getModifier(machine, recipe,
                coilMachine.getOverclockVoltage());
        if (coilMachine.getCoilTier() > 0) {
            var coilModifier = ModifierFunction.builder()
                    .eutMultiplier(1.0 - coilMachine.getCoilTier() * 0.1)
                    .build();
            oc = oc.andThen(coilModifier);
        }
        return oc;
    }

    public static @NotNull ModifierFunction durationDiscount(@NotNull MetaMachine machine,
                                                             @NotNull GTRecipe recipe) {
        if (machine instanceof CoilWorkableElectricMultiblockMachine coilMachine) {
            int tier = coilMachine.getTier();
            double power = Math.pow(0.9, tier);
            if (power < 0.1) {
                power = 0.1;
            }
            return ModifierFunction.builder()
                    .durationModifier(ContentModifier.multiplier(power))
                    .build();
        }
        return ModifierFunction.IDENTITY;
    }

    public static @NotNull ModifierFunction macroBlastFurnaceParallel(@NotNull MetaMachine machine,
                                                                      @NotNull GTRecipe recipe) {
        if (machine instanceof CoilWorkableElectricMultiblockMachine coilMachine) {
            int tier = coilMachine.getCoilTier();
            double power = Math.pow(4, tier + 1);
            int maxParallels = Math.max((int) Math.min(Integer.MAX_VALUE, power), 64);

            int parallels = ParallelLogic.getParallelAmount(machine, recipe, maxParallels);

            if (parallels == 1) return ModifierFunction.IDENTITY;
            return ModifierFunction.builder()
                    .modifyAllContents(ContentModifier.multiplier(parallels))
                    .eutMultiplier(parallels)
                    .parallels(parallels)
                    .build();
        }
        return ModifierFunction.IDENTITY;
    }
}
