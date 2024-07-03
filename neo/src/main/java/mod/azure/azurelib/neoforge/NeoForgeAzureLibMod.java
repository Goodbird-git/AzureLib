package mod.azure.azurelib.neoforge;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.common.internal.common.AzureLibMod;
import mod.azure.azurelib.common.internal.common.blocks.TickingLightBlock;
import mod.azure.azurelib.common.internal.common.blocks.TickingLightEntity;
import mod.azure.azurelib.common.internal.common.config.AzureLibConfig;
import mod.azure.azurelib.common.internal.common.config.format.ConfigFormats;
import mod.azure.azurelib.common.internal.common.config.io.ConfigIO;
import mod.azure.azurelib.common.internal.common.network.packet.SendConfigDataPacket;
import mod.azure.azurelib.neoforge.platform.NeoForgeAzureLibNetwork;
import mod.azure.azurelib.neoforge.platform.NeoForgeCommonRegistry;
import mod.azure.azurelib.sblforked.SBLConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@Mod(AzureLib.MOD_ID)
public final class NeoForgeAzureLibMod {

    public static final DeferredRegister.DataComponents DATA_COMPONENTS_REGISTER = DeferredRegister.createDataComponents(
            AzureLib.MOD_ID);

    public NeoForgeAzureLibMod(IEventBus modEventBus) {
        AzureLib.initialize();
        AzureLibMod.initRegistry();
        NeoForgeAzureLibNetwork.init(modEventBus);
        DATA_COMPONENTS_REGISTER.register(modEventBus);
        if (NeoForgeCommonRegistry.blockEntityTypeDeferredRegister != null)
            NeoForgeCommonRegistry.blockEntityTypeDeferredRegister.register(modEventBus);
        if (NeoForgeCommonRegistry.blockDeferredRegister != null)
            NeoForgeCommonRegistry.blockDeferredRegister.register(modEventBus);
        if (NeoForgeCommonRegistry.entityTypeDeferredRegister != null)
            NeoForgeCommonRegistry.entityTypeDeferredRegister.register(modEventBus);
        if (NeoForgeCommonRegistry.armorMaterialDeferredRegister != null)
            NeoForgeCommonRegistry.armorMaterialDeferredRegister.register(modEventBus);
        if (NeoForgeCommonRegistry.itemDeferredRegister != null)
            NeoForgeCommonRegistry.itemDeferredRegister.register(modEventBus);
        if (NeoForgeCommonRegistry.soundEventDeferredRegister != null)
            NeoForgeCommonRegistry.soundEventDeferredRegister.register(modEventBus);
        if (NeoForgeCommonRegistry.menuTypeDeferredRegister != null)
            NeoForgeCommonRegistry.menuTypeDeferredRegister.register(modEventBus);
        if (NeoForgeCommonRegistry.structureTypeDeferredRegister != null)
            NeoForgeCommonRegistry.structureTypeDeferredRegister.register(modEventBus);
        if (NeoForgeCommonRegistry.particleTypeDeferredRegister != null)
            NeoForgeCommonRegistry.particleTypeDeferredRegister.register(modEventBus);
        if (NeoForgeCommonRegistry.creativeModeTabDeferredRegister != null)
            NeoForgeCommonRegistry.creativeModeTabDeferredRegister.register(modEventBus);
        if (NeoForgeCommonRegistry.statusEffectDeferredRegister != null)
            NeoForgeCommonRegistry.statusEffectDeferredRegister.register(modEventBus);
        if (NeoForgeCommonRegistry.fluidDeferredRegister != null)
            NeoForgeCommonRegistry.fluidDeferredRegister.register(modEventBus);
        AzureLibMod.config = AzureLibMod.registerConfig(AzureLibConfig.class, ConfigFormats.json()).getConfigInstance();
        modEventBus.addListener(this::init);
        modEventBus.addListener(this::registerMessages);
        SBLConstants.SBL_LOADER.init(modEventBus);
    }

    private void init(FMLCommonSetupEvent event) {
        ConfigIO.FILE_WATCH_MANAGER.startService();
    }

    public void registerMessages(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(AzureLib.MOD_ID);

        registrar.playToClient(SendConfigDataPacket.TYPE, SendConfigDataPacket.CODEC, (msg, ctx) -> {});
    }
}
