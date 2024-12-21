package mod.azure.azurelib.fabric.core2.example.armors;

import net.minecraft.resources.ResourceLocation;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.render.armor.AzArmorRenderer;
import mod.azure.azurelib.core2.render.armor.AzArmorRendererConfig;

public class AzDoomArmorRenderer extends AzArmorRenderer {

    private static final ResourceLocation MODEL = AzureLib.modResource("geo/item/doomicorn.geo.json");

    private static final ResourceLocation TEXTURE = AzureLib.modResource("textures/item/doomicorn.png");

    public AzDoomArmorRenderer() {
        super(
            AzArmorRendererConfig.builder(MODEL, TEXTURE)
                .setAnimatorProvider(AzDoomArmorAnimator::new)
                .setBoneProvider(new AzDoomArmorBoneProvider())
                .build()
        );
    }
}
