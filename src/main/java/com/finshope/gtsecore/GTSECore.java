package com.finshope.gtsecore;

import com.finshope.gtsecore.client.renderer.GTSERenderers;
import com.finshope.gtsecore.common.data.GTSECreativeModeTabs;
import com.finshope.gtsecore.common.data.GTSEItems;
import com.finshope.gtsecore.common.data.GTSEMachines;
import com.finshope.gtsecore.common.data.GTSERecipeTypes;
import com.finshope.gtsecore.common.vanilla.ItemProperties;
import com.finshope.gtsecore.config.GTSEConfig;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialRegistryEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.event.PostMaterialEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.sound.SoundEntry;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.finshope.gtsecore.api.registries.GTSERegistires.REGISTRATE;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Diamond;

@Mod(GTSECore.MOD_ID)
public class GTSECore {

    static {
        REGISTRATE.creativeModeTab(() -> GTSECreativeModeTabs.GTSE);
    }

    public static final String MOD_ID = "gtse";
    public static final Logger LOGGER = LogManager.getLogger();

    public GTSECore() {
        init();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        modEventBus.addListener(this::addMaterialRegistries);
        modEventBus.addListener(this::addMaterials);
        modEventBus.addListener(this::modifyMaterials);

        modEventBus.addGenericListener(GTRecipeType.class, this::registerRecipeTypes);
        modEventBus.addGenericListener(MachineDefinition.class, this::registerMachines);
        modEventBus.addGenericListener(SoundEntry.class, this::registerSounds);

        // Most other events are fired on Forge's bus.
        // If we want to use annotations to register event listeners,
        // we need to register our object like this!
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void init() {
        GTSEConfig.init();
        GTSEItems.init();
        ItemProperties.init();
        GTSERenderers.init();
        REGISTRATE.registerRegistrate();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("Hello from common setup! This is *after* registries are done, so we can do this:");
            LOGGER.info("Look, I found a {}!", Items.DIAMOND);
        });
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Hey, we're on Minecraft version {}!", Minecraft.getInstance().getLaunchedVersion());
    }

    /**
     * Create a ResourceLocation in the format "modid:path"
     *
     * @return ResourceLocation with the namespace of your mod
     */
    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    /**
     * Create a material manager for your mod using GT's API.
     * You MUST have this if you have custom materials.
     * Remember to register them not to GT's namespace, but your own.
     *
     */
    private void addMaterialRegistries(MaterialRegistryEvent event) {
        GTCEuAPI.materialManager.createRegistry(GTSECore.MOD_ID);
    }

    /**
     * You will also need this for registering custom materials
     * Call init() from your Material class(es) here
     *
     */
    private void addMaterials(MaterialEvent event) {
        // CustomMaterials.init();
    }

    /**
     * (Optional) Used to modify pre-existing materials from GregTech
     *
     */
    private void modifyMaterials(PostMaterialEvent event) {
        // CustomMaterials.modify();
        Diamond.getProperty(PropertyKey.DUST).setBurnTime(512 * 10 * 20); // 512 items
    }

    /**
     * Used to register your own new RecipeTypes.
     * Call init() from your RecipeType class(es) here
     *
     */
    private void registerRecipeTypes(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event) {
        GTSERecipeTypes.init();
    }

    /**
     * Used to register your own new RecipeTypes.
     * Call init() from your Machine class(es) here
     *
     */
    private void registerMachines(GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event) {
        GTSEMachines.init();
    }

    /**
     * Used to register your own new sounds
     * Call init from your Sound class(es) here
     *
     */
    public void registerSounds(GTCEuAPI.RegisterEvent<ResourceLocation, SoundEntry> event) {
        // CustomSounds.init();
    }
}
