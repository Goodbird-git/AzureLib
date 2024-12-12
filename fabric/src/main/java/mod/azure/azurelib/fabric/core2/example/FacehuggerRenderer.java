package mod.azure.azurelib.fabric.core2.example;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.animation.impl.AzEntityAnimator;
import mod.azure.azurelib.core2.render.entity.AzEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FacehuggerRenderer extends AzEntityRenderer<Facehugger> {

    private static final ResourceLocation MODEL = AzureLib.modResource("geo/entity/facehugger.geo.json");

    private static final ResourceLocation TEXTURE = AzureLib.modResource("textures/entity/facehugger.png");

    public FacehuggerRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected @Nullable AzEntityAnimator<Facehugger> createAnimator() {
        return new FacehuggerAnimator();
    }

    @Override
    protected @NotNull ResourceLocation getModelLocation(Facehugger facehugger) {
        return MODEL;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Facehugger facehugger) {
        return TEXTURE;
    }
}
