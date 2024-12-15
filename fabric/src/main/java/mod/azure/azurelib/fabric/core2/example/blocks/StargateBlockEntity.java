package mod.azure.azurelib.fabric.core2.example.blocks;

import net.minecraft.core.BlockPos;
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

public class StargateBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    private static final String SPIN_ANIMATION_NAME = "equipping";

    private static final RawAnimation SPIN_ANIMATION = RawAnimation.begin().thenLoop(SPIN_ANIMATION_NAME);

    public StargateBlockEntity(BlockPos pos, BlockState blockState) {
        super(ExampleEntityTypes.STARGATE, pos, blockState);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
            new AnimationController<>(this, "base_controller", 0, state -> PlayState.CONTINUE).triggerableAnim(
                SPIN_ANIMATION_NAME,
                SPIN_ANIMATION
            )
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
