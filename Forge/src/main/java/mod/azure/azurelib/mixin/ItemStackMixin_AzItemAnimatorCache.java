package mod.azure.azurelib.mixin;

import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.AzAnimatorAccessor;
import mod.azure.azurelib.animation.cache.AzIdentifiableItemStackAnimatorCache;
import mod.azure.azurelib.animation.impl.AzItemAnimator;
import mod.azure.azurelib.util.AzureLibUtil;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin_AzItemAnimatorCache implements AzAnimatorAccessor<ItemStack> {

    @Override
    public void setAnimator(AzAnimator<ItemStack> animator) {
        ItemStack itemStack = AzureLibUtil.<ItemStack>self(this);
        AzIdentifiableItemStackAnimatorCache.getInstance().add(itemStack, (AzItemAnimator) animator);
    }

    @Override
    public AzAnimator<ItemStack> getAnimatorOrNull() {
        ItemStack self = AzureLibUtil.<ItemStack>self(this);
        java.util.UUID uuid = self.serializeNBT().getUniqueId("az_id");
        return AzIdentifiableItemStackAnimatorCache.getInstance().getOrNull(uuid);
    }
}
