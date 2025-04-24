package com.finshope.gtsecore.common.machine.multiblock.steam;

import com.finshope.gtsecore.config.GTSEConfig;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.common.machine.multiblock.steam.SteamParallelMultiblockMachine;

public class IndustrialSteamParallelMultiblockMachine extends SteamParallelMultiblockMachine {
    public IndustrialSteamParallelMultiblockMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, GTSEConfig.INSTANCE.server.industrialSteamMachineMaxParallels);
    }
}
