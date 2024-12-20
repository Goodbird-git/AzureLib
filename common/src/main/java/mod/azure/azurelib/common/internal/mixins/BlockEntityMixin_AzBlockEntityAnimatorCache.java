package mod.azure.azurelib.common.internal.mixins;

import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.AzAnimatorAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin_AzBlockEntityAnimatorCache implements AzAnimatorAccessor<BlockEntity> {

    @Unique
    @Nullable
    private AzAnimator<BlockEntity> animator;

    @Override
    public void setAnimator(@Nullable AzAnimator<BlockEntity> animator) {
        this.animator = animator;
    }

    @Override
    public @Nullable AzAnimator<BlockEntity> getAnimatorOrNull() {
        return animator;
    }
}
