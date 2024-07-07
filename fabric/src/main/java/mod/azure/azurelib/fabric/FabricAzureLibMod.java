package mod.azure.azurelib.fabric;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.common.internal.common.AzureLibMod;
import mod.azure.azurelib.common.internal.common.blocks.TickingLightBlock;
import mod.azure.azurelib.common.internal.common.blocks.TickingLightEntity;
import mod.azure.azurelib.common.internal.common.config.AzureLibConfig;
import mod.azure.azurelib.common.internal.common.config.format.ConfigFormats;
import mod.azure.azurelib.common.internal.common.config.io.ConfigIO;
import mod.azure.azurelib.common.internal.common.network.packet.*;
import mod.azure.azurelib.common.platform.services.AzureLibNetwork;
import mod.azure.azurelib.sblforked.SBLConstants;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

public final class FabricAzureLibMod implements ModInitializer {

    @Override
    public void onInitialize() {
        SBLConstants.SBL_LOADER.init(null);
        ConfigIO.FILE_WATCH_MANAGER.startService();
        AzureLib.initialize();
        AzureLibMod.initRegistry();
        AzureLibMod.config = AzureLibMod.registerConfig(AzureLibConfig.class, ConfigFormats.json()).getConfigInstance();
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
            ConfigIO.FILE_WATCH_MANAGER.stopService();
        });
        PayloadTypeRegistry.playS2C().register(BlockEntityAnimTriggerPacket.TYPE, BlockEntityAnimTriggerPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(BlockEntityAnimDataSyncPacket.TYPE, BlockEntityAnimDataSyncPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(EntityAnimTriggerPacket.TYPE, EntityAnimTriggerPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(EntityAnimDataSyncPacket.TYPE, EntityAnimDataSyncPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(AnimTriggerPacket.TYPE, AnimTriggerPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(AnimDataSyncPacket.TYPE, AnimDataSyncPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(SendConfigDataPacket.TYPE, SendConfigDataPacket.CODEC);
    }
}
