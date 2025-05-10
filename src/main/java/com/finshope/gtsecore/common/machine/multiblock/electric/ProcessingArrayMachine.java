package com.finshope.gtsecore.common.machine.multiblock.electric;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.BlockableSlotWidget;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.*;
import com.gregtechceu.gtceu.api.machine.feature.IMachineLife;
import com.gregtechceu.gtceu.api.machine.multiblock.TieredWorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.utils.GTUtil;

import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author KilaBash
 * @date 2023/7/23
 * @implNote ProcessingArrayMachine
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ProcessingArrayMachine extends TieredWorkableElectricMultiblockMachine implements IMachineLife {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            ProcessingArrayMachine.class, TieredWorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DescSynced
    public final NotifiableItemStackHandler machineStorage;
    // runtime
    @Nullable
    private GTRecipeType[] recipeTypeCache;

    public ProcessingArrayMachine(IMachineBlockEntity holder, int tier, Object... args) {
        super(holder, tier, args);
        this.machineStorage = createMachineStorage(args);
    }

    //////////////////////////////////////
    // ***** Initialization ******//
    //////////////////////////////////////
    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    protected NotifiableItemStackHandler createMachineStorage(Object... args) {
        var storage = new NotifiableItemStackHandler(this, 1, IO.NONE, IO.NONE, slots -> new CustomItemStackHandler(1) {

            @Override
            public int getSlotLimit(int slot) {
                return getMachineLimit(getDefinition().getTier());
            }
        });
        storage.setFilter(this::isMachineStack);
        return storage;
    }

    protected boolean isMachineStack(ItemStack itemStack) {
        if (itemStack.getItem() instanceof MetaMachineItem metaMachineItem) {
            MachineDefinition definition = metaMachineItem.getDefinition();

            if (definition instanceof MultiblockMachineDefinition) {
                return false;
            }

            var recipeTypes = definition.getRecipeTypes();
            if (recipeTypes == null) {
                return false;
            }
            for (GTRecipeType type : recipeTypes) {
                if (type != GTRecipeTypes.DUMMY_RECIPES) {
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    public MachineDefinition getMachineDefinition() {
        if (machineStorage.storage.getStackInSlot(0).getItem() instanceof MetaMachineItem metaMachineItem) {
            return metaMachineItem.getDefinition();
        }
        return null;
    }

    @Override
    @NotNull
    public GTRecipeType[] getRecipeTypes() {
        if (recipeTypeCache == null) {
            var definition = getMachineDefinition();
            recipeTypeCache = definition == null ? null : definition.getRecipeTypes();
        }
        if (recipeTypeCache == null) {
            recipeTypeCache = new GTRecipeType[] { GTRecipeTypes.DUMMY_RECIPES };
        }
        return recipeTypeCache;
    }

    @NotNull
    @Override
    public GTRecipeType getRecipeType() {
        return getRecipeTypes()[getActiveRecipeType()];
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!isRemote()) {
            machineStorage.addChangedListener(this::onMachineChanged);
        }
    }

    protected void onMachineChanged() {
        recipeTypeCache = null;
        if (isFormed) {
            if (getRecipeLogic().getLastRecipe() != null) {
                getRecipeLogic().markLastRecipeDirty();
            }
            getRecipeLogic().updateTickSubscription();
        }
    }

    @Override
    public void onMachineRemoved() {
        clearInventory(machineStorage.storage);
    }

    //////////////////////////////////////
    // ******* Recipe Logic *******//
    //////////////////////////////////////

    /**
     * For available recipe tier, decided by the held machine.
     */
    @Override
    public int getTier() {
        var definition = getMachineDefinition();
        return definition == null ? 0 : definition.getTier();
    }

    @Override
    public long getMaxVoltage() {
        if (this.energyContainer == null) {
            this.energyContainer = getEnergyContainer();
        }
        if (energyContainer.getOutputVoltage() > 0) {
            // Generators
            long voltage = energyContainer.getOutputVoltage();
            long amperage = energyContainer.getOutputAmperage();
            if (amperage == 1) {
                // Amperage is 1 when the energy is not exactly on a tier.
                // The voltage for recipe search is always on tier, so take the closest lower tier.
                // List check is done because single hatches will always be a "clean voltage," no need
                // for any additional checks.
                return GTValues.V[Math.min(GTUtil.getFloorTierByVoltage(voltage), GTValues.MAX)];
            } else {
                return voltage;
            }
        } else {
            // Machines
            long highestVoltage = energyContainer.getHighestInputVoltage();
            if (energyContainer.getNumHighestInputContainers() > 1) {
                // allow tier + 1 if there are multiple hatches present at the highest tier
                int tier = GTUtil.getTierByVoltage(highestVoltage);
                return GTValues.V[Math.min(tier + 1, GTValues.MAX)];
            } else {
                return highestVoltage;
            }
        }
    }

    @Override
    public int getOverclockTier() {
        MachineDefinition machineDefinition = getMachineDefinition();
        int machineTier = machineDefinition == null ? getDefinition().getTier() :
                Math.min(getDefinition().getTier(), machineDefinition.getTier());
        return Math.min(machineTier, GTUtil.getTierByVoltage(getMaxVoltage()));
    }

    @Override
    public int getMinOverclockTier() {
        return getOverclockTier();
    }

    @Override
    public int getMaxOverclockTier() {
        return getOverclockTier();
    }

    @Override
    public long getOverclockVoltage() {
        if (this.energyContainer == null) {
            this.energyContainer = this.getEnergyContainer();
        }

        long voltage;
        long amperage;
        if (this.energyContainer.getInputVoltage() > this.energyContainer.getOutputVoltage()) {
            voltage = this.energyContainer.getInputVoltage();
            amperage = this.energyContainer.getInputAmperage();
        } else {
            voltage = this.energyContainer.getOutputVoltage();
            amperage = this.energyContainer.getOutputAmperage();
        }

        return amperage == 1L ? GTValues.VEX[GTUtil.getFloorTierByVoltage(voltage)] : voltage;
    }

    @Nullable
    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine,
                                                  @NotNull GTRecipe recipe) {
        if (machine instanceof ProcessingArrayMachine processingArray &&
                processingArray.machineStorage.storage.getStackInSlot(0).getCount() > 0) {
            if (RecipeHelper.getRecipeEUtTier(recipe) > processingArray.getTier())
                return ModifierFunction.NULL;

            long eut = RecipeHelper.getInputEUt(recipe);
            if (eut == 0) {
                eut = RecipeHelper.getOutputEUt(recipe);
            }
            int parallelLimit = Math.min(
                    processingArray.machineStorage.storage.getStackInSlot(0).getCount(),
                    (int) (processingArray.getMaxVoltage() / eut));

            if (parallelLimit <= 0)
                return ModifierFunction.NULL;

            var parallels = Math.min(parallelLimit, getMachineLimit(machine.getDefinition().getTier()));
            if (parallels == 1) return ModifierFunction.IDENTITY;
            return ModifierFunction.builder()
                    .modifyAllContents(ContentModifier.multiplier(parallels))
                    .eutMultiplier(parallels)
                    .parallels(parallels)
                    .build();
        }
        return ModifierFunction.NULL;
    }

    // @Override
    // public Map<RecipeCapability<?>, Integer> getOutputLimits() {
    // if (getMachineDefinition() != null) {
    // return getMachineDefinition().getRecipeOutputLimits();
    // }
    // return GTRegistries.RECIPE_CAPABILITIES.values().stream().map(key -> Map.entry(key, 0))
    // .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    // }

    //////////////////////////////////////
    // ******** Gui ********//
    //////////////////////////////////////

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isActive()) {
            textList.add(Component.translatable("gtceu.machine.machine_hatch.locked")
                    .withStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
        }
    }

    @Override
    public Widget createUIWidget() {
        var widget = super.createUIWidget();
        if (widget instanceof WidgetGroup group) {
            var size = group.getSize();
            group.addWidget(
                    new BlockableSlotWidget(machineStorage.storage, 0, size.width - 30, size.height - 30, true, true)
                            .setIsBlocked(() -> isActive())
                            .setBackground(GuiTextures.SLOT));
        }
        return widget;
    }

    //////////////////////////////////////
    // ******** Structure ********//
    //////////////////////////////////////
    public static Block getCasingState(int tier) {
        if (tier <= GTValues.IV) {
            return GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get();
        } else {
            return GTBlocks.CASING_HSSE_STURDY.get();
        }
    }

    public static int getMachineLimit(Integer tier) {
        return tier <= GTValues.IV ? 16 : 64;
    }
}
