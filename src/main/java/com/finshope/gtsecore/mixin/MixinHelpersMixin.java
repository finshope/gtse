package com.finshope.gtsecore.mixin;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialStack;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTMaterialBlocks;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.core.MixinHelpers;
import com.gregtechceu.gtceu.core.mixins.BlockBehaviourAccessor;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Logger;

@Mixin(MixinHelpers.class)
public abstract class MixinHelpersMixin {

    @ModifyArg(method = "lambda$generateGTDynamicLoot$25", remap = false, at = @At(value = "INVOKE", remap = true, target = "Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;apply(Lnet/minecraft/world/level/storage/loot/functions/LootItemFunction$Builder;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer$Builder;", ordinal = 0), index = 0)
    private static LootItemFunction.Builder AllowFortune(LootItemFunction.Builder builder) {
        return ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE);
    }
//
//    @ModifyArg(method = "generateGTDynamicLoot", at = @At(value = "INVOKE", target = "Lcom/gregtechceu/gtceu/api/data/chemical/ChemicalHelper;get(Lcom/gregtechceu/gtceu/api/data/tag/TagPrefix;Lcom/gregtechceu/gtceu/api/data/chemical/material/Material;)Lnet/minecraft/world/item/ItemStack", ordinal = 0, remap = false), index = 0, remap = false)
//    private static TagPrefix generateGTDynamicLoot(TagPrefix ignoreTagPrefix, Material ignoreMaterial) {
//        return TagPrefix.gem;
//    }
//    @Inject(method = "lambda$generateGTDynamicLoot$25", at = @At(value = "INVOKE", target = "Lcom/gregtechceu/gtceu/api/data/chemical/ChemicalHelper;get(Lcom/gregtechceu/gtceu/api/data/tag/TagPrefix;Lcom/gregtechceu/gtceu/api/data/chemical/material/Material;)Lnet/minecraft/world/item/ItemStack;"), remap = false)
//    private static void generateGTDynamicLoot() {
//        GTCEu.LOGGER.info("testinvoke");
//    }

//    @Inject(method = "generateGTDynamicLoot", at = @At("HEAD"), cancellable = true, remap = false)
//    private static void generateGTDynamicLoot(Map<ResourceLocation, LootTable> lootTables, CallbackInfo cir) {
//        GTCEu.LOGGER.info("testhead");
//        cir.cancel();
//    }

}
