package mod.azure.azurelib.fabric.core2.example.items;

import net.minecraft.resources.ResourceLocation;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.render.AzItemRendererConfig;
import mod.azure.azurelib.core2.render.item.AzItemRenderer;

public class AzPistolRenderer extends AzItemRenderer {

    private static final ResourceLocation MODEL = AzureLib.modResource("geo/item/pistol.geo.json");

    private static final ResourceLocation TEXTURE = AzureLib.modResource("textures/item/pistol.png");

    public AzPistolRenderer() {
        super(
            AzItemRendererConfig.builder(MODEL, TEXTURE).setAnimatorProvider(AzPistolAnimator::new).build()
        );
    }
}
