package com.finshope.gtsecore.api.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.Block;

import com.tterrag.registrate.util.entry.BlockEntry;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.gregtechceu.gtceu.common.data.GTBlocks.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class HullWorkableElectricMultiblockMachine extends WorkableElectricMultiblockMachine {

    public static BlockEntry<Block>[] MACHINE_CASING_ALL = new BlockEntry[15];

    static {
        MACHINE_CASING_ALL[0] = MACHINE_CASING_ULV;
        MACHINE_CASING_ALL[1] = MACHINE_CASING_LV;
        MACHINE_CASING_ALL[2] = MACHINE_CASING_MV;
        MACHINE_CASING_ALL[3] = MACHINE_CASING_HV;
        MACHINE_CASING_ALL[4] = MACHINE_CASING_EV;
        MACHINE_CASING_ALL[5] = MACHINE_CASING_IV;
        MACHINE_CASING_ALL[6] = MACHINE_CASING_LuV;
        MACHINE_CASING_ALL[7] = MACHINE_CASING_ZPM;
        MACHINE_CASING_ALL[8] = MACHINE_CASING_UV;
        MACHINE_CASING_ALL[9] = MACHINE_CASING_UHV;
        MACHINE_CASING_ALL[10] = MACHINE_CASING_UEV;
        MACHINE_CASING_ALL[11] = MACHINE_CASING_UIV;
        MACHINE_CASING_ALL[12] = MACHINE_CASING_UXV;
        MACHINE_CASING_ALL[13] = MACHINE_CASING_OpV;
        MACHINE_CASING_ALL[14] = MACHINE_CASING_MAX;
    }

    private int hullTier = 0;

    public HullWorkableElectricMultiblockMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    //////////////////////////////////////
    // *** Multiblock LifeCycle ***//
    //////////////////////////////////////
    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        var hullEntry = getMultiblockState().getMatchContext().get("HullEntry");
        if (hullEntry instanceof BlockEntry<?> be) {
            for (int i = 0; i < MACHINE_CASING_ALL.length; i++) {
                if (MACHINE_CASING_ALL[i] == be) {
                    this.hullTier = i;
                    break;
                }
            }
        } else {
            this.hullTier = 0;
        }
    }

    public int getHullTier() {
        return this.hullTier;
    }
}
