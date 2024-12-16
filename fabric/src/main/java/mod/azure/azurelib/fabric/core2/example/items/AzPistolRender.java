package mod.azure.azurelib.fabric.core2.example.items;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.render.item.AzItemRenderer;

public class AzPistolRender extends AzItemRenderer<AzPistol> {

    private static final ResourceLocation MODEL = AzureLib.modResource("geo/item/pistol.geo.json");

    private static final ResourceLocation TEXTURE = AzureLib.modResource("textures/item/pistol.png");

    public AzPistolRender() {
        super();
    }

    @Override
    protected @NotNull ResourceLocation getModelLocation(AzPistol item) {
        return MODEL;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(AzPistol item) {
        return TEXTURE;
    }
}
