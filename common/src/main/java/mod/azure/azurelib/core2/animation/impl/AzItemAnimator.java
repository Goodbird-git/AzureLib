package mod.azure.azurelib.core2.animation.impl;

import net.minecraft.world.item.Item;

import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.AzAnimatorConfig;

public abstract class AzItemAnimator<T extends Item> extends AzAnimator<T> {

    protected AzItemAnimator(AzAnimatorConfig config) {
        super(config);
    }
}
