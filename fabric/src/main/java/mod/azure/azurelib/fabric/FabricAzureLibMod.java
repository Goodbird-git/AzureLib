package mod.azure.azurelib.fabric;

import mod.azure.azurelib.common.api.common.enchantments.IncendiaryEnchantment;
import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.common.internal.common.AzureLibMod;
import mod.azure.azurelib.common.internal.common.blocks.TickingLightBlock;
import mod.azure.azurelib.common.internal.common.blocks.TickingLightEntity;
import mod.azure.azurelib.common.internal.common.config.AzureLibConfig;
import mod.azure.azurelib.common.internal.common.config.format.ConfigFormats;
import mod.azure.azurelib.common.internal.common.config.io.ConfigIO;
import mod.azure.azurelib.common.internal.common.network.packet.*;
import mod.azure.azurelib.common.platform.services.AzureLibNetwork;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

public final class FabricAzureLibMod implements ModInitializer {

    public static final TickingLightBlock TICKING_LIGHT_BLOCK = new TickingLightBlock(
            BlockBehaviour.Properties.of()
                    .sound(SoundType.CANDLE)
                    .lightLevel(TickingLightBlock.litBlockEmission(15))
                    .pushReaction(PushReaction.DESTROY)
                    .noOcclusion()
    );
    public static final Enchantment INCENDIARYENCHANTMENT = new IncendiaryEnchantment(
            EquipmentSlot.MAINHAND
    );
    public static BlockEntityType<TickingLightEntity> TICKING_LIGHT_ENTITY;

    @Override
    public void onInitialize() {
        ConfigIO.FILE_WATCH_MANAGER.startService();
        AzureLib.initialize();
        Registry.register(
                BuiltInRegistries.BLOCK,
                AzureLib.modResource("lightblock"),
                FabricAzureLibMod.TICKING_LIGHT_BLOCK
        );
        FabricAzureLibMod.TICKING_LIGHT_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                AzureLib.MOD_ID + ":lightblock",
                BlockEntityType.Builder.of(TickingLightEntity::new, FabricAzureLibMod.TICKING_LIGHT_BLOCK)
                        .build(null)
        );
        AzureLibMod.config = AzureLibMod.registerConfig(AzureLibConfig.class, ConfigFormats.json()).getConfigInstance();
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
            ConfigIO.FILE_WATCH_MANAGER.stopService();
        });
        Registry.register(
                BuiltInRegistries.ENCHANTMENT,
                AzureLib.modResource("incendiaryenchantment"),
                INCENDIARYENCHANTMENT
        );
        PayloadTypeRegistry.playS2C().register(BlockEntityAnimTriggerPacket.TYPE, BlockEntityAnimTriggerPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(BlockEntityAnimDataSyncPacket.TYPE, BlockEntityAnimDataSyncPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(EntityAnimTriggerPacket.TYPE, EntityAnimTriggerPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(EntityAnimDataSyncPacket.TYPE, EntityAnimDataSyncPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(AnimTriggerPacket.TYPE, AnimTriggerPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(AnimDataSyncPacket.TYPE, AnimDataSyncPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(SendConfigDataPacket.TYPE, SendConfigDataPacket.CODEC);
    }
}
