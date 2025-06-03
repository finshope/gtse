package com.finshope.gtsecore.common.machine.recipelogic;

import com.finshope.gtsecore.common.machine.multiblock.electric.PersonalBeaconMachine;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;

import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionUtils;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class PersonalBeaconRecipeLogic extends RecipeLogic {

    @Setter
    @Getter
    private int voltageTier;
    protected final PersonalBeaconMachine beaconMachine;

    @Persisted
    @Getter
    protected final Map<CompoundTag, Integer> potionEffects = new HashMap<>();

    protected final int updateInterval = 20;
    protected final int eachApplyDuration = 20 * 30; // 30 seconds

    public PersonalBeaconRecipeLogic(IRecipeLogicMachine machine, PersonalBeaconMachine beaconMachine) {
        super(machine);
        this.beaconMachine = beaconMachine;
    }

    @Override
    public void serverTick() {
        var machine = getMachine();

        if (machine.getOffsetTimer() % updateInterval == 0) {
            updateDuration();
        }

        if (isSuspend() || !(machine.getLevel() instanceof ServerLevel)) {
            setStatus(Status.IDLE);
            return;
        }

        if (!beaconMachine.drainInput(true) || !beaconMachine.drainPotion(true)) {
            setStatus(Status.IDLE);
            return;
        }

        beaconMachine.drainInput(false);

        // apply potion effects every 20 ticks
        if (machine.getOffsetTimer() % 20 == 0) {
            var playerUUID = machine.getOwnerUUID();
            var level = getMachine().getLevel();
            if (level != null && playerUUID != null) {
                var player = level.getPlayerByUUID(playerUUID);
                if (player != null) {
                    beaconMachine.drainPotion(false);
                    int tier = beaconMachine.getOverclockTier();
                    int amplifierBoost = tier - GTValues.LV;
                    Set<MobEffectInstance> effects = new HashSet<>();
                    for (Map.Entry<CompoundTag, Integer> entry : potionEffects.entrySet()) {
                        CompoundTag potionTag = entry.getKey();
                        effects.addAll(PotionUtils.getAllEffects(potionTag));
                    }
                    if (effects.isEmpty()) {
                        setStatus(Status.IDLE);
                        return;
                    }

                    setStatus(Status.WORKING);
                    // Apply the potion effects to the player
                    for (MobEffectInstance effect : effects) {
                        player.addEffect(new MobEffectInstance(effect.getEffect(), eachApplyDuration,
                                effect.getAmplifier() + amplifierBoost, effect.isAmbient(), effect.isVisible(),
                                effect.showIcon()));
                    }
                }
            }
        }
    }

    private void updateDuration() {
        // Update the duration of each potion effect based on the current state
        for (Map.Entry<CompoundTag, Integer> entry : potionEffects.entrySet()) {
            CompoundTag potionTag = entry.getKey();
            int duration = entry.getValue();
            if (duration - updateInterval > 0) {
                // Decrease the duration by 1 second
                potionEffects.put(potionTag, duration - updateInterval);
            } else {
                // Remove the effect if duration is zero
                potionEffects.remove(potionTag);
            }
        }
    }

    public boolean hasPotionEffect(CompoundTag potionTag) {
        return potionEffects.containsKey(potionTag);
    }

    public void clearPotionEffects() {
        potionEffects.clear();
    }

    public boolean addPotionEffect(CompoundTag potionTag, int duration) {
        if (hasPotionEffect(potionTag)) {
            // merge the duration if the effect already exists
            int existingDuration = potionEffects.get(potionTag);
            // add modulo the update interval to ensure it syncs with the update cycle
            potionEffects.put(potionTag, (int) Math.min((long) existingDuration + duration + updateInterval -
                    getMachine().getOffsetTimer() % updateInterval, Integer.MAX_VALUE));
        }
        potionEffects.put(potionTag, duration);
        return true;
    }
}
