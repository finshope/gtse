package com.finshope.gtsecore.mixin;

import com.gregtechceu.gtceu.core.MixinHelpers;

import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MixinHelpers.class)
public abstract class MixinHelpersMixin {

    /**
     * Allow fortune to be applied to GT ores.
     */
    @ModifyArg(method = "lambda$generateGTDynamicLoot$25",
               remap = false,
               at = @At(value = "INVOKE",
                        remap = true,
                        target = "Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;apply(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;",
                        ordinal = 0),
               index = 0)
    private static LootItemFunction.Builder AllowFortune(LootItemFunction.Builder builder) {
        return ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE);
    }
}
