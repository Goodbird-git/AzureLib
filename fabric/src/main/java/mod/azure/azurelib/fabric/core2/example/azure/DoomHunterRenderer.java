package mod.azure.azurelib.fabric.core2.example.azure;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.animation.impl.AzEntityAnimator;
import mod.azure.azurelib.core2.render.entity.AzEntityRenderer;

public class DoomHunterRenderer extends AzEntityRenderer<DoomHunter> {

    private static final ResourceLocation MODEL = AzureLib.modResource("geo/entity/doomhunter.geo.json");

    private static final ResourceLocation TEXTURE = AzureLib.modResource("textures/entity/doomhunter.png");

    public DoomHunterRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected @Nullable AzEntityAnimator<DoomHunter> createAnimator() {
        return new DoomHunterAnimator();
    }

    @Override
    protected @NotNull ResourceLocation getModelLocation(DoomHunter doomHunter) {
        return MODEL;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull DoomHunter doomHunter) {
        return TEXTURE;
    }
}
