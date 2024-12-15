package mod.azure.azurelib.fabric.core2.example.blocks;

import mod.azure.azurelib.common.api.client.model.DefaultedBlockGeoModel;
import mod.azure.azurelib.common.api.client.renderer.GeoBlockRenderer;
import mod.azure.azurelib.common.internal.common.AzureLib;

public class StargateRender extends GeoBlockRenderer<StargateBlockEntity> {

    public StargateRender() {
        super(new DefaultedBlockGeoModel<>(AzureLib.modResource("stargate")));
    }
}
