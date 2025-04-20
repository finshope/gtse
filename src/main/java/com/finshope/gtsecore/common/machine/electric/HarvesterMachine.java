package com.finshope.gtsecore.common.machine.electric;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.defaultTankSizeFunction;

public class HarvesterMachine extends SimpleTieredMachine {

    public HarvesterMachine(IMachineBlockEntity holder, int tier, Object... args) {
        super(holder, tier, defaultTankSizeFunction, args);
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        // Custom logic for the harvester machine can be added here
        System.out.println("HarvesterMachine: afterWorking");
        // try harvesting crops

        int harvest_radius = 4;
        var pos = getPos();
        var level = (ServerLevel) getLevel();
        // loop 9 x 9 behind the machine
        for (int x = -harvest_radius; x <= harvest_radius; x++) {
            for (int z = -harvest_radius; z <= harvest_radius; z++) {
                var blockPos = new BlockPos(pos.getX() + x, pos.getY() - 1, pos.getZ() + z);
                var blockState = level.getBlockState(blockPos);
                if (blockState.getBlock() instanceof CropBlock crop) {
                    // make sure crop is fully grown
                    final int currentAge = crop.getAge(blockState);
                    final int maxAge = crop.getMaxAge();

                    if (0 == maxAge || currentAge < maxAge) {
                        continue;
                    }

                    List<ItemStack> drops = Block.getDrops(blockState, level, blockPos, null);

                    for (ItemStack drop : drops) {
                        if (!canFillOutput(this.exportItems, drop)) {
                            return;
                        }
                    }

                    BlockState modified = crop.getStateForAge(0);
                    boolean updated = level.setBlockAndUpdate(blockPos, modified);
                    if (!updated) {
                        return;
                    }

                    for (ItemStack drop : drops) {

                        if (drop.isEmpty()) {
                            continue;
                        }

                        boolean result = insertItemOrDiscard(this.exportItems, drop);
                        if (!result) {
                            return;
                        }
                    }
                }
            }
        }

    }

    private boolean insertItemOrDiscard(NotifiableItemStackHandler itemHandler, ItemStack itemStack) {
        ItemStack progress = itemStack;
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            progress = itemHandler.insertItemInternal(slot, progress, false);
            if (progress.isEmpty()) {
                break;
            }
        }
        return progress.isEmpty();
    }

    private boolean canFillOutput(NotifiableItemStackHandler itemHandler, ItemStack stack) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (itemHandler.insertItemInternal(i, stack, true).getCount() < stack.getCount())
                return true;
        }

        return false;
    }
}
