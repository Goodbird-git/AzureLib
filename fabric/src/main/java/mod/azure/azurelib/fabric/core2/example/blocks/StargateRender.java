package mod.azure.azurelib.fabric.core2.example.blocks;

import net.minecraft.resources.ResourceLocation;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.render.AzBlockEntityRendererConfig;
import mod.azure.azurelib.core2.render.block.AzBlockRenderer;

public class StargateRender extends AzBlockRenderer<StargateBlockEntity> {

    private static final ResourceLocation MODEL = AzureLib.modResource("geo/block/stargate.geo.json");

    private static final ResourceLocation TEXTURE = AzureLib.modResource("textures/block/stargate.png");

    public StargateRender() {
        super(
            AzBlockEntityRendererConfig.<StargateBlockEntity>builder(MODEL, TEXTURE)
                .setAnimatorProvider(StargateBlockEntityAnimator::new)
                .build()
        );
    }
}
