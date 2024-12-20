package mod.azure.azurelib.core2.animation.impl;

import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.AzAnimatorConfig;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AzBlockAnimator<T extends BlockEntity> extends AzAnimator<T> {

    protected AzBlockAnimator(AzAnimatorConfig config) {
        super(config);
    }
}