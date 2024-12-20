package mod.azure.azurelib.common.internal.mixins;

import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.lang.ref.WeakReference;

import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.AzAnimatorAccessor;
import mod.azure.azurelib.core2.animation.cache.AzIdentifiableItemStackAnimatorCache;
import mod.azure.azurelib.core2.util.WeakSelfReference;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin_AzItemAnimatorCache implements AzAnimatorAccessor<ItemStack>, WeakSelfReference<ItemStack> {

    @Unique
    @Nullable
    private AzAnimator<ItemStack> animator;

    @Unique
    @SuppressWarnings("all")
    private final WeakReference<ItemStack> ref = new WeakReference<>(((ItemStack) ((Object) this)));

    @Override
    public void setAnimator(@Nullable AzAnimator<ItemStack> animator) {
        this.animator = animator;
    }

    @Override
    public @Nullable AzAnimator<ItemStack> getAnimatorOrNull() {
        var self = AzureLibUtil.<ItemStack>self(this);
        AzIdentifiableItemStackAnimatorCache.getInstance().add(self);
        return animator;
    }

    @Override
    public WeakReference<ItemStack> getOrCreateRef() {
        return ref;
    }
}
