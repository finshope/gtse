package com.finshope.gtsecore.common.data;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.CoilWorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.utils.GTUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;

import static com.finshope.gtsecore.api.recipe.OverclockingLogic.*;
import static com.gregtechceu.gtceu.api.recipe.OverclockingLogic.*;
import static com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic.getMaxByInput;
import static com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic.limitByOutputMerging;

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

    public static @NotNull ModifierFunction createModifier(@NotNull MetaMachine metaMachine,
                                                           @NotNull GTRecipe recipe, double durationFactor,
                                                           double volatageFactor) {
        if (!(metaMachine instanceof WorkableElectricMultiblockMachine machine)) {
            return RecipeModifier.nullWrongType(WorkableElectricMultiblockMachine.class, metaMachine);
        }

        long EUt = Math.abs(RecipeHelper.getRealEUt(recipe).voltage());
        if (EUt == 0) return ModifierFunction.IDENTITY;

        int recipeTier = GTUtil.getTierByVoltage(EUt);
        int maximumTier = GTUtil.getOCTierByVoltage(machine.getOverclockVoltage());
        int OCs = maximumTier - recipeTier;
        if (recipeTier == GTValues.ULV) OCs--;
        if (OCs == 0) return ModifierFunction.IDENTITY;

        int maxParallels;
        maxParallels = getParallelAmountFast(machine, recipe, Integer.MAX_VALUE);

        OverclockingLogic.OCParams params = new OverclockingLogic.OCParams(EUt, recipe.duration, OCs, maxParallels);
        OverclockingLogic.OCResult result = subSecondParallelOC(params, machine.getOverclockVoltage(),
                durationFactor, volatageFactor);
        return result.toModifier();
    }

    public static @NotNull ModifierFunction fastPerfectOcSubsecondParallel(@NotNull MetaMachine metaMachine,
                                                                           @NotNull GTRecipe recipe) {
        return createModifier(metaMachine, recipe, PERFECT_DURATION_FACTOR, STD_VOLTAGE_FACTOR);
    }

    public static @NotNull ModifierFunction fastNonPerfectOcSubsecondParallel(@NotNull MetaMachine metaMachine,
                                                                              @NotNull GTRecipe recipe) {
        return createModifier(metaMachine, recipe, STD_DURATION_FACTOR, STD_VOLTAGE_FACTOR);
    }

    public static int getParallelAmountFast(MetaMachine machine, GTRecipe recipe, int parallelLimit) {
        if (parallelLimit <= 1) return parallelLimit;
        if (!(machine instanceof IRecipeLogicMachine rlm)) return 1;
        // First check if we are limited by recipe inputs. This can short circuit a lot of consecutive checking
        int maxInputMultiplier = getMaxByInput(rlm, recipe, parallelLimit, Collections.emptyList());
        if (maxInputMultiplier == 0) return 0;

        // Simulate the merging of the maximum amount of recipes that can be run with these items
        // and limit by the amount we can successfully merge
        return limitByOutputMerging(rlm, recipe, maxInputMultiplier, rlm::canVoidRecipeOutputs,
                Collections.emptyList());
    }
    //
    // public static int limitByOutputMergingFast(IRecipeCapabilityHolder holder, GTRecipe recipe, int parallelLimit,
    // Predicate<RecipeCapability<?>> canVoid) {
    // int minimum = parallelLimit;
    // for (RecipeCapability<?> cap : recipe.outputs.keySet()) {
    // if (canVoid.test(cap) || !cap.doMatchInRecipe()) {
    // continue;
    // }
    // // Check both normal item outputs and chanced item outputs
    // if (!recipe.getOutputContents(cap).isEmpty()) {
    // int limit = limitByOutputMerging(holder, recipe, parallelLimit);
    // // If we are not voiding, and cannot fit any items, return 0
    // if (limit == 0) {
    // return 0;
    // }
    // minimum = Math.min(minimum, limit);
    // }
    // }
    // for (RecipeCapability<?> cap : recipe.tickOutputs.keySet()) {
    // if (canVoid.test(cap) || !cap.doMatchInRecipe()) {
    // continue;
    // }
    // // Check both normal item outputs and chanced item outputs
    // if (!recipe.getTickOutputContents(cap).isEmpty()) {
    // int limit;
    // if (cap instanceof ItemRecipeCapability itemCap) {
    // limit = limitParallelFast(recipe, holder, parallelLimit);
    // } else {
    // limit = cap.limitMaxParallelByOutput(holder, recipe, parallelLimit, true);
    // }
    // // If we are not voiding, and cannot fit any items, return 0
    // if (limit == 0) {
    // return 0;
    // }
    // minimum = Math.min(minimum, limit);
    // }
    // }
    // return minimum;
    // }
    //
    // public static int limitParallelFast(GTRecipe recipe, IRecipeCapabilityHolder holder, int multiplier) {
    // if (holder instanceof ItemRecipeCapability.ICustomParallel p) return p.limitParallel(recipe, multiplier);
    //
    // int minMultiplier = 0;
    // int maxMultiplier = multiplier;
    //
    // FastOverlayedItemHandler itemHandler = new FastOverlayedItemHandler(new CombinedInvWrapper(
    // holder.getCapabilitiesFlat(IO.OUT, ItemRecipeCapability.CAP).stream()
    // .filter(IItemHandlerModifiable.class::isInstance)
    // .map(IItemHandlerModifiable.class::cast)
    // .toArray(IItemHandlerModifiable[]::new)));
    //
    // Object2IntMap<ItemStack> recipeOutputs = GTHashMaps
    // .fromItemStackCollection(recipe.getOutputContents(ItemRecipeCapability.CAP)
    // .stream()
    // .map(content -> ItemRecipeCapability.CAP.of(content.getContent()))
    // .filter(ingredient -> !ingredient.isEmpty())
    // .map(ingredient -> ingredient.getItems()[0])
    // .toList());
    //
    // while (minMultiplier != maxMultiplier) {
    // itemHandler.reset();
    //
    // int returnedAmount = 0;
    // int amountToInsert;
    //
    // for (Object2IntMap.Entry<ItemStack> entry : recipeOutputs.object2IntEntrySet()) {
    // // Since multiplier starts at Int.MAX, check here for integer overflow
    // if (entry.getIntValue() != 0 && multiplier > Integer.MAX_VALUE / entry.getIntValue()) {
    // amountToInsert = Integer.MAX_VALUE;
    // } else {
    // amountToInsert = entry.getIntValue() * multiplier;
    // }
    // returnedAmount = itemHandler.insertStackedItemStack(entry.getKey(), amountToInsert);
    // if (returnedAmount > 0) {
    // break;
    // }
    // }
    //
    // int[] bin = ParallelLogic.adjustMultiplier(returnedAmount == 0, minMultiplier, multiplier, maxMultiplier);
    // minMultiplier = bin[0];
    // multiplier = bin[1];
    // maxMultiplier = bin[2];
    //
    // }
    // return multiplier;
    // }
}
