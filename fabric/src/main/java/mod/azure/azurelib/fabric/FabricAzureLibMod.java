package mod.azure.azurelib.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.common.internal.common.AzureLibMod;
import mod.azure.azurelib.common.internal.common.config.AzureLibConfig;
import mod.azure.azurelib.common.internal.common.config.format.ConfigFormats;
import mod.azure.azurelib.common.internal.common.config.io.ConfigIO;
import mod.azure.azurelib.common.internal.common.network.packet.AnimDataSyncPacket;
import mod.azure.azurelib.common.internal.common.network.packet.AnimTriggerPacket;
import mod.azure.azurelib.common.internal.common.network.packet.AzEntityAnimTriggerPacket;
import mod.azure.azurelib.common.internal.common.network.packet.BlockEntityAnimDataSyncPacket;
import mod.azure.azurelib.common.internal.common.network.packet.BlockEntityAnimTriggerPacket;
import mod.azure.azurelib.common.internal.common.network.packet.EntityAnimDataSyncPacket;
import mod.azure.azurelib.common.internal.common.network.packet.EntityAnimTriggerPacket;
import mod.azure.azurelib.common.internal.common.network.packet.SendConfigDataPacket;
import mod.azure.azurelib.fabric.core2.example.ExampleEntityTypes;
import mod.azure.azurelib.fabric.core2.example.armors.DoomArmor;
import mod.azure.azurelib.fabric.core2.example.blocks.Stargate;
import mod.azure.azurelib.fabric.core2.example.items.Pistol;
import mod.azure.azurelib.fabric.platform.FabricAzureLibNetwork;

public final class FabricAzureLibMod implements ModInitializer {

    public static final Block STARGATE = new Stargate(
        BlockBehaviour.Properties.of().sound(SoundType.DRIPSTONE_BLOCK).strength(5.0f, 8.0f).noOcclusion()
    );

    @Override
    public void onInitialize() {
        ConfigIO.FILE_WATCH_MANAGER.startService();
        AzureLib.initialize();
        AzureLibMod.initRegistry();
        new FabricAzureLibNetwork();
        AzureLibMod.config = AzureLibMod.registerConfig(AzureLibConfig.class, ConfigFormats.json()).getConfigInstance();
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> ConfigIO.FILE_WATCH_MANAGER.stopService());
        PayloadTypeRegistry.playS2C().register(BlockEntityAnimTriggerPacket.TYPE, BlockEntityAnimTriggerPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(BlockEntityAnimDataSyncPacket.TYPE, BlockEntityAnimDataSyncPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(EntityAnimTriggerPacket.TYPE, EntityAnimTriggerPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(AzEntityAnimTriggerPacket.TYPE, AzEntityAnimTriggerPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(EntityAnimDataSyncPacket.TYPE, EntityAnimDataSyncPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(AnimTriggerPacket.TYPE, AnimTriggerPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(AnimDataSyncPacket.TYPE, AnimDataSyncPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(SendConfigDataPacket.TYPE, SendConfigDataPacket.CODEC);
        Registry.register(BuiltInRegistries.BLOCK, AzureLib.modResource("stargate"), STARGATE);
        Registry.register(
            BuiltInRegistries.ITEM,
            AzureLib.modResource("stargate"),
            new BlockItem(STARGATE, new Item.Properties())
        );
        Registry.register(
            BuiltInRegistries.ITEM,
            AzureLib.modResource("pistol"),
            new Pistol()
        );
        Registry.register(
            BuiltInRegistries.ITEM,
            AzureLib.modResource("doomicorn_helmet"),
            new DoomArmor(ArmorItem.Type.HELMET)
        );
        Registry.register(
            BuiltInRegistries.ITEM,
            AzureLib.modResource("doomicorn_chestplate"),
            new DoomArmor(ArmorItem.Type.CHESTPLATE)
        );
        Registry.register(
            BuiltInRegistries.ITEM,
            AzureLib.modResource("doomicorn_leggings"),
            new DoomArmor(ArmorItem.Type.LEGGINGS)
        );
        Registry.register(
            BuiltInRegistries.ITEM,
            AzureLib.modResource("doomicorn_boots"),
            new DoomArmor(ArmorItem.Type.BOOTS)
        );
        ExampleEntityTypes.initialize();
    }
}
