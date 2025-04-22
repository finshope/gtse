package com.finshope.gtsecore.common.machine.electric;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.defaultTankSizeFunction;

public class MobSimulatorMachine extends SimpleTieredMachine {
    static final List<SimpleParticleType> SimpleParticles = new ArrayList<>();

    static {
        // get all static field of class ParticleTypes which is ParticleOptions
        // and add them to SimpleParticles
        var fields = ParticleTypes.class.getDeclaredFields();
        for (var field : fields) {
            if (SimpleParticleType.class.isAssignableFrom(field.getType())) {
                try {
                    var particle = (SimpleParticleType) field.get(null);
                    if (particle == ParticleTypes.ELDER_GUARDIAN || particle == ParticleTypes.EXPLOSION_EMITTER) {
                        continue;
                    }
                    SimpleParticles.add(particle);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public MobSimulatorMachine(IMachineBlockEntity holder, int tier, Object... args) {
        super(holder, tier, defaultTankSizeFunction, args);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        // trigger once every 5 ticks
        if (getLevel().getGameTime() % 5 != 0) {
            return;
        }

        var level = getLevel();
        var pos = getPos();
        var recipeLogic = getRecipeLogic();
        if (recipeLogic == null || !recipeLogic.isWorking()) {
            return;
        }


        int index = 0;
        int radius = 4;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (index >= SimpleParticles.size()) {
                    index = 0;
                }
                var particle = SimpleParticles.get(index);
                // get random offset and random speed
                float offsetX = (float) (Math.random() - 0.5);
                float offsetY = (float) (Math.random() - 0.5);
                float offsetZ = (float) (Math.random() - 0.5);
                float speedX = (float) (Math.random() - 0.5) * 0.01f;
                float speedY = (float) (Math.random() - 0.5) * 0.01f;
                float speedZ = (float) (Math.random() - 0.5) * 0.01f;
                level.addParticle(particle, pos.getX() + dx + 0.5 + offsetX, pos.getY() + 1.5 + offsetY, pos.getZ() + dz + 0.5 + offsetZ, speedX, speedY, speedZ);
                index++;
            }
        }

    }

    @Override
    public void afterWorking() {
        super.afterWorking();


    }
}
