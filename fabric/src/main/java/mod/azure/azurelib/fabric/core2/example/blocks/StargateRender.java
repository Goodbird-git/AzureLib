package mod.azure.azurelib.fabric.core2.example.blocks;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.animation.impl.AzBlockAnimator;
import mod.azure.azurelib.core2.animation.impl.AzEntityAnimator;
import mod.azure.azurelib.core2.render.block.AzBlockRenderer;
import mod.azure.azurelib.fabric.core2.example.entities.drone.Drone;
import mod.azure.azurelib.fabric.core2.example.entities.drone.DroneAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StargateRender extends AzBlockRenderer<StargateBlockEntity> {

    private static final ResourceLocation MODEL = AzureLib.modResource("geo/block/stargate.geo.json");

    private static final ResourceLocation TEXTURE = AzureLib.modResource("textures/block/stargate.png");

    public StargateRender() {
        super();
    }

    @Override
    protected @Nullable AzBlockAnimator<StargateBlockEntity> createAnimator() {
        return new StargateBlockEntityAnimator();
    }

    @Override
    protected @NotNull ResourceLocation getModelLocation(StargateBlockEntity entity) {
        return MODEL;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(StargateBlockEntity entity) {
        return TEXTURE;
    }
}
