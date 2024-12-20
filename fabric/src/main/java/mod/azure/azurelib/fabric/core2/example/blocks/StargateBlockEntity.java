package mod.azure.azurelib.fabric.core2.example.blocks;

import mod.azure.azurelib.core2.animation.AzAnimationDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import mod.azure.azurelib.common.api.common.animatable.GeoBlockEntity;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.fabric.core2.example.ExampleEntityTypes;

public class StargateBlockEntity extends BlockEntity {

    public final StargateAnimationDispatcher animationDispatcher;

    public StargateBlockEntity(BlockPos pos, BlockState blockState) {
        super(ExampleEntityTypes.STARGATE, pos, blockState);
        this.animationDispatcher = new StargateAnimationDispatcher();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, StargateBlockEntity blockEntity) {
        if (blockEntity.level != null && level.isClientSide()) {
            blockEntity.animationDispatcher.serverSpin(blockEntity);
        }
    }
}
