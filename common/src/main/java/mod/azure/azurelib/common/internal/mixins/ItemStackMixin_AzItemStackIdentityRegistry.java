package mod.azure.azurelib.common.internal.mixins;

import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.UUID;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import mod.azure.azurelib.core2.animation.cache.AzIdentityRegistry;

@Mixin(ItemStack.class)
public class ItemStackMixin_AzItemStackIdentityRegistry {

    @Inject(method = "<init>", at = @At("TAIL"))
    public void addIdentity() {
        var self = AzureLibUtil.<ItemStack>self(this);
        if (
            AzIdentityRegistry.hasIdentity(self.getItem()) && self
                .getComponents() instanceof PatchedDataComponentMap components && !components.has(AzureLib.AZ_ID.get())
        ) {
            components.set(AzureLib.AZ_ID.get(), UUID.randomUUID());
        }
    }
}
