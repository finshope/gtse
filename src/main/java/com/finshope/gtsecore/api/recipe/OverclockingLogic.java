package com.finshope.gtsecore.api.recipe;

import static com.gregtechceu.gtceu.api.recipe.OverclockingLogic.*;

public class OverclockingLogic {

    public static com.gregtechceu.gtceu.api.recipe.OverclockingLogic PERFECT_OVERCLOCK_SUBSECOND = (params,
                                                                                                    maxV) -> subSecondParallelOC(
                                                                                                            params,
                                                                                                            maxV,
                                                                                                            PERFECT_DURATION_FACTOR,
                                                                                                            STD_VOLTAGE_FACTOR);

    public static com.gregtechceu.gtceu.api.recipe.OverclockingLogic.OCResult subSecondParallelOC(com.gregtechceu.gtceu.api.recipe.OverclockingLogic.OCParams params,
                                                                                           long maxVoltage,
                                                                                           double durationFactor,
                                                                                           double voltageFactor) {
        double duration = params.duration();
        double eut = params.eut();
        int ocAmount = params.ocAmount();
        int maxParallels = params.maxParallels();

        double parallel = 1;
        boolean shouldParallel = false;
        int ocLevel = 0;
        double durationMultiplier = 1;

        boolean normlized = false;
        while (ocAmount-- > 0) {
            // Check if EUt can be multiplied again without going over the max
            double potentialEUt = eut * voltageFactor;
            if (potentialEUt > maxVoltage) break;

            // If we're already doing parallels or our duration would go below 20, try parallels
            if (shouldParallel || duration * durationFactor < 20) {
                // Check if parallels can be multiplied without going over the maximum
                double potentialParallel = parallel;

                if (!normlized) {
                    double factorToSecond = duration * durationFactor / 20;
                    potentialParallel = Math.ceil(potentialParallel / factorToSecond);
                    durationMultiplier *= (20 / duration);
                    normlized = true;
                } else {
                    potentialParallel /= durationFactor;
                }

                if (potentialParallel > maxParallels) break;
                parallel = potentialParallel;
                shouldParallel = true;
            } else {
                duration *= durationFactor;
                durationMultiplier *= durationFactor;
            }

            // Only set EUt after checking parallels - no need to OC if parallels would be too high
            eut = potentialEUt;
            ocLevel++;
        }

        return new com.gregtechceu.gtceu.api.recipe.OverclockingLogic.OCResult(Math.pow(voltageFactor, ocLevel),
                durationMultiplier, ocLevel, (int) parallel);
    }

    public static OCResult industrialHeatingCoilOC(OCParams params, long maxVoltage, int recipeTemp, int machineTemp) {
        double duration = (double) params.duration();
        double eut = (double) params.eut();
        int ocAmount = params.ocAmount();
        int maxParallels = params.maxParallels();
        double parallel = 1.0;
        boolean shouldParallel = false;
        int ocLevel = 0;

        double durationMultiplier;
        for (durationMultiplier = 1.0; ocAmount-- > 0; ++ocLevel) {
            double potentialEUt = eut * 4.0;
            if (potentialEUt > (double) maxVoltage) {
                break;
            }

            double dFactor = 0.25;
            if (!shouldParallel && !(duration * dFactor < 1.0)) {
                duration *= dFactor;
                durationMultiplier *= dFactor;
            } else {
                double pFactor = 4.0;
                double potentialParallel = parallel * pFactor;
                if (potentialParallel > (double) maxParallels) {
                    break;
                }

                if (potentialParallel > maxParallels) break;

                parallel = potentialParallel;
                shouldParallel = true;
            }

            eut = potentialEUt;
        }

        return new OCResult(Math.pow(4.0, (double) ocLevel), durationMultiplier, ocLevel, (int) parallel);
    }
}
