package mod.azure.azurelib.core2.animation.impl;

import net.minecraft.world.level.block.entity.BlockEntity;

import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.AzAnimatorConfig;

public abstract class AzBlockAnimator<T extends BlockEntity> extends AzAnimator<T> {

    protected AzBlockAnimator(AzAnimatorConfig config) {
        super(config);
    }
}
