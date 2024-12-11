package mod.azure.azurelib.common.internal.mixins;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.AzAnimatorAccessor;

@Mixin(Entity.class)
public abstract class EntityMixin_AzEntityAnimatorCache implements AzAnimatorAccessor<Entity> {

    @Unique
    @Nullable
    private AzAnimator<Entity> animator;

    @Override
    public void setAnimator(@Nullable AzAnimator<Entity> animator) {
        this.animator = animator;
    }

    @Override
    public @Nullable AzAnimator<Entity> getAnimatorOrNull() {
        return animator;
    }
}
