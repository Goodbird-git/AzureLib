package mod.azure.azurelib.common.internal.common.registry;

import mod.azure.azurelib.common.api.common.registry.CommonBlockEntityRegistryInterface;
import mod.azure.azurelib.common.api.common.registry.CommonBlockRegistryInterface;
import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.common.internal.common.blocks.TickingLightBlock;
import mod.azure.azurelib.common.internal.common.blocks.TickingLightEntity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Supplier;

public class AzureBlocksEntityRegistry implements CommonBlockEntityRegistryInterface {

    public static final Supplier<BlockEntityType<TickingLightEntity>> TICKING_LIGHT_ENTITY = CommonBlockEntityRegistryInterface.registerBlockEntity(
            AzureLib.MOD_ID,
            "lightblock",
            () -> BlockEntityType.Builder.of(TickingLightEntity::new, AzureBlocksRegistry.TICKING_LIGHT_BLOCK.get()).build(null));

    public static void init() {
    }
}
