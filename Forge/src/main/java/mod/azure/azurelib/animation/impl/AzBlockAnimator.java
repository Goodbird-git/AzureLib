package mod.azure.azurelib.animation.impl;


import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.AzAnimatorConfig;
import net.minecraft.tileentity.TileEntity;

/**
 * The {@code AzBlockAnimator} class extends the functionality of the {@link AzAnimator} to provide animation support
 * specifically for {@link TileEntity} instances. This abstract class serves as a base for creating block entity
 * animators with reusable configuration and animation controller registration mechanisms.
 *
 * @param <T> The type of {@link TileEntity} that this animator will manage animations for.
 */
public abstract class AzBlockAnimator<T extends TileEntity> extends AzAnimator<T> {

    protected AzBlockAnimator(AzAnimatorConfig config) {
        super(config);
    }
}
