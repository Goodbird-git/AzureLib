package mod.azure.azurelib.fabric.core2.example.entities.doomhunter;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.render.AzEntityRendererConfig;
import mod.azure.azurelib.core2.render.entity.AzEntityRenderer;

public class DoomHunterRenderer extends AzEntityRenderer<DoomHunter> {

    private static final ResourceLocation MODEL = AzureLib.modResource("geo/entity/doomhunter.geo.json");

    private static final ResourceLocation TEXTURE = AzureLib.modResource("textures/entity/doomhunter.png");

    public DoomHunterRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.<DoomHunter>builder(MODEL, TEXTURE)
                .setAnimatorProvider(DoomHunterAnimator::new)
                .build(),
            context
        );
    }
}
