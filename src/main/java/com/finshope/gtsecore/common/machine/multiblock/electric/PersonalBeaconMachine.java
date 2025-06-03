package com.finshope.gtsecore.common.machine.multiblock.electric;

import com.finshope.gtsecore.common.machine.recipelogic.PersonalBeaconRecipeLogic;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.transfer.fluid.FluidHandlerList;
import com.gregtechceu.gtceu.common.fluid.potion.PotionFluid;
import com.gregtechceu.gtceu.utils.GTTransferUtils;
import com.gregtechceu.gtceu.utils.GTUtil;

import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.gregtechceu.gtceu.api.recipe.OverclockingLogic.STD_VOLTAGE_FACTOR;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PersonalBeaconMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            WorkableElectricMultiblockMachine.class,
            WorkableMultiblockMachine.MANAGED_FIELD_HOLDER);
    @Nullable
    protected EnergyContainerList energyContainer;
    @Nullable
    protected FluidHandlerList inputFluidInventory;

    public PersonalBeaconMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    public static ManagedFieldHolder getManagedFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    protected RecipeLogic createRecipeLogic(Object... args) {
        return new PersonalBeaconRecipeLogic(this, this);
    }

    @Override
    public PersonalBeaconRecipeLogic getRecipeLogic() {
        return (PersonalBeaconRecipeLogic) super.getRecipeLogic();
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        initializeAbilities();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        var recipeLogic = getRecipeLogic();
        if (recipeLogic != null) {
            recipeLogic.clearPotionEffects();
        }
    }

    private void initializeAbilities() {
        List<IEnergyContainer> energyContainers = new ArrayList<>();
        List<IFluidHandler> fluidTanks = new ArrayList<>();
        Map<Long, IO> ioMap = getMultiblockState().getMatchContext().getOrCreate("ioMap", Long2ObjectMaps::emptyMap);
        for (IMultiPart part : getParts()) {
            IO io = ioMap.getOrDefault(part.self().getPos().asLong(), IO.BOTH);
            if (io == IO.NONE) continue;

            var handlerLists = part.getRecipeHandlers();
            for (var handlerList : handlerLists) {
                if (!handlerList.isValid(io)) continue;
                handlerList.getCapability(EURecipeCapability.CAP).stream()
                        .filter(IEnergyContainer.class::isInstance)
                        .map(IEnergyContainer.class::cast)
                        .forEach(energyContainers::add);
                handlerList.getCapability(FluidRecipeCapability.CAP).stream()
                        .filter(IFluidHandler.class::isInstance)
                        .map(IFluidHandler.class::cast)
                        .forEach(fluidTanks::add);
            }
        }
        this.energyContainer = new EnergyContainerList(energyContainers);
        this.inputFluidInventory = new FluidHandlerList(fluidTanks);

        getRecipeLogic().setVoltageTier(GTUtil.getTierByVoltage(this.energyContainer.getInputVoltage()));
    }

    public int getEnergyTier() {
        if (energyContainer == null) return this.tier;
        return GTUtil.getFloorTierByVoltage(energyContainer.getInputVoltage());
    }

    @Override
    public long getMaxVoltage() {
        return GTValues.V[getEnergyTier()];
    }

    @Override
    public int getOverclockTier() {
        return GTUtil.getFloorTierByVoltage(getMaxVoltage());
    }

    @Override
    public long getOverclockVoltage() {
        long power = (long) (GTValues.VA[GTValues.LV] * Math.pow(STD_VOLTAGE_FACTOR, getOverclockTier() - GTValues.LV));
        return Math.min(power, super.getOverclockVoltage());
    }

    public boolean drainInput(boolean simulate) {
        // drain energy
        if (energyContainer != null && energyContainer.getEnergyStored() > 0) {
            long energyToDrain = getOverclockVoltage();
            long resultEnergy = energyContainer.getEnergyStored() - energyToDrain;
            if (resultEnergy >= 0L && resultEnergy <= energyContainer.getEnergyCapacity()) {
                if (!simulate) {
                    energyContainer.changeEnergy(-energyToDrain);
                }
            } else {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }

    public boolean drainPotion(boolean simulate) {
        int potionFluidAmount = 100;
        int potionDuration = 20 * 120; // 120 seconds

        // drain fluid
        if (inputFluidInventory != null && inputFluidInventory.handlers.length > 0) {
            int tanks = inputFluidInventory.getTanks();
            for (int i = 0; i < tanks; i++) {
                FluidStack fluidStack = inputFluidInventory.getFluidInTank(i);
                if (fluidStack != FluidStack.EMPTY && fluidStack.getFluid() instanceof PotionFluid potionFluid) {
                    var potionStack = fluidStack.copy();
                    potionStack.setAmount(potionFluidAmount);
                    if (fluidStack.getAmount() > potionStack.getAmount()) {
                        if (simulate) {
                            return true;
                        }
                        var tag = fluidStack.getOrCreateTag();
                        if (!getRecipeLogic().hasPotionEffect(tag)) {
                            GTTransferUtils.drainFluidAccountNotifiableList(inputFluidInventory, potionStack,
                                    IFluidHandler.FluidAction.EXECUTE);
                            getRecipeLogic().addPotionEffect(tag, potionDuration);
                        }
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) {
            var potions = getRecipeLogic().getPotionEffects();
            for (var potionEntry : potions.entrySet()) {
                var name = PotionUtils.getPotion(potionEntry.getKey()).getName("");
                textList.add(Component.literal(
                        "%s: %s".formatted(name, durationToString(potionEntry.getValue()))));
            }
        }
    }

    private String durationToString(Integer value) {
        if (value < 20) {
            return "%d ticks".formatted(value);
        } else {
            return "%d seconds".formatted(value / 20);
        }
    }
}
