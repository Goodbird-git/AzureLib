package mod.azure.azurelib.core2.animation.impl;

import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.AzAnimatorConfig;
import net.minecraft.world.item.ItemStack;

public abstract class AzItemAnimator extends AzAnimator<ItemStack> {

    protected AzItemAnimator(AzAnimatorConfig config) {
        super(config);
    }
}
