package com.finshope.gtsecore.common.machine.multiblock.steam;

import com.finshope.gtsecore.util.PositionUtil;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import net.minecraft.core.particles.ParticleTypes;

public class SteamOreWaherMachine extends IndustrialSteamParallelMultiblockMachine {
    public SteamOreWaherMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public void clientTick() {
        super.clientTick();

        var recipeLogic = getRecipeLogic();
        if (!recipeLogic.isWorking()) {
            return;
        }
        var level = getLevel();
        if (level == null) {
            return;
        }
        var pos = getPos();
        // get front facing
        var facing = getFrontFacing();
        int dy = 0;
        int dz = -2;
        for (int dx = -1; dx <= 1; dx++) {
            // add bubble particles where has water
            var blockPos = PositionUtil.OffsetWithDirection(facing, pos, dx, dy, dz);
            level.addParticle(
                    ParticleTypes.BUBBLE,
                    blockPos.getX() + 0.3 + Math.random() * 0.4,
                    blockPos.getY() + 0.6 + Math.random() * 0.4,
                    blockPos.getZ() + 0.3 + Math.random() * 0.4,
                    0, 0.01, 0);
            level.addParticle(
                    ParticleTypes.BUBBLE,
                    blockPos.getX() + 0.3 + Math.random() * 0.4,
                    blockPos.getY() + 0.6 + Math.random() * 0.4,
                    blockPos.getZ() + 0.3 + Math.random() * 0.4,
                    0, 0.005, 0);
        }
    }
}
