package mod.azure.azurelib.common.internal.mixins;

import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.AzAnimatorAccessor;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

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
    public @Nullable AzAnimator<Entity> getAnimator() {
        return animator;
    }
}
