package com.finshope.gtsecore.common.machine.electric;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;

import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.defaultTankSizeFunction;

public class MobSimulatorMachine extends SimpleTieredMachine {

    public MobSimulatorMachine(IMachineBlockEntity holder, int tier, Object... args) {
        super(holder, tier, defaultTankSizeFunction, args);
    }
}
