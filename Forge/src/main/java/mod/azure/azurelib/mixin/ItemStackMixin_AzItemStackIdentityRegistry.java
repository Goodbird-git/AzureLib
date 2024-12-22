package mod.azure.azurelib.mixin;

import mod.azure.azurelib.animation.cache.AzIdentityRegistry;
import mod.azure.azurelib.util.AzureLibUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ItemStack.class)
public class ItemStackMixin_AzItemStackIdentityRegistry {

    @Inject(
        method = "<init>(Lnet/minecraft/item/Item;IILnet/minecraft/nbt/NBTTagCompound;)V",
        at = @At("TAIL")
    )
    public void az_addIdentityComponent(Item itemIn, int amount, int meta, NBTTagCompound capNBT, CallbackInfo ci) {
        ItemStack self = AzureLibUtil.<ItemStack>self(this);
        if (AzIdentityRegistry.hasIdentity(self.getItem()) && !capNBT.hasKey("az_id")) {
            capNBT.setUniqueId("az_id", UUID.randomUUID());
        }
    }
}
