package com.finshope.gtsecore.common.items;

import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.common.data.GTBlockEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CompleteCardItemBehaviour implements IAddInformation, IInteractionItem {
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("behaviour.complete_card.complete_logic"));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // check if in server side
        if (context.getLevel().isClientSide()) {
            return InteractionResult.PASS;
        }

        System.out.println("CompleteCardItemBehaviour: useOn");

        var pos = context.getClickedPos();

        var level = context.getLevel();
        var recipeLogic = GTCapabilityHelper.getRecipeLogic(level, pos, null);
        if (recipeLogic != null && recipeLogic.isWorking()) {
            recipeLogic.setProgress(recipeLogic.getDuration());
            recipeLogic.onRecipeFinish();
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
