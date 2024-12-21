package mod.azure.azurelib.common.internal.mixins;

import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import mod.azure.azurelib.core2.animation.cache.AzIdentityRegistry;

@Mixin(ItemStack.class)
public class ItemStackMixin_AzItemStackIdentityRegistry {

    @Inject(
        method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)V",
        at = @At("TAIL")
    )
    public void az_addIdentityComponent(ItemLike item, int count, PatchedDataComponentMap components, CallbackInfo ci) {
        var self = AzureLibUtil.<ItemStack>self(this);
        if (AzIdentityRegistry.hasIdentity(self.getItem()) && !components.has(AzureLib.AZ_ID.get())) {
            components.set(AzureLib.AZ_ID.get(), UUID.randomUUID());
        }
    }
}
