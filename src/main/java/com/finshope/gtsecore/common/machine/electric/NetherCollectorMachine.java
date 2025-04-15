package com.finshope.gtsecore.common.machine.electric;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;

import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.defaultTankSizeFunction;

public class NetherCollectorMachine extends SimpleTieredMachine {

    public NetherCollectorMachine(IMachineBlockEntity holder, int tier, Object... args) {
        super(holder, tier, defaultTankSizeFunction, args);
    }

}
