package mod.azure.azurelib.neoforge;

import mod.azure.azurelib.common.api.common.enchantments.IncendiaryEnchantment;
import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.common.internal.common.AzureLibMod;
import mod.azure.azurelib.common.internal.common.blocks.TickingLightBlock;
import mod.azure.azurelib.common.internal.common.blocks.TickingLightEntity;
import mod.azure.azurelib.common.internal.common.config.AzureLibConfig;
import mod.azure.azurelib.common.internal.common.config.format.ConfigFormats;
import mod.azure.azurelib.common.internal.common.config.io.ConfigIO;
import mod.azure.azurelib.common.internal.common.network.packet.*;
import mod.azure.azurelib.neoforge.platform.NeoForgeAzureLibNetwork;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
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
        DATA_COMPONENTS_REGISTER.register(modEventBus);
        AzureLibMod.config = AzureLibMod.registerConfig(AzureLibConfig.class, ConfigFormats.json()).getConfigInstance();
        modEventBus.addListener(this::init);
        AzureEnchantments.ENCHANTMENTS.register(modEventBus);
        AzureBlocks.BLOCKS.register(modEventBus);
        AzureEntities.TILE_TYPES.register(modEventBus);
        modEventBus.addListener(this::registerMessages);
    }

    private void init(FMLCommonSetupEvent event) {
        ConfigIO.FILE_WATCH_MANAGER.startService();
    }

    public void registerMessages(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(AzureLib.MOD_ID);

        registrar.playBidirectional(BlockEntityAnimTriggerPacket.TYPE, BlockEntityAnimTriggerPacket.CODEC, (msg, ctx) -> msg.handle());
        registrar.playBidirectional(BlockEntityAnimDataSyncPacket.TYPE, BlockEntityAnimDataSyncPacket.CODEC, (msg, ctx) -> msg.handle());
        registrar.playBidirectional(EntityAnimTriggerPacket.TYPE, EntityAnimTriggerPacket.CODEC, (msg, ctx) -> msg.handle());
        registrar.playBidirectional(EntityAnimDataSyncPacket.TYPE, EntityAnimDataSyncPacket.CODEC, (msg, ctx) -> msg.handle());
        registrar.playBidirectional(AnimTriggerPacket.TYPE, AnimTriggerPacket.CODEC, (msg, ctx) -> msg.handle());
        registrar.playBidirectional(AnimDataSyncPacket.TYPE, AnimDataSyncPacket.CODEC, (msg, ctx) -> msg.handle());
        registrar.playBidirectional(SendConfigDataPacket.TYPE, SendConfigDataPacket.CODEC, (msg, ctx) -> msg.handle());
    }

    public record AzureEnchantments() {

        public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(Registries.ENCHANTMENT,
                AzureLib.MOD_ID);

        public static final Supplier<Enchantment> INCENDIARYENCHANTMENT = ENCHANTMENTS.register("incendiaryenchantment",
                () -> new IncendiaryEnchantment(EquipmentSlot.MAINHAND));
    }

    public record AzureBlocks() {

        public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, AzureLib.MOD_ID);

        public static final Supplier<TickingLightBlock> TICKING_LIGHT_BLOCK = BLOCKS.register("lightblock",
                () -> new TickingLightBlock(BlockBehaviour.Properties.of().sound(SoundType.CANDLE).lightLevel(
                        TickingLightBlock.litBlockEmission(15)).pushReaction(PushReaction.DESTROY).noOcclusion()));
    }

    public record AzureEntities() {

        public static final DeferredRegister<BlockEntityType<?>> TILE_TYPES = DeferredRegister.create(
                Registries.BLOCK_ENTITY_TYPE, AzureLib.MOD_ID);

        public static final Supplier<BlockEntityType<TickingLightEntity>> TICKING_LIGHT_ENTITY = TILE_TYPES.register(
                "lightblock",
                () -> BlockEntityType.Builder.of(TickingLightEntity::new, AzureBlocks.TICKING_LIGHT_BLOCK.get()).build(
                        null));
    }
}
