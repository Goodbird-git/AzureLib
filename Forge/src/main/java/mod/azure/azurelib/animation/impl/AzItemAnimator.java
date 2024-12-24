package mod.azure.azurelib.animation.impl;

import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.AzAnimatorConfig;
import net.minecraft.item.ItemStack;

/**
 * The {@code AzItemAnimator} class is an abstract extension of the {@code AzAnimator} class, specifically designed to
 * handle animations for {@link ItemStack} objects. It provides common functionality and structure for animating items
 * within the framework. <br/>
 * <br/>
 * This class serves as a base for developing custom item animator implementations. Subclasses are required to implement
 * methods for animation controller registration and for specifying the animation location for the corresponding
 * {@code ItemStack}.
 */
public abstract class AzItemAnimator extends AzAnimator<ItemStack> {

    protected AzItemAnimator() {
        super();
    }

    protected AzItemAnimator(AzAnimatorConfig config) {
        super(config);
    }
}