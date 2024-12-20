package mod.azure.azurelib.core2.animation.impl;

import net.minecraft.world.item.ItemStack;

import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.AzAnimatorConfig;

public abstract class AzItemAnimator extends AzAnimator<ItemStack> {

    protected AzItemAnimator() {
        super();
    }

    protected AzItemAnimator(AzAnimatorConfig config) {
        super(config);
    }
}
