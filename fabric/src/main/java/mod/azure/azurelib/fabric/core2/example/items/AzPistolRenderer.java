package mod.azure.azurelib.fabric.core2.example.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.animation.impl.AzItemAnimator;
import mod.azure.azurelib.core2.render.item.AzItemRenderer;

public class AzPistolRenderer extends AzItemRenderer {

    private static final ResourceLocation MODEL = AzureLib.modResource("geo/item/pistol.geo.json");

    private static final ResourceLocation TEXTURE = AzureLib.modResource("textures/item/pistol.png");

    @Override
    protected @Nullable AzItemAnimator createAnimator() {
        return new AzPistolAnimator();
    }

    @Override
    protected @NotNull ResourceLocation getModelLocation(ItemStack item) {
        return MODEL;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(ItemStack item) {
        return TEXTURE;
    }
}
