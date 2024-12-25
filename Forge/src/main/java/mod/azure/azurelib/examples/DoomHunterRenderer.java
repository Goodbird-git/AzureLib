package mod.azure.azurelib.examples;


import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.render.entity.AzEntityRenderer;
import mod.azure.azurelib.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class DoomHunterRenderer extends AzEntityRenderer<DoomHunter> {

    private static final ResourceLocation MODEL = AzureLib.modResource("geo/entity/doomhunter.geo.json");

    private static final ResourceLocation TEXTURE = AzureLib.modResource("textures/entity/doomhunter.png");

    public DoomHunterRenderer(RenderManager renderManagerIn) {
        super(
            AzEntityRendererConfig.<DoomHunter>builder(MODEL, TEXTURE)
                .setAnimatorProvider(DoomHunterAnimator::new)
                .build(),
            renderManagerIn
        );
    }
}
