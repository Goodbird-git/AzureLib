package mod.azure.azurelib.common.internal.mixins;

import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.AzAnimatorAccessor;
import mod.azure.azurelib.core2.animation.cache.AzIdentifiableItemStackAnimatorCache;
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
        // TODO: Use a utility function to perform this type of cast.
        @SuppressWarnings("all")
        var self = (ItemStack) ((Object) this);
        AzIdentifiableItemStackAnimatorCache.getInstance().add(self);
        return animator;
    }
}
