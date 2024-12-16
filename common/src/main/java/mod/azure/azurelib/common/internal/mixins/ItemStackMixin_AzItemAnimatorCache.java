package mod.azure.azurelib.common.internal.mixins;

import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.AzAnimatorAccessor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin_AzItemAnimatorCache implements AzAnimatorAccessor<ItemStack> {

    @Unique
    @Nullable
    private AzAnimator<ItemStack> animator;

    @Override
    public void setAnimator(@Nullable AzAnimator<ItemStack> animator) {
        this.animator = animator;
    }

    @Override
    public @Nullable AzAnimator<ItemStack> getAnimatorOrNull() {
        return animator;
    }
}
